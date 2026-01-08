package com.spectra.agent.web.inspector;

import com.spectra.commons.dto.inspector.LocatorSuggestionDTO;
import com.spectra.commons.dto.inspector.UiNodeDTO;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WebInspectorService {

    private final WebInspectorDriverManager driverManager;

    private WebDriver driver() {
        return driverManager.getOrThrow();
    }

    public void openUrl(String url) {
        if (url == null || url.isBlank()) return;
        driver().navigate().to(url);
    }

    public void cleanupInspectorIds() {
        JavascriptExecutor js = (JavascriptExecutor) driver();
        String script = """
        (function(){
          try {
            const els = document.querySelectorAll('[data-spectra-inspector-id]');
            els.forEach(el => el.removeAttribute('data-spectra-inspector-id'));
            return els.length;
          } catch(e) { return 0; }
        })();
        """;
        js.executeScript(script);
    }

    public UiNodeDTO getDomTree(int maxDepth, int maxChildrenPerNode, int maxNodes) {
        maxDepth = Math.max(0, maxDepth);
        maxChildrenPerNode = Math.max(1, maxChildrenPerNode);
        maxNodes = Math.max(1, maxNodes);
        JavascriptExecutor js = (JavascriptExecutor) driver();

        String script = """
        (function(maxDepth, maxChildren, maxNodes){
          const SAFE_ATTRS = ["id","class","name","type","role","aria-label","placeholder",
                              "data-testid","data-test","data-qa","href","src"];
          
          const root = document.documentElement;
          let nodeCounter = 0;

          function shortText(s){
            if(!s) return "";
            s = (""+s).replace(/\\s+/g, " ").trim();
            return s.length > 80 ? s.slice(0, 77) + "..." : s;
          }

          function getAttr(el, k){
            try {
              if(k === "class") return (el.className || "").toString().trim();
              const v = el.getAttribute && el.getAttribute(k);
              return v == null ? "" : (""+v);
            } catch(e){ return ""; }
          }

          // IMPORTANT: Stabil ID
          function assignId(el){
            try {
              const existing = el.getAttribute("data-spectra-inspector-id");
              if(existing) return existing;
            } catch(e){}
            nodeCounter++;
            const id = "n" + nodeCounter;
            try { el.setAttribute("data-spectra-inspector-id", id); } catch(e){}
            return id;
          }

          function build(el, depth){
            if(!el) return null;
            if(nodeCounter >= maxNodes) return null;

            const id = assignId(el);

            const attrs = {};
            for(const k of SAFE_ATTRS){
              const v = getAttr(el, k);
              if(v) attrs[k] = v;
            }

            const tag = (el.tagName || "").toLowerCase();
            attrs["tag"] = tag;

            let name = "";
            if(tag === "input" || tag === "textarea") name = shortText(el.value);
            else name = shortText(el.textContent);

            const node = {
              nodeId: id,
              type: tag,
              name,
              attrs,
              children: []
            };

            if(depth >= maxDepth) return node;

            const kids = el.children ? Array.from(el.children) : [];
            const limit = Math.min(kids.length, maxChildren);
            for(let i=0;i<limit;i++){
              if(nodeCounter >= maxNodes) break;
              const child = build(kids[i], depth+1);
              if(child) node.children.push(child);
            }
            return node;
          }

          return build(root, 0);
        })(arguments[0], arguments[1], arguments[2]);
        """;

        @SuppressWarnings("unchecked")
        Map<String, Object> raw = (Map<String, Object>) js.executeScript(
                script, maxDepth, maxChildrenPerNode, maxNodes
        );

        return mapNode(raw);
    }

    public List<LocatorSuggestionDTO> getSuggestions(String nodeId) {
        JavascriptExecutor js = (JavascriptExecutor) driver();

        String script = """
        (function(nodeId){
          function cssEscapeFallback(v){
            return (""+v).replace(/([\\\\.#:[\\]()>+~*^$|=])/g, '\\\\$1');
          }
          const esc = (typeof CSS !== "undefined" && CSS.escape) ? CSS.escape : cssEscapeFallback;

          const el = document.querySelector('[data-spectra-inspector-id="' + nodeId + '"]');
          if(!el) return { found:false, suggestions:[] };

          const suggestions = [];

          const id = el.getAttribute("id");
          if(id){
            try {
              const count = document.querySelectorAll('#' + esc(id)).length;
              if(count === 1){
                suggestions.push({ strategy:"id", value:id, score:1.0, note:"Unique id" });
              }
            } catch(e){}
          }

          const testid = el.getAttribute("data-testid") || el.getAttribute("data-test") || el.getAttribute("data-qa");
          if(testid){
            suggestions.push({ strategy:"css", value:'[data-testid="' + testid + '"]', score:0.95, note:"data-testid/data-test" });
          }

          const aria = el.getAttribute("aria-label");
          if(aria){
            suggestions.push({ strategy:"css", value:'[aria-label="' + aria + '"]', score:0.85, note:"aria-label" });
          }

          function cssPath(e){
            const parts = [];
            let cur = e;
            for(let i=0;i<6 && cur && cur.nodeType===1;i++){
              let part = cur.tagName.toLowerCase();
              const cid = cur.getAttribute("id");
              if(cid){
                part += "#" + esc(cid);
                parts.unshift(part);
                break;
              }
              const cls = (cur.className || "").toString().trim().split(/\\s+/).filter(Boolean);
              if(cls.length) part += "." + cls.slice(0,2).map(c=>esc(c)).join(".");
              const parent = cur.parentElement;
              if(parent){
                const siblings = Array.from(parent.children).filter(x => x.tagName === cur.tagName);
                if(siblings.length > 1){
                  const idx = siblings.indexOf(cur) + 1;
                  part += ":nth-of-type(" + idx + ")";
                }
              }
              parts.unshift(part);
              cur = cur.parentElement;
            }
            return parts.join(" > ");
          }

          const css = cssPath(el);
          if(css){
            suggestions.push({ strategy:"css", value: css, score:0.7, note:"Generated CSS path (MVP)" });
          }

          function xPath(e){
            const segs = [];
            let cur = e;
            for(let i=0;i<8 && cur && cur.nodeType===1;i++){
              let tag = cur.tagName.toLowerCase();
              const parent = cur.parentElement;
              if(!parent){ segs.unshift("/" + tag); break; }
              const siblings = Array.from(parent.children).filter(x => x.tagName === cur.tagName);
              if(siblings.length > 1){
                const idx = siblings.indexOf(cur) + 1;
                segs.unshift("/" + tag + "[" + idx + "]");
              } else {
                segs.unshift("/" + tag);
              }
              cur = parent;
            }
            return segs.join("");
          }

          suggestions.push({ strategy:"xpath", value: xPath(el), score:0.4, note:"Fallback XPath" });

          suggestions.sort((a,b)=>b.score - a.score);
          return { found:true, suggestions };
        })(arguments[0]);
        """;

        @SuppressWarnings("unchecked")
        Map<String, Object> res = (Map<String, Object>) js.executeScript(script, nodeId);

        Object foundObj = res.get("found");
        boolean found = foundObj instanceof Boolean && (Boolean) foundObj;
        if (!found) return List.of();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> raw = (List<Map<String, Object>>) res.get("suggestions");

        List<LocatorSuggestionDTO> out = new ArrayList<>();
        for (var s : raw) {
            out.add(new LocatorSuggestionDTO(
                    Objects.toString(s.get("strategy"), ""),
                    Objects.toString(s.get("value"), ""),
                    ((Number) s.get("score")).doubleValue(),
                    Objects.toString(s.get("note"), "")
            ));
        }
        return out;
    }

    public String getPageTitle() {
        try { return driver().getTitle(); } catch (Exception e) { return ""; }
    }

    public String getPageUrl() {
        try { return driver().getCurrentUrl(); } catch (Exception e) { return ""; }
    }

    // ---- mapping helper ----
    private UiNodeDTO mapNode(Map<String, Object> raw) {
        if (raw == null) return new UiNodeDTO("", "", "", Map.of(), List.of());

        String nodeId = Objects.toString(raw.get("nodeId"), "");
        String type = Objects.toString(raw.get("type"), "");
        String name = Objects.toString(raw.get("name"), "");

        @SuppressWarnings("unchecked")
        Map<String, Object> attrs = (Map<String, Object>) raw.getOrDefault("attrs", Map.of());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> kids = (List<Map<String, Object>>) raw.getOrDefault("children", List.of());

        List<UiNodeDTO> children = new ArrayList<>();
        for (var k : kids) children.add(mapNode(k));

        return new UiNodeDTO(nodeId, type, name, attrs, children);
    }
}