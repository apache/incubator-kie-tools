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

package org.kie.workbench.common.stunner.core.client.shape.view.event;

import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;

public class NativeHandler {

    private final String eventType;
    private final EventListener eventListener;
    private final HTMLElement element;

    public NativeHandler(String eventType,
                         EventListener eventListener,
                         HTMLElement element) {
        this.eventType = eventType;
        this.eventListener = eventListener;
        this.element = element;
    }

    public NativeHandler add() {
        element.addEventListener(eventType, eventListener);
        return this;
    }

    public void removeHandler() {
        element.removeEventListener(eventType, eventListener);
    }

    public String getEventType() {
        return eventType;
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public HTMLElement getElement() {
        return element;
    }
}
