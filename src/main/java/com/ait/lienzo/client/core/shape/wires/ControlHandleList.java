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
// TODO - review DSJ

package com.ait.lienzo.client.core.shape.wires;

import java.util.Collections;
import java.util.Iterator;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.tooling.common.api.types.Activatable;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;

public final class ControlHandleList extends Activatable implements IControlHandleList
{
    private static final long                    serialVersionUID = 2074423747469042319L;

    private final NFastArrayList<IControlHandle> m_chlist         = new NFastArrayList<IControlHandle>();

    private final HandlerRegistrationManager     m_manage         = new HandlerRegistrationManager();

    private Layer                                m_layer;

    private IPrimitive<?>                        m_shape;

    private boolean                              m_visible;

    public ControlHandleList()
    {
        super(true);
    }

    public ControlHandleList(IPrimitive<?> shape)
    {
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
        return (0 == m_chlist.size());
    }

    public IControlHandle getHandle(int index)
    {
        return m_chlist.get(index);
    }

    @Override
    public Layer getLayer()
    {
        return m_layer;
    }

    @Override
    public final boolean contains(final IControlHandle handle)
    {
        if ((null != handle) && (m_chlist.size() > 0))
        {
            return m_chlist.contains(handle);
        }
        return false;
    }

    @Override
    public final void add(final IControlHandle handle)
    {
        if (false == contains(handle))
        {
            m_chlist.add(handle);
        }
    }

    @Override
    public final void remove(final IControlHandle handle)
    {
        if (contains(handle))
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

        if (null != m_layer)
        {
            m_layer.batch();

            m_layer = null;
        }
    }

    @Override
    public void show()
    {
        if (m_shape != null)
        {
            show(m_shape.getLayer());
        }
    }

    @Override
    public void show(final Layer layer)
    {
        if (!m_visible && null != layer)
        {
            int totl = 0;

            final int size = size();

            for (int i = 0; i < size; i++)
            {
                final IControlHandle handle = m_chlist.get(i);

                if (null != handle)
                {
                    IPrimitive<?> prim = handle.getControl();

                    if (null != prim)
                    {
                        layer.add(prim);

                        totl++;
                    }
                }
            }
            if (totl > 0)
            {
                m_layer = layer;

                m_layer.batch();
            }

            m_visible = true;
        }
    }

    @Override
    public void hide()
    {
        if (m_visible)
        {
            int totl = 0;

            final int size = size();

            for (int i = 0; i < size; i++)
            {
                final IControlHandle handle = m_chlist.get(i);

                if (null != handle)
                {
                    IPrimitive<?> prim = handle.getControl();

                    if (null != prim)
                    {
                        prim.removeFromParent();

                        totl++;
                    }
                }
            }
            if (totl > 0)
            {
                m_layer.batch();
            }
            m_visible = false;
        }
    }

    @Override
    public final Iterator<IControlHandle> iterator()
    {
        return Collections.unmodifiableList(m_chlist.toList()).iterator();
    }

    public HandlerRegistrationManager getHandlerRegistrationManager()
    {
        return m_manage;
    }
}
