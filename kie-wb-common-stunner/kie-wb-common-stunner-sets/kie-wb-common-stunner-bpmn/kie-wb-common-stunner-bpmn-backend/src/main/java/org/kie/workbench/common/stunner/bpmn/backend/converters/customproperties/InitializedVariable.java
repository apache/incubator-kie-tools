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

package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.VariableScope;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public abstract class InitializedVariable {

    private final String identifier;
    private final String type;
    private ItemDefinition itemDefinition;

    public InitializedVariable(String parentId, VariableDeclaration varDecl) {
        this.identifier = varDecl.getIdentifier();
        this.type = varDecl.getType();
        this.itemDefinition = bpmn2.createItemDefinition();
        itemDefinition.setId(Ids.item(parentId));
        itemDefinition.setStructureRef(getType());
    }

    public static InitializedInputVariable inputOf(
            String parentId,
            VariableScope variableScope,
            VariableDeclaration varDecl,
            AssociationDeclaration associationDeclaration) {

        if (associationDeclaration == null) {
            return new InputEmpty(parentId, varDecl);
        }
        AssociationDeclaration.Type type = associationDeclaration.getType();
        switch (type) {
            case FromTo:
                if (associationDeclaration.getTarget() == null) {
                    return new InputEmpty(parentId, varDecl);
                } else {
                    return new InputConstant(parentId, varDecl, associationDeclaration.getSource());
                }
            case SourceTarget:
                return new InputVariableReference(parentId, variableScope, varDecl, associationDeclaration.getSource());
            default:
                throw new IllegalArgumentException("Unknown type " + type);
        }
    }

    public static InitializedOutputVariable outputOf(
            String parentId,
            VariableScope variableScope,
            VariableDeclaration varDecl,
            AssociationDeclaration associationDeclaration) {

        if (associationDeclaration == null) {
            return new OutputEmpty(parentId, varDecl);
        }
        AssociationDeclaration.Type type = associationDeclaration.getType();
        switch (type) {
            case FromTo:
                if (associationDeclaration.getTarget() == null) {
                    return new OutputEmpty(parentId, varDecl);
                } else {
                    throw new IllegalArgumentException("Cannot assign constant to output variable");
                }
            case SourceTarget:
                return new OutputVariableReference(parentId, variableScope, varDecl, associationDeclaration.getTarget());
            default:
                throw new IllegalArgumentException("Unknown type " + type);
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public static abstract class InitializedInputVariable extends InitializedVariable {

        private final DataInput dataInput;

        public InitializedInputVariable(String parentId, VariableDeclaration varDecl) {
            super(parentId, varDecl);
            getItemDefinition().setId(Ids.dataInputItem(parentId, varDecl.getIdentifier()));
            this.dataInput = dataInputOf(
                    parentId, varDecl.getIdentifier(), getItemDefinition());
        }

        public DataInput getDataInput() {
            return dataInput;
        }

        public abstract DataInputAssociation getDataInputAssociation();
    }

    public static abstract class InitializedOutputVariable extends InitializedVariable {

        private final DataOutput dataOutput;

        public InitializedOutputVariable(String parentId, VariableDeclaration varDecl) {
            super(parentId, varDecl);
            getItemDefinition().setId(Ids.dataOutputItem(parentId, varDecl.getIdentifier()));
            this.dataOutput = dataOutputOf(
                    parentId,
                    varDecl.getIdentifier(),
                    getItemDefinition());
        }

        public DataOutput getDataOutput() {
            return dataOutput;
        }

        public abstract DataOutputAssociation getDataOutputAssociation();
    }

    public static class InputVariableReference extends InitializedInputVariable {

        private final String sourceVariable;
        private final VariableScope scope;

        public InputVariableReference(String parentId, VariableScope variableScope, VariableDeclaration varDecl, String sourceVariable) {
            super(parentId, varDecl);
            this.scope = variableScope;
            this.sourceVariable = sourceVariable;
        }

        public DataInputAssociation getDataInputAssociation() {
            DataInputAssociation dataInputAssociation =
                    bpmn2.createDataInputAssociation();

            dataInputAssociation
                    .getSourceRef()
                    .add(scope.lookup(sourceVariable).getTypedIdentifier());

            dataInputAssociation
                    .setTargetRef(getDataInput());
            return dataInputAssociation;
        }
    }

    public static class OutputVariableReference extends InitializedOutputVariable {

        private final DataOutput dataOutput;
        private final String targetVariable;
        private final VariableScope scope;

        public OutputVariableReference(String parentId, VariableScope scope, VariableDeclaration varDecl, String targetVariable) {
            super(parentId, varDecl);
            this.scope = scope;
            this.targetVariable = targetVariable;
            this.dataOutput = dataOutputOf(
                    parentId,
                    varDecl.getIdentifier(),
                    getItemDefinition());
        }

        public DataOutput getDataOutput() {
            return dataOutput;
        }

        public DataOutputAssociation getDataOutputAssociation() {
            VariableScope.Variable variable = scope.lookup(targetVariable);
            return associationOf(variable.getTypedIdentifier(), dataOutput);
        }
    }

    public static class InputEmpty extends InitializedInputVariable {

        public InputEmpty(String parentId, VariableDeclaration varDecl) {
            super(parentId, varDecl);
        }

        @Override
        public DataInputAssociation getDataInputAssociation() {
            return null;
        }
    }

    public static class InputConstant extends InitializedInputVariable {

        final String expression;

        public InputConstant(String parentId, VariableDeclaration varDecl, String expression) {
            super(parentId, varDecl);
            this.expression = expression;
        }

        public DataInputAssociation getDataInputAssociation() {
            DataInputAssociation dataInputAssociation =
                    bpmn2.createDataInputAssociation();

            Assignment assignment = bpmn2.createAssignment();
            String id = getDataInput().getId();

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
                    .setTargetRef(getDataInput());
            return dataInputAssociation;
        }

        private String decode(String text) {
            try {
                return URLDecoder.decode(text, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(text, e);
            }
        }
    }

    public static class OutputEmpty extends InitializedOutputVariable {

        public OutputEmpty(String parentId, VariableDeclaration varDecl) {
            super(parentId, varDecl);
        }

        @Override
        public DataOutputAssociation getDataOutputAssociation() {
            return null;
        }
    }

    private static DataInput dataInputOf(String parentId, String identifier, ItemDefinition itemDefinition) {
        DataInput dataInput = bpmn2.createDataInput();
        dataInput.setId(Ids.dataInput(parentId, identifier));
        dataInput.setName(identifier);
        dataInput.setItemSubjectRef(itemDefinition);
        CustomAttribute.dtype.of(dataInput).set(itemDefinition.getStructureRef());
        return dataInput;
    }

    private static DataOutput dataOutputOf(String parentId, String identifier, ItemDefinition itemDefinition) {
        DataOutput dataOutput = bpmn2.createDataOutput();
        dataOutput.setId(Ids.dataOutput(parentId, identifier));
        dataOutput.setName(identifier);
        dataOutput.setItemSubjectRef(itemDefinition);
        CustomAttribute.dtype.of(dataOutput).set(itemDefinition.getStructureRef());
        return dataOutput;
    }

    private static DataOutputAssociation associationOf(Property source, DataOutput dataOutput) {
        DataOutputAssociation dataOutputAssociation =
                bpmn2.createDataOutputAssociation();

        dataOutputAssociation
                .getSourceRef()
                .add(dataOutput);

        dataOutputAssociation
                .setTargetRef(source);
        return dataOutputAssociation;
    }
}
