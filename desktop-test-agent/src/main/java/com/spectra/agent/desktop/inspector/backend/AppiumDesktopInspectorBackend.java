package com.spectra.agent.desktop.inspector.backend;

import com.spectra.agent.desktop.inspector.DesktopInspectorRuntimeManager;
import com.spectra.commons.dto.inspector.LocatorSuggestionDTO;
import com.spectra.commons.dto.inspector.UiNodeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
public class AppiumDesktopInspectorBackend implements DesktopInspectorBackend {

    private final DesktopInspectorRuntimeManager runtime;

    @Override
    public UiNodeDTO snapshotTree(int maxDepth, int maxChildrenPerNode, int maxNodes) {
        String xml = safePageSource();

        Document doc = parseXml(xml);
        if (doc == null) {
            return new UiNodeDTO("", "root", "", Map.of("error", "pageSource parse failed"), List.of());
        }

        Element root = doc.getDocumentElement();
        if (root == null) {
            return new UiNodeDTO("", "root", "", Map.of("error", "empty pageSource"), List.of());
        }

        Counter counter = new Counter(maxNodes);
        // nodeId olarak xpath-like path üretiyoruz (InspectorPick'te direkt xpath önerisine dönüşüyor)
        return buildNode(root, 0, maxDepth, maxChildrenPerNode, counter, "/");
    }

    @Override
    public List<LocatorSuggestionDTO> suggestionsForNode(String nodeId) {
        if (nodeId == null || nodeId.isBlank()) return List.of();

        List<LocatorSuggestionDTO> out = new ArrayList<>();
        out.add(new LocatorSuggestionDTO("xpath", nodeId, 0.55, "XPath from inspector node path"));

        // daha iyi öneriler üretmek için aynı pageSource'u parse edip node'u resolve edelim
        String xml = safePageSource();
        Document doc = parseXml(xml);
        if (doc == null || doc.getDocumentElement() == null) {
            out.sort((a,b) -> Double.compare(b.score(), a.score()));
            return out;
        }

        Element target = findByXPathLike(doc.getDocumentElement(), nodeId);
        if (target == null) {
            out.sort((a,b) -> Double.compare(b.score(), a.score()));
            return out;
        }

        Map<String, String> a = collectAttrs(target);

        // Windows ağırlıklı: AutomationId
        String automationId = firstNonBlank(a.get("AutomationId"), a.get("automationId"));
        if (automationId != null) {
            out.add(new LocatorSuggestionDTO("automationId", automationId, 0.98, "AutomationId (Windows)"));
        }

        // Mac/Windows: accessibility identifier
        String accId = firstNonBlank(
                a.get("accessibilityId"),
                a.get("AccessibilityId"),
                a.get("identifier"),
                a.get("Identifier")
        );
        if (accId != null) {
            out.add(new LocatorSuggestionDTO("accessibilityId", accId, 0.92, "Accessibility identifier"));
        }

        String name = firstNonBlank(a.get("Name"), a.get("name"), a.get("label"), a.get("Label"));
        if (name != null) {
            out.add(new LocatorSuggestionDTO("name", name, 0.86, "Name/label"));
        }

        String className = firstNonBlank(a.get("ClassName"), a.get("className"), a.get("type"));
        if (className != null) {
            out.add(new LocatorSuggestionDTO("className", className, 0.45, "ClassName/type (weak)"));
        }

        out.sort((x, y) -> Double.compare(y.score(), x.score()));
        return out;
    }

    @Override
    public String pageTitle() {
        return "";
    }

    @Override
    public String pageUrl() {
        return "";
    }

    // --------------------------
    // Helpers
    // --------------------------

    private String safePageSource() {
        try {
            // Runtime'dan DesktopClient alıyoruz; o client Appium tabanlıysa pageSource alabiliriz.
            // En temiz çözüm: DesktopClient interface -> pageSource() metodu eklemek.
            // MVP için: AbstractAppiumDriverClient'e getPageSource() ekleyip DesktopClient üzerinden expose et.
            var rt = runtime.getOrThrow();

            if (rt.client() == null) {
                return "<root error=\"no_client\"/>";
            }

            // Önerilen: DesktopClient interface'inde pageSource() metodu olsun.
            // Burada iki seçenek var:

            // (A) DesktopClient.pageSource() eklediysen:
            // return rt.client().pageSource();

            // (B) Eklemek istemiyorsan ve AbstractAppiumDriverClient'e erişebiliyorsan:
            if (rt.client() instanceof com.spectra.agent.desktop.engine.client.AbstractAppiumDriverClient<?> c) {
                return c.getDriver().getPageSource();
            }

            return "<root error=\"not_appium_client\"/>";
        } catch (Exception e) {
            return "<root error=\"getPageSource_failed\"/>";
        }
    }

    private Document parseXml(String xml) {
        if (xml == null || xml.isBlank()) return null;

        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);

