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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.Collections;
import java.util.Optional;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;

public class ActivityPropertyReader extends FlowElementPropertyReader {

    private final Activity activity;
    private DefinitionResolver definitionResolver;

    public ActivityPropertyReader(Activity activity, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(activity, diagram, definitionResolver.getShape(activity.getId()), definitionResolver.getResolutionFactor());
        this.activity = activity;
        this.definitionResolver = definitionResolver;
    }

    public ScriptTypeListValue getOnEntryAction() {
        return Scripts.onEntry(element.getExtensionValues());
    }

    public ScriptTypeListValue getOnExitAction() {
        return Scripts.onExit(element.getExtensionValues());
    }

    public boolean isIndependent() {
        return CustomAttribute.independent.of(element).get();
    }

    public boolean isWaitForCompletion() {
        return CustomAttribute.waitForCompletion.of(element).get();
    }

    public boolean isAsync() {
        return CustomElement.async.of(element).get();
    }

    public AssignmentsInfo getAssignmentsInfo() {
        Optional<InputOutputSpecification> ioSpecification =
                Optional.ofNullable(activity.getIoSpecification());

        return AssignmentsInfos.of(
                ioSpecification.map(InputOutputSpecification::getDataInputs)
                        .orElse(Collections.emptyList()),
                activity.getDataInputAssociations(),
                ioSpecification.map(InputOutputSpecification::getDataOutputs)
                        .orElse(Collections.emptyList()),
                activity.getDataOutputAssociations(),
                ioSpecification.isPresent()
        );
    }

    public SimulationSet getSimulationSet() {
        return definitionResolver.resolveSimulationParameters(element.getId())
                .map(SimulationSets::of)
                .orElse(new SimulationSet());
    }
}
