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

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RuleFlowGroupDataEvent;
import org.kie.workbench.common.stunner.forms.client.session.StunnerFormsHandler;

import static java.util.Arrays.stream;

@ApplicationScoped
public class RuleFlowGroupDataProvider {

    private final StunnerFormsHandler formsHandler;
    final List<RuleFlowGroup> groups;

    // CDI proxy.
    public RuleFlowGroupDataProvider() {
        this(null);
    }

    @Inject
    public RuleFlowGroupDataProvider(final StunnerFormsHandler formsHandler) {
        this.formsHandler = formsHandler;
        this.groups = new LinkedList<>();
    }

    public List<RuleFlowGroup> getRuleFlowGroupNames() {
        return groups;
    }

    void onRuleFlowGroupDataChanged(final @Observes RuleFlowGroupDataEvent event) {
        setRuleFlowGroupNames(toList(event.getGroups()));
    }

    private void setRuleFlowGroupNames(final List<RuleFlowGroup> groups) {
        if (!this.groups.equals(groups)) {
            this.groups.clear();
            this.groups.addAll(groups);
            formsHandler.refreshCurrentSessionForms(BPMNDefinitionSet.class);
        }
    }

    private static List<RuleFlowGroup> toList(final RuleFlowGroup[] s) {
        return stream(s).collect(Collectors.toList());
    }
}
