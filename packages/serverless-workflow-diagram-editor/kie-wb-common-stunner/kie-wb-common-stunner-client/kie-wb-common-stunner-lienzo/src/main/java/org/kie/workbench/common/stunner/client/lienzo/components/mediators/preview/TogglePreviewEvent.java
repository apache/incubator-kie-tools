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


package org.kie.workbench.common.stunner.client.lienzo.components.mediators.preview;

public class TogglePreviewEvent {

    public enum EventType {
        TOGGLE,
        HIDE,
        RESIZE,
        SHOW
    }

    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final EventType eventType;

    public TogglePreviewEvent(EventType eventType) {
        this(0, 0, 0, 0, eventType);
    }

    public TogglePreviewEvent(final double x,
                              final double y,
                              final double width,
                              final double height,
                              EventType eventType) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.eventType = eventType;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public EventType getEventType() {
        return eventType;
    }
}
