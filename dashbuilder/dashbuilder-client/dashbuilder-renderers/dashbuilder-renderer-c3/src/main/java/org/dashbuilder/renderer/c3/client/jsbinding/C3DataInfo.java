package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3DataInfo {
    
    @JsProperty
    public native String getId();
    
    @JsProperty
    public native int getIndex();
    
    @JsProperty
    public native String getX();
    
    @JsProperty
    public native String getValue();
    
    @JsProperty
    public native String getName();
    
}