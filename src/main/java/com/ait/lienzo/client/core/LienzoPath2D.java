package com.ait.lienzo.client.core;

import elemental2.dom.Path2D;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * This currently is only used by SVGPath and exists only to track if the Path2D was closed or not, and thus should be filled
 */
@JsType(isNative = true, name="Path2D", namespace = JsPackage.GLOBAL)
public class LienzoPath2D extends Path2D
{
    public boolean closed;

    @JsOverlay
    public final boolean isClosed()
    {
        return closed;
    }

}
