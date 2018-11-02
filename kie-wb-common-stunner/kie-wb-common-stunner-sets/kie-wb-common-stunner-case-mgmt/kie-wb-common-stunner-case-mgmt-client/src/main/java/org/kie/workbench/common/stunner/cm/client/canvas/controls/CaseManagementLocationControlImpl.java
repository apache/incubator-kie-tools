/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.client.canvas.controls;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LocationControlImpl;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.ShapeLocationsChangedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;

@Dependent
@CaseManagementEditor
public class CaseManagementLocationControlImpl extends LocationControlImpl {

    @Inject
    public CaseManagementLocationControlImpl(@CaseManagementEditor CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                             Event<ShapeLocationsChangedEvent> shapeLocationsChangedEvent,
                                             Event<CanvasSelectionEvent> selectionEvent) {
        super(canvasCommandFactory, shapeLocationsChangedEvent, selectionEvent);
    }
}
