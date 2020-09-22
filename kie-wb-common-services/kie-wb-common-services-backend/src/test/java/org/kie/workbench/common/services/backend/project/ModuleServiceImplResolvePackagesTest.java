/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.service.PackageServiceLoader;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.PackageItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for ModuleServiceImpl resolveTestPackage
 */
@RunWith(WeldJUnitRunner.class)
public class ModuleServiceImplResolvePackagesTest extends ModuleTestBase {

    @Test
    public void testResolvePackages() throws Exception {

        final KieModuleService moduleService = getKieModuleService();

        final URL root = this.getClass().getResource("/ModuleBackendTestModule1");
        final URL pomUrl = this.getClass().getResource("/ModuleBackendTestModule1/pom.xml");
        final URL kmodule = this.getClass().getResource("/ModuleBackendTestModule1/src/main/resources/META-INF/kmodule.xml");
        final URL imports = this.getClass().getResource("/ModuleBackendTestModule1/project.imports");
        final URL repositories = this.getClass().getResource("/ModuleBackendTestModule1/project.repositories");
        final URL packageNameWhiteList = this.getClass().getResource("/ModuleBackendTestModule1/package-names-white-list");
        final POM pom = mock(POM.class);
        when(pom.getName()).thenReturn("ModuleBackendTestModule1");

        final Module module = new KieModule(paths.convert(fs.getPath(root.toURI())),
                                            paths.convert(fs.getPath(pomUrl.toURI())),
                                            paths.convert(fs.getPath(kmodule.toURI())),
                                            paths.convert(fs.getPath(imports.toURI())),
                                            paths.convert(fs.getPath(repositories.toURI())),
                                            paths.convert(fs.getPath(packageNameWhiteList.toURI())),
                                            pom);

        {
            Set<Package> packages = moduleService.resolvePackages((Package) null);
            assertEquals(0,
                         packages.size());
        }

        {
            when(pom.getGav()).thenReturn(new GAV("org.mygroup", "my-project", "1.0.0"));
            final Set<Package> packages = moduleService.resolvePackages(module);
            final HashSet<String> packageNames = new HashSet<>();
            packageNames.add("");
            packageNames.add("org");
            packageNames.add("org.kie");
            packageNames.add("org.kie.test");
            packageNames.add("org.kie.test.project");
            packageNames.add("org.kie.test.project.backend");
            final Set<Package> actual = moduleService.resolvePackages(module,
                                                                      packageNames);
            assertEquals(packages.size(), actual.size());
            for (Package aPackage : actual) {
                assertEquals(1, packages.stream().filter(p -> p.getPackageName().equals(aPackage.getPackageName())).count());
            }

            assertEquals(PackageItem.DEFAULT_PACKAGE_NAME, moduleService.resolveDefaultPackage(module).getCaption());
        }

        Package defaultPkg = null;
        {
            Set<Package> packages = moduleService.resolvePackages(module);
            assertEquals(6,
                         packages.size());
            for (final Package pkg : packages) {
                if (pkg.getCaption().equals(PackageItem.DEFAULT_PACKAGE_NAME)) {
                    defaultPkg = pkg;
                    break;
                }
            }
            assertEquals(defaultPkg,
                         moduleService.resolveDefaultPackage(module));
        }

        assertNotNull(defaultPkg);
        assertEquals(PackageItem.DEFAULT_PACKAGE_NAME,
                     defaultPkg.getCaption());
        assertEquals(PackageItem.DEFAULT_PACKAGE_NAME,
                     defaultPkg.getRelativeCaption());

        Package rootPkg = null;
        {
            Set<Package> packages = moduleService.resolvePackages(defaultPkg);
            assertEquals(1,
                         packages.size());
            rootPkg = packages.iterator().next();
        }

        assertNotNull(rootPkg);
        assertEquals("org",
                     rootPkg.getCaption());
        assertEquals("org",
                     rootPkg.getRelativeCaption());

        Package kiePkg = null;
        {
            Set<Package> packages = moduleService.resolvePackages(rootPkg);
            assertEquals(1,
                         packages.size());
            kiePkg = packages.iterator().next();
        }
        assertNotNull(kiePkg);
        assertEquals("org.kie",
                     kiePkg.getCaption());
        assertEquals("kie",
                     kiePkg.getRelativeCaption());

        final Package actual = moduleService.resolveParentPackage(kiePkg);
        assertEquals(rootPkg.getPackageName(),
                     actual.getPackageName());

        assertEquals(defaultPkg,
                     moduleService.resolveParentPackage(rootPkg));

        assertNull(moduleService.resolveParentPackage(defaultPkg));

        {
            Set<Package> packages = moduleService.resolvePackages(kiePkg);
            assertEquals(1,
                         packages.size());
        }
    }

    private KieModuleService getKieModuleService() {
        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        return (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                           KieModuleService.class,
                                                           cc);
    }

    private PackageServiceLoader getPackageServiceLoader() {
        final Bean bean = (Bean) beanManager.getBeans(PackageServiceLoader.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(bean);
        return (PackageServiceLoader) beanManager.getReference(bean,
                                                               PackageServiceLoader.class,
                                                               cc);
    }
}
