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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import bpsim.ElementParameters;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.SimulationSets;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class ActivityPropertyWriter extends PropertyWriter {

    protected final Activity activity;
    private ElementParameters simulationParameters;

    public ActivityPropertyWriter(Activity activity, VariableScope variableScope) {
        super(activity, variableScope);
        this.activity = activity;
    }

    @Override
    public Activity getFlowElement() {
        return activity;
    }

    public void setSimulationSet(SimulationSet simulationSet) {
        this.simulationParameters = SimulationSets.toElementParameters(simulationSet);
        simulationParameters.setElementRef(activity.getId());
    }

    public ElementParameters getSimulationParameters() {
        return simulationParameters;
    }

    public void setAssignmentsInfo(AssignmentsInfo info) {
        final ParsedAssignmentsInfo assignmentsInfo = ParsedAssignmentsInfo.of(info);
        final InputOutputSpecification ioSpec = getIoSpecification();

        assignmentsInfo.getAssociations()
                .getInputs()
                .stream()
                .map(declaration -> new InputAssignmentWriter(
                        flowElement.getId(),
                        // source is a variable
                        variableScope.lookup(declaration.getLeft()),
                        // target is an input
                        assignmentsInfo
                                .getInputs()
                                .lookup(declaration.getRight()))
                ).forEach(dia -> {
            this.addItemDefinition(dia.getItemDefinition());
            ioSpec.getInputSets().add(dia.getInputSet());
            ioSpec.getDataInputs().add(dia.getDataInput());
            activity.getDataInputAssociations().add(dia.getAssociation());
        });

        assignmentsInfo.getAssociations()
                .getOutputs()
                .stream()
                .map(declaration -> new OutputAssignmentWriter(
                        flowElement.getId(),
                        // source is an output
                        assignmentsInfo
                                .getOutputs()
                                .lookup(declaration.getLeft()),
                        // target is a variable
                        variableScope.lookup(declaration.getRight())
                ))
                .forEach(doa -> {
                    this.addItemDefinition(doa.getItemDefinition());
                    ioSpec.getOutputSets().add(doa.getOutputSet());
                    ioSpec.getDataOutputs().add(doa.getDataOutput());
                    activity.getDataOutputAssociations().add(doa.getAssociation());
                });
    }

    private InputOutputSpecification getIoSpecification() {
        InputOutputSpecification ioSpecification = activity.getIoSpecification();
        if (ioSpecification == null) {
            ioSpecification = bpmn2.createInputOutputSpecification();
            activity.setIoSpecification(ioSpecification);
        }
        return ioSpecification;
    }
}
