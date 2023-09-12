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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.DataAssociation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class MultipleInstanceActivityPropertyReader extends ActivityPropertyReader {

    public MultipleInstanceActivityPropertyReader(Activity activity, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(activity, diagram, definitionResolver);
    }

    public boolean isMultipleInstance() {
        return getMultiInstanceLoopCharacteristics().isPresent();
    }

    public String getCollectionInput() {
        String ieDataInputId = getLoopDataInputRefId();
        return super.getDataInputAssociations().stream()
                .filter(dia -> hasTargetRef(dia, ieDataInputId))
                .filter(MultipleInstanceActivityPropertyReader::hasSourceRefs)
                .map(dia -> ItemNameReader.from(dia.getSourceRef().get(0)).getName())
                .findFirst()
                .orElse(null);
    }

    public String getCollectionOutput() {
        String ieDataOutputId = getLoopDataOutputRefId();
        return super.getDataOutputAssociations().stream()
                .filter(doa -> hasSourceRef(doa, ieDataOutputId))
                .map(doa -> ItemNameReader.from(doa.getTargetRef()).getName())
                .findFirst()
                .orElse(null);
    }

    public String getDataInput() {
        return getMultiInstanceLoopCharacteristics()
                .map(MultiInstanceLoopCharacteristics::getInputDataItem)
                .map(MultipleInstanceActivityPropertyReader::createInputVariable)
                .orElse("");
    }

    private static String createInputVariable(DataInput input) {
        String name = (input.getName() != null) ? input.getName() : input.getId();
        return name + ":" + getVariableType(input);
    }

    public String getDataOutput() {
        return getMultiInstanceLoopCharacteristics()
                .map(MultiInstanceLoopCharacteristics::getOutputDataItem)
                .map(MultipleInstanceActivityPropertyReader::createOutputVariable)
                .orElse("");
    }

    private static String createOutputVariable(DataOutput output) {
        String name = (output.getName() != null) ? output.getName() : output.getId();
        return name + ":" + getVariableType(output);
    }

    private static String getVariableType(ItemAwareElement variable) {
        String type = "";
        if (variable.getItemSubjectRef() != null) {
            type = variable.getItemSubjectRef().getStructureRef();
        }
        return StringUtils.isEmpty(type) ? "Object" : type;
    }

    public String getCompletionCondition() {
        return getMultiInstanceLoopCharacteristics()
                .map(miloop -> (FormalExpression) miloop.getCompletionCondition())
                .map(fe -> FormalExpressionBodyHandler.of(fe).getBody())
                .orElse("");
    }

    public boolean isSequential() {
        return getMultiInstanceLoopCharacteristics()
                .map(MultiInstanceLoopCharacteristics::isIsSequential)
                .orElse(false);
    }

    private Optional<MultiInstanceLoopCharacteristics> getMultiInstanceLoopCharacteristics() {
        return Optional.ofNullable((MultiInstanceLoopCharacteristics) activity.getLoopCharacteristics());
    }

    @Override
    protected List<DataInput> getDataInputs() {
        if (getMultiInstanceLoopCharacteristics().isPresent()) {
            String dataInputIdForInputVariable = getDataInputIdForDataInputVariable();
            String dataInputIdForInputCollection = getLoopDataInputRefId();
            return super.getDataInputs().stream()
                    .filter(di -> !di.getId().equals(dataInputIdForInputVariable))
                    .filter(di -> !di.getId().equals(dataInputIdForInputCollection))
                    .collect(Collectors.toList());
        }
        return super.getDataInputs();
    }

    @Override
    protected List<DataOutput> getDataOutputs() {
        if (getMultiInstanceLoopCharacteristics().isPresent()) {
            String dataOuputIdForOutputVariable = getDataOutputIdForDataOutputVariable();
            String dataOutputIdForCollection = getLoopDataOutputRefId();
            return super.getDataOutputs().stream()
                    .filter(dout -> !dout.getId().equals(dataOuputIdForOutputVariable))
                    .filter(dout -> !dout.getId().equals(dataOutputIdForCollection))
                    .collect(Collectors.toList());
        }
        return super.getDataOutputs();
    }

    @Override
    protected List<DataInputAssociation> getDataInputAssociations() {
        if (getMultiInstanceLoopCharacteristics().isPresent()) {
            String dataInputIdForInputVariable = getDataInputIdForDataInputVariable();
            String dataInputIdForInputCollection = getLoopDataInputRefId();
            return super.getDataInputAssociations().stream()
                    .filter(dia -> !hasTargetRef(dia, dataInputIdForInputVariable))
                    .filter(dia -> !hasTargetRef(dia, dataInputIdForInputCollection))
                    .collect(Collectors.toList());
        }
        return super.getDataInputAssociations();
    }

    @Override
    protected List<DataOutputAssociation> getDataOutputAssociations() {
        if (getMultiInstanceLoopCharacteristics().isPresent()) {
            String dataOutputIdForOutputVariable = getDataOutputIdForDataOutputVariable();
            String dataOutputIdForOutputCollection = getLoopDataOutputRefId();
            return super.getDataOutputAssociations().stream()
                    .filter(doa -> !hasSourceRef(doa, dataOutputIdForOutputVariable))
                    .filter(doa -> !hasSourceRef(doa, dataOutputIdForOutputCollection))
                    .collect(Collectors.toList());
        }
        return super.getDataOutputAssociations();
    }

    protected String getDataInputIdForDataInputVariable() {
        String dataInputVariableId = null;
        DataInput variableDataInput = getMultiInstanceLoopCharacteristics()
                .map(MultiInstanceLoopCharacteristics::getInputDataItem)
                .orElse(null);
        if (variableDataInput != null) {
            String itemSubjectRef = getItemSubjectRef(variableDataInput);
            String variableId = ItemNameReader.from(variableDataInput).getName();
            dataInputVariableId = super.getDataInputs().stream()
                    .filter(input -> Objects.equals(variableId, input.getName()))
                    .filter(input -> hasItemSubjectRef(input, itemSubjectRef))
                    .map(BaseElement::getId)
                    .findFirst().orElse(null);
        }
        return dataInputVariableId;
    }

    protected String getDataOutputIdForDataOutputVariable() {
        String dataOutputVariableId = null;
        DataOutput variableDataOutput = getMultiInstanceLoopCharacteristics()
                .map(MultiInstanceLoopCharacteristics::getOutputDataItem)
                .orElse(null);
        if (variableDataOutput != null) {
            String itemSubjectRef = getItemSubjectRef(variableDataOutput);
            String variableId = ItemNameReader.from(variableDataOutput).getName();
            dataOutputVariableId = super.getDataOutputs().stream()
                    .filter(output -> Objects.equals(variableId, output.getName()))
                    .filter(output -> hasItemSubjectRef(output, itemSubjectRef))
                    .map(BaseElement::getId)
                    .findFirst().orElse(null);
        }
        return dataOutputVariableId;
    }

    protected String getLoopDataInputRefId() {
        return getMultiInstanceLoopCharacteristics()
                .map(MultiInstanceLoopCharacteristics::getLoopDataInputRef)
                .map(ItemAwareElement::getId)
                .orElse(null);
    }

    protected String getLoopDataOutputRefId() {
        return getMultiInstanceLoopCharacteristics()
                .map(MultiInstanceLoopCharacteristics::getLoopDataOutputRef)
                .map(ItemAwareElement::getId)
                .orElse(null);
    }

    static boolean hasSourceRefs(DataAssociation dataAssociation) {
        return dataAssociation.getSourceRef() != null && !dataAssociation.getSourceRef().isEmpty();
    }

    static boolean hasSourceRef(DataAssociation dataAssociation, String id) {
        return hasSourceRefs(dataAssociation) && Objects.equals(dataAssociation.getSourceRef().get(0).getId(), id);
    }

    static boolean hasTargetRef(DataAssociation dataAssociation, String id) {
        return dataAssociation.getTargetRef() != null && Objects.equals(dataAssociation.getTargetRef().getId(), id);
    }

    static boolean hasItemSubjectRef(ItemAwareElement element, String itemSubjectRef) {
        return element.getItemSubjectRef() != null && Objects.equals(element.getItemSubjectRef().getId(), itemSubjectRef);
    }

    static String getItemSubjectRef(ItemAwareElement element) {
        return element.getItemSubjectRef() != null ? element.getItemSubjectRef().getId() : null;
    }
}
