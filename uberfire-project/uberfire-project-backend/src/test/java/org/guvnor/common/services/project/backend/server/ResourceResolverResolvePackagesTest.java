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
package org.guvnor.common.services.project.backend.server;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.inject.Instance;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.POMService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class ResourceResolverResolvePackagesTest {

    private static FileSystemTestingUtils fsUtils = new FileSystemTestingUtils();

    @Mock
    private POMService pomService;
    @Mock
    private CommentedOptionFactory commentedOptionFactory;
    @Mock
    private Instance<ModuleResourcePathResolver> resourcePathResolversInstance;

    private Module module;
    private ResourceResolver resourceResolver;

    @Before
    public void setUp() throws IOException {
        fsUtils.setup(false);

        Path root = fsUtils.getIoService().get(URI.create("git://amend-repo-test"));
        Path pomPath = fsUtils.getIoService().get(URI.create("git://amend-repo-test/pom.xml"));
        Path mainJava = fsUtils.getIoService().get(URI.create("git://amend-repo-test/src/main/java/org/test/.keep"));
        Path mainResource = fsUtils.getIoService().get(URI.create("git://amend-repo-test/src/main/resources/org/test/.keep"));
        Path testJava = fsUtils.getIoService().get(URI.create("git://amend-repo-test/src/test/java/org/test/.keep"));
        Path testResource = fsUtils.getIoService().get(URI.create("git://amend-repo-test/src/test/resources/org/test/.keep"));

        fsUtils.getIoService().write(pomPath,
                                     "");
        fsUtils.getIoService().write(mainJava,
                                     "");
        fsUtils.getIoService().write(mainResource,
                                     "");
        fsUtils.getIoService().write(testJava,
                                     "");
        fsUtils.getIoService().write(testResource,
                                     "");

        module = new Module(Paths.convert(root),
                            Paths.convert(pomPath));

        resourceResolver = new ResourceResolver(fsUtils.getIoService(),
                                                pomService,
                                                commentedOptionFactory,
                                                resourcePathResolversInstance) {

            @Override
            public Module resolveModule(final org.uberfire.backend.vfs.Path resource,
                                        final boolean loadPOM) {
                return module;
            }

            @Override
            public Module simpleModuleInstance(final Path nioModuleRootPath) {
                return null;
            }
        };
    }

    @After
    public void cleanupFileSystem() {
        fsUtils.cleanup();
    }

    @Test
    public void testResolvePackages() {

        final Set<Package> packages = resourceResolver.resolvePackages(module);

        assertEquals(3, packages.size());
        assertContains("", packages);
        assertContains("org", packages);
        assertContains("org.test", packages);
    }

    @Test
    public void testResolvePackagesWithPackageNames() {

        final HashSet<String> packageNames = new HashSet<>();
        packageNames.add("");
        packageNames.add("org");
        packageNames.add("org.test");

        final Set packages = resourceResolver.resolvePackages(module,
                                                              packageNames);

        assertEquals(3, packages.size());
        assertContains("", packages);
        assertContains("org", packages);
        assertContains("org.test", packages);
    }

    @Test
    public void testResolvePackagesWithPackageNamesEmpty() {

        final Set packages = resourceResolver.resolvePackages(module,
                                                              new HashSet<>());
        assertEquals(0, packages.size());
    }

    @Test
    public void testResolvePackagesWhenModuleNull() {

        final Set packages = resourceResolver.resolvePackages(null,
                                                              new HashSet<>());
        assertEquals(0, packages.size());
    }

    private void assertContains(final String pkgName,
                                final Set<Package> packages) {
        for (Package aPackage : packages) {
            if (Objects.equals(aPackage.getPackageName(), pkgName)) {
                return;
            }
        }
        fail("Could not find package " + pkgName);
    }
}