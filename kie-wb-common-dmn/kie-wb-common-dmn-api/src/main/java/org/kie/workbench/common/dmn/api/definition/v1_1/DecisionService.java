/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.stunner.core.util.HashUtil;

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
             null,
             null,
             null,
             null);
    }

    public DecisionService(final Id id,
                           final Description description,
                           final Name name,
                           final List<DMNElementReference> outputDecision,
                           final List<DMNElementReference> encapsulatedDecision,
                           final List<DMNElementReference> inputDecision,
                           final List<DMNElementReference> inputData) {
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DecisionService)) {
            return false;
        }

        final DecisionService that = (DecisionService) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (outputDecision != null ? !outputDecision.equals(that.outputDecision) : that.outputDecision != null) {
            return false;
        }
        if (encapsulatedDecision != null ? !encapsulatedDecision.equals(that.encapsulatedDecision) : that.encapsulatedDecision != null) {
            return false;
        }
        if (inputDecision != null ? !inputDecision.equals(that.inputDecision) : that.inputDecision != null) {
            return false;
        }
        return inputData != null ? inputData.equals(that.inputData) : that.inputData == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         name != null ? name.hashCode() : 0,
                                         outputDecision != null ? outputDecision.hashCode() : 0,
                                         encapsulatedDecision != null ? encapsulatedDecision.hashCode() : 0,
                                         inputDecision != null ? inputDecision.hashCode() : 0,
                                         inputData != null ? inputDecision.hashCode() : 0);
    }
}
