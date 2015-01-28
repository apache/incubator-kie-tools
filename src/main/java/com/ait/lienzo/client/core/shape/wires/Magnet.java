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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.ait.lienzo.client.core.shape.IControlHandle;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.NFastArrayList;

public class Magnet implements IControlHandle, Iterable<Handle>
{
    private final int              m_indexer;

    private final IPrimitive<?>    m_control;

    private final IWiresContext    m_context;

    private double                 m_strong  = 0.5;

    private boolean                m_active  = true;

    private NFastArrayList<Handle> m_handles = null;

    private MagnetPowerFunction    m_powerfn = null;

    public Magnet(final IWiresContext context, final int indexer, final IPrimitive<?> control)
    {
        m_context = context;

        m_indexer = indexer;

        m_control = control;
    }

    public Magnet(final IWiresContext context, final int indexer, final IPrimitive<?> control, final boolean active)
    {
        this(context, indexer, control);

        setActive(active);
    }

    @Override
    public Iterator<Handle> iterator()
    {
        if (null == m_handles)
        {
            return Collections.unmodifiableList(new ArrayList<Handle>(0)).iterator();
        }
        return Collections.unmodifiableList(m_handles.toList()).iterator();
    }

    public IWiresContext getWiresContext()
    {
        return m_context;
    }

    public Magnet move(final double x, final double y)
    {
        m_control.setX(x);

        m_control.setY(y);

        if (null != m_handles)
        {
            final int size = m_handles.size();

            for (int i = 0; i < size; i++)
            {
                m_handles.get(i).move(x, y);
            }
        }
        return this;
    }

    public Magnet addHandle(final Handle handle)
    {
        if (null != handle)
        {
            if (null == m_handles)
            {
                m_handles = new NFastArrayList<Handle>();

                m_handles.add(handle);
            }
            else
            {
                if (false == m_handles.contains(handle))
                {
                    m_handles.add(handle);
                }
            }
        }
        return this;
    }

    public Magnet removeHandle(final Handle handle)
    {
        if ((null != m_handles) && (null != handle))
        {
            m_handles.remove(handle);
        }
        return this;
    }

    public Magnet setPowerFunction(final MagnetPowerFunction power)
    {
        m_powerfn = power;

        return this;
    }

    public MagnetPowerFunction getPowerFunction()
    {
        return m_powerfn;
    }

    public Magnet setStrength(final double strength)
    {
        if ((strength >= 0) && (strength <= 1))
        {
            m_strong = strength;
        }
        return this;
    }

    public double getStrength()
    {
        if (null != m_powerfn)
        {
            return m_powerfn.calculate(m_strong);
        }
        return m_strong;
    }

    public NFastArrayList<Handle> getHandles()
    {
        return m_handles;
    }

    public int getHandlesSize()
    {
        if (null != m_handles)
        {
            return m_handles.size();
        }
        return 0;
    }

    public boolean containsHandle(final Handle handle)
    {
        if ((null != m_handles) && (null != handle))
        {
            return m_handles.contains(handle);
        }
        return false;
    }

    public int getIndexer()
    {
        return m_indexer;
    }

    @Override
    public IPrimitive<?> getControl()
    {
        return m_control;
    }

    @Override
    public ControlHandleType getType()
    {
        return ControlHandleStandardType.MAGNET;
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
        m_context.getMagnetManager().destroy(this);
    }
}
