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

package org.guvnor.common.services.project.backend.server;

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.ModuleResourceResolver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AbstractModuleRepositoriesServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private ModuleRepositoryResolver repositoryResolver;

    @Mock
    private ModuleResourceResolver resourceResolver;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    private ModuleRepositoriesService service;
    private ModuleRepositoriesContentHandler contentHandler;

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
        contentHandler = spy(new ModuleRepositoriesContentHandler());
        service = new AbstractModuleRepositoriesServiceImpl<Module>(ioService,
                                                                    repositoryResolver,
                                                                    contentHandler,
                                                                    commentedOptionFactory) {
            @Override
            protected Module getModule(final Path path) {
                return resourceResolver.resolveModule(path);
            }
        };
    }

    @Test
    public void testCreateWithoutProject() {
        final String xml = "<project-repositories>\n" +
                "  <repositories>\n" +
                "    <repository>\n" +
                "      <include>true</include>\n" +
                "      <metadata>\n" +
                "        <id>local</id>\n" +
                "        <url>local-url</url>\n" +
                "        <source>LOCAL</source>\n" +
                "      </metadata>\n" +
                "    </repository>\n" +
                "  </repositories>\n" +
                "</project-repositories>";

        final Path path = mock(Path.class);
        when(path.toURI()).thenReturn("file://p0/project.repositories");
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);

        final Set<MavenRepositoryMetadata> metadata = new HashSet<MavenRepositoryMetadata>();
        metadata.add(new MavenRepositoryMetadata("local",
                                                 "local-url",
                                                 MavenRepositorySource.LOCAL));
        when(repositoryResolver.getRemoteRepositoriesMetaData()).thenReturn(metadata);
        when(resourceResolver.resolveModule(path)).thenReturn(null);

        service.create(path);

        verify(resourceResolver,
               times(1)).resolveModule(path);
        verify(repositoryResolver,
               times(1)).getRemoteRepositoriesMetaData();

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class),
                                    any(CommentedOption.class));
        verify(ioService,
               times(1)).write(eq(nioPath),
                               eq(xml));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testCreateWithProject() {
        final String xml = "<project-repositories>\n" +
                "  <repositories>\n" +
                "    <repository>\n" +
                "      <include>true</include>\n" +
                "      <metadata>\n" +
                "        <id>local</id>\n" +
                "        <url>local-url</url>\n" +
                "        <source>LOCAL</source>\n" +
                "      </metadata>\n" +
                "    </repository>\n" +
                "  </repositories>\n" +
                "</project-repositories>";

        final Path path = mock(Path.class);
        when(path.toURI()).thenReturn("file://p0/project.repositories");
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);

        final Module module = mock(Module.class);

        final Set<MavenRepositoryMetadata> metadata = new HashSet<MavenRepositoryMetadata>();
        metadata.add(new MavenRepositoryMetadata("local",
                                                 "local-url",
                                                 MavenRepositorySource.LOCAL));
        when(repositoryResolver.getRemoteRepositoriesMetaData(eq(module))).thenReturn(metadata);
        when(resourceResolver.resolveModule(path)).thenReturn(module);

        service.create(path);

        verify(resourceResolver,
               times(1)).resolveModule(path);
        verify(repositoryResolver,
               times(1)).getRemoteRepositoriesMetaData(eq(module));

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class),
                                    any(CommentedOption.class));
        verify(ioService,
               times(1)).write(eq(nioPath),
                               eq(xml));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testLoadExisting() {
        final String xml = "<project-repositories>\n" +
                "  <repositories>\n" +
                "    <repository>\n" +
                "      <include>true</include>\n" +
                "      <metadata>\n" +
                "        <id>id</id>\n" +
                "        <url>url</url>\n" +
                "        <source>LOCAL</source>\n" +
                "      </metadata>\n" +
                "    </repository>\n" +
                "  </repositories>\n" +
                "</project-repositories>";

        final Path path = mock(Path.class);
        when(path.toURI()).thenReturn("file://p0/project.repositories");
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);
        when(ioService.exists(eq(nioPath))).thenReturn(true);
        when(ioService.readAllString(eq(nioPath))).thenReturn(xml);

        final ModuleRepositories repositories = service.load(path);
        assertNotNull(repositories);
        assertNotNull(repositories.getRepositories());
        assertEquals(1,
                     repositories.getRepositories().size());

        final ModuleRepositories.ModuleRepository repository = repositories.getRepositories().iterator().next();
        assertEquals("id",
                     repository.getMetadata().getId());
        assertEquals("url",
                     repository.getMetadata().getUrl());
        assertEquals(MavenRepositorySource.LOCAL,
                     repository.getMetadata().getSource());
        assertEquals(true,
                     repository.isIncluded());

        verify(contentHandler,
               times(1)).toModel(eq(xml));
    }

    @Test
    public void testLoadNonExisting() {
        final String xml = "<project-repositories>\n" +
                "  <repositories>\n" +
                "    <repository>\n" +
                "      <include>true</include>\n" +
                "      <metadata>\n" +
                "        <id>local</id>\n" +
                "        <url>local-url</url>\n" +
                "        <source>LOCAL</source>\n" +
                "      </metadata>\n" +
                "    </repository>\n" +
                "  </repositories>\n" +
                "</project-repositories>";

        final Path path = mock(Path.class);
        when(path.toURI()).thenReturn("file://p0/project.repositories");
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);
        when(ioService.exists(eq(nioPath))).thenReturn(false);

        final Set<MavenRepositoryMetadata> metadata = new HashSet<MavenRepositoryMetadata>();
        metadata.add(new MavenRepositoryMetadata("local",
                                                 "local-url",
                                                 MavenRepositorySource.LOCAL));
        when(repositoryResolver.getRemoteRepositoriesMetaData()).thenReturn(metadata);

        final ModuleRepositories repositories = service.load(path);
        assertNotNull(repositories);
        assertNotNull(repositories.getRepositories());
        assertEquals(1,
                     repositories.getRepositories().size());

        final ModuleRepositories.ModuleRepository repository = repositories.getRepositories().iterator().next();
        assertEquals("local",
                     repository.getMetadata().getId());
        assertEquals("local-url",
                     repository.getMetadata().getUrl());
        assertEquals(MavenRepositorySource.LOCAL,
                     repository.getMetadata().getSource());
        assertEquals(true,
                     repository.isIncluded());

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class),
                                    any(CommentedOption.class));
        verify(ioService,
               times(1)).write(eq(nioPath),
                               eq(xml));
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testSave() {
        final Set<ModuleRepositories.ModuleRepository> repositories = new HashSet<ModuleRepositories.ModuleRepository>();
        repositories.add(new ModuleRepositories.ModuleRepository(true,
                                                                 new MavenRepositoryMetadata("id",
                                                                                             "url",
                                                                                             MavenRepositorySource.LOCAL)));
        final ModuleRepositories moduleRepositories = new ModuleRepositories(repositories);

        final String xml = "<project-repositories>\n" +
                "  <repositories>\n" +
                "    <repository>\n" +
                "      <include>true</include>\n" +
                "      <metadata>\n" +
                "        <id>id</id>\n" +
                "        <url>url</url>\n" +
                "        <source>LOCAL</source>\n" +
                "      </metadata>\n" +
                "    </repository>\n" +
                "  </repositories>\n" +
                "</project-repositories>";

        final Path path = mock(Path.class);
        when(path.toURI()).thenReturn("file://p0/project.repositories");
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);

        service.save(path,
                     moduleRepositories,
                     "comment");

        verify(ioService,
               times(1)).write(eq(nioPath),
                               eq(xml));
    }

    @Test
    public void testFiltered() {
        final String xml = "<project-repositories>\n" +
                "  <repositories>\n" +
                "    <repository>\n" +
                "      <include>true</include>\n" +
                "      <metadata>\n" +
                "        <id>id1</id>\n" +
                "        <url>url1</url>\n" +
                "        <source>LOCAL</source>\n" +
                "      </metadata>\n" +
                "    </repository>\n" +
                "    <repository>\n" +
                "      <include>false</include>\n" +
                "      <metadata>\n" +
                "        <id>id2</id>\n" +
                "        <url>url2</url>\n" +
                "        <source>LOCAL</source>\n" +
                "      </metadata>\n" +
                "    </repository>\n" +
                "  </repositories>\n" +
                "</project-repositories>";

        final Path path = mock(Path.class);
        when(path.toURI()).thenReturn("file://p0/project.repositories");
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);
        when(ioService.exists(eq(nioPath))).thenReturn(true);
        when(ioService.readAllString(eq(nioPath))).thenReturn(xml);

        final ModuleRepositories repositories = service.load(path);
        assertNotNull(repositories);
        assertNotNull(repositories.getRepositories());
        assertEquals(2,
                     repositories.getRepositories().size());

        final MavenRepositoryMetadata[] metadatas = repositories.filterByIncluded();
        assertEquals(1,
                     metadatas.length);

        final MavenRepositoryMetadata metadata = metadatas[0];
        assertEquals("id1",
                     metadata.getId());
        assertEquals("url1",
                     metadata.getUrl());
        assertEquals(MavenRepositorySource.LOCAL,
                     metadata.getSource());
    }
}
