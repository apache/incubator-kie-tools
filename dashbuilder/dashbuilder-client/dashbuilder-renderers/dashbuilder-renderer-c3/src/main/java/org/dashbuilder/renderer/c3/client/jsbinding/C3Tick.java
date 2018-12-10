package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Tick {
    
    @JsOverlay
    static C3Tick create(FormatterCallback callback) {
        C3Tick instance = new C3Tick();
        instance.setFormat(callback);
        return instance;
    }

    @JsProperty
    public native void setFormat(FormatterCallback callback);
    
    @JsProperty
    public native void setFit(boolean fit);
    
    @JsProperty
    public native void setRotate(int rotate);

    @JsFunction
    @FunctionalInterface
    public interface FormatterCallback {
        
        String callback(String value);
    
    }
        
}
