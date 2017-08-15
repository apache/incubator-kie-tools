/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.Request;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;

@Dependent
@CaseManagementEditor
public class CaseManagementClientFullSession extends AbstractClientFullSession {

    private CanvasInPlaceTextEditorControl<AbstractCanvasHandler, ClientSession, Element> canvasInPlaceTextEditorControl;

    @Inject
    @SuppressWarnings("unchecked")
    public CaseManagementClientFullSession(final @CaseManagementEditor CanvasFactory<AbstractCanvas, AbstractCanvasHandler> factory,
                                           final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                           final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                           final @Request SessionCommandManager<AbstractCanvasHandler> requestCommandManager,
                                           final RegistryFactory registryFactory) {
        super(factory.newCanvas(),
              factory.newCanvasHandler(),
              factory.newControl(SelectionControl.class),
              factory.newControl(ZoomControl.class),
              factory.newControl(PanControl.class),
              canvasCommandManager,
              () -> sessionCommandManager,
              () -> requestCommandManager,
              registryFactory.newCommandRegistry(),
              factory.newControl(ConnectionAcceptorControl.class),
              factory.newControl(ContainmentAcceptorControl.class),
              factory.newControl(DockingAcceptorControl.class),
              factory.newControl(ElementBuilderControl.class),
              factory.newControl(KeyboardControl.class));

        this.canvasInPlaceTextEditorControl = factory.newControl(CanvasInPlaceTextEditorControl.class);
        getRegistrationHandler().registerCanvasHandlerControl(canvasInPlaceTextEditorControl);
        canvasInPlaceTextEditorControl.setCommandManagerProvider(() -> sessionCommandManager);
    }

    CanvasInPlaceTextEditorControl<AbstractCanvasHandler, ClientSession, Element> getCanvasInPlaceTextEditorControl() {
        return canvasInPlaceTextEditorControl;
    }
}
