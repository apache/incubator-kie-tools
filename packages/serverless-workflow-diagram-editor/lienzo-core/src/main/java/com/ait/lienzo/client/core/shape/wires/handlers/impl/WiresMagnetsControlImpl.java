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

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.Direction;

import static com.ait.lienzo.client.core.shape.wires.MagnetManager.EIGHT_CARDINALS;
import static com.ait.lienzo.client.core.shape.wires.MagnetManager.FOUR_CARDINALS;

public class WiresMagnetsControlImpl implements WiresMagnetsControl {

    private final WiresShape shape;

    public WiresMagnetsControlImpl(WiresShape shape) {
        this.shape = shape;
    }

    @Override
    public void onMoveStart(double x,
                            double y) {

    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        shape.shapeMoved();
        return false;
    }

    @Override
    public void onMoveComplete() {
        shape.shapeMoved();
    }

    @Override
    public Point2D getAdjust() {
        return new Point2D(0,
                           0);
    }

    public void shapeMoved() {
        if (null != getMagnets()) {
            IPrimitive<?> prim = getMagnets().getGroup();
            Point2D absLoc = prim.getComputedLocation();
            double x = absLoc.getX();
            double y = absLoc.getY();
            shapeMoved(x,
                       y);
        }
    }

    private void shapeMoved(final double x,
                            final double y) {
        if (null != getMagnets()) {
            final IControlHandleList controlHandles = getMagnets().getMagnets();
            for (int i = 0; i < controlHandles.size(); i++) {
                WiresMagnet m = (WiresMagnet) controlHandles.getHandle(i);
                m.shapeMoved(x,
                             y);
            }
        }
    }

    public void shapeChanged() {
        final IControlHandleList controlHandles = null != getMagnets() ? getMagnets().getMagnets() : null;
        if (null == controlHandles || controlHandles.isEmpty()) {
            return;
        }
        Direction[] cardinals = controlHandles.size() == 9 ? EIGHT_CARDINALS : FOUR_CARDINALS;
        final Point2DArray points = MagnetManager.getWiresIntersectionPoints(shape,
                                                                             cardinals);
        final int size = controlHandles.size() <= points.size() ? controlHandles.size() : points.size();
        for (int i = 0; i < size; i++) {
            Point2D p = points.get(i);
            WiresMagnet m = (WiresMagnet) controlHandles.getHandle(i);
            m.setRx(p.getX()).setRy(p.getY());
        }
        this.shapeMoved();
    }

    private MagnetManager.Magnets getMagnets() {
        return shape.getMagnets();
    }
}
