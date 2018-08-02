/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class LiteralExpression extends Expression {

    protected String text;

    protected ImportedValues importedValues;

    protected String expressionLanguage;

    public LiteralExpression() {
        this(new Id(),
             new Description(),
             new QName(),
             "",
             new ImportedValues(),
             "");
    }

    public LiteralExpression(final @MapsTo("id") Id id,
                             final @MapsTo("description") org.kie.workbench.common.dmn.api.property.dmn.Description description,
                             final @MapsTo("typeRef") QName typeRef,
                             final @MapsTo("text") String text,
                             final @MapsTo("importedValues") ImportedValues importedValues,
                             final @MapsTo("expressionLanguage") String expressionLanguage) {
        super(id,
              description,
              typeRef);
        this.text = text;
        this.importedValues = importedValues;
        this.expressionLanguage = expressionLanguage;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public ImportedValues getImportedValues() {
        return importedValues;
    }

    public void setImportedValues(final ImportedValues importedValues) {
        this.importedValues = importedValues;
    }

    public String getExpressionLanguage() {
        return expressionLanguage;
    }

    public void setExpressionLanguage(final String expressionLanguage) {
        this.expressionLanguage = expressionLanguage;
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
                                         text != null ? text.hashCode() : 0,
                                         importedValues != null ? importedValues.hashCode() : 0,
                                         expressionLanguage != null ? expressionLanguage.hashCode() : 0);
    }
}
