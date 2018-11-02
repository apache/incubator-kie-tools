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

import java.util.Optional;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;

public class MultipleInstanceSubProcessPropertyReader extends SubProcessPropertyReader {

    public MultipleInstanceSubProcessPropertyReader(SubProcess element, BPMNPlane plane, DefinitionResolver definitionResolver) {
        super(element, plane, definitionResolver);
    }

    public String getCollectionInput() {
        ItemAwareElement ieDataInput = getMultiInstanceLoopCharacteristics()
                .map(MultiInstanceLoopCharacteristics::getLoopDataInputRef)
                .orElse(null);
        return process.getDataInputAssociations().stream()
                .filter(dia -> dia.getTargetRef().getId().equals(ieDataInput.getId()))
                .map(dia -> getVariableName((Property) dia.getSourceRef().get(0)))
                .findFirst()
                .orElse(null);
    }

    public String getCollectionOutput() {
        ItemAwareElement ieDataOutput = getMultiInstanceLoopCharacteristics()
                .map(MultiInstanceLoopCharacteristics::getLoopDataOutputRef)
                .orElse(null);
        return process.getDataOutputAssociations().stream()
                .filter(doa -> doa.getSourceRef().get(0).getId().equals(ieDataOutput.getId()))
                .map(doa -> getVariableName((Property) doa.getTargetRef()))
                .findFirst()
                .orElse(null);
    }

    public String getDataInput() {
        return getMultiInstanceLoopCharacteristics()
                .map(MultiInstanceLoopCharacteristics::getInputDataItem)
                .map(d -> Optional.ofNullable(d.getName()).orElse(d.getId()))
                .orElse("");
    }

    public String getDataOutput() {
        return getMultiInstanceLoopCharacteristics()
                .map(MultiInstanceLoopCharacteristics::getOutputDataItem)
                .map(d -> Optional.ofNullable(d.getName()).orElse(d.getId()))
                .orElse("");
    }

    public String getCompletionCondition() {
        return getMultiInstanceLoopCharacteristics()
                .map(miloop -> (FormalExpression) miloop.getCompletionCondition())
                .map(FormalExpression::getBody).orElse("");
    }

    private Optional<MultiInstanceLoopCharacteristics> getMultiInstanceLoopCharacteristics() {
        return Optional.ofNullable((MultiInstanceLoopCharacteristics) process.getLoopCharacteristics());
    }

    private static String getVariableName(Property property) {
        return ProcessVariableReader.getProcessVariableName(property);
    }
}
