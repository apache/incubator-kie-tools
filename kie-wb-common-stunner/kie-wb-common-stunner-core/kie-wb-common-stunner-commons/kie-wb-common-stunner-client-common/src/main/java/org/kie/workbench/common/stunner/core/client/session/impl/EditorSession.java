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

package org.kie.workbench.common.stunner.core.client.session.impl;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;

public abstract class EditorSession
        extends AbstractSession<AbstractCanvas, AbstractCanvasHandler> {

    public EditorSession() {
        super();
    }

    public abstract CanvasCommandManager<AbstractCanvasHandler> getCommandManager();

    public abstract CommandRegistry<org.kie.workbench.common.stunner.core.command.Command<AbstractCanvasHandler, CanvasViolation>> getCommandRegistry();

    public abstract ZoomControl<AbstractCanvas> getZoomControl();

    public abstract PanControl<AbstractCanvas> getPanControl();

    public abstract KeyboardControl<AbstractCanvas, ClientSession> getKeyboardControl();

    public abstract ClipboardControl<Element, AbstractCanvas, ClientSession> getClipboardControl();

    public abstract SelectionControl<AbstractCanvasHandler, Element> getSelectionControl();

    public abstract ConnectionAcceptorControl<AbstractCanvasHandler> getConnectionAcceptorControl();

    public abstract ContainmentAcceptorControl<AbstractCanvasHandler> getContainmentAcceptorControl();

    public abstract DockingAcceptorControl<AbstractCanvasHandler> getDockingAcceptorControl();
}
