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
// TODO - review DSJ

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ArrowEnd;
import com.ait.lienzo.shared.core.types.Direction;

public class Connection extends AbstractControlHandle
{
    private static final long                     serialVersionUID = 9178762251337207445L;

    private Magnet                                m_magnet;

    private Connector                             m_connector;

    private AbstractDirectionalMultiPointShape<?> m_line;

    private Point2D                               m_point;

    private ArrowEnd                              m_end;

    public Connection(Connector connector, AbstractDirectionalMultiPointShape<?> line, ArrowEnd end)
    {
        m_connector = connector;
        m_line = line;
        m_point = (end == ArrowEnd.HEAD) ? m_line.getPoint2DArray().get(0) : m_line.getPoint2DArray().get(m_line.getPoint2DArray().size() - 1);
        m_end = end;
    }

    public Connection(final boolean active)
    {
        setActive(active);
    }

    public Connection move(final double x, final double y)
    {
        m_point.setX(x);
        m_point.setY(y);
        m_line.refresh();

        IControlHandle handle;
        if (m_end == ArrowEnd.HEAD)
        {
            handle = m_connector.getPointHandles().getHandle(0);
        }
        else
        {
            handle = m_connector.getPointHandles().getHandle(m_connector.getPointHandles().size() - 1);
        }
        if (handle != null && handle.getControl() != null)
        {
            handle.getControl().setX(x);
            handle.getControl().setY(y);
        }
        m_line.getLayer().batch();
        return this;
    }

    public ArrowEnd getEnd()
    {
        return m_end;
    }

    public void setEnd(ArrowEnd end)
    {
        m_end = end;
    }

    public AbstractDirectionalMultiPointShape<?> getLine()
    {
        return m_line;
    }

    public void setLine(AbstractDirectionalMultiPointShape<?> line)
    {
        m_line = line;
    }

    public Connector getConnector()
    {
        return m_connector;
    }

    public Connection setMagnet(final Magnet magnet)
    {
        if (m_magnet != null)
        {
            m_magnet.removeHandle(this);
        }

        if (magnet != null)
        {
            magnet.addHandle(this);

            IPrimitive<?> control = magnet.getControl();

            move(control.getX(), control.getY());

            if (m_end == ArrowEnd.TAIL)
            {
                m_line.setTailDirection(magnet.getDirection());
            }
            else
            {
                m_line.setHeadDirection(magnet.getDirection());
            }
        }
        else
        {
            if (m_end == ArrowEnd.TAIL)
            {
                m_line.setTailDirection(Direction.NONE);
            }
            else
            {
                m_line.setHeadDirection(Direction.NONE);
            }
        }

        m_magnet = magnet;

        // The Line is only draggable if both Connections are unconnected
        m_connector.setDraggable();

        return this;
    }

    public Magnet getMagnet()
    {
        return m_magnet;
    }

    @Override
    public IPrimitive<?> getControl()
    {
        if (m_end == ArrowEnd.HEAD)
        {
            return m_connector.getPointHandles().getHandle(0).getControl();
        }
        else
        {
            return m_connector.getPointHandles().getHandle(m_connector.getPointHandles().size() - 1).getControl();
        }
    }

    @Override
    public ControlHandleType getType()
    {
        return ControlHandleStandardType.HANDLE;
    }

    @Override
    public void destroy()
    {
        super.destroy();

        //        m_context.getHandleManager().destroy(this);
    }
}
