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

package org.kie.workbench.common.stunner.client.widgets.notification.canvas;

public final class CanvasNotificationContext {

    private final String canvasUUID;
    private final String diagramUUID;
    private final String diagramTitle;

    public CanvasNotificationContext( final String canvasUUID,
                                      final String diagramUUID,
                                      final String diagramTitle ) {
        this.canvasUUID = canvasUUID;
        this.diagramUUID = diagramUUID;
        this.diagramTitle = diagramTitle;
    }

    public String getCanvasUUID() {
        return canvasUUID;
    }

    public String getDiagramUUID() {
        return diagramUUID;
    }

    public String getDiagramTitle() {
        return diagramTitle;
    }

    @Override
    public String toString() {
        return "[Diagram='" + diagramTitle + "' (" + diagramUUID + "), Canvas=" + canvasUUID + "]";
    }
}
