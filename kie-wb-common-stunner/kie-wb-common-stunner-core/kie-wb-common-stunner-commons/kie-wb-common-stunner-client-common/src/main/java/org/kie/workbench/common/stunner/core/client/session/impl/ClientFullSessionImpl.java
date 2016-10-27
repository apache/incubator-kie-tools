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

import org.kie.workbench.common.stunner.core.client.api.platform.Desktop;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasNameEditionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasValidationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.palette.CanvasPaletteControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.Wheel;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class ClientFullSessionImpl extends AbstractClientFullSession {

    @Inject
    public ClientFullSessionImpl( final AbstractCanvas canvas,
                                  final AbstractCanvasHandler canvasHandler,
                                  final ResizeControl<AbstractCanvasHandler, Element> resizeControl,
                                  final CanvasValidationControl<AbstractCanvasHandler> canvasValidationControl,
                                  final CanvasPaletteControl<AbstractCanvasHandler> canvasPaletteControl,
                                  final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                  final ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl,
                                  final ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl,
                                  final DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl,
                                  final CanvasNameEditionControl<AbstractCanvasHandler, Element> canvasNameEditionControl,
                                  final @Desktop SelectionControl<AbstractCanvasHandler, Element> selectionControl,
                                  final DragControl<AbstractCanvasHandler, Element> dragControl,
                                  final ToolboxControl<AbstractCanvasHandler, Element> toolboxControl,
                                  final @Observer ElementBuilderControl<AbstractCanvasHandler> builderControl,
                                  final @Wheel ZoomControl<AbstractCanvas> zoomControl,
                                  final PanControl<AbstractCanvas> panControl ) {
        super( canvas, canvasHandler, resizeControl, canvasValidationControl, canvasPaletteControl,
                selectionControl, zoomControl, panControl, canvasCommandManager, connectionAcceptorControl,
                containmentAcceptorControl, dockingAcceptorControl, canvasNameEditionControl, dragControl,
                toolboxControl, builderControl );

    }

}
