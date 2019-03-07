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

package org.kie.workbench.common.services.backend.builder.core;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.io.Resources;
import org.drools.core.rule.TypeMetaInfo;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.m2repo.backend.server.M2ServletContextListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.validation.asset.DefaultGenericKieValidator;
import org.kie.workbench.common.services.backend.whitelist.PackageNameSearchProvider;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListLoader;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListSaver;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListServiceImpl;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BuilderTest
        extends BuilderTestBase {

    private static final Logger logger = LoggerFactory.getLogger(BuilderTest.class);

    private final Predicate<String> alwaysTrue = o -> true;

    @Mock
    private PackageNameSearchProvider packageNameSearchProvider;

    private IOService ioService;
    private KieModuleService moduleService;
    private ProjectImportsService importsService;
    private LRUModuleDependenciesClassLoaderCache dependenciesClassLoaderCache;
    private LRUPomModelCache pomModelCache;
    private DefaultGenericKieValidator validator;

    @Before
    public void setUp() throws Exception {
        PackageNameSearchProvider.PackageNameSearch nameSearch = mock(PackageNameSearchProvider.PackageNameSearch.class);
        when(nameSearch.search()).thenReturn(new HashSet<String>());
        when(packageNameSearchProvider.newTopLevelPackageNamesSearch(any(POM.class))).thenReturn(nameSearch);
        super.startWeld();
        setUpGuvnorM2Repo();

        ioService = getReference(IOService.class);
        moduleService = getReference(KieModuleService.class);
        importsService = getReference(ProjectImportsService.class);
        dependenciesClassLoaderCache = getReference(LRUModuleDependenciesClassLoaderCache.class);
        pomModelCache = getReference(LRUPomModelCache.class);
        validator = getReference(DefaultGenericKieValidator.class);
    }

    @After
    public void cleanUp() {
        super.stopWeld();
    }

    @Test
    public void testBuilderSimpleKModule() throws Exception {
        LRUPomModelCache pomModelCache = getReference(LRUPomModelCache.class);

        URL url = this.getClass().getResource("/GuvnorM2RepoDependencyExample1");
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath(url.toURI());

        final Module module = moduleService.resolveModule(Paths.convert(path));

        final Builder builder = new Builder(module,
                                            ioService,
                                            moduleService,
                                            importsService,
                                            new ArrayList<>(),
                                            dependenciesClassLoaderCache,
                                            pomModelCache,
                                            getPackageNameWhiteListService(),
                                            alwaysTrue);

        assertNotNull(builder.getKieContainer());
    }

    @Test
    public void testBuilderKModuleHasDependency() throws Exception {
        URL url = this.getClass().getResource("/GuvnorM2RepoDependencyExample2");
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath(url.toURI());

        final Module module = moduleService.resolveModule(Paths.convert(path));

        final Builder builder = new Builder(module,
                                            ioService,
                                            moduleService,
                                            importsService,
                                            new ArrayList<>(),
                                            dependenciesClassLoaderCache,
                                            pomModelCache,
                                            getPackageNameWhiteListService(),
                                            alwaysTrue);

        final BuildResults results = builder.build();

        //Debug output
        if (!results.getMessages().isEmpty()) {
            for (BuildMessage m : results.getMessages()) {
                logger.debug(m.getText());
            }
        }

        assertTrue(results.getMessages().isEmpty());
    }

    @Test
    public void testBuilderKModuleHasSnapshotDependency() throws Exception {
        M2ServletContextListener context = new M2ServletContextListener();
        GAV gav = new GAV("org.kie.workbench.common.services.builder.tests",
                          "dependency-test1-snapshot",
                          "1.0-SNAPSHOT");
        URL urlJar = this.getClass().getResource("/dependency-test1-snapshot-1.0-SNAPSHOT.jar");
        context.deploy(gav,
                       urlJar.getPath());
        URL url = this.getClass().getResource("/GuvnorM2RepoDependencyExample2Snapshot");
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath(url.toURI());

        final Module module = moduleService.resolveModule(Paths.convert(path));

        final Builder builder = new Builder(module,
                                            ioService,
                                            moduleService,
                                            importsService,
                                            new ArrayList<>(),
                                            dependenciesClassLoaderCache,
                                            pomModelCache,
                                            getPackageNameWhiteListService(),
                                            alwaysTrue);

        final BuildResults results = builder.build();

        //Debug output
        if (!results.getMessages().isEmpty()) {
            for (BuildMessage m : results.getMessages()) {
                logger.debug(m.getText());
            }
        }

        assertTrue(results.getMessages().isEmpty());
    }

    @Test
    public void testBuilderKModuleHasDependencyMetaData() throws Exception {
        URL url = this.getClass().getResource("/GuvnorM2RepoDependencyExample2");
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath(url.toURI());

        final Module module = moduleService.resolveModule(Paths.convert(path));

        final Builder builder = new Builder(module,
                                            ioService,
                                            moduleService,
                                            importsService,
                                            new ArrayList<>(),
                                            dependenciesClassLoaderCache,
                                            pomModelCache,
                                            getPackageNameWhiteListService(),
                                            alwaysTrue);

        final BuildResults results = builder.build();

        //Debug output
        if (!results.getMessages().isEmpty()) {
            for (BuildMessage m : results.getMessages()) {
                logger.debug(m.getText());
            }
        }

        assertTrue(results.getMessages().isEmpty());

        final KieModuleMetaData metaData = KieModuleMetaData.Factory.newKieModuleMetaData(builder.getKieModule());

        //Check packages
        final Set<String> packageNames = new HashSet<>();
        final Iterator<String> packageNameIterator = metaData.getPackages().iterator();
        while (packageNameIterator.hasNext()) {
            packageNames.add(packageNameIterator.next());
        }
        assertEquals(2,
                     packageNames.size());
        assertTrue(packageNames.contains("defaultpkg"));
        assertTrue(packageNames.contains("org.kie.workbench.common.services.builder.tests.test1"));

        //Check classes
        final String packageName = "org.kie.workbench.common.services.builder.tests.test1";
        assertEquals(1,
                     metaData.getClasses(packageName).size());
        final String className = metaData.getClasses(packageName).iterator().next();
        assertEquals("Bean",
                     className);

        //Check metadata
        final Class clazz = metaData.getClass(packageName,
                                              className);
        final TypeMetaInfo typeMetaInfo = metaData.getTypeMetaInfo(clazz);
        assertNotNull(typeMetaInfo);
        assertFalse(typeMetaInfo.isEvent());
    }

    @Test
    public void testKModuleContainsXLS() throws Exception {
        URL url = this.getClass().getResource("/ExampleWithExcel");
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath(url.toURI());

        final Module module = moduleService.resolveModule(Paths.convert(path));

        final Builder builder = new Builder(module,
                                            ioService,
                                            moduleService,
                                            importsService,
                                            new ArrayList<>(),
                                            dependenciesClassLoaderCache,
                                            pomModelCache,
                                            getPackageNameWhiteListService(),
                                            alwaysTrue);

        final BuildResults results = builder.build();

        //Debug output
        if (!results.getMessages().isEmpty()) {
            for (BuildMessage m : results.getMessages()) {
                logger.debug(m.getText());
            }
        }

        assertTrue(results.getMessages().isEmpty());
    }

    @Test
    public void testBuilderFixForBrokenKModule() throws Exception {

        LRUPomModelCache pomModelCache = getReference(LRUPomModelCache.class);

        SimpleFileSystemProvider provider = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = provider.getPath(this.getClass().getResource("/BuilderExampleBrokenSyntax").toURI());

        final Module module = moduleService.resolveModule(Paths.convert(path));

        final Builder builder = new Builder(module,
                                            ioService,
                                            moduleService,
                                            importsService,
                                            new ArrayList<>(),
                                            dependenciesClassLoaderCache,
                                            pomModelCache,
                                            mock(PackageNameWhiteListService.class),
                                            alwaysTrue);

        assertNull(builder.getKieContainer());

        builder.deleteResource(provider.getPath(this.getClass().getResource(File.separatorChar + "BuilderExampleBrokenSyntax" +
                                                                                    File.separatorChar + "src" +
                                                                                    File.separatorChar + "main" +
                                                                                    File.separatorChar + "resources" +
                                                                                    File.separatorChar + "rule1.drl"
        ).toURI()));

        assertNotNull(builder.getKieContainer());
    }

    @Test
    public void testBuilderKieContainerInstantiation() throws Exception {

        final URL url = this.getClass().getResource("/GuvnorM2RepoDependencyExample1");
        final SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        final org.uberfire.java.nio.file.Path path = p.getPath(url.toURI());

        final Module module = moduleService.resolveModule(Paths.convert(path));

        //Build Module, including Rules and Global definition
        final Builder builder = new Builder(module,
                                            ioService,
                                            moduleService,
                                            importsService,
                                            new ArrayList<>(),
                                            dependenciesClassLoaderCache,
                                            pomModelCache,
                                            getPackageNameWhiteListService(),
                                            alwaysTrue);

        assertNotNull(builder.getKieContainer());

        //Validate Rule excluding Global definition
        final URL urlToValidate = this.getClass().getResource("/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl");
        final org.uberfire.java.nio.file.Path pathToValidate = p.getPath(urlToValidate.toURI());
        final List<ValidationMessage> validationMessages = validator.validate(Paths.convert(pathToValidate),
                                                                              Resources.toString(urlToValidate,
                                                                                                 Charset.forName("UTF-8")));
        assertNotNull(validationMessages);
        assertEquals(0,
                     validationMessages.size());

        // Retrieve a KieSession for the Module and set the Global. This should not fail as the
        // KieContainer is retrieved direct from the KieBuilder and not KieRepository (as was the
        // case before BZ1202551 was fixed.
        final KieContainer kieContainer1 = builder.getKieContainer();
        final KieSession kieSession1 = kieContainer1.newKieSession();
        kieSession1.setGlobal("list",
                              new ArrayList<String>());
    }

    @Test
    public void buildDetectsFilesWithSpecialCharacters() throws Exception {
        LRUPomModelCache pomModelCache = getReference(LRUPomModelCache.class);

        URL url = this.getClass().getResource("/ModuleBuildTestFileWithSpecialCharacter");
        SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        org.uberfire.java.nio.file.Path path = p.getPath(url.toURI());

        final Module module = moduleService.resolveModule(Paths.convert(path));

        final Builder builder = new Builder(module,
                                            ioService,
                                            moduleService,
                                            importsService,
                                            new ArrayList<>(),
                                            dependenciesClassLoaderCache,
                                            pomModelCache,
                                            getPackageNameWhiteListService(),
                                            alwaysTrue);

        BuildResults buildResults = builder.build();
        List<BuildMessage> errorMessages = buildResults.getErrorMessages();
        assertEquals(2,
                     errorMessages.size());
        assertTrue(errorMessages.get(0).getText().contains("mismatched input 'Build' expecting one of the following tokens:"));
    }

    @Test
    public void testBuildProjectWithDmn() throws Exception {
        final LRUPomModelCache pomModelCache = getReference(LRUPomModelCache.class);

        final URL url = this.getClass().getResource("/ProjectWithDmn");
        final SimpleFileSystemProvider p = new SimpleFileSystemProvider();
        final org.uberfire.java.nio.file.Path path = p.getPath(url.toURI());

        final Module module = moduleService.resolveModule(Paths.convert(path));

        final Builder builder = new Builder(module,
                                            ioService,
                                            moduleService,
                                            importsService,
                                            new ArrayList<>(),
                                            dependenciesClassLoaderCache,
                                            pomModelCache,
                                            getPackageNameWhiteListService(),
                                            alwaysTrue);

        final BuildResults buildResults = builder.build();
        final List<BuildMessage> errorMessages = buildResults.getErrorMessages();
        assertEquals(1,
                     errorMessages.size());
        assertTrue(errorMessages.get(0).getText().contains("Error compiling FEEL expression"));
    }

    private PackageNameWhiteListService getPackageNameWhiteListService() {
        return new PackageNameWhiteListServiceImpl(ioService,
                                                   mock(KieModuleService.class),
                                                   new PackageNameWhiteListLoader(packageNameSearchProvider,
                                                                                  ioService),
                                                   mock(PackageNameWhiteListSaver.class));
    }
}
