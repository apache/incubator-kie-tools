/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.tools.client.collection;

import elemental2.core.JsArray;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;

@JsType(isNative = true, name = "Array", namespace = JsPackage.GLOBAL)
public class NFastDoubleArray extends NArrayBase<Double> {

    public NFastDoubleArray() {
    }

    @JsOverlay
    public static final NFastDoubleArray makeFromDoubles(double... list) {
        NFastDoubleArray array = new NFastDoubleArray();
        for (double d : list) {
            array.push(d);
        }

        return array;
    }

    @JsOverlay
    public static final NFastDoubleArray make2P(final double i, final double i1) {
        NFastDoubleArray array = new NFastDoubleArray();
        array.push(i, i1);
        return array;
    }

    /**
     * Return the primitive found at the specified index.
     *
     * @param index
     * @return
     */
    @JsOverlay
    public final Double get(final int index) {
        if ((index >= 0) && (index < size())) {
            return getAt(index);
        }
        return null;
    }

    /**
     * Add a value to the List
     *
     * @param value
     */
    @JsOverlay
    public final NFastDoubleArray add(final Double value) {
        push(value);

        return this;
    }

    /**
     * Add a value to the List
     *
     * @param value
     */
    @JsOverlay
    public final NFastDoubleArray set(final int i, final Double value) {
        setAt(i, value);

        return this;
    }

    /**
     * Return true if the List contains the passed in value.
     *
     * @param value
     * @return boolean
     */
    @JsOverlay
    public final boolean contains(final Double value) {
        return indexOf(value) > -1;
    }

    public native int indexOf(Double obj);

    @JsOverlay
    public final double[] toArray() {
        double[] d = Js.uncheckedCast(JsArray.from((JsArrayLike<Double>) this));
        return d;
    }
}
