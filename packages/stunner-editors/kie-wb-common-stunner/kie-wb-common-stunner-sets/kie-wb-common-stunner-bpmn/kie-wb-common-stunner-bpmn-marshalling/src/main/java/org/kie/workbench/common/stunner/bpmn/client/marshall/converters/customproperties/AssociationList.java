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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.uberfire.commons.UUID;

import static java.util.stream.Collectors.toList;

public class AssociationList {

    public static final String REGEX_DELIMITER = ",\\[";
    public static final String RANDOM_DELIMITER = UUID.uuid();
    public static final String REPLACE_DELIMITER_AVOID_CONFLICTS = RANDOM_DELIMITER + ",[";
    public static final String REPLACED_DELIMITER = RANDOM_DELIMITER + ",";
    private final List<AssociationDeclaration> inputs;
    private final List<AssociationDeclaration> outputs;

    public AssociationList(List<AssociationDeclaration> inputs, List<AssociationDeclaration> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public AssociationList(List<AssociationDeclaration> all) {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        for (AssociationDeclaration associationDeclaration : all) {
            if (associationDeclaration.getDirection() == AssociationDeclaration.Direction.Input) {
                inputs.add(associationDeclaration);
            } else {
                outputs.add(associationDeclaration);
            }
        }
    }

    public AssociationList() {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }

    public static AssociationList fromString(String encoded) {
        // The change below is to fix SDM issue on Mac OS
        if (encoded == null || encoded.isEmpty()) {
            return new AssociationList();
        } else {
            return new AssociationList(
                    Arrays.stream(encoded.replaceAll(REGEX_DELIMITER, REPLACE_DELIMITER_AVOID_CONFLICTS)
                                    .split(REPLACED_DELIMITER))
                            .map(AssociationDeclaration::fromString)
                            .collect(toList()));

        }
    }

    public List<AssociationDeclaration> getInputs() {
        return inputs;
    }

    public AssociationDeclaration lookupInput(String id) {
        return inputs.stream().filter(in -> in.getTarget().equals(id)).findFirst().orElse(null);
    }

    public List<AssociationDeclaration> lookupOutputs(String id) {
        return outputs.stream().filter(in -> in.getSource().equals(id)).collect(Collectors.toList());
    }

    public List<AssociationDeclaration> getOutputs() {
        return outputs;
    }

    @Override
    public String toString() {
        return Stream.concat(inputs.stream(), outputs.stream())
                .map(AssociationDeclaration::toString)
                .collect(Collectors.joining(","));
    }
}