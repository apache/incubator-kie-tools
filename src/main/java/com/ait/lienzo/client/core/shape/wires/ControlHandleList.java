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
// TODO - review DSJ

package com.ait.lienzo.client.core.shape.wires;

import java.util.Iterator;

import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.NFastArrayListIterator;
import com.ait.lienzo.tools.common.api.types.Activatable;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;

public class ControlHandleList extends Activatable implements IControlHandleList
{
    private final HandlerRegistrationManager     m_manage = new HandlerRegistrationManager();

    private final NFastArrayList<IControlHandle> m_chlist = new NFastArrayList<IControlHandle>();

    private final IPrimitive<?>                  m_shape;

    private boolean                              m_visible;

    public ControlHandleList(final IPrimitive<?> shape)
    {
        super(true);

        m_shape = shape;
    }

    @Override
    public final int size()
    {
        return m_chlist.size();
    }

    @Override
    public final boolean isEmpty()
    {
        return (size() == 0);
    }

    @Override
    public IControlHandle getHandle(final int index)
    {
        return m_chlist.get(index);
    }

    @Override
    public final boolean contains(final IControlHandle handle)
    {
        if ((null != handle) && (!isEmpty()))
        {
            return m_chlist.contains(handle);
        }
        return false;
    }

    @Override
    public final void add(final IControlHandle handle)
    {
        if ((null != handle) && (!contains(handle)))
        {
            m_chlist.add(handle);
        }
    }

    @Override
    public final void remove(final IControlHandle handle)
    {
        if ((null != handle) && (contains(handle)))
        {
            m_chlist.remove(handle);
        }
    }

    @Override
    public void destroy()
    {
        m_manage.destroy();

        final int size = size();

        hide();

        for (int i = 0; i < size; i++)
        {
            final IControlHandle handle = m_chlist.get(i);

            if (null != handle)
            {
                handle.destroy();
            }
        }
        m_chlist.clear();

        m_shape.batch();
    }

    @Override
    public void show()
    {
        showOn(m_shape.getLayer());
    }

    @Override
    public void hide()
    {
        if ((isVisible()) && (null != m_shape.getLayer()))
        {
            int totl = 0;

            final int size = size();

            for (int i = 0; i < size; i++)
            {
                final IControlHandle handle = m_chlist.get(i);

                if (null != handle)
                {
                    final IPrimitive<?> prim = handle.getControl();

                    if (null != prim)
                    {
                        prim.removeFromParent();

                        totl++;
                    }
                }
            }
            m_visible = false;

            if (totl > 0)
            {
                m_shape.batch();
            }
        }
    }

    @Override
    public boolean isVisible()
    {
        return m_visible;
    }

    @Override
    public final Iterator<IControlHandle> iterator()
    {
        return new NFastArrayListIterator<>(m_chlist);
    }

    @Override
    public HandlerRegistrationManager getHandlerRegistrationManager()
    {
        return m_manage;
    }

    void showOn(final IContainer<?, IPrimitive<?>> container)
    {
        if ((null != container) && (!isVisible()))
        {
            int totl = 0;

            final int size = size();

            for (int i = 0; i < size; i++)
            {
                final IControlHandle handle = m_chlist.get(i);

                if (null != handle)
                {
                    final IPrimitive<?> prim = handle.getControl();

                    if (null != prim)
                    {
                        container.add(prim);

                        totl++;
                    }
                }
            }
            m_visible = true;

            if (totl > 0)
            {
                m_shape.batch();
            }
        }
    }
}
