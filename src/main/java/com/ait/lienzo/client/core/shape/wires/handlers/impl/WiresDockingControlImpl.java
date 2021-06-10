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

import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;

import java.util.function.Supplier;

public class WiresDockingControlImpl extends AbstractWiresControl<WiresDockingControlImpl>
        implements WiresDockingControl {

    private final HandlerRegistrationManager      m_handlerRegistrations;
    private Point2D                               m_absInitialPathLocation;
    private Point2D                               m_intersection;
    private Point2D                               m_absDockPosition;
    private double  xRatio;
    private double  yRatio;

    public WiresDockingControlImpl(final Supplier<WiresParentPickerControl> parentPickerControl) {
        this(parentPickerControl, new HandlerRegistrationManager());
    }

    WiresDockingControlImpl(final Supplier<WiresParentPickerControl> parentPickerControl,
                                   final HandlerRegistrationManager registrationManager) {
        super(parentPickerControl);
        m_handlerRegistrations = registrationManager;
    }

    @Override
    protected void doMoveStart(final double x,
                               final double y) {
        m_absInitialPathLocation = getShape().getPath().getComputedLocation();
        final WiresShape shape = getShape();
        if (null != shape.getDockedTo()) {
            shape.setDockedTo(null);
        }
    }

    @Override
    protected boolean doMove(final double dx,
                             final double dy) {
        m_intersection = null;
        if (isAllow()) {
            m_intersection = findIntersection(dx,
                                              dy,
                                              m_absInitialPathLocation,
                                              getShape(),
                                              (WiresShape) getParent());
        }
        return isIntersecting();
    }

    private Point2D findIntersection(double dx, double dy, final Point2D initialPathLocation, final WiresShape shape,  final WiresShape parent) {
        if (null == parent) {
            return null;
        }
        final Point2D parentLocation = parent.getComputedLocation();
        final BoundingBox box = shape.getPath().getBoundingBox();
        final double shapeX = initialPathLocation.getX() + dx + (box.getWidth() / 2) - parentLocation.getX();
        final double shapeY = initialPathLocation.getY() + dy + (box.getHeight() / 2)- parentLocation.getY();

        return Geometry.findIntersection((int) shapeX, (int) shapeY, parent.getPath());
    }

    @Override
    public void clear() {
        m_absInitialPathLocation = null;
        m_intersection = null;
    }

    @Override
    public Point2D getAdjust() {
        if (isEnabled() && isIntersecting()) {
            final Point2D candidateLocation = getCandidateLocation();
            final Point2D absLoc = getParent().getComputedLocation();
            m_absDockPosition = new Point2D(absLoc.getX() + candidateLocation.getX(),
                                            absLoc.getY() + candidateLocation.getY());
            return new Point2D(m_absDockPosition.getX() - m_absInitialPathLocation.getX(),
                               m_absDockPosition.getY() - m_absInitialPathLocation.getY());
        }
        return new Point2D(0, 0);
    }

    @Override
    public boolean isAllow() {
        final WiresLayer m_layer = getWiresLayer();
        final PickerPart.ShapePart parentShapePart = getParentPickerControl().getParentShapePart();
        final WiresManager wiresManager = m_layer.getWiresManager();
        final IDockingAcceptor dockingAcceptor = wiresManager.getDockingAcceptor();
        return !isEnabled() ||
                null != getParent() &&
                        null != parentShapePart &&
                        getParent() instanceof WiresShape &&
                        parentShapePart == PickerPart.ShapePart.BORDER &&
                        (dockingAcceptor.dockingAllowed(getParent(),
                                                        getShape()));
    }

    @Override
    public boolean accept() {
        return !isEnabled() || (isAllow() && _isAccept());
    }

    private Point2D getCandidateAbsoluteLocation() {
        return m_absDockPosition;
    }

    @Override
    public Point2D getCandidateLocation() {
        final WiresShape shape = getShape();
        if (m_absInitialPathLocation == null || !isIntersecting()) {
            return computeCandidateLocation(shape);
        }
        return getCandidateLocation(shape);
    }

    private Point2D computeCandidateLocation(WiresShape shape) {
        m_absInitialPathLocation = shape.getPath().getComputedLocation();
        m_intersection = findIntersection(0, 0, m_absInitialPathLocation, shape, (WiresShape) shape.getParent());
        final Point2D candidateLocation = getCandidateLocation(shape);
        clear();
        return candidateLocation;
    }

    private Point2D getCandidateLocation(WiresShape shape) {
        if (!isIntersecting()) {
            return null;
        }
        BoundingBox box = shape.getPath().getBoundingBox();
        double x = m_intersection.getX() - (box.getWidth() / 2);
        double y = m_intersection.getY() -  (box.getHeight() / 2);
        return new Point2D(x, y);
    }

    @Override
    public void execute() {
        if (isEnabled()) {
            dock(getParent());
            move(getCandidateAbsoluteLocation());
        }
    }

    @Override
    public void reset() {
        if (isEnabled() &&
                isStartDocked() &&
                getParentPickerControl().getInitialParent() != getShape().getParent()) {
            dock(getParentPickerControl().getInitialParent());
            moveChild(getShape(),
                      getParentPickerControl().getInitialParent(),
                      getParentPickerControl().getShapeInitialLocation());
        }
    }

    @Override
    public void destroy() {
        clear();
        m_handlerRegistrations.destroy();
        m_absDockPosition = null;
    }

    private boolean _isAccept() {
        final WiresLayer m_layer = getWiresLayer();
        final PickerPart.ShapePart parentShapePart = getParentPickerControl().getParentShapePart();
        final WiresManager wiresManager = m_layer.getWiresManager();
        final IDockingAcceptor dockingAcceptor = wiresManager.getDockingAcceptor();
        return null != getParent()
                && parentShapePart == PickerPart.ShapePart.BORDER
                && dockingAcceptor.acceptDocking(getParent(),
                                                 getShape());
    }

    void move(final Point2D absLocation) {
        moveChild(getShape(),
                  getParent(),
                  absLocation);
    }

    private void moveChild(final WiresShape shape,
                           final WiresContainer parent,
                           final Point2D absLocation) {
        final Point2D relLocation = absLocation.sub(parent.getComputedLocation());
        shape.setLocation(relLocation);
    }

    @Override
    public void dock(final WiresContainer parent) {
        final WiresShape shape = getShape();
        if (null != shape.getDockedTo()) {
            undock();
        }

        shape.removeFromParent();
        parent.add(shape);
        shape.setDockedTo(parent);

        final WiresShape parentWireShape = (WiresShape) parent;

        //recalculate location during shape resizing
        final BoundingBox shapeBox = shape.getPath().getBoundingBox();

        xRatio = -1;
        yRatio = -1;
        registerHandler(parentWireShape.addWiresResizeStepHandler(event -> {
            if ( xRatio == -1 && yRatio == -1 )
            {
                // this is a hack, to ensure it runs on the first before any resize computations
                BoundingBox parentBox = event.getSource().getPath().getBoundingBox();

                // makeXY sure everything is shifted to have x/y greater than 0
                double normaliseX = parentBox.getX() >= 0 ? 0 : 0 - parentBox.getX();
                double normaliseY = parentBox.getY() >= 0 ? 0 : 0 - parentBox.getY();

                Point2D location = shape.getLocation();
                xRatio = Geometry.getRatio(location.getX() + normaliseX + (shapeBox.getWidth() / 2), parentBox.getX() + normaliseX, parentBox.getWidth());
                yRatio = Geometry.getRatio(location.getY() + normaliseY + (shapeBox.getHeight() / 2), parentBox.getY() + normaliseY, parentBox.getHeight());
            }
            shape.setLocation(new Point2D(event.getX() + (event.getWidth() * xRatio) - (shapeBox.getWidth() / 2),
                                          event.getY() + (event.getHeight() * yRatio) - (shapeBox.getHeight() / 2)));

            shape.shapeMoved();
        }));
        registerHandler(parentWireShape.addWiresResizeEndHandler(event -> {
            shape.setLocation(new Point2D(event.getX() + (event.getWidth() * xRatio) - (shapeBox.getWidth() / 2),
                                          event.getY() + (event.getHeight() * yRatio) - (shapeBox.getHeight() / 2)));
            shape.shapeMoved();
            shape.getControl().getAlignAndDistributeControl().updateIndex();
        }));
    }

    @Override
    public void undock() {
        final WiresShape shape = getShape();
        final WiresContainer parent = (shape.getDockedTo() != null ? shape.getDockedTo() : shape.getParent());
        if (null != parent) {
            parent.remove(shape);
            shape.setDockedTo(null);
        }
        removeHandlers();
    }

    private boolean isIntersecting() {
        return null != m_intersection;
    }

    private void registerHandler(HandlerRegistration handler) {
        m_handlerRegistrations.register(handler);
    }

    private void removeHandlers() {
        m_handlerRegistrations.removeHandler();
        m_handlerRegistrations.clear();
    }
}