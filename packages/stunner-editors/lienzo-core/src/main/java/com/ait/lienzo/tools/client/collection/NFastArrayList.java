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

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * A facade implementation in JavaScript for fast Lists.
 */
@JsType(isNative = true, name = "Array", namespace = JsPackage.GLOBAL)
public final class NFastArrayList<M> extends NArrayBase<M> {

    @JsOverlay
    public static <M> NFastArrayList<M> fromObjects(M... objects) {
        final NFastArrayList<M> list = new NFastArrayList<>();
        for (M item : objects) {
            list.add(item);
        }
        return list;
    }

    public NFastArrayList() {
    }

    /**
     * Return the primitive found at the specified index.
     *
     * @param index
     * @return
     */
    @JsOverlay
    public final M get(final int index) {
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
    public final NFastArrayList<M> add(final M value) {
        push(value);

        return this;
    }

    /**
     * Add a value to the List
     *
     * @param value
     */
    @JsOverlay
    public final NFastArrayList<M> set(final int i, final M value) {
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
    public final boolean contains(final M value) {
        return indexOf(value) > -1;
    }

    public native int indexOf(M obj);

    @JsOverlay
    public final NFastArrayList<M> copy() {
        NFastArrayList<M> list = new NFastArrayList<>();
        M[] array = slice(0);
        list.push(array);
        return list;
    }

    /**
     * Remove the value passed in as argument from the List.
     *
     * @param value
     */
    @JsOverlay
    public final NFastArrayList<M> remove(final M value) {
        for (int i = 0; i < getLength(); i++) {
            if (getAt(i) == value) {
                this.splice(i, 1);
                break;
            }
        }
        return this;
    }

    @JsOverlay
    public final NFastArrayList<M> moveUp(final M value) {
        int leng = getLength();
        if (leng < 2) {
            return this;
        }
        for (int i = 0; i < leng; i++) {
            if (getAt(i) == value) {
                int j = i + 1;
                if (j != leng) {
                    setAt(i, getAt(j));
                    setAt(j, value);
                }
                break;
            }
        }

        return this;
    }

    @JsOverlay
    public final NFastArrayList<M> moveDown(final M value) {
        int leng = getLength();
        if (leng < 2) {
            return this;
        }

        for (int i = 0; i < leng; i++) {
            if (getAt(i) == value) {
                if (i != 0) {
                    int j = i - 1;
                    setAt(i, getAt(j));
                    setAt(j, value);
                }
                break;
            }
        }

        return this;
    }

    @JsOverlay
    public final NFastArrayList<M> moveToTop(final M value) {
        if ((size() < 2) || (!contains(value))) {
            return this;
        }
        remove(value);

        add(value);

        return this;
    }

    @JsOverlay
    public final NFastArrayList<M> moveToBottom(final M value) {
        if ((size() < 2) || (!contains(value))) {
            return this;
        }
        remove(value);

        unshift(value);

        return this;
    }
}
