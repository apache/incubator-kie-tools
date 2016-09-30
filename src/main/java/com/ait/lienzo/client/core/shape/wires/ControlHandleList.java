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

import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.tooling.common.api.types.Activatable;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;

public class ControlHandleList extends Activatable implements IControlHandleList
{
    private final NFastArrayList<IControlHandle> m_chlist = new NFastArrayList<IControlHandle>();

    private final HandlerRegistrationManager     m_manage = new HandlerRegistrationManager();

    private final IPrimitive<?>                        m_shape;

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
        return (0 == m_chlist.size());
    }

    @Override
    public IControlHandle getHandle(int index)
    {
        return m_chlist.get(index);
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

        for ( int i = 0; i < size; i++ ) {
            final IControlHandle handle = m_chlist.get( i );

            if ( null != handle ) {
                handle.destroy();
            }
        }

        m_chlist.clear();

        batch();
    }

    @Override
    public void show()
    {
        showOn( getParent() );
    }

    @Override
    public void hide()
    {
        if ( m_visible && null != m_shape.getLayer() )
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
            m_visible = false;
            if ( totl > 0 )
            {
                batch();
            }
        }
    }

    @Override
    public boolean isVisible() {
        return m_visible;
    }

    @Override
    public final Iterator<IControlHandle> iterator()
    {
        return Collections.unmodifiableList(m_chlist.toList()).iterator();
    }

    @Override
    public HandlerRegistrationManager getHandlerRegistrationManager()
    {
        return m_manage;
    }

    void showOn( IContainer<?, IPrimitive<?>> container )
    {

        if ( null != container && !m_visible )
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
                        container.add( prim );
                        totl++;
                    }
                }
            }

            m_visible = true;

            if (totl > 0)
            {
                container.moveToTop();
                batch();

            }
        }

    }

    protected IContainer<?, IPrimitive<?>> getParent() {
        return m_shape.getLayer();
    }


    private void batch() {
        if ( null != m_shape.getLayer() ) {
            m_shape.getLayer().batch();
        }
    }

}
