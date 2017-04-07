package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;

@StubClass("com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO")
public class NFastDoubleArrayJSO extends NFastPrimitiveArrayBaseJSO<NFastDoubleArrayJSO> {

    public static NFastDoubleArrayJSO make(final double d,
                                           final double... list) {
        final NFastDoubleArrayJSO jso = make();

        jso.push(d,
                 list);

        return jso;
    }

    public static NFastDoubleArrayJSO make() {
        return new NFastDoubleArrayJSO();
    }

    protected NFastDoubleArrayJSO() {
    }

    public double[] toArray() {
        final int size = size();

        final double[] array = new double[size];

        for (int i = 0; i < size; i++) {
            array[i] = get(i);
        }
        return array;
    }

    public void push(final double d,
                     final double... list) {
        push(d);

        final int size = list.length;

        for (int i = 0; i < size; i++) {
            push(list[i]);
        }
    }

    public void push(double value) {
        list.add(value);
    }

    public void set(int indx,
                    double value) {
        list.set(indx,
                 value);
    }

    public double get(int indx) {
        return (double) list.get(indx);
    }

    public double pop() {
        double result = 0;

        if (!list.isEmpty()) {
            int i = list.size() - 1;

            result = (double) list.get(i);

            list.remove(i);
        }
        return result;
    }

    public double shift() {
        return doShift();
    }

    public boolean contains(double value) {
        return list.contains(value);
    }
}
