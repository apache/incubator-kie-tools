/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.ImportType;
import org.kie.workbench.common.dmn.api.property.dmn.ImportedElement;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Namespace;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

@Portable
@Bindable
@PropertySet
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, startElement = "importType")
public class ImportedValues extends Import implements DMNPropertySet {

    @Name
    @FieldLabel
    public static final transient String propertySetName = "ImportedValues";

    @Property
    @FormField(afterElement = "importType")
    protected ImportedElement importedElement;

    @Property
    @FormField(afterElement = "importedElement")
    protected ExpressionLanguage expressionLanguage;

    public ImportedValues() {
        this(new Namespace(),
             new LocationURI(),
             new ImportType(),
             new ImportedElement(),
             new ExpressionLanguage());
    }

    public ImportedValues(final Namespace namespace,
                          final LocationURI locationURI,
                          final ImportType importType,
                          final ImportedElement importedElement,
                          final ExpressionLanguage expressionLanguage) {
        super(namespace,
              locationURI,
              importType);
        this.importedElement = importedElement;
        this.expressionLanguage = expressionLanguage;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public ImportedElement getImportedElement() {
        return importedElement;
    }

    public void setImportedElement(final ImportedElement importedElement) {
        this.importedElement = importedElement;
    }

    public ExpressionLanguage getExpressionLanguage() {
        return expressionLanguage;
    }

    public void setExpressionLanguage(final ExpressionLanguage expressionLanguage) {
        this.expressionLanguage = expressionLanguage;
    }
}
