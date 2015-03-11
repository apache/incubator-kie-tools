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

package com.ait.lienzo.client.core.storage;

import java.util.Collections;
import java.util.Iterator;

import com.ait.lienzo.client.core.shape.json.AbstractFactory;
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.ClipRegion;
import com.ait.lienzo.client.core.types.MetaData;
import com.ait.lienzo.client.core.types.NFastStringMapMixedJSO;
import com.ait.lienzo.shared.core.types.StorageEngineType;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public abstract class AbstractStorageEngine<M extends IJSONSerializable<M>> implements IStorageEngine<M, AbstractStorageEngine<M>>
{
    private final String            m_type;

    private final MetaData          m_meta;

    private final StorageEngineType m_stor;

    protected AbstractStorageEngine(final String type, final StorageEngineType stor)
    {
        m_type = type;

        m_stor = stor;

        m_meta = new MetaData();
    }

    protected AbstractStorageEngine(final String type, final StorageEngineType stor, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        m_type = type;

        m_stor = stor;

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
    public MetaData getMetaData()
    {
        return m_meta;
    }

    @Override
    public String toJSONString()
    {
        return toJSONObject().toString();
    }

    @Override
    public JSONObject toJSONObject()
    {
        final JSONObject object = new JSONObject();

        object.put("type", new JSONString(getType()));

        if (false == getMetaData().isEmpty())
        {
            object.put("meta", new JSONObject(getMetaData().getJSO()));
        }
        return object;
    }

    @Override
    public String getType()
    {
        return m_type;
    }

    @Override
    public StorageEngineType getStorageEngineType()
    {
        return m_stor;
    }

    @Override
    public Iterator<M> iterator(final ClipRegion bounds)
    {
        return Collections.unmodifiableList(getChildren(bounds).toList()).iterator();
    }

    @Override
    public Iterator<M> iterator()
    {
        return Collections.unmodifiableList(getChildren().toList()).iterator();
    }

    protected static abstract class AbstractStorageEngineFactory<S extends IJSONSerializable<S>> extends AbstractFactory<S>
    {
        protected AbstractStorageEngineFactory(final String type)
        {
            super(type);
        }
    }
}
