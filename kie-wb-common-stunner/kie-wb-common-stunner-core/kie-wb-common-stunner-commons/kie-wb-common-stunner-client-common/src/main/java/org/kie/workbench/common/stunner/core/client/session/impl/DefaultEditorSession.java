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

import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.MultiLineTextEditorBox;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.AbstractCanvasShortcutsControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientCommandRegistry;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.uberfire.mvp.Command;

@Dependent
public class DefaultEditorSession
        extends EditorSession {

    private final ManagedSession session;
    private final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final ClientCommandRegistry<org.kie.workbench.common.stunner.core.command.Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry;

    @Inject
    public DefaultEditorSession(final ManagedSession session,
                                final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                final ClientCommandRegistry<org.kie.workbench.common.stunner.core.command.Command<AbstractCanvasHandler, CanvasViolation>> clientCommandRegistry) {
        this.session = session;
        this.commandRegistry = clientCommandRegistry;
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandManager = canvasCommandManager;
    }

    @PostConstruct
    public void constructInstance() {
        session.onCanvasControlRegistered(this::onControlRegistered)
                .onCanvasHandlerControlRegistered(this::onCanvasHandlerControlRegistered)
                .onCanvasControlDestroyed(AbstractSession::onControlDestroyed)
                .onCanvasHandlerControlDestroyed(AbstractSession::onControlDestroyed);
        commandRegistry.setSession(getSession());
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
                     .registerCanvasHandlerControl(CanvasInPlaceTextEditorControl.class,
                                                   MultiLineTextEditorBox.class)
                     .registerCanvasHandlerControl(LocationControl.class)
                     .registerCanvasHandlerControl(ToolboxControl.class)
                     .registerCanvasHandlerControl(ElementBuilderControl.class,
                                                   Observer.class)
                     .registerCanvasHandlerControl(NodeBuilderControl.class)
                     .registerCanvasHandlerControl(EdgeBuilderControl.class)
                     .registerCanvasHandlerControl(AbstractCanvasShortcutsControlImpl.class)
                     .registerCanvasControl(KeyboardControl.class)
                     .registerCanvasControl(ClipboardControl.class)
                     .registerCanvasHandlerControl(ControlPointControl.class),
             metadata,
             callback);
    }

    public void init(final Consumer<ManagedSession> sessionControls,
                     final Metadata metadata,
                     final Command callback) {
        sessionControls.accept(session);
        session.init(metadata,
                     callback);
    }

    @Override
    public void open() {
        session.open();
    }

    @Override
    public void destroy() {
        commandRegistry.clear();
        session.destroy();
    }

    protected ManagedSession getSession() {
        return session;
    }

    @Override
    public String getSessionUUID() {
        return session.getSessionUUID();
    }

    @Override
    public AbstractCanvas getCanvas() {
        return session.getCanvas();
    }

    @Override
    public AbstractCanvasHandler getCanvasHandler() {
        return session.getCanvasHandler();
    }

    @Override
    public CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return canvasCommandManager;
    }

    @Override
    public CommandRegistry<org.kie.workbench.common.stunner.core.command.Command<AbstractCanvasHandler, CanvasViolation>> getCommandRegistry() {
        return commandRegistry;
    }

    @Override
    public MediatorsControl<AbstractCanvas> getMediatorsControl() {
        return (MediatorsControl<AbstractCanvas>) session.getCanvasControl(MediatorsControl.class);
    }

    @Override
    public KeyboardControl<AbstractCanvas, ClientSession> getKeyboardControl() {
        return (KeyboardControl<AbstractCanvas, ClientSession>) session.getCanvasControl(KeyboardControl.class);
    }

    @Override
    public ClipboardControl<Element, AbstractCanvas, ClientSession> getClipboardControl() {
        return (ClipboardControl<Element, AbstractCanvas, ClientSession>) session.getCanvasControl(ClipboardControl.class);
    }

    @Override
    public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
        return (SelectionControl<AbstractCanvasHandler, Element>) session.getCanvasHandlerControl(SelectionControl.class);
    }

    @Override
    public ConnectionAcceptorControl<AbstractCanvasHandler> getConnectionAcceptorControl() {
        return (ConnectionAcceptorControl<AbstractCanvasHandler>) session.getCanvasHandlerControl(ConnectionAcceptorControl.class);
    }

    @Override
    public ContainmentAcceptorControl<AbstractCanvasHandler> getContainmentAcceptorControl() {
        return (ContainmentAcceptorControl<AbstractCanvasHandler>) session.getCanvasHandlerControl(ContainmentAcceptorControl.class);
    }

    @Override
    public DockingAcceptorControl<AbstractCanvasHandler> getDockingAcceptorControl() {
        return (DockingAcceptorControl<AbstractCanvasHandler>) session.getCanvasHandlerControl(DockingAcceptorControl.class);
    }

    @SuppressWarnings("unchecked")
    private void onCanvasHandlerControlRegistered(final CanvasControl<AbstractCanvasHandler> control) {
        if (control instanceof RequiresCommandManager) {
            ((RequiresCommandManager) control).setCommandManagerProvider(() -> sessionCommandManager);
        }
        onControlRegistered(control);
    }
}
