/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.image.filter;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.shape.json.AbstractFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.MetaData;
import com.ait.lienzo.client.core.types.NFastStringMapMixedJSO;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public abstract class AbstractImageDataFilter<T extends AbstractImageDataFilter<T>> implements ImageDataFilter<T>
{
    private final MetaData   m_meta;

    private final Attributes m_attr;

    private final String     m_type;

    protected AbstractImageDataFilter()
    {
        m_attr = new Attributes();

        m_meta = new MetaData();

        m_type = getClass().getSimpleName();

        setActive(true);
    }

    protected AbstractImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        m_type = getClass().getSimpleName();

        if (null == node)
        {
            m_attr = new Attributes();

            m_meta = new MetaData();

            return;
        }
        JSONValue aval = node.get("attributes");

        if (null == aval)
        {
            m_attr = new Attributes();
        }
        else
        {
            JSONObject aobj = aval.isObject();

            if (null == aobj)
            {
                m_attr = new Attributes();
            }
            else
            {
                JavaScriptObject ajso = aobj.getJavaScriptObject();

                if (null == ajso)
                {
                    m_attr = new Attributes();
                }
                else
                {
                    m_attr = new Attributes(ajso);
                }
            }
        }
        JSONValue mval = node.get("meta");

        if (null == mval)
        {
            m_meta = new MetaData();
        }
        else
        {
            JSONObject mobj = mval.isObject();

            if (null == mobj)
            {
                m_meta = new MetaData();
            }
            else
            {
                JavaScriptObject mjso = mobj.getJavaScriptObject();

                if (null == mjso)
                {
                    m_meta = new MetaData();
                }
                else
                {
                    NFastStringMapMixedJSO jso = mjso.cast();

                    m_meta = new MetaData(jso);
                }
            }
        }
    }

    @Override
    public boolean isTransforming()
    {
        return false;
    }

    @Override
    public boolean isActive()
    {
        return getAttributes().isActive();
    }

    @Override
    public void setActive(boolean active)
    {
        getAttributes().setActive(active);
    }

    @Override
    public final String getType()
    {
        return m_type;
    }

    @SuppressWarnings("unchecked")
    protected final T cast()
    {
        return (T) this;
    }

    public final MetaData getMetaData()
    {
        return m_meta;
    }

    public final Attributes getAttributes()
    {
        return m_attr;
    }

    @Override
    public String toJSONString()
    {
        return toJSONObject().toString();
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject object = new JSONObject();

        object.put("type", new JSONString(getType()));

        if (false == getMetaData().isEmpty())
        {
            object.put("meta", new JSONObject(getMetaData().getJSO()));
        }
        object.put("attributes", new JSONObject(getAttributes().getJSO()));

        return object;
    }

    protected static abstract class ImageDataFilterFactory<T extends ImageDataFilter<T>> extends AbstractFactory<T>
    {
        protected ImageDataFilterFactory(String type)
        {
            super(type);

            addAttribute(Attribute.ACTIVE, true);
        }
    }
}
