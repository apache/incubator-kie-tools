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

package com.ait.lienzo.tools.client.event;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class HandlerManagerTest {

    @Mock
    private NodeMouseClickHandler nodeMouseClickHandler;

    @Mock
    private NodeMouseClickHandler nodeMouseClickHandler2;

    @Mock
    private NodeMouseClickHandler nodeMouseClickHandler3;

    @Mock
    private NodeMouseClickHandler nodeMouseClickHandler4;

    private IPrimitive<?> node;

    private HandlerManager tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        node = new Rectangle(10, 10);
        tested = new HandlerManager(node);
    }

    @Test
    public void testFireEvent() {
        final HTMLElement element = mock(HTMLElement.class);
        final NodeMouseClickEvent nodeMouseClickEvent = spy(new NodeMouseClickEvent(element));
        final HTMLElement element2 = mock(HTMLElement.class);
        final NodeMouseClickEvent nodeMouseClickEvent2 = spy(new NodeMouseClickEvent(element2));
        final HTMLElement element3 = mock(HTMLElement.class);
        final NodeMouseClickEvent nodeMouseClickEvent3 = spy(new NodeMouseClickEvent(element3));
        final HTMLElement element4 = mock(HTMLElement.class);
        final NodeMouseClickEvent nodeMouseClickEvent4 = spy(new NodeMouseClickEvent(element4));

        for (int i = 0; i < 10; i++) {
            tested.addHandler(NodeMouseClickEvent.getType(), nodeMouseClickHandler);
            tested.addHandler(NodeMouseClickEvent.getType(), nodeMouseClickHandler2);
            tested.addHandler(NodeMouseClickEvent.getType(), nodeMouseClickHandler3);
            tested.addHandler(NodeMouseClickEvent.getType(), nodeMouseClickHandler4);

            tested.fireEvent(nodeMouseClickEvent);
            tested.fireEvent(nodeMouseClickEvent2);
            tested.fireEvent(nodeMouseClickEvent3);
            tested.fireEvent(nodeMouseClickEvent4);

            // clear the event handlers
            tested.map = null;

            // No event must be dispatched
            tested.fireEvent(nodeMouseClickEvent);
            tested.fireEvent(nodeMouseClickEvent2);
            tested.fireEvent(nodeMouseClickEvent3);
            tested.fireEvent(nodeMouseClickEvent4);
        }

        verify(nodeMouseClickEvent, times(10)).dispatch(nodeMouseClickHandler);
        verify(nodeMouseClickEvent2, times(10)).dispatch(nodeMouseClickHandler2);
        verify(nodeMouseClickEvent3, times(10)).dispatch(nodeMouseClickHandler3);
        verify(nodeMouseClickEvent4, times(10)).dispatch(nodeMouseClickHandler4);
    }
}
