/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;

public class CaseManagementColorMapBackedPicker extends ColorMapBackedPicker {

    public CaseManagementColorMapBackedPicker(final NFastArrayList<WiresShape> shapes,
                                              final ScratchPad scratchPad,
                                              final WiresShape shapeToSkip) {
        super(shapes,
              scratchPad,
              shapeToSkip);
    }

    public CaseManagementColorMapBackedPicker(final NFastArrayList<WiresShape> shapes,
                                              final ScratchPad scratchPad,
                                              final WiresShape shapeToSkip,
                                              final boolean addHotspots,
                                              final double borderWidth) {
        super(shapes,
              scratchPad,
              shapeToSkip,
              addHotspots,
              borderWidth);
    }

    public CaseManagementColorMapBackedPicker(final NFastArrayList<WiresShape> shapes,
                                              final ScratchPad scratchPad,
                                              final NFastArrayList<WiresShape> shapesToSkip,
                                              final boolean addHotspots,
                                              final double borderWidth) {
        super(shapes,
              scratchPad,
              shapesToSkip,
              addHotspots,
              borderWidth);
    }

    @Override
    protected void addSupplementaryPaths(final WiresShape prim) {
        if (prim instanceof AbstractCaseManagementShape) {
            addDropZone((AbstractCaseManagementShape) prim);
        }
    }

    @SuppressWarnings("unused")
    //This is a convenience method for debugging ColorMapBackedPicker
    public ImageData getBackingImageData() {
        return super.m_imageData;
    }

    void addDropZone(final AbstractCaseManagementShape<?> prim) {
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
