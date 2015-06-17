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
import java.util.List;

import com.ait.lienzo.client.core.Context2D;
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
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.google.gwt.json.client.JSONObject;

public abstract class AbstractMultiPathPartShape<T extends AbstractMultiPathPartShape<T>> extends Shape<T>
{
    private final NFastArrayList<PathPartList> m_list = new NFastArrayList<PathPartList>();

    protected AbstractMultiPathPartShape(final ShapeType type)
    {
        super(type);
    }

    protected AbstractMultiPathPartShape(final ShapeType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        final int size = m_list.size();

        if (size < 1)
        {
            return new BoundingBox(0, 0, 0, 0);
        }
        final BoundingBox bbox = new BoundingBox();

        for (int i = 0; i < size; i++)
        {
            bbox.add(m_list.get(i).getBoundingBox());
        }
        return bbox;
    }

    @Override
    public T refresh()
    {
        return clear();
    }

    public T clear()
    {
        final int size = m_list.size();

        for (int i = 0; i < size; i++)
        {
            m_list.get(i).clear();
        }
        m_list.clear();

        return cast();
    }

    protected final void add(PathPartList list)
    {
        m_list.add(list);
    }

    public final NFastArrayList<PathPartList> getPathPartListArray()
    {
        return m_list;
    }

    @Override
    protected void drawWithoutTransforms(final Context2D context, double alpha, BoundingBox bounds)
    {
        final Attributes attr = getAttributes();

        if ((context.isSelection()) && (false == attr.isListening()))
        {
            return;
        }
        alpha = alpha * attr.getAlpha();

        if (alpha <= 0)
        {
            return;
        }
        if (prepare(context, attr, alpha))
        {
            final int size = m_list.size();

            if (size < 1)
            {
                return;
            }
            for (int i = 0; i < size; i++)
            {
                setAppliedShadow(false);

                setWasFilledFlag(false);

                final PathPartList list = m_list.get(i);

                if (list.size() > 1)
                {
                    if (context.path(list))
                    {
                        fill(context, attr, alpha);
                    }
                    stroke(context, attr, alpha);
                }
            }
        }
    }

    public IControlHandleFactory getControlHandleFactory()
    {
        IControlHandleFactory factory = super.getControlHandleFactory();

        if (null != factory)
        {
            return factory;
        }
        return new DefaultMultiPathShapeHandleFactory(this);
    }

    private static final class DefaultMultiPathShapeHandleFactory implements IControlHandleFactory
    {
        private final AbstractMultiPathPartShape m_shape;

        private IControlHandleList               m_hlist;

        private DragMode                         m_dmode = DragMode.SAME_LAYER;

        private DefaultMultiPathShapeHandleFactory(final AbstractMultiPathPartShape shape)
        {
            m_shape = shape;
        }

        @Override
        public IControlHandleList getControlHandles(ControlHandleType... types)
        {
            return getControlHandles(Arrays.asList(types));
        }

        @Override
        public IControlHandleList getControlHandles(List<ControlHandleType> types)
        {
            if ((null == types) || (types.isEmpty()))
            {
                return null;
            }
            return getResizeHandles();
        }

