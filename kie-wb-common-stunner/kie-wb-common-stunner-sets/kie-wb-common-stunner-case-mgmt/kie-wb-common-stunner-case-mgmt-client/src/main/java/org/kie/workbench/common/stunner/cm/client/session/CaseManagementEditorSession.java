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

package org.kie.workbench.common.stunner.cm.client.session;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.MultiLineTextEditorBox;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SingleSelection;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientCommandRegistry;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultEditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.mvp.Command;

@Dependent
@CaseManagementEditor
public class CaseManagementEditorSession
        extends DefaultEditorSession {

    @Inject
    public CaseManagementEditorSession(final ManagedSession session,
                                       final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                       final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                       final ClientCommandRegistry<org.kie.workbench.common.stunner.core.command.Command<AbstractCanvasHandler, CanvasViolation>> clientCommandRegistry) {
        super(session,
              canvasCommandManager,
              sessionCommandManager,
              clientCommandRegistry);
    }

    @Override
    public void init(final Metadata metadata,
                     final Command callback) {
        super.init(s -> s.registerCanvasControl(MediatorsControl.class)
                           .registerCanvasHandlerControl(SelectionControl.class,
                                                         SingleSelection.class)
                           .registerCanvasHandlerControl(ResizeControl.class)
                           .registerCanvasHandlerControl(ConnectionAcceptorControl.class)
                           .registerCanvasHandlerControl(ContainmentAcceptorControl.class)
                           .registerCanvasHandlerControl(DockingAcceptorControl.class)
                           .registerCanvasHandlerControl(CanvasInPlaceTextEditorControl.class,
                                                         MultiLineTextEditorBox.class)
                           .registerCanvasHandlerControl(LocationControl.class)
                           .registerCanvasHandlerControl(ElementBuilderControl.class,
                                                         Observer.class)
                           .registerCanvasHandlerControl(NodeBuilderControl.class)
                           .registerCanvasHandlerControl(EdgeBuilderControl.class)
                           .registerCanvasControl(KeyboardControl.class)
                           .registerCanvasControl(ClipboardControl.class)
                           .registerCanvasHandlerControl(ControlPointControl.class),
                   metadata,
                   callback);
    }
}
