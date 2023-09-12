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

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.HasShapeState;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class StunnerWiresShapeStateHighlightTest {

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresShape view;

    @Mock
    private StunnerWiresShapeHighlight delegate;

    @Mock
    private Consumer<ShapeState> stateExecutor;

    private StunnerWiresShapeStateHighlight tested;
    private WiresShapeWithState viewWithState;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        viewWithState = new WiresShapeWithState(stateExecutor);
        when(wiresManager.getDockingAcceptor()).thenReturn(IDockingAcceptor.ALL);
        when(wiresManager.getContainmentAcceptor()).thenReturn(IContainmentAcceptor.ALL);
        when(wiresManager.getLocationAcceptor()).thenReturn(ILocationAcceptor.ALL);
        when(wiresManager.getConnectionAcceptor()).thenReturn(IConnectionAcceptor.ALL);
        tested = new StunnerWiresShapeStateHighlight(delegate);
    }

    @Test
    public void testHighlightBodyForNonHasShapeStateView() {
        tested.highlight(view,
                         PickerPart.ShapePart.BODY);
        verify(delegate, times(1)).highlight(eq(view),
                                             eq(PickerPart.ShapePart.BODY));
        verify(stateExecutor, never()).accept(any(ShapeState.class));
    }

    @Test
    public void testHighlightBodyForHasShapeStateView() {
        tested.highlight(viewWithState,
                         PickerPart.ShapePart.BODY);
        verify(stateExecutor, times(1)).accept(eq(ShapeState.HIGHLIGHT));
        verify(delegate, never()).highlight(eq(viewWithState),
                                            any(PickerPart.ShapePart.class));
    }

    @Test
    public void testHighlightBorderForNonStateView() {
        tested.highlight(view,
                         PickerPart.ShapePart.BORDER);
        verify(delegate, times(1)).highlight(eq(view),
                                             eq(PickerPart.ShapePart.BORDER));
        verify(stateExecutor, never()).accept(any(ShapeState.class));
    }

    @Test
    public void testHighlightBorderForStateView() {
        tested.highlight(viewWithState,
                         PickerPart.ShapePart.BORDER);
        verify(delegate, times(1)).highlight(eq(viewWithState),
                                             eq(PickerPart.ShapePart.BORDER));
        verify(stateExecutor, never()).accept(any(ShapeState.class));
    }

    @Test
    public void testRestore() {
        tested.setCurrent(viewWithState);
        tested.restore();
        verify(stateExecutor, times(1)).accept(eq(ShapeState.NONE));
        verify(delegate, never()).highlight(eq(viewWithState),
                                            any(PickerPart.ShapePart.class));
    }

    private static class WiresShapeWithState extends WiresShape implements HasShapeState {

        private final Consumer<ShapeState> stateExecutor;

        public WiresShapeWithState(Consumer<ShapeState> stateExecutor) {
            super(new MultiPath());
            this.stateExecutor = stateExecutor;
        }

        @Override
        public void applyState(ShapeState shapeState) {
            stateExecutor.accept(shapeState);
        }
    }
}
