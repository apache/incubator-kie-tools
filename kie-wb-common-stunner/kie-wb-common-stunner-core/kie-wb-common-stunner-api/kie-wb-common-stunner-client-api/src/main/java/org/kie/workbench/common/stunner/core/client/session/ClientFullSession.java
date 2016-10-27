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

package org.kie.workbench.common.stunner.core.client.session;

import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasNameEditionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasValidationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.palette.CanvasPaletteControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * A session that provides controls which can potentially update or modify the session's diagram structure/metadata.
 * @param <C> The canvas.
 * @param <H> The canvas handler.
 */
public interface ClientFullSession<C extends Canvas, H extends CanvasHandler>
        extends ClientReadOnlySession<C, H> {

    ResizeControl<H, Element> getResizeControl();

    CanvasValidationControl<H> getCanvasValidationControl();

    CanvasPaletteControl<H> getCanvasPaletteControl();

    CanvasCommandManager<H> getCanvasCommandManager();

    ConnectionAcceptorControl<H> getConnectionAcceptorControl();

    ContainmentAcceptorControl<H> getContainmentAcceptorControl();

    DockingAcceptorControl<H> getDockingAcceptorControl();

    CanvasNameEditionControl<H, Element> getCanvasNameEditionControl();

    DragControl<H, Element> getDragControl();

    ToolboxControl<H, Element> getToolboxControl();

    ElementBuilderControl<H> getBuilderControl();

}
