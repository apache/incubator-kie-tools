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
package org.kie.workbench.common.stunner.cm.backend;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionBackendService;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Groupid;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseIdPrefix;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseRoles;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Height;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Width;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocOrdering;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CreatedBy;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Description;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Independent;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Skippable;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Subject;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.WaitForCompletion;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.properties.CaseManagementPropertyWriterFactory;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.UserTask;
import org.kie.workbench.common.stunner.cm.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.cm.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.cm.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.cm.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.cm.definition.property.task.AdHocCompletionCondition;
import org.kie.workbench.common.stunner.cm.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.cm.definition.property.task.CaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.cm.definition.property.task.ProcessReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.cm.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.cm.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.cm.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.BackendFactoryManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertyAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertySetAdapter;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSetImpl;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementDirectDiagramMarshallerTest {

    private static final String UUID_REGEX = "_[A-F0-9]{8}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{12}";
    private static final String ID_REGEX = "_[_a-zA-Z0-9\\-]{22}";

    private static final String CM_FILE = "org/kie/workbench/common/stunner/cm/backend/case.bpmn-cm";
    private static final String DIAGRAM_FILE = "org/kie/workbench/common/stunner/cm/backend/diagram.bpmn-cm";
    private static final String STAGE_FILE = "org/kie/workbench/common/stunner/cm/backend/stage.bpmn-cm";
    private static final String SUBCASE_FILE = "org/kie/workbench/common/stunner/cm/backend/subcase.bpmn-cm";
    private static final String SUBPROCESS_FILE = "org/kie/workbench/common/stunner/cm/backend/subprocess.bpmn-cm";
    private static final String TASK_FILE = "org/kie/workbench/common/stunner/cm/backend/task.bpmn-cm";

    @Mock
    DefinitionManager definitionManager;

    @Mock
    AdapterManager adapterManager;

    @Mock
    AdapterRegistry adapterRegistry;

    @Mock
    RuleManager rulesManager;

    BackendFactoryManager applicationFactoryManager;

    private CaseManagementDirectDiagramMarshaller tested;

    @Before
    public void setUp() throws Exception {
        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        // initApplicationFactoryManagerAlt();
        when(rulesManager.evaluate(any(RuleSet.class), any(RuleEvaluationContext.class)))
                .thenReturn(new DefaultRuleViolations());

        DefinitionUtils definitionUtils = new DefinitionUtils(definitionManager, applicationFactoryManager, null);
        TestScopeModelFactory testScopeModelFactory =
                new TestScopeModelFactory(new CaseManagementDefinitionSet.CaseManagementDefinitionSetBuilder().build());
        // Definition manager.
        final BackendDefinitionAdapter definitionAdapter = new BackendDefinitionAdapter(definitionUtils);
        final BackendDefinitionSetAdapter definitionSetAdapter = new BackendDefinitionSetAdapter(definitionAdapter);
        final BackendPropertySetAdapter propertySetAdapter = new BackendPropertySetAdapter();
        final BackendPropertyAdapter propertyAdapter = new BackendPropertyAdapter();
        mockAdapterManager(definitionAdapter, definitionSetAdapter, propertySetAdapter, propertyAdapter);
        mockAdapterRegistry(definitionAdapter, definitionSetAdapter, propertySetAdapter, propertyAdapter);
        applicationFactoryManager = new MockApplicationFactoryManager(definitionManager,
                                                                      new GraphFactoryImpl(definitionManager),
                                                                      testScopeModelFactory,
                                                                      new EdgeFactoryImpl(definitionManager),
                                                                      new NodeFactoryImpl(definitionUtils)
        );

        GraphCommandManagerImpl commandManager = new GraphCommandManagerImpl(null, null, null);
        GraphCommandFactory commandFactory = new GraphCommandFactory();

        // The work item definition service.
        WorkItemDefinitionBackendService widService = mock(WorkItemDefinitionBackendService.class);

        // The tested CM marshaller.
        tested = new CaseManagementDirectDiagramMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                                           definitionManager,
                                                           rulesManager,
                                                           widService,
                                                           applicationFactoryManager,
                                                           commandFactory,
                                                           commandManager);
    }

    private void mockAdapterRegistry(BackendDefinitionAdapter definitionAdapter,
                                     BackendDefinitionSetAdapter definitionSetAdapter,
                                     BackendPropertySetAdapter propertySetAdapter,
                                     BackendPropertyAdapter propertyAdapter) {
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
    }

    private void mockAdapterManager(BackendDefinitionAdapter definitionAdapter,
                                    BackendDefinitionSetAdapter definitionSetAdapter,
                                    BackendPropertySetAdapter propertySetAdapter,
                                    BackendPropertyAdapter propertyAdapter) {
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
    }

    private void hasElement(String result, String elementPattern) {
        hasElement(result, elementPattern, 1);
    }

    private void hasElement(String result, String elementPattern, int count) {
        Pattern pattern = Pattern.compile(elementPattern);
        Matcher matcher = pattern.matcher(result);

        for (int i = 0; i < count; i++) {
            assertTrue("Error results: " + result, matcher.find());
        }

        assertFalse("Error results: " + result, matcher.find());
    }

    private DiagramImpl createCase() {
        CaseManagementDiagram root = new CaseManagementDiagram();
        root.getDiagramSet().setName(new Name("Case"));
        root.getDiagramSet().setId(new Id("New Case Management diagram"));
        root.getDimensionsSet().setWidth(new Width(2800.0));
        root.getDimensionsSet().setHeight(new Height(1400.0));
        View<CaseManagementDiagram> rootContent = new ViewImpl<>(root, Bounds.create(0.0, 0.0, 2800.0, 1400.0));
        Node<View<CaseManagementDiagram>, Edge> rootNode = new NodeImpl<>("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");
        rootNode.getLabels().addAll(root.getLabels());
        rootNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram");
        rootNode.setContent(rootContent);

        AdHocSubprocess stage1 = new AdHocSubprocess();
        View<AdHocSubprocess> stage1Content = new ViewImpl<>(stage1, Bounds.create(0.0, 0.0, 175.0, 50.0));
        Node<View<AdHocSubprocess>, Edge> stage1Node = new NodeImpl<>("_F0A19BD0-3F42-493A-9A2D-2F4C24ED75D9");
        stage1Node.getLabels().addAll(stage1.getLabels());
        stage1Node.getLabels().add("org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess");
        stage1Node.setContent(stage1Content);

        Edge<Child, Node> stage1InEdge = new EdgeImpl<>("_CF1684C2-3D30-4FDE-A5AA-D88B81E08418");
        stage1InEdge.setSourceNode(rootNode);
        stage1InEdge.setTargetNode(stage1Node);
        stage1InEdge.setContent(new Child());
        rootNode.getOutEdges().add(stage1InEdge);
        stage1Node.getInEdges().add(stage1InEdge);

        UserTask task1 = new UserTask();
        View<UserTask> task1Content = new ViewImpl<>(task1, Bounds.create(0.0, 0.0, 153.0, 103.0));
        Node<View<UserTask>, Edge> task1Node = new NodeImpl<>("_E95AD08A-4595-4FA4-8948-3318D8BE7941");
        task1Node.getLabels().addAll(task1.getLabels());
        task1Node.getLabels().add("org.kie.workbench.common.stunner.cm.definition.UserTask");
        task1Node.setContent(task1Content);

        Edge<Child, Node> task1InEdge = new EdgeImpl<>("_B24CB4A4-93A0-4BC0-87A5-BD3968CC184F");
        task1InEdge.setSourceNode(stage1Node);
        task1InEdge.setTargetNode(task1Node);
        task1InEdge.setContent(new Child());
        stage1Node.getOutEdges().add(task1InEdge);
        task1Node.getInEdges().add(task1InEdge);

        CaseReusableSubprocess case1 = new CaseReusableSubprocess();
        View<CaseReusableSubprocess> case1Content = new ViewImpl<>(case1, Bounds.create(0.0, 0.0, 153.0, 103.0));
        Node<View<CaseReusableSubprocess>, Edge> case1Node = new NodeImpl<>("_C468418F-A1EE-470A-BC30-D85888DF3DF7");
        case1Node.getLabels().addAll(case1.getLabels());
        case1Node.getLabels().add("org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess");
        case1Node.setContent(case1Content);

        Edge<Child, Node> case1InEdge = new EdgeImpl<>("_17571CDC-9736-4110-B7EB-27C0EA959AA0");
        case1InEdge.setSourceNode(stage1Node);
        case1InEdge.setTargetNode(case1Node);
        case1InEdge.setContent(new Child());
        stage1Node.getOutEdges().add(case1InEdge);
        case1Node.getInEdges().add(case1InEdge);

        AdHocSubprocess stage2 = new AdHocSubprocess();
        View<AdHocSubprocess> stage2Content = new ViewImpl<>(stage2, Bounds.create(0.0, 0.0, 175.0, 50.0));
        Node<View<AdHocSubprocess>, Edge> stage2Node = new NodeImpl<>("_BCD8C7E1-9833-407D-9833-E12763A9A63D");
        stage2Node.getLabels().addAll(stage2.getLabels());
        stage2Node.getLabels().add("org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess");
        stage2Node.setContent(stage2Content);

        Edge<Child, Node> stage2InEdge = new EdgeImpl<>("_2FBF1046-A4BA-4407-A68B-20F2A5DEB5A4");
        stage2InEdge.setSourceNode(rootNode);
        stage2InEdge.setTargetNode(stage2Node);
        stage2InEdge.setContent(new Child());
        rootNode.getOutEdges().add(stage2InEdge);
        stage2Node.getInEdges().add(stage2InEdge);

        CaseReusableSubprocess case2 = new CaseReusableSubprocess();
        View<CaseReusableSubprocess> case2Content = new ViewImpl<>(case2, Bounds.create(0.0, 0.0, 153.0, 103.0));
        Node<View<CaseReusableSubprocess>, Edge> case2Node = new NodeImpl<>("_4DF08597-2D2D-4CEE-B0EF-1AF0ED4ADAC2");
        case2Node.getLabels().addAll(case2.getLabels());
        case2Node.getLabels().add("org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess");
        case2Node.setContent(case2Content);

        Edge<Child, Node> case2InEdge = new EdgeImpl<>("_A8727CFC-E58C-4876-BCDD-E4C75FDD1252");
        case2InEdge.setSourceNode(stage2Node);
        case2InEdge.setTargetNode(case2Node);
        case2InEdge.setContent(new Child());
        stage2Node.getOutEdges().add(case2InEdge);
        case2Node.getInEdges().add(case2InEdge);

        ProcessReusableSubprocess process2 = new ProcessReusableSubprocess();
        View<ProcessReusableSubprocess> process2Content = new ViewImpl<>(process2, Bounds.create(0.0, 0.0, 153.0, 103.0));
        Node<View<ProcessReusableSubprocess>, Edge> process2Node = new NodeImpl<>("_438D1DB6-4161-43C5-86F5-FC6B0F97BA7B");
        process2Node.getLabels().addAll(process2.getLabels());
        process2Node.getLabels().add("org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess");
        process2Node.setContent(process2Content);

        Edge<Child, Node> process2InEdge = new EdgeImpl<>("_8B517D71-EC7A-441C-91EB-5AF86BC11974");
        process2InEdge.setSourceNode(stage2Node);
        process2InEdge.setTargetNode(process2Node);
        process2InEdge.setContent(new Child());
        stage2Node.getOutEdges().add(process2InEdge);
        process2Node.getInEdges().add(process2InEdge);

        DefinitionSet definitionSet = new DefinitionSetImpl("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");

        Graph graph = new GraphImpl<>("_E0752AEB-6594-483D-9757-F147960EA60A", new GraphNodeStoreImpl());
        graph.getLabels().add("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        graph.setContent(definitionSet);
        graph.addNode(rootNode);
        graph.addNode(stage1Node);
        graph.addNode(stage2Node);
        graph.addNode(task1Node);
        graph.addNode(case1Node);
        graph.addNode(case2Node);
        graph.addNode(process2Node);

        MetadataImpl metaData = new MetadataImpl();
        metaData.setDefinitionSetId("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        metaData.setTitle("New Case Management diagram");
        metaData.setShapeSetId("org.kie.workbench.common.stunner.cm.client.CaseManagementShapeSet");
        metaData.setCanvasRootUUID("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");

        DiagramImpl diagram = new DiagramImpl("_D518B746-92D2-4BF1-8AD1-1EBA552C5F6F", metaData);
        diagram.setGraph(graph);

        return diagram;
    }

    @Test
    public void testMarshall() throws Exception {
        String result = tested.marshall(createCase());

        // all nodes saved
        hasElement(result,
                   "<bpmn2:adHocSubProcess id=\"_BCD8C7E1-9833-407D-9833-E12763A9A63D\" name=\"Stage\" ordering=\"Sequential\">");
        hasElement(result,
                   "<bpmn2:adHocSubProcess id=\"_F0A19BD0-3F42-493A-9A2D-2F4C24ED75D9\" name=\"Stage\" ordering=\"Sequential\">");
        hasElement(result,
                   "<bpmn2:userTask id=\"_E95AD08A-4595-4FA4-8948-3318D8BE7941\" name=\"Task\">");
        hasElement(result,
                   "<bpmn2:callActivity id=\"_C468418F-A1EE-470A-BC30-D85888DF3DF7\" drools:independent=\"false\" drools:waitForCompletion=\"true\" name=\"Subcase\" calledElement=\"\">");
        hasElement(result,
                   "<bpmn2:callActivity id=\"_4DF08597-2D2D-4CEE-B0EF-1AF0ED4ADAC2\" drools:independent=\"false\" drools:waitForCompletion=\"true\" name=\"Subcase\" calledElement=\"\">");
        hasElement(result,
                   "<bpmn2:callActivity id=\"_438D1DB6-4161-43C5-86F5-FC6B0F97BA7B\" drools:independent=\"false\" drools:waitForCompletion=\"true\" name=\"Subprocess\" calledElement=\"\">");

        // case saved
        hasElement(result,
                   "<drools:metaData name=\"case\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[true]]></drools:metaValue>") + "(\\s*)</drools:metaData>",
                   2);

        // start and end event created
        hasElement(result,
                   "<bpmn2:startEvent id=\"" + UUID_REGEX + "\">(\\s*)<bpmn2:outgoing>" + UUID_REGEX + "</bpmn2:outgoing>(\\s*)</bpmn2:startEvent>");
        hasElement(result,
                   "<bpmn2:endEvent id=\"" + UUID_REGEX + "\">(\\s*)<bpmn2:incoming>" + UUID_REGEX + "</bpmn2:incoming>(\\s*)</bpmn2:endEvent>");

        // sequence flow created between stages
        hasElement(result,
                   "<bpmn2:sequenceFlow id=\"(" + UUID_REGEX + ")\" sourceRef=\"(" + UUID_REGEX + ")\" targetRef=\"_F0A19BD0-3F42-493A-9A2D-2F4C24ED75D9\"/>");
        hasElement(result,
                   "<bpmn2:sequenceFlow id=\"(" + UUID_REGEX + ")\" sourceRef=\"_F0A19BD0-3F42-493A-9A2D-2F4C24ED75D9\" targetRef=\"_BCD8C7E1-9833-407D-9833-E12763A9A63D\"/>");
        hasElement(result,
                   "<bpmn2:sequenceFlow id=\"(" + UUID_REGEX + ")\" sourceRef=\"_BCD8C7E1-9833-407D-9833-E12763A9A63D\" targetRef=\"(" + UUID_REGEX + ")\"/>");

        // sequence flow created inside stages
        hasElement(result,
                   "<bpmn2:sequenceFlow id=\"(" + UUID_REGEX + ")\" sourceRef=\"_4DF08597-2D2D-4CEE-B0EF-1AF0ED4ADAC2\" targetRef=\"_438D1DB6-4161-43C5-86F5-FC6B0F97BA7B\"/>");
        hasElement(result,
                   "<bpmn2:sequenceFlow id=\"(" + UUID_REGEX + ")\" sourceRef=\"_E95AD08A-4595-4FA4-8948-3318D8BE7941\" targetRef=\"_C468418F-A1EE-470A-BC30-D85888DF3DF7\"/>");
    }

    private DiagramImpl createDiagram() {
        CaseManagementDiagram root = new CaseManagementDiagram();

        root.getDiagramSet().setName(new Name("DiagramTest"));
        root.getDiagramSet().setId(new Id("DiagramTest"));
        root.getDiagramSet().setDocumentation(new Documentation("DiagramTest"));
        root.getDiagramSet().setPackageProperty(new Package("DiagramTest"));
        root.getDiagramSet().setVersion(new Version("2.0"));
        root.getDiagramSet().setAdHoc(new AdHoc(true));
        root.getDiagramSet().setProcessInstanceDescription(new ProcessInstanceDescription("DiagramTest"));

        root.getProcessData().setProcessVariables(new ProcessVariables("DiagramTest:Boolean"));

        root.getCaseManagementSet().setCaseIdPrefix(new CaseIdPrefix("DiagramTest"));
        root.getCaseManagementSet().setCaseRoles(new CaseRoles("DiagramTest:5"));
        root.getCaseManagementSet().setCaseFileVariables(new CaseFileVariables("DiagramTest:Boolean"));

        root.getDimensionsSet().setWidth(new Width(2800.0));
        root.getDimensionsSet().setHeight(new Height(1400.0));

        View<CaseManagementDiagram> rootContent = new ViewImpl<>(root, Bounds.create(0.0, 0.0, 2800.0, 1400.0));
        Node<View<CaseManagementDiagram>, Edge> rootNode = new NodeImpl<>("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");
        rootNode.getLabels().addAll(root.getLabels());
        rootNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram");
        rootNode.setContent(rootContent);

        DefinitionSet definitionSet = new DefinitionSetImpl("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");

        Graph graph = new GraphImpl<>("_E0752AEB-6594-483D-9757-F147960EA60A", new GraphNodeStoreImpl());
        graph.getLabels().add("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        graph.setContent(definitionSet);
        graph.addNode(rootNode);

        MetadataImpl metaData = new MetadataImpl();
        metaData.setDefinitionSetId("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        metaData.setTitle("New Case Management diagram");
        metaData.setShapeSetId("org.kie.workbench.common.stunner.cm.client.CaseManagementShapeSet");
        metaData.setCanvasRootUUID("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");

        DiagramImpl diagram = new DiagramImpl("_D518B746-92D2-4BF1-8AD1-1EBA552C5F6F", metaData);
        diagram.setGraph(graph);

        return diagram;
    }

    @Test
    public void testMarshall_diagram() throws Exception {
        String result = tested.marshall(createDiagram());

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_DiagramTestItem\" structureRef=\"Boolean\"/>");

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_caseFile_DiagramTestItem\" structureRef=\"Boolean\"/>");

        hasElement(result,
                   "<bpmn2:process id=\"DiagramTest\" drools:packageName=\"DiagramTest\" drools:version=\"2.0\" drools:adHoc=\"true\" " +
                           "name=\"DiagramTest\" isExecutable=\"true\">");

        hasElement(result,
                   "<bpmn2:documentation id=\"" + ID_REGEX + "\">" + Pattern.quote("<![CDATA[DiagramTest]]>") + "</bpmn2:documentation>");

        hasElement(result,
                   "<drools:metaData name=\"customDescription\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[DiagramTest]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:metaData name=\"customCaseIdPrefix\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[DiagramTest]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:metaData name=\"customCaseRoles\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[DiagramTest:5]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<bpmn2:property id=\"DiagramTest\" itemSubjectRef=\"_DiagramTestItem\" name=\"DiagramTest\"/>");

        hasElement(result,
                   "<bpmn2:property id=\"caseFile_DiagramTest\" itemSubjectRef=\"_caseFile_DiagramTestItem\" name=\"caseFile_DiagramTest\"/>");
    }

    private DiagramImpl createStage() {
        CaseManagementDiagram root = new CaseManagementDiagram();
        root.getDiagramSet().setName(new Name("StageTest"));
        root.getDiagramSet().setId(new Id("StageTest"));
        root.getDimensionsSet().setWidth(new Width(2800.0));
        root.getDimensionsSet().setHeight(new Height(1400.0));

        View<CaseManagementDiagram> rootContent = new ViewImpl<>(root, Bounds.create(0.0, 0.0, 2800.0, 1400.0));
        Node<View<CaseManagementDiagram>, Edge> rootNode = new NodeImpl<>("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");
        rootNode.getLabels().addAll(root.getLabels());
        rootNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram");
        rootNode.setContent(rootContent);

        AdHocSubprocess stage = new AdHocSubprocess();

        stage.getGeneral().setName(new Name("StageTest"));
        stage.getGeneral().setDocumentation(new Documentation("StageTest"));

        stage.getProcessData().setProcessVariables(new ProcessVariables("StageTest:Boolean"));

        stage.getExecutionSet().setAdHocOrdering(new AdHocOrdering("Sequential"));

        stage.getExecutionSet().setAdHocAutostart(new AdHocAutostart(true));

        ScriptTypeValue completionCondition = new ScriptTypeValue();
        completionCondition.setScript("autocomplete");
        completionCondition.setLanguage("drools");
        stage.getExecutionSet().setAdHocCompletionCondition(new AdHocCompletionCondition(completionCondition));

        ScriptTypeValue entryAction = new ScriptTypeValue();
        entryAction.setScript("StageTest");
        entryAction.setLanguage("java");
        stage.getExecutionSet().setOnEntryAction(new OnEntryAction(new ScriptTypeListValue(Collections.singletonList(entryAction))));

        ScriptTypeValue exitAction = new ScriptTypeValue();
        exitAction.setScript("StageTest");
        exitAction.setLanguage("java");
        stage.getExecutionSet().setOnExitAction(new OnExitAction(new ScriptTypeListValue(Collections.singletonList(exitAction))));

        View<AdHocSubprocess> stageContent = new ViewImpl<>(stage, Bounds.create(0.0, 0.0, 175.0, 50.0));
        Node<View<AdHocSubprocess>, Edge> stageNode = new NodeImpl<>("_F0A19BD0-3F42-493A-9A2D-2F4C24ED75D9");
        stageNode.getLabels().addAll(stage.getLabels());
        stageNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess");
        stageNode.setContent(stageContent);

        Edge<Child, Node> stageInEdge = new EdgeImpl<>("_CF1684C2-3D30-4FDE-A5AA-D88B81E08418");
        stageInEdge.setSourceNode(rootNode);
        stageInEdge.setTargetNode(stageNode);
        stageInEdge.setContent(new Child());
        rootNode.getOutEdges().add(stageInEdge);
        stageNode.getInEdges().add(stageInEdge);

        DefinitionSet definitionSet = new DefinitionSetImpl("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");

        Graph graph = new GraphImpl<>("_E0752AEB-6594-483D-9757-F147960EA60A", new GraphNodeStoreImpl());
        graph.getLabels().add("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        graph.setContent(definitionSet);
        graph.addNode(rootNode);
        graph.addNode(stageNode);

        MetadataImpl metaData = new MetadataImpl();
        metaData.setDefinitionSetId("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        metaData.setTitle("New Case Management diagram");
        metaData.setShapeSetId("org.kie.workbench.common.stunner.cm.client.CaseManagementShapeSet");
        metaData.setCanvasRootUUID("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");

        DiagramImpl diagram = new DiagramImpl("_D518B746-92D2-4BF1-8AD1-1EBA552C5F6F", metaData);
        diagram.setGraph(graph);

        return diagram;
    }

    @Test
    public void testMarshall_stage() throws Exception {
        String result = tested.marshall(createStage());

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_StageTestItem\" structureRef=\"Boolean\"/>");

        hasElement(result,
                   "<bpmn2:process id=\"StageTest\" drools:version=\"1.0\" drools:adHoc=\"true\" name=\"StageTest\" isExecutable=\"true\">");

        hasElement(result,
                   "<bpmn2:adHocSubProcess id=\"" + UUID_REGEX + "\" name=\"StageTest\" ordering=\"Sequential\">");

        hasElement(result,
                   "<bpmn2:documentation id=\"" + ID_REGEX + "\">" + Pattern.quote("<![CDATA[StageTest]]>") + "</bpmn2:documentation>");

        hasElement(result,
                   "<drools:metaData name=\"elementname\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[StageTest]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:metaData name=\"customAutoStart\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[true]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:onEntry-script scriptFormat=\"http://www.java.com/java\">(\\s*)"
                           + Pattern.quote("<drools:script><![CDATA[StageTest]]></drools:script>")
                           + "(\\s*)</drools:onEntry-script>");

        hasElement(result,
                   "<drools:onExit-script scriptFormat=\"http://www.java.com/java\">(\\s*)"
                           + Pattern.quote("<drools:script><![CDATA[StageTest]]></drools:script>")
                           + "(\\s*)</drools:onExit-script>");

        hasElement(result,
                   "<bpmn2:completionCondition xsi:type=\"bpmn2:tFormalExpression\" id=\"" + ID_REGEX
                           + "\" language=\"http://www.jboss.org/drools/rule\">"
                           + Pattern.quote("<![CDATA[autocomplete]]>") + "</bpmn2:completionCondition>");

        hasElement(result,
                   "<bpmn2:property id=\"StageTest\" itemSubjectRef=\"_StageTestItem\" name=\"StageTest\"/>");
    }

    private DiagramImpl createSubcase() {
        CaseManagementDiagram root = new CaseManagementDiagram();
        root.getDiagramSet().setName(new Name("SubcaseTest"));
        root.getDiagramSet().setId(new Id("SubcaseTest"));
        root.getDimensionsSet().setWidth(new Width(2800.0));
        root.getDimensionsSet().setHeight(new Height(1400.0));

        View<CaseManagementDiagram> rootContent = new ViewImpl<>(root, Bounds.create(0.0, 0.0, 2800.0, 1400.0));
        Node<View<CaseManagementDiagram>, Edge> rootNode = new NodeImpl<>("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");
        rootNode.getLabels().addAll(root.getLabels());
        rootNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram");
        rootNode.setContent(rootContent);

        AdHocSubprocess stage = new AdHocSubprocess();
        stage.getGeneral().setName(new Name("StageSubcaseTest"));

        View<AdHocSubprocess> stageContent = new ViewImpl<>(stage, Bounds.create(0.0, 0.0, 175.0, 50.0));
        Node<View<AdHocSubprocess>, Edge> stageNode = new NodeImpl<>("_F0A19BD0-3F42-493A-9A2D-2F4C24ED75D9");
        stageNode.getLabels().addAll(stage.getLabels());
        stageNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess");
        stageNode.setContent(stageContent);

        Edge<Child, Node> stageInEdge = new EdgeImpl<>("_CF1684C2-3D30-4FDE-A5AA-D88B81E08418");
        stageInEdge.setSourceNode(rootNode);
        stageInEdge.setTargetNode(stageNode);
        stageInEdge.setContent(new Child());
        rootNode.getOutEdges().add(stageInEdge);
        stageNode.getInEdges().add(stageInEdge);

        CaseReusableSubprocess subcase = new CaseReusableSubprocess();
        subcase.getGeneral().setName(new Name("SubcaseTest"));
        subcase.getGeneral().setDocumentation(new Documentation("SubcaseTest"));

        CaseReusableSubprocessTaskExecutionSet subcaseExecutionSet = subcase.getExecutionSet();
        subcaseExecutionSet.setIndependent(new Independent(true));
        subcaseExecutionSet.setIsAsync(new IsAsync(true));
        subcaseExecutionSet.setWaitForCompletion(new WaitForCompletion(true));
        subcaseExecutionSet.setAdHocAutostart(new AdHocAutostart(true));

        ScriptTypeValue entryAction = new ScriptTypeValue();
        entryAction.setScript(("SubcaseTest"));
        entryAction.setLanguage("java");
        subcaseExecutionSet.setOnEntryAction(new OnEntryAction(new ScriptTypeListValue(Collections.singletonList(entryAction))));

        ScriptTypeValue exitAction = new ScriptTypeValue();
        exitAction.setScript("SubcaseTest");
        exitAction.setLanguage("java");
        subcaseExecutionSet.setOnExitAction(new OnExitAction(new ScriptTypeListValue(Collections.singletonList(exitAction))));

        DataIOSet dataIOSet = new DataIOSet();
        dataIOSet.setAssignmentsinfo(new AssignmentsInfo("|SubcaseTest:Boolean||SubcaseTest:Boolean|[din]SubcaseTest=true"));
        subcase.setDataIOSet(dataIOSet);

        View<CaseReusableSubprocess> subcaseContent = new ViewImpl<>(subcase, Bounds.create(0.0, 0.0, 153.0, 103.0));
        Node<View<CaseReusableSubprocess>, Edge> subcaseNode = new NodeImpl<>("_C468418F-A1EE-470A-BC30-D85888DF3DF7");
        subcaseNode.getLabels().addAll(subcase.getLabels());
        subcaseNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess");
        subcaseNode.setContent(subcaseContent);

        Edge<Child, Node> subcaseInEdge = new EdgeImpl<>("_17571CDC-9736-4110-B7EB-27C0EA959AA0");
        subcaseInEdge.setSourceNode(stageNode);
        subcaseInEdge.setTargetNode(subcaseNode);
        subcaseInEdge.setContent(new Child());
        stageNode.getOutEdges().add(subcaseInEdge);
        subcaseNode.getInEdges().add(subcaseInEdge);

        DefinitionSet definitionSet = new DefinitionSetImpl("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");

        Graph graph = new GraphImpl<>("_E0752AEB-6594-483D-9757-F147960EA60A", new GraphNodeStoreImpl());
        graph.getLabels().add("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        graph.setContent(definitionSet);
        graph.addNode(rootNode);
        graph.addNode(stageNode);
        graph.addNode(subcaseNode);

        MetadataImpl metaData = new MetadataImpl();
        metaData.setDefinitionSetId("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        metaData.setTitle("New Case Management diagram");
        metaData.setShapeSetId("org.kie.workbench.common.stunner.cm.client.CaseManagementShapeSet");
        metaData.setCanvasRootUUID("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");

        DiagramImpl diagram = new DiagramImpl("_D518B746-92D2-4BF1-8AD1-1EBA552C5F6F", metaData);
        diagram.setGraph(graph);

        return diagram;
    }

    @Test
    public void testMarshall_subcase() throws Exception {
        String result = tested.marshall(createSubcase());

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_" + UUID_REGEX + "_SubcaseTestInputXItem\" structureRef=\"Boolean\"/>");

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_+" + UUID_REGEX + "_SubcaseTestOutputXItem\" structureRef=\"Boolean\"/>");

        hasElement(result,
                   "<bpmn2:process id=\"SubcaseTest\" drools:version=\"1.0\" drools:adHoc=\"true\" name=\"SubcaseTest\" isExecutable=\"true\">");

        hasElement(result,
                   "<bpmn2:adHocSubProcess id=\"" + UUID_REGEX + "\" name=\"StageSubcaseTest\" ordering=\"Sequential\">");

        hasElement(result,
                   "<bpmn2:callActivity id=\"" + UUID_REGEX + "\" drools:independent=\"true\" drools:waitForCompletion=\"true\" name=\"SubcaseTest\" calledElement=\"\">");

        hasElement(result,
                   "<bpmn2:documentation id=\"" + ID_REGEX + "\">" + Pattern.quote("<![CDATA[SubcaseTest]]>") + "</bpmn2:documentation>");

        hasElement(result,
                   "<drools:metaData name=\"elementname\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[SubcaseTest]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:onEntry-script scriptFormat=\"http://www.java.com/java\">(\\s*)"
                           + Pattern.quote("<drools:script><![CDATA[SubcaseTest]]></drools:script>")
                           + "(\\s*)</drools:onEntry-script>");

        hasElement(result,
                   "<drools:onExit-script scriptFormat=\"http://www.java.com/java\">(\\s*)"
                           + Pattern.quote("<drools:script><![CDATA[SubcaseTest]]></drools:script>")
                           + "(\\s*)</drools:onExit-script>");

        hasElement(result,
                   "<drools:metaData name=\"customAsync\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[true]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:metaData name=\"case\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[true]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:metaData name=\"customAutoStart\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[true]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<bpmn2:ioSpecification id=\"" + ID_REGEX + "\">");

        hasElement(result,
                   "<bpmn2:dataInput id=\"" + UUID_REGEX + "_SubcaseTestInputX\" drools:dtype=\"Boolean\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_SubcaseTestInputXItem\" name=\"SubcaseTest\"/>");

        hasElement(result,
                   "<bpmn2:dataOutput id=\"" + UUID_REGEX + "_SubcaseTestOutputX\" drools:dtype=\"Boolean\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_SubcaseTestOutputXItem\" name=\"SubcaseTest\"/>");

        hasElement(result,
                   "<bpmn2:inputSet id=\"" + ID_REGEX + "\">(\\s*)"
                           + "<bpmn2:dataInputRefs>" + UUID_REGEX + "_SubcaseTestInputX</bpmn2:dataInputRefs>"
                           + "(\\s*)</bpmn2:inputSet>");

        hasElement(result,
                   "<bpmn2:outputSet id=\"" + ID_REGEX + "\">(\\s*)"
                           + "<bpmn2:dataOutputRefs>" + UUID_REGEX + "_SubcaseTestOutputX</bpmn2:dataOutputRefs>"
                           + "(\\s*)</bpmn2:outputSet>");

        hasElement(result,
                   "<bpmn2:dataInputAssociation id=\"" + ID_REGEX + "\">");

        hasElement(result,
                   "<bpmn2:targetRef>" + UUID_REGEX + "_SubcaseTestInputX</bpmn2:targetRef>");

        hasElement(result,
                   "<bpmn2:assignment id=\"" + ID_REGEX + "\">");

        hasElement(result,
                   "<bpmn2:from xsi:type=\"bpmn2:tFormalExpression\" id=\"" + ID_REGEX + "\">" + Pattern.quote("<![CDATA[true]]>") + "</bpmn2:from>");

        hasElement(result,
                   "<bpmn2:to xsi:type=\"bpmn2:tFormalExpression\" id=\"" + ID_REGEX + "\">" + UUID_REGEX + "_SubcaseTestInputX</bpmn2:to>");
    }

    private DiagramImpl createSubprocess() {
        CaseManagementDiagram root = new CaseManagementDiagram();
        root.getDiagramSet().setName(new Name("SubprocessTest"));
        root.getDiagramSet().setId(new Id("SubprocessTest"));
        root.getDimensionsSet().setWidth(new Width(2800.0));
        root.getDimensionsSet().setHeight(new Height(1400.0));

        View<CaseManagementDiagram> rootContent = new ViewImpl<>(root, Bounds.create(0.0, 0.0, 2800.0, 1400.0));
        Node<View<CaseManagementDiagram>, Edge> rootNode = new NodeImpl<>("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");
        rootNode.getLabels().addAll(root.getLabels());
        rootNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram");
        rootNode.setContent(rootContent);

        AdHocSubprocess stage = new AdHocSubprocess();
        stage.getGeneral().setName(new Name("StageSubprocessTest"));

        View<AdHocSubprocess> stageContent = new ViewImpl<>(stage, Bounds.create(0.0, 0.0, 175.0, 50.0));
        Node<View<AdHocSubprocess>, Edge> stageNode = new NodeImpl<>("_F0A19BD0-3F42-493A-9A2D-2F4C24ED75D9");
        stageNode.getLabels().addAll(stage.getLabels());
        stageNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess");
        stageNode.setContent(stageContent);

        Edge<Child, Node> stageInEdge = new EdgeImpl<>("_CF1684C2-3D30-4FDE-A5AA-D88B81E08418");
        stageInEdge.setSourceNode(rootNode);
        stageInEdge.setTargetNode(stageNode);
        stageInEdge.setContent(new Child());
        rootNode.getOutEdges().add(stageInEdge);
        stageNode.getInEdges().add(stageInEdge);

        ProcessReusableSubprocess subprocess = new ProcessReusableSubprocess();
        subprocess.getGeneral().setName(new Name("SubprocessTest"));
        subprocess.getGeneral().setDocumentation(new Documentation("SubprocessTest"));

        ProcessReusableSubprocessTaskExecutionSet subprocessExecutionSet = subprocess.getExecutionSet();
        subprocessExecutionSet.setIndependent(new Independent(true));
        subprocessExecutionSet.setIsAsync(new IsAsync(true));
        subprocessExecutionSet.setWaitForCompletion(new WaitForCompletion(true));
        subprocessExecutionSet.setAdHocAutostart(new AdHocAutostart(true));

        ScriptTypeValue entryAction = new ScriptTypeValue();
        entryAction.setScript(("SubprocessTest"));
        entryAction.setLanguage("java");
        subprocessExecutionSet.setOnEntryAction(new OnEntryAction(new ScriptTypeListValue(Collections.singletonList(entryAction))));

        ScriptTypeValue exitAction = new ScriptTypeValue();
        exitAction.setScript("SubprocessTest");
        exitAction.setLanguage("java");
        subprocessExecutionSet.setOnExitAction(new OnExitAction(new ScriptTypeListValue(Collections.singletonList(exitAction))));

        DataIOSet dataIOSet = new DataIOSet();
        dataIOSet.setAssignmentsinfo(new AssignmentsInfo("|SubprocessTest:Boolean||SubprocessTest:Boolean|[din]SubprocessTest=true"));
        subprocess.setDataIOSet(dataIOSet);

        View<ProcessReusableSubprocess> subprocessContent = new ViewImpl<>(subprocess, Bounds.create(0.0, 0.0, 153.0, 103.0));
        Node<View<ProcessReusableSubprocess>, Edge> subprocessNode = new NodeImpl<>("_C468418F-A1EE-470A-BC30-D85888DF3DF7");
        subprocessNode.getLabels().addAll(subprocess.getLabels());
        subprocessNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess");
        subprocessNode.setContent(subprocessContent);

        Edge<Child, Node> subprocessInEdge = new EdgeImpl<>("_17571CDC-9736-4110-B7EB-27C0EA959AA0");
        subprocessInEdge.setSourceNode(stageNode);
        subprocessInEdge.setTargetNode(subprocessNode);
        subprocessInEdge.setContent(new Child());
        stageNode.getOutEdges().add(subprocessInEdge);
        subprocessNode.getInEdges().add(subprocessInEdge);

        DefinitionSet definitionSet = new DefinitionSetImpl("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");

        Graph graph = new GraphImpl<>("_E0752AEB-6594-483D-9757-F147960EA60A", new GraphNodeStoreImpl());
        graph.getLabels().add("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        graph.setContent(definitionSet);
        graph.addNode(rootNode);
        graph.addNode(stageNode);
        graph.addNode(subprocessNode);

        MetadataImpl metaData = new MetadataImpl();
        metaData.setDefinitionSetId("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        metaData.setTitle("New Case Management diagram");
        metaData.setShapeSetId("org.kie.workbench.common.stunner.cm.client.CaseManagementShapeSet");
        metaData.setCanvasRootUUID("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");

        DiagramImpl diagram = new DiagramImpl("_D518B746-92D2-4BF1-8AD1-1EBA552C5F6F", metaData);
        diagram.setGraph(graph);

        return diagram;
    }

    @Test
    public void testMarshall_subprocess() throws Exception {
        String result = tested.marshall(createSubprocess());

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_" + UUID_REGEX + "_SubprocessTestInputXItem\" structureRef=\"Boolean\"/>");

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_+" + UUID_REGEX + "_SubprocessTestOutputXItem\" structureRef=\"Boolean\"/>");

        hasElement(result,
                   "<bpmn2:process id=\"SubprocessTest\" drools:version=\"1.0\" drools:adHoc=\"true\" name=\"SubprocessTest\" isExecutable=\"true\">");

        hasElement(result,
                   "<bpmn2:adHocSubProcess id=\"" + UUID_REGEX + "\" name=\"StageSubprocessTest\" ordering=\"Sequential\">");

        hasElement(result,
                   "<bpmn2:callActivity id=\"" + UUID_REGEX + "\" drools:independent=\"true\" drools:waitForCompletion=\"true\" name=\"SubprocessTest\" calledElement=\"\">");

        hasElement(result,
                   "<bpmn2:documentation id=\"" + ID_REGEX + "\">" + Pattern.quote("<![CDATA[SubprocessTest]]>") + "</bpmn2:documentation>");

        hasElement(result,
                   "<drools:metaData name=\"elementname\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[SubprocessTest]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:onEntry-script scriptFormat=\"http://www.java.com/java\">(\\s*)"
                           + Pattern.quote("<drools:script><![CDATA[SubprocessTest]]></drools:script>")
                           + "(\\s*)</drools:onEntry-script>");

        hasElement(result,
                   "<drools:onExit-script scriptFormat=\"http://www.java.com/java\">(\\s*)"
                           + Pattern.quote("<drools:script><![CDATA[SubprocessTest]]></drools:script>")
                           + "(\\s*)</drools:onExit-script>");

        hasElement(result,
                   "<drools:metaData name=\"customAsync\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[true]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:metaData name=\"customAutoStart\">(\\s*)" + Pattern.quote("<drools:metaValue><![CDATA[true]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<bpmn2:ioSpecification id=\"" + ID_REGEX + "\">");

        hasElement(result,
                   "<bpmn2:dataInput id=\"" + UUID_REGEX + "_SubprocessTestInputX\" drools:dtype=\"Boolean\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_SubprocessTestInputXItem\" name=\"SubprocessTest\"/>");

        hasElement(result,
                   "<bpmn2:dataOutput id=\"" + UUID_REGEX + "_SubprocessTestOutputX\" drools:dtype=\"Boolean\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_SubprocessTestOutputXItem\" name=\"SubprocessTest\"/>");

        hasElement(result,
                   "<bpmn2:inputSet id=\"" + ID_REGEX + "\">(\\s*)"
                           + "<bpmn2:dataInputRefs>" + UUID_REGEX + "_SubprocessTestInputX</bpmn2:dataInputRefs>"
                           + "(\\s*)</bpmn2:inputSet>");

        hasElement(result,
                   "<bpmn2:outputSet id=\"" + ID_REGEX + "\">(\\s*)"
                           + "<bpmn2:dataOutputRefs>" + UUID_REGEX + "_SubprocessTestOutputX</bpmn2:dataOutputRefs>"
                           + "(\\s*)</bpmn2:outputSet>");

        hasElement(result,
                   "<bpmn2:dataInputAssociation id=\"" + ID_REGEX + "\">");

        hasElement(result,
                   "<bpmn2:targetRef>" + UUID_REGEX + "_SubprocessTestInputX</bpmn2:targetRef>");

        hasElement(result,
                   "<bpmn2:assignment id=\"" + ID_REGEX + "\">");

        hasElement(result,
                   "<bpmn2:from xsi:type=\"bpmn2:tFormalExpression\" id=\"" + ID_REGEX + "\">" + Pattern.quote("<![CDATA[true]]>") + "</bpmn2:from>");

        hasElement(result,
                   "<bpmn2:to xsi:type=\"bpmn2:tFormalExpression\" id=\"" + ID_REGEX + "\">" + UUID_REGEX + "_SubprocessTestInputX</bpmn2:to>");
    }

    private DiagramImpl createTask() {
        CaseManagementDiagram root = new CaseManagementDiagram();
        root.getDiagramSet().setName(new Name("TaskTest"));
        root.getDiagramSet().setId(new Id("TaskTest"));
        root.getDimensionsSet().setWidth(new Width(2800.0));
        root.getDimensionsSet().setHeight(new Height(1400.0));

        View<CaseManagementDiagram> rootContent = new ViewImpl<>(root, Bounds.create(0.0, 0.0, 2800.0, 1400.0));
        Node<View<CaseManagementDiagram>, Edge> rootNode = new NodeImpl<>("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");
        rootNode.getLabels().addAll(root.getLabels());
        rootNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram");
        rootNode.setContent(rootContent);

        AdHocSubprocess stage = new AdHocSubprocess();
        stage.getGeneral().setName(new Name("StageTaskTest"));

        View<AdHocSubprocess> stageContent = new ViewImpl<>(stage, Bounds.create(0.0, 0.0, 175.0, 50.0));
        Node<View<AdHocSubprocess>, Edge> stageNode = new NodeImpl<>("_F0A19BD0-3F42-493A-9A2D-2F4C24ED75D9");
        stageNode.getLabels().addAll(stage.getLabels());
        stageNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess");
        stageNode.setContent(stageContent);

        Edge<Child, Node> stageInEdge = new EdgeImpl<>("_CF1684C2-3D30-4FDE-A5AA-D88B81E08418");
        stageInEdge.setSourceNode(rootNode);
        stageInEdge.setTargetNode(stageNode);
        stageInEdge.setContent(new Child());
        rootNode.getOutEdges().add(stageInEdge);
        stageNode.getInEdges().add(stageInEdge);

        UserTask task = new UserTask();

        task.getGeneral().setName(new Name("TaskTest"));
        task.getGeneral().setDocumentation(new Documentation("TaskTest"));

        UserTaskExecutionSet executionSet = task.getExecutionSet();
        executionSet.setTaskName(new TaskName("TaskTest"));
        executionSet.setSubject(new Subject("TaskTest"));
        executionSet.setActors(new Actors("TaskTest"));
        executionSet.setGroupid(new Groupid("TaskTest"));
        executionSet.setPriority(new Priority("5"));
        executionSet.setDescription(new Description("TaskTest"));
        executionSet.setCreatedBy(new CreatedBy("TaskTest"));
        executionSet.setIsAsync(new IsAsync(true));
        executionSet.setSkippable(new Skippable(true));
        executionSet.setAdHocAutostart(new AdHocAutostart(true));
        executionSet.setAssignmentsinfo(new AssignmentsInfo(
                "|TaskTest:Boolean,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object|" +
                        "|TaskTest:Boolean|[din]TaskTest=true"));

        ScriptTypeValue entryAction = new ScriptTypeValue();
        entryAction.setScript(("TaskTest"));
        entryAction.setLanguage("java");
        executionSet.setOnEntryAction(new OnEntryAction(new ScriptTypeListValue(Collections.singletonList(entryAction))));

        ScriptTypeValue exitAction = new ScriptTypeValue();
        exitAction.setScript("TaskTest");
        exitAction.setLanguage("java");
        executionSet.setOnExitAction(new OnExitAction(new ScriptTypeListValue(Collections.singletonList(exitAction))));

        task.setExecutionSet(executionSet);

        View<UserTask> taskContent = new ViewImpl<>(task, Bounds.create(0.0, 0.0, 153.0, 103.0));
        Node<View<UserTask>, Edge> taskNode = new NodeImpl<>("_E95AD08A-4595-4FA4-8948-3318D8BE7941");
        taskNode.getLabels().addAll(task.getLabels());
        taskNode.getLabels().add("org.kie.workbench.common.stunner.cm.definition.UserTask");
        taskNode.setContent(taskContent);

        Edge<Child, Node> taskInEdge = new EdgeImpl<>("_B24CB4A4-93A0-4BC0-87A5-BD3968CC184F");
        taskInEdge.setSourceNode(stageNode);
        taskInEdge.setTargetNode(taskNode);
        taskInEdge.setContent(new Child());
        stageNode.getOutEdges().add(taskInEdge);
        taskNode.getInEdges().add(taskInEdge);

        DefinitionSet definitionSet = new DefinitionSetImpl("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");

        Graph graph = new GraphImpl<>("_E0752AEB-6594-483D-9757-F147960EA60A", new GraphNodeStoreImpl());
        graph.getLabels().add("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        graph.setContent(definitionSet);
        graph.addNode(rootNode);
        graph.addNode(stageNode);
        graph.addNode(taskNode);

        MetadataImpl metaData = new MetadataImpl();
        metaData.setDefinitionSetId("org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet");
        metaData.setTitle("New Case Management diagram");
        metaData.setShapeSetId("org.kie.workbench.common.stunner.cm.client.CaseManagementShapeSet");
        metaData.setCanvasRootUUID("_0E761372-8B3C-4BE1-88BC-808D647D9EFF");

        DiagramImpl diagram = new DiagramImpl("_D518B746-92D2-4BF1-8AD1-1EBA552C5F6F", metaData);
        diagram.setGraph(graph);

        return diagram;
    }

    @Test
    public void testMarshall_task() throws Exception {
        String result = tested.marshall(createTask());

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_" + UUID_REGEX + "_SkippableInputXItem\" structureRef=\"Object\"/>");

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_" + UUID_REGEX + "_PriorityInputXItem\" structureRef=\"Object\"/>");

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_" + UUID_REGEX + "_CommentInputXItem\" structureRef=\"Object\"/>");

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_" + UUID_REGEX + "_DescriptionInputXItem\" structureRef=\"Object\"/>");

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_" + UUID_REGEX + "_CreatedByInputXItem\" structureRef=\"Object\"/>");

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_" + UUID_REGEX + "_TaskNameInputXItem\" structureRef=\"Object\"/>");

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_" + UUID_REGEX + "_GroupIdInputXItem\" structureRef=\"Object\"/>");

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_" + UUID_REGEX + "_TaskTestInputXItem\" structureRef=\"Boolean\"/>");

        hasElement(result,
                   "<bpmn2:itemDefinition id=\"_" + UUID_REGEX + "_TaskTestOutputXItem\" structureRef=\"Boolean\"/>");

        hasElement(result,
                   "<bpmn2:process id=\"TaskTest\" drools:version=\"1.0\" drools:adHoc=\"true\" name=\"TaskTest\" isExecutable=\"true\">");

        hasElement(result,
                   "<bpmn2:adHocSubProcess id=\"" + UUID_REGEX + "\" name=\"StageTaskTest\" ordering=\"Sequential\">");

        hasElement(result,
                   "<bpmn2:userTask id=\"" + UUID_REGEX + "\" name=\"TaskTest\">");

        hasElement(result,
                   "<bpmn2:documentation id=\"" + ID_REGEX + "\">" + Pattern.quote("<![CDATA[TaskTest]]>") + "</bpmn2:documentation>");

        hasElement(result,
                   "<drools:metaData name=\"elementname\">(\\s*)"
                           + Pattern.quote("<drools:metaValue><![CDATA[TaskTest]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:metaData name=\"customAsync\">(\\s*)"
                           + Pattern.quote("<drools:metaValue><![CDATA[true]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:metaData name=\"customAutoStart\">(\\s*)"
                           + Pattern.quote("<drools:metaValue><![CDATA[true]]></drools:metaValue>")
                           + "(\\s*)</drools:metaData>");

        hasElement(result,
                   "<drools:onEntry-script scriptFormat=\"http://www.java.com/java\">(\\s*)"
                           + Pattern.quote("<drools:script><![CDATA[TaskTest]]></drools:script>")
                           + "(\\s*)</drools:onEntry-script>");

        hasElement(result,
                   "<drools:onExit-script scriptFormat=\"http://www.java.com/java\">(\\s*)"
                           + Pattern.quote("<drools:script><![CDATA[TaskTest]]></drools:script>")
                           + "(\\s*)</drools:onExit-script>");

        hasElement(result,
                   "<bpmn2:ioSpecification id=\"" + ID_REGEX + "\">");

        hasElement(result,
                   "<bpmn2:dataInput id=\"" + UUID_REGEX + "_TaskNameInputX\" drools:dtype=\"Object\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_TaskNameInputXItem\" name=\"TaskName\"/>");

        hasElement(result,
                   "<bpmn2:dataInput id=\"" + UUID_REGEX + "_TaskTestInputX\" drools:dtype=\"Boolean\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_TaskTestInputXItem\" name=\"TaskTest\"/>");

        hasElement(result,
                   "<bpmn2:dataInput id=\"" + UUID_REGEX + "_SkippableInputX\" drools:dtype=\"Object\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_SkippableInputXItem\" name=\"Skippable\"/>");

        hasElement(result,
                   "<bpmn2:dataInput id=\"" + UUID_REGEX + "_GroupIdInputX\" drools:dtype=\"Object\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_GroupIdInputXItem\" name=\"GroupId\"/>");

        hasElement(result,
                   "<bpmn2:dataInput id=\"" + UUID_REGEX + "_CommentInputX\" drools:dtype=\"Object\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_CommentInputXItem\" name=\"Comment\"/>");

        hasElement(result,
                   "<bpmn2:dataInput id=\"" + UUID_REGEX + "_DescriptionInputX\" drools:dtype=\"Object\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_DescriptionInputXItem\" name=\"Description\"/>");

        hasElement(result,
                   "<bpmn2:dataInput id=\"" + UUID_REGEX + "_PriorityInputX\" drools:dtype=\"Object\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_PriorityInputXItem\" name=\"Priority\"/>");

        hasElement(result,
                   "<bpmn2:dataInput id=\"" + UUID_REGEX + "_CreatedByInputX\" drools:dtype=\"Object\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_CreatedByInputXItem\" name=\"CreatedBy\"/>");

        hasElement(result,
                   "<bpmn2:dataOutput id=\"" + UUID_REGEX + "_TaskTestOutputX\" drools:dtype=\"Boolean\" itemSubjectRef=\"_"
                           + UUID_REGEX + "_TaskTestOutputXItem\" name=\"TaskTest\"/>");

        hasElement(result,
                   "<bpmn2:dataInputRefs>" + UUID_REGEX + "_TaskNameInputX</bpmn2:dataInputRefs>");

        hasElement(result,
                   "<bpmn2:dataInputRefs>" + UUID_REGEX + "_TaskTestInputX</bpmn2:dataInputRefs>");

        hasElement(result,
                   "<bpmn2:dataInputRefs>" + UUID_REGEX + "_SkippableInputX</bpmn2:dataInputRefs>");

        hasElement(result,
                   "<bpmn2:dataInputRefs>" + UUID_REGEX + "_GroupIdInputX</bpmn2:dataInputRefs>");

        hasElement(result,
                   "<bpmn2:dataInputRefs>" + UUID_REGEX + "_CommentInputX</bpmn2:dataInputRefs>");

        hasElement(result,
                   "<bpmn2:dataInputRefs>" + UUID_REGEX + "_DescriptionInputX</bpmn2:dataInputRefs>");

        hasElement(result,
                   "<bpmn2:dataInputRefs>" + UUID_REGEX + "_PriorityInputX</bpmn2:dataInputRefs>");

        hasElement(result,
                   "<bpmn2:dataInputRefs>" + UUID_REGEX + "_CreatedByInputX</bpmn2:dataInputRefs>");

        hasElement(result,
                   "<bpmn2:dataOutputRefs>" + UUID_REGEX + "_TaskTestOutputX</bpmn2:dataOutputRefs>");

        hasElement(result,
                   "<bpmn2:targetRef>" + UUID_REGEX + "_TaskNameInputX</bpmn2:targetRef>");

        hasElement(result,
                   "<bpmn2:targetRef>" + UUID_REGEX + "_TaskTestInputX</bpmn2:targetRef>");

        hasElement(result,
                   "<bpmn2:targetRef>" + UUID_REGEX + "_SkippableInputX</bpmn2:targetRef>");

        hasElement(result,
                   "<bpmn2:targetRef>" + UUID_REGEX + "_GroupIdInputX</bpmn2:targetRef>");

        hasElement(result,
                   "<bpmn2:targetRef>" + UUID_REGEX + "_CommentInputX</bpmn2:targetRef>");

        hasElement(result,
                   "<bpmn2:targetRef>" + UUID_REGEX + "_DescriptionInputX</bpmn2:targetRef>");

        hasElement(result,
                   "<bpmn2:targetRef>" + UUID_REGEX + "_PriorityInputX</bpmn2:targetRef>");

        hasElement(result,
                   "<bpmn2:targetRef>" + UUID_REGEX + "_CreatedByInputX</bpmn2:targetRef>");
    }

    @Test
    public void testUnmarshall() throws Exception {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CM_FILE)) {
            Metadata metadata = new MetadataImpl.MetadataImplBuilder(
                    BindableAdapterUtils.getDefinitionSetId(CaseManagementDefinitionSet.class)).build();

            Graph<DefinitionSet, Node> graph = tested.unmarshall(metadata, inputStream);

            List<Node<View<?>, Edge>> nodes = StreamSupport.stream(graph.nodes().spliterator(), false)
                    .map(n -> (Node<View<?>, Edge>) n).collect(Collectors.toList());

            Node<View<?>, Edge> root = nodes.stream()
                    .filter(node -> CaseManagementDiagram.class.isInstance(node.getContent().getDefinition()))
                    .findAny().get();
            assertEquals(2, root.getOutEdges().size());

            Node<View<AdHocSubprocess>, Edge> stage1 = root.getOutEdges().get(0).getTargetNode();
            assertEquals(1, stage1.getInEdges().size());
            assertEquals(2, stage1.getOutEdges().size());
            assertTrue(AdHocSubprocess.class.isInstance(stage1.getContent().getDefinition()));

            Node<View<UserTask>, Edge> task1 = stage1.getOutEdges().get(0).getTargetNode();
            assertEquals(1, task1.getInEdges().size());
            assertEquals(0, task1.getOutEdges().size());
            assertTrue(UserTask.class.isInstance(task1.getContent().getDefinition()));

            Node<View<CaseReusableSubprocess>, Edge> case1 = stage1.getOutEdges().get(1).getTargetNode();
            assertEquals(1, case1.getInEdges().size());
            assertEquals(0, case1.getOutEdges().size());
            assertTrue(CaseReusableSubprocess.class.isInstance(case1.getContent().getDefinition()));
            assertTrue(case1.getContent().getDefinition().getExecutionSet().getIsCase().getValue());

            Node<View<AdHocSubprocess>, Edge> stage2 = root.getOutEdges().get(1).getTargetNode();
            assertEquals(1, stage2.getInEdges().size());
            assertEquals(2, stage2.getOutEdges().size());
            assertTrue(AdHocSubprocess.class.isInstance(stage2.getContent().getDefinition()));

            Node<View<CaseReusableSubprocess>, Edge> case2 = stage2.getOutEdges().get(0).getTargetNode();
            assertEquals(1, case2.getInEdges().size());
            assertEquals(0, case2.getOutEdges().size());
            assertTrue(CaseReusableSubprocess.class.isInstance(case2.getContent().getDefinition()));
            assertTrue(case2.getContent().getDefinition().getExecutionSet().getIsCase().getValue());

            Node<View<ProcessReusableSubprocess>, Edge> process2 = stage2.getOutEdges().get(1).getTargetNode();
            assertEquals(1, process2.getInEdges().size());
            assertEquals(0, process2.getOutEdges().size());
            assertTrue(ProcessReusableSubprocess.class.isInstance(process2.getContent().getDefinition()));
            assertFalse(process2.getContent().getDefinition().getExecutionSet().getIsCase().getValue());
        }
    }

    @Test
    public void testUnmarshall_diagram() throws Exception {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DIAGRAM_FILE)) {
            Metadata metadata = new MetadataImpl.MetadataImplBuilder(
                    BindableAdapterUtils.getDefinitionSetId(CaseManagementDefinitionSet.class)).build();

            Graph<DefinitionSet, Node> graph = tested.unmarshall(metadata, inputStream);

            List<Node<View<?>, Edge>> nodes = StreamSupport.stream(graph.nodes().spliterator(), false)
                    .map(n -> (Node<View<?>, Edge>) n).collect(Collectors.toList());

            Node<View<?>, Edge> root = nodes.stream()
                    .filter(node -> CaseManagementDiagram.class.isInstance(node.getContent().getDefinition()))
                    .findAny().get();

            CaseManagementDiagram diagram = (CaseManagementDiagram) root.getContent().getDefinition();

            DiagramSet diagramSet = diagram.getDiagramSet();
            assertEquals("DiagramTest", diagramSet.getName().getValue());
            assertEquals("DiagramTest", diagramSet.getDocumentation().getValue());
            assertEquals("DiagramTest", diagramSet.getId().getValue());
            assertEquals("DiagramTest", diagramSet.getPackageProperty().getValue());
            assertEquals("2.0", diagramSet.getVersion().getValue());
            assertTrue(diagramSet.getAdHoc().getValue());
            assertEquals("DiagramTest", diagramSet.getProcessInstanceDescription().getValue());

            ProcessData processData = diagram.getProcessData();
            assertEquals("DiagramTest:Boolean", processData.getProcessVariables().getValue());

            CaseManagementSet caseManagementSet = diagram.getCaseManagementSet();
            assertEquals("DiagramTest", caseManagementSet.getCaseIdPrefix().getValue());
            assertEquals("DiagramTest:5", caseManagementSet.getCaseRoles().getValue());
            assertEquals("DiagramTest:Boolean", caseManagementSet.getCaseFileVariables().getValue());
        }
    }

    @Test
    public void testUnmarshall_stage() throws Exception {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(STAGE_FILE)) {
            Metadata metadata = new MetadataImpl.MetadataImplBuilder(
                    BindableAdapterUtils.getDefinitionSetId(CaseManagementDefinitionSet.class)).build();

            Graph<DefinitionSet, Node> graph = tested.unmarshall(metadata, inputStream);

            List<Node<View<?>, Edge>> nodes = StreamSupport.stream(graph.nodes().spliterator(), false)
                    .map(n -> (Node<View<?>, Edge>) n).collect(Collectors.toList());

            Node<View<?>, Edge> root = nodes.stream()
                    .filter(node -> CaseManagementDiagram.class.isInstance(node.getContent().getDefinition()))
                    .findAny().get();
            assertEquals(1, root.getOutEdges().size());

            CaseManagementDiagram diagram = (CaseManagementDiagram) root.getContent().getDefinition();

            DiagramSet diagramSet = diagram.getDiagramSet();
            assertEquals("StageTest", diagramSet.getName().getValue());
            assertEquals("StageTest", diagramSet.getId().getValue());

            Node<View<AdHocSubprocess>, Edge> stageNode = root.getOutEdges().get(0).getTargetNode();
            AdHocSubprocess stage = stageNode.getContent().getDefinition();

            BPMNGeneralSet general = stage.getGeneral();
            assertEquals("StageTest", general.getName().getValue());
            assertEquals("StageTest", general.getDocumentation().getValue());

            ProcessData stageProcessData = stage.getProcessData();
            assertEquals("StageTest:Boolean", stageProcessData.getProcessVariables().getValue());

            AdHocSubprocessTaskExecutionSet executionSet = stage.getExecutionSet();
            assertEquals("Sequential", executionSet.getAdHocOrdering().getValue());
            assertTrue(executionSet.getAdHocAutostart().getValue());

            ScriptTypeValue completionCondition = executionSet.getAdHocCompletionCondition().getValue();
            assertEquals("autocomplete", completionCondition.getScript());
            assertEquals("drools", completionCondition.getLanguage());

            ScriptTypeValue entryAction = executionSet.getOnEntryAction().getValue().getValues().get(0);
            assertEquals("StageTest", entryAction.getScript());
            assertEquals("java", entryAction.getLanguage());

            ScriptTypeValue exitAction = executionSet.getOnExitAction().getValue().getValues().get(0);
            assertEquals("StageTest", exitAction.getScript());
            assertEquals("java", exitAction.getLanguage());
        }
    }

    @Test
    public void testUnmarshall_subcase() throws Exception {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SUBCASE_FILE)) {
            Metadata metadata = new MetadataImpl.MetadataImplBuilder(
                    BindableAdapterUtils.getDefinitionSetId(CaseManagementDefinitionSet.class)).build();

            Graph<DefinitionSet, Node> graph = tested.unmarshall(metadata, inputStream);

            List<Node<View<?>, Edge>> nodes = StreamSupport.stream(graph.nodes().spliterator(), false)
                    .map(n -> (Node<View<?>, Edge>) n).collect(Collectors.toList());

            Node<View<?>, Edge> root = nodes.stream()
                    .filter(node -> CaseManagementDiagram.class.isInstance(node.getContent().getDefinition()))
                    .findAny().get();
            assertEquals(1, root.getOutEdges().size());

            CaseManagementDiagram diagram = (CaseManagementDiagram) root.getContent().getDefinition();

            DiagramSet diagramSet = diagram.getDiagramSet();
            assertEquals("SubcaseTest", diagramSet.getName().getValue());
            assertEquals("SubcaseTest", diagramSet.getId().getValue());

            Node<View<AdHocSubprocess>, Edge> stageNode = root.getOutEdges().get(0).getTargetNode();
            assertEquals(1, stageNode.getOutEdges().size());

            AdHocSubprocess stage = stageNode.getContent().getDefinition();
            BPMNGeneralSet stageGeneral = stage.getGeneral();
            assertEquals("StageSubcaseTest", stageGeneral.getName().getValue());

            Node<View<CaseReusableSubprocess>, Edge> subcaseNode = stageNode.getOutEdges().get(0).getTargetNode();
            CaseReusableSubprocess subcase = subcaseNode.getContent().getDefinition();

            BPMNGeneralSet subcaseGeneral = subcase.getGeneral();
            assertEquals("SubcaseTest", subcaseGeneral.getName().getValue());
            assertEquals("SubcaseTest", subcaseGeneral.getDocumentation().getValue());

            CaseReusableSubprocessTaskExecutionSet subcaseExecutionSet = subcase.getExecutionSet();
            assertTrue(subcaseExecutionSet.getIsCase().getValue());
            assertTrue(subcaseExecutionSet.getIndependent().getValue());
            assertTrue(subcaseExecutionSet.getIsAsync().getValue());
            assertTrue(subcaseExecutionSet.getWaitForCompletion().getValue());
            assertTrue(subcaseExecutionSet.getAdHocAutostart().getValue());

            ScriptTypeValue entryAction = subcaseExecutionSet.getOnEntryAction().getValue().getValues().get(0);
            assertEquals("SubcaseTest", entryAction.getScript());
            assertEquals("java", entryAction.getLanguage());

            ScriptTypeValue exitAction = subcaseExecutionSet.getOnExitAction().getValue().getValues().get(0);
            assertEquals("SubcaseTest", exitAction.getScript());
            assertEquals("java", exitAction.getLanguage());

            DataIOSet dataIOSet = subcase.getDataIOSet();
            assertEquals("|SubcaseTest:Boolean||SubcaseTest:Boolean|[din]SubcaseTest=true", dataIOSet.getAssignmentsinfo().getValue());
        }
    }

    @Test
    public void testUnmarshall_subprocess() throws Exception {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SUBPROCESS_FILE)) {
            Metadata metadata = new MetadataImpl.MetadataImplBuilder(
                    BindableAdapterUtils.getDefinitionSetId(CaseManagementDefinitionSet.class)).build();

            Graph<DefinitionSet, Node> graph = tested.unmarshall(metadata, inputStream);

            List<Node<View<?>, Edge>> nodes = StreamSupport.stream(graph.nodes().spliterator(), false)
                    .map(n -> (Node<View<?>, Edge>) n).collect(Collectors.toList());

            Node<View<?>, Edge> root = nodes.stream()
                    .filter(node -> CaseManagementDiagram.class.isInstance(node.getContent().getDefinition()))
                    .findAny().get();
            assertEquals(1, root.getOutEdges().size());

            CaseManagementDiagram diagram = (CaseManagementDiagram) root.getContent().getDefinition();

            DiagramSet diagramSet = diagram.getDiagramSet();
            assertEquals("SubprocessTest", diagramSet.getName().getValue());
            assertEquals("SubprocessTest", diagramSet.getId().getValue());

            Node<View<AdHocSubprocess>, Edge> stageNode = root.getOutEdges().get(0).getTargetNode();
            assertEquals(1, stageNode.getOutEdges().size());

            AdHocSubprocess stage = stageNode.getContent().getDefinition();
            BPMNGeneralSet stageGeneral = stage.getGeneral();
            assertEquals("StageSubprocessTest", stageGeneral.getName().getValue());

            Node<View<ProcessReusableSubprocess>, Edge> subprocessNode = stageNode.getOutEdges().get(0).getTargetNode();
            ProcessReusableSubprocess subprocess = subprocessNode.getContent().getDefinition();

            BPMNGeneralSet subprocessGeneral = subprocess.getGeneral();
            assertEquals("SubprocessTest", subprocessGeneral.getName().getValue());
            assertEquals("SubprocessTest", subprocessGeneral.getDocumentation().getValue());

            ProcessReusableSubprocessTaskExecutionSet subprocessExecutionSet = subprocess.getExecutionSet();
            assertFalse(subprocessExecutionSet.getIsCase().getValue());
            assertTrue(subprocessExecutionSet.getIndependent().getValue());
            assertTrue(subprocessExecutionSet.getIsAsync().getValue());
            assertTrue(subprocessExecutionSet.getWaitForCompletion().getValue());
            assertTrue(subprocessExecutionSet.getAdHocAutostart().getValue());

            ScriptTypeValue entryAction = subprocessExecutionSet.getOnEntryAction().getValue().getValues().get(0);
            assertEquals("SubprocessTest", entryAction.getScript());
            assertEquals("java", entryAction.getLanguage());

            ScriptTypeValue exitAction = subprocessExecutionSet.getOnExitAction().getValue().getValues().get(0);
            assertEquals("SubprocessTest", exitAction.getScript());
            assertEquals("java", exitAction.getLanguage());

            DataIOSet dataIOSet = subprocess.getDataIOSet();
            assertEquals("|SubprocessTest:Boolean||SubprocessTest:Boolean|[din]SubprocessTest=true", dataIOSet.getAssignmentsinfo().getValue());
        }
    }

    @Test
    public void testUnmarshall_task() throws Exception {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(TASK_FILE)) {
            Metadata metadata = new MetadataImpl.MetadataImplBuilder(
                    BindableAdapterUtils.getDefinitionSetId(CaseManagementDefinitionSet.class)).build();

            Graph<DefinitionSet, Node> graph = tested.unmarshall(metadata, inputStream);

            List<Node<View<?>, Edge>> nodes = StreamSupport.stream(graph.nodes().spliterator(), false)
                    .map(n -> (Node<View<?>, Edge>) n).collect(Collectors.toList());

            Node<View<?>, Edge> root = nodes.stream()
                    .filter(node -> CaseManagementDiagram.class.isInstance(node.getContent().getDefinition()))
                    .findAny().get();
            assertEquals(1, root.getOutEdges().size());

            CaseManagementDiagram diagram = (CaseManagementDiagram) root.getContent().getDefinition();

            DiagramSet diagramSet = diagram.getDiagramSet();
            assertEquals("TaskTest", diagramSet.getName().getValue());
            assertEquals("TaskTest", diagramSet.getId().getValue());

            Node<View<AdHocSubprocess>, Edge> stageNode = root.getOutEdges().get(0).getTargetNode();
            assertEquals(1, stageNode.getOutEdges().size());

            AdHocSubprocess stage = stageNode.getContent().getDefinition();
            BPMNGeneralSet stageGeneral = stage.getGeneral();
            assertEquals("StageTaskTest", stageGeneral.getName().getValue());

            Node<View<UserTask>, Edge> taskNode = stageNode.getOutEdges().get(0).getTargetNode();
            UserTask task = taskNode.getContent().getDefinition();

            TaskGeneralSet taskGeneral = task.getGeneral();
            assertEquals("TaskTest", taskGeneral.getName().getValue());
            assertEquals("TaskTest", taskGeneral.getDocumentation().getValue());

            UserTaskExecutionSet executionSet = task.getExecutionSet();
            assertEquals("TaskTest", executionSet.getTaskName().getValue());
            assertEquals("TaskTest", executionSet.getSubject().getValue());
            assertEquals("TaskTest", executionSet.getActors().getValue());
            assertEquals("TaskTest", executionSet.getGroupid().getValue());
            assertEquals("5", executionSet.getPriority().getValue());
            assertEquals("TaskTest", executionSet.getDescription().getValue());
            assertEquals("TaskTest", executionSet.getCreatedBy().getValue());
            assertTrue(executionSet.getIsAsync().getValue());
            assertTrue(executionSet.getSkippable().getValue());
            assertTrue(executionSet.getAdHocAutostart().getValue());
            assertEquals("|TaskTest:Boolean,Skippable:Object,GroupId:Object,Comment:Object,Description:Object,Priority:Object,CreatedBy:Object|" +
                                 "|TaskTest:Boolean|[din]TaskTest=true", executionSet.getAssignmentsinfo().getValue());

            ScriptTypeValue entryAction = executionSet.getOnEntryAction().getValue().getValues().get(0);
            assertEquals("TaskTest", entryAction.getScript());
            assertEquals("java", entryAction.getLanguage());

            ScriptTypeValue exitAction = executionSet.getOnExitAction().getValue().getValues().get(0);
            assertEquals("TaskTest", exitAction.getScript());
            assertEquals("java", exitAction.getLanguage());
        }
    }

    @Test
    public void testCreateFromStunnerConverterFactory() throws Exception {
        assertTrue(org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.CaseManagementConverterFactory.class.isInstance(
                tested.createFromStunnerConverterFactory(new GraphImpl("x", new GraphNodeStoreImpl()), new CaseManagementPropertyWriterFactory())));
    }

    @Test
    public void testCreateToStunnerConverterFactory() throws Exception {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        DefinitionResolver definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        FactoryManager factoryManager = mock(FactoryManager.class);

        TypedFactoryManager typedFactoryManager = new TypedFactoryManager(factoryManager);

        assertTrue(org.kie.workbench.common.stunner.cm.backend.converters.tostunner.CaseManagementConverterFactory.class.isInstance(
                tested.createToStunnerConverterFactory(definitionResolver, typedFactoryManager)));
    }

    @Test
    public void testCreatePropertyWriterFactory() throws Exception {
        assertTrue(CaseManagementPropertyWriterFactory.class.isInstance(tested.createPropertyWriterFactory()));
    }

    @Test
    public void testGetDefinitionSetClass() throws Exception {
        assertEquals(tested.getDefinitionSetClass(), CaseManagementDefinitionSet.class);
    }
}
