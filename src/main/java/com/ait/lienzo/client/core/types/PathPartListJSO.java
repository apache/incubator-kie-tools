package com.ait.lienzo.client.core.types;

import elemental2.core.JsIterable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsArrayLike;

@JsType(isNative = true, name = "Array", namespace = JsPackage.GLOBAL)
public class PathPartListJSO implements JsIterable<PathPartEntryJSO>,
                                               JsArrayLike<PathPartEntryJSO>
{
    @JsOverlay
    public static final PathPartListJSO make()
    {
        return new PathPartListJSO();
    }

    protected PathPartListJSO()
    {
    }

    public native int push(PathPartEntryJSO... var_args);

    @JsOverlay
    public final PathPartEntryJSO get(final int i)
    {
        return getAt(i);
    }

    @JsOverlay
    public final int length()
    {
        return getLength();
    }
}
