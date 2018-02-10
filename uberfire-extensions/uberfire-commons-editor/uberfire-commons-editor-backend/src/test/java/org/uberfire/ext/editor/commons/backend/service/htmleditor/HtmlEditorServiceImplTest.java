/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.backend.service.htmleditor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HtmlEditorServiceImplTest {

    @Mock
    private VFSService vfsServices;

    @Mock
    private DeleteService deleteService;

    @Mock
    private RenameService renameService;

    @Mock
    private CopyService copyService;

    @Mock
    private SaveAndRenameServiceImpl<String, DefaultMetadata> saveAndRenameService;

    @Mock
    private Path path;

    @Mock
    private DefaultMetadata metadata;

    private String content = "content";

    private String comment = "comment";

    private String newFileName = "newFileName";

    @Spy
    @InjectMocks
    private HtmlEditorServiceImpl htmlEditorService;

    @Test
    public void testInit() throws Exception {

        htmlEditorService.init();

        verify(saveAndRenameService).init(htmlEditorService);
    }

    @Test
    public void testDelete() throws Exception {

        htmlEditorService.delete(path, comment);

        verify(deleteService).delete(path, comment);
    }

    @Test
    public void testRename() throws Exception {

        final Path expectedPath = mock(Path.class);
        doReturn(expectedPath).when(renameService).rename(path, newFileName, comment);

        final Path actualPath = htmlEditorService.rename(path, newFileName, comment);

        verify(renameService).rename(path, newFileName, comment);
        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void testSave() throws Exception {

        final Path expectedPath = mock(Path.class);
        doReturn(expectedPath).when(vfsServices).write(path, content);

        final Path actualPath = htmlEditorService.save(path, content, metadata, comment);

        verify(vfsServices).write(path, content);
        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void testCopyWithoutTargetDirectory() throws Exception {

        final Path expectedPath = mock(Path.class);
        doReturn(expectedPath).when(copyService).copy(path, newFileName, comment);

        final Path actualPath = htmlEditorService.copy(path, newFileName, comment);

        verify(copyService).copy(path, newFileName, comment);
        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void testCopyWithTargetDirectory() throws Exception {

        final Path targetDirectory = mock(Path.class);
        final Path expectedPath = mock(Path.class);

        doReturn(expectedPath).when(copyService).copy(path, newFileName, targetDirectory, comment);

        final Path actualPath = htmlEditorService.copy(path, newFileName, targetDirectory, comment);

        verify(copyService).copy(path, newFileName, targetDirectory, comment);
        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void testLoad() throws Exception {

        final String expectedString = "string";

        doReturn(expectedString).when(vfsServices).readAllString(path);

        final String actualString = htmlEditorService.load(path);

        verify(vfsServices).readAllString(path);
        assertEquals(expectedString, actualString);
    }

    @Test
    public void testSaveAndRename() throws Exception {

        final Path expectedPath = mock(Path.class);
        doReturn(expectedPath).when(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);

        final Path actualPath = htmlEditorService.saveAndRename(path, newFileName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);
        assertEquals(expectedPath, actualPath);
    }
}
