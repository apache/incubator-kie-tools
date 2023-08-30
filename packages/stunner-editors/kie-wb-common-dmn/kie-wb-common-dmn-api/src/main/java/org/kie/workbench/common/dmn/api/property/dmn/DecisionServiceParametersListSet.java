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

package org.kie.workbench.common.dmn.api.property.dmn;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.model.Categories;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;

@Portable
@Bindable
@FormDefinition(policy = FieldPolicy.ONLY_MARKED)
public class DecisionServiceParametersListSet implements DMNPropertySet {

    @Category
    private static final String stunnerCategory = Categories.DOMAIN_OBJECTS;

    @Labels
    private static final Set<String> stunnerLabels = new HashSet<>();

    @Property
    @FormField(type = DecisionParametersListFieldType.class)
    @Valid
    private DecisionServiceParametersListHolder value;

    public DecisionServiceParametersListSet() {
        value = new DecisionServiceParametersListHolder();
    }

    public DecisionServiceParametersListHolder getValue() {
        return value;
    }

    public void setValue(final DecisionServiceParametersListHolder value) {
        this.value = value;
    }

    // -----------------------
    // Stunner core properties
    // -----------------------
    public String getStunnerCategory() {
        return stunnerCategory;
    }

    public Set<String> getStunnerLabels() {
        return stunnerLabels;
    }

    public void setDecisionService(final DecisionService decisionService) {
        value.setDecisionService(decisionService);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DecisionServiceParametersListSet that = (DecisionServiceParametersListSet) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
