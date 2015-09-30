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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.event.AttributesChangedEvent;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
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
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public abstract class AbstractMultiPointShape<T extends AbstractMultiPointShape<T> & IMultiPointShape<T>> extends Shape<T> implements IMultiPointShape<T>
{
    protected final PathPartList m_list = new PathPartList();

    protected AbstractMultiPointShape(final ShapeType type)
    {
        super(type);
    }

    protected AbstractMultiPointShape(final ShapeType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);
    }

    public PathPartList getPathPartList()
    {
        return m_list;
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

        private DragMode m_dmode = DragMode.SAME_LAYER;

        private DefaultMultiPointShapeHandleFactory(final AbstractMultiPointShape<?> shape)
        {
            m_shape = shape;
        }

        @Override
        public Map<ControlHandleType, IControlHandleList> getControlHandles(ControlHandleType... types)
        {
            return getControlHandles(Arrays.asList(types));
        }

        @Override
        public Map<ControlHandleType, IControlHandleList> getControlHandles(final List<ControlHandleType> types)
        {
            if ((null == types) || (types.isEmpty()))
            {
                return null;
            }
            if (false == types.contains(ControlHandleStandardType.POINT) && false == types.contains(
                    ControlHandleStandardType.HANDLE))
            {
                return null;
            }

            Map map = new HashMap<ControlHandleType, IControlHandleList>();
            for (ControlHandleType type : types)
            {
                if (type == ControlHandleStandardType.HANDLE)
                {
                    IControlHandleList chList = getPointHandles();
                    map.put(IControlHandle.ControlHandleStandardType.HANDLE, chList);
                }
                else if ( type == ControlHandleStandardType.POINT )
                {
                    IControlHandleList chList = getPointHandles();
                    map.put(IControlHandle.ControlHandleStandardType.POINT, chList);
                }
            }
            return map;
        }

        private IControlHandleList getHandles()
        {
            final ControlHandleList chlist = new ControlHandleList();

            return null;
        }

        private IControlHandleList getPointHandles()
        {
            final ControlHandleList chlist = new ControlHandleList();

            for (Point2D point : m_shape.getPoint2DArray())
            {
                final Point2D p = point;

                final Circle prim = new Circle(9).setFillColor(ColorName.RED).setFillAlpha(0.4).setX(m_shape.getX() + p.getX()).setY(m_shape.getY() + p.getY()).setDraggable(true).setDragMode(m_dmode).setStrokeColor(ColorName.BLACK).setStrokeWidth(2);

                chlist.add(new AbstractPointControlHandle()
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
                                if ((isActive()) && (chlist.isActive()))
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
                                if ((isActive()) && (chlist.isActive()))
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
                                if ((isActive()) && (chlist.isActive()))
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

            return chlist;
        }
    }

    public static class XorYChanged implements AttributesChangedHandler, NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        private Shape              m_prim;

        private AbstractPointControlHandle m_handle;

        private Point2D            m_point;

        private IControlHandleList m_handleList;

        private Shape              m_shape;

        private Layer              m_layer;

        private boolean            m_isDragging;

        public XorYChanged(IControlHandleList handleList, Shape shape, Layer layer)
        {
            m_handleList = handleList;
            m_shape = shape;
            m_layer = layer;
        }

        public void onAttributesChanged(AttributesChangedEvent event)
        {
            if (!m_isDragging && event.any(Attribute.X, Attribute.Y))
            {
                shapeMoved();
            }
        }

        @Override public void onNodeDragStart(NodeDragStartEvent event)
        {
            m_isDragging = true;

            if ((m_handle.isActive()) && (m_handleList.isActive()))
            {
                m_prim.setFillColor(ColorName.GREEN);

                m_prim.getLayer().draw();
            }
        }

        @Override public void onNodeDragEnd(NodeDragEndEvent event)
        {
            m_isDragging = false;
            if ((m_handle.isActive()) && (m_handleList.isActive()))
            {
                m_prim.setFillColor(ColorName.RED);

                m_prim.getLayer().draw();
            }
        }

        @Override public void onNodeDragMove(NodeDragMoveEvent event)
        {
            if ((m_handle.isActive()) && (m_handleList.isActive()))
            {
                m_point.setX(m_prim.getX() - m_shape.getX());

                m_point.setY(m_prim.getY() - m_shape.getY());

                m_shape.refresh();

                m_shape.getLayer().draw();
            }

            shapeMoved();
        }

        private void shapeMoved()
        {
            double x = m_shape.getX();
            double y = m_shape.getY();
            for (int i = 0; i < m_handleList.size(); i++)
            {
//                Magnet m = (Magnet) m_handleList.getHandle(i);
//                m.shapeMoved(x, y);
            }
            m_handleList.getLayer().batch();
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
