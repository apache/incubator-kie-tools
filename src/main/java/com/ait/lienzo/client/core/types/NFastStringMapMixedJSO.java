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
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.json.client.JSONObject;

public final class NFastStringMapMixedJSO extends JavaScriptObject
{
    protected NFastStringMapMixedJSO()
    {
    }

    public static final NFastStringMapMixedJSO make()
    {
        return JavaScriptObject.createObject().cast();
    };

    public final native boolean isEmpty()
    /*-{
        var that = this;

        for ( var i in that) {
            return false;
        }
        return true;
    }-*/;

    public final void put(String name, String value)
    {
        if (null != value)
        {
            put0(name, value.substring(0));
        }
        else
        {
            delete(name);
        }
    }

    private final native void put0(String name, String value)
    /*-{
        this[name] = value;
    }-*/;

    public final native void put(String name, int value)
    /*-{
        this[name] = value;
    }-*/;

    public final native void put(String name, double value)
    /*-{
        this[name] = value;
    }-*/;

    public final native void put(String name, boolean value)
    /*-{
        this[name] = value;
    }-*/;

    public final void put(String name, JavaScriptObject value)
    {
        if (null != value)
        {
            put0(name, value);
        }
        else
        {
            delete(name);
        }
    }

    private final native void put0(String name, JavaScriptObject value)
    /*-{
        this[name] = value;
    }-*/;

    public final Collection<String> keys()
    {
        ArrayList<String> keys = new ArrayList<String>();

        fill(keys);

        return keys;
    }

    private final native void fill(Collection<String> keys)
    /*-{
        var self = this;

        for ( var name in self) {
            if ((self.hasOwnProperty(name)) && (self[name] !== undefined)) {
                keys.@java.util.Collection::add(Ljava/lang/Object;)(name);
            }
        }
    }-*/;

    public final native boolean isDefined(String name)
    /*-{
        return this.hasOwnProperty(String(name));
    }-*/;

    public final native double getDouble(String name)
    /*-{
        return this[name];
    }-*/;

    public final native int getInteger(String name)
    /*-{
        return Math.round(this[name]);
    }-*/;

    public final native String getString(String name)
    /*-{
        return this[name];
    }-*/;

    public final native boolean getBoolean(String name)
    /*-{
        return this[name];
    }-*/;

    public final native JavaScriptObject getObject(String name)
    /*-{
        return this[name];
    }-*/;

    public final native JsArray<JavaScriptObject> getArrayOfJSO(String name)
    /*-{
        return this[name];
    }-*/;

    public final native JsArrayMixed getArray(String name)
    /*-{
        return this[name];
    }-*/;

    public final native void delete(String name)
    /*-{
        delete this[name];
    }-*/;

    public final native NativeInternalType typeOf(String name)
    /*-{
        if (this.hasOwnProperty(String(name)) && (this[name] !== undefined)) {

            var valu = this[name];

            var type = typeof valu;

            switch (type) {
            case 'string': {
                return @com.ait.lienzo.client.core.types.NativeInternalType::STRING;
            }
            case 'boolean': {
                return @com.ait.lienzo.client.core.types.NativeInternalType::BOOLEAN;
            }
            case 'number': {
                if (isFinite(valu)) {
                    return @com.ait.lienzo.client.core.types.NativeInternalType::NUMBER;
                }
                return @com.ait.lienzo.client.core.types.NativeInternalType::UNDEFINED;
            }
            case 'object': {
                if ((valu instanceof Array) || (valu instanceof $wnd.Array)) {
                    return @com.ait.lienzo.client.core.types.NativeInternalType::ARRAY;
                }
                return @com.ait.lienzo.client.core.types.NativeInternalType::OBJECT;
            }
            case 'function': {
                return @com.ait.lienzo.client.core.types.NativeInternalType::FUNCTION;
            }
            }
        }
        return @com.ait.lienzo.client.core.types.NativeInternalType::UNDEFINED;
    }-*/;

    public static final native NativeInternalType typeOf(JavaScriptObject valu)
    /*-{
        if (valu) {

            var type = typeof valu;

            switch (type) {
            case 'string': {
                return @com.ait.lienzo.client.core.types.NativeInternalType::STRING;
            }
            case 'boolean': {
                return @com.ait.lienzo.client.core.types.NativeInternalType::BOOLEAN;
            }
            case 'number': {
                if (isFinite(valu)) {
                    return @com.ait.lienzo.client.core.types.NativeInternalType::NUMBER;
                }
                return @com.ait.lienzo.client.core.types.NativeInternalType::UNDEFINED;
            }
            case 'object': {
                if ((valu instanceof Array) || (valu instanceof $wnd.Array)) {
                    return @com.ait.lienzo.client.core.types.NativeInternalType::ARRAY;
                }
                return @com.ait.lienzo.client.core.types.NativeInternalType::OBJECT;
            }
            case 'function': {
                return @com.ait.lienzo.client.core.types.NativeInternalType::FUNCTION;
            }
            }
        }
        return @com.ait.lienzo.client.core.types.NativeInternalType::UNDEFINED;
    }-*/;

    public final String getString(String name, JavaScriptObject dict)
    {
        if (typeOf(dict) == NativeInternalType.OBJECT)
        {
            NFastStringMapMixedJSO njso = dict.cast();

            return njso.getString(name);
        }
        return null;
    }
    
    public final String toJSONString()
    {
        return new JSONObject(this).toString();
    }
}
