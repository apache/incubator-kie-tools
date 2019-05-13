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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionBackendService;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTaskFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddChildNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPropertyValueCommand;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseGraphFactoryImplTest {

    public static final String MILESTONE = "Milestone";
    public static final String MILESTONE_ID = "milestoneId";
    public static final String ADHOC_ID = "adhocId";
    private CaseGraphFactoryImpl tested;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private GraphCommandManager graphCommandManager;

    private GraphCommandFactory graphCommandFactory;

    @Mock
    private GraphIndexBuilder<?> indexBuilder;

    @Mock
    private ServiceTaskFactory serviceTaskFactory;

    @Mock
    private WorkItemDefinitionBackendService workItemDefinitionService;

    @Mock
    private Node diagramNode;

    @Mock
    private Node milestoneNode;

    @Mock
    private ProjectMetadata projectMetadata;

    @Mock
    private AdapterManager adapters;

    @Mock
    private AdapterRegistry registry;

    @Mock
    private DefinitionAdapter<Object> adapter;

    private ServiceTask milestone;

    @Mock
    private Definition<BPMNDiagram> diagramContent;

    @Mock
    private BPMNDiagram diagramDefinition;

    @Mock
    private DiagramSet diagramSet;

    @Mock
    private AdHoc adHoc;

    @Mock
    private PropertyAdapter<Object, Object> propertyAdapter;

    @Before
    public void setUp() throws Exception {
        milestone = new ServiceTask();
        graphCommandFactory = new GraphCommandFactory();
        when(serviceTaskFactory.buildItem(MILESTONE)).thenReturn(milestone);
        when(definitionManager.adapters()).thenReturn(adapters);
        when(adapters.registry()).thenReturn(registry);
        when(registry.getDefinitionAdapter(ServiceTask.class)).thenReturn(adapter);
        when(adapter.getId(milestone)).thenReturn(DefinitionId.build(MILESTONE_ID));
        when(factoryManager.newElement(anyString(),
                                       eq(BPMNDiagramImpl.class))).thenReturn(diagramNode);
        when(factoryManager.newElement(anyString(),
                                       eq(MILESTONE_ID))).thenReturn(milestoneNode);
        when(diagramNode.getContent()).thenReturn(diagramContent);
        when(diagramContent.getDefinition()).thenReturn(diagramDefinition);
        when(diagramDefinition.getDiagramSet()).thenReturn(diagramSet);
        when(diagramSet.getAdHoc()).thenReturn(adHoc);
        when(adapters.forProperty()).thenReturn(propertyAdapter);
        when(propertyAdapter.getId(adHoc)).thenReturn(ADHOC_ID);
        when(diagramNode.getUUID()).thenReturn(UUID.uuid());
        Index index = mock(Index.class);
        when(indexBuilder.build(any(Graph.class))).thenReturn(index);

        tested = new CaseGraphFactoryImpl(definitionManager,
                                          factoryManager,
                                          ruleManager,
                                          graphCommandManager,
                                          graphCommandFactory,
                                          indexBuilder,
                                          serviceTaskFactory,
                                          workItemDefinitionService);
    }

    @Test
    public void build() {
        tested.build("uuid", "def", projectMetadata);
        verify(workItemDefinitionService).execute(projectMetadata);
    }

    @Test
    public void buildInitialisationCommands() {
        final List<Command> commands = tested.buildInitialisationCommands();
        assertEquals(commands.size(), 3);

        final AddNodeCommand addNodeCommand = (AddNodeCommand) commands.get(0);
        final AddChildNodeCommand addChildNodeCommand = (AddChildNodeCommand) commands.get(1);
        final UpdateElementPropertyValueCommand updatePropertyCommand = (UpdateElementPropertyValueCommand) commands.get(2);

        assertEquals(addNodeCommand.getCandidate(), diagramNode);
        assertEquals(addChildNodeCommand.getCandidate(), milestoneNode);
        assertEquals(addChildNodeCommand.getParent(), diagramNode);
        assertEquals(updatePropertyCommand.getElement(), diagramNode);
        assertEquals(updatePropertyCommand.getPropertyId(), ADHOC_ID);
        assertEquals(updatePropertyCommand.getValue(), true);
    }
}