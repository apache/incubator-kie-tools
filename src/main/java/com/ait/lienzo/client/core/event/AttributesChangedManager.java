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
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.tooling.nativetools.client.collection.NFastStringCounter;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;
import com.ait.tooling.nativetools.client.collection.NFastStringSet;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public final class AttributesChangedManager
{
    private NFastStringCounter             m_ctr;

    private NFastStringMap<HandlerManager> m_map;

    private final IJSONSerializable<?>     m_ser;

    public AttributesChangedManager(final IJSONSerializable<?> ser)
    {
        m_ser = ser;
    }

    public final HandlerRegistration addAttributesChangedHandler(final Attribute attribute, final AttributesChangedHandler handler)
    {
        if ((null == attribute) || (null == handler))
        {
            return null;
        }
        if (null != m_ser)
        {
            if (null == m_ctr)
            {
                m_ctr = new NFastStringCounter();
            }
            if (null == m_map)
            {
                m_map = new NFastStringMap<HandlerManager>();
            }
            final String name = attribute.getProperty();

            m_ctr.inc(name);

            HandlerManager entry = m_map.get(name);

            if (null == entry)
            {
                m_map.put(name, entry = new HandlerManager(m_ser));
            }
            return new HandlerRegistrationProxy(name, entry.addHandler(AttributesChangedEvent.getType(), handler));
        }
        return null;
    }

    public final boolean canDispatchAttributesChanged(final String name)
    {
        return ((null != m_ctr) && (m_ctr.contains(name)));
    }

    public final void fireChanged(final NFastStringSet changed)
    {
        if ((null != m_ctr) && (null != m_map) && (null != changed) && (false == m_ctr.isEmpty()) && (false == changed.isEmpty()))
        {
            final AttributesChangedEvent event = new AttributesChangedEvent(changed);

            for (String name : changed)
            {
                if (m_ctr.contains(name))
                {
                    final HandlerManager entry = m_map.get(name);

                    if (null != entry)
                    {
                        entry.fireEvent(event);
                    }
                }
            }
        }
    }

    private final class HandlerRegistrationProxy implements HandlerRegistration
    {
        private final String              m_name;

        private final HandlerRegistration m_prox;

        private HandlerRegistrationProxy(final String name, final HandlerRegistration prox)
        {
            m_name = name;

            m_prox = prox;
        }

        @Override
        public final void removeHandler()
        {
            m_prox.removeHandler();

            if (null != m_ctr)
            {
                m_ctr.dec(m_name);

                if (false == m_ctr.contains(m_name))
                {
                    if (null != m_map)
                    {
                        m_map.remove(m_name);
                    }
                }
                if (m_ctr.isEmpty())
                {
                    m_ctr = null;

                    m_map = null;
                }
            }
        }
    }
}
