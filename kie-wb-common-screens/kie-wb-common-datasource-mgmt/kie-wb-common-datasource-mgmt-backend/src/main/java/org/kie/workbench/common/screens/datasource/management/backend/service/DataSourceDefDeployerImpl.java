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

import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceDefDeployer;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.kie.workbench.common.screens.datasource.management.util.DataSourceDefSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@ApplicationScoped
public class DataSourceDefDeployerImpl
        extends AbstractDefDeployer<DataSourceDefInfo>
        implements DataSourceDefDeployer {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceDefDeployerImpl.class);

    public DataSourceDefDeployerImpl() {
    }

    @Inject
    public DataSourceDefDeployerImpl(@Named("ioStrategy") IOService ioService,
                                     DataSourceDefQueryService queryService,
                                     DataSourceRuntimeManager runtimeManager,
                                     DefRegistry defRegistry) {
        super(ioService, queryService, runtimeManager, defRegistry);
    }

    @Override
    protected Collection<DataSourceDefInfo> findGlobalDefs() {
        return queryService.findGlobalDataSources(false);
    }

    @Override
    protected Collection<DataSourceDefInfo> findProjectDefs(Path path) {
        return queryService.findModuleDataSources(path);
    }

    @Override
    protected void deployDef(DataSourceDefInfo defInfo) {
        try {
            logger.debug("Deploying data source def: " + defInfo);
            String source = ioService.readAllString(Paths.convert(defInfo.getPath()));
            DataSourceDef dataSourceDef = DataSourceDefSerializer.deserialize(source);
            runtimeManager.deployDataSource(dataSourceDef, DeploymentOptions.createOrResync());
            defRegistry.setEntry(defInfo.getPath(), dataSourceDef);
            logger.debug("Data source was successfully deployed");
        } catch (Exception e) {
            logger.error("Data source deployment failed, defInfo: " + defInfo, e);
        }
    }
}