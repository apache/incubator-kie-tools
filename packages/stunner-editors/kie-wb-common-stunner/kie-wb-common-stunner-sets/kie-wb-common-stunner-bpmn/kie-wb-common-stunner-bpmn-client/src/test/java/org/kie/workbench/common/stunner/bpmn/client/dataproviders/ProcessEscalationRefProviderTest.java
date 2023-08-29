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


package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.CancellingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.InterruptingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProcessEscalationRefProviderTest extends AbstractProcessFilteredNodeProviderBaseTest {

    private static final int START_ESCALATION_EVENT_COUNT = 10;

    private static final String START_ESCALATION_EVENT_PREFIX = "START_ESCALATION_EVENT_PREFIX";

    private static final int INTERMEDIATE_ESCALATION_EVENT_CATCHING_COUNT = 10;

    private static final String INTERMEDIATE_ESCALATION_EVENT_CATCHING_PREFIX = "INTERMEDIATE_ESCALATION_EVENT_CATCHING_PREFIX";

    private static final int INTERMEDIATE_ESCALATION_EVENT_THROWING_COUNT = 10;

    private static final String INTERMEDIATE_ESCALATION_EVENT_THROWING_PREFIX = "INTERMEDIATE_ESCALATION_EVENT_THROWING_PREFIX";

    private static final int END_ESCALATION_EVENT_COUNT = 10;

    private static final String END_ESCALATION_EVENT_PREFIX = "END_ERROR_EVENT_PREFIX";

    @Override
    protected SelectorDataProvider createProvider() {
        return new ProcessEscalationRefProvider(sessionManager);
    }

    @Override
    protected List<Element> mockModes() {
        List<Element> nodes = new ArrayList<>();

        nodes.addAll(mockElements(START_ESCALATION_EVENT_COUNT,
                                  index -> mockStartEscalationEventNode(START_ESCALATION_EVENT_PREFIX + index)));
        nodes.addAll(mockElements(INTERMEDIATE_ESCALATION_EVENT_CATCHING_COUNT,
                                  index -> mockIntermediateEscalationEventCatchingNode(INTERMEDIATE_ESCALATION_EVENT_CATCHING_PREFIX + index)));
        nodes.addAll(mockElements(INTERMEDIATE_ESCALATION_EVENT_THROWING_COUNT,
                                  index -> mockIntermediateEscalationEventThrowingNode(INTERMEDIATE_ESCALATION_EVENT_THROWING_PREFIX + index)));
        nodes.addAll(mockElements(END_ESCALATION_EVENT_COUNT,
                                  index -> mockEndEscalationEventNode(END_ESCALATION_EVENT_PREFIX + index)));
        return nodes;
    }

    @Override
    protected void verifyValues(Map values) {
        verifyValues(START_ESCALATION_EVENT_COUNT,
                     START_ESCALATION_EVENT_PREFIX,
                     values);
        verifyValues(INTERMEDIATE_ESCALATION_EVENT_CATCHING_COUNT,
                     INTERMEDIATE_ESCALATION_EVENT_CATCHING_PREFIX,
                     values);
        verifyValues(INTERMEDIATE_ESCALATION_EVENT_THROWING_COUNT,
                     INTERMEDIATE_ESCALATION_EVENT_THROWING_PREFIX,
                     values);
        verifyValues(END_ESCALATION_EVENT_COUNT,
                     END_ESCALATION_EVENT_PREFIX,
                     values);
    }

    private Node mockStartEscalationEventNode(String escalationRefValue) {
        StartEscalationEvent event = new StartEscalationEvent();
        event.setExecutionSet(new InterruptingEscalationEventExecutionSet(new IsInterrupting(true),
                                                                          new SLADueDate(),
                                                                          new EscalationRef(escalationRefValue)));
        return mockNode(event);
    }

    private Node mockIntermediateEscalationEventCatchingNode(String escalationRefValue) {
        IntermediateEscalationEvent event = new IntermediateEscalationEvent();
        event.setExecutionSet(new CancellingEscalationEventExecutionSet(new CancelActivity(true),
                                                                        new SLADueDate(),
                                                                        new EscalationRef(escalationRefValue)));
        return mockNode(event);
    }

    private Node mockIntermediateEscalationEventThrowingNode(String escalationRefValue) {
        IntermediateEscalationEventThrowing event = new IntermediateEscalationEventThrowing();
        event.setExecutionSet(new EscalationEventExecutionSet(new EscalationRef(escalationRefValue)));
        return mockNode(event);
    }

    private Node mockEndEscalationEventNode(String escalationRefValue) {
        EndEscalationEvent event = new EndEscalationEvent();
        event.setExecutionSet(new EscalationEventExecutionSet(new EscalationRef(escalationRefValue)));
        return mockNode(event);
    }
}