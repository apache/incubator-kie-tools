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

package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import java.util.Collections;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;

public class ActivityPropertyReader extends FlowElementPropertyReader {

    private final Activity activity;
    private DefinitionResolver definitionResolver;

    public ActivityPropertyReader(Activity activity, BPMNPlane plane, DefinitionResolver definitionResolver) {
        super(activity, plane);
        this.activity = activity;
        this.definitionResolver = definitionResolver;
    }

    public boolean isIndependent() {
        return Boolean.parseBoolean(attribute("independent"));
    }

    public boolean isWaitForCompletion() {
        return Boolean.parseBoolean(attribute("waitForCompletion"));
    }

    public boolean isAsync() {
        return Boolean.parseBoolean(metaData("customAsync"));
    }

    public String getAssignmentsInfo() {
        InputOutputSpecification ioSpecification = activity.getIoSpecification();
        if (ioSpecification == null) {
            return AssignmentsInfos.makeString(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    activity.getDataInputAssociations(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    activity.getDataOutputAssociations());
        } else {
            return AssignmentsInfos.makeWrongString(
                    ioSpecification.getDataInputs(),
                    activity.getDataInputAssociations(),
                    ioSpecification.getDataOutputs(),
                    activity.getDataOutputAssociations());
        }
    }

    public String getProcessVariables() {
        return activity.getProperties()
                .stream()
                .map(p -> p.getId() + ":" + p.getItemSubjectRef().getStructureRef())
                .collect(Collectors.joining(","));
    }

    public SimulationSet getSimulationSet() {
        return definitionResolver.extractSimulationSet(activity.getId());
    }
}
