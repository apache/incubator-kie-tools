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

package org.kie.workbench.common.screens.projecteditor.backend.server;

import java.util.HashSet;

import org.assertj.core.api.Assertions;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.test.TestFileSystem;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.backend.builder.core.LRUPomModelCache;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenModelSaverTest {

    private static final String GROUP = "group";
    private static final String ARTIFACT = "artifact";
    private static final String VERSION = "1.0";
    private static final String SNAPSHOT = "-SNAPSHOT";

    @Mock
    private POMService pomService;

    @Mock
    private KModuleService kModuleService;

    @Mock
    private ProjectImportsService importsService;

    @Mock
    private ModuleRepositoriesService repositoriesService;

    @Mock
    private PackageNameWhiteListService whiteListService;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private ModuleRepositoryResolver repositoryResolver;

    @Mock
    private User identity;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private LRUPomModelCache pomModelCache;

    @Mock
    private IOService ioService;

    private Path pathToPom;

    private ProjectScreenModelSaver saver;

    private TestFileSystem testFileSystem;

    @BeforeClass
    public static void setupSystemProperties() {
        //These are not needed for the tests
        System.setProperty("org.uberfire.nio.git.daemon.enabled",
                           "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled",
                           "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled",
                           "true");
    }

    @Before
    public void setUp() throws Exception {
        testFileSystem = new TestFileSystem();

        saver = new ProjectScreenModelSaver(pomService,
                                            kModuleService,
                                            importsService,
                                            repositoriesService,
                                            whiteListService,
                                            ioService,
                                            moduleService,
                                            repositoryResolver,
                                            commentedOptionFactory,
                                            pomModelCache);

        pathToPom = testFileSystem.createTempFile("testproject/pom.xml");
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void testBatchSave() throws Exception {
        final CommentedOption commentedOption = new CommentedOption("hello");
        when(commentedOptionFactory.makeCommentedOption("message")).thenReturn(commentedOption);

        saver.save(pathToPom,
                   new ProjectScreenModel(),
                   DeploymentMode.FORCED,
                   "message");

        verify(ioService).startBatch(any(FileSystem.class),
                                     eq(commentedOption));

        verify(ioService).endBatch();
    }

    @Test
    public void testPOMSave() throws Exception {
        final ProjectScreenModel model = new ProjectScreenModel();
        final POM pom = new POM();
        model.setPOM(pom);
        final Metadata pomMetaData = new Metadata();
        model.setPOMMetaData(pomMetaData);

        saver.save(pathToPom,
                   model,
                   DeploymentMode.FORCED,
                   "message");

        verify(pomService).save(eq(pathToPom),
                                eq(pom),
                                eq(pomMetaData),
                                eq("message"));
    }

    @Test
    public void checkPOMSaveInvalidatesPomModelCache() {
        // See https://issues.jboss.org/browse/RHBRMS-2822
        // Saving the pom.xml (eventually) triggers an InvalidateDMOProjectCacheEvent once VFS's WatchService
        // has observed the file change after the batch has been committed. The InvalidateDMOProjectCacheEvent then
        // invalidates the PomModelCache. The PomModelCache is used to find the Project's GAV when the Project is
        // "Built (& Deployed)" and if it's content is stale can lead to the generated KJAR containing the
        // wrong GAV. Therefore invalidate the PomModelCache as soon as the save starts.
        final ProjectScreenModel model = new ProjectScreenModel();
        final Metadata pomMetaData = new Metadata();
        final POM pom = new POM();
        model.setPOM(pom);
        model.setPOMMetaData(pomMetaData);

        KieModule module = mock(KieModule.class);

        when(moduleService.resolveModule(pathToPom)).thenReturn(module);

        saver.save(pathToPom,
                   model,
                   DeploymentMode.FORCED,
                   "message");

        verify(pomModelCache).invalidateCache(module);
    }

    @Test
    public void testKModuleSave() throws Exception {
        final ProjectScreenModel model = new ProjectScreenModel();
        final KModuleModel kModule = new KModuleModel();
        model.setKModule(kModule);
        final Path pathToKModule = mock(Path.class);
        model.setPathToKModule(pathToKModule);
        final Metadata metadata = new Metadata();
        model.setKModuleMetaData(metadata);

        saver.save(pathToPom,
                   model,
                   DeploymentMode.FORCED,
                   "message kmodule");

        verify(kModuleService).save(eq(pathToKModule),
                                    eq(kModule),
                                    eq(metadata),
                                    eq("message kmodule"));
    }

    @Test
    public void testImportsSave() throws Exception {
        final ProjectScreenModel model = new ProjectScreenModel();
        final ProjectImports projectImports = new ProjectImports();
        model.setProjectImports(projectImports);
        final Path pathToImports = mock(Path.class);
        model.setPathToImports(pathToImports);
        final Metadata metadata = new Metadata();
        model.setProjectImportsMetaData(metadata);

        saver.save(pathToPom,
                   model,
                   DeploymentMode.FORCED,
                   "message imports");

        verify(importsService).save(eq(pathToImports),
                                    eq(projectImports),
                                    eq(metadata),
                                    eq("message imports"));
    }

    @Test
    public void testRepositoriesSave() throws Exception {
        final ProjectScreenModel model = new ProjectScreenModel();
        final ModuleRepositories moduleRepositories = new ModuleRepositories();
        model.setRepositories(moduleRepositories);
        final Path pathToRepositories = mock(Path.class);
        model.setPathToRepositories(pathToRepositories);

        saver.save(pathToPom,
                   model,
                   DeploymentMode.FORCED,
                   "message repositories");

        verify(repositoriesService).save(eq(pathToRepositories),
                                         eq(moduleRepositories),
                                         eq("message repositories"));
    }

    @Test
    public void testWhiteListSave() throws Exception {
        final ProjectScreenModel model = new ProjectScreenModel();
        final WhiteList whiteList = new WhiteList();
        model.setWhiteList(whiteList);
        final Path pathToWhiteList = mock(Path.class);
        model.setPathToWhiteList(pathToWhiteList);
        final Metadata metadata = new Metadata();
        model.setWhiteListMetaData(metadata);

        saver.save(pathToPom,
                   model,
                   DeploymentMode.FORCED,
                   "message white list");

        verify(whiteListService).save(eq(pathToWhiteList),
                                      eq(whiteList),
                                      eq(metadata),
                                      eq("message white list"));
    }

    @Test
    public void testWithSaveClashingGAV() {
        final ProjectScreenModel model = new ProjectScreenModel();

        final POM pom = new POM(new GAV(GROUP, ARTIFACT, VERSION));

        model.setPOM(pom);
        model.setRepositories(new ModuleRepositories());

        final Metadata metadata = new Metadata();
        model.setWhiteListMetaData(metadata);

        KieModule module = mock(KieModule.class);

        when(module.getPom()).thenReturn(new POM(new GAV(GROUP, ARTIFACT, VERSION + SNAPSHOT)));

        when(moduleService.resolveModule(pathToPom)).thenReturn(module);

        HashSet<MavenRepositoryMetadata> mavenRepositoryMetadata = new HashSet<>();
        mavenRepositoryMetadata.add(mock(MavenRepositoryMetadata.class));

        when(repositoryResolver.getRepositoriesResolvingArtifact(any(GAV.class), any(Module.class))).thenReturn(mavenRepositoryMetadata);

        Assertions.assertThatThrownBy(() -> saver.save(pathToPom, model, DeploymentMode.VALIDATED, ""))
                .isInstanceOf(GAVAlreadyExistsException.class);
    }

    @Test
    public void testSaveWithSnapshotClashingGAV() {
        final ProjectScreenModel model = new ProjectScreenModel();

        final POM pom = new POM(new GAV(GROUP, ARTIFACT, VERSION + SNAPSHOT));

        model.setPOM(pom);
        model.setRepositories(new ModuleRepositories());

        final Metadata metadata = new Metadata();
        model.setWhiteListMetaData(metadata);

        KieModule module = mock(KieModule.class);

        when(module.getPom()).thenReturn(new POM(new GAV(GROUP, ARTIFACT, VERSION + SNAPSHOT)));

        when(moduleService.resolveModule(pathToPom)).thenReturn(module);

        HashSet<MavenRepositoryMetadata> mavenRepositoryMetadata = new HashSet<>();
        mavenRepositoryMetadata.add(mock(MavenRepositoryMetadata.class));

        when(repositoryResolver.getRepositoriesResolvingArtifact(any(GAV.class), any(Module.class))).thenReturn(mavenRepositoryMetadata);

        saver.save(pathToPom, model, DeploymentMode.VALIDATED, "");

        verify(pomModelCache).invalidateCache(module);
        verify(ioService).startBatch(any(), any());
        verify(pomService).save(any(), any(), any(), any());
        verify(kModuleService).save(any(), any(), any(), any());
        verify(importsService).save(any(), any(), any(), any());
        verify(repositoriesService).save(any(), any(), any());
        verify(whiteListService).save(any(), any(), any(), any());
        verify(ioService).endBatch();
    }
}