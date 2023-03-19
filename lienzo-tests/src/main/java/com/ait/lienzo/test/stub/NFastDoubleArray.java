package com.ait.lienzo.test.stub;

import com.ait.lienzo.test.annotation.StubClass;
import com.ait.lienzo.test.stub.overlays.NFastPrimitiveArrayBase;

@StubClass("com.ait.lienzo.tools.client.collection.NFastDoubleArray")
public class NFastDoubleArray extends NFastPrimitiveArrayBase<NFastDoubleArray> {

    public static NFastDoubleArray make(final double d, final double... list) {
        final NFastDoubleArray jso = make();

        jso.push(d, list);

        return jso;
    }

    public static NFastDoubleArray make() {
        return new NFastDoubleArray();
    }

    public static final NFastDoubleArray makeFromDoubles(double... list) {
        NFastDoubleArray array = new NFastDoubleArray();
        for (double d : list) {
            array.push(d);
        }

        return array;
    }

    public NFastDoubleArray() {
    }

    public double[] toArray() {
        final int size = size();

        final double[] array = new double[size];

        for (int i = 0; i < size; i++) {
            array[i] = get(i);
        }
        return array;
    }

    public int push(final Object... list) {
        final int size = list.length;

        for (int i = 0; i < size; i++) {
            push(list[i]);
        }

        return this.list.size();
    }

    public void push(final double d, final double... list) {
        push(d);

        final int size = list.length;

        for (int i = 0; i < size; i++) {
            push(list[i]);
        }
    }

    public void push(final Object value) {
        list.add(value);
    }

    public void set(final int indx, final double value) {
        list.set(indx, value);
    }

    public Double get(final int indx) {
        return (Double) list.get(indx);
    }

    public double pop() {
        double result = 0;

        if (!list.isEmpty()) {
            final int i = list.size() - 1;

            result = (double) list.get(i);

            list.remove(i);
        }
        return result;
    }

    public Object shift() {
        return doShift();
    }

    public boolean contains(final double value) {
        return list.contains(value);
    }

    public static final com.ait.lienzo.tools.client.collection.NFastDoubleArray make2P(final double i, final double i1) {
        com.ait.lienzo.tools.client.collection.NFastDoubleArray array = new com.ait.lienzo.tools.client.collection.NFastDoubleArray();
        array.push(i, i1);
        return array;
    }
}
