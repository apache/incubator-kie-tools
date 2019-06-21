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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.WorkspaceProjectServiceImpl;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryCopier;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.backend.builder.core.LRUPomModelCache;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.spaces.Space;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenServiceImplTest {

    @Mock
    private KModuleService kModuleService;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private ProjectImportsService importsService;

    @Mock
    private ModuleRepositoriesService repositoriesService;

    @Mock
    private PackageNameWhiteListService whiteListService;

    @Mock
    private ModuleRepositoryResolver repositoryResolver;

    @Mock
    private IOService ioService;

    @Mock
    private Path pathToPom;

    @Mock
    private Path pathToKieModule;

    @Mock
    private Path pathToModuleImports;

    @Mock
    private Path pathToModuleRepositories;

    @Mock
    private KieModule module;

    @Mock
    private KModuleModel kmodule;

    @Mock
    private Metadata pomMetaData;

    @Mock
    private Metadata kmoduleMetaData;

    @Mock
    private Metadata projectImportsMetaData;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private LRUPomModelCache pomModelCache;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private RepositoryCopier repositoryCopier;

    @Mock
    private POMService pomService;

    @Mock
    private MetadataService metadataService;

    @Mock
    private WorkspaceProjectServiceImpl projectService;

    private ProjectScreenService service;
    private ProjectScreenModelLoader loader;
    private ProjectScreenModelSaver saver;
    private ProjectImports projectImports;
    private ModuleRepositories moduleRepositories;

    private GAV gav = new GAV("org.test",
                              "project-screen-test",
                              "1.0.0");
    private POM pom = new POM("test",
                              "test",
                              "url",
                              gav);

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
    public void setup() {
        projectImports = new ProjectImports();
        moduleRepositories = new ModuleRepositories();
        loader = new ProjectScreenModelLoader(moduleService,
                                              pomService,
                                              metadataService,
                                              kModuleService,
                                              importsService,
                                              repositoriesService,
                                              whiteListService,
                                              projectService) {
            @Override
            protected boolean fileExists(final Path path) {
                return true;
            }
        };
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
        service = new ProjectScreenServiceImpl(projectService,
                                               repositoryService,
                                               moduleService,
                                               loader,
                                               saver,
                                               repositoryCopier,
                                               pomService,
                                               metadataService);

        when(module.getKModuleXMLPath()).thenReturn(pathToKieModule);
        when(module.getImportsPath()).thenReturn(pathToModuleImports);
        when(module.getRepositoriesPath()).thenReturn(pathToModuleRepositories);
        when(module.getPom()).thenReturn(pom);

        when(pathToPom.toURI()).thenReturn("default://pom.xml");

        when(pomService.load(eq(pathToPom))).thenReturn(pom);
        when(kModuleService.load(eq(pathToKieModule))).thenReturn(kmodule);
        when(importsService.load(eq(pathToModuleImports))).thenReturn(projectImports);
        when(repositoriesService.load(eq(pathToModuleRepositories))).thenReturn(moduleRepositories);

        when(moduleService.resolveModule(eq(pathToPom))).thenReturn(module);

        when(metadataService.getMetadata(eq(pathToPom))).thenReturn(pomMetaData);
        when(metadataService.getMetadata(eq(pathToKieModule))).thenReturn(kmoduleMetaData);
        when(metadataService.getMetadata(eq(pathToModuleImports))).thenReturn(projectImportsMetaData);

        when(projectService.resolveProject((Path) any()))
                .thenReturn(spy(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                     new GitRepository("alias",
                                                                       mock(Space.class),
                                                                       emptyList()),
                                                     mock(Branch.class),
                                                     mock(Module.class))));
    }

    @Test
    public void testLoad() throws Exception {
        final ProjectScreenModel model = service.load(pathToPom);

        assertEquals(pom,
                     model.getPOM());
        assertEquals(pomMetaData,
                     model.getPOMMetaData());
        assertEquals(pathToPom,
                     model.getPathToPOM());

        assertEquals(kmodule,
                     model.getKModule());
        assertEquals(kmoduleMetaData,
                     model.getKModuleMetaData());
        assertEquals(pathToKieModule,
                     model.getPathToKModule());

        assertEquals(projectImports,
                     model.getProjectImports());
        assertEquals(projectImportsMetaData,
                     model.getProjectImportsMetaData());
        assertEquals(pathToModuleImports,
                     model.getPathToImports());

        assertEquals(moduleRepositories,
                     model.getRepositories());
        assertEquals(pathToModuleRepositories,
                     model.getPathToRepositories());

        assertEquals(emptyList(),
                     model.getGitUrls());

        verify(pomService,
               times(1)).load(eq(pathToPom));
        verify(metadataService,
               times(1)).getMetadata(eq(pathToPom));
        verify(moduleService,
               times(1)).resolveModule(eq(pathToPom));

        verify(kModuleService,
               times(1)).load(eq(pathToKieModule));
        verify(metadataService,
               times(1)).getMetadata(eq(pathToKieModule));

        verify(importsService,
               times(1)).load(eq(pathToModuleImports));
        verify(metadataService,
               times(1)).getMetadata(eq(pathToModuleImports));

        verify(repositoriesService,
               times(1)).load(eq(pathToModuleRepositories));
    }

    @Test
    public void testSaveNonClashingGAVChangeToGAV() {
        when(pathToPom.toURI()).thenReturn("default://p0/pom.xml");

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM(new POM(new GAV("groupId",
                                     "artifactId",
                                     "2.0.0")));
        model.setPOMMetaData(pomMetaData);
        model.setPathToPOM(pathToPom);

        model.setKModule(kmodule);
        model.setKModuleMetaData(kmoduleMetaData);
        model.setPathToKModule(pathToKieModule);

        model.setProjectImports(projectImports);
        model.setProjectImportsMetaData(projectImportsMetaData);
        model.setPathToImports(pathToModuleImports);

        model.setRepositories(moduleRepositories);
        model.setPathToRepositories(pathToModuleRepositories);

        final String comment = "comment";

        service.save(pathToPom,
                     model,
                     comment);

        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(model.getPOM().getGav()),
                                                          eq(module));

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class),
                                    any(CommentedOption.class));
        verify(pomService,
               times(1)).save(eq(pathToPom),
                              eq(model.getPOM()),
                              eq(pomMetaData),
                              eq(comment));
        verify(kModuleService,
               times(1)).save(eq(pathToKieModule),
                              eq(kmodule),
                              eq(kmoduleMetaData),
                              eq(comment));
        verify(importsService,
               times(1)).save(eq(pathToModuleImports),
                              eq(projectImports),
                              eq(projectImportsMetaData),
                              eq(comment));
        verify(repositoriesService,
               times(1)).save(eq(pathToModuleRepositories),
                              eq(moduleRepositories),
                              eq(comment));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testSaveNonClashingGAVNoChangeToGAV() {
        when(pathToPom.toURI()).thenReturn("default://p0/pom.xml");

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM(pom);
        model.setPOMMetaData(pomMetaData);
        model.setPathToPOM(pathToPom);

        model.setKModule(kmodule);
        model.setKModuleMetaData(kmoduleMetaData);
        model.setPathToKModule(pathToKieModule);

        model.setProjectImports(projectImports);
        model.setProjectImportsMetaData(projectImportsMetaData);
        model.setPathToImports(pathToModuleImports);

        model.setRepositories(moduleRepositories);
        model.setPathToRepositories(pathToModuleRepositories);

        final String comment = "comment";

        service.save(pathToPom,
                     model,
                     comment);

        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(model.getPOM().getGav()),
                                                         eq(module));

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class),
                                    any(CommentedOption.class));
        verify(pomService,
               times(1)).save(eq(pathToPom),
                              eq(model.getPOM()),
                              eq(pomMetaData),
                              eq(comment));
        verify(kModuleService,
               times(1)).save(eq(pathToKieModule),
                              eq(kmodule),
                              eq(kmoduleMetaData),
                              eq(comment));
        verify(importsService,
               times(1)).save(eq(pathToModuleImports),
                              eq(projectImports),
                              eq(projectImportsMetaData),
                              eq(comment));
        verify(repositoriesService,
               times(1)).save(eq(pathToModuleRepositories),
                              eq(moduleRepositories),
                              eq(comment));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test()
    public void testSaveClashingGAVChangeToGAV() {
        when(pathToPom.toURI()).thenReturn("default://p0/pom.xml");

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM(new POM(new GAV("groupId",
                                     "artifactId",
                                     "2.0.0")));
        model.setPOMMetaData(pomMetaData);
        model.setPathToPOM(pathToPom);

        model.setKModule(kmodule);
        model.setKModuleMetaData(kmoduleMetaData);
        model.setPathToKModule(pathToKieModule);

        model.setProjectImports(projectImports);
        model.setProjectImportsMetaData(projectImportsMetaData);
        model.setPathToImports(pathToModuleImports);

        model.setRepositories(moduleRepositories);
        model.setPathToRepositories(pathToModuleRepositories);

        final MavenRepositoryMetadata repositoryMetadata = new MavenRepositoryMetadata("id",
                                                                                       "url",
                                                                                       MavenRepositorySource.LOCAL);

        moduleRepositories.getRepositories().add(new ModuleRepositories.ModuleRepository(true,
                                                                                         repositoryMetadata));

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav),
                                                                 eq(module),
                                                                 eq(repositoryMetadata))).thenReturn(new HashSet<MavenRepositoryMetadata>() {{
            add(repositoryMetadata);
        }});

        final String comment = "comment";

        try {
            service.save(pathToPom,
                         model,
                         comment);
        } catch (GAVAlreadyExistsException e) {
            // This is expected! We catch here rather than let JUnit handle it with
            // @Test(expected = GAVAlreadyExistsException.class) so we can verify
            // that only the expected methods have been invoked.

        } catch (Exception e) {
            fail(e.getMessage());
        }

        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(model.getPOM().getGav()),
                                                          eq(module),
                                                          any(MavenRepositoryMetadata.class));

        verify(pomService,
               times(1)).save(eq(pathToPom),
                              eq(model.getPOM()),
                              eq(pomMetaData),
                              eq(comment));
        verify(kModuleService,
               times(1)).save(eq(pathToKieModule),
                              eq(kmodule),
                              eq(kmoduleMetaData),
                              eq(comment));
        verify(importsService,
               times(1)).save(eq(pathToModuleImports),
                              eq(projectImports),
                              eq(projectImportsMetaData),
                              eq(comment));
        verify(repositoriesService,
               times(1)).save(eq(pathToModuleRepositories),
                              eq(moduleRepositories),
                              eq(comment));
    }

    @Test()
    public void testSaveClashingGAVNoChangeToGAV() {
        when(pathToPom.toURI()).thenReturn("default://p0/pom.xml");

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM(pom);
        model.setPOMMetaData(pomMetaData);
        model.setPathToPOM(pathToPom);

        model.setKModule(kmodule);
        model.setKModuleMetaData(kmoduleMetaData);
        model.setPathToKModule(pathToKieModule);

        model.setProjectImports(projectImports);
        model.setProjectImportsMetaData(projectImportsMetaData);
        model.setPathToImports(pathToModuleImports);

        model.setRepositories(moduleRepositories);
        model.setPathToRepositories(pathToModuleRepositories);

        final MavenRepositoryMetadata repositoryMetadata = new MavenRepositoryMetadata("id",
                                                                                       "url",
                                                                                       MavenRepositorySource.LOCAL);

        moduleRepositories.getRepositories().add(new ModuleRepositories.ModuleRepository(true,
                                                                                         repositoryMetadata));

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav),
                                                                 eq(module),
                                                                 eq(repositoryMetadata))).thenReturn(new HashSet<MavenRepositoryMetadata>() {{
            add(repositoryMetadata);
        }});

        final String comment = "comment";

        try {
            service.save(pathToPom,
                         model,
                         comment);
        } catch (GAVAlreadyExistsException e) {
            fail(e.getMessage());
        }

        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(model.getPOM().getGav()),
                                                         eq(module));

        verify(pomService,
               times(1)).save(eq(pathToPom),
                              eq(model.getPOM()),
                              eq(pomMetaData),
                              eq(comment));
        verify(kModuleService,
               times(1)).save(eq(pathToKieModule),
                              eq(kmodule),
                              eq(kmoduleMetaData),
                              eq(comment));
        verify(importsService,
               times(1)).save(eq(pathToModuleImports),
                              eq(projectImports),
                              eq(projectImportsMetaData),
                              eq(comment));
        verify(repositoriesService,
               times(1)).save(eq(pathToModuleRepositories),
                              eq(moduleRepositories),
                              eq(comment));
    }

    @Test()
    public void testSaveClashingGAVFilteredRepositoryChangeToGAV() {
        when(pathToPom.toURI()).thenReturn("default://p0/pom.xml");

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM(new POM(new GAV("groupId",
                                     "artifactId",
                                     "2.0.0")));
        model.setPOMMetaData(pomMetaData);
        model.setPathToPOM(pathToPom);

        model.setKModule(kmodule);
        model.setKModuleMetaData(kmoduleMetaData);
        model.setPathToKModule(pathToKieModule);

        model.setProjectImports(projectImports);
        model.setProjectImportsMetaData(projectImportsMetaData);
        model.setPathToImports(pathToModuleImports);

        model.setRepositories(moduleRepositories);
        model.setPathToRepositories(pathToModuleRepositories);

        final MavenRepositoryMetadata repositoryMetadata = new MavenRepositoryMetadata("id",
                                                                                       "url",
                                                                                       MavenRepositorySource.LOCAL);

        moduleRepositories.getRepositories().add(new ModuleRepositories.ModuleRepository(false,
                                                                                         repositoryMetadata));

        final ArgumentCaptor<MavenRepositoryMetadata> filterCaptor = ArgumentCaptor.forClass(MavenRepositoryMetadata.class);
        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav),
                                                                 eq(module),
                                                                 filterCaptor.capture())).thenReturn(new HashSet<MavenRepositoryMetadata>());

        final String comment = "comment";

        try {
            service.save(pathToPom,
                         model,
                         comment);
        } catch (GAVAlreadyExistsException e) {
            //This should not be thrown if we're filtering out the Repository from the check
            fail(e.getMessage());
        }

        final List<MavenRepositoryMetadata> filter = filterCaptor.getAllValues();
        assertEquals(0,
                     filter.size());

        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(model.getPOM().getGav()),
                                                          eq(module));

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class),
                                    any(CommentedOption.class));
        verify(pomService,
               times(1)).save(eq(pathToPom),
                              eq(model.getPOM()),
                              eq(pomMetaData),
                              eq(comment));
        verify(kModuleService,
               times(1)).save(eq(pathToKieModule),
                              eq(kmodule),
                              eq(kmoduleMetaData),
                              eq(comment));
        verify(importsService,
               times(1)).save(eq(pathToModuleImports),
                              eq(projectImports),
                              eq(projectImportsMetaData),
                              eq(comment));
        verify(repositoriesService,
               times(1)).save(eq(pathToModuleRepositories),
                              eq(moduleRepositories),
                              eq(comment));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test()
    public void testSaveClashingGAVFilteredRepositoryNoChangeToGAV() {
        when(pathToPom.toURI()).thenReturn("default://p0/pom.xml");

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM(pom);
        model.setPOMMetaData(pomMetaData);
        model.setPathToPOM(pathToPom);

        model.setKModule(kmodule);
        model.setKModuleMetaData(kmoduleMetaData);
        model.setPathToKModule(pathToKieModule);

        model.setProjectImports(projectImports);
        model.setProjectImportsMetaData(projectImportsMetaData);
        model.setPathToImports(pathToModuleImports);

        model.setRepositories(moduleRepositories);
        model.setPathToRepositories(pathToModuleRepositories);

        final MavenRepositoryMetadata repositoryMetadata = new MavenRepositoryMetadata("id",
                                                                                       "url",
                                                                                       MavenRepositorySource.LOCAL);

        moduleRepositories.getRepositories().add(new ModuleRepositories.ModuleRepository(false,
                                                                                         repositoryMetadata));

        final ArgumentCaptor<MavenRepositoryMetadata> filterCaptor = ArgumentCaptor.forClass(MavenRepositoryMetadata.class);
        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav),
                                                                 eq(module),
                                                                 filterCaptor.capture())).thenReturn(new HashSet<MavenRepositoryMetadata>());

        final String comment = "comment";

        try {
            service.save(pathToPom,
                         model,
                         comment);
        } catch (GAVAlreadyExistsException e) {
            //This should not be thrown if we're filtering out the Repository from the check
            fail(e.getMessage());
        }

        final List<MavenRepositoryMetadata> filter = filterCaptor.getAllValues();
        assertEquals(0,
                     filter.size());

        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(model.getPOM().getGav()),
                                                         eq(module));

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class),
                                    any(CommentedOption.class));
        verify(pomService,
               times(1)).save(eq(pathToPom),
                              eq(model.getPOM()),
                              eq(pomMetaData),
                              eq(comment));
        verify(kModuleService,
               times(1)).save(eq(pathToKieModule),
                              eq(kmodule),
                              eq(kmoduleMetaData),
                              eq(comment));
        verify(importsService,
               times(1)).save(eq(pathToModuleImports),
                              eq(projectImports),
                              eq(projectImportsMetaData),
                              eq(comment));
        verify(repositoriesService,
               times(1)).save(eq(pathToModuleRepositories),
                              eq(moduleRepositories),
                              eq(comment));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test()
    public void testSaveClashingGAVForced() {
        when(pathToPom.toURI()).thenReturn("default://p0/pom.xml");

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM(pom);
        model.setPOMMetaData(pomMetaData);
        model.setPathToPOM(pathToPom);

        model.setKModule(kmodule);
        model.setKModuleMetaData(kmoduleMetaData);
        model.setPathToKModule(pathToKieModule);

        model.setProjectImports(projectImports);
        model.setProjectImportsMetaData(projectImportsMetaData);
        model.setPathToImports(pathToModuleImports);

        model.setRepositories(moduleRepositories);
        model.setPathToRepositories(pathToModuleRepositories);

        final MavenRepositoryMetadata repositoryMetadata = new MavenRepositoryMetadata("id",
                                                                                       "url",
                                                                                       MavenRepositorySource.LOCAL);

        moduleRepositories.getRepositories().add(new ModuleRepositories.ModuleRepository(true,
                                                                                         repositoryMetadata));

        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav),
                                                                 eq(module),
                                                                 eq(repositoryMetadata))).thenReturn(new HashSet<MavenRepositoryMetadata>() {{
            add(repositoryMetadata);
        }});

        final String comment = "comment";

        final WorkspaceProject projectToBeReturned = new WorkspaceProject();
        doReturn(projectToBeReturned).when(projectService).resolveProject(pathToPom);

        try {
            final WorkspaceProject project = service.save(pathToPom,
                                                          model,
                                                          comment,
                                                          DeploymentMode.FORCED);
            assertEquals(projectToBeReturned,
                         project);
        } catch (GAVAlreadyExistsException e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }

        verify(pomService,
               times(1)).save(eq(pathToPom),
                              eq(model.getPOM()),
                              eq(pomMetaData),
                              eq(comment));
        verify(kModuleService,
               times(1)).save(eq(pathToKieModule),
                              eq(kmodule),
                              eq(kmoduleMetaData),
                              eq(comment));
        verify(importsService,
               times(1)).save(eq(pathToModuleImports),
                              eq(projectImports),
                              eq(projectImportsMetaData),
                              eq(comment));
        verify(repositoriesService,
               times(1)).save(eq(pathToModuleRepositories),
                              eq(moduleRepositories),
                              eq(comment));
    }

    @Test
    public void testCopy() throws Exception {

        when(projectService.createFreshProjectName(any(),
                                                   any())).thenCallRealMethod();
        doReturn(Arrays.asList(mock(WorkspaceProject.class))).when(projectService).getAllWorkspaceProjectsByName(any(),
                                                                                                                 eq("newName"),
                                                                                                                 anyBoolean());

        final WorkspaceProject project = mock(WorkspaceProject.class);
        final OrganizationalUnit ou = mock(OrganizationalUnit.class);
        final Path projectRoot = mock(Path.class);
        doReturn(ou).when(project).getOrganizationalUnit();
        doReturn(projectRoot).when(project).getRootPath();

        final Repository newRepository = mock(Repository.class);
        final Path newRepositoryRoot = PathFactory.newPath("root",
                                                           "file:///root");
        doReturn(Optional.of(new Branch("master",
                                        newRepositoryRoot))).when(newRepository).getDefaultBranch();

        doReturn(newRepository).when(repositoryCopier).copy(ou,
                                                            "newName",
                                                            projectRoot);

        final ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);

        final POM pom = new POM();
        doReturn(pom).when(pomService).load(any(Path.class));

        final Metadata metadata = mock(Metadata.class);
        doReturn(metadata).when(metadataService).getMetadata(any(Path.class));

        service.copy(project,
                     "newName");

        verify(pomService).save(any(Path.class),
                                pomArgumentCaptor.capture(),
                                eq(metadata),
                                eq("Renaming the project."),
                                eq(true));

        final POM updatedPom = pomArgumentCaptor.getValue();
        assertEquals("newName-1",
                     updatedPom.getName());
        assertEquals("newName",
                     updatedPom.getGav().getArtifactId());
    }

    @Test
    public void testCopyNoPOM() throws Exception {
        final WorkspaceProject project = mock(WorkspaceProject.class);
        final OrganizationalUnit ou = mock(OrganizationalUnit.class);
        final Path projectRoot = mock(Path.class);
        doReturn(ou).when(project).getOrganizationalUnit();
        doReturn(projectRoot).when(project).getRootPath();

        final Repository newRepository = mock(Repository.class);
        final Path newRepositoryRoot = PathFactory.newPath("root",
                                                           "file:///root");

        doReturn(Optional.of(new Branch("master",
                                        newRepositoryRoot))).when(newRepository).getDefaultBranch();

        doReturn(newRepository).when(repositoryCopier).copy(ou,
                                                            "newName",
                                                            projectRoot);

        doReturn(null).when(pomService).load(any(Path.class));

        service.copy(project,
                     "newName");

        verify(repositoryCopier).copy(ou,
                                      "newName",
                                      projectRoot);

        verify(metadataService,
               never()).getMetadata(any(Path.class));
        verify(pomService,
               never()).save(any(Path.class),
                             any(POM.class),
                             any(Metadata.class),
                             anyString(),
                             anyBoolean());
    }

    @Test
    public void testReImport() throws Exception {
        service.reImport(pathToPom);

        verify(moduleService).reImport(pathToPom);
    }

    @Test
    public void testDelete() throws Exception {
        final WorkspaceProject project = mock(WorkspaceProject.class);
        final Repository repository = mock(Repository.class);
        final String ouName = "test-realm";
        final Space space = new Space(ouName);

        doReturn(repository).when(project).getRepository();
        doReturn("myrepo").when(repository).getAlias();
        doReturn(space).when(repository).getSpace();

        service.delete(project);

        verify(repositoryService).removeRepository(eq(space),
                                                   eq("myrepo"));
    }
}