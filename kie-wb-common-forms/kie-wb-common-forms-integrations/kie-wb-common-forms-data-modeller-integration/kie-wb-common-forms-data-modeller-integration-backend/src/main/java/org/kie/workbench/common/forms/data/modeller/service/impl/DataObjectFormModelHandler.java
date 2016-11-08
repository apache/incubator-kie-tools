/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.service.FormModelHandler;
import org.kie.workbench.common.forms.service.impl.AbstractFormModelHandler;
import org.kie.workbench.common.services.datamodeller.core.DataObject;

@Dependent
public class DataObjectFormModelHandler extends AbstractFormModelHandler<DataObjectFormModel>{

    protected DataObjectFinderService finderService;

    protected DataModellerFieldGenerator dataModellerFieldGenerator;

    protected DataObject dataObject;

    @Inject
    public DataObjectFormModelHandler( DataObjectFinderService finderService,
                                       DataModellerFieldGenerator dataModellerFieldGenerator ) {
        this.finderService = finderService;
        this.dataModellerFieldGenerator = dataModellerFieldGenerator;
    }

    @Override
    public Class<DataObjectFormModel> getModelType() {
        return DataObjectFormModel.class;
    }

    @Override
    protected void initialize() {
        super.checkInitialized();

        dataObject = finderService.getDataObject( formModel.getClassName(), path );
    }

    @Override
    public FormModelHandler<DataObjectFormModel> newInstance() {
        return new DataObjectFormModelHandler( finderService, dataModellerFieldGenerator );
    }

    @Override
    protected List<FieldDefinition> doGenerateModelFields() {
        return dataModellerFieldGenerator.getFieldsFromDataObject( formModel.getName(), dataObject );
    }

    @Override
    protected FieldDefinition doCreateFieldDefinition( String fieldName ) {
        return dataModellerFieldGenerator.createFieldDefinition( formModel.getName(), dataObject.getProperty( fieldName ) );
    }
}
