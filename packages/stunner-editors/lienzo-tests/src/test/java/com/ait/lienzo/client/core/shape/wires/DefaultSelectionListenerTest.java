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

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DefaultSelectionListenerTest {

    @Mock
    private Consumer<WiresShape> onSelectShape;

    @Mock
    private Consumer<WiresShape> onDeselectShape;

    @Mock
    private Consumer<WiresConnector> onSelectConnector;

    @Mock
    private Consumer<WiresConnector> onDeselectConnector;

    @Mock
    private SelectionManager selectionManager;

    @Mock
    private WiresShape selectedShape;

    @Mock
    private WiresShape deselectedShape;

    @Mock
    private WiresConnector selectedConnector;

    @Mock
    private WiresConnector deselectedConnector;

    private DefaultSelectionListener tested;

    private SelectionManager.SelectedItems selectedItems;

    @Before
    public void setUp() {
        Layer layer = new Layer();
        selectedItems = new SelectionManager.SelectedItems(selectionManager, layer);
        selectedItems.add(selectedShape);
        selectedItems.add(selectedConnector);
        selectedItems.setSelectionGroup(true);
        selectedItems.getChanged().getRemovedShapes().add(deselectedShape);
        selectedItems.getChanged().getRemovedConnectors().add(deselectedConnector);
        tested = new DefaultSelectionListener(onSelectShape,
                                              onDeselectShape,
                                              onSelectConnector,
                                              onDeselectConnector);
    }

    @Test
    public void testOnItemsChanged() {
        tested.onChanged(selectedItems);
        verify(onSelectShape, times(1)).accept(eq(selectedShape));
        verify(onDeselectShape, times(1)).accept(eq(deselectedShape));
        verify(onSelectConnector, times(1)).accept(eq(selectedConnector));
        verify(onDeselectConnector, times(1)).accept(eq(deselectedConnector));
        verify(selectedShape, times(1)).listen(eq(false));
        verify(deselectedShape, times(1)).listen(eq(true));
        verify(selectedConnector, times(1)).listen(eq(false));
        verify(deselectedConnector, times(1)).listen(eq(true));
    }
}
