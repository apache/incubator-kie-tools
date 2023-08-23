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

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.definition.model.ImportDMN;
import org.kie.workbench.common.dmn.api.definition.model.ImportPMML;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DMNIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DefaultIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.PMMLIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.stunner.core.util.StringUtils;

import static org.uberfire.commons.UUID.uuid;

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

    List<BaseIncludedModelActiveRecord> makeIncludedModels(final List<Import> imports) {
        getIncludedModelsIndex().clear();
        return imports
                .stream()
                .map(this::makeIncludedModel)
                .collect(Collectors.toList());
    }

    /**
     * Wrapped due to test
     */
    protected String uuidWrapper() {
        return uuid();
    }

    private BaseIncludedModelActiveRecord makeIncludedModel(final Import anImport) {
        BaseIncludedModelActiveRecord includedModel;
        if (anImport instanceof ImportDMN) {
            final DMNIncludedModelActiveRecord dmnIncludedModel = new DMNIncludedModelActiveRecord(getRecordEngine());
            dmnIncludedModel.setDataTypesCount(getDataTypesCount((ImportDMN) anImport));
            dmnIncludedModel.setDrgElementsCount(getDrgElementsCount((ImportDMN) anImport));
            includedModel = dmnIncludedModel;
        } else if (anImport instanceof ImportPMML) {
            final PMMLIncludedModelActiveRecord pmmlIncludedModel = new PMMLIncludedModelActiveRecord(getRecordEngine());
            pmmlIncludedModel.setModelCount(getPMMLModelCount((ImportPMML) anImport));
            includedModel = pmmlIncludedModel;
        } else {
            includedModel = new DefaultIncludedModelActiveRecord(getRecordEngine());
        }

        setUuid(anImport, includedModel);

        includedModel.setName(getName(anImport));
        includedModel.setNamespace(getNamespace(anImport));
        includedModel.setImportType(getImportType(anImport));
        includedModel.setPath(getPath(anImport));

        getIncludedModelsIndex().index(includedModel, anImport);

        return includedModel;
    }

    void setUuid(final Import anImport, final BaseIncludedModelActiveRecord includedModel) {
        if (StringUtils.isEmpty(anImport.getUuid())) {
            includedModel.setUuid(uuidWrapper());
        } else {
            includedModel.setUuid(anImport.getUuid());
        }
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
        return anImport.getLocationURI().getValue();
    }

    private String getNamespace(final Import anImport) {
        return anImport.getNamespace();
    }

    private String getImportType(final Import anImport) {
        return anImport.getImportType();
    }

    private int getDataTypesCount(final ImportDMN anImport) {
        return anImport.getItemDefinitionsCount();
    }

    private int getDrgElementsCount(final ImportDMN anImport) {
        return anImport.getDrgElementsCount();
    }

    private int getPMMLModelCount(final ImportPMML anImport) {
        return anImport.getModelCount();
    }
}
