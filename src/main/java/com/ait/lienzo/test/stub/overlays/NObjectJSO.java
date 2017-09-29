/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.test.stub.overlays;

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.test.annotation.StubClass;
import com.ait.lienzo.test.util.LienzoMockitoLogger;
import com.ait.tooling.nativetools.client.NHasJSO;
import com.ait.tooling.nativetools.client.NValue;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * In-memory Map implementation sub for class <code>com.ait.tooling.nativetools.client.NObjectJSO</code>.
 *
 * @author Roger Martinez
 * @since 1.0
 *
 */
@StubClass("com.ait.tooling.nativetools.client.NObjectJSO")
public class NObjectJSO extends JavaScriptObject
{
    private final Map<String, Object> attributes = new HashMap<String, Object>();

    public static NObjectJSO make()
    {
        return new NObjectJSO();
    }

    protected NObjectJSO()
    {
        LienzoMockitoLogger.log("NObjectJSO", "Creating custom Lienzo overlay type.");
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(final Object jso)
    {
        if (null != jso)
        {
            return (T) jso;
        }
        return null;
    }

    public void put(final String name, final int value)
    {
        attributes.put(name, value);
    }

    public void put(final String name, final double value)
    {
        attributes.put(name, value);
    }

    public void put(final String name, final boolean value)
    {
        attributes.put(name, value);
    }

    public void put(final String name, final String value)
    {
        attributes.put(name, value);
    }

    public void put(final String name, final NHasJSO<? extends JavaScriptObject> value)
    {
        if (null != value)
        {
            attributes.put(name, value);
        }
        else
        {
            attributes.put(name, value);
        }
    }

    public void put(final String name, final JavaScriptObject value)
    {
        attributes.put(name, value);
    }

    public NValue<?> getAsNValue(final String name)
    {
        return (NValue<?>) attributes.get(name);
    }

    public JavaScriptObject getAsJSO(final String name)
    {
        return (JavaScriptObject) attributes.get(name);
    }

    public int getAsInteger(final String name)
    {
        return (Integer) attributes.get(name);
    }

    public double getAsDouble(final String name)
    {
        return (Double) attributes.get(name);
    }

    public boolean getAsBoolean(final String name)
    {
        return (Boolean) attributes.get(name);
    }

    public String getAsString(final String name)
    {
        return (String) attributes.get(name);
    }

    public String getAsString(final String name, final String otherwise)
    {
        final String value = (String) attributes.get(name);

        return ((null != value) ? value : otherwise);
    }

    public boolean isArray(final String name)
    {
        return attributes.get(name) instanceof JavaScriptObject;
    }

    public boolean isEmpty()
    {
        return attributes.isEmpty();
    }

    public boolean isString(final String name)
    {
        return (attributes.get(name) != null) && (attributes.get(name) instanceof String);
    }

    public boolean isNumber(final String name)
    {
        return (attributes.get(name) != null) && (attributes.get(name) instanceof Number);
    }

    public boolean isInteger(final String name)
    {
        return (attributes.get(name) != null) && (attributes.get(name) instanceof Integer);
    }

    public boolean isBoolean(final String name)
    {
        return (attributes.get(name) != null) && (attributes.get(name) instanceof Boolean);
    }

    public boolean isObject(final String name)
    {
        return (attributes.get(name) != null) && (attributes.get(name) instanceof JavaScriptObject);
    }

    public boolean isDefined(final String name)
    {
        return attributes.containsKey(name);
    }

    public void remove(final String name)
    {
        attributes.remove(name);
    }
}
