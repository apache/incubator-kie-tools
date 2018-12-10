package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Padding {
    
    @JsOverlay
    static C3Padding create(int top, int right, int bottom, int left ) {
        C3Padding instance = new C3Padding();
        instance.setTop(top);
        instance.setRight(right);
        instance.setBottom(bottom);
        instance.setLeft(left);
        return instance;
    }
    
    @JsProperty
    public native void setTop(int top);
    
    @JsProperty
    public native void setRight(int right);
    
    @JsProperty
    public native void setBottom(int bottom);
    
    @JsProperty
    public native void setLeft(int left);
    
}