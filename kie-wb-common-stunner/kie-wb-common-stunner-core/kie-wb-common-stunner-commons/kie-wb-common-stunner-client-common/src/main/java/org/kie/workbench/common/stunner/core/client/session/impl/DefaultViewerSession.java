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
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.uberfire.mvp.Command;

@Dependent
public class DefaultViewerSession
        extends ViewerSession {

    private final ManagedSession session;
    private final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;


    @Inject
    public DefaultViewerSession(final ManagedSession session,
                                final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager) {
        this.session = session;
        this.canvasCommandManager = canvasCommandManager;
    }

    @PostConstruct
    public void constructInstance() {
        session.onCanvasControlRegistered(this::onControlRegistered)
                .onCanvasHandlerControlRegistered(this::onControlRegistered)
                .onCanvasControlDestroyed(AbstractSession::onControlDestroyed)
                .onCanvasHandlerControlDestroyed(AbstractSession::onControlDestroyed);
    }

    @Override
    public void init(final Metadata metadata,
                     final Command callback) {
        init(s ->
                     s.registerCanvasControl(ZoomControl.class)
                             .registerCanvasControl(PanControl.class)
                             .registerCanvasHandlerControl(SelectionControl.class,
                                                           MultipleSelection.class),
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
        session.destroy();
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
    public ZoomControl<AbstractCanvas> getZoomControl() {
        return (ZoomControl<AbstractCanvas>) session.getCanvasControl(ZoomControl.class);
    }

    @Override
    public PanControl<AbstractCanvas> getPanControl() {
        return (PanControl<AbstractCanvas>) session.getCanvasControl(PanControl.class);
    }

    @Override
    public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
        return (SelectionControl<AbstractCanvasHandler, Element>) session.getCanvasHandlerControl(SelectionControl.class);
    }
}
