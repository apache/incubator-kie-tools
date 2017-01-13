/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;

public class ContextImpl implements Context {

    private final AbstractCanvasHandler canvasHandler;
    private final EventType eventType;
    private final int x;
    private final int y;
    private final int absX;
    private final int absY;
    private final int clientX;
    private final int clientY;

    public ContextImpl(final AbstractCanvasHandler canvasHandler,
                       final EventType eventType,
                       final int x,
                       final int y,
                       final int absX,
                       final int absY,
                       final int clientX,
                       final int clientY) {
        this.canvasHandler = canvasHandler;
        this.eventType = eventType;
        this.x = x;
        this.y = y;
        this.absX = absX;
        this.absY = absY;
        this.clientX = clientX;
        this.clientY = clientY;
    }

    @Override
    public AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getAbsoluteX() {
        return absX;
    }

    @Override
    public int getAbsoluteY() {
        return absY;
    }

    @Override
    public int getClientX() {
        return clientX;
    }

    @Override
    public int getClientY() {
        return clientY;
    }
}
