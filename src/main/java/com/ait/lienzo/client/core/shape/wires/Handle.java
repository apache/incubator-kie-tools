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

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ArrowEnd;

public class Handle extends AbstractControlHandle
{
    private static final long   serialVersionUID = 9178762251337207445L;

    private              Magnet m_magnet         = null;

    private AbstractDirectionalMultiPointShape m_line;

    private Point2D                            m_point;

    private ArrowEnd                           m_end;

    public Handle(AbstractDirectionalMultiPointShape line, ArrowEnd end)
    {
        m_line = line;
        m_point = ( end == ArrowEnd.HEAD) ? m_line.getPoint2DArray().get(0) : m_line.getPoint2DArray().get(m_line.getPoint2DArray().size()-1);
        m_end = end;
    }

    public Handle(AbstractDirectionalMultiPointShape line, Point2D point)
    {
        m_line = line;
        m_point = point;
        m_end = ( m_line.getPoint2DArray().get(0) == m_point ) ? ArrowEnd.HEAD : ArrowEnd.TAIL;
    }

    public Handle(final boolean active)
    {
        setActive(active);
    }

    public Handle move(final double x, final double y)
    {
        m_point.setX(x);
        m_point.setY(y);
        m_line.refresh();
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

    public AbstractDirectionalMultiPointShape getLine()
    {
        return m_line;
    }

    public void setLine(AbstractDirectionalMultiPointShape line)
    {
        m_line = line;
    }

//    public IWiresContext getWiresContext()
//    {
//        return m_context;
//    }
//
//    public int getIndexer()
//    {
//        return m_indexer;
//    }

    public Handle setMagnet(final Magnet magnet)
    {
        m_magnet = magnet;

        return this;
    }

    public Magnet getMagnet()
    {
        return m_magnet;
    }

    @Override public IPrimitive<?> getControl()
    {
        throw new UnsupportedOperationException();
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
