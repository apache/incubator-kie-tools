package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Grid {
    
    @JsOverlay
    static C3Grid create(C3GridConf x , C3GridConf y) {
        C3Grid instance = new C3Grid();
        instance.setX(x);
        instance.setY(y);
        return instance;
    }
    
    @JsProperty
    public native void setX(C3GridConf x);

    
    @JsProperty
    public native void setY(C3GridConf y);
}
