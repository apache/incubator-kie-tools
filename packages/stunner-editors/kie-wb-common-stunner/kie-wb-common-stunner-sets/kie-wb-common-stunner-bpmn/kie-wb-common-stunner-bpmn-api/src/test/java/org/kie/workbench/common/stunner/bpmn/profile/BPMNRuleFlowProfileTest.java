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


package org.kie.workbench.common.stunner.bpmn.profile;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class BPMNRuleFlowProfileTest {

    @Test
    public void testProfile() {
        BPMNRuleFlowProfile profile = new BPMNRuleFlowProfile();
        assertEquals(BPMNRuleFlowProfile.ID, profile.getProfileId());
        assertTrue(profile.definitionAllowedFilter().test(getDefinitionId(NoneTask.class)));
        assertTrue(profile.definitionAllowedFilter().test(getDefinitionId(ScriptTask.class)));
        assertTrue(profile.definitionAllowedFilter().test(getDefinitionId(BusinessRuleTask.class)));
        assertTrue(profile.definitionAllowedFilter().test(getDefinitionId(ReusableSubprocess.class)));
        assertTrue(profile.definitionAllowedFilter().test(getDefinitionId(StartNoneEvent.class)));
        assertTrue(profile.definitionAllowedFilter().test(getDefinitionId(EndNoneEvent.class)));
        assertTrue(profile.definitionAllowedFilter().test(getDefinitionId(EndTerminateEvent.class)));
        assertTrue(profile.definitionAllowedFilter().test(getDefinitionId(ParallelGateway.class)));
        assertTrue(profile.definitionAllowedFilter().test(getDefinitionId(ExclusiveGateway.class)));
        assertFalse(profile.definitionAllowedFilter().test(getDefinitionId(UserTask.class)));
        assertFalse(profile.definitionAllowedFilter().test(getDefinitionId(Lane.class)));
        assertFalse(profile.definitionAllowedFilter().test(getDefinitionId(EmbeddedSubprocess.class)));
        assertFalse(profile.definitionAllowedFilter().test(getDefinitionId(IntermediateCompensationEvent.class)));
        assertFalse(profile.definitionAllowedFilter().test(getDefinitionId(IntermediateTimerEvent.class)));
    }
}
