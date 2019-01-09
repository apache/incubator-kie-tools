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

package org.kie.workbench.common.stunner.bpmn.project.profile;

import org.junit.Test;
import org.kie.workbench.common.profile.api.preferences.Profile;
import org.kie.workbench.common.stunner.bpmn.profile.BPMNRuleFlowProfile;

import static org.junit.Assert.assertEquals;

public class BPMNRuleFlowProjectProfileTest {

    @Test
    public void testProfile() {
        BPMNRuleFlowProjectProfile profile = new BPMNRuleFlowProjectProfile();
        assertEquals(new BPMNRuleFlowProfile().getProfileId(), profile.getProfileId());
        assertEquals(Profile.PLANNER_AND_RULES.getName(), profile.getProjectProfileName());
    }
}

