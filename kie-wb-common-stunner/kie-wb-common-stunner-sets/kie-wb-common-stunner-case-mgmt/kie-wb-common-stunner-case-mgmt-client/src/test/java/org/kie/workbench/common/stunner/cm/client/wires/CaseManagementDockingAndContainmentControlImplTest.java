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

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementDockingAndContainmentControlImplTest {

    @Mock
    private DragContext context;

    private Layer layer;
    private MockCaseManagementShape child;
    private MockCaseManagementShape parent;
    private WiresManager wiresManager;
    private CaseManagementContainmentStateHolder state;
    private ScratchPad scratchPad;
    private PickerPart pickerPart;

    private CaseManagementDockingAndContainmentControlImpl control;

    @Before
    public void setup() {
        layer = spy(new Layer());
        scratchPad = new ScratchPad(1000,
                                    1000);
        when(context.getDragStartX()).thenReturn(0);
        when(context.getDragStartY()).thenReturn(0);
        child = new MockCaseManagementShape();
        parent = new MockCaseManagementShape();
        parent.add(child);

        wiresManager = WiresManager.get(layer);
        state = new CaseManagementContainmentStateHolder();
        control = new CaseManagementDockingAndContainmentControlImpl(child,
                                                                     wiresManager,
                                                                     state) {
            @Override
            protected ColorMapBackedPicker makeColorMapBackedPicker(final WiresLayer layer,
                                                                    final WiresContainer parent,
                                                                    final WiresShape shape) {
                //Override findShapeAt(..) as the JUnit Runner mocks the classes used by ColorMapBackedPicker
                final ColorMapBackedPicker picker = spy(super.makeColorMapBackedPicker(layer,
                                                                                       parent,
                                                                                       shape));
                doReturn(pickerPart).when(picker).findShapeAt(anyInt(),
                                                              anyInt());
                return picker;
            }
        };
        wiresManager.getLayer().add(parent);

        when(layer.getScratchPad()).thenReturn(scratchPad);
        when(layer.getOverLayer()).thenReturn(layer);
    }

    @Test
    public void checkDragStartWhenCaseManagementShapeDragged() {
        control.dragStart(context);

        assertTrue(state.getOriginalIndex().isPresent());
        assertTrue(state.getOriginalParent().isPresent());
        assertTrue(state.getGhost().isPresent());
        assertEquals(0,
                     (int) state.getOriginalIndex().get());
        assertEquals(parent,
                     state.getOriginalParent().get());
        assertNotNull(state.getGhost().get());
    }

    @Test
    public void checkDragAdjustOverOriginalParentWhenCaseManagementShapeDragged() {
        doDragOverParent(parent);

        assertEquals(1,
                     parent.getChildShapes().size());
        assertEquals(state.getGhost().get(),
                     parent.getChildShapes().get(0));
    }

    private void doDragOverParent(final WiresShape parent) {
        pickerPart = new PickerPart(parent,
                                    PickerPart.ShapePart.BODY);

        control.dragStart(context);

        control.dragAdjust(new Point2D(0,
                                       0));
    }

    @Test
    public void checkDragAdjustOverDifferentParentWhenCaseManagementShapeDragged() {
        final MockCaseManagementShape newParent = new MockCaseManagementShape();
        doDragOverParent(newParent);

        assertEquals(0,
                     parent.getChildShapes().size());
        assertEquals(1,
                     newParent.getChildShapes().size());
        assertEquals(state.getGhost().get(),
                     newParent.getChildShapes().get(0));
    }

    @Test
    public void addShapeToNewParent() {
        final MockCaseManagementShape newParent = new MockCaseManagementShape();
        doDragOverParent(newParent);

        control.addShapeToParent();

        assertEquals(0,
                     parent.getChildShapes().size());
        assertEquals(1,
                     newParent.getChildShapes().size());
        assertEquals(child,
                     newParent.getChildShapes().get(0));
        assertFalse(state.getOriginalIndex().isPresent());
        assertFalse(state.getOriginalParent().isPresent());
        assertFalse(state.getGhost().isPresent());
    }

    @Test
    public void addShapeToExistingParent() {
        doDragOverParent(parent);

        control.addShapeToParent();

        assertEquals(1,
                     parent.getChildShapes().size());
        assertEquals(child,
                     parent.getChildShapes().get(0));
        assertFalse(state.getOriginalIndex().isPresent());
        assertFalse(state.getOriginalParent().isPresent());
        assertFalse(state.getGhost().isPresent());
    }

    @Test
    public void addShapeToParentWhenDragStartsOverNonCaseManagementShape() {
        final WiresShape newParent = new WiresShape(new MultiPath());
        newParent.add(child);

        doDragOverParent(parent);

        control.addShapeToParent();

        assertEquals(1,
                     parent.getChildShapes().size());
        assertEquals(0,
                     newParent.getChildShapes().size());
        assertEquals(child,
                     parent.getChildShapes().get(0));
        assertFalse(state.getOriginalIndex().isPresent());
        assertFalse(state.getOriginalParent().isPresent());
        assertFalse(state.getGhost().isPresent());
    }

    @Test
    public void addShapeToNullParentRestoresOriginalParent() {
        doDragOverParent(null);

        control.addShapeToParent();

        assertEquals(1,
                     parent.getChildShapes().size());
        assertEquals(child,
                     parent.getChildShapes().get(0));
        assertFalse(state.getOriginalIndex().isPresent());
        assertFalse(state.getOriginalParent().isPresent());
        assertFalse(state.getGhost().isPresent());
    }
}
