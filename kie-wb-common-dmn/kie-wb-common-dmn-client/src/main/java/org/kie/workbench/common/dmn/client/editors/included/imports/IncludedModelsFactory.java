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

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;

import static org.uberfire.commons.uuid.UUID.uuid;

@ApplicationScoped
public class IncludedModelsFactory {

    private final ImportRecordEngine recordEngine;

    private final IncludedModelsIndex includedModelsIndex;

    @Inject
    public IncludedModelsFactory(final ImportRecordEngine recordEngine,
                                 final IncludedModelsIndex includedModelsIndex) {
        this.recordEngine = recordEngine;
        this.includedModelsIndex = includedModelsIndex;
    }

    List<IncludedModel> makeIncludedModels(final List<Import> imports) {
        getIncludedModelsIndex().clear();
        return imports
                .stream()
                .map(this::makeIncludedModel)
                .collect(Collectors.toList());
    }

    private IncludedModel makeIncludedModel(final Import anImport) {

        final IncludedModel includedModel = new IncludedModel(getRecordEngine());

        includedModel.setUuid(uuid());
        includedModel.setName(getName(anImport));
        includedModel.setPath(getPath(anImport));
        includedModel.setDataTypesCount(getDataTypesCount());
        includedModel.setDrgElementsCount(getDrgElementsCount());

        getIncludedModelsIndex().index(includedModel, anImport);

        return includedModel;
    }

    public IncludedModelsIndex getIncludedModelsIndex() {
        return includedModelsIndex;
    }

    public ImportRecordEngine getRecordEngine() {
        return recordEngine;
    }

    private String getName(final Import anImport) {
        return anImport.getName().getValue();
    }

    private String getPath(final Import anImport) {
        // TODO: The 'namespace' is temporary - https://issues.jboss.org/browse/DROOLS-3722
        return anImport.getNamespace();
    }

    private int getDataTypesCount() {
        // TODO: The '99' value is temporary - https://issues.jboss.org/browse/DROOLS-3720
        return 99;
    }

    private int getDrgElementsCount() {
        // TODO: The '99' value is temporary - https://issues.jboss.org/browse/DROOLS-3721
        return 99;
    }
}
