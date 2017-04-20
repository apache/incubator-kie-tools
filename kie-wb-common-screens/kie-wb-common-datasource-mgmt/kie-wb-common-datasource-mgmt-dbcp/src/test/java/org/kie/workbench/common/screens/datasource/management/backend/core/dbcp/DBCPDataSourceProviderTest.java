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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceProviderBaseTest;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.util.MavenArtifactResolver;
import org.kie.workbench.common.screens.datasource.management.util.URLConnectionFactory;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DBCPDataSourceProviderTest
        extends DataSourceProviderBaseTest {

    @Mock
    private DBCPDriverProvider dbcpDriverProvider;

    @Mock
    private MavenArtifactResolver artifactResolver;

    @Mock
    private URLConnectionFactory urlConnectionFactory;

    private URI driver1Uri;

    private DriverDef driverDef1;

    private List< DriverDef > dbcpDrivers;

    @Before
    public void setup() throws Exception {
        super.setup();

        driverDef1 = new DriverDef();
        driverDef1.setUuid(DRIVER1_UUID);
        driverDef1.setName(DRIVER1_NAME);
        driverDef1.setDriverClass(DRIVER1_CLASS);
        driverDef1.setArtifactId(ARTIFACT_ID);
        driverDef1.setGroupId(GROUP_ID);
        driverDef1.setVersion(VERSION);

        dbcpDrivers = new ArrayList<>();
        dbcpDrivers.add(driverDef1);

        driver1Uri = new URI("file:///maven_dir/driver1_file.jar");
        when(artifactResolver.resolve(driverDef1.getGroupId(),
                                      driverDef1.getArtifactId(),
                                      driverDef1.getVersion()))
                .thenReturn(driver1Uri);

        driverProvider = dbcpDriverProvider;
        dataSourceProvider = new DBCPDataSourceProvider(dbcpDriverProvider,
                                                        artifactResolver) {
            @Override
            protected URLConnectionFactory buildConnectionFactory(URI uri,
                                                                  String driverClass,
                                                                  String connectionURL,
                                                                  Properties connectionProperties) throws Exception {
                return urlConnectionFactory;
            }
        };
    }

    @Override
    protected void setupDrivers() {
        when(dbcpDriverProvider.getDeployments()).thenReturn(dbcpDrivers);
    }

    @Override
    protected void deployDataSource(DataSourceDef dataSourceDef) throws Exception {
        dataSourceProvider.deploy(dataSourceDef);
    }

    @Override
    protected void unDeployDataSource(DataSourceDeploymentInfo deploymentInfo) throws Exception {
        dataSourceProvider.undeploy(deploymentInfo);
    }

    @Test
    public void testHasStarted() {
        try {
            dataSourceProvider.hasStarted();
        } catch (Exception e) {
            fail("The hasStarted method of the DBCPDataSourceProviderTest never throws exceptions by construction");
        }
    }
}