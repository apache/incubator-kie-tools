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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ait.lienzo.test.annotation.StubClass;
import com.ait.tooling.common.api.json.JSONType;
import com.ait.tooling.nativetools.client.NJSONReplacer;
import com.ait.tooling.nativetools.client.NUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;

@StubClass("com.ait.tooling.nativetools.client.NArrayBaseJSO")
public class NArrayBaseJSO<T extends NArrayBaseJSO<T>> extends JavaScriptObject
{
    protected final List<Object> list = new ArrayList<Object>();

    @SuppressWarnings("unchecked")
    protected static <T extends NArrayBaseJSO<T>> T createNArrayBaseJSO()
    {
        return (T) new NArrayBaseJSO<T>();
    }

    protected NArrayBaseJSO()
    {
    }

    public JSONArray toJSONArray()
    {
        return new JSONArray(this);
    }

    public String toJSONString()
    {
        return NUtils.JSON.toJSONString(this);
    }

    public String toJSONString(final NJSONReplacer replacer)
    {
        return NUtils.JSON.toJSONString(this, replacer);
    }

    public String toJSONString(final String indent)
    {
        return NUtils.JSON.toJSONString(this, indent);
    }

    public String toJSONString(final NJSONReplacer replacer, final String indent)
    {
        return NUtils.JSON.toJSONString(this, replacer, indent);
    }

    public String toJSONString(final int indent)
    {
        return NUtils.JSON.toJSONString(this, indent);
    }

    public String toJSONString(final NJSONReplacer replacer, final int indent)
    {
        return NUtils.JSON.toJSONString(this, replacer, indent);
    }

    public void clear()
    {
        list.clear();
    }

    public String join()
    {
        return join(",");
    }

    public JSONType getNativeTypeOf(final int index)
    {
        if ((index < 0) || (index >= size()))
        {
            return JSONType.UNDEFINED;
        }
        return NUtils.Native.getNativeTypeOf(this, index);
    }

    public boolean isNull(final int index)
    {
        if ((index < 0) || (index >= size()))
        {
            return true;
        }
        return list.get(index) == null;
    }

    public boolean isDefined(final int index)
    {
        if ((index < 0) || (index >= size()))
        {
            return false;
        }
        return list.get(index) != null;
    }

    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    public int size()
    {
        return list.size();
    }

    public void setSize(final int size)
    {
        // TODO
    }

    public void splice(final int beg, final int removed)
    {
        // TODO
    }

    public void reverse()
    {
        Collections.reverse(list);
    }

    public String join(final String separator)
    {
        return StringUtils.join(list, separator);
    }

    public T concat(final T value)
    {
        list.addAll(value.list);

        return value;
    }

    @SuppressWarnings("unchecked")
    public T copy()
    {
        return isEmpty() ? null : (T) list.get(list.size() - 1);
    }

    public T slice(final int beg)
    {
        // TODO
        return copy();
    }

    public T slice(final int beg, final int end)
    {
        // TODO
        return copy();
    }

    protected double doShift()
    {
        final double t = (double) list.get(0);

        list.remove(0);

        return t;
    }

    protected void doUnShift(final double value)
    {
        list.add(0, value);
    }
}
