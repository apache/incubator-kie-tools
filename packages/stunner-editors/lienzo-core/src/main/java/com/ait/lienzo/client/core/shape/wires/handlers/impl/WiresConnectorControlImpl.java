/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.AbstractMultiPointShape.DefaultMultiPointShapeHandleFactory.ConnectionHandleShape;
import com.ait.lienzo.client.core.shape.AbstractMultiPointShape.DefaultMultiPointShapeHandleFactory.SegmentHandle;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.decorator.IShapeDecorator.ShapeState;
import com.ait.lienzo.client.core.shape.wires.decorator.PointHandleDecorator;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.tools.client.collection.NFastDoubleArray;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;

/**
 * This class can be a little confusing, due to the way that drag works. All lines have a Group that is x=0, y=0. when
 * you drag a line, you actually drag a group. So the group x,y changes, the line does not. For this reason the CPs are
 * moved with the group, during drag. When the drag ends, the Group is re-adjusted back to 0,0 and the lines have their
 * points adjusted to reflect the final position. However if the lines are part selection that is being dragged then the
 * move behaviour is different, the group is not moving, so the line points must move. This differing behaviour is
 * controlled by booleans on the relevant classes.
 */
public class WiresConnectorControlImpl implements WiresConnectorControl {

    private final WiresConnector m_connector;

    private final WiresManager m_wiresManager;

    private final WiresConnectorControlPointBuilder m_cpBuilder;

    private NFastDoubleArray m_startPointHandles;

    private Point2DArray m_startLinePoints;

    private WiresConnectionControl m_headConnectionControl;

    private WiresConnectionControl m_tailConnectionControl;

    private PointHandleDecorator m_pointHandleDecorator;

    public WiresConnectorControlImpl(final WiresConnector connector,
                                     final WiresManager wiresManager) {
        this(connector,
             wiresManager,
             new WiresConnectorControlPointBuilder(WiresConnectorEventFunctions.canShowControlPoints(),
                                                   WiresConnectorEventFunctions.canHideControlPoints(wiresManager),
                                                   connector));
    }

    public WiresConnectorControlImpl(final WiresConnector connector,
                                     final WiresManager wiresManager,
                                     final WiresConnectorControlPointBuilder m_cpBuilder) {
        this.m_connector = connector;
        this.m_wiresManager = wiresManager;
        this.m_cpBuilder = m_cpBuilder;
        this.m_pointHandleDecorator = new PointHandleDecorator();
    }

    @Override
    public void onMoveStart(double x,
                            double y) {
        m_startLinePoints = m_connector.getLine().getPoint2DArray().copy();
        m_startPointHandles = new NFastDoubleArray();
        IControlHandleList handles = m_connector.getPointHandles();
        for (int i = 0; i < handles.size(); i++) {
            IControlHandle h = handles.getHandle(i);
            IPrimitive<?> prim = h.getControl();
            m_startPointHandles.push(prim.getX());
            m_startPointHandles.push(prim.getY());
        }
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        move(dx,
             dy);
        return false;
    }

    @Override
    public Point2D getAdjust() {
        return new Point2D(0,
                           0);
    }

    private void move(double dx,
                      double dy) {
        final boolean isConnected = isConnected(m_connector.getHeadConnection()) || isConnected(m_connector.getTailConnection());

        IControlHandleList handles = m_connector.getPointHandles();

        int start = 0;
        int end = handles.size();
        if (isConnected) {
            if (m_connector.getHeadConnection().getMagnet() != null) {
                start++;
            }
            if (m_connector.getTailConnection().getMagnet() != null) {
                end--;
            }
        }

        Point2DArray points = m_connector.getLine().getPoint2DArray();

        for (int i = start, j = (start == 0) ? start : 2; i < end; i++, j += 2) {
            if (isConnected) {
                Point2D p = points.get(i);
                p.setX(m_startPointHandles.get(j) + dx);
                p.setY(m_startPointHandles.get(j + 1) + dy);
            }

            IControlHandle h = handles.getHandle(i);
            IPrimitive<?> prim = h.getControl();
            prim.setX(m_startPointHandles.get(j) + dx);
            prim.setY(m_startPointHandles.get(j + 1) + dy);
        }

        if (isConnected) {
            m_connector.getLine().refresh();
        }

        WiresConnector.updateHeadTailForRefreshedConnector(m_connector);

        batch();
    }

    @Override
    public void onMoveComplete() {
        m_connector.getGroup().setX(0).setY(0);
        m_connector.getLine().refresh();
        if (null != m_wiresManager.getLayer().getLayer()) {
            m_wiresManager.getLayer().getLayer().draw();
        }
        hideControlPoints();
        showControlPoints();
    }

