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
package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class EventFilterProviderFactoryTest {

    @Mock
    private SessionManager sessionManager;

    @Mock
    private GraphUtils graphUtils;

    private StartEventFilterProviderFactory factory;

    @Test
    public void getFilterProvidersTest() {
        factory = new StartEventFilterProviderFactory(sessionManager, graphUtils);
        List<StartEventFilterProvider> filterProviders = factory.getFilterProviders();

        List<Class<?>> definitionTypes = new ArrayList<>();
        filterProviders.forEach(startEventFilterProvider -> definitionTypes.add(startEventFilterProvider.getDefinitionType()));

        assertEquals(4, filterProviders.size());
        assertTrue(definitionTypes.contains(StartSignalEvent.class));
        assertTrue(definitionTypes.contains(StartMessageEvent.class));
        assertTrue(definitionTypes.contains(StartTimerEvent.class));
        assertTrue(definitionTypes.contains(StartErrorEvent.class));
    }
}