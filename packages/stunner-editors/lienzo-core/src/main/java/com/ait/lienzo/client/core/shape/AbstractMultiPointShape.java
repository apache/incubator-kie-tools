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
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.wires.AbstractControlHandle;
import com.ait.lienzo.client.core.shape.wires.ControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.shared.core.types.ArrowType;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;

public abstract class AbstractMultiPointShape<T extends AbstractMultiPointShape<T> & IMultiPointShape<T>> extends Shape<T> implements IMultiPointShape<T> {

    @JsProperty
    protected Point2DArray points;

    private final PathPartList m_list = new PathPartList();

    @JsIgnore
    protected AbstractMultiPointShape(final ShapeType type) {
        super(type);
    }

    public T setControlPoints(final Point2DArray points) {
        this.points = points;

        return refresh();
    }

    public final Point2DArray getControlPoints() {
        return this.points;
    }

    @Override
    public PathPartList getPathPartList() {
        return m_list;
    }

    /**
     * Gets this triangles points.
     *
     * @return {@link Point2DArray}
     */
    public Point2DArray getPoints() {
        return this.points;
    }

    /**
     * Sets the end-points of this line.
     * The points should be a 2-element {@link Point2DArray}
     *
     * @param points
     * @return this Line
     */
    public T setPoints(final Point2DArray points) {
        this.points = points;

        return refresh();
    }

    @Override
    public T setPoint2DArray(final Point2DArray points) {
        if (points.size() > 3) {
            throw new IllegalArgumentException("Cannot have more than 3 points");
        }

        this.points = points;

        return refresh();
    }

    public void updatePoint(Point2D point, double x, double y) {
        updatePointAtIndex(indexOfPoint(point), x, y);
    }

    public void updatePointAtIndex(int index, double x, double y) {
        points.get(index).setX(x).setY(y);
        refresh();
    }

    public void updatePointCompleted(int index) {
        refresh();
    }

    @Override
    public Point2DArray getPoint2DArray() {
        return getPoints();
    }

    protected int indexOfPoint(Point2D point) {
        return points.asList().indexOf(point);
    }

    @Override
    public boolean isControlPointShape() {
        return false;
    }

    @Override
    public IMultiPointShape<?> asMultiPointShape() {
        return this;
    }

    @Override
    public IOffsetMultiPointShape<?> asOffsetMultiPointShape() {
        return null;
    }

    @Override
    public IDirectionalMultiPointShape<?> asDirectionalMultiPointShape() {
        return null;
    }

    @Override
    public IControlHandleFactory getControlHandleFactory() {
        IControlHandleFactory factory = super.getControlHandleFactory();

        if (null != factory) {
            return factory;
        }
        return new DefaultMultiPointShapeHandleFactory(this);
    }

    @Override
    public T refresh() {
        getPathPartList().clear();
        return super.refresh();
    }

    public static final class DefaultMultiPointShapeHandleFactory implements IControlHandleFactory {

        public static final double R0 = 5;

        public static final double R1 = 10;

        public static final double SELECTION_OFFSET = R0 * 2;

        private static final double ANIMATION_DURATION = 50;

        private final AbstractMultiPointShape<?> m_shape;

        private DragMode m_dmode = DragMode.SAME_LAYER;

        private DefaultMultiPointShapeHandleFactory(final AbstractMultiPointShape<?> shape) {
            m_shape = shape;
        }

        @Override
        public Map<ControlHandleType, IControlHandleList> getControlHandles(ControlHandleType... types) {
            return getControlHandles(Arrays.asList(types));
        }

        @Override
        public Map<ControlHandleType, IControlHandleList> getControlHandles(final List<ControlHandleType> types) {
            if ((null == types) || (types.isEmpty())) {
                return null;
            }
            HashMap<ControlHandleType, IControlHandleList> map = new HashMap<ControlHandleType, IControlHandleList>();
            for (ControlHandleType type : types) {
                if (type == ControlHandleStandardType.HANDLE) {
                    IControlHandleList chList = getPointHandles();
                    map.put(IControlHandle.ControlHandleStandardType.HANDLE, chList);
                } else if (type == ControlHandleStandardType.POINT) {
                    IControlHandleList chList = getPointHandles();
                    map.put(IControlHandle.ControlHandleStandardType.POINT, chList);
                } else if (type == ControlHandleStandardType.OFFSET) {
                    IControlHandleList chList = getSegmentHandles();
                    map.put(IControlHandle.ControlHandleStandardType.OFFSET, chList);
                }
            }
            return map;
        }

