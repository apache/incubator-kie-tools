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

import elemental2.core.JsIterable;
import elemental2.core.JsIteratorIterable;
import elemental2.core.JsMap;
import elemental2.core.JsMap.EntriesJsIteratorIterableTypeParameterArrayUnionType;
import elemental2.core.JsMap.JsIterableTypeParameterArrayUnionType;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Simple, super-fast minimal native Map that by default uses a String as a key, but does not fully implement the Map interface
 * 
 * For our purposes, in benchmarking, this is 50-60% faster than HashMap
 */

@JsType(isNative = true, name = "Map", namespace = JsPackage.GLOBAL)
public final class NFastStringMap<V> implements JsIterable<JsIterableTypeParameterArrayUnionType<String, V>[]>
{
    public NFastStringMap()
    {
    }

    /**
     * Add <key, value> to the map.
     * @param key
     * @param value
     */
    @JsOverlay
    public final NFastStringMap<V> put(final String key, final V value)
    {
        set(key, value);

        return this;
    }


    /**
     * Remove the value based on the key passed in as argument.
     * @param key
     */
    @JsOverlay
    public final NFastStringMap<V> remove(final String key)
    {
        delete(key);
        return this;
    }

    /**
     * Returns true if the map has a value for the specified key
     * @param key
     */
    @JsOverlay
    public final boolean isDefined(final String key)
    {
        return has(key);
    }

    @JsOverlay
    public final boolean isNull(final String key)
    {
        return has(key) && get(key) != null;
    }

    /**
     * Returns the number of key-value mappings in this map
     */
    @JsOverlay
    public final int size()
    {
        return size;
    }

    /**
     * Returns true if this map contains no key-value mappings
     */
    @JsOverlay
    public final boolean isEmpty() {
        return 0 == this.size();
    }


    /**
     * Get the value based on the key passed in.
     * @param key
     * @return
     */
    public native final V get(final String key);

    public native NFastStringMap<V> set(String key, V value);

    public int size;

    public native void clear();

    public native boolean delete(String key);

    public native JsIteratorIterable<
            EntriesJsIteratorIterableTypeParameterArrayUnionType<String, V>[]>
    entries();

    public native Object forEach(
            JsMap.ForEachCallbackFn<? super String, ? super V> callback, Object thisArg);

    public native Object forEach(JsMap.ForEachCallbackFn<? super String, ? super V> callback);

    public native boolean has(String key);

    public native JsIteratorIterable<String> keys();

    public native JsIteratorIterable<V> values();

}
