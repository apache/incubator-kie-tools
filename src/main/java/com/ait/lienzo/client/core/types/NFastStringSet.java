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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public class NFastStringSet implements Iterable<String>
{
    private final FastStringSetJSO m_jso;

    public NFastStringSet(final FastStringSetJSO jso)
    {
        m_jso = jso;
    }

    public NFastStringSet()
    {
        this(FastStringSetJSO.make());
    }

    public NFastStringSet(final String key)
    {
        this();

        add(key);
    }

    public NFastStringSet(final String key, final String... keys)
    {
        this();

        add(key, keys);
    }

    public NFastStringSet(final Iterable<String> keys)
    {
        this();

        add(keys);
    }

    public NFastStringSet(final NFastStringSet nset)
    {
        this();

        add(nset);
    }

    public final NFastStringSet add(final String key)
    {
        m_jso.add(key);

        return this;
    }

    public final NFastStringSet add(final String key, final String... keys)
    {
        m_jso.add(key);

        for (String k : keys)
        {
            m_jso.add(k);
        }
        return this;
    }

    public final NFastStringSet add(final Iterable<String> keys)
    {
        for (String key : keys)
        {
            m_jso.add(key);
        }
        return this;
    }

    public final NFastStringSet add(final NFastStringSet nset)
    {
        m_jso.add(nset.m_jso);

        return this;
    }

    public final boolean contains(String key)
    {
        return m_jso.contains(key);
    }

    public final NFastStringSet remove(String key)
    {
        m_jso.remove(key);

        return this;
    }

    public final Collection<String> keys()
    {
        ArrayList<String> keys = new ArrayList<String>();

        m_jso.fillKeys(keys);

        return Collections.unmodifiableCollection(keys);
    }

    public final int size()
    {
        return m_jso.size();
    }

    public final NFastStringSet clear()
    {
        m_jso.clear();

        return this;
    }

    public final boolean isEmpty()
    {
        return m_jso.isEmpty();
    }

    @Override
    public final Iterator<String> iterator()
    {
        return Collections.unmodifiableCollection(keys()).iterator();
    }

    public final String toJSONString()
    {
        JSONObject object = new JSONObject(m_jso);

        return object.toString();
    }

    private static final class FastStringSetJSO extends JavaScriptObject
    {
        protected FastStringSetJSO()
        {
        }

        static final FastStringSetJSO make()
        {
            return JavaScriptObject.createObject().cast();
        }

        private final native boolean contains(String key)
        /*-{
            if (this.hasOwnProperty(String(key))) {
                return (this[key] == true);
            }
            return false;
        }-*/;

        private final native boolean add(String key)
        /*-{
        	if (this.hasOwnProperty(String(key))) {
        	    if (this[key] == true) {
                    return true;
        	    }
        	}
        	this[key] = true;

        	return false;
        }-*/;

        private final native boolean remove(String key)
        /*-{
            if (this.hasOwnProperty(String(key))) {                
                delete this[key];
                return true;
            }
            return false;
        }-*/;

        private final native void add(FastStringSetJSO nset)
        /*-{
        	for ( var name in nset) {
        		if (nset.hasOwnProperty(String(name))) {
        		    if (nset[name] == true) {
        			    this[name] = true;
        			}
        		}
        	}
        }-*/;

        private final native void fillKeys(Collection<String> keys)
        /*-{
        	for ( var name in this) {
        		if (this.hasOwnProperty(String(name))) {
        		    if (this[name] == true) {
        			    keys.@java.util.Collection::add(Ljava/lang/Object;)(name);
        			}
        		}
        	}
        }-*/;

        private final native int size()
        /*-{
        	var i = 0;

        	for ( var name in this) {
        		if (this.hasOwnProperty(String(name))) {
        		    if (this[name] == true) {
        			    ++i;
        			}
        		}
        	}
        	return i;
        }-*/;

        private final native void clear()
        /*-{
        	for ( var name in this) {
        		if (this.hasOwnProperty(String(name))) {
        			delete this[name];
        		}
        	}
        }-*/;

        private final native boolean isEmpty()
        /*-{
            for ( var name in this) {
                if (this.hasOwnProperty(String(name))) {
                    if (this[name] == true) {
                        return false;
                    }
                }
            }
            return true;
        }-*/;
    }
}
