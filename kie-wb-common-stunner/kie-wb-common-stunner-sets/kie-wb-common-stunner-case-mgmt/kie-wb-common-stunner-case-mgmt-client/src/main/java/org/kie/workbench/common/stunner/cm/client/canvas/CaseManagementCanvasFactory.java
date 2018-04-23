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

package org.kie.workbench.common.stunner.cm.client.canvas;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.SingleLineTextEditorBox;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SingleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;

/**
 * The CM factory for canvas, canvas handler and controls.
 */
@CaseManagementEditor
@ApplicationScoped
public class CaseManagementCanvasFactory
        extends AbstractCanvasFactory<CaseManagementCanvasFactory> {

    private final ManagedInstance<ConnectionAcceptorControl> connectionAcceptorControls;
    private final ManagedInstance<ContainmentAcceptorControl> containmentAcceptorControls;
    private final ManagedInstance<DockingAcceptorControl> dockingAcceptorControls;
    private final ManagedInstance<CanvasInPlaceTextEditorControl> nameEditionControls;
    private final ManagedInstance<SelectionControl> selectionControls;
    private final ManagedInstance<ElementBuilderControl> elementBuilderControls;
    private final ManagedInstance<NodeBuilderControl> nodeBuilderControls;
    private final ManagedInstance<EdgeBuilderControl> edgeBuilderControls;
    private final ManagedInstance<ZoomControl> zoomControls;
    private final ManagedInstance<PanControl> panControls;
    private final ManagedInstance<KeyboardControl> keyboardControls;
    private final ManagedInstance<ClipboardControl> clipboardControls;
    private final ManagedInstance<AbstractCanvas> canvasInstances;
    private final ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances;
    private final ManagedInstance<ControlPointControl> controlPointControls;

    protected CaseManagementCanvasFactory() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null
        );
    }

    @Inject
    public CaseManagementCanvasFactory(final ManagedInstance<ConnectionAcceptorControl> connectionAcceptorControls,
                                       final @CaseManagementEditor ManagedInstance<ContainmentAcceptorControl> containmentAcceptorControls,
                                       final ManagedInstance<DockingAcceptorControl> dockingAcceptorControls,
                                       final @SingleLineTextEditorBox ManagedInstance<CanvasInPlaceTextEditorControl> nameEditionControls,
                                       final @SingleSelection ManagedInstance<SelectionControl> selectionControls,
                                       final @CaseManagementEditor @Observer ManagedInstance<ElementBuilderControl> elementBuilderControls,
                                       final @CaseManagementEditor ManagedInstance<NodeBuilderControl> nodeBuilderControls,
                                       final ManagedInstance<EdgeBuilderControl> edgeBuilderControls,
                                       final ManagedInstance<ZoomControl> zoomControls,
                                       final ManagedInstance<PanControl> panControls,
                                       final ManagedInstance<KeyboardControl> keyboardControls,
                                       final ManagedInstance<ClipboardControl> clipboardControls,
                                       final @CaseManagementEditor ManagedInstance<AbstractCanvas> canvasInstances,
                                       final @CaseManagementEditor ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances,
                                       final ManagedInstance<ControlPointControl> controlPointControls) {
        this.connectionAcceptorControls = connectionAcceptorControls;
        this.containmentAcceptorControls = containmentAcceptorControls;
        this.dockingAcceptorControls = dockingAcceptorControls;
        this.nameEditionControls = nameEditionControls;
        this.selectionControls = selectionControls;
        this.elementBuilderControls = elementBuilderControls;
        this.nodeBuilderControls = nodeBuilderControls;
        this.edgeBuilderControls = edgeBuilderControls;
        this.zoomControls = zoomControls;
        this.panControls = panControls;
        this.keyboardControls = keyboardControls;
        this.canvasInstances = canvasInstances;
        this.canvasHandlerInstances = canvasHandlerInstances;
        this.clipboardControls = clipboardControls;
        this.controlPointControls = controlPointControls;
    }

    @PostConstruct
    public void init() {
        this
                .register(ConnectionAcceptorControl.class,
                          connectionAcceptorControls)
                .register(ContainmentAcceptorControl.class,
                          containmentAcceptorControls)
                .register(DockingAcceptorControl.class,
                          dockingAcceptorControls)
                .register(CanvasInPlaceTextEditorControl.class,
                          nameEditionControls)
                .register(SelectionControl.class,
                          selectionControls)
                .register(ElementBuilderControl.class,
                          elementBuilderControls)
                .register(NodeBuilderControl.class,
                          nodeBuilderControls)
                .register(EdgeBuilderControl.class,
                          edgeBuilderControls)
                .register(ZoomControl.class,
                          zoomControls)
                .register(PanControl.class,
                          panControls)
                .register(KeyboardControl.class,
                          keyboardControls)
                .register(ClipboardControl.class,
                          clipboardControls)
                .register(ControlPointControl.class,
                          controlPointControls);
    }

    @Override
    public AbstractCanvas newCanvas() {
        return canvasInstances.get();
    }

    @Override
    public AbstractCanvasHandler newCanvasHandler() {
        return canvasHandlerInstances.get();
    }
}
