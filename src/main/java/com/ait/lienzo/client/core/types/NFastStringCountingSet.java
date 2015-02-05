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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public final class NFastStringCountingSet
{
    private final FastStringCountingSetJSO m_jso = FastStringCountingSetJSO.make();

    public NFastStringCountingSet()
    {
    }

    public NFastStringCountingSet(final String key)
    {
        inc(key);
    }

    public NFastStringCountingSet(final String key, final String... keys)
    {
        inc(key, keys);
    }

    public NFastStringCountingSet(final Iterable<String> keys)
    {
        inc(keys);
    }

    public final NFastStringCountingSet inc(final String key)
    {
        if (null != key)
        {
            m_jso.inc(key);
        }
        return this;
    }

    public final NFastStringCountingSet inc(final String key, final String... keys)
    {
        inc(key);

        for (String k : keys)
        {
            inc(k);
        }
        return this;
    }

    public final NFastStringCountingSet inc(final Iterable<String> keys)
    {
        for (String k : keys)
        {
            inc(k);
        }
        return this;
    }

    public final NFastStringCountingSet dec(final String key)
    {
        if (null != key)
        {
            m_jso.dec(key);
        }
        return this;
    }

    public final NFastStringCountingSet dec(final String key, final String... keys)
    {
        dec(key);

        for (String k : keys)
        {
            dec(k);
        }
        return this;
    }

    public final NFastStringCountingSet dec(final Iterable<String> keys)
    {
        for (String k : keys)
        {
            dec(k);
        }
        return this;
    }

    public final boolean contains(final String key)
    {
        if (null != key)
        {
            return m_jso.contains(key);
        }
        return false;
    }

    public final int total()
    {
        return m_jso.total();
    }

    public final int total(final String key)
    {
        if (null != key)
        {
            return m_jso.total(key);
        }
        return 0;
    }

    public final Collection<String> keys()
    {
        final ArrayList<String> keys = new ArrayList<String>();

        m_jso.fillKeys(keys);

        return keys;
    }

    public final int size()
    {
        return m_jso.size();
    }

    public final NFastStringCountingSet clear()
    {
        m_jso.clear();

        return this;
    }

    public final boolean isEmpty()
    {
        return m_jso.isEmpty();
    }

    public final String toJSONString()
    {
        return new JSONObject(m_jso).toString();
    }

    @Override
    public final String toString()
    {
        return toJSONString();
    }

    private static final class FastStringCountingSetJSO extends JavaScriptObject
    {
        protected FastStringCountingSetJSO()
        {
        }

        static final FastStringCountingSetJSO make()
        {
            return JavaScriptObject.createObject().cast();
        }

        private final native boolean contains(String key)
        /*-{
            if (this.hasOwnProperty(String(key))) {
                var val = this[key];
                return ((val !== undefined) && (val > 0));
            }
            return false;
        }-*/;

        private final native void inc(String key)
        /*-{
        	if (this.hasOwnProperty(String(key))) {
        	    var val = this[key];
        	    if (val !== undefined) {
        	        this[key] = val + 1;
        	    } else {
        	        this[key] = 1;
        	    }
        	} else {
        	    this[key] = 1;
        	}
        }-*/;

        private final native void dec(String key)
        /*-{
            if (this.hasOwnProperty(String(key))) {                
                var val = this[key];
                if (val !== undefined) {
                    val = val - 1;
                    if (val < 1) {
                        delete this[key];
                    } else {
                        this[key] = val;
                    }
                }
            }
        }-*/;

        private final native void fillKeys(Collection<String> keys)
        /*-{
        	for ( var name in this) {
        		if (this.hasOwnProperty(String(name))) {
                    var val = this[name];
                    if ((val !== undefined) && (val > 0)) {
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
        		    var val = this[name];
                    if ((val !== undefined) && (val > 0)) {
        			    ++i;
        			}
        		}
        	}
        	return i;
        }-*/;

        private final native int total()
        /*-{
            var i = 0;

            for ( var name in this) {
                if (this.hasOwnProperty(String(name))) {
                    var val = this[name];
                    if ((val !== undefined) && (val > 0)) {
                        i = i + val;
                    }
                }
            }
            return i;
        }-*/;

        private final native int total(String key)
        /*-{
            if (this.hasOwnProperty(String(key))) {
                var val = this[key];
                if ((val !== undefined) && (val > 0)) {
                    return val;
                }
            }
            return 0;
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
                    var val = this[name];
                    if ((val !== undefined) && (val > 0)) {
                        return false;
                    }
                }
            }
            return true;
        }-*/;
    }
}