            // secure XML
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            var builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            return null;
        }
    }

    private UiNodeDTO buildNode(
            Element el,
            int depth,
            int maxDepth,
            int maxChildren,
            Counter counter,
            String parentPath
    ) {
        if (el == null) return null;
        if (!counter.tryTake()) return null;

        String tag = safe(el.getTagName());

        int idx = siblingIndex(el);
        String nodePath = parentPath.endsWith("/")
                ? parentPath + tag + "[" + idx + "]"
                : parentPath + "/" + tag + "[" + idx + "]";

        Map<String, Object> attrs = new LinkedHashMap<>();
        Map<String, String> raw = collectAttrs(el);

        putIfPresent(attrs, "Name", raw.get("Name"));
        putIfPresent(attrs, "name", raw.get("name"));
        putIfPresent(attrs, "label", raw.get("label"));
        putIfPresent(attrs, "value", raw.get("value"));
        putIfPresent(attrs, "AutomationId", raw.get("AutomationId"));
        putIfPresent(attrs, "automationId", raw.get("automationId"));
        putIfPresent(attrs, "ClassName", raw.get("ClassName"));
        putIfPresent(attrs, "className", raw.get("className"));
        putIfPresent(attrs, "identifier", raw.get("identifier"));
        putIfPresent(attrs, "enabled", raw.get("enabled"));
        putIfPresent(attrs, "visible", raw.get("visible"));

        String name = firstNonBlank(
                raw.get("Name"),
                raw.get("name"),
                raw.get("label"),
                raw.get("value")
        );
        name = shortText(name, 80);

        List<UiNodeDTO> children = new ArrayList<>();
        if (depth < maxDepth) {
            List<Element> kids = childElements(el);
            int limit = Math.min(kids.size(), Math.max(0, maxChildren));
            for (int i = 0; i < limit; i++) {
                UiNodeDTO c = buildNode(kids.get(i), depth + 1, maxDepth, maxChildren, counter, nodePath);
                if (c != null) children.add(c);
                if (counter.isExhausted()) break;
            }
        }

        return new UiNodeDTO(nodePath, tag, name, attrs, children);
    }

    private List<Element> childElements(Element parent) {
        List<Element> out = new ArrayList<>();
        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) out.add((Element) n);
        }
        return out;
    }

    private int siblingIndex(Element el) {
        Node parent = el.getParentNode();
        if (parent == null) return 1;

        int idx = 0;
        NodeList nl = parent.getChildNodes();
        String tag = el.getTagName();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            Element e = (Element) n;
            if (!tag.equals(e.getTagName())) continue;
            idx++;
            if (e == el) return idx;
        }
        return 1;
    }

    private Map<String, String> collectAttrs(Element el) {
        Map<String, String> out = new LinkedHashMap<>();
        NamedNodeMap nm = el.getAttributes();
        if (nm == null) return out;
        for (int i = 0; i < nm.getLength(); i++) {
            Node a = nm.item(i);
            if (a == null) continue;
            String k = safe(a.getNodeName());
            String v = safe(a.getNodeValue());
            if (!k.isBlank() && !v.isBlank()) out.put(k, v);
        }
        return out;
    }

    private Element findByXPathLike(Element root, String xpathLike) {
        if (root == null || xpathLike == null || xpathLike.isBlank()) return null;

        String[] parts = xpathLike.split("/");
        Element cur = root;

        int start = 0;
        while (start < parts.length && parts[start].isBlank()) start++;

        for (int i = start; i < parts.length; i++) {
            String p = parts[i].trim();
            if (p.isBlank()) continue;

            String tag = p;
            int idx = 1;
            int lb = p.indexOf('[');
            int rb = p.indexOf(']');
            if (lb > 0 && rb > lb) {
                tag = p.substring(0, lb);
                try { idx = Integer.parseInt(p.substring(lb + 1, rb)); } catch (Exception ignore) {}
            }

            if (i == start) {
                if (cur.getTagName().equals(tag)) continue;
            }

            cur = nthChildByTag(cur, tag, idx);
            if (cur == null) return null;
        }
        return cur;
    }

    private Element nthChildByTag(Element parent, String tag, int idx1Based) {
        int seen = 0;
        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            Element e = (Element) n;
            if (!tag.equals(e.getTagName())) continue;
            seen++;
            if (seen == idx1Based) return e;
        }
        return null;
    }

    private static void putIfPresent(Map<String, Object> m, String k, String v) {
        if (v != null && !v.isBlank()) m.put(k, v);
    }

    private static String shortText(String s, int max) {
        if (s == null) return "";
        String t = s.replaceAll("\\s+", " ").trim();
        if (t.length() <= max) return t;
        return t.substring(0, Math.max(0, max - 3)) + "...";
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String firstNonBlank(String... xs) {
        if (xs == null) return null;
        for (String x : xs) {
            if (x != null && !x.isBlank()) return x;
        }
        return null;
    }

    private static final class Counter {
        private final int max;
        private int used = 0;
        private Counter(int max) { this.max = Math.max(1, max); }

        private boolean tryTake() {
            if (used >= max) return false;
            used++;
            return true;
        }

        private boolean isExhausted() { return used >= max; }
    }
}