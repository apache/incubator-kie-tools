package com.ait.lienzo.test.stub;

import com.ait.lienzo.test.annotation.StubClass;
import elemental2.dom.Event;
import elemental2.dom.EventInit;

@StubClass("elemental2.dom.UIEvent")
public class UIEvent extends Event {

    public UIEvent(String type) {
        this(type, null);
    }

    public UIEvent(String type, EventInit eventInitDict) {
        super(type, eventInitDict);
    }
}
