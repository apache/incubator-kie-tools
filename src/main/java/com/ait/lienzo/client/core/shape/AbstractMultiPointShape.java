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

package com.ait.lienzo.client.core.shape;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.event.AttributesChangedEvent;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
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
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.json.client.JSONObject;

public abstract class AbstractMultiPointShape<T extends AbstractMultiPointShape<T> & IMultiPointShape<T>> extends Shape<T> implements IMultiPointShape<T>
{
    private final PathPartList m_list = new PathPartList();

    protected AbstractMultiPointShape(final ShapeType type)
    {
        super(type);
    }

    protected AbstractMultiPointShape(final ShapeType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);
    }

    @Override
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

    @Override
    public T refresh()
    {
        getPathPartList().clear();
        return super.refresh();
    }

    public static final class DefaultMultiPointShapeHandleFactory implements IControlHandleFactory
    {
        public static final double              R0                  = 6;

        public static final double              R1                  = 10;

        public static final double              SELECTION_OFFSET    = R0 * 0.5;

        private static final double             ANIMATION_DURATION  = 100;

        private final AbstractMultiPointShape<?> m_shape;

        private DragMode                         m_dmode            = DragMode.SAME_LAYER;

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
            if (false == types.contains(ControlHandleStandardType.POINT) && false == types.contains(ControlHandleStandardType.HANDLE))
            {
                return null;
            }
            HashMap<ControlHandleType, IControlHandleList> map = new HashMap<ControlHandleType, IControlHandleList>();

            for (ControlHandleType type : types)
            {
                if (type == ControlHandleStandardType.HANDLE)
                {
                    IControlHandleList chList = getPointHandles();

                    map.put(IControlHandle.ControlHandleStandardType.HANDLE, chList);
                }
                else if (type == ControlHandleStandardType.POINT)
                {
                    IControlHandleList chList = getPointHandles();

                    map.put(IControlHandle.ControlHandleStandardType.POINT, chList);
                }
            }
            return map;
        }

        private IControlHandleList getPointHandles()
        {
            final ControlHandleList chlist = new ControlHandleList(m_shape);

            HandlerRegistrationManager manager = chlist.getHandlerRegistrationManager();

            ShapeXorYChanged shapeXoYChangedHandler = new ShapeXorYChanged(m_shape, chlist);

            manager.register(m_shape.addNodeDragStartHandler(shapeXoYChangedHandler));

            manager.register(m_shape.addNodeDragMoveHandler(shapeXoYChangedHandler));

            manager.register(m_shape.addNodeDragEndHandler(shapeXoYChangedHandler));

            for (Point2D point : m_shape.getPoint2DArray())
            {
                final Point2D p = point;

                final Circle prim = new Circle(R0).setX(m_shape.getX() + p.getX()).setY(m_shape.getY() + p.getY()).setFillColor(ColorName.DARKRED).setFillAlpha(0.8).setStrokeAlpha(0).setDraggable(true).setDragMode(m_dmode);

                prim.setSelectionStrokeOffset(SELECTION_OFFSET);
                prim.setSelectionBoundsOffset(SELECTION_OFFSET);
                prim.setFillBoundsForSelection(true);

                chlist.add(new AbstractPointControlHandle()
                {
                    @Override
                    public AbstractPointControlHandle init()
                    {
                        ControlXorYChanged handler = new ControlXorYChanged(chlist, m_shape, p, prim, this, m_shape.getLayer());

                        register(prim.addNodeDragMoveHandler(handler));

                        register(prim.addNodeDragStartHandler(handler));

                        register(prim.addNodeDragEndHandler(handler));

                        register(prim.addNodeMouseEnterHandler(new NodeMouseEnterHandler()
                        {
                            @Override
                            public void onNodeMouseEnter(NodeMouseEnterEvent event)
                            {
                                animate(prim, R1);
                            }
                        }));
                        register(prim.addNodeMouseExitHandler(new NodeMouseExitHandler()
                        {
                            @Override
                            public void onNodeMouseExit(NodeMouseExitEvent event)
                            {
                                animate(prim, R0);
                            }
                        }));
                        setPoint(p);

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

        private static void animate(final Circle circle, final double radius)
        {
            circle.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.RADIUS(radius)), ANIMATION_DURATION);
        }
    }

    public static class ShapeXorYChanged implements AttributesChangedHandler, NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        private IControlHandleList m_handleList;

        private Shape<?>           m_shape;

        private boolean            m_dragging;

        public ShapeXorYChanged(Shape<?> shape, IControlHandleList handleList)
        {
            m_shape = shape;

            m_handleList = handleList;
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event)
        {
            shapeMoved();
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event)
        {
            m_dragging = true;
        }

        @Override
        public void onAttributesChanged(AttributesChangedEvent event)
        {
            if (!m_dragging && event.all(Attribute.X, Attribute.Y))
            {
                shapeMoved();
            }
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            m_dragging = false;
        }

        private void shapeMoved()
        {
            for (IControlHandle handle : m_handleList)
            {
                Point2D p = ((AbstractPointControlHandle) handle).getPoint();

                handle.getControl().setX(m_shape.getX() + p.getX());

                handle.getControl().setY(m_shape.getY() + p.getY());
            }
            m_shape.getLayer().batch();
        }
    }

    public static class ControlXorYChanged implements NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        private Shape<?>                   m_prim;

        private AbstractPointControlHandle m_handle;

        private Point2D                    m_point;

        private IControlHandleList         m_handleList;

        private Shape<?>                   m_shape;

        private Layer                      m_layer;

        private boolean                    m_isDragging;

        public ControlXorYChanged(IControlHandleList handleList, Shape<?> shape, Point2D point, Shape<?> prim, AbstractPointControlHandle handle, Layer layer)
        {
            m_handleList = handleList;

            m_shape = shape;

            m_layer = layer;

            m_prim = prim;

            m_point = point;

            m_handle = handle;
        }

        public Layer getLayer()
        {
            return m_layer;
        }

        public boolean isDragging()
        {
            return m_isDragging;
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event)
        {
            m_isDragging = true;

            if ((m_handle.isActive()) && (m_handleList.isActive()))
            {
                m_prim.setFillColor(ColorName.GREEN);

                m_prim.getLayer().batch();
            }
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            m_isDragging = false;

            if ((m_handle.isActive()) && (m_handleList.isActive()))
            {
                m_prim.setFillColor(ColorName.DARKRED);

                m_prim.getLayer().batch();
            }
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event)
        {
            if ((m_handle.isActive()) && (m_handleList.isActive()))
            {
                m_point.setX(m_prim.getX() - m_shape.getX());

                m_point.setY(m_prim.getY() - m_shape.getY());

                m_shape.refresh();

                m_shape.getLayer().batch();
            }
        }
    }

    private static abstract class AbstractPointControlHandle extends AbstractControlHandle
    {
        private Point2D m_point;

        public abstract AbstractPointControlHandle init();

        public Point2D getPoint()
        {
            return m_point;
        }

        public void setPoint(Point2D point)
        {
            m_point = point;
        }

        @Override
        public final ControlHandleType getType()
        {
            return ControlHandleStandardType.POINT;
        }
    }
}
