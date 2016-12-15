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

package org.kie.workbench.common.stunner.core.client.canvas.controls;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;

public abstract class AbstractCanvasHandlerControl implements CanvasControl<AbstractCanvasHandler> {

    protected AbstractCanvasHandler canvasHandler;

    protected abstract void doDisable();

    @Override
    public void enable( final AbstractCanvasHandler canvasHandler ) {
        this.canvasHandler = canvasHandler;
    }

    @Override
    public void disable() {
        doDisable();
        this.canvasHandler = null;
    }

    protected boolean isEnabled() {
        return canvasHandler != null;
    }

}
