/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.VariableScope;
import org.kie.workbench.common.stunner.core.util.Patterns;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public abstract class InitializedVariable {

    private final String identifier;
    private final String type;
    private ItemDefinition itemDefinition;
    private static final Pattern EXPRESSION = Pattern.compile(Patterns.EXPRESSION);

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
            AssociationDeclaration associationDeclaration,
            Set<DataObject> dataObject) {

        if (associationDeclaration == null) {
            return new InputEmpty(parentId, varDecl);
        }
        AssociationDeclaration.Type type = associationDeclaration.getType();
        switch (type) {
            case FromTo:
                if (associationDeclaration.getTarget() == null) {
                    return new InputEmpty(parentId, varDecl);
                } else {
                    return createCustomInput(parentId, varDecl, associationDeclaration.getSource());
                }
            case SourceTarget:
                return new InputVariableReference(parentId, variableScope, varDecl, associationDeclaration.getSource(), dataObject);
            default:
                throw new IllegalArgumentException("Unknown type " + type);
        }
    }

    public static InitializedInputVariable createCustomInput(String parentId, VariableDeclaration varDecl, String expression) {
        String decodedExpression = decode(expression);
        if (EXPRESSION.matcher(decodedExpression).matches()) {
            return new InputExpression(parentId, varDecl, decodedExpression);
        } else {
            return new InputConstant(parentId, varDecl, decodedExpression);
        }
    }

    private static String decode(String text) {
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(text, e);
        }
    }

    public static InitializedOutputVariable outputOf(
            String parentId,
            VariableScope variableScope,
            VariableDeclaration varDecl,
            AssociationDeclaration associationDeclaration,
            Set<DataObject> dataObject) {

        if (associationDeclaration == null) {
            return new OutputEmpty(parentId, varDecl);
        }
        AssociationDeclaration.Type type = associationDeclaration.getType();
        switch (type) {
            case FromTo:
                if (associationDeclaration.getTarget() == null) {
                    return new OutputEmpty(parentId, varDecl);
                } else {
                    return new OutputExpression(parentId, varDecl, associationDeclaration.getTarget());
                }
            case SourceTarget:
                return new OutputVariableReference(parentId, variableScope, varDecl, associationDeclaration.getTarget(), dataObject);
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

    public abstract static class InitializedInputVariable extends InitializedVariable {

        private final DataInput dataInput;

        public InitializedInputVariable(String parentId, VariableDeclaration varDecl) {
            super(parentId, varDecl);
            getItemDefinition().setId(Ids.dataInputItem(parentId, varDecl.getIdentifier()));
            this.dataInput = dataInputOf(
                    parentId,
                    varDecl.getIdentifier(),
                    varDecl.getTypedIdentifier().getName(),
                    getItemDefinition());
        }

        public DataInput getDataInput() {
            return dataInput;
        }

        public abstract DataInputAssociation getDataInputAssociation();

        private static DataInput dataInputOf(String parentId, String identifier, String name, ItemDefinition itemDefinition) {
            DataInput dataInput = bpmn2.createDataInput();
            dataInput.setId(Ids.dataInput(parentId, identifier));
            dataInput.setName(name);
            dataInput.setItemSubjectRef(itemDefinition);
            CustomAttribute.dtype.of(dataInput).set(itemDefinition.getStructureRef());
            return dataInput;
        }
    }

    public abstract static class InitializedOutputVariable extends InitializedVariable {

        private final DataOutput dataOutput;

        public InitializedOutputVariable(String parentId, VariableDeclaration varDecl) {
            super(parentId, varDecl);
            getItemDefinition().setId(Ids.dataOutputItem(parentId, varDecl.getIdentifier()));
            this.dataOutput = dataOutputOf(
                    parentId,
                    varDecl.getIdentifier(),
                    varDecl.getTypedIdentifier().getName(),
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
        private final Set<DataObject> dataObjectSet;

        public InputVariableReference(String parentId, VariableScope variableScope, VariableDeclaration varDecl,
                                      String sourceVariable, Set<DataObject> dataObjectSet) {
            super(parentId, varDecl);
            this.scope = variableScope;
            this.sourceVariable = sourceVariable;
            this.dataObjectSet = dataObjectSet;
        }

        public DataInputAssociation getDataInputAssociation() {
            DataInputAssociation dataInputAssociation = bpmn2.createDataInputAssociation();
            Optional<ItemAwareElement> typedIdentifier = Optional.empty();
            Optional<VariableScope.Variable> maybeVariable = scope.lookup(sourceVariable);

            if (maybeVariable.isPresent()) {
                typedIdentifier = Optional.of(maybeVariable.get().getTypedIdentifier());
            } else {
                for (DataObject dataObject : dataObjectSet) {
                    if (dataObject.getId().equals(sourceVariable)) {
                        typedIdentifier = Optional.of(dataObject);
                        break;
                    }
                }
            }

            return typedIdentifier
                    .map(variable -> dataInputAssociation.getSourceRef().add(variable))
                    .map(added -> {
                        dataInputAssociation.setTargetRef(getDataInput());
                        return added;
                    })
                    .map(added -> dataInputAssociation)
                    .orElse(null);
        }
    }

    public static class OutputVariableReference extends InitializedOutputVariable {

        private final DataOutput dataOutput;
        private final String targetVariable;
        private final VariableScope scope;
        private final Set<DataObject> dataObjects;

        public OutputVariableReference(String parentId, VariableScope scope, VariableDeclaration varDecl,
                                       String targetVariable, Set<DataObject> dataObjects) {
            super(parentId, varDecl);
            this.scope = scope;
            this.targetVariable = targetVariable;
            this.dataObjects = dataObjects;
            this.dataOutput = dataOutputOf(
                    parentId,
                    varDecl.getIdentifier(),
                    varDecl.getTypedIdentifier().getName(),
                    getItemDefinition());
        }

        @Override
        public DataOutput getDataOutput() {
            return dataOutput;
        }

        public DataOutputAssociation getDataOutputAssociation() {
            Optional<ItemAwareElement> typedIdentifier = Optional.empty();

            Optional<VariableScope.Variable> maybeVariable = scope.lookup(targetVariable);
            if (maybeVariable.isPresent()) {
                typedIdentifier = Optional.of(maybeVariable.get().getTypedIdentifier());
            } else {
                for (DataObject dataObject : dataObjects) {
                    if (dataObject.getId().equals(targetVariable)) {
                        typedIdentifier = Optional.of(dataObject);
                        break;
                    }
                }
            }
            return typedIdentifier
                    .map(variable -> associationOf(variable, dataOutput))
                    .orElse(null);
        }

        private static DataOutputAssociation associationOf(ItemAwareElement source, DataOutput dataOutput) {
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

            assignment.setFrom(createCdataExpression(expression));

            dataInputAssociation
                    .getAssignment().add(assignment);

            dataInputAssociation
                    .setTargetRef(getDataInput());
            return dataInputAssociation;
        }
    }

    public static class InputExpression extends InitializedInputVariable {

        final String expression;

        public InputExpression(String parentId, VariableDeclaration varDecl, String expression) {
            super(parentId, varDecl);
            this.expression = expression;
        }

        @Override
        public DataInputAssociation getDataInputAssociation() {
            DataInputAssociation dataInputAssociation = bpmn2.createDataInputAssociation();

            Assignment assignment = bpmn2.createAssignment();
            String id = getDataInput().getId();

            assignment.setFrom(createCdataExpression(expression));

            FormalExpression toExpr = bpmn2.createFormalExpression();
            toExpr.setBody(id);
            assignment.setTo(toExpr);

            dataInputAssociation.getAssignment().add(assignment);

            dataInputAssociation.setTargetRef(getDataInput());
            return dataInputAssociation;
        }
    }

    public static class OutputExpression extends InitializedOutputVariable {

        final String expression;

        public OutputExpression(String parentId, VariableDeclaration varDecl, String expression) {
            super(parentId, varDecl);
            this.expression = decode(expression);
        }

        @Override
        public DataOutputAssociation getDataOutputAssociation() {
            DataOutputAssociation dataOutputAssociation = bpmn2.createDataOutputAssociation();

            Assignment assignment = bpmn2.createAssignment();
            String id = getDataOutput().getId();

            FormalExpression toExpr = bpmn2.createFormalExpression();
            toExpr.setBody(id);
            assignment.setFrom(toExpr);

            assignment.setTo(createCdataExpression(expression));

            dataOutputAssociation.getAssignment().add(assignment);

            dataOutputAssociation.getSourceRef().add(getDataOutput());
            return dataOutputAssociation;
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

    private static DataOutput dataOutputOf(String parentId, String identifier, String name, ItemDefinition itemDefinition) {
        DataOutput dataOutput = bpmn2.createDataOutput();
        dataOutput.setId(Ids.dataOutput(parentId, identifier));
        dataOutput.setName(name);
        dataOutput.setItemSubjectRef(itemDefinition);
        CustomAttribute.dtype.of(dataOutput).set(itemDefinition.getStructureRef());
        return dataOutput;
    }

    private static FormalExpression createCdataExpression(String value) {
        FormalExpression expression = bpmn2.createFormalExpression();
        expression.setBody(asCData(value));
        return expression;
    }
}
