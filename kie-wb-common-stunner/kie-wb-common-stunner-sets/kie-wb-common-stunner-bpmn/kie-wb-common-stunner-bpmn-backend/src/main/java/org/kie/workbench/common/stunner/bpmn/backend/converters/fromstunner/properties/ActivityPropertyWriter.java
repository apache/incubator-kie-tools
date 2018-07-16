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

import java.util.List;
import java.util.stream.Stream;

import bpsim.ElementParameters;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.OutputSet;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.SimulationSets;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.AssignmentsInfos.isReservedIdentifier;

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
        final InputSet inputSet = getInputSet();
        final OutputSet outputSet = getOutputSet();
        ioSpec.getOutputSets().add(outputSet);

        assignmentsInfo
                .getInputs().getDeclarations()
                .stream()
                .filter(varDecl -> !isReservedIdentifier(varDecl.getIdentifier()))
                .map(varDecl -> new DeclarationWriter(flowElement.getId(), varDecl))
                .peek(dw -> {
                    this.addItemDefinition(dw.getItemDefinition());
                    inputSet.getDataInputRefs().add(dw.getDataInput());
                    ioSpec.getDataInputs().add(dw.getDataInput());
                })
                .flatMap(dw -> toInputAssignmentStream(assignmentsInfo, dw))
                .forEach(dia -> {
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
                                .lookup(declaration.getSource()),
                        // target is a variable
                        variableScope.lookup(declaration.getTarget())
                ))
                .forEach(doa -> {
                    this.addItemDefinition(doa.getItemDefinition());
                    outputSet.getDataOutputRefs().add(doa.getDataOutput());
                    ioSpec.getDataOutputs().add(doa.getDataOutput());
                    activity.getDataOutputAssociations().add(doa.getAssociation());
                });

    }


    private Stream<InputAssignmentWriter> toInputAssignmentStream(ParsedAssignmentsInfo assignmentsInfo, DeclarationWriter dw) {
        return assignmentsInfo.getAssociations().lookupInput(dw.getVarId())
                .map(targetVar ->
                             InputAssignmentWriter.fromDeclaration(
                                     targetVar, dw, variableScope));
    }

    private InputOutputSpecification getIoSpecification() {
        InputOutputSpecification ioSpecification = activity.getIoSpecification();
        if (ioSpecification == null) {
            ioSpecification = bpmn2.createInputOutputSpecification();
            activity.setIoSpecification(ioSpecification);
        }
        return ioSpecification;
    }


    public InputSet getInputSet() {
        InputSet inputSet;
        List<InputSet> inputSets = getIoSpecification().getInputSets();
        if (inputSets.isEmpty()) {
            inputSet = bpmn2.createInputSet();
            inputSets.add(inputSet);
        } else {
            inputSet = inputSets.get(0);
        }
        return inputSet;
    }

    public OutputSet getOutputSet() {
        OutputSet outputSet;
        List<OutputSet> outputSets = getIoSpecification().getOutputSets();
        if (outputSets.isEmpty()) {
            outputSet = bpmn2.createOutputSet();
            outputSets.add(outputSet);
        } else {
            outputSet = outputSets.get(0);
        }
        return outputSet;
    }
}
