package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3AxisX {
    
    @JsOverlay
    static C3AxisX create(String type, String[] categories, C3Tick tick) {
        C3AxisX instance = new C3AxisX();
        instance.setType(type);
        instance.setCategories(categories);
        instance.setTick(tick);
        return instance;
    }

    @JsProperty
    public native void setType(String type);
    
    @JsProperty
    public native void setCategories(String categories[]);
    
    @JsProperty
    public native void setTick(C3Tick tick);
    
    @JsProperty
    public native C3Tick getTick();
    
    @JsProperty
    public native void setShow(boolean show);
    
    @JsProperty
    public native void setLabel(C3AxisLabel label);
    
}
