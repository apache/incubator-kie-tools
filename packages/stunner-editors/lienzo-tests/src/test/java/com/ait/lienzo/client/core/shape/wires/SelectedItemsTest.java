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

package com.ait.lienzo.client.core.shape.wires;

import java.util.HashSet;
import java.util.Set;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SelectedItemsTest {

    @Mock
    private WiresShape shape;

    @Mock
    private MagnetManager.Magnets magnets;

    private SelectionManager.SelectedItems selectedItems;

    @Before
    public void setup() {
        SelectionManager manager = mock(SelectionManager.class);
        Layer layer = mock(Layer.class);
        selectedItems = new SelectionManager.SelectedItems(manager, layer);
    }

    @Test
    public void testAddToSelectionShapeWithoutMagnets() {
        selectedItems.add(shape);
        verify(shape, times(1)).getMagnets();

        assertEquals(1, selectedItems.getShapes().size());
        assertTrue(selectedItems.getShapes().contains(shape));
    }

    @Test
    public void testAddToSelectionShapeWithMagnetsWithoutConnections() {
        when(shape.getMagnets()).thenReturn(magnets);

        selectedItems.add(shape);

        verify(shape, times(2)).getMagnets();
        verify(magnets).size();
        assertEquals(1, selectedItems.getShapes().size());
        assertTrue(selectedItems.getShapes().contains(shape));
    }

    @Test
    public void testAddToSelectionShapeWithMagnetsAndConnections() {
        WiresMagnet magnet = mock(WiresMagnet.class);
        NFastArrayList<WiresConnection> wiresConnections = new NFastArrayList<>();
        WiresConnection connection = mock(WiresConnection.class);
        WiresConnector connector = mock(WiresConnector.class);
        when(connection.getConnector()).thenReturn(connector);
        wiresConnections.add(connection);

        Set<WiresConnector> externallyConnected = spy(new HashSet<WiresConnector>());
        externallyConnected.add(connector);
        selectedItems = spy(selectedItems);
        when(selectedItems.getExternallyConnected()).thenReturn(externallyConnected);

        when(shape.getMagnets()).thenReturn(magnets);
        when(magnets.size()).thenReturn(1);
        when(magnets.getMagnet(0)).thenReturn(magnet);
        when(magnet.getConnections()).thenReturn(wiresConnections);

        selectedItems.add(connector);
        selectedItems.add(shape);

        verify(connection).getOppositeConnection();
    }
}
