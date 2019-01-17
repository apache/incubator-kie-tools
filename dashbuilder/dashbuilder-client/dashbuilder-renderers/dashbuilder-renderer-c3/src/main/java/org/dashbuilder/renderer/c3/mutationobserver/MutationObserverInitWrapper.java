package org.dashbuilder.renderer.c3.mutationobserver;

import elemental2.dom.MutationObserverInit;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;


//This can be removed once Elemental is updated do RC1, where a create method will be available for MutationObserverInit
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class MutationObserverInitWrapper extends MutationObserverInit {
    
    @JsOverlay
    static MutationObserverInit create() {
      return Js.uncheckedCast(JsPropertyMap.of());
    }
    
}