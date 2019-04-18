/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.refactoring.backend.server.query;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllLibraryAssetsQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryValueFileExtensionIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryValueRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FileLoaderTest {

    @Captor
    ArgumentCaptor<Set<ValueIndexTerm>> termsCaptor;
    @Mock
    private RefactoringQueryService refactoringQueryService;
    @InjectMocks
    private FileLoader fileLoader;
    private SimpleFileSystemProvider fileSystemProvider;

    @Before
    public void setUp() throws Exception {
        fileSystemProvider = new SimpleFileSystemProvider();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullPath() {
        fileLoader.loadPaths(null, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSuffix() {
        fileLoader.loadPaths(mock(Path.class), null);
    }

    @Test
    public void testPathDoesNotExist() {

        final ArrayList<RefactoringPageRow> rows = new ArrayList<>();
        doReturn(rows).when(refactoringQueryService).query(eq(FindAllLibraryAssetsQuery.NAME),
                                                           anySet());

        final org.uberfire.java.nio.file.Path root = fileSystemProvider.getPath(URI.create("default://master@myRepository/ThisDoesNotExists"));

        final List<Path> list = fileLoader.loadPaths(Paths.convert(root), "txt");

        assertTrue(list.isEmpty());

        verify(refactoringQueryService, never()).query(eq(FindAllLibraryAssetsQuery.NAME),
                                                       anySet());
    }

    @Test
    public void testCorrectTerms() {

        final ArrayList<RefactoringPageRow> rows = new ArrayList<>();
        doReturn(rows).when(refactoringQueryService).query(eq(FindAllLibraryAssetsQuery.NAME),
                                                           anySet());

        final org.uberfire.java.nio.file.Path root = fileSystemProvider.getPath(URI.create("default://master@myRepository"));

        final List<Path> list = fileLoader.loadPaths(Paths.convert(root), "txt");

        assertTrue(list.isEmpty());

        verify(refactoringQueryService).query(eq(FindAllLibraryAssetsQuery.NAME),
                                              termsCaptor.capture());
        final Set<ValueIndexTerm> terms = termsCaptor.getValue();

        assertEquals(2, terms.size());

        testLibraryValueFileExtensionIndexTerm(terms);
        testLibraryValueRepositoryRootIndexTerm(terms);
    }

    @Test
    public void testResult() {
        Path path1 = mock(Path.class);
        Path path2 = mock(Path.class);

        final ArrayList<RefactoringPageRow> rows = new ArrayList<>();
        rows.add(new RefactoringPageRow() {
            @Override
            public Object getValue() {
                return path1;
            }
        });
        rows.add(new RefactoringPageRow() {
            @Override
            public Object getValue() {
                return path2;
            }
        });
        doReturn(rows).when(refactoringQueryService).query(eq(FindAllLibraryAssetsQuery.NAME),
                                                           anySet());

        final List<Path> list = fileLoader.loadPaths(Paths.convert(fileSystemProvider.getPath(URI.create("default://master@myRepository"))),
                                                     "txt");

        assertEquals(2, list.size());
        assertEquals(path1, list.get(0));
        assertEquals(path2, list.get(1));
    }

    private void testLibraryValueFileExtensionIndexTerm(final Set<ValueIndexTerm> terms) {

        for (final ValueIndexTerm term : terms) {
            if (term instanceof LibraryValueFileExtensionIndexTerm) {
                final LibraryValueFileExtensionIndexTerm libraryValueFileExtensionIndexTerm = (LibraryValueFileExtensionIndexTerm) term;
                assertEquals(".*(txt)", libraryValueFileExtensionIndexTerm.getValue());
                return;
            }
        }

        fail("LibraryValueFileExtensionIndexTerm was not found");
    }

    private void testLibraryValueRepositoryRootIndexTerm(final Set<ValueIndexTerm> terms) {

        for (final ValueIndexTerm term : terms) {
            if (term instanceof LibraryValueRepositoryRootIndexTerm) {
                final LibraryValueRepositoryRootIndexTerm libraryValueRepositoryRootIndexTerm = (LibraryValueRepositoryRootIndexTerm) term;
                assertTrue(libraryValueRepositoryRootIndexTerm.getValue().endsWith("kie-wb-common-services/kie-wb-common-refactoring/kie-wb-common-refactoring-backend"));
                return;
            }
        }

        fail("LibraryValueRepositoryRootIndexTerm was not found");
    }
}