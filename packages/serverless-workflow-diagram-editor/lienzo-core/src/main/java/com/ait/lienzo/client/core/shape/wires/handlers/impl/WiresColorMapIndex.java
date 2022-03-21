/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;

public class WiresColorMapIndex implements WiresLayerIndex {

    private final ColorMapBackedPicker picker;

    public WiresColorMapIndex(final ColorMapBackedPicker picker) {
        this.picker = picker;
    }

    @Override
    public WiresLayerIndex exclude(final WiresContainer shape) {
        picker.getPickerOptions().getShapesToSkip().add(shape);
        return this;
    }

    @Override
    public WiresLayerIndex build(final WiresLayer layer) {
        picker.build(layer.getChildShapes());
        return this;
    }

    @Override
    public PickerPart findShapeAt(final int x,
                                  final int y) {
        return picker.findShapeAt(x, y);
    }

    @Override
    public void clear() {
        picker.clear();
        picker.getPickerOptions().getShapesToSkip().clear();
    }
}
