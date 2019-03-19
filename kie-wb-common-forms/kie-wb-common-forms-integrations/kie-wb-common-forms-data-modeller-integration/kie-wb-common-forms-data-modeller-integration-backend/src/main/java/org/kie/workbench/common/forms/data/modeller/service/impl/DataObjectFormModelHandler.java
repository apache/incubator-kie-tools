/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.shared.ModelFinderService;
import org.kie.workbench.common.forms.editor.backend.service.impl.AbstractFormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.SourceFormModelNotFoundException;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class DataObjectFormModelHandler extends AbstractFormModelHandler<DataObjectFormModel> {

    private static final String SHORT_KEY = "DataObjectFormModelHandler.shortMessage";
    private static final String FULL_KEY = "DataObjectFormModelHandler.fullMessage";
    private static final String DATA_OBJECT_KEY = "DataObjectFormModelHandler.dataObject";

    private static final Logger logger = LoggerFactory.getLogger(DataObjectFormModelHandler.class);

    private ModelFinderService finderService;

    private DataObjectFormModel updatedFormModel;

    protected FieldManager fieldManager;

    @Inject
    public DataObjectFormModelHandler(final KieModuleService moduleService,
                                      final ModuleClassLoaderHelper classLoaderHelper,
                                      final ModelFinderService finderService,
                                      final FieldManager fieldManager) {
        super(moduleService, classLoaderHelper);

        this.fieldManager = fieldManager;
        this.finderService = finderService;
    }

    @Override
    public Class<DataObjectFormModel> getModelType() {
        return DataObjectFormModel.class;
    }

    @Override
    protected void initialize() {
        checkInitialized();

        updatedFormModel = finderService.getModel(formModel.getClassName(), path);
    }

    @Override
    public void checkSourceModel() throws SourceFormModelNotFoundException {
        checkInitialized();

        if (updatedFormModel == null) {
            String[] params = new String[]{formModel.getClassName()};

            throw new SourceFormModelNotFoundException(SHORT_KEY, params, FULL_KEY, params, DATA_OBJECT_KEY, formModel);
        }
    }

    @Override
    protected void log(String message, Exception e) {
        logger.warn(message, e);
    }

    @Override
    protected List<ModelProperty> getCurrentModelProperties() {
        return updatedFormModel.getProperties();
    }

    @Override
    public FormModelHandler<DataObjectFormModel> newInstance() {
        return new DataObjectFormModelHandler(moduleService,
                                              classLoaderHelper,
                                              finderService,
                                              fieldManager);
    }
}
