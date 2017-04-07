package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;

@StubClass("com.ait.tooling.nativetools.client.collection.NFastPrimitiveArrayBaseJSO")
public class NFastPrimitiveArrayBaseJSO<T extends NFastPrimitiveArrayBaseJSO<T>> extends NArrayBaseJSO<T> {


    protected NFastPrimitiveArrayBaseJSO() {
    }

    public T sort() {
        // TODO
        return copy();
    }

    public T uniq() {
        // TODO
        return copy();
    }
}
