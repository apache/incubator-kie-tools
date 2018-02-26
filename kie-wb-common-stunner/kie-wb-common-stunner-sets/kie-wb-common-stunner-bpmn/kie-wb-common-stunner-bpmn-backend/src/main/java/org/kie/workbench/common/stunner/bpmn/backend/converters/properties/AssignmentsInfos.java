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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.emf.ecore.util.FeatureMap;

public class AssignmentsInfos {

    // this is actually for compatibility with the previous generator
    public static String makeWrongString(
            final List<DataInput> datainput,
            final List<DataInputAssociation> inputAssociations,
            final List<DataOutput> dataoutput,
            final List<DataOutputAssociation> outputAssociations) {

        String dataInputString = dataInputsToString(datainput);
        List<String> dataInputAssociationsToString = inAssociationsToString(inputAssociations);

        String dataOutputString = dataOutputsToString(dataoutput);
        List<String> dataOutputAssociationsToString = outAssociationsToString(outputAssociations);

        String associationString =
                Stream.concat(dataInputAssociationsToString.stream(), dataOutputAssociationsToString.stream())
                        .collect(Collectors.joining(","));

        return Stream.of("",
                         dataInputString,
                         "",
                         dataOutputString,
                         associationString)
                .collect(Collectors.joining("|"));
    }

    public static String makeString(
            final List<DataInput> datainput,
            final List<InputSet> inputSets,
            final List<DataInputAssociation> inputAssociations,
            final List<DataOutput> dataoutput,
            final List<OutputSet> dataoutputset,
            final List<DataOutputAssociation> outputAssociations) {

        String dataInputString = dataInputsToString(datainput);
        String inputSetsToString = inputSetsToString(inputSets);
        List<String> dataInputAssociationsToString = inAssociationsToString(inputAssociations);

        String dataOutputString = dataOutputsToString(dataoutput);
        String outputSetsToString = outputSetsToString(dataoutputset);
        List<String> dataOutputAssociationsToString = outAssociationsToString(outputAssociations);

        String associationString =
                Stream.concat(dataInputAssociationsToString.stream(), dataOutputAssociationsToString.stream())
                        .collect(Collectors.joining(","));

        return Stream.of(dataInputString,
                         inputSetsToString,
                         dataOutputString,
                         outputSetsToString,
                         associationString)
                .collect(Collectors.joining("|"));
    }

    private static String inputSetsToString(List<InputSet> inputSets) {
        return inputSets.stream()
                .map(AssignmentsInfos::toString)
                .collect(Collectors.joining(","));
    }

    private static String outputSetsToString(List<OutputSet> outputSets) {
        return outputSets.stream()
                .map(AssignmentsInfos::toString)
                .collect(Collectors.joining(","));
    }

    public static String dataInputsToString(List<DataInput> dataInputs) {
        return dataInputs.stream()
                .filter(o -> !o.getName().equals("TaskName"))
                .map(AssignmentsInfos::toString)
                .collect(Collectors.joining(","));
    }

    public static String dataOutputsToString(List<DataOutput> dataInputs) {
        return dataInputs.stream()
                .filter(o -> !extractDtype(o).isEmpty())
                .map(AssignmentsInfos::toString)
                .collect(Collectors.joining(","));
    }

    private static String toString(OutputSet outputSet) {
        return "";
    }

    public static String toString(InputSet dataInput) {
        return "";
    }

    public static List<String> outAssociationsToString(List<DataOutputAssociation> outputAssociations) {
        List<String> result = new ArrayList<>();
        for (DataOutputAssociation doa : outputAssociations) {
            String doaName = ((DataOutput) doa.getSourceRef().get(0)).getName();
            if (doaName != null && doaName.length() > 0) {
                result.add(String.format("[dout]%s->%s", doaName, doa.getTargetRef().getId()));
            }
        }
        return result;
    }

    public static List<String> inAssociationsToString(List<DataInputAssociation> inputAssociations) {
        List<String> result = new ArrayList<>();

        for (DataInputAssociation dia : inputAssociations) {
            List<ItemAwareElement> sourceRef = dia.getSourceRef();
            if (sourceRef.isEmpty()) {
                continue;
            }
            String doaName = sourceRef.get(0).getId();
            if (doaName != null && doaName.length() > 0) {
                result.add(String.format("[din]%s->%s", doaName, ((DataInput) dia.getTargetRef()).getName()));
            }
        }

        return result;
    }

    public static String toString(DataInput dataInput) {
        String name = dataInput.getName();
        String dtype = extractDtype(dataInput);
        return dtype.isEmpty() ? name : name + ':' + dtype;
    }

    public static String toString(DataOutput dataInput) {
        String name = dataInput.getName();
        String dtype = extractDtype(dataInput);
        return dtype.isEmpty() ? name : name + ':' + dtype;
    }

    private static String extractDtype(BaseElement el) {
        return getAnyAttributeValue(el, "dtype");
    }

    static String getAnyAttributeValue(BaseElement el, String attrName) {
        for (FeatureMap.Entry entry : el.getAnyAttribute()) {
            if (attrName.equals(entry.getEStructuralFeature().getName())) {
                return entry.getValue().toString();
            }
        }
        return "";
    }
}
