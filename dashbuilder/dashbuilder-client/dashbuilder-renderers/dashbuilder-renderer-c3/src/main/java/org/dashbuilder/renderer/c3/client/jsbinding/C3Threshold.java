package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Threshold {
    
    @JsOverlay
    static C3Threshold create(int[] values) {
        C3Threshold threshold = new C3Threshold();
        threshold.setValues(values);
        return threshold;
    }

    @JsProperty
    public native void setValues(int[] values); 

}