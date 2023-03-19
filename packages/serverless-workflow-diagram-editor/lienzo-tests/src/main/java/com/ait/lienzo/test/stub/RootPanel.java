package com.ait.lienzo.test.stub;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.test.annotation.StubClass;
import elemental2.dom.Element;

@StubClass("com.ait.lienzo.client.widget.RootPanel")
public class RootPanel {

    static RootPanel INSTANCE = new RootPanel();

    private List<Element> elements = new ArrayList<>();

    public static RootPanel get() {
        return INSTANCE;
    }

    public void add(Element child) {
        elements.add(child);
    }

    public void remove(Element child) {
        child.remove();
    }
}
