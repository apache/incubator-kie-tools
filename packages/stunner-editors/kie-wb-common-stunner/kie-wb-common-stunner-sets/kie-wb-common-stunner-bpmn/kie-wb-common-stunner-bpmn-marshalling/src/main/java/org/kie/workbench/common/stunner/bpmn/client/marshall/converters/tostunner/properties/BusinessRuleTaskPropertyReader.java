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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.DataAssociation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomInput;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;

public class BusinessRuleTaskPropertyReader extends TaskPropertyReader {

    private final BusinessRuleTask task;

    public BusinessRuleTaskPropertyReader(BusinessRuleTask task, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(task, diagram, definitionResolver);
        this.task = task;
    }

    public String getImplementation() {
        return task.getImplementation();
    }

    public String getRuleFlowGroup() {
        return CustomAttribute.ruleFlowGroup.of(element).get();
    }

    public String getFileName() {
        return CustomInput.fileName.of(task).get();
    }

    public String getNamespace() {
        return CustomInput.namespace.of(task).get();
    }

    public String getDecisionName() {
        return CustomInput.decisionName.of(task).get();
    }

    public String getDmnModelName() {
        return CustomInput.dmnModelName.of(task).get();
    }

    public boolean isAsync() {
        return CustomElement.async.of(element).get();
    }

    public boolean isAdHocAutoStart() {
        return CustomElement.autoStart.of(element).get();
    }

    public String getSlaDueDate() {
        return CustomElement.slaDueDate.of(element).get();
    }

    @Override
    protected List<DataInput> getDataInputs() {
        List<DataInput> filteredInputs = super.getDataInputs().stream()
                .filter(dataInput -> !isReservedDataInput(dataInput))
                .collect(Collectors.toList());

        return filteredInputs;
    }

    @Override
    protected List<DataInputAssociation> getDataInputAssociations() {
        List<DataInput> rawDataInputs = super.getDataInputs();
        List<DataInputAssociation> filteredInputs = super.getDataInputAssociations().stream()
                .filter(dia -> !isReservedDataInputAssociation(rawDataInputs, dia))
                .collect(Collectors.toList());

        return filteredInputs;
    }

    @Override
    protected List<DataOutput> getDataOutputs() {
        List<DataOutput> filterOutputs = super.getDataOutputs().stream()
                .filter(dataOutput -> !isReservedDataOutput(dataOutput))
                .collect(Collectors.toList());

        return filterOutputs;
    }

    @Override
    protected List<DataOutputAssociation> getDataOutputAssociations() {
        List<DataOutput> rawDataOutputs = super.getDataOutputs();
        List<DataOutputAssociation> filteredOutputs = super.getDataOutputAssociations().stream()
                .filter(doa -> !isReservedDataOutputAssociation(rawDataOutputs, doa))
                .collect(Collectors.toList());

        return filteredOutputs;
    }

    private static String getTargetRefID(DataAssociation dataAssociation) {
        return dataAssociation.getTargetRef() != null ? dataAssociation.getTargetRef().getId() : "";
    }

    private static boolean isReservedDataInputAssociation(List<DataInput> dataInputs,
                                                          DataInputAssociation dataInputAssociation) {
        DataInput dataInput = dataInputs.stream()
                .filter(input -> input.getId().equals(getTargetRefID(dataInputAssociation)))
                .findFirst()
                .orElse(null);

        return isReservedDataInput(dataInput);
    }

    private static boolean isReservedDataOutputAssociation(List<DataOutput> dataOutputs,
                                                           DataOutputAssociation dataOutputAssociation) {
        DataOutput dataOutput = dataOutputs.stream()
                .filter(output -> output.getId().equals(getTargetRefID(dataOutputAssociation)))
                .findFirst()
                .orElse(null);

        return isReservedDataOutput(dataOutput);
    }

    private static boolean isReservedDataInput(DataInput dataInput) {
        if (dataInput == null) {
            return false;
        }

        String dataName = dataInput.getName();
        return isReservedDataName(dataName);
    }

    private static boolean isReservedDataOutput(DataOutput dataOutput) {
        if (dataOutput == null) {
            return false;
        }

        String dataName = dataOutput.getName();
        return isReservedDataName(dataName);
    }

    private static boolean isReservedDataName(String dataName) {
        if (dataName.isEmpty()) {
            return false;
        }

        return  dataName.equals("fileName") ||
                dataName.equals("namespace") ||
                dataName.equals("model") ||
                dataName.equals("decision");
    }
}