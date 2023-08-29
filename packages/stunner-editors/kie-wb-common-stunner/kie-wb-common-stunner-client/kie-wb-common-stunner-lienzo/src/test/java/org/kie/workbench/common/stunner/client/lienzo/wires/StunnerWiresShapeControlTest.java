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

import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImpl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class StunnerWiresShapeControlTest {

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresShapeControlImpl delegate;

    @Mock
    private WiresParentPickerControl parentPickerControl;

    @Mock
    private WiresShapeView shapeView;

    private StunnerWiresShapeControl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(wiresManager.getDockingAcceptor()).thenReturn(IDockingAcceptor.ALL);
        when(wiresManager.getContainmentAcceptor()).thenReturn(IContainmentAcceptor.ALL);
        when(wiresManager.getLocationAcceptor()).thenReturn(ILocationAcceptor.ALL);
        when(wiresManager.getConnectionAcceptor()).thenReturn(IConnectionAcceptor.ALL);
        when(delegate.getParentPickerControl()).thenReturn(parentPickerControl);
        when(parentPickerControl.getShape()).thenReturn(shapeView);
        tested = new StunnerWiresShapeControl(delegate);
    }

    @Test
    public void testMoveViewsTopTop() {
        tested.onMoveStart(10d, 10d);
        verify(delegate,
               times(1)).onMoveStart(eq(10d),
                                     eq(10d));
        verify(shapeView,
               times(1)).moveToTop();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(delegate, times(1)).destroy();
    }
}
