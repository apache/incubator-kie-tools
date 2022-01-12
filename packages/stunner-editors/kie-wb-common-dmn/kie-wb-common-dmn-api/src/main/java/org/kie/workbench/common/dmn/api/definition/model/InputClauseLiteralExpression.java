/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.api.definition.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.dmn.api.definition.HasText;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.QNameFieldType;
import org.kie.workbench.common.dmn.api.property.dmn.QNameHolder;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
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

import static java.util.Collections.singletonList;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM;

/**
 * This is in essence a clone of {@link LiteralExpression} specifically for {@link InputClause}
 * to expose both {@link Text} and {@link QName} as Form Properties to the Dynamic Forms Engine with specific
 * labels for "Input expression" and its "TypeRef".
 */
@Portable
@Bindable
@Definition
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        i18n = @I18nSettings(keyPreffix = "org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression"),
        startElement = "text")
public class InputClauseLiteralExpression extends DMNModelInstrumentedBase implements IsLiteralExpression,
                                                                                      HasText,
                                                                                      HasTypeRef,
                                                                                      DMNPropertySet,
                                                                                      DomainObject {

    @Category
    private static final String stunnerCategory = Categories.DOMAIN_OBJECTS;

    @Labels
    private static final Set<String> stunnerLabels = new Sets.Builder<String>().build();

    protected Id id;

    protected Description description;

    protected QName typeRef;

    @Property
    @FormField(afterElement = "text", type = QNameFieldType.class)
    @Valid
    protected QNameHolder typeRefHolder;

    @Property
    @FormField(afterElement = "description", labelKey = "text")
    protected Text text;

    protected ImportedValues importedValues;

    public InputClauseLiteralExpression() {
        this(new Id(),
             new Description(),
             new QName(),
             new Text(),
             null);
    }

    public InputClauseLiteralExpression(final Id id,
                                        final Description description,
                                        final QName typeRef,
                                        final Text text,
                                        final ImportedValues importedValues) {
        this.id = id;
        this.description = description;
        this.typeRef = typeRef;
        this.typeRefHolder = new QNameHolder(typeRef);
        this.text = text;
        this.importedValues = importedValues;
    }

    public InputClauseLiteralExpression copy() {
        final InputClauseLiteralExpression clonedInputClauseLiteralExpression = new InputClauseLiteralExpression();
        clonedInputClauseLiteralExpression.description =  Optional.ofNullable(description).map(Description::copy).orElse(null);
        clonedInputClauseLiteralExpression.typeRef =  Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        clonedInputClauseLiteralExpression.typeRefHolder =  Optional.ofNullable(typeRefHolder).map(QNameHolder::copy).orElse(null);
        clonedInputClauseLiteralExpression.text =  Optional.ofNullable(text).map(Text::copy).orElse(null);
        clonedInputClauseLiteralExpression.importedValues = Optional.ofNullable(importedValues).map(ImportedValues::copy).orElse(null);
        return clonedInputClauseLiteralExpression;
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {
        return new ArrayList<>(singletonList(this));
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
    public Id getId() {
        return id;
    }

    @Override
    public Description getDescription() {
        return description;
    }

    public void setDescription(final Description description) {
        this.description = description;
    }

    @Override
    public QName getTypeRef() {
        return typeRefHolder.getValue();
    }

    @Override
    public void setTypeRef(final QName typeRef) {
        this.typeRefHolder.setValue(typeRef);
    }

    @Override
    public Text getText() {
        return text;
    }

    @Override
    public void setText(final Text text) {
        this.text = text;
    }

    @Override
    public ImportedValues getImportedValues() {
        return importedValues;
    }

    public void setImportedValues(final ImportedValues importedValues) {
        this.importedValues = importedValues;
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return this;
    }

    // ------------------
    // Errai Data Binding
    // ------------------
    public QNameHolder getTypeRefHolder() {
        return typeRefHolder;
    }

    public void setTypeRefHolder(final QNameHolder typeRefHolder) {
        this.typeRefHolder = typeRefHolder;
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
        return DMNAPIConstants.LiteralExpression_DomainObjectName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InputClauseLiteralExpression)) {
            return false;
        }

        final InputClauseLiteralExpression that = (InputClauseLiteralExpression) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (typeRef != null ? !typeRef.equals(that.typeRef) : that.typeRef != null) {
            return false;
        }
        if (text != null ? !text.equals(that.text) : that.text != null) {
            return false;
        }
        return importedValues != null ? importedValues.equals(that.importedValues) : that.importedValues == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0,
                                         text != null ? text.hashCode() : 0,
                                         importedValues != null ? importedValues.hashCode() : 0);
    }
}
