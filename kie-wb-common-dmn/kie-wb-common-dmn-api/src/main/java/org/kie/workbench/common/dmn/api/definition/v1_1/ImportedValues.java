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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class ImportedValues extends Import implements DMNPropertySet {

    protected String importedElement;

    protected String expressionLanguage;

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
                          final String expressionLanguage) {
        super(namespace,
              locationURI,
              importType);
        this.importedElement = importedElement;
        this.expressionLanguage = expressionLanguage;
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
