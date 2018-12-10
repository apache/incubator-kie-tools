package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3ChartSize {
    
    @JsOverlay
    static C3ChartSize create(double width, double height) {
        C3ChartSize instance = new C3ChartSize();
        instance.setWidth(width);
        instance.setHeight(height);
        return instance;
    }


    @JsProperty
    public native void setWidth(double width);

    @JsProperty
    public native void setHeight(double height);

}
