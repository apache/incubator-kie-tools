/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.stream.StreamSupport;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDecorators;
import org.kie.workbench.common.stunner.core.client.shape.view.IsConnector;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class WiresConnectorView<T> extends WiresConnector
        implements
        ShapeView<T>,
        IsConnector<T>,
        HasControlPoints<T>,
        HasDecorators<Shape<?>> {

    protected String uuid;
    private WiresConnectorControl connectorControl;

    public WiresConnectorView(final AbstractDirectionalMultiPointShape<?> line,
                              final MultiPathDecorator headDecorator,
                              final MultiPathDecorator tailDecorator) {
        super(line,
              headDecorator,
              tailDecorator);
        init();
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
        init();
    }

    private void init() {
        getLine().setFillColor(ColorName.WHITE).setStrokeWidth(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setUUID(final String uuid) {
        this.uuid = uuid;
        WiresUtils.assertShapeUUID(this.getGroup(),
                                   uuid);
        return (T) this;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @SuppressWarnings("unchecked")
    public T setControl(final WiresConnectorControl connectorControl) {
        this.connectorControl = connectorControl;
        return (T) this;
    }

    public WiresConnectorControl getControl() {
        return connectorControl;
    }

    @SuppressWarnings("unchecked")
    public T connect(final ShapeView headShapeView,
                     final Magnet headMagnetDef,
                     final ShapeView tailShapeView,
                     final Magnet tailMagnetDef,
                     final boolean tailArrow,
                     final boolean headArrow) {
        final WiresShape headWiresShape = (WiresShape) headShapeView;
        final WiresShape tailWiresShape = (WiresShape) tailShapeView;
        final MagnetManager.Magnets headMagnets = headWiresShape.getMagnets();
        final MagnetManager.Magnets tailMagnets = tailWiresShape.getMagnets();
        // Obtain the magnet for the head shape which location is equals/closer to the headMagnetDef.
        com.ait.lienzo.client.core.types.Point2D headCoords = com.ait.lienzo.client.core.shape.wires.WiresUtils.getLocation(headWiresShape.getGroup());
        final WiresMagnet headMagnet = getWiresMagnet(headMagnets,
                                                      headMagnetDef,
                                                      headCoords.getX(),
                                                      headCoords.getY(),
                                                      LienzoShapeUtils.DEFAULT_SOURCE_MAGNET);
        // Obtain the magnet for the tail shape which location is equals/closer to the tailMagnetDef.
        com.ait.lienzo.client.core.types.Point2D tailCoords = com.ait.lienzo.client.core.shape.wires.WiresUtils.getLocation(tailWiresShape.getGroup());
        final WiresMagnet tailMagnet = getWiresMagnet(tailMagnets,
                                                      tailMagnetDef,
                                                      tailCoords.getX(),
                                                      tailCoords.getY(),
                                                      LienzoShapeUtils.DEFAULT_TARGET_MAGNET);
        // Update the magnets.
        this.setHeadMagnet(headMagnet);
        this.setTailMagnet(tailMagnet);
        return (T) this;
    }

    /**
     * Returns the Lienzo's magnet instance which location is closer to the magnet definition
     */
    private WiresMagnet getWiresMagnet(final MagnetManager.Magnets wiresMagnets,
                                       final Magnet magnetDef,
                                       final double shapeX,
                                       final double shapeY,
                                       final int defaultMagnetIndex) {
        if (magnetDef == null) {
            return wiresMagnets.getMagnet(0);
        } else if (magnetDef.getLocation() != null) {
            Magnet absMagnetDef = MagnetImpl.Builder.build(magnetDef.getLocation().getX() + shapeX,
                                                           magnetDef.getLocation().getY() + shapeY);
            return (WiresMagnet) StreamSupport.stream(wiresMagnets.getMagnets().spliterator(),
                                                      false)
                    .sorted((m1, m2) -> compare(m1,
                                                m2,
                                                absMagnetDef))
                    .findFirst()
                    .get();
        } else if (magnetDef.getMagnetType() != null) {
            if (magnetDef.getMagnetType() == Magnet.MagnetType.OUTGOING) {
                return wiresMagnets.getMagnet(LienzoShapeUtils.DEFAULT_SOURCE_MAGNET);
            } else {
                return wiresMagnets.getMagnet(LienzoShapeUtils.DEFAULT_TARGET_MAGNET);
            }
        } else {
            return wiresMagnets.getMagnet(defaultMagnetIndex);
        }
    }

    private int compare(final IControlHandle m1,
                        final IControlHandle m2,
                        final Magnet magnet) {
        final double mx = magnet.getLocation().getX();
        final double my = magnet.getLocation().getY();
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


    @Override
    public void removeFromParent() {
        // Remove the main line.
        super.removeFromLayer();
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
    @SuppressWarnings("unchecked")
    public T setShapeX(final double x) {
        getGroup().setX(x);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setShapeY(final double y) {
        getGroup().setY(y);
        return (T) this;
    }

    @Override
    public double getAlpha() {
        return getGroup().getAlpha();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setAlpha(final double alpha) {
        getGroup().setAlpha(alpha);
        return (T) this;
    }

    @Override
    public Point2D getShapeAbsoluteLocation() {
        return WiresUtils.getAbsolute(getGroup());
    }

    @Override
    public String getFillColor() {
        return getLine().getFillColor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setFillColor(final String color) {
        getLine().setFillColor(color);
        if (null != getHead()) {
            getHead().setFillColor(color);
        }
        if (null != getTail()) {
            getTail().setFillColor(color);
        }
        return (T) this;
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
        return (T) this;
    }

    @Override
    public String getStrokeColor() {
        return getLine().getStrokeColor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setStrokeColor(final String color) {
        getLine().setStrokeColor(color);
        if (null != getHead()) {
            getHead().setStrokeColor(color);
        }
        if (null != getTail()) {
            getTail().setStrokeColor(color);
        }
        return (T) this;
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
        return (T) this;
    }

    @Override
    public double getStrokeWidth() {
        return getLine().getStrokeWidth();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setStrokeWidth(final double width) {
        getLine().setStrokeWidth(width);
        if (null != getHead()) {
            getHead().setStrokeWidth(width);
        }
        if (null != getTail()) {
            getTail().setStrokeWidth(width);
        }
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveToTop() {
        getGroup().moveToTop();
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveToBottom() {
        getGroup().moveToBottom();
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveUp() {
        getGroup().moveUp();
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveDown() {
        getGroup().moveDown();
        return (T) this;
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
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T hideControlPoints() {
        if (null != getControl()) {
            getControl().hideControlPoints();
        }
        return (T) this;
    }

    @Override
    public boolean areControlsVisible() {
        return getPointHandles().isVisible();
    }

    @Override
    public void destroy() {
        // Remove me.
        super.destroy();
        this.connectorControl = null;
    }

    @Override
    public List<Shape<?>> getDecorators() {
        final List<Shape<?>> decorators = new ArrayList<>(3);
        decorators.add(getLine());
        if (null != getHead()) {
            decorators.add(getHead());
        }
        if (null != getTail()) {
            decorators.add(getTail());
        }
        return decorators;
    }
}
