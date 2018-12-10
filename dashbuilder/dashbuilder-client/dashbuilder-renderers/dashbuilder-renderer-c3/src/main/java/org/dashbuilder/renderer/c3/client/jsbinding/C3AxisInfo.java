package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3AxisInfo {
    
    @JsOverlay
    static C3AxisInfo create(boolean rotated, C3AxisX x, C3AxisY y) {
        C3AxisInfo instance = new C3AxisInfo();
        instance.setRotated(rotated);
        instance.setX(x);
        instance.setY(y);
        return instance;
    }
    
    @JsProperty
    public native void setRotated(boolean rotated);
    
    @JsProperty
    public native void setX(C3AxisX x);
    
    @JsProperty
    public native C3AxisX getX();
    
    @JsProperty
    public native void setY(C3AxisY y);
    
    @JsProperty
    public native C3AxisY getY();
}
