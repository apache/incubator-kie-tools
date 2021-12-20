package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;
import elemental2.dom.CanvasGradient;
import elemental2.dom.CanvasPattern;
import jsinterop.base.Js;

@StubClass("elemental2.dom.CanvasRenderingContext2D.FillStyleUnionType")
public class FillStyleUnionType {

    static elemental2.dom.CanvasRenderingContext2D.FillStyleUnionType of(Object o) {
        return null;
    }

    public CanvasGradient asCanvasGradient() {
        return Js.cast(this);
    }

    public CanvasPattern asCanvasPattern() {
        return Js.cast(this);
    }

    public String asString() {
        return Js.asString(this);
    }

    public boolean isCanvasGradient() {
        return (Object) this instanceof CanvasGradient;
    }

    public boolean isCanvasPattern() {
        return (Object) this instanceof CanvasPattern;
    }

    public boolean isString() {
        return (Object) this instanceof String;
    }
}
