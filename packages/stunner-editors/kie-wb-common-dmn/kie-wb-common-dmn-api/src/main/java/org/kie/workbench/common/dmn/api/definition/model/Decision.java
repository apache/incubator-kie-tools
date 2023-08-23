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
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Question;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.dmn.api.resource.i18n.DMNAPIConstants;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition()
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        startElement = "id")
public class Decision extends DRGElement implements DomainObject,
                                                    HasExpression,
                                                    DMNViewDefinition<GeneralRectangleDimensionsSet>,
                                                    HasVariable<InformationItemPrimary> {

    @Category
    private static final String stunnerCategory = Categories.NODES;

    @Labels
    private static final Set<String> stunnerLabels = Stream.of("decision").collect(Collectors.toSet());

    @Property
    @FormField(afterElement = "nameHolder", type = TextAreaFieldType.class)
    @Valid
    protected Question question;

    @Property
    @FormField(afterElement = "question", type = TextAreaFieldType.class)
    @Valid
    protected AllowedAnswers allowedAnswers;

    @Property
    @FormField(afterElement = "allowedAnswers")
    @Valid
    protected InformationItemPrimary variable;

    protected Expression expression;

    @Property
    @FormField
    @Valid
    protected StylingSet stylingSet;

    @Property
    protected GeneralRectangleDimensionsSet dimensionsSet;

    public Decision() {
        this(new Id(),
             new org.kie.workbench.common.dmn.api.property.dmn.Description(),
             new Name(),
             new Question(),
             new AllowedAnswers(),
             new InformationItemPrimary(),
             null,
             new StylingSet(),
             new GeneralRectangleDimensionsSet());
    }

    public Decision(final Id id,
                    final org.kie.workbench.common.dmn.api.property.dmn.Description description,
                    final Name name,
                    final Question question,
                    final AllowedAnswers allowedAnswers,
                    final InformationItemPrimary variable,
                    final Expression expression,
                    final StylingSet stylingSet,
                    final GeneralRectangleDimensionsSet dimensionsSet) {
        super(id,
              description,
              name);
        this.question = question;
        this.allowedAnswers = allowedAnswers;
        this.variable = variable;
        this.expression = expression;
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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(final Question question) {
        this.question = question;
    }

    public AllowedAnswers getAllowedAnswers() {
        return allowedAnswers;
    }

    public void setAllowedAnswers(final AllowedAnswers allowedAnswers) {
        this.allowedAnswers = allowedAnswers;
    }

    @Override
    public InformationItemPrimary getVariable() {
        return variable;
    }

    @Override
    public void setVariable(final InformationItemPrimary variable) {
        this.variable = variable;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return this;
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
        return DMNAPIConstants.Decision_DomainObjectName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Decision)) {
            return false;
        }

        Decision that = (Decision) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (nameHolder != null ? !nameHolder.equals(that.nameHolder) : that.nameHolder != null) {
            return false;
        }
        if (question != null ? !question.equals(that.question) : that.question != null) {
            return false;
        }
        if (allowedAnswers != null ? !allowedAnswers.equals(that.allowedAnswers) : that.allowedAnswers != null) {
            return false;
        }
        if (variable != null ? !variable.equals(that.variable) : that.variable != null) {
            return false;
        }
        if (expression != null ? !expression.equals(that.expression) : that.expression != null) {
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
                                         question != null ? question.hashCode() : 0,
                                         allowedAnswers != null ? allowedAnswers.hashCode() : 0,
                                         variable != null ? variable.hashCode() : 0,
                                         expression != null ? expression.hashCode() : 0,
                                         stylingSet != null ? stylingSet.hashCode() : 0,
                                         dimensionsSet != null ? dimensionsSet.hashCode() : 0,
                                         linksHolder != null ? linksHolder.hashCode() : 0);
    }

    private void setVariableParent() {
        Optional.ofNullable(variable).ifPresent(v -> v.setParent(this));
    }
}
