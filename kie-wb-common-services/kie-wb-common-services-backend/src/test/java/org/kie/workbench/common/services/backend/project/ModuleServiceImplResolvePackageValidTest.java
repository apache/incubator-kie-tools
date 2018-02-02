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
public class ModuleServiceImplResolvePackageValidTest extends ModuleTestBase {

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

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        //Test a root resolves to null
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

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        //Test a root/src resolves to null
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

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        //Test a root/src/main resolves to null
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

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/java");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/java");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test /src/main/java resolves as the default package
        final Package result = moduleService.resolvePackage(testPath);
        assertEquals(rootPath.toURI(),
                     result.getPackageMainSrcPath().toURI());
    }

    @Test
    public void testResolvePackageDefaultResources() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/resources");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/resources");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test /src/main/resources resolves as the default package
        final Package result = moduleService.resolvePackage(testPath);
        assertEquals(rootPath.toURI(),
                     result.getPackageMainResourcesPath().toURI());
    }

    @Test
    public void testResolvePackageWithJavaFileInDefaultPackage() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/java");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/java/Bean.java");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a Java file resolves to the containing package
        final Package result = moduleService.resolvePackage(testPath);
        assertEquals(rootPath.toURI(),
                     result.getPackageMainSrcPath().toURI());
    }

    @Test
    public void testResolvePackageWithJavaFileInSubPackage() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/java/org/kie/test");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/java/org/kie/test/Bean.java");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a Java file resolves to the containing package
        final Package result = moduleService.resolvePackage(testPath);
        assertEquals(rootPath.toURI(),
                     result.getPackageMainSrcPath().toURI());
    }

    @Test
    public void testResolvePackageWithResourcesFileInDefaultPackage() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/resources");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/resources/rule1.drl");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a Resources file resolves to the containing package
        final Package result = moduleService.resolvePackage(testPath);
        assertEquals(rootPath.toURI(),
                     result.getPackageMainResourcesPath().toURI());
    }

    @Test
    public void testResolvePackageWithResourcesFileInSubPackage() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL rootUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/resources/org/kie/test");
        final org.uberfire.java.nio.file.Path nioRootPath = fs.getPath(rootUrl.toURI());
        final Path rootPath = paths.convert(nioRootPath);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/resources/org/kie/test/rule1.drl");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a Resources file resolves to the containing package
        final Package result = moduleService.resolvePackage(testPath);
        assertEquals(rootPath.toURI(),
                     result.getPackageMainResourcesPath().toURI());
    }

    @Test
    public void testResolvePackageWithPOMFile() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/pom.xml");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a kModule.xml file resolves to a null package
        final Package result = moduleService.resolvePackage(testPath);
        assertNull(result);
    }

    @Test
    public void testResolvePackageWithKModuleFile() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/src/main/resources/META-INF/kmodule.xml");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a kModule.xml file resolves to a null package
        final Package result = moduleService.resolvePackage(testPath);
        assertNull(result);
    }

    @Test
    public void testIsPOMFileWithPOMFile() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/pom.xml");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a kModule.xml file resolves to a null package
        final boolean result = moduleService.isPom(testPath);
        assertTrue(result);
    }

    @Test
    public void testIsPOMFileWithNonPOMFile() throws Exception {

        final Bean moduleServiceBean = (Bean) beanManager.getBeans(KieModuleService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(moduleServiceBean);
        final KieModuleService moduleService = (KieModuleService) beanManager.getReference(moduleServiceBean,
                                                                                           KieModuleService.class,
                                                                                           cc);

        final URL testUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/project.imports");
        final org.uberfire.java.nio.file.Path nioTestPath = fs.getPath(testUrl.toURI());
        final Path testPath = paths.convert(nioTestPath);

        //Test a kModule.xml file resolves to a null package
        final boolean result = moduleService.isPom(testPath);
        assertFalse(result);
    }
}
