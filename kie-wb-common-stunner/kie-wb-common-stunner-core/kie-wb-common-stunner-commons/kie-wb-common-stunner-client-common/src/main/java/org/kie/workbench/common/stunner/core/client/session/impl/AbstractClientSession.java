/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.util.UUID;

public abstract class AbstractClientSession implements ClientSession<AbstractCanvas, AbstractCanvasHandler> {

    private final transient AbstractCanvas canvas;
    private final transient AbstractCanvasHandler canvasHandler;
    private final transient String uuid;
    boolean isOpened;

    public AbstractClientSession(final AbstractCanvas canvas,
                                 final AbstractCanvasHandler canvasHandler) {
        this.uuid = UUID.uuid();
        this.canvas = canvas;
        this.canvasHandler = canvasHandler;
        this.isOpened = false;
    }

    protected abstract void doOpen();

    protected abstract void doPause();

    protected abstract void doResume();

    protected abstract void doDestroy();

    @Override
    public String getSessionUUID() {
        return canvasHandler.getUuid();
    }

    public void open() {
        doOpen();
        this.isOpened = true;
    }

    public void pause() {
        if (!isOpened) {
            throw new IllegalStateException("Session cannot be paused as it has been not opened yet.");
        }
        doPause();
    }

    public void resume() {
        if (isOpened) {
            doResume();
        }
    }

    public void destroy() {
        if (!isOpened) {
            throw new IllegalStateException("Session cannot be destroyed as it has been not opened.");
        }
        doDestroy();
        canvasHandler.destroy();
        isOpened = false;
    }

    @Override
    public AbstractCanvas getCanvas() {
        return canvas;
    }

    @Override
    public AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractClientSession)) {
            return false;
        }
        AbstractClientSession that = (AbstractClientSession) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid == null ? 0 : ~~uuid.hashCode();
    }

    public boolean isOpened() {
        return isOpened;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [uuid=" + uuid + "]";
    }
}
