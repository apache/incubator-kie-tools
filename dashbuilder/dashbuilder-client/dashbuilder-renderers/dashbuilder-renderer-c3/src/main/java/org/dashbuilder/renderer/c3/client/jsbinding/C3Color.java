package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Color {
    
    @JsOverlay
    static C3Color create(String[] pattern, C3Threshold threshold) {
        C3Color color = new C3Color();
        color.setPattern(pattern);
        color.setThreshold(threshold);
        return color;
    }
    
    @JsProperty
    public native void setPattern(String[] pattern); 
    
    @JsProperty
    public native void setThreshold(C3Threshold threshold); 

}
