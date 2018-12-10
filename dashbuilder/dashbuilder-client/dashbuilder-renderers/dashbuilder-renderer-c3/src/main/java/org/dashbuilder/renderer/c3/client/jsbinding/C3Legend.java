package org.dashbuilder.renderer.c3.client.jsbinding;

import org.dashbuilder.displayer.Position;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Legend {
    
    @JsOverlay
    static C3Legend create(boolean show, String position) {
        C3Legend instance = new C3Legend();
        instance.setShow(show);
        instance.setPosition(position);
        return instance;
    }
    
    @JsOverlay
    public static String convertPosition(Position position) {
        // Not all positions are supported by C3.
        switch(position) {
        case BOTTOM:
            return position.name().toLowerCase();
        case IN:
            return "inset";
        case RIGHT:
            return position.name().toLowerCase();
        default:
            return "";
        }
    }
    
    @JsProperty
    public native void setShow(boolean show);
    
    @JsProperty
    public native void setPosition(String position);
    
}