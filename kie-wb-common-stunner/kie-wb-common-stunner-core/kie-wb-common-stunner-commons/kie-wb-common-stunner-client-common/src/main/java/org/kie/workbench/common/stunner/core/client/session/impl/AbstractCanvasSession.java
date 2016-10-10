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

package org.kie.workbench.common.stunner.core.client.session.impl;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.util.UUID;

public abstract class AbstractCanvasSession implements DefaultCanvasSession {

    protected final transient AbstractCanvas canvas;
    protected final transient AbstractCanvasHandler canvasHandler;
    private final transient String uuid;

    public AbstractCanvasSession( final AbstractCanvas canvas,
                                  final AbstractCanvasHandler canvasHandler ) {
        this.uuid = UUID.uuid();
        this.canvas = canvas;
        this.canvasHandler = canvasHandler;
    }

    protected abstract void doDispose();

    @Override
    public AbstractCanvas getCanvas() {
        return canvas;
    }

    @Override
    public AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    @Override
    public void onOpen() {
    }

    public void onDispose() {
        doDispose();
        canvasHandler.destroy();
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AbstractCanvasSession ) ) {
            return false;
        }
        AbstractCanvasSession that = ( AbstractCanvasSession ) o;
        return uuid.equals( that.uuid );
    }

    @Override
    public String toString() {
        return "AbstractCanvasSession [uuid=" + uuid + "]";
    }
}
