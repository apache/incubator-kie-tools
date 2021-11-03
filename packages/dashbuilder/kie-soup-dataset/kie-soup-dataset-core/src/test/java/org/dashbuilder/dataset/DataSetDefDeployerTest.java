/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset;

import java.io.File;
import java.io.FileOutputStream;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.DataSetDefRegistryListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetDefDeployerTest {

    DataSetDefRegistry dataSetDefRegistry;
    DataSetDefDeployer dataSetDefDeployer;
    String dataSetsDir = Thread.currentThread().getContextClassLoader().getResource("deployments").getFile();

    @Mock
    DataSetDefRegistryListener registryListener;

    @Before
    public void setUp() {
        dataSetDefDeployer = DataSetCore.get().getDataSetDefDeployer();
        dataSetDefRegistry = DataSetCore.get().getDataSetDefRegistry();
        dataSetDefRegistry.addListener(registryListener);
        dataSetDefDeployer.setScanIntervalInMillis(1000);
        assertNotNull(dataSetDefDeployer);
        assertNotNull(dataSetDefRegistry);
    }

    @Test
    public void testDoDeploy() throws Exception {
        assertNull(dataSetDefRegistry.getDataSetDef("salesPerYearAutoDeploy"));
        dataSetDefDeployer.deploy(dataSetsDir);

        FileOutputStream doDeploy = new FileOutputStream(new File(dataSetsDir, "salesPerYear.dset.deploy"));
        doDeploy.write("".getBytes());
        doDeploy.flush();
        doDeploy.close();

        Thread.sleep(2000);
        DataSetDef def = dataSetDefRegistry.getDataSetDef("salesPerYearAutoDeploy");
        assertNotNull(def);
        verify(registryListener).onDataSetDefRegistered(def);
    }
}
