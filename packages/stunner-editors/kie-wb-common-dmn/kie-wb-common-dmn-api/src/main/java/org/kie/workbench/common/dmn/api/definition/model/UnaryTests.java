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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.HasText;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
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
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.NONE;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        startElement = "id")
public class UnaryTests extends DMNElement implements IsUnaryTests,
                                                      HasText,
                                                      DomainObject {

    @Category
    private static final String stunnerCategory = Categories.DOMAIN_OBJECTS;

    @Labels
    private static final Set<String> stunnerLabels = new HashSet<>();

    private Text text;

    private ConstraintType constraintType;

    @Property
    @FormField(afterElement = "description")
    protected ExpressionLanguage expressionLanguage;

    public UnaryTests() {
        this(new Id(),
             new Description(),
             new Text(),
             new ExpressionLanguage(),
             NONE);
    }

    public UnaryTests(final Id id,
                      final Description description,
                      final Text text,
                      final ExpressionLanguage expressionLanguage,
                      final ConstraintType constraintType) {
        super(id,
              description);
        this.text = text;
        this.expressionLanguage = expressionLanguage;
        this.constraintType = constraintType;
    }

    public UnaryTests copy() {
        return new UnaryTests(
                new Id(),
                Optional.ofNullable(description).map(Description::copy).orElse(null),
                Optional.ofNullable(text).map(Text::copy).orElse(null),
                Optional.ofNullable(expressionLanguage).map(ExpressionLanguage::copy).orElse(null),
                constraintType
        );
    }

    public UnaryTests exactCopy() {
        return new UnaryTests(
                Optional.ofNullable(id).map(Id::copy).orElse(null),
                Optional.ofNullable(description).map(Description::copy).orElse(null),
                Optional.ofNullable(text).map(Text::copy).orElse(null),
                Optional.ofNullable(expressionLanguage).map(ExpressionLanguage::copy).orElse(null),
                constraintType
        );
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

    @Override
    public Text getText() {
        return text;
    }

    @Override
    public void setText(final Text value) {
        this.text = value;
    }

    public ExpressionLanguage getExpressionLanguage() {
        return expressionLanguage;
    }

    public void setExpressionLanguage(final ExpressionLanguage value) {
        this.expressionLanguage = value;
    }

    public ConstraintType getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(final ConstraintType constraintType) {
        this.constraintType = constraintType;
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
        return DMNAPIConstants.UnaryTests_DomainObjectName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnaryTests)) {
            return false;
        }

        final UnaryTests that = (UnaryTests) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (text != null ? !text.equals(that.text) : that.text != null) {
            return false;
        }
        return expressionLanguage != null ? expressionLanguage.equals(that.expressionLanguage) : that.expressionLanguage == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         text != null ? text.hashCode() : 0,
                                         expressionLanguage != null ? expressionLanguage.hashCode() : 0);
    }
}
