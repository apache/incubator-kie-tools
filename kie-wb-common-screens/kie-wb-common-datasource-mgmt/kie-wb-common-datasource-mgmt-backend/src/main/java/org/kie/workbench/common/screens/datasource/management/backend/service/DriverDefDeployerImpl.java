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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.backend.core.DriverDefDeployer;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.kie.workbench.common.screens.datasource.management.util.DriverDefSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@ApplicationScoped
public class DriverDefDeployerImpl
        extends AbstractDefDeployer<DriverDefInfo>
        implements DriverDefDeployer {

    private static final Logger logger = LoggerFactory.getLogger(DriverDefDeployerImpl.class);

    public DriverDefDeployerImpl() {
    }

    @Inject
    public DriverDefDeployerImpl(@Named("ioStrategy") IOService ioService,
                                 DataSourceDefQueryService queryService,
                                 DataSourceRuntimeManager runtimeManager,
                                 DefRegistry defRegistry) {
        super(ioService, queryService, runtimeManager, defRegistry);
    }

    @Override
    protected Collection<DriverDefInfo> findGlobalDefs() {
        return queryService.findGlobalDrivers();
    }

    @Override
    protected Collection<DriverDefInfo> findProjectDefs(Path path) {
        return queryService.findModuleDrivers(path);
    }

    @Override
    protected void deployDef(DriverDefInfo defInfo) {
        try {
            logger.debug("Deploying driver def: " + defInfo);
            String source = ioService.readAllString(Paths.convert(defInfo.getPath()));
            DriverDef driverDef = DriverDefSerializer.deserialize(source);
            runtimeManager.deployDriver(driverDef, DeploymentOptions.createOrResync());
            defRegistry.setEntry(defInfo.getPath(), driverDef);
            logger.debug("Driver was successfully deployed");
        } catch (Exception e) {
            logger.error("Driver deployment failed, defInfo: " + defInfo, e);
        }
    }
}
