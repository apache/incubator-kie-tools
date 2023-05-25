package org.kie.workbench.common.stunner.core.client.api;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class JsWindow {

    @JsProperty
    public static JsStunnerEditor editor;

    @Deprecated // Use editor.canvas instead
    @JsProperty
    public static Object canvas;
}