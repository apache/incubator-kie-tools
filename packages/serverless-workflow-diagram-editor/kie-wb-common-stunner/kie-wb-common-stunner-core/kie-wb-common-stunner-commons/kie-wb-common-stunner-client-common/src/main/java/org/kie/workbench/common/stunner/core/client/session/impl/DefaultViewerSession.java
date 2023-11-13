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


package org.kie.workbench.common.stunner.core.client.session.impl;

import java.util.function.Consumer;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AlertsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SingleSelection;
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
        init(s -> s.registerCanvasControl(MediatorsControl.class)
                     .registerCanvasControl(AlertsControl.class)
                     .registerCanvasHandlerControl(SelectionControl.class,
                                                   SingleSelection.class),
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
    public void close() {
        session.close();
    }

    @Override
    public void destroy() {
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
    public MediatorsControl<AbstractCanvas> getMediatorsControl() {
        return (MediatorsControl<AbstractCanvas>) session.getCanvasControl(MediatorsControl.class);
    }

    @Override
    public AlertsControl<AbstractCanvas> getAlertsControl() {
        return (AlertsControl<AbstractCanvas>) session.getCanvasControl(AlertsControl.class);
    }

    @Override
    public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
        return (SelectionControl<AbstractCanvasHandler, Element>) session.getCanvasHandlerControl(SelectionControl.class);
    }
}
