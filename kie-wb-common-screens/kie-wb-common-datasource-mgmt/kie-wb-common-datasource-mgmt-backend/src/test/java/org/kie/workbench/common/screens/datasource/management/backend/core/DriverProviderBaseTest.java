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

import java.net.URI;

import org.junit.Test;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.util.MavenArtifactResolver;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public abstract class DriverProviderBaseTest
        implements DataSourceManagementTestConstants {

    @Mock
    protected MavenArtifactResolver artifactResolver;

    protected DriverProvider driverProvider;

    protected URI driver1Uri;

    protected DriverDef driverDef1;

    public void setup( ) throws Exception {
        driverDef1 = new DriverDef( );
        driverDef1.setUuid( DRIVER1_UUID );
        driverDef1.setName( DRIVER1_NAME );
        driverDef1.setDriverClass( DRIVER1_CLASS );
        driverDef1.setArtifactId( ARTIFACT_ID );
        driverDef1.setGroupId( GROUP_ID );
        driverDef1.setVersion( VERSION );

        driver1Uri = new URI( "file:///maven_dir/driver1_file.jar" );
        when( artifactResolver.resolve( driverDef1.getGroupId( ), driverDef1.getArtifactId( ), driverDef1.getVersion( ) ) )
                .thenReturn( driver1Uri );
    }

    @Test
    public void testDeployDriver( ) throws Exception {
        // the driver is deployed
        deployDriver( driverDef1 );
        // then the deployment info should be returned.
        DriverDeploymentInfo deploymentInfo = driverProvider.getDeploymentInfo( driverDef1.getUuid( ) );
        assertNotNull( deploymentInfo );
        assertTrue( deploymentInfo.isManaged( ) );
    }

    protected abstract void deployDriver( DriverDef driverDef ) throws Exception;

    protected abstract void unDeployDriver( DriverDeploymentInfo deploymentInfo ) throws Exception;

    @Test
    public void testUnDeployDriver( ) throws Exception {
        // deploy the driver
        deployDriver( driverDef1 );
        // the deployment info should be returned.
        DriverDeploymentInfo deploymentInfo = driverProvider.getDeploymentInfo( driverDef1.getUuid( ) );
        // un-deploy the driver.
        unDeployDriver( deploymentInfo );
        // query the deployment info again
        deploymentInfo = driverProvider.getDeploymentInfo( driverDef1.getUuid( ) );
        // no deployment info should have been returned.
        assertNull( deploymentInfo );
    }

    /**
     * Tests the querying of the deployment information for a driver that wasn't deployed.
     */
    @Test
    public void testGetDeploymentInfoForNotDeployedDriver( ) throws Exception {
        DriverDeploymentInfo deploymentInfo = driverProvider.getDeploymentInfo( DRIVER1_UUID );
        // a null value should have been returned since the driver wasn't deployed.
        assertNull( deploymentInfo );
    }

    /**
     * Tests the querying of the deployment information for a driver that was previously deployed.
     */
    @Test
    public void testGetDeploymentInfoForDeployedDriver( ) throws Exception {
        deployDriver( driverDef1 );
        DriverDeploymentInfo deploymentInfo = driverProvider.getDeploymentInfo( DRIVER1_UUID );
        // a non null value should have been returned since de driver was deployed.
        assertNotNull( deploymentInfo );
    }
}