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

package com.ait.lienzo.client.core.shape;

import java.util.List;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.shape.wires.AbstractControlHandle;
import com.ait.lienzo.client.core.shape.wires.ControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public abstract class AbstractMultiPointShape<T extends AbstractMultiPointShape<T> & IMultiPointShape<T>> extends Shape<T> implements IMultiPointShape<T>
{
    protected AbstractMultiPointShape(final ShapeType type)
    {
        super(type);
    }

    protected AbstractMultiPointShape(final ShapeType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);
    }

    @Override
    public boolean isControlPointShape()
    {
        return false;
    }

    @Override
    public IMultiPointShape<?> asMultiPointShape()
    {
        return this;
    }

    @Override
    public IOffsetMultiPointShape<?> asOffsetMultiPointShape()
    {
        return null;
    }

    @Override
    public IDirectionalMultiPointShape<?> asDirectionalMultiPointShape()
    {
        return null;
    }

    @Override
    public IControlHandleFactory getControlHandleFactory()
    {
        IControlHandleFactory factory = super.getControlHandleFactory();

        if (null != factory)
        {
            return factory;
        }
        return new DefaultMultiPointShapeHandleFactory(this);
    }

    private static final class DefaultMultiPointShapeHandleFactory implements IControlHandleFactory
    {
        private final AbstractMultiPointShape<?> m_shape;

        private IControlHandleList               m_hlist;

        private DragMode                         m_dmode = DragMode.SAME_LAYER;

        private DefaultMultiPointShapeHandleFactory(final AbstractMultiPointShape<?> shape)
        {
            m_shape = shape;
        }

        @Override
        public IControlHandleList getControlHandles(final List<ControlHandleType> types)
        {
            if (false == types.contains(ControlHandleStandardType.POINT))
            {
                return null;
            }
            if (null != m_hlist)
            {
                return m_hlist;
            }
            m_hlist = new ControlHandleList();

            for (Point2D point : m_shape.getPoint2DArray())
            {
                final Point2D p = point;

                final Circle prim = new Circle(9).setFillColor(ColorName.RED).setFillAlpha(0.4).setX(m_shape.getX() + p.getX()).setY(m_shape.getY() + p.getY()).setDraggable(true).setDragMode(m_dmode).setStrokeColor(ColorName.BLACK).setStrokeWidth(2);

                m_hlist.add(new AbstractPointControlHandle()
                {
                    private static final long serialVersionUID = -1839635043082960976L;

                    @Override
                    public AbstractPointControlHandle init()
                    {
                        register(prim.addNodeDragMoveHandler(new NodeDragMoveHandler()
                        {
                            @Override
                            public void onNodeDragMove(NodeDragMoveEvent event)
                            {
                                if ((isActive()) && (m_hlist.isActive()))
                                {
                                    p.setX(prim.getX() - m_shape.getX());

                                    p.setY(prim.getY() - m_shape.getY());

                                    m_shape.refresh();

                                    m_shape.getLayer().draw();
                                }
                            }
                        }));
                        register(prim.addNodeDragStartHandler(new NodeDragStartHandler()
                        {
                            @Override
                            public void onNodeDragStart(NodeDragStartEvent event)
                            {
                                if ((isActive()) && (m_hlist.isActive()))
                                {
                                    prim.setFillColor(ColorName.GREEN);

                                    prim.getLayer().draw();
                                }
                            }
                        }));
                        register(prim.addNodeDragEndHandler(new NodeDragEndHandler()
                        {
                            @Override
                            public void onNodeDragEnd(NodeDragEndEvent event)
                            {
                                if ((isActive()) && (m_hlist.isActive()))
                                {
                                    prim.setFillColor(ColorName.RED);

                                    prim.getLayer().draw();
                                }
                            }
                        }));
                        return this;
                    }

                    @Override
                    public IPrimitive<?> getControl()
                    {
                        return prim;
                    }

                    @Override
                    public void destroy()
                    {
                        super.destroy();
                    }
                }.init());
            }
            return m_hlist;
        }
    }

    @SuppressWarnings("serial")
    private static abstract class AbstractPointControlHandle extends AbstractControlHandle
    {
        public abstract AbstractPointControlHandle init();

        @Override
        public final ControlHandleType getType()
        {
            return ControlHandleStandardType.POINT;
        }
    }
}
