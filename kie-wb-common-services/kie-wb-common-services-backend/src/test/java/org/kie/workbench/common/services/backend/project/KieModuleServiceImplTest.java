/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.project;

import java.util.HashSet;
import java.util.Objects;

import javax.enterprise.event.Event;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.ModuleFinder;
import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.guvnor.common.services.project.events.NewModuleEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.service.PackageServiceLoader;
import org.kie.workbench.common.services.shared.project.KieModulePackages;
import org.kie.workbench.common.services.shared.project.PackageItem;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class KieModuleServiceImplTest {

    @Mock
    private PackageServiceLoader packageServiceLoader;

    @Mock
    private KieResourceResolver resourceResolver;

    private Path rootPath;
    private KieModuleServiceImpl moduleService;
    private Module activeModule;

    @Before
    public void setup() {
        rootPath = PathFactory.newPath("root",
                                       "file:///root");
        activeModule = new Module(rootPath,
                                  mock(Path.class));

        final Event<NewModuleEvent> newModuleEvent = mock(Event.class);
        final Event<NewPackageEvent> newPackageEvent = mock(Event.class);
        final Event<InvalidateDMOModuleCacheEvent> invalidateDMOCache = mock(Event.class);

        moduleService = new KieModuleServiceImpl(mock(IOService.class),
                                                 mock(ModuleSaver.class),
                                                 mock(POMService.class),
                                                 mock(RepositoryService.class),
                                                 newModuleEvent,
                                                 newPackageEvent,
                                                 invalidateDMOCache,
                                                 mock(SessionInfo.class),
                                                 mock(CommentedOptionFactory.class),
                                                 mock(ModuleFinder.class),
                                                 packageServiceLoader,
                                                 resourceResolver,
                                                 mock(ModuleRepositoryResolver.class)) {
        };
    }

    @Test
    public void testResolvePackage() {
        final Package pkg = new Package();
        doReturn(new HashSet<>()).when(packageServiceLoader).find(rootPath);
        doReturn(pkg).when(resourceResolver).resolvePackage(any());

        final Package resolvedPackage = moduleService.resolvePackage(activeModule,
                                                                     "org.test");
        assertNotNull(resolvedPackage);
    }

    @Test
    public void testNoPackages() {
        doReturn(new HashSet<>()).when(packageServiceLoader).find(rootPath);
        final KieModulePackages kieModulePackages = moduleService.resolveModulePackages(activeModule);

        assertEquals(1, kieModulePackages.getPackages().size());
        assertEquals("", kieModulePackages.getPackages().iterator().next().getPackageName());
        assertEquals("<default>", kieModulePackages.getPackages().iterator().next().getCaption());
    }

    @Test
    public void testPackages() {
        final HashSet<Object> packages = new HashSet<>();
        packages.add("");
        packages.add("org.test");

        doReturn(packages).when(packageServiceLoader).find(rootPath);
        final KieModulePackages kieModulePackages = moduleService.resolveModulePackages(activeModule);

        assertEquals(3, kieModulePackages.getPackages().size());
        assertContains("", kieModulePackages);
        assertContains("org", kieModulePackages);
        assertContains("org.test", kieModulePackages);
    }

    private void assertContains(final String pkgName,
                                final KieModulePackages kieModulePackages) {
        for (PackageItem aPackage : kieModulePackages.getPackages()) {
            if (Objects.equals(aPackage.getPackageName(), pkgName)) {
                return;
            }
        }
        fail("Could not find " + pkgName);
    }
}