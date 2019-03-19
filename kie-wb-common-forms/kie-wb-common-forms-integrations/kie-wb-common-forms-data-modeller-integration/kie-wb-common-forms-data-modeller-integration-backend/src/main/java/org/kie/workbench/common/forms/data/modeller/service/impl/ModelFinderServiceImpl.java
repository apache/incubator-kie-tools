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

package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.util.Collection;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReaderService;
import org.kie.workbench.common.forms.data.modeller.service.shared.ModelFinderService;
import org.uberfire.backend.vfs.Path;

@Service
@Dependent
public class ModelFinderServiceImpl implements ModelFinderService {

    private ModelReaderService<Path> modelReaderService;

    @Inject
    public ModelFinderServiceImpl(ModelReaderService<Path> modelReaderService) {
        this.modelReaderService = modelReaderService;
    }

    @Override
    public DataObjectFormModel getModel(String typeName, Path path) {
        return modelReaderService.getModelReader(path).readFormModel(typeName);
    }

    @Override
    public Collection<DataObjectFormModel> getModuleModels(Path path) {
        return modelReaderService.getModelReader(path).readModuleFormModels();
    }

    @Override
    public Collection<DataObjectFormModel> getAllModels(Path path) {
        return modelReaderService.getModelReader(path).readAllFormModels();
    }
}
