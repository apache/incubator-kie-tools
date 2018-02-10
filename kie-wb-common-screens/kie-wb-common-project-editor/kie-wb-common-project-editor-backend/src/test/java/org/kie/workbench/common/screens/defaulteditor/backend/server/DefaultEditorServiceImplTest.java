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

package org.kie.workbench.common.screens.defaulteditor.backend.server;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.RenameService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEditorServiceImplTest {

    @Mock
    private RenameService renameService;

    @Mock
    private SaveAndRenameServiceImpl<String, Metadata> saveAndRenameService;

    @InjectMocks
    private DefaultEditorServiceImpl service;

    @Test
    public void init() throws Exception {

        service.init();

        verify(saveAndRenameService).init(service);
    }

    @Test
    public void rename() throws Exception {

        final Path path = mock(Path.class);
        final String newName = "newName";
        final String comment = "comment";

        service.rename(path, newName, comment);

        verify(renameService).rename(path, newName, comment);
    }

    @Test
    public void saveAndRename() throws Exception {

        final Path path = mock(Path.class);
        final Metadata metadata = mock(Metadata.class);
        final String newName = "newName";
        final String content = "content";
        final String comment = "comment";

        service.saveAndRename(path, newName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newName, metadata, content, comment);
    }
}
