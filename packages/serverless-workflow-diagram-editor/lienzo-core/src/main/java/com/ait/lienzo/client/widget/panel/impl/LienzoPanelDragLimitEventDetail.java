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

import java.util.Set;

import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.CustomEvent;
import elemental2.dom.Event;

public class LienzoPanelDragLimitEventDetail extends LienzoPanelEventDetail {

    public enum LimitDirections {
        LEFT,
        RIGHT,
        TOP,
        DOWN
    }

    private Set<LimitDirections> limitDirection;

    public static LienzoPanelDragLimitEventDetail getDragLimitDetail(Event event) {
        return (LienzoPanelDragLimitEventDetail) ((CustomEvent) event).detail;
    }

    public LienzoPanelDragLimitEventDetail(LienzoPanel panel, Set<LimitDirections> limitDirection) {
        super(panel);
        this.limitDirection = limitDirection;
    }

    public Set<LimitDirections> getLimitDirection() {
        return limitDirection;
    }
}
