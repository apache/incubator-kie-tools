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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration.Direction;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationList;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.DeclarationList;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.VariableDeclaration;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;

import static java.util.Arrays.asList;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration.Type;

public class AssignmentsInfos {

    private static Set<String> RESERVED_DECLARATIONS = new HashSet<>(asList(
            "TaskName"));

    private static Set<String> RESERVED_ASSIGNMENTS = new HashSet<>(asList(
            "GroupId",
            "Skippable",
            "Comment",
            "Description",
            "Priority",
            "Content",
            "TaskName",
            "Locale",
            "CreatedBy",
            "NotCompletedReassign",
            "NotStartedReassign",
            "NotCompletedNotify",
            "NotStartedNotify"));

    public static AssignmentsInfo of(
            final List<DataInput> datainput,
            final List<DataInputAssociation> inputAssociations,
            final List<DataOutput> dataoutput,
            final List<DataOutputAssociation> outputAssociations,
            boolean alternativeEncoding) {

        ParsedAssignmentsInfo parsedAssignmentsInfo = parsed(
                datainput,
                inputAssociations,
                dataoutput,
                outputAssociations,
                alternativeEncoding);

        return new AssignmentsInfo(parsedAssignmentsInfo.toString());
    }

    public static ParsedAssignmentsInfo parsed(
            List<DataInput> datainput,
            List<DataInputAssociation> inputAssociations,
            List<DataOutput> dataoutput,
            List<DataOutputAssociation> outputAssociations,
            boolean alternativeEncoding) {
        DeclarationList inputs = dataInputDeclarations(datainput);
        DeclarationList outputs = dataOutputDeclarations(dataoutput);

        AssociationList associations = new AssociationList(
                inAssociationDeclarations(inputAssociations),
                outAssociationDeclarations(outputAssociations));

        return new ParsedAssignmentsInfo(
                inputs, outputs, associations, alternativeEncoding);
    }

    public static boolean isReservedDeclaration(DataInput o) {
        return RESERVED_DECLARATIONS.contains(o.getName());
    }

    public static boolean isReservedIdentifier(String targetName) {
        return RESERVED_ASSIGNMENTS.contains(targetName);
    }

    private static DeclarationList dataInputDeclarations(List<DataInput> dataInputs) {
        return new DeclarationList(
                dataInputs.stream()
                        .filter(o -> !isReservedDeclaration(o))
                        .map(in -> new VariableDeclaration(
                                in.getName(),
                                CustomAttribute.dtype.of(in).get()))
                        .collect(Collectors.toList()));
    }

    private static DeclarationList dataOutputDeclarations(List<DataOutput> dataOutputs) {
        return new DeclarationList(
                dataOutputs.stream()
                        .map(out -> new VariableDeclaration(
                                out.getName(),
                                CustomAttribute.dtype.of(out).get()))
                        .collect(Collectors.toList()));
    }

    private static List<AssociationDeclaration> inAssociationDeclarations(List<DataInputAssociation> inputAssociations) {
        return inputAssociations
                .stream()
                .map(InputAssignmentReader::fromAssociation)
                .filter(Objects::nonNull)
                .map(InputAssignmentReader::getAssociationDeclaration)
                .collect(Collectors.toList());
    }

    private static List<AssociationDeclaration> outAssociationDeclarations(List<DataOutputAssociation> outputAssociations) {
        return outputAssociations.stream()
                .map(out -> new AssociationDeclaration(
                        Direction.Output,
                        Type.SourceTarget,
                        ((DataOutput) out.getSourceRef().get(0)).getName(),
                        getPropertyName((Property) out.getTargetRef())))
                .collect(Collectors.toList());
    }

    // fallback to ID for https://issues.jboss.org/browse/JBPM-6708
    private static String getPropertyName(Property prop) {
        return prop.getName() == null ? prop.getId() : prop.getName();
    }
}
