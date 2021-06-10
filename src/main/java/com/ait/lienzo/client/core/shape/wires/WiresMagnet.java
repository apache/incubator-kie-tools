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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.MagnetManager.Magnets;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.DoublePowerFunction;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

public class WiresMagnet extends AbstractControlHandle implements Iterable<WiresConnection>
{
    private final int           m_index;

    private final IPrimitive<?> m_control;

    private final IWiresContext m_context;

    private Magnets             m_magnets;

    private double              m_x;

    private double              m_y;

    private double                          m_strong      = 0.5;

    private NFastArrayList<WiresConnection> m_connections = null;

    private DoublePowerFunction             m_powerfn     = null;

    private Direction                       m_direction   = Direction.NONE;

    public WiresMagnet(Magnets magnets, final IWiresContext context, final int index, final double x, final double y, final IPrimitive<?> control, final boolean active)
    {
        m_context = context;

        m_index = index;

        m_magnets = magnets;

        m_control = control;

        m_x = x;

        m_y = y;

        setActive(active);
    }

    public Direction getDirection()
    {
        return m_direction;
    }

    public void setDirection(Direction direction)
    {
        m_direction = direction;
    }

    @Override
    public Iterator<WiresConnection> iterator()
    {
        if (null == m_connections)
        {
            return Collections.unmodifiableList(new ArrayList<WiresConnection>(0)).iterator();
        }
        return Collections.unmodifiableList(m_connections.toList()).iterator();
    }

    public IWiresContext getWiresContext()
    {
        return m_context;
    }

    public void shapeMoved(final double x, final double y)
    {
        m_control.setX(m_x + x);

        m_control.setY(m_y + y);

        m_control.moveToTop();

        if (null != m_connections)
        {
            final int size = m_connections.size();

            for (int i = 0; i < size; i++)
            {
                WiresConnection h = m_connections.get(i);

                h.move(m_x + x, m_y + y);
            }
        }
    }

    public WiresMagnet addHandle(final WiresConnection connection)
    {
        if (null != connection)
        {
            if (null == m_connections)
            {
                m_connections = new NFastArrayList<WiresConnection>();

                m_connections.add(connection);
            }
            else
            {
                if (false == m_connections.contains(connection))
                {
                    m_connections.add(connection);
                }
            }
        }
        return this;
    }

    public WiresMagnet removeHandle(final WiresConnection connection)
    {
        if ((null != m_connections) && (null != connection))
        {
            m_connections.remove(connection);
        }
        return this;
    }

    public Magnets getMagnets()
    {
        return m_magnets;
    }

    public WiresMagnet setPowerFunction(final DoublePowerFunction power)
    {
        m_powerfn = power;

        return this;
    }

    public DoublePowerFunction getPowerFunction()
    {
        return m_powerfn;
    }

    public WiresMagnet setStrength(final double strength)
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
            return m_powerfn.apply(m_strong);
        }
        return m_strong;
    }

    public NFastArrayList<WiresConnection> getConnections()
    {
        return m_connections;
    }

    public int getConnectionsSize()
    {
        if (null != m_connections)
        {
            return m_connections.size();
        }
        return 0;
    }

    public boolean containsConnection(final WiresConnection connection)
    {
        if ((null != m_connections) && (null != connection))
        {
            return m_connections.contains(connection);
        }
        return false;
    }

    public double getX()
    {
        return m_x;
    }

    public double getY()
    {
        return m_y;
    }

    public int getIndex()
    {
        return m_index;
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
    public void destroy()
    {
        super.destroy();

        m_magnets.destroy(this);
    }

    public WiresMagnet setRx(final double x)
    {
        this.m_x = x;
        return this;
    }

    public WiresMagnet setRy(final double y)
    {
        this.m_y = y;
        return this;
    }
}
