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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.Context2D;
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
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;

public abstract class AbstractMultiPathPartShape<T extends AbstractMultiPathPartShape<T>> extends Shape<T>
{
    private final NFastArrayList<PathPartList> m_points = new NFastArrayList<PathPartList>();
    private NFastArrayList<PathPartList> m_cornerPoints = new NFastArrayList<PathPartList>();

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
        NFastArrayList<PathPartList> points = m_points;

        if (getCornerRadius() > 0)
        {
            points = m_cornerPoints;
        }
        final int size = points.size();

        if (size < 1)
        {
            return new BoundingBox(0, 0, 0, 0);
        }
        final BoundingBox bbox = new BoundingBox();

        for (int i = 0; i < size; i++)
        {
            bbox.add(points.get(i).getBoundingBox());
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
        final int size = m_points.size();

        for (int i = 0; i < size; i++)
        {
            m_points.get(i).clear();
        }
        m_points.clear();

        return cast();
    }

    @Override
    protected boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        double radius = getCornerRadius();

        if (radius != 0)
        {
            m_cornerPoints = new NFastArrayList<PathPartList>();

            for (int i = 0; i < m_points.size(); i++)
            {
                PathPartList baseList = m_points.get(i);

                Point2DArray basePoints = baseList.getPoints();

                PathPartList cornerList = new PathPartList();

                Geometry.drawArcJoinedLines(cornerList, baseList, basePoints, radius);

                m_cornerPoints.add(cornerList);
            }
        }

