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

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMoveControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;

public abstract class AbstractWiresControl<T> implements WiresMoveControl {

    private final Supplier<WiresParentPickerControl> parentPickerControl;
    private       boolean                            enabled;

    protected AbstractWiresControl(final Supplier<WiresParentPickerControl> parentPickerControl) {
        this.parentPickerControl = parentPickerControl;
        enable();
    }

    @SuppressWarnings("unchecked")
    public T setEnabled(final boolean enabled) {
        if (enabled) {
            enable();
        } else {
            disable();
        }
        return (T) this;
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

        doMoveStart(x, y);
    }

    protected void doMoveStart(double x, double y) {

    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        if (!enabled) {
            return false;
        }

        return doMove(dx, dy);
    }

    protected boolean doMove(double dx, double dy) {
        return false;
    }

    @Override
    public void onMoveComplete() {
    }

    public WiresParentPickerControl getParentPickerControl() {
        return parentPickerControl.get();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public WiresShape getShape() {
        return getParentPickerControl().getShape();
    }

    public WiresContainer getParent() {
        return getParentPickerControl().getParent();
    }

    protected WiresLayer getWiresLayer() {
        return getShape().getWiresManager().getLayer();
    }

    protected boolean isStartDocked() {
        return ((WiresParentPickerControlImpl) getParentPickerControl()).isStartDocked();
    }

}
