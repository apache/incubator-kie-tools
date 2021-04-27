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

package org.kie.workbench.common.stunner.client.lienzo.wires;

import com.ait.lienzo.client.core.shape.wires.OptionalBounds;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresBoundsConstraintControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImpl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.tooling.common.api.java.util.function.Supplier;

public abstract class DelegateWiresShapeControl implements WiresShapeControl,
                                                           WiresBoundsConstraintControl.SupportsOptionalBounds<DelegateWiresShapeControl> {

    public abstract WiresShapeControlImpl getDelegate();

    @Override
    public WiresShapeControl useIndex(Supplier<WiresLayerIndex> index) {
        getDelegate().useIndex(index);
        return this;
    }

    @Override
    public WiresShapeControl setAlignAndDistributeControl(AlignAndDistributeControl control) {
        getDelegate().setAlignAndDistributeControl(control);
        return this;
    }

    @Override
    public WiresMagnetsControl getMagnetsControl() {
        return getDelegate().getMagnetsControl();
    }

    @Override
    public AlignAndDistributeControl getAlignAndDistributeControl() {
        return getDelegate().getAlignAndDistributeControl();
    }

    @Override
    public WiresDockingControl getDockingControl() {
        return getDelegate().getDockingControl();
    }

    @Override
    public WiresContainmentControl getContainmentControl() {
        return getDelegate().getContainmentControl();
    }

    @Override
    public WiresParentPickerControl getParentPickerControl() {
        return getDelegate().getParentPickerControl();
    }

    @Override
    public void execute() {
        getDelegate().execute();
    }

    @Override
    public boolean accept() {
        return getDelegate().accept();
    }

    @Override
    public boolean isAccepted() {
        return getDelegate().isAccepted();
    }

    @Override
    public boolean isOutOfBounds(double dx, double dy) {
        return getDelegate().isOutOfBounds(dx, dy);
    }

    @Override
    public DelegateWiresShapeControl setLocationBounds(final OptionalBounds bounds) {
        getDelegate().setLocationBounds(bounds);
        return this;
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public void reset() {
        getDelegate().reset();
    }

    @Override
    public void onMouseClick(MouseEvent event) {
        getDelegate().onMouseClick(event);
    }

    @Override
    public void onMouseDown(MouseEvent event) {
        getDelegate().onMouseDown(event);
    }

    @Override
    public void onMouseUp(MouseEvent event) {
        getDelegate().onMouseUp(event);
    }

    @Override
    public void onMoveStart(double x,
                            double y) {
        getDelegate().onMoveStart(x,
                                  y);
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        return getDelegate().onMove(dx,
                                    dy);
    }

    @Override
    public void onMoveComplete() {
        getDelegate().onMoveComplete();
    }

    @Override
    public Point2D getAdjust() {
        return getDelegate().getAdjust();
    }

    @Override
    public void destroy() {
        getDelegate().destroy();
    }
}
