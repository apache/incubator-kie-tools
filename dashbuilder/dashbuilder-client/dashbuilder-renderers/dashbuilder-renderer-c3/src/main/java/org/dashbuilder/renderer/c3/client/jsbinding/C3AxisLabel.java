package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3AxisLabel {
    
    @JsOverlay
    static C3AxisLabel create(String text, String position) {
        C3AxisLabel instance = new C3AxisLabel();
        instance.setText(text);
        instance.setPosition(position);
        return instance;
    }
    
    @JsProperty
    public native void setText(String text);

    
    @JsProperty
    public native void setPosition(String position);
}
