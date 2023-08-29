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


package org.kie.workbench.common.stunner.client.widgets.notification;

public final class NotificationContext {

    private final String canvasHandlerUUID;
    private final String diagramName;
    private final String diagramTitle;

    NotificationContext(final String canvasHandlerUUID,
                        final String diagramName,
                        final String diagramTitle) {
        this.canvasHandlerUUID = canvasHandlerUUID;
        this.diagramName = diagramName;
        this.diagramTitle = diagramTitle;
    }

    public String getCanvasHandlerUUID() {
        return canvasHandlerUUID;
    }

    public String getDiagramName() {
        return diagramName;
    }

    public String getDiagramTitle() {
        return diagramTitle;
    }

    @Override
    public String toString() {
        return "Canvas [diagram='" + diagramTitle + "' (" + diagramName + "), " +
                "canvasHandler=" + canvasHandlerUUID + "]";
    }

    public static class Builder {

        public static NotificationContext build(final String uuid,
                                                final String diagramName,
                                                final String diagramTitle) {
            return new NotificationContext(uuid,
                                           diagramName,
                                           diagramTitle);
        }
    }
}
