package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Transition {
    
    @JsOverlay
    public static C3Transition create(int duration) {
        C3Transition instance = new C3Transition();
        instance.setDuration(duration);
        return instance;
    }


    @JsProperty
    public native void setDuration(int duration);

}
