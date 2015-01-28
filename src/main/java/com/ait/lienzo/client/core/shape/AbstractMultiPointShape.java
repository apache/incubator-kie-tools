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

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.event.HandlerRegistrationManager;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.shape.IControlHandle.ControlHandleStandardType;
import com.ait.lienzo.client.core.shape.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.event.shared.HandlerRegistration;
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

    private static abstract class DefaultControlHandle implements IControlHandle
    {
        private boolean                          m_active = true;

        private final HandlerRegistrationManager m_dlist  = new HandlerRegistrationManager();

        public abstract DefaultControlHandle regsiter();

        public HandlerRegistration add(final HandlerRegistration handler)
        {
            return m_dlist.add(handler);
        }

        public void delete(final HandlerRegistrationManager manager)
        {
            if (null != manager)
            {
                manager.delete(m_dlist);
            }
        }

        @Override
        public ControlHandleType getType()
        {
            return ControlHandleStandardType.POINT;
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

        public void clear()
        {
            m_dlist.clear();
        }
    }

    private static final class DefaultMultiPointShapeHandleFactory implements IControlHandleFactory
    {
        private final AbstractMultiPointShape<?> m_shape;

        private final HandlerRegistrationManager m_rlist;

        private IControlHandleList               m_hlist;

        private List<IControlHandle>             m_ilist;

        private Layer                            m_layer;

        private DragMode                         m_dmode = DragMode.SAME_LAYER;

        private DefaultMultiPointShapeHandleFactory(final AbstractMultiPointShape<?> shape)
        {
            m_shape = shape;

            m_rlist = new HandlerRegistrationManager();
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
            m_hlist = new IControlHandleList()
            {
                @Override
                public List<IControlHandle> getList()
                {
                    if (null != m_ilist)
                    {
                        if (false == m_ilist.isEmpty())
                        {
                            return m_ilist;
                        }
                    }
                    m_ilist = new ArrayList<IControlHandle>();

                    for (Point2D point : m_shape.getPoint2DArray())
                    {
                        final Point2D p = point;

                        final Circle prim = new Circle(9).setFillColor(ColorName.RED).setFillAlpha(0.4).setX(m_shape.getX() + p.getX()).setY(m_shape.getY() + p.getY()).setDraggable(true).setDragMode(m_dmode).setStrokeColor(ColorName.BLACK).setStrokeWidth(2);

                        if (null != m_layer)
                        {
                            m_layer.add(prim);
                        }
                        m_ilist.add(new DefaultControlHandle()
                        {
                            @Override
                            public DefaultControlHandle regsiter()
                            {
                                add(m_rlist.add(prim.addNodeDragMoveHandler(new NodeDragMoveHandler()
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
                                })));
                                add(m_rlist.add(prim.addNodeDragStartHandler(new NodeDragStartHandler()
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
                                })));
                                add(m_rlist.add(prim.addNodeDragEndHandler(new NodeDragEndHandler()
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
                                })));
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
                                delete(m_rlist);

                                Layer save = prim.getLayer();

                                prim.removeFromParent();

                                if (null != save)
                                {
                                    save.batch();
                                }
                                m_ilist.remove(this);
                            }
                        }.regsiter());
                    }
                    if (null != m_layer)
                    {
                        m_layer.draw();
                    }
                    return m_ilist;
                }

                @Override
                public boolean isActive()
                {
                    return true;
                }

                @Override
                public void setActive(boolean active)
                {
                }

                @Override
                public void destroy()
                {
                    m_rlist.delete();

                    if (null != m_ilist)
                    {
                        for (IControlHandle handle : m_ilist)
                        {
                            if (handle instanceof DefaultControlHandle)
                            {
                                ((DefaultControlHandle) handle).clear();
                            }
                            IPrimitive<?> prim = handle.getControl();

                            Layer save = prim.getLayer();

                            prim.removeFromParent();

                            if ((null != save) && (save != m_layer))
                            {
                                save.batch();
                            }
                        }
                        m_ilist.clear();

                        m_ilist = null;

                        if (null != m_layer)
                        {
                            m_layer.draw();
                        }
                    }
                }

                @Override
                public void setLayerAndDraw(Layer layer, DragMode dmode)
                {
                    m_layer = layer;

                    m_dmode = dmode;

                    if (null != m_layer)
                    {
                        m_hlist.getList();
                    }
                }
            };
            return m_hlist;
        }
    }
}