        public IControlHandleList getResizeHandles()
        {
            m_hlist = new ControlHandleList();

            BoundingBox box = m_shape.getBoundingBox();

            final Point2D topLeftPoint = new Point2D(box.getX(), box.getY());
            final Point2D topRightPoint = new Point2D(box.getX() + box.getHeight(), box.getY());
            final Point2D bottomRightPoint = new Point2D(box.getX() + box.getWidth(), box.getY() + box.getHeight());
            ;
            final Point2D bottomLeftPoint = new Point2D(box.getX(), box.getY() + box.getHeight());

            Circle prim = getControlPrimitive(topLeftPoint, box.getX(), box.getY());
            ResizeTopLeft topLeft = new ResizeTopLeft(prim, m_hlist, m_shape, box);
            m_hlist.add(topLeft);

            prim = getControlPrimitive(topRightPoint, box.getX() + box.getWidth(), box.getY());
            ResizeTopRight topRight = new ResizeTopRight(prim, m_hlist, m_shape, box);
            m_hlist.add(topRight);

            prim = getControlPrimitive(topRightPoint, box.getX() + box.getWidth(), box.getY() + box.getHeight());
            ResizeBottomRight bottomRight = new ResizeBottomRight(prim, m_hlist, m_shape, box);
            m_hlist.add(bottomRight);

            prim = getControlPrimitive(topRightPoint, box.getX(), box.getY() + box.getHeight());
            ResizeBottomLeft bottomLeft = new ResizeBottomLeft(prim, m_hlist, m_shape, box);
            m_hlist.add(bottomLeft);

            return m_hlist;
        }

        private Circle getControlPrimitive(Point2D point, double x, double y)
        {
            final Point2D p = point;

            return new Circle(9).setFillColor(ColorName.RED).setFillAlpha(0.4).setX(x).setY(y).setDraggable(true).setDragMode(m_dmode).setStrokeColor(ColorName.BLACK).setStrokeWidth(2);
        }
    }

    private static abstract class AbstractPointControlHandle extends AbstractControlHandle
    {
        public abstract AbstractPointControlHandle init();

        @Override
        public final ControlHandleType getType()
        {
            return ControlHandleStandardType.RESIZE;
        }
    }

    private static class ResizeTopLeft extends ResizeAbstractControlHandle
    {
        public ResizeTopLeft(Circle prim, IControlHandleList hlist, AbstractMultiPathPartShape shape, BoundingBox box)
        {
            super(prim, hlist, shape, box);
        }

        @Override
        double getX(double startTopLeftX, double startTopLeftY, double startW, double startH, double x, double dx)
        {
            double wpc = ((100 / startW) * ((startTopLeftX + startW) - x)) / 100;
            double newX = x + (dx * wpc);
            return newX;
        }

        @Override
        double getY(double startTopLeftX, double startTopLeftY, double startW, double startH, double y, double dy)
        {
            double hpc = ((100 / startH) * ((startTopLeftY + startH) - y)) / 100;
            double newY = y + (dy * hpc);
            return newY;
        }

        @Override
        void updateOtherHandles(double dx, double dy, double boxStartX, double boxStartY, double boxStartWidth, double boxStartHeight)
        {
            IControlHandle topRight = m_hlist.getHandle(1);
            topRight.getControl().setY(boxStartY + dy);

            IControlHandle bottomLeft = m_hlist.getHandle(3);
            bottomLeft.getControl().setX(boxStartX + dx);
        }
    }

    private static class ResizeTopRight extends ResizeAbstractControlHandle
    {
        public ResizeTopRight(Circle prim, IControlHandleList hlist, AbstractMultiPathPartShape shape, BoundingBox box)
        {
            super(prim, hlist, shape, box);
        }

        @Override
        double getX(double startTopLeftX, double startTopLeftY, double startW, double startH, double x, double dx)
        {
            double wpc = ((100 / startW) * (x - startTopLeftX)) / 100;
            double newX = x + (dx * wpc);
            return newX;
        }

        @Override
        double getY(double startTopLeftX, double startTopLeftY, double startW, double startH, double y, double dy)
        {
            double hpc = ((100 / startH) * ((startTopLeftY + startH) - y)) / 100;
            double newY = y + (dy * hpc);
            return newY;
        }

        @Override
        void updateOtherHandles(double dx, double dy, double boxStartX, double boxStartY, double boxStartWidth, double boxStartHeight)
        {
            IControlHandle topLeft = m_hlist.getHandle(0);
            topLeft.getControl().setY(boxStartY + dy);

            IControlHandle bottomRight = m_hlist.getHandle(2);
            bottomRight.getControl().setX(boxStartX + boxStartWidth + dx);
        }
    }

