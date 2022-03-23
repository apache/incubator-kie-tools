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

package com.ait.lienzo.tools.client;

import elemental2.core.Global;
import elemental2.core.JsIterable;
import elemental2.core.JsIteratorIterable;
import elemental2.core.JsMap;
import elemental2.core.JsMap.JsIterableTypeParameterArrayUnionType;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, name = "Map", namespace = JsPackage.GLOBAL)
public class NObjectJSO implements JsIterable<JsIterableTypeParameterArrayUnionType<String, ?>[]> {

    @JsOverlay
    public static final NObjectJSO make() {
        return new NObjectJSO(); //createNObjectBaseJSO();
    }

    protected NObjectJSO() {
    }

    /**
     * Returns the number of key-value mappings in this map
     */
    @JsOverlay
    public final int size() {
        return size;
    }

    /**
     * Returns true if this map contains no key-value mappings
     */
    @JsOverlay
    public final boolean isEmpty() {
        return 0 == this.size();
    }

    public int size;

    /**
     * Get the value based on the key passed in.
     *
     * @param key
     * @return
     */
    public native final Object get(final String key);

    public native void clear();

    public native boolean delete(String key);

    public native boolean has(String key);

    public native JsIteratorIterable<String> keys();

    public native JsMap<String, ?> set(String key, Object value);

    public native JsIteratorIterable<?> values();

    @JsOverlay
    public final void put(final String name, final int value) {
        set(name, value);
    }

    @JsOverlay
    public final void put(final String name, final double value) {
        set(name, value);
    }

    @JsOverlay
    public final void put(final String name, final boolean value) {
        set(name, value);
    }

    @JsOverlay
    public final void put(final String name, final String value) {
        set(name, value);
    }

    @JsOverlay
    public final int getAsInteger(final String name) {
        Object o = get(name);
        if (o == null || o == Global.undefined) {
            return 0;
        }
        return Js.cast(o);
    }

    @JsOverlay
    public final double getAsDouble(final String name) {
        Object o = get(name);
        if (o == null || o == Global.undefined) {
            return 0;
        }
        return Js.cast(o);
    }

    @JsOverlay
    public final boolean getAsBoolean(final String name) {
        Object o = get(name);
        if (o == null || o == Global.undefined) {
            return false;
        }
        return Js.cast(o);
    }

    @JsOverlay
    public final String getAsString(final String name) {
        Object o = get(name);
        if (o == null || o == Global.undefined) {
            return null;
        }
        return Js.cast(o);
    }
}