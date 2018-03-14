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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.backend.ApplicationFactoryManager;
import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.InterruptingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertyAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertySetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.DiscreteConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Assertions.assertDiagram;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNDirectDiagramMarshallerTest {

    static final String BPMN_DEF_SET_ID = BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);

    private static final String BPMN_BASIC = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/basic.bpmn";
    private static final String BPMN_EVALUATION = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/evaluation.bpmn";
    private static final String BPMN_LANES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/lanes.bpmn";
    private static final String BPMN_BOUNDARY_EVENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundaryIntmEvent.bpmn";
    private static final String BPMN_NOT_BOUNDARY_EVENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/notBoundaryIntmEvent.bpmn";
    private static final String BPMN_PROCESSVARIABLES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/processVariables.bpmn";
    private static final String BPMN_USERTASKASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskAssignments.bpmn";
    private static final String BPMN_BUSINESSRULETASKASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/businessRuleTaskAssignments.bpmn";
    private static final String BPMN_STARTNONEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startNoneEvent.bpmn";
    private static final String BPMN_STARTTIMEREVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startTimerEvent.bpmn";
    private static final String BPMN_STARTSIGNALEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startSignalEvent.bpmn";
    private static final String BPMN_STARTMESSAGEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startMessageEvent.bpmn";
    private static final String BPMN_STARTERROREVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startErrorEvent.bpmn";
    private static final String BPMN_INTERMEDIATE_SIGNAL_EVENTCATCHING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateSignalEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_ERROR_EVENTCATCHING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateErrorEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_SIGNAL_EVENTTHROWING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateSignalEventThrowing.bpmn";
    private static final String BPMN_INTERMEDIATE_MESSAGE_EVENTCATCHING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateMessageEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_MESSAGE_EVENTTHROWING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateMessageEventThrowing.bpmn";
    private static final String BPMN_INTERMEDIATE_TIMER_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateTimerEvent.bpmn";
    private static final String BPMN_ENDSIGNALEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endSignalEvent.bpmn";
    private static final String BPMN_ENDMESSAGEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endMessageEvent.bpmn";
    private static final String BPMN_ENDNONEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endNoneEvent.bpmn";
    private static final String BPMN_ENDTERMINATEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endTerminateEvent.bpmn";
    private static final String BPMN_PROCESSPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/processProperties.bpmn";
    private static final String BPMN_BUSINESSRULETASKRULEFLOWGROUP = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/businessRuleTask.bpmn";
    private static final String BPMN_EVENT_SUBPROCESS_STARTERROREVENT= "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/isInterruptingStartErrorEvent.bpmn";
    private static final String BPMN_REUSABLE_SUBPROCESS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/reusableSubprocessCalledElement.bpmn";
    private static final String BPMN_EMBEDDED_SUBPROCESS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/embeddedSubprocess.bpmn";
    private static final String BPMN_ADHOC_SUBPROCESS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/adHocSubProcess.bpmn";
    private static final String BPMN_SCRIPTTASK = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/scriptTask.bpmn";
    private static final String BPMN_USERTASKASSIGNEES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskAssignees.bpmn";
    private static final String BPMN_USERTASKPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskProperties.bpmn";
    private static final String BPMN_SEQUENCEFLOW = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/sequenceFlow.bpmn";
    private static final String BPMN_XORGATEWAY = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/xorGateway.bpmn";
    private static final String BPMN_TIMER_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/timerEvent.bpmn";
    private static final String BPMN_SIMULATIONPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/simulationProperties.bpmn";
    private static final String BPMN_MAGNETDOCKERS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/magnetDockers.bpmn";
    private static final String BPMN_MAGNETSINLANE = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/magnetsInLane.bpmn";
    private static final String BPMN_ENDERROR_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endErrorEvent.bpmn";

    @Mock
    DefinitionManager definitionManager;

    @Mock
    AdapterManager adapterManager;

    @Mock
    AdapterRegistry adapterRegistry;

    @Mock
    RuleManager rulesManager;

    ApplicationFactoryManager applicationFactoryManager;

    private BPMNDirectDiagramMarshaller tested;

    private Diagram<Graph, Metadata> unmarshall(String s) throws Exception {
        return Unmarshalling.unmarshall(tested, s);
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {

        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        // initApplicationFactoryManagerAlt();
        when(rulesManager.evaluate(any(RuleSet.class),
                                   any(RuleEvaluationContext.class))).thenReturn(new DefaultRuleViolations());

        DefinitionUtils definitionUtils = new DefinitionUtils(definitionManager,
                                                              applicationFactoryManager);
        TestScopeModelFactory testScopeModelFactory = new TestScopeModelFactory(new BPMNDefinitionSet.BPMNDefinitionSetBuilder().build());
        // Definition manager.
        final BackendDefinitionAdapter definitionAdapter = new BackendDefinitionAdapter(definitionUtils);
        final BackendDefinitionSetAdapter definitionSetAdapter = new BackendDefinitionSetAdapter(definitionAdapter);
        final BackendPropertySetAdapter propertySetAdapter = new BackendPropertySetAdapter();
        final BackendPropertyAdapter propertyAdapter = new BackendPropertyAdapter();
        mockAdapterManager(definitionAdapter, definitionSetAdapter, propertySetAdapter, propertyAdapter);
        mockAdapterRegistry(definitionAdapter, definitionSetAdapter, propertySetAdapter, propertyAdapter);
        applicationFactoryManager = new MockApplicationFactoryManager(
                new GraphFactoryImpl(definitionManager),
                testScopeModelFactory,
                new EdgeFactoryImpl(definitionManager),
                new NodeFactoryImpl(definitionUtils)
        );

        GraphCommandManagerImpl commandManager = new GraphCommandManagerImpl(null,
                                                                             null,
                                                                             null);
        GraphCommandFactory commandFactory = new GraphCommandFactory();

        // The tested BPMN marshaller.
        tested = new BPMNDirectDiagramMarshaller(definitionManager, rulesManager, applicationFactoryManager, commandFactory, commandManager);
    }

    private void mockAdapterRegistry(BackendDefinitionAdapter definitionAdapter, BackendDefinitionSetAdapter definitionSetAdapter, BackendPropertySetAdapter propertySetAdapter, BackendPropertyAdapter propertyAdapter) {
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
    }

    private void mockAdapterManager(BackendDefinitionAdapter definitionAdapter, BackendDefinitionSetAdapter definitionSetAdapter, BackendPropertySetAdapter propertySetAdapter, BackendPropertyAdapter propertyAdapter) {
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
    }

    // 4 nodes expected: BPMNDiagram, StartNode, Task and EndNode
    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallBasic() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BASIC);
        assertDiagram(diagram,
                      4);
        assertEquals("Basic process",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> task1 = diagram.getGraph().getNode("810797AB-7D09-4E1F-8A5B-96C424E4B031");
        assertTrue(task1.getContent().getDefinition() instanceof NoneTask);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEvaluation() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVALUATION);
        assertDiagram(diagram,
                      8);
        assertEquals("Evaluation",
                     diagram.getMetadata().getTitle());
        Node<? extends View, ?> task1 = diagram.getGraph().getNode("_88233779-B395-4B8C-A086-9EF43698426C");
        Node<? extends View, ?> task2 = diagram.getGraph().getNode("_AE5BF0DC-B720-4FDE-9499-5ED89D41FB1A");
        Node<? extends View, ?> task3 = diagram.getGraph().getNode("_6063D302-9D81-4C86-920B-E808A45377C2");
        assertTrue(task1.getContent().getDefinition() instanceof UserTask);
        assertTrue(task2.getContent().getDefinition() instanceof UserTask);
        assertTrue(task3.getContent().getDefinition() instanceof UserTask);
        // Assert bounds.
        Bounds task1Bounds = task1.getContent().getBounds();
        Bounds.Bound task1ULBound = task1Bounds.getUpperLeft();
        Bounds.Bound task1LRBound = task1Bounds.getLowerRight();
        assertEquals(648d,
                     task1ULBound.getX(),
                     0);
        assertEquals(149d,
                     task1ULBound.getY(),
                     0);
        assertEquals(784d,
                     task1LRBound.getX(),
                     0);
        assertEquals(197d,
                     task1LRBound.getY(),
                     0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallProcessVariables() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSVARIABLES);
        assertDiagram(diagram,
                      8);
        assertEquals("ProcessVariables",
                     diagram.getMetadata().getTitle());
        ProcessVariables variables = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof BPMNDiagram) {
                    BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) oDefinition;
                    variables = bpmnDiagram.getProcessData().getProcessVariables();
                    break;
                }
            }
        }
        assertEquals(variables.getValue(),
                     "employee:java.lang.String,reason:java.lang.String,performance:java.lang.String");
        Node<? extends Definition, ?> diagramNode = diagram.getGraph().getNode("_luRBMdEjEeWXpsZ1tNStKQ");
        assertTrue(diagramNode.getContent().getDefinition() instanceof BPMNDiagram);
        BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) diagramNode.getContent().getDefinition();
        assertTrue(bpmnDiagram.getProcessData() != null);
        assertTrue(bpmnDiagram.getProcessData().getProcessVariables() != null);
        variables = bpmnDiagram.getProcessData().getProcessVariables();
        assertEquals(variables.getValue(),
                     "employee:java.lang.String,reason:java.lang.String,performance:java.lang.String");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallProcessProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSPROPERTIES);
        assertDiagram(diagram,
                      4);
        assertEquals("BPSimple",
                     diagram.getMetadata().getTitle());
        DiagramSet diagramProperties = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof BPMNDiagram) {
                    BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) oDefinition;
                    diagramProperties = bpmnDiagram.getDiagramSet();
                    break;
                }
            }
        }
        assertEquals("BPSimple",
                     diagramProperties.getName().getValue());
        assertEquals("This is a\n" +
                             "simple\n" +
                             "process",
                     diagramProperties.getDocumentation().getValue());
        assertEquals("JDLProj.BPSimple",
                     diagramProperties.getId().getValue());
        assertEquals("org.jbpm",
                     diagramProperties.getPackageProperty().getValue());
        assertEquals(Boolean.TRUE,
                     diagramProperties.getExecutable().getValue());
        assertEquals(Boolean.TRUE,
                     diagramProperties.getAdHoc().getValue());
        assertEquals("This is the\n" +
                             "Process\n" +
                             "Instance\n" +
                             "Description",
                     diagramProperties.getProcessInstanceDescription().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallUserTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNMENTS);
        assertDiagram(diagram,
                      8);
        assertEquals("UserTaskAssignments",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> selfEvaluationNode = diagram.getGraph().getNode("_6063D302-9D81-4C86-920B-E808A45377C2");
        UserTask selfEvaluationTask = (UserTask) selfEvaluationNode.getContent().getDefinition();
        assertEquals(selfEvaluationTask.getTaskType().getValue(),
                     TaskTypes.USER);
        UserTaskExecutionSet executionSet = selfEvaluationTask.getExecutionSet();
        AssignmentsInfo assignmentsinfo = executionSet.getAssignmentsinfo();
        assertEquals(assignmentsinfo.getValue(),
                     "|reason:com.test.Reason,Comment:Object,Skippable:Object||performance:Object|[din]reason->reason,[dout]performance->performance");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallBusinessRuleTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKASSIGNMENTS);
        assertDiagram(diagram,
                      4);
        assertEquals("BusinessRuleTaskAssignments",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> businessRuleNode = diagram.getGraph().getNode("_45C2C340-D1D0-4D63-8419-EF38F9E73507");
        BusinessRuleTask businessRuleTask = (BusinessRuleTask) businessRuleNode.getContent().getDefinition();
        assertEquals(businessRuleTask.getTaskType().getValue(),
                     TaskTypes.BUSINESS_RULE);
        DataIOSet dataIOSet = businessRuleTask.getDataIOSet();
        AssignmentsInfo assignmentsinfo = dataIOSet.getAssignmentsinfo();
        assertEquals(assignmentsinfo.getValue(),
                     "|input1:String,input2:String||output1:String,output2:String|[din]pv1->input1,[din]pv2->input2,[dout]output1->pv2,[dout]output2->pv2");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartNoneEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTNONEEVENT);
        assertDiagram(diagram,
                      4);
        assertEquals("startNoneEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startNoneEventNode = diagram.getGraph().getNode("processStartEvent");
        StartNoneEvent startNoneEvent = (StartNoneEvent) startNoneEventNode.getContent().getDefinition();
        assertNotNull(startNoneEvent.getGeneral());
        assertEquals("MyStartNoneEvent",
                     startNoneEvent.getGeneral().getName().getValue());
        assertEquals("MyStartNoneEventDocumentation",
                     startNoneEvent.getGeneral().getDocumentation().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartTimerEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTTIMEREVENT);
        assertDiagram(diagram,
                      4);
        assertEquals("StartTimerEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startTimerEventNode = diagram.getGraph().getNode("_49ADC988-B63D-4AEB-B811-67969F305FD0");
        StartTimerEvent startTimerEvent = (StartTimerEvent) startTimerEventNode.getContent().getDefinition();
        IsInterrupting isInterrupting = startTimerEvent.getExecutionSet().getIsInterrupting();
        assertEquals(false,
                     isInterrupting.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartSignalEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTSIGNALEVENT);
        assertDiagram(diagram,
                      4);
        assertEquals("StartSignalEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startSignalEventNode = diagram.getGraph().getNode("_1876844A-4DAC-4214-8BCD-2ABA3FCC8EB5");
        StartSignalEvent startSignalEvent = (StartSignalEvent) startSignalEventNode.getContent().getDefinition();
        assertNotNull(startSignalEvent.getExecutionSet());
        SignalRef signalRef = startSignalEvent.getExecutionSet().getSignalRef();
        assertEquals("sig1",
                     signalRef.getValue());
    }

    @Test
    public void testUnmarshallStartErrorEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTERROREVENT);
        assertDiagram(diagram,
                      3);
        assertEquals("startErrorEventProcess",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startEventNode = diagram.getGraph().getNode("3BD5BBC8-F1C7-45DE-8BDF-A06D8464A61B");
        StartErrorEvent startErrorEvent = (StartErrorEvent) startEventNode.getContent().getDefinition();
        assertNotNull(startErrorEvent.getGeneral());
        assertEquals("MyStartErrorEvent",
                     startErrorEvent.getGeneral().getName().getValue());
        assertEquals("MyStartErrorEventDocumentation",
                     startErrorEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(startErrorEvent.getExecutionSet());
        assertNotNull(startErrorEvent.getExecutionSet().getErrorRef());
        assertEquals("MyError",
                     startErrorEvent.getExecutionSet().getErrorRef().getValue());

        DataIOSet dataIOSet = startErrorEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||errorOutput_:String||[dout]errorOutput_->var1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartMessageEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTMESSAGEEVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("StartMessageEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startMessageEventNode = diagram.getGraph().getNode("_34C4BBFC-544F-4E23-B17B-547BB48EEB63");
        StartMessageEvent startMessageEvent = (StartMessageEvent) startMessageEventNode.getContent().getDefinition();
        assertNotNull(startMessageEvent.getExecutionSet());
        MessageRef messageRef = startMessageEvent.getExecutionSet().getMessageRef();
        IsInterrupting isInterrupting = startMessageEvent.getExecutionSet().getIsInterrupting();
        assertEquals("msgref",
                     messageRef.getValue());
        assertEquals(true,
                     isInterrupting.getValue());
        DataIOSet dataIOSet = startMessageEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||StartMessageEventOutputVar1:String||[dout]StartMessageEventOutputVar1->var1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateTimerEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_TIMER_EVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("intermediateTimer",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateEventNode = diagram.getGraph().getNode("_8D881072-284F-4F0D-8CF2-AD1F4540FC4E");
        IntermediateTimerEvent intermediateTimerEvent = (IntermediateTimerEvent) intermediateEventNode.getContent().getDefinition();
        assertNotNull(intermediateTimerEvent.getGeneral());
        assertEquals("MyTimer",
                     intermediateTimerEvent.getGeneral().getName().getValue());
        assertNotNull(intermediateTimerEvent.getExecutionSet());
        assertEquals("abc",
                     intermediateTimerEvent.getExecutionSet().getTimerSettings().getValue().getTimeCycle());
        assertEquals("none",
                     intermediateTimerEvent.getExecutionSet().getTimerSettings().getValue().getTimeCycleLanguage());
        assertEquals("abc",
                     intermediateTimerEvent.getExecutionSet().getTimerSettings().getValue().getTimeDate());
        assertEquals("abc",
                     intermediateTimerEvent.getExecutionSet().getTimerSettings().getValue().getTimeDuration());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateSignalEventCatching() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_SIGNAL_EVENTCATCHING);
        assertDiagram(diagram,
                      2);
        assertEquals("intermediateSignalCatching",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateEventNode = diagram.getGraph().getNode("_2C9B14A3-F663-476D-9FDF-31590D3A9CC5");
        IntermediateSignalEventCatching intermediateSignalEventCatching = (IntermediateSignalEventCatching) intermediateEventNode.getContent().getDefinition();
        assertNotNull(intermediateSignalEventCatching.getGeneral());
        assertEquals("MySignalCatchingEvent",
                     intermediateSignalEventCatching.getGeneral().getName().getValue());
        assertEquals("MySignalCatchingEventDocumentation",
                     intermediateSignalEventCatching.getGeneral().getDocumentation().getValue());
        assertNotNull(intermediateSignalEventCatching.getExecutionSet());
        assertEquals(true,
                     intermediateSignalEventCatching.getExecutionSet().getCancelActivity().getValue());
        assertEquals("MySignal",
                     intermediateSignalEventCatching.getExecutionSet().getSignalRef().getValue());

        DataIOSet dataIOSet = intermediateSignalEventCatching.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||output1_:String||[dout]output1_->var1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateErrorEventCatching() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_ERROR_EVENTCATCHING);
        assertDiagram(diagram,
                      2);
        assertEquals("intermediateErrorCatching",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateEventNode = diagram.getGraph().getNode("80A2A7A9-7C68-408C-BE3B-467562A2C139");
        IntermediateErrorEventCatching intermediateErrorEventCatching = (IntermediateErrorEventCatching) intermediateEventNode.getContent().getDefinition();
        assertNotNull(intermediateErrorEventCatching.getGeneral());
        assertEquals("MyErrorCatchingEvent",
                     intermediateErrorEventCatching.getGeneral().getName().getValue());
        assertEquals("MyErrorCatchingEventDocumentation",
                     intermediateErrorEventCatching.getGeneral().getDocumentation().getValue());
        assertNotNull(intermediateErrorEventCatching.getExecutionSet());
        assertEquals(true,
                     intermediateErrorEventCatching.getExecutionSet().getCancelActivity().getValue());
        assertEquals("MyError",
                     intermediateErrorEventCatching.getExecutionSet().getErrorRef().getValue());

        DataIOSet dataIOSet = intermediateErrorEventCatching.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||theErrorEventOutput:String||[dout]theErrorEventOutput->errorVar",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateSignalEventThrowing() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_SIGNAL_EVENTTHROWING);
        assertDiagram(diagram,
                      2);
        assertEquals("intermediateSignalThrowing",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateEventNode = diagram.getGraph().getNode("_A45EC77D-5414-4348-BA8F-05C4FFD660EE");
        IntermediateSignalEventThrowing intermediateSignalEventThrowing = (IntermediateSignalEventThrowing) intermediateEventNode.getContent().getDefinition();
        assertNotNull(intermediateSignalEventThrowing.getGeneral());
        assertEquals("MySignalThrowingEvent",
                     intermediateSignalEventThrowing.getGeneral().getName().getValue());
        assertEquals("MySignalThrowingEventDocumentation",
                     intermediateSignalEventThrowing.getGeneral().getDocumentation().getValue());
        assertNotNull(intermediateSignalEventThrowing.getExecutionSet());
        assertEquals("processInstance",
                     intermediateSignalEventThrowing.getExecutionSet().getSignalScope().getValue());
        assertEquals("MySignal",
                     intermediateSignalEventThrowing.getExecutionSet().getSignalRef().getValue());

        DataIOSet dataIOSet = intermediateSignalEventThrowing.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("_input1:String||||[din]var1->_input1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateMessageEventCatching() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_MESSAGE_EVENTCATCHING);
        assertDiagram(diagram,
                      2);
        assertEquals("IntermediateMessageEventCatching",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateMessageEventCatchingNode = diagram.getGraph().getNode("_BD708E30-CA48-4051-BAEA-BBCB5F396CEE");
        IntermediateMessageEventCatching intermediateMessageEventCatching = (IntermediateMessageEventCatching) intermediateMessageEventCatchingNode.getContent().getDefinition();

        assertNotNull(intermediateMessageEventCatching.getExecutionSet());
        MessageRef messageRef = intermediateMessageEventCatching.getExecutionSet().getMessageRef();
        assertEquals("msgref1",
                     messageRef.getValue());
        DataIOSet dataIOSet = intermediateMessageEventCatching.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||IntermediateMessageEventCatchingOutputVar1:String||[dout]IntermediateMessageEventCatchingOutputVar1->var1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateMessageEventThrowing() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_MESSAGE_EVENTTHROWING);
        assertDiagram(diagram,
                      2);
        assertEquals("IntermediateMessageEventThrowing",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateMessageEventThrowingNode = diagram.getGraph().getNode("_85823DF6-02A0-4B8D-AE7A-61641A3A2E4B");
        IntermediateMessageEventThrowing intermediateMessageEventThrowing = (IntermediateMessageEventThrowing) intermediateMessageEventThrowingNode.getContent().getDefinition();

        assertNotNull(intermediateMessageEventThrowing.getExecutionSet());
        MessageRef messageRef = intermediateMessageEventThrowing.getExecutionSet().getMessageRef();
        assertEquals("msgref",
                     messageRef.getValue());
        DataIOSet dataIOSet = intermediateMessageEventThrowing.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("IntermediateMessageEventThrowingInputVar1:String||||[din]var1->IntermediateMessageEventThrowingInputVar1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEndNoneEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDNONEEVENT);
        assertDiagram(diagram,
                      3);
        assertEquals("endNoneEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> endNoneEventNode = diagram.getGraph().getNode("_9DF2C9D3-15DF-4436-B6C6-85B58B8696B6");
        EndNoneEvent endNoneEvent = (EndNoneEvent) endNoneEventNode.getContent().getDefinition();
        assertNotNull(endNoneEvent.getGeneral());
        assertEquals("MyEndNoneEvent",
                     endNoneEvent.getGeneral().getName().getValue());
        assertEquals("MyEndNoneEventDocumentation",
                     endNoneEvent.getGeneral().getDocumentation().getValue());
    }

    @Test
    @Ignore
    public void testUnmarshallIsInterruptingStartErrorEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVENT_SUBPROCESS_STARTERROREVENT);
        assertDiagram(diagram,7);
        assertEquals("EventSubprocessStartErrorEvent", diagram.getMetadata().getTitle());

        // Check first start event with all FILLED properties
        Node<? extends Definition, ?> startEventNode = diagram.getGraph().getNode("9ABD5C04-C6E2-4DF3-829F-ADB283330AD6");
        StartErrorEvent startErrorEvent = (StartErrorEvent) startEventNode.getContent().getDefinition();
        BPMNGeneralSet eventGeneralSet = startErrorEvent.getGeneral();
        assertNotNull(eventGeneralSet);
        assertEquals("StartErrorEvent", eventGeneralSet.getName().getValue());
        assertEquals("Some not empty\nDocumentation\n~`!@#$%^&*()_+=-{}|[]\\:\";'<>/?.,",
                     eventGeneralSet.getDocumentation().getValue());

        InterruptingErrorEventExecutionSet eventExecutionSet = startErrorEvent.getExecutionSet();
        assertNotNull(eventExecutionSet);
        assertNotNull(eventExecutionSet.getErrorRef());
        assertEquals("Error1", eventExecutionSet.getErrorRef().getValue());
        assertEquals(true, eventExecutionSet.getIsInterrupting().getValue());

        DataIOSet eventDataIOSet = startErrorEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = eventDataIOSet.getAssignmentsinfo();
        assertEquals("||Var1:String||[dout]Var1->Var1", assignmentsInfo.getValue());

        // Check second start event with all EMPTY properties
        Node<? extends Definition, ?> emptyEventNode = diagram.getGraph().getNode("50B93E5E-C05D-40DD-BF48-2B6AE919763E");
        StartErrorEvent emptyErrorEvent = (StartErrorEvent) emptyEventNode.getContent().getDefinition();
        BPMNGeneralSet emptyEventGeneralSet = emptyErrorEvent.getGeneral();
        assertNotNull(emptyEventGeneralSet);
        assertEquals("", emptyEventGeneralSet.getName().getValue());
        assertEquals("", emptyEventGeneralSet.getDocumentation().getValue());

        InterruptingErrorEventExecutionSet emptyExecutionSet = emptyErrorEvent.getExecutionSet();
        assertNotNull(emptyExecutionSet);
        assertNotNull(emptyExecutionSet.getErrorRef());
        assertEquals("", emptyExecutionSet.getErrorRef().getValue());
        assertEquals(false, emptyExecutionSet.getIsInterrupting().getValue());

        DataIOSet emptyDataIOSet = emptyErrorEvent.getDataIOSet();
        AssignmentsInfo emptyAssignmentsInfo = emptyDataIOSet.getAssignmentsinfo();
        assertEquals("", emptyAssignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEndTerminateEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDTERMINATEEVENT);
        assertDiagram(diagram,
                      3);
        assertEquals("endTerminateEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> endNoneEventNode = diagram.getGraph().getNode("_1B379E3E-E4ED-4BD2-AEE8-CD85374CEC78");
        EndTerminateEvent endTerminateEvent = (EndTerminateEvent) endNoneEventNode.getContent().getDefinition();
        assertNotNull(endTerminateEvent.getGeneral());
        assertEquals("MyEndTerminateEvent",
                     endTerminateEvent.getGeneral().getName().getValue());
        assertEquals("MyEndTerminateEventDocumentation",
                     endTerminateEvent.getGeneral().getDocumentation().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEndSignalEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDSIGNALEVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("EndEventAssignments",
                     diagram.getMetadata().getTitle());

        Node<? extends Definition, ?> endSignalEventNode = diagram.getGraph().getNode("_C9151E0C-2E3E-4558-AFC2-34038E3A8552");
        EndSignalEvent endSignalEvent = (EndSignalEvent) endSignalEventNode.getContent().getDefinition();
        DataIOSet dataIOSet = endSignalEvent.getDataIOSet();
        AssignmentsInfo assignmentsinfo = dataIOSet.getAssignmentsinfo();
        assertEquals("EndSignalEventInput1:String||||[din]employee->EndSignalEventInput1",
                     assignmentsinfo.getValue());
        assertEquals("project",
                     endSignalEvent.getExecutionSet().getSignalScope().getValue());
        assertEquals("employee",
                     endSignalEvent.getExecutionSet().getSignalRef().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEndMessageEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDMESSAGEEVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("EndMessageEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> endMessageEventNode = diagram.getGraph().getNode("_4A8A0A9E-D4A5-4B6E-94A6-20817A57B3C6");
        EndMessageEvent endMessageEvent = (EndMessageEvent) endMessageEventNode.getContent().getDefinition();

        assertNotNull(endMessageEvent.getExecutionSet());
        MessageRef messageRef = endMessageEvent.getExecutionSet().getMessageRef();
        assertEquals("msgref",
                     messageRef.getValue());
        DataIOSet dataIOSet = endMessageEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("EndMessageEventInputVar1:String||||[din]var1->EndMessageEventInputVar1",
                     assignmentsInfo.getValue());
    }

    public void testUnmarshallEndErrorEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDERROR_EVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("endErrorEventProcess",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> endEventNode = diagram.getGraph().getNode("_E69BD781-AB7F-45C4-85DA-B1F3BAE5BCCB");
        EndErrorEvent endErrorEvent = (EndErrorEvent) endEventNode.getContent().getDefinition();
        assertNotNull(endErrorEvent.getGeneral());
        assertEquals("MyErrorEventName",
                     endErrorEvent.getGeneral().getName().getValue());
        assertEquals("MyErrorEventDocumentation",
                     endErrorEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(endErrorEvent.getExecutionSet());
        assertNotNull(endErrorEvent.getExecutionSet().getErrorRef());
        assertEquals("MyError",
                     endErrorEvent.getExecutionSet().getErrorRef().getValue());

        DataIOSet dataIOSet = endErrorEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("myErrorEventInput:String||||[din]var1->myErrorEventInput",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallUserTaskAssignees() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNEES);
        assertDiagram(diagram,
                      6);
        assertEquals("UserGroups",
                     diagram.getMetadata().getTitle());
        UserTaskExecutionSet executionSet = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof UserTask) {
                    UserTask userTask = (UserTask) oDefinition;
                    executionSet = userTask.getExecutionSet();
                    break;
                }
            }
        }
        assertEquals("user,user1",
                     executionSet.getActors().getValue());
        assertEquals("admin,kiemgmt",
                     executionSet.getGroupid().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallUserTaskProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKPROPERTIES);
        assertDiagram(diagram,
                      4);
        assertEquals("MyBP",
                     diagram.getMetadata().getTitle());
        UserTaskExecutionSet userTaskExecutionSet = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof UserTask) {
                    UserTask userTask = (UserTask) oDefinition;
                    userTaskExecutionSet = userTask.getExecutionSet();
                    break;
                }
            }
        }
        assertEquals("MyUserTask",
                     userTaskExecutionSet.getTaskName().getValue());
        assertEquals("true",
                     userTaskExecutionSet.getIsAsync().getValue().toString());

        assertEquals("false",
                     userTaskExecutionSet.getSkippable().getValue().toString());

        assertEquals("my subject",
                     userTaskExecutionSet.getSubject().getValue());

        assertEquals("admin",
                     userTaskExecutionSet.getCreatedBy().getValue());

        assertEquals("my description",
                     userTaskExecutionSet.getDescription().getValue());

        assertEquals("3",
                     userTaskExecutionSet.getPriority().getValue());

        assertEquals("true",
                     userTaskExecutionSet.getAdHocAutostart().getValue().toString());

        assertEquals("System.out.println(\"Hello\");",
                     userTaskExecutionSet.getOnEntryAction().getValue().getValues().get(0).getScript());

        assertEquals("java",
                     userTaskExecutionSet.getOnEntryAction().getValue().getValues().get(0).getLanguage());

        assertEquals("System.out.println(\"Bye\");",
                     userTaskExecutionSet.getOnExitAction().getValue().getValues().get(0).getScript());

        assertEquals("java",
                     userTaskExecutionSet.getOnExitAction().getValue().getValues().get(0).getLanguage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallSimulationProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SIMULATIONPROPERTIES);
        assertDiagram(diagram,
                      4);
        assertEquals("SimulationProperties",
                     diagram.getMetadata().getTitle());

        SimulationSet simulationSet = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof UserTask) {
                    UserTask userTask = (UserTask) oDefinition;
                    simulationSet = userTask.getSimulationSet();
                    break;
                }
            }
        }

        assertEquals(Double.valueOf(111),
                     simulationSet.getQuantity().getValue());
        assertEquals("poisson",
                     simulationSet.getDistributionType().getValue());
        assertEquals(Double.valueOf(123),
                     simulationSet.getUnitCost().getValue());
        assertEquals(Double.valueOf(999),
                     simulationSet.getWorkingHours().getValue());
        assertEquals(Double.valueOf(321),
                     simulationSet.getMean().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallNotBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_NOT_BOUNDARY_EVENTS);
        assertEquals("Not Boundary Event",
                     diagram.getMetadata().getTitle());
        assertDiagram(diagram,
                      6);
        // Assert than the intermediate event is connected using a view connector,
        // so not boundary to the task ( not docked ).
        Node event = diagram.getGraph().getNode("_CB178D55-8DC2-4CAA-8C42-4F5028D4A1F6");
        List<Edge> inEdges = event.getInEdges();
        boolean foundViewConnector = false;
        for (Edge e : inEdges) {
            if (e.getContent() instanceof ViewConnector) {
                foundViewConnector = true;
            }
        }
        assertTrue(foundViewConnector);
        // Assert absolute position as the node is not docked.
        Bounds bounds = ((View) event.getContent()).getBounds();
        Bounds.Bound ul = bounds.getUpperLeft();
        Bounds.Bound lr = bounds.getLowerRight();
        assertEquals(305,
                     ul.getX(),
                     0);
        assertEquals(300,
                     ul.getY(),
                     0);
        assertEquals(335,
                     lr.getX(),
                     0);
        assertEquals(330,
                     lr.getY(),
                     0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BOUNDARY_EVENTS);
        // Basic assertions.
        assertEquals("Boundary Event",
                     diagram.getMetadata().getTitle());
        assertDiagram(diagram,
                      6);
        // Assert than the intermediate event is connected using a dock connector,
        // so boundary to the task.
        Node event = diagram.getGraph().getNode("_CB178D55-8DC2-4CAA-8C42-4F5028D4A1F6");
        List<Edge> inEdges = event.getInEdges();
        boolean foundDockConector = false;
        for (Edge e : inEdges) {
            if (e.getContent() instanceof Dock) {
                foundDockConector = true;
            }
        }
        assertTrue(foundDockConector);
        // Assert relative position for the docked node.
        Bounds bounds = ((View) event.getContent()).getBounds();
        Bounds.Bound ul = bounds.getUpperLeft();
        Bounds.Bound lr = bounds.getLowerRight();
        assertEquals(57,
                     ul.getX(),
                     0);
        assertEquals(70,
                     ul.getY(),
                     0);
        assertEquals(87,
                     lr.getX(),
                     0);
        assertEquals(100,
                     lr.getY(),
                     0);
    }

    @Test
    public void testUnmarshallScriptTask() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SCRIPTTASK);
        ScriptTask javascriptScriptTask = null;
        ScriptTask javaScriptTask = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof ScriptTask) {
                    ScriptTask task = (ScriptTask) oDefinition;
                    if ("Javascript Script Task".equals(task.getGeneral().getName().getValue())) {
                        javascriptScriptTask = task;
                    } else if ("Java Script Task".equals(task.getGeneral().getName().getValue())) {
                        javaScriptTask = task;
                    }
                }
            }
        }
        assertNotNull(javascriptScriptTask);
        assertNotNull(javascriptScriptTask.getExecutionSet());
        assertNotNull(javascriptScriptTask.getExecutionSet().getScript());
        assertEquals(javascriptScriptTask.getTaskType().getValue(),
                     TaskTypes.SCRIPT);
        assertEquals("Javascript Script Task",
                     javascriptScriptTask.getGeneral().getName().getValue());
        assertEquals("var str = FirstName + LastName;",
                     javascriptScriptTask.getExecutionSet().getScript().getValue().getScript());
        assertEquals("javascript",
                     javascriptScriptTask.getExecutionSet().getScript().getValue().getLanguage());
        assertEquals("true",
                     javascriptScriptTask.getExecutionSet().getIsAsync().getValue().toString());

        assertEquals("true",
                     javascriptScriptTask.getExecutionSet().getIsAsync().getValue().toString());

        assertNotNull(javaScriptTask);
        assertNotNull(javaScriptTask.getExecutionSet());
        assertNotNull(javaScriptTask.getExecutionSet().getScript());
        assertEquals(javaScriptTask.getTaskType().getValue(),
                     TaskTypes.SCRIPT);
        assertEquals("Java Script Task",
                     javaScriptTask.getGeneral().getName().getValue());
        assertEquals("if (name.toString().equals(\"Jay\")) {\n" +
                             "\n" +
                             "      System.out.println(\"Hello\\n\" + name.toString() + \"\\n\");\n" +
                             "\n" +
                             "} else {\n" +
                             "\n" +
                             "\n" +
                             "  System.out.println(\"Hi\\n\" + name.toString() + \"\\n\");\n" +
                             "\n" +
                             "\n" +
                             "}\n",
                     javaScriptTask.getExecutionSet().getScript().getValue().getScript());
        assertEquals("java",
                     javaScriptTask.getExecutionSet().getScript().getValue().getLanguage());
        assertEquals("true",
                     javaScriptTask.getExecutionSet().getIsAsync().getValue().toString());

        assertEquals("true",
                     javaScriptTask.getExecutionSet().getIsAsync().getValue().toString());
    }

    @Test
    public void testUnmarshallSequenceFlow() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SEQUENCEFLOW);
        SequenceFlow sequenceFlow1 = null;
        SequenceFlow sequenceFlow2 = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof ExclusiveGateway) {
                    List<Edge> outEdges = ((NodeImpl) element).getOutEdges();
                    for (Edge edge : outEdges) {
                        SequenceFlow flow = (SequenceFlow) ((ViewConnectorImpl) edge.getContent()).getDefinition();
                        if ("route1".equals(flow.getGeneral().getName().getValue())) {
                            sequenceFlow1 = flow;
                        }
                        if ("route2".equals(flow.getGeneral().getName().getValue())) {
                            sequenceFlow2 = flow;
                        }
                    }
                }
            }
        }
        assertNotNull(sequenceFlow1);
        assertNotNull(sequenceFlow1.getExecutionSet());
        assertNotNull(sequenceFlow1.getExecutionSet().getConditionExpression());
        assertNotNull(sequenceFlow1.getExecutionSet().getPriority());
        assertNotNull(sequenceFlow1.getGeneral());
        assertNotNull(sequenceFlow1.getGeneral().getName());
        assertEquals("route1",
                     sequenceFlow1.getGeneral().getName().getValue());
        assertEquals("age >= 10;",
                     sequenceFlow1.getExecutionSet().getConditionExpression().getValue().getScript());
        assertEquals("javascript",
                     sequenceFlow1.getExecutionSet().getConditionExpression().getValue().getLanguage());
        assertEquals("2",
                     sequenceFlow1.getExecutionSet().getPriority().getValue());

        assertNotNull(sequenceFlow2);
        assertNotNull(sequenceFlow2.getExecutionSet());
        assertNotNull(sequenceFlow2.getExecutionSet().getConditionExpression());
        assertNotNull(sequenceFlow2.getExecutionSet().getPriority());
        assertNotNull(sequenceFlow2.getGeneral());
        assertNotNull(sequenceFlow2.getGeneral().getName());
        assertEquals("route2",
                     sequenceFlow2.getGeneral().getName().getValue());
        assertEquals("age\n" +
                             "<\n" +
                             "10;",
                     sequenceFlow2.getExecutionSet().getConditionExpression().getValue().getScript());
        assertEquals("java",
                     sequenceFlow2.getExecutionSet().getConditionExpression().getValue().getLanguage());
        assertEquals("1",
                     sequenceFlow2.getExecutionSet().getPriority().getValue());
    }

    @Test
    public void testUnmarshallBusinessRuleTask() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKRULEFLOWGROUP);
        BusinessRuleTask businessRuleTask = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof BusinessRuleTask) {
                    businessRuleTask = (BusinessRuleTask) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(businessRuleTask);
        assertNotNull(businessRuleTask.getExecutionSet());
        assertNotNull(businessRuleTask.getExecutionSet().getRuleFlowGroup());
        assertNotNull(businessRuleTask.getGeneral());
        assertNotNull(businessRuleTask.getGeneral().getName());
        assertEquals(businessRuleTask.getTaskType().getValue(),
                     TaskTypes.BUSINESS_RULE);
        assertEquals("my business rule task",
                     businessRuleTask.getGeneral().getName().getValue());
        assertEquals("my-ruleflow-group",
                     businessRuleTask.getExecutionSet().getRuleFlowGroup().getValue());
        assertEquals("true",
                     businessRuleTask.getExecutionSet().getIsAsync().getValue().toString());

        assertEquals("true",
                     businessRuleTask.getExecutionSet().getIsAsync().getValue().toString());

        assertEquals("System.out.println(\"Hello\");",
                     businessRuleTask.getExecutionSet().getOnEntryAction().getValue().getValues().get(0).getScript());

        assertEquals("java",
                     businessRuleTask.getExecutionSet().getOnEntryAction().getValue().getValues().get(0).getLanguage());

        assertEquals("System.out.println(\"Bye\");",
                     businessRuleTask.getExecutionSet().getOnExitAction().getValue().getValues().get(0).getScript());

        assertEquals("java",
                     businessRuleTask.getExecutionSet().getOnExitAction().getValue().getValues().get(0).getLanguage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallXorGateway() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_XORGATEWAY);
        assertDiagram(diagram,
                      7);
        assertEquals(diagram.getMetadata().getTitle(),
                     "XORGateway");
        Graph graph = diagram.getGraph();
        Node<? extends Definition, ?> gatewayNode = graph.getNode("_877EA035-1A14-42E9-8CAA-43E9BF908C70");
        ExclusiveGateway xorGateway = (ExclusiveGateway) gatewayNode.getContent().getDefinition();
        assertEquals("AgeSplit",
                     xorGateway.getGeneral().getName().getValue());
        assertEquals("_5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD",
                     xorGateway.getExecutionSet().getDefaultRoute().getValue());
        SequenceFlow sequenceFlow1 = null;
        SequenceFlow sequenceFlow2 = null;
        List<Edge> outEdges = (List<Edge>) gatewayNode.getOutEdges();
        if (outEdges != null) {
            for (Edge edge : outEdges) {
                if ("_C72E00C3-70DC-4BC9-A08E-761B4263A239".equals(edge.getUUID())) {
                    sequenceFlow1 = (SequenceFlow) ((ViewConnector) edge.getContent()).getDefinition();
                } else if ("_5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD".equals(edge.getUUID())) {
                    sequenceFlow2 = (SequenceFlow) ((ViewConnector) edge.getContent()).getDefinition();
                }
            }
        }

        assertNotNull(sequenceFlow1);
        assertEquals("10 and over",
                     sequenceFlow1.getGeneral().getName().getValue());
        assertNotNull(sequenceFlow2);
        assertEquals("under 10",
                     sequenceFlow2.getGeneral().getName().getValue());
    }

    @Test
    public void testUnmarshallReusableSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_REUSABLE_SUBPROCESS);
        ReusableSubprocess reusableSubprocess = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof ReusableSubprocess) {
                    reusableSubprocess = (ReusableSubprocess) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(reusableSubprocess);
        assertNotNull(reusableSubprocess.getExecutionSet());
        assertNotNull(reusableSubprocess.getExecutionSet().getCalledElement());
        assertNotNull(reusableSubprocess.getGeneral());

        BPMNGeneralSet generalSet = reusableSubprocess.getGeneral();
        ReusableSubprocessTaskExecutionSet executionSet = reusableSubprocess.getExecutionSet();
        assertNotNull(generalSet);
        assertNotNull(executionSet);

        assertEquals("my subprocess",
                     generalSet.getName().getValue());
        assertEquals("my-called-element",
                     executionSet.getCalledElement().getValue());
        assertEquals(false,
                     executionSet.getIndependent().getValue());
        assertEquals(false,
                     executionSet.getWaitForCompletion().getValue());

        String assignmentsInfo = reusableSubprocess.getDataIOSet().getAssignmentsinfo().getValue();
        assertEquals("|input1:String,input2:Float||output1:String,output2:Float|[din]pv1->input1,[din]pv2->input2,[dout]output1->pv1,[dout]output2->pv2",
                     assignmentsInfo);

        assertEquals("true",
                     reusableSubprocess.getExecutionSet().getIsAsync().getValue().toString());
    }

    @Ignore
    public void testUnmarshallAddHocSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ADHOC_SUBPROCESS);
        AdHocSubprocess adHocSubprocess = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof AdHocSubprocess) {
                    adHocSubprocess = (AdHocSubprocess) oDefinition;
                    break;
                }
            }
        }

        assertNotNull(adHocSubprocess);

        BPMNGeneralSet generalSet = adHocSubprocess.getGeneral();
        AdHocSubprocessTaskExecutionSet executionSet = adHocSubprocess.getExecutionSet();
        ProcessData processData = adHocSubprocess.getProcessData();
        assertNotNull(generalSet);
        assertNotNull(executionSet);
        assertNotNull(processData);

        assertEquals("AdHocSubprocess1",
                     generalSet.getName().getValue());
        assertEquals("AdHocSubprocess1Documentation",
                     generalSet.getDocumentation().getValue());

        assertNotNull(executionSet.getAdHocCompletionCondition());
        assertNotNull(executionSet.getAdHocCompletionCondition().getValue());
        assertNotNull(executionSet.getAdHocOrdering());
        assertNotNull(executionSet.getOnEntryAction());
        assertNotNull(executionSet.getOnExitAction());

        assertEquals("autocomplete",
                     executionSet.getAdHocCompletionCondition().getValue().getScript());
        assertEquals("drools",
                     executionSet.getAdHocCompletionCondition().getValue().getLanguage());

        assertEquals("Sequential",
                     executionSet.getAdHocOrdering().getValue());

        assertEquals(1,
                     executionSet.getOnEntryAction().getValue().getValues().size());
        assertEquals("System.out.println(\"onEntryAction\");",
                     executionSet.getOnEntryAction().getValue().getValues().get(0).getScript());
        assertEquals("mvel",
                     executionSet.getOnEntryAction().getValue().getValues().get(0).getLanguage());

        assertEquals(1,
                     executionSet.getOnExitAction().getValue().getValues().size());
        assertEquals("System.out.println(\"onExitAction\");",
                     executionSet.getOnExitAction().getValue().getValues().get(0).getScript());
        assertEquals("java",
                     executionSet.getOnExitAction().getValue().getValues().get(0).getLanguage());

        assertEquals("subProcessVar1:String,subProcessVar2:String",
                     processData.getProcessVariables().getValue());
    }

    @Test
    public void testUnmarshallMagnetDockers() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_MAGNETDOCKERS);

        testMagnetDockers(diagram);
    }

    private void testMagnetDockers(Diagram<Graph, Metadata> diagram) {
        Node userTaskNode = (Node) findElementByContentType(diagram,
                                                            UserTask.class);
        Node businessRuleTaskNode = (Node) findElementByContentType(diagram,
                                                                    BusinessRuleTask.class);
        Node scriptTaskNode = (Node) findElementByContentType(diagram,
                                                              ScriptTask.class);
        assertNotNull(userTaskNode);
        assertNotNull(businessRuleTaskNode);
        assertNotNull(scriptTaskNode);

        ViewConnector userTaskInEdgeConnector = getInEdgeViewConnector(userTaskNode);
        ViewConnector businessRuleTaskInEdgeConnector = getInEdgeViewConnector(businessRuleTaskNode);
        ViewConnector scriptTaskInEdgeConnector = getInEdgeViewConnector(scriptTaskNode);
        assertNotNull(userTaskInEdgeConnector);
        assertNotNull(businessRuleTaskInEdgeConnector);
        assertNotNull(scriptTaskInEdgeConnector);

        ViewConnector userTaskOutEdgeConnector = getOutEdgeViewConnector(userTaskNode);
        ViewConnector businessRuleTaskOutEdgeConnector = getOutEdgeViewConnector(businessRuleTaskNode);
        ViewConnector scriptTaskOutEdgeConnector = getOutEdgeViewConnector(scriptTaskNode);
        assertNotNull(userTaskOutEdgeConnector);
        assertNotNull(businessRuleTaskOutEdgeConnector);
        assertNotNull(scriptTaskOutEdgeConnector);

        // userTaskInEdgeConnector is from magnet top-middle to left-middle
        assertTrue(userTaskInEdgeConnector.getSourceConnection().isPresent());
        assertTrue(userTaskInEdgeConnector.getTargetConnection().isPresent());

        DiscreteConnection sourceConnection = (DiscreteConnection) userTaskInEdgeConnector.getSourceConnection().get();
        DiscreteConnection targetConnection = (DiscreteConnection) userTaskInEdgeConnector.getTargetConnection().get();
        assertEquals(20d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(24d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // Assert both connections for userTaskInEdgeConnector are set to auto.
        assertTrue(sourceConnection.isAuto());
        assertTrue(targetConnection.isAuto());

        // businessRuleTaskInEdgeConnector is from magnet right-middle to top-left
        assertTrue(businessRuleTaskInEdgeConnector.getSourceConnection().isPresent());
        assertTrue(businessRuleTaskInEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (DiscreteConnection) businessRuleTaskInEdgeConnector.getSourceConnection().get();
        targetConnection = (DiscreteConnection) businessRuleTaskInEdgeConnector.getTargetConnection().get();
        assertEquals(40d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(20d,
                     sourceConnection.getLocation().getY(),
                     0.1d);

        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // Assert both connections for businessRuleTaskInEdgeConnector are NOT set to auto.
        assertFalse(sourceConnection.isAuto());
        assertFalse(targetConnection.isAuto());

        // scriptTaskInEdgeConnector is from magnet left-bottom to left-bottom
        assertTrue(scriptTaskInEdgeConnector.getSourceConnection().isPresent());
        assertTrue(scriptTaskInEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (DiscreteConnection) scriptTaskInEdgeConnector.getSourceConnection().get();
        targetConnection = (DiscreteConnection) scriptTaskInEdgeConnector.getTargetConnection().get();

        assertEquals(0d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(40d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(48d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // userTaskOutEdgeConnector is from magnet right-middle to left-middle
        assertTrue(userTaskOutEdgeConnector.getSourceConnection().isPresent());
        assertTrue(userTaskOutEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (DiscreteConnection) userTaskOutEdgeConnector.getSourceConnection().get();
        targetConnection = (DiscreteConnection) userTaskOutEdgeConnector.getTargetConnection().get();

        assertEquals(136d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(24d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(14d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // businessRuleTaskOutEdgeConnector is from magnet middle-bottom to middle-bottom
        assertTrue(businessRuleTaskOutEdgeConnector.getSourceConnection().isPresent());
        assertTrue(businessRuleTaskOutEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (DiscreteConnection) businessRuleTaskOutEdgeConnector.getSourceConnection().get();
        targetConnection = (DiscreteConnection) businessRuleTaskOutEdgeConnector.getTargetConnection().get();

        assertEquals(68d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(48d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(14d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(28d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // scriptTaskOutEdgeConnector is from magnet left-top to left-top
        assertTrue(scriptTaskOutEdgeConnector.getSourceConnection().isPresent());
        assertTrue(scriptTaskOutEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (DiscreteConnection) scriptTaskOutEdgeConnector.getSourceConnection().get();
        targetConnection = (DiscreteConnection) scriptTaskOutEdgeConnector.getTargetConnection().get();

        assertEquals(0d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getY(),
                     0.1d);
    }

    @Test
    public void testUnmarshallMagnetsInLane() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_MAGNETSINLANE);

        testMagnetsInLane(diagram);
    }

    private void testMagnetsInLane(Diagram<Graph, Metadata> diagram) {
        Node userTaskNode = (Node) findElementByContentType(diagram,
                                                            UserTask.class);
        Node scriptTaskNode = (Node) findElementByContentType(diagram,
                                                              ScriptTask.class);
        assertNotNull(userTaskNode);
        assertNotNull(scriptTaskNode);

        ViewConnector userTaskInEdgeConnector = getInEdgeViewConnector(userTaskNode);
        ViewConnector scriptTaskInEdgeConnector = getInEdgeViewConnector(scriptTaskNode);
        assertNotNull(userTaskInEdgeConnector);
        assertNotNull(scriptTaskInEdgeConnector);

        ViewConnector userTaskOutEdgeConnector = getOutEdgeViewConnector(userTaskNode);
        ViewConnector scriptTaskOutEdgeConnector = getOutEdgeViewConnector(scriptTaskNode);
        assertNotNull(userTaskOutEdgeConnector);
        assertNotNull(scriptTaskOutEdgeConnector);

        // userTaskInEdgeConnector is from magnet right-middle to middle-top
        assertTrue(userTaskInEdgeConnector.getSourceConnection().isPresent());
        assertTrue(userTaskInEdgeConnector.getTargetConnection().isPresent());

        Connection sourceConnection = (Connection) userTaskInEdgeConnector.getSourceConnection().get();
        Connection targetConnection = (Connection) userTaskInEdgeConnector.getTargetConnection().get();
        assertEquals(136d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(24d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(68d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // scriptTaskInEdgeConnector is from magnet right-bottom to left-top
        assertTrue(scriptTaskInEdgeConnector.getSourceConnection().isPresent());
        assertTrue(scriptTaskInEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (Connection) scriptTaskInEdgeConnector.getSourceConnection().get();
        targetConnection = (Connection) scriptTaskInEdgeConnector.getTargetConnection().get();

        assertEquals(136d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(48d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // userTaskOutEdgeConnector is from magnet right-bottom to left-top
        assertTrue(userTaskOutEdgeConnector.getSourceConnection().isPresent());
        assertTrue(userTaskOutEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (Connection) userTaskOutEdgeConnector.getSourceConnection().get();
        targetConnection = (Connection) userTaskOutEdgeConnector.getTargetConnection().get();

        assertEquals(136d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(48d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // scriptTaskOutEdgeConnector is from magnet right-top to left-middle
        assertTrue(scriptTaskOutEdgeConnector.getSourceConnection().isPresent());
        assertTrue(scriptTaskOutEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (Connection) scriptTaskOutEdgeConnector.getSourceConnection().get();
        targetConnection = (Connection) scriptTaskOutEdgeConnector.getTargetConnection().get();

        assertEquals(136d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(14d,
                     targetConnection.getLocation().getY(),
                     0.1d);
    }

    private ViewConnector getInEdgeViewConnector(Node node) {
        List<Edge> edges = node.getInEdges();
        if (edges != null) {
            for (Edge edge : edges) {
                if (edge.getContent() instanceof ViewConnector) {
                    return (ViewConnector) edge.getContent();
                }
            }
        }
        return null;
    }

    private ViewConnector getOutEdgeViewConnector(Node node) {
        List<Edge> edges = node.getOutEdges();
        if (edges != null) {
            for (Edge edge : edges) {
                if (edge.getContent() instanceof ViewConnector) {
                    return (ViewConnector) edge.getContent();
                }
            }
        }
        return null;
    }

    private Element findElementByContentType(Diagram<Graph, Metadata> diagram,
                                             Class contentClass) {
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (contentClass.isInstance(oDefinition)) {
                    return element;
                }
            }
        }
        return null;
    }

    @Test
    public void testUnmarshallEmbeddedSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EMBEDDED_SUBPROCESS);
        EmbeddedSubprocess subprocess = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof EmbeddedSubprocess) {
                    subprocess = (EmbeddedSubprocess) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(subprocess);
    }

    @Test
    public void testUnmarshallSeveralDiagrams() throws Exception {
        Diagram<Graph, Metadata> diagram1 = unmarshall(BPMN_EVALUATION);
        assertDiagram(diagram1,
                      8);
        assertEquals("Evaluation",
                     diagram1.getMetadata().getTitle());
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_LANES);
        assertDiagram(diagram,
                      7);
        assertEquals("Lanes test",
                     diagram.getMetadata().getTitle());
    }

    @SuppressWarnings("unchecked")
    private Iterator<Element> nodesIterator(Diagram<Graph, Metadata> diagram) {
        return (Iterator<Element>) diagram.getGraph().nodes().iterator();
    }
}
