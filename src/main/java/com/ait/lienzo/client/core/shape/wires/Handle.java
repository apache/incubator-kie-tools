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

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.IControlHandle;
import com.ait.lienzo.client.core.shape.IPrimitive;

public class Handle implements IControlHandle
{
    private final int           m_indexer;

    private final IPrimitive<?> m_control;

    private final IWiresContext m_context;

    private Magnet              m_magnet = null;

    private boolean             m_active = true;

    public Handle(final IWiresContext context, final int indexer, final IPrimitive<?> control)
    {
        m_context = context;

        m_indexer = indexer;

        m_control = control;
    }

    public Handle(final IWiresContext context, final int indexer, final IPrimitive<?> control, final boolean active)
    {
        this(context, indexer, control);

        setActive(active);
    }

    public Handle move(final double x, final double y)
    {
        m_control.setX(x);

        m_control.setY(y);

        m_context.getHandleManager().move(this, x, y);

        return this;
    }

    public IWiresContext getWiresContext()
    {
        return m_context;
    }

    public int getIndexer()
    {
        return m_indexer;
    }

    public Handle setMagnet(final Magnet magnet)
    {
        m_magnet = magnet;

        return this;
    }

    public Magnet getMagnet()
    {
        return m_magnet;
    }

    @Override
    public IPrimitive<?> getControl()
    {
        return m_control;
    }

    @Override
    public ControlHandleType getType()
    {
        return ControlHandleStandardType.HANDLE;
    }

    @Override
    public boolean isActive()
    {
        return m_active;
    }

    @Override
    public void setActive(final boolean active)
    {
        m_active = active;
    }

    @Override
    public void destroy()
    {
        m_context.getHandleManager().destroy(this);
    }
}
