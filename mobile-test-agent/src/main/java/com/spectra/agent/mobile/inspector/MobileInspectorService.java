package com.spectra.agent.mobile.inspector;

import com.spectra.commons.dto.inspector.LocatorSuggestionDTO;
import com.spectra.commons.dto.inspector.UiNodeDTO;
import io.appium.java_client.AppiumDriver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MobileInspectorService {

    private final MobileInspectorDriverManager driverManager;

    private AppiumDriver driver() {
        return driverManager.getOrThrow();
    }

    public UiNodeDTO getUiTree(int maxDepth, int maxChildrenPerNode, int maxNodes) {
        String xml = driver().getPageSource(); // Android: UIAutomator XML, iOS: XCUI XML
        Document doc = parseXml(xml);
        Element root = doc.getDocumentElement();
        if (root == null) return new UiNodeDTO("", "", "", Map.of(), List.of());

        Counter counter = new Counter(maxNodes);
        return build(root, 0, "/"+safeName(root.getTagName()), maxDepth, maxChildrenPerNode, counter);
    }

    public List<LocatorSuggestionDTO> getSuggestions(String nodeId) {
        // nodeId’yi xpath-like path olarak ürettik.
        // Suggestions hesaplamak için tekrar XML parse edip path ile elementi bulacağız.
        String xml = driver().getPageSource();
        Document doc = parseXml(xml);
        Element root = doc.getDocumentElement();
        if (root == null) return List.of();

        Element el = findByPath(root, nodeId);
        if (el == null) return List.of();

        List<LocatorSuggestionDTO> out = new ArrayList<>();

        // Android: resource-id
        String resourceId = attr(el, "resource-id");
        if (!resourceId.isBlank()) {
            out.add(new LocatorSuggestionDTO("id", resourceId, 0.95, "Android resource-id"));
        }

        // iOS/Android: content-desc (Android) / name/label (iOS) gibi alanlar pageSource’ta farklı olabilir
        String contentDesc = firstNonBlank(attr(el, "content-desc"), attr(el, "name"), attr(el, "label"));
        if (!contentDesc.isBlank()) {
            out.add(new LocatorSuggestionDTO("accessibilityId", contentDesc, 0.90, "Accessibility identifier"));
        }

        // class + text kombinasyonu ile kısmi xpath (MVP)
        String cls = firstNonBlank(attr(el, "class"), safeName(el.getTagName()));
        String text = firstNonBlank(attr(el, "text"), attr(el, "value"));
        if (!cls.isBlank() && !text.isBlank()) {
            String xp = "//*[@class='" + escapeQuotes(cls) + "' and (@text='" + escapeQuotes(text) + "' or @value='" + escapeQuotes(text) + "')]";
            out.add(new LocatorSuggestionDTO("xpath", xp, 0.60, "class + text/value"));
        }

        // fallback: nodeId zaten path → xpath gibi kullanılabilir
        // Not: Bu path gerçek XPath değil; ama istersen gerçek XPath üretmek için build sırasında tag + index ile zaten yakınız.
        out.add(new LocatorSuggestionDTO("xpath", nodeId, 0.40, "Fallback path"));

        out.sort(Comparator.comparingDouble(LocatorSuggestionDTO::score).reversed());
        return out;
    }

    public String getPageTitle() {
        // Mobile’da title yok; istersen app/package name döndürebilirsin.
        return "";
    }

    public String getPageUrl() {
        // Mobile’da url yok
        return "";
    }

    // ---------------- helpers ----------------

    private static class Counter {
        int remaining;
        Counter(int max) { this.remaining = max; }
        boolean dec() { return --remaining >= 0; }
    }

    private UiNodeDTO build(
            Element el,
            int depth,
            String path,
            int maxDepth,
            int maxChildren,
            Counter counter
    ) {
        if (!counter.dec()) return null;

        Map<String, Object> attrs = extractAttrs(el);
        String type = safeName(firstNonBlank(attr(el, "class"), el.getTagName()));
        String name = shortText(firstNonBlank(attr(el, "text"), attr(el, "label"), attr(el, "name"), attr(el, "value")));

        List<UiNodeDTO> children = new ArrayList<>();
        if (depth < maxDepth) {
            List<Element> childEls = childElements(el);
            int limit = Math.min(childEls.size(), maxChildren);

            // sibling index ile stabil path üretelim
            Map<String, Integer> tagCount = new HashMap<>();
            for (int i = 0; i < limit; i++) {
                Element c = childEls.get(i);
                String tag = safeName(c.getTagName());
                int idx = tagCount.merge(tag, 1, Integer::sum);

                String childPath = path + "/" + tag + "[" + idx + "]";
                UiNodeDTO child = build(c, depth + 1, childPath, maxDepth, maxChildren, counter);
                if (child != null) children.add(child);
                if (counter.remaining <= 0) break;
            }
        }

        return new UiNodeDTO(
                path,          // nodeId = path
                type,
                name,
                attrs,
                children
        );
    }

    private Map<String, Object> extractAttrs(Element el) {
        Map<String, Object> attrs = new LinkedHashMap<>();
        NamedNodeMap nnm = el.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Node a = nnm.item(i);
            String k = a.getNodeName();
            String v = a.getNodeValue();
            if (v != null && !v.isBlank()) attrs.put(k, v);
        }
        // tag info
        attrs.put("tag", safeName(el.getTagName()));
        return attrs;
    }

    private static List<Element> childElements(Element el) {
        NodeList nl = el.getChildNodes();
        List<Element> out = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) out.add((Element) n);
        }
        return out;
    }

    private static Document parseXml(String xml) {
        try {
            var dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            var db = dbf.newDocumentBuilder();
            return db.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse pageSource XML", e);
        }
    }

    private static String attr(Element el, String name) {
        try {
            String v = el.getAttribute(name);
            return v == null ? "" : v;
        } catch (Exception e) {
            return "";
        }
    }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return "";
    }

    private static String shortText(String s) {
        if (s == null) return "";
        s = s.replaceAll("\\s+", " ").trim();
        if (s.length() > 80) return s.substring(0, 77) + "...";
        return s;
    }

    private static String safeName(String s) {
        return (s == null || s.isBlank()) ? "node" : s.trim();
    }

    private static String escapeQuotes(String s) {
        return s == null ? "" : s.replace("'", "\\'");
    }

    /**
     * nodeId path’i (/root[1]/child[2]) gibi varsayalım.
     * Bu MVP implementasyon: build sırasında ürettiğimiz path formatını bire bir çözmeye yarar.
     */
    private static Element findByPath(Element root, String path) {
        if (path == null || path.isBlank()) return null;
        // root path’i "/tag" ile başlıyor; biz de build’de root için "/tag" verdik.
        String[] parts = path.split("/");
        Element cur = root;

        // parts[0] = "" (leading slash)
        for (int i = 1; i < parts.length; i++) {
            String p = parts[i];
            if (p.isBlank()) continue;

            String tag;
            int idx = 1;
            int lb = p.indexOf('[');
            int rb = p.indexOf(']');
            if (lb > 0 && rb > lb) {
                tag = p.substring(0, lb);
                idx = Integer.parseInt(p.substring(lb + 1, rb));
            } else {
                tag = p;
            }

            List<Element> kids = childElements(cur);
            int seen = 0;
            Element next = null;
            for (Element k : kids) {
                String kt = safeName(k.getTagName());
                if (kt.equals(tag)) {
                    seen++;
                    if (seen == idx) { next = k; break; }
                }
            }
            if (next == null) return null;
            cur = next;
        }
        return cur;
    }
}