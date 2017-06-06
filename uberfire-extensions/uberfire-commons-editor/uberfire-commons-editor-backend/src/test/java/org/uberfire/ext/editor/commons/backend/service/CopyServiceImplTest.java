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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSLockService;
import org.uberfire.ext.editor.commons.backend.service.naming.PathNamingServiceImpl;
import org.uberfire.ext.editor.commons.backend.service.restriction.LockRestrictor;
import org.uberfire.ext.editor.commons.service.ValidationService;
import org.uberfire.ext.editor.commons.service.restriction.PathOperationRestriction;
import org.uberfire.ext.editor.commons.service.restrictor.CopyRestrictor;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceCopiedEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CopyServiceImplTest {

    private static final String PATH_PREFIX = "git://amend-repo-test/";

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();
    private final List<String> restrictedFileNames = new ArrayList<String>();
    @Spy
    private Event<ResourceCopiedEvent> resourceCopiedEvent = new EventSourceMock<>();
    @Mock
    private Instance<CopyRestrictor> copyRestrictorBeans;
    @Mock
    private User identity;
    @Mock
    private SessionInfo sessionInfo;
    @Mock
    private VFSLockService lockService;
    @Mock
    private ValidationService validationService;
    @Spy
    private PathNamingServiceImpl pathNamingService = new PathNamingServiceImpl();
    private CopyServiceImpl copyService;
    @Spy
    @InjectMocks
    private LockRestrictor lockRestrictor;

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();

        this.copyService = spy(new CopyServiceImpl(fileSystemTestingUtils.getIoService(),
                                                   identity,
                                                   sessionInfo,
                                                   null,
                                                   resourceCopiedEvent,
                                                   copyRestrictorBeans,
                                                   pathNamingService));

        when(identity.getIdentifier()).thenReturn("user");

        doReturn(Collections.EMPTY_LIST).when(pathNamingService).getResourceTypeDefinitions();

        mockCopyRestrictors();

        doNothing().when(resourceCopiedEvent).fire(any(ResourceCopiedEvent.class));
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void copyRestrictedPathTest() {
        final Path path = createFile("restricted-file.txt");

        givenThatPathIsRestricted(path);

        try {
            whenPathIsCopied(path);
        } catch (RuntimeException e) {
            thenPathWasNotCopied(path,
                                 e);
        }

        thenPathWasNotCopied(path);
    }

    @Test
    public void copyUnrestrictedPathTest() {
        final Path path = createFile();

        givenThatPathIsUnrestricted(path);
        whenPathIsCopied(path);
        thenPathWasCopied(path);
    }

    @Test
    public void copyRestrictedPathIfExistsTest() {
        final List<Path> paths = new ArrayList<Path>();
        paths.add(createFile("file0.txt"));
        paths.add(createFile("file1.txt"));
        paths.add(createFile("file2.txt"));

        givenThatPathIsUnrestricted(paths.get(0));
        givenThatPathIsRestricted(paths.get(1));
        givenThatPathIsUnrestricted(paths.get(2));

        try {
            whenPathsAreCopiedIfExists(paths);
        } catch (RuntimeException e) {
            thenPathWasNotCopiedIfExists(paths.get(1),
                                         e);
        }

        thenPathWasCopiedIfExists(paths.get(0));
        thenPathWasNotCopiedIfExists(paths.get(1));

        // This will not be copied because the process stops when some exception is raised.
        thenPathWasNotCopiedIfExists(paths.get(2));
    }

    @Test
    public void copyUnrestrictedPathIfExistsTest() {
        final List<Path> paths = new ArrayList<Path>();
        paths.add(createFile("dir1/file1.txt"));
        paths.add(createFile("dir2/file2.txt"));
        paths.add(createFile("dir3/file3.txt"));

        givenThatPathIsUnrestricted(paths.get(0));
        givenThatPathIsUnrestricted(paths.get(1));
        givenThatPathIsUnrestricted(paths.get(2));

        whenPathsAreCopiedIfExists(paths);

        thenPathWasCopiedIfExists(paths.get(0));
        thenPathWasCopiedIfExists(paths.get(1));
        thenPathWasCopiedIfExists(paths.get(2));
    }

    @Test
    public void pathHasNoCopyRestrictionTest() {
        final Path path = createFile();

        givenThatPathIsUnrestricted(path);
        boolean hasRestriction = whenPathIsCheckedForCopyRestrictions(path);
        thenPathHasNoCopyRestrictions(hasRestriction);
    }

    @Test
    public void pathHasCopyRestrictionTest() {
        final Path path = createFile();

        givenThatPathIsRestricted(path);
        boolean hasRestriction = whenPathIsCheckedForCopyRestrictions(path);
        thenPathHasCopyRestrictions(hasRestriction);
    }

    @Test
    public void copyFileToAnotherDirectory() {
        final Path path = createFile();
        final String newName = "new-name";
        final Path targetDirectory = getAnotherDirectory();

        givenThatPathIsUnrestricted(path);
        whenPathIsCopiedToAnotherDirectory(path,
                                           newName,
                                           targetDirectory);
        thenPathWasCopiedToAnotherDirectory(path,
                                            newName,
                                            targetDirectory);
    }

    @Test
    public void copyFileToNullDirectory() {
        final Path path = createFile();
        final String newName = "new-name";

        givenThatPathIsUnrestricted(path);
        whenPathIsCopiedToAnotherDirectory(path,
                                           newName,
                                           null);
        thenPathWasCopiedToSameDirectory(path,
                                         newName);
    }

    private void givenThatPathIsRestricted(final Path path) {
        restrictedFileNames.add(path.getFileName());
    }

    private void givenThatPathIsUnrestricted(final Path path) {
        restrictedFileNames.remove(path.getFileName());
    }

    private void whenPathIsCopied(final Path path) {
        copyService.copy(path,
                         "newName",
                         "comment");
    }

    private void whenPathIsCopiedToAnotherDirectory(final Path path,
                                                    final String newName,
                                                    final Path targetDirectory) {
        copyService.copy(path,
                         newName,
                         targetDirectory,
                         "comment");
    }

    private void whenPathsAreCopiedIfExists(final Collection<Path> paths) {
        copyService.copyIfExists(paths,
                                 "newName",
                                 "comment");
    }

    private boolean whenPathIsCheckedForCopyRestrictions(final Path path) {
        return copyService.hasRestriction(path);
    }

    private void thenPathWasCopied(final Path path) {
        verify(copyService).copyPath(eq(path),
                                     any(String.class),
                                     any(Path.class),
                                     any(String.class));
        verify(resourceCopiedEvent).fire(any(ResourceCopiedEvent.class));
    }

    private void thenPathWasCopiedToAnotherDirectory(final Path path,
                                                     final String newName,
                                                     final Path targetDirectory) {
        Path targetPath = Paths.convert(Paths.convert(targetDirectory).resolve(newName + ".txt"));
        verify(copyService).copyPath(eq(path),
                                     any(String.class),
                                     eq(targetPath),
                                     any(String.class));
    }

    private void thenPathWasCopiedToSameDirectory(final Path path,
                                                  final String newName) {
        Path targetPath = Paths.convert(Paths.convert(path).getParent().resolve(newName + ".txt"));
        verify(copyService).copyPath(eq(path),
                                     any(String.class),
                                     eq(targetPath),
                                     any(String.class));
    }

    private void thenPathWasNotCopied(final Path path) {
        verify(copyService,
               never()).copyPath(eq(path),
                                 any(String.class),
                                 any(Path.class),
                                 any(String.class));
    }

    private void thenPathWasNotCopied(final Path path,
                                      final RuntimeException e) {
        assertEquals(path.toURI() + " cannot be copied.",
                     e.getMessage());
    }

    private void thenPathWasCopiedIfExists(final Path path) {
        verify(copyService).copyPathIfExists(eq(path),
                                             any(String.class),
                                             any(String.class));
    }

    private void thenPathWasNotCopiedIfExists(final Path path) {
        verify(copyService,
               never()).copyPathIfExists(eq(path),
                                         any(String.class),
                                         any(String.class));
    }

    private void thenPathWasNotCopiedIfExists(final Path path,
                                              final RuntimeException e) {
        assertEquals(path.toURI() + " cannot be copied.",
                     e.getMessage());
    }

    private void thenPathHasNoCopyRestrictions(final boolean hasRestriction) {
        assertFalse(hasRestriction);
    }

    private void thenPathHasCopyRestrictions(final boolean hasRestriction) {
        assertTrue(hasRestriction);
    }

    private Path createFile() {
        return createFile("file.txt");
    }

    private Path createFile(String fileName) {
        final Path path = PathFactory.newPath(fileName,
                                              PATH_PREFIX + "parent/" + fileName);
        fileSystemTestingUtils.getIoService().write(Paths.convert(path),
                                                    "content");
        return path;
    }

    private Path getAnotherDirectory() {
        return PathFactory.newPath("/",
                                   PATH_PREFIX + "new-parent/");
    }

    private void mockCopyRestrictors() {
        List<CopyRestrictor> copyRestrictors = new ArrayList<CopyRestrictor>();
        copyRestrictors.add(new CopyRestrictor() {
            @Override
            public PathOperationRestriction hasRestriction(final Path path) {
                if (restrictedFileNames.contains(path.getFileName())) {
                    return new PathOperationRestriction() {
                        @Override
                        public String getMessage(final Path path) {
                            return path.toURI() + " cannot be copied.";
                        }
                    };
                }

                return null;
            }
        });
        when(copyService.getCopyRestrictors()).thenReturn(copyRestrictors);
    }

}
