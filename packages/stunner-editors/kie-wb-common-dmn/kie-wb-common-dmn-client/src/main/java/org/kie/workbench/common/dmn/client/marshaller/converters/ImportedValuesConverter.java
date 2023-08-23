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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.model.ImportedValues;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImportedValues;

public class ImportedValuesConverter {

    public static ImportedValues wbFromDMN(final JSITImportedValues dmn) {
        if (Objects.isNull(dmn)) {
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
        final String name = dmn.getName();
        final String description = dmn.getDescription();
        wb.setId(IdPropertyConverter.wbFromDMN(dmn.getId()));
        wb.setName(new Name(Objects.nonNull(name) ? name : wb.getId().getValue()));
        wb.setDescription(DescriptionPropertyConverter.wbFromDMN(description));
        return wb;
    }

    public static JSITImportedValues dmnFromWB(final ImportedValues wb) {
        if (Objects.isNull(wb)) {
            return null;
        }
        final JSITImportedValues dmn = JSITImportedValues.newInstance();
        dmn.setNamespace(wb.getNamespace());
        dmn.setLocationURI(wb.getLocationURI().getValue());
        dmn.setImportType(wb.getImportType());
        dmn.setImportedElement(wb.getImportedElement());
        dmn.setId(wb.getId().getValue());
        dmn.setName(wb.getName().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(dmn::setDescription);
        dmn.setExpressionLanguage(ExpressionLanguagePropertyConverter.dmnFromWB(wb.getExpressionLanguage()));
        return dmn;
    }
}
