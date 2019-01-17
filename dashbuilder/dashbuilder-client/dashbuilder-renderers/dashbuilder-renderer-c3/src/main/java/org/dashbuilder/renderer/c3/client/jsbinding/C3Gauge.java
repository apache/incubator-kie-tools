package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Gauge {
    
    @JsOverlay
    static C3Gauge create(int min, int max) {
        C3Gauge gauge = new C3Gauge();
        gauge.setMin(min);
        gauge.setMax(max);
        return gauge;
    }
    
    @JsProperty
    public native void setMin(int min); 

    @JsProperty
    public native void setMax(int max);
    
}