    @Override
    public void execute() {
        WiresConnector.updateHeadTailForRefreshedConnector(m_connector);
        getControlPointsAcceptor().update(m_connector,
                                          m_connector.getControlPoints().copy());
        hideControlPoints();
    }

    @Override
    public void clear() {
        m_startPointHandles = null;
        m_startLinePoints = null;
    }

    @Override
    public void reset() {
        boolean done = false;
        if (null != m_startPointHandles) {
            final IControlHandleList handles = m_connector.getPointHandles();
            for (int i = 0, j = 0; i < handles.size(); i++, j += 2) {
                final double px = m_startPointHandles.get(j);
                final double py = m_startPointHandles.get(j + 1);
                final IControlHandle h = handles.getHandle(i);
                final IPrimitive<?> prim = h.getControl();
                prim.setX(px);
                prim.setY(py);
            }
            done = true;
        }

        if (null != m_startLinePoints) {
            Point2DArray points = m_connector.getLine().getPoint2DArray();
            for (int i = 0; i < points.size(); i++) {
                final Point2D point = points.get(i);
                final Point2D startPoint = m_startLinePoints.get(i);
                point.setX(startPoint.getX());
                point.setY(startPoint.getY());
            }
            done = true;
        }

        if (done) {
            m_connector.getLine().refresh();
            batch();
        }

        m_startPointHandles = null;
        m_startLinePoints = null;
    }

    @Override
    public int addControlPoint(final double x,
                               final double y) {

        final int index = m_connector.getControlPointIndex(x, y);
        if (index < 0) {
            final int pointIndex = m_connector.getIndexForSelectedSegment((int) x,
                                                                          (int) y);
            if (pointIndex > -1 && addControlPoint(x, y, pointIndex)) {
                refreshControlPoints();
            }
            return pointIndex;
        }
        return index;
    }

    public boolean addControlPoint(final double x,
                                   final double y,
                                   final int index) {
        return getControlPointsAcceptor().add(m_connector, index, new Point2D(x, y));
    }

    @Override
    public void destroyControlPoint(final int index) {
        // Connection (line) need at least 2 points to be drawn
        if (m_connector.getPointHandles().size() <= 2) {
            return;
        }
        getControlPointsAcceptor().delete(m_connector, index);
        refreshControlPoints();
    }

    public WiresConnectionControl getHeadConnectionControl() {
        return m_headConnectionControl;
    }

    public WiresConnectionControl getTailConnectionControl() {
        return m_tailConnectionControl;
    }

    @Override
    public void showControlPoints() {
        showPointHandles();
        showOffsetHandles();
    }

    @Override
    public void hideControlPoints() {
        destroyPointHandles();
        destroyOffsetHandles();
    }

    @Override
    public boolean areControlPointsVisible() {
        return null != m_connector.getPointHandles() && m_connector.getPointHandles().isVisible();
    }

    @Override
    public boolean accept() {
        return true;
    }

    public WiresConnectorControlPointBuilder getControlPointBuilder() {
        return m_cpBuilder;
    }

    @Override
    public boolean updateControlPoints(final Point2DArray candidatePoints) {
        return getControlPointsAcceptor().update(m_connector,
                                                 candidatePoints);
    }

    private static void operateWithOtherHandles(IControlHandleList list, IControlHandle candidate, Consumer<IControlHandle> handle) {
        for (int i = 0; i < list.size(); i++) {
            final IControlHandle h = list.getHandle(i);
            if (h != candidate) {
                handle.accept(h);
            }
        }
    }

    public void showOffsetHandles() {
        final IControlHandleList offsetHandles = m_connector.getOffsetHandles();

        if (!offsetHandles.isVisible()) {

            offsetHandles.show();

            HandlerRegistrationManager manager = offsetHandles.getHandlerRegistrationManager();
            for (int i = 0; i < offsetHandles.size(); i++) {
                final SegmentHandle handle = (SegmentHandle) offsetHandles.getHandle(i);
                final Shape<?> shape = handle.getControl().asShape();
                manager.register(shape.addNodeDragStartHandler(new NodeDragStartHandler() {
                    @Override
                    public void onNodeDragStart(NodeDragStartEvent event) {
                        destroyPointHandles();
                        operateWithOtherHandles(offsetHandles, handle, h -> h.getControl().setVisible(false));
                    }
                }));
                manager.register(shape.addNodeDragEndHandler(new NodeDragEndHandler() {
                    @Override
                    public void onNodeDragEnd(NodeDragEndEvent event) {
                        destroyOffsetHandles();
                        updateControlPoints(m_connector.getControlPoints());
                        showControlPoints();
                    }
                }));
                manager.register(shape.addNodeMouseClickHandler(new NodeMouseClickHandler() {
                    @Override
                    public void onNodeMouseClick(NodeMouseClickEvent event) {
                        updateControlPoints(m_connector.getControlPoints());
                    }
                }));
                shape.moveToTop();
            }
        }
    }

