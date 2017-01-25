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

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;

public interface Context<H extends CanvasHandler> {

    enum EventType {
        CLICK,
        MOUSE_ENTER,
        MOUSE_EXIT,
        MOUSE_DOWN;
    }

    /**
     * Returns the canvas handler instance for this context.
     */
    H getCanvasHandler();

    /**
     * Returns the canvas command manager instance assigned to the canvas toolbox control.
     */
    CanvasCommandManager<H> getCommandManager();

    /**
     * Returns the type of event that has been fired.
     */
    EventType getEventType();

    /**
     * Returns the X coordinate for the canvas.
     * It's relative to the current layer/viewport's transform, if any.
     */
    int getX();

    /**
     * Returns the Y coordinate for the canvas.
     * It's relative to the current layer/viewport's transform, if any.
     */
    int getY();

    /**
     * Returns the X coordinate for the canvas.
     * It's absolute, not relative to the current layer/viewport's transform, if any.
     */
    int getAbsoluteX();

    /**
     * Returns the Y coordinate for the canvas.
     * It's absolute, not relative to the current layer/viewport's transform, if any.
     */
    int getAbsoluteY();

    /**
     * Returns the X coordinate for the input human event.
     */
    int getClientX();

    /**
     * Returns the Y coordinate for the input human event.
     */
    int getClientY();
}
