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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.DataObject;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.InitializedVariable.InitializedInputVariable;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.InitializedVariable.InitializedOutputVariable;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.VariableScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;

/**
 * Parses and generate the AssignmentsInfo string
 * <p>
 * AssignmentsInfo represents variables, their types, and their assignments
 * in a Task.
 * <p>
 * It has historically been represented through a delimited String.
 * The format of such a String follows the following EBNF:
 *
 * <pre>
 * AssignmentInfoString ::= InputDeclarations ‘|’ OutputDeclarations ‘|’ Assignments
 * InputDeclarations ::= (Declaration (‘,’ Declaration)*)?
 * OutputDeclarations ::= (Declaration (‘,’ Declaration)*)?
 * Declaration ::= ( identifier ( ‘:’ type )? )
 * Assignments ::= (Assignment (‘,’ Assignment)* )?;
 * Assignment ::= InputAssignment | OutputAssignment;
 * InputAssignment ::= ‘[din]’ identifier ‘->’ identifier;
 * OutputAssignment ::= ‘[dout]’ identifier ‘->’ identifier;
 *
 * </pre>
 * Where identifier is a valid identifier, and type is a valid identifier
 * representing a data type.
 * Semantically, the identifiers that has been declared in an InputDeclaration
 * or an OutputDeclaration, or in a ProcessVariable that contains this Task
 * <p>
 * <p>
 * The input String follows the following rules:
 *
 * <pre>
 * |      | in | inSet | out | outSet | assignments |
 * +------+----+-------+-----+--------+-------------+
 * |Catch |    |       |  x  |        |      x      |
 * |Throw | x  |       |     |        |      x      |
 * |Other |    |  x    |     |  x     |      x      |
 * +------+----+-------+-----+--------+-------------+
 * </pre>
 * <p>
 * Where Other are CallActivity, Task, SubProcess.
 * <p>
 * The distinction between input/inputSet,
 * and output/outputSet is really not necessary
 * because we just need to know what are the inputs
 * and what are the outputs.
 * <p>
 * Thus, we can just use one field for inputs, and one field for outputs.
 */
public class ParsedAssignmentsInfo {

    private final DeclarationList inputs;
    private final DeclarationList outputs;
    private final AssociationList associations;
    private final boolean alternativeEncoding;

