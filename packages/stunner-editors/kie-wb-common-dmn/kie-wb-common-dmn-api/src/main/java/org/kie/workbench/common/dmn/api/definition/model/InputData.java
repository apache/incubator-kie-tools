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
package org.kie.workbench.common.dmn.api.definition.model;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition()
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        startElement = "id")
public class InputData extends DRGElement implements DMNViewDefinition<GeneralRectangleDimensionsSet>,
                                                     HasVariable<InformationItemPrimary> {

    @Category
    private static final String stunnerCategory = Categories.NODES;

    @Labels
    private static final Set<String> stunnerLabels = Stream.of("input-data").collect(Collectors.toSet());

    @Property
    @FormField(afterElement = "nameHolder")
    @Valid
    protected InformationItemPrimary variable;

    @Property
    @FormField(afterElement = "variable")
    @Valid
    protected StylingSet stylingSet;

    @Property
    protected GeneralRectangleDimensionsSet dimensionsSet;

    public InputData() {
        this(new Id(),
             new org.kie.workbench.common.dmn.api.property.dmn.Description(),
             new Name(),
             new InformationItemPrimary(),
             new StylingSet(),
             new GeneralRectangleDimensionsSet());
    }

    public InputData(final Id id,
                     final org.kie.workbench.common.dmn.api.property.dmn.Description description,
                     final Name name,
                     final InformationItemPrimary variable,
                     final StylingSet stylingSet,
                     final GeneralRectangleDimensionsSet dimensionsSet) {
        super(id,
              description,
              name);
        this.variable = variable;
        this.stylingSet = stylingSet;
        this.dimensionsSet = dimensionsSet;

        setVariableParent();
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

    @Override
    public StylingSet getStylingSet() {
        return stylingSet;
    }

    public void setStylingSet(final StylingSet stylingSet) {
        this.stylingSet = stylingSet;
    }

    @Override
    public GeneralRectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(final GeneralRectangleDimensionsSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public InformationItemPrimary getVariable() {
        return variable;
    }

    @Override
    public void setVariable(final InformationItemPrimary variable) {
        this.variable = variable;
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InputData)) {
            return false;
        }

        final InputData that = (InputData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (nameHolder != null ? !nameHolder.equals(that.nameHolder) : that.nameHolder != null) {
            return false;
        }
        if (variable != null ? !variable.equals(that.variable) : that.variable != null) {
            return false;
        }
        if (stylingSet != null ? !stylingSet.equals(that.stylingSet) : that.stylingSet != null) {
            return false;
        }
        if (linksHolder != null ? !linksHolder.equals(that.linksHolder) : that.linksHolder != null) {
            return false;
        }
        return dimensionsSet != null ? dimensionsSet.equals(that.dimensionsSet) : that.dimensionsSet == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         nameHolder != null ? nameHolder.hashCode() : 0,
                                         variable != null ? variable.hashCode() : 0,
                                         stylingSet != null ? stylingSet.hashCode() : 0,
                                         dimensionsSet != null ? dimensionsSet.hashCode() : 0,
                                         linksHolder != null ? linksHolder.hashCode() : 0);
    }

    private void setVariableParent() {
        Optional.ofNullable(variable).ifPresent(v -> v.setParent(this));
    }
}
