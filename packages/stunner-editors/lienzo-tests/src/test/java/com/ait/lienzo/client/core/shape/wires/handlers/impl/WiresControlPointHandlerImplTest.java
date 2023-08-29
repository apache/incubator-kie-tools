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

import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresControlPointHandlerImplTest {

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresConnector m_connector;

    @Mock
    private IDirectionalMultiPointShape line;

    @Mock
    private WiresConnectorControl m_connectorControl;

    private WiresControlPointHandlerImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(m_connector.getControl()).thenReturn(m_connectorControl);
        when(m_connector.getLine()).thenReturn(line);
        tested = new WiresControlPointHandlerImpl(m_connector, wiresManager);
    }

    @Test
    public void testOnNodeDragMove() {

        elemental2.dom.HTMLElement element = mock(HTMLElement.class);

        NodeDragMoveEvent event = spy(new NodeDragMoveEvent(element));
        when(event.getX()).thenReturn(2);
        when(event.getY()).thenReturn(7);
        Line primitive = spy(new Line());
        when(primitive.getX()).thenReturn(12d);
        when(primitive.getY()).thenReturn(17d);
        when(event.getSource()).thenReturn(primitive);
        tested.onNodeDragMove(event);
        verify(line, times(1)).adjustPoint(eq(12d),
                                           eq(17d),
                                           eq(2d),
                                           eq(7d));
    }
}
