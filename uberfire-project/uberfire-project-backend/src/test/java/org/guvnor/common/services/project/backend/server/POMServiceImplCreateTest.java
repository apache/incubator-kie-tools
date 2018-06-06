/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.utils.POMContentHandler;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.m2repo.service.M2RepoService;
import org.guvnor.test.TestTempFileSystem;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(WeldJUnitRunner.class)
public class POMServiceImplCreateTest {

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    POMContentHandler pomContentHandler;

    @Mock
    M2RepoService m2RepoService;

    @Mock
    MetadataService metadataService;

    PomEnhancer pomEnhancer;

    private POMServiceImpl service;

    @Inject
    private Paths paths;

    private IOService ioServiceSpy;

    @Inject
    private TestTempFileSystem testFileSystem;

    @Before
    public void setUp() throws Exception {
        pomEnhancer = new DefaultPomEnhancer();
        MockitoAnnotations.initMocks(this);

        ioServiceSpy = spy(ioService);

        service = new POMServiceImpl(ioServiceSpy,
                                     pomContentHandler,
                                     m2RepoService,
                                     metadataService,
                                     new EventSourceMock<>(),
                                     mock(ModuleService.class),
                                     mock(CommentedOptionFactory.class),
                                     pomEnhancer);
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void testCreate() throws Exception {
        final Path path = testFileSystem.createTempDirectory("/MyTestProject");

        service.create(path,
                       new POM());

        ArgumentCaptor<org.uberfire.java.nio.file.Path> pathArgumentCaptor = ArgumentCaptor.forClass(org.uberfire.java.nio.file.Path.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(ioServiceSpy).write(pathArgumentCaptor.capture(),
                                   stringArgumentCaptor.capture());

        assertEquals(pathArgumentCaptor.getValue().toUri().toString(),
                     path.toURI() + "/pom.xml");

        String pomXML = stringArgumentCaptor.getValue();

        assertTrue(pomXML.contains("<id>guvnor-m2-repo</id>"));
    }
}