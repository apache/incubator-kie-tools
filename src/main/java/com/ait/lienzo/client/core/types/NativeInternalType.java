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

import java.util.List;

import com.ait.lienzo.shared.core.types.EnumWithValue;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Used internally by the toolkit to map JSON types.
 */
public enum NativeInternalType implements EnumWithValue
{
    STRING("string"), NUMBER("number"), BOOLEAN("boolean"), FUNCTION("function"), ARRAY("array"), OBJECT("object"), UNDEFINED("undefined"), NULL("null");

    private final String                                    m_value;

    private final static NativeInternalTypeOps              NOPS       = NativeInternalTypeOps.make();

    private static final NFastStringMap<NativeInternalType> LOOKUP_MAP = Statics.build(NativeInternalType.values());

    private NativeInternalType(String value)
    {
        m_value = value;
    }

    @Override
    public final String getValue()
    {
        return m_value;
    }

    @Override
    public final String toString()
    {
        return m_value;
    }

    public static final NativeInternalType lookup(String key)
    {
        return Statics.lookup(key, LOOKUP_MAP, null);
    }

    public static final List<String> getKeys()
    {
        return Statics.getKeys(NativeInternalType.values());
    }

    public static final List<NativeInternalType> getValues()
    {
        return Statics.getValues(NativeInternalType.values());
    }

    private final static native NativeInternalType getNativeTypeOfWithOps(NativeInternalTypeOps nops, JavaScriptObject jso, String name)
    /*-{
        return nops.getNativeTypeOf(jso[name]);
    }-*/;

    private final static native NativeInternalType getNativeTypeOfWithOps(NativeInternalTypeOps nops, JavaScriptObject jso, int index)
    /*-{
        return nops.getNativeTypeOf(jso[index]);
    }-*/;

    private final static native NativeInternalType getNativeTypeOfWithOps(NativeInternalTypeOps nops, JavaScriptObject jso)
    /*-{
        return nops.getNativeTypeOf(jso);
    }-*/;

    public final static NativeInternalType getNativeTypeOf(JavaScriptObject jso)
    {
        if (null == jso)
        {
            return NULL;
        }
        return getNativeTypeOfWithOps(NOPS, jso);
    }

    public final static NativeInternalType getNativeTypeOf(NBaseNativeArrayJSO<?> jso, int index)
    {
        if ((null != jso) && (index >= 0) && (index < jso.size()))
        {
            return getNativeTypeOfWithOps(NOPS, jso, index);
        }
        return UNDEFINED;
    }

    public final static NativeInternalType getNativeTypeOf(JavaScriptObject jso, String name)
    {
        if ((null != jso) && (null != name))
        {
            return getNativeTypeOfWithOps(NOPS, jso, name);
        }
        return UNDEFINED;
    }

    private static final class NativeInternalTypeOps extends JavaScriptObject
    {
        public static final NativeInternalTypeOps make()
        {
            NativeInternalTypeOps self = JavaScriptObject.createObject().cast();

            self.init();

            return self;
        }

        protected NativeInternalTypeOps()
        {
        }

        private final native void init()
        /*-{
            this.getNativeTypeOf = function(value) {
                if (null == value) {
                    return @com.ait.lienzo.client.core.types.NativeInternalType::NULL;
                }
                var type = typeof value;

                switch (type) {
                case 'string': {
                    return @com.ait.lienzo.client.core.types.NativeInternalType::STRING;
                }
                case 'boolean': {
                    return @com.ait.lienzo.client.core.types.NativeInternalType::BOOLEAN;
                }
                case 'number': {
                    if (isFinite(value)) {
                        return @com.ait.lienzo.client.core.types.NativeInternalType::NUMBER;
                    }
                    return @com.ait.lienzo.client.core.types.NativeInternalType::UNDEFINED;
                }
                case 'object': {
                    if ((value instanceof Array)
                            || (value instanceof $wnd.Array)) {
                        return @com.ait.lienzo.client.core.types.NativeInternalType::ARRAY;
                    }
                    if (value === Object(value)) {
                        return @com.ait.lienzo.client.core.types.NativeInternalType::OBJECT;
                    }
                    return @com.ait.lienzo.client.core.types.NativeInternalType::UNDEFINED;
                }
                case 'function': {
                    return @com.ait.lienzo.client.core.types.NativeInternalType::FUNCTION;
                }
                }
                return @com.ait.lienzo.client.core.types.NativeInternalType::UNDEFINED;
            };
        }-*/;
    }
}
