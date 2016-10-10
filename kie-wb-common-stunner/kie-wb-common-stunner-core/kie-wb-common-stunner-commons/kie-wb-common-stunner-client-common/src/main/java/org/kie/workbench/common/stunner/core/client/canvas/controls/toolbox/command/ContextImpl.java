/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
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
    private final Event event;
    private final double x;
    private final double y;
    private final double clientX;
    private final double clientY;

    public ContextImpl( final AbstractCanvasHandler canvasHandler,
                        final Event event,
                        final double x,
                        final double y,
                        final double clientX,
                        final double clientY ) {
        this.canvasHandler = canvasHandler;
        this.event = event;
        this.x = x;
        this.y = y;
        this.clientX = clientX;
        this.clientY = clientY;
    }

    @Override
    public AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getClientX() {
        return clientX;
    }

    @Override
    public double getClientY() {
        return clientY;
    }

}
