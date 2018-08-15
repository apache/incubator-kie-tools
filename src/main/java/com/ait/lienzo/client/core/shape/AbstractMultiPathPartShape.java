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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.Attribute;
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
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.WiresShapeControlHandleList;
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

    private NFastDoubleArrayJSO                m_pointRatios;

    protected BoundingBox                      m_box;

    private static final int TOP_LEFT     = 0;
    private static final int TOP_RIGHT    = 1;
    private static final int BOTTOM_RIGHT = 2;
    private static final int BOTTOM_LEFT  = 3;

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
        if (m_box != null)
        {
            return m_box;
        }

        NFastArrayList<PathPartList> points = m_points;

        if (getCornerRadius() > 0)
        {
            points = m_cornerPoints;
        }
        final int size = points.size();

        if (size < 1)
        {
            m_box = new BoundingBox(0, 0, 0, 0);
            return m_box;
        }
        m_box = new BoundingBox();

        for (int i = 0; i < size; i++)
        {
            m_box.add(points.get(i).getBoundingBox());
        }
        return m_box;
    }


    public void resetBoundingBox()
    {
        m_box = null;
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

        resetBoundingBox();

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

    public Double getMinWidth()
    {
        if (getAttributes().isDefined(Attribute.MIN_WIDTH))
        {
            return getAttributes().getMinWidth();
        }
        else
        {
            return null;
        }
    }

    public T setMinWidth(final Double minWidth)
    {
        getAttributes().setMinWidth(minWidth);
        return refresh();
    }

    public Double getMaxWidth()
    {
        if (getAttributes().isDefined(Attribute.MAX_WIDTH))
        {
            return getAttributes().getMaxWidth();
        }
        else
        {
            return null;
        }
    }

    public T setMaxWidth(final Double maxWidth)
    {
        getAttributes().setMaxWidth(maxWidth);
        return refresh();
    }

    public Double getMinHeight()
    {
        if (getAttributes().isDefined(Attribute.MIN_HEIGHT))
        {
            return getAttributes().getMinHeight();
        }
        else
        {
            return null;
        }
    }

    public T setMinHeight(final Double minHeight)
    {
        getAttributes().setMinHeight(minHeight);
        return refresh();
    }

    public Double getMaxHeight()
    {
        if (getAttributes().isDefined(Attribute.MAX_HEIGHT))
        {
            return getAttributes().getMaxHeight();
        }
        else
        {
            return null;
        }
    }

    public T setMaxHeight(final Double maxHeight)
    {
        getAttributes().setMaxHeight(maxHeight);
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
        private final AbstractMultiPathPartShape    m_shape;

        private IControlHandleList                  m_chlist;

        private double[]                            m_startPoints;

        private HandlerRegistration                 m_nodeDragStartHandlerReg;

        private HandlerRegistration                 m_nodeDragMoveHandlerReg;

        public OnDragMoveIControlHandleList(final AbstractMultiPathPartShape shape, final IControlHandleList chlist)
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

        private final AbstractMultiPathPartShape<?> m_shape;

        private DragMode m_dmode = DragMode.SAME_LAYER;

        public DefaultMultiPathShapeHandleFactory(NFastArrayList<PathPartList> listOfPaths, AbstractMultiPathPartShape<?> shape)
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

        public static IControlHandleList getResizeHandles(AbstractMultiPathPartShape<?> shape, NFastArrayList<PathPartList> listOfPaths, DragMode dragMode)
        {
            final ControlHandleList chlist = new ControlHandleList(shape);

            BoundingBox box = shape.getBoundingBox();

            final Point2D tl = new Point2D(box.getX(), box.getY());

            final Point2D tr = new Point2D(box.getX() + box.getWidth(), box.getY());

            final Point2D bl = new Point2D(box.getX(), box.getHeight() + box.getY());

            final Point2D br = new Point2D(box.getX() + box.getWidth(), box.getHeight() + box.getY());

            final ResizeControlHandle topLeft = getResizeControlHandle(chlist, shape, listOfPaths, tl, TOP_LEFT, dragMode);

            chlist.add(topLeft);

            final ResizeControlHandle topRight = getResizeControlHandle(chlist, shape, listOfPaths, tr, TOP_RIGHT, dragMode);

            chlist.add(topRight);

            final ResizeControlHandle bottomRight = getResizeControlHandle(chlist, shape, listOfPaths, br, BOTTOM_RIGHT, dragMode);

            chlist.add(bottomRight);

            ResizeControlHandle bottomLeft = getResizeControlHandle(chlist, shape, listOfPaths, bl, 3, dragMode);

            chlist.add(bottomLeft);

            new OnDragMoveIControlHandleList(shape, chlist);

            return chlist;
        }

        private static ResizeControlHandle getResizeControlHandle(IControlHandleList chlist, AbstractMultiPathPartShape<?> shape, NFastArrayList<PathPartList> listOfPaths, Point2D point, int position, DragMode dragMode)
        {
            final Circle prim = getControlPrimitive(R0, point.getX(), point.getY(), shape, dragMode);

            ResizeControlHandle handle = new ResizeControlHandle(prim, chlist, shape, listOfPaths, position);

            animate(handle, AnimationProperty.Properties.RADIUS(R1), AnimationProperty.Properties.RADIUS(R0));

            return handle;
        }

        private static Circle getControlPrimitive(final double size, final double x, final double y, final AbstractMultiPathPartShape shape, final DragMode dragMode)
        {
            return new Circle(size).setX(x + shape.getX()).setY(y + shape.getY()).setFillColor(ColorName.DARKRED).setFillAlpha(0.8).setStrokeColor(ColorName.BLACK).setStrokeWidth(0.5).setDraggable(true).setDragMode(dragMode);
        }
    }

    private static class PointControlHandle extends AbstractControlHandle
    {
        private final AbstractMultiPathPartShape   m_shape;

        private final NFastArrayList<PathPartList> m_listOfPaths;

        private final IControlHandleList           m_chlist;

        private final Shape<?>                     m_prim;

        private final int                          m_pathIndex;

        private final int                          m_entryIndex;

        public PointControlHandle(final Shape<?> prim, final int pathIndex, final int entryIndex, final AbstractMultiPathPartShape shape, final NFastArrayList<PathPartList> listOfPaths, final PathPartList plist, final IControlHandleList hlist)
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
        protected final AbstractMultiPathPartShape    m_shape;

        private final NFastArrayList<PathPartList>    m_listOfPaths;

        protected final IControlHandleList            m_chlist;

        protected final Shape<?>                      m_prim;

        protected final PointControlHandle            m_handle;

        protected NFastArrayList<NFastDoubleArrayJSO> m_entries;

        public PointHandleDragHandler(final AbstractMultiPathPartShape shape, final NFastArrayList<PathPartList> listOfPaths, final IControlHandleList chlist, final Shape<?> prim, final PointControlHandle handle)
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
                m_shape.resetBoundingBox();

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
                m_shape.resetBoundingBox();
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
        private final AbstractMultiPathPartShape<?> m_shape;

        private final NFastArrayList<PathPartList>   m_listOfPaths;

        private final IControlHandleList             m_chlist;

        private final Shape<?>                       m_prim;

        private int                                  m_position;

        public ResizeControlHandle(Circle prim, IControlHandleList hlist, AbstractMultiPathPartShape<?> shape, NFastArrayList<PathPartList> listOfPaths, int position)
        {
            m_prim = prim;

            m_chlist = hlist;

            m_shape = shape;

            m_position = position;

            m_listOfPaths = listOfPaths;

            init();
        }

        public void init()
        {
            final ResizeHandleDragHandler handler = new ResizeHandleDragHandler(m_shape, m_listOfPaths, m_chlist, m_prim, this);

            m_prim.setDragConstraints(handler);

            register(m_prim.addNodeDragEndHandler(handler));
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

        public double getX(final double startTopLeftX, final double startW, final double dx, double wpc)
        {
            double newX = 0;

            switch (m_position)
            {
                case 0:
                case 3:
                    newX = getLeft(startTopLeftX, startW, dx, wpc);
                    break;
                case 1:
                case 2:
                    newX = getRight(startTopLeftX, startW, dx, wpc);
                    break;
            }

            return newX;
        }

        public double getY(final double startTopLeftY, final double startH, final double dy, double hpc)
        {
            double newY = 0;

            switch (m_position)
            {
                case 0:
                case 1:
                    newY = getTop(startTopLeftY, startH, dy, hpc);
                    break;
                case 2:
                case 3:
                    newY = getBottom(startTopLeftY, startH, dy, hpc);
                    break;
            }

            return newY;
        }

        void updateOtherHandles(final double dx, final double dy, final double offsetX, final double offsetY, final double boxStartX, final double boxStartY, final double boxStartWidth, final double boxStartHeight)
        {
            switch (m_position)
            {
                case TOP_LEFT:
                {
                    final IControlHandle bottomLeft = m_chlist.getHandle(BOTTOM_LEFT);
                    bottomLeft.getControl().setX(offsetX + boxStartX + dx);

                    final IControlHandle topRight = m_chlist.getHandle(TOP_RIGHT);
                    topRight.getControl().setY(offsetY + boxStartY + dy);
                    break;
                }
                case TOP_RIGHT:
                {
                    final IControlHandle bottomRight = m_chlist.getHandle(BOTTOM_RIGHT);
                    bottomRight.getControl().setX(offsetX + boxStartX + boxStartWidth + dx);

                    final IControlHandle topLeft = m_chlist.getHandle(TOP_LEFT);
                    topLeft.getControl().setY(offsetY + boxStartY + dy);
                    break;
                }
                case BOTTOM_RIGHT:
                {
                    final IControlHandle topRight = m_chlist.getHandle(TOP_RIGHT);
                    topRight.getControl().setX(offsetX + boxStartX + boxStartWidth + dx);

                    final IControlHandle bottomLeft = m_chlist.getHandle(BOTTOM_LEFT);
                    bottomLeft.getControl().setY(offsetY + boxStartY + boxStartHeight + dy);
                    break;
                }
                case BOTTOM_LEFT:
                {
                    final IControlHandle topLeft = m_chlist.getHandle(TOP_LEFT);
                    topLeft.getControl().setX(offsetX + boxStartX + dx);

                    final IControlHandle bottomRight = m_chlist.getHandle(BOTTOM_RIGHT);
                    bottomRight.getControl().setY(offsetY + boxStartY + boxStartHeight + dy);
                    break;
                }
            }
        }

        double getLeft(final double startTopLeftX, final double startW, final double dx, final double wpc)
        {
            double right = startTopLeftX + startW;
            double left = startTopLeftX + dx;
            double newX = left + (wpc * (right-left));

            return newX;
        }

        double getRight(final double startTopLeftX, final double startW, final double dx, final double wpc)
        {
            double right = startTopLeftX + startW + dx;
            double left = startTopLeftX ;
            double newX = left + (wpc * (right-left));

            return newX;
        }

        double getTop(final double startTopLeftY, final double startH, final double dy, final double hpc)
        {
            double top = startTopLeftY + dy;
            double bottom = startTopLeftY + startH;
            double newY = top + (hpc * (bottom-top));

            return newY;
        }

        double getBottom(final double startTopLeftY, final double startH, final double dy, final double hpc)
        {
            double top = startTopLeftY;
            double bottom = startTopLeftY +  startH + dy;
            double newY = top + (hpc * (bottom-top));

            return newY;
        }
    }

    public static class ResizeHandleDragHandler implements DragConstraintEnforcer, NodeDragEndHandler
    {
        private final AbstractMultiPathPartShape<?> m_shape;

        private final NFastArrayList<PathPartList>  m_listOfPaths;

        private final IControlHandleList            m_chlist;

        private final Shape<?>                      m_prim;

        private final ResizeControlHandle           m_handle;

        private double                              m_boxStartX;

        private double                              m_boxStartY;

        private double                              m_boxStartWidth;

        private double                              m_boxStartHeight;

        private double                              m_offsetX;

        private double                              m_offsetY;

        public ResizeHandleDragHandler(AbstractMultiPathPartShape<?> shape, NFastArrayList<PathPartList> listOfPaths, IControlHandleList chlist, Shape<?> prim, ResizeControlHandle handle)
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

            copyRatios();

            if ((m_handle.isActive()) && (m_chlist.isActive()))
            {
                m_prim.setFillColor(ColorName.GREEN);

                m_prim.getLayer().draw();
            }
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

                int position = m_handle.getPosition();

                resizePoints(dxy, position);

                m_shape.resetBoundingBox();

                m_handle.updateOtherHandles(dxy.getX(), dxy.getY(), m_offsetX, m_offsetY, m_boxStartX, m_boxStartY, m_boxStartWidth, m_boxStartHeight);

                m_shape.getLayer().batch();
            }

            return true;
        }

        private void resizePoints(final Point2D dxy, int position)
        {
            final NFastDoubleArrayJSO ratios   = m_shape.m_pointRatios;
            int                       ratioPos = 0;
            for (PathPartList list : m_listOfPaths)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    final PathPartEntryJSO pathPartEntry = list.get(i);

                    final NFastDoubleArrayJSO points = pathPartEntry.getPoints();

                    switch (pathPartEntry.getCommand())
                    {
                        case PathPartEntryJSO.MOVETO_ABSOLUTE:
                        case PathPartEntryJSO.LINETO_ABSOLUTE:
                        {
                            resizePoints(dxy, points, 1, ratioPos, ratios, position);
                            ratioPos = ratioPos + 2;
                            break;
                        }
                        case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE:
                        {
                            resizePoints(dxy, points, 3, ratioPos, ratios, position);
                            ratioPos = ratioPos + 6;
                            break;
                        }
                    }
                }
                list.resetBoundingBox();
            }
        }

        private void resizePoints(final Point2D dxy, NFastDoubleArrayJSO points, int numberOfPoints, int ratioPos, NFastDoubleArrayJSO ratios, int position)
        {
            for ( int i = 0; i < numberOfPoints * 2; i = i + 2)
            {
                double wpc = ratios.get(ratioPos);
                ratioPos++;
                double hpc = ratios.get(ratioPos);
                ratioPos++;
                resizePoint(dxy.getX(), dxy.getY(), points, i, wpc, hpc, position);
            }
        }

        private void resizePoint(double dx, double dy, NFastDoubleArrayJSO points, int i, double wpc, double hpc, int position)
        {

            double newX = getX(m_boxStartX, m_boxStartWidth, dx, wpc, position);
            double newY = getY(m_boxStartY, m_boxStartHeight, dy, hpc, position);

            points.set(i, newX);
            points.set(i+1, newY);
        }

        public double getX(final double startTopLeftX, final double startW, double dx, double wpc, int position)
        {
            double newX = 0;
            switch (position)
            {
                case TOP_LEFT:
                case BOTTOM_LEFT:
                    newX = getLeft(startTopLeftX, startW, dx, wpc);
                    break;
                case TOP_RIGHT:
                case BOTTOM_RIGHT:
                    newX = getRight(startTopLeftX, startW, dx, wpc);
                    break;
            }

            return newX;
        }

        public double getY(final double startTopLeftY, final double startH, final double dy, double hpc, int position)
        {
            double newY = 0;

            switch (m_handle.m_position)
            {
                case TOP_LEFT:
                case TOP_RIGHT:
                    newY = getTop(startTopLeftY, startH, dy, hpc);
                    break;
                case BOTTOM_LEFT:
                case BOTTOM_RIGHT:
                    newY = getBottom(startTopLeftY, startH, dy, hpc);
                    break;
            }

            return newY;
        }


        double getLeft(final double startTopLeftX, final double startW, final double dx, final double wpc)
        {
            double right = startTopLeftX + startW;
            double left = startTopLeftX + dx;
            double newX = left + (wpc * (right-left));

            return newX;
        }

        double getRight(final double startTopLeftX, final double startW, final double dx, final double wpc)
        {
            double right = startTopLeftX + startW + dx;
            double left = startTopLeftX ;
            double newX = left + (wpc * (right-left));

            return newX;
        }

        double getTop(final double startTopLeftY, final double startH, final double dy, final double hpc)
        {
            double top = startTopLeftY + dy;
            double bottom = startTopLeftY + startH;
            double newY = top + (hpc * (bottom-top));

            return newY;
        }

        double getBottom(final double startTopLeftY, final double startH, final double dy, final double hpc)
        {
            double top = startTopLeftY;
            double bottom = startTopLeftY +  startH + dy;
            double newY = top + (hpc * (bottom-top));

            return newY;
        }

        private void shiftPoints(final Point2D dxy)
        {
            for (PathPartList list : m_listOfPaths)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    final PathPartEntryJSO pathPartEntry = list.get(i);

                    final NFastDoubleArrayJSO points = pathPartEntry.getPoints();

                    switch (pathPartEntry.getCommand())
                    {
                        case PathPartEntryJSO.MOVETO_ABSOLUTE:
                        case PathPartEntryJSO.LINETO_ABSOLUTE:
                        {
                            shiftPoints(dxy, points, 1);
                            break;
                        }
                        case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE:
                        {
                            shiftPoints(dxy, points, 3);
                            break;
                        }
                    }
                }
                list.resetBoundingBox();
            }
        }

        private void shiftPoints(final Point2D dxy, NFastDoubleArrayJSO points, int numberOfPoints)
        {
            for ( int i = 0; i < numberOfPoints * 2; i = i + 2)
            {
                points.set(i, points.get(i) + dxy.getX());
                points.set(i + 1, points.get(i + 1) + dxy.getY());
            }
        }

        @Override
        public void onNodeDragEnd(final NodeDragEndEvent event)
        {
            if ((m_handle.isActive()) && (m_chlist.isActive()))
            {
                updateRatiosIfFlip(event);

                // reset boxstart
                resetBoundingBoxBackToStartXY();

                for (final PathPartList list : m_listOfPaths)
                {
                    list.resetBoundingBox();

                }
                m_shape.resetBoundingBox();
                m_prim.setFillColor(ColorName.DARKRED);

                m_prim.getLayer().draw();
            }
        }

        private void resetBoundingBoxBackToStartXY()
        {
            BoundingBox box    = m_shape.getBoundingBox();
            double      xShift = 0;
            if (box.getX() != m_boxStartX)
            {
                xShift = box.getX() - m_boxStartX;
            }

            double yShift = 0;
            if (box.getY() != m_boxStartY )
            {
                yShift = box.getY() - m_boxStartY;
            }

            if ( xShift != 0 || yShift != 0)
            {
                Point2D dxy = new Point2D(0 - xShift, 0 - yShift);
                shiftPoints(dxy);

                Group g = ((Group) m_shape.getParent());
                g.setX(g.getX() + xShift);
                g.setY(g.getY() + yShift);

                // TODO (mdp) this is a hack for now, as pure lienzo shapes cannot access Wires yet, and no easy way to inject a callable interface.
                WiresShapeControlHandleList wiresHandleList = (WiresShapeControlHandleList) m_handle.getControl().getUserData();
                if (wiresHandleList != null)
                {
                    wiresHandleList.updateParentLocation();
                    wiresHandleList.getWiresShape().shapeMoved();
                }

                ResizeControlHandle topLeft = (ResizeControlHandle) m_handle.m_chlist.getHandle(TOP_LEFT);
                ResizeControlHandle topRight = (ResizeControlHandle) m_handle.m_chlist.getHandle(TOP_RIGHT);
                ResizeControlHandle bottomRight = (ResizeControlHandle) m_handle.m_chlist.getHandle(BOTTOM_RIGHT);
                ResizeControlHandle bottomLeft = (ResizeControlHandle) m_handle.m_chlist.getHandle(BOTTOM_LEFT);


                topLeft.getControl().setX(m_boxStartX).setY(m_boxStartY);
                topRight.getControl().setX(m_boxStartX+box.getWidth()).setY(m_boxStartY);
                bottomRight.getControl().setX(m_boxStartX+box.getWidth()).setY(m_boxStartY+box.getHeight());
                bottomLeft.getControl().setX(m_boxStartX).setY(m_boxStartY+box.getHeight());
            }
        }

        private void updateRatiosIfFlip(final NodeDragEndEvent event)
        {
            double dx = event.getDragContext().getDx();
            double dy = event.getDragContext().getDy();

            boolean flipH = false;
            boolean flipV = false;

            switch (m_handle.getPosition())
            {
                case TOP_LEFT:
                case TOP_RIGHT:
                {
                    if ( dy > m_boxStartHeight)
                    {
                        // it flipped horizontally
                        flipH =  true;
                    }
                    break;
                }
                case BOTTOM_LEFT:
                case BOTTOM_RIGHT:
                {
                    if ( m_boxStartHeight + dy < 0)
                    {
                        // it flipped horizontally
                        flipH =  true;
                    }
                    break;
                }
            }

            switch (m_handle.getPosition())
            {
                case TOP_LEFT:
                case BOTTOM_LEFT:
                {
                    if ( dx > m_boxStartWidth)
                    {
                        // it flipped horizontally
                        flipV =  true;
                    }
                    break;
                }
                case TOP_RIGHT:
                case BOTTOM_RIGHT:
                {
                    if ( m_boxStartWidth + dx < 0)
                    {
                        // it flipped horizontally
                        flipV =  true;
                    }
                    break;
                }
            }

            if (!flipV && !flipH)
            {
                // no flip, so nothing to do.
                return;
            }

            ResizeControlHandle topLeft = (ResizeControlHandle) m_handle.m_chlist.getHandle(TOP_LEFT);
            ResizeControlHandle topRight = (ResizeControlHandle) m_handle.m_chlist.getHandle(TOP_RIGHT);
            ResizeControlHandle bottomRight = (ResizeControlHandle) m_handle.m_chlist.getHandle(BOTTOM_RIGHT);
            ResizeControlHandle bottomLeft = (ResizeControlHandle) m_handle.m_chlist.getHandle(BOTTOM_LEFT);

            // remove, they will be re-added back, in correct order
            m_handle.m_chlist.remove(topLeft);
            m_handle.m_chlist.remove(topRight);
            m_handle.m_chlist.remove(bottomRight);
            m_handle.m_chlist.remove(bottomLeft);

            if (flipV)
            {
                ResizeControlHandle temp = topLeft;
                topLeft = topRight;
                topRight = temp;
                topLeft.setPosition(TOP_LEFT);
                topRight.setPosition(TOP_RIGHT);

                temp = bottomLeft;
                bottomLeft = bottomRight;
                bottomRight = temp;
                bottomLeft.setPosition(BOTTOM_LEFT);
                bottomRight.setPosition(BOTTOM_RIGHT);
            }

            if (flipH)
            {
                ResizeControlHandle temp = topLeft;
                topLeft = bottomLeft;
                bottomLeft = temp;
                topLeft.setPosition(TOP_LEFT);
                bottomLeft.setPosition(BOTTOM_LEFT);

                temp = topRight;
                topRight = bottomRight;
                bottomRight = temp;
                topRight.setPosition(TOP_RIGHT);
                bottomRight.setPosition(BOTTOM_RIGHT);
            }
            m_handle.m_chlist.add(topLeft);
            m_handle.m_chlist.add(topRight);
            m_handle.m_chlist.add(bottomRight);
            m_handle.m_chlist.add(bottomLeft);

            final NFastDoubleArrayJSO ratios   = m_shape.m_pointRatios;
            int                       ratioPos = 0;
            NFastDoubleArrayJSO       reversed = NFastDoubleArrayJSO.make();
            for (PathPartList list : m_listOfPaths)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    final PathPartEntryJSO pathPartEntry = list.get(i);

                    switch (pathPartEntry.getCommand())
                    {
                        case PathPartEntryJSO.MOVETO_ABSOLUTE:
                        case PathPartEntryJSO.LINETO_ABSOLUTE:
                        {
                            reversed.push(getRatio(flipV, ratios, ratioPos));
                            reversed.push(getRatio(flipH, ratios, ratioPos+1));
                            ratioPos = ratioPos + 2;
                            break;
                        }
                        case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE:
                        {
                            // reverse
                            reversed.push(getRatio(flipV, ratios, ratioPos));
                            reversed.push(getRatio(flipH, ratios, ratioPos+1));
                            reversed.push(getRatio(flipV, ratios, ratioPos+2));
                            reversed.push(getRatio(flipH, ratios, ratioPos+3));
                            reversed.push(getRatio(flipV, ratios, ratioPos+4));
                            reversed.push(getRatio(flipH, ratios, ratioPos+5));
                            ratioPos = ratioPos + 6;
                            break;
                        }
                    }
                }
                m_shape.m_pointRatios = reversed;
            }
        }

        private double getRatio(final boolean flip, final NFastDoubleArrayJSO ratios, final int ratioPos)
        {
            return flip ? 1-ratios.get(ratioPos) : ratios.get(ratioPos);
        }

        private void copyRatios()
        {
            NFastDoubleArrayJSO pointRatios = m_shape.m_pointRatios;
            if (pointRatios == null)
            {
                pointRatios = NFastDoubleArrayJSO.make();
                m_shape.m_pointRatios = pointRatios;

                for (PathPartList pathPart : m_listOfPaths)
                {
                    for (int i = 0; i < pathPart.size(); i++)
                    {
                        final PathPartEntryJSO entry = pathPart.get(i);
                        final NFastDoubleArrayJSO points = entry.getPoints();

                        switch (entry.getCommand())
                        {
                            case PathPartEntryJSO.MOVETO_ABSOLUTE:
                            case PathPartEntryJSO.LINETO_ABSOLUTE:
                            {
                                addPointRatio(pointRatios, points, 0);
                                break;
                            }
                            case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE:
                            {
                                addPointRatio(pointRatios, points, 0);
                                addPointRatio(pointRatios, points, 2);
                                addPointRatio(pointRatios, points, 4);
                                break;
                            }
                        }

                    }
                }
            }
        }

        private void addPointRatio(final NFastDoubleArrayJSO pointRatios, final NFastDoubleArrayJSO points, final int j)
        {
            final double x = points.get(j);
            final double y = points.get(j+1);

            double xRatio = Geometry.getRatio(x, m_boxStartX, m_boxStartWidth);
            double yRatio = Geometry.getRatio(y, m_boxStartY, m_boxStartHeight);

            pointRatios.push(xRatio);
            pointRatios.push(yRatio);
        }

        public boolean adjustPrimitive(Point2D dxy)
        {
            Double minWidth = m_shape.getMinWidth();

            Double maxWidth = m_shape.getMaxWidth();

            Double minHeight = m_shape.getMinHeight();

            Double maxHeight = m_shape.getMaxHeight();

            Point2D adjustedDelta = adjustForPosition(dxy);

            double adjustedX = adjustedDelta.getX();

            double adjustedY = adjustedDelta.getY();

            double width = m_boxStartWidth + adjustedX;

            double height = m_boxStartHeight + adjustedY;

            boolean needsAdjustment = false;

            if (minWidth != null && width < minWidth)
            {
                double difference = width - minWidth;

                adjustedDelta.setX(adjustedX - difference);
            }
            else
            {
                needsAdjustment = true;
            }

            if (maxWidth != null && width > maxWidth)
            {
                double difference = width - maxWidth;

                adjustedDelta.setX(adjustedX - difference);
            }
            else
            {
                needsAdjustment = true;
            }

            if (minHeight != null && height < minHeight)
            {
                double difference = height - minHeight;

                adjustedDelta.setY(adjustedY - difference);
            }
            else
            {
                needsAdjustment = true;
            }

            if (maxHeight != null && height > maxHeight)
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