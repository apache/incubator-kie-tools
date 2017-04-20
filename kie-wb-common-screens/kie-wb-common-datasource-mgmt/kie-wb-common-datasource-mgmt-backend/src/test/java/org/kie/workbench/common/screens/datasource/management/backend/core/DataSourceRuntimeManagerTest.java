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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.impl.DataSourceRuntimeManagerImpl;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSourceRuntimeManagerTest
        implements DataSourceManagementTestConstants {

    @Mock
    private DataSourceProviderFactory providerFactory;

    @Mock
    private DriverProvider driverProvider;

    @Mock
    private DataSourceProvider dataSourceProvider;

    private DataSourceRuntimeManager runtimeManager;

    private DataSourceDef dataSourceDef;

    private DriverDef driverDef;

    private DriverDeploymentInfo driverDeploymentInfo;

    private DataSourceDeploymentInfo dataSourceDeploymentInfo;

    @Mock
    private DataSource dataSource;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        when(providerFactory.getDataSourceProvider()).thenReturn(dataSourceProvider);
        when(providerFactory.getDriverProvider()).thenReturn(driverProvider);
        runtimeManager = new DataSourceRuntimeManagerMock(providerFactory);

        dataSourceDef = new DataSourceDef();
        dataSourceDef.setUuid(DS1_UUID);
        dataSourceDef.setName(DS1_NAME);
        dataSourceDef.setDriverUuid(DRIVER1_UUID);
        dataSourceDef.setConnectionURL(DS1_CONNECTION_URL);
        dataSourceDef.setUser(DS1_USER);
        dataSourceDef.setPassword(DS1_PASSWORD);

        driverDef = new DriverDef();
        driverDef.setUuid(DRIVER1_UUID);
        driverDef.setName(DRIVER1_NAME);
        driverDef.setDriverClass(DRIVER1_CLASS);
        driverDef.setArtifactId(ARTIFACT_ID);
        driverDef.setGroupId(GROUP_ID);
        driverDef.setVersion(VERSION);

        driverDeploymentInfo = new DriverDeploymentInfo();
        dataSourceDeploymentInfo = new DataSourceDeploymentInfo();
    }

    /**
     * Tests the deployment of a driver that wasn't previously deployed.
     */
    @Test
    public void testDeployDriver() {
        testDeployDriver(false,
                         DeploymentOptions.create());
    }

    /**
     * Tests the deployment of a driver that was already deployed with the create option.
     */
    @Test
    public void testDeployDriverExisting() {
        testDeployDriver(true,
                         DeploymentOptions.create());
    }

    /**
     * Tests the deployment of a driver that was already deployed by using the resync mode.
     */
    @Test
    public void testDeployDriverExistingResync() {
        testDeployDriver(true,
                         DeploymentOptions.createOrResync());
    }

    private void testDeployDriver(boolean isDeployed,
                                  DeploymentOptions options) {
        try {
            if (isDeployed) {
                when(driverProvider.getDeploymentInfo(driverDef.getUuid())).thenReturn(driverDeploymentInfo);
            }
            if (!isDeployed) {
                runtimeManager.deployDriver(driverDef,
                                            options);
                verify(driverProvider,
                       times(1)).deploy(driverDef);
            } else if (options.isCreateOrResyncDeployment()) {
                runtimeManager.deployDriver(driverDef,
                                            options);
                verify(driverProvider,
                       times(1)).resync(driverDef,
                                        driverDeploymentInfo);
            } else {
                expectedException.expectMessage("Driver: " + driverDef + " is already deployed.");
                runtimeManager.deployDriver(driverDef,
                                            options);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the un-deployment of a driver with no dependant data sources.
     */
    @Test
    public void testUnDeployDriver() {
        testUnDeployDriver(false,
                           UnDeploymentOptions.forcedUnDeployment());
    }

    /**
     * Tests the un-deployment of a driver with dependant data sources with soft un-deployment mode.
     */
    @Test
    public void testUnDeployDriverWithDependantsSoft() {
        testUnDeployDriver(true,
                           UnDeploymentOptions.softUnDeployment());
    }

    /**
     * Tests the un-deployment of a driver with dependant data sources with forced un-deployment mode.
     */
    @Test
    public void testUnDeployDriverWithDependantsHard() {
        testUnDeployDriver(true,
                           UnDeploymentOptions.forcedUnDeployment());
    }

    private void testUnDeployDriver(boolean hasDependants,
                                    UnDeploymentOptions options) {
        try {
            // simulate we have an already deployed driver
            deployDriver(driverDef);
            if (hasDependants) {
                // add a dependant data source.
                when(dataSourceProvider.deploy(dataSourceDef)).thenReturn(dataSourceDeploymentInfo);
                runtimeManager.deployDataSource(dataSourceDef,
                                                DeploymentOptions.create());
            }
            DriverDeploymentInfo deploymentInfo = runtimeManager.getDriverDeploymentInfo(driverDef.getUuid());
            assertNotNull(deploymentInfo);

            if (!hasDependants || options.isForcedUnDeployment()) {
                runtimeManager.unDeployDriver(deploymentInfo,
                                              options);
                when(driverProvider.getDeploymentInfo(driverDef.getUuid())).thenReturn(null);
                // the driver should have been un-deployed with the provider.
                verify(driverProvider,
                       times(1)).undeploy(driverDeploymentInfo);
                deploymentInfo = runtimeManager.getDriverDeploymentInfo(driverDef.getUuid());
                // no deployment info should exist
                assertNull(deploymentInfo);
            } else if (options.isSoftUnDeployment()) {
                expectedException.expectMessage("Driver: " + deploymentInfo + " can't be un-deployed. " +
                                                        "It's currently referenced by : " + 1 + " data sources");
                runtimeManager.unDeployDriver(driverDeploymentInfo,
                                              UnDeploymentOptions.softUnDeployment());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the deployment info retrieval for a driver.
     */
    @Test
    public void testGetDriverDeploymentInfo() {
        try {
            // simulate we have an already deployed driver
            deployDriver(driverDef);
            DriverDeploymentInfo deploymentInfo = runtimeManager.getDriverDeploymentInfo(driverDef.getUuid());
            assertNotNull(deploymentInfo);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void deployDriver(DriverDef driverDef) throws Exception {
        when(driverProvider.deploy(driverDef)).thenReturn(driverDeploymentInfo);
        runtimeManager.deployDriver(driverDef,
                                    DeploymentOptions.create());
        when(driverProvider.getDeploymentInfo(driverDef.getUuid())).thenReturn(driverDeploymentInfo);
    }

    /**
     * Tests the deployment of a data source.
     */
    @Test
    public void testDeployDataSource() {
        testDeployDataSource(true,
                             false,
                             DeploymentOptions.create());
    }

    /**
     * Tests the attempt for deployment of a data source when the driver is missing.
     */
    @Test
    public void testDeployDataSourceWithMissingDriver() {
        testDeployDataSource(false,
                             false,
                             DeploymentOptions.create());
    }

    /**
     * Tests the deployment of an existing data source by using the create deployment option.
     */
    @Test
    public void testDeployDataSourceExisting() {
        testDeployDataSource(true,
                             true,
                             DeploymentOptions.create());
    }

    /**
     * Tests the deployment of an existing data source by using the resync deployment option.
     */
    @Test
    public void testDeployDataSourceExistingResync() {
        testDeployDataSource(true,
                             true,
                             DeploymentOptions.createOrResync());
    }

    private void testDeployDataSource(boolean driverDeployed,
                                      boolean isDeployed,
                                      DeploymentOptions options) {
        try {
            if (!driverDeployed) {
                expectedException.expectMessage("Required driver: " + dataSourceDef.getDriverUuid() + " is not deployed.");
                runtimeManager.deployDataSource(dataSourceDef,
                                                options);
            } else {
                //emulates that the driver is properly deployed.
                when(driverProvider.getDeploymentInfo(dataSourceDef.getDriverUuid())).thenReturn(driverDeploymentInfo);

                if (isDeployed) {
                    when(dataSourceProvider.getDeploymentInfo(dataSourceDef.getUuid())).thenReturn(dataSourceDeploymentInfo);
                }
                if (!isDeployed) {
                    runtimeManager.deployDataSource(dataSourceDef,
                                                    options);
                    verify(dataSourceProvider,
                           times(1)).deploy(dataSourceDef);
                } else if (options.isCreateOrResyncDeployment()) {
                    runtimeManager.deployDataSource(dataSourceDef,
                                                    options);
                    verify(dataSourceProvider,
                           times(1)).resync(dataSourceDef,
                                            dataSourceDeploymentInfo);
                } else {
                    expectedException.expectMessage("Data source: " + dataSourceDef + " is already deployed");
                    runtimeManager.deployDataSource(dataSourceDef,
                                                    options);
                }
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the un-deployment of a data source.
     */
    @Test
    public void testUnDeployDataSource() {
        try {
            // emulates that the required driver is properly deployed.
            when(driverProvider.getDeploymentInfo(dataSourceDef.getDriverUuid())).thenReturn(driverDeploymentInfo);
            // simulate we have an already deployed data source
            deployDataSource(dataSourceDef);
            DataSourceDeploymentInfo deploymentInfo = runtimeManager.getDataSourceDeploymentInfo(dataSourceDef.getUuid());
            assertNotNull(deploymentInfo);

            runtimeManager.unDeployDataSource(deploymentInfo,
                                              UnDeploymentOptions.forcedUnDeployment());
            when(dataSourceProvider.getDeploymentInfo(dataSourceDef.getUuid())).thenReturn(null);
            // the data source should have been un-deployed with the provider.
            verify(dataSourceProvider,
                   times(1)).undeploy(dataSourceDeploymentInfo);
            deploymentInfo = runtimeManager.getDataSourceDeploymentInfo(dataSourceDef.getUuid());
            // no deployment info should exist
            assertNull(deploymentInfo);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the lookup of a data source properly deployed.
     */
    @Test
    public void testLookupDataSource() {
        testLookupDataSource(true);
    }

    /**
     * Tests the lookup of a not deployed data source.
     */
    @Test
    public void testLookupDataSourceNotDeployed() {
        testLookupDataSource(false);
    }

    private void testLookupDataSource(boolean isDeployed) {
        try {
            if (isDeployed) {
                // emulates that the required driver is properly deployed.
                when(driverProvider.getDeploymentInfo(dataSourceDef.getDriverUuid())).thenReturn(driverDeploymentInfo);
                // deploy the data source.
                deployDataSource(dataSourceDef);

                // the data source should exist.
                DataSource dataSource = runtimeManager.lookupDataSource(dataSourceDef.getUuid());
                assertNotNull(dataSource);
            } else {
                expectedException.expectMessage("Data source: " + dataSourceDef.getUuid() + " is not deployed in current system.");
                runtimeManager.lookupDataSource(dataSourceDef.getUuid());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests that the datasource runtime manager is properly initialized.
     */
    @Test
    public void testHasStartedOk() throws Exception {
        runtimeManager.hasStarted();
        verify(driverProvider,
               times(1)).hasStarted();
        verify(dataSourceProvider,
               times(1)).hasStarted();
    }

    /**
     * Tests that the case where the drivers provider throws errors upon the hasStarted check.
     */
    @Test
    public void testHasStartedWithError1() throws Exception {
        doThrow(new Exception(METHOD_EXECUTION_FAILED)).when(driverProvider).hasStarted();
        expectedException.expectMessage(METHOD_EXECUTION_FAILED);
        runtimeManager.hasStarted();
        verify(driverProvider,
               times(1)).hasStarted();
        verify(dataSourceProvider,
               never()).hasStarted();
    }

    /**
     * Tests that the case where the data sources provider throws errors upon the hasStarted check.
     */
    @Test
    public void testHasStartedWithError2() throws Exception {
        doThrow(new Exception(METHOD_EXECUTION_FAILED)).when(dataSourceProvider).hasStarted();
        expectedException.expectMessage(METHOD_EXECUTION_FAILED);
        runtimeManager.hasStarted();
        verify(driverProvider,
               times(1)).hasStarted();
        verify(dataSourceProvider,
               times(1)).hasStarted();
    }

    private void deployDataSource(DataSourceDef dataSourceDef) throws Exception {
        when(dataSourceProvider.deploy(dataSourceDef)).thenReturn(dataSourceDeploymentInfo);
        runtimeManager.deployDataSource(dataSourceDef,
                                        DeploymentOptions.create());
        when(dataSourceProvider.getDeploymentInfo(dataSourceDef.getUuid())).thenReturn(dataSourceDeploymentInfo);
        when(dataSourceProvider.lookupDataSource(dataSourceDeploymentInfo)).thenReturn(dataSource);
    }

    private class DataSourceRuntimeManagerMock
            extends DataSourceRuntimeManagerImpl {

        public DataSourceRuntimeManagerMock(DataSourceProviderFactory providerFactory) {
            super(providerFactory);
            init();
        }
    }
}