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

package org.kie.workbench.common.dmn.client.session;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.common.base.Strings;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.dmn.client.graph.DMNElementsSynchronizer;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

@DMNEditor
@Dependent
public class DMNCanvasHandler<D extends Diagram, C extends AbstractCanvas> extends CanvasHandlerImpl<D, C> {

    private final DMNElementsSynchronizer dmnElementsSynchronizer;
    private final GraphsProvider graphsProvider;

    @Inject
    public DMNCanvasHandler(final ClientDefinitionManager clientDefinitionManager,
                            final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                            final RuleManager ruleManager,
                            final GraphUtils graphUtils,
                            final GraphIndexBuilder<? extends MutableIndex<Node, Edge>> indexBuilder,
                            final ShapeManager shapeManager,
                            final TextPropertyProviderFactory textPropertyProviderFactory,
                            final Event<CanvasElementAddedEvent> canvasElementAddedEvent,
                            final Event<CanvasElementRemovedEvent> canvasElementRemovedEvent,
                            final Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent,
                            final Event<CanvasElementsClearEvent> canvasElementsClearEvent,
                            final DMNElementsSynchronizer dmnElementsSynchronizer,
                            final @DMNEditor DMNGraphsProvider graphsProvider) {
        super(clientDefinitionManager, commandFactory, ruleManager, graphUtils, indexBuilder, shapeManager, textPropertyProviderFactory, canvasElementAddedEvent, canvasElementRemovedEvent, canvasElementUpdatedEvent, canvasElementsClearEvent);
        this.dmnElementsSynchronizer = dmnElementsSynchronizer;
        this.graphsProvider = graphsProvider;
    }

    @Override
    protected void beforeElementUpdated(final Element element,
                                        final Shape shape) {
        super.beforeElementUpdated(element, shape);

        if (element instanceof Node) {
            dmnElementsSynchronizer.synchronizeFromNode(Optional.of((Node) element));
            updateDiagramId(element);
        }
    }

    void updateDiagramId(final Element element) {
        final Object content = element.getContent();
        if (content instanceof Definition) {
            final Object definition = ((Definition) content).getDefinition();
            if (definition instanceof HasContentDefinitionId) {
                final HasContentDefinitionId hasContentDefinitionId = (HasContentDefinitionId) definition;
                if (Strings.isNullOrEmpty(hasContentDefinitionId.getDiagramId())) {
                    hasContentDefinitionId.setDiagramId(getGraphsProvider().getCurrentDiagramId());
                }
            }
        }
    }

    GraphsProvider getGraphsProvider() {
        return graphsProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addChild(final Element parent,
                         final Element child) {
        if (!isCanvasRoot(parent)) {
            final Shape parentShape = getCanvas().getShape(parent.getUUID());
            if (Objects.isNull(parentShape)) {
                return;
            }
        }
        superAddChild(parent, child);
    }

    void superAddChild(final Element parent,
                       final Element child) {
        super.addChild(parent, child);
    }
}
