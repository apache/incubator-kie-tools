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

package org.kie.workbench.common.stunner.core.client.canvas;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContextImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * The default canvas handler type implementation for any sutype of <code>AbstractCanvas</code> view and any
 * subtype of <code>Diagram</code> model.
 * @param <D> The diagram type.
 * @param <C> The handled canvas type.
 */
@Dependent
public class CanvasHandlerImpl<D extends Diagram, C extends AbstractCanvas> extends BaseCanvasHandler<D, C> {

    private final ClientFactoryService clientFactoryServices;
    private final CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    private final GraphIndexBuilder<? extends MutableIndex<Node, Edge>> indexBuilder;
    private final RuleManager ruleManager;
    private final Event<CanvasElementAddedEvent> canvasElementAddedEvent;
    private final Event<CanvasElementRemovedEvent> canvasElementRemovedEvent;
    private final Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent;
    private final Event<CanvasElementsClearEvent> canvasElementsClearEvent;

    private MutableIndex<?, ?> graphIndex;

    @Inject
    public CanvasHandlerImpl(final ClientDefinitionManager clientDefinitionManager,
                             final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                             final ClientFactoryService clientFactoryServices,
                             final RuleManager ruleManager,
                             final GraphUtils graphUtils,
                             final GraphIndexBuilder<? extends MutableIndex<Node, Edge>> indexBuilder,
                             final ShapeManager shapeManager,
                             final TextPropertyProviderFactory textPropertyProviderFactory,
                             final Event<CanvasElementAddedEvent> canvasElementAddedEvent,
                             final Event<CanvasElementRemovedEvent> canvasElementRemovedEvent,
                             final Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent,
                             final Event<CanvasElementsClearEvent> canvasElementsClearEvent) {
        super(clientDefinitionManager,
              graphUtils,
              shapeManager,
              textPropertyProviderFactory);
        this.commandFactory = commandFactory;
        this.indexBuilder = indexBuilder;
        this.ruleManager = ruleManager;
        this.clientFactoryServices = clientFactoryServices;
        this.canvasElementAddedEvent = canvasElementAddedEvent;
        this.canvasElementRemovedEvent = canvasElementRemovedEvent;
        this.canvasElementUpdatedEvent = canvasElementUpdatedEvent;
        this.canvasElementsClearEvent = canvasElementsClearEvent;
    }

    @Override
    public RuleManager getRuleManager() {
        return ruleManager;
    }

    @Override
    public Index<?, ?> getGraphIndex() {
        return graphIndex;
    }

    @Override
    public GraphCommandExecutionContext getGraphExecutionContext() {
        return new GraphCommandExecutionContextImpl(getDefinitionManager(),
                                                    getFactoryManager(),
                                                    getRuleManager(),
                                                    getGraphIndex(),
                                                    getRuleSet());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void buildGraphIndex(final Command loadCallback) {
        this.graphIndex = getIndexBuilder().build(getDiagram().getGraph());
        loadCallback.execute();
    }

    @Override
    public void draw(final ParameterizedCommand<CommandResult<?>> loadCallback) {
        loadCallback.execute(commandFactory.draw().execute(this));
    }

    @Override
    public void clearCanvas() {
        super.clearCanvas();
        canvasElementsClearEvent.fire(new CanvasElementsClearEvent(this));
    }

    @Override
    protected void afterElementAdded(final Element element,
                                     final Shape shape) {
        // Fire a canvas element added event.
        canvasElementAddedEvent.fire(new CanvasElementAddedEvent(this,
                                                                 element));
    }

    @Override
    protected void beforeElementDeleted(final Element element,
                                        final Shape shape) {
        // Fire a canvas element deleted event.
        canvasElementRemovedEvent.fire(new CanvasElementRemovedEvent(this,
                                                                     element));
    }

    @Override
    protected void afterElementDeleted(final Element element,
                                       final Shape shape) {
    }

    @Override
    protected void beforeElementUpdated(final Element element,
                                        final Shape shape) {
    }

    @Override
    protected void afterElementUpdated(final Element element,
                                       final Shape shape) {
        // Fire a canvas element added event.
        canvasElementUpdatedEvent.fire(new CanvasElementUpdatedEvent(this,
                                                                     element));
    }

    @Override
    protected void destroyGraphIndex(final Command callback) {
        if (null != graphIndex) {
            graphIndex.clear();
            graphIndex = null;
        }
        callback.execute();
    }

    private GraphIndexBuilder<? extends MutableIndex<Node, Edge>> getIndexBuilder() {
        return indexBuilder;
    }

    private FactoryManager getFactoryManager() {
        return clientFactoryServices.getClientFactoryManager();
    }
}
