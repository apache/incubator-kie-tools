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

package org.kie.workbench.common.stunner.bpmn.factory;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.impl.AbstractGraphFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.EmptyRulesCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.UUID;

/**
 * The custom factory for BPMN graphs.
 * It initializes the BPMN graph with a new Diagram node instance, which represents the main process.
 * This class uses the Commands API in order to avoid adding nodes/edges and setting bean values manually on the graph structure,
 * so this will avoid further errors, but in fact there should be not need to check runtime rules when executing
 * these commands.
 */
@Dependent
@Typed(BPMNGraphFactoryImpl.class)
public class BPMNGraphFactoryImpl
        extends AbstractGraphFactory
        implements BPMNGraphFactory {

    protected final DefinitionManager definitionManager;
    private final RuleManager ruleManager;
    private final GraphIndexBuilder<?> indexBuilder;
    private final GraphCommandManager graphCommandManager;
    protected final GraphCommandFactory graphCommandFactory;
    protected final FactoryManager factoryManager;

    private Class<? extends BPMNDiagram> diagramType;

    protected BPMNGraphFactoryImpl() {
        this(null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public BPMNGraphFactoryImpl(final DefinitionManager definitionManager,
                                final FactoryManager factoryManager,
                                final RuleManager ruleManager,
                                final GraphCommandManager graphCommandManager,
                                final GraphCommandFactory graphCommandFactory,
                                final GraphIndexBuilder<?> indexBuilder) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.ruleManager = ruleManager;
        this.graphCommandManager = graphCommandManager;
        this.graphCommandFactory = graphCommandFactory;
        this.indexBuilder = indexBuilder;
        this.diagramType = BPMNDiagramImpl.class;
    }

    public void setDiagramType(final Class<? extends BPMNDiagram> diagramType) {
        this.diagramType = diagramType;
    }

    public Class<? extends BPMNDiagram> getDiagramType() {
        return diagramType;
    }

    @Override
    public Class<? extends ElementFactory> getFactoryType() {
        return BPMNGraphFactory.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Graph<DefinitionSet, Node> build(final String uuid,
                                            final String definitionSetId) {
        final Graph<DefinitionSet, Node> graph = super.build(uuid,
                                                             definitionSetId);
        //Add default elements
        final List<Command> commands = buildInitialisationCommands();
        final CompositeCommand.Builder commandBuilder =
                new CompositeCommand.Builder();
        commands.forEach(commandBuilder::addCommand);
        graphCommandManager.execute(createGraphContext(graph),
                                    commandBuilder.build());

        return graph;
    }

    @Override
    public boolean accepts(final String source) {
        return true;
    }

    @Override
    protected double getWidth() {
        return GRAPH_DEFAULT_WIDTH;
    }

    @Override
    protected double getHeight() {
        return GRAPH_DEFAULT_HEIGHT;
    }

    @SuppressWarnings("unchecked")
    // Add a BPMN diagram and a start event nodes by default.
    protected List<Command> buildInitialisationCommands() {
        final List<Command> commands = new ArrayList<>();
        final Node<Definition<BPMNDiagram>, Edge> diagramNode = (Node<Definition<BPMNDiagram>, Edge>) factoryManager.newElement(UUID.uuid(),
                                                                                                                                diagramType);
        final Node<Definition<StartNoneEvent>, Edge> startEventNode = (Node<Definition<StartNoneEvent>, Edge>) factoryManager.newElement(UUID.uuid(),
                                                                                                                                         StartNoneEvent.class);
        commands.add(graphCommandFactory.addNode(diagramNode));
        commands.add(graphCommandFactory.addChildNode(diagramNode,
                                                      startEventNode,
                                                      new Point2D(START_X,
                                                                  START_Y)));
        return commands;
    }

    @SuppressWarnings("unchecked")
    protected GraphCommandExecutionContext createGraphContext(final Graph graph) {
        final Index<?, ?> index = indexBuilder.build(graph);
        return new EmptyRulesCommandExecutionContext(definitionManager,
                                                     factoryManager,
                                                     ruleManager,
                                                     index);
    }

    @Override
    protected DefinitionManager getDefinitionManager() {
        return definitionManager;
    }
}
