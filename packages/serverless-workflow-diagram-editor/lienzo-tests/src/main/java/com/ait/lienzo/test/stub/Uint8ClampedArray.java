package com.ait.lienzo.test.stub;

import com.ait.lienzo.test.annotation.StubClass;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 10/30/19
 */
@StubClass("elemental2.core.Uint8ClampedArray")
public class Uint8ClampedArray {

    public int length = 0;
    private Double[] array;

    public Uint8ClampedArray() {
        array = new Double[256];
    }

    public Uint8ClampedArray(int length) {
        array = new Double[length];

        for (int i = 0; i < length; i++) {
            array[i] = 1d;
        }
    }

    public <T> T getAt(int index) {

        if (array.length < index) {
            return null;
        }

        return (T) array[index];
    }

    public <T> void setAt(int index, T value) {
        array[index] = (Double) value;
    }

    public int getLength() {
        return array.length;
    }
}
