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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public final class ImportConverter {

    public static Import wbFromDMN(org.kie.dmn.model.api.Import dmn) {
        final LocationURI locationURI = new LocationURI(dmn.getLocationURI());
        final Import result = new Import(dmn.getNamespace(), locationURI, dmn.getImportType());
        final Map<QName, String> additionalAttributes = new HashMap<>();
        for (Map.Entry<javax.xml.namespace.QName, String> entry : dmn.getAdditionalAttributes().entrySet()) {
            additionalAttributes.put(QNamePropertyConverter.wbFromDMN(entry.getKey(), dmn), entry.getValue());
        }
        result.setAdditionalAttributes(additionalAttributes);
        final String id = dmn.getId();
        final String name = dmn.getName();
        final String description = dmn.getDescription();
        result.setId(new Id(id != null ? id : UUID.randomUUID().toString()));
        result.setName(new Name(name));
        result.setDescription(DescriptionPropertyConverter.wbFromDMN(description));
        return result;
    }

    public static org.kie.dmn.model.api.Import dmnFromWb(Import wb) {
        final org.kie.dmn.model.api.Import result = new org.kie.dmn.model.v1_2.TImport();
        result.setImportType(wb.getImportType());
        result.setLocationURI(wb.getLocationURI().getValue());
        result.setNamespace(wb.getNamespace());
        final Map<javax.xml.namespace.QName, String> additionalAttributes = new HashMap<>();
        for (Map.Entry<QName, String> entry : wb.getAdditionalAttributes().entrySet()) {
            QNamePropertyConverter.dmnFromWB(entry.getKey())
                    .ifPresent(qName -> additionalAttributes.put(qName, entry.getValue()));
        }
        result.setId(wb.getId().getValue());
        result.setName(wb.getName().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        result.setAdditionalAttributes(additionalAttributes);
        return result;
    }
}
