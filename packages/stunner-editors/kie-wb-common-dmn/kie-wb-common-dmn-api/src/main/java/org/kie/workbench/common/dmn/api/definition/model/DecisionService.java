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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.property.dimensions.DecisionServiceRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceParametersListSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.dmn.api.resource.i18n.DMNAPIConstants;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition()
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        startElement = "id")
@CanContain(roles = {
        "decision"
})
public class DecisionService extends DRGElement implements HasVariable<InformationItemPrimary>,
                                                           DMNViewDefinition<DecisionServiceRectangleDimensionsSet>,
                                                           DomainObject {

    @Category
    private static final String stunnerCategory = Categories.NODES;

    @Labels
    private static final Set<String> stunnerLabels = Stream.of("decision-service").collect(Collectors.toSet());

    @Property
    @FormField(afterElement = "nameHolder")
    @Valid
    protected DecisionServiceParametersListSet decisionServiceParametersList;

    @Property
    @FormField(afterElement = "decisionServiceParametersList")
    @Valid
    protected InformationItemPrimary variable;

    @Property
    @FormField(afterElement = "variable")
    @Valid
    protected StylingSet stylingSet;

    @Property
    protected DecisionServiceRectangleDimensionsSet dimensionsSet;

    @Property
    protected DecisionServiceDividerLineY dividerLineY;

    private List<DMNElementReference> outputDecision;
    private List<DMNElementReference> encapsulatedDecision;
    private List<DMNElementReference> inputDecision;
    private List<DMNElementReference> inputData;

    public DecisionService() {
        this(new Id(),
             new Description(),
             new Name(),
             new InformationItemPrimary(),
             null,
             null,
             null,
             null,
             new StylingSet(),
             new DecisionServiceRectangleDimensionsSet(),
             new DecisionServiceDividerLineY());
    }

    public DecisionService(final Id id,
                           final Description description,
                           final Name name,
                           final InformationItemPrimary variable,
                           final List<DMNElementReference> outputDecision,
                           final List<DMNElementReference> encapsulatedDecision,
                           final List<DMNElementReference> inputDecision,
                           final List<DMNElementReference> inputData,
                           final StylingSet stylingSet,
                           final DecisionServiceRectangleDimensionsSet dimensionsSet,
                           final DecisionServiceDividerLineY dividerLineY) {
        super(id,
              description,
              name);
        this.variable = variable;
        this.outputDecision = outputDecision;
        this.encapsulatedDecision = encapsulatedDecision;
        this.inputDecision = inputDecision;
        this.inputData = inputData;
        this.stylingSet = stylingSet;
        this.dimensionsSet = dimensionsSet;
        this.dividerLineY = dividerLineY;
        this.decisionServiceParametersList = new DecisionServiceParametersListSet();
        this.decisionServiceParametersList.setDecisionService(this);

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
    public DecisionServiceRectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(final DecisionServiceRectangleDimensionsSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    public DecisionServiceDividerLineY getDividerLineY() {
        return dividerLineY;
    }

    public void setDividerLineY(final DecisionServiceDividerLineY dividerY) {
        this.dividerLineY = dividerY;
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

    public DecisionServiceParametersListSet getDecisionServiceParametersList() {
        return decisionServiceParametersList;
    }

    public void setDecisionServiceParametersList(final DecisionServiceParametersListSet decisionParametersList) {
        this.decisionServiceParametersList = decisionParametersList;
    }

    // ------------------------------------------------------
    // DomainObject requirements - to use in Properties Panel
    // ------------------------------------------------------
    @Override
    public String getDomainObjectUUID() {
        return getId().getValue();
    }

    @Override
    public String getDomainObjectNameTranslationKey() {
        return DMNAPIConstants.DecisionService_DomainObjectName;
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
        if (nameHolder != null ? !nameHolder.equals(that.nameHolder) : that.nameHolder != null) {
            return false;
        }
        if (variable != null ? !variable.equals(that.variable) : that.variable != null) {
            return false;
        }
        if (dividerLineY != null ? !dividerLineY.equals(that.dividerLineY) : that.dividerLineY != null) {
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
        if (linksHolder != null ? !linksHolder.equals(that.linksHolder) : that.linksHolder != null) {
            return false;
        }
        if (decisionServiceParametersList != null ? !decisionServiceParametersList.equals(that.decisionServiceParametersList) : that.decisionServiceParametersList != null) {
            return false;
        }
        if (stylingSet != null ? !stylingSet.equals(that.stylingSet) : that.stylingSet != null) {
            return false;
        }
        return inputData != null ? inputData.equals(that.inputData) : that.inputData == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         nameHolder != null ? nameHolder.hashCode() : 0,
                                         variable != null ? variable.hashCode() : 0,
                                         dividerLineY != null ? dividerLineY.hashCode() : 0,
                                         outputDecision != null ? outputDecision.hashCode() : 0,
                                         encapsulatedDecision != null ? encapsulatedDecision.hashCode() : 0,
                                         stylingSet != null ? stylingSet.hashCode() : 0,
                                         inputDecision != null ? inputDecision.hashCode() : 0,
                                         inputData != null ? inputData.hashCode() : 0,
                                         linksHolder != null ? linksHolder.hashCode() : 0,
                                         decisionServiceParametersList != null ? decisionServiceParametersList.hashCode() : 0);
    }

    private void setVariableParent() {
        Optional.ofNullable(variable).ifPresent(v -> v.setParent(this));
    }
}
