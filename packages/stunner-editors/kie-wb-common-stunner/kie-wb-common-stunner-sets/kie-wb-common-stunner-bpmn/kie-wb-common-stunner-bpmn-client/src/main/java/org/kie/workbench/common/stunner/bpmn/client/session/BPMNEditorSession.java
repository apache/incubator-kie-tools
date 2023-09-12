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


package org.kie.workbench.common.stunner.bpmn.client.session;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.appformer.client.stateControl.registry.DefaultRegistry;
import org.appformer.client.stateControl.registry.Registry;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasInlineTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LineSpliceAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultEditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.mvp.Command;

@Dependent
@BPMN
public class BPMNEditorSession extends DefaultEditorSession {

    @Inject
    public BPMNEditorSession(final ManagedSession session,
                             final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                             final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                             final Registry<org.kie.workbench.common.stunner.core.command.Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry,
                             final DefaultRegistry<org.kie.workbench.common.stunner.core.command.Command<AbstractCanvasHandler, CanvasViolation>> redoCommandRegistry,
                             final Event<RegisterChangedEvent> registerChangedEvent) {
        super(session,
              canvasCommandManager,
              sessionCommandManager,
              commandRegistry,
              redoCommandRegistry,
              registerChangedEvent);
    }

    @Override
    public ManagedSession getSession() {
        return super.getSession();
    }

    @Override
    public void init(final Metadata metadata,
                     final Command callback) {

        init(s -> s.registerCanvasControl(MediatorsControl.class)
                     .registerCanvasHandlerControl(SelectionControl.class,
                                                   MultipleSelection.class)
                     .registerCanvasHandlerControl(ResizeControl.class)
                     .registerCanvasHandlerControl(ConnectionAcceptorControl.class)
                     .registerCanvasHandlerControl(ContainmentAcceptorControl.class)
                     .registerCanvasHandlerControl(DockingAcceptorControl.class)
                     .registerCanvasHandlerControl(LineSpliceAcceptorControl.class)
                     .registerCanvasHandlerControl(CanvasInlineTextEditorControl.class)
                     .registerCanvasHandlerControl(LocationControl.class)
                     .registerCanvasHandlerControl(ToolboxControl.class)
                     .registerCanvasHandlerControl(ElementBuilderControl.class,
                                                   Observer.class)
                     .registerCanvasHandlerControl(NodeBuilderControl.class)
                     .registerCanvasHandlerControl(EdgeBuilderControl.class)
                     .registerCanvasHandlerControl(BPMNCanvasShortcutsControl.class)
                     .registerCanvasControl(KeyboardControl.class)
                     .registerCanvasControl(ClipboardControl.class)
                     .registerCanvasHandlerControl(ControlPointControl.class),
             metadata,
             callback);
    }
}
