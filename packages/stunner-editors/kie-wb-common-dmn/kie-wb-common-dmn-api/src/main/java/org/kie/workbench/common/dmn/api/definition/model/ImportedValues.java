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
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
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

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        startElement = "id")
public class ImportedValues extends Import implements DMNPropertySet,
                                                      DomainObject {

    @Category
    private static final String stunnerCategory = Categories.DOMAIN_OBJECTS;

    @Labels
    private static final Set<String> stunnerLabels = new HashSet<>();

    private final String UUID = org.kie.workbench.common.stunner.core.util.UUID.uuid();

    protected String importedElement;

    @Property
    @FormField
    protected ExpressionLanguage expressionLanguage;

    public ImportedValues() {
        this(null,
             new LocationURI(),
             null,
             null,
             null);
    }

    public ImportedValues(final String namespace,
                          final LocationURI locationURI,
                          final String importType,
                          final String importedElement,
                          final ExpressionLanguage expressionLanguage) {
        super(namespace,
              locationURI,
              importType);
        this.importedElement = importedElement;
        this.expressionLanguage = expressionLanguage;
    }

    public ImportedValues copy() {
        ImportedValues clonedImportedValues = new ImportedValues();
        clonedImportedValues.id = new Id();
        clonedImportedValues.namespace = namespace;
        clonedImportedValues.locationURI = Optional.ofNullable(locationURI).map(LocationURI::copy).orElse(null);
        clonedImportedValues.importType = importType;
        clonedImportedValues.importedElement = importedElement;
        clonedImportedValues.expressionLanguage = Optional.ofNullable(expressionLanguage).map(ExpressionLanguage::copy).orElse(null);
        return clonedImportedValues;
    }

    public ImportedValues exactCopy() {
        ImportedValues clonedImportedValues = new ImportedValues();
        clonedImportedValues.id = Optional.ofNullable(id).map(Id::copy).orElse(null);
        clonedImportedValues.namespace = namespace;
        clonedImportedValues.locationURI = Optional.ofNullable(locationURI).map(LocationURI::copy).orElse(null);
        clonedImportedValues.importType = importType;
        clonedImportedValues.importedElement = importedElement;
        clonedImportedValues.expressionLanguage = Optional.ofNullable(expressionLanguage).map(ExpressionLanguage::copy).orElse(null);
        return clonedImportedValues;
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

    public String getImportedElement() {
        return importedElement;
    }

    public void setImportedElement(final String importedElement) {
        this.importedElement = importedElement;
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
        return UUID;
    }

    @Override
    public String getDomainObjectNameTranslationKey() {
        return DMNAPIConstants.ImportedValues_DomainObjectName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportedValues)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final ImportedValues that = (ImportedValues) o;

        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) {
            return false;
        }
        if (locationURI != null ? !locationURI.equals(that.locationURI) : that.locationURI != null) {
            return false;
        }
        if (locationURI != null ? !locationURI.equals(that.locationURI) : that.locationURI != null) {
            return false;
        }
        if (importedElement != null ? !importedElement.equals(that.importedElement) : that.importedElement != null) {
            return false;
        }
        return expressionLanguage != null ? expressionLanguage.equals(that.expressionLanguage) : that.expressionLanguage == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(namespace != null ? namespace.hashCode() : 0,
                                         locationURI != null ? locationURI.hashCode() : 0,
                                         importType != null ? importType.hashCode() : 0,
                                         importedElement != null ? importedElement.hashCode() : 0,
                                         expressionLanguage != null ? expressionLanguage.hashCode() : 0);
    }
}
