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

package org.kie.workbench.common.screens.datasource.management.backend.core.wildfly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;

import org.kie.workbench.common.screens.datasource.management.backend.core.DataSource;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceProvider;
import org.kie.workbench.common.screens.datasource.management.backend.integration.wildfly.WildflyDataSourceDef;
import org.kie.workbench.common.screens.datasource.management.backend.integration.wildfly.WildflyDataSourceManagementClient;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceStatus;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.util.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Widlfly based implementation of a DataSourceProvider.
 */
@ApplicationScoped
@Named(value = "WildflyDataSourceProvider" )
public class WildflyDataSourceProvider
        implements DataSourceProvider {

    private static final Logger logger = LoggerFactory.getLogger( WildflyDataSourceProvider.class );

    @Inject
    private WildflyDriverProvider driverProvider;

    private WildflyDataSourceManagementClient dataSourceMgmtClient = new WildflyDataSourceManagementClient();

    private Map<String, WildlfyDataSource> managedDataSources = new HashMap<>( );

    private Map<String, WildlfyDataSource> unManagedDataSources = new HashMap<>( );

    public WildflyDataSourceProvider() {
    }

    /**
     * Gets the list of data source definitions for the currently defined data sources in the Wildfly server.
     *
     * @return list with the definitions for the defined data sources.
     *
     * @throws Exception exceptions may be thrown if e.g. communication with the Wildfly server fails, etc.
     */
    @Override
    public List<DataSourceDef> getDeployments() throws Exception {

        List<WildflyDataSourceDef> dataSources;
        List<DataSourceDef> dataSourceDefs = new ArrayList<>( );
        DataSourceDef dataSourceDef;
        String dataSourceUuid;
        String driverUuid;

        dataSources = dataSourceMgmtClient.getDataSources();
        for ( WildflyDataSourceDef internalDef : dataSources ) {
            dataSourceDef = new DataSourceDef();
            try {
                dataSourceUuid = DeploymentIdGenerator.extractUuid( internalDef.getName() );
            } catch ( Exception e ) {
                dataSourceUuid = internalDef.getName();
            }
            try {
                driverUuid = DeploymentIdGenerator.extractUuid( internalDef.getDriverName() );
            } catch ( Exception e ) {
                driverUuid = internalDef.getDriverName();
            }

            dataSourceDef.setUuid( dataSourceUuid );
            dataSourceDef.setName( internalDef.getName() );
            dataSourceDef.setConnectionURL( internalDef.getConnectionURL() );
            dataSourceDef.setDriverUuid( driverUuid );
            dataSourceDef.setUser( internalDef.getUser() );
            dataSourceDef.setPassword( internalDef.getPassword() );
            dataSourceDefs.add( dataSourceDef );
        }

        return dataSourceDefs;
    }

    @Override
    public DataSourceDeploymentInfo deploy( DataSourceDef dataSourceDef ) throws Exception {

        //This random identifiers calculation should be removed when WF supports deletion
        //of data sources without letting them published on server until next restart.
        String random = "_" + UUIDGenerator.generateUUID();
        String deploymentId = DeploymentIdGenerator.generateDeploymentId( dataSourceDef ) + random;
        String kieJndi = JndiNameGenerator.generateJNDIName( dataSourceDef );
        String deploymentJndi = kieJndi + random;

        DataSourceDeploymentInfo deploymentInfo = deploy( dataSourceDef, deploymentJndi, deploymentId );

        javax.sql.DataSource dataSource = (javax.sql.DataSource) lookupDataSource( deploymentJndi );
        WildlfyDataSource wfDataSource = new WildlfyDataSource( dataSource, deploymentJndi );
        //bindDataSource( kieJndi, dataSource );
        managedDataSources.put( deploymentId, wfDataSource );
        return deploymentInfo;
    }

    /**
     * Creates a data source in the Wildfly server.
     *
     * @param dataSourceDef Data source definition to be created.
     *
     * @param jndi jndi name to be use the Wildly server to bound the data source in the jndi context.
     *
     * @return returns the deployment information for the created data source.
     *
     * @throws Exception exceptions may be thrown if the data source couldn't be created.
     */
    private DataSourceDeploymentInfo deploy( final DataSourceDef dataSourceDef, final String jndi, String deploymentId ) throws Exception {
        DriverDeploymentInfo driverDeploymentInfo = driverProvider.getDeploymentInfo( dataSourceDef.getDriverUuid() );
        if ( driverDeploymentInfo == null ) {
            throw new Exception( "Required driver: " + dataSourceDef.getDriverUuid() + " has not been deployed." );
        }

        WildflyDataSourceDef wfDataSourceDef = buildWFDataSource( deploymentId,
                jndi, dataSourceDef, driverDeploymentInfo.getDeploymentId() );

        dataSourceMgmtClient.createDataSource( wfDataSourceDef );
        return new DataSourceDeploymentInfo( deploymentId, true, dataSourceDef.getUuid(), jndi, false );
    }

    public DataSourceDeploymentInfo resync( DataSourceDef dataSourceDef, DataSourceDeploymentInfo deploymentInfo ) throws Exception {
        javax.sql.DataSource dataSource = (javax.sql.DataSource) lookupDataSource( deploymentInfo.getJndi() );
        WildlfyDataSource wfDataSource = new WildlfyDataSource( dataSource, deploymentInfo.getJndi() );
        //bindDataSource( kieJndi, dataSource );
        managedDataSources.put( deploymentInfo.getDeploymentId(), wfDataSource );
        return deploymentInfo;
    }

    /**
     * Deletes a data source in the Widlfy server.
     *
     * @param uuid Identifier of the data source definition to be deleted.
     *
     * @throws Exception exceptions may be thrown if the data source couldn't be deleted.
     */
    public void undeploy( final String uuid ) throws Exception {
        DataSourceDeploymentInfo deploymentInfo = getDeploymentInfo( uuid );
        if ( deploymentInfo != null ) {
            dataSourceMgmtClient.deleteDataSource( deploymentInfo.getDeploymentId() );
        }
    }

    @Override
    public void undeploy( final DataSourceDeploymentInfo deploymentInfo ) throws Exception {
        dataSourceMgmtClient.deleteDataSource( deploymentInfo.getDeploymentId() );
        managedDataSources.remove( deploymentInfo.getDeploymentId() );
    }

    /**
     * Gets the deployment information about a data source definition.
     *
     * @param uuid the data source definition identifier.
     *
     * @return the deployment information for the data source definition of null if no data source has been created
     * with the given uuid.
     *
     * @throws Exception exceptions may be thrown if e.g. communication with the Wildfly server fails, etc.
     */
    public DataSourceDeploymentInfo getDeploymentInfo( final String uuid ) throws Exception {
        for ( DataSourceDeploymentInfo deploymentInfo : getDeploymentsInfo() ) {
            if ( uuid.equals( deploymentInfo.getUuid() ) ) {
                return deploymentInfo;
            }
        }
        return null;
    }

    /**
     * Gets the deployment information for all the data sources currently defined on the Wildfly server.
     *
     * @return a list with the deployment information for all the data sources.
     *
     * @throws Exception exceptions may be thrown if e.g. communication with the Wildfly server fails, etc.
     */
    public List<DataSourceDeploymentInfo> getDeploymentsInfo() throws Exception {
        List<WildflyDataSourceDef> dataSources = dataSourceMgmtClient.getDataSources();
        List<DataSourceDeploymentInfo> result = new ArrayList<>( );
        DataSourceDeploymentInfo deploymentInfo;
        String uuid;
        WildlfyDataSource managedDataSource;
        boolean managed;
        String jndi;
        for ( WildflyDataSourceDef internalDef : dataSources ) {
            try {
                uuid = DeploymentIdGenerator.extractUuid( internalDef.getName() );
            } catch ( Exception e ) {
                uuid = internalDef.getName();
            }
            managedDataSource = managedDataSources.get( internalDef.getName() );
            if ( managedDataSource != null ) {
                managed = true;
                jndi = managedDataSource.getExternalJndi();
            } else {
                managed = false;
                jndi = internalDef.getJndi();
            }
            deploymentInfo = new DataSourceDeploymentInfo( internalDef.getName(),
                    managed, uuid, jndi, wasReferenced( internalDef.getName() ) );
            result.add( deploymentInfo );
        }
        return result;
    }

    @Override
    public DataSource lookupDataSource( DataSourceDeploymentInfo deploymentInfo ) throws Exception {
        WildlfyDataSource dataSource = managedDataSources.get( deploymentInfo.getDeploymentId() );
        if ( dataSource == null ) {
            dataSource = unManagedDataSources.get( deploymentInfo.getDeploymentId() );
        }
        if ( dataSource == null ) {
            DataSourceDeploymentInfo refreshedDeploymentInfo = getDeploymentInfo( deploymentInfo.getUuid() );
            if ( refreshedDeploymentInfo != null && refreshedDeploymentInfo.getJndi() != null ) {
                javax.sql.DataSource sqlDataSource = (javax.sql.DataSource) lookupDataSource( refreshedDeploymentInfo.getJndi() );
                if ( sqlDataSource != null) {
                    dataSource = new WildlfyDataSource( sqlDataSource, refreshedDeploymentInfo.getJndi() );
                    unManagedDataSources.put( deploymentInfo.getDeploymentId(), dataSource );
                    return dataSource;
                }
            }
        }

        if ( dataSource != null ) {
            if ( dataSource.isNew() ) {
                //first access to the data source
                dataSource.setStatus( DataSourceStatus.REFERENCED );
            }
            return dataSource;
        } else {
            throw new Exception( "Data source: " + dataSource + " is not deployed in current system." );
        }
    }

    @Override
    public void loadConfig( Properties properties ) {
        dataSourceMgmtClient.loadConfig( properties );
        driverProvider.loadConfig( properties );
    }

    private Object lookupDataSource( String jndi ) {
        try {
            InitialContext context = new InitialContext();
            return context.lookup( jndi );
        } catch ( Exception e ) {
            return null;
        }
    }

    private void bindDataSource( String jndi, Object object ) {
        try {
            InitialContext context = new InitialContext();
            if ( lookupDataSource( jndi ) != null ) {
                context.unbind( jndi );
            }
            context.bind( jndi, object );
        } catch ( Exception e ) {
            logger.error( "an error was produced during object binding, jndi: " + jndi, e );
        }
    }

    private WildflyDataSourceDef buildWFDataSource( String deploymentId,
            String jndi, DataSourceDef dataSourceDef, String driverDeploymentId ) {
        WildflyDataSourceDef wfDataSourceDef = new WildflyDataSourceDef();
        wfDataSourceDef.setName( deploymentId );
        wfDataSourceDef.setDriverName( driverDeploymentId );
        wfDataSourceDef.setJndi( jndi );
        wfDataSourceDef.setConnectionURL( dataSourceDef.getConnectionURL() );
        wfDataSourceDef.setUser( dataSourceDef.getUser() );
        wfDataSourceDef.setPassword( dataSourceDef.getPassword() );
        wfDataSourceDef.setUseJTA( true );

        return wfDataSourceDef;
    }


    private boolean wasReferenced( String deploymentId ) {
        WildlfyDataSource dataSource = managedDataSources.get( deploymentId );
        if ( dataSource == null ) {
            dataSource = unManagedDataSources.get( deploymentId );
        }
        if ( dataSource != null ) {
            return dataSource.isReferenced();
        }
        return false;
    }
}
