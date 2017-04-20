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

package org.kie.workbench.common.screens.datasource.management.backend.core.dbcp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DriverProviderBaseTest;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DBCPDriverProviderTest
        extends DriverProviderBaseTest {

    @Before
    public void setup() throws Exception {
        super.setup();
        driverProvider = new DBCPDriverProvider(artifactResolver);
    }

    @Test
    public void testDeployDriver() throws Exception {
        super.testDeployDriver();
        // additional verification
        DriverDeploymentInfo deploymentInfo = driverProvider.getDeploymentInfo(driverDef1.getUuid());
        DriverDeploymentInfo expectedDeploymentInfo = new DriverDeploymentInfo(DRIVER1_UUID,
                                                                               DRIVER1_UUID,
                                                                               true,
                                                                               DRIVER1_UUID,
                                                                               DRIVER1_CLASS);
        assertEquals(expectedDeploymentInfo,
                     deploymentInfo);
    }

    @Override
    protected void deployDriver(DriverDef driverDef) throws Exception {
        driverProvider.deploy(driverDef);
    }

    @Override
    protected void unDeployDriver(DriverDeploymentInfo deploymentInfo) throws Exception {
        driverProvider.undeploy(deploymentInfo);
    }

    protected void testHasStarted() {
        try {
            driverProvider.hasStarted();
        } catch (Exception e) {
            fail("The hasStarted method of the DBCPDriverProvider never throws exceptions by construction");
        }
    }
}