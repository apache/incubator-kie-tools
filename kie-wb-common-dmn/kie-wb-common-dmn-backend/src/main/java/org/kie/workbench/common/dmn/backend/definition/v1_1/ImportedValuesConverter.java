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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.ImportedValues;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.ImportType;
import org.kie.workbench.common.dmn.api.property.dmn.ImportedElement;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Namespace;

public class ImportedValuesConverter {

    public static ImportedValues wbFromDMN(final org.kie.dmn.model.v1_1.ImportedValues dmn) {
        if (dmn == null) {
            return null;
        }
        Namespace namespace = new Namespace(dmn.getNamespace());
        LocationURI locationURI = new LocationURI(dmn.getLocationURI());
        ImportType importType = new ImportType(dmn.getImportType());
        ImportedElement importedElement = new ImportedElement(dmn.getImportedElement());
        ExpressionLanguage expressionLanguage = new ExpressionLanguage(dmn.getExpressionLanguage());
        ImportedValues wb = new ImportedValues(namespace,
                                               locationURI,
                                               importType,
                                               importedElement,
                                               expressionLanguage);
        return wb;
    }

    public static org.kie.dmn.model.v1_1.ImportedValues wbFromDMN(final ImportedValues wb) {
        if (wb == null) {
            return null;
        }
        org.kie.dmn.model.v1_1.ImportedValues dmn = new org.kie.dmn.model.v1_1.ImportedValues();
        dmn.setNamespace(wb.getNamespace().getValue());
        dmn.setLocationURI(wb.getLocationURI().getValue());
        dmn.setImportType(wb.getImportType().getValue());
        dmn.setImportedElement(wb.getImportedElement().getValue());
        dmn.setExpressionLanguage(wb.getExpressionLanguage().getValue());
        return dmn;
    }
}
