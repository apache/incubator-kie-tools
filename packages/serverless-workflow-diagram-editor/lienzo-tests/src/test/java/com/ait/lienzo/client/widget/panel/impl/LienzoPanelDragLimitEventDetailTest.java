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

import java.util.EnumSet;

import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelDragLimitEventDetail.LimitDirections;
import elemental2.dom.CustomEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

public class LienzoPanelDragLimitEventDetailTest {

    private final EnumSet<LimitDirections> LIMIT_DIRECTIONS = EnumSet.allOf(LimitDirections.class);
    private final String CUSTOM_EVENT_TYPE = "LIENZO EVENT TYPE";

    @Mock
    private LienzoPanel<? extends LienzoPanel> lienzoPanel;

    private LienzoPanelDragLimitEventDetail tested;

    @Before
    public void setup() {
        tested = new LienzoPanelDragLimitEventDetail(lienzoPanel,
                                                     LIMIT_DIRECTIONS);
    }

    @Test
    public void testGetDragDetail() {
        CustomEvent<?> customEvent = createCustomEvent();
        assertEquals(tested, LienzoPanelDragLimitEventDetail.getDragLimitDetail(customEvent));
    }

    @Test
    public void testGetLienzoPanel() {
        assertEquals(lienzoPanel, tested.getLienzoPanel());
    }

    @Test
    public void testGetLimitDirection() {
        assertEquals(LIMIT_DIRECTIONS, tested.getLimitDirection());
    }

    private CustomEvent<?> createCustomEvent() {
        CustomEvent event = new CustomEvent(CUSTOM_EVENT_TYPE);
        event.detail = tested;
        return event;
    }
}
