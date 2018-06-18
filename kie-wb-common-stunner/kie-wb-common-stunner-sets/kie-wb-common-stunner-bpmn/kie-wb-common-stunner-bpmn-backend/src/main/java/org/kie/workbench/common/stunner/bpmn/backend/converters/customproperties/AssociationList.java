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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class AssociationList {

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
        if (encoded.isEmpty()) {
            return new AssociationList();
        } else {
            return new AssociationList(
                    Arrays.stream(encoded.split(","))
                            .map(AssociationDeclaration::fromString)
                            .collect(toList()));
        }
    }

    public List<AssociationDeclaration> getInputs() {
        return inputs;
    }

    public Stream<AssociationDeclaration> lookupInput(String id) {
        return inputs.stream().filter(in -> in.getTarget().equals(id));
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