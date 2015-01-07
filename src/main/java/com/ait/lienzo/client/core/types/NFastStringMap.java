/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.types;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Simple, super-fast minimal native Map that by default uses a String as a key, but does not fully implement the Map interface
 * 
 * For our purposes, in benchmarking, this is 50-60% faster than HashMap
 */
public final class NFastStringMap<V>
{
    private final NFastStringMapJSO<V> m_jso;

    public NFastStringMap()
    {
        m_jso = NFastStringMapJSO.make().cast();
    }

    /**
     * Add <key, value> to the map.
     * @param key
     * @param value
     */
    public final void put(String key, V value)
    {
        m_jso.put(key, value);
    }

    /**
     * Get the value based on the key passed in.
     * @param key
     * @return
     */
    public final V get(String key)
    {
        return m_jso.get(key);
    }

    /**
     * Remove the value based on the key passed in as argument.
     * @param key
     */
    public final void remove(String key)
    {
        m_jso.remove(key);
    }

    /**
     * Returns true if the map has a value for the specified key
     * @param key
     */
    public final boolean containsKey(String key)
    {
        return m_jso.containsKey(key);
    }

    /**
     * Returns the number of key-value mappings in this map
     */
    public final int size()
    {
        return m_jso.size();
    }

    /**
     * Returns true if this map contains no key-value mappings
     */
    public final boolean isEmpty()
    {
        return (m_jso.size() == 0);
    }

    private static final class NFastStringMapJSO<V> extends JavaScriptObject
    {
        protected NFastStringMapJSO()
        {
        }

        private static final JavaScriptObject make()
        {
            return JavaScriptObject.createObject();
        }

        public final native void put(String key, V value)
        /*-{
			this[key] = value;
        }-*/;

        public final native V get(String key)
        /*-{
			return this[key];
        }-*/;

        public final native void remove(String key)
        /*-{
			delete this[key];
        }-*/;

        public final native boolean containsKey(String key)
        /*-{
			return this.hasOwnProperty(String(key));
        }-*/;

        public final native int size()
        /*-{
			var i = 0;

			var self = this;

			for ( var name in self) {
				if (self.hasOwnProperty(String(name))) {
					++i;
				}
			}
			return i;
        }-*/;
    }
}
