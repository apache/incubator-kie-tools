package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3AxisY {
    
    @JsOverlay
    static C3AxisY create(boolean show, C3Tick tick) {
        C3AxisY instance = new C3AxisY();
        instance.setShow(show);
        instance.setTick(tick);
        return instance;
    }

    @JsProperty
    public native void setShow(boolean show);
    
    @JsProperty
    public native void setTick(C3Tick tick);
    
    @JsProperty
    public native void setLabel(C3AxisLabel label);    
    
        
}
