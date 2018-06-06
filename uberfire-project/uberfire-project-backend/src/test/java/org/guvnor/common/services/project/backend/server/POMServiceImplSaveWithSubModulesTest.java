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

import java.util.ArrayList;

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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class POMServiceImplSaveWithSubModulesTest {

    @Mock
    private IOService ioService;

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

        doReturn(mock(Module.class)).when(moduleService).resolveModule(any());

        service = new POMServiceImpl(ioService,
                                     mock(POMContentHandler.class),
                                     mock(M2RepoService.class),
                                     mock(MetadataService.class),
                                     moduleUpdatedEvent,
                                     moduleService,
                                     mock(CommentedOptionFactory.class),
                                     mock(PomEnhancer.class)) {
            int index = 0;

            @Override
            public POM load(Path pomPath) {
                return new POM("mymodule " + index++,
                               "desctiption",
                               "url",
                               new GAV());
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void whenProjectSubModulesEdited_UpdateModuleEventIsFired() throws Exception {

        final Path path = testFileSystem.createTempFile("testproject/pom.xml");
        doReturn(true).when(ioService).exists(any());

        final POM pom = new POM("newName",
                                "desctiption",
                                "url",
                                new GAV());
        pom.setPackaging("pom");
        final ArrayList<String> modules = new ArrayList<>();
        modules.add("module1");
        modules.add("module2");
        pom.setModules(modules);

        service.save(path,
                     pom,
                     mock(Metadata.class),
                     "",
                     true);

        verify(moduleUpdatedEvent, times(3)).fire(any());
    }
}