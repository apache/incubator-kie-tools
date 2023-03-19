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

import org.appformer.client.stateControl.registry.Registry;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AlertsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LineSpliceAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;

public abstract class EditorSession
        extends AbstractSession<AbstractCanvas, AbstractCanvasHandler> {

    public EditorSession() {
        super();
    }

    public abstract Registry<Command<AbstractCanvasHandler, CanvasViolation>> getCommandRegistry();

    public abstract Registry<Command<AbstractCanvasHandler, CanvasViolation>> getRedoCommandRegistry();

    public abstract MediatorsControl<AbstractCanvas> getMediatorsControl();

    public abstract AlertsControl<AbstractCanvas> getAlertsControl();

    public abstract KeyboardControl<AbstractCanvas, ClientSession> getKeyboardControl();

    public abstract ClipboardControl<Element, AbstractCanvas, ClientSession> getClipboardControl();

    public abstract ConnectionAcceptorControl<AbstractCanvasHandler> getConnectionAcceptorControl();

    public abstract ContainmentAcceptorControl<AbstractCanvasHandler> getContainmentAcceptorControl();

    public abstract DockingAcceptorControl<AbstractCanvasHandler> getDockingAcceptorControl();

    public abstract LineSpliceAcceptorControl<AbstractCanvasHandler> getLineSpliceAcceptorControl();
}
