/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;

@RunWith(WeldJUnitRunner.class)
public class ModuleServiceImplResolvePackageInvalidNoPOMTest extends ModuleTestBase {

    @Test
    public void testProjectServiceInstantiation() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);
        assertNotNull(moduleService);
    }

    @Test
    public void testResolvePackageWithNonProjectPath() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/");
        final org.uberfire.java.nio.file.Path testNioPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(testNioPath);

        //Test a non-Project Path resolves to null
        final Package result = moduleService.resolvePackage(testPath);
        assertNull(result);
    }

    @Test
    public void testResolvePackageWithRootPath() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        //Test a non-Project Path resolves to null
        final Package result = moduleService.resolvePackage(rootPath);
        assertNull(result);
    }

    @Test
    public void testResolvePackageWithSrcPath() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM/src");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        //Test a non-Project Path resolves to null
        final Package result = moduleService.resolvePackage(rootPath);
        assertNull(result);
    }

    @Test
    public void testResolvePackageWithMainPath() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM/src/main");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        //Test a non-Project Path resolves to null
        final Package result = moduleService.resolvePackage(rootPath);
        assertNull(result);
    }

    @Test
    public void testResolvePackageDefaultJava() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM/src/main/java");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a non-Project Path resolves to null
        final Package result = moduleService.resolvePackage(testPath);
        assertNull(result);
    }

    @Test
    public void testResolvePackageDefaultResources() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM/src/main/resources");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a non-Project Path resolves to null
        final Package result = moduleService.resolvePackage(testPath);
        assertNull(result);
    }

    @Test
    public void testResolvePackageWithJavaFileInDefaultPackage() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM/src/main/java/Bean.java");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a non-Project Path resolves to null
        final Package result = moduleService.resolvePackage(testPath);
        assertNull(result);
    }

    @Test
    public void testResolvePackageWithJavaFileInSubPackage() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM/src/main/java/org/kie/test/Bean.java");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a non-Project Path resolves to null
        final Package result = moduleService.resolvePackage(testPath);
        assertNull(result);
    }

    @Test
    public void testResolvePackageWithResourcesFileInDefaultPackage() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM/src/main/resources/rule1.drl");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a non-Project Path resolves to null
        final Package result = moduleService.resolvePackage(testPath);
        assertNull(result);
    }

    @Test
    public void testResolvePackageWithResourcesFileInSubPackage() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureInvalidNoPOM/src/main/resources/org/kie/test/rule1.drl");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a non-Project Path resolves to null
        final Package result = moduleService.resolvePackage(testPath);
        assertNull(result);
    }
}
