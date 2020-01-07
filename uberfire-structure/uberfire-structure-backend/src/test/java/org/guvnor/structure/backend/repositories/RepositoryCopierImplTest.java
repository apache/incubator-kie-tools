/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.structure.backend.repositories;

import java.net.URI;
import java.util.Optional;

import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryCopierImplTest {

    private static final String PATH_PREFIX = "git://amend-repo-test/";

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    @Mock
    private EventSourceMock<NewBranchEvent> newBranchEventEvent;

    @Mock
    private ConfiguredRepositories configuredRepositories;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private Repository repository;

    @Mock
    private Branch branch;

    @Mock
    private User user;

    private RepositoryCopierImpl copier;

    private IOService ioService;

    @Before
    public void setUp() throws Exception {
        doReturn("user").when(user).getIdentifier();

        fileSystemTestingUtils.setup();
        ioService = spy(fileSystemTestingUtils.getIoService());

        copier = new RepositoryCopierImpl(ioService,
                                          newBranchEventEvent,
                                          configuredRepositories,
                                          repositoryService,
                                          sessionInfo);
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void makeSafeName() throws Exception {
        assertEquals("hello", copier.makeSafeRepositoryName("hello"));
        assertEquals("test-this", copier.makeSafeRepositoryName("test this"));
    }

    @Test
    public void withExistingBranch() throws Exception {
        final org.uberfire.java.nio.file.Path nioFrom = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "from"));
        final Path from = Paths.convert(nioFrom);

        fileSystemTestingUtils.getIoService().createDirectory(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "from/sub1")));
        fileSystemTestingUtils.getIoService().createDirectory(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "from/sub2")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "from/sub1/file1.txt")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "from/sub1/file2.txt")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "from/sub2/file3.txt")));

        final org.uberfire.java.nio.file.Path nioTo = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "to"));
        final Path to = Paths.convert(nioTo);

        fileSystemTestingUtils.getIoService().createDirectory(nioTo);

        doReturn(repository).when(repositoryService).getRepository(to);

        copier.copy(from,
                    to);

        verify(ioService).startBatch(fileSystemTestingUtils.getFileSystem());
        verify(ioService).endBatch();
        verify(newBranchEventEvent,
               never()).fire(any(NewBranchEvent.class));
    }

    @Test
    public void testFireNewBranchEvent() throws Exception {
        final org.uberfire.java.nio.file.Path nioFrom = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "from"));

        final org.uberfire.java.nio.file.Path nioTo = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "to"));
        final Path to = Paths.convert(nioTo);

        doReturn(repository).when(repositoryService).getRepository(any(Path.class));
        when(repository.getBranch(any(Path.class))).thenReturn(Optional.of(branch));
        when(branch.getName()).thenReturn("testBranch");

        when(sessionInfo.getIdentity()).thenReturn(user);

        copier.fireNewBranchEvent(to, nioTo, nioFrom);

        verify(newBranchEventEvent,
               times(1)).fire(any(NewBranchEvent.class));
    }
}
