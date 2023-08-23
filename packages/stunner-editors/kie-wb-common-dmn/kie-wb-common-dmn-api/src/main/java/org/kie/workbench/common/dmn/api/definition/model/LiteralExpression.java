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
import java.util.Optional;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.HasText;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
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

import static org.kie.workbench.common.dmn.api.definition.model.common.DomainObjectSearcherHelper.matches;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        startElement = "id")
public class LiteralExpression extends Expression implements IsLiteralExpression,
                                                             HasText,
                                                             DomainObject {

    private static final int STATIC_COLUMNS = 1;

    @Category
    private static final String stunnerCategory = Categories.DOMAIN_OBJECTS;

    @Labels
    private static final Set<String> stunnerLabels = new HashSet<>();

    protected Text text;

    protected ImportedValues importedValues;

    @Property
    @FormField(afterElement = "description")
    protected ExpressionLanguage expressionLanguage;

    public LiteralExpression() {
        this(new Id(),
             new Description(),
             new QName(),
             new Text(),
             null,
             new ExpressionLanguage());
    }

    public LiteralExpression(final Id id,
                             final org.kie.workbench.common.dmn.api.property.dmn.Description description,
                             final QName typeRef,
                             final Text text,
                             final ImportedValues importedValues,
                             final ExpressionLanguage expressionLanguage) {
        super(id,
              description,
              typeRef);
        this.text = text;
        this.importedValues = importedValues;
        this.expressionLanguage = expressionLanguage;
    }

    @Override
    public LiteralExpression copy() {
        final LiteralExpression clonedLiteralExpression = new LiteralExpression();
        clonedLiteralExpression.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        clonedLiteralExpression.typeRef = Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        clonedLiteralExpression.componentWidths = new ArrayList<>(componentWidths);
        clonedLiteralExpression.text = Optional.ofNullable(text).map(Text::copy).orElse(null);
        clonedLiteralExpression.importedValues = Optional.ofNullable(importedValues).map(ImportedValues::copy).orElse(null);
        clonedLiteralExpression.expressionLanguage = Optional.ofNullable(expressionLanguage).map(ExpressionLanguage::copy).orElse(null);
        return clonedLiteralExpression;
    }

    @Override
    public LiteralExpression exactCopy() {
        final LiteralExpression exactelyClonedLiteralExpression = new LiteralExpression();
        exactelyClonedLiteralExpression.id = Optional.ofNullable(id).map(Id::copy).orElse(null);
        exactelyClonedLiteralExpression.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        exactelyClonedLiteralExpression.typeRef = Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        exactelyClonedLiteralExpression.componentWidths = new ArrayList<>(componentWidths);
        exactelyClonedLiteralExpression.text = Optional.ofNullable(text).map(Text::copy).orElse(null);
        exactelyClonedLiteralExpression.importedValues = Optional.ofNullable(importedValues).map(ImportedValues::exactCopy).orElse(null);
        exactelyClonedLiteralExpression.expressionLanguage = Optional.ofNullable(expressionLanguage).map(ExpressionLanguage::copy).orElse(null);
        return exactelyClonedLiteralExpression;
    }

    @Override
    public Optional<DomainObject> findDomainObject(final String uuid) {
        if (matches(this, uuid)) {
            return Optional.of(this);
        }
        return Optional.empty();
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

    public ExpressionLanguage getExpressionLanguage() {
        return expressionLanguage;
    }

    public void setExpressionLanguage(final ExpressionLanguage expressionLanguage) {
        this.expressionLanguage = expressionLanguage;
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
    public int getRequiredComponentWidthCount() {
        return STATIC_COLUMNS;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LiteralExpression)) {
            return false;
        }

        final LiteralExpression that = (LiteralExpression) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (typeRef != null ? !typeRef.equals(that.typeRef) : that.typeRef != null) {
            return false;
        }
        if (componentWidths != null ? !componentWidths.equals(that.componentWidths) : that.componentWidths != null) {
            return false;
        }
        if (text != null ? !text.equals(that.text) : that.text != null) {
            return false;
        }
        if (importedValues != null ? !importedValues.equals(that.importedValues) : that.importedValues != null) {
            return false;
        }
        return expressionLanguage != null ? expressionLanguage.equals(that.expressionLanguage) : that.expressionLanguage == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0,
                                         componentWidths != null ? componentWidths.hashCode() : 0,
                                         text != null ? text.hashCode() : 0,
                                         importedValues != null ? importedValues.hashCode() : 0,
                                         expressionLanguage != null ? expressionLanguage.hashCode() : 0);
    }
}
