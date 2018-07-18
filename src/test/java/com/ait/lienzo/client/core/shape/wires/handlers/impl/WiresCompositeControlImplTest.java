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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresCompositeControlImplTest extends AbstractWiresControlTest {
    @Mock
    private WiresShapeControl shapeControl;

    @Mock
    private WiresDockingControl dockingControl;

    @Mock
    private WiresContainmentControl containmentControl;

    @Mock
    private WiresShape shape1;

    @Mock
    private WiresShapeControl shapeControl1;

    @Mock
    private WiresParentPickerCachedControl parentPicker1;

    @Mock
    private WiresParentPickerControl.Index index1;

    @Mock
    private WiresDockingControl dockingControl1;

    @Mock
    private WiresContainmentControl containmentControl1;

    private Collection<WiresShape> selectionShapes;
    private WiresCompositeControl.Context context;

    private WiresCompositeControlImpl tested;

    @Before
    public void setup() {
        super.setUp();
        when(shapeControl.getParentPickerControl()).thenReturn(parentPicker);
        when(shapeControl.getDockingControl()).thenReturn(dockingControl);
        when(shapeControl.getContainmentControl()).thenReturn(containmentControl);
        shape.setWiresShapeControl(shapeControl);
        when(shape1.getChildShapes()).thenReturn(new NFastArrayList<WiresShape>());
        final Point2D location1 = new Point2D(11d, 22d);
        when(shape1.getLocation()).thenReturn(location1);
        when(shape1.getComputedLocation()).thenReturn(location1);
        when(shape1.getControl()).thenReturn(shapeControl1);
        when(shapeControl1.getParentPickerControl()).thenReturn(parentPicker1);
        when(shapeControl1.getDockingControl()).thenReturn(dockingControl1);
        when(shapeControl1.getContainmentControl()).thenReturn(containmentControl1);
        when(parentPicker1.getIndex()).thenReturn(index1);
        selectionShapes = Arrays.asList(shape, shape1);
        context = spy(new WiresCompositeControl.Context() {
            @Override
            public Collection<WiresShape> getShapes() {
                return selectionShapes;
            }

            @Override
            public Collection<WiresConnector> getConnectors() {
                return Collections.emptyList();
            }
        });
        tested = new WiresCompositeControlImpl(context);
    }

    @Test
    public void testSkipAllSelectedShapesOnStart() {
        tested.onMoveStart(2d, 7d);
        verify(index, times(1)).addShapeToSkip(shape);
        verify(index, times(1)).addShapeToSkip(shape1);
        verify(index1, times(1)).addShapeToSkip(shape);
        verify(index1, times(1)).addShapeToSkip(shape1);
    }

    @Test
    public void testGetCandidateShapeLocationRelativeToInitialParent() {
        Point2D shapeLoc = new Point2D(11d, 22d);
        when(containmentControl.getCandidateLocation()).thenReturn(shapeLoc);
        Point2D parentLoc = new Point2D(0.55d, 0.77d);
        parent.setLocation(parentLoc);
        WiresContainer initialParent = mock(WiresContainer.class);
        Point2D initialParentLoc = new Point2D(1d, 3d);
        when(parentPicker.getInitialParent()).thenReturn(initialParent);
        when(initialParent.getLocation()).thenReturn(initialParentLoc);
        when(initialParent.getComputedLocation()).thenReturn(initialParentLoc);
        Point2D result = WiresCompositeControlImpl.getCandidateShapeLocationRelativeToInitialParent(shape);
        assertEquals(10.55d, result.getX(), 0);
        assertEquals(19.77, result.getY(), 0);
    }
}
