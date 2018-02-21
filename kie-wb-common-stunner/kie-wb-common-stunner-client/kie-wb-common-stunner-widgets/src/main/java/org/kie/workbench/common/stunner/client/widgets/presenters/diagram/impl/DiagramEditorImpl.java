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

package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import org.jboss.errai.ioc.client.api.Disposer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControlRegistrationHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * This DiagramEditor type wraps a DiagramViewer implementation and adds on top of it the
 * controls and a command manager instance,to provide authoring features.
 * @param <D> The diagram type.
 * @param <H> The canvas handler type.
 */
public class DiagramEditorImpl<D extends Diagram, H extends AbstractCanvasHandler, S extends ClientSession>
        implements DiagramEditor<D, H> {

    private final DiagramViewer<D, H> viewer;
    private final CanvasCommandManager<H> commandManager;
    private final ConnectionAcceptorControl<H> connectionAcceptorControl;
    private final ContainmentAcceptorControl<H> containmentAcceptorControl;
    private final DockingAcceptorControl<H> dockingAcceptorControl;
    private final Disposer<CanvasControl> disposer;

    private CanvasControlRegistrationHandler<AbstractCanvas, H, S> registrationHandler;

    DiagramEditorImpl(final DiagramViewer<D, H> viewer,
                      final CanvasCommandManager<H> commandManager,
                      final ConnectionAcceptorControl<H> connectionAcceptorControl,
                      final ContainmentAcceptorControl<H> containmentAcceptorControl,
                      final DockingAcceptorControl<H> dockingAcceptorControl,
                      final Disposer<CanvasControl> disposer) {
        this.viewer = viewer;
        this.commandManager = commandManager;
        this.connectionAcceptorControl = connectionAcceptorControl;
        this.containmentAcceptorControl = containmentAcceptorControl;
        this.dockingAcceptorControl = dockingAcceptorControl;
        this.disposer = disposer;
        this.registrationHandler = null;
    }

    @Override
    public void open(final D item,
                     final DiagramViewerCallback<D> callback) {
        viewer.open(item,
                    new ViewCallback(callback));
    }

    @Override
    public void open(final D item,
                     final int width,
                     final int height,
                     final DiagramViewerCallback<D> callback) {
        viewer.open(item,
                    width,
                    height,
                    new ViewCallback(callback));
    }

    @Override
    public void scale(final int width,
                      final int height) {
        viewer.scale(width,
                     height);
    }

    @Override
    public D getInstance() {
        return viewer.getInstance();
    }

    @Override
    public H getHandler() {
        return viewer.getHandler();
    }

    @Override
    public WidgetWrapperView getView() {
        return viewer.getView();
    }

    public void clear() {
        if (null != registrationHandler) {
            this.registrationHandler.disable();
            this.registrationHandler.clear();
            this.registrationHandler = null;
        }
        viewer.clear();
    }

    @Override
    public void destroy() {
        if (null != registrationHandler) {
            this.registrationHandler.disable();
            this.registrationHandler.destroy();
            this.registrationHandler = null;
        }
        viewer.destroy();
    }

    @Override
    public SelectionControl<H, ?> getSelectionControl() {
        return viewer.getSelectionControl();
    }

    @Override
    public <C extends Canvas> ZoomControl<C> getZoomControl() {
        return viewer.getZoomControl();
    }

    @Override
    public CanvasCommandManager<H> getCommandManager() {
        return commandManager;
    }

    @Override
    public ConnectionAcceptorControl<H> getConnectionAcceptorControl() {
        return connectionAcceptorControl;
    }

    @Override
    public ContainmentAcceptorControl<H> getContainmentAcceptorControl() {
        return containmentAcceptorControl;
    }

    @Override
    public DockingAcceptorControl<H> getDockingAcceptorControl() {
        return dockingAcceptorControl;
    }

    /**
     * A private inner viewer callback type that wraps the given callback from api methods
     * and additionally prepared the edition once the canvas and its handler have been initialized.
     */
    private final class ViewCallback implements DiagramViewerCallback<D> {

        private final DiagramViewerCallback<D> wrapped;

        private ViewCallback(final DiagramViewerCallback<D> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void afterCanvasInitialized() {
            prepareEdit();
            wrapped.afterCanvasInitialized();
        }

        @Override
        public void onSuccess() {
            wrapped.onSuccess();
        }

        @Override
        public void onError(final ClientRuntimeError error) {
            wrapped.onError(error);
        }
    }

    private void prepareEdit() {
        registrationHandler =
                new CanvasControlRegistrationHandler<AbstractCanvas, H, S>((AbstractCanvas) getHandler().getCanvas(),
                                                                           getHandler(), disposer);
        registrationHandler.setCommandManagerProvider(this::getCommandManager);
        // Register the canvas controls that the aggregated diagram viewer instance does not provide.
        registrationHandler.registerCanvasHandlerControl(connectionAcceptorControl);
        registrationHandler.registerCanvasHandlerControl(containmentAcceptorControl);
        registrationHandler.registerCanvasHandlerControl(dockingAcceptorControl);
        registrationHandler.enable();
    }
}
