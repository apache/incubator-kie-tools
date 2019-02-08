/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.backend.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.utils.POMContentHandler;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorContent;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
import org.kie.workbench.common.screens.projecteditor.model.InvalidPomException;
import org.kie.workbench.common.screens.projecteditor.service.PomEditorService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PomEditorServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private DefaultEditorService defaultEditorService;

    @Mock
    private MetadataServerSideService metadataService;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private ModuleRepositoryResolver repositoryResolver;

    @Mock
    private ModuleRepositoriesService moduleRepositoriesService;

    @Mock
    private RenameService renameService;

    @Mock
    private SaveAndRenameServiceImpl<String, Metadata> saveAndRenameService;

    @Mock
    private Path pomPath;

    @Mock
    private Metadata metaData;

    @Mock
    private KieModule module;

    @Mock
    private POM pom;

    @Mock
    private Path moduleRepositoriesPath;

    private PomEditorService service;

    private String pomPathUri = "default://p0/pom.xml";

    private Map<String, Object> attributes = new HashMap<String, Object>();

    private DefaultEditorContent content = new DefaultEditorContent();

    private POMContentHandler pomContentHandler = spy(new POMContentHandler());

    private String pomXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "<modelVersion>4.0.0</modelVersion>\n" +
            "<groupId>groupId</groupId>\n" +
            "<artifactId>artifactId</artifactId>\n" +
            "<version>0.0.1</version>\n" +
            "<name>name</name>\n" +
            "<description>description</description>\n" +
            "</project>";

    private String comment = "comment";

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

        service = spy(new PomEditorServiceImpl(ioService,
                                               defaultEditorService,
                                               metadataService,
                                               commentedOptionFactory,
                                               moduleService,
                                               pomContentHandler,
                                               repositoryResolver,
                                               moduleRepositoriesService,
                                               renameService,
                                               saveAndRenameService));

        when(pomPath.toURI()).thenReturn(pomPathUri);
        when(defaultEditorService.loadContent(pomPath)).thenReturn(content);
        when(metadataService.setUpAttributes(eq(pomPath),
                                             any(Metadata.class))).thenReturn(attributes);
        when(moduleService.resolveModule(pomPath)).thenReturn(module);
        when(module.getRepositoriesPath()).thenReturn(moduleRepositoriesPath);
        when(module.getPom()).thenReturn(pom);
    }

    @Test
    public void testLoad() {
        final DefaultEditorContent content = service.loadContent(pomPath);
        assertNotNull(content);
        assertEquals(this.content,
                     content);
    }

    @Test
    public void testSaveNonClashingGAVChangeToGAV() {
        final Set<ModuleRepositories.ModuleRepository> moduleRepositoriesMetadata = new HashSet<ModuleRepositories.ModuleRepository>();
        final ModuleRepositories moduleRepositories = new ModuleRepositories(moduleRepositoriesMetadata);
        when(moduleRepositoriesService.load(moduleRepositoriesPath)).thenReturn(moduleRepositories);

        final ArgumentCaptor<MavenRepositoryMetadata> resolvedRepositoriesCaptor = ArgumentCaptor.forClass(MavenRepositoryMetadata.class);
        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(pomXml),
                                                                 resolvedRepositoriesCaptor.capture())).thenReturn(Collections.EMPTY_SET);
        when(pom.getGav()).thenReturn(new GAV("groupId",
                                              "artifactId",
                                              "0.0.2"));

        service.save(pomPath,
                     pomXml,
                     metaData,
                     comment,
                     DeploymentMode.VALIDATED);

        verify(moduleService,
               times(1)).resolveModule(pomPath);
        verify(moduleRepositoriesService,
               times(1)).load(moduleRepositoriesPath);
        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(pomXml));
        final List<MavenRepositoryMetadata> resolvedRepositories = resolvedRepositoriesCaptor.getAllValues();
        assertNotNull(resolvedRepositories);
        assertEquals(0,
                     resolvedRepositories.size());

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class));
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               eq(pomXml),
                               eq(attributes),
                               any(CommentedOption.class));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testSaveNonClashingGAVNoChangeToGAV() {
        when(pom.getGav()).thenReturn(new GAV("groupId",
                                              "artifactId",
                                              "0.0.1"));

        service.save(pomPath,
                     pomXml,
                     metaData,
                     comment,
                     DeploymentMode.VALIDATED);

        verify(moduleService,
               times(1)).resolveModule(pomPath);
        verify(moduleRepositoriesService,
               never()).load(moduleRepositoriesPath);
        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(pomXml));

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class));
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               eq(pomXml),
                               eq(attributes),
                               any(CommentedOption.class));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testSaveNonClashingGAVFilteredChangeToGAV() {
        final Set<ModuleRepositories.ModuleRepository> moduleRepositoriesMetadata = new HashSet<ModuleRepositories.ModuleRepository>() {{
            add(new ModuleRepositories.ModuleRepository(true,
                                                        new MavenRepositoryMetadata("local-id",
                                                                                    "local-url",
                                                                                    MavenRepositorySource.LOCAL)));
        }};
        final ModuleRepositories moduleRepositories = new ModuleRepositories(moduleRepositoriesMetadata);
        when(moduleRepositoriesService.load(moduleRepositoriesPath)).thenReturn(moduleRepositories);

        final ArgumentCaptor<MavenRepositoryMetadata> resolvedRepositoriesCaptor = ArgumentCaptor.forClass(MavenRepositoryMetadata.class);
        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(pomXml),
                                                                 resolvedRepositoriesCaptor.capture())).thenReturn(Collections.EMPTY_SET);
        when(pom.getGav()).thenReturn(new GAV("groupId",
                                              "artifactId",
                                              "0.0.2"));

        service.save(pomPath,
                     pomXml,
                     metaData,
                     comment,
                     DeploymentMode.VALIDATED);

        verify(moduleService,
               times(1)).resolveModule(pomPath);
        verify(moduleRepositoriesService,
               times(1)).load(moduleRepositoriesPath);
        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(pomXml),
                                                          any(MavenRepositoryMetadata.class));
        final List<MavenRepositoryMetadata> resolvedRepositories = resolvedRepositoriesCaptor.getAllValues();
        assertNotNull(resolvedRepositories);
        assertEquals(1,
                     resolvedRepositories.size());
        final MavenRepositoryMetadata repositoryMetadata = resolvedRepositories.get(0);
        assertEquals("local-id",
                     repositoryMetadata.getId());
        assertEquals("local-url",
                     repositoryMetadata.getUrl());
        assertEquals(MavenRepositorySource.LOCAL,
                     repositoryMetadata.getSource());

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class));
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               eq(pomXml),
                               eq(attributes),
                               any(CommentedOption.class));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testSaveNonClashingGAVFilteredNoChangeToGAV() {
        when(pom.getGav()).thenReturn(new GAV("groupId",
                                              "artifactId",
                                              "0.0.1"));

        service.save(pomPath,
                     pomXml,
                     metaData,
                     comment,
                     DeploymentMode.VALIDATED);

        verify(moduleService,
               times(1)).resolveModule(pomPath);
        verify(moduleRepositoriesService,
               never()).load(moduleRepositoriesPath);
        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(pomXml),
                                                         any(MavenRepositoryMetadata.class));

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class));
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               eq(pomXml),
                               eq(attributes),
                               any(CommentedOption.class));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testSaveClashingGAVChangeToGAV() {
        final Set<ModuleRepositories.ModuleRepository> moduleRepositoriesMetadata = new HashSet<ModuleRepositories.ModuleRepository>() {{
            add(new ModuleRepositories.ModuleRepository(true,
                                                        new MavenRepositoryMetadata("local-id",
                                                                                    "local-url",
                                                                                    MavenRepositorySource.LOCAL)));
        }};
        final ModuleRepositories moduleRepositories = new ModuleRepositories(moduleRepositoriesMetadata);
        when(moduleRepositoriesService.load(moduleRepositoriesPath)).thenReturn(moduleRepositories);

        final Set<MavenRepositoryMetadata> clashingRepositories = new HashSet<MavenRepositoryMetadata>() {{
            add(new MavenRepositoryMetadata("local-id",
                                            "local-url",
                                            MavenRepositorySource.LOCAL));
        }};
        final ArgumentCaptor<MavenRepositoryMetadata> resolvedRepositoriesCaptor = ArgumentCaptor.forClass(MavenRepositoryMetadata.class);
        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(pomXml),
                                                                 resolvedRepositoriesCaptor.capture())).thenReturn(clashingRepositories);
        when(pom.getGav()).thenReturn(new GAV("groupId",
                                              "artifactId",
                                              "0.0.2"));

        try {
            service.save(pomPath,
                         pomXml,
                         metaData,
                         comment,
                         DeploymentMode.VALIDATED);
        } catch (GAVAlreadyExistsException e) {
            // This is expected! We catch here rather than let JUnit handle it with
            // @Test(expected = GAVAlreadyExistsException.class) so we can verify
            // that only the expected methods have been invoked.

        } catch (Exception e) {
            fail(e.getMessage());
        }

        verify(moduleService,
               times(1)).resolveModule(pomPath);
        verify(moduleRepositoriesService,
               times(1)).load(moduleRepositoriesPath);
        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(pomXml),
                                                          any(MavenRepositoryMetadata.class));
        final List<MavenRepositoryMetadata> resolvedRepositories = resolvedRepositoriesCaptor.getAllValues();
        assertNotNull(resolvedRepositories);
        assertEquals(1,
                     resolvedRepositories.size());
        final MavenRepositoryMetadata repositoryMetadata = resolvedRepositories.get(0);
        assertEquals("local-id",
                     repositoryMetadata.getId());
        assertEquals("local-url",
                     repositoryMetadata.getUrl());
        assertEquals(MavenRepositorySource.LOCAL,
                     repositoryMetadata.getSource());

        verify(ioService,
               never()).startBatch(any(FileSystem.class));
        verify(ioService,
               never()).write(any(org.uberfire.java.nio.file.Path.class),
                              eq(pomXml),
                              eq(attributes),
                              any(CommentedOption.class));
        verify(ioService,
               never()).endBatch();
    }

    @Test
    public void testSaveInvalidPom() throws IOException, XmlPullParserException {

        final InvalidPomException invalidPomException = new InvalidPomException(10, 10);

        try {
            doThrow(invalidPomException).when(pomContentHandler).toModel(pomXml);

            service.save(pomPath,
                         pomXml,
                         metaData,
                         comment,
                         DeploymentMode.VALIDATED);

            Assert.fail("Exception should've been thrown");
        } catch (final InvalidPomException e) {
            Assert.assertEquals(invalidPomException, e);
        }

        verify(moduleService,
               times(1)).resolveModule(pomPath);
        verify(moduleRepositoriesService,
               never()).load(moduleRepositoriesPath);
        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(pomXml),
                                                         any(MavenRepositoryMetadata.class));

        verify(ioService,
               never()).startBatch(any());
        verify(ioService,
               never()).write(any(org.uberfire.java.nio.file.Path.class),
                               eq(pomXml),
                               eq(attributes),
                               any(CommentedOption.class));
        verify(ioService,
               never()).endBatch();
    }

    @Test
    public void testSaveClashingGAVNoChangeToGAV() {
        when(pom.getGav()).thenReturn(new GAV("groupId",
                                              "artifactId",
                                              "0.0.1"));

        try {
            service.save(pomPath,
                         pomXml,
                         metaData,
                         comment,
                         DeploymentMode.VALIDATED);
        } catch (GAVAlreadyExistsException e) {
            // This is should not be thrown if the GAV has not changed.
            fail(e.getMessage());
        }

        verify(moduleService,
               times(1)).resolveModule(pomPath);
        verify(moduleRepositoriesService,
               never()).load(moduleRepositoriesPath);
        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(pomXml),
                                                         any(MavenRepositoryMetadata.class));

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class));
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               eq(pomXml),
                               eq(attributes),
                               any(CommentedOption.class));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testSaveClashingGAVForced() {
        final Set<ModuleRepositories.ModuleRepository> moduleRepositoriesMetadata = new HashSet<ModuleRepositories.ModuleRepository>() {{
            add(new ModuleRepositories.ModuleRepository(true,
                                                        new MavenRepositoryMetadata("local-id",
                                                                                    "local-url",
                                                                                    MavenRepositorySource.LOCAL)));
        }};
        final ModuleRepositories moduleRepositories = new ModuleRepositories(moduleRepositoriesMetadata);
        when(moduleRepositoriesService.load(moduleRepositoriesPath)).thenReturn(moduleRepositories);

        final Set<MavenRepositoryMetadata> clashingRepositories = new HashSet<MavenRepositoryMetadata>() {{
            add(new MavenRepositoryMetadata("local-id",
                                            "local-url",
                                            MavenRepositorySource.LOCAL));
        }};
        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(pomXml),
                                                                 any(MavenRepositoryMetadata.class))).thenReturn(clashingRepositories);
        when(pom.getGav()).thenReturn(new GAV("groupId",
                                              "artifactId",
                                              "0.0.1"));

        try {
            service.save(pomPath,
                         pomXml,
                         metaData,
                         comment,
                         DeploymentMode.FORCED);
        } catch (GAVAlreadyExistsException e) {
            fail(e.getMessage());
        }

        verify(moduleService,
               never()).resolveModule(pomPath);
        verify(moduleRepositoriesService,
               never()).load(pomPath);
        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(pomXml),
                                                         any(MavenRepositoryMetadata.class));

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class));
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               eq(pomXml),
                               eq(attributes),
                               any(CommentedOption.class));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testSaveSnapshotGAV() throws IOException {
        when(pom.getGav()).thenReturn(new GAV("groupId",
                                              "artifactId",
                                              "0.0.1"));

        pomXml = IOUtils.toString(this.getClass().getResourceAsStream("/testproject/pom.xml"), Charset.defaultCharset());

        service.save(pomPath,
                     pomXml,
                     metaData,
                     comment,
                     DeploymentMode.VALIDATED);

        verify(moduleService,
               times(1)).resolveModule(pomPath);
        verify(moduleRepositoriesService,
               never()).load(moduleRepositoriesPath);
        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(pomXml),
                                                         any(MavenRepositoryMetadata.class));

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class));
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               eq(pomXml),
                               eq(attributes),
                               any(CommentedOption.class));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testInit() {

        final PomEditorServiceImpl serviceImpl = (PomEditorServiceImpl) this.service;

        serviceImpl.init();

        verify(saveAndRenameService).init(serviceImpl);
    }

    @Test
    public void testSaveAndRename() {

        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final Metadata metadata = mock(Metadata.class);
        final String content = "content";
        final String comment = "comment";

        service.saveAndRename(path, newFileName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);
    }

    @Test
    public void testRename() {

        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final String comment = "comment";

        service.rename(path, newFileName, comment);

        verify(renameService).rename(path, newFileName, comment);
    }

    @Test
    public void testSave() {

        final Path path = mock(Path.class);
        final String content = "content";
        final Metadata metadata = mock(Metadata.class);
        final String comment = "comment";

        doReturn(path).when(service).save(path, content, metadata, comment, DeploymentMode.FORCED);

        service.save(path, content, metadata, comment);

        verify(service).save(path, content, metadata, comment, DeploymentMode.FORCED);
    }
}
