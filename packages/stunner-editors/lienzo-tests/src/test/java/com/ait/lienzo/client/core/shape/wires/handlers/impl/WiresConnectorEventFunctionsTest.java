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


package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.SelectionManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorEventFunctionsTest {

    @Mock
    private WiresManager wiresManager;

    @Mock
    private SelectionManager selectionManager;

    @Mock
    private WiresConnector connector;

    @Mock
    private WiresConnectorControl connectorControl;

    @Before
    public void setup() {
        when(wiresManager.getSelectionManager()).thenReturn(selectionManager);
        when(connector.getControl()).thenReturn(connectorControl);
    }

    @Test
    public void testCanShowControlPoints() {
        when(connectorControl.areControlPointsVisible()).thenReturn(true);
        assertFalse(WiresConnectorEventFunctions.canShowControlPoints().test(connector));
        when(connectorControl.areControlPointsVisible()).thenReturn(false);
        assertTrue(WiresConnectorEventFunctions.canShowControlPoints().test(connector));
    }

    @Test
    public void testCanHideControlPoints() {
        when(wiresManager.getSelectionManager()).thenReturn(null);
        assertTrue(WiresConnectorEventFunctions.canHideControlPoints(wiresManager).test(connector));
        when(wiresManager.getSelectionManager()).thenReturn(selectionManager);
        SelectionManager.SelectedItems selectedItems = new SelectionManager.SelectedItems(selectionManager, new Layer());
        when(selectionManager.getSelectedItems()).thenReturn(selectedItems);
        assertTrue(WiresConnectorEventFunctions.canHideControlPoints(wiresManager).test(connector));
        selectedItems.add(connector);
        assertFalse(WiresConnectorEventFunctions.canHideControlPoints(wiresManager).test(connector));
    }

    @Test
    public void testSelect() {
        WiresConnectorHandlerImpl.Event event = mock(WiresConnectorHandlerImpl.Event.class);
        WiresConnectorEventFunctions.select(wiresManager, connector).accept(event);
        verify(selectionManager, times(1)).selected(eq(connector), eq(false));
        verify(connectorControl, times(1)).showControlPoints();
        verify(connectorControl, never()).hideControlPoints();
    }

    @Test
    public void testAddControlPoint() {
        WiresConnectorHandlerImpl.Event event = new WiresConnectorHandlerImpl.Event(22.1, 324.22, false);
        WiresConnectorEventFunctions.addControlPoint(connector).accept(event);
        verify(connectorControl, times(1)).addControlPoint(eq(22.1d), eq(324.22d));
    }
}
