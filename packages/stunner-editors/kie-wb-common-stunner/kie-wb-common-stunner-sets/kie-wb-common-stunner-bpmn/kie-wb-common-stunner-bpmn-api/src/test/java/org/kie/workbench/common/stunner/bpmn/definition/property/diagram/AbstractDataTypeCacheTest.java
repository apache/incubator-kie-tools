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


package org.kie.workbench.common.stunner.bpmn.definition.property.diagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class AbstractDataTypeCacheTest {

    private AbstractDataTypeCache dataTypeCache;

    private ArrayList<String> defaultDataTypes = new ArrayList<>(Arrays.asList("MyType", "MyString", "MyObject", "com.myspace.Person"));

    private View firstView;

    private View lastView;

    @Before
    public void setUp() throws Exception {

        firstView = spy(View.class);
        lastView = spy(View.class);

        dataTypeCache = new AbstractDataTypeCache() {
            @Override
            protected void cacheDataTypes(Object processRoot) {
            }

            @Override
            protected List<String> processAssignments(AssignmentsInfo info) {
                return defaultDataTypes;
            }

            @Override
            protected List<String> getDataTypes(String variables, boolean isTwoColonFormat) {
                return defaultDataTypes;
            }
        };
    }

    @Test
    public void testStartExtractCustomTask() {
        CustomTask customTask = new CustomTask();
        CustomTask customTask2 = new CustomTask();

        when(firstView.getDefinition()).thenReturn(customTask);
        when(lastView.getDefinition()).thenReturn(customTask2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractStartSignalEvent() {
        StartSignalEvent startSignalEvent = new StartSignalEvent();
        StartSignalEvent startSignalEvent2 = new StartSignalEvent();

        when(firstView.getDefinition()).thenReturn(startSignalEvent);
        when(lastView.getDefinition()).thenReturn(startSignalEvent2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractStartMessageEvent() {
        StartMessageEvent startMessageEvent = new StartMessageEvent();
        StartMessageEvent startMessageEvent2 = new StartMessageEvent();

        when(firstView.getDefinition()).thenReturn(startMessageEvent);
        when(lastView.getDefinition()).thenReturn(startMessageEvent2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractStartEscalationEvent() {
        StartEscalationEvent startEscalationEvent = new StartEscalationEvent();
        StartEscalationEvent startEscalationEvent2 = new StartEscalationEvent();

        when(firstView.getDefinition()).thenReturn(startEscalationEvent);
        when(lastView.getDefinition()).thenReturn(startEscalationEvent2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractStartErrorEvent() {
        StartErrorEvent startErrorEvent = new StartErrorEvent();
        StartErrorEvent startErrorEvent2 = new StartErrorEvent();

        when(firstView.getDefinition()).thenReturn(startErrorEvent);
        when(lastView.getDefinition()).thenReturn(startErrorEvent2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractReusableSubprocess() {
        ReusableSubprocess reusableSubprocess = new ReusableSubprocess();
        ReusableSubprocess reusableSubprocess2 = new ReusableSubprocess();

        when(firstView.getDefinition()).thenReturn(reusableSubprocess);
        when(lastView.getDefinition()).thenReturn(reusableSubprocess2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractIntermediateSignalEventThrowing() {
        IntermediateSignalEventThrowing intermediateSignalEventThrowing = new IntermediateSignalEventThrowing();
        IntermediateSignalEventThrowing intermediateSignalEventThrowing2 = new IntermediateSignalEventThrowing();

        when(firstView.getDefinition()).thenReturn(intermediateSignalEventThrowing);
        when(lastView.getDefinition()).thenReturn(intermediateSignalEventThrowing2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractIntermediateSignalEventCatching() {
        IntermediateSignalEventCatching intermediateSignalEventCatching = new IntermediateSignalEventCatching();
        IntermediateSignalEventCatching intermediateSignalEventCatching2 = new IntermediateSignalEventCatching();

        when(firstView.getDefinition()).thenReturn(intermediateSignalEventCatching);
        when(lastView.getDefinition()).thenReturn(intermediateSignalEventCatching2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractIntermediateMessageEventThrowing() {
        IntermediateMessageEventThrowing intermediateMessageEventThrowing = new IntermediateMessageEventThrowing();
        IntermediateMessageEventThrowing intermediateMessageEventThrowing2 = new IntermediateMessageEventThrowing();

        when(firstView.getDefinition()).thenReturn(intermediateMessageEventThrowing);
        when(lastView.getDefinition()).thenReturn(intermediateMessageEventThrowing2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractIntermediateMessageEventCatching() {
        IntermediateMessageEventCatching intermediateMessageEventCatching = new IntermediateMessageEventCatching();
        IntermediateMessageEventCatching intermediateMessageEventCatching2 = new IntermediateMessageEventCatching();

        when(firstView.getDefinition()).thenReturn(intermediateMessageEventCatching);
        when(lastView.getDefinition()).thenReturn(intermediateMessageEventCatching2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractIntermediateEscalationEventThrowing() {
        IntermediateEscalationEventThrowing intermediateEscalationEventThrowing = new IntermediateEscalationEventThrowing();
        IntermediateEscalationEventThrowing intermediateEscalationEventThrowing2 = new IntermediateEscalationEventThrowing();

        when(firstView.getDefinition()).thenReturn(intermediateEscalationEventThrowing);
        when(lastView.getDefinition()).thenReturn(intermediateEscalationEventThrowing);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractIntermediateEscalationEvent() {
        IntermediateEscalationEvent intermediateEscalationEvent = new IntermediateEscalationEvent();
        IntermediateEscalationEvent intermediateEscalationEvent2 = new IntermediateEscalationEvent();

        when(firstView.getDefinition()).thenReturn(intermediateEscalationEvent);
        when(lastView.getDefinition()).thenReturn(intermediateEscalationEvent2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractIntermediateErrorEventCatching() {
        IntermediateErrorEventCatching intermediateErrorEventCatching = new IntermediateErrorEventCatching();
        IntermediateErrorEventCatching intermediateErrorEventCatching2 = new IntermediateErrorEventCatching();

        when(firstView.getDefinition()).thenReturn(intermediateErrorEventCatching);
        when(lastView.getDefinition()).thenReturn(intermediateErrorEventCatching2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractIntermediateLinkEventThrowing() {
        IntermediateLinkEventThrowing intermediateLinkEventThrowing = new IntermediateLinkEventThrowing();
        IntermediateLinkEventThrowing intermediateLinkEventThrowing2 = new IntermediateLinkEventThrowing();

        when(firstView.getDefinition()).thenReturn(intermediateLinkEventThrowing);
        when(lastView.getDefinition()).thenReturn(intermediateLinkEventThrowing2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractIntermediateLinkEventCatching() {
        IntermediateLinkEventCatching intermediateLinkEventCatching = new IntermediateLinkEventCatching();
        IntermediateLinkEventCatching intermediateLinkEventCatching2 = new IntermediateLinkEventCatching();

        when(firstView.getDefinition()).thenReturn(intermediateLinkEventCatching);
        when(lastView.getDefinition()).thenReturn(intermediateLinkEventCatching2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractEndSignalEvent() {
        EndSignalEvent endSignalEvent = new EndSignalEvent();
        EndSignalEvent endSignalEvent2 = new EndSignalEvent();

        when(firstView.getDefinition()).thenReturn(endSignalEvent);
        when(lastView.getDefinition()).thenReturn(endSignalEvent2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractEndMessageEvent() {
        EndMessageEvent endMessageEvent = new EndMessageEvent();
        EndMessageEvent endMessageEvent2 = new EndMessageEvent();

        when(firstView.getDefinition()).thenReturn(endMessageEvent);
        when(lastView.getDefinition()).thenReturn(endMessageEvent2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractEndEscalationEvent() {
        EndEscalationEvent endEscalationEvent = new EndEscalationEvent();
        EndEscalationEvent endEscalationEvent2 = new EndEscalationEvent();

        when(firstView.getDefinition()).thenReturn(endEscalationEvent);
        when(lastView.getDefinition()).thenReturn(endEscalationEvent2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractEndErrorEvent() {
        EndErrorEvent endErrorEvent = new EndErrorEvent();
        EndErrorEvent endErrorEvent2 = new EndErrorEvent();

        when(firstView.getDefinition()).thenReturn(endErrorEvent);
        when(lastView.getDefinition()).thenReturn(endErrorEvent2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractBusinessRuleTask() {
        BusinessRuleTask businessRuleTask = new BusinessRuleTask();
        BusinessRuleTask businessRuleTask2 = new BusinessRuleTask();

        when(firstView.getDefinition()).thenReturn(businessRuleTask);
        when(lastView.getDefinition()).thenReturn(businessRuleTask2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractGenericServiceTask() {
        GenericServiceTask genericServiceTask = new GenericServiceTask();
        GenericServiceTask genericServiceTask2 = new GenericServiceTask();

        when(firstView.getDefinition()).thenReturn(genericServiceTask);
        when(lastView.getDefinition()).thenReturn(genericServiceTask2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractUserTask() {
        UserTask userTask = new UserTask();
        UserTask userTask2 = new UserTask();

        when(firstView.getDefinition()).thenReturn(userTask);
        when(lastView.getDefinition()).thenReturn(userTask2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractMultipleInstanceSubprocess() {
        MultipleInstanceSubprocess multipleInstanceSubprocess = new MultipleInstanceSubprocess();
        MultipleInstanceSubprocess multipleInstanceSubprocess2 = new MultipleInstanceSubprocess();

        when(firstView.getDefinition()).thenReturn(multipleInstanceSubprocess);
        when(lastView.getDefinition()).thenReturn(multipleInstanceSubprocess2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractEventSubprocess() {
        EventSubprocess eventSubprocess = new EventSubprocess();
        EventSubprocess eventSubprocess2 = new EventSubprocess();

        when(firstView.getDefinition()).thenReturn(eventSubprocess);
        when(lastView.getDefinition()).thenReturn(eventSubprocess2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractEmbeddedSubprocess() {
        EmbeddedSubprocess embeddedSubprocess = new EmbeddedSubprocess();
        EmbeddedSubprocess embeddedSubprocess2 = new EmbeddedSubprocess();

        when(firstView.getDefinition()).thenReturn(embeddedSubprocess);
        when(lastView.getDefinition()).thenReturn(embeddedSubprocess2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void textExtractBPMNDiagram() {
        BPMNDiagramImpl diagram = new BPMNDiagramImpl();
        BPMNDiagramImpl diagram2 = new BPMNDiagramImpl();

        when(firstView.getDefinition()).thenReturn(diagram);
        when(lastView.getDefinition()).thenReturn(diagram2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void textExtractAdhocSubprocess() {
        AdHocSubprocess adHocSubprocess = new AdHocSubprocess();
        AdHocSubprocess adHocSubprocess2 = new AdHocSubprocess();

        when(firstView.getDefinition()).thenReturn(adHocSubprocess);
        when(lastView.getDefinition()).thenReturn(adHocSubprocess2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains("MyType"));
        assertTrue(dataTypeCache.allDataTypes.contains("com.myspace.Person"));
    }

    @Test
    public void testExtractDataObject() {
        DataObject object = new DataObject();
        DataObject object2 = new DataObject();

        when(firstView.getDefinition()).thenReturn(object);
        when(lastView.getDefinition()).thenReturn(object2);

        dataTypeCache.extractFromItem(firstView);
        dataTypeCache.extractFromItem(lastView);

        assertTrue(dataTypeCache.allDataTypes.contains(object.getType().getValue().getType()));
        assertTrue(dataTypeCache.allDataTypes.contains(object2.getType().getValue().getType()));
    }
}
