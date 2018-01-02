/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.session;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.Request;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;

@Dependent
@DMNEditor
public class DMNClientFullSession extends AbstractClientFullSession {

    private LocationControl<AbstractCanvasHandler, Element> locationControl;
    private ResizeControl<AbstractCanvasHandler, Element> resizeControl;
    private CanvasInPlaceTextEditorControl<AbstractCanvasHandler, AbstractClientFullSession, Element> canvasInPlaceTextEditorControl;
    private ToolboxControl<AbstractCanvasHandler, Element> toolboxControl;

    @Inject
    @SuppressWarnings("unchecked")
    public DMNClientFullSession(final @DMNEditor CanvasFactory<AbstractCanvas, AbstractCanvasHandler> factory,
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
              factory.newControl(KeyboardControl.class),
              factory.newControl(ClipboardControl.class));
        this.locationControl = factory.newControl(LocationControl.class);
        this.resizeControl = factory.newControl(ResizeControl.class);
        this.canvasInPlaceTextEditorControl = factory.newControl(CanvasInPlaceTextEditorControl.class);
        this.toolboxControl = factory.newControl(ToolboxControl.class);
        getRegistrationHandler().registerCanvasHandlerControl(locationControl);
        locationControl.setCommandManagerProvider(() -> requestCommandManager);
        getRegistrationHandler().registerCanvasHandlerControl(resizeControl);
        resizeControl.setCommandManagerProvider(() -> sessionCommandManager);
        getRegistrationHandler().registerCanvasHandlerControl(toolboxControl);
        getRegistrationHandler().registerCanvasHandlerControl(canvasInPlaceTextEditorControl);
        canvasInPlaceTextEditorControl.setCommandManagerProvider(() -> sessionCommandManager);
    }

    @Override
    protected void doPause() {
        getKeyboardControl().disable();
        super.doPause();
    }

    @Override
    protected void doResume() {
        getKeyboardControl().enable(getCanvas());
        super.doResume();
    }

    public LocationControl<AbstractCanvasHandler, Element> getLocationControl() {
        return locationControl;
    }

    public ResizeControl<AbstractCanvasHandler, Element> getResizeControl() {
        return resizeControl;
    }

    public ToolboxControl<AbstractCanvasHandler, Element> getToolboxControl() {
        return toolboxControl;
    }

    public CanvasInPlaceTextEditorControl<AbstractCanvasHandler, AbstractClientFullSession, Element> getCanvasInPlaceTextEditorControl() {
        return canvasInPlaceTextEditorControl;
    }
}
