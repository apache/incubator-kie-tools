package com.ait.lienzo.test.stub.overlays;

import java.util.Random;

import com.ait.lienzo.test.annotation.StubClass;
import elemental2.dom.Element;
import elemental2.dom.Window;

@StubClass("elemental2.dom.DomGlobal")
public class DomGlobal {

    public static final elemental2.dom.HTMLDocument document = new elemental2.dom.HTMLDocument();
    public static Window window = new elemental2.dom.Window();

    public static int requestAnimationFrame(
            elemental2.dom.FrameRequestCallback callback, Element element) {
        return new Random().nextInt();
    }

    public static double setTimeout(elemental2.dom.DomGlobal.SetTimeoutCallbackFn callback, double delay, Object... callbackParams) {
        return 0d;
    }
}
