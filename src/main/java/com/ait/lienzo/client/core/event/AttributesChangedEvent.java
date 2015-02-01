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

package com.ait.lienzo.client.core.event;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.ait.lienzo.client.core.Attribute;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;

public class AttributesChangedEvent extends GwtEvent<AttributesChangedHandler>
{
    private static final Type<AttributesChangedHandler> TYPE = new Type<AttributesChangedHandler>();

    private final LinkedHashSet<String>                 m_changed;

    public static Type<AttributesChangedHandler> getType()
    {
        return TYPE;
    }

    private static final LinkedHashSet<String> set(final String name)
    {
        final LinkedHashSet<String> make = new LinkedHashSet<String>(1);

        make.add(name);

        return make;
    }

    public AttributesChangedEvent(final String name)
    {
        this(set(name));
    }

    public AttributesChangedEvent(LinkedHashSet<String> changed)
    {
        m_changed = changed;
    }

    @SuppressWarnings("unchecked")
    public final <T> T getTarget()
    {
        try
        {
            return ((T) getSource());
        }
        catch (Exception e)
        {
            GWT.log("AttributesChangedEvent cast error: ", e);
        }
        return null;
    }

    public final Set<String> getChanged()
    {
        return Collections.unmodifiableSet(m_changed);
    }

    public final boolean isAttributeChanged(final Attribute attribute)
    {
        return m_changed.contains(attribute.getProperty());
    }

    public final boolean isAttributeChanged(final String name)
    {
        return m_changed.contains(name);
    }

    @Override
    public final Type<AttributesChangedHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(AttributesChangedHandler handler)
    {
        handler.onAttributesChanged(this);
    }
}
