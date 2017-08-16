/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

@Portable
public class DecisionService extends NamedElement {

    private List<DMNElementReference> outputDecision;
    private List<DMNElementReference> encapsulatedDecision;
    private List<DMNElementReference> inputDecision;
    private List<DMNElementReference> inputData;

    public DecisionService() {
        this(new Id(),
             new Description(),
             new Name(),
             new ArrayList<>(),
             new ArrayList<>(),
             new ArrayList<>(),
             new ArrayList<>());
    }

    public DecisionService(final @MapsTo("id") Id id,
                           final @MapsTo("description") Description description,
                           final @MapsTo("name") Name name,
                           final @MapsTo("outputDecision") List<DMNElementReference> outputDecision,
                           final @MapsTo("encapsulatedDecision") List<DMNElementReference> encapsulatedDecision,
                           final @MapsTo("inputDecision") List<DMNElementReference> inputDecision,
                           final @MapsTo("inputData") List<DMNElementReference> inputData) {
        super(id,
              description,
              name);
        this.outputDecision = outputDecision;
        this.encapsulatedDecision = encapsulatedDecision;
        this.inputDecision = inputDecision;
        this.inputData = inputData;
    }

    public List<DMNElementReference> getOutputDecision() {
        if (outputDecision == null) {
            outputDecision = new ArrayList<>();
        }
        return this.outputDecision;
    }

    public List<DMNElementReference> getEncapsulatedDecision() {
        if (encapsulatedDecision == null) {
            encapsulatedDecision = new ArrayList<>();
        }
        return this.encapsulatedDecision;
    }

    public List<DMNElementReference> getInputDecision() {
        if (inputDecision == null) {
            inputDecision = new ArrayList<>();
        }
        return this.inputDecision;
    }

    public List<DMNElementReference> getInputData() {
        if (inputData == null) {
            inputData = new ArrayList<>();
        }
        return this.inputData;
    }
}
