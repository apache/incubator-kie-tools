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
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.util.ScratchPad;

public class WiresControlFactoryImpl implements WiresControlFactory {

    @Override
    public WiresShapeControl newShapeControl(WiresShape shape,
                                             WiresManager wiresManager) {
        return new WiresShapeControlImpl(shape);
    }

    @Override
    public WiresCompositeControl newCompositeControl(WiresCompositeControl.Context selectionContext,
                                                     WiresManager wiresManager) {
        return new WiresCompositeControlImpl(selectionContext);
    }

    @Override
    public WiresShapeHighlight<PickerPart.ShapePart> newShapeHighlight(WiresManager wiresManager) {
        return new WiresShapeHighlightImpl(wiresManager.getDockingAcceptor().getHotspotSize());
    }

    @Override
    public WiresLayerIndex newIndex(WiresManager manager) {
        final ScratchPad scratchPad = manager.getLayer().getLayer().getScratchPad();
        final ColorMapBackedPicker.PickerOptions pickerOptions =
                new ColorMapBackedPicker.PickerOptions(true,
                                                       manager.getDockingAcceptor().getHotspotSize());
        return new WiresColorMapIndex(new ColorMapBackedPicker(scratchPad,
                                                               pickerOptions));
    }

    @Override
    public WiresConnectorControl newConnectorControl(WiresConnector connector,
                                                     WiresManager wiresManager) {
        return new WiresConnectorControlImpl(connector,
                                             wiresManager);
    }

    @Override
    public WiresConnectionControl newConnectionControl(WiresConnector connector,
                                                       boolean headNotTail,
                                                       WiresManager wiresManager) {
        return new WiresConnectionControlImpl(connector,
                                              headNotTail,
                                              wiresManager);
    }
}
