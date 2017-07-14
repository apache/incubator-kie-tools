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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
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
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, startElement = "id")
public class LiteralExpression extends Expression implements DMNPropertySet {

    @Name
    @FieldLabel
    public static final transient String propertySetName = "LiteralExpression";

    @Property
    @FormField(afterElement = "typeRef")
    protected Text text;

    @PropertySet
    @FormField(afterElement = "text")
    protected ImportedValues importedValues;

    @Property
    @FormField(afterElement = "importedValues")
    protected ExpressionLanguage expressionLanguage;

    public LiteralExpression() {
        this(new Id(),
             new Description(),
             new QName(),
             new Text(),
             new ImportedValues(),
             new ExpressionLanguage());
    }

    public LiteralExpression(final @MapsTo("id") Id id,
                             final @MapsTo("description") org.kie.workbench.common.dmn.api.property.dmn.Description description,
                             final @MapsTo("typeRef") QName typeRef,
                             final @MapsTo("text") Text text,
                             final @MapsTo("importedValues") ImportedValues importedValues,
                             final @MapsTo("expressionLanguage") ExpressionLanguage expressionLanguage) {
        super(id,
              description,
              typeRef);
        this.text = text;
        this.importedValues = importedValues;
        this.expressionLanguage = expressionLanguage;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public Text getText() {
        return text;
    }

    public void setText(final Text text) {
        this.text = text;
    }

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
}
