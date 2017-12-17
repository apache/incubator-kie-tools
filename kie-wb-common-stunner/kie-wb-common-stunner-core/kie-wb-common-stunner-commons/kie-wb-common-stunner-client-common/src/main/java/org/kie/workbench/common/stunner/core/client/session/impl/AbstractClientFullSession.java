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

package org.kie.workbench.common.stunner.core.client.session.impl;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
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
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;

public abstract class AbstractClientFullSession extends AbstractClientReadOnlySession
        implements ClientFullSession<AbstractCanvas, AbstractCanvasHandler, ClientSession> {

    private CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    private CommandRegistry<Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry;
    private ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl;
    private ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl;
    private DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl;
    private ElementBuilderControl<AbstractCanvasHandler> builderControl;
    private KeyboardControl<AbstractCanvas, ClientSession> keyboardControl;
    private ClipboardControl<Element, AbstractCanvas, ClientSession> clipboardControl;

    public AbstractClientFullSession(final AbstractCanvas canvas,
                                     final AbstractCanvasHandler canvasHandler,
                                     final SelectionControl<AbstractCanvasHandler, Element> selectionControl,
                                     final ZoomControl<AbstractCanvas> zoomControl,
                                     final PanControl<AbstractCanvas> panControl,
                                     final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                     final RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> sessionCommandManagerProvider,
                                     final RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> requestCommandManagerProvider,
                                     final CommandRegistry<Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry,
                                     final ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl,
                                     final ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl,
                                     final DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl,
                                     final ElementBuilderControl<AbstractCanvasHandler> builderControl,
                                     final KeyboardControl<AbstractCanvas, ClientSession> keyboardControl,
                                     final ClipboardControl<Element, AbstractCanvas, ClientSession> clipboardControl) {
        super(canvas,
              canvasHandler,
              selectionControl,
              zoomControl,
              panControl);
        this.canvasCommandManager = canvasCommandManager;
        this.commandRegistry = commandRegistry;
        this.connectionAcceptorControl = connectionAcceptorControl;
        this.containmentAcceptorControl = containmentAcceptorControl;
        this.dockingAcceptorControl = dockingAcceptorControl;
        this.builderControl = builderControl;
        this.keyboardControl = keyboardControl;
        this.clipboardControl = clipboardControl;
        getRegistrationHandler().registerCanvasHandlerControl(connectionAcceptorControl);
        connectionAcceptorControl.setCommandManagerProvider(requestCommandManagerProvider);
        getRegistrationHandler().registerCanvasHandlerControl(containmentAcceptorControl);
        containmentAcceptorControl.setCommandManagerProvider(requestCommandManagerProvider);
        getRegistrationHandler().registerCanvasHandlerControl(dockingAcceptorControl);
        dockingAcceptorControl.setCommandManagerProvider(requestCommandManagerProvider);
        getRegistrationHandler().registerCanvasHandlerControl(builderControl);
        builderControl.setCommandManagerProvider(sessionCommandManagerProvider);
        getRegistrationHandler().registerCanvasControl(keyboardControl);
    }

    @Override
    protected void doOpen() {
        super.doOpen();
        getRegistrationHandler().bind(this);
    }

    @Override
    public void doDestroy() {
        super.doDestroy();
        getRegistrationHandler().unbind();
        if (null != commandRegistry) {
            commandRegistry.clear();
        }
    }

    @Override
    public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
        return selectionControl;
    }

    @Override
    public ZoomControl<AbstractCanvas> getZoomControl() {
        return zoomControl;
    }

    @Override
    public PanControl<AbstractCanvas> getPanControl() {
        return panControl;
    }

    @Override
    public CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return canvasCommandManager;
    }

    @Override
    public CommandRegistry<Command<AbstractCanvasHandler, CanvasViolation>> getCommandRegistry() {
        return commandRegistry;
    }

    @Override
    public ConnectionAcceptorControl<AbstractCanvasHandler> getConnectionAcceptorControl() {
        return connectionAcceptorControl;
    }

    @Override
    public ContainmentAcceptorControl<AbstractCanvasHandler> getContainmentAcceptorControl() {
        return containmentAcceptorControl;
    }

    @Override
    public DockingAcceptorControl<AbstractCanvasHandler> getDockingAcceptorControl() {
        return dockingAcceptorControl;
    }

    @Override
    public ElementBuilderControl<AbstractCanvasHandler> getBuilderControl() {
        return builderControl;
    }

    @Override
    public KeyboardControl<AbstractCanvas, ClientSession> getKeyboardControl() {
        return keyboardControl;
    }

    @Override
    public ClipboardControl<Element, AbstractCanvas, ClientSession> getClipboardControl() {
        return clipboardControl;
    }
}
