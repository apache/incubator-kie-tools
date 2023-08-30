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

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class StunnerWiresHandlerFactoryTest {

    private StunnerWiresHandlerFactory tested;

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresConnector connector;

    @Mock
    private WiresShapeHighlight<PickerPart.ShapePart> highlight;

    @Mock
    private WiresShape shape;

    @Mock
    private WiresHandlerFactory delegate;

    @Before
    public void setUp() throws Exception {
        tested = new StunnerWiresHandlerFactory(delegate);
    }

    @Test
    public void newConnectorHandler() {
        WiresConnectorHandler wiresConnectorHandler = tested.newConnectorHandler(connector, wiresManager);
        verify(delegate).newConnectorHandler(connector, wiresManager);
    }

    @Test
    public void newControlPointHandler() {
        tested.newControlPointHandler(connector, wiresManager);
        verify(delegate).newControlPointHandler(connector, wiresManager);
    }

    @Test
    public void newShapeHandler() {
        tested.newShapeHandler(shape, highlight, wiresManager);
        verify(delegate).newShapeHandler(shape, highlight, wiresManager);
    }
}