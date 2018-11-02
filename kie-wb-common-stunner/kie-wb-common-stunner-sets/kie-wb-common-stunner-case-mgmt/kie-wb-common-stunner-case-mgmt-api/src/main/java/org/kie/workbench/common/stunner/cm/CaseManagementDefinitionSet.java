/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.cm.factory.CaseManagementGraphFactory;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.rule.annotation.Occurrences;

@ApplicationScoped
@Bindable
@DefinitionSet(
        graphFactory = CaseManagementGraphFactory.class,
        qualifier = CaseManagementEditor.class,
        definitions = {
                CaseManagementDiagram.class,
                Lane.class,
                NoneTask.class,
                UserTask.class,
                ScriptTask.class,
                BusinessRuleTask.class,
                StartNoneEvent.class,
                StartSignalEvent.class,
                StartTimerEvent.class,
                EndNoneEvent.class,
                EndTerminateEvent.class,
                IntermediateTimerEvent.class,
                ParallelGateway.class,
                ExclusiveGateway.class,
                AdHocSubprocess.class,
                EmbeddedSubprocess.class,
                ReusableSubprocess.class,
                SequenceFlow.class
        },
        builder = CaseManagementDefinitionSet.CaseManagementDefinitionSetBuilder.class
)
@CanContain(roles = {"diagram"})
@Occurrences(role = "diagram", max = 1)
@Occurrences(role = "Startevents_all", min = 0)
@Occurrences(role = "Endevents_all", min = 0)
public class CaseManagementDefinitionSet {

    @Description
    public static final transient String description = "Case Management";

    public CaseManagementDefinitionSet() {
    }

    public String getDescription() {
        return description;
    }

    @NonPortable
    public static class CaseManagementDefinitionSetBuilder implements Builder<CaseManagementDefinitionSet> {

        @Override
        public CaseManagementDefinitionSet build() {
            return new CaseManagementDefinitionSet();
        }
    }
}
