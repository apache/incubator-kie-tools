/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.shape.json;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.AttributeType;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;

/**
 * AbstractFactory is an abstract implementation of {@IFactory}.
 *
 * @param <T>
 * @since 1.1
 */
public abstract class AbstractFactory<T extends IJSONSerializable<T>> implements IFactory<T>
{
    private final LinkedHashMap<String, Attribute> m_requiredsSheet = new LinkedHashMap<String, Attribute>();

    private final LinkedHashMap<String, Attribute> m_attributeSheet = new LinkedHashMap<String, Attribute>();

    private String                                 m_typeName;

    protected AbstractFactory(final String typeName)
    {
        m_typeName = typeName;
    }

    /**
     * Only factories that wish to extend other factories should use this.
     * 
     * @param typeName
     */
    protected void setTypeName(final String typeName)
    {
        m_typeName = typeName;
    }

    @Override
    public String getTypeName()
    {
        return m_typeName;
    }

    protected void addAttribute(final Attribute attr, final boolean required)
    {
        // Allow setting the attribute twice to override the requiredness
        // with a different value.

        final String prop = attr.getProperty();

        if (false == m_attributeSheet.containsKey(prop))
        {
            m_attributeSheet.put(prop, attr);
        }
        if (required)
        {
            if (false == m_requiredsSheet.containsKey(prop))
            {
                m_requiredsSheet.put(prop, attr);
            }
        }
        else
        {
            m_requiredsSheet.remove(prop);
        }
    }

    /**
     * Add optional attribute
     * @param attr
     */
    protected void addAttribute(final Attribute attr)
    {
        addAttribute(attr, false);
    }

    @Override
    public Collection<Attribute> getAttributeSheet()
    {
        return Collections.unmodifiableCollection(m_attributeSheet.values());
    }

    @Override
    public Collection<Attribute> getRequiredAttributes()
    {
        return Collections.unmodifiableCollection(m_requiredsSheet.values());
    }

    @Override
    public AttributeType getAttributeType(final String type)
    {
        final Attribute attr = m_attributeSheet.get(type);

        if (null != attr)
        {
            return attr.getType();
        }
        return null;
    }

    @Override
    public void process(IJSONSerializable<?> node, ValidationContext ctx) throws ValidationException
    {
    }

    @Override
    public boolean isPostProcessed()
    {
        return false;
    }
}
