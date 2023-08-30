/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.wires.OptionalBounds;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresBoundsConstraintControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLineSpliceControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMoveControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.Geometry;

/**
 * The default WiresShapeControl implementation.
 * It orchestrates different controls for handling interactions with a single wires shape.
 */
public class WiresShapeControlImpl
        implements WiresShapeControl,
                   WiresBoundsConstraintControl.SupportsOptionalBounds<WiresShapeControlImpl> {

    private final WiresParentPickerControl parentPickerControl;
    private Supplier<WiresLayerIndex> index;
    private WiresMagnetsControl m_magnetsControl;
    private WiresDockingControl m_dockingAndControl;
    private WiresContainmentControl m_containmentControl;
    private AlignAndDistributeControl m_alignAndDistributeControl;
    private WiresLineSpliceControl m_lineSpliceControl;
    private BoundingBox absoluteShapeBounds;
    private WiresShapeLocationBounds locationBounds;
    private Point2D m_adjust;
    private boolean c_accept;
    private boolean d_accept;
    private boolean s_accept;
    private boolean accepted;
    private WiresConnector[] m_connectorsWithSpecialConnections;
    private Collection<WiresConnector> m_connectors;

    public WiresShapeControlImpl(WiresShape shape) {
        parentPickerControl = new WiresParentPickerControlImpl(shape,
                                                               () -> index.get());
        m_dockingAndControl = new WiresDockingControlImpl(() -> parentPickerControl);
        m_containmentControl = new WiresContainmentControlImpl(() -> parentPickerControl);
        m_lineSpliceControl = new WiresLineSpliceControlImpl(() -> parentPickerControl);
        m_magnetsControl = new WiresMagnetsControlImpl(shape);
    }

    public WiresShapeControlImpl(WiresParentPickerControl parentPickerControl,
                                 WiresMagnetsControl m_magnetsControl,
                                 WiresDockingControl m_dockingAndControl,
                                 WiresContainmentControl m_containmentControl,
                                 WiresLineSpliceControl m_lineSpliceControl) {
        this.parentPickerControl = parentPickerControl;
        this.m_magnetsControl = m_magnetsControl;
        this.m_dockingAndControl = m_dockingAndControl;
        this.m_containmentControl = m_containmentControl;
        this.m_lineSpliceControl = m_lineSpliceControl;
    }

    @Override
    public void onMoveStart(final double x,
                            final double y) {
        absoluteShapeBounds = getShape().getGroup().getComputedBoundingPoints().getBoundingBox();
        m_adjust = new Point2D(0, 0);
        d_accept = false;
        c_accept = false;
        s_accept = false;
        accepted = false;

        // Delegate move start to the shape's parent control
        parentPickerControl.onMoveStart(x,
                                        y);

        // Delegate move start to the shape's docking control
        if (m_dockingAndControl != null) {
            m_dockingAndControl.onMoveStart(x,
                                            y);
        }

        // Delegate move start to the shape's containment control
        if (m_containmentControl != null) {
            m_containmentControl.onMoveStart(x,
                                             y);
        }

        // Delegate move start to the shape's line splice control
        if (m_lineSpliceControl != null) {
            m_lineSpliceControl.onMoveStart(x,
                                            y);
        }

        // Delegate move start to the align and distribute control.
        if (m_alignAndDistributeControl != null) {
            // move selected shapes and its connectors to top before dragging.
            moveShapeUpToParent();
            m_alignAndDistributeControl.dragStart();
        }

        // index nested shapes that have special m_connectors, to avoid searching during drag.
        m_connectorsWithSpecialConnections = WiresShapeControlUtils.collectionSpecialConnectors(getShape());
        //setting the child connectors that should be moved with the Shape
        m_connectors = getShape().getChildShapes() != null && !getShape().getChildShapes().isEmpty() ?
                WiresShapeControlUtils.lookupChildrenConnectorsToUpdate(getShape()).values() :
                Collections.emptyList();
        forEachConnectorControl(control -> control.onMoveStart(x, y));
    }

    @Override
    public WiresShapeControlImpl setLocationBounds(final OptionalBounds bounds) {
        if (null == locationBounds) {
            locationBounds = new WiresShapeLocationBounds(() -> absoluteShapeBounds);
        }
        locationBounds.setBounds(bounds);
        return this;
    }

    public OptionalBounds getLocationBounds() {
        return null != locationBounds ? locationBounds.getBounds() : null;
    }

    @Override
    public boolean isOutOfBounds(final double dx,
                                 final double dy) {
        return null != locationBounds &&
                locationBounds.isOutOfBounds(dx, dy);
    }

    @Override
    public boolean onMove(final double dx,
                          final double dy) {

        if (isOutOfBounds(dx, dy)) {
            return true;
        }

        // First step is to delegate the location deltas to the shared parent picker control.
        if (parentPickerControl.onMove(dx, dy)) {
            m_adjust = parentPickerControl.getAdjust();
            return true;
        }

        final Point2D dxy = new Point2D(dx, dy);

        final boolean isDockAdjust = null != m_dockingAndControl && m_dockingAndControl.onMove(dx, dy);
        if (isDockAdjust) {
            final Point2D dadjust = m_dockingAndControl.getAdjust();
            double adjustDistance = Geometry.distance(dx, dy, dadjust.getX(), dadjust.getY());
            if (adjustDistance < getWiresManager().getDockingAcceptor().getHotspotSize()) {
                dxy.setX(dadjust.getX());
                dxy.setY(dadjust.getY());
            }
        }

        final boolean isContAdjust = null != m_containmentControl &&
                m_containmentControl.onMove(dx,
                                            dy);
        if (isContAdjust) {
            final Point2D cadjust = m_containmentControl.getAdjust();
            dxy.setX(cadjust.getX());
            dxy.setY(cadjust.getY());
        }

        if (null != m_lineSpliceControl) {
            m_lineSpliceControl.onMove(dx,
                                       dy);
        }

        final boolean isAlignDistroAdjust = null != m_alignAndDistributeControl &&
                m_alignAndDistributeControl.dragAdjust(dxy);

        // Special adjustments.
        boolean adjust = true;
        if ((isDockAdjust || isContAdjust)
                && isAlignDistroAdjust
                && (dxy.getX() != dx || dxy.getY() != dy)) {
            BoundingBox box = getShape().getPath().getBoundingBox();

            PickerPart part = parentPickerControl
                    .getIndex()
                    .findShapeAt((int) (absoluteShapeBounds.getMinX() + dxy.getX() + (box.getWidth() / 2)),
                                 (int) (absoluteShapeBounds.getMinY() + dxy.getY() + (box.getHeight() / 2)));

            if (part == null || part.getShapePart() != PickerPart.ShapePart.BORDER) {
                dxy.setX(dx);
                dxy.setY(dy);
                adjust = false;
            }
        }

        // Cache the current adjust point.
        m_adjust = dxy;
        parentPickerControl.onMoveAdjusted(m_adjust);

        shapeUpdated(false);

        forEachConnectorControl(control -> control.onMove(dx,
                                                          dy));

        return adjust;
    }

    @Override
    public boolean accept() {
        Point2D location = null;
        d_accept = null != getDockingControl() && getDockingControl().accept();
        c_accept = !d_accept && null != getContainmentControl() && getContainmentControl().accept();
        s_accept = null != getLineSpliceControl() && getLineSpliceControl().accept();

        if (c_accept) {
            location = getContainmentControl().getCandidateLocation();
        } else if (d_accept) {
            location = getDockingControl().getCandidateLocation();
        }

        boolean accept = false;
        if (null != location) {
            accept = getShape().getWiresManager()
                    .getLocationAcceptor()
                    .accept(new WiresShape[]{getShape()},
                            new Point2D[]{location});
        }
        accepted = accept;
        return accept;
    }

    @Override
    public void onMoveComplete() {
        parentPickerControl.onMoveComplete();
        if (null != m_dockingAndControl) {
            m_dockingAndControl.onMoveComplete();
        }
        if (null != m_containmentControl) {
            m_containmentControl.onMoveComplete();
        }
        if (null != m_lineSpliceControl) {
            m_lineSpliceControl.onMoveComplete();
        }
        if (m_alignAndDistributeControl != null) {
            m_alignAndDistributeControl.dragEnd();
        }
        forEachConnectorControl(WiresMoveControl::onMoveComplete);
    }

    @Override
    public boolean isAccepted() {
        return accepted;
    }

    @Override
    public void execute() {
        final boolean accept = c_accept || d_accept || s_accept;
        if (!accept) {
            throw new IllegalStateException("Execute should not be called. No containment neither docking operations have been accepted.");
        }

        Point2D location = null;
        if (!s_accept) {
            location = c_accept ?
                    getContainmentControl().getCandidateLocation() :
                    getDockingControl().getCandidateLocation();
        }

        if (s_accept) {
            getLineSpliceControl().execute();
        } else if (d_accept) {
            getDockingControl().execute();
        } else {
            getContainmentControl().execute();
        }

        if (null != location) {
            getParentPickerControl().setShapeLocation(location);
            shapeUpdated(true);
        }

        forEachConnectorControl(WiresControl::execute);

        clear();
    }

    @Override
    public void clear() {
        parentPickerControl.clear();
        parentPickerControl.getIndex().clear();
        if (null != m_dockingAndControl) {
            m_dockingAndControl.clear();
        }
        if (null != m_containmentControl) {
            m_containmentControl.clear();
        }
        if (null != m_lineSpliceControl) {
            m_lineSpliceControl.clear();
        }
        forEachConnectorControl(WiresControl::clear);
        clearState();
    }

    @Override
    public void reset() {
        if (null != getDockingControl()) {
            getDockingControl().reset();
        }
        if (null != getContainmentControl()) {
            getContainmentControl().reset();
        }
        if (null != getLineSpliceControl()) {
            getLineSpliceControl().reset();
        }
        parentPickerControl.reset();
        if (null != m_alignAndDistributeControl) {
            m_alignAndDistributeControl.reset();
        }
        getShape().shapeMoved();
        forEachConnectorControl(WiresControl::reset);
        clearState();
    }

    @Override
    public void destroy() {
        clearState();
        if (null != getDockingControl()) {
            getDockingControl().destroy();
        }
        if (null != getContainmentControl()) {
            getContainmentControl().destroy();
        }
        if (null != getLineSpliceControl()) {
            getLineSpliceControl().destroy();
        }
        parentPickerControl.destroy();
        if (null != m_alignAndDistributeControl) {
            m_alignAndDistributeControl.dragEnd();
        }
        if (null != locationBounds) {
            locationBounds.clear();
            locationBounds = null;
        }
    }

    @Override
    public void onMouseClick(MouseEvent event) {
        parentPickerControl.onMouseClick(event);
    }

    @Override
    public void onMouseDown(MouseEvent event) {
        parentPickerControl.onMouseDown(event);
    }

    @Override
    public void onMouseUp(MouseEvent event) {
        parentPickerControl.onMouseUp(event);
    }

    @Override
    public WiresShapeControl useIndex(Supplier<WiresLayerIndex> index) {
        this.index = index;
        return this;
    }

    @Override
    public WiresShapeControl setAlignAndDistributeControl(AlignAndDistributeControl control) {
        this.m_alignAndDistributeControl = control;
        return this;
    }

    @Override
    public WiresMagnetsControl getMagnetsControl() {
        return m_magnetsControl;
    }

    @Override
    public AlignAndDistributeControl getAlignAndDistributeControl() {
        return m_alignAndDistributeControl;
    }

    @Override
    public WiresDockingControl getDockingControl() {
        return m_dockingAndControl;
    }

    @Override
    public WiresContainmentControl getContainmentControl() {
        return m_containmentControl;
    }

    @Override
    public WiresLineSpliceControl getLineSpliceControl() {
        return m_lineSpliceControl;
    }

    @Override
    public WiresParentPickerControl getParentPickerControl() {
        return parentPickerControl;
    }

    @Override
    public Point2D getAdjust() {
        return m_adjust;
    }

    public Supplier<WiresLayerIndex> getIndex() {
        return index;
    }

    private void shapeUpdated(final boolean isAcceptOp) {
        WiresShapeControlUtils.updateSpecialConnections(m_connectorsWithSpecialConnections,
                                                        isAcceptOp);
        WiresShapeControlUtils.updateNestedShapes(getShape());
    }

    private void clearState() {
        absoluteShapeBounds = null;
        m_adjust = new Point2D(0, 0);
        m_connectorsWithSpecialConnections = null;
    }

    private void forEachConnectorControl(final Consumer<WiresConnectorControl> consumer) {
        if (m_connectors != null && !m_connectors.isEmpty()) {
            for (WiresConnector connector : m_connectors) {
                consumer.accept(connector.getControl());
            }
        }
    }

    private WiresShape getShape() {
        return parentPickerControl.getShape();
    }

    private WiresManager getWiresManager() {
        return getShape().getWiresManager();
    }

    private void moveShapeUpToParent() {
        WiresContainer parent = getParentPickerControl().getParent();
        WiresShapeControlUtils.moveShapeUpToParent(getShape(), parent);
    }
}
