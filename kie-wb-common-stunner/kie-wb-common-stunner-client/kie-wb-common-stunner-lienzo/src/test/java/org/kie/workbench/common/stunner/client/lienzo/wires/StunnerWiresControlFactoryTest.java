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

package org.kie.workbench.common.stunner.client.lienzo.wires;

import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresControlFactoryImpl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.wires.decorator.StunnerPointHandleDecorator;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasUnhighlightEvent;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class StunnerWiresControlFactoryTest {

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresShape wiresShape;

    @Mock
    private WiresConnector wiresConnector;

    @Mock
    private WiresControlFactoryImpl delegate;

    @Mock
    private WiresShapeControl shapeControl;

    @Mock
    private WiresConnectorControlImpl connectorControl;

    @Mock
    private WiresConnectionControl connectionControl;

    @Mock
    private WiresCompositeControl.Context compositeContext;

    @Mock
    private WiresCompositeControl compositeControl;

    @Mock
    private EventSourceMock<CanvasUnhighlightEvent> unhighlightEvent;

    private StunnerWiresControlFactory tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(wiresManager.getDockingAcceptor()).thenReturn(IDockingAcceptor.ALL);
        when(wiresManager.getContainmentAcceptor()).thenReturn(IContainmentAcceptor.ALL);
        when(wiresManager.getLocationAcceptor()).thenReturn(ILocationAcceptor.ALL);
        when(wiresManager.getConnectionAcceptor()).thenReturn(IConnectionAcceptor.ALL);
        when(delegate.newShapeControl(eq(wiresShape),
                                      eq(wiresManager))).thenReturn(shapeControl);
        when(delegate.newConnectorControl(eq(wiresConnector),
                                          eq(wiresManager))).thenReturn(connectorControl);
        when(delegate.newConnectionControl(eq(wiresConnector),
                                           anyBoolean(),
                                           eq(wiresManager))).thenReturn(connectionControl);
        when(delegate.newCompositeControl(eq(compositeContext),
                                          eq(wiresManager))).thenReturn(compositeControl);
        tested = new StunnerWiresControlFactory(delegate, unhighlightEvent);
    }

    @Test
    public void testNewShapeControl() {
        final WiresShapeControl wiresShapeControl = tested.newShapeControl(wiresShape,
                                                                           wiresManager);
        assertNotNull(wiresShapeControl);
        assertTrue(wiresShapeControl instanceof StunnerWiresShapeControl);
    }

    @Test
    public void testNewConnectorControl() {
        assertEquals(connectorControl,
                     tested.newConnectorControl(wiresConnector,
                                                wiresManager));
        verify(connectorControl).setPointHandleDecorator(any(StunnerPointHandleDecorator.class));
    }

    @Test
    public void testNewConnectionControl() {
        assertEquals(connectionControl,
                     tested.newConnectionControl(wiresConnector,
                                                 true,
                                                 wiresManager));
    }

    @Test
    public void testNewCompositeControl() {
        assertEquals(compositeControl,
                     tested.newCompositeControl(compositeContext,
                                                wiresManager));
    }

    @Test
    public void testNewShapeHighlight() {
        final WiresShapeHighlight<PickerPart.ShapePart> instance = tested.newShapeHighlight(wiresManager);
        assertNotNull(instance);
        assertTrue(instance instanceof StunnerWiresShapeStateHighlight);
    }
}
