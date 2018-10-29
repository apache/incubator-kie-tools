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

package org.kie.workbench.common.dmn.api.definition.v1_1;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM;

/**
 * This is in essence a clone of {@link UnaryTests} specifically for {@link OutputClause}
 * to expose the {@link Text} as a Form Property to the Dynamic Forms Engine with a specific
 * label for "Output values".
 */
@Portable
@Bindable
@PropertySet
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        i18n = @I18nSettings(keyPreffix = "org.kie.workbench.common.dmn.api.definition.v1_1.OutputClauseUnaryTests"),
        startElement = "id")
public class OutputClauseUnaryTests extends DMNElement implements IsUnaryTests,
                                                                  DMNPropertySet {

    @Property
    @FormField(afterElement = "description", labelKey = "text")
    protected Text text;

    @Property
    @FormField(afterElement = "text")
    protected ExpressionLanguage expressionLanguage;

    public OutputClauseUnaryTests() {
        this(new Id(),
             new Description(),
             new Text(),
             new ExpressionLanguage());
    }

    public OutputClauseUnaryTests(final Id id,
                                  final Description description,
                                  final Text text,
                                  final ExpressionLanguage expressionLanguage) {
        super(id,
              description);
        this.text = text;
        this.expressionLanguage = expressionLanguage;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public Text getText() {
        return text;
    }

    public void setText(final Text value) {
        this.text = value;
    }

    @Override
    public ExpressionLanguage getExpressionLanguage() {
        return expressionLanguage;
    }

    public void setExpressionLanguage(final ExpressionLanguage value) {
        this.expressionLanguage = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OutputClauseUnaryTests)) {
            return false;
        }

        final OutputClauseUnaryTests that = (OutputClauseUnaryTests) o;

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
