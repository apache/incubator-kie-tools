/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.service;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNDiagramServiceImplTest {

    public static final String DIR_URI = "default://master@diagrams/root";

    private static final String FILE_NAME = ".caseproject";

    public static final String FILE_URI = DIR_URI + "/" + FILE_NAME;

    private BPMNDiagramServiceImpl tested;

    @Mock
    private IOService ioService;

    @Mock
    private DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream;

    @Mock
    private org.uberfire.backend.vfs.Path projectPath;

    @Mock
    private Path fsPath;

    @Mock
    private Path fileName;

    @Mock
    private FileSystem fs;

    @Before
    public void setUp() throws Exception {

        ArgumentCaptor<DirectoryStream.Filter> filter = ArgumentCaptor.forClass(DirectoryStream.Filter.class);
        when(ioService.newDirectoryStream(any(), filter.capture())).thenReturn(directoryStream);
        when(directoryStream.spliterator()).thenReturn(Arrays.asList(fsPath).spliterator());
        when(projectPath.toURI()).thenReturn(DIR_URI);
        when(fsPath.getFileName()).thenReturn(fileName);
        when(fsPath.toUri()).thenReturn(new URI(FILE_URI));
        when(fsPath.getFileSystem()).thenReturn(fs);
        when(fileName.toString()).thenReturn(FILE_NAME);

        tested = new BPMNDiagramServiceImpl(ioService);
    }

    @Test
    public void getProjectTypeCase() {
        final ProjectType projectType = tested.getProjectType(projectPath);
        assertEquals(projectType, ProjectType.CASE);
    }

    @Test
    public void getProjectTypeNull() {
        when(directoryStream.spliterator()).thenReturn(Collections.<Path>emptyList().spliterator());
        final ProjectType projectType = tested.getProjectType(projectPath);
        assertEquals(projectType, null);
    }
}