package org.dashbuilder.renderer.c3.client.jsbinding;

import com.google.gwt.dom.client.DivElement;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Chart {
    
    @JsProperty
    public native DivElement getElement();
    
    @JsProperty
    public native C3Legend getLegend();
    
    public native void flush();
    
    public native void select(String[] points);
    
    public native void focus(String points);
    
    public native void defocus();
    
}