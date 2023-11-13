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

import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.InterruptingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.core.graph.Element;

public class ProcessMessageRefProviderTest
        extends AbstractProcessFilteredNodeProviderBaseTest {

    private static final int START_MESSAGE_EVENT_COUNT = 10;

    private static final String START_MESSAGE_EVENT_PREFIX = "START_MESSAGE_EVENT_PREFIX";

    private static final int INTERMEDIATE_MESSAGE_EVENT_CATCHING_COUNT = 10;

    private static final String INTERMEDIATE_MESSAGE_EVENT_CATCHING_PREFIX = "INTERMEDIATE_MESSAGE_EVENT_CATCHING_PREFIX";

    private static final int END_MESSAGE_EVENT_COUNT = 10;

    private static final String END_MESSAGE_EVENT_PREFIX = "END_MESSAGE_EVENT_PREFIX";

    @Override
    protected SelectorDataProvider createProvider() {
        return new ProcessMessageRefProvider(sessionManager);
    }

    @Override
    protected List<Element> mockModes() {
        List<Element> nodes = new ArrayList<>();
        nodes.addAll(mockElements(START_MESSAGE_EVENT_COUNT,
                                  index -> mockStartMessageEventNode(START_MESSAGE_EVENT_PREFIX + index)));
        nodes.addAll(mockElements(INTERMEDIATE_MESSAGE_EVENT_CATCHING_COUNT,
                                  index -> mockIntermediateMessageEventCatchingNode(INTERMEDIATE_MESSAGE_EVENT_CATCHING_PREFIX + index)));
        nodes.addAll(mockElements(END_MESSAGE_EVENT_COUNT,
                                  index -> mockEndMessageEventNode(END_MESSAGE_EVENT_PREFIX + index)));
        return nodes;
    }

    @Override
    protected void verifyValues(Map values) {
        verifyValues(START_MESSAGE_EVENT_COUNT,
                     START_MESSAGE_EVENT_PREFIX,
                     values);
        verifyValues(INTERMEDIATE_MESSAGE_EVENT_CATCHING_COUNT,
                     INTERMEDIATE_MESSAGE_EVENT_CATCHING_PREFIX,
                     values);
        verifyValues(END_MESSAGE_EVENT_COUNT,
                     END_MESSAGE_EVENT_PREFIX,
                     values);
    }

    private Element mockStartMessageEventNode(String messageRefValue) {
        StartMessageEvent event = new StartMessageEvent();
        event.setExecutionSet(new InterruptingMessageEventExecutionSet(new IsInterrupting(true),
                                                                       new SLADueDate(),
                                                                       new MessageRef(messageRefValue, "")));
        return mockNode(event);
    }

    private Element mockIntermediateMessageEventCatchingNode(String messageRefValue) {
        IntermediateMessageEventCatching event = new IntermediateMessageEventCatching();
        event.setExecutionSet(new CancellingMessageEventExecutionSet(new CancelActivity(true),
                                                                     new SLADueDate(),
                                                                     new MessageRef(messageRefValue, "")));
        return mockNode(event);
    }

    private Element mockEndMessageEventNode(String messageRefValue) {
        EndMessageEvent event = new EndMessageEvent();
        event.setExecutionSet(new MessageEventExecutionSet(new MessageRef(messageRefValue, "")));
        return mockNode(event);
    }
}
