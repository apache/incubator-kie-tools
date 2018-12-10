package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "c3")
public class C3 {
    
    @JsMethod
    public static native C3Chart generate(C3ChartConf conf);
    
}
