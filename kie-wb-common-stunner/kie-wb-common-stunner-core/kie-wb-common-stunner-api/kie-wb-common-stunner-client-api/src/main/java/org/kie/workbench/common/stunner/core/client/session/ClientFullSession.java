/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.session;

import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasValidationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;

/**
 * A session that provides controls which can potentially update or modify the session's diagram structure/metadata.
 * Any full session instance must provide at least the following controls.
 * Implementation can provide additional controls.
 * @param <C> The canvas.
 * @param <H> The canvas handler.
 */
public interface ClientFullSession<C extends Canvas, H extends CanvasHandler>
        extends ClientReadOnlySession<C, H> {

    CanvasValidationControl<H> getValidationControl();

    CanvasCommandManager<H> getCommandManager();

    CommandRegistry<Command<H, CanvasViolation>> getCommandRegistry();

    ConnectionAcceptorControl<H> getConnectionAcceptorControl();

    ContainmentAcceptorControl<H> getContainmentAcceptorControl();

    DockingAcceptorControl<H> getDockingAcceptorControl();

    DragControl<H, Element> getDragControl();

    ElementBuilderControl<H> getBuilderControl();
}
