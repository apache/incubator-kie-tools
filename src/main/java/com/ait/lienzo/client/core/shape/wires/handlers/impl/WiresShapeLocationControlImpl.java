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
        final Point2D absShapeLoc = m_shape.getPath().getComputedLocation();
        final BoundingBox box = m_shape.getPath().getBoundingBox();
        m_shapeStartCenterX = absShapeLoc.getX() + (box.getWidth() / 2);
        m_shapeStartCenterY = absShapeLoc.getY() + (box.getHeight() / 2);
        m_startDocked = false;

        final WiresContainer parent = m_shape.getParent();
        if (parent != null && parent instanceof WiresShape) {
            if (m_shape.getDockedTo() != null) {
                m_startDocked = true;
            }
        }
    }

    @Override
    public Point2D getCurrentLocation() {
        double x = 0;
        double y = 0;
        if (m_startDocked) {
            x = (int) m_shapeStartCenterX;
            y = (int) m_shapeStartCenterY;
        } else {
            x = (int) m_mouseStartX;
            y = (int) m_mouseStartY;
        }

        return new Point2D(x, y)
                .offset(m_delta);
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
    public boolean onMoveComplete() {
        return true;
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
        return getShapeInitialLocation().copy().offset(getCurrentDelta());
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

    protected boolean isStartDocked() {
        return m_startDocked;
    }

    public double getShapeStartCenterX() {
        return m_shapeStartCenterX;
    }

    public double getShapeStartCenterY() {
        return m_shapeStartCenterY;
    }

    protected Point2D getShapeInitialLocation() {
        return shapeInitialLocation;
    }

    protected Point2D getCurrentDelta() {
        return m_delta;
    }
}
