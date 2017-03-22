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
package org.kie.workbench.common.stunner.cm.factory;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactoryImpl;
import org.kie.workbench.common.stunner.cm.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.UUID;

/**
 * The custom factory for Case Management graphs.
 */
@ApplicationScoped
public class CaseManagementGraphFactoryImpl
        extends BPMNGraphFactoryImpl implements CaseManagementGraphFactory {

    protected CaseManagementGraphFactoryImpl() {
        super();
    }

    @Inject
    public CaseManagementGraphFactoryImpl(final DefinitionManager definitionManager,
                                          final FactoryManager factoryManager,
                                          final RuleManager ruleManager,
                                          final GraphCommandManager graphCommandManager,
                                          final GraphCommandFactory graphCommandFactory,
                                          final GraphIndexBuilder<?> indexBuilder) {
        super(definitionManager,
              factoryManager,
              ruleManager,
              graphCommandManager,
              graphCommandFactory,
              indexBuilder);
    }

    @Override
    public Class<? extends ElementFactory> getFactoryType() {
        return CaseManagementGraphFactory.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    // Add a BPMN diagram and a start event nodes by default.
    // This is not duplicated code as the Generics class is Case Management's BPMNDiagram
    protected List<Command> buildInitialisationCommands() {
        final List<Command> commands = new ArrayList<>();
        final Node<Definition<BPMNDiagram>, Edge> diagramNode = (Node<Definition<BPMNDiagram>, Edge>) factoryManager.newElement(UUID.uuid(),
                                                                                                                                BPMNDiagram.class);
        final Node<Definition<StartNoneEvent>, Edge> startEventNode = (Node<Definition<StartNoneEvent>, Edge>) factoryManager.newElement(UUID.uuid(),
                                                                                                                                         StartNoneEvent.class);
        commands.add(graphCommandFactory.addNode(diagramNode));
        commands.add(graphCommandFactory.addChildNode(diagramNode,
                                                      startEventNode,
                                                      100d,
                                                      100d));
        return commands;
    }
}