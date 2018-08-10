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

package org.drools.workbench.screens.enums.backend.server;

import java.util.Collections;

import javax.enterprise.event.Event;

import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.builder.events.InvalidateDMOPackageCacheEvent;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileAlreadyExistsException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnumServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private CommentedOption commentedOption;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private MetadataServerSideService metadataService;

    @Mock
    private Event<InvalidateDMOPackageCacheEvent> invalidateDMOPackageCache;

    @Mock
    private SaveAndRenameServiceImpl<String, Metadata> saveAndRenameService;

    @Spy
    @InjectMocks
    private EnumServiceImpl enumService = new EnumServiceImpl();

    @Test
    public void testCreate() throws Exception {
        final String fileName = "enum.enumeration";
        final String fileContent = "'Person.age' : [10,20,30]\n";
        final String comment = "comment of the author";
        when(commentedOptionFactory.makeCommentedOption(comment)).thenReturn(commentedOption);

        enumService.create(PathFactory.newPath(fileName, getClass().getResource("enums").toString()),
                           fileName,
                           fileContent,
                           comment);

        verify(ioService).write(any(org.uberfire.java.nio.file.Path.class), eq(fileContent), eq(commentedOption));
    }

    @Test
    public void testSave() throws Exception {
        final String fileName = "enum.enumeration";
        final String fileContent = "'Person.age' : [10,20,30]\n";
        final String comment = "comment of the author";
        final Metadata metadata = mock(Metadata.class);
        final Path path = PathFactory.newPath(fileName, getClass().getResource("enums").toString());
        when(commentedOptionFactory.makeCommentedOption(comment)).thenReturn(commentedOption);
        when(metadataService.setUpAttributes(path, metadata)).thenReturn(Collections.EMPTY_MAP);

        enumService.save(path,
                         fileContent,
                         metadata,
                         comment);

        verify(ioService).write(any(org.uberfire.java.nio.file.Path.class),
                                eq(fileContent),
                                eq(Collections.EMPTY_MAP),
                                eq(commentedOption));
    }

    @Test
    public void testInit() throws Exception {
        enumService.init();

        verify(saveAndRenameService).init(enumService);
    }

    @Test
    public void testSaveAndRename() throws Exception {

        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final Metadata metadata = mock(Metadata.class);
        final String content = "content";
        final String comment = "comment";

        enumService.saveAndRename(path, newFileName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);
    }

    @Test
    public void testCreateEnumAlreadyExists() {
        final Path path = PathFactory.newPath("enum.enumeration", "file:///enum.enumeration");
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);
        assertThatThrownBy(() -> enumService.create(path, "", "", ""))
                .isInstanceOf(FileAlreadyExistsException.class);
    }
}
