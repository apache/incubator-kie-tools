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

package com.ait.lienzo.client.core.shape;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
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
import com.ait.lienzo.client.core.shape.wires.AbstractControlHandle;
import com.ait.lienzo.client.core.shape.wires.ControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.WiresShapeControlHandleList;
import com.ait.lienzo.client.core.shape.wires.decorator.IShapeDecorator;
import com.ait.lienzo.client.core.shape.wires.decorator.PointHandleDecorator;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.core.util.JsInteropUtils;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.collection.NFastDoubleArray;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import elemental2.core.JsArray;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;

import static com.ait.lienzo.client.core.shape.wires.decorator.IShapeDecorator.ShapeState.VALID;

public abstract class AbstractMultiPathPartShape<T extends AbstractMultiPathPartShape<T>>
        extends Shape<T> {

    private final NFastArrayList<PathPartList> m_points = new NFastArrayList<PathPartList>();
    private NFastArrayList<PathPartList> m_cornerPoints = new NFastArrayList<PathPartList>();

    private double[] m_pointRatios;

    protected BoundingBox m_box;

    private static final int TOP_LEFT = 0;
    private static final int TOP_RIGHT = 1;
    private static final int BOTTOM_RIGHT = 2;
    private static final int BOTTOM_LEFT = 3;

    @JsProperty
    private Double cornerRadius = 0d;

    @JsProperty
    private Double minWidth = -1d;

    @JsProperty
    private Double maxWidth = -1d;

    @JsProperty
    private Double minHeight = -1d;

    @JsProperty
    private Double maxHeight = -1d;

    @JsIgnore
    protected AbstractMultiPathPartShape(final ShapeType type) {
        super(type);
    }

    protected AbstractMultiPathPartShape<T> copyTo(AbstractMultiPathPartShape<T> other) {
        super.copyTo(other);
        JsInteropUtils.populate(this.m_points, other.m_points, PathPartList::copy);
        other.m_cornerPoints = JsInteropUtils.clone(this.m_cornerPoints, PathPartList::copy);
        other.m_pointRatios = this.m_pointRatios;
        other.m_box = null != m_box ? this.m_box.copy() : null;
        other.cornerRadius = this.cornerRadius;
        other.minWidth = this.minWidth;
        other.maxWidth = this.maxWidth;
        other.minHeight = this.minHeight;
        other.maxHeight = this.maxHeight;
        return other;
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (m_box != null) {
            return m_box;
        }

        NFastArrayList<PathPartList> points = getActualPathPartListArray();

        final int size = points.size();

        if (size < 1) {
            m_box = BoundingBox.fromDoubles(0, 0, 0, 0);
            return m_box;
        }
        m_box = new BoundingBox();

        for (int i = 0; i < size; i++) {
            m_box.addBoundingBox(points.get(i).getBoundingBox());
        }

        return m_box;
    }

    public void resetBoundingBox() {
        m_box = null;
    }

    @Override
    public T refresh() {
        return clear();
    }

    public T clear() {
        final int size = m_points.size();

        for (int i = 0; i < size; i++) {
            m_points.get(i).clear();
        }
        m_points.clear();

        final int cornerSize = m_cornerPoints.size();
        for (int i = 0; i < cornerSize; i++) {
            m_cornerPoints.get(i).clear();
        }
        m_cornerPoints.clear();

        resetBoundingBox();

        return cast();
    }

    @Override
    protected boolean prepare(Context2D context, double alpha) {
        return true;
    }

    protected final void add(final PathPartList list) {
        m_points.add(list);
    }

    public final NFastArrayList<PathPartList> getPathPartListArray() {
        return m_points;
    }

    public final NFastArrayList<PathPartList> getActualPathPartListArray() {
        double radius = getCornerRadius();

        if (getCornerRadius() > 0) {

            if (m_cornerPoints.size() > 0) {
                return m_cornerPoints;
            }

            if (radius != 0) {
                m_cornerPoints = new NFastArrayList<>();

                for (int i = 0; i < m_points.size(); i++) {
                    PathPartList baseList = m_points.get(i);

                    Point2DArray basePoints = baseList.getPoints();

                    PathPartList cornerList = new PathPartList();

                    Geometry.drawArcJoinedLines(cornerList, baseList, basePoints, radius);

                    m_cornerPoints.add(cornerList);
                }
            }
            return m_cornerPoints;
        } else {
            return m_points;
        }
    }

    @Override
    protected void drawWithoutTransforms(final Context2D context, double alpha, BoundingBox bounds) {
        alpha = alpha * getAlpha();

        if (alpha <= 0) {
            return;
        }
        if (prepare(context, alpha)) {
            NFastArrayList<PathPartList> points = m_points;

            if (getCornerRadius() > 0) {
                points = m_cornerPoints;
            }
            final int size = points.size();

            if (size < 1) {
                return;
            }
            for (int i = 0; i < size; i++) {
                setAppliedShadow(false);

                final PathPartList list = points.get(i);

                if (list.size() > 1) {
                    boolean fill = false;

                    if (context.path(list)) {
                        fill = fill(context, alpha);
                    }
                    stroke(context, alpha, fill);
                }
            }
        }
    }

    public Double getMinWidth() {
        return this.minWidth;
    }

    public T setMinWidth(final Double minWidth) {
        this.minWidth = minWidth;
        return refresh();
    }

    public Double getMaxWidth() {
        return this.maxWidth;
    }

    public T setMaxWidth(final Double maxWidth) {
        this.maxWidth = maxWidth;
        return refresh();
    }

    public Double getMinHeight() {
        return this.minHeight;
    }

    public T setMinHeight(final Double minHeight) {
        this.minHeight = minHeight;
        return refresh();
    }

    public Double getMaxHeight() {
        return this.maxHeight;
    }

    public T setMaxHeight(final Double maxHeight) {
        this.maxHeight = maxHeight;
        return refresh();
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public T setCornerRadius(final double radius) {
        this.cornerRadius = radius;
        return refresh();
    }

    @Override
    public IControlHandleFactory getControlHandleFactory() {
        IControlHandleFactory factory = super.getControlHandleFactory();

        if (null != factory) {
            return factory;
        }
        return new DefaultMultiPathShapeHandleFactory(m_points, this);
    }

    public static class OnDragMoveIControlHandleList implements NodeDragStartHandler,
                                                                NodeDragMoveHandler,
                                                                NodeDragEndHandler {

        private final AbstractMultiPathPartShape m_shape;

        private IControlHandleList m_chlist;

        private double[] m_startPoints;

        private HandlerRegistration m_nodeDragStartHandlerReg;

        private HandlerRegistration m_nodeDragMoveHandlerReg;

        public OnDragMoveIControlHandleList(final AbstractMultiPathPartShape shape, final IControlHandleList chlist) {
            m_shape = shape;

            m_chlist = chlist;

            HandlerRegistrationManager regManager = m_chlist.getHandlerRegistrationManager();

            m_nodeDragStartHandlerReg = m_shape.addNodeDragStartHandler(this);

            m_nodeDragMoveHandlerReg = m_shape.addNodeDragMoveHandler(this);

            regManager.register(m_nodeDragStartHandlerReg);

            regManager.register(m_nodeDragMoveHandlerReg);
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event) {
            int size = m_chlist.size();

            m_startPoints = new double[size * 2];

            int i = 0;

            for (IControlHandle handle : m_chlist) {
                m_startPoints[i] = handle.getControl().getX();

                m_startPoints[i + 1] = handle.getControl().getY();

                i = i + 2;
            }
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event) {
            int i = 0;

            for (IControlHandle handle : m_chlist) {
                IPrimitive<?> prim = handle.getControl();

                prim.setX(m_startPoints[i] + event.getDragContext().getDistanceAdjusted().getX());

                prim.setY(m_startPoints[i + 1] + event.getDragContext().getDistanceAdjusted().getY());

                i = i + 2;
            }
            m_shape.getLayer().draw();
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event) {
            m_startPoints = null;
        }
    }

    public static final class DefaultMultiPathShapeHandleFactory implements IControlHandleFactory {

        private final NFastArrayList<PathPartList> m_listOfPaths;

        private final AbstractMultiPathPartShape<?> m_shape;

        private DragMode m_dmode = DragMode.SAME_LAYER;

        public DefaultMultiPathShapeHandleFactory(NFastArrayList<PathPartList> listOfPaths, AbstractMultiPathPartShape<?> shape) {
            m_listOfPaths = listOfPaths;

            m_shape = shape;
        }

        @Override
        public Map<ControlHandleType, IControlHandleList> getControlHandles(ControlHandleType... types) {
            return getControlHandles(Arrays.asList(types));
        }

        @Override
        public Map<ControlHandleType, IControlHandleList> getControlHandles(List<ControlHandleType> types) {
            if ((null == types) || (types.isEmpty())) {
                return null;
            }
            HashMap<ControlHandleType, IControlHandleList> map = new HashMap<ControlHandleType, IControlHandleList>();

            for (ControlHandleType type : types) {
                if (type == IControlHandle.ControlHandleStandardType.RESIZE) {
                    IControlHandleList chList = getResizeHandles(m_shape, m_listOfPaths, m_dmode);

                    map.put(IControlHandle.ControlHandleStandardType.RESIZE, chList);
                } else if (type == IControlHandle.ControlHandleStandardType.POINT) {
                    IControlHandleList chList = getPointHandles();

                    map.put(IControlHandle.ControlHandleStandardType.POINT, chList);
                }
            }
            return map;
        }

        public IControlHandleList getPointHandles() {
            ControlHandleList chlist = new ControlHandleList(m_shape);

            NFastArrayList<Point2DArray> allPoints = new NFastArrayList<Point2DArray>();

            int pathIndex = 0;

            for (PathPartList path : m_listOfPaths.asList()) {
                Point2DArray points = path.getPoints();

                allPoints.add(points);

                int entryIndex = 0;

                for (Point2D point : points.asArray()) {
                    final Circle prim = getControlPrimitive(R0, point.getX(), point.getY(), m_shape, m_dmode);

                    PointControlHandle pointHandle = new PointControlHandle(prim, pathIndex, entryIndex++, m_shape, m_listOfPaths, path, chlist);

                    animate(pointHandle, AnimationProperty.Properties.RADIUS(R1), AnimationProperty.Properties.RADIUS(R0));

                    chlist.add(pointHandle);
                }
                pathIndex++;
            }
            new OnDragMoveIControlHandleList(m_shape, chlist);

            return chlist;
        }

        public static final double R0 = 5;

        public static final double R1 = 10;

        public static final double ANIMATION_DURATION = 150d;

        public static void animate(final AbstractControlHandle handle, final AnimationProperty initialProperty, final AnimationProperty endProperty) {
            final Node<?> node = (Node<?>) handle.getControl();

            handle.getHandlerRegistrationManager().register(node.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                @Override
                public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                    animate(node, initialProperty);
                }
            }));
            handle.getHandlerRegistrationManager().register(node.addNodeMouseExitHandler(new NodeMouseExitHandler() {
                @Override
                public void onNodeMouseExit(NodeMouseExitEvent event) {
                    animate(node, endProperty);
                }
            }));
        }

        public static void animate(final Node<?> node, final AnimationProperty property) {
            node.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(property), ANIMATION_DURATION);
        }

        public static IControlHandleList getResizeHandles(AbstractMultiPathPartShape<?> shape, NFastArrayList<PathPartList> listOfPaths, DragMode dragMode) {
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

            ResizeControlHandle bottomLeft = getResizeControlHandle(chlist, shape, listOfPaths, bl, BOTTOM_LEFT, dragMode);

            chlist.add(bottomLeft);

            new OnDragMoveIControlHandleList(shape, chlist);

            return chlist;
        }

        private static ResizeControlHandle getResizeControlHandle(IControlHandleList chlist, AbstractMultiPathPartShape<?> shape, NFastArrayList<PathPartList> listOfPaths, Point2D point, int position, DragMode dragMode) {
            final Circle prim = getControlPrimitive(R0, point.getX(), point.getY(), shape, dragMode);

            ResizeControlHandle handle = new ResizeControlHandle(prim, chlist, shape, listOfPaths, position);

            animate(handle, AnimationProperty.Properties.RADIUS(R1), AnimationProperty.Properties.RADIUS(R0));

            return handle;
        }

        public static Circle getControlPrimitive(final double size, final double x, final double y, final Shape<?> shape, final DragMode dragMode) {
            return PointHandleDecorator.decorateShape(new Circle(size)
                                                              .setX(x + shape.getX())
                                                              .setY(y + shape.getY())
                                                              .setDraggable(true)
                                                              .setDragMode(dragMode),
                                                      VALID);
        }
    }

    private static class PointControlHandle extends AbstractControlHandle {

        private final AbstractMultiPathPartShape m_shape;

        private final NFastArrayList<PathPartList> m_listOfPaths;

        private final IControlHandleList m_chlist;

        private final Shape<?> m_prim;

        private final int m_pathIndex;

        private final int m_entryIndex;

        public PointControlHandle(final Shape<?> prim, final int pathIndex, final int entryIndex, final AbstractMultiPathPartShape shape, final NFastArrayList<PathPartList> listOfPaths, final PathPartList plist, final IControlHandleList hlist) {
            m_shape = shape;

            m_listOfPaths = listOfPaths;

            m_chlist = hlist;

            m_prim = prim;

            m_pathIndex = pathIndex;

            m_entryIndex = entryIndex;

            init();
        }

        public void init() {
            PointHandleDragHandler topRightHandler = new PointHandleDragHandler(m_shape, m_listOfPaths, m_chlist, m_prim, this);

            register(m_prim.addNodeDragMoveHandler(topRightHandler));

            register(m_prim.addNodeDragStartHandler(topRightHandler));

            register(m_prim.addNodeDragEndHandler(topRightHandler));
        }

        public int getPathIndex() {
            return m_pathIndex;
        }

        public int getEntryIndex() {
            return m_entryIndex;
        }

        @Override
        public IPrimitive<?> getControl() {
            return m_prim;
        }

        @Override
        public void destroy() {
            super.destroy();
        }

        @Override
        public final ControlHandleType getType() {
            return ControlHandleStandardType.POINT;
        }
    }

    public static class PointHandleDragHandler implements NodeDragStartHandler,
                                                          NodeDragMoveHandler,
                                                          NodeDragEndHandler {

        protected final AbstractMultiPathPartShape m_shape;

        private final NFastArrayList<PathPartList> m_listOfPaths;

        protected final IControlHandleList m_chlist;

        protected final Shape<?> m_prim;

        protected final PointControlHandle m_handle;

        protected NFastArrayList<NFastDoubleArray> m_entries;

        public PointHandleDragHandler(final AbstractMultiPathPartShape shape, final NFastArrayList<PathPartList> listOfPaths, final IControlHandleList chlist, final Shape<?> prim, final PointControlHandle handle) {
            m_shape = shape;

            m_listOfPaths = listOfPaths;

            m_chlist = chlist;

            m_prim = prim;

            m_handle = handle;
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event) {
            copyDoubles();

            if ((m_handle.isActive()) && (m_chlist.isActive())) {
                // Set reversed colors
                PointHandleDecorator.decorateShape(m_prim, IShapeDecorator.ShapeState.INVALID);

                m_prim.getLayer().draw();
            }
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event) {
            if ((m_handle.isActive()) && (m_chlist.isActive())) {
                double dx = event.getDragContext().getDistanceAdjusted().getX();

                double dy = event.getDragContext().getDistanceAdjusted().getY();

                PathPartList list = m_listOfPaths.get(m_handle.getPathIndex());

                PathPartEntryJSO entry = list.get(m_handle.getEntryIndex());

                double[] points = entry.getPoints();

                switch (entry.getCommand()) {
                    case PathPartEntryJSO.MOVETO_ABSOLUTE:
                    case PathPartEntryJSO.LINETO_ABSOLUTE: {
                        NFastDoubleArray doubles = m_entries.get(m_handle.getEntryIndex());

                        double x = doubles.get(0);

                        double y = doubles.get(1);

                        points[0] = x + dx;

                        points[1] = y + dy;

                        break;
                    }
                }
                m_shape.resetBoundingBox();

                m_shape.getLayer().batch();
            }
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event) {
            if ((m_handle.isActive()) && (m_chlist.isActive())) {
                NFastArrayList<PathPartList> lists = m_listOfPaths;

                for (PathPartList list : lists.asList()) {
                    list.resetBoundingBox();
                }
                PointHandleDecorator.decorateShape(m_prim, VALID);

                m_prim.getLayer().draw();
            }
        }

        private void copyDoubles() {
            m_entries = new NFastArrayList<NFastDoubleArray>();

            NFastArrayList<PathPartList> lists = m_listOfPaths;

            for (PathPartList list : lists.asList()) {
                for (int i = 0; i < list.size(); i++) {
                    PathPartEntryJSO entry = list.get(i);

                    double[] points = entry.getPoints();

                    switch (entry.getCommand()) {
                        case PathPartEntryJSO.MOVETO_ABSOLUTE:
                        case PathPartEntryJSO.LINETO_ABSOLUTE: {
                            double x = points[0];

                            double y = points[1];

                            NFastDoubleArray doubles = NFastDoubleArray.make2P(x, y);

                            m_entries.push(doubles);

                            break;
                        }
                    }
                }
            }
        }
    }

    private static class ResizeControlHandle extends AbstractControlHandle {

        private final AbstractMultiPathPartShape<?> m_shape;

        private final NFastArrayList<PathPartList> m_listOfPaths;

        private final IControlHandleList m_chlist;

        private final Shape<?> m_prim;

        private int m_position;

        public ResizeControlHandle(Circle prim, IControlHandleList hlist, AbstractMultiPathPartShape<?> shape, NFastArrayList<PathPartList> listOfPaths, int position) {
            m_prim = prim;

            m_chlist = hlist;

            m_shape = shape;

            m_position = position;

            m_listOfPaths = listOfPaths;

            init();
        }

        public void init() {
            final ResizeHandleDragHandler handler = new ResizeHandleDragHandler(m_shape, m_listOfPaths, m_chlist, m_prim, this);

            m_prim.setDragConstraints(handler);

            register(m_prim.addNodeDragEndHandler(handler));
        }

        public int getPosition() {
            return m_position;
        }

        public void setPosition(int position) {
            m_position = position;
        }

        @Override
        public IPrimitive<?> getControl() {
            return m_prim;
        }

        @Override
        public void destroy() {
            super.destroy();
        }

        @Override
        public ControlHandleType getType() {
            return ControlHandleStandardType.RESIZE;
        }

        public Shape<?> getPrimitive() {
            return m_prim;
        }

        public double getX(final double startTopLeftX, final double startW, final double dx, double wpc) {
            double newX = 0;

            switch (m_position) {
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

        public double getY(final double startTopLeftY, final double startH, final double dy, double hpc) {
            double newY = 0;

            switch (m_position) {
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

        void updateOtherHandles(final double dx, final double dy, final double offsetX, final double offsetY, final double boxStartX, final double boxStartY, final double boxStartWidth, final double boxStartHeight) {
            switch (m_position) {
                case TOP_LEFT: {
                    final IControlHandle bottomLeft = m_chlist.getHandle(BOTTOM_LEFT);
                    bottomLeft.getControl().setX(offsetX + boxStartX + dx);

                    final IControlHandle topRight = m_chlist.getHandle(TOP_RIGHT);
                    topRight.getControl().setY(offsetY + boxStartY + dy);
                    break;
                }
                case TOP_RIGHT: {
                    final IControlHandle bottomRight = m_chlist.getHandle(BOTTOM_RIGHT);
                    bottomRight.getControl().setX(offsetX + boxStartX + boxStartWidth + dx);

                    final IControlHandle topLeft = m_chlist.getHandle(TOP_LEFT);
                    topLeft.getControl().setY(offsetY + boxStartY + dy);
                    break;
                }
                case BOTTOM_RIGHT: {
                    final IControlHandle topRight = m_chlist.getHandle(TOP_RIGHT);
                    topRight.getControl().setX(offsetX + boxStartX + boxStartWidth + dx);

                    final IControlHandle bottomLeft = m_chlist.getHandle(BOTTOM_LEFT);
                    bottomLeft.getControl().setY(offsetY + boxStartY + boxStartHeight + dy);
                    break;
                }
                case BOTTOM_LEFT: {
                    final IControlHandle topLeft = m_chlist.getHandle(TOP_LEFT);
                    topLeft.getControl().setX(offsetX + boxStartX + dx);

                    final IControlHandle bottomRight = m_chlist.getHandle(BOTTOM_RIGHT);
                    bottomRight.getControl().setY(offsetY + boxStartY + boxStartHeight + dy);
                    break;
                }
            }
        }

        double getLeft(final double startTopLeftX, final double startW, final double dx, final double wpc) {
            double right = startTopLeftX + startW;
            double left = startTopLeftX + dx;
            double newX = left + (wpc * (right - left));

            return newX;
        }

        double getRight(final double startTopLeftX, final double startW, final double dx, final double wpc) {
            double right = startTopLeftX + startW + dx;
            double left = startTopLeftX;
            double newX = left + (wpc * (right - left));

            return newX;
        }

        double getTop(final double startTopLeftY, final double startH, final double dy, final double hpc) {
            double top = startTopLeftY + dy;
            double bottom = startTopLeftY + startH;
            double newY = top + (hpc * (bottom - top));

            return newY;
        }

        double getBottom(final double startTopLeftY, final double startH, final double dy, final double hpc) {
            double top = startTopLeftY;
            double bottom = startTopLeftY + startH + dy;
            double newY = top + (hpc * (bottom - top));

            return newY;
        }
    }

    public static class ResizeHandleDragHandler implements DragConstraintEnforcer,
                                                           NodeDragEndHandler {

        private final AbstractMultiPathPartShape<?> m_shape;

        private final NFastArrayList<PathPartList> m_listOfPaths;

        private final IControlHandleList m_chlist;

        private final Shape<?> m_prim;

        private final ResizeControlHandle m_handle;

        private double m_boxStartX;

        private double m_boxStartY;

        private double m_boxStartWidth;

        private double m_boxStartHeight;

        private double m_offsetX;

        private double m_offsetY;

        public ResizeHandleDragHandler(AbstractMultiPathPartShape<?> shape, NFastArrayList<PathPartList> listOfPaths, IControlHandleList chlist, Shape<?> prim, ResizeControlHandle handle) {
            m_shape = shape;

            m_listOfPaths = listOfPaths;

            m_chlist = chlist;

            m_prim = prim;

            m_handle = handle;
        }

        @Override
        public void startDrag(DragContext dragContext) {
            BoundingBox box = m_shape.getBoundingBox();

            m_boxStartX = box.getX();

            m_boxStartY = box.getY();

            m_boxStartWidth = box.getWidth();

            m_boxStartHeight = box.getHeight();

            m_offsetX = m_shape.getX();

            m_offsetY = m_shape.getY();

            copyRatios();

            if ((m_handle.isActive()) && (m_chlist.isActive())) {
                // Set reversed colors
                PointHandleDecorator.decorateShape(m_prim, IShapeDecorator.ShapeState.INVALID);

                m_prim.getLayer().draw();
            }
        }

        @Override
        public boolean adjust(Point2D dxy) {
            if ((m_handle.isActive()) && (m_chlist.isActive())) {
                if (!adjustPrimitive(dxy)) {
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

        private void resizePoints(final Point2D dxy, int position) {
            final double[] ratios = m_shape.m_pointRatios;
            int ratioPos = 0;
            for (int k = 0, size = m_listOfPaths.size(); k < size; k++) {
                PathPartList list = m_listOfPaths.get(k);
                for (int i = 0; i < list.size(); i++) {
                    final PathPartEntryJSO pathPartEntry = list.get(i);

                    final double[] points = pathPartEntry.getPoints();

                    switch (pathPartEntry.getCommand()) {
                        case PathPartEntryJSO.MOVETO_ABSOLUTE:
                        case PathPartEntryJSO.LINETO_ABSOLUTE: {
                            resizePoints(dxy, points, 1, ratioPos, ratios, position);
                            ratioPos = ratioPos + 2;
                            break;
                        }
                        case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE: {
                            resizePoints(dxy, points, 3, ratioPos, ratios, position);
                            ratioPos = ratioPos + 6;
                            break;
                        }
                    }
                }
                list.resetBoundingBox();
            }
        }

        private void resizePoints(final Point2D dxy, double[] points, int numberOfPoints, int ratioPos, double[] ratios, int position) {
            for (int i = 0; i < numberOfPoints * 2; i = i + 2) {
                double wpc = ratios[ratioPos];
                ratioPos++;
                double hpc = ratios[ratioPos];
                ratioPos++;
                resizePoint(dxy.getX(), dxy.getY(), points, i, wpc, hpc, position);
            }
        }

        private void resizePoint(double dx, double dy, double[] points, int i, double wpc, double hpc, int position) {

            double newX = getX(m_boxStartX, m_boxStartWidth, dx, wpc, position);
            double newY = getY(m_boxStartY, m_boxStartHeight, dy, hpc, position);

            points[i] = newX;
            points[i + 1] = newY;
        }

        public double getX(final double startTopLeftX, final double startW, double dx, double wpc, int position) {
            double newX = 0;
            switch (position) {
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

        public double getY(final double startTopLeftY, final double startH, final double dy, double hpc, int position) {
            double newY = 0;

            switch (m_handle.m_position) {
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

        double getLeft(final double startTopLeftX, final double startW, final double dx, final double wpc) {
            double right = startTopLeftX + startW;
            double left = startTopLeftX + dx;
            double newX = left + (wpc * (right - left));

            return newX;
        }

        double getRight(final double startTopLeftX, final double startW, final double dx, final double wpc) {
            double right = startTopLeftX + startW + dx;
            double left = startTopLeftX;
            double newX = left + (wpc * (right - left));

            return newX;
        }

        double getTop(final double startTopLeftY, final double startH, final double dy, final double hpc) {
            double top = startTopLeftY + dy;
            double bottom = startTopLeftY + startH;
            double newY = top + (hpc * (bottom - top));

            return newY;
        }

        double getBottom(final double startTopLeftY, final double startH, final double dy, final double hpc) {
            double top = startTopLeftY;
            double bottom = startTopLeftY + startH + dy;
            double newY = top + (hpc * (bottom - top));

            return newY;
        }

        private void shiftPoints(final Point2D dxy) {
            for (PathPartList list : m_listOfPaths.asList()) {
                for (int i = 0; i < list.size(); i++) {
                    final PathPartEntryJSO pathPartEntry = list.get(i);

                    final double[] points = pathPartEntry.getPoints();

                    switch (pathPartEntry.getCommand()) {
                        case PathPartEntryJSO.MOVETO_ABSOLUTE:
                        case PathPartEntryJSO.LINETO_ABSOLUTE: {
                            shiftPoints(dxy, points, 1);
                            break;
                        }
                        case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE: {
                            shiftPoints(dxy, points, 3);
                            break;
                        }
                    }
                }
                list.resetBoundingBox();
            }
        }

        private void shiftPoints(final Point2D dxy, double[] points, int numberOfPoints) {
            for (int i = 0; i < numberOfPoints * 2; i = i + 2) {
                points[i] = points[i] + dxy.getX();
                points[i + 1] = points[i + 1] + dxy.getY();
            }
        }

        @Override
        public void onNodeDragEnd(final NodeDragEndEvent event) {
            if ((m_handle.isActive()) && (m_chlist.isActive())) {

                // reset boxstart
                resetBoundingBoxBackToStartXY();

                for (final PathPartList list : m_listOfPaths.asList()) {
                    list.resetBoundingBox();
                }
                m_shape.resetBoundingBox();
                PointHandleDecorator.decorateShape(m_prim, VALID);

                m_prim.getLayer().draw();
            }
        }

        private void resetBoundingBoxBackToStartXY() {
            BoundingBox box = m_shape.getBoundingBox();
            double xShift = 0;
            if (box.getX() != m_boxStartX) {
                xShift = box.getX() - m_boxStartX;
            }

            double yShift = 0;
            if (box.getY() != m_boxStartY) {
                yShift = box.getY() - m_boxStartY;
            }

            if (xShift != 0 || yShift != 0) {
                Point2D dxy = new Point2D(0 - xShift, 0 - yShift);
                shiftPoints(dxy);

                // This is a hack for now, as pure lienzo shapes cannot access Wires yet, and no easy way to inject a callable interface.
                WiresShapeControlHandleList wiresHandleList = (WiresShapeControlHandleList) m_handle.getControl().getUserData();
                if (wiresHandleList != null) {
                    Group g = ((Group) m_shape.getParent());
                    g.setX(g.getX() + xShift);
                    g.setY(g.getY() + yShift);

                    wiresHandleList.updateParentLocation();
                    wiresHandleList.getWiresShape().shapeMoved();
                }

                ResizeControlHandle topLeft = (ResizeControlHandle) m_handle.m_chlist.getHandle(TOP_LEFT);
                ResizeControlHandle topRight = (ResizeControlHandle) m_handle.m_chlist.getHandle(TOP_RIGHT);
                ResizeControlHandle bottomRight = (ResizeControlHandle) m_handle.m_chlist.getHandle(BOTTOM_RIGHT);
                ResizeControlHandle bottomLeft = (ResizeControlHandle) m_handle.m_chlist.getHandle(BOTTOM_LEFT);

                topLeft.getControl().setX(m_boxStartX).setY(m_boxStartY);
                topRight.getControl().setX(m_boxStartX + box.getWidth()).setY(m_boxStartY);
                bottomRight.getControl().setX(m_boxStartX + box.getWidth()).setY(m_boxStartY + box.getHeight());
                bottomLeft.getControl().setX(m_boxStartX).setY(m_boxStartY + box.getHeight());
            }
        }

        private void updateRatiosIfFlip(final NodeDragEndEvent event) {
            double dx = event.getDragContext().getDx();
            double dy = event.getDragContext().getDy();

            boolean flipH = false;
            boolean flipV = false;

            switch (m_handle.getPosition()) {
                case TOP_LEFT:
                case TOP_RIGHT: {
                    if (dy > m_boxStartHeight) {
                        // it flipped horizontally
                        flipH = true;
                    }
                    break;
                }
                case BOTTOM_LEFT:
                case BOTTOM_RIGHT: {
                    if (m_boxStartHeight + dy < 0) {
                        // it flipped horizontally
                        flipH = true;
                    }
                    break;
                }
            }

            switch (m_handle.getPosition()) {
                case TOP_LEFT:
                case BOTTOM_LEFT: {
                    if (dx > m_boxStartWidth) {
                        // it flipped horizontally
                        flipV = true;
                    }
                    break;
                }
                case TOP_RIGHT:
                case BOTTOM_RIGHT: {
                    if (m_boxStartWidth + dx < 0) {
                        // it flipped horizontally
                        flipV = true;
                    }
                    break;
                }
            }

            if (!flipV && !flipH) {
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

            if (flipV) {
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

            if (flipH) {
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

            final double[] ratios = m_shape.m_pointRatios;
            int ratioPos = 0;
            double[] reversed = new double[ratios.length];
            for (PathPartList list : m_listOfPaths.asList()) {
                for (int i = 0; i < list.size(); i++) {
                    final PathPartEntryJSO pathPartEntry = list.get(i);

                    switch (pathPartEntry.getCommand()) {
                        case PathPartEntryJSO.MOVETO_ABSOLUTE:
                        case PathPartEntryJSO.LINETO_ABSOLUTE: {
                            reversed[ratioPos] = getRatio(flipV, ratios, ratioPos);
                            reversed[ratioPos + 1] = getRatio(flipH, ratios, ratioPos + 1);
                            ratioPos = ratioPos + 2;
                            break;
                        }
                        case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE: {
                            // reverse
                            reversed[ratioPos] = getRatio(flipV, ratios, ratioPos);
                            reversed[ratioPos + 1] = getRatio(flipH, ratios, ratioPos + 1);
                            reversed[ratioPos + 2] = getRatio(flipV, ratios, ratioPos + 2);
                            reversed[ratioPos + 3] = getRatio(flipH, ratios, ratioPos + 3);
                            reversed[ratioPos + 4] = getRatio(flipV, ratios, ratioPos + 4);
                            reversed[ratioPos + 5] = getRatio(flipH, ratios, ratioPos + 5);
                            ratioPos = ratioPos + 6;
                            break;
                        }
                    }
                }
                m_shape.m_pointRatios = reversed;
            }
        }

        private double getRatio(final boolean flip, final double[] ratios, final int ratioPos) {
            return flip ? 1 - ratios[ratioPos] : ratios[ratioPos];
        }

        private void copyRatios() {
            if (m_shape.m_pointRatios == null) {
                JsArray<Double> pointRatios = new JsArray<Double>();

                for (PathPartList pathPart : m_listOfPaths.asList()) {
                    for (int i = 0; i < pathPart.size(); i++) {
                        final PathPartEntryJSO entry = pathPart.get(i);
                        final double[] points = entry.getPoints();

                        switch (entry.getCommand()) {
                            case PathPartEntryJSO.MOVETO_ABSOLUTE:
                            case PathPartEntryJSO.LINETO_ABSOLUTE: {
                                addPointRatio(pointRatios, points, 0);
                                break;
                            }
                            case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE: {
                                addPointRatio(pointRatios, points, 0);
                                addPointRatio(pointRatios, points, 2);
                                addPointRatio(pointRatios, points, 4);
                                break;
                            }
                        }
                    }
                }

                // copy back in, to coerce from Double to double
                m_shape.m_pointRatios = new double[pointRatios.length];
                for (int i = 0; i < m_shape.m_pointRatios.length; i++) {
                    m_shape.m_pointRatios[i] = pointRatios.getAt(i);
                }
            }
        }

        private void addPointRatio(final JsArray<Double> pointRatios, final double[] points, final int j) {
            final double x = points[j];
            final double y = points[j + 1];

            double xRatio = Geometry.getRatio(x, m_boxStartX, m_boxStartWidth);
            double yRatio = Geometry.getRatio(y, m_boxStartY, m_boxStartHeight);

            pointRatios.push(xRatio);
            pointRatios.push(yRatio);
        }

        public boolean adjustPrimitive(Point2D dxy) {
            double minWidth = m_shape.getMinWidth();

            double maxWidth = m_shape.getMaxWidth();

            double minHeight = m_shape.getMinHeight();

            double maxHeight = m_shape.getMaxHeight();

            Point2D adjustedDelta = adjustForPosition(dxy);

            double adjustedX = adjustedDelta.getX();

            double adjustedY = adjustedDelta.getY();

            double width = m_boxStartWidth + adjustedX;

            double height = m_boxStartHeight + adjustedY;

            boolean needsAdjustment = false;

            if (minWidth >= 0 && width < minWidth) {
                double difference = width - minWidth;

                adjustedDelta.setX(adjustedX - difference);
            } else {
                needsAdjustment = true;
            }

            if (maxWidth >= 0 && width > maxWidth) {
                double difference = width - maxWidth;

                adjustedDelta.setX(adjustedX - difference);
            } else {
                needsAdjustment = true;
            }

            if (minHeight >= 0 && height < minHeight) {
                double difference = height - minHeight;

                adjustedDelta.setY(adjustedY - difference);
            } else {
                needsAdjustment = true;
            }

            if (maxHeight >= 0 && height > maxHeight) {
                double difference = height - maxHeight;

                adjustedDelta.setY(adjustedY - difference);
            } else {
                needsAdjustment = true;
            }

            adjustedDelta = adjustForPosition(adjustedDelta);

            dxy.setX(adjustedDelta.getX());

            dxy.setY(adjustedDelta.getY());

            return needsAdjustment;
        }

        private Point2D adjustForPosition(Point2D dxy) {
            Point2D adjustedDXY = dxy.copy();

            double x = adjustedDXY.getX();

            double y = adjustedDXY.getY();

            switch (m_handle.getPosition()) {
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