        private IControlHandleList getSegmentHandles() {
            final ControlHandleList chlist = new ControlHandleList(m_shape);
            HandlerRegistrationManager manager = chlist.getHandlerRegistrationManager();
            ShapeXorYChanged shapeXoYChangedHandler = new ShapeXorYChanged(m_shape, chlist);
            manager.register(m_shape.addNodeDragStartHandler(shapeXoYChangedHandler));
            manager.register(m_shape.addNodeDragMoveHandler(shapeXoYChangedHandler));
            manager.register(m_shape.addNodeDragEndHandler(shapeXoYChangedHandler));

            Point2DArray points = m_shape.getPoint2DArray();
            int size = points.size();
            if (size >= 2) {
                for (int i = 1; i < size - 2; i++) {
                    SegmentHandle handleI = SegmentHandle.build(i, m_shape);
                    chlist.add(handleI);
                }
            }

            return chlist;
        }

        public static final String HANDLE_FILL_COLOR = "#0088CE";
        public static final String USER_HANDLE_FILL_COLOR = "#F0601F";

        private static <T extends Shape<?>> T applyDefaultControlHandleAttributes(T control) {
            control.setFillColor(HANDLE_FILL_COLOR)
                    .setStrokeWidth(2)
                    .setStrokeColor("#FFFFFF")
                    .setFillAlpha(0.8)
                    .setStrokeAlpha(1);
            return control;
        }

        public static class SegmentHandle extends AbstractControlHandle {

            private final int index;
            private final AbstractMultiPointShape<?> shape;
            private SegmentXorYChanged handle;
            private HandleSegmentClick handleClick;
            private static int MINIMUM_OFFSET_SIZE = 20;

            public static SegmentHandle build(int index, AbstractMultiPointShape<?> shape) {
                SegmentHandle handle = new SegmentHandle(index, shape);
                Point2D p0 = handle.getP0();
                Point2D p1 = handle.getP1();

                if ((SegmentXorYChanged.isVertical(p0, p1) && Math.abs(p1.getY() - p0.getY()) > MINIMUM_OFFSET_SIZE) ||
                        (SegmentXorYChanged.isHorizontal(p0, p1) && Math.abs(p1.getX() - p0.getX()) > MINIMUM_OFFSET_SIZE)) {
                    return handle.init();
                }
                return null;
            }

            private SegmentHandle(int index, AbstractMultiPointShape<?> shape) {
                this.index = index;
                this.shape = shape;
            }