    public void showPointHandles() {
        IControlHandleList pointHandles = m_connector.getPointHandles();
        if (!pointHandles.isVisible()) {

            pointHandles.show();

            HandlerRegistrationManager manager = pointHandles.getHandlerRegistrationManager();
            final WiresControlPointHandler controlPointsHandler =
                    m_wiresManager.getWiresHandlerFactory().newControlPointHandler(m_connector, m_wiresManager);
            final ControlPointDecoratorHandler controlHandleDecoratorHandler = new ControlPointDecoratorHandler();
            for (int i = 1; i < pointHandles.size() - 1; i++) {
                final IControlHandle handle = pointHandles.getHandle(i);
                final Shape<?> shape = handle.getControl().asShape();
                manager.register(shape.addNodeMouseClickHandler(controlPointsHandler));
                manager.register(shape.addNodeMouseDoubleClickHandler(controlPointsHandler));
                manager.register(shape.addNodeDragStartHandler(controlPointsHandler));
                manager.register(shape.addNodeDragEndHandler(controlPointsHandler));
                manager.register(shape.addNodeDragMoveHandler(controlPointsHandler));
                manager.register(shape.addNodeDragStartHandler(controlHandleDecoratorHandler));
                manager.register(shape.addNodeDragEndHandler(controlHandleDecoratorHandler));
                manager.register(shape.addNodeDragStartHandler(new NodeDragStartHandler() {
                    @Override
                    public void onNodeDragStart(NodeDragStartEvent event) {
                        destroyOffsetHandles();
                    }
                }));
                manager.register(shape.addNodeDragEndHandler(new NodeDragEndHandler() {
                    @Override
                    public void onNodeDragEnd(NodeDragEndEvent event) {
                        hideControlPoints();
                        updateControlPoints(m_connector.getControlPoints());
                        showControlPoints();
                    }
                }));
                m_pointHandleDecorator.decorate(shape, ShapeState.VALID);
                shape.setDragBounds(m_connector.getGroup().getDragBounds());
                shape.moveToTop();
            }

            initHeadConnection();
            final WiresConnection headConnection = m_connector.getHeadConnection();
            updateConnectionHandleShape(headConnection);
            final Shape<?> head = headConnection.getControl().asShape();
            ConnectionHandler headConnectionHandler = new ConnectionHandler(m_headConnectionControl,
                                                                            head,
                                                                            headConnection,
                                                                            () -> m_wiresManager.getConnectionAcceptor().acceptHead(headConnection, headConnection.getMagnet()));
            head.setDragConstraints(headConnectionHandler);
            manager.register(head.addNodeDragStartHandler(headConnectionHandler));
            manager.register(head.addNodeDragEndHandler(headConnectionHandler));
            manager.register(head.addNodeMouseClickHandler(headConnectionHandler));

            initTailConnection();
            final WiresConnection tailConnection = m_connector.getTailConnection();
            updateConnectionHandleShape(tailConnection);
            final Shape<?> tail = tailConnection.getControl().asShape();
            ConnectionHandler tailConnectionHandler = new ConnectionHandler(m_tailConnectionControl,
                                                                            tail,
                                                                            tailConnection,
                                                                            () -> m_wiresManager.getConnectionAcceptor().acceptTail(tailConnection, tailConnection.getMagnet()));
            tail.setDragConstraints(tailConnectionHandler);
            manager.register(tail.addNodeDragStartHandler(tailConnectionHandler));
            manager.register(tail.addNodeDragEndHandler(tailConnectionHandler));
            manager.register(tail.addNodeMouseClickHandler(tailConnectionHandler));
        }
    }

    private static void updateConnectionHandleShape(final WiresConnection connection) {
        if (connection.getControl() instanceof ConnectionHandleShape) {
            final ConnectionHandleShape control = (ConnectionHandleShape) connection.getControl();
            boolean visible = null != connection.getMagnet();
            control.setPathVisible(visible);
            if (visible) {
                control.setConnectionLocked(!connection.isAutoConnection());
            }
        }
    }

    public void initHeadConnection() {
        m_headConnectionControl = m_wiresManager.getControlFactory()
                .newConnectionControl(m_connector,
                                      true,
                                      m_wiresManager);
    }

