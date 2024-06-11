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

import com.ait.lienzo.client.core.shape.wires.event.WiresConnectorPointsChangedEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragMoveEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveEvent;
import elemental2.dom.HTMLElement;

public class WiresEventHandlers {

    private HTMLElement relativeDiv;

    public final WiresDragStartEvent dragStartEvent;
    public final WiresDragMoveEvent dragMoveEvent;
    public final WiresDragEndEvent dragEndEvent;
    public final WiresMoveEvent wiresMoveEvent;

    public final WiresConnectorPointsChangedEvent wiresConnectorPointsChangedEvent;

    public WiresEventHandlers(final HTMLElement relativeDiv) {
        this.relativeDiv = relativeDiv;

        this.dragStartEvent = new WiresDragStartEvent(relativeDiv);
        this.dragMoveEvent = new WiresDragMoveEvent(relativeDiv);
        this.dragEndEvent = new WiresDragEndEvent(relativeDiv);
        this.wiresMoveEvent = new WiresMoveEvent(relativeDiv);

        this.wiresConnectorPointsChangedEvent = new WiresConnectorPointsChangedEvent(relativeDiv);
    }
}
