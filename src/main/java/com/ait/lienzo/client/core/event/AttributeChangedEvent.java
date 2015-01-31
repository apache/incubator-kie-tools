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

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.AttributeMapper;
import com.google.gwt.event.shared.GwtEvent;

public class AttributeChangedEvent extends GwtEvent<AttributeChangedHandler>
{
    private static final Type<AttributeChangedHandler> TYPE = new Type<AttributeChangedHandler>();

    private final String                               m_name;

    private final Object                               m_self;

    public static Type<AttributeChangedHandler> getType()
    {
        return TYPE;
    }

    public AttributeChangedEvent(final Object self, final String name)
    {
        m_self = self;

        m_name = name;
    }

    @SuppressWarnings("unchecked")
    public final <T> T getTarget()
    {
        return (T) m_self;
    }

    public final String getName()
    {
        return m_name;
    }

    public final Attribute getAttribute()
    {
        return AttributeMapper.get().find(getName());
    }

    @Override
    public final Type<AttributeChangedHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(AttributeChangedHandler handler)
    {
        handler.onNodeAttributeChanged(this);
    }
}
