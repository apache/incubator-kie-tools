/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.lienzo.wires;

import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLineSpliceControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImpl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DelegateWiresShapeControlTest {

    private static Point2D ADJUST = new Point2D(5.5d, 6.6d);

    @Mock
    private WiresShapeControlImpl delegate;

    @Mock
    private WiresMagnetsControl magnetsControl;

    @Mock
    private AlignAndDistributeControl alignAndDistributeControl;

    @Mock
    private WiresDockingControl dockingControl;

    @Mock
    private WiresContainmentControl containmentControl;

    @Mock
    private WiresLineSpliceControl lineSpliceControl;

    @Mock
    private WiresParentPickerControl parentPickerControl;

    private DelegateWiresShapeControl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(delegate.getAlignAndDistributeControl()).thenReturn(alignAndDistributeControl);
        when(delegate.getParentPickerControl()).thenReturn(parentPickerControl);
        when(delegate.getContainmentControl()).thenReturn(containmentControl);
        when(delegate.getLineSpliceControl()).thenReturn(lineSpliceControl);
        when(delegate.getDockingControl()).thenReturn(dockingControl);
        when(delegate.getAdjust()).thenReturn(ADJUST);
        tested = new DelegateWiresShapeControl() {
            @Override
            public WiresShapeControlImpl getDelegate() {
                return delegate;
            }
        };
    }

    @Test
    public void testGettersFromDelegate() {
        assertEquals(alignAndDistributeControl, tested.getAlignAndDistributeControl());
        assertEquals(parentPickerControl, tested.getParentPickerControl());
        assertEquals(containmentControl, tested.getContainmentControl());
        assertEquals(lineSpliceControl, tested.getLineSpliceControl());
        assertEquals(dockingControl, tested.getDockingControl());
        assertEquals(ADJUST, tested.getAdjust());
    }

    @Test
    public void testControlMethods() {
        tested.setAlignAndDistributeControl(alignAndDistributeControl);
        verify(delegate, times(1)).setAlignAndDistributeControl(eq(alignAndDistributeControl));
        tested.execute();
        verify(delegate, times(1)).execute();
        tested.accept();
        verify(delegate, times(1)).accept();
        tested.isOutOfBounds(1d, 2d);
        verify(delegate, times(1)).isOutOfBounds(eq(1d), eq(2d));
        tested.clear();
        verify(delegate, times(1)).clear();
        tested.reset();
        verify(delegate, times(1)).reset();
        tested.onMoveStart(1d, 2d);
        verify(delegate, times(1)).onMoveStart(eq(1d), eq(2d));
        tested.onMove(3d, 4d);
        verify(delegate, times(1)).onMove(eq(3d), eq(4d));
        tested.onMoveComplete();
        verify(delegate, times(1)).onMoveComplete();
        final MouseEvent mouseEvent = mock(MouseEvent.class);
        tested.onMouseClick(mouseEvent);
        verify(delegate, times(1)).onMouseClick(eq(mouseEvent));
        tested.onMouseDown(mouseEvent);
        verify(delegate, times(1)).onMouseDown(eq(mouseEvent));
        tested.onMouseUp(mouseEvent);
        verify(delegate, times(1)).onMouseUp(eq(mouseEvent));
    }
}
