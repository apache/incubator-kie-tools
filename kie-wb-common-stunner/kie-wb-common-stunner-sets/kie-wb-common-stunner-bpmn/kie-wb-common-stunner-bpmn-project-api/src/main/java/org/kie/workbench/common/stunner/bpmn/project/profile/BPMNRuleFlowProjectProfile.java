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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

import org.kie.workbench.common.profile.api.preferences.Profile;
import org.kie.workbench.common.stunner.bpmn.profile.BPMNRuleFlowProfile;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.project.profile.ProjectProfile;

/**
 * Associates the Stunner profile to a workbench profile (preferences).
 */
@ApplicationScoped
@BPMN
@Specializes
public class BPMNRuleFlowProjectProfile
        extends BPMNRuleFlowProfile
        implements ProjectProfile {

    @Override
    public String getProjectProfileName() {
        return Profile.PLANNER_AND_RULES.getName();
    }
}
