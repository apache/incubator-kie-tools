/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.canvas.controls.builder;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ObserverBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequestImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.CanvasShapeDragStartEvent;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.CanvasShapeDragUpdateEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.model.ModelCardinalityRuleManager;
import org.kie.workbench.common.stunner.core.rule.model.ModelContainmentRuleManager;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@CaseManagementEditor
public class CaseManagementBuilderControl extends ObserverBuilderControl {

    protected CaseManagementBuilderControl() {
        this(null,
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
    public CaseManagementBuilderControl(final ClientDefinitionManager clientDefinitionManager,
                                        final ClientFactoryService clientFactoryServices,
                                        final GraphUtils graphUtils,
                                        final ModelContainmentRuleManager modelContainmentRuleManager,
                                        final ModelCardinalityRuleManager modelCardinalityRuleManager,
                                        final @CaseManagementEditor CanvasCommandFactory canvasCommandFactory,
                                        final GraphBoundsIndexer graphBoundsIndexer,
                                        final CanvasLayoutUtils canvasLayoutUtils,
                                        final Event<CanvasElementSelectedEvent> elementSelectedEvent) {
        super(clientDefinitionManager,
              clientFactoryServices,
              graphUtils,
              modelContainmentRuleManager,
              modelCardinalityRuleManager,
              canvasCommandFactory,
              graphBoundsIndexer,
              canvasLayoutUtils,
              elementSelectedEvent);
    }

    @SuppressWarnings("unchecked")
    void onCanvasShapeDragStart(final @Observes CanvasShapeDragStartEvent canvasShapeDragStartEvent) {
        checkNotNull("canvasShapeDragStartEvent",
                     canvasShapeDragStartEvent);
    }

    @SuppressWarnings("unchecked")
    void onCanvasShapeDragUpdate(final @Observes CanvasShapeDragUpdateEvent canvasShapeDragUpdateEvent) {
        checkNotNull("canvasShapeDragUpdateEvent",
                     canvasShapeDragUpdateEvent);
        if (null != canvasHandler) {
            final CanvasHandler context = canvasShapeDragUpdateEvent.getCanvasHandler();
            if (null != context && context.equals(canvasHandler)) {
                final Object definition = canvasShapeDragUpdateEvent.getDefinition();
                final double x = canvasShapeDragUpdateEvent.getX();
                final double y = canvasShapeDragUpdateEvent.getY();
                final double _x = x >= 0 ? x - canvasHandler.getAbstractCanvas().getAbsoluteX() : -1;
                final double _y = y >= 0 ? y - canvasHandler.getAbstractCanvas().getAbsoluteY() : -1;

                //Now to highlight canvas ;-)
                final ElementBuildRequest<AbstractCanvasHandler> request = new ElementBuildRequestImpl(_x,
                                                                                                       _y,
                                                                                                       definition);
                // This checks if the Shape can be added to a Parent over which it hovers but it does
                // not invoke WiresDockingAndContainmentControl that we need to handle the canvas highlighting
                // TODO {manstis} Need to find a way!
                // - Need to create a transient WiresShape
                // - Need to set it's WiresDockingAndContainmentControl to that in
                // TODO {masntis} This will not work as we need to keep Lienzo away from this module
                //final WiresControlFactory controlFactory = ((WiresCanvas) canvasHandler.getCanvas()).getWiresManager().getControlFactory();
                // - Perhaps add a method to org.kie.workbench.common.stunner.core.client.canvas.Canvas (or CanvasHandler) to handle highlighting?
                //allows(request);
                //TODO {manstis} Probably need to handle drag start to initialise WiresDockingAndContainmentControl
            }
        }
    }
}