    private static class ResizeBottomRight extends ResizeAbstractControlHandle
    {
        public ResizeBottomRight(Circle prim, IControlHandleList hlist, AbstractMultiPathPartShape shape, BoundingBox box)
        {
            super(prim, hlist, shape, box);
        }

        @Override
        double getX(double startTopLeftX, double startTopLeftY, double startW, double startH, double x, double dx)
        {
            double wpc = ((100 / startW) * (x - startTopLeftX)) / 100;
            double newX = x + (dx * wpc);
            return newX;
        }

        @Override
        double getY(double startTopLeftX, double startTopLeftY, double startW, double startH, double y, double dy)
        {
            double hpc = ((100 / startH) * (y - startTopLeftY)) / 100;
            double newY = y + (dy * hpc);
            return newY;
        }

        @Override
        void updateOtherHandles(double dx, double dy, double boxStartX, double boxStartY, double boxStartWidth, double boxStartHeight)
        {
            IControlHandle topRight = m_hlist.getHandle(1);
            topRight.getControl().setX(boxStartX + boxStartWidth + dx);

            IControlHandle bottomLeft = m_hlist.getHandle(3);
            bottomLeft.getControl().setY(boxStartY + boxStartHeight + dy);
        }
    }

    private static class ResizeBottomLeft extends ResizeAbstractControlHandle
    {
        public ResizeBottomLeft(Circle prim, IControlHandleList hlist, AbstractMultiPathPartShape shape, BoundingBox box)
        {
            super(prim, hlist, shape, box);
        }

        @Override
        double getX(double startTopLeftX, double startTopLeftY, double startW, double startH, double x, double dx)
        {
            double wpc = ((100 / startW) * ((startTopLeftX + startW) - x)) / 100;
            double newX = x + (dx * wpc);
            return newX;
        }

        @Override
        double getY(double startTopLeftX, double startTopLeftY, double startW, double startH, double y, double dy)
        {
            double hpc = ((100 / startH) * (y - startTopLeftY)) / 100;
            double newY = y + (dy * hpc);
            return newY;
        }

        @Override
        void updateOtherHandles(double dx, double dy, double boxStartX, double boxStartY, double boxStartWidth, double boxStartHeight)
        {
            IControlHandle topLeft = m_hlist.getHandle(0);
            topLeft.getControl().setX(boxStartX + dx);

            IControlHandle bottomRight = m_hlist.getHandle(2);
            bottomRight.getControl().setY(boxStartY + boxStartHeight + dy);
        }

    }

    private static abstract class ResizeAbstractControlHandle extends AbstractControlHandle
    {
        final AbstractMultiPathPartShape m_shape;

        final IControlHandleList         m_hlist;

        final Shape                      m_prim;

        public ResizeAbstractControlHandle(Circle prim, IControlHandleList hlist, AbstractMultiPathPartShape shape, BoundingBox box)
        {
            this.m_prim = prim;
            m_hlist = hlist;
            m_shape = shape;
            init();
        }

        public void init()
        {
            HandleStartMoveEnddHandler topRightHandler = new HandleStartMoveEnddHandler(m_shape, m_hlist, m_prim, this);
            register(m_prim.addNodeDragMoveHandler(topRightHandler));
            register(m_prim.addNodeDragStartHandler(topRightHandler));
            register(m_prim.addNodeDragEndHandler(topRightHandler));
        }

        @Override
        public IPrimitive<?> getControl()
        {
            return m_prim;
        }

        @Override
        public void destroy()
        {
            super.destroy();
        }

        @Override
        public final ControlHandleType getType()
        {
            return ControlHandleStandardType.RESIZE;
        }

        abstract double getX(double startTopLeftX, double startTopLeftY, double startW, double startH, double x, double dx);

        abstract double getY(double startTopLeftX, double startTopLeftY, double startW, double startH, double y, double dy);

        abstract void updateOtherHandles(double dx, double dy, double boxStartX, double boxStartY, double boxStartWidth, double boxStartHeight);
    }

