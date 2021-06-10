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

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeLocationControl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;

public class WiresShapeLocationControlImpl implements WiresShapeLocationControl {

    private final WiresShape m_shape;
    private Point2D m_delta;
    private Point2D shapeInitialLocation;
    private Point2D shapeResetLocation;
    private double m_mouseStartX;
    private double m_mouseStartY;
    private double m_shapeStartCenterX;
    private double m_shapeStartCenterY;
    private boolean m_startDocked;

    public WiresShapeLocationControlImpl(WiresShape m_shape) {
        this.m_shape = m_shape;
    }

    @Override
    public void onMoveStart(double x,
                            double y) {
        m_delta = new Point2D(0,
                              0);
        shapeResetLocation = getShape().getLocation();
        shapeInitialLocation = getShape().getComputedLocation();
        m_mouseStartX = x;
        m_mouseStartY = y;

        final Point2D absShapeLoc = getShape().getPath().getComputedLocation();
        final BoundingBox box     = getShape().getPath().getBoundingBox();

        m_shapeStartCenterX = absShapeLoc.getX() + (box.getWidth() / 2);
        m_shapeStartCenterY = absShapeLoc.getY() + (box.getHeight() / 2);
        m_startDocked = false;

        final WiresContainer parent = getShape().getParent();
        if (parent != null && parent instanceof WiresShape) {
            if (getShape().getDockedTo() != null) {
                m_startDocked = true;
            }
        }
    }

    @Override
    public Point2D getCurrentLocation() {
        double x;
        double y;
        if (m_startDocked) {
            x = (int) m_shapeStartCenterX;
            y = (int) m_shapeStartCenterY;
        } else {
            x = (int) m_mouseStartX;
            y = (int) m_mouseStartY;
        }

        return new Point2D(x, y).offset(m_delta.getX(), m_delta.getY());
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        m_delta = new Point2D(dx, dy);
        return false;
    }

    @Override
    public void onMoveAdjusted(final Point2D dxy) {
        m_delta = dxy.copy();
    }

    @Override
    public Point2D getAdjust() {
        return new Point2D(0,
                           0);
    }

    @Override
    public void onMoveComplete() {
    }

    @Override
    public void execute() {
        setShapeLocation(getCurrentLocation());
    }

    @Override
    public void clear() {
        m_mouseStartX = 0;
        m_mouseStartY = 0;
        m_shapeStartCenterX = 0;
        m_shapeStartCenterY = 0;
        m_startDocked = false;
        m_delta = new Point2D(0, 0);
        shapeInitialLocation = null;
        shapeResetLocation = null;
    }

    @Override
    public void reset() {
        if (null != shapeResetLocation) {
            setShapeLocation(shapeResetLocation);
        }
    }

    @Override
    public void destroy() {
        clear();
    }

    public Point2D getShapeLocation() {
        return getShapeInitialLocation().copy().offset(m_delta.getX(), m_delta.getY());
    }

    @Override
    public void setShapeLocation(final Point2D location) {
        getShape().setLocation(location);
    }

    @Override
    public WiresShape getShape() {
        return m_shape;
    }

    public double getMouseStartX() {
        return m_mouseStartX;
    }

    public double getMouseStartY() {
        return m_mouseStartY;
    }

    public boolean isStartDocked() {
        return m_startDocked;
    }

    public double getShapeStartCenterX() {
        return m_shapeStartCenterX;
    }

    public double getShapeStartCenterY() {
        return m_shapeStartCenterY;
    }

    public Point2D getShapeInitialLocation() {
        return shapeInitialLocation;
    }

    protected Point2D getCurrentDelta() {
        return m_delta;
    }
}
