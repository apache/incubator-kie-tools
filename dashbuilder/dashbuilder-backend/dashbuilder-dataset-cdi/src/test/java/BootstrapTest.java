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

import javax.inject.Inject;

import org.dashbuilder.Bootstrap;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderRegistryCDI;
import org.dashbuilder.dataset.DataSetDefDeployer;
import org.dashbuilder.dataset.DataSetDefDeployerCDI;
import org.dashbuilder.dataset.DataSetDefRegistryCDI;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.test.BaseCDITest;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Arquillian.class)
@Ignore("see https://issues.jboss.org/browse/RHPAM-832")
public class BootstrapTest extends BaseCDITest {

    public static final String CSV_JSON = "{\n" +
            "  \"uuid\": \"expenseReports\",\n" +
            "  \"name\": \"Expense Reports\",\n" +
            "  \"provider\": \"CSV\",\n" +
            "}";

    @Inject
    Bootstrap bootstrap;

    @Inject
    DataSetProviderRegistryCDI providerRegistryCDI;

    @Inject
    DataSetDefRegistryCDI dataSetDefRegistryCDI;

    @Inject
    DataSetDefDeployerCDI dataSetDefDeployerCDI;

    @Before
    public void setUp() throws Exception {
        bootstrap.init();
    }

    @Test
    public void testStartupAnnotation() throws Exception {
        Startup startup = Bootstrap.class.getAnnotation(Startup.class);
        assertNotNull(startup);
        assertEquals(startup.value(), StartupType.BOOTSTRAP);
    }

    @Test
    public void testProviderRegistryInit() throws Exception {
        bootstrap.init();

        DataSetProviderRegistry dataSetProviderRegistry = DataSetCore.get().getDataSetProviderRegistry();
        assertEquals(dataSetProviderRegistry, providerRegistryCDI);
        assertEquals(dataSetProviderRegistry.getAvailableTypes().size(), 6);

        DataSetDefJSONMarshaller jsonMarshaller = DataSetCore.get().getDataSetDefJSONMarshaller();
        jsonMarshaller.fromJson(CSV_JSON); // No exception
    }

    @Test
    public void testDataSetDefRegistryInit() throws Exception {
        DataSetProviderRegistry providerRegistry = dataSetDefRegistryCDI.getDataSetProviderRegistry();
        DataSetDefJSONMarshaller jsonMarshaller = dataSetDefRegistryCDI.getDataSetDefJsonMarshaller();

        assertNotNull(providerRegistry);
        assertNotNull(jsonMarshaller);
        assertEquals(jsonMarshaller, providerRegistryCDI.getDataSetDefJSONMarshaller());
    }

    @Test
    public void testDataSetDeployerInit() throws Exception {
        DataSetDefDeployer dataSetDefDeployer = DataSetCore.get().getDataSetDefDeployer();
        assertNotNull(dataSetDefDeployer);
        assertEquals(dataSetDefDeployer, dataSetDefDeployerCDI);
    }

    @Test
    public void testNoListenerRegistration() throws Exception {
        assertEquals(dataSetDefRegistryCDI.getListeners().size(), 0);
    }
}

