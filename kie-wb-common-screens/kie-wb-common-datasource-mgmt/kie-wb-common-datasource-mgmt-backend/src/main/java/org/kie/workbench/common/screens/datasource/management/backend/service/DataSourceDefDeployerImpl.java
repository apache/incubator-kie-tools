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
        implements DataSourceDefDeployer {

    private static final Logger logger = LoggerFactory.getLogger( DataSourceDefDeployerImpl.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private DataSourceDefQueryService queryService;

    @Inject
    private DataSourceRuntimeManager runtimeManager;

    public DataSourceDefDeployerImpl() {
    }

    @Override
    public void deployGlobalDefs() {
        try {
            logger.debug( "Starting global data sources deployment" );
            deployDefs( queryService.findGlobalDataSources( false ) );
            logger.debug( "End of global data sources deployment" );
        } catch ( Exception e ) {
            logger.error( "Global data sources deployment failed.", e );
        }
    }

    @Override
    public void deployProjectDefs( Path path ) {
        try {
            logger.debug( "Starting project data sources deployment for path: " + path );
            deployDefs( queryService.findProjectDataSources( path ) );
            logger.debug( "End of project data sources deployment for path: " + path );
        } catch ( Exception e ) {
            logger.error( "Project data sources deployment failed for paht: " + path, e );
        }
    }

    private void deployDefs( Collection<DataSourceDefInfo> defs ) {
        for ( DataSourceDefInfo dataSourceDefInfo : defs ) {
            deployDataSource( dataSourceDefInfo );
        }
    }

    private void deployDataSource( DataSourceDefInfo dataSourceDefInfo ) {
        try {
            String source = ioService.readAllString( Paths.convert( dataSourceDefInfo.getPath() ) );
            DataSourceDef dataSourceDef = DataSourceDefSerializer.deserialize( source );
            runtimeManager.deployDataSource( dataSourceDef, DeploymentOptions.createOrResync() );
        } catch ( Exception e ) {
            logger.error( "Data source deployment failed, dataSourceDefInfo: " + dataSourceDefInfo, e );
        }
    }
}
