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

package org.kie.workbench.common.stunner.bpmn.project.backend.service;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.project.service.BPMNDiagramEditorService;
import org.kie.workbench.common.stunner.bpmn.project.service.MigrationResult;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNDiagramEditorServiceImplTest {

    private static final String FILE_URI = "default:///some_directory/ProcessFile.bpmn2";

    private static final String NEW_FILE_NAME = "NewFileName";

    private static final String NEW_EXTENSION = ".NewExtension";

    private static final String EXPECTED_FILE_URI = "default:///some_directory/" + NEW_FILE_NAME + NEW_EXTENSION;

    private static final String COMMIT_MESSAGE = "COMMIT_MESSAGE";

    @Mock
    private IOService ioService;

    @Mock
    private CommentedOptionFactory optionFactory;

    @Mock
    private CommentedOption commentedOption;

    @Mock
    private Path path;

    @Mock
    private ArgumentCaptor<org.uberfire.java.nio.file.Path> sourcePathCaptor;

    @Mock
    private ArgumentCaptor<org.uberfire.java.nio.file.Path> targetPathCaptor;

    private BPMNDiagramEditorServiceImpl editorService;

    @Before
    public void setUp() {
        sourcePathCaptor = ArgumentCaptor.forClass(org.uberfire.java.nio.file.Path.class);
        targetPathCaptor = ArgumentCaptor.forClass(org.uberfire.java.nio.file.Path.class);
        editorService = new BPMNDiagramEditorServiceImpl(ioService,
                                                         optionFactory);
    }

    @Test
    public void testMigrateDiagramSuccessful() {
        when(path.toURI()).thenReturn(FILE_URI);
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(false);
        when(optionFactory.makeCommentedOption(COMMIT_MESSAGE)).thenReturn(commentedOption);
        MigrationResult migrationResult = editorService.migrateDiagram(path,
                                                                       NEW_FILE_NAME,
                                                                       NEW_EXTENSION,
                                                                       COMMIT_MESSAGE);
        verify(ioService,
               times(1)).move(sourcePathCaptor.capture(),
                              targetPathCaptor.capture(),
                              eq(commentedOption));
        assertEquals(FILE_URI,
                     sourcePathCaptor.getValue().toUri().toString());
        assertEquals(EXPECTED_FILE_URI,
                     targetPathCaptor.getValue().toUri().toString());
        verify(ioService,
               times(1)).startBatch(any(FileSystem.class));
        verify(ioService,
               times(1)).endBatch();

        assertFalse(migrationResult.hasError());
        assertNull(migrationResult.getError());
        assertEquals(EXPECTED_FILE_URI,
                     migrationResult.getPath().toURI());
    }

    @Test
    public void testMigrateDiagramFailed() {
        when(path.toURI()).thenReturn(FILE_URI);
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);
        when(optionFactory.makeCommentedOption(COMMIT_MESSAGE)).thenReturn(commentedOption);
        MigrationResult migrationResult = editorService.migrateDiagram(path,
                                                                       NEW_FILE_NAME,
                                                                       NEW_EXTENSION,
                                                                       COMMIT_MESSAGE);
        verify(ioService,
               never()).move(any(org.uberfire.java.nio.file.Path.class),
                             any(org.uberfire.java.nio.file.Path.class),
                             any(CommentedOption.class));
        verify(ioService,
               never()).startBatch(any(FileSystem.class));
        verify(ioService,
               never()).endBatch();

        assertTrue(migrationResult.hasError());
        Assert.assertEquals(BPMNDiagramEditorService.ServiceError.MIGRATION_ERROR_PROCESS_ALREADY_EXIST,
                            migrationResult.getError());
    }
}
