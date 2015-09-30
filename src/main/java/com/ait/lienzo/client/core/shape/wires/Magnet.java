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

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.DoublePowerFunction;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.lienzo.shared.core.types.ArrowEnd;

public class Magnet extends AbstractControlHandle implements Iterable<Connection>
{
    private static final long serialVersionUID = 3820187031688704400L;

    private final int           m_indexer;

    private final IPrimitive<?> m_control;

    private final IWiresContext m_context;

    private       IMagnets      m_magnets;

    private       double        m_x;

    private       double        m_y;

    private double                     m_strong      = 0.5;

    private NFastArrayList<Connection> m_connections = null;

    private DoublePowerFunction        m_powerfn     = null;

    private Direction                  m_direction   = Direction.NONE;

    public Magnet(IMagnets magnets, final IWiresContext context, final int indexer, final double x, final double y, final IPrimitive<?> control, final boolean active)
    {
        m_context = context;

        m_indexer = indexer;

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
    public Iterator<Connection> iterator()
    {
        if (null == m_connections)
        {
            return Collections.unmodifiableList(new ArrayList<Connection>(0)).iterator();
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

        if (null != m_connections)
        {
            final int size = m_connections.size();

            for (int i = 0; i < size; i++)
            {
                Connection h = m_connections.get(i);
                h.move(m_x + x, m_y + y);
            }
        }
    }


    public Magnet addHandle(final Connection connection)
    {
        if (null != connection)
        {
            if (null == m_connections)
            {
                m_connections = new NFastArrayList<Connection>();

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

        connection.move(m_control.getX(), m_control.getY());
        return this;
    }

    public Magnet removeHandle(final Connection connection)
    {
        if ((null != m_connections) && (null != connection))
        {
            m_connections.remove(connection);
        }
        return this;
    }

    public IMagnets getMagnets()
    {
        return m_magnets;
    }

    public Magnet setPowerFunction(final DoublePowerFunction power)
    {
        m_powerfn = power;

        return this;
    }

    public DoublePowerFunction getPowerFunction()
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
            return m_powerfn.apply(m_strong);
        }
        return m_strong;
    }

    public NFastArrayList<Connection> getConnections()
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

    public boolean containsConnection(final Connection connection)
    {
        if ((null != m_connections) && (null != connection))
        {
            return m_connections.contains(connection);
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
    public void destroy()
    {
        super.destroy();

        m_magnets.destroy(this);

    }
}
