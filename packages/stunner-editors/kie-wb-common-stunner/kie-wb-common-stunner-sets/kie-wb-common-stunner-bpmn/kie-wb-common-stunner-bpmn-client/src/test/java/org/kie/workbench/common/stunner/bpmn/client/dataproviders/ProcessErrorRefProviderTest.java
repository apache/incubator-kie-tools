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
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.CancellingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.InterruptingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;

public class ProcessErrorRefProviderTest
        extends AbstractProcessFilteredNodeProviderBaseTest {

    private static final int START_ERROR_EVENT_COUNT = 10;

    private static final String START_ERROR_EVENT_PREFIX = "START_ERROR_EVENT_PREFIX";

    private static final int INTERMEDIATE_ERROR_EVENT_CATCHING_COUNT = 10;

    private static final String INTERMEDIATE_ERROR_EVENT_CATCHING_PREFIX = "INTERMEDIATE_ERROR_EVENT_CATCHING_PREFIX";

    private static final int END_ERROR_EVENT_COUNT = 10;

    private static final String END_ERROR_EVENT_PREFIX = "END_ERROR_EVENT_PREFIX";

    @Override
    protected SelectorDataProvider createProvider() {
        return new ProcessErrorRefProvider(sessionManager);
    }

    @Override
    protected List<Element> mockModes() {
        List<Element> nodes = new ArrayList<>();
        nodes.addAll(mockElements(START_ERROR_EVENT_COUNT,
                                  index -> mockStartErrorEventNode(START_ERROR_EVENT_PREFIX + index)));
        nodes.addAll(mockElements(INTERMEDIATE_ERROR_EVENT_CATCHING_COUNT,
                                  index -> mockIntermediateErrorEventCatchingNode(INTERMEDIATE_ERROR_EVENT_CATCHING_PREFIX + index)));
        nodes.addAll(mockElements(END_ERROR_EVENT_COUNT,
                                  index -> mockEndErrorEventNode(END_ERROR_EVENT_PREFIX + index)));
        return nodes;
    }

    @Override
    protected void verifyValues(Map values) {
        verifyValues(START_ERROR_EVENT_COUNT,
                     START_ERROR_EVENT_PREFIX,
                     values);
        verifyValues(INTERMEDIATE_ERROR_EVENT_CATCHING_COUNT,
                     INTERMEDIATE_ERROR_EVENT_CATCHING_PREFIX,
                     values);
        verifyValues(END_ERROR_EVENT_COUNT,
                     END_ERROR_EVENT_PREFIX,
                     values);
    }

    private Element mockStartErrorEventNode(String errorRefValue) {
        StartErrorEvent event = new StartErrorEvent();
        event.setExecutionSet(new InterruptingErrorEventExecutionSet(new IsInterrupting(true),
                                                                     new SLADueDate(),
                                                                     new ErrorRef(errorRefValue)));
        return mockNode(event);
    }

    private Node mockIntermediateErrorEventCatchingNode(String errorRefValue) {
        IntermediateErrorEventCatching event = new IntermediateErrorEventCatching();
        event.setExecutionSet(new CancellingErrorEventExecutionSet(new CancelActivity(true),
                                                                   new SLADueDate(),
                                                                   new ErrorRef(errorRefValue)));
        return mockNode(event);
    }

    private Node mockEndErrorEventNode(String errorRefValue) {
        EndErrorEvent event = new EndErrorEvent();
        event.setExecutionSet(new ErrorEventExecutionSet(new ErrorRef(errorRefValue)));
        return mockNode(event);
    }
}
