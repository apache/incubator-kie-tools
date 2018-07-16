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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public class InputAssignmentWriter {

    private final DataInputAssociation association;
    private final DeclarationWriter declarationWriter;

    public static InputAssignmentWriter fromDeclaration(
            AssociationDeclaration declaration,
            DeclarationWriter dw,
            VariableScope variableScope) {

        switch (declaration.getType()) {
            case SourceTarget:
                return new InputAssignmentWriter(
                        dw,
                        variableScope.lookup(declaration.getSource()));

            case FromTo:
                return new InputAssignmentWriter(
                        dw,
                        declaration.getSource());
            default:
                throw new IllegalArgumentException("Unrecognized association type " + declaration.getType() +
                                                           " in declaration " + declaration);
        }
    }

    public InputAssignmentWriter(DeclarationWriter dw, VariableScope.Variable variable) {
        this.declarationWriter = dw;
        this.association = associationOf(variable.getTypedIdentifier(), declarationWriter.getDataInput());
    }

    public InputAssignmentWriter(
            DeclarationWriter dw,
            String expression) {
        this.declarationWriter = dw;

        // then we create the actual association between the two
        // e.g. myTarget = expression
        this.association = associationOf(expression, declarationWriter.getDataInput());
    }

    private DataInputAssociation associationOf(Property source, DataInput dataInput) {
        DataInputAssociation dataInputAssociation =
                bpmn2.createDataInputAssociation();

        dataInputAssociation
                .getSourceRef()
                .add(source);

        dataInputAssociation
                .setTargetRef(dataInput);
        return dataInputAssociation;
    }

    private DataInputAssociation associationOf(String expression, DataInput dataInput) {
        DataInputAssociation dataInputAssociation =
                bpmn2.createDataInputAssociation();

        Assignment assignment = bpmn2.createAssignment();
        String id = dataInput.getId();

        FormalExpression toExpr = bpmn2.createFormalExpression();
        toExpr.setBody(id);
        assignment.setTo(toExpr);

        FormalExpression fromExpr = bpmn2.createFormalExpression();
        // this should be handled **outside** the marshallers!
        String decodedExpression = decode(expression);
        String cdataExpression = asCData(decodedExpression);
        fromExpr.setBody(cdataExpression);
        assignment.setFrom(fromExpr);

        dataInputAssociation
                .getAssignment().add(assignment);

        dataInputAssociation
                .setTargetRef(dataInput);
        return dataInputAssociation;
    }

    public DataInput getDataInput() {
        return declarationWriter.getDataInput();
    }

    public ItemDefinition getItemDefinition() {
        return declarationWriter.getItemDefinition();
    }

    public DataInputAssociation getAssociation() {
        return association;
    }

    private String decode(String text) {
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(text, e);
        }
    }
}
