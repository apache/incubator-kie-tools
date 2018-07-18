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

import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresControlPointHandlerImplTest {

    @Mock
    private WiresConnector m_connector;

    @Mock
    private WiresConnectorControl m_connectorControl;

    private WiresControlPointHandlerImpl tested;

    @Before
    public void setup() {
        tested = new WiresControlPointHandlerImpl(m_connector, m_connectorControl);
    }

    @Test
    public void testOnNodeDragMove() {
        NodeDragMoveEvent event = mock(NodeDragMoveEvent.class);
        when(event.getX()).thenReturn(2);
        when(event.getY()).thenReturn(7);
        IPrimitive<?> primitive = mock(IPrimitive.class);
        when(primitive.getX()).thenReturn(12d);
        when(primitive.getY()).thenReturn(17d);
        when(event.getSource()).thenReturn(primitive);
        tested.onNodeDragMove(event);
        verify(m_connectorControl, times(1)).adjustControlPointAt(eq(12d),
                                                                  eq(17d),
                                                                  eq(2d),
                                                                  eq(7d));
        Point2D point = new Point2D(0.1d, 0.3d);
        when(m_connectorControl.adjustControlPointAt(anyDouble(),
                                                anyDouble(),
                                                anyDouble(),
                                                anyDouble()))
                .thenReturn(point);
        tested.onNodeDragMove(event);
        verify(primitive, times(1)).setX(eq(0.1d));
        verify(primitive, times(1)).setY(eq(0.3d));
    }
}
