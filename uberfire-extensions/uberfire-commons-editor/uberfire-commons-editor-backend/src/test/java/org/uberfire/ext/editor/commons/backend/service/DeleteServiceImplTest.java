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

package org.uberfire.ext.editor.commons.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSLockService;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.ext.editor.commons.backend.service.helper.DeleteHelper;
import org.uberfire.ext.editor.commons.backend.service.restriction.LockRestrictor;
import org.uberfire.ext.editor.commons.service.ValidationService;
import org.uberfire.ext.editor.commons.service.restrictor.DeleteRestrictor;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeleteServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private User identity;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private VFSLockService lockService;

    @Mock
    private ValidationService validationService;

    @Spy
    @InjectMocks
    private DeleteServiceImpl deleteService;

    @Spy
    @InjectMocks
    private LockRestrictor lockRestrictor;

    @Mock
    private DeleteHelper deleteHelper;

    @Before
    public void setup() {
        when(identity.getIdentifier()).thenReturn("user");

        List<DeleteRestrictor> deleteRestrictors = new ArrayList<>();
        deleteRestrictors.add(lockRestrictor);
        when(deleteService.getDeleteRestrictors()).thenReturn(deleteRestrictors);

        List<DeleteHelper> deleteHelpers = new ArrayList<>();
        deleteHelpers.add(deleteHelper);
        when(deleteService.getDeleteHelpers()).thenReturn(deleteHelpers);
        when(deleteHelper.supports(any(Path.class))).thenReturn(true);
    }

    @Test
    public void deleteLockedPathTest() {
        final Path path = getPath();

        givenThatPathIsLocked(path);

        try {
            whenPathIsDeleted(path);
        } catch (RuntimeException e) {
            thenPathWasNotDeleted(path,
                                  e);
        }

        thenPathWasNotDeleted(path);
    }

    @Test
    public void deleteUnlockedPathTest() {
        final Path path = getPath();

        givenThatPathIsUnlocked(path);
        whenPathIsDeleted(path);
        thenPathWasDeleted(path);
    }

    @Test
    public void deleteLockedPathIfExistsTest() {
        final List<Path> paths = new ArrayList<Path>();
        paths.add(getPath("file0.txt"));
        paths.add(getPath("file1.txt"));
        paths.add(getPath("file2.txt"));

        givenThatPathIsUnlocked(paths.get(0));
        givenThatPathIsLocked(paths.get(1));
        givenThatPathIsUnlocked(paths.get(2));

        try {
            whenPathsAreDeletedIfExists(paths);
        } catch (RuntimeException e) {
            thenPathWasNotDeletedIfExists(paths.get(1),
                                          e);
        }

        thenPathWasDeletedIfExists(paths.get(0));
        thenPathWasNotDeletedIfExists(paths.get(1));

        // This will not be deleted because the process stops when some exception is raised.
        thenPathWasNotDeletedIfExists(paths.get(2));
    }

    @Test
    public void deleteUnlockedPathIfExistsTest() {
        final List<Path> paths = new ArrayList<Path>();
        paths.add(getPath("file0.txt"));
        paths.add(getPath("file1.txt"));
        paths.add(getPath("file2.txt"));

        givenThatPathIsUnlocked(paths.get(0));
        givenThatPathIsUnlocked(paths.get(1));
        givenThatPathIsUnlocked(paths.get(2));

        whenPathsAreDeletedIfExists(paths);

        thenPathWasDeletedIfExists(paths.get(0));
        thenPathWasDeletedIfExists(paths.get(1));
        thenPathWasDeletedIfExists(paths.get(2));
    }

    @Test
    public void pathHasNoDeleteRestrictionTest() {
        final Path path = getPath();

        givenThatPathIsUnlocked(path);
        boolean hasRestriction = whenPathIsCheckedForDeleteRestrictions(path);
        thenPathHasNoDeleteRestrictions(hasRestriction);
    }

    @Test
    public void pathHasDeleteRestrictionTest() {
        final Path path = getPath();

        givenThatPathIsLocked(path);
        boolean hasRestriction = whenPathIsCheckedForDeleteRestrictions(path);
        thenPathHasDeleteRestrictions(hasRestriction);
    }

    @Test
    public void deletePathInvokesDeleteHelpers() {
        final Path path = getPath();

        whenPathIsDeleted(path);

        thenIOServiceBatchStarted();
        thenDeleteHelperWasInvoked(path);
        thenIOServiceBatchEnded();
    }

    @Test
    public void deletePathsIfExistsInvokesDeleteHelpers() {
        final List<Path> paths = new ArrayList<Path>();
        paths.add(getPath("file0.txt"));
        paths.add(getPath("file1.txt"));
        paths.add(getPath("file2.txt"));

        whenPathsAreDeletedIfExists(paths);

        thenIOServiceBatchStarted();
        thenDeleteHelperWasInvoked(paths.get(0));
        thenDeleteHelperWasInvoked(paths.get(1));
        thenDeleteHelperWasInvoked(paths.get(2));
        thenIOServiceBatchEnded();
    }

    @Test
    public void deletePathInvokesDeleteHelpersInCorrectOrder() {
        final Path path = getPath();

        final InOrder order = inOrder(ioService,
                                      deleteHelper,
                                      ioService,
                                      ioService);

        whenPathIsDeleted(path);

        order.verify(ioService).startBatch(any(FileSystem.class));
        order.verify(deleteHelper).postProcess(eq(path));
        order.verify(ioService).delete(any(org.uberfire.java.nio.file.Path.class),
                                       any(CommentedOption.class));
        order.verify(ioService).endBatch();
    }

    private void givenThatPathIsLocked(final Path path) {
        changeLockInfo(path,
                       true);
    }

    private void givenThatPathIsUnlocked(final Path path) {
        changeLockInfo(path,
                       false);
    }

    private void whenPathIsDeleted(final Path path) {
        deleteService.delete(path,
                             "comment");
    }

    private void whenPathsAreDeletedIfExists(final Collection<Path> paths) {
        deleteService.deleteIfExists(paths,
                                     "comment");
    }

    private boolean whenPathIsCheckedForDeleteRestrictions(final Path path) {
        return deleteService.hasRestriction(path);
    }

    private void thenPathWasDeleted(final Path path) {
        verify(deleteService).deletePath(eq(path),
                                         any(String.class));
    }

    private void thenPathWasNotDeleted(final Path path) {
        verify(deleteService,
               never()).deletePath(eq(path),
                                   any(String.class));
    }

    private void thenPathWasNotDeleted(final Path path,
                                       final RuntimeException e) {
        assertEquals(path.toURI() + " cannot be deleted, moved or renamed. It is locked by: lockedBy",
                     e.getMessage());
    }

    private void thenPathWasDeletedIfExists(final Path path) {
        verify(deleteService).deletePathIfExists(eq(path),
                                                 any(String.class));
    }

    private void thenPathWasNotDeletedIfExists(final Path path) {
        verify(deleteService,
               never()).deletePathIfExists(eq(path),
                                           any(String.class));
    }

    private void thenPathWasNotDeletedIfExists(final Path path,
                                               final RuntimeException e) {
        assertEquals(path.toURI() + " cannot be deleted, moved or renamed. It is locked by: lockedBy",
                     e.getMessage());
    }

    private void thenPathHasNoDeleteRestrictions(final boolean hasRestriction) {
        assertFalse(hasRestriction);
    }

    private void thenPathHasDeleteRestrictions(final boolean hasRestriction) {
        assertTrue(hasRestriction);
    }

    private void thenIOServiceBatchStarted() {
        verify(ioService).startBatch(any(FileSystem.class));
    }

    private void thenIOServiceBatchEnded() {
        verify(ioService).endBatch();
    }

    private void thenDeleteHelperWasInvoked(final Path path) {
        verify(deleteHelper).postProcess(eq(path));
    }

    private Path getPath() {
        return getPath("file.txt");
    }

    private Path getPath(String fileName) {
        return PathFactory.newPath(fileName,
                                   "file://tmp/" + fileName);
    }

    private void changeLockInfo(Path path,
                                boolean locked) {
        when(lockService.retrieveLockInfo(path)).thenReturn(new LockInfo(locked,
                                                                         "lockedBy",
                                                                         path));
    }
}
