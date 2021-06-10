/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.tools.client.event;

import com.ait.lienzo.tools.client.collection.NFastArrayList;

public final class HandlerRegistrationManager implements HandlerRegistration
{
    private final NFastArrayList<HandlerRegistration> m_list = new NFastArrayList<>();

    public HandlerRegistrationManager()
    {
    }

    public HandlerRegistrationManager(final HandlerRegistration handler, final HandlerRegistration... handlers)
    {
        register(handler);

        for (HandlerRegistration h : handlers)
        {
            register(h);
        }
    }

    public final int size()
    {
        return m_list.size();
    }

    public final boolean isEmpty()
    {
        return m_list.isEmpty();
    }

    public final HandlerRegistrationManager destroy()
    {
        final int size = size();

        for (int i = 0; i < size; i++)
        {
            m_list.get(i).removeHandler();
        }
        return clear();
    }

    public final HandlerRegistration register(final HandlerRegistration handler)
    {
        if ((null != handler) && (!m_list.contains(handler)))
        {
            m_list.add(handler);
        }
        return handler;
    }

    public final boolean isRegistered(final HandlerRegistration handler)
    {
        return ((null != handler) && (size() > 0) && (m_list.contains(handler)));
    }

    public final HandlerRegistrationManager deregister(final HandlerRegistration handler)
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

    public final HandlerRegistrationManager clear()
    {
        m_list.clear();

        return this;
    }

    @Override
    public void removeHandler()
    {
        destroy();
    }
}
