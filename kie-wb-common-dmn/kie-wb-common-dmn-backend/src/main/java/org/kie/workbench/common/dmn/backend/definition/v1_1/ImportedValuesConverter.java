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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.UUID;

import org.kie.workbench.common.dmn.api.definition.v1_1.ImportedValues;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

public class ImportedValuesConverter {

    public static ImportedValues wbFromDMN(final org.kie.dmn.model.api.ImportedValues dmn) {
        if (dmn == null) {
            return null;
        }
        final String namespace = dmn.getNamespace();
        final LocationURI locationURI = new LocationURI(dmn.getLocationURI());
        final String importType = dmn.getImportType();
        final String importedElement = dmn.getImportedElement();
        final ExpressionLanguage expressionLanguage = ExpressionLanguagePropertyConverter.wbFromDMN(dmn.getExpressionLanguage());
        final ImportedValues wb = new ImportedValues(namespace,
                                                     locationURI,
                                                     importType,
                                                     importedElement,
                                                     expressionLanguage);
        final String id = dmn.getId();
        final String name = dmn.getName();
        final String description = dmn.getDescription();
        final String fallbackUUID = UUID.randomUUID().toString();
        wb.setId(new Id(id != null ? id : fallbackUUID));
        wb.setName(new Name(name != null ? name : fallbackUUID));
        wb.setDescription(DescriptionPropertyConverter.wbFromDMN(description));
        return wb;
    }

    public static org.kie.dmn.model.api.ImportedValues dmnFromWB(final ImportedValues wb) {
        if (wb == null) {
            return null;
        }
        final org.kie.dmn.model.api.ImportedValues dmn = new org.kie.dmn.model.v1_2.TImportedValues();
        dmn.setNamespace(wb.getNamespace());
        dmn.setLocationURI(wb.getLocationURI().getValue());
        dmn.setImportType(wb.getImportType());
        dmn.setImportedElement(wb.getImportedElement());
        dmn.setId(wb.getId().getValue());
        dmn.setName(wb.getName().getValue());
        dmn.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        dmn.setExpressionLanguage(ExpressionLanguagePropertyConverter.dmnFromWB(wb.getExpressionLanguage()));
        return dmn;
    }
}
