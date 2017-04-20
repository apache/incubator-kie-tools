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

package org.kie.workbench.common.screens.datasource.management.backend.core.wildfly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceProviderBaseTest;
import org.kie.workbench.common.screens.datasource.management.backend.integration.wildfly.WildflyDataSourceDef;
import org.kie.workbench.common.screens.datasource.management.backend.integration.wildfly.WildflyDataSourceManagementClient;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WildflyDataSourceProviderTest
        extends DataSourceProviderBaseTest {

    @Mock
    private WildflyDataSourceManagementClient managementClient;

    @Mock
    private WildflyDriverProvider wfDriverProvider;

    private List< WildflyDataSourceDef > wfDataSources;

    @Mock
    protected DriverDeploymentInfo driverDeploymentInfo;

    @Mock
    protected DataSourceDeploymentInfo dataSourceDeploymentInfo;

    @Mock
    protected javax.sql.DataSource sqlDataSource;

    @Before
    public void setup() throws Exception {
        super.setup();
        driverProvider = wfDriverProvider;
        dataSourceProvider = new WildflyDataSourceProvider(wfDriverProvider) {
            @Override
            protected Object jndiLookupDataSource(String jndi) {
                return sqlDataSource;
            }

            @Override
            protected String generateRandomUUID() {
                return RANDOM_UUID;
            }
        };

        ((WildflyDataSourceProvider) dataSourceProvider).setDataSourceMgmtClient(managementClient);

        wfDataSources = createWFDataSources();
    }

    @Override
    public void testDeployDataSource() throws Exception {
        super.testDeployDataSource();
        // additional check
        // expected WF data source definition that should be created in the WF server.
        WildflyDataSourceDef wfDataSource = new WildflyDataSourceDef();
        wfDataSource.setName(DeploymentIdGenerator.generateDeploymentId(dataSourceDef));
        wfDataSource.setConnectionURL(dataSourceDef.getConnectionURL());
        wfDataSource.setUser(dataSourceDef.getUser());
        wfDataSource.setPassword(dataSourceDef.getPassword());
        wfDataSource.setDriverName(DRIVER1_DEPLOYMENT_ID);
        wfDataSource.setJndi(JndiNameGenerator.generateJNDIName(dataSourceDef));

        // the expected WF data source definition should have been created in the WF server.
        ArgumentCaptor< WildflyDataSourceDef > argumentCaptor = ArgumentCaptor.forClass(WildflyDataSourceDef.class);
        verify(managementClient,
               times(1)).createDataSource(argumentCaptor.capture());
        if (!areTheSame(wfDataSource,
                        argumentCaptor.getValue())) {
            fail("Data source definition wasn't properly created in the WF server: " + wfDataSource);
        }
    }

    @Override
    protected void setupDrivers() throws Exception {
        // driver information that will be returned for this case.
        when(driverProvider.getDeploymentInfo(DRIVER1_UUID)).thenReturn(driverDeploymentInfo);
        when(driverDeploymentInfo.getDriverDeploymentId()).thenReturn(DRIVER1_DEPLOYMENT_ID);
    }

    @Override
    protected void deployDataSource(DataSourceDef dataSourceDef) throws Exception {
        when(managementClient.getDataSources()).thenReturn(wfDataSources);
        dataSourceProvider.deploy(dataSourceDef);
    }

    @Override
    protected void unDeployDataSource(DataSourceDeploymentInfo deploymentInfo) throws Exception {
        // un-deploy the data source.
        dataSourceProvider.undeploy(deploymentInfo);
        wfDataSources.remove(0);
    }

    /**
     * Tests a data source un-deployment.
     */
    @Test
    public void testUnDeployDataSource() throws Exception {
        super.testUnDeployDataSource();
        // additional check
        // the data source should have been deleted from the WF server by using the management client.
        ArgumentCaptor< String > argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(managementClient,
               times(1)).deleteDataSource(argumentCaptor.capture());
        assertEquals(DS1_DEPLOYMENT_ID,
                     argumentCaptor.getValue());
    }

    /**
     * Tests the resync of a data source.
     */
    @Test
    public void testDataSourceResync() throws Exception {
        when(managementClient.getDataSources()).thenReturn(wfDataSources);
        when(dataSourceDeploymentInfo.getJndi()).thenReturn(DS1_JNID);
        when(dataSourceDeploymentInfo.getDeploymentId()).thenReturn(DS1_DEPLOYMENT_ID);

        DataSourceDeploymentInfo deploymentInfo = dataSourceProvider.getDeploymentInfo(DS1_UUID);

        // the data source deployment info should exist, but it's not managed.
        assertNotNull(deploymentInfo);
        assertFalse(deploymentInfo.isManaged());

        dataSourceProvider.resync(dataSourceDef,
                                  dataSourceDeploymentInfo);
        deploymentInfo = dataSourceProvider.getDeploymentInfo(DS1_UUID);
        // after the resync the data source should have been tagged as managed.
        assertNotNull(deploymentInfo);
        assertTrue(deploymentInfo.isManaged());
    }

    /**
     * Tests that the provider is properly initialized.
     */
    @Test
    public void testHasStartedOk() throws Exception {
        when(managementClient.testConnection()).thenReturn(METHOD_EXECUTION_OK);
        when(managementClient.getDataSources()).thenReturn(Collections.EMPTY_LIST);
        dataSourceProvider.hasStarted();
        verify(managementClient,
               times(1)).testConnection();
        verify(managementClient,
               times(1)).getDataSources();
    }

    /**
     * Tests the case when the connection fails.
     */
    @Test
    public void testHasStartedWithError1() throws Exception {
        when(managementClient.testConnection()).thenThrow(new Exception(METHOD_EXECUTION_FAILED));
        when(managementClient.getDataSources()).thenReturn(Collections.EMPTY_LIST);
        expectedException.expectMessage(METHOD_EXECUTION_FAILED);
        dataSourceProvider.hasStarted();
        verify(managementClient,
               times(1)).testConnection();
        verify(managementClient,
               never()).getDataSources();
    }

    /**
     * Tests the case when the data sources retrieval fails.
     */
    @Test
    public void testHasStartedWithError2() throws Exception {
        when(managementClient.testConnection()).thenReturn(METHOD_EXECUTION_OK);
        when(managementClient.getDataSources()).thenThrow(new Exception(METHOD_EXECUTION_FAILED));
        expectedException.expectMessage(METHOD_EXECUTION_FAILED);
        dataSourceProvider.hasStarted();
        verify(managementClient,
               times(1)).testConnection();
        verify(managementClient,
               times(1)).getDataSources();
    }

    private boolean areTheSame(WildflyDataSourceDef expectedDef,
                               WildflyDataSourceDef currentDef) {
        return currentDef != null &&
                currentDef.getName().startsWith(expectedDef.getName()) &&
                currentDef.getDriverName().equals(expectedDef.getDriverName()) &&
                currentDef.getJndi().startsWith(expectedDef.getJndi()) &&
                currentDef.getConnectionURL().equals(expectedDef.getConnectionURL()) &&
                currentDef.getUser().equals(expectedDef.getUser()) &&
                currentDef.isUseJTA() == currentDef.isUseJTA();
    }

    private List< WildflyDataSourceDef > createWFDataSources() {
        // emulates the data sources deployments information returned by the WF server.
        List< WildflyDataSourceDef > result = new ArrayList<>();

        WildflyDataSourceDef dataSourceDef = new WildflyDataSourceDef();
        dataSourceDef.setName(DS1_DEPLOYMENT_ID);
        dataSourceDef.setDriverName(DRIVER1_DEPLOYMENT_ID);
        dataSourceDef.setConnectionURL(DS1_CONNECTION_URL);
        dataSourceDef.setPassword(DS1_PASSWORD);
        dataSourceDef.setUser(DS1_USER);
        dataSourceDef.setJndi(DS1_JNID);
        result.add(dataSourceDef);

        dataSourceDef = new WildflyDataSourceDef();
        dataSourceDef.setName(DS2_DEPLOYMENT_ID);
        dataSourceDef.setDriverName(DRIVER1_DEPLOYMENT_ID);
        dataSourceDef.setConnectionURL(DS2_CONNECTION_URL);
        dataSourceDef.setPassword(DS2_PASSWORD);
        dataSourceDef.setUser(DS2_USER);
        dataSourceDef.setJndi(DS2_JNID);

        result.add(dataSourceDef);
        return result;
    }
}