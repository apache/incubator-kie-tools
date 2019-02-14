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

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresParentPickerControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeLocationControlImpl;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementShapeControlTest {

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresLayer wiresLayer;

    @Mock
    private Layer layer;

    @Mock
    private ScratchPad scratchPad;

    @Mock
    private Context2D context;

    @Mock
    private WiresShape shape;

    @Mock
    private CaseManagementContainmentStateHolder state;

    private CaseManagementShapeControl tested;

    @Before
    public void setup() {
        when(shape.getWiresManager()).thenReturn(wiresManager);
        when(wiresManager.getLayer()).thenReturn(wiresLayer);
        when(wiresLayer.getLayer()).thenReturn(layer);
        when(wiresLayer.getChildShapes()).thenReturn(new NFastArrayList<>());
        when(layer.getScratchPad()).thenReturn(scratchPad);
        when(scratchPad.getContext()).thenReturn(context);
        tested = new CaseManagementShapeControl(shape,
                                                state);
    }

    @Test
    public void checkRightDelegates() {
        final WiresShapeControlImpl delegate = tested.getDelegate();
        final WiresParentPickerControlImpl parentPickerControl = (WiresParentPickerControlImpl) delegate.getParentPickerControl();
        final WiresShapeLocationControlImpl shapeLocationControl = parentPickerControl.getShapeLocationControl();
        assertTrue(shapeLocationControl instanceof CaseManagementShapeLocationControl);
        final WiresContainmentControl containmentControl = delegate.getContainmentControl();
        assertTrue(containmentControl instanceof CaseManagementContainmentControl);
        assertNull(delegate.getDockingControl());
    }
}
