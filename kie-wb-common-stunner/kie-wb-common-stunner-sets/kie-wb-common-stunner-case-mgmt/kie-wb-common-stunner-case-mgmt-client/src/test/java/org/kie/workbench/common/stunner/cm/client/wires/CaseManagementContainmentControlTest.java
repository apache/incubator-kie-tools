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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILayoutHandler;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresContainmentControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresParentPickerControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeLocationControlImpl;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementContainmentControlTest {

    private static ColorMapBackedPicker.PickerOptions PICKER_OPTIONS =
            new ColorMapBackedPicker.PickerOptions(false,
                                                   0d);

    @Mock
    private WiresManager wiresManager;

    @Mock
    private IContainmentAcceptor containmentAcceptor;

    @Mock
    private WiresParentPickerControlImpl parentPickerControl;

    @Mock
    private WiresShapeLocationControlImpl shapeLocationControl;

    @Mock
    private WiresContainmentControlImpl containmentControl;

    @Mock
    private CaseManagementContainmentStateHolder state;

    @Mock
    private CaseManagementShapeView parent;

    @Mock
    private ILayoutHandler parentLayoutHandler;

    @Mock
    private CaseManagementShapeView ghost;

    @Mock
    private WiresParentPickerControl.Index index;

    private CaseManagementContainmentControl control;

    @Mock
    private CaseManagementShapeView shape;

    private Layer layer;

    @Before
    public void setup() {
        layer = spy(new Layer());

        final Group shapeGroup = mock(Group.class);
        when(shapeGroup.getLayer()).thenReturn(layer);
        when(shape.getGroup()).thenReturn(shapeGroup);
        when(shape.getWiresManager()).thenReturn(wiresManager);
        doReturn(ghost).when(shape).getGhost();
        when(wiresManager.getContainmentAcceptor()).thenReturn(containmentAcceptor);
        when(parentPickerControl.getPickerOptions()).thenReturn(PICKER_OPTIONS);
        when(parentPickerControl.getShapeLocationControl()).thenReturn(shapeLocationControl);
        when(parentPickerControl.getIndex()).thenReturn(index);
        when(containmentControl.getParentPickerControl()).thenReturn(parentPickerControl);
        when(containmentControl.getShape()).thenReturn(shape);
        when(containmentControl.getParent()).thenReturn(parent);
        final NFastArrayList<WiresShape> children = new NFastArrayList<>();
        children.add(shape);
        children.add(ghost);
        when(parent.getChildShapes()).thenReturn(children);
        when(parent.getLayoutHandler()).thenReturn(parentLayoutHandler);
        when(parent.getIndex(eq(shape))).thenReturn(0);
        when(parent.getGroup()).thenReturn(new Group());
        when(state.getGhost()).thenReturn(Optional.of(ghost));
        when(state.getOriginalParent()).thenReturn(Optional.of(parent));
        when(state.getOriginalIndex()).thenReturn(Optional.of(0));
        control = new CaseManagementContainmentControl(containmentControl,
                                                       state);
    }

    @Test
    public void testEnable() {
        control.setEnabled(true);
        verify(containmentControl, times(1)).setEnabled(eq(true));
        control.setEnabled(false);
        verify(containmentControl, times(1)).setEnabled(eq(false));
    }

    @Test
    public void testOnMoveStartButNoCMShape() {
        final WiresShape aShape = mock(WiresShape.class);
        when(containmentControl.getShape()).thenReturn(aShape);
        final double x = 15.5d;
        final double y = 21.63d;
        control.onMoveStart(x, y);
        verify(containmentControl, times(1)).onMoveStart(eq(x),
                                                         eq(y));
        verify(state, times(1)).setGhost(eq(Optional.empty()));
        verify(state, times(1)).setOriginalIndex(eq(Optional.empty()));
        verify(state, times(1)).setOriginalParent(eq(Optional.empty()));
        verify(parent, never()).logicallyReplace(any(CaseManagementShapeView.class),
                                                 any(CaseManagementShapeView.class));
    }

    @Test
    public void testOnMoveStart() {
        final double x = 15.5d;
        final double y = 21.63d;
        control.onMoveStart(x, y);
        verify(containmentControl, times(1)).onMoveStart(eq(x),
                                                         eq(y));
        verify(state, times(1)).setGhost(eq(Optional.of(ghost)));
        verify(state, times(1)).setOriginalIndex(eq(Optional.of(0)));
        verify(state, times(1)).setOriginalParent(eq(Optional.of(parent)));
//        assertEquals(1, PICKER_OPTIONS.getShapesToSkip().size());
//        assertEquals(ghost, PICKER_OPTIONS.getShapesToSkip().get(0));
        verify(parent, times(1)).logicallyReplace(eq(shape),
                                                  eq(ghost));
    }

    @Test
    public void testOnMoveButContainmentNotAllowed() {
        when(containmentAcceptor
                     .containmentAllowed(eq(parent),
                                         eq(new WiresShape[]{shape})))
                .thenReturn(false);
        final double x = 15.5d;
        final double y = 21.63d;
        control.onMove(x, y);
        verify(containmentControl, times(1)).onMove(eq(x),
                                                    eq(y));
        verify(parentLayoutHandler, never()).add(any(WiresShape.class),
                                                 any(WiresContainer.class),
                                                 any(Point2D.class));
        verify(parentPickerControl, never()).rebuildPicker();
    }

    @Test
    public void testOnMove() {
        when(containmentAcceptor
                     .containmentAllowed(eq(parent),
                                         eq(new WiresShape[]{shape})))
                .thenReturn(true);
        final double x = 15.5d;
        final double y = 21.63d;
        control.onMove(x, y);
        verify(containmentControl, times(1)).onMove(eq(x),
                                                    eq(y));
        verify(parentLayoutHandler, never()).add(eq(ghost),
                                                  eq(parent),
                                                  eq(new Point2D(x, y)));
        verify(parentPickerControl, times(1)).rebuildPicker();
        verify(containmentAcceptor, never()).acceptContainment(any(WiresContainer.class),
                                                               any(WiresShape[].class));
    }

    @Test
    public void testOnMoveComplete() {
        control.onMoveComplete();
        verify(containmentControl, times(1)).onMoveComplete();
    }

    @Test
    public void testIsAllow() {
        when(containmentControl.isAllow()).thenReturn(true);
        assertTrue(control.isAllow());
        when(containmentControl.isAllow()).thenReturn(false);
        assertFalse(control.isAllow());
    }

    @Test
    public void testAccept() {
        control.accept();
        verify(containmentAcceptor, never()).containmentAllowed(any(WiresContainer.class),
                                                                any(WiresShape[].class));
        verify(containmentAcceptor, times(1)).acceptContainment(eq(parent),
                                                                eq(new WiresShape[]{shape}));
    }

    @Test
    public void testCandidateLocation() {
        Point2D location = new Point2D(0d, 1d);
        when(containmentControl.getCandidateLocation()).thenReturn(location);
        assertEquals(location, control.getCandidateLocation());
    }

    @Test
    public void testAdjust() {
        Point2D adjust = new Point2D(0d, 1d);
        when(containmentControl.getAdjust()).thenReturn(adjust);
        assertEquals(adjust, control.getAdjust());
    }

    @Test
    public void testExecuteAndRestoreGhost() {
        control.execute();
        verify(parent, never()).logicallyReplace(eq(ghost),
                                                  eq(shape));
        verify(ghost, times(1)).destroy();
        verify(state, times(1)).setGhost(Optional.empty());
        verify(state, times(1)).setOriginalParent(Optional.empty());
        verify(state, times(1)).setOriginalIndex(Optional.empty());
        verify(layer, times(1)).batch();
    }

    @Test
    public void testClear() {
        control.clear();
        verify(ghost, times(1)).destroy();
        verify(state, times(1)).setGhost(Optional.empty());
        verify(state, times(1)).setOriginalParent(Optional.empty());
        verify(state, times(1)).setOriginalIndex(Optional.empty());
    }

    @Test
    public void testReset() {
        control.reset();
        verify(ghost, times(2)).removeFromParent();
        verify(parent, times(1)).addShape(eq(shape),
                                          eq(0));
        verify(state, times(1)).setGhost(Optional.empty());
        verify(state, times(1)).setOriginalParent(Optional.empty());
        verify(state, times(1)).setOriginalIndex(Optional.empty());
    }

    @Test
    public void testReparentDraggedShape() {
        when(state.getOriginalParent()).thenReturn(Optional.of(mock(WiresContainer.class)));
        final CaseManagementShapeView ghostParent = mock(CaseManagementShapeView.class);
        when(ghost.getParent()).thenReturn(ghostParent);
        when(ghostParent.getIndex(eq(ghost))).thenReturn(1);

        control.execute();

        verify(containmentControl, times(1)).execute();
        verify(ghost, atLeast(1)).removeFromParent();
        verify(ghostParent, times(1)).addShape(eq(shape), eq(1));
    }
}
