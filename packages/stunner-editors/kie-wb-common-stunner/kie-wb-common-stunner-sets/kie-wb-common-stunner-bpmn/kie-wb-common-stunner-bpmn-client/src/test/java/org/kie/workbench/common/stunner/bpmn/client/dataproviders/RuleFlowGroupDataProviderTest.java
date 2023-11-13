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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RuleFlowGroupDataEvent;
import org.kie.workbench.common.stunner.forms.client.session.StunnerFormsHandler;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuleFlowGroupDataProviderTest {

    @Mock
    private StunnerFormsHandler formsHandler;

    private RuleFlowGroupDataProvider tested;

    @Before
    public void setup() {
        tested = new RuleFlowGroupDataProvider(formsHandler);
    }

    @Test
    public void testOnRuleFlowGroupDataChanged() {
        RuleFlowGroup group1 = new RuleFlowGroup("g1");
        RuleFlowGroup group2 = new RuleFlowGroup("g2");
        RuleFlowGroupDataEvent event = mock(RuleFlowGroupDataEvent.class);
        when(event.getGroups()).thenReturn(new RuleFlowGroup[]{group1, group2});
        tested.onRuleFlowGroupDataChanged(event);
        verify(formsHandler, times(1)).refreshCurrentSessionForms(eq(BPMNDefinitionSet.class));
        List<RuleFlowGroup> values = tested.getRuleFlowGroupNames();
        assertNotNull(values);
        assertEquals(2, values.size());
        assertEquals("g1", values.get(0).getName());
        assertEquals("g2", values.get(1).getName());
    }

    @Test
    public void testOnRuleFlowGroupDataNotChanged() {
        RuleFlowGroup group1 = new RuleFlowGroup("g1");
        RuleFlowGroup group2 = new RuleFlowGroup("g2");
        tested.groups.add(group1);
        tested.groups.add(group2);
        RuleFlowGroupDataEvent event = mock(RuleFlowGroupDataEvent.class);
        when(event.getGroups()).thenReturn(new RuleFlowGroup[]{group1, group2});
        tested.onRuleFlowGroupDataChanged(event);
        verify(formsHandler, never()).refreshCurrentSessionForms(any(Class.class));
    }
}
