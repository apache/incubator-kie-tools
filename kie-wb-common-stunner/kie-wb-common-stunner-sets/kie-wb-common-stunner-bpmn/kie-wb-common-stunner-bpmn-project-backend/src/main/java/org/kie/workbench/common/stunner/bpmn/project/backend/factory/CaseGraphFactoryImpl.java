/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.backend.factory;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionBackendService;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTaskFactory;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionService;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.UUID;

@Dependent
@Typed(CaseGraphFactoryImpl.class)
public class CaseGraphFactoryImpl extends BPMNGraphFactoryImpl {

    private ServiceTaskFactory serviceTaskFactory;
    private WorkItemDefinitionService workItemDefinitionService;

    public CaseGraphFactoryImpl() {
    }

    @Inject
    public CaseGraphFactoryImpl(DefinitionManager definitionManager, FactoryManager factoryManager,
                                RuleManager ruleManager, GraphCommandManager graphCommandManager,
                                GraphCommandFactory graphCommandFactory, GraphIndexBuilder<?> indexBuilder,
                                ServiceTaskFactory serviceTaskFactory,
                                WorkItemDefinitionBackendService workItemDefinitionService) {
        super(definitionManager, factoryManager, ruleManager, graphCommandManager, graphCommandFactory, indexBuilder);
        this.serviceTaskFactory = serviceTaskFactory;
        this.workItemDefinitionService = workItemDefinitionService;
    }

    @Override
    public Graph<DefinitionSet, Node> build(String uuid, String definition, Metadata metadata) {
        //before build it is necessary to load the WorkDefinitions to get the Milestone
        workItemDefinitionService.execute(metadata);
        return super.build(uuid, definition);
    }

    // Add a Case diagram and a start event nodes by default.
    @Override
    protected List<Command> buildInitialisationCommands() {
        final List<Command> commands = new ArrayList<>();
        final Node<Definition<BPMNDiagram>, Edge> diagramNode =
                (Node<Definition<BPMNDiagram>, Edge>) factoryManager.newElement(UUID.uuid(), getDiagramType());

        final ServiceTask milestone = serviceTaskFactory.buildItem("Milestone");
        final DefinitionAdapter<Object> adapter =
                definitionManager.adapters().registry().getDefinitionAdapter(milestone.getClass());

        final Node<View<ServiceTask>, Edge> firstElement =
                (Node<View<ServiceTask>, Edge>) factoryManager.newElement(UUID.uuid(), adapter.getId(milestone).value());

        final AdHoc adHoc = diagramNode.getContent().getDefinition().getDiagramSet().getAdHoc();
        final String adHocPropertyId = definitionManager.adapters().forProperty().getId(adHoc);

        commands.add(graphCommandFactory.addNode(diagramNode));
        commands.add(graphCommandFactory.addChildNode(diagramNode, firstElement, new Point2D(START_X, START_Y)));
        commands.add(graphCommandFactory.updatePropertyValue(diagramNode, adHocPropertyId, true));
        return commands;
    }
}