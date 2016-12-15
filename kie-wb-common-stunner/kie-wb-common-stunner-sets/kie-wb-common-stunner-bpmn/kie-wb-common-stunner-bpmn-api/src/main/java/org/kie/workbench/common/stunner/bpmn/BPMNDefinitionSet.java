/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.definition.*;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactory;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.ShapeSet;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.rule.annotation.Occurrences;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Bindable
@DefinitionSet(
        graphFactory = BPMNGraphFactory.class,
        definitions = {
                BPMNDiagram.class,
                Lane.class,
                NoneTask.class,
                UserTask.class,
                ScriptTask.class,
                BusinessRuleTask.class,
                StartNoneEvent.class,
                EndNoneEvent.class,
                EndTerminateEvent.class,
                // TODO: Removed for M1 ( no form properties available for it yet ) - IntermediateTimerEvent.class,
                ParallelGateway.class,
                ExclusiveDatabasedGateway.class,
                ReusableSubprocess.class,
                SequenceFlow.class

        },
        builder = BPMNDefinitionSet.BPMNDefinitionSetBuilder.class
)
@CanContain( roles = { "diagram" } )
@Occurrences(
        role = "Startevents_all",
        min = 0
)
@Occurrences(
        role = "Endevents_all",
        min = 0
)
@ShapeSet
public class BPMNDefinitionSet {

    @Description
    public static final transient String description = "BPMN2";

    @NonPortable
    public static class BPMNDefinitionSetBuilder implements Builder<BPMNDefinitionSet> {

        @Override
        public BPMNDefinitionSet build() {
            return new BPMNDefinitionSet();
        }

    }

    public BPMNDefinitionSet() {
    }

    public String getDescription() {
        return description;
    }

}
