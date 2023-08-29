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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasTypeRefs;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.resource.i18n.DMNAPIConstants;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.dmn.api.definition.model.common.DomainObjectSearcherHelper.matches;
import static org.kie.workbench.common.dmn.api.definition.model.common.HasTypeRefHelper.getNotNullHasTypeRefs;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        i18n = @I18nSettings(keyPreffix = "org.kie.workbench.common.dmn.api.definition.model.InputClause"),
        startElement = "id")
public class InputClause extends DMNElement implements HasTypeRefs,
                                                       DomainObject,
                                                       HasDomainObject {

    @Category
    private static final String stunnerCategory = Categories.DOMAIN_OBJECTS;

    @Labels
    private static final Set<String> stunnerLabels = new HashSet<>();

    @Property
    @FormField(afterElement = "description", labelKey = "inputExpression")
    protected InputClauseLiteralExpression inputExpression;

    @Property
    @FormField(afterElement = "inputExpression", labelKey = "inputValues")
    protected InputClauseUnaryTests inputValues;

    public InputClause() {
        this(new Id(),
             new Description(),
             new InputClauseLiteralExpression(),
             new InputClauseUnaryTests());
    }

    public InputClause(final Id id,
                       final Description description,
                       final InputClauseLiteralExpression inputExpression,
                       final InputClauseUnaryTests inputValues) {
        super(id,
              description);
        this.inputExpression = inputExpression;
        this.inputValues = inputValues;
    }

    public InputClause copy() {
        return new InputClause(
                new Id(),
                Optional.ofNullable(description).map(Description::copy).orElse(null),
                Optional.ofNullable(inputExpression).map(InputClauseLiteralExpression::copy).orElse(null),
                Optional.ofNullable(inputValues).map(InputClauseUnaryTests::copy).orElse(null)
        );
    }

    public InputClause exactCopy() {
        return new InputClause(
                Optional.ofNullable(id).map(Id::copy).orElse(null),
                Optional.ofNullable(description).map(Description::copy).orElse(null),
                Optional.ofNullable(inputExpression).map(InputClauseLiteralExpression::exactCopy).orElse(null),
                Optional.ofNullable(inputValues).map(InputClauseUnaryTests::exactCopy).orElse(null)
        );
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {
        return new ArrayList<>(getNotNullHasTypeRefs(getInputExpression()));
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

    // -----------------------
    // DMN properties
    // -----------------------

    public InputClauseLiteralExpression getInputExpression() {
        return inputExpression;
    }

    public void setInputExpression(final InputClauseLiteralExpression value) {
        this.inputExpression = value;
    }

    public InputClauseUnaryTests getInputValues() {
        return inputValues;
    }

    public void setInputValues(final InputClauseUnaryTests value) {
        this.inputValues = value;
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
        return DMNAPIConstants.InputClause_DomainObjectName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InputClause)) {
            return false;
        }

        final InputClause that = (InputClause) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (inputExpression != null ? !inputExpression.equals(that.inputExpression) : that.inputExpression != null) {
            return false;
        }
        return inputValues != null ? inputValues.equals(that.inputValues) : that.inputValues == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         inputExpression != null ? inputExpression.hashCode() : 0,
                                         inputValues != null ? inputValues.hashCode() : 0);
    }

    @Override
    public Optional<DomainObject> findDomainObject(final String uuid) {

        if (matches(this, uuid)) {
            return Optional.of(this);
        }

        return inputExpression.findDomainObject(uuid);
    }
}