            public SegmentHandle init() {
                Point2D p0 = getP0();
                Point2D p1 = getP1();
                handle = new SegmentXorYChanged(this, shape, p0, p1);

                handleClick = new HandleSegmentClick(shape, p0, p1);

                final Arrow prim = (Arrow) handle.getPrimitive();
                register(prim.addNodeDragMoveHandler(handle));
                register(prim.addNodeDragStartHandler(handle));
                register(prim.addNodeDragEndHandler(handle));
                register(prim.addNodeMouseClickHandler(handleClick));

                prim.setDragConstraints(handle);
                final boolean horizontal = handle.isVertical();
                register(prim.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                    @Override
                    public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                        updateArrowAttributes(prim, SegmentXorYChanged.RADIUS * 1.5, horizontal);
                    }
                }));
                register(prim.addNodeMouseExitHandler(new NodeMouseExitHandler() {
                    @Override
                    public void onNodeMouseExit(NodeMouseExitEvent event) {
                        updateArrowAttributes(prim, SegmentXorYChanged.RADIUS, horizontal);
                    }
                }));
                return this;
            }

            private static void updateArrowAttributes(Arrow arrow, double radius, boolean horizontal) {
                arrow.setBaseWidth(SegmentXorYChanged.getBaseWidth(radius));
                arrow.setHeadWidth(SegmentXorYChanged.getHeadWidth(radius));
                arrow.setStart(SegmentXorYChanged.getArrowStartPoint(new Point2D(0, 0), horizontal, radius));
                arrow.setEnd(SegmentXorYChanged.getArrowEndPoint(new Point2D(0, 0), horizontal, radius));
                arrow.batch();
            }

            public void move() {
                handle.onMove();
            }

            public int getIndex() {
                return index;
            }

            public Point2D getP0() {
                return shape.getPoint2DArray().get(index);
            }

            public Point2D getP1() {
                return shape.getPoint2DArray().get(index + 1);
            }

            @Override
            public IPrimitive<?> getControl() {
                return handle.getPrimitive();
            }

            @Override
            public ControlHandleType getType() {
                return ControlHandleStandardType.HANDLE;
            }
        }

        public static class HandleSegmentClick implements NodeMouseClickHandler {

            private final Point2D p0;
            private final Point2D p1;
            private final AbstractMultiPointShape<?> shape;

            public HandleSegmentClick(AbstractMultiPointShape<?> shape, Point2D p0, Point2D p1) {
                this.p0 = p0;
                this.p1 = p1;
                this.shape = shape;
            }

            @Override
            public void onNodeMouseClick(NodeMouseClickEvent event) {
                // Add / remove inferred points on segment handle click
                if (shape instanceof PolyMorphicLine) {
                    ((PolyMorphicLine) shape).handleInferredPoints(p0, p1);
                }
            }
        }

        public static class SegmentXorYChanged extends HandleXorYChanged implements DragConstraintEnforcer {

            private final Point2D p0;
            private final Point2D p1;
            private final AbstractMultiPointShape<?> shape;
            private static final double RADIUS = 10;

            public SegmentXorYChanged(IControlHandle m_handle, AbstractMultiPointShape<?> shape, Point2D p0, Point2D p1) {
                super(m_handle, shape);
                this.p0 = p0;
                this.p1 = p1;
                this.shape = shape;
                init();
            }

            @Override
            Shape<?> buildPrimitive() {
                double px = isHorizontal() ? p0.getX() + (p1.getX() - p0.getX()) / 2 : p1.getX();
                double py = isVertical() ? p0.getY() + (p1.getY() - p0.getY()) / 2 : p1.getY();
                return applyDefaultControlHandleAttributes(buildArrow(new Point2D(0, 0), !isHorizontal(), RADIUS))
                        .setX(m_shape.getX() + px)
                        .setY(m_shape.getY() + py)
                        .setDraggable(true)
                        .setDragMode(DragMode.SAME_LAYER)
                        .setFillColor(getSegmentColor());
            }

            private String getSegmentColor() {
                if (shape instanceof PolyMorphicLine) {
                    if (!((PolyMorphicLine) shape).isInferred(p0) && !((PolyMorphicLine) shape).isInferred(p1)) {
                        return DefaultMultiPointShapeHandleFactory.USER_HANDLE_FILL_COLOR;
                    }
                }
                return DefaultMultiPointShapeHandleFactory.HANDLE_FILL_COLOR;
            }

            private static Point2D getArrowStartPoint(Point2D location,
                                                      boolean horizontal,
                                                      double radius) {
                Point2D start = new Point2D(horizontal ? location.getX() - radius : location.getX(),
                                            horizontal ? location.getY() : location.getY() - radius);
                return start;
            }

            private static Point2D getArrowEndPoint(Point2D location,
                                                    boolean horizontal,
                                                    double radius) {
                Point2D end = new Point2D(horizontal ? location.getX() + radius : location.getX(),
                                          horizontal ? location.getY() : location.getY() + radius);
                return end;
            }

            private static double getBaseWidth(double radius) {
                return radius / 2;
            }

            private static double getHeadWidth(double radius) {
                return radius;
            }

            private static Arrow buildArrow(Point2D location,
                                            boolean horizontal,
                                            double radius) {
                Point2D start = getArrowStartPoint(location, horizontal, radius);
                Point2D end = getArrowEndPoint(location, horizontal, radius);
                double baseWidth = getBaseWidth(radius);
                double headWidth = getHeadWidth(radius);
                Arrow arrow = new Arrow(start, end, baseWidth, headWidth, 45, 45, ArrowType.AT_BOTH_ENDS);
                return arrow;
            }

            @Override
            public void onMove() {
                double p0x = isVertical() ? m_prim.getX() - m_shape.getX() : p0.getX();
                double p0y = isHorizontal() ? m_prim.getY() - m_shape.getY() : p0.getY();
                double p1x = isVertical() ? m_prim.getX() - m_shape.getX() : p1.getX();
                double p1y = isHorizontal() ? m_prim.getY() - m_shape.getY() : p1.getY();
                p0.setX(p0x);
                p0.setY(p0y);
                p1.setX(p1x);
                p1.setY(p1y);
                m_shape.refresh();
            }

            private double deltamin;
            private double deltamax;

            @Override
            public void startDrag(DragContext dragContext) {
                Point2DArray points = m_shape.getPoint2DArray();
                int index = points.asList().indexOf(p0);
                int prevIndex = index - 1;
                int nextIndex = index + 2;
                Point2D prev = points.get(prevIndex);
                Point2D next = points.get(nextIndex);
                boolean horizontal = isHorizontal();
                double min = horizontal ? Math.min(prev.getY(), next.getY()) : Math.min(prev.getX(), next.getX());
                double max = horizontal ? Math.max(prev.getY(), next.getY()) : Math.max(prev.getX(), next.getX());
                /*if (prevIndex == 0 || (nextIndex == (points.size() - 1))) {
                    min = (min > 0) ? min - 1 : min + 1;
                    max = (max > 0) ? max - 1 : max + 1;
                }*/
                deltamin = min - (horizontal ? p0.getY() : p0.getX());
                deltamax = max - (horizontal ? p0.getY() : p0.getX());
            }

            @Override
            public boolean adjust(Point2D dxy) {
                if (dxy.getX() == 0 && dxy.getY() == 0) {
                    return true;
                }
                Point2D delta = null;
                if (isHorizontal()) {
                    dxy.setX(0);
                    if (deltamin <= 0 && dxy.getY() <= deltamin) {
                        dxy.setY(deltamin);
                    } else if (deltamin > 0 && dxy.getY() >= deltamax) {
                        dxy.setY(deltamin);
                    } else if (deltamax >= 0 && dxy.getY() >= deltamax) {
                        dxy.setY(deltamax);
                    } else if (deltamax < 0 && dxy.getY() <= deltamax) {
                        dxy.setY(deltamax);
                    }
                    delta = dxy;
                } else if (isVertical()) {
                    dxy.setY(0);
                    if (deltamin <= 0 && dxy.getX() <= deltamin) {
                        dxy.setX(deltamin);
                    } else if (deltamin > 0 && dxy.getX() >= deltamin) {
                        dxy.setX(deltamin);
                    } else if (deltamax >= 0 && dxy.getX() >= deltamax) {
                        dxy.setX(deltamax);
                    } else if (deltamax < 0 && dxy.getX() <= deltamax) {
                        dxy.setX(deltamax);
                    }
                    delta = dxy;
                }
                return null != delta;
            }

            public boolean isHorizontal() {
                return isHorizontal(p0, p1);
            }

            public boolean isVertical() {
                return isVertical(p0, p1);
            }

            public static boolean isVertical(Point2D p0, Point2D p1) {
                return p1.getX() == p0.getX();
            }

            public static boolean isHorizontal(Point2D p0, Point2D p1) {
                return p1.getY() == p0.getY();
            }

            @Override
            Point2D getPoint() {
                return p1;
            }
        }

        public static class ConnectionHandle extends AbstractPointControlHandle {

            private final boolean headNorTail;
            private final AbstractMultiPointShape<?> shape;
            private ConnectionHandleChanged handle;

            private ConnectionHandle(boolean headNorTail, AbstractMultiPointShape<?> shape) {
                this.headNorTail = headNorTail;
                this.shape = shape;
            }

            @Override
            public ConnectionHandle init() {
                handle = new ConnectionHandleChanged(headNorTail, this, shape);
                final Shape<?> prim = handle.getPrimitive();
                register(prim.addNodeDragMoveHandler(handle));
                register(prim.addNodeDragStartHandler(handle));
                register(prim.addNodeDragEndHandler(handle));
                register(prim.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                    @Override
                    public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                        animate(prim, R1);
                    }
                }));
                register(prim.addNodeMouseExitHandler(new NodeMouseExitHandler() {
                    @Override
                    public void onNodeMouseExit(NodeMouseExitEvent event) {
                        animate(prim, R0);
                    }
                }));
                return this;
            }

            @Override
            public Point2D getPoint() {
                return headNorTail ? shape.getPoints().get(0) : shape.getPoints().get(shape.getPoints().size() - 1);
            }

            public void move(double x, double y) {
                handle.move(x, y);
            }

            @Override
            public IPrimitive<?> getControl() {
                return handle.getPrimitive();
            }
        }

        public static class ConnectionHandleChanged extends HandleXorYChanged {

            private final boolean headNorTail;

            public ConnectionHandleChanged(boolean headNorTail, IControlHandle m_handle, AbstractMultiPointShape<?> m_shape) {
                super(m_handle, m_shape);
                this.headNorTail = headNorTail;
                init();
            }

            @Override
            Shape<?> buildPrimitive() {
                Circle circle = new ConnectionHandleShape(R0)
                        .setX(m_shape.getX() + getPoint().getX())
                        .setY(m_shape.getY() + getPoint().getY())
                        .setDraggable(true)
                        .setDragMode(DragMode.SAME_LAYER)
                        .setFillColor(ColorName.GOLD);
                return applyDefaultControlHandleAttributes(circle);
            }

            @Override
            public void move(double x, double y) {
                m_shape.updatePointAtIndex(headNorTail ? 0 : m_shape.getPoints().size() - 1, x, y);
            }

            @Override
            public void onNodeDragEnd(NodeDragEndEvent event) {
                super.onNodeDragEnd(event);
                if (isActive()) {
                    m_shape.updatePointCompleted(headNorTail ? 0 : m_shape.getPoints().size() - 1);
                }
            }

            @Override
            Point2D getPoint() {
                return headNorTail ? m_shape.getPoints().get(0) : m_shape.getPoints().get(m_shape.getPoints().size() - 1);
            }
        }

        private static ControlHandleShape buildOrthogonalControlPointShape() {
            double ratio = 1;
            double r = ratio * R1;
            double d = R1;
            return new ControlHandleShape(R0)
                    .setPath(new PathPartList()
                                     .M(-r, r)
                                     .L(-r, r + d)
                                     .L(-r + d, r + d))
                    .setPathStrokeColor(AbstractMultiPointShape.DefaultMultiPointShapeHandleFactory.HANDLE_FILL_COLOR);
        }

        public static class ConnectionHandleShape extends ControlHandleShape {

            private final PathPartList lockPath;
            private final PathPartList unlockPath;

            public ConnectionHandleShape(double radius) {
                super(radius);
                this.lockPath = getLockPath(radius);
                this.unlockPath = getUnlockPath(radius);
            }

            public void setConnectionLocked(boolean locked) {
                if (locked) {
                    setPath(lockPath);
                    setPathStrokeColor(ColorName.BLACK.getColorString());
                } else {
                    setPath(unlockPath);
                    setPathStrokeColor(ColorName.WHITE.getColorString());
                }
            }

            private static PathPartList getLockPath(double fontSize) {
                double fontSize2 = fontSize / 1.5;
                double fontSize4 = fontSize / 2;
                return new PathPartList()
                        .M(-fontSize4, fontSize2)
                        .L(-fontSize4, -fontSize2)
                        .L(fontSize4, -fontSize2)
                        .M(-fontSize4, 0)
                        .L(fontSize4, 0);
            }

            private static PathPartList getUnlockPath(double fontSize) {
                double fontSize2 = fontSize / 1.5;
                double fontSize4 = fontSize / 2;
                return new PathPartList()
                        .M(-fontSize4, fontSize2)
                        .L(0, -fontSize2)
                        .L(fontSize4, fontSize2)
                        .M(-fontSize4 / 2, fontSize4)
                        .L(fontSize4 / 2, fontSize4);
            }
        }

        private static class ControlHandleShape extends Circle {

            private PathPartList path;
            private String pathFillColor;
            private String pathStrokeColor;
            private boolean pathVisible;

            public ControlHandleShape(double radius) {
                super(radius);
                this.path = null;
                this.pathVisible = false;
                this.pathFillColor = null;
                this.pathStrokeColor = null;
            }

            public ControlHandleShape setPath(PathPartList path) {
                this.path = path;
                return this;
            }

            public ControlHandleShape setPathFillColor(String pathFillColor) {
                this.pathFillColor = pathFillColor;
                return this;
            }

            public ControlHandleShape setPathStrokeColor(String pathStrokeColor) {
                this.pathStrokeColor = pathStrokeColor;
                return this;
            }

            public void setPathVisible(boolean pathVisible) {
                this.pathVisible = pathVisible;
            }

            @Override
            protected void drawWithoutTransforms(Context2D context, double alpha, BoundingBox bounds) {
                super.drawWithoutTransforms(context, alpha, bounds);
                if (canDrawPath() && !context.isSelection()) {
                    drawPathWithoutTransforms(this, path, context, alpha, pathFillColor, pathStrokeColor);
                }
            }

            private boolean canDrawPath() {
                return pathVisible && null != path && path.size() > 0;
            }

            private static void drawPathWithoutTransforms(final Shape<?> shape,
                                                          final PathPartList path,
                                                          final Context2D context,
                                                          final double alpha,
                                                          final String fillColor,
                                                          final String strokeColor) {
                if (alpha <= 0) {
                    return;
                }
                if (context.path(path)) {
                    fillPath(shape, context, fillColor);
                }
                strokePath(shape, context, strokeColor);
            }

            private static void strokePath(final Shape<?> shape,
                                           final Context2D context,
                                           final String strokeColor) {
                if (null != strokeColor) {
                    context.save(shape.getID());
                    context.setStrokeColor(strokeColor);
                    context.setStrokeWidth(1.5);
                    context.stroke();
                    context.restore();
                }
            }

            private static void fillPath(final Shape<?> shape,
                                         final Context2D context,
                                         final String fillColor) {
                if (null != fillColor) {
                    context.save(shape.getID());
                    context.setFillColor(fillColor);
                    context.fill();
                    context.restore();
                }
            }
        }

        public static abstract class HandleXorYChanged implements NodeDragStartHandler,
                                                                  NodeDragMoveHandler,
                                                                  NodeDragEndHandler {

            protected IControlHandle m_handle;
            protected AbstractMultiPointShape<?> m_shape;
            protected Shape<?> m_prim;
            protected AbstractMultiPointShape<?> m_shadow;

            HandleXorYChanged(IControlHandle m_handle, AbstractMultiPointShape<?> m_shape) {
                this.m_handle = m_handle;
                this.m_shape = m_shape;
            }

            void init() {
                m_prim = buildPrimitive();
                m_prim.setSelectionStrokeOffset(SELECTION_OFFSET);
                m_prim.setSelectionBoundsOffset(SELECTION_OFFSET);
                m_prim.setFillBoundsForSelection(true);
            }

            abstract Shape<?> buildPrimitive();

            abstract Point2D getPoint();

            @Override
            public void onNodeDragStart(NodeDragStartEvent event) {
                if (isActive()) {
                    m_shadow = buildShadow(m_shape);
                    m_prim.getOverLayer().add(m_shadow);
                    m_prim.getOverLayer().batch();
                }
            }

            @Override
            public void onNodeDragMove(NodeDragMoveEvent event) {
                if (isActive()) {
                    onMove();
                }
            }

            @Override
            public void onNodeDragEnd(NodeDragEndEvent event) {
                if (isActive()) {
                    onMove();
                    m_prim.getOverLayer().remove(m_shadow);
                    m_prim.getOverLayer().batch();
                }
            }

            public void onMove() {
                double x = m_prim.getX() - m_shape.getX();
                double y = m_prim.getY() - m_shape.getY();
                move(x, y);
            }

            public void move(double x, double y) {
                Point2D point = getPoint();
                m_shape.updatePoint(point, x, y);
            }

            protected boolean isActive() {
                return m_handle.isActive();
            }

            public Shape<?> getPrimitive() {
                return m_prim;
            }

            public Layer getLayer() {
                return m_shape.getLayer();
            }
        }

        private IControlHandleList getPointHandles() {
            final ControlHandleList chlist = new ControlHandleList(m_shape);

            HandlerRegistrationManager manager = chlist.getHandlerRegistrationManager();

            ShapeXorYChanged shapeXoYChangedHandler = new ShapeXorYChanged(m_shape, chlist);

            manager.register(m_shape.addNodeDragStartHandler(shapeXoYChangedHandler));

            manager.register(m_shape.addNodeDragMoveHandler(shapeXoYChangedHandler));

            manager.register(m_shape.addNodeDragEndHandler(shapeXoYChangedHandler));

            Point2DArray points = m_shape.getPoint2DArray();

            ConnectionHandle c0 = new ConnectionHandle(true, m_shape).init();
            chlist.add(c0);

            int size = points.size();
            for (int i = 1; i < size - 1; i++) {
                final Point2D p = points.get(i);

                final Circle prim = applyDefaultControlHandleAttributes(buildOrthogonalControlPointShape())
                        .setX(m_shape.getX() + p.getX())
                        .setY(m_shape.getY() + p.getY())
                        .setDraggable(true)
                        .setDragMode(m_dmode);

                prim.setSelectionStrokeOffset(SELECTION_OFFSET);
                prim.setSelectionBoundsOffset(SELECTION_OFFSET);
                prim.setFillBoundsForSelection(true);

                final int idx = i;

                // Point handle.
                chlist.add(new AbstractPointControlHandle() {
                    @Override
                    public AbstractPointControlHandle init() {
                        ControlPointChanged handler = new ControlPointChanged(chlist, m_shape, idx, p, (ControlHandleShape) prim, this, m_shape.getLayer());

                        register(prim.addNodeDragMoveHandler(handler));

                        register(prim.addNodeDragStartHandler(handler));

                        register(prim.addNodeDragEndHandler(handler));

                        register(prim.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                            @Override
                            public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                                animate(prim, R1);
                            }
                        }));
                        register(prim.addNodeMouseExitHandler(new NodeMouseExitHandler() {
                            @Override
                            public void onNodeMouseExit(NodeMouseExitEvent event) {
                                animate(prim, R0);
                            }
                        }));

                        prim.setDragConstraints(handler);

                        setPoint(p);

                        return this;
                    }

                    @Override
                    public IPrimitive<?> getControl() {
                        return prim;
                    }

                    @Override
                    public void destroy() {
                        super.destroy();
                    }
                }.init());
            }

            ConnectionHandle cN = new ConnectionHandle(false, m_shape).init();
            chlist.add(cN);

            return chlist;
        }

        private static void animate(final IPrimitive<?> primitive, final double radius) {
            primitive.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.RADIUS(radius)), ANIMATION_DURATION);
        }
    }

    public static class ShapeXorYChanged implements NodeDragStartHandler,
                                                    NodeDragMoveHandler,
                                                    NodeDragEndHandler {

        private IControlHandleList m_handleList;

        private Shape<?> m_shape;

        private boolean m_dragging;

        public ShapeXorYChanged(Shape<?> shape, IControlHandleList handleList) {
            m_shape = shape;
            m_handleList = handleList;
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event) {
            shapeMoved();
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event) {
            m_dragging = true;
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event) {
            m_dragging = false;
        }

        private void shapeMoved() {
            for (IControlHandle handle : m_handleList) {
                Point2D p = ((AbstractPointControlHandle) handle).getPoint();

                handle.getControl().setX(m_shape.getX() + p.getX());

                handle.getControl().setY(m_shape.getY() + p.getY());
            }
            m_shape.getLayer().batch();
        }
    }

    public static class ControlPointChanged implements NodeDragStartHandler,
                                                       NodeDragMoveHandler,
                                                       NodeDragEndHandler,
                                                       DragConstraintEnforcer {

        private DefaultMultiPointShapeHandleFactory.ControlHandleShape m_prim;

        private AbstractPointControlHandle m_handle;

        private Point2D m_point;

        private IControlHandleList m_handleList;

        private AbstractMultiPointShape<?> m_shape;

        private AbstractMultiPointShape<?> m_shadow;

        private int m_index;

        private Layer m_layer;

        public ControlPointChanged(IControlHandleList handleList, AbstractMultiPointShape<?> shape, int index, Point2D point, DefaultMultiPointShapeHandleFactory.ControlHandleShape prim, AbstractPointControlHandle handle, Layer layer) {
            m_handleList = handleList;

            m_shape = shape;

            m_index = index;

            m_layer = layer;

            m_prim = prim;

            m_point = point;

            m_handle = handle;
        }

        public Layer getLayer() {
            return m_layer;
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event) {
            if ((m_handle.isActive()) && (m_handleList.isActive())) {
                m_shadow = buildShadow((AbstractMultiPointShape<?>) m_shape);
                m_prim.getOverLayer().add(m_shadow);
                m_prim.getOverLayer().batch();
                m_prim.getLayer().batch();
            }
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event) {
            if ((m_handle.isActive()) && (m_handleList.isActive())) {
                m_shape.updatePointCompleted(m_index);
                m_prim.getOverLayer().remove(m_shadow);
                m_prim.getOverLayer().batch();
                m_prim.getLayer().batch();
            }
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event) {
            if ((m_handle.isActive()) && (m_handleList.isActive())) {
                double x = m_prim.getX() - m_shape.getX();
                double y = m_prim.getY() - m_shape.getY();
                m_point.setX(x);
                m_point.setY(y);
                if (isOrthogonal()) {
                    m_prim.setPathVisible(true);
                } else {
                    m_prim.setPathVisible(false);
                }
                m_shape.refresh();
                m_shape.getLayer().batch();
            }
        }

        private boolean isOrthogonal() {
            if (m_handleList.size() >= 3 && m_index > 0 && (m_index < (m_handleList.size() - 1))) {
                AbstractPointControlHandle prev = (AbstractPointControlHandle) m_handleList.getHandle(m_index - 1);
                AbstractPointControlHandle next = (AbstractPointControlHandle) m_handleList.getHandle(m_index + 1);
                boolean orthogonal = Geometry.isOrthogonal(prev.getPoint(), m_point, next.getPoint());
                return orthogonal;
            }
            return false;
        }

        private final DefaultDragConstraintEnforcer dragBoundsEnforcer = new DefaultDragConstraintEnforcer();
        private double dragStartX;
        private double dragStartY;

        @Override
        public void startDrag(DragContext dragContext) {
            dragBoundsEnforcer.startDrag(dragContext);
            dragStartX = dragContext.getNode().getX();
            dragStartY = dragContext.getNode().getY();
        }

        @Override
        public boolean adjust(Point2D dxy) {
            // First check point is not out drag bounds (canvas bounds)
            if (!dragBoundsEnforcer.adjust(dxy)) {
                Point2D before = null;
                if (m_index > 0) {
                    before = m_shape.getPoints().get(m_index - 1);
                }
                Point2D after = null;
                if (m_index < m_handleList.size() - 1) {
                    after = m_shape.getPoints().get(m_index + 1);
                }
                Point2D target = new Point2D(dragStartX + dxy.getX(), dragStartY + dxy.getY());
                adjustPoint(before, target, after);
                dxy.setX(target.getX() - dragStartX);
                dxy.setY(target.getY() - dragStartY);
                return true;
            }
            return true;
        }

        private static final double SEGMENT_SNAP_DISTANCE = 5d;

        private static void adjustPoint(Point2D before, Point2D target, Point2D after) {

            double xDiffBefore = Double.MAX_VALUE;
            double yDiffBefore = Double.MAX_VALUE;

            if (before != null) {
                xDiffBefore = target.getX() - before.getX();
                yDiffBefore = target.getY() - before.getY();
            }

            double xDiffAfter = Double.MAX_VALUE;
            double yDiffAfter = Double.MAX_VALUE;

            if (after != null) {
                xDiffAfter = target.getX() - after.getX();
                yDiffAfter = target.getY() - after.getY();
            }

            if (Math.abs(xDiffBefore) < Math.abs(xDiffAfter) && Math.abs(xDiffBefore) <= SEGMENT_SNAP_DISTANCE) {
                target.setX(target.getX() - xDiffBefore);
            } else if (Math.abs(xDiffAfter) <= SEGMENT_SNAP_DISTANCE) {
                target.setX(target.getX() - xDiffAfter);
            }

            if (Math.abs(yDiffBefore) < Math.abs(yDiffAfter) && Math.abs(yDiffBefore) <= SEGMENT_SNAP_DISTANCE) {
                target.setY(target.getY() - yDiffBefore);
            } else if (Math.abs(yDiffAfter) <= SEGMENT_SNAP_DISTANCE) {
                target.setY(target.getY() - yDiffAfter);
            }
        }
    }

    private static PolyLine buildShadow(AbstractMultiPointShape<?> m_shape) {
        return new PolyLine(m_shape.getControlPoints().copy())
                .setFillColor(m_shape.getFillColor())
                .setStrokeColor(m_shape.getStrokeColor())
                .setStrokeWidth(m_shape.getStrokeWidth())
                .setAlpha(0.33)
                .setHeadDirection(m_shape.asDirectionalMultiPointShape().getHeadDirection())
                .setTailDirection(m_shape.asDirectionalMultiPointShape().getTailDirection())
                .setHeadOffset(0)
                .setTailOffset(0);
    }

    private static abstract class AbstractPointControlHandle extends AbstractControlHandle {

        private Point2D m_point;

        public abstract AbstractPointControlHandle init();

        public Point2D getPoint() {
            return m_point;
        }

        public void setPoint(Point2D point) {
            m_point = point;
        }

        @Override
        public final ControlHandleType getType() {
            return ControlHandleStandardType.POINT;
        }
    }
}
