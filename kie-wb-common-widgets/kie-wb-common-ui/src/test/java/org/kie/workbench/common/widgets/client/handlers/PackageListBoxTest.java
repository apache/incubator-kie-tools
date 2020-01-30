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

package org.kie.workbench.common.widgets.client.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModulePackages;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class PackageListBoxTest {

    @Mock
    private KieModuleService moduleService;

    @Mock
    private PackageListBoxView view;

    @Captor
    private ArgumentCaptor<Package> packageArgumentCaptor;

    @Captor
    private ArgumentCaptor<Map> mapArgumentCaptor;

    private CallerMock<KieModuleService> moduleServiceCaller;

    private WorkspaceProjectContext moduleContext;

    private PackageListBox packageListBox;

    private KieModulePackages kieModulePackages;

    @Before
    public void setup() {
        moduleServiceCaller = new CallerMock<>(moduleService);
        setupWorkspaceProjectContext();
        packageListBox = spy(new PackageListBox(view,
                                                moduleContext,
                                                moduleServiceCaller));
    }

    private void setupPackageList() {
        final List<Package> packages = new ArrayList<>();
        packages.add(createPackage(""));
        packages.add(createPackage("com"));
        packages.add(createPackage("com.myteam"));
        final Package defaultPackage = createPackage("com.myteam.mymodule");
        packages.add(defaultPackage);
        packages.add(createPackage("com.myteam.mymodule.mypackage"));

        kieModulePackages = new KieModulePackages(new HashSet<>(packages), defaultPackage);
        doReturn(kieModulePackages).when(moduleService).resolveModulePackages(any(Module.class));
    }

    @Test
    public void setContextTest() {
        setupPackageList();

        final Command packagesLoadedCommand = mock(Command.class);

        packageListBox.setUp(true,
                             packagesLoadedCommand);

        verify(view).setUp(eq("com.myteam.mymodule"),
                           mapArgumentCaptor.capture());

        final Map map = mapArgumentCaptor.getValue();
        assertEquals(5, map.size());
        assertTrue(map.keySet().contains(""));
        assertTrue(map.keySet().contains("com"));
        assertTrue(map.keySet().contains("com.myteam"));
        assertTrue(map.keySet().contains("com.myteam.mymodule"));
        assertTrue(map.keySet().contains("com.myteam.mymodule.mypackage"));

        verify(packagesLoadedCommand).execute();
    }

    @Test
    public void setContextTestDropDefaultPackage() {
        setupPackageList();

        final Command packagesLoadedCommand = mock(Command.class);

        packageListBox.setUp(false,
                             packagesLoadedCommand);

        verify(view).setUp(eq("com.myteam.mymodule"),
                           mapArgumentCaptor.capture());

        final Map map = mapArgumentCaptor.getValue();
        assertEquals(4, map.size());
        assertFalse(map.keySet().contains(""));
        assertTrue(map.keySet().contains("com"));
        assertTrue(map.keySet().contains("com.myteam"));
        assertTrue(map.keySet().contains("com.myteam.mymodule"));
        assertTrue(map.keySet().contains("com.myteam.mymodule.mypackage"));

        verify(packagesLoadedCommand).execute();
    }

    @Test
    public void setContextTestDropDefaultDefaultPackageInTheList() {
        setupPackageList();
        kieModulePackages.setDefaultPackage(createPackage(""));
        doReturn(kieModulePackages).when(moduleService).resolveModulePackages(any());

        final Command packagesLoadedCommand = mock(Command.class);

        packageListBox.setUp(true,
                             packagesLoadedCommand);

        verify(view).setUp(eq(""),
                           mapArgumentCaptor.capture());

        final Map map = mapArgumentCaptor.getValue();
        assertEquals(5, map.size());
        assertTrue(map.keySet().contains(""));
        assertTrue(map.keySet().contains("com"));
        assertTrue(map.keySet().contains("com.myteam"));
        assertTrue(map.keySet().contains("com.myteam.mymodule"));
        assertTrue(map.keySet().contains("com.myteam.mymodule.mypackage"));

        verify(packagesLoadedCommand).execute();
    }

    @Test
    public void setContextTestDropDefaultNoDefaultPackageInTheList() {
        setupPackageList();
        kieModulePackages.setDefaultPackage(createPackage("com"));
        doReturn(kieModulePackages).when(moduleService).resolveModulePackages(any());

        final Command packagesLoadedCommand = mock(Command.class);

        packageListBox.setUp(false,
                             packagesLoadedCommand);

        verify(view).setUp(eq("com"),
                           mapArgumentCaptor.capture());

        final Map map = mapArgumentCaptor.getValue();
        assertEquals(4, map.size());
        assertFalse(map.keySet().contains(""));
        assertTrue(map.keySet().contains("com"));
        assertTrue(map.keySet().contains("com.myteam"));
        assertTrue(map.keySet().contains("com.myteam.mymodule"));
        assertTrue(map.keySet().contains("com.myteam.mymodule.mypackage"));

        verify(packagesLoadedCommand).execute();
    }

    private Package createPackage(String caption) {
        final Package pkg = mock(Package.class);
        doReturn(caption).when(pkg).getCaption();
        doReturn(caption).when(pkg).getPackageName();
        return pkg;
    }

    private void setupWorkspaceProjectContext() {
        moduleContext = new WorkspaceProjectContext() {
            {
                setActiveModule(mock(Module.class));
                Package activePackage = mock(Package.class);
                doReturn("com").when(activePackage).getCaption();
                setActivePackage(activePackage);
            }
        };
    }
}
