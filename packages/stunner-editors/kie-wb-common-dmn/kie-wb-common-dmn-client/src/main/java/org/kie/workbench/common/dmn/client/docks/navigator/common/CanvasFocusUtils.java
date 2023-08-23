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

package org.kie.workbench.common.dmn.client.docks.navigator.common;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;

@ApplicationScoped
public class CanvasFocusUtils {

    private final DMNGraphUtils dmnGraphUtils;

    private final Event<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent;

    private final Event<CanvasSelectionEvent> canvasSelectionEvent;

    @Inject
    public CanvasFocusUtils(final DMNGraphUtils dmnGraphUtils,
                            final Event<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent,
                            final Event<CanvasSelectionEvent> canvasSelectionEvent) {
        this.dmnGraphUtils = dmnGraphUtils;
        this.canvasFocusedSelectionEvent = canvasFocusedSelectionEvent;
        this.canvasSelectionEvent = canvasSelectionEvent;
    }

    public void focus(final String nodeUUID) {

        final CanvasHandler canvasHandler = dmnGraphUtils.getCanvasHandler();

        canvasSelectionEvent.fire(makeCanvasSelectionEvent(canvasHandler, nodeUUID));
        canvasFocusedSelectionEvent.fire(makeCanvasFocusedShapeEvent(canvasHandler, nodeUUID));

        if (canvasHandler != null && canvasHandler.getCanvas() != null) {
            canvasHandler.getCanvas().focus();
        }
    }

    CanvasSelectionEvent makeCanvasSelectionEvent(final CanvasHandler canvas,
                                                  final String uuid) {
        return new CanvasSelectionEvent(canvas, uuid);
    }

    CanvasFocusedShapeEvent makeCanvasFocusedShapeEvent(final CanvasHandler canvas,
                                                        final String uuid) {
        return new CanvasFocusedShapeEvent(canvas, uuid);
    }
}
