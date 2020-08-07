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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.MetaDataType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.BPMNTestDefinitionFactory;
import org.kie.workbench.common.stunner.bpmn.WorkItemDefinitionMockRegistry;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.DeclarationList;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.VariableDeclaration;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventGateway;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.InterruptingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionLookupService;
import org.kie.workbench.common.stunner.core.StunnerTestingGraphAPI;
import org.kie.workbench.common.stunner.core.backend.StunnerTestingGraphBackendAPI;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.DiscreteConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Assertions.assertDiagram;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Assertions.assertDocumentation;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNDirectDiagramMarshallerTest {

    private static final String PATH_DIAGRAM = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram";

    //unsupported
    private static final String BPMN_MANUAL_TASK = PATH_DIAGRAM + "/unsupported/manualTask.bpmn";
    private static final String BPMN_SEND_TASK = PATH_DIAGRAM + "/unsupported/sendTask.bpmn";
    private static final String BPMN_RECEIVED_TASK = PATH_DIAGRAM + "/unsupported/receivedTask.bpmn";
    private static final String BPMN_CHILDLANESET = PATH_DIAGRAM + "/unsupported/4.6.10.10_P1_v3.bpmn";
    private static final String EXECUTION = PATH_DIAGRAM + "/unsupported/Execution.bpmn";
    private static final String BPMN_DATASTORE = PATH_DIAGRAM + "/unsupported/TestDataStore.bpmn";
    private static final String JBPM_DESIGNER_ALL_ELEMENTS = PATH_DIAGRAM + "/unsupported/jbpmDesigner1.bpmn";
    private static final String BPMN_DATAOBJECT = PATH_DIAGRAM + "/unsupported/dataObject1.bpmn";

    //supported
    private static final String BPMN_BASIC = PATH_DIAGRAM + "/basic.bpmn";
    private static final String BPMN_EVALUATION = PATH_DIAGRAM + "/evaluation.bpmn";
    private static final String BPMN_LANES = PATH_DIAGRAM + "/lanes.bpmn";
    private static final String BPMN_BOUNDARY_EVENTS = PATH_DIAGRAM + "/boundaryIntmEvent.bpmn";
    private static final String BPMN_NOT_BOUNDARY_EVENTS = PATH_DIAGRAM + "/notBoundaryIntmEvent.bpmn";
    private static final String BPMN_PROCESSVARIABLES = PATH_DIAGRAM + "/processVariables.bpmn";
    private static final String BPMN_GLOBALVARIABLES = PATH_DIAGRAM + "/globalVariables.bpmn";
    private static final String BPMN_USERTASKASSIGNMENTS = PATH_DIAGRAM + "/userTaskAssignments.bpmn";
    private static final String BPMN_USERTASK_MI = PATH_DIAGRAM + "/userTaskMI.bpmn";
    private static final String BPMN_BUSINESSRULETASKASSIGNMENTS = PATH_DIAGRAM + "/businessRuleTaskAssignments.bpmn";
    private static final String BPMN_STARTNONEEVENT = PATH_DIAGRAM + "/startNoneEvent.bpmn";
    private static final String BPMN_STARTTIMEREVENT = PATH_DIAGRAM + "/startTimerEvent.bpmn";
    private static final String BPMN_STARTSIGNALEVENT = PATH_DIAGRAM + "/startSignalEvent.bpmn";
    private static final String BPMN_STARTMESSAGEEVENT = PATH_DIAGRAM + "/startMessageEvent.bpmn";
    private static final String BPMN_STARTERROREVENT = PATH_DIAGRAM + "/startErrorEvent.bpmn";
    private static final String BPMN_STARTCONDITIONALEVENT = PATH_DIAGRAM + "/startConditionalEvent.bpmn";
    private static final String BPMN_STARTESCALATIONEVENT = PATH_DIAGRAM + "/startEscalationEvent.bpmn";
    private static final String BPMN_STARTCOMPENSATIONEVENT = PATH_DIAGRAM + "/startCompensationEvent.bpmn";
    private static final String BPMN_INTERMEDIATE_SIGNAL_EVENTCATCHING = PATH_DIAGRAM + "/intermediateSignalEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_ERROR_EVENTCATCHING = PATH_DIAGRAM + "/intermediateErrorEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_SIGNAL_EVENTTHROWING = PATH_DIAGRAM + "/intermediateSignalEventThrowing.bpmn";
    private static final String BPMN_INTERMEDIATE_MESSAGE_EVENTCATCHING = PATH_DIAGRAM + "/intermediateMessageEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_MESSAGE_EVENTTHROWING = PATH_DIAGRAM + "/intermediateMessageEventThrowing.bpmn";
    private static final String BPMN_INTERMEDIATE_TIMER_EVENT = PATH_DIAGRAM + "/intermediateTimerEvent.bpmn";
    private static final String BPMN_INTERMEDIATE_CONDITIONAL_EVENTS = PATH_DIAGRAM + "/intermediateConditionalEvents.bpmn";
    private static final String BPMN_INTERMEDIATE_ESCALATION_EVENTS = PATH_DIAGRAM + "/intermediateEscalationEvents.bpmn";
    private static final String BPMN_INTERMEDIATE_COMPENSATION_EVENTS = PATH_DIAGRAM + "/intermediateCompensationEvents.bpmn";
    private static final String BPMN_INTERMEDIATE_COMPENSATION_EVENTS_WITH_ASSOCIATION = PATH_DIAGRAM + "/intermediateCompensationEventsWithAssociation.bpmn";
    private static final String BPMN_INTERMEDIATE_ESCALATION_EVENTTHROWING = PATH_DIAGRAM + "/intermediateEscalationEventThrowing.bpmn";
    private static final String BPMN_INTERMEDIATE_COMPENSATION_EVENTTHROWING = PATH_DIAGRAM + "/intermediateCompensationEventThrowing.bpmn";
    private static final String BPMN_ENDSIGNALEVENT = PATH_DIAGRAM + "/endSignalEvent.bpmn";
    private static final String BPMN_ENDMESSAGEEVENT = PATH_DIAGRAM + "/endMessageEvent.bpmn";
    private static final String BPMN_ENDNONEEVENT = PATH_DIAGRAM + "/endNoneEvent.bpmn";
    private static final String BPMN_ENDTERMINATEEVENT = PATH_DIAGRAM + "/endTerminateEvent.bpmn";
    private static final String BPMN_ENDESCALATIONEVENT = PATH_DIAGRAM + "/endEscalationEvent.bpmn";
    private static final String BPMN_ENDCOMPENSATIONEVENT = PATH_DIAGRAM + "/endCompensationEvent.bpmn";
    private static final String BPMN_PROCESSPROPERTIES = PATH_DIAGRAM + "/processProperties.bpmn";
    private static final String BPMN_BUSINESSRULETASKRULEFLOWGROUP = PATH_DIAGRAM + "/businessRuleTask.bpmn";
    private static final String BPMN_EVENT_SUBPROCESS_STARTERROREVENT = PATH_DIAGRAM + "/isInterruptingStartErrorEvent.bpmn";
    private static final String BPMN_REUSABLE_SUBPROCESS = PATH_DIAGRAM + "/reusableSubprocessCalledElement.bpmn";
    private static final String BPMN_REUSABLE_SUBPROCESS_MI = PATH_DIAGRAM + "/reusableSubProcessMI.bpmn";
    private static final String BPMN_EMBEDDED_SUBPROCESS = PATH_DIAGRAM + "/embeddedSubprocess.bpmn";
    private static final String BPMN_EVENT_SUBPROCESS = PATH_DIAGRAM + "/eventSubprocess.bpmn";
    private static final String BPMN_ADHOC_SUBPROCESS = PATH_DIAGRAM + "/adHocSubProcess.bpmn";
    private static final String BPMN_MULTIPLE_INSTANCE_SUBPROCESS = PATH_DIAGRAM + "/multipleInstanceSubprocess.bpmn";
    private static final String BPMN_SCRIPTTASK = PATH_DIAGRAM + "/scriptTask.bpmn";
    private static final String BPMN_USERTASKASSIGNEES = PATH_DIAGRAM + "/userTaskAssignees.bpmn";
    private static final String BPMN_USERTASKPROPERTIES = PATH_DIAGRAM + "/userTaskProperties.bpmn";
    private static final String BPMN_SEQUENCEFLOW = PATH_DIAGRAM + "/sequenceFlow.bpmn";
    private static final String BPMN_XORGATEWAY = PATH_DIAGRAM + "/xorGateway.bpmn";
    private static final String BPMN_INCLUSIVE_GATEWAY = PATH_DIAGRAM + "/inclusiveGateway.bpmn";
    private static final String BPMN_TIMER_EVENT = PATH_DIAGRAM + "/timerEvent.bpmn";
    private static final String BPMN_SIMULATIONPROPERTIES = PATH_DIAGRAM + "/simulationProperties.bpmn";
    private static final String BPMN_MAGNETDOCKERS = PATH_DIAGRAM + "/magnetDockers.bpmn";
    private static final String BPMN_MAGNETSINLANE = PATH_DIAGRAM + "/magnetsInLane.bpmn";
    private static final String BPMN_ENDERROR_EVENT = PATH_DIAGRAM + "/endErrorEvent.bpmn";
    private static final String BPMN_EVENT_DEFINITION_REF = PATH_DIAGRAM + "/eventDefinitionRef.bpmn";
    private static final String BPMN_SERVICE_TASKS = PATH_DIAGRAM + "/serviceTasks.bpmn";
    private static final String BPMN_NESTED_SUBPROCESSES = PATH_DIAGRAM + "/nestedSubprocesses.bpmn";
    private static final String BPMN_REASSIGNMENT_NOTIFICATION = PATH_DIAGRAM + "/reassignmentAndNotification.bpmn";
    private static final String BPMN_ARIS_LANES_1 = PATH_DIAGRAM + "/aris/ARIS_LANES_1.bpmn";
    private static final String BPMN_ARIS_LANES_2 = PATH_DIAGRAM + "/aris/ARIS_LANES_2.bpmn";
    private static final String BPMN_ARIS_LANES_3 = PATH_DIAGRAM + "/aris/ARIS_LANES_3.bpmn";
    private static final String ARIS_MULTIPLE_COLLAPSED_SUBPROCESSES = PATH_DIAGRAM + "/aris/ARIS_MULTIPLE_COLLAPSED_SUBPROCESSES.bpmn";
    private static final String ARIS_NESTED_COLLAPSED_SUBPROCESSES = PATH_DIAGRAM + "/aris/ARIS_NESTED_COLLAPSED_SUBPROCESES.bpmn";
    private static final String ARIS_COLLAPSED_SUBPROCESS_IN_LANE = PATH_DIAGRAM + "/aris/ARIS_COLLAPSED_SUBPROCESS_IN_LANE.bpmn";
    private static final String BPMN_LOG_TASK_JBPM_DESIGNER = PATH_DIAGRAM + "/logtask.bpmn";
    private static final String BPMN_SERVICETASKS_JBPM_DESIGNER = PATH_DIAGRAM + "/serviceTasksJBPMDeginer.bpmn";
    private static final String BPMN_EVENT_GATEWAY = PATH_DIAGRAM + "/eventGateway.bpmn";
    private static final String BPMN_TRAVELS = PATH_DIAGRAM + "/travels.bpmn";
    private static final String BPMN_FLIGHT_BOOKING = PATH_DIAGRAM + "/flightBooking.bpmn";

    private static final String NEW_LINE = System.lineSeparator();

    private StunnerTestingGraphAPI stunnerAPI;
    private XMLEncoderDiagramMetadataMarshaller xmlEncoder;
    private WorkItemDefinitionRegistry widRegistry;
    private WorkItemDefinitionLookupService widService;

    private BPMNDirectDiagramMarshaller tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        // Setup context.
        widRegistry = new WorkItemDefinitionMockRegistry();
        widService = mock(WorkItemDefinitionLookupService.class);
        when(widService.execute(any(Metadata.class))).thenReturn(widRegistry.items());
        stunnerAPI = StunnerTestingGraphBackendAPI.build(BPMNDefinitionSet.class,
                                                         new BPMNTestDefinitionFactory(widRegistry));
        xmlEncoder = new XMLEncoderDiagramMetadataMarshaller();
        // Setup tested instance.
        tested = new BPMNDirectDiagramMarshaller(xmlEncoder,
                                                 stunnerAPI.getDefinitionManager(),
                                                 stunnerAPI.getRuleManager(),
                                                 widService,
                                                 stunnerAPI.getFactoryManager(),
                                                 stunnerAPI.commandFactory,
                                                 stunnerAPI.commandManager);
    }

    //Unsupported nodes
    @Test
    public void testManualTask() throws Exception {
        final Diagram<Graph, Metadata> diagram = unmarshall(BPMN_MANUAL_TASK);
        final String result = tested.marshall(diagram);
        final String uuid = "_25DC90B5-E0BA-4D32-842E-DD11CE507B01";
        final Node<? extends Definition, ?> element = diagram.getGraph().getNode(uuid);
        assertDiagram(diagram, 4);
        assertTrue(element.getContent().getDefinition() instanceof NoneTask);
        assertTrue(result.contains("<bpmn2:task id=\"$uuid\" name=\"manual\">".replace("$uuid", uuid)));
    }

    @Test
    public void testSendTask() throws Exception {
        final Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SEND_TASK);
        final String result = tested.marshall(diagram);
        final String uuid = "_25DC90B5-E0BA-4D32-842E-DD11CE507B01";
        final Node<? extends Definition, ?> element = diagram.getGraph().getNode(uuid);
        assertDiagram(diagram, 4);
        assertTrue(element.getContent().getDefinition() instanceof NoneTask);
        assertTrue(result.contains("<bpmn2:task id=\"$uuid\" name=\"send\">".replace("$uuid", uuid)));
    }

    @Test
    public void testReceiveTask() throws Exception {
        final Diagram<Graph, Metadata> diagram = unmarshall(BPMN_RECEIVED_TASK);
        final String result = tested.marshall(diagram);
        final String uuid = "_25DC90B5-E0BA-4D32-842E-DD11CE507B01";
        final Node<? extends Definition, ?> element = diagram.getGraph().getNode(uuid);
        assertDiagram(diagram, 4);
        assertTrue(element.getContent().getDefinition() instanceof NoneTask);
        assertTrue(result.contains("<bpmn2:task id=\"$uuid\" name=\"received\">".replace("$uuid", uuid)));
    }

    @Test
    public void testDataObject() throws Exception {
        final Diagram<Graph, Metadata> diagram = unmarshall(BPMN_DATAOBJECT);
        final String result = tested.marshall(diagram);
        final String uuid = "dataObject1";
        final Node<? extends Definition, ?> element = diagram.getGraph().getNode(uuid);
        assertNull(element);
        assertDiagram(diagram, 4);
        assertFalse(result.contains("<bpmn2:dataObject id=\"dataObject1\""));
    }

    @Test
    public void testDataStore() throws Exception {
        final Diagram<Graph, Metadata> diagram = unmarshall(BPMN_DATASTORE);
        final String result = tested.marshall(diagram);
        final String uuid = "ID-f701d630-7adb-11e9-74db-069646171c32";
        final Node<? extends Definition, ?> element = diagram.getGraph().getNode(uuid);
        assertNull(element);
        assertDiagram(diagram, 2);
        assertFalse(result.contains("<semantic:dataStore name=\"Data store\" id=\"ID-f701d630-7adb-11e9-74db-069646171c32\"/>"));
    }

    @Test
    public void testChildLaneSet() throws Exception {
        final Diagram<Graph, Metadata> diagram = unmarshall(BPMN_CHILDLANESET);
        final String result = tested.marshall(diagram);

        final List<String> uudis = Arrays.asList("ID-d840fb6a-2566-11e9-4768-06332ceaf548",
                                                 "ID-953d72f2-0f1d-11e7-62b5-d0bf9cf32000",
                                                 "ID-bd86bfb8-3391-11e9-4932-0200a2035cb6");

        assertTrue(uudis.stream()
                           .map(diagram.getGraph()::getNode)
                           .map(Node::getContent)
                           .map(Definition.class::cast)
                           .map(Definition::getDefinition)
                           .allMatch(Lane.class::isInstance));

        assertDiagram(diagram, 63);
        assertTrue(uudis.stream()
                           .allMatch(uuid -> result.contains("<bpmn2:lane id=\"$uuid\"".replace("$uuid", uuid))));
    }

    @Test
    public void testExecution() throws Exception {
        final Diagram<Graph, Metadata> diagram = unmarshall(EXECUTION);
        String result = tested.marshall(diagram);
        assertDiagram(diagram, 23);
    }

    @Test
    public void testJBPMAllElements() throws Exception {
        //assert no errors unmarshalling all elements
        final Diagram<Graph, Metadata> diagram = unmarshall(JBPM_DESIGNER_ALL_ELEMENTS);
        assertDiagram(diagram, 68);
    }

    //END Unsupported nodes

    // 4 nodes expected: BPMNDiagram, StartNode, Task and EndNode
    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallBasic() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BASIC);
        assertDiagram(diagram,
                      4);
        assertEquals("Basic process",
                     diagram.getMetadata().getTitle());

        Node<? extends Definition, ?> diagramNode = diagram.getGraph().getNode("_8nbnEfbPEeWV2qFDuocQ6Q");
        assertTrue(diagramNode.getContent().getDefinition() instanceof BPMNDiagram);
        BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) diagramNode.getContent().getDefinition();
        assertTrue(bpmnDiagram.getDiagramSet() != null);
        assertTrue(bpmnDiagram.getDiagramSet().getExecutable() != null);
        assertTrue(bpmnDiagram.getDiagramSet().getExecutable().getValue());

        Node<? extends Definition, ?> task1 = diagram.getGraph().getNode("810797AB-7D09-4E1F-8A5B-96C424E4B031");
        assertTrue(task1.getContent().getDefinition() instanceof NoneTask);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallLogTaskJBPMDesigner() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_LOG_TASK_JBPM_DESIGNER);
        assertDiagram(diagram, 4);

        Node<? extends Definition, ?> log = diagram.getGraph().getNode("_AE76ACC9-CCD0-425D-BD40-5E4F3533A1DF");
        assertTrue(log.getContent().getDefinition() instanceof CustomTask);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallServiceTaskJBPMDesigner() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SERVICETASKS_JBPM_DESIGNER);
        assertDiagram(diagram, 9);

        Node<? extends Definition, ?> email = diagram.getGraph().getNode("_2255B80D-ADCF-47C8-A16F-82E7BB9AD929");
        Node<? extends Definition, ?> rest = diagram.getGraph().getNode("_D8B91719-0540-4A98-9734-CAF4C703B051");
        Node<? extends Definition, ?> ws = diagram.getGraph().getNode("_FD3F17AB-199B-4A59-A8B4-CBDCCDBFF7DA");
        Node<? extends Definition, ?> log = diagram.getGraph().getNode("_324A9674-039E-4B80-80EF-A9B6A44ACA33");

        assertTrue(email.getContent().getDefinition() instanceof CustomTask);
        assertTrue(rest.getContent().getDefinition() instanceof CustomTask);
        assertTrue(ws.getContent().getDefinition() instanceof CustomTask);
        assertTrue(log.getContent().getDefinition() instanceof CustomTask);
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
        Bound task1ULBound = task1Bounds.getUpperLeft();
        Bound task1LRBound = task1Bounds.getLowerRight();
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
    public void testUnmarshallFlightBooking() throws Exception {
        final Diagram<Graph, Metadata> diagram = unmarshall(BPMN_FLIGHT_BOOKING);

        //User Task 1
        Node<? extends View, ?> serviceTask1Node = diagram.getGraph().getNode("ServiceTask_1");
        GenericServiceTask serviceTask1 = (GenericServiceTask) serviceTask1Node.getContent().getDefinition();

        //Assert properties from Generic Service Task
        GenericServiceTaskValue serviceTaskValue = serviceTask1.getExecutionSet().getGenericServiceTaskInfo().getValue();
        assertEquals("Java", serviceTaskValue.getServiceImplementation());
        assertEquals("org.acme.travels.service.FlightBookingService", serviceTaskValue.getServiceInterface());
        assertEquals("bookFlight", serviceTaskValue.getServiceOperation());
        assertEquals("org.acme.travels.Trip", serviceTaskValue.getInMessageStructure());
        assertEquals("org.acme.travels.Flight", serviceTaskValue.getOutMessagetructure());

        String marshalled = tested.marshall(diagram);
        assertTrue(marshalled.contains("<bpmn2:itemDefinition id=\"ServiceTask_1_InMessageType\" structureRef=\"org.acme.travels.Trip\"/>"));
        assertTrue(marshalled.contains("<bpmn2:itemDefinition id=\"ServiceTask_1_OutMessageType\" structureRef=\"org.acme.travels.Flight\"/>"));
    }

    @Test
    public void testUnmarshallTravels() throws Exception {
        final Diagram<Graph, Metadata> diagram = unmarshall(BPMN_TRAVELS);

        //User Task 1
        Node<? extends View, ?> userTask1Node = diagram.getGraph().getNode("UserTask_1");
        UserTask userTask1 = (UserTask) userTask1Node.getContent().getDefinition();
        ParsedAssignmentsInfo parsedAssignmentsInfo = ParsedAssignmentsInfo.fromString(userTask1.getExecutionSet().getAssignmentsinfo().getValue());
        assertDataTye("org.acme.travels.Trip", "trip", parsedAssignmentsInfo.getInputs());
        assertDataTye("org.acme.travels.Traveller", "traveller", parsedAssignmentsInfo.getInputs());
        assertDataTye("java.lang.Boolean", "Skippable", parsedAssignmentsInfo.getInputs());
        assertDataTye("java.lang.Integer", "Priority", parsedAssignmentsInfo.getInputs());
        assertDataTye("java.lang.String", "Comment", parsedAssignmentsInfo.getInputs());

        //Business Rule 1
        Node<? extends View, ?> businessRuleTaskNode = diagram.getGraph().getNode("BusinessRuleTask_1");
        BusinessRuleTask businessRuleTask1 = (BusinessRuleTask) businessRuleTaskNode.getContent().getDefinition();
        ParsedAssignmentsInfo businessRuleParsedAssignmentsInfo =
                ParsedAssignmentsInfo.fromString(businessRuleTask1.getDataIOSet().getAssignmentsinfo().getValue());

        assertDataTye("org.acme.travels.Traveller", "traveller", businessRuleParsedAssignmentsInfo.getInputs());
        assertDataTye("org.acme.travels.Trip", "trip", businessRuleParsedAssignmentsInfo.getInputs());
        assertDataTye("org.acme.travels.Trip", "trip", businessRuleParsedAssignmentsInfo.getOutputs());
    }

    private void assertDataTye(String expectedType, String name, DeclarationList declarationList) {
        assertEquals(expectedType,
                     declarationList
                             .getDeclarations()
                             .stream()
                             .filter(d -> Objects.equals(d.getIdentifier(), name))
                             .map(VariableDeclaration::getType)
                             .findFirst()
                             .orElse(null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallProcessVariables() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSVARIABLES);
        assertDiagram(diagram, 8);
        assertEquals("ProcessVariables", diagram.getMetadata().getTitle());

        BPMNDiagramImpl bpmnDiagram = getBpmnDiagram(diagram);
        ProcessVariables variables = bpmnDiagram.getProcessData().getProcessVariables();
        assertEquals(variables.getValue(),
                     "employee:java.lang.String:[],reason:java.lang.String:[],performance:java.lang.String:[]");

        Node<? extends Definition, ?> diagramNode = diagram.getGraph().getNode("_luRBMdEjEeWXpsZ1tNStKQ");
        assertTrue(diagramNode.getContent().getDefinition() instanceof BPMNDiagram);
        bpmnDiagram = (BPMNDiagramImpl) diagramNode.getContent().getDefinition();
        assertTrue(bpmnDiagram.getProcessData() != null);
        assertTrue(bpmnDiagram.getProcessData().getProcessVariables() != null);

        variables = bpmnDiagram.getProcessData().getProcessVariables();
        assertEquals(variables.getValue(),
                     "employee:java.lang.String:[],reason:java.lang.String:[],performance:java.lang.String:[]");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallGlobalVariables() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_GLOBALVARIABLES);
        assertDiagram(diagram, 1);
        assertEquals("Global Variables", diagram.getMetadata().getTitle());

        BPMNDiagramImpl bpmnDiagram = getBpmnDiagram(diagram);
        GlobalVariables variables = bpmnDiagram.getAdvancedData().getGlobalVariables();
        assertEquals("GV1:Boolean,GV2:Boolean,GV3:Integer",
                     variables.getValue());

        Node<? extends Definition, ?> diagramNode = diagram.getGraph().getNode("__-CvwCveEemCffTTkSwXXQ");
        assertTrue(diagramNode.getContent().getDefinition() instanceof BPMNDiagram);
        bpmnDiagram = (BPMNDiagramImpl) diagramNode.getContent().getDefinition();
        assertTrue(bpmnDiagram.getDiagramSet() != null);
        assertTrue(bpmnDiagram.getAdvancedData().getGlobalVariables() != null);

        variables = bpmnDiagram.getAdvancedData().getGlobalVariables();
        assertEquals("GV1:Boolean,GV2:Boolean,GV3:Integer",
                     variables.getValue());
    }

    private BPMNDiagramImpl getBpmnDiagram(Diagram<Graph, Metadata> diagram) {
        BPMNDiagramImpl bpmnDiagram = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof BPMNDiagram) {
                    bpmnDiagram = (BPMNDiagramImpl) oDefinition;
                    break;
                }
            }
        }
        return bpmnDiagram;
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
        assertEquals("12/25/1983",
                     diagramProperties.getSlaDueDate().getValue());
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
    public void testUnmarshallUserTaskMI() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASK_MI);
        assertDiagram(diagram, 4);
        assertEquals("userTaskMI", diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> node = diagram.getGraph().getNode("_CACC5C21-CE79-4445-9411-BE8C7A75E860");
        assertNotNull(node);
        UserTask userTask = (UserTask) node.getContent().getDefinition();
        assertNotNull(userTask);
        assertEquals(TaskTypes.USER, userTask.getTaskType().getValue());
        assertEquals("TheUserTask", userTask.getGeneral().getName().getValue());
        UserTaskExecutionSet executionSet = userTask.getExecutionSet();
        assertTrue(executionSet.getIsMultipleInstance().getValue());
        assertEquals("theInputCollection", executionSet.getMultipleInstanceCollectionInput().getValue());
        assertEquals("theInputVariable:java.lang.Object", executionSet.getMultipleInstanceDataInput().getValue());
        assertEquals("theOutputCollection", executionSet.getMultipleInstanceCollectionOutput().getValue());
        assertEquals("theOutputVariable:java.lang.Object", executionSet.getMultipleInstanceDataOutput().getValue());
        assertEquals("theCompletionCondition", executionSet.getMultipleInstanceCompletionCondition().getValue());
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
        assertEquals("12/25/1983", startNoneEvent.getExecutionSet().getSlaDueDate().getValue());
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
        assertEquals(false, isInterrupting.getValue());
        assertEquals("12/25/1983", startTimerEvent.getExecutionSet().getSlaDueDate().getValue());
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
        assertEquals("12/25/1983", startSignalEvent.getExecutionSet().getSlaDueDate().getValue());
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
        assertEquals("12/25/1983", startErrorEvent.getExecutionSet().getSlaDueDate().getValue());

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
        assertEquals("12/25/1983", startMessageEvent.getExecutionSet().getSlaDueDate().getValue());
        DataIOSet dataIOSet = startMessageEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||StartMessageEventOutputVar1:String||[dout]StartMessageEventOutputVar1->var1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartConditionalEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTCONDITIONALEVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("StartConditionalEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startEventNode = diagram.getGraph().getNode("_8F9C10C4-F1EE-4B49-B4CE-3059ADD4B391");
        assertNotNull(startEventNode);
        StartConditionalEvent startConditionalEvent = (StartConditionalEvent) startEventNode.getContent().getDefinition();

        assertNotNull(startConditionalEvent.getGeneral());
        assertEquals("StartConditionalEventName",
                     startConditionalEvent.getGeneral().getName().getValue());
        assertEquals("StartConditionalEventDocumentation",
                     startConditionalEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(startConditionalEvent.getExecutionSet());
        assertEquals("drools",
                     startConditionalEvent.getExecutionSet().getConditionExpression().getValue().getLanguage());
        assertEquals("StartConditionalEventConditionExpression",
                     startConditionalEvent.getExecutionSet().getConditionExpression().getValue().getScript());
        assertEquals(true,
                     startConditionalEvent.getExecutionSet().getIsInterrupting().getValue());
        assertEquals("12/25/1983", startConditionalEvent.getExecutionSet().getSlaDueDate().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartEscalationEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTESCALATIONEVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("StartEscalationEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startEventNode = diagram.getGraph().getNode("_D5AAA79F-9CD6-43C1-92E2-5D3C9340A303");
        assertNotNull(startEventNode);
        StartEscalationEvent startEscalationEvent = (StartEscalationEvent) startEventNode.getContent().getDefinition();

        assertNotNull(startEscalationEvent.getGeneral());
        assertEquals("StartEscalationEventName",
                     startEscalationEvent.getGeneral().getName().getValue());
        assertEquals("StartEscalationEventDocumentation",
                     startEscalationEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(startEscalationEvent.getExecutionSet());
        assertEquals("EscalationCode",
                     startEscalationEvent.getExecutionSet().getEscalationRef().getValue());
        assertEquals(true,
                     startEscalationEvent.getExecutionSet().getIsInterrupting().getValue());
        assertEquals("12/25/1983", startEscalationEvent.getExecutionSet().getSlaDueDate().getValue());

        DataIOSet dataIOSet = startEscalationEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||escalationOutput:String||[dout]escalationOutput->processVar1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartCompensationEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTCOMPENSATIONEVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("StartCompensationEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startEventNode = diagram.getGraph().getNode("_19C23644-6CF0-4508-81B2-4CA2179137AB");
        assertNotNull(startEventNode);
        StartCompensationEvent startCompensationEvent = (StartCompensationEvent) startEventNode.getContent().getDefinition();

        assertNotNull(startCompensationEvent.getGeneral());
        assertEquals("StartCompensationEventName",
                     startCompensationEvent.getGeneral().getName().getValue());
        assertEquals("StartCompensationEventDocumentation",
                     startCompensationEvent.getGeneral().getDocumentation().getValue());
        assertFalse(startCompensationEvent.getExecutionSet().getIsInterrupting().getValue());
        assertEquals("12/25/1983", startCompensationEvent.getExecutionSet().getSlaDueDate().getValue());
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
        assertEquals("12/25/1983", intermediateTimerEvent.getExecutionSet().getSlaDueDate().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateConditionalEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_CONDITIONAL_EVENTS);
        assertDiagram(diagram,
                      4);
        assertEquals("IntermediateConditionalEvents",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> catchingEventNode = diagram.getGraph().getNode("_31A24997-C7B3-4286-8B4D-0EFD7CB11206");
        assertNotNull(catchingEventNode);
        IntermediateConditionalEvent catchingConditionalEvent = (IntermediateConditionalEvent) catchingEventNode.getContent().getDefinition();

        assertNotNull(catchingConditionalEvent.getGeneral());
        assertEquals("CatchingIntermediateConditionalEventName",
                     catchingConditionalEvent.getGeneral().getName().getValue());
        assertEquals("CatchingIntermediateConditionalDocumentation",
                     catchingConditionalEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(catchingConditionalEvent.getExecutionSet());
        assertEquals("drools",
                     catchingConditionalEvent.getExecutionSet().getConditionExpression().getValue().getLanguage());
        assertEquals("CatchingIntermediateConditionalEventCompletionCondition",
                     catchingConditionalEvent.getExecutionSet().getConditionExpression().getValue().getScript());
        assertEquals(true,
                     catchingConditionalEvent.getExecutionSet().getCancelActivity().getValue());
        assertEquals("12/25/1983", catchingConditionalEvent.getExecutionSet().getSlaDueDate().getValue());

        Node<? extends Definition, ?> boundaryEventNode = diagram.getGraph().getNode("_FD2DB546-4A85-4C50-9003-548A9A354F97");
        assertNotNull(boundaryEventNode);
        IntermediateConditionalEvent boundaryConditionalEvent = (IntermediateConditionalEvent) boundaryEventNode.getContent().getDefinition();

        assertNotNull(boundaryConditionalEvent.getGeneral());
        assertEquals("BoundaryConditionalEventName",
                     boundaryConditionalEvent.getGeneral().getName().getValue());
        assertEquals("BoundaryConditionalEventDocumentation",
                     boundaryConditionalEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(boundaryConditionalEvent.getExecutionSet());
        assertEquals("drools",
                     boundaryConditionalEvent.getExecutionSet().getConditionExpression().getValue().getLanguage());
        assertEquals("BoundaryConditionalEventCompletionCondition",
                     boundaryConditionalEvent.getExecutionSet().getConditionExpression().getValue().getScript());
        assertEquals(true,
                     boundaryConditionalEvent.getExecutionSet().getCancelActivity().getValue());
        assertEquals("12/25/1983", boundaryConditionalEvent.getExecutionSet().getSlaDueDate().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateEscalationEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_ESCALATION_EVENTS);
        assertDiagram(diagram,
                      3);
        assertEquals("IntermediateEscalationEvents",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> catchingEventNode = diagram.getGraph().getNode("_C7F2BA98-1100-4CF8-A994-40E2B65F6E5D");
        assertNotNull(catchingEventNode);
        IntermediateEscalationEvent intermediateEscalationEvent = (IntermediateEscalationEvent) catchingEventNode.getContent().getDefinition();

        assertNotNull(intermediateEscalationEvent.getGeneral());
        assertEquals("BoundaryEscalationEventName",
                     intermediateEscalationEvent.getGeneral().getName().getValue());
        assertEquals("BoundaryEscalationEventDocumentation",
                     intermediateEscalationEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(intermediateEscalationEvent.getExecutionSet());
        assertEquals(true,
                     intermediateEscalationEvent.getExecutionSet().getCancelActivity().getValue());
        assertEquals("EscalationCode",
                     intermediateEscalationEvent.getExecutionSet().getEscalationRef().getValue());
        DataIOSet dataIOSet = intermediateEscalationEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||escalationOutput:String||[dout]escalationOutput->processVar1",
                     assignmentsInfo.getValue());
        assertEquals("12/25/1983", intermediateEscalationEvent.getExecutionSet().getSlaDueDate().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateCompensationEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_COMPENSATION_EVENTS);
        assertDiagram(diagram,
                      3);
        assertEquals("IntermediateCompensationEvents",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> catchingEventNode = diagram.getGraph().getNode("_8CE8AB54-7E8B-4032-B340-2DBEBAB5080F");
        assertNotNull(catchingEventNode);
        IntermediateCompensationEvent intermediateCompensationEvent = (IntermediateCompensationEvent) catchingEventNode.getContent().getDefinition();

        assertNotNull(intermediateCompensationEvent.getGeneral());
        assertEquals("IntermediateCompensationEventName",
                     intermediateCompensationEvent.getGeneral().getName().getValue());
        assertEquals("IntermediateCompensationEventDocumentation",
                     intermediateCompensationEvent.getGeneral().getDocumentation().getValue());
        assertEquals("IntermediateCompensationEventDocumentation",
                     intermediateCompensationEvent.getGeneral().getDocumentation().getValue());
        assertEquals("12/25/1983", intermediateCompensationEvent.getExecutionSet().getSlaDueDate().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateCompensationEventsWithAssociations() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_COMPENSATION_EVENTS_WITH_ASSOCIATION);
        assertDiagram(diagram,
                      6);
        assertEquals("IntermediateCompensationEventsWithAssociation",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> catchingEventNode = diagram.getGraph().getNode("_DF70C614-A641-4109-8A8D-506B15E3F31B");
        assertNotNull(catchingEventNode);
        IntermediateCompensationEvent intermediateCompensationEvent = (IntermediateCompensationEvent) catchingEventNode.getContent().getDefinition();

        assertTrue(GraphUtils.isDockedNode(catchingEventNode));
        assertNotNull(intermediateCompensationEvent.getGeneral());
        assertEquals("IntermediateCompensationEventName",
                     intermediateCompensationEvent.getGeneral().getName().getValue());
        assertEquals("IntermediateCompensationEventDocumentation",
                     intermediateCompensationEvent.getGeneral().getDocumentation().getValue());

        assertEquals("12/25/1983", intermediateCompensationEvent.getExecutionSet().getSlaDueDate().getValue());

        Node<? extends Definition, ?> userTask1Node = diagram.getGraph().getNode("_C18CC8D2-D7CA-457D-9258-01D1E6973A86");
        assertNotNull(userTask1Node);
        UserTask userTask1 = (UserTask) userTask1Node.getContent().getDefinition();
        assertEquals("Task1",
                     userTask1.getGeneral().getName().getValue());
        assertEquals("Task1Documentation",
                     userTask1.getGeneral().getDocumentation().getValue());
        assertEquals(userTask1Node,
                     GraphUtils.getDockParent(catchingEventNode).orElse(null));

        Node<? extends Definition, ?> userTask2Node = diagram.getGraph().getNode("_7EF24042-BD4E-4843-9874-8AC3F7AFF3CD");
        assertNotNull(userTask2Node);
        UserTask userTask2 = (UserTask) userTask2Node.getContent().getDefinition();
        assertEquals("Task2",
                     userTask2.getGeneral().getName().getValue());
        assertEquals("Task2Documentation",
                     userTask2.getGeneral().getDocumentation().getValue());

        Edge associationEdge = userTask2Node.getInEdges().stream()
                .filter(edge -> edge.getUUID().equals("_B41D28D1-FC39-40E8-BF89-C57649989014"))
                .map(e -> e.asEdge())
                .findFirst().orElse(null);
        assertNotNull(associationEdge);
        assertNotNull(associationEdge.getContent());
        Association association = (Association) ((View) associationEdge.getContent()).getDefinition();
        assertEquals("AssociationDocumentation",
                     association.getGeneral().getDocumentation().getValue());

        assertEquals(associationEdge.getSourceNode(),
                     catchingEventNode);
        assertEquals(associationEdge.getTargetNode(),
                     userTask2Node);
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
        assertEquals("12/25/1983", intermediateSignalEventCatching.getExecutionSet().getSlaDueDate().getValue());

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
        assertEquals("12/25/1983", intermediateErrorEventCatching.getExecutionSet().getSlaDueDate().getValue());

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
    public void testUnmarshallIntermediateEscalationEventThrowing() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_ESCALATION_EVENTTHROWING);
        assertDiagram(diagram,
                      2);
        assertEquals("IntermediateEscalationEventThrowing",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> throwingEventNode = diagram.getGraph().getNode("_8516D854-F67F-4697-9837-40A32033AE25");
        assertNotNull(throwingEventNode);
        IntermediateEscalationEventThrowing throwingEscalationEvent = (IntermediateEscalationEventThrowing) throwingEventNode.getContent().getDefinition();

        assertNotNull(throwingEscalationEvent.getGeneral());
        assertEquals("ThrowingEscalationEventName",
                     throwingEscalationEvent.getGeneral().getName().getValue());
        assertEquals("ThrowingEscalationEventDocumentation",
                     throwingEscalationEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(throwingEscalationEvent.getExecutionSet());
        assertEquals("EscalationCode",
                     throwingEscalationEvent.getExecutionSet().getEscalationRef().getValue());
        DataIOSet dataIOSet = throwingEscalationEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("escalationOutput:String||||[din]processVar1->escalationOutput",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateCompensationEventThrowing() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_COMPENSATION_EVENTTHROWING);
        assertDiagram(diagram,
                      3);
        assertEquals("IntermediateCompensationEventThrowing",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> throwingEventNode = diagram.getGraph().getNode("_F1D87D25-4D73-4DC5-A0C2-C627CED773BA");
        assertNotNull(throwingEventNode);
        IntermediateCompensationEventThrowing throwingCompensationEvent = (IntermediateCompensationEventThrowing) throwingEventNode.getContent().getDefinition();

        assertNotNull(throwingCompensationEvent.getGeneral());
        assertEquals("ThrowingCompensationEventName",
                     throwingCompensationEvent.getGeneral().getName().getValue());
        assertEquals("ThrowingCompensationEventDocumentation",
                     throwingCompensationEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(throwingCompensationEvent.getExecutionSet());
        assertEquals("_E318295E-B0B6-4FB2-B5EB-A43BFD44FCBD",
                     throwingCompensationEvent.getExecutionSet().getActivityRef().getValue());
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
        assertEquals("12/25/1983", intermediateMessageEventCatching.getExecutionSet().getSlaDueDate().getValue());
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
    public void testUnmarshallIsInterruptingStartErrorEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVENT_SUBPROCESS_STARTERROREVENT);
        assertDiagram(diagram, 7);
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
        assertEquals("12/25/1983", eventExecutionSet.getSlaDueDate().getValue());

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
        assertEquals("", emptyExecutionSet.getSlaDueDate().getValue());

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

    @Test
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
    public void testUnmarshallEndEscalationEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDESCALATIONEVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("EndEscalationEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> endEventNode = diagram.getGraph().getNode("_8F6A4096-26AA-4C14-B1F0-B96ED24BD5C7");
        assertNotNull(endEventNode);
        EndEscalationEvent endEscalationEvent = (EndEscalationEvent) endEventNode.getContent().getDefinition();

        assertNotNull(endEscalationEvent.getGeneral());
        assertEquals("EndEscalationEventName",
                     endEscalationEvent.getGeneral().getName().getValue());
        assertEquals("EndEscalationEventDocumentation",
                     endEscalationEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(endEscalationEvent.getExecutionSet());
        assertEquals("EscalationCode",
                     endEscalationEvent.getExecutionSet().getEscalationRef().getValue());
        DataIOSet dataIOSet = endEscalationEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("escalationInput:String||||[din]processVar1->escalationInput",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEndCompensationEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDCOMPENSATIONEVENT);
        assertDiagram(diagram,
                      3);
        assertEquals("EndCompensationEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> endEventNode = diagram.getGraph().getNode("_2AEA503B-26E5-4F11-A54D-3E275F8BFA6A");
        assertNotNull(endEventNode);
        EndCompensationEvent endCompensationEvent = (EndCompensationEvent) endEventNode.getContent().getDefinition();

        assertNotNull(endCompensationEvent.getGeneral());
        assertEquals("EndCompensationEventName",
                     endCompensationEvent.getGeneral().getName().getValue());
        assertEquals("EndCompensationEventDocumentation",
                     endCompensationEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(endCompensationEvent.getExecutionSet());
        assertEquals("_7795AFA7-E602-4D90-B4B6-249639C4D965",
                     endCompensationEvent.getExecutionSet().getActivityRef().getValue());
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
        Bound ul = bounds.getUpperLeft();
        Bound lr = bounds.getLowerRight();
        assertEquals(305,
                     ul.getX(),
                     0);
        assertEquals(300,
                     ul.getY(),
                     0);
        assertEquals(361,
                     lr.getX(),
                     0);
        assertEquals(356,
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
        Bound ul = bounds.getUpperLeft();
        Bound lr = bounds.getLowerRight();
        assertEquals(57,
                     ul.getX(),
                     0);
        assertEquals(70,
                     ul.getY(),
                     0);
        assertEquals(113,
                     lr.getX(),
                     0);
        assertEquals(126,
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
                     javascriptScriptTask.getExecutionSet().getAdHocAutostart().getValue().toString());

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
        assertEquals("false",
                     javaScriptTask.getExecutionSet().getAdHocAutostart().getValue().toString());
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
                     businessRuleTask.getExecutionSet().getRuleFlowGroup().getName());
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
    @SuppressWarnings("unchecked")
    public void testUnmarshallInclusiveGateway() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INCLUSIVE_GATEWAY);
        assertDiagram(diagram,
                      7);
        assertEquals(diagram.getMetadata().getTitle(),
                     "TestInclusiveGateway");
        Graph graph = diagram.getGraph();
        Node<? extends Definition, ?> gatewayNode = graph.getNode("_526EE472-FE8B-4E9A-A951-CFBA86C3691F");
        assertTrue(gatewayNode.getContent().getDefinition() instanceof InclusiveGateway);
        InclusiveGateway inclusiveGateway = (InclusiveGateway) gatewayNode.getContent().getDefinition();
        assertEquals("InclusiveGatewayName",
                     inclusiveGateway.getGeneral().getName().getValue());
        assertEquals("_3D5701E9-CFD3-4218-9200-897B6D4FF041",
                     inclusiveGateway.getExecutionSet().getDefaultRoute().getValue());
        SequenceFlow sequenceFlow1 = null;
        SequenceFlow sequenceFlow2 = null;
        List<Edge> outEdges = (List<Edge>) gatewayNode.getOutEdges();
        if (outEdges != null) {
            for (Edge edge : outEdges) {
                if ("_3D5701E9-CFD3-4218-9200-897B6D4FF041".equals(edge.getUUID())) {
                    sequenceFlow1 = (SequenceFlow) ((ViewConnector) edge.getContent()).getDefinition();
                } else if ("_A414F16D-90BB-4742-A4E7-EBF7EA1ECD7E".equals(edge.getUUID())) {
                    sequenceFlow2 = (SequenceFlow) ((ViewConnector) edge.getContent()).getDefinition();
                }
            }
        }
        assertNotNull(sequenceFlow1);
        assertEquals("OutSequence1",
                     sequenceFlow1.getGeneral().getName().getValue());
        assertNotNull(sequenceFlow2);
        assertEquals("OutSequence2",
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
        BaseReusableSubprocessTaskExecutionSet executionSet = reusableSubprocess.getExecutionSet();
        assertNotNull(generalSet);
        assertNotNull(executionSet);

        assertEquals("my subprocess",
                     generalSet.getName().getValue());
        assertEquals("my-called-element\" <&> \"",
                     executionSet.getCalledElement().getValue());
        assertEquals(false,
                     executionSet.getIndependent().getValue());
        assertEquals(true,
                     executionSet.getWaitForCompletion().getValue());

        String assignmentsInfo = reusableSubprocess.getDataIOSet().getAssignmentsinfo().getValue();
        assertEquals("|input1:String,input2:Float||output1:String,output2:Float|[din]pv1->input1,[din]pv2->input2,[dout]output1->pv1,[dout]output2->pv2",
                     assignmentsInfo);

        assertEquals("true",
                     reusableSubprocess.getExecutionSet().getIsAsync().getValue().toString());

        final String SLA_DUE_DATE = "12/25/1983";
        assertEquals(SLA_DUE_DATE, executionSet.getSlaDueDate().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallReusableSubprocessMI() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_REUSABLE_SUBPROCESS_MI);
        assertDiagram(diagram, 4);
        assertEquals("reusableSubProcessMI", diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> node = diagram.getGraph().getNode("_CACC5C21-CE79-4445-9411-BE8C7A75E860");
        assertNotNull(node);
        ReusableSubprocess subprocess = (ReusableSubprocess) node.getContent().getDefinition();
        assertNotNull(subprocess);
        assertEquals("TheReusableSubProcess", subprocess.getGeneral().getName().getValue());
        ReusableSubprocessTaskExecutionSet executionSet = subprocess.getExecutionSet();
        assertEquals("test.SubProcess", executionSet.getCalledElement().getValue());
        assertTrue(executionSet.getIsMultipleInstance().getValue());
        assertEquals("theInputCollection", executionSet.getMultipleInstanceCollectionInput().getValue());
        assertEquals("theInputVariable:java.lang.Object", executionSet.getMultipleInstanceDataInput().getValue());
        assertEquals("theOutputCollection", executionSet.getMultipleInstanceCollectionOutput().getValue());
        assertEquals("theOutputVariable:java.lang.Object", executionSet.getMultipleInstanceDataOutput().getValue());
        assertEquals("theCompletionCondition", executionSet.getMultipleInstanceCompletionCondition().getValue());

        final String SLA_DUE_DATE = "12/25/1983";
        assertEquals(SLA_DUE_DATE, executionSet.getSlaDueDate().getValue());
    }

    @Test
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

        assertEquals("subProcessVar1:String:[],subProcessVar2:String:[]",
                     processData.getProcessVariables().getValue());

        assertTrue(executionSet.getIsAsync().getValue());

        final String SLA_DUE_DATE = "12/25/1983";
        assertEquals(SLA_DUE_DATE, executionSet.getSlaDueDate().getValue());
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

    private void testMagnetsInLane(Diagram<Graph, Metadata> diagram) throws Exception {
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

    @Test
    public void testUnmarshallEventDefinitionRef() {
        try {
            Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVENT_DEFINITION_REF);
            Node<? extends Definition<IntermediateTimerEvent>, ?> intermediateTimerEvent =
                    diagram.getGraph().getNode("FLOWNODE_9e71d692-986c-11e7-40d3-005056844bde");
            IntermediateTimerEvent definition = intermediateTimerEvent.getContent().getDefinition();
            CancellingTimerEventExecutionSet executionSet = definition.getExecutionSet();
            TimerSettings timerSettings = executionSet.getTimerSettings();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testMarshallBasic() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BASIC);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);
    }

    @Test
    public void testMarshallEvaluation() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVALUATION);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      7,
                      7);
    }

    @Test
    public void testMarshallNotBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_NOT_BOUNDARY_EVENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      5,
                      4);
    }

    @Test
    public void testMarshallBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BOUNDARY_EVENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      5,
                      3);
        // Assert that the boundary event location and size are the expected ones.
        assertTrue(result.contains("Bounds height=\"56.0\" width=\"56.0\" x=\"327.0\" y=\"210.0\""));
    }

    @Test
    public void testMarshallProcessVariables() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSVARIABLES);

        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      7,
                      7);

        Definitions definitions =
                new DefinitionsConverter(diagram.getGraph()).toDefinitions();

        assertNotNull(definitions);
        List<RootElement> rootElements = definitions.getRootElements();
        assertNotNull(rootElements);

        assertItemExists(rootElements,
                         "_employeeItem",
                         "java.lang.String");
        assertItemExists(rootElements,
                         "_reasonItem",
                         "java.lang.String");
        assertItemExists(rootElements,
                         "_performanceItem",
                         "java.lang.String");

        Process process = getProcess(definitions);
        assertNotNull(process);
        List<Property> properties = process.getProperties();
        assertNotNull(properties);
        assertNotNull(getProcessProperty(properties,
                                         "employee",
                                         "_employeeItem"));
        assertNotNull(getProcessProperty(properties,
                                         "reason",
                                         "_reasonItem"));
        assertNotNull(getProcessProperty(properties,
                                         "performance",
                                         "_performanceItem"));
    }

    @Test
    public void testMarshallProcessProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSPROPERTIES);

        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);

        Definitions definitions =
                new DefinitionsConverter(diagram.getGraph()).toDefinitions();
        assertNotNull(definitions);
        Process process = getProcess(definitions);
        assertNotNull(process);

        assertEquals("JDLProj.BPSimple",
                     process.getId());
        assertEquals("BPSimple",
                     process.getName());
        assertTrue(process.isIsExecutable());
        assertEquals("true",
                     getProcessPropertyValue(process,
                                             "adHoc"));
        assertEquals("org.jbpm",
                     getProcessPropertyValue(process,
                                             "packageName"));
        assertEquals("1.0",
                     getProcessPropertyValue(process,
                                             "version"));
        assertNotNull(process.getDocumentation());
        assertFalse(process.getDocumentation().isEmpty());
        assertEquals("<![CDATA[This is a\nsimple\nprocess]]>",
                     process.getDocumentation().get(0).getText());
        assertEquals("<![CDATA[This is the\nProcess\nInstance\nDescription]]>",
                     getProcessExtensionValue(process,
                                              "customDescription"));
        assertEquals("<![CDATA[12/25/1983]]>",
                     getProcessExtensionValue(process,
                                              "customSLADueDate"));
    }

    @Test
    public void testMarshallUserTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNMENTS);
        //JBPMBpmn2ResourceImpl resource = tested.marshallToBpmn2Resource(diagram);

        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      7,
                      7);

        DefinitionsConverter definitionsConverter = new DefinitionsConverter(diagram.getGraph());

        Definitions definitions = definitionsConverter.toDefinitions();
        assertNotNull(definitions);
        Process process = getProcess(definitions);
        assertNotNull(process);
        org.eclipse.bpmn2.UserTask userTask = (org.eclipse.bpmn2.UserTask) getNamedFlowElement(process,
                                                                                               org.eclipse.bpmn2.UserTask.class,
                                                                                               "Self Evaluation");
        assertNotNull(userTask);
        DataInput dataInput = getDataInput(userTask,
                                           "reason");

        // this fails because of type
        validateDataInputOrOutput(dataInput,
                                  "_reasonInputX",
                                  "com.test.Reason",
                                  "_reasonInputXItem");
        DataOutput dataOutput = getDataOutput(userTask,
                                              "performance");
        validateDataInputOrOutput(dataOutput,
                                  "_performanceOutputX",
                                  "Object",
                                  "_performanceOutputXItem");

        ItemAwareElement sourceRef = getDataInputAssociationSourceRef(userTask,
                                                                      "reason");
        assertNotNull(sourceRef);

        ItemAwareElement targetRef = getDataInputAssociationTargetRef(userTask,
                                                                      "_reasonInputX");
        assertNotNull(targetRef);

        sourceRef = getDataOutputAssociationSourceRef(userTask,
                                                      "_performanceOutputX");
        assertNotNull(sourceRef);

        targetRef = getDataOutputAssociationTargetRef(userTask,
                                                      "performance");
        assertNotNull(targetRef);
    }

    @Test
    public void testMarshallUserTaskMI() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASK_MI);
        String result = tested.marshall(diagram);
        assertDiagram(result, 1, 3, 2);
        assertTrue(result.contains("<bpmn2:itemDefinition id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theInputVariable\" structureRef=\"java.lang.Object\"/>"));
        assertTrue(result.contains("<bpmn2:itemDefinition id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theOutputVariable\" structureRef=\"java.lang.Object\"/>"));

        assertTrue(result.contains("<bpmn2:dataInput id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_IN_COLLECTIONInputX\" itemSubjectRef=\"_theInputCollectionItem\" name=\"IN_COLLECTION\"/>"));
        assertTrue(result.contains("<bpmn2:dataInput id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_theInputVariableInputX\" itemSubjectRef=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theInputVariable\" name=\"theInputVariable\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_OUT_COLLECTIONOutputX\" itemSubjectRef=\"_theOutputCollectionItem\" name=\"OUT_COLLECTION\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_theOutputVariableOutputX\" itemSubjectRef=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theOutputVariable\" name=\"theOutputVariable\"/>"));

        assertTrue(result.contains("<bpmn2:dataInputRefs>_CACC5C21-CE79-4445-9411-BE8C7A75E860_IN_COLLECTIONInputX</bpmn2:dataInputRefs>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_CACC5C21-CE79-4445-9411-BE8C7A75E860_theInputVariableInputX</bpmn2:dataInputRefs>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_CACC5C21-CE79-4445-9411-BE8C7A75E860_OUT_COLLECTIONOutputX</bpmn2:dataOutputRefs>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_CACC5C21-CE79-4445-9411-BE8C7A75E860_theOutputVariableOutputX</bpmn2:dataOutputRefs>"));

        assertTrue(result.contains("<bpmn2:sourceRef>theInputCollection</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_IN_COLLECTIONInputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:sourceRef>theInputVariable</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_theInputVariableInputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:sourceRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_OUT_COLLECTIONOutputX</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>theOutputCollection</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:sourceRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_theOutputVariableOutputX</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>theOutputVariable</bpmn2:targetRef>"));

        assertTrue(result.contains("<bpmn2:multiInstanceLoopCharacteristics"));
        assertTrue(result.contains("<bpmn2:loopDataInputRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_IN_COLLECTIONInputX</bpmn2:loopDataInputRef>"));
        assertTrue(result.contains("<bpmn2:loopDataOutputRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_OUT_COLLECTIONOutputX</bpmn2:loopDataOutputRef>"));
        assertTrue(result.contains("<bpmn2:inputDataItem xsi:type=\"bpmn2:tDataInput\" id=\"theInputVariable\" itemSubjectRef=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theInputVariable\" name=\"theInputVariable\"/>"));
        assertTrue(result.contains("<bpmn2:outputDataItem xsi:type=\"bpmn2:tDataOutput\" id=\"theOutputVariable\" itemSubjectRef=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theOutputVariable\" name=\"theOutputVariable\"/>"));
        assertTrue(result.contains("<bpmn2:completionCondition xsi:type=\"bpmn2:tFormalExpression\" id=\""));
        assertTrue(result.contains("<![CDATA[theCompletionCondition]]></bpmn2:completionCondition>"));
        assertTrue(result.contains("</bpmn2:multiInstanceLoopCharacteristics>"));
    }

    @Test
    public void testMarshallBusinessRuleTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKASSIGNMENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);
        assertTrue(result.contains("<bpmn2:dataInput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_input1InputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_input1InputXItem\" name=\"input1\"/>"));
        assertTrue(result.contains("<bpmn2:dataInput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_input2InputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_input2InputXItem\" name=\"input2\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_output1OutputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_output1OutputXItem\" name=\"output1\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_output2OutputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_output2OutputXItem\" name=\"output2\"/>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_input1InputX</bpmn2:dataInputRefs>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_input2InputX</bpmn2:dataInputRefs>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_output1OutputX</bpmn2:dataOutputRefs>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_output2OutputX</bpmn2:dataOutputRefs>"));
    }

    @Test
    public void testMarshallStartNoneEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTNONEEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);

        assertTrue(result.contains("<bpmn2:startEvent"));
        assertTrue(result.contains("name=\"MyStartNoneEvent\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[MyStartNoneEvent]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[MyStartNoneEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("</bpmn2:startEvent>"));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallStartTimerEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTTIMEREVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);
        assertTrue(result.contains("name=\"StartTimer\" isInterrupting=\"false\">"));
        assertTrue(result.contains("name=\"StartTimer\" isInterrupting=\"false\">"));
        assertTrue(result.contains("P4H</bpmn2:timeDuration>"));
        assertTrue(result.contains("language=\"cron\">*/2 * * * *</bpmn2:timeCycle>"));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallStartSignalEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTSIGNALEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);

        assertTrue(result.contains("<bpmn2:startEvent"));
        assertTrue(result.contains(" name=\"StartSignalEvent1\""));
        assertTrue(result.contains("<bpmn2:signal id=\"_47718ea6-a6a4-3ceb-9e93-2111bdad0b8c\" name=\"sig1\"/>"));
        assertTrue(result.contains("<bpmn2:signalEventDefinition"));
        assertTrue(result.contains("signalRef=\"_47718ea6-a6a4-3ceb-9e93-2111bdad0b8c\"/>"));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallStartConditionalEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTCONDITIONALEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:startEvent id=\"_8F9C10C4-F1EE-4B49-B4CE-3059ADD4B391\""));
        assertTrue(result.contains(" name=\"StartConditionalEvent\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[StartConditionalEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[StartConditionalEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:conditionalEventDefinition"));
        assertTrue(result.contains("<bpmn2:condition"));
        assertTrue(result.contains("![CDATA[StartConditionalEventConditionExpression]]></bpmn2:condition>"));
        assertTrue(result.contains("</bpmn2:condition>"));
        assertTrue(result.contains("</bpmn2:conditionalEventDefinition>"));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallStartEscalationEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTESCALATIONEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:startEvent id=\"_D5AAA79F-9CD6-43C1-92E2-5D3C9340A303\""));
        assertTrue(result.contains(" name=\"StartEscalationEvent\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[StartEscalationEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[StartEscalationEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:escalationEventDefinition"));
        assertTrue(result.contains("drools:esccode=\"EscalationCode\""));
        assertTrue(result.contains("<bpmn2:escalation"));
        assertTrue(result.contains("escalationCode=\"EscalationCode\""));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallStartCompensationEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTCOMPENSATIONEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:startEvent id=\"_19C23644-6CF0-4508-81B2-4CA2179137AB\""));
        assertTrue(result.contains(" name=\"StartCompensationEvent\""));
        assertTrue(result.contains("isInterrupting=\"false\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[StartCompensationEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[StartCompensationEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:compensateEventDefinition"));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallStartErrorEventEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTERROREVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      2,
                      1);

        assertTrue(result.contains("<bpmn2:startEvent"));
        assertTrue(result.contains(" name=\"MyStartErrorEvent\""));
        assertTrue(result.contains("<bpmn2:errorEventDefinition"));
        assertTrue(result.contains("errorRef=\"MyError\""));
        assertTrue(result.contains("drools:erefname=\"MyError\""));
        assertTrue(result.contains("<bpmn2:error"));
        assertTrue(result.contains("id=\"MyError\""));
        assertTrue(result.contains("errorCode=\"MyError\""));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallEndSignalEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDSIGNALEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);
        assertTrue(result.contains("<bpmn2:endEvent id=\"_C9151E0C-2E3E-4558-AFC2-34038E3A8552\""));
        assertTrue(result.contains(" name=\"EndSignalEvent\""));
        assertTrue(result.contains("<bpmn2:signalEventDefinition"));
        assertTrue(result.contains("<bpmn2:signal id=\"_fa547353-0e4d-3a5a-9e1e-b53d2fedb10c\" name=\"employee\"/>"));
        assertTrue(result.contains("<bpmndi:BPMNDiagram"));
        assertTrue(result.contains("<bpmn2:relationship"));
        assertTrue(result.contains("<bpmn2:extensionElements"));
    }

    @Test
    public void testMarshallStartMessageEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTMESSAGEEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);
        assertTrue(result.contains("<bpmn2:startEvent id=\"_34C4BBFC-544F-4E23-B17B-547BB48EEB63\""));
        assertTrue(result.contains(" name=\"StartMessageEvent\""));
        assertTrue(result.contains("<bpmn2:message "));
        assertTrue(result.contains(" name=\"msgref\""));
        assertTrue(result.contains("<bpmn2:messageEventDefinition"));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallEndMessageEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDMESSAGEEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);
        assertTrue(result.contains("<bpmn2:endEvent id=\"_4A8A0A9E-D4A5-4B6E-94A6-20817A57B3C6\""));
        assertTrue(result.contains(" name=\"EndMessageEvent\""));
        assertTrue(result.contains("<bpmn2:message "));
        assertTrue(result.contains(" name=\"msgref\""));
        assertTrue(result.contains("<bpmn2:messageEventDefinition"));
    }

    @Test
    public void testMarshallTimerIntermediateEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_TIMER_EVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:intermediateCatchEvent"));
        assertTrue(result.contains(" name=\"MyTimer\""));
        assertTrue(result.contains("<bpmn2:timerEventDefinition"));
        assertTrue(result.contains("<bpmn2:timeDate"));
        assertTrue(result.contains("<bpmn2:timeDuration"));
        assertTrue(result.contains("<bpmn2:timeCycle"));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallIntermediateSignalEventCatching() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_SIGNAL_EVENTCATCHING);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:intermediateCatchEvent"));
        assertTrue(result.contains(" name=\"MySignalCatchingEvent\""));
        assertTrue(result.contains("<bpmn2:signalEventDefinition"));
        assertTrue(result.contains(" signalRef=\"_3b677877-9be0-3fe7-bfc4-94a862fdc919\""));
        assertTrue(result.contains("<bpmn2:signal"));
        assertTrue(result.contains("name=\"MySignal\""));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallIntermediatErrorEventCatching() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_ERROR_EVENTCATCHING);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:intermediateCatchEvent"));
        assertTrue(result.contains(" name=\"MyErrorCatchingEvent\""));
        assertTrue(result.contains("<bpmn2:errorEventDefinition"));
        assertTrue(result.contains("errorRef=\"MyError\""));
        assertTrue(result.contains("<bpmn2:error"));
        assertTrue(result.contains("id=\"MyError\""));
        assertTrue(result.contains("errorCode=\"MyError\""));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallIntermediateMessageEventThrowing() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_MESSAGE_EVENTTHROWING);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:intermediateThrowEvent"));
        assertTrue(result.contains(" name=\"IntermediateMessageEventThrowing\""));
        assertTrue(result.contains("<bpmn2:message "));
        assertTrue(result.contains(" name=\"msgref\""));
        assertTrue(result.contains("<bpmn2:messageEventDefinition"));
    }

    @Test
    public void testMarshallIntermediateSignalEventThrowing() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_SIGNAL_EVENTTHROWING);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:intermediateThrowEvent"));
        assertTrue(result.contains(" name=\"MySignalThrowingEvent\""));
        assertTrue(result.contains("<bpmn2:signalEventDefinition"));
        assertTrue(result.contains(" signalRef=\"_3b677877-9be0-3fe7-bfc4-94a862fdc919\""));
        assertTrue(result.contains("<bpmn2:signal"));
        assertTrue(result.contains("name=\"MySignal\""));
        assertTrue(result.contains("<drools:metaData name=\"customScope\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[processInstance]]></drools:metaValue>"));
    }

    @Test
    public void testMarshallIntermediateEscalationEventThrowing() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_ESCALATION_EVENTTHROWING);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:intermediateThrowEvent id=\"_8516D854-F67F-4697-9837-40A32033AE25\""));
        assertTrue(result.contains(" name=\"ThrowingEscalationEventName\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[ThrowingEscalationEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[ThrowingEscalationEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:escalationEventDefinition"));
        assertTrue(result.contains("drools:esccode=\"EscalationCode\""));
        assertTrue(result.contains("<bpmn2:escalation"));
        assertTrue(result.contains("escalationCode=\"EscalationCode\""));
    }

    @Test
    public void testMarshallIntermediateCompensationEventThrowing() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_COMPENSATION_EVENTTHROWING);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      2,
                      0);

        assertTrue(result.contains("<bpmn2:intermediateThrowEvent id=\"_F1D87D25-4D73-4DC5-A0C2-C627CED773BA\""));
        assertTrue(result.contains(" name=\"IntermediateCompensationEventThrowing\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[ThrowingCompensationEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[ThrowingCompensationEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:compensateEventDefinition"));
        assertTrue(result.contains("activityRef=\"_E318295E-B0B6-4FB2-B5EB-A43BFD44FCBD\""));
    }

    @Test
    public void testMarshallIntermediateMessageEventCatching() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_MESSAGE_EVENTCATCHING);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:intermediateCatchEvent"));
        assertTrue(result.contains(" name=\"IntermediateMessageEventCatching\""));
        assertTrue(result.contains("<bpmn2:message "));
        assertTrue(result.contains(" name=\"msgref1\""));
        assertTrue(result.contains("<bpmn2:messageEventDefinition"));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallEndNoneEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDNONEEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      2,
                      1);

        assertTrue(result.contains("<bpmn2:endEvent"));
        assertTrue(result.contains(" id=\"_9DF2C9D3-15DF-4436-B6C6-85B58B8696B6\""));
        assertTrue(result.contains("name=\"MyEndNoneEvent\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[MyEndNoneEvent]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[MyEndNoneEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("</bpmn2:endEvent>"));
    }

    @Test
    public void testMarshallEndTerminateEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDTERMINATEEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      2,
                      1);

        assertTrue(result.contains("<bpmn2:endEvent"));
        assertTrue(result.contains(" id=\"_1B379E3E-E4ED-4BD2-AEE8-CD85374CEC78\""));
        assertTrue(result.contains("name=\"MyEndTerminateEvent\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[MyEndTerminateEvent]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[MyEndTerminateEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:terminateEventDefinition"));
        assertTrue(result.contains("</bpmn2:endEvent>"));
    }

    @Test
    public void testMarshallEndErrorEnd() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDERROR_EVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);
        assertTrue(result.contains("<bpmn2:error id=\"MyError\" errorCode=\"MyError\"/>"));
        assertTrue(result.contains("<bpmn2:endEvent"));
        assertTrue(result.contains(" name=\"MyErrorEventName\""));
        assertTrue(result.contains("<bpmn2:errorEventDefinition"));
        assertTrue(result.contains(" errorRef=\"MyError\""));
        assertTrue(result.contains(" drools:erefname=\"MyError\""));
    }

    @Test
    public void testMarshallEndEscalationEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDESCALATIONEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:endEvent id=\"_8F6A4096-26AA-4C14-B1F0-B96ED24BD5C7\""));
        assertTrue(result.contains(" name=\"EndEscalationEventName\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[EndEscalationEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[EndEscalationEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:escalationEventDefinition"));
        assertTrue(result.contains("drools:esccode=\"EscalationCode\""));
        assertTrue(result.contains("<bpmn2:escalation"));
        assertTrue(result.contains("escalationCode=\"EscalationCode\""));
    }

    @Test
    public void testMarshallEndCompensationEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDCOMPENSATIONEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      2,
                      0);

        assertTrue(result.contains("<bpmn2:endEvent id=\"_2AEA503B-26E5-4F11-A54D-3E275F8BFA6A\""));
        assertTrue(result.contains(" name=\"EndCompensationEventName\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[EndCompensationEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[EndCompensationEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:compensateEventDefinition"));
        assertTrue(result.contains("activityRef=\"_7795AFA7-E602-4D90-B4B6-249639C4D965\""));
    }

    @Test
    public void testMarshallReusableSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_REUSABLE_SUBPROCESS);
        assertDiagram(diagram,
                      4);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);

        assertTrue(result.contains("<bpmn2:callActivity id=\"_FC6D8570-8C67-40C2-8B7B-953DE15765FB\" drools:independent=\"false\" drools:waitForCompletion=\"true\" name=\"my subprocess\" calledElement=\"my-called-element&quot; &lt;&amp;&gt; &quot;\">"));

        assertTrue(result.contains("<bpmn2:dataInput id=\"_FC6D8570-8C67-40C2-8B7B-953DE15765FB_input1InputX\" drools:dtype=\"String\" itemSubjectRef=\"__FC6D8570-8C67-40C2-8B7B-953DE15765FB_input1InputXItem\" name=\"input1\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_FC6D8570-8C67-40C2-8B7B-953DE15765FB_output2OutputX\" drools:dtype=\"Float\" itemSubjectRef=\"__FC6D8570-8C67-40C2-8B7B-953DE15765FB_output2OutputXItem\" name=\"output2\"/>"));
        assertTrue(result.contains("<bpmn2:sourceRef>pv1</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_FC6D8570-8C67-40C2-8B7B-953DE15765FB_input1InputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:sourceRef>_FC6D8570-8C67-40C2-8B7B-953DE15765FB_output2OutputX</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>pv2</bpmn2:targetRef>"));

        String flatResult = result.replace(NEW_LINE,
                                           " ").replaceAll("( )+",
                                                           " ");
        assertTrue(flatResult.contains("<drools:metaData name=\"elementname\"> <drools:metaValue><![CDATA[my subprocess]]></drools:metaValue> </drools:metaData>"));
        assertTrue(flatResult.contains("<drools:metaData name=\"customAsync\"> <drools:metaValue><![CDATA[true]]></drools:metaValue>"));
    }

    @Test
    public void testMarshallReusableSubprocessMI() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_REUSABLE_SUBPROCESS_MI);
        String result = tested.marshall(diagram);
        assertDiagram(result, 1, 3, 2);
        assertTrue(result.contains("<bpmn2:callActivity id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860\""));
        assertTrue(result.contains("name=\"TheReusableSubProcess\" calledElement=\"test.SubProcess\">"));

        assertTrue(result.contains("<bpmn2:itemDefinition id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theInputVariable\" structureRef=\"java.lang.Object\"/>"));
        assertTrue(result.contains("<bpmn2:itemDefinition id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theOutputVariable\" structureRef=\"java.lang.Object\"/>"));

        assertTrue(result.contains("<bpmn2:dataInput id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_IN_COLLECTIONInputX\" itemSubjectRef=\"_theInputCollectionItem\" name=\"IN_COLLECTION\"/>"));
        assertTrue(result.contains("<bpmn2:dataInput id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_theInputVariableInputX\" itemSubjectRef=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theInputVariable\" name=\"theInputVariable\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_OUT_COLLECTIONOutputX\" itemSubjectRef=\"_theOutputCollectionItem\" name=\"OUT_COLLECTION\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_theOutputVariableOutputX\" itemSubjectRef=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theOutputVariable\" name=\"theOutputVariable\"/>"));

        assertTrue(result.contains("<bpmn2:dataInputRefs>_CACC5C21-CE79-4445-9411-BE8C7A75E860_IN_COLLECTIONInputX</bpmn2:dataInputRefs>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_CACC5C21-CE79-4445-9411-BE8C7A75E860_theInputVariableInputX</bpmn2:dataInputRefs>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_CACC5C21-CE79-4445-9411-BE8C7A75E860_OUT_COLLECTIONOutputX</bpmn2:dataOutputRefs>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_CACC5C21-CE79-4445-9411-BE8C7A75E860_theOutputVariableOutputX</bpmn2:dataOutputRefs>"));

        assertTrue(result.contains("<bpmn2:sourceRef>theInputCollection</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_IN_COLLECTIONInputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:sourceRef>theInputVariable</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_theInputVariableInputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:sourceRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_OUT_COLLECTIONOutputX</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>theOutputCollection</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:sourceRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_theOutputVariableOutputX</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>theOutputVariable</bpmn2:targetRef>"));

        assertTrue(result.contains("<bpmn2:multiInstanceLoopCharacteristics"));
        assertTrue(result.contains("<bpmn2:loopDataInputRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_IN_COLLECTIONInputX</bpmn2:loopDataInputRef>"));
        assertTrue(result.contains("<bpmn2:loopDataOutputRef>_CACC5C21-CE79-4445-9411-BE8C7A75E860_OUT_COLLECTIONOutputX</bpmn2:loopDataOutputRef>"));
        assertTrue(result.contains("<bpmn2:inputDataItem xsi:type=\"bpmn2:tDataInput\" id=\"theInputVariable\" itemSubjectRef=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theInputVariable\" name=\"theInputVariable\"/>"));
        assertTrue(result.contains("<bpmn2:outputDataItem xsi:type=\"bpmn2:tDataOutput\" id=\"theOutputVariable\" itemSubjectRef=\"_CACC5C21-CE79-4445-9411-BE8C7A75E860_multiInstanceItemType_theOutputVariable\" name=\"theOutputVariable\"/>"));
        assertTrue(result.contains("<bpmn2:completionCondition xsi:type=\"bpmn2:tFormalExpression\" id=\""));
        assertTrue(result.contains("<![CDATA[theCompletionCondition]]></bpmn2:completionCondition>"));
        assertTrue(result.contains("</bpmn2:multiInstanceLoopCharacteristics>"));
    }

    @Test
    public void testMarshallEmbeddedSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EMBEDDED_SUBPROCESS);
        assertDiagram(diagram,
                      10);
        assertDocumentation(diagram,
                            "_C3EBE7F1-8E57-4BB1-B380-40BB02E9464E",
                            "Subprocess  Documentation Value");

        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      9,
                      7);

        final String SLA_DUE_DATE = "12/25/1983";

        assertTrue(result.contains("<bpmn2:subProcess id=\"_C3EBE7F1-8E57-4BB1-B380-40BB02E9464E\" "));
        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[" + SLA_DUE_DATE + "]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallEmbeddedSubprocessDuplicateElements() throws Exception {
        String f = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/subprocessDuplicateElements.bpmn";
        Diagram<Graph, Metadata> diagram = unmarshall(f);
        String result = tested.marshall(diagram);

        assertFalse(
                "should not contain the same node twice",
                result.replaceFirst("id=\"5FAC4991-B287-4787-9916-9D3D5D215977\"", "BLAH")
                        .contains("id=\"5FAC4991-B287-4787-9916-9D3D5D215977\""));
    }

    @Test
    public void testMarshallEmbeddedSubprocessNestedDuplicates() throws Exception {
        String f = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/subprocessNested.bpmn";
        Diagram<Graph, Metadata> diagram = unmarshall(f);

        String result = tested.marshall(diagram);

        assertFalse(
                "should not contain the same node twice",
                result.replaceFirst("id=\"_CANDIDATE_\"", "BLAH")
                        .contains("id=\"_CANDIDATE_\""));
    }

    @Test
    public void testMarshallMultipleInstanceSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_MULTIPLE_INSTANCE_SUBPROCESS);
        assertDiagram(diagram,
                      2);

        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertEquals("Unexpected number of extensionElements sections.",
                     2, countOccurrences(result, "<bpmn2:extensionElements>"));
        assertTrue(result.contains("<bpmn2:subProcess id=\"_2316CEC1-C1F7-41B1-8C91-3CE73ADE5571\""));
        assertTrue(result.contains("name=\"MultipleInstanceSubprocess\""));
        assertTrue(result.contains("<drools:onEntry-script scriptFormat=\"http://www.java.com/java\">"));
        assertTrue(result.contains("<drools:script><![CDATA[onEntryAction]]></drools:script>"));
        assertTrue(result.contains("</drools:onEntry-script>"));
        assertTrue(result.contains("<drools:onExit-script scriptFormat=\"http://www.java.com/java\">"));
        assertTrue(result.contains("<drools:script><![CDATA[onExitAction]]></drools:script>"));
        assertTrue(result.contains("</drools:onExit-script>"));
        assertTrue(result.contains("<drools:metaData name=\"customAsync\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[true]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_2316CEC1-C1F7-41B1-8C91-3CE73ADE5571_OUT_COLLECTIONOutputX\" itemSubjectRef=\"_var2Item\" name=\"OUT_COLLECTION\"/>"));
        assertTrue(result.contains("<bpmn2:multiInstanceLoopCharacteristics"));
        assertTrue(result.contains("<bpmn2:loopDataInputRef>_2316CEC1-C1F7-41B1-8C91-3CE73ADE5571_IN_COLLECTIONInputX</bpmn2:loopDataInputRef>"));
        assertTrue(result.contains("<bpmn2:loopDataOutputRef>_2316CEC1-C1F7-41B1-8C91-3CE73ADE5571_OUT_COLLECTIONOutputX</bpmn2:loopDataOutputRef>"));
        assertTrue(result.contains("<bpmn2:inputDataItem xsi:type=\"bpmn2:tDataInput\" id=\"dataInput\" itemSubjectRef=\"_2316CEC1-C1F7-41B1-8C91-3CE73ADE5571_multiInstanceItemType_dataInput\" name=\"dataInput\"/>"));
        assertTrue(result.contains("<bpmn2:outputDataItem xsi:type=\"bpmn2:tDataOutput\" id=\"dataOutput\" itemSubjectRef=\"_2316CEC1-C1F7-41B1-8C91-3CE73ADE5571_multiInstanceItemType_dataOutput\" name=\"dataOutput\"/>"));
        assertTrue(result.contains("<bpmn2:completionCondition xsi:type=\"bpmn2:tFormalExpression\""));
        assertTrue(result.contains("<![CDATA[a=b]]></bpmn2:completionCondition>"));
        assertTrue(result.contains("</bpmn2:multiInstanceLoopCharacteristics>"));
        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    private int countOccurrences(String input, String lookup) {
        Pattern p = Pattern.compile(lookup);
        Matcher matcher = p.matcher(input);
        int count = 0;
        while (matcher.find()) {
            count += 1;
        }
        return count;
    }

    @Test
    public void testMarshallEventSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVENT_SUBPROCESS);
        assertDiagram(diagram,
                      2);

        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);
        final String SLA_DUE_DATE = "12/25/1983";

        assertTrue(result.contains("<bpmn2:subProcess id=\"_DF031493-5F1C-4D2B-9916-2FEABB1FADFF\""));
        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[" + SLA_DUE_DATE + "]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallAdHocSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ADHOC_SUBPROCESS);
        assertDiagram(diagram,
                      9);
        String result = tested.marshall(diagram);

        assertDiagram(result,
                      1,
                      8,
                      5);

        assertTrue(result.contains("<bpmn2:adHocSubProcess id=\"_B65DDF51-9822-4B12-8669-2018A845A01B\""));
        assertTrue(result.contains("name=\"AdHocSubprocess1\""));

        assertTrue(result.contains("<drools:onEntry-script scriptFormat=\"http://www.mvel.org/2.0\">"));
        assertTrue(result.contains("<drools:script><![CDATA[System.out.println(\"onEntryAction\");]]></drools:script>"));
        assertTrue(result.contains("</drools:onEntry-script>"));

        assertTrue(result.contains("<drools:onExit-script scriptFormat=\"http://www.java.com/java\">"));
        assertTrue(result.contains("<drools:script><![CDATA[System.out.println(\"onExitAction\");]]></drools:script>"));
        assertTrue(result.contains("</drools:onExit-script>"));

        assertTrue(result.contains("<bpmn2:completionCondition xsi:type=\"bpmn2:tFormalExpression\""));
        assertTrue(result.contains("language=\"http://www.jboss.org/drools/rule\"><![CDATA[autocomplete]]></bpmn2:completionCondition>"));

        assertTrue(result.contains("<drools:metaData name=\"customAsync\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[true]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));

        final String SLA_DUE_DATE = "12/25/1983";
        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[" + SLA_DUE_DATE + "]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallUserTaskAssignees() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNEES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      5,
                      4);
        assertTrue(result.contains("<![CDATA[admin,kiemgmt]]>"));
        result = result.replace(NEW_LINE,
                                " ");
        assertTrue(result.matches("(.*)<bpmn2:resourceAssignmentExpression(.*)>user</bpmn2:formalExpression>(.*)"));
        assertTrue(result.matches("(.*)<bpmn2:resourceAssignmentExpression(.*)>user1</bpmn2:formalExpression>(.*)"));
    }

    @Test
    public void testMarshallUserTaskProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKPROPERTIES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);
        assertTrue(result.contains("MyUserTask]]></bpmn2:from>"));
        String flatResult = result.replace(NEW_LINE,
                                           " ").replaceAll("( )+",
                                                           " ");
        assertTrue(flatResult.contains("<drools:metaData name=\"customAsync\"> <drools:metaValue><![CDATA[true]]></drools:metaValue>"));
        assertTrue(flatResult.contains("<drools:metaData name=\"customAutoStart\"> <drools:metaValue><![CDATA[true]]></drools:metaValue>"));

        assertTrue(flatResult.contains("<drools:onEntry-script scriptFormat=\"http://www.java.com/java\">"));
        assertTrue(flatResult.contains("<drools:script><![CDATA[System.out.println(\"Hello\");]]></drools:script>"));
        assertTrue(flatResult.contains("<drools:script><![CDATA[System.out.println(\"Bye\");]]></drools:script>"));
    }

    @Test
    public void testMarshallSimulationProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SIMULATIONPROPERTIES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);

        result = result.replaceAll("\\s+",
                                   " ");
        result = result.replaceAll("> <",
                                   "><");
        assertTrue(result.contains("<bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\"><bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\"><bpsim:PoissonDistribution mean=\"321.0\"/>"));
        assertTrue(result.contains("<bpsim:ResourceParameters xsi:type=\"bpsim:ResourceParameters\"><bpsim:Availability xsi:type=\"bpsim:Parameter\"><bpsim:FloatingParameter value=\"999.0\"/>"));
        assertTrue(result.contains("<bpsim:Quantity xsi:type=\"bpsim:Parameter\"><bpsim:FloatingParameter value=\"111.0\"/></bpsim:Quantity>"));
        assertTrue(result.contains("<bpsim:CostParameters xsi:type=\"bpsim:CostParameters\"><bpsim:UnitCost xsi:type=\"bpsim:Parameter\"><bpsim:FloatingParameter value=\"123.0\"/>"));
        assertTrue(result.contains("<bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\"><bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\"><bpsim:UniformDistribution max=\"10.0\" min=\"5.0\"/>"));
    }

    @Test
    public void testMarshallEvaluationTwice() throws Exception {
        Diagram diagram = unmarshall(BPMN_EVALUATION);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      7,
                      7);
        Diagram diagram2 = unmarshall(BPMN_EVALUATION);
        String result2 = tested.marshall(diagram2);
        assertDiagram(result2,
                      1,
                      7,
                      7);
    }

    @Test
    public void testMarshallScriptTask() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SCRIPTTASK);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      4,
                      3);
        assertTrue(result.contains("name=\"Javascript Script Task\" scriptFormat=\"http://www.javascript.com/javascript\""));
        assertTrue(result.contains("name=\"Java Script Task\" scriptFormat=\"http://www.java.com/java\""));

        assertTrue(result.contains("<bpmn2:script><![CDATA[var str = FirstName + LastName;]]></bpmn2:script>"));
        assertTrue(result.contains("<bpmn2:script><![CDATA[if (name.toString().equals(\"Jay\")) {" + NEW_LINE +
                                           NEW_LINE +
                                           "      System.out.println(\"Hello\\n\" + name.toString() + \"\\n\");" + NEW_LINE +
                                           NEW_LINE +
                                           "} else {" + NEW_LINE +
                                           NEW_LINE +
                                           NEW_LINE +
                                           "  System.out.println(\"Hi\\n\" + name.toString() + \"\\n\");" + NEW_LINE +
                                           NEW_LINE +
                                           NEW_LINE +
                                           "}" + NEW_LINE +
                                           "]]></bpmn2:script>"));

        String flatResult = result.replace(NEW_LINE,
                                           " ").replaceAll("( )+",
                                                           " ");
        assertTrue(flatResult.contains("<drools:metaData name=\"customAsync\"> <drools:metaValue><![CDATA[true]]></drools:metaValue>"));
        assertTrue(flatResult.contains("<drools:metaData name=\"customAutoStart\"> <drools:metaValue><![CDATA[true]]></drools:metaValue>"));
    }

    @Test
    public void testMarshallSequenceFlow() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SEQUENCEFLOW);
        assertConditionLanguage(diagram,
                                "_C9F8F30D-E772-4504-A480-6EC894B289DC",
                                "javascript");
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      6,
                      5);
        assertTrue(result.contains("language=\"http://www.javascript.com/javascript\"><![CDATA[age >= 10;]]></bpmn2:conditionExpression>"));
        assertTrue(result.contains("language=\"http://www.java.com/java\"><![CDATA[age" + NEW_LINE +
                                           "<" + NEW_LINE +
                                           "10;]]></bpmn2:conditionExpression>"));
    }

    private void assertConditionLanguage(Diagram<Graph, Metadata> diagram,
                                         String id,
                                         String value) {
        List<Node> nodes = getNodes(diagram);
        Optional<SequenceFlow> sequenceFlow =
                Stream.concat(nodes.stream().flatMap(node -> {
                                  List<Edge> d = node.getInEdges();
                                  return d.stream();
                              }),
                              nodes.stream().flatMap(node -> {
                                  List<Edge> d = node.getOutEdges();
                                  return d.stream();
                              }))
                        .filter(edge -> edge.getUUID().equals(id))
                        .map(node -> (View) node.getContent())
                        .filter(view -> view.getDefinition() instanceof SequenceFlow)
                        .map(view -> ((SequenceFlow) view.getDefinition()))
                        .findFirst();

        String conditionLanguage = (sequenceFlow.isPresent() ? sequenceFlow.get().getExecutionSet().getConditionExpression().getValue().getLanguage() : null);
        assertEquals(value,
                     conditionLanguage);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMarshallBusinessRuleTask() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKRULEFLOWGROUP);
        String result = tested.marshall(diagram);
        assertDiagram(diagram,
                      2);

        assertTrue(result.contains("<bpmn2:businessRuleTask "));
        String flatResult = result.replace(NEW_LINE,
                                           " ").replaceAll("( )+",
                                                           " ");
        assertTrue(flatResult.contains("<drools:metaData name=\"customAsync\"> <drools:metaValue><![CDATA[true]]></drools:metaValue>"));

        assertTrue(flatResult.contains("<drools:onEntry-script scriptFormat=\"http://www.java.com/java\">"));

        assertTrue(flatResult.contains("<drools:script><![CDATA[System.out.println(\"Hello\");]]></drools:script>"));

        assertTrue(flatResult.contains("<drools:script><![CDATA[System.out.println(\"Bye\");]]></drools:script>"));
    }

    @Test
    public void testMarshallInclusiveGateway() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INCLUSIVE_GATEWAY);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      6,
                      6);
        assertTrue(result.contains("<bpmn2:inclusiveGateway id=\"_526EE472-FE8B-4E9A-A951-CFBA86C3691F\" drools:dg=\"_3D5701E9-CFD3-4218-9200-897B6D4FF041\" name=\"InclusiveGatewayName\" gatewayDirection=\"Diverging\" default=\"_3D5701E9-CFD3-4218-9200-897B6D4FF041\">"));
    }

    @Test
    public void testMarshallXorGateway() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_XORGATEWAY);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      6,
                      5);
        assertTrue(result.contains("<bpmn2:exclusiveGateway id=\"_877EA035-1A14-42E9-8CAA-43E9BF908C70\" drools:dg=\"_5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD\" name=\"AgeSplit\" gatewayDirection=\"Diverging\" default=\"_5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD\">"));
    }

    @Test
    public void testMarshallIntermediateTimerEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_TIMER_EVENT);
        IntermediateTimerEvent timerEvent = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof IntermediateTimerEvent) {
                    timerEvent = (IntermediateTimerEvent) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(timerEvent);
        assertNotNull(timerEvent.getGeneral());
        assertNotNull(timerEvent.getExecutionSet());

        assertEquals("myTimeDateValue",
                     timerEvent.getExecutionSet().getTimerSettings().getValue().getTimeDate());
        assertEquals("MyTimeDurationValue",
                     timerEvent.getExecutionSet().getTimerSettings().getValue().getTimeDuration());
        assertEquals("myTimeCycleValue",
                     timerEvent.getExecutionSet().getTimerSettings().getValue().getTimeCycle());
        assertEquals("cron",
                     timerEvent.getExecutionSet().getTimerSettings().getValue().getTimeCycleLanguage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMarshallIntermediateConditionalEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_CONDITIONAL_EVENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      0);

        assertTrue(result.contains("<bpmn2:intermediateCatchEvent id=\"_31A24997-C7B3-4286-8B4D-0EFD7CB11206\""));
        assertTrue(result.contains(" name=\"CatchingIntermediateConditionalEventName\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[CatchingIntermediateConditionalEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[CatchingIntermediateConditionalDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:conditionalEventDefinition"));
        assertTrue(result.contains("<bpmn2:condition"));
        assertTrue(result.contains("![CDATA[CatchingIntermediateConditionalEventCompletionCondition]]></bpmn2:condition>"));
        assertTrue(result.contains("</bpmn2:condition>"));
        assertTrue(result.contains("</bpmn2:conditionalEventDefinition>"));

        assertTrue(result.contains("<bpmn2:boundaryEvent id=\"_FD2DB546-4A85-4C50-9003-548A9A354F97\""));
        assertTrue(result.contains(" name=\"BoundaryConditionalEventName\""));
        assertTrue(result.contains("attachedToRef=\"_0EE77FB7-0610-496D-AF48-6ADECE39897A\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[BoundaryConditionalEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[BoundaryConditionalEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:conditionalEventDefinition"));
        assertTrue(result.contains("<bpmn2:condition"));
        assertTrue(result.contains("![CDATA[BoundaryConditionalEventCompletionCondition]]></bpmn2:condition>"));
        assertTrue(result.contains("</bpmn2:condition>"));
        assertTrue(result.contains("</bpmn2:conditionalEventDefinition>"));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallIntermediateEscalationEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_ESCALATION_EVENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      2,
                      0);

        assertTrue(result.contains("<bpmn2:boundaryEvent id=\"_C7F2BA98-1100-4CF8-A994-40E2B65F6E5D\""));
        assertTrue(result.contains(" name=\"BoundaryEscalationEventName\""));
        assertTrue(result.contains("attachedToRef=\"_3817E92F-D45A-4878-AAB2-95B057C485A1\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[BoundaryEscalationEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[BoundaryEscalationEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:escalationEventDefinition"));
        assertTrue(result.contains("drools:esccode=\"EscalationCode\""));
        assertTrue(result.contains("<bpmn2:escalation"));
        assertTrue(result.contains("escalationCode=\"EscalationCode\""));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallIntermediateCompensationEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_COMPENSATION_EVENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      2,
                      0);

        assertTrue(result.contains("<bpmn2:boundaryEvent id=\"_8CE8AB54-7E8B-4032-B340-2DBEBAB5080F\""));
        assertTrue(result.contains(" name=\"IntermediateCompensationEventName\""));
        assertTrue(result.contains("attachedToRef=\"_0D213C4C-5B7F-4319-A69A-9F840345F330\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[IntermediateCompensationEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[IntermediateCompensationEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:compensateEventDefinition"));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallIntermediateCompensationEventsWithAssociations() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_COMPENSATION_EVENTS_WITH_ASSOCIATION);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      5,
                      3);

        assertTrue(result.contains("<bpmn2:boundaryEvent id=\"_DF70C614-A641-4109-8A8D-506B15E3F31B\""));
        assertTrue(result.contains(" name=\"IntermediateCompensationEventName\""));
        assertTrue(result.contains("attachedToRef=\"_C18CC8D2-D7CA-457D-9258-01D1E6973A86\""));
        assertTrue(result.contains("<drools:metaValue><![CDATA[IntermediateCompensationEventName]]></drools:metaValue>"));
        assertTrue(result.contains("<![CDATA[IntermediateCompensationEventDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<bpmn2:compensateEventDefinition"));

        assertTrue(result.contains("<bpmn2:association id=\"_B41D28D1-FC39-40E8-BF89-C57649989014\""));
        assertTrue(result.contains("associationDirection=\"One\""));
        assertTrue(result.contains("sourceRef=\"_DF70C614-A641-4109-8A8D-506B15E3F31B\""));
        assertTrue(result.contains("targetRef=\"_7EF24042-BD4E-4843-9874-8AC3F7AFF3CD\""));
        assertTrue(result.contains("<![CDATA[AssociationDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("</bpmn2:association>"));

        assertTrue(result.contains("<drools:metaData name=\"customSLADueDate\">"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[12/25/1983]]></drools:metaValue>"));
        assertTrue(result.contains("</drools:metaData>"));
    }

    @Test
    public void testMarshallMagnetDockers() throws Exception {
        Diagram<Graph, Metadata> diagram1 = unmarshall(BPMN_MAGNETDOCKERS);
        String result = tested.marshall(diagram1);
        assertDiagram(result,
                      1,
                      8,
                      7);
        Diagram<Graph, Metadata> diagram2 = unmarshall(new ByteArrayInputStream(result.getBytes()));
        testMagnetDockers(diagram2);
    }

    @Test
    public void testMarshallMagnetsInlane() throws Exception {
        Diagram<Graph, Metadata> diagram1 = unmarshall(BPMN_MAGNETSINLANE);
        String result = tested.marshall(diagram1);
        assertDiagram(result,
                      1,
                      6,
                      4);

        // Check the waypoints are as in the original process
        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"371.0\" y=\"86.0\"/>"));
        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"406.0\" y=\"324.0\"/>"));

        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"692.0\" y=\"276.0\"/>"));
        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"805.0\" y=\"76.0\"/>"));

        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"81.0\" y=\"86.0\"/>"));
        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"235.0\" y=\"86.0\"/>"));

        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"474.0\" y=\"372.0\"/>"));
        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"556.0\" y=\"276.0\"/>"));

        // Test unmarshall
        Diagram<Graph, Metadata> diagram2 = unmarshall(new ByteArrayInputStream(result.getBytes()));
        testMagnetsInLane(diagram2);
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

        Node<? extends Definition, ?> embeddedSubprocessNode = diagram.getGraph().getNode("_C3EBE7F1-8E57-4BB1-B380-40BB02E9464E");
        EmbeddedSubprocess embeddedSubprocess = (EmbeddedSubprocess) embeddedSubprocessNode.getContent().getDefinition();

        final String SLA_DUE_DATE = "12/25/1983";
        assertEquals(SLA_DUE_DATE, embeddedSubprocess.getExecutionSet().getSlaDueDate().getValue());
    }

    @Test
    public void testUnmarshallMultipleInstanceSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_MULTIPLE_INSTANCE_SUBPROCESS);
        assertDiagram(diagram,
                      2);
        assertEquals("MultipleInstanceSubprocess",
                     diagram.getMetadata().getTitle());

        Node<? extends Definition, ?> multipleInstanceSubprocessNode = diagram.getGraph().getNode("_2316CEC1-C1F7-41B1-8C91-3CE73ADE5571");
        MultipleInstanceSubprocess multipleInstanceSubprocess = (MultipleInstanceSubprocess) multipleInstanceSubprocessNode.getContent().getDefinition();

        assertEquals("var1", multipleInstanceSubprocess.getExecutionSet().getMultipleInstanceCollectionInput().getValue());
        assertEquals("var2", multipleInstanceSubprocess.getExecutionSet().getMultipleInstanceCollectionOutput().getValue());
        assertEquals("dataInput:java.lang.Object", multipleInstanceSubprocess.getExecutionSet().getMultipleInstanceDataInput().getValue());
        assertEquals("dataOutput:java.lang.Object", multipleInstanceSubprocess.getExecutionSet().getMultipleInstanceDataOutput().getValue());
        assertEquals("a=b", multipleInstanceSubprocess.getExecutionSet().getMultipleInstanceCompletionCondition().getValue());
        assertEquals("onEntryAction",
                     multipleInstanceSubprocess.getExecutionSet().getOnEntryAction().getValue().getValues().get(0).getScript());
        assertEquals("java",
                     multipleInstanceSubprocess.getExecutionSet().getOnEntryAction().getValue().getValues().get(0).getLanguage());
        assertEquals("onExitAction",
                     multipleInstanceSubprocess.getExecutionSet().getOnExitAction().getValue().getValues().get(0).getScript());
        assertEquals("java",
                     multipleInstanceSubprocess.getExecutionSet().getOnExitAction().getValue().getValues().get(0).getLanguage());
        assertTrue(multipleInstanceSubprocess.getExecutionSet().getIsAsync().getValue());
        assertEquals("mi-var1:String:[]", multipleInstanceSubprocess.getProcessData().getProcessVariables().getValue());
        assertEquals(Boolean.TRUE, multipleInstanceSubprocess.getExecutionSet().getIsAsync().getValue());

        final String SLA_DUE_DATE = "12/25/1983";
        assertEquals(SLA_DUE_DATE, multipleInstanceSubprocess.getExecutionSet().getSlaDueDate().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEventSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVENT_SUBPROCESS);
        assertDiagram(diagram,
                      2);
        assertEquals("EventSubProcess",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> eventSubprocessNode = diagram.getGraph().getNode("_DF031493-5F1C-4D2B-9916-2FEABB1FADFF");
        EventSubprocess eventSubprocess = (EventSubprocess) eventSubprocessNode.getContent().getDefinition();
        assertTrue(eventSubprocess.getExecutionSet().getIsAsync().getValue());
        assertEquals(eventSubprocess.getProcessData().getProcessVariables().getValue(),
                     "Var1:String:[]");

        final String SLA_DUE_DATE = "12/25/1983";
        assertEquals(SLA_DUE_DATE, eventSubprocess.getExecutionSet().getSlaDueDate().getValue());
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

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallWorkItems() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SERVICE_TASKS);
        assertDiagram(diagram,
                      5);
        // Email service task assertions.
        Node<? extends Definition, ?> emailNode = diagram.getGraph().getNode("_277CE006-5E6E-4960-A68C-CC8A5347C33F");
        assertTrue(emailNode.getContent().getDefinition() instanceof CustomTask);
        CustomTask email = (CustomTask) emailNode.getContent().getDefinition();
        assertEquals(WorkItemDefinitionMockRegistry.EMAIL.getName(),
                     email.getName());
        assertEquals(WorkItemDefinitionMockRegistry.EMAIL.getCategory(),
                     email.getCategory());
        assertEquals(WorkItemDefinitionMockRegistry.EMAIL.getDefaultHandler(),
                     email.getDefaultHandler());
        assertEquals(WorkItemDefinitionMockRegistry.EMAIL.getDescription(),
                     email.getDescription());
        assertEquals(WorkItemDefinitionMockRegistry.EMAIL.getDisplayName(),
                     email.getGeneral().getName().getValue());
        assertEquals(WorkItemDefinitionMockRegistry.EMAIL.getDocumentation(),
                     email.getGeneral().getDocumentation().getValue());
        // Log service task assertions.
        Node<? extends Definition, ?> logNode = diagram.getGraph().getNode("_A940748F-A658-4FB8-84FD-B69F4B7A9205");
        assertTrue(logNode.getContent().getDefinition() instanceof CustomTask);
        CustomTask log = (CustomTask) logNode.getContent().getDefinition();
        assertEquals(WorkItemDefinitionMockRegistry.LOG.getName(),
                     log.getName());
        assertEquals(WorkItemDefinitionMockRegistry.LOG.getCategory(),
                     log.getCategory());
        assertEquals(WorkItemDefinitionMockRegistry.LOG.getDefaultHandler(),
                     log.getDefaultHandler());
        assertEquals(WorkItemDefinitionMockRegistry.LOG.getDescription(),
                     log.getDescription());
        assertEquals(WorkItemDefinitionMockRegistry.LOG.getDisplayName(),
                     log.getGeneral().getName().getValue());
        assertEquals(WorkItemDefinitionMockRegistry.LOG.getDocumentation(),
                     log.getGeneral().getDocumentation().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallNestedSubprocesses() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_NESTED_SUBPROCESSES);
        assertDiagram(diagram, 8);

        Bounds event1ExpectedBounds = Bounds.create(84.0, 275.0, 140.0, 331.0);
        Node<View<? extends Definition>, ?> event1 = diagram.getGraph().getNode("_46579C86-4ED2-4DDC-BBD0-51AC99034F0A");
        assertEquals(event1ExpectedBounds, event1.getContent().getBounds());

        Bounds subProcess1ExpectedBounds = Bounds.create(278.0, 68.0, 1443.0, 537.0);
        Bounds subProcess2ExpectedBounds = Bounds.create(196.0, 63.0, 1095.0, 428.0);
        Bounds task1ExpectedBounds = Bounds.create(25.0, 143.0, 179.0, 245.0);
        Node<View<? extends Definition>, ?> subProcess1 = diagram.getGraph().getNode("_3EF003AC-1AB2-416A-83EA-926A3978D6C0");
        Node<View<? extends Definition>, ?> task1 = diagram.getGraph().getNode("_529CFC51-DEAE-4E40-8404-BEAABC043171");
        Node<View<? extends Definition>, ?> subProcess2 = diagram.getGraph().getNode("_793D68BA-AC40-4B16-A036-36353F686977");
        List subProcess1Children = GraphUtils.getChildNodes(subProcess1);
        assertEquals(2, subProcess1Children.size());
        assertTrue(subProcess1Children.contains(task1));
        assertTrue(subProcess1Children.contains(subProcess2));
        assertEquals(subProcess1ExpectedBounds, subProcess1.getContent().getBounds());
        assertEquals(subProcess2ExpectedBounds, subProcess2.getContent().getBounds());
        assertEquals(task1ExpectedBounds, task1.getContent().getBounds());

        Bounds subProcess3ExpectedBounds = Bounds.create(193.0, 46.0, 846.0, 299.0);
        Bounds task2ExpectedBounds = Bounds.create(20.0, 80.0, 174.0, 182.0);
        Node<View<? extends Definition>, ?> subProcess3 = diagram.getGraph().getNode("_AE368C8F-8BE4-40A5-BC48-5CE5ACF05605");
        Node<View<? extends Definition>, ?> task2 = diagram.getGraph().getNode("_2265BA12-91FA-4064-8A7E-C963E15517EB");
        List subProcess2Children = GraphUtils.getChildNodes(subProcess2);
        assertEquals(2, subProcess2Children.size());
        assertTrue(subProcess2Children.contains(task2));
        assertTrue(subProcess2Children.contains(subProcess3));
        assertEquals(subProcess3ExpectedBounds, subProcess3.getContent().getBounds());
        assertEquals(task2ExpectedBounds, task2.getContent().getBounds());

        Bounds task3ExpectedBounds = Bounds.create(24.0, 34.0, 178.0, 136.0);
        Node<View<? extends Definition>, ?> task3 = diagram.getGraph().getNode("_B7873741-0FFA-4E46-B2E7-3B81D25842B3");
        List subProcess3Children = GraphUtils.getChildNodes(subProcess3);
        assertEquals(1, subProcess3Children.size());
        assertTrue(subProcess3Children.contains(task3));
        assertEquals(task3ExpectedBounds, task3.getContent().getBounds());
    }

    @Test
    public void testMarshallNestedSubprocesses() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_NESTED_SUBPROCESSES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      7,
                      1);
        //event1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "_46579C86-4ED2-4DDC-BBD0-51AC99034F0A", null);
        //subProcess1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "_3EF003AC-1AB2-416A-83EA-926A3978D6C0", true);
        //task1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "_529CFC51-DEAE-4E40-8404-BEAABC043171", null);
        //subProcess2
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "_793D68BA-AC40-4B16-A036-36353F686977", true);
        //subProcess3
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "_AE368C8F-8BE4-40A5-BC48-5CE5ACF05605", true);
        //task2
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "_2265BA12-91FA-4064-8A7E-C963E15517EB", null);
        //task3
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "_B7873741-0FFA-4E46-B2E7-3B81D25842B3", null);
    }

    @SuppressWarnings("unchecked")
    private static void assertCoordinatesProperlyCalculatedAndMarshalled(Diagram<Graph, Metadata> diagram, String marshalledDiagram, String elementId, Boolean isExpandedAttribute) {
        Node<View<? extends Definition>, ?> node = diagram.getGraph().getNode(elementId);
        Bounds nodeAbsoluteBounds = BasePropertyWriter.absoluteBounds(node);
        float expectedX = Double.valueOf(nodeAbsoluteBounds.getX()).floatValue();
        float expectedY = Double.valueOf(nodeAbsoluteBounds.getY()).floatValue();
        float expectedWidth = nodeAbsoluteBounds.getLowerRight().getX().floatValue() - nodeAbsoluteBounds.getUpperLeft().getX().floatValue();
        float expectedHeight = nodeAbsoluteBounds.getLowerRight().getY().floatValue() - nodeAbsoluteBounds.getUpperLeft().getY().floatValue();
        assertContainsShape(marshalledDiagram, elementId, expectedX, expectedY, expectedWidth, expectedHeight, isExpandedAttribute);
    }

    private static void assertContainsShape(String bpmnContent, String elementId, float expectedX, float expectedY, float expectedWidth, float expectedHeight, Boolean isExpandedAttribute) {
        if (isExpandedAttribute != null) {
            assertTrue(bpmnContent.contains(String.format("<bpmndi:BPMNShape id=\"shape_%s\" bpmnElement=\"%s\" isExpanded=\"%s\">", elementId, elementId, isExpandedAttribute.toString())));
        } else {
            assertTrue(bpmnContent.contains(String.format("<bpmndi:BPMNShape id=\"shape_%s\" bpmnElement=\"%s\">", elementId, elementId)));
        }
        assertTrue(bpmnContent.contains(String.format("<dc:Bounds height=\"%s\" width=\"%s\" x=\"%s\" y=\"%s\"/>", expectedHeight, expectedWidth, expectedX, expectedY)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshall_ARIS_LANES_1() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ARIS_LANES_1);
        Node<? extends Definition, ?> bpmnDiagramNode = diagram.getGraph().getNode("Definitions_ID-ef3bd1b1-35d2-11e9-21c1-02b28450efee");
        assertNotNull(bpmnDiagramNode);
        BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) bpmnDiagramNode.getContent().getDefinition();
        assertDiagram(diagram, 7);
        assertEquals("ARIS_LANES_1", bpmnDiagram.getDiagramSet().getName().getValue());
        //Lane1 contains Task1
        assertExpectedLane(diagram, "ID-fba59d50-35d2-11e9-21c1-02b28450efee", "Lane1", bpmnDiagramNode, "ID-fba59d56-35d2-11e9-21c1-02b28450efee");
        //Lane2 contains Task2
        assertExpectedLane(diagram, "ID-fba59d52-35d2-11e9-21c1-02b28450efee", "Lane2", bpmnDiagramNode, "ID-fba59d59-35d2-11e9-21c1-02b28450efee");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshall_ARIS_LANES_2() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ARIS_LANES_2);
        Node<? extends Definition, ?> bpmnDiagramNode = diagram.getGraph().getNode("Definitions_ID-4c9756c0-35da-11e9-21c1-02b28450efee");
        assertNotNull(bpmnDiagramNode);
        BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) bpmnDiagramNode.getContent().getDefinition();
        assertDiagram(diagram, 12);
        assertEquals("ARIS_LANES_2", bpmnDiagram.getDiagramSet().getName().getValue());
        //Lane1.1 contains Task3 and Task4
        assertExpectedLane(diagram, "ID-57e50405-35da-11e9-21c1-02b28450efee", "Lane1.1", bpmnDiagramNode, "ID-57e5041a-35da-11e9-21c1-02b28450efee", "ID-57e5041d-35da-11e9-21c1-02b28450efee");
        //Lane1.2 contains Task1 and Task2
        assertExpectedLane(diagram, "ID-57e50402-35da-11e9-21c1-02b28450efee", "Lane1.2", bpmnDiagramNode, "ID-57e50414-35da-11e9-21c1-02b28450efee", "ID-57e50417-35da-11e9-21c1-02b28450efee");
        //Lane2.1 contains no tasks
        assertExpectedLane(diagram, "ID-57e5040e-35da-11e9-21c1-02b28450efee", "Lane2.1", bpmnDiagramNode);
        //Lane2.2 contains Task5
        assertExpectedLane(diagram, "ID-57e5040b-35da-11e9-21c1-02b28450efee", "Lane2.2", bpmnDiagramNode, "ID-57e50420-35da-11e9-21c1-02b28450efee");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshall_ARIS_LANES_3() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ARIS_LANES_3);
        Node<? extends Definition, ?> bpmnDiagramNode = diagram.getGraph().getNode("Definitions_ID-3cad84c1-35dd-11e9-21c1-02b28450efee");
        assertNotNull(bpmnDiagramNode);
        BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) bpmnDiagramNode.getContent().getDefinition();
        assertDiagram(diagram, 17);
        assertEquals("ARIS_LANES_3", bpmnDiagram.getDiagramSet().getName().getValue());
        //LaneA1.1 contains Task4
        assertExpectedLane(diagram, "ID-43452405-35dd-11e9-21c1-02b28450efee", "LaneA1.1", bpmnDiagramNode, "ID-43452430-35dd-11e9-21c1-02b28450efee");
        //LaneA1.2.2 contains Task1
        assertExpectedLane(diagram, "ID-43452409-35dd-11e9-21c1-02b28450efee", "LaneA1.2.2", bpmnDiagramNode, "ID-43452424-35dd-11e9-21c1-02b28450efee");
        //LaneA1.2.1.1 contains Task3
        assertExpectedLane(diagram, "ID-43452410-35dd-11e9-21c1-02b28450efee", "LaneA1.2.1.1", bpmnDiagramNode, "ID-4345242d-35dd-11e9-21c1-02b28450efee");
        //LaneA1.2.1.2 contains Task2
        assertExpectedLane(diagram, "ID-43452413-35dd-11e9-21c1-02b28450efee", "LaneA1.2.1.2", bpmnDiagramNode, "ID-4345242a-35dd-11e9-21c1-02b28450efee");
        //LaneA5 contains Task5
        assertExpectedLane(diagram, "ID-43452417-35dd-11e9-21c1-02b28450efee", "LaneA2", bpmnDiagramNode, "ID-69882521-35df-11e9-21c1-02b28450efee");
        //LaneA3.1 contains Task7
        assertExpectedLane(diagram, "ID-4345241e-35dd-11e9-21c1-02b28450efee", "LaneA3.1", bpmnDiagramNode, "ID-69882527-35df-11e9-21c1-02b28450efee");
        //LaneA3.2 contains Task6
        assertExpectedLane(diagram, "ID-4345241b-35dd-11e9-21c1-02b28450efee", "LaneA3.2", bpmnDiagramNode, "ID-69882524-35df-11e9-21c1-02b28450efee");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshall_ARIS_MULTIPLE_COLLAPSED_SUBPROCESSES() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(ARIS_MULTIPLE_COLLAPSED_SUBPROCESSES);
        assertDiagram(diagram, 11);
        String subProcess1_ID = "ID-4dee0202-4681-11e9-21c1-02b28450efee";
        Node<View<? extends Definition>, ?> subProcess1 = diagram.getGraph().getNode(subProcess1_ID);
        //subProcess1 must contain task1.1 and task1.2 both from the logical model and the UI perspective.
        String task1_1 = "ID-4dee0204-4681-11e9-21c1-02b28450efee";
        String task1_2 = "ID-4dee0207-4681-11e9-21c1-02b28450efee";
        assertContains(subProcess1, Arrays.asList(task1_1, task1_2));
        //subProcess2 must contain task2.1 and task2.2 both from the logical model and the UI perspective.
        String subProcess2_ID = "ID-4dee020a-4681-11e9-21c1-02b28450efee";
        Node<View<? extends Definition>, ?> subProcess2 = diagram.getGraph().getNode(subProcess2_ID);
        String task2_1 = "ID-4dee020c-4681-11e9-21c1-02b28450efee";
        String task2_2 = "ID-4dee020f-4681-11e9-21c1-02b28450efee";
        assertContains(subProcess2, Arrays.asList(task2_1, task2_2));
    }

    @Test
    public void testMarshall_ARIS_MULTIPLE_COLLAPSED_SUBPROCESSES() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(ARIS_MULTIPLE_COLLAPSED_SUBPROCESSES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      10,
                      7);
        //subProcess1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-4dee0202-4681-11e9-21c1-02b28450efee", true);
        //task1.1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-4dee0204-4681-11e9-21c1-02b28450efee", null);
        //task1.2
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-4dee0207-4681-11e9-21c1-02b28450efee", null);
        //subProcess2
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-4dee020a-4681-11e9-21c1-02b28450efee", true);
        //task2.1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-4dee020c-4681-11e9-21c1-02b28450efee", null);
        //task2.2
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-4dee020f-4681-11e9-21c1-02b28450efee", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshall_ARIS_NESTED_COLLAPSED_SUBPROCESSES() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(ARIS_NESTED_COLLAPSED_SUBPROCESSES);
        assertDiagram(diagram, 9);
        String subProcessA1_ID = "ID-401c4a50-4673-11e9-21c1-02b28450efee";
        Node<View<? extends Definition>, ?> subProcessA1 = diagram.getGraph().getNode(subProcessA1_ID);
        //subProcessA1 must contain subProcessA2, taskA1.1, taskA1.2 and taskA1.3 both from the logical model and the UI perspective.
        String subProcessA2_ID = "ID-401c4a58-4673-11e9-21c1-02b28450efee";
        String taskA1_1 = "ID-401c4a55-4673-11e9-21c1-02b28450efee";
        String taskA1_2 = "ID-401c4a5b-4673-11e9-21c1-02b28450efee";
        String taskA1_3 = "ID-401c4a6b-4673-11e9-21c1-02b28450efee";

        assertContains(subProcessA1, Arrays.asList(subProcessA2_ID, taskA1_1, taskA1_2, taskA1_3));
        //subProcessA2 must contain taskA2.1 and taskA2.2 both from the logical model and the UI perspective.
        String taskA2_1 = "ID-401c4a64-4673-11e9-21c1-02b28450efee";
        String taskA2_2 = "ID-401c4a67-4673-11e9-21c1-02b28450efee";
        Node<View<? extends Definition>, ?> subProcessA2 = diagram.getGraph().getNode(subProcessA2_ID);
        assertContains(subProcessA2, Arrays.asList(taskA2_1, taskA2_2));
    }

    @Test
    public void testMarshall_ARIS_NESTED_COLLAPSED_SUBPROCESSES() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(ARIS_NESTED_COLLAPSED_SUBPROCESSES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      8,
                      6);
        //task1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-401c4a52-4673-11e9-21c1-02b28450efee", null);
        //subProcessA1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-401c4a50-4673-11e9-21c1-02b28450efee", true);
        //works taskA1.1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-401c4a55-4673-11e9-21c1-02b28450efee", null);
        //taskA1.2
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-401c4a5b-4673-11e9-21c1-02b28450efee", null);
        //taskA1.3
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-401c4a6b-4673-11e9-21c1-02b28450efee", null);
        //SubprocessA2
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-401c4a58-4673-11e9-21c1-02b28450efee", true);
        //TaskA2.1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-401c4a64-4673-11e9-21c1-02b28450efee", null);
        //TaskA2.2
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-401c4a67-4673-11e9-21c1-02b28450efee", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshall_ARIS_COLLAPSED_SUBPROCESS_IN_LANE() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(ARIS_COLLAPSED_SUBPROCESS_IN_LANE);
        assertDiagram(diagram, 7);
        String lane_ID = "ID-feebbb40-5aab-11e9-21c1-02b28450efee";
        Node<View<? extends Definition>, ?> lane = diagram.getGraph().getNode(lane_ID);
        //the lane must contain task1, task2 and subprocess1 from the logical model and the UI perspective.
        String task1 = "ID-feebbb42-5aab-11e9-21c1-02b28450efee";
        String task2 = "ID-feebbb49-5aab-11e9-21c1-02b28450efee";
        String subProcess1_ID = "ID-feebbb45-5aab-11e9-21c1-02b28450efee";
        assertContains(lane, Arrays.asList(task1, task2, subProcess1_ID));

        //subProcess1 must contain task1.1 and task1.2 both from the logical model and the UI perspective.
        Node<View<? extends Definition>, ?> subProcess1 = diagram.getGraph().getNode(subProcess1_ID);
        String task1_1 = "ID-feebbb50-5aab-11e9-21c1-02b28450efee";
        String task1_2 = "ID-feebbb53-5aab-11e9-21c1-02b28450efee";
        assertContains(subProcess1, Arrays.asList(task1_1, task1_2));
    }

    @Test
    public void testMarshall_ARIS_COLLAPSED_SUBPROCESS_IN_LANE() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(ARIS_COLLAPSED_SUBPROCESS_IN_LANE);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      6,
                      3);
        //lane
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-feebbb40-5aab-11e9-21c1-02b28450efee", null);
        //task1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-feebbb42-5aab-11e9-21c1-02b28450efee", null);
        //task2
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-feebbb49-5aab-11e9-21c1-02b28450efee", null);
        //subprocess1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-feebbb45-5aab-11e9-21c1-02b28450efee", true);
        //task1.1
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-feebbb50-5aab-11e9-21c1-02b28450efee", null);
        //task1.2
        assertCoordinatesProperlyCalculatedAndMarshalled(diagram, result, "ID-feebbb53-5aab-11e9-21c1-02b28450efee", null);
    }

    @SuppressWarnings("unchecked")
    private static void assertContains(Node<View<? extends Definition>, ?> container, List<String> containedNodes) {
        List<Node> children = GraphUtils.getChildNodes(container).stream()
                .filter(child -> containedNodes.contains(child.getUUID()))
                .collect(Collectors.toList());

        Bounds containerBounds = container.getContent().getBounds();
        Point2D containerAbsolutePosition = GraphUtils.getComputedPosition(container);
        Bounds containerAbsoluteBounds = Bounds.create(containerAbsolutePosition.getX(),
                                                       containerAbsolutePosition.getY(),
                                                       containerAbsolutePosition.getX() + containerBounds.getWidth(),
                                                       containerAbsolutePosition.getY() + containerBounds.getHeight());
        assertEquals(containedNodes.size(), children.size());
        children.forEach(child -> {
            Bounds relativeBounds = ((View) child.getContent()).getBounds();
            Point2D absolutePosition = GraphUtils.getComputedPosition(child);
            Bounds absoluteBounds = Bounds.create(absolutePosition.getX(),
                                                  absolutePosition.getY(),
                                                  absolutePosition.getX() + relativeBounds.getWidth(),
                                                  absolutePosition.getY() + relativeBounds.getHeight());
            assertContains(containerAbsoluteBounds, absoluteBounds);
        });
    }

    private static void assertContains(Bounds container, Bounds contained) {
        assertTrue(container.getUpperLeft().getX() < contained.getUpperLeft().getX());
        assertTrue(container.getUpperLeft().getY() < contained.getUpperLeft().getY());
        assertTrue(container.getLowerRight().getX() > contained.getLowerRight().getX());
        assertTrue(container.getLowerRight().getY() > contained.getLowerRight().getY());
    }

    @SuppressWarnings("unchecked")
    private void assertExpectedLane(Diagram<Graph, Metadata> diagram, String laneId, String laneName, Node<? extends Definition, ?> parentNode, String... expectedChildrenIds) {
        Node<? extends Definition, ?> laneNode = diagram.getGraph().getNode(laneId);
        assertNotNull("Node: " + laneId + " was not found in diagram", laneNode);
        assertTrue("Node: " + laneId + " is not a Lane", laneNode.getContent().getDefinition() instanceof Lane);
        Lane lane = (Lane) laneNode.getContent().getDefinition();
        assertEquals(laneName, lane.getGeneral().getName().getValue());
        assertEquals(1, laneNode.getInEdges().size());
        assertEquals(parentNode, laneNode.getInEdges().get(0).getSourceNode());

        if (expectedChildrenIds == null) {
            assertEquals(0, laneNode.getOutEdges().size());
        } else {
            assertEquals(expectedChildrenIds.length, laneNode.getOutEdges().size());
            Arrays.stream(expectedChildrenIds).forEach(expectedChildrenId -> assertTrue(laneNode.getOutEdges().stream().anyMatch(outEdge -> outEdge.getTargetNode().getUUID().equals(expectedChildrenId))));
        }
    }

    @Test
    public void testMarshallWorkItems() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SERVICE_TASKS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      4,
                      3);
        assertTrue(result.contains("drools:taskName=\"Email\""));
        assertTrue(result.contains("drools:taskName=\"Log\""));
    }

    @Test
    public void testNotifications() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_REASSIGNMENT_NOTIFICATION);
        Node<? extends Definition, ?> multipleInstanceSubprocessNode = diagram.getGraph().getNode("_F402A212-CBB8-4F1B-A7FC-EE185C41BBF7");
        UserTask userTask = (UserTask) multipleInstanceSubprocessNode.getContent().getDefinition();
        NotificationsInfo notificationsInfo = userTask.getExecutionSet().getNotificationsInfo();
        assertEquals(1, notificationsInfo.getValue().getValues().size());
        NotificationValue notification = notificationsInfo.getValue().getValues().get(0);
        assertEquals("[from:director|tousers:alessio,guest,john|togroups:Developer,IT|toemails:|replyTo:guest|subject:test|body:test body]@[11h]", notification.toCDATAFormat());
        assertEquals("NotStartedNotify", notification.getType());
    }

    @Test
    public void testReassignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_REASSIGNMENT_NOTIFICATION);
        Node<? extends Definition, ?> multipleInstanceSubprocessNode = diagram.getGraph().getNode("_F402A212-CBB8-4F1B-A7FC-EE185C41BBF7");
        UserTask userTask = (UserTask) multipleInstanceSubprocessNode.getContent().getDefinition();
        ReassignmentsInfo reassignmentsInfo = userTask.getExecutionSet().getReassignmentsInfo();
        assertEquals(2, reassignmentsInfo.getValue().getValues().size());

        ReassignmentValue reassignment = reassignmentsInfo.getValue().getValues().get(0);
        assertEquals("[users:Reviewer|groups:kirill]@[1111w]", reassignment.toCDATAFormat());
        assertEquals("NotStartedReassign", reassignment.getType());

        reassignment = reassignmentsInfo.getValue().getValues().get(1);
        assertEquals("[users:Forms,HR|groups:director,guest]@[22h]", reassignment.toCDATAFormat());
        assertEquals("NotCompletedReassign", reassignment.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEventGateway() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVENT_GATEWAY);
        assertDiagram(diagram, 7);
        assertEquals(diagram.getMetadata().getTitle(), "TestEventGateway");
        Graph graph = diagram.getGraph();
        Node<? extends Definition, ?> gatewayNode = graph.getNode("_AFDF2596-C521-4753-AC22-2DCCAD391F98");
        assertTrue(gatewayNode.getContent().getDefinition() instanceof EventGateway);
        EventGateway eventGateway = (EventGateway) gatewayNode.getContent().getDefinition();
        assertEquals("EventGatewayName", eventGateway.getGeneral().getName().getValue());
        assertEquals("EventGatewayDocumentation", eventGateway.getGeneral().getDocumentation().getValue());
        SequenceFlow inSequenceFlow = gatewayNode.getInEdges().stream()
                .filter(edge -> "_E805280D-5862-4F56-B02A-E34F7D519050".equals(edge.getUUID()))
                .map(edge -> (SequenceFlow) ((ViewConnector) edge.getContent()).getDefinition())
                .findFirst().orElseThrow(() -> new Exception("Expected sequenceFlow: _E805280D-5862-4F56-B02A-E34F7D519050 was not found"));
        SequenceFlow outSequenceFlow1 = gatewayNode.getOutEdges().stream()
                .filter(edge -> "_CCEF6352-760D-4641-B9C9-0B01FD4DD704".equals(edge.getUUID()))
                .map(edge -> (SequenceFlow) ((ViewConnector) edge.getContent()).getDefinition())
                .findFirst().orElseThrow(() -> new Exception("Expected sequenceFlow: _CCEF6352-760D-4641-B9C9-0B01FD4DD704 was not found"));
        SequenceFlow outSequenceFlow2 = gatewayNode.getOutEdges().stream()
                .filter(edge -> "_1CD28E0D-1910-45FE-9AEC-932FA28C77AA".equals(edge.getUUID()))
                .map(edge -> (SequenceFlow) ((ViewConnector) edge.getContent()).getDefinition())
                .findFirst().orElseThrow(() -> new Exception("Expected sequenceFlow: _1CD28E0D-1910-45FE-9AEC-932FA28C77AA was not found"));

        assertNotNull(inSequenceFlow);
        assertEquals("inSequence", inSequenceFlow.getGeneral().getName().getValue());
        assertNotNull(outSequenceFlow1);
        assertEquals("outSequence1", outSequenceFlow1.getGeneral().getName().getValue());
        assertNotNull(outSequenceFlow2);
        assertEquals("outSequence2", outSequenceFlow2.getGeneral().getName().getValue());
    }

    @Test
    public void testMarshallEventGateway() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVENT_GATEWAY);
        assertDiagram(diagram, 7);

        String result = tested.marshall(diagram);
        assertDiagram(result, 1, 6, 5);

        assertTrue(result.contains("<bpmn2:eventBasedGateway id=\"_AFDF2596-C521-4753-AC22-2DCCAD391F98\" name=\"EventGatewayName\" gatewayDirection=\"Diverging\">"));
        assertTrue(result.contains("<![CDATA[EventGatewayDocumentation]]></bpmn2:documentation>"));
        assertTrue(result.contains("<drools:metaValue><![CDATA[EventGatewayName]]></drools:metaValue>"));
        assertTrue(result.contains("<bpmn2:incoming>_E805280D-5862-4F56-B02A-E34F7D519050</bpmn2:incoming>"));
        assertTrue(result.contains("<bpmn2:outgoing>_1CD28E0D-1910-45FE-9AEC-932FA28C77AA</bpmn2:outgoing>"));
        assertTrue(result.contains("<bpmn2:outgoing>_CCEF6352-760D-4641-B9C9-0B01FD4DD704</bpmn2:outgoing>"));
        assertTrue(result.contains("</bpmn2:eventBasedGateway>"));
        assertTrue(result.contains("<bpmn2:sequenceFlow id=\"_E805280D-5862-4F56-B02A-E34F7D519050\" name=\"inSequence\" sourceRef=\"_FC7A4CF4-08CC-4F85-A518-34E67416160C\" targetRef=\"_AFDF2596-C521-4753-AC22-2DCCAD391F98\">"));
        assertTrue(result.contains("<bpmn2:sequenceFlow id=\"_CCEF6352-760D-4641-B9C9-0B01FD4DD704\" name=\"outSequence1\" sourceRef=\"_AFDF2596-C521-4753-AC22-2DCCAD391F98\" targetRef=\"_F2D949C2-84FE-4AF9-A4B2-C2DC917C1050\">"));
        assertTrue(result.contains("<bpmn2:sequenceFlow id=\"_1CD28E0D-1910-45FE-9AEC-932FA28C77AA\" name=\"outSequence2\" sourceRef=\"_AFDF2596-C521-4753-AC22-2DCCAD391F98\" targetRef=\"_19AA4F91-684B-495F-9880-DA506E0696FE\">"));
    }

    private List<Node> getNodes(Diagram<Graph, Metadata> diagram) {
        Graph graph = diagram.getGraph();
        assertNotNull(graph);
        Iterator<Node> nodesIterable = graph.nodes().iterator();
        List<Node> nodes = new ArrayList<>();
        nodesIterable.forEachRemaining(nodes::add);
        return nodes;
    }

    @SuppressWarnings("unchecked")
    private Iterator<Element> nodesIterator(Diagram<Graph, Metadata> diagram) {
        return (Iterator<Element>) diagram.getGraph().nodes().iterator();
    }

    private Process getProcess(Definitions definitions) {
        Object o = Arrays.stream(definitions.getRootElements().toArray())
                .filter(x -> x instanceof Process)
                .findFirst()
                .orElse(null);
        return (Process) o;
    }

    private void assertItemExists(List<RootElement> rootElements,
                                  String id,
                                  String structureRef) {
        for (RootElement rootElement : rootElements) {
            if (id.equals(rootElement.getId()) && rootElement instanceof ItemDefinition) {
                ItemDefinition itemDefinition = (ItemDefinition) rootElement;
                if (structureRef.equals(itemDefinition.getStructureRef())) {
                    // pass;
                    return;
                } else {
                    fail("Found mismatching item with id = " + id + " " + itemDefinition);
                }
            }
        }
        fail("Could not find item id = " + id);
    }

    private Property getProcessProperty(List<Property> properties,
                                        String id,
                                        String itemSubjectRef) {
        for (Property property : properties) {
            if (id.equals(property.getId()) || id.equals(property.getName())) {
                if (itemSubjectRef.equals(property.getItemSubjectRef().getId())) {
                    return property;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private String getProcessPropertyValue(Process process,
                                           String propertyName) {
        Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (propertyName.equals(entry.getEStructuralFeature().getName())) {
                return entry.getValue().toString();
            }
        }
        return null;
    }

    private String getProcessExtensionValue(Process process,
                                            String propertyName) {
        List<ExtensionAttributeValue> extensionValues = process.getExtensionValues();
        for (ExtensionAttributeValue extensionValue : extensionValues) {
            FeatureMap featureMap = extensionValue.getValue();
            for (int i = 0; i < featureMap.size(); i++) {
                EStructuralFeatureImpl.SimpleFeatureMapEntry featureMapEntry = (EStructuralFeatureImpl.SimpleFeatureMapEntry) featureMap.get(i);
                MetaDataType featureMapValue = (MetaDataType) featureMapEntry.getValue();
                if (propertyName.equals(featureMapValue.getName())) {
                    return featureMapValue.getMetaValue();
                }
            }
        }
        return "";
    }

    private Object getNamedFlowElement(Process process,
                                       Class cls,
                                       String name) {
        List<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (cls.isInstance(flowElement) && name.equals(flowElement.getName())) {
                return flowElement;
            }
        }
        return null;
    }

    private DataInput getDataInput(Activity activity,
                                   String name) {
        InputOutputSpecification ioSpecification = activity.getIoSpecification();
        if (ioSpecification != null) {
            List<DataInput> dataInputs = ioSpecification.getDataInputs();
            if (dataInputs != null) {
                return Arrays.stream(dataInputs.toArray(new DataInput[dataInputs.size()]))
                        .filter(dataInput -> name.equals(dataInput.getName()))
                        .findFirst()
                        .orElse(null);
            }
        }

        return null;
    }

    private DataOutput getDataOutput(Activity activity,
                                     String name) {
        InputOutputSpecification ioSpecification = activity.getIoSpecification();
        if (ioSpecification != null) {
            List<DataOutput> dataOutputs = ioSpecification.getDataOutputs();
            if (dataOutputs != null) {
                return Arrays.stream(dataOutputs.toArray(new DataOutput[dataOutputs.size()]))
                        .filter(dataOutput -> name.equals(dataOutput.getName()))
                        .findFirst()
                        .orElse(null);
            }
        }
        return null;
    }

    private void validateDataInputOrOutput(ItemAwareElement itemAwareElement,
                                           String idSuffix,
                                           String dataType,
                                           String itemSubjectRefSuffix) {
        assertNotNull(itemAwareElement);

        assertTrue(itemAwareElement.getId().endsWith(idSuffix));
        ItemDefinition itemDefinition = itemAwareElement.getItemSubjectRef();
        assertNotNull(itemDefinition);
        assertEquals(itemDefinition.getStructureRef(), (dataType));
        assertTrue(itemDefinition.getId().endsWith(itemSubjectRefSuffix));
    }

    private ItemAwareElement getDataInputAssociationSourceRef(Activity activity,
                                                              String id) {
        List<DataInputAssociation> dataInputAssociations = activity.getDataInputAssociations();
        if (dataInputAssociations != null) {
            for (DataInputAssociation dataInputAssociation : dataInputAssociations) {
                List<ItemAwareElement> sourceRef = dataInputAssociation.getSourceRef();
                if (sourceRef != null && !sourceRef.isEmpty()) {
                    ItemAwareElement result = Arrays.stream(sourceRef.toArray(new ItemAwareElement[sourceRef.size()]))
                            .filter(itemAwareElement ->
                                            id.equals(itemAwareElement.getId())
                                                    || id.equals(((Property) itemAwareElement).getName()))
                            .findFirst()
                            .orElse(null);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private ItemAwareElement getDataInputAssociationTargetRef(Activity activity,
                                                              String idSuffix) {
        List<DataInputAssociation> dataInputAssociations = activity.getDataInputAssociations();
        if (dataInputAssociations != null) {
            for (DataInputAssociation dataInputAssociation : dataInputAssociations) {
                ItemAwareElement targetRef = dataInputAssociation.getTargetRef();
                if (targetRef != null && targetRef.getId().endsWith(idSuffix)) {
                    return targetRef;
                }
            }
        }
        return null;
    }

    private ItemAwareElement getDataOutputAssociationSourceRef(Activity activity,
                                                               String idSuffix) {
        List<DataOutputAssociation> dataOutputAssociations = activity.getDataOutputAssociations();
        if (dataOutputAssociations != null) {
            for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
                List<ItemAwareElement> sourceRef = dataOutputAssociation.getSourceRef();
                if (sourceRef != null && !sourceRef.isEmpty()) {
                    ItemAwareElement result = Arrays.stream(sourceRef.toArray(new ItemAwareElement[sourceRef.size()]))
                            .filter(itemAwareElement -> itemAwareElement.getId().endsWith(idSuffix))
                            .findFirst()
                            .orElse(null);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private ItemAwareElement getDataOutputAssociationTargetRef(Activity activity,
                                                               String id) {
        List<DataOutputAssociation> dataOutputAssociations = activity.getDataOutputAssociations();
        if (dataOutputAssociations != null) {
            for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
                ItemAwareElement targetRef = dataOutputAssociation.getTargetRef();
                if (targetRef != null &&
                        id.equals(targetRef.getId())
                        || id.equals(((Property) targetRef).getName())) {
                    return targetRef;
                }
            }
        }
        return null;
    }

    private Diagram<Graph, Metadata> unmarshall(String s) throws Exception {
        return Unmarshalling.unmarshall(tested, s);
    }

    private Diagram<Graph, Metadata> unmarshall(ByteArrayInputStream s) throws Exception {
        return Unmarshalling.unmarshall(tested, s);
    }
}
