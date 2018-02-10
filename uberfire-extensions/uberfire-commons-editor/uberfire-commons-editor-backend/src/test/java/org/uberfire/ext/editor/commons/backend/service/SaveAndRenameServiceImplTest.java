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

package org.uberfire.ext.editor.commons.backend.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SaveAndRenameServiceImplTest {

    @Mock
    private SupportsSaveAndRename<String, DefaultMetadata> supportsSaveAndRename;

    private SaveAndRenameServiceImpl<String, DefaultMetadata> service;

    @Before
    public void setup() throws Exception {
        service = spy(new SaveAndRenameServiceImpl<String, DefaultMetadata>() {{
            init(supportsSaveAndRename);
        }});
    }

    @Test
    public void testRename() throws Exception {

        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final String comment = "comment";

        service.rename(path, newFileName, comment);

        verify(supportsSaveAndRename).rename(path, newFileName, comment);
    }

    @Test
    public void testSave() throws Exception {

        final Path path = mock(Path.class);
        final String content = "content";
        final DefaultMetadata metadata = mock(DefaultMetadata.class);
        final String comment = "comment";

        service.save(path, content, metadata, comment);

        verify(supportsSaveAndRename).save(path, content, metadata, comment);
    }

    @Test
    public void testSaveAndRename() throws Exception {

        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final DefaultMetadata metadata = mock(DefaultMetadata.class);
        final String content = "content";
        final String comment = "comment";

        doReturn(path).when(service).save(path, content, metadata, comment);

        service.saveAndRename(path, newFileName, metadata, content, comment);

        verify(service).save(path, content, metadata, comment);
        verify(service).rename(path, newFileName, comment);
    }
}
