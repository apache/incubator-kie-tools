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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RequestRuleFlowGroupDataEvent;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class RuleFlowGroupFormProviderTest {

    @Mock
    private RuleFlowGroupDataProvider dataProvider;

    @Mock
    private EventSourceMock<RequestRuleFlowGroupDataEvent> requestRuleFlowGroupDataEvent;

    private RuleFlowGroupFormProvider tested;

    @Before
    public void setup() {
        tested = spy(new RuleFlowGroupFormProvider());
        tested.dataProvider = dataProvider;
        tested.requestRuleFlowGroupDataEvent = requestRuleFlowGroupDataEvent;
    }

    @Test
    public void testGetProviderName() {
        assertEquals(tested.getClass().getSimpleName(), tested.getProviderName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetSelectorData() {
        RuleFlowGroup group1 = new RuleFlowGroup("g1");
        group1.setPathUri("default://main@MySpace/Project1/src/main/resources/com/myspace/project1/RulesFile.rdrl");
        RuleFlowGroup group2 = new RuleFlowGroup("g2");
        group2.setPathUri("default://main@MySpace/Project1/src/main/resources/com/myspace/RulesFile2.rdrl".replace('/', '\\'));
        RuleFlowGroup group3 = new RuleFlowGroup("g1");
        group3.setPathUri("default://main@MySpace/Project2/src/main/resources/com/myspace/project1/RulesFile.rdrl");
        List<RuleFlowGroup> groups = Arrays.asList(group1, group2, group3);
        when(dataProvider.getRuleFlowGroupNames()).thenReturn(groups);
        FormRenderingContext context = mock(FormRenderingContext.class);
        SelectorData data = tested.getSelectorData(context);
        Map<String, String> values = data.getValues();
        assertNotNull(values);
        assertEquals(2, values.size());
        assertEquals("g2 [Project1]", values.get(group2.getName()));
        assertEquals("g1 [Project1, Project2]", values.get(group1.getName()));
        verify(requestRuleFlowGroupDataEvent, times(1)).fire(any(RequestRuleFlowGroupDataEvent.class));
    }

    @Test
    public void testGroupWithSameProject() {
        RuleFlowGroup group1 = new RuleFlowGroup("g1");
        group1.setPathUri("default://main@MySpace/Project1/src/main/resources/com/myspace/project1/RulesFile.rdrl");
        RuleFlowGroup group2 = new RuleFlowGroup("g1");
        group2.setPathUri("default://main@MySpace/Project1/src/main/resources/com/myspace/RulesFile2.rdrl".replace('/', '\\'));
        RuleFlowGroup group3 = new RuleFlowGroup("g1");
        group3.setPathUri("default://main@MySpace/Project2/src/main/resources/com/myspace/project1/RulesFile.rdrl");
        List<RuleFlowGroup> groups = Arrays.asList(group1, group2, group3);
        when(dataProvider.getRuleFlowGroupNames()).thenReturn(groups);
        FormRenderingContext context = mock(FormRenderingContext.class);
        SelectorData<String> data = tested.getSelectorData(context);
        Map<String, String> values = data.getValues();
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals("g1 [Project1, Project2]", values.get(group1.getName()));
        verify(requestRuleFlowGroupDataEvent, times(1)).fire(any(RequestRuleFlowGroupDataEvent.class));
    }

    @Test
    public void serviceInitialized() {
        tested.populateData();
        verify(requestRuleFlowGroupDataEvent, times(1)).fire(any(RequestRuleFlowGroupDataEvent.class));
        assertTrue(tested.requestRuleFlowGroupDataEvent.equals(tested.getRequestRuleFlowGroupDataEventEventSingleton()));
    }
}
