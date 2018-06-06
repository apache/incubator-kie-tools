/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.common.services.project.backend.server;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.utils.POMContentHandler;
import org.guvnor.common.services.project.events.ModuleUpdatedEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.m2repo.service.M2RepoService;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class POMServiceImplSaveTest {

    @Mock
    private IOService ioService;

    @Mock
    private POMContentHandler pomContentHandler;

    @Mock
    private ModuleService moduleService;

    @Mock
    private EventSourceMock<ModuleUpdatedEvent> moduleUpdatedEvent;

    @Captor
    private ArgumentCaptor<org.uberfire.java.nio.file.Path> pathArgumentCaptor;

    private TestFileSystem testFileSystem;
    private POMServiceImpl service;

    @Before
    public void setUp() throws Exception {
        testFileSystem = new TestFileSystem();

        doReturn(new POM("mymodule",
                         "desctiption",
                         "url",
                         new GAV())).when(pomContentHandler).toModel(any());
        doReturn("").when(pomContentHandler).toString(any(), any());

        doReturn(mock(Module.class)).when(moduleService).resolveModule(any());

        service = new POMServiceImpl(ioService,
                                     pomContentHandler,
                                     mock(M2RepoService.class),
                                     mock(MetadataService.class),
                                     moduleUpdatedEvent,
                                     moduleService,
                                     mock(CommentedOptionFactory.class),
                                     mock(PomEnhancer.class));
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void whenProjectSavedWithoutChanges_ModuleUpdatedEventNotFired() throws Exception {
        final Path path = testFileSystem.createTempFile("testproject/pom.xml");

        POM originalPOM = new POM("mymodule",
                                  "desctiption",
                                  "url",
                                  new GAV());
        service.save(path,
                     originalPOM,
                     mock(Metadata.class),
                     "");

        verify(ioService).startBatch(any(), any());
        verify(ioService).write(pathArgumentCaptor.capture(),
                                anyString(),
                                anyMap());
        verify(ioService).endBatch();
        verify(moduleUpdatedEvent, never()).fire(any());

        assertTrue(path.toURI().endsWith(pathArgumentCaptor.getValue().toString()));
    }

    @Test
    public void whenProjectRenamed_ModuleUpdatedEventIsFired() throws Exception {
        final Path path = testFileSystem.createTempFile("testproject/pom.xml");

        service.save(path,
                     new POM("newName",
                             "desctiption",
                             "url",
                             new GAV()),
                     mock(Metadata.class),
                     "");

        verify(moduleUpdatedEvent).fire(any());
    }
}