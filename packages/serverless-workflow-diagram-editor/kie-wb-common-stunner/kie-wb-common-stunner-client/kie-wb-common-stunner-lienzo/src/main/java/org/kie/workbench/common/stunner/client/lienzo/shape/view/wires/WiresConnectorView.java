/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Shadow;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.shape.common.DashArray;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.HasManageableControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.IsConnector;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.DiscreteConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class WiresConnectorView<T> extends WiresConnector
        implements
        LienzoShapeView<T>,
        IsConnector<T>,
        HasManageableControlPoints<T> {

    protected String uuid;

    public WiresConnectorView(final AbstractDirectionalMultiPointShape<?> line,
                              final MultiPathDecorator headDecorator,
                              final MultiPathDecorator tailDecorator) {
        super(line,
              headDecorator,
              tailDecorator);
    }

    public WiresConnectorView(final WiresMagnet headMagnet,
                              final WiresMagnet tailMagnet,
                              final AbstractDirectionalMultiPointShape<?> line,
                              final MultiPathDecorator headDecorator,
                              final MultiPathDecorator tailDecorator) {
        super(headMagnet,
              tailMagnet,
              line,
              headDecorator,
              tailDecorator);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setUUID(final String uuid) {
        this.uuid = uuid;
        WiresUtils.assertShapeUUID(this.getGroup(),
                                   uuid);
        return cast();
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    public T setListening(final boolean listening) {
        listen(listening);
        return cast();
    }

    @Override
    public ControlPoint[] getManageableControlPoints() {
        final Point2DArray controlPoints = getControlPoints();
        if (null != controlPoints && controlPoints.size() > 2) {
            // Notice first and last CP from lienzo's connector are discarded,
            // as they're considered the Connection's on Stunner side, so not
            // part of the model's ControlPoint array.
            final ControlPoint[] result = new ControlPoint[controlPoints.size() - 2];
            for (int i = 1; i < controlPoints.size() - 1; i++) {
                com.ait.lienzo.client.core.types.Point2D p = controlPoints.get(i);
                ControlPoint point = ControlPoint.build(p.getX(), p.getY());
                result[i - 1] = point;
            }
            return result;
        }
        return new ControlPoint[0];
    }

    @Override
    public T addControlPoint(final ControlPoint controlPoint,
                             final int index) {
        addControlPoint(controlPoint.getLocation().getX(),
                        controlPoint.getLocation().getY()
                , index + 1);
        refreshControlPoints();
        return cast();
    }

    @Override
    public T updateControlPoints(final ControlPoint[] controlPoints) {
        for (int i = 0; i < controlPoints.length; i++) {
            final Point2D location = controlPoints[i].getLocation();
            final com.ait.lienzo.client.core.types.Point2D lienzoPoint =
                    new com.ait.lienzo.client.core.types.Point2D(location.getX(),
                                                                 location.getY());
            moveControlPoint(i + 1, lienzoPoint);
        }
        refreshControlPoints();
        return cast();
    }

    @Override
    public T deleteControlPoint(final int index) {
        destroyControlPoints(new int[]{index + 1});
        return cast();
    }

    private void refreshControlPoints() {
        getLine().refresh();
    }

    @SuppressWarnings("unchecked")
    public T connect(final ShapeView headShapeView,
                     final Connection headConnection,
                     final ShapeView tailShapeView,
                     final Connection tailConnection) {
        final Optional<WiresShape> headWiresShape = Optional.ofNullable((WiresShape) headShapeView);
        final Optional<WiresShape> tailWiresShape = Optional.ofNullable((WiresShape) tailShapeView);
        return connect(headWiresShape.map(WiresShape::getMagnets).orElse(null),
                       headWiresShape.map(s -> s.getGroup().getComputedLocation()).orElse(null),
                       headConnection,
                       tailWiresShape.map(WiresShape::getMagnets).orElse(null),
                       tailWiresShape.map(s -> s.getGroup().getComputedLocation()).orElse(null),
                       tailConnection);
    }

    T connect(final MagnetManager.Magnets headMagnets,
              final com.ait.lienzo.client.core.types.Point2D headAbsoluteLoc,
              final Connection headConnection,
              final MagnetManager.Magnets tailMagnets,
              final com.ait.lienzo.client.core.types.Point2D tailAbsoluteLoc,
              final Connection tailConnection
    ) {
        // Update head connection.
        updateConnection(headConnection,
                         headMagnets,
                         headAbsoluteLoc,
                         isAuto -> getHeadConnection().setAutoConnection(isAuto),
                         this::applyHeadLocation,
                         this::applyHeadMagnet);
        // Update tail connection.
        updateConnection(tailConnection,
                         tailMagnets,
                         tailAbsoluteLoc,
                         isAuto -> getTailConnection().setAutoConnection(isAuto),
                         this::applyTailLocation,
                         this::applyTailMagnet);
        return cast();
    }

    @Override
    public double getShapeX() {
        return getGroup().getX();
    }

    @Override
    public double getShapeY() {
        return getGroup().getY();
    }

    @Override
    public T setShapeLocation(final Point2D location) {
        getGroup().setLocation(new com.ait.lienzo.client.core.types.Point2D(location.getX(),
                                                                            location.getY()));
        return cast();
    }

    @Override
    public double getAlpha() {
        return getGroup().getAlpha();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setAlpha(final double alpha) {
        getGroup().setAlpha(alpha);
        return cast();
    }

    @Override
    public Point2D getShapeAbsoluteLocation() {
        return WiresUtils.getAbsolute(getGroup());
    }

    @Override
    public String getFillColor() {
        return getLine().asShape().getFillColor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setFillColor(final String color) {
        getLine().asShape().setFillColor(color);
        if (null != getHead()) {
            getHead().setFillColor(color);
        }
        if (null != getTail()) {
            getTail().setFillColor(color);
        }
        return cast();
    }

    @Override
    public double getFillAlpha() {
        return getLine().getFillAlpha();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setFillAlpha(final double alpha) {
        getLine().setFillAlpha(alpha);
        if (null != getHead()) {
            getHead().setFillAlpha(alpha);
        }
        if (null != getTail()) {
            getTail().setFillAlpha(alpha);
        }
        return cast();
    }

    @Override
    public String getStrokeColor() {
        return getLine().asShape().getStrokeColor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setStrokeColor(final String color) {
        getLine().asShape().setStrokeColor(color);
        if (null != getHead()) {
            getHead().setStrokeColor(color);
        }
        if (null != getTail()) {
            getTail().setStrokeColor(color);
        }
        return cast();
    }

    @Override
    public double getStrokeAlpha() {
        return getLine().getStrokeAlpha();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setStrokeAlpha(final double alpha) {
        getLine().setStrokeAlpha(alpha);
        if (null != getHead()) {
            getHead().setStrokeAlpha(alpha);
        }
        if (null != getTail()) {
            getTail().setStrokeAlpha(alpha);
        }
        return cast();
    }

    @Override
    public double getStrokeWidth() {
        return getLine().asShape().getStrokeWidth();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setStrokeWidth(final double width) {
        getLine().asShape().setStrokeWidth(width);
        if (null != getHead()) {
            getHead().setStrokeWidth(width);
        }
        if (null != getTail()) {
            getTail().setStrokeWidth(width);
        }
        return cast();
    }

    @Override
    public T setDragEnabled(boolean draggable) {
        setDraggable();
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveToTop() {
        getGroup().moveToTop();
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveToBottom() {
        getGroup().moveToBottom();
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveUp() {
        getGroup().moveUp();
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveDown() {
        getGroup().moveDown();
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T showControlPoints(final ControlPointType type) {
        if (null != getControl()) {
            if (ControlPointType.POINTS.equals(type)) {
                getControl().showControlPoints();
            } else {
                throw new UnsupportedOperationException("Control point type [" + type + "] not supported yet");
            }
        }
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T hideControlPoints() {
        if (null != getControl()) {
            getControl().hideControlPoints();
        }
        return cast();
    }

    @Override
    public boolean areControlsVisible() {
        return getPointHandles().isVisible();
    }

    @Override
    public List<Shape<?>> getDecorators() {
        final List<Shape<?>> decorators = new ArrayList<>(3);
        decorators.add(getLine().asShape());
        if (null != getHead()) {
            decorators.add(getHead());
        }
        if (null != getTail()) {
            decorators.add(getTail());
        }
        return decorators;
    }

    @Override
    public BoundingBox getBoundingBox() {
        final com.ait.lienzo.client.core.types.BoundingBox bb = getGroup().getBoundingBox();
        return new BoundingBox(bb.getMinX(),
                               bb.getMinY(),
                               bb.getMaxX(),
                               bb.getMaxY());
    }

    @Override
    public void removeFromParent() {
        // Remove the main line.
        removeFromLayer();
    }

    private WiresConnector applyHeadLocation(final Point2D location) {
        getHeadConnection().move(location.getX(), location.getY());
        return this;
    }

    private WiresConnector applyTailLocation(final Point2D location) {
        getTailConnection().move(location.getX(), location.getY());
        return this;
    }

    private WiresConnector applyHeadMagnet(final WiresMagnet headMagnet) {
        ifNotSpecialConnection(getHeadConnection(),
                               headMagnet,
                               WiresConnectorView::clearConnectionOffset);
        return super.setHeadMagnet(headMagnet);
    }

    private WiresConnector applyTailMagnet(final WiresMagnet tailMagnet) {
        ifNotSpecialConnection(getTailConnection(),
                               tailMagnet,
                               WiresConnectorView::clearConnectionOffset);
        return super.setTailMagnet(tailMagnet);
    }

    private static void updateConnection(final Connection connection,
                                         final MagnetManager.Magnets magnets,
                                         final com.ait.lienzo.client.core.types.Point2D absLocation,
                                         final Consumer<Boolean> isAutoConnectionConsumer,
                                         final Consumer<Point2D> locationConsumer,
                                         final Consumer<WiresMagnet> magnetConsumer) {
        final WiresMagnet[] magnet = new WiresMagnet[]{null};
        final boolean[] auto = new boolean[]{false};
        Optional<Point2D> connectionLoc = Optional.empty();
        if (null != connection) {
            final DiscreteConnection dc = connection instanceof DiscreteConnection ?
                    (DiscreteConnection) connection : null;
            if (null != dc && null != magnets) {
                // Obtain the magnet index and auto flag, if the connection is a discrete type and it has been already set.
                dc.getMagnetIndex().ifPresent(index -> magnet[0] = magnets.getMagnet(index));
                auto[0] = dc.isAuto();
            }
            // If still no magnet found from the connection's cache, figure it out as by the connection's location.
            if (null == magnet[0] && null != absLocation) {
                magnet[0] = getMagnetForConnection(connection,
                                                   magnets,
                                                   absLocation);
                // Update the discrete connection magnet cached index, if possible.
                if (null != dc) {
                    dc.setIndex(magnet[0].getIndex());
                }
            }
            if (null != connection.getLocation()) {
                connectionLoc = Optional.of(Point2D.create(connection.getLocation().getX(),
                                                           connection.getLocation().getY()));
            }
        }
        // Call the magnet and auto connection consumers to assign the new values.
        isAutoConnectionConsumer.accept(auto[0]);
        magnetConsumer.accept(magnet[0]);
        // When no magnet is present, use the connection's location, if any.
        if (null == magnet[0]) {
            connectionLoc.ifPresent(locationConsumer::accept);
        }
    }

    private static WiresMagnet getMagnetForConnection(final Connection connection,
                                                      final MagnetManager.Magnets magnets,
                                                      final com.ait.lienzo.client.core.types.Point2D absLocation) {
        if (null != connection && null != connection.getLocation()) {
            Point2D magnetAbs = new Point2D(absLocation.getX() + connection.getLocation().getX(),
                                            absLocation.getY() + connection.getLocation().getY());
            return getMagnetNearTo(magnets,
                                   magnetAbs);
        }
        return magnets.getMagnet(MagnetConnection.MAGNET_CENTER);
    }

    /**
     * Returns the Lienzo's magnet instance which location is closer to the magnet definition
     */
    private static WiresMagnet getMagnetNearTo(final MagnetManager.Magnets magnets,
                                               final Point2D location) {
        return (WiresMagnet) StreamSupport
                .stream(magnets.getMagnets().spliterator(),
                        false)
                .sorted((m1, m2) -> compare(m1,
                                            m2,
                                            location))
                .findFirst()
                .get();
    }

    private static int compare(final IControlHandle m1,
                               final IControlHandle m2,
                               final Point2D location) {
        final double mx = location.getX();
        final double my = location.getY();
        final com.ait.lienzo.client.core.types.Point2D m1p = m1.getControl().getLocation();
        final com.ait.lienzo.client.core.types.Point2D m2p = m2.getControl().getLocation();
        final double d1 = ShapeUtils.dist(mx,
                                          my,
                                          m1p.getX(),
                                          m1p.getY());
        final double d2 = ShapeUtils.dist(mx,
                                          my,
                                          m2p.getX(),
                                          m2p.getY());
        return Double.compare(d1,
                              d2);
    }

    private static void ifNotSpecialConnection(final WiresConnection connection,
                                               final WiresMagnet magnet,
                                               final Consumer<WiresConnection> regularConnectionConsumer) {
        if (!WiresConnection.isSpecialConnection(connection.isAutoConnection(),
                                                 null != magnet ? magnet.getIndex() : null)) {
            regularConnectionConsumer.accept(connection);
        }
    }

    private static void clearConnectionOffset(final WiresConnection connection) {
        connection.setXOffset(0d);
        connection.setYOffset(0d);
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }

    @Override
    public T setShadow(final String color,
                       final int blur,
                       final double offx,
                       final double offy) {
        getDirectionalLine().setShadow(new Shadow(color,
                                                  blur,
                                                  offx,
                                                  offy));
        return cast();
    }

    @Override
    public T removeShadow() {
        getDirectionalLine().setShadow(null);
        return cast();
    }

    public T setDashArray(DashArray dashArray) {
        if (dashArray != null) {
            getDirectionalLine().setDashArray(getDashDoubleArray(dashArray));
        }
        return cast();
    }

    static double[] getDashDoubleArray(double d, double[] array) {
        final double[] result = new double[array.length + 1];
        result[0] = d;
        for (int i = 0; i < array.length; i++) {
            result[i + 1] = array[i];
        }
        return result;
    }

    static double[] getDashDoubleArray(DashArray dashArray) {
        return getDashDoubleArray(dashArray.getDash(), dashArray.getDashes());
    }

    private AbstractDirectionalMultiPointShape<?> getDirectionalLine() {
        return (AbstractDirectionalMultiPointShape<?>) getLine();
    }

    @Override
    public Object getUserData() {
        return getDirectionalLine().getUserData();
    }

    @Override
    public void setUserData(Object userData) {
        getDirectionalLine().setUserData(userData);
    }
}
