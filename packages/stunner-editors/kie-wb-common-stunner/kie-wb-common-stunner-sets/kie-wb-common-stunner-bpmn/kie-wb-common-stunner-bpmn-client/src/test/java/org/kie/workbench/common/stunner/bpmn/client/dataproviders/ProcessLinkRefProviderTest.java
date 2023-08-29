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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkRef;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProcessLinkRefProviderTest
        extends AbstractProcessFilteredNodeProviderBaseTest {

    private static final int INTERMEDIATE_LINK_EVENT_CATCHING_COUNT = 10;

    private static final String INTERMEDIATE_LINK_EVENT_CATCHING_PREFIX = "INTERMEDIATE_LINK_EVENT_CATCHING_PREFIX";

    private static final int INTERMEDIATE_LINK_EVENT_THROWING_COUNT = 10;

    private static final String INTERMEDIATE_LINK_EVENT_THROWING_PREFIX = "INTERMEDIATE_LINK_EVENT_THROWING_PREFIX";

    @Override
    protected SelectorDataProvider createProvider() {
        return new ProcessLinkRefProvider(sessionManager);
    }

    @Override
    protected List<Element> mockModes() {
        List<Element> nodes = new ArrayList<>();

        nodes.addAll(mockElements(INTERMEDIATE_LINK_EVENT_CATCHING_COUNT,
                                  index -> mockIntermediateLinkEventCatchingNode(INTERMEDIATE_LINK_EVENT_CATCHING_PREFIX + index)));
        nodes.addAll(mockElements(INTERMEDIATE_LINK_EVENT_THROWING_COUNT,
                                  index -> mockIntermediateLinkEventThrowingNode(INTERMEDIATE_LINK_EVENT_THROWING_PREFIX + index)));
        return nodes;
    }

    @Override
    protected void verifyValues(Map values) {
        verifyValues(INTERMEDIATE_LINK_EVENT_CATCHING_COUNT,
                     INTERMEDIATE_LINK_EVENT_CATCHING_PREFIX,
                     values);
        verifyValues(INTERMEDIATE_LINK_EVENT_THROWING_COUNT,
                     INTERMEDIATE_LINK_EVENT_THROWING_PREFIX,
                     values);
    }

    private Node mockIntermediateLinkEventCatchingNode(String linkRefValue) {
        IntermediateLinkEventCatching event = new IntermediateLinkEventCatching();
        event.setExecutionSet(new LinkEventExecutionSet(new LinkRef(linkRefValue)));
        return mockNode(event);
    }

    private Node mockIntermediateLinkEventThrowingNode(String linkRefValue) {
        IntermediateLinkEventThrowing event = new IntermediateLinkEventThrowing();
        event.setExecutionSet(new LinkEventExecutionSet(new LinkRef(linkRefValue)));
        return mockNode(event);
    }
}
