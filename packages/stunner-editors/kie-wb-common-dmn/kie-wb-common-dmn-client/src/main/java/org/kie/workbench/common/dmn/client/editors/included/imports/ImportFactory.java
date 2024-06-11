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

package org.kie.workbench.common.dmn.client.editors.included.imports;

import java.util.Objects;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.definition.model.ImportDMN;
import org.kie.workbench.common.dmn.api.definition.model.ImportPMML;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DMNIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.PMMLIncludedModelActiveRecord;

public class ImportFactory {

    private final IncludedModelsIndex modelsIndex;

    @Inject
    public ImportFactory(final IncludedModelsIndex modelsIndex) {
        this.modelsIndex = modelsIndex;
    }

    public Import makeImport(final BaseIncludedModelActiveRecord record) {
        Import anImport;

        if (record instanceof DMNIncludedModelActiveRecord) {
            final ImportDMN dmn = new ImportDMN();
            final DMNIncludedModelActiveRecord dmnRecord = (DMNIncludedModelActiveRecord) record;
            dmn.setName(name(record));
            dmn.setNamespace(record.getNamespace());
            dmn.setLocationURI(location(record));
            dmn.setImportType(record.getImportType());
            dmn.setDrgElementsCount(dmnRecord.getDrgElementsCount());
            dmn.setItemDefinitionsCount(dmnRecord.getDataTypesCount());
            anImport = dmn;
        } else if (record instanceof PMMLIncludedModelActiveRecord) {
            final ImportPMML pmml = new ImportPMML();
            final PMMLIncludedModelActiveRecord pmmlRecord = (PMMLIncludedModelActiveRecord) record;
            pmml.setName(name(record));
            pmml.setNamespace(record.getNamespace());
            pmml.setLocationURI(location(record));
            pmml.setImportType(record.getImportType());
            pmml.setModelCount(pmmlRecord.getModelCount());
            anImport = pmml;
        } else {
            anImport = new Import();
        }

        anImport.setLocationURI(location(record));
        anImport.setImportType(record.getImportType());
        anImport.setUuid(record.getUUID());

        return anImport;
    }

    private LocationURI location(final BaseIncludedModelActiveRecord record) {
        return new LocationURI(record.getPath());
    }

    Name name(final BaseIncludedModelActiveRecord record) {
        return new Name(uniqueName(record.getName()));
    }

    private String uniqueName(final String name) {
        return uniqueName(name, 1);
    }

    private String uniqueName(final String name,
                              final int suffix) {

        final String newName = suffix == 1 ? name : name + " - " + suffix;

        if (isUnique(newName)) {
            return newName;
        } else {
            return uniqueName(name, suffix + 1);
        }
    }

    private boolean isUnique(final String name) {
        return modelsIndex
                .getIndexedImports()
                .stream()
                .map(anImport -> anImport.getName().getValue())
                .noneMatch(importName -> Objects.equals(importName, name));
    }
}
