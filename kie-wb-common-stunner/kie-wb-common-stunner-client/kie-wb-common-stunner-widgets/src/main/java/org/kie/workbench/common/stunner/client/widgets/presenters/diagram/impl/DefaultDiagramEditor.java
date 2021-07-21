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

import java.lang.annotation.Annotation;
import java.util.Arrays;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasElementListener;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * This DiagramEditor type wraps a DiagramViewer implementation and adds on top of it the
 * controls and a command manager instance,to provide authoring features.
 */
@Dependent
@Typed(DiagramEditor.class)
public class DefaultDiagramEditor
        implements DiagramEditor<Diagram, AbstractCanvasHandler> {

    private final DefinitionUtils definitionUtils;
    private final DiagramViewer<Diagram, AbstractCanvasHandler> viewer;
    private final ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> commandManagers;
    private final ManagedInstance<LocationControl<AbstractCanvasHandler, Element>> locationControls;
    private final ManagedInstance<ResizeControl<AbstractCanvasHandler, Element>> resizeControls;
    private final ManagedInstance<ElementBuilderControl<AbstractCanvasHandler>> elementBuilderControls;
    private final ManagedInstance<NodeBuilderControl<AbstractCanvasHandler>> nodeBuilderControls;
    private final ManagedInstance<EdgeBuilderControl<AbstractCanvasHandler>> edgeBuilderControls;
    private final ManagedInstance<ControlPointControl<AbstractCanvasHandler>> controlPointControls;
    private final ManagedInstance<ConnectionAcceptorControl<AbstractCanvasHandler>> connectionAcceptorControls;
    private final ManagedInstance<ContainmentAcceptorControl<AbstractCanvasHandler>> containmentAcceptorControls;
    private final ManagedInstance<DockingAcceptorControl<AbstractCanvasHandler>> dockingAcceptorControls;

    private CanvasElementListener elementListener;
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;
    private LocationControl<AbstractCanvasHandler, Element> locationControl;
    private ResizeControl<AbstractCanvasHandler, Element> resizeControl;
    private ElementBuilderControl<AbstractCanvasHandler> elementBuilderControl;
    private NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl;
    private EdgeBuilderControl<AbstractCanvasHandler> edgeBuilderControl;
    private ControlPointControl<AbstractCanvasHandler> controlPointControl;
    private ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl;
    private ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl;
    private DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl;

    @Inject
    public DefaultDiagramEditor(final DefinitionUtils definitionUtils,
                                final DiagramViewer<Diagram, AbstractCanvasHandler> viewer,
                                final @Any ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> commandManagers,
                                final @Any ManagedInstance<LocationControl<AbstractCanvasHandler, Element>> locationControls,
                                final @Any ManagedInstance<ResizeControl<AbstractCanvasHandler, Element>> resizeControls,
                                final @Any @Observer ManagedInstance<ElementBuilderControl<AbstractCanvasHandler>> elementBuilderControls,
                                final @Any ManagedInstance<NodeBuilderControl<AbstractCanvasHandler>> nodeBuilderControls,
                                final @Any ManagedInstance<EdgeBuilderControl<AbstractCanvasHandler>> edgeBuilderControls,
                                final @Any ManagedInstance<ControlPointControl<AbstractCanvasHandler>> controlPointControls,
                                final @Any ManagedInstance<ConnectionAcceptorControl<AbstractCanvasHandler>> connectionAcceptorControls,
                                final @Any ManagedInstance<ContainmentAcceptorControl<AbstractCanvasHandler>> containmentAcceptorControls,
                                final @Any ManagedInstance<DockingAcceptorControl<AbstractCanvasHandler>> dockingAcceptorControls) {
        this.definitionUtils = definitionUtils;
        this.viewer = viewer;
        this.commandManagers = commandManagers;
        this.locationControls = locationControls;
        this.resizeControls = resizeControls;
        this.elementBuilderControls = elementBuilderControls;
        this.nodeBuilderControls = nodeBuilderControls;
        this.edgeBuilderControls = edgeBuilderControls;
        this.controlPointControls = controlPointControls;
        this.connectionAcceptorControls = connectionAcceptorControls;
        this.containmentAcceptorControls = containmentAcceptorControls;
        this.dockingAcceptorControls = dockingAcceptorControls;
    }

    @Override
    public void open(final Diagram item,
                     final DiagramViewerCallback<Diagram> callback) {
        viewer.open(item,
                    new ViewCallback(callback));
    }

    @Override
    public void scale(final int width,
                      final int height) {
        viewer.scale(width,
                     height);
    }

    @Override
    public Diagram getInstance() {
        return viewer.getInstance();
    }

    @Override
    public AbstractCanvasHandler getHandler() {
        return viewer.getHandler();
    }

    @Override
    public WidgetWrapperView getView() {
        return viewer.getView();
    }

    public void clear() {
        viewer.clear();
    }

    @Override
    public void destroy() {
        // Destroy control instances.
        commandManagers.destroy(commandManager);
        commandManagers.destroyAll();
        locationControl.destroy();
        locationControl.setCommandManagerProvider(() -> null);
        locationControls.destroyAll();
        resizeControl.destroy();
        resizeControl.setCommandManagerProvider(() -> null);
        resizeControls.destroyAll();
        elementBuilderControl.destroy();
        elementBuilderControl.setCommandManagerProvider(() -> null);
        elementBuilderControls.destroyAll();
        nodeBuilderControl.destroy();
        nodeBuilderControl.setCommandManagerProvider(() -> null);
        nodeBuilderControls.destroyAll();
        edgeBuilderControl.destroy();
        edgeBuilderControl.setCommandManagerProvider(() -> null);
        edgeBuilderControls.destroyAll();
        controlPointControl.destroy();
        controlPointControl.setCommandManagerProvider(() -> null);
        controlPointControls.destroyAll();
        connectionAcceptorControl.destroy();
        connectionAcceptorControl.setCommandManagerProvider(() -> null);
        connectionAcceptorControls.destroy(connectionAcceptorControl);
        connectionAcceptorControls.destroyAll();
        containmentAcceptorControl.destroy();
        containmentAcceptorControl.setCommandManagerProvider(() -> null);
        containmentAcceptorControls.destroy(containmentAcceptorControl);
        containmentAcceptorControls.destroyAll();
        dockingAcceptorControl.destroy();
        dockingAcceptorControl.setCommandManagerProvider(() -> null);
        dockingAcceptorControls.destroy(dockingAcceptorControl);
        dockingAcceptorControls.destroyAll();
        // Destroy the viewer state as well.
        viewer.destroy();
        // Nullify
        elementListener = null;
        commandManager = null;
        locationControl = null;
        resizeControl = null;
        elementBuilderControl = null;
        nodeBuilderControl = null;
        edgeBuilderControl = null;
        controlPointControl = null;
        connectionAcceptorControl = null;
        containmentAcceptorControl = null;
        dockingAcceptorControl = null;
    }

    @Override
    public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
        return viewer.getSelectionControl();
    }

    @Override
    public <C extends Canvas> MediatorsControl<C> getMediatorsControl() {
        return viewer.getMediatorsControl();
    }

    @Override
    public CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManager;
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

    public LocationControl<AbstractCanvasHandler, Element> getLocationControl() {
        return locationControl;
    }

    public ResizeControl<AbstractCanvasHandler, Element> getResizeControl() {
        return resizeControl;
    }

    public ElementBuilderControl<AbstractCanvasHandler> getElementBuilderControl() {
        return elementBuilderControl;
    }

    public NodeBuilderControl<AbstractCanvasHandler> getNodeBuilderControl() {
        return nodeBuilderControl;
    }

    public EdgeBuilderControl<AbstractCanvasHandler> getEdgeBuilderControl() {
        return edgeBuilderControl;
    }

    public ControlPointControl<AbstractCanvasHandler> getControlPointControl() {
        return controlPointControl;
    }

    /**
     * A private inner viewer callback type that wraps the given callback from api methods
     * and additionally prepared the edition once the canvas and its handler have been initialized.
     */
    private final class ViewCallback implements DiagramViewerCallback<Diagram> {

        private final DiagramViewerCallback<Diagram> wrapped;

        private ViewCallback(final DiagramViewerCallback<Diagram> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void onOpen(final Diagram diagram) {
            wrapped.onOpen(diagram);
            DefaultDiagramEditor.this.onOpen(diagram);
        }

        @Override
        public void afterCanvasInitialized() {
            wrapped.afterCanvasInitialized();
            locationControl.init(getHandler());
            resizeControl.init(getHandler());
            elementBuilderControl.init(getHandler());
            nodeBuilderControl.init(getHandler());
            edgeBuilderControl.init(getHandler());
            controlPointControl.init(getHandler());
            connectionAcceptorControl.init(getHandler());
            containmentAcceptorControl.init(getHandler());
            dockingAcceptorControl.init(getHandler());
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

    private void onOpen(final Diagram diagram) {
        final Annotation qualifier =
                definitionUtils.getQualifier(diagram.getMetadata().getDefinitionSetId());
        commandManager = InstanceUtils.lookup(commandManagers, qualifier);
        locationControl = InstanceUtils.lookup(locationControls, qualifier);
        locationControl.setCommandManagerProvider(() -> commandManager);
        resizeControl = InstanceUtils.lookup(resizeControls, qualifier);
        resizeControl.setCommandManagerProvider(() -> commandManager);
        elementBuilderControl = InstanceUtils.lookup(elementBuilderControls, qualifier);
        elementBuilderControl.setCommandManagerProvider(() -> commandManager);
        nodeBuilderControl = InstanceUtils.lookup(nodeBuilderControls, qualifier);
        nodeBuilderControl.setCommandManagerProvider(() -> commandManager);
        edgeBuilderControl = InstanceUtils.lookup(edgeBuilderControls, qualifier);
        edgeBuilderControl.setCommandManagerProvider(() -> commandManager);
        controlPointControl = InstanceUtils.lookup(controlPointControls, qualifier);
        controlPointControl.setCommandManagerProvider(() -> commandManager);
        connectionAcceptorControl = InstanceUtils.lookup(connectionAcceptorControls, qualifier);
        connectionAcceptorControl.setCommandManagerProvider(() -> commandManager);
        containmentAcceptorControl = InstanceUtils.lookup(containmentAcceptorControls, qualifier);
        containmentAcceptorControl.setCommandManagerProvider(() -> commandManager);
        dockingAcceptorControl = InstanceUtils.lookup(dockingAcceptorControls, qualifier);
        containmentAcceptorControl.setCommandManagerProvider(() -> commandManager);
        elementListener = new DefaultCanvasElementListener(Arrays.asList(locationControl,
                                                                         resizeControl,
                                                                         elementBuilderControl,
                                                                         nodeBuilderControl,
                                                                         edgeBuilderControl,
                                                                         controlPointControl,
                                                                         containmentAcceptorControl,
                                                                         connectionAcceptorControl,
                                                                         dockingAcceptorControl));
        viewer.getHandler().addRegistrationListener(elementListener);
    }
}
