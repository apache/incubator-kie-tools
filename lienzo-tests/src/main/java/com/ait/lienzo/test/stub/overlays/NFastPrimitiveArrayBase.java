package com.ait.lienzo.test.stub.overlays;

public class NFastPrimitiveArrayBase<T extends NFastPrimitiveArrayBase<T>> extends NArrayBase<T> {

    protected NFastPrimitiveArrayBase() {
    }

    public T sort() {
        return copy();
    }

    public T uniq() {
        return copy();
    }
}
