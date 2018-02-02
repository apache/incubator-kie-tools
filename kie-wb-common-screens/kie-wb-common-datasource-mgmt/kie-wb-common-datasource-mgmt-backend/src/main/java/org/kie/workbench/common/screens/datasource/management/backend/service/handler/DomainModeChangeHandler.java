/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.backend.service.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.service.DataSourceServicesHelper;
import org.kie.workbench.common.screens.datasource.management.model.Def;
import org.kie.workbench.common.screens.datasource.management.util.DataSourceEventHelper;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.io.IOService;

/**
 * DefChangeHandler handler to be used in cluster installations that don't need to replicate in current node the
 * deployments/un-deployments realized in other nodes. E.g. cluster installations that relies on the
 * WildflyDataSourceProvider and WildflyDriverProvider implementations.
 */
@ApplicationScoped
@Named(value = "DomainModeChangeHandler")
public class DomainModeChangeHandler extends AbstractDefChangeHandler {

    public DomainModeChangeHandler() {
    }

    @Inject
    public DomainModeChangeHandler(DataSourceRuntimeManager runtimeManager,
                                   DataSourceServicesHelper serviceHelper,
                                   @Named("ioStrategy") IOService ioService,
                                   KieModuleService moduleService,
                                   DataSourceEventHelper eventHelper) {
        super(runtimeManager,
              serviceHelper,
              ioService,
              moduleService,
              eventHelper);
    }

    @Override
    protected void unDeploy(Def def) throws Exception {
        //avoid un-deployments in this mode.
    }

    @Override
    protected void deploy(Def def) throws Exception {
        //avoid deployments in this mode.
    }
}