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
package org.kie.workbench.common.stunner.cm.client.wires;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;

public class CaseManagementColorMapBackedPicker extends ColorMapBackedPicker {

    public CaseManagementColorMapBackedPicker(final WiresLayer layer,
                                              final NFastArrayList<WiresShape> shapes,
                                              final ScratchPad scratchPad,
                                              final PickerOptions options) {
        super(layer,
              shapes,
              scratchPad,
              options);
    }

    @Override
    protected void addSupplementaryPaths(final WiresShape prim) {
        if (prim instanceof CaseManagementShapeView) {
            addDropZone((CaseManagementShapeView) prim);
        }
    }

    void addDropZone(final CaseManagementShapeView prim) {
        final Optional<MultiPath> optMultiPath = prim.getDropZone();
        if (optMultiPath.isPresent()) {
            final MultiPath multiPath = optMultiPath.get();
            multiPath.getAttributes().setX(prim.getX());
            multiPath.getAttributes().setY(prim.getY());
            drawShape(m_colorKeyRotor.next(),
                      multiPath.getStrokeWidth(),
                      multiPath,
                      new PickerPart(prim,
                                     PickerPart.ShapePart.BODY),
                      true);
        }
    }
}
