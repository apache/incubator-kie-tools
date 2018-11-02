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

package org.kie.workbench.common.stunner.bpmn;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactory;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.SvgNodeId;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.rule.annotation.Occurrences;

@ApplicationScoped
@Bindable
@DefinitionSet(
        graphFactory = BPMNGraphFactory.class,
        qualifier = BPMN.class,
        definitions = {
                BPMNDiagramImpl.class,
                Lane.class,
                NoneTask.class,
                UserTask.class,
                ScriptTask.class,
                BusinessRuleTask.class,
                StartNoneEvent.class,
                StartMessageEvent.class,
                StartSignalEvent.class,
                StartTimerEvent.class,
                StartErrorEvent.class,
                StartConditionalEvent.class,
                StartEscalationEvent.class,
                StartCompensationEvent.class,
                EndNoneEvent.class,
                EndSignalEvent.class,
                EndMessageEvent.class,
                EndTerminateEvent.class,
                EndErrorEvent.class,
                EndEscalationEvent.class,
                EndCompensationEvent.class,
                IntermediateTimerEvent.class,
                IntermediateMessageEventCatching.class,
                IntermediateSignalEventCatching.class,
                IntermediateSignalEventThrowing.class,
                IntermediateErrorEventCatching.class,
                IntermediateEscalationEvent.class,
                IntermediateCompensationEvent.class,
                IntermediateMessageEventThrowing.class,
                IntermediateConditionalEvent.class,
                IntermediateEscalationEventThrowing.class,
                IntermediateCompensationEventThrowing.class,
                ParallelGateway.class,
                ExclusiveGateway.class,
                InclusiveGateway.class,
                ReusableSubprocess.class,
                EmbeddedSubprocess.class,
                EventSubprocess.class,
                AdHocSubprocess.class,
                MultipleInstanceSubprocess.class,
                SequenceFlow.class,
                Association.class
        },
        builder = BPMNDefinitionSet.BPMNDefinitionSetBuilder.class
)
@CanContain(roles = {"diagram"})
@Occurrences(role = "diagram", max = 1)
@Occurrences(role = "Startevents_all", min = 1)
@Occurrences(role = "Endevents_all", min = 1)
public class BPMNDefinitionSet {

    @SvgNodeId
    public static final String SVG_BPMN_ID = "bpmn2nodeid";

    public BPMNDefinitionSet() {
    }

    @NonPortable
    public static class BPMNDefinitionSetBuilder implements Builder<BPMNDefinitionSet> {

        @Override
        public BPMNDefinitionSet build() {
            return new BPMNDefinitionSet();
        }
    }
}
