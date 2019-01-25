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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.DataAssociation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomInput;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;

public class BusinessRuleTaskPropertyReader extends TaskPropertyReader {

    private final BusinessRuleTask task;

    public BusinessRuleTaskPropertyReader(BusinessRuleTask task, BPMNPlane plane, DefinitionResolver definitionResolver) {
        super(task, plane, definitionResolver);
        this.task = task;
    }

    public String getImplementation() {
        return this.task.getImplementation();
    }

    public String getRuleFlowGroup() {
        return CustomAttribute.ruleFlowGroup.of(element).get();
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

    public AssignmentsInfo getAssignmentsInfo() {
        Optional<InputOutputSpecification> ioSpecification =
                Optional.ofNullable(task.getIoSpecification());

        List<DataInput> rawDataInputs = ioSpecification.map(InputOutputSpecification::getDataInputs)
                .orElse(Collections.emptyList());

        List<DataOutput> rawDataOutputs = ioSpecification.map(InputOutputSpecification::getDataOutputs)
                .orElse(Collections.emptyList());

        AssignmentsInfo info = AssignmentsInfos.of(getDataInputs(rawDataInputs),
                                                   getDataInputAssociations(rawDataInputs),
                                                   getDataOutputs(rawDataOutputs),
                                                   getDataOutputAssociations(rawDataOutputs),
                                                   ioSpecification.isPresent());
        if (info.getValue().isEmpty()) {
            info.setValue("||||");
        }
        return info;
    }

    public ScriptTypeListValue getOnEntryAction() {
        return Scripts.onEntry(element.getExtensionValues());
    }

    public ScriptTypeListValue getOnExitAction() {
        return Scripts.onExit(element.getExtensionValues());
    }

    public boolean isAsync() {
        return CustomElement.async.of(element).get();
    }

    public boolean isAdHocAutoStart() {
        return CustomElement.autoStart.of(element).get();
    }

    private List<DataInput> getDataInputs(final List<DataInput> rawDataInput) {
        List<DataInput> filteredInputs = rawDataInput.stream()
                .filter(dataInput -> !isReservedDataInput(dataInput))
                .collect(Collectors.toList());

        return filteredInputs;
    }

    private List<DataInputAssociation> getDataInputAssociations(final List<DataInput> dataInputs) {
        List<DataInputAssociation> dataInputAssociations = task.getDataInputAssociations();

        List<DataInputAssociation> filteredInputs = dataInputAssociations.stream()
                .filter(dia -> !isReservedDataInputAssociation(dataInputs, dia))
                .collect(Collectors.toList());

        return filteredInputs;
    }

    private List<DataOutput> getDataOutputs(final List<DataOutput> rawDataOutput) {
        List<DataOutput> filterOutputs = rawDataOutput.stream()
                .filter(dataOutput -> !isReservedDataOutput(dataOutput))
                .collect(Collectors.toList());

        return filterOutputs;
    }

    private List<DataOutputAssociation> getDataOutputAssociations(final List<DataOutput> dataOutputs) {
        List<DataOutputAssociation> dataOutputAssociations = task.getDataOutputAssociations();

        List<DataOutputAssociation> filteredOutputs = dataOutputAssociations.stream()
                .filter(doa -> !isReservedDataOutputAssociation(dataOutputs, doa))
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

        return dataName.equals("namespace") ||
                dataName.equals("model") ||
                dataName.equals("decision");
    }
}