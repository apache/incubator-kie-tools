package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.Point2D;

public abstract class AbstractWiresParentPickerControl implements WiresParentPickerControl {

    private final WiresParentPickerControlImpl parentPickerControl;
    private boolean enabled;

    protected AbstractWiresParentPickerControl(WiresShape shape,
                                               ColorMapBackedPicker.PickerOptions pickerOptions) {
        this(new WiresParentPickerControlImpl(shape,
                                              pickerOptions));
    }

    protected AbstractWiresParentPickerControl(WiresParentPickerControlImpl parentPickerControl) {
        this.parentPickerControl = parentPickerControl;
        enable();
    }

    protected void enable() {
        this.enabled = true;
    }

    protected void disable() {
        this.enabled = false;
    }

    @Override
    public void onMoveStart(double x,
                            double y) {
        if (!enabled) {
            return;
        }

        beforeMoveStart(x,
                        y);

        parentPickerControl.onMoveStart(x,
                                        y);
        afterMoveStart(x,
                       y);
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        if (!enabled) {
            return false;
        }
        parentPickerControl.onMove(dx,
                                   dy);
        return afterMove(dx,
                         dy);
    }

    @Override
    public void onMoveAdjusted(Point2D dxy) {
        parentPickerControl.onMoveAdjusted(dxy);
    }

    @Override
    public boolean onMoveComplete() {
        if (!enabled) {
            return true;
        }
        parentPickerControl.onMoveComplete();
        return afterMoveComplete();
    }

    @Override
    public Point2D getShapeLocation() {
        return parentPickerControl.getShapeLocation();
    }

    @Override
    public void setShapeLocation(Point2D location) {
        parentPickerControl.getShapeLocationControl().setShapeLocation(location);
    }

    protected void beforeMoveStart(double x,
                                   double y) {
    }

    protected void afterMoveStart(double x,
                                  double y) {
    }

    protected boolean afterMove(double dx,
                                double dy) {
        return false;
    }

    protected boolean afterMoveComplete() {
        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Point2D getCurrentLocation() {
        return parentPickerControl.getCurrentLocation();
    }

    @Override
    public WiresContainer getParent() {
        return parentPickerControl.getParent();
    }

    @Override
    public WiresShape getShape() {
        return parentPickerControl.getShape();
    }

    @Override
    public PickerPart.ShapePart getParentShapePart() {
        return parentPickerControl.getParentShapePart();
    }

    @Override
    public Index getIndex()
    {
        return parentPickerControl.getIndex();
    }

    public WiresParentPickerControlImpl getParentPickerControl() {
        return parentPickerControl;
    }
}
