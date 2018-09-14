/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.backend.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.backend.core.impl.DefaultDriverInitializerImpl;
import org.kie.workbench.common.screens.datasource.management.backend.service.DataSourceServicesHelper;
import org.kie.workbench.common.screens.datasource.management.backend.service.DefRegistry;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.util.DriverDefSerializer;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDriverInitializerTest {

    private static final String GLOBAL_URI = "default://master@datasources/";

    @Mock
    private IOService ioService;

    @Mock
    private DataSourceServicesHelper serviceHelper;

    @Mock
    private CommentedOptionFactory optionsFactory;

    @Mock
    private CommentedOption commentedOption;

    private MyDefaultDriverInitializer driverInitializer;

    @Mock
    private DefRegistry defRegistry;

    @Mock
    private Path globalPath;

    private org.uberfire.java.nio.file.Path globalNioPath;

    private org.uberfire.java.nio.file.Path globalNioPaths[];

    private List<DriverDef> expectedDrivers;

    @Before
    public void setup() {
        when(globalPath.toURI()).thenReturn(GLOBAL_URI);
        globalNioPath = Paths.convert(globalPath);
        when(serviceHelper.getGlobalDataSourcesContext()).thenReturn(globalPath);
        when(serviceHelper.getDefRegistry()).thenReturn(defRegistry);

        setUpSystemDrivers();
        // create the expected drivers and the expected destination paths
        expectedDrivers = createExpectedDrivers();
        globalNioPaths = new org.uberfire.java.nio.file.Path[expectedDrivers.size()];
        for (int i = 0; i < globalNioPaths.length; i++) {
            globalNioPaths[i] = globalNioPath.resolve(expectedDrivers.get(i).getName() + ".driver");
        }

        when(optionsFactory.makeCommentedOption("system generated driver")).thenReturn(commentedOption);
        driverInitializer = spy(new MyDefaultDriverInitializer(ioService,
                                                               serviceHelper,
                                                               optionsFactory));
    }

    @Test
    public void testInitializeDefaultDriversEnabled() {
        doReturn(false).when(driverInitializer).areDriversDisabledByDefault();

        driverInitializer.initializeDefaultDrivers();

        // All driver definitions provided as System properties, and the ones provided in the configuration file should
        // have written in the expected target paths.
        String expectedSource;
        for (int i = 0; i < expectedDrivers.size(); i++) {
            expectedSource = DriverDefSerializer.serialize(expectedDrivers.get(i));
            verify(ioService,
                   times(1)).write(globalNioPaths[i],
                                   expectedSource,
                                   commentedOption);
            verify(defRegistry,
                   times(1)).setEntry(Paths.convert(globalNioPaths[i]),
                                      expectedDrivers.get(i));
        }
    }

    @Test
    public void testInitializeDefaultDriversDisabled() {
        doReturn(true).when(driverInitializer).areDriversDisabledByDefault();

        driverInitializer.initializeDefaultDrivers();

        verify(ioService, never()).write(any(org.uberfire.java.nio.file.Path.class), anyString(), any(CommentedOption.class));
        verify(defRegistry, never()).setEntry(any(Path.class), any(DriverDef.class));
    }

    @Test
    public void testDisableDefaultDrivers() {
        System.getProperties().setProperty(DefaultDriverInitializerImpl.DISABLE_DEFAULT_DRIVERS,
                                           "true");
        driverInitializer.initializeDefaultDrivers();
        verify(driverInitializer,
               never()).initializeFromConfigFile();
        verify(driverInitializer,
               never()).initializeFromSystemProperties();
    }

    class MyDefaultDriverInitializer
            extends DefaultDriverInitializerImpl {

        public MyDefaultDriverInitializer(IOService ioService,
                                          DataSourceServicesHelper serviceHelper,
                                          CommentedOptionFactory optionsFactory) {
            super(ioService,
                  serviceHelper,
                  optionsFactory);
        }

        @Override
        protected void initializeFromSystemProperties() {
            super.initializeFromSystemProperties();
        }

        @Override
        protected void initializeFromConfigFile() {
            super.initializeFromConfigFile();
        }
    }

    private void setUpSystemDrivers() {
        // emulates drivers definitions passed as System properties.
        Properties properties = System.getProperties();
        for (int i = 0; i < 2; i++) {
            properties.put("driverDef.uuid." + i,
                           "sys-uuid" + i);
            properties.put("driverDef.name." + i,
                           "sys-name" + i);
            properties.put("driverDef.driverClass." + i,
                           "sys-driverClass" + i);
            properties.put("driverDef.groupId." + i,
                           "sys-groupId" + i);
            properties.put("driverDef.artifactId." + i,
                           "sys-artifactId" + i);
            properties.put("driverDef.version." + i,
                           "sys-version" + i);
        }
    }

    private List<DriverDef> createExpectedDrivers() {
        // create 2 expected drivers defined by System properties.
        List<DriverDef> expectedDrivers = createDrivers("sys-",
                                                        2);
        // and 2 more coming from the configuration file.
        expectedDrivers.addAll(createDrivers("",
                                             2));
        return expectedDrivers;
    }

    private List<DriverDef> createDrivers(String prefix,
                                          int count) {
        List<DriverDef> driverDefs = new ArrayList<>();
        DriverDef driverDef;
        for (int i = 0; i < count; i++) {
            driverDef = new DriverDef();
            driverDef.setUuid(prefix + "uuid" + i);
            driverDef.setName(prefix + "name" + i);
            driverDef.setDriverClass(prefix + "driverClass" + i);
            driverDef.setGroupId(prefix + "groupId" + i);
            driverDef.setArtifactId(prefix + "artifactId" + i);
            driverDef.setVersion(prefix + "version" + i);
            driverDefs.add(driverDef);
        }
        return driverDefs;
    }
}