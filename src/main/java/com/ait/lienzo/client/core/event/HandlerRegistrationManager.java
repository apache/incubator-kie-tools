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

import com.ait.lienzo.client.core.types.NFastArrayList;
import com.google.gwt.event.shared.HandlerRegistration;

public final class HandlerRegistrationManager
{
    private final NFastArrayList<HandlerRegistration> m_list = new NFastArrayList<HandlerRegistration>();

    public HandlerRegistrationManager()
    {
    }

    public final int size()
    {
        return m_list.size();
    }

    public final boolean isEmpty()
    {
        return (0 == size());
    }

    public final HandlerRegistration get(final int i)
    {
        return m_list.get(i);
    }

    public final HandlerRegistrationManager delete()
    {
        final int size = size();

        for (int i = 0; i < size; i++)
        {
            get(i).removeHandler();
        }
        return clear();
    }

    public final HandlerRegistration add(final HandlerRegistration handler)
    {
        if (null != handler)
        {
            if (false == m_list.contains(handler))
            {
                m_list.add(handler);
            }
        }
        return handler;
    }

    public final boolean contains(final HandlerRegistration handler)
    {
        return ((null != handler) && (size() > 0) && (m_list.contains(handler)));
    }

    public final HandlerRegistrationManager delete(final HandlerRegistration handler)
    {
        if (null != handler)
        {
            if (size() > 0)
            {
                if (m_list.contains(handler))
                {
                    m_list.remove(handler);
                }
            }
            handler.removeHandler();
        }
        return this;
    }

    public final HandlerRegistrationManager delete(final HandlerRegistrationManager manager)
    {
        if ((null != manager) && (manager != this))
        {
            final int size = manager.size();

            for (int i = 0; i < size; i++)
            {
                delete(manager.get(i));
            }
            manager.clear();
        }
        return this;
    }

    public final HandlerRegistrationManager clear()
    {
        m_list.clear();

        return this;
    }
}
