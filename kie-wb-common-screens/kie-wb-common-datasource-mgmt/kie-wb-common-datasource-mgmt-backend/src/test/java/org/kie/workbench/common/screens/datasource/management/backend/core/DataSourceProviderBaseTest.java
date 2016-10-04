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

package org.kie.workbench.common.screens.datasource.management.backend.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;

import static org.junit.Assert.*;

public abstract class DataSourceProviderBaseTest
        implements DataSourceManagementTestConstants {

    protected DriverProvider driverProvider;

    protected DataSourceProvider dataSourceProvider;

    protected DataSourceDef dataSourceDef;

    @Rule
    public ExpectedException expectedException = ExpectedException.none( );

    protected void setup( ) throws Exception {
        dataSourceDef = new DataSourceDef( );
        dataSourceDef.setUuid( DS1_UUID );
        dataSourceDef.setName( DS1_NAME );
        dataSourceDef.setDriverUuid( DRIVER1_UUID );
        dataSourceDef.setConnectionURL( DS1_CONNECTION_URL );
        dataSourceDef.setUser( DS1_USER );
        dataSourceDef.setPassword( DS1_PASSWORD );
    }

    /**
     * Tests the successful deployment of a data source.
     */
    @Test
    public void testDeployDataSource( ) throws Exception {
        setupDrivers( );
        // deploy the data source
        deployDataSource( dataSourceDef );
        DataSourceDeploymentInfo deploymentInfo = dataSourceProvider.getDeploymentInfo( dataSourceDef.getUuid( ) );
        // the data source should have been properly deployed.
        assertNotNull( deploymentInfo );
    }

    /**
     * Tests a data source deployment attempt in the case when the required driver is not deployed.
     */
    @Test
    public void testDeployDataSourceWithMissingDriver( ) throws Exception {
        // an exception should have been thrown.
        expectedException.expectMessage( "Required driver: " + dataSourceDef.getDriverUuid( ) + " is not deployed" );
        dataSourceProvider.deploy( dataSourceDef );
        DataSourceDeploymentInfo deploymentInfo = dataSourceProvider.getDeploymentInfo( dataSourceDef.getUuid( ) );
        assertNull( deploymentInfo );
    }

    protected abstract void setupDrivers( ) throws Exception;

    protected abstract void deployDataSource( DataSourceDef dataSourceDef ) throws Exception;

    @Test
    public void testUnDeployDataSource( ) throws Exception {
        setupDrivers( );
        // deploy the data source
        deployDataSource( dataSourceDef );
        DataSourceDeploymentInfo initialDeploymentInfo = dataSourceProvider.getDeploymentInfo( dataSourceDef.getUuid( ) );
        // deployment info should exists
        assertNotNull( initialDeploymentInfo );
        // un-deploy the datasource
        unDeployDataSource( initialDeploymentInfo );
        // the deployment info should not exists now
        DataSourceDeploymentInfo currentDeploymentInfo = dataSourceProvider.getDeploymentInfo( dataSourceDef.getUuid( ) );
        assertNull( currentDeploymentInfo );
        // un-deploy the non existing data source
        expectedException.expectMessage( "DataSource: " + initialDeploymentInfo.getUuid( ) + " is not deployed" );
        dataSourceProvider.undeploy( initialDeploymentInfo );
    }

    protected abstract void unDeployDataSource( DataSourceDeploymentInfo deploymentInfo ) throws Exception;

    /**
     * Tests the lookup of a previously deployed data source.
     */
    @Test
    public void testLookupDataSourceForDeployed( ) throws Exception {
        setupDrivers( );
        // emulate that the data source was deployed in an earlier time.
        deployDataSource( dataSourceDef );
        // query the deployment information in a later time.
        DataSourceDeploymentInfo deploymentInfo = dataSourceProvider.getDeploymentInfo( dataSourceDef.getUuid( ) );
        // the deployment information for the previously deployed data source should be available and should be a
        // managed data source.
        assertNotNull( deploymentInfo );
        assertTrue( deploymentInfo.isManaged( ) );
        // finally lookup the data source.
        DataSource dataSource = dataSourceProvider.lookupDataSource( deploymentInfo );
        // the lookup must return a value.
        assertNotNull( dataSource );
    }

    /**
     * Tests the lookup of a data source that has not been deployed.
     */
    @Test
    public void testLookupDataSourceForNotDeployed( ) throws Exception {
        setupDrivers( );

        // case 1
        // emulate that the data source #1 was deployed in an earlier time.
        deployDataSource( dataSourceDef );

        // now do a lookup for data source #1
        DataSourceDeploymentInfo deploymentInfo = dataSourceProvider.getDeploymentInfo( dataSourceDef.getUuid( ) );
        // deployment info for data source #1 should exists.
        assertNotNull( deploymentInfo );
        // perform the lookup on data source #1
        DataSource dataSource = dataSourceProvider.lookupDataSource( deploymentInfo );
        // the lookup should be ok.
        assertNotNull( dataSource );

        // now un-deploy data source #1
        unDeployDataSource( deploymentInfo );
        // now a lookup for data source #1 must fail
        expectedException.expectMessage( "Data source for: " + deploymentInfo + " is not deployed in current system." );
        dataSourceProvider.lookupDataSource( deploymentInfo );

        // case 2
        // invent that a valid deployment information for a non deployed (or not existing) data source.
        deploymentInfo = new DataSourceDeploymentInfo( DS3_DEPLOYMENT_ID, true, DS3_UUID, false );
        // the lookup should fail.
        expectedException.expectMessage( "Data source for: " + deploymentInfo + " is not deployed in current system." );
        dataSourceProvider.lookupDataSource( deploymentInfo );
    }
}