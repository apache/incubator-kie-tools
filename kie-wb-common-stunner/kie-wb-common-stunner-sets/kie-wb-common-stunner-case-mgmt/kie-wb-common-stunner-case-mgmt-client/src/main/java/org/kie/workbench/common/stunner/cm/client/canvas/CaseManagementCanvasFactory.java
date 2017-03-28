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

package org.kie.workbench.common.stunner.cm.client.canvas;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasNameEditionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasValidationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;

/**
 * The CM factory for canvas, canvas handler and controls.
 */
@CaseManagementEditor
@ApplicationScoped
public class CaseManagementCanvasFactory implements CanvasFactory<AbstractCanvas, AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger(CaseManagementCanvasFactory.class.getName());

    private final ManagedInstance<CanvasValidationControl> validationControls;
    private final ManagedInstance<ConnectionAcceptorControl> connectionAcceptorControls;
    private final ManagedInstance<ContainmentAcceptorControl> containmentAcceptorControls;
    private final ManagedInstance<DockingAcceptorControl> dockingAcceptorControls;
    private final ManagedInstance<CanvasNameEditionControl> nameEditionControls;
    private final ManagedInstance<SelectionControl> selectionControls;
    private final ManagedInstance<DragControl> dragControls;
    private final ManagedInstance<ElementBuilderControl> elementBuilderControls;
    private final ManagedInstance<NodeBuilderControl> nodeBuilderControls;
    private final ManagedInstance<EdgeBuilderControl> edgeBuilderControls;
    private final ManagedInstance<ZoomControl> zoomControls;
    private final ManagedInstance<PanControl> panControls;
    private final ManagedInstance<AbstractCanvas> canvasInstances;
    private final ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances;
    private final WiresControlFactory caseManagementControlFactory;

    private final Map<Class<? extends CanvasControl>, ManagedInstance> controls = new HashMap<>(15);

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
             null);
    }

    @Inject
    public CaseManagementCanvasFactory(final ManagedInstance<CanvasValidationControl> validationControls,
                                       final ManagedInstance<ConnectionAcceptorControl> connectionAcceptorControls,
                                       final @CaseManagementEditor ManagedInstance<ContainmentAcceptorControl> containmentAcceptorControls,
                                       final ManagedInstance<DockingAcceptorControl> dockingAcceptorControls,
                                       final ManagedInstance<CanvasNameEditionControl> nameEditionControls,
                                       final ManagedInstance<SelectionControl> selectionControls,
                                       final @CaseManagementEditor ManagedInstance<DragControl> dragControls,
                                       final @CaseManagementEditor @Observer ManagedInstance<ElementBuilderControl> elementBuilderControls,
                                       final @CaseManagementEditor ManagedInstance<NodeBuilderControl> nodeBuilderControls,
                                       final ManagedInstance<EdgeBuilderControl> edgeBuilderControls,
                                       final ManagedInstance<ZoomControl> zoomControls,
                                       final ManagedInstance<PanControl> panControls,
                                       final @CaseManagementEditor ManagedInstance<AbstractCanvas> canvasInstances,
                                       final @CaseManagementEditor ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances,
                                       final @CaseManagementEditor WiresControlFactory caseManagementControlFactory) {
        this.validationControls = validationControls;
        this.connectionAcceptorControls = connectionAcceptorControls;
        this.containmentAcceptorControls = containmentAcceptorControls;
        this.dockingAcceptorControls = dockingAcceptorControls;
        this.nameEditionControls = nameEditionControls;
        this.selectionControls = selectionControls;
        this.dragControls = dragControls;
        this.elementBuilderControls = elementBuilderControls;
        this.nodeBuilderControls = nodeBuilderControls;
        this.edgeBuilderControls = edgeBuilderControls;
        this.zoomControls = zoomControls;
        this.panControls = panControls;
        this.canvasInstances = canvasInstances;
        this.canvasHandlerInstances = canvasHandlerInstances;
        this.caseManagementControlFactory = caseManagementControlFactory;
    }

    @PostConstruct
    public void init() {
        controls.put(CanvasValidationControl.class,
                     validationControls);
        controls.put(ConnectionAcceptorControl.class,
                     connectionAcceptorControls);
        controls.put(ContainmentAcceptorControl.class,
                     containmentAcceptorControls);
        controls.put(DockingAcceptorControl.class,
                     dockingAcceptorControls);
        controls.put(CanvasNameEditionControl.class,
                     nameEditionControls);
        controls.put(SelectionControl.class,
                     selectionControls);
        controls.put(DragControl.class,
                     dragControls);
        controls.put(ElementBuilderControl.class,
                     elementBuilderControls);
        controls.put(NodeBuilderControl.class,
                     nodeBuilderControls);
        controls.put(EdgeBuilderControl.class,
                     edgeBuilderControls);
        controls.put(ZoomControl.class,
                     zoomControls);
        controls.put(PanControl.class,
                     panControls);
    }

    @Override
    public AbstractCanvas newCanvas() {
        final AbstractCanvas canvas = canvasInstances.get();
        ((WiresCanvas) canvas).getWiresManager().setWiresControlFactory(caseManagementControlFactory);
        return canvas;
    }

    @Override
    public AbstractCanvasHandler newCanvasHandler() {
        return canvasHandlerInstances.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends CanvasControl> A newControl(final Class<A> type) {
        if (controls.containsKey(type)) {
            final ManagedInstance<A> mi = controls.get(type);
            return mi.get();
        } else {
            LOGGER.log(Level.WARNING,
                       "Canvas Control for type [" + type.getName() + "] is not supported by " +
                               "this canvas factory [" + this.getClass().getName() + "]");
        }
        return null;
    }
}
