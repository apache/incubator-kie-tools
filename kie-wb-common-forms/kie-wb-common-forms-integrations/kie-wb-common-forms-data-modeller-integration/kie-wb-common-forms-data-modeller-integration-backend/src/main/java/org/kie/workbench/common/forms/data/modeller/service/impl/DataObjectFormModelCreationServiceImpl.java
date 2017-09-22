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
package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFormModelCreationService;
import org.uberfire.backend.vfs.Path;

@Service
@Dependent
public class DataObjectFormModelCreationServiceImpl implements DataObjectFormModelCreationService {

    private DataObjectFinderService finderService;

    private DataObjectFormModelHandler formModelHandler;

    @Inject
    public DataObjectFormModelCreationServiceImpl(DataObjectFinderService finderService,
                                                  DataObjectFormModelHandler formModelHandler) {
        this.finderService = finderService;
        this.formModelHandler = formModelHandler;
    }

    @Override
    public List<DataObjectFormModel> getAvailableDataObjects(final Path path) {
        return finderService.getProjectDataObjects(path).stream().map(dataObject -> formModelHandler.createFormModel(dataObject, path)).collect(Collectors.toList());
    }
}
