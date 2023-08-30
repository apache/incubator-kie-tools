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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.List;
import java.util.Set;

import bpsim.ElementParameters;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.OutputSet;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.InitializedVariable.InitializedInputVariable;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.InitializedVariable.InitializedOutputVariable;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.SimulationSets;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.AssignmentsInfos.isReservedIdentifier;

public class ActivityPropertyWriter extends PropertyWriter {

    protected final Activity activity;
    private ElementParameters simulationParameters;
    private final Set<DataObject> dataObjects;

    public ActivityPropertyWriter(Activity activity, VariableScope variableScope, Set<DataObject> dataObjects) {
        super(activity, variableScope);
        this.activity = activity;
        this.dataObjects = dataObjects;
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
        info.setValue(StringUtils.preFilterVariablesTwoSemicolonForGenerics(info.getValue()));
        final ParsedAssignmentsInfo assignmentsInfo = ParsedAssignmentsInfo.of(info);
        final List<InitializedInputVariable> inputs = assignmentsInfo.createInitializedInputVariables(getId(),
                                                                                                      variableScope, dataObjects);
        if (!inputs.isEmpty()) {
            final InputOutputSpecification ioSpec = getIoSpecification();
            for (InitializedInputVariable input : inputs) {
                input.getItemDefinition().setStructureRef(StringUtils.postFilterForGenerics(input.getType()));

                if (isReservedIdentifier(input.getIdentifier())) {
                    continue;
                }

                DataInput dataInput = input.getDataInput();
                getInputSet(ioSpec).getDataInputRefs().add(dataInput);
                ioSpec.getDataInputs().add(dataInput);

                this.addItemDefinition(input.getItemDefinition());
                DataInputAssociation dataInputAssociation = input.getDataInputAssociation();
                if (dataInputAssociation != null) {
                    activity.getDataInputAssociations().add(dataInputAssociation);
                }
            }
        }
        final List<InitializedOutputVariable> outputs = assignmentsInfo.createInitializedOutputVariables(getId(),
                                                                                                         variableScope, dataObjects);
        if (!outputs.isEmpty()) {
            final InputOutputSpecification ioSpec = getIoSpecification();
            for (InitializedOutputVariable output : outputs) {
                output.getItemDefinition().setStructureRef(StringUtils.postFilterForGenerics(output.getType()));
                DataOutput dataOutput = output.getDataOutput();
                getOutputSet(ioSpec).getDataOutputRefs().add(dataOutput);
                ioSpec.getDataOutputs().add(dataOutput);

                this.addItemDefinition(output.getItemDefinition());
                DataOutputAssociation dataOutputAssociation = output.getDataOutputAssociation();
                if (dataOutputAssociation != null) {
                    activity.getDataOutputAssociations().add(dataOutputAssociation);
                }
            }
        }
    }

    protected InputOutputSpecification getIoSpecification() {
        InputOutputSpecification ioSpecification = activity.getIoSpecification();
        if (ioSpecification == null) {
            ioSpecification = bpmn2.createInputOutputSpecification();
            activity.setIoSpecification(ioSpecification);
        }
        return ioSpecification;
    }

    protected InputSet getInputSet(InputOutputSpecification ioSpecification) {
        List<InputSet> inputSets = ioSpecification.getInputSets();
        InputSet inputSet;
        if (inputSets.isEmpty()) {
            inputSet = bpmn2.createInputSet();
            inputSets.add(inputSet);
        } else {
            inputSet = inputSets.get(0);
        }
        return inputSet;
    }

    protected OutputSet getOutputSet(InputOutputSpecification ioSpecification) {
        List<OutputSet> outputSets = ioSpecification.getOutputSets();
        OutputSet outputSet;
        if (outputSets.isEmpty()) {
            outputSet = bpmn2.createOutputSet();
            outputSets.add(outputSet);
        } else {
            outputSet = outputSets.get(0);
        }
        return outputSet;
    }

    public Set<DataObject> getDataObjects() {
        return dataObjects;
    }
}
