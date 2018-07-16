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

import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ThrowEvent;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.AssignmentsInfos.isReservedIdentifier;

public class ThrowEventPropertyWriter extends EventPropertyWriter {

    private final ThrowEvent throwEvent;
    private final InputSet inputSet;

    public ThrowEventPropertyWriter(ThrowEvent flowElement, VariableScope variableScope) {
        super(flowElement, variableScope);
        this.throwEvent = flowElement;
        this.inputSet = bpmn2.createInputSet();
        throwEvent.setInputSet(inputSet);
    }

    @Override
    public void setAssignmentsInfo(AssignmentsInfo info) {
        ParsedAssignmentsInfo assignmentsInfo = ParsedAssignmentsInfo.of(info);
        assignmentsInfo
                .getInputs().getDeclarations()
                .stream()
                .filter(varDecl -> !isReservedIdentifier(varDecl.getIdentifier()))
                .map(varDecl -> new DeclarationWriter(flowElement.getId(), varDecl))
                .peek(dw -> {
                    this.addItemDefinition(dw.getItemDefinition());
                    throwEvent.getDataInputs().add(dw.getDataInput());
                    inputSet.getDataInputRefs().add(dw.getDataInput());
                })
                .flatMap(dw -> toInputAssignmentStream(assignmentsInfo, dw))
                .forEach(dia -> {
                    throwEvent.getDataInputAssociation().add(dia.getAssociation());
                });
    }

    private Stream<InputAssignmentWriter> toInputAssignmentStream(ParsedAssignmentsInfo assignmentsInfo, DeclarationWriter dw) {
        return assignmentsInfo.getAssociations().lookupInput(dw.getVarId())
                .map(targetVar -> variableScope.lookup(targetVar.getSource()))
                .filter(Objects::nonNull)
                .map(targetVar -> new InputAssignmentWriter(dw, targetVar));
    }

    @Override
    protected void addEventDefinition(EventDefinition eventDefinition) {
        this.throwEvent.getEventDefinitions().add(eventDefinition);
    }
}