    public void initTailConnection() {
        m_tailConnectionControl = m_wiresManager.getControlFactory()
                .newConnectionControl(m_connector,
                                      false,
                                      m_wiresManager);
    }

    private void destroyOffsetHandles() {
        m_connector.destroyOffsetHandles();
    }

    private void destroyPointHandles() {
        m_connector.destroyPointHandles();
        m_headConnectionControl = null;
        m_tailConnectionControl = null;
    }

    protected final class ControlPointDecoratorHandler implements NodeDragStartHandler,
                                                                  NodeDragEndHandler {

        @Override
        public void onNodeDragStart(NodeDragStartEvent event) {
            decorateShape(event.getDragContext(), ShapeState.INVALID);
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event) {
            decorateShape(event.getDragContext(), ShapeState.VALID);
        }

        private void decorateShape(DragContext dragContext, ShapeState state) {
            final Shape node = (Shape) dragContext.getNode();
            m_pointHandleDecorator.decorate(node, state);
        }
    }

    protected final class ConnectionHandler implements DragConstraintEnforcer,
                                                       NodeDragStartHandler,
                                                       NodeDragEndHandler,
                                                       NodeMouseClickHandler {

        private final WiresConnectionControl connectionControl;

        private final Shape<?> shape;

        private final WiresConnection connection;

        private final BooleanSupplier acceptOnClick;

        private ConnectionHandler(final WiresConnectionControl connectionControl, Shape<?> shape, WiresConnection connection, BooleanSupplier acceptOnClick) {
            this.connectionControl = connectionControl;
            this.shape = shape;
            this.connection = connection;
            this.acceptOnClick = acceptOnClick;
            m_pointHandleDecorator.decorate(shape, ShapeState.VALID);
        }

        @Override
        public void startDrag(final DragContext dragContext) {
            connectionControl.onMoveStart(dragContext.getDragStartX(),
                                          dragContext.getDragStartY());
            m_pointHandleDecorator.decorate(this.shape, ShapeState.INVALID);
        }

        @Override
        public boolean adjust(final Point2D dxy) {
            boolean adjusted = false;
            if (connectionControl.onMove(dxy.getX(),
                                         dxy.getY())) {
                // Check if need for drag adjustments.
                final Point2D adjustPoint = connectionControl.getAdjust();
                if (null != adjustPoint) {
                    dxy.set(adjustPoint);
                    adjusted = true;
                }
            }
            m_pointHandleDecorator.decorate(shape, adjusted ? ShapeState.VALID : ShapeState.INVALID);
            return adjusted;
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event) {
            updateConnectionHandleShape(connection);
        }

        @Override
        public void onNodeDragEnd(final NodeDragEndEvent event) {
            connectionControl.onMoveComplete();
            execute();
            onMoveComplete();

            //decorate connection shape
            final ShapeState shapeState = (connection.isAutoConnection() || connection.getMagnet() != null ?
                    ShapeState.VALID : ShapeState.INVALID);
            m_pointHandleDecorator.decorate(shape, shapeState);
        }

        @Override
        public void onNodeMouseClick(NodeMouseClickEvent event) {
            WiresMagnet magnet = connection.getMagnet();
            if (null != magnet && magnet.getIndex() != 0) {
                connection.setAutoConnection(!connection.isAutoConnection());
                if (acceptOnClick.getAsBoolean()) {
                    updateConnectionHandleShape(connection);
                    connection.getControl().getLayer().batch();
                }
            }
        }
    }

    private void refreshControlPoints() {
        hideControlPoints();
        showPointHandles();
        showOffsetHandles();
    }

    private void batch() {
        if (null != m_wiresManager.getLayer().getLayer()) {
            m_wiresManager.getLayer().getLayer().batch();
        }
    }

    private IControlPointsAcceptor getControlPointsAcceptor() {
        return m_wiresManager.getControlPointsAcceptor();
    }

    private static boolean isConnected(final WiresConnection connection) {
        return null != connection && null != connection.getMagnet();
    }

    @Override
    public void destroy() {
        if (null != m_headConnectionControl) {
            m_headConnectionControl.destroy();
            m_headConnectionControl = null;
        }
        if (null != m_tailConnectionControl) {
            m_tailConnectionControl.destroy();
            m_tailConnectionControl = null;
        }
        clear();
        destroyPointHandles();
        destroyOffsetHandles();
        m_cpBuilder.destroy();
        m_pointHandleDecorator = null;
    }

    public void setPointHandleDecorator(PointHandleDecorator pointHandleDecorator) {
        this.m_pointHandleDecorator = pointHandleDecorator;
    }

    public PointHandleDecorator getPointHandleDecorator() {
        return m_pointHandleDecorator;
    }
}