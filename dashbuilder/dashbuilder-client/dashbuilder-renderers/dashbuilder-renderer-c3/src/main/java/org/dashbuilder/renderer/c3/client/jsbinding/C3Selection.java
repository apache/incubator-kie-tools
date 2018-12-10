package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Selection {
    
    @JsOverlay
    static C3Selection create(boolean enabled, boolean multiple, boolean grouped) {
        C3Selection c3Selection = new C3Selection();
        c3Selection.setEnabled(enabled);
        c3Selection.setMultiple(multiple);
        c3Selection.setGrouped(grouped);
        return c3Selection;
    }
    
    @JsProperty
    public native void setEnabled(boolean enabled);

    @JsProperty
    public native void setMultiple(boolean multiple);
    
    @JsProperty
    public native void setGrouped(boolean grouped);

}
