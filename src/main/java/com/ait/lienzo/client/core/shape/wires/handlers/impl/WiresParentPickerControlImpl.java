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

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMouseControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.types.Point2D;
import java.util.function.Supplier;

public class WiresParentPickerControlImpl implements WiresParentPickerControl,
                                                     WiresMouseControl {

    private final WiresShapeLocationControlImpl shapeLocationControl;
    private final Supplier<WiresLayerIndex>     index;
    private       WiresContainer                m_parent;
    private       PickerPart                    m_parentPart;
    private       WiresContainer                initialParent;

    public WiresParentPickerControlImpl(final WiresShape m_shape,
                                        final Supplier<WiresLayerIndex> index) {
        this(new WiresShapeLocationControlImpl(m_shape),
             index);
    }

    public WiresParentPickerControlImpl(final WiresShapeLocationControlImpl shapeLocationControl,
                                        final Supplier<WiresLayerIndex> index) {
        this.shapeLocationControl = shapeLocationControl;
        this.index = index;
    }

    @Override
    public void onMoveStart(double x,
                            double y) {
        shapeLocationControl.onMoveStart(x, y);
        initialParent = getShape().getParent();
        m_parent = getShape().getParent();

        if (m_parent != null && m_parent instanceof WiresShape) {
            if (getShape().getDockedTo() == null) {
                m_parentPart = new PickerPart((WiresShape) m_parent,
                                              PickerPart.ShapePart.BODY);
            } else {
                m_parentPart = findShapeAt((int) shapeLocationControl.getShapeStartCenterX(),
                                           (int) shapeLocationControl.getShapeStartCenterY());
            }
        }
    }

    @Override
    public Point2D getCurrentLocation() {
        return shapeLocationControl.getCurrentLocation();
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        if (!shapeLocationControl.onMove(dx, dy)) {
            final Point2D currentLocation = getCurrentLocation();
            final double x = currentLocation.getX();
            final double y = currentLocation.getY();
            WiresContainer parent = null;
            PickerPart parentPart = findShapeAt(x,
                                                y);
            if (parentPart != null) {
                parent = parentPart.getShape();
            }

            if (parent != m_parent || parentPart != m_parentPart) {
                parentPart = findShapeAt(x,
                                         y);
                parent = null != parentPart ? parentPart.getShape() : null;
            }

            m_parent = parent;
            m_parentPart = parentPart;
        }
        return false;
    }

    @Override
    public void onMoveAdjusted(final Point2D dxy) {
        shapeLocationControl.onMoveAdjusted(dxy);
    }

    private PickerPart findShapeAt(double x,
                                   double y) {
        final PickerPart parent = index.get().findShapeAt((int) x,
                                                       (int) y);
        // Ensure same shape is not the parent found, even if it
        // has been indexed in the colormap picker.
        if (null != parent
                && parent.getShape() != getShape()) {
            return parent;
        }
        return null;
    }

    @Override
    public Point2D getAdjust() {
        return shapeLocationControl.getAdjust();
    }

    @Override
    public void onMoveComplete() {
        shapeLocationControl.onMoveComplete();
    }

    @Override
    public void onMouseClick(MouseEvent event) {

    }

    @Override
    public void onMouseDown(MouseEvent event) {
        m_parent = getShape().getParent();
    }

    @Override
    public void onMouseUp(MouseEvent event) {
        if (m_parent != getShape().getParent()) {
            onMoveComplete();
        }
    }

    @Override
    public void execute() {
        shapeLocationControl.execute();
    }

    public void clear() {
        shapeLocationControl.clear();
        m_parent = null;
        m_parentPart = null;
        initialParent = null;
    }

    @Override
    public void reset() {
        shapeLocationControl.reset();
        clear();
    }

    @Override
    public void destroy() {
        clear();
        shapeLocationControl.destroy();
    }

    public Point2D getShapeLocation() {
        return shapeLocationControl.getShapeLocation();
    }

    @Override
    public void setShapeLocation(Point2D location) {
        shapeLocationControl.setShapeLocation(location);
    }

    @Override
    public WiresShape getShape() {
        return shapeLocationControl.getShape();
    }

    @Override
    public Point2D getShapeInitialLocation() {
        return shapeLocationControl.getShapeInitialLocation();
    }

    public WiresShapeLocationControlImpl getShapeLocationControl() {
        return shapeLocationControl;
    }

    @Override
    public WiresContainer getParent() {
        return null != m_parent ?
                m_parent :
                getShape().getWiresManager().getLayer();
    }

    public PickerPart.ShapePart getParentShapePart() {
        return null != m_parentPart ? m_parentPart.getShapePart() : null;
    }

    @Override
    public WiresLayerIndex getIndex()
    {
        return index.get();
    }

    public WiresContainer getInitialParent() {
        return initialParent;
    }

    public double getMouseStartX() {
        return shapeLocationControl.getMouseStartX();
    }

    public double getMouseStartY() {
        return shapeLocationControl.getMouseStartY();
    }

    public boolean isStartDocked() {
        return shapeLocationControl.isStartDocked();
    }

}
