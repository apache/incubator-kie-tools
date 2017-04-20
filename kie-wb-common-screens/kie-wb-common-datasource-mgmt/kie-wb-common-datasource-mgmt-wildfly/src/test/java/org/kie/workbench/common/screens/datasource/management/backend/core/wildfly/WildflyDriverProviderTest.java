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
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DriverProviderBaseTest;
import org.kie.workbench.common.screens.datasource.management.backend.integration.wildfly.WildflyDriverDef;
import org.kie.workbench.common.screens.datasource.management.backend.integration.wildfly.WildflyDriverManagementClient;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WildflyDriverProviderTest
        extends DriverProviderBaseTest {

    private static final String DRIVER1_DEPLOYMENT_ID = "kie#" + DRIVER1_UUID + "#";

    private static final String UUID2_DEPLOYMENT_ID = "kie#" + DRIVER2_UUID + "#";

    @Mock
    private WildflyDriverManagementClient managementClient;

    private List< WildflyDriverDef > wfDrivers = new ArrayList<>();

    @Mock
    private DriverDeploymentInfo deploymentInfo;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        super.setup();
        driverProvider = new WildflyDriverProvider(artifactResolver);
        ((WildflyDriverProvider) driverProvider).setDriverMgmtClient(managementClient);

        wfDrivers = createWFDrivers();
    }

    /**
     * Tests a driver deployment.
     */
    @Test
    public void testDeployDriver() throws Exception {
        super.testDeployDriver();
        // the driver should have been deployed by using the management client.
        verify(managementClient,
               times(1)).deploy(DRIVER1_DEPLOYMENT_ID,
                                driver1Uri);

        DriverDeploymentInfo expectedDeploymentInfo = new DriverDeploymentInfo(DRIVER1_DEPLOYMENT_ID,
                                                                               DRIVER1_DEPLOYMENT_ID,
                                                                               true,
                                                                               DRIVER1_UUID,
                                                                               DRIVER1_CLASS);
        DriverDeploymentInfo deploymentInfo = driverProvider.getDeploymentInfo(driverDef1.getUuid());
        assertEquals(expectedDeploymentInfo,
                     deploymentInfo);
    }

    /**
     * Tests that the provider is properly initialized.
     */
    @Test
    public void testHasStartedOk() throws Exception {
        when(managementClient.testConnection()).thenReturn(METHOD_EXECUTION_OK);
        when(managementClient.getDeployedDrivers()).thenReturn(Collections.EMPTY_LIST);
        driverProvider.hasStarted();
        verify(managementClient,
               times(1)).testConnection();
        verify(managementClient,
               times(1)).getDeployedDrivers();
    }

    /**
     * Tests the case when the connection fails.
     */
    @Test
    public void testHasStartedWithError1() throws Exception {
        when(managementClient.testConnection()).thenThrow(new Exception(METHOD_EXECUTION_FAILED));
        when(managementClient.getDeployedDrivers()).thenReturn(Collections.EMPTY_LIST);
        expectedException.expectMessage(METHOD_EXECUTION_FAILED);
        driverProvider.hasStarted();
        verify(managementClient,
               times(1)).testConnection();
        verify(managementClient,
               never()).getDeployedDrivers();
    }

    /**
     * Tests the case when the drivers retrieval fails.
     */
    @Test
    public void testHasStartedWithError2() throws Exception {
        when(managementClient.testConnection()).thenReturn(METHOD_EXECUTION_OK);
        when(managementClient.getDeployedDrivers()).thenThrow(new Exception(METHOD_EXECUTION_FAILED));
        expectedException.expectMessage(METHOD_EXECUTION_FAILED);
        driverProvider.hasStarted();
        verify(managementClient,
               times(1)).testConnection();
        verify(managementClient,
               times(1)).getDeployedDrivers();
    }

    @Override
    protected void deployDriver(DriverDef driverDef) throws Exception {
        when(managementClient.getDeployedDrivers()).thenReturn(wfDrivers);
        driverProvider.deploy(driverDef);
    }

    @Override
    protected void unDeployDriver(DriverDeploymentInfo deploymentInfo) throws Exception {
        driverProvider.undeploy(deploymentInfo);
        if (DRIVER1_UUID.equals(deploymentInfo.getUuid())) {
            wfDrivers.remove(0);
        } else if (DRIVER2_UUID.equals(deploymentInfo.getUuid())) {
            wfDrivers.remove(1);
        }
    }

    /**
     * Tests a driver un-deployment.
     */
    @Test
    public void testUnDeployDriver() throws Exception {
        super.testUnDeployDriver();
        // additionally the driver should have been un-deployed by using the management client.
        verify(managementClient,
               times(1)).undeploy(DRIVER1_DEPLOYMENT_ID);
    }

    /**
     * Tests the resync of a driver.
     */
    @Test
    public void testDriverResync() throws Exception {
        when(managementClient.getDeployedDrivers()).thenReturn(wfDrivers);
        DriverDeploymentInfo deploymentInfo = driverProvider.getDeploymentInfo(DRIVER1_UUID);
        // the deployment exists by construction but is not managed.
        assertNotNull(deploymentInfo);
        assertFalse(deploymentInfo.isManaged());
        driverProvider.resync(driverDef1,
                              deploymentInfo);
        deploymentInfo = driverProvider.getDeploymentInfo(DRIVER1_UUID);
        // after the resync operation the should have been tagged as managed.
        assertNotNull(deploymentInfo);
        assertTrue(deploymentInfo.isManaged());
    }

    private List< WildflyDriverDef > createWFDrivers() {
        // emulates the deployments information returned by the WF server.
        List< WildflyDriverDef > result = new ArrayList<>();

        WildflyDriverDef driverDef = new WildflyDriverDef();
        driverDef.setDeploymentName(DRIVER1_DEPLOYMENT_ID);
        driverDef.setDriverName(DRIVER1_DEPLOYMENT_ID);
        driverDef.setDriverClass(DRIVER1_CLASS);
        result.add(driverDef);

        driverDef = new WildflyDriverDef();
        driverDef.setDeploymentName(UUID2_DEPLOYMENT_ID);
        driverDef.setDriverName(UUID2_DEPLOYMENT_ID);
        driverDef.setDriverClass(DRIVER2_CLASS);
        result.add(driverDef);

        return result;
    }
}