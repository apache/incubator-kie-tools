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
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.InterruptingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProcessSignalRefProviderTest
        extends AbstractProcessFilteredNodeProviderBaseTest {

    private static final int START_SIGNAL_EVENT_COUNT = 10;

    private static final String START_SIGNAL_EVENT_PREFIX = "START_SIGNAL_EVENT_PREFIX";

    private static final int INTERMEDIATE_SIGNAL_EVENT_CATCHING_COUNT = 10;

    private static final String INTERMEDIATE_SIGNAL_EVENT_CATCHING_PREFIX = "INTERMEDIATE_SIGNAL_EVENT_CATCHING_PREFIX";

    private static final int INTERMEDIATE_SIGNAL_EVENT_THROWING_COUNT = 10;

    private static final String INTERMEDIATE_SIGNAL_EVENT_THROWING_PREFIX = "INTERMEDIATE_SIGNAL_EVENT_THROWING_PREFIX";

    private static final int END_SIGNAL_EVENT_COUNT = 10;

    private static final String END_SIGNAL_EVENT_PREFIX = "END_ERROR_EVENT_PREFIX";

    @Override
    protected SelectorDataProvider createProvider() {
        return new ProcessSignalRefProvider(sessionManager);
    }

    @Override
    protected List<Element> mockModes() {
        List<Element> nodes = new ArrayList<>();

        nodes.addAll(mockElements(START_SIGNAL_EVENT_COUNT,
                                  index -> mockStartSignalEventNode(START_SIGNAL_EVENT_PREFIX + index)));
        nodes.addAll(mockElements(INTERMEDIATE_SIGNAL_EVENT_CATCHING_COUNT,
                                  index -> mockIntermediateSignalEventCatchingNode(INTERMEDIATE_SIGNAL_EVENT_CATCHING_PREFIX + index)));
        nodes.addAll(mockElements(INTERMEDIATE_SIGNAL_EVENT_THROWING_COUNT,
                                  index -> mockIntermediateSignalEventThrowingNode(INTERMEDIATE_SIGNAL_EVENT_THROWING_PREFIX + index)));
        nodes.addAll(mockElements(END_SIGNAL_EVENT_COUNT,
                                  index -> mockEndSignalEventNode(END_SIGNAL_EVENT_PREFIX + index)));
        return nodes;
    }

    @Override
    protected void verifyValues(Map values) {
        verifyValues(START_SIGNAL_EVENT_COUNT,
                     START_SIGNAL_EVENT_PREFIX,
                     values);
        verifyValues(INTERMEDIATE_SIGNAL_EVENT_CATCHING_COUNT,
                     INTERMEDIATE_SIGNAL_EVENT_CATCHING_PREFIX,
                     values);
        verifyValues(INTERMEDIATE_SIGNAL_EVENT_THROWING_COUNT,
                     INTERMEDIATE_SIGNAL_EVENT_THROWING_PREFIX,
                     values);
        verifyValues(END_SIGNAL_EVENT_COUNT,
                     END_SIGNAL_EVENT_PREFIX,
                     values);
    }

    private Node mockStartSignalEventNode(String signalRefValue) {
        StartSignalEvent event = new StartSignalEvent();
        event.setExecutionSet(new InterruptingSignalEventExecutionSet(new IsInterrupting(true),
                                                                      new SLADueDate(),
                                                                      new SignalRef(signalRefValue)));
        return mockNode(event);
    }

    private Node mockIntermediateSignalEventCatchingNode(String signalRefValue) {
        IntermediateSignalEventCatching event = new IntermediateSignalEventCatching();
        event.setExecutionSet(new CancellingSignalEventExecutionSet(new CancelActivity(true),
                                                                    new SLADueDate(),
                                                                    new SignalRef(signalRefValue)));
        return mockNode(event);
    }

    private Node mockIntermediateSignalEventThrowingNode(String signalRefValue) {
        IntermediateSignalEventThrowing event = new IntermediateSignalEventThrowing();
        event.setExecutionSet(new ScopedSignalEventExecutionSet(new SignalRef(signalRefValue),
                                                                new SignalScope()));
        return mockNode(event);
    }

    private Node mockEndSignalEventNode(String signalRefValue) {
        EndSignalEvent event = new EndSignalEvent();
        event.setExecutionSet(new ScopedSignalEventExecutionSet(new SignalRef(signalRefValue),
                                                                new SignalScope()));
        return mockNode(event);
    }
}
