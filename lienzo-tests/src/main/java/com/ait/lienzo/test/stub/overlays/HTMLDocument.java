package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;

@StubClass("elemental2.dom.HTMLDocument")
public class HTMLDocument {

    public <T extends elemental2.dom.Element> T createElement(String tagName) {
        if (tagName.equals("canvas")) {
            return (T) new elemental2.dom.HTMLCanvasElement();
        } else if (tagName.equals("div")) {
            return (T) new elemental2.dom.HTMLDivElement();
        }

        return (T) new elemental2.dom.HTMLElement();
    }
}
