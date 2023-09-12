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

import java.util.Objects;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.HasText;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.dmn.api.property.dmn.ConstraintTypeProperty;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM;

/**
 * This is in essence a clone of {@link UnaryTests} specifically for {@link InputClause}
 * to expose the {@link Text} as a Form Property to the Dynamic Forms Engine with a specific
 * label for "Constraint".
 */
@Portable
@Bindable
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        i18n = @I18nSettings(keyPreffix = "org.kie.workbench.common.dmn.api.definition.model.InputClauseUnaryTests"),
        startElement = "text")
public class InputClauseUnaryTests extends DMNModelInstrumentedBase implements IsUnaryTests,
                                                                               HasText,
                                                                               DMNPropertySet {

    protected Id id;

    @Property
    @FormField(afterElement = "description", labelKey = "text")
    protected Text text;

    @Property
    @FormField(afterElement = "text",
            labelKey = "constraintType",
            type = ListBoxFieldType.class,
            settings = {@FieldParam(name = "addEmptyOption", value = "expression")})
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.dmn.api.property.dmn.dataproviders.ConstraintTypeDataProvider")
    protected ConstraintTypeProperty constraintTypeProperty;

    public InputClauseUnaryTests() {
        this(new Id(),
             new Text(),
             ConstraintType.NONE);
    }

    public InputClauseUnaryTests(final Id id,
                                 final Text text,
                                 final ConstraintType constraintType) {
        this.id = id;
        this.text = text;
        String constraintTypeString = "";
        if (constraintType != null) {
            constraintTypeString = constraintType.value();
        }
        this.constraintTypeProperty = new ConstraintTypeProperty(constraintTypeString);
    }

    public InputClauseUnaryTests copy() {
        return new InputClauseUnaryTests(
                new Id(),
                Optional.ofNullable(text).map(Text::copy).orElse(null),
                ConstraintType.fromString(constraintTypeProperty.getValue())
        );
    }

    public InputClauseUnaryTests exactCopy() {
        return new InputClauseUnaryTests(
                Optional.ofNullable(id).map(Id::copy).orElse(null),
                Optional.ofNullable(text).map(Text::copy).orElse(null),
                ConstraintType.fromString(constraintTypeProperty.getValue())
        );
    }

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Text getText() {
        return text;
    }

    @Override
    public ConstraintType getConstraintType() {
        return ConstraintType.fromString(constraintTypeProperty.getValue());
    }

    public void setConstraintTypeProperty(final ConstraintTypeProperty constraintTypeProperty) {
        this.constraintTypeProperty = constraintTypeProperty;
    }

    public ConstraintTypeProperty getConstraintTypeProperty() {
        return constraintTypeProperty;
    }

    public void setText(final Text value) {
        this.text = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InputClauseUnaryTests)) {
            return false;
        }

        final InputClauseUnaryTests that = (InputClauseUnaryTests) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        if (!Objects.equals(constraintTypeProperty, that.constraintTypeProperty)) {
            return false;
        }

        return text != null ? text.equals(that.text) : that.text == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         text != null ? text.hashCode() : 0,
                                         constraintTypeProperty != null ? constraintTypeProperty.hashCode() : 0);
    }
}