    public static class HandleStartMoveEnddHandler implements NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        protected AbstractMultiPathPartShape          m_shape;

        protected IControlHandleList                  m_chlist;

        protected Shape                               m_prim;

        protected ResizeAbstractControlHandle         m_handle;

        protected double                              m_boxStartX;

        protected double                              m_boxStartY;

        protected double                              m_boxStartWidth;

        protected double                              m_boxStartHeight;

        protected NFastArrayList<NFastDoubleArrayJSO> m_entries;

        public HandleStartMoveEnddHandler(AbstractMultiPathPartShape shape, IControlHandleList chlist, Shape prim, ResizeAbstractControlHandle handle)
        {
            this.m_shape = shape;
            this.m_chlist = chlist;
            this.m_prim = prim;
            this.m_handle = handle;
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event)
        {
            BoundingBox box = m_shape.getBoundingBox();

            m_boxStartX = box.getX();
            m_boxStartY = box.getY();
            m_boxStartWidth = box.getWidth();
            m_boxStartHeight = box.getHeight();

            copyDoubles();

            if ((m_handle.isActive()) && (m_chlist.isActive()))
            {
                m_prim.setFillColor(ColorName.GREEN);

                m_prim.getLayer().draw();
            }
        }

        public void onNodeDragMove(NodeDragMoveEvent event)
        {
            if ((m_handle.isActive()) && (m_chlist.isActive()))
            {
                double dx = event.getDragContext().getDx();
                double dy = event.getDragContext().getDy();

                NFastArrayList<PathPartList> lists = m_shape.m_list;
                for (PathPartList list : lists)
                {
                    for (int i = 0; i < list.size(); i++)
                    {
                        PathPartEntryJSO entry = list.get(i);
                        NFastDoubleArrayJSO points = entry.getPoints();
                        switch (entry.getCommand())
                        {
                            case PathPartEntryJSO.MOVETO_ABSOLUTE:
                            case PathPartEntryJSO.LINETO_ABSOLUTE:
                            {
                                NFastDoubleArrayJSO doubles = m_entries.get(i);
                                double x = doubles.get(0);
                                double newX = m_handle.getX(m_boxStartX, m_boxStartY, m_boxStartWidth, m_boxStartHeight, x, dx);

                                double y = doubles.get(1);
                                double newY = m_handle.getY(m_boxStartX, m_boxStartY, m_boxStartWidth, m_boxStartHeight, y, dy);

                                points.set(0, newX);
                                points.set(1, newY);
                                break;
                            }
                        }
                    }
                }

                m_handle.updateOtherHandles(dx, dy, m_boxStartX, m_boxStartY, m_boxStartWidth, m_boxStartHeight);

                m_shape.refresh();
                m_shape.getLayer().draw();
            }
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            System.out.println("end");
            if ((m_handle.isActive()) && (m_chlist.isActive()))
            {
                NFastArrayList<PathPartList> lists = m_shape.m_list;
                for (PathPartList list : lists)
                {
                    list.resetBoundingBox();
                }

                m_prim.setFillColor(ColorName.RED);
                m_prim.getLayer().draw();
            }
        }

        private void copyDoubles()
        {
            m_entries = new NFastArrayList();
            NFastArrayList<PathPartList> lists = m_shape.m_list;
            for (PathPartList list : lists)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    PathPartEntryJSO entry = list.get(i);
                    NFastDoubleArrayJSO points = entry.getPoints();
                    switch (entry.getCommand())
                    {
                        case PathPartEntryJSO.MOVETO_ABSOLUTE:
                        case PathPartEntryJSO.LINETO_ABSOLUTE:
                        {
                            double x = points.get(0);
                            double y = points.get(1);
                            NFastDoubleArrayJSO doubles = NFastDoubleArrayJSO.make(x, y);
                            m_entries.push(doubles);
                            break;
                        }
                    }
                }
            }
        }
    }
}
