package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3GridConf {
    
    @JsOverlay
    static C3GridConf create(boolean show) {
        C3GridConf instance = new C3GridConf();
        instance.setShow(show);
        return instance;
    }
    
    @JsProperty
    public native void setShow(boolean show);
}
