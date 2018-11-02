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

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementColorMapBackedPickerTest {

    @Mock
    private ScratchPad scratchPad;

    @Mock
    private Context2D context2D;

    @Mock
    private WiresLayer layer;

    @Mock
    private WiresShape shapeToSkip;

    private TestCaseManagementColorMapBackedPicker picker;

    @Before
    public void setup() {
        when(scratchPad.getContext()).thenReturn(context2D);
        final NFastArrayList<WiresShape> shapesToSkip = new NFastArrayList<>();
        shapesToSkip.add(shapeToSkip);
        this.picker = spy(new TestCaseManagementColorMapBackedPicker(new NFastArrayList<>(),
                                                                     scratchPad,
                                                                     shapesToSkip));
    }

    @Test
    public void checkDropZonesAreNotAddedForWiresShapes() {
        final WiresShape shape = mock(WiresShape.class);

        picker.addSupplementaryPaths(shape);

        verify(picker,
               never()).addDropZone(any(CaseManagementShapeView.class));
    }

    @Test
    public void checkDropZonesAreNotAddedForCaseManagementShapesWithNoDropZone() {
        final CaseManagementShapeView shape = mock(CaseManagementShapeView.class);
        when(shape.getDropZone()).thenReturn(Optional.empty());

        picker.addSupplementaryPaths(shape);

        verify(picker,
               times(1)).addDropZone(eq(shape));
    }

    @Test
    public void checkDropZonesAreAddedForCaseManagementShapesWithDropZone() {
        final MultiPath dropZone = mock(MultiPath.class);
        when(dropZone.getAttributes()).thenReturn(mock(Attributes.class));
        when(dropZone.getPathPartListArray()).thenReturn(new NFastArrayList<>());
        final CaseManagementShapeView shape = mock(CaseManagementShapeView.class);
        when(shape.getDropZone()).thenReturn(Optional.of(dropZone));
        when(dropZone.getActualPathPartListArray()).thenReturn(new NFastArrayList<PathPartList>());

        picker.addSupplementaryPaths(shape);

        verify(picker,
               times(1)).addDropZone(eq(shape));
        verify(picker,
               times(1)).drawShape(anyString(),
                                   anyDouble(),
                                   eq(dropZone),
                                   any(PickerPart.class),
                                   eq(true));
    }

    private class TestCaseManagementColorMapBackedPicker extends CaseManagementColorMapBackedPicker {

        public TestCaseManagementColorMapBackedPicker(final NFastArrayList<WiresShape> shapes,
                                                      final ScratchPad scratchPad,
                                                      final NFastArrayList<WiresShape> shapesToSkip) {
            super(layer,
                  shapes,
                  scratchPad,
                  new PickerOptions(false,
                                    0));
        }

        @Override
        //Override to make public for test
        public void drawShape(final String color,
                              final double strokeWidth,
                              final MultiPath multiPath,
                              final PickerPart pickerPart,
                              final boolean fill) {
            super.drawShape(color,
                            strokeWidth,
                            multiPath,
                            pickerPart,
                            fill);
        }
    }
}
