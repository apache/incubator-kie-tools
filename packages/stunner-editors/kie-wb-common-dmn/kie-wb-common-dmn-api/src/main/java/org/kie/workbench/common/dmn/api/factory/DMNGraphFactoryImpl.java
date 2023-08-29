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
package org.kie.workbench.common.dmn.api.factory;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.impl.AbstractGraphFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.DirectGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

@ApplicationScoped
public class DMNGraphFactoryImpl
        extends AbstractGraphFactory
        implements DMNGraphFactory {

    private static final String DIAGRAM_ID = getDefinitionId(DMNDiagram.class);

    private final DefinitionManager definitionManager;
    private final GraphCommandManager graphCommandManager;
    private final GraphCommandFactory graphCommandFactory;
    private final FactoryManager factoryManager;
    private final GraphIndexBuilder<?> indexBuilder;

    protected DMNGraphFactoryImpl() {
        this(null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public DMNGraphFactoryImpl(final DefinitionManager definitionManager,
                               final FactoryManager factoryManager,
                               final GraphCommandManager graphCommandManager,
                               final GraphCommandFactory graphCommandFactory,
                               final GraphIndexBuilder<?> indexBuilder) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.graphCommandManager = graphCommandManager;
        this.graphCommandFactory = graphCommandFactory;
        this.indexBuilder = indexBuilder;
    }

    @Override
    public Class<? extends ElementFactory> getFactoryType() {
        return DMNGraphFactory.class;
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
                new CompositeCommand.Builder<>();
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
    protected DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    @SuppressWarnings("unchecked")
    protected List<Command> buildInitialisationCommands() {
        final List<Command> commands = new ArrayList<>();
        final Node<Definition<DMNDiagram>, Edge> diagramNode = (Node<Definition<DMNDiagram>, Edge>) factoryManager.newElement(UUID.uuid(),
                                                                                                                              DIAGRAM_ID);
        commands.add(graphCommandFactory.addNode(diagramNode));

        return commands;
    }

    @SuppressWarnings("unchecked")
    protected GraphCommandExecutionContext createGraphContext(final Graph graph) {
        final Index<?, ?> index = (Index<?, ?>) indexBuilder.build(graph);
        return new DirectGraphCommandExecutionContext(definitionManager,
                                                      factoryManager,
                                                      index);
    }
}