/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.backend.service;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RequestRuleFlowGroupDataEvent;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RuleFlowGroupDataEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuleFlowGroupDataServiceTest {

    @Mock
    private RuleFlowGroupQueryService queryService;

    @Mock
    private Event<RuleFlowGroupDataEvent> dataChangedEvent;

    private RuleFlowGroupDataService tested;

    @Before
    public void setUp() {
        RuleFlowGroup group1 = new RuleFlowGroup("g1");
        RuleFlowGroup group2 = new RuleFlowGroup("g2");
        when(queryService.getRuleFlowGroupNames()).thenReturn(Arrays.asList(group1, group2));
        tested = new RuleFlowGroupDataService(queryService, dataChangedEvent);
    }

    @Test
    public void testGetRuleFlowGroupNames() {
        List<RuleFlowGroup> names = tested.getRuleFlowGroupNames();
        assertRightRuleFlowGroupNames(names);
    }

    @Test
    public void testFireData() {
        tested.fireData();
        ArgumentCaptor<RuleFlowGroupDataEvent> ec = ArgumentCaptor.forClass(RuleFlowGroupDataEvent.class);
        verify(dataChangedEvent, times(1)).fire(ec.capture());
        RuleFlowGroupDataEvent event = ec.getValue();
        assertRightRuleFlowGroups(event.getGroups());
    }


    @Test
    public void testRuleFlowGroupDataService() {
        RuleFlowGroupDataService tested = spy(new RuleFlowGroupDataService(queryService, dataChangedEvent));
        tested.onRequestRuleFlowGroupDataEvent(new RequestRuleFlowGroupDataEvent());
        verify(tested).fireData();
    }

    private static void assertRightRuleFlowGroups(RuleFlowGroup[] names) {
        assertNotNull(names);
        assertEquals(2, names.length);
        assertEquals("g1", names[0].getName());
        assertEquals("g2", names[1].getName());
    }

    private static void assertRightRuleFlowGroupNames(List<RuleFlowGroup> names) {
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains(new RuleFlowGroup("g1")));
        assertTrue(names.contains(new RuleFlowGroup("g2")));
    }
}
