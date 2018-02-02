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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceProviderFactory;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSourceDefQueryServiceTest {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    private final IOService ioService = new IOServiceDotFileImpl();

    @Mock
    private KieModuleService moduleService;

    @Mock
    private DataSourceServicesHelper serviceHelper;

    @Mock
    private DataSourceProviderFactory providerFactory;

    @Mock
    private DataSourceRuntimeManager runtimeManager;

    private DataSourceDefQueryService queryService;

    private org.uberfire.java.nio.file.Path nioDataSourcesPath;

    private Path dataSourcesPath;

    @Mock
    private KieModule module;

    @Mock
    private Path modulePath;

    private List<DataSourceDefInfo> expectedDataSources;

    private List<DriverDefInfo> expectedDrivers;

    @Before
    public void setup() throws Exception {

        fs.forceAsDefault();

        final URL dataSourcesPathURL = this.getClass().getResource("/DataSourceFiles");
        nioDataSourcesPath = fs.getPath(dataSourcesPathURL.toURI());
        dataSourcesPath = Paths.convert(nioDataSourcesPath);

        queryService = new DataSourceDefQueryServiceImpl(ioService,
                                                         moduleService,
                                                         serviceHelper,
                                                         providerFactory,
                                                         runtimeManager);

        setupExpectedResults();
    }

    @Test
    public void testFindGlobalDataSources() {
        when(serviceHelper.getGlobalDataSourcesContext()).thenReturn(dataSourcesPath);
        Collection<DataSourceDefInfo> results = queryService.findGlobalDataSources(true);
        assertCollectionEquals(expectedDataSources,
                               results);
    }

    @Test
    public void testFindModuleDataSourcesByModule() {
        when(serviceHelper.getModuleDataSourcesContext(module)).thenReturn(dataSourcesPath);
        Collection<DataSourceDefInfo> result = queryService.findModuleDataSources(module);
        assertCollectionEquals(expectedDataSources,
                               result);
    }

    @Test
    public void testFindModuleDataSourcesByModulePath() {
        when(moduleService.resolveModule(modulePath)).thenReturn(module);
        when(serviceHelper.getModuleDataSourcesContext(module)).thenReturn(dataSourcesPath);
        Collection<DataSourceDefInfo> result = queryService.findModuleDataSources(modulePath);
        assertCollectionEquals(expectedDataSources,
                               result);
    }

    @Test
    public void testFindGlobalDrivers() {
        when(serviceHelper.getGlobalDataSourcesContext()).thenReturn(dataSourcesPath);
        Collection<DriverDefInfo> results = queryService.findGlobalDrivers();
        assertCollectionEquals(expectedDrivers,
                               results);
    }

    @Test
    public void testFindModuleDriversByModule() {
        when(serviceHelper.getModuleDataSourcesContext(module)).thenReturn(dataSourcesPath);
        Collection<DriverDefInfo> result = queryService.findModuleDrivers(module);
        assertCollectionEquals(expectedDrivers,
                               result);
    }

    @Test
    public void testFindModuleDriversByModulePath() {
        when(moduleService.resolveModule(modulePath)).thenReturn(module);
        when(serviceHelper.getModuleDataSourcesContext(module)).thenReturn(dataSourcesPath);
        Collection<DriverDefInfo> result = queryService.findModuleDrivers(modulePath);
        assertCollectionEquals(expectedDrivers,
                               result);
    }

    @Test
    public void testFindModuleDriverByUuid() {
        when(moduleService.resolveModule(modulePath)).thenReturn(module);
        when(serviceHelper.getModuleDataSourcesContext(module)).thenReturn(dataSourcesPath);
        DriverDefInfo driverDefInfo = queryService.findModuleDriver("driver2Id",
                                                                    modulePath);
        assertEquals(expectedDrivers.get(1),
                     driverDefInfo);
    }

    private void assertCollectionEquals(Collection<?> expectedValues,
                                        Collection<?> values) {
        assertEquals(expectedValues.size(),
                     values.size());
        for (Object value : values) {
            assertTrue(expectedValues.contains(value));
        }
    }

    private void setupExpectedResults() {
        expectedDataSources = new ArrayList<>();
        expectedDataSources.add(
                new DataSourceDefInfo("ds1Id",
                                      "DS1",
                                      Paths.convert(nioDataSourcesPath.resolve("DS1.datasource")),
                                      null));

        expectedDataSources.add(
                new DataSourceDefInfo("ds2Id",
                                      "DS2",
                                      Paths.convert(nioDataSourcesPath.resolve("DS2.datasource")),
                                      null));

        expectedDrivers = new ArrayList<>();
        expectedDrivers.add(
                new DriverDefInfo("driver1Id",
                                  "Driver1",
                                  Paths.convert(nioDataSourcesPath.resolve("Driver1.driver")),
                                  null));

        expectedDrivers.add(
                new DriverDefInfo("driver2Id",
                                  "Driver2",
                                  Paths.convert(nioDataSourcesPath.resolve("Driver2.driver")),
                                  null));
    }
}