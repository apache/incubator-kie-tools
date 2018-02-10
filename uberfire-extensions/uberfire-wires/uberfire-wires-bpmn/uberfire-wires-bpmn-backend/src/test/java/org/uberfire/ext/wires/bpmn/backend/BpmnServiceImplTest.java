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

package org.uberfire.ext.wires.bpmn.backend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.service.todo.Metadata;
import org.uberfire.java.nio.file.FileSystem;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BpmnServiceImplTest {

    @Mock
    private SaveAndRenameServiceImpl<ProcessNode, Metadata> saveAndRenameService;

    @Spy
    @InjectMocks
    private BpmnServiceImpl bpmnService = new BpmnServiceImpl();

    @Test(expected = UnsupportedOperationException.class)
    public void bpmnResourceCopyTest() {
        Path path = mock(Path.class);
        String newName = "newName";
        Path targetDirectory = mock(Path.class);
        String comment = "comment";

        bpmnService.copy(path,
                         newName,
                         targetDirectory,
                         comment);
    }

    @Test
    public void testSaveAndRename() {

        final String newFileName = "newFileName";
        final String comment = "comment";
        final Path path = mock(Path.class);
        final Metadata metadata = mock(Metadata.class);
        final ProcessNode content = mock(ProcessNode.class);

        bpmnService.saveAndRename(path, newFileName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);
    }
}
