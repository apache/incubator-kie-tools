/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.session;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
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
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

@DMNEditor
@Dependent
public class DMNCanvasHandler<D extends Diagram, C extends AbstractCanvas> extends CanvasHandlerImpl<D, C> {

    private final DMNElementsSynchronizer dmnElementsSynchronizer;

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
                            final DMNElementsSynchronizer dmnElementsSynchronizer) {
        super(clientDefinitionManager, commandFactory, ruleManager, graphUtils, indexBuilder, shapeManager, textPropertyProviderFactory, canvasElementAddedEvent, canvasElementRemovedEvent, canvasElementUpdatedEvent, canvasElementsClearEvent);
        this.dmnElementsSynchronizer = dmnElementsSynchronizer;
    }

    @Override
    protected void beforeElementUpdated(final Element element,
                                        final Shape shape) {
        super.beforeElementUpdated(element, shape);

        if (element instanceof Node) {
            dmnElementsSynchronizer.synchronizeFromNode(Optional.of((Node) element));
        }
    }
}

