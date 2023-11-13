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

import java.util.function.Predicate;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.profile.BindableDomainProfile;
import org.kie.workbench.common.stunner.core.profile.DomainProfile;

@ApplicationScoped
@BPMN
public class BPMNRuleFlowProfile implements DomainProfile {

    static final String ID = BPMNRuleFlowProfile.class.getName();
    private static final BindableDomainProfile domainProfile = BindableDomainProfile.build(NoneTask.class,
                                                                                           ScriptTask.class,
                                                                                           BusinessRuleTask.class,
                                                                                           ReusableSubprocess.class,
                                                                                           StartNoneEvent.class,
                                                                                           EndNoneEvent.class,
                                                                                           EndTerminateEvent.class,
                                                                                           ParallelGateway.class,
                                                                                           ExclusiveGateway.class,
                                                                                           GenericServiceTask.class);

    @Override
    public String getProfileId() {
        return ID;
    }

    @Override
    public String getName() {
        return "RuleFlow";
    }

    @Override
    public Predicate<String> definitionAllowedFilter() {
        return domainProfile.definitionAllowedFilter();
    }
}
