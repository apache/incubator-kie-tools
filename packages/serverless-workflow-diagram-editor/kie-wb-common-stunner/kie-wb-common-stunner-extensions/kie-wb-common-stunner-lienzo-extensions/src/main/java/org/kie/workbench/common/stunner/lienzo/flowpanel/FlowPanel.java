package org.kie.workbench.common.stunner.lienzo.flowpanel;

import java.util.HashMap;
import java.util.Map;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;

public class FlowPanel implements IsElement {

    private final Map<IsElement, HTMLElement> children = new HashMap<>();

    private final HTMLDivElement root = (HTMLDivElement) DomGlobal.document.createElement("div");

    public void add(IsElement element) {
        HTMLElement child = element.getElement();
        children.put(element, child);
        root.appendChild(child);
    }

    public void remove(IsElement element) {
        HTMLElement child = children.remove(element);
        if (child != null) {
            root.removeChild(child);
        }
    }

    public void clear() {
        children.forEach((element, htmlElement) -> root.removeChild(htmlElement));
        children.clear();
    }

    @Override
    public HTMLElement getElement() {
        return root;
    }

    public boolean isVisible() {
        return !root.style.display.equals("none");
    }
}