        return true;
    }

    protected final void add(final PathPartList list)
    {
        m_points.add(list);
    }

    public final NFastArrayList<PathPartList> getPathPartListArray()
    {
        return m_points;
    }

    public final NFastArrayList<PathPartList> getActualPathPartListArray()
    {
        if (getCornerRadius() > 0)
        {
            return m_cornerPoints;
        }
        else
        {
            return m_points;
        }
    }

    @Override
    protected void drawWithoutTransforms(final Context2D context, double alpha, BoundingBox bounds)
    {
        final Attributes attr = getAttributes();

        alpha = alpha * attr.getAlpha();

        if (alpha <= 0)
        {
            return;
        }
        if (prepare(context, attr, alpha))
        {
            NFastArrayList<PathPartList> points = m_points;

            if (getCornerRadius() > 0)
            {
                points = m_cornerPoints;
            }
            final int size = points.size();

            if (size < 1)
            {
                return;
            }
            for (int i = 0; i < size; i++)
            {
                setAppliedShadow(false);

                final PathPartList list = points.get(i);

                if (list.size() > 1)
                {
                    boolean fill = false;

                    if (context.path(list))
                    {
                        fill = fill(context, attr, alpha);
                    }
                    stroke(context, attr, alpha, fill);
                }
            }
        }
    }

    public BoundingBox getSizeConstraints()
    {
        return getAttributes().getSizeConstraints();
    }

    public T setSizeConstraints(final BoundingBox sizeConstraints)
    {
        getAttributes().setSizeConstraints(sizeConstraints);
        return refresh();
    }

    public double getCornerRadius()
    {
        return getAttributes().getCornerRadius();
    }

    public T setCornerRadius(final double radius)
    {
        getAttributes().setCornerRadius(radius);
        return refresh();
    }

    @Override
    public IControlHandleFactory getControlHandleFactory()
    {
        IControlHandleFactory factory = super.getControlHandleFactory();

        if (null != factory)
        {
            return factory;
        }
        return new DefaultMultiPathShapeHandleFactory(m_points, this);
    }

    public static class OnDragMoveIControlHandleList implements AttributesChangedHandler, NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        private Shape<?>            m_shape;

        private IControlHandleList  m_chlist;

        private double[]            m_startPoints;

        private HandlerRegistration m_nodeDragStartHandlerReg;

        private HandlerRegistration m_nodeDragMoveHandlerReg;

        public OnDragMoveIControlHandleList(Shape<?> shape, IControlHandleList chlist)
        {
            m_shape = shape;

            m_chlist = chlist;

            HandlerRegistrationManager regManager = m_chlist.getHandlerRegistrationManager();

            m_nodeDragStartHandlerReg = m_shape.addNodeDragStartHandler(this);

            m_nodeDragMoveHandlerReg = m_shape.addNodeDragMoveHandler(this);

            regManager.register(m_nodeDragStartHandlerReg);

            regManager.register(m_nodeDragMoveHandlerReg);
        }

        @Override
        public void onAttributesChanged(AttributesChangedEvent event)
        {
            //event.
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event)
        {
            int size = m_chlist.size();

            m_startPoints = new double[size * 2];

            int i = 0;

            for (IControlHandle handle : m_chlist)
            {
                m_startPoints[i] = handle.getControl().getX();

                m_startPoints[i + 1] = handle.getControl().getY();

                i = i + 2;
            }
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event)
        {
            int i = 0;

            for (IControlHandle handle : m_chlist)
            {
                IPrimitive<?> prim = handle.getControl();

                prim.setX(m_startPoints[i] + event.getDragContext().getDistanceAdjusted().getX());

                prim.setY(m_startPoints[i + 1] + event.getDragContext().getDistanceAdjusted().getY());

                i = i + 2;
            }
            m_shape.getLayer().draw();
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            m_startPoints = null;
        }
    }

    public static final class DefaultMultiPathShapeHandleFactory implements IControlHandleFactory
    {
        private final NFastArrayList<PathPartList> m_listOfPaths;

        private final Shape<?>                     m_shape;

        private DragMode                           m_dmode = DragMode.SAME_LAYER;

        public DefaultMultiPathShapeHandleFactory(NFastArrayList<PathPartList> listOfPaths, Shape<?> shape)
        {
            m_listOfPaths = listOfPaths;

            m_shape = shape;
        }

        @Override
        public Map<ControlHandleType, IControlHandleList> getControlHandles(ControlHandleType... types)
        {
            return getControlHandles(Arrays.asList(types));
        }

        @Override
        public Map<ControlHandleType, IControlHandleList> getControlHandles(List<ControlHandleType> types)
        {
            if ((null == types) || (types.isEmpty()))
            {
                return null;
            }
            HashMap<ControlHandleType, IControlHandleList> map = new HashMap<ControlHandleType, IControlHandleList>();

            for (ControlHandleType type : types)
            {
                if (type == IControlHandle.ControlHandleStandardType.RESIZE)
                {
                    IControlHandleList chList = getResizeHandles(m_shape, m_listOfPaths, m_dmode);

                    map.put(IControlHandle.ControlHandleStandardType.RESIZE, chList);
                }
                else if (type == IControlHandle.ControlHandleStandardType.POINT)
                {
                    IControlHandleList chList = getPointHandles();

                    map.put(IControlHandle.ControlHandleStandardType.POINT, chList);
                }
            }
            return map;
        }

        public IControlHandleList getPointHandles()
        {
            ControlHandleList chlist = new ControlHandleList(m_shape);

            NFastArrayList<Point2DArray> allPoints = new NFastArrayList<Point2DArray>();

            int pathIndex = 0;

            for (PathPartList path : m_listOfPaths)
            {
                Point2DArray points = path.getPoints();

                allPoints.add(points);

                int entryIndex = 0;

                for (Point2D point : points)
                {
                    final Circle prim = getControlPrimitive(5, point.getX(), point.getY(), m_shape, m_dmode);

                    PointControlHandle pointHandle = new PointControlHandle(prim, pathIndex, entryIndex++, m_shape, m_listOfPaths, path, chlist);

                    animate(pointHandle, AnimationProperty.Properties.RADIUS(15), AnimationProperty.Properties.RADIUS(5));

                    chlist.add(pointHandle);
                }
                pathIndex++;
            }
            new OnDragMoveIControlHandleList(m_shape, chlist);

            return chlist;
        }

        private static final double R0                 = 5;

        private static final double R1                 = 10;

        private static final double ANIMATION_DURATION = 150d;

        private static void animate(final AbstractControlHandle handle, final AnimationProperty initialProperty, final AnimationProperty endProperty)
        {
            final Node<?> node = (Node<?>) handle.getControl();

            handle.getHandlerRegistrationManager().register(node.addNodeMouseEnterHandler(new NodeMouseEnterHandler()
            {
                @Override
                public void onNodeMouseEnter(NodeMouseEnterEvent event)
                {
                    animate(node, initialProperty);
                }
            }));
            handle.getHandlerRegistrationManager().register(node.addNodeMouseExitHandler(new NodeMouseExitHandler()
            {
                @Override
                public void onNodeMouseExit(NodeMouseExitEvent event)
                {
                    animate(node, endProperty);
                }
            }));
        }

        private static void animate(final Node<?> node, final AnimationProperty property)
        {
            node.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(property), ANIMATION_DURATION);
        }

        public static IControlHandleList getResizeHandles(Shape<?> shape, NFastArrayList<PathPartList> listOfPaths, DragMode dragMode)
        {
            // FIXME This isn't quite right yet, do not release  (mdp, um what did I mean here?)

            ControlHandleList chlist = new ControlHandleList(shape);

            BoundingBox box = shape.getBoundingBox();

            final Point2D tl = new Point2D(box.getX(), box.getY());

            final Point2D tr = new Point2D(box.getX() + box.getWidth(), box.getY());

            final Point2D bl = new Point2D(box.getX(), box.getHeight() + box.getY());

            final Point2D br = new Point2D(box.getX() + box.getWidth(), box.getHeight() + box.getY());

            ArrayList<ResizeControlHandle> orderedChList = new ArrayList<ResizeControlHandle>();

            ResizeControlHandle topLeft = getResizeControlHandle(chlist, orderedChList, shape, listOfPaths, tl, 0, dragMode);

            chlist.add(topLeft);

            orderedChList.add(topLeft);

            ResizeControlHandle topRight = getResizeControlHandle(chlist, orderedChList, shape, listOfPaths, tr, 1, dragMode);

            chlist.add(topRight);

            orderedChList.add(topRight);

            ResizeControlHandle bottomRight = getResizeControlHandle(chlist, orderedChList, shape, listOfPaths, br, 2, dragMode);

            chlist.add(bottomRight);

            orderedChList.add(bottomRight);

            ResizeControlHandle bottomLeft = getResizeControlHandle(chlist, orderedChList, shape, listOfPaths, bl, 3, dragMode);

            chlist.add(bottomLeft);

            orderedChList.add(bottomLeft);

            new OnDragMoveIControlHandleList(shape, chlist);

            return chlist;
        }

        private static ResizeControlHandle getResizeControlHandle(IControlHandleList chlist, ArrayList<ResizeControlHandle> orderedChList, Shape<?> shape, NFastArrayList<PathPartList> listOfPaths, Point2D point, int position, DragMode dragMode)
        {
            final Circle prim = getControlPrimitive(R0, point.getX(), point.getY(), shape, dragMode);

            ResizeControlHandle handle = new ResizeControlHandle(prim, chlist, orderedChList, shape, listOfPaths, position);

            animate(handle, AnimationProperty.Properties.RADIUS(R1), AnimationProperty.Properties.RADIUS(R0));

            return handle;
        }

        private static Circle getControlPrimitive(double size, double x, double y, Shape<?> shape, DragMode dragMode)
        {
            return new Circle(size).setX(x + shape.getX()).setY(y + shape.getY()).setFillColor(ColorName.DARKRED).setFillAlpha(0.8).setStrokeColor(ColorName.BLACK).setStrokeWidth(0.5).setDraggable(true).setDragMode(dragMode);
        }
    }

    private static class PointControlHandle extends AbstractControlHandle
    {
        private final Shape<?>                     m_shape;

        private final NFastArrayList<PathPartList> m_listOfPaths;

        private final IControlHandleList           m_chlist;

        private final Shape<?>                     m_prim;

        private final int                          m_pathIndex;

        private final int                          m_entryIndex;

        public PointControlHandle(Shape<?> prim, int pathIndex, int entryIndex, Shape<?> shape, NFastArrayList<PathPartList> listOfPaths, PathPartList plist, IControlHandleList hlist)
        {
            m_shape = shape;

            m_listOfPaths = listOfPaths;

            m_chlist = hlist;

            m_prim = prim;

            m_pathIndex = pathIndex;

            m_entryIndex = entryIndex;

            init();
        }

        public void init()
        {
            PointHandleDragHandler topRightHandler = new PointHandleDragHandler(m_shape, m_listOfPaths, m_chlist, m_prim, this);

            register(m_prim.addNodeDragMoveHandler(topRightHandler));

            register(m_prim.addNodeDragStartHandler(topRightHandler));

            register(m_prim.addNodeDragEndHandler(topRightHandler));
        }

        public int getPathIndex()
        {
            return m_pathIndex;
        }

        public int getEntryIndex()
        {
            return m_entryIndex;
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
            return ControlHandleStandardType.POINT;
        }
    }

    public static class PointHandleDragHandler implements NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        protected final Shape<?>                      m_shape;

        private final NFastArrayList<PathPartList>    m_listOfPaths;

        protected final IControlHandleList            m_chlist;

        protected final Shape<?>                      m_prim;

        protected final PointControlHandle            m_handle;

        protected NFastArrayList<NFastDoubleArrayJSO> m_entries;

        public PointHandleDragHandler(Shape<?> shape, NFastArrayList<PathPartList> listOfPaths, IControlHandleList chlist, Shape<?> prim, PointControlHandle handle)
        {
            m_shape = shape;

            m_listOfPaths = listOfPaths;

            m_chlist = chlist;

            m_prim = prim;

            m_handle = handle;
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event)
        {
            copyDoubles();

            if ((m_handle.isActive()) && (m_chlist.isActive()))
            {
                m_prim.setFillColor(ColorName.GREEN);

                m_prim.getLayer().draw();
            }
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event)
        {
            if ((m_handle.isActive()) && (m_chlist.isActive()))
            {
                double dx = event.getDragContext().getDistanceAdjusted().getX();

                double dy = event.getDragContext().getDistanceAdjusted().getY();

                PathPartList list = m_listOfPaths.get(m_handle.getPathIndex());

                PathPartEntryJSO entry = list.get(m_handle.getEntryIndex());

                NFastDoubleArrayJSO points = entry.getPoints();

                switch (entry.getCommand())
                {
                    case PathPartEntryJSO.MOVETO_ABSOLUTE:
                    case PathPartEntryJSO.LINETO_ABSOLUTE:
                    {
                        NFastDoubleArrayJSO doubles = m_entries.get(m_handle.getEntryIndex());

                        double x = doubles.get(0);

                        double y = doubles.get(1);

                        points.set(0, x + dx);

                        points.set(1, y + dy);

                        break;
                    }
                }
                m_shape.refresh();

                m_shape.getLayer().batch();
            }
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            if ((m_handle.isActive()) && (m_chlist.isActive()))
            {
                NFastArrayList<PathPartList> lists = m_listOfPaths;

                for (PathPartList list : lists)
                {
                    list.resetBoundingBox();
                }
                m_prim.setFillColor(ColorName.DARKRED);

                m_prim.getLayer().draw();
            }
        }

        private void copyDoubles()
        {
            m_entries = new NFastArrayList<NFastDoubleArrayJSO>();

            NFastArrayList<PathPartList> lists = m_listOfPaths;

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

    private static class ResizeControlHandle extends AbstractControlHandle
    {
        private final Shape<?>                       m_shape;

        private final NFastArrayList<PathPartList>   m_listOfPaths;

        private final IControlHandleList             m_chlist;

        private final ArrayList<ResizeControlHandle> m_orderedChList;

        private final Shape<?>                       m_prim;

        private int                                  m_position;

        public ResizeControlHandle(Circle prim, IControlHandleList hlist, ArrayList<ResizeControlHandle> orderedChList, Shape<?> shape, NFastArrayList<PathPartList> listOfPaths, int position)
        {
            m_prim = prim;

            m_chlist = hlist;

            m_shape = shape;

            m_position = position;

            m_orderedChList = orderedChList;

            m_listOfPaths = listOfPaths;

            init();
        }

        public void init()
        {
            ResizeHandleDragHandler topRightHandler = new ResizeHandleDragHandler(m_shape, m_listOfPaths, m_chlist, m_prim, this);

            m_prim.setDragConstraints(topRightHandler);

            register(m_prim.addNodeDragEndHandler(topRightHandler));


        }

        public int getPosition()
        {
            return m_position;
        }

        public void setPosition(int position)
        {
            m_position = position;
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
        public ControlHandleType getType()
        {
            return ControlHandleStandardType.RESIZE;
        }

        public Shape<?> getPrimitive()
        {
            return m_prim;
        }

        public double getX(double startTopLeftX, double startTopLeftY, double startW, double startH, double x, double dx)
        {
            double newX = 0;

            switch (m_position)
            {
                case 0:
                case 3:
                    newX = getLeft(startTopLeftX, startTopLeftY, startW, startH, x, dx);
                    break;
                case 1:
                case 2:
                    newX = getRight(startTopLeftX, startTopLeftY, startW, startH, x, dx);
                    break;
            }
            return newX;
        }

        public double getY(double startTopLeftX, double startTopLeftY, double startW, double startH, double y, double dy)
        {
            double newY = 0;

            switch (m_position)
            {
                case 0:
                case 1:
                    newY = getTop(startTopLeftX, startTopLeftY, startW, startH, y, dy);
                    break;
                case 2:
                case 3:
                    newY = getBottom(startTopLeftX, startTopLeftY, startW, startH, y, dy);
                    break;
            }
            return newY;
        }

        void updateOtherHandles(double dx, double dy, double offsetX, double offsetY, double boxStartX, double boxStartY, double boxStartWidth, double boxStartHeight)
        {
            switch (m_position)
            {
                case 0:
                {
                    IControlHandle topRight = m_orderedChList.get(1);
                    topRight.getControl().setY(boxStartY + dy + offsetY);

                    IControlHandle bottomLeft = m_orderedChList.get(3);
                    bottomLeft.getControl().setX(boxStartX + dx + offsetX);
                    break;
                }
                case 1:
                {
                    IControlHandle topLeft = m_orderedChList.get(0);
                    topLeft.getControl().setY(boxStartY + dy + offsetY);

                    IControlHandle bottomRight = m_orderedChList.get(2);
                    bottomRight.getControl().setX(boxStartX + boxStartWidth + dx + offsetX);
                    break;
                }
                case 2:
                {
                    IControlHandle topRight = m_orderedChList.get(1);
                    topRight.getControl().setX(boxStartX + boxStartWidth + dx + offsetX);

                    IControlHandle bottomLeft = m_orderedChList.get(3);
                    bottomLeft.getControl().setY(boxStartY + boxStartHeight + dy + offsetY);
                    break;
                }
                case 3:
                {
                    IControlHandle topLeft = m_orderedChList.get(0);
                    topLeft.getControl().setX(boxStartX + dx + offsetX);

                    IControlHandle bottomRight = m_orderedChList.get(2);
                    bottomRight.getControl().setY(boxStartY + boxStartHeight + dy + offsetY);
                    break;
                }
            }
        }

        double getLeft(double startTopLeftX, double startTopLeftY, double startW, double startH, double x, double dx)
        {
            double wpc = ((100 / startW) * ((startTopLeftX + startW) - x)) / 100;

            double newX = x + (dx * wpc);

            return newX;
        }

        double getRight(double startTopLeftX, double startTopLeftY, double startW, double startH, double x, double dx)
        {
            double wpc = ((100 / startW) * (x - startTopLeftX)) / 100;

            double newX = x + (dx * wpc);

            return newX;
        }

        double getTop(double startTopLeftX, double startTopLeftY, double startW, double startH, double y, double dy)
        {
            double hpc = ((100 / startH) * ((startTopLeftY + startH) - y)) / 100;

            double newY = y + (dy * hpc);

            return newY;
        }

        double getBottom(double startTopLeftX, double startTopLeftY, double startW, double startH, double y, double dy)
        {
            double hpc = ((100 / startH) * (y - startTopLeftY)) / 100;

            double newY = y + (dy * hpc);

            return newY;
        }
    }

    public static class ResizeHandleDragHandler implements DragConstraintEnforcer, NodeDragEndHandler
    {
        private final Shape<?>                      m_shape;

        private final NFastArrayList<PathPartList>  m_listOfPaths;

        private final IControlHandleList            m_chlist;

        private final Shape<?>                      m_prim;

        private final ResizeControlHandle           m_handle;

        private double                              m_boxStartX;

        private double                              m_boxStartY;

        private double                              m_boxStartWidth;

        private double                              m_boxStartHeight;

        private NFastArrayList<NFastDoubleArrayJSO> m_entries;

        private double                              m_offsetX;

        private double                              m_offsetY;

        public ResizeHandleDragHandler(Shape<?> shape, NFastArrayList<PathPartList> listOfPaths, IControlHandleList chlist, Shape<?> prim, ResizeControlHandle handle)
        {
            m_shape = shape;

            m_listOfPaths = listOfPaths;

            m_chlist = chlist;

            m_prim = prim;

            m_handle = handle;
        }

        @Override
        public void startDrag(DragContext dragContext)
        {
            BoundingBox box = m_shape.getBoundingBox();

            m_boxStartX = box.getX();

            m_boxStartY = box.getY();

            m_boxStartWidth = box.getWidth();

            m_boxStartHeight = box.getHeight();

            m_offsetX = m_shape.getX();

            m_offsetY = m_shape.getY();

            repositionAndResortHandles(box);

            copyDoubles();

            if ((m_handle.isActive()) && (m_chlist.isActive()))
            {
                m_prim.setFillColor(ColorName.GREEN);

                m_prim.getLayer().draw();
            }
        }

        /**
         * If the handles are flip horizontally or vertically, they must be re-aligned with the correct box corner.
         * @param box
         */
        private void repositionAndResortHandles(BoundingBox box)
        {
            double x = m_handle.m_orderedChList.get(0).getPrimitive().getX();

            double y = m_handle.m_orderedChList.get(0).getPrimitive().getY();

            ResizeControlHandle topLeft = m_handle.m_orderedChList.get(0);

            for (ResizeControlHandle handle : m_handle.m_orderedChList)
            {
                if (handle.getPrimitive().getX() <= x && handle.getPrimitive().getY() <= y)
                {
                    x = handle.getPrimitive().getX();

                    y = handle.getPrimitive().getY();

                    topLeft = handle;
                }
            }
            topLeft.setPosition(0);

            x = m_handle.m_orderedChList.get(0).getPrimitive().getX();

            y = m_handle.m_orderedChList.get(0).getPrimitive().getY();

            ResizeControlHandle topRight = m_handle.m_orderedChList.get(0);

            for (ResizeControlHandle handle : m_handle.m_orderedChList)
            {
                if (handle.getPrimitive().getX() >= x && handle.getPrimitive().getY() <= y)
                {
                    x = handle.getPrimitive().getX();

                    y = handle.getPrimitive().getY();

                    topRight = handle;
                }
            }
            topRight.setPosition(1);

            x = m_handle.m_orderedChList.get(0).getPrimitive().getX();

            y = m_handle.m_orderedChList.get(0).getPrimitive().getY();

            ResizeControlHandle bottomRight = m_handle.m_orderedChList.get(0);

            for (ResizeControlHandle handle : m_handle.m_orderedChList)
            {
                if (handle.getPrimitive().getX() >= x && handle.getPrimitive().getY() >= y)
                {
                    x = handle.getPrimitive().getX();

                    y = handle.getPrimitive().getY();

                    bottomRight = handle;
                }
            }
            bottomRight.setPosition(2);

            x = m_handle.m_orderedChList.get(0).getPrimitive().getX();

            y = m_handle.m_orderedChList.get(0).getPrimitive().getY();

            ResizeControlHandle bottomLeft = m_handle.m_orderedChList.get(0);

            for (ResizeControlHandle handle : m_handle.m_orderedChList)
            {
                if (handle.getPrimitive().getX() <= x && handle.getPrimitive().getY() >= y)
                {
                    x = handle.getPrimitive().getX();

                    y = handle.getPrimitive().getY();

                    bottomLeft = handle;
                }
            }
            bottomLeft.setPosition(3);

            Collections.sort(m_handle.m_orderedChList, new Comparator<ResizeControlHandle>()
            {
                @Override
                public int compare(ResizeControlHandle h1, ResizeControlHandle h2)
                {
                    if (h1.getPosition() > h2.getPosition())
                    {
                        return 1;
                    }
                    if (h1.getPosition() < h2.getPosition())
                    {
                        return -1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            });
        }

        @Override
        public boolean adjust(Point2D dxy)
        {
            if ((m_handle.isActive()) && (m_chlist.isActive()))
            {
                if (!adjustPrimitive(dxy))
                {
                    return false;
                }

                for (PathPartList list : m_listOfPaths)
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
                                double newX = m_handle.getX(m_boxStartX, m_boxStartY, m_boxStartWidth, m_boxStartHeight, x, dxy.getX());

                                double y = doubles.get(1);
                                double newY = m_handle.getY(m_boxStartX, m_boxStartY, m_boxStartWidth, m_boxStartHeight, y, dxy.getY());

                                points.set(0, newX);
                                points.set(1, newY);

                                break;
                            }
                        }
                    }
                    list.resetBoundingBox();
                }

                m_handle.updateOtherHandles(dxy.getX(), dxy.getY(), m_offsetX, m_offsetY, m_boxStartX, m_boxStartY, m_boxStartWidth, m_boxStartHeight);

                m_shape.refresh();

                m_shape.getLayer().batch();
            }

            return true;
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            if ((m_handle.isActive()) && (m_chlist.isActive()))
            {
                for (PathPartList list : m_listOfPaths)
                {
                    list.resetBoundingBox();
                }
                m_prim.setFillColor(ColorName.DARKRED);

                m_prim.getLayer().draw();
            }
        }

        private void copyDoubles()
        {
            m_entries = new NFastArrayList<NFastDoubleArrayJSO>();

            for (PathPartList list : m_listOfPaths)
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

        public boolean adjustPrimitive(Point2D dxy)
        {
            BoundingBox sizeConstraints = m_shape.getAttributes().getSizeConstraints();

            if (sizeConstraints == null)
            {
                return true;
            }

            double minWidth = sizeConstraints.getMinX();

            double maxWidth = sizeConstraints.getMaxX();

            double minHeight = sizeConstraints.getMinY();

            double maxHeight = sizeConstraints.getMaxY();

            Point2D adjustedDelta = adjustForPosition(dxy);

            double adjustedX = adjustedDelta.getX();

            double adjustedY = adjustedDelta.getY();

            double width = m_boxStartWidth + adjustedX;

            double height = m_boxStartHeight + adjustedY;

            boolean needsAdjustment = false;

            if (width < minWidth)
            {
                double difference = width - minWidth;

                adjustedDelta.setX(adjustedX - difference);
            }
            else
            {
                needsAdjustment = true;
            }

            if (width > maxWidth)
            {
                double difference = width - maxWidth;

                adjustedDelta.setX(adjustedX - difference);
            }
            else
            {
                needsAdjustment = true;
            }

            if (height < minHeight)
            {
                double difference = height - minHeight;

                adjustedDelta.setY(adjustedY - difference);
            }
            else
            {
                needsAdjustment = true;
            }

            if (height > maxHeight)
            {
                double difference = height - maxHeight;

                adjustedDelta.setY(adjustedY - difference);
            }
            else
            {
                needsAdjustment = true;
            }

            adjustedDelta = adjustForPosition(adjustedDelta);

            dxy.setX(adjustedDelta.getX());

            dxy.setY(adjustedDelta.getY());

            return needsAdjustment;
        }

        private Point2D adjustForPosition(Point2D dxy)
        {
            Point2D adjustedDXY = dxy.copy();

            double x = adjustedDXY.getX();

            double y = adjustedDXY.getY();

            switch (m_handle.getPosition())
            {
                case 0: //tl
                    x *= -1;
                    y *= -1;
                    break;
                case 1: //tr
                    y *= -1;
                    break;
                case 2: //br
                    break;
                case 3: //bl
                    x *= -1;
                    break;
            }

            adjustedDXY.setX(x);
            adjustedDXY.setY(y);

            return adjustedDXY;
        }
    }
}