    public ParsedAssignmentsInfo(DeclarationList inputs, DeclarationList outputs, AssociationList associations, boolean alternativeEncoding) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.associations = associations;
        this.alternativeEncoding = alternativeEncoding;
    }

    public static ParsedAssignmentsInfo of(AssignmentsInfo assignmentsInfo) {
        return fromString(assignmentsInfo.getValue());
    }

    public static ParsedAssignmentsInfo fromString(String encoded) {
        DeclarationList inputs = new DeclarationList();
        DeclarationList outputs = new DeclarationList();
        AssociationList associations = new AssociationList();

        if (encoded.isEmpty()) {
            return new ParsedAssignmentsInfo(
                    inputs,
                    outputs,
                    associations,
                    false
            );
        }

        String[] split = encoded.split("\\|", -1 /* preserve empty fields */);
        if (split.length == 0) {
            return new ParsedAssignmentsInfo(
                    inputs,
                    outputs,
                    associations,
                    false
            );
        }

        boolean alternativeEncoding = false;
        String in = split[0];
        String out = split[2];
        String assoc = split.length < 5 ? null : split[4];

        if (in.isEmpty() && out.isEmpty()) {
            if (!split[1].isEmpty() || !split[3].isEmpty()) {
                alternativeEncoding = true;
                in = split[1];
                out = split[3];
            }
        }

        DeclarationList inputList = DeclarationList.fromString(in);
        DeclarationList outputList = DeclarationList.fromString(out);
        AssociationList associationList = AssociationList.fromString(assoc);
        return new ParsedAssignmentsInfo(
                inputList,
                outputList,
                associationList,
                alternativeEncoding);
    }

    private static String encodeStringRepresentation(
            DeclarationList inputs,
            DeclarationList outputs,
            AssociationList associations,
            boolean alternativeEncoding) {
        if (alternativeEncoding) {
            return nonCanonicalEncoding(inputs, outputs, associations);
        } else {
            return canonicalEncoding(inputs, outputs, associations);
        }
    }

    private static String canonicalEncoding(DeclarationList inputs, DeclarationList outputs, AssociationList associations) {
        return Stream.of(
                inputs.toString(),
                "",
                outputs.toString(),
                "",
                associations.toString())
                .collect(Collectors.joining("|"));
    }

    private static String nonCanonicalEncoding(DeclarationList inputs, DeclarationList outputs, AssociationList associations) {
        return Stream.of("",
                         inputs.toString(),
                         "",
                         outputs.toString(),
                         associations.toString())
                .collect(Collectors.joining("|"));
    }

    public DeclarationList getInputs() {
        return inputs;
    }

    public DeclarationList getOutputs() {
        return outputs;
    }

    public AssociationList getAssociations() {
        return associations;
    }

    public List<InitializedInputVariable> createInitializedInputVariables(String parentId, VariableScope variableScope, Set<DataObject> dataObjects) {
        return getInputs()
                .getDeclarations()
                .stream()
                .map(varDecl -> InitializedVariable.inputOf(
                        parentId,
                        variableScope,
                        varDecl,
                        associations.lookupInput(varDecl.getTypedIdentifier().getName()), dataObjects))
                .collect(Collectors.toList());
    }

    public List<InitializedOutputVariable> createInitializedOutputVariables(String parentId, VariableScope variableScope, Set<DataObject> dataObjects) {
        List<InitializedOutputVariable> initializedOutputVariables = new ArrayList<>();
        Set<String> processedVariables = new HashSet<>();
        getOutputs()
                .getDeclarations()
                .forEach(varDecl -> {
                    String varName = varDecl.getTypedIdentifier().getName();

                    if (processedVariables.contains(varName)) {
                        return;
                    }
                    processedVariables.add(varName);

                    List<AssociationDeclaration> associationDeclarations = associations.lookupOutputs(varName);
                    if (associationDeclarations.isEmpty()) {
                        addUnassociatedVariable(parentId, variableScope, dataObjects, initializedOutputVariables, varDecl);
                    } else {
                        addVariable(parentId, variableScope, dataObjects, initializedOutputVariables, associationDeclarations);
                    }
                });

        return initializedOutputVariables;
    }

    private void addVariable(String parentId, VariableScope variableScope, Set<DataObject> dataObjects, List<InitializedOutputVariable> initializedOutputVariables, List<AssociationDeclaration> declarations) {
        initializedOutputVariables.addAll(declarations
                                                  .stream()
                                                  .distinct()
                                                  .map(outputDec -> InitializedVariable.outputOf(
                                                          parentId,
                                                          variableScope,
                                                          getOutputs().lookup(outputDec.getSource().replace(" ", "-")),
                                                          outputDec,
                                                          dataObjects))
                                                  .collect(Collectors.toList()));
    }

    private void addUnassociatedVariable(String parentId, VariableScope variableScope, Set<DataObject> dataObjects, List<InitializedOutputVariable> initializedOutputVariables, VariableDeclaration varDecl) {
        initializedOutputVariables.add(InitializedVariable.outputOf(
                parentId,
                variableScope,
                varDecl,
                null,
                dataObjects));
    }

    public boolean isEmpty() {
        return inputs.getDeclarations().isEmpty() && outputs.getDeclarations().isEmpty() &&
                associations.getInputs().isEmpty() && associations.getOutputs().isEmpty();
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "";
        }
        return encodeStringRepresentation(
                inputs,
                outputs,
                associations,
                alternativeEncoding);
    }
}
