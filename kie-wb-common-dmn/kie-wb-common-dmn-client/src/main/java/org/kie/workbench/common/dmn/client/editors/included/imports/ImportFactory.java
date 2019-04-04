/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included.imports;

import java.util.Objects;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;

public class ImportFactory {

    static String IMPORT_TYPE = "http://www.omg.org/spec/DMN/20180521/MODEL/";

    private final IncludedModelsIndex modelsIndex;

    @Inject
    public ImportFactory(final IncludedModelsIndex modelsIndex) {
        this.modelsIndex = modelsIndex;
    }

    public Import makeImport(final IncludedModel record) {

        final Import anImport = new Import();

        anImport.setImportType(IMPORT_TYPE);
        anImport.setName(name(record));
        anImport.setLocationURI(location(record));
        anImport.setNamespace(record.getNamespace());

        return anImport;
    }

    private LocationURI location(final IncludedModel record) {
        return new LocationURI(record.getPath());
    }

    Name name(final IncludedModel record) {
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
