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


package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.CustomEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

public class LienzoPanelPrimitiveDragEventDetailTest {

    private final double DRAG_X = 1.0;
    private final double DRAG_Y = 2.0;
    private final String CUSTOM_EVENT_TYPE = "LIENZO EVENT TYPE";

    @Mock
    private LienzoPanel<? extends LienzoPanel> lienzoPanel;

    @Mock
    private IPrimitive<?> primitive;

    private LienzoPanelPrimitiveDragEventDetail tested;

    @Before
    public void setup() {
        tested = new LienzoPanelPrimitiveDragEventDetail(lienzoPanel,
                                                         primitive,
                                                         DRAG_X,
                                                         DRAG_Y);
    }

    @Test
    public void testGetDragDetail() {
        CustomEvent<?> customEvent = createCustomEvent();
        assertEquals(tested, LienzoPanelPrimitiveDragEventDetail.getDragDetail(customEvent));
    }

    @Test
    public void testGetLienzoPanel() {
        assertEquals(lienzoPanel, tested.getLienzoPanel());
    }

    @Test
    public void testGetPrimitive() {
        assertEquals(primitive, tested.getPrimitive());
    }

    @Test
    public void testGetDragX() {
        assertEquals(DRAG_X, tested.getDragX(), Double.MIN_NORMAL);
    }

    @Test
    public void testGetDragY() {
        assertEquals(DRAG_Y, tested.getDragY(), Double.MIN_NORMAL);
    }

    private CustomEvent<?> createCustomEvent() {
        CustomEvent event = new CustomEvent(CUSTOM_EVENT_TYPE);
        event.detail = tested;
        return event;
    }
}