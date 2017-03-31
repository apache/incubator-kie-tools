/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramPresenterFactory;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasValidationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;

@ApplicationScoped
public class DiagramPresenterFactoryImpl implements DiagramPresenterFactory<Diagram> {

    private final ShapeManager shapeManager;
    private final ManagedInstance<WidgetWrapperView> viewInstances;
    private final ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> commandManagerInstances;

    protected DiagramPresenterFactoryImpl() {
        this(null,
             null,
             null);
    }

    @Inject
    public DiagramPresenterFactoryImpl(final ShapeManager shapeManager,
                                       final ManagedInstance<WidgetWrapperView> viewInstances,
                                       final ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> commandManagerInstances) {
        this.shapeManager = shapeManager;
        this.viewInstances = viewInstances;
        this.commandManagerInstances = commandManagerInstances;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DiagramViewer<Diagram, ?> newViewer(final Diagram diagram) {
        final CanvasFactory<AbstractCanvas, AbstractCanvasHandler> canvasFactory = shapeManager.getCanvasFactory(diagram);
        final AbstractCanvas canvas = canvasFactory.newCanvas();
        final AbstractCanvasHandler canvasHandler = canvasFactory.newCanvasHandler();
        final ZoomControl<AbstractCanvas> zoomControl = canvasFactory.newControl(ZoomControl.class);
        final SelectionControl<AbstractCanvasHandler, Element> selectionControl = canvasFactory.newControl(SelectionControl.class);
        return new DiagramViewerImpl<>(canvas,
                                       canvasHandler,
                                       viewInstances.get(),
                                       zoomControl,
                                       selectionControl);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DiagramEditor<Diagram, ?> newEditor(final Diagram diagram) {
        final DiagramViewer<Diagram, AbstractCanvasHandler> viewer = (DiagramViewer<Diagram, AbstractCanvasHandler>) newViewer(diagram);
        final CanvasFactory<AbstractCanvas, AbstractCanvasHandler> canvasFactory = shapeManager.getCanvasFactory(diagram);
        final CanvasValidationControl<AbstractCanvasHandler> validationControl = canvasFactory.newControl(CanvasValidationControl.class);
        final ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl = canvasFactory.newControl(ConnectionAcceptorControl.class);
        final ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl = canvasFactory.newControl(ContainmentAcceptorControl.class);
        final DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl = canvasFactory.newControl(DockingAcceptorControl.class);
        return new DiagramEditorImpl<>(viewer,
                                       commandManagerInstances.get(),
                                       validationControl,
                                       connectionAcceptorControl,
                                       containmentAcceptorControl,
                                       dockingAcceptorControl);
    }
}
