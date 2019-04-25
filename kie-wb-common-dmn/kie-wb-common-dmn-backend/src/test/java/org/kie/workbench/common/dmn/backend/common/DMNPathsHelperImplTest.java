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

package org.kie.workbench.common.dmn.backend.common;

import java.util.Iterator;
import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.resource.DMNDefinitionSetResourceType;
import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.paging.PageResponse;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.backend.common.DMNPathsHelperImpl.STANDALONE_FILE_NAME;
import static org.kie.workbench.common.dmn.backend.common.DMNPathsHelperImpl.STANDALONE_URI;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNPathsHelperImplTest {

    @Mock
    private RefactoringQueryServiceImpl refactoringQueryService;

    @Mock
    private IOService ioService;

    @Mock
    private PageResponse<RefactoringPageRow> pageResponse;

    @Mock
    private WorkspaceProject workspaceProject;

    private DMNDefinitionSetResourceType resourceType;

    private DMNPathsHelperImpl helper;

    @Before
    public void setup() {
        resourceType = new DMNDefinitionSetResourceType();
        helper = spy(new DMNPathsHelperImpl(refactoringQueryService, resourceType, ioService));
    }

    @Test
    public void testGetDiagramsPathsWhenWorkspaceProjectIsNull() {

        final org.uberfire.java.nio.file.Path nioPath1 = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path nioPath2 = mock(org.uberfire.java.nio.file.Path.class);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final DirectoryStreamFake fakeStream = new DirectoryStreamFake(nioPath1, nioPath2);

        doReturn(fakeStream).when(helper).getDMNPaths();
        doReturn(path1).when(helper).convertPath(nioPath1);
        doReturn(path2).when(helper).convertPath(nioPath2);

        final List<Path> expectedPaths = asList(path1, path2);
        final List<Path> actualPaths = helper.getDiagramsPaths(null);

        assertEquals(expectedPaths, actualPaths);
    }

    @Test
    public void testGetDiagramsPathsWhenWorkspaceProjectIsNotNull() {

        final Path rootPath = mock(Path.class);
        final String uri = "/src/path/file.dmn";
        final RefactoringPageRow row1 = mock(RefactoringPageRow.class);
        final RefactoringPageRow row2 = mock(RefactoringPageRow.class);
        final RefactoringPageRow row3 = mock(RefactoringPageRow.class);
        final List<RefactoringPageRow> rows = asList(row1, row2, row3);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final Path path3 = mock(Path.class);

        when(workspaceProject.getRootPath()).thenReturn(rootPath);
        when(rootPath.toURI()).thenReturn(uri);
        when(refactoringQueryService.query(any(RefactoringPageRequest.class))).thenReturn(pageResponse);
        when(pageResponse.getPageRowList()).thenReturn(rows);
        when(row1.getValue()).thenReturn(path1);
        when(row2.getValue()).thenReturn(path2);
        when(row3.getValue()).thenReturn(path3);

        final List<Path> paths = helper.getDiagramsPaths(workspaceProject);

        assertEquals(3, paths.size());
        assertEquals(path1, paths.get(0));
        assertEquals(path2, paths.get(1));
        assertEquals(path3, paths.get(2));
    }

    @Test
    public void testGetDMNPaths() {

        final org.uberfire.java.nio.file.Path root = mock(org.uberfire.java.nio.file.Path.class);
        final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter = (path) -> true;
        final DirectoryStream<org.uberfire.java.nio.file.Path> expectedStream = new DirectoryStreamFake();

        doReturn(root).when(helper).getStandaloneRootPath();
        doReturn(filter).when(helper).dmnAssetsFilter();
        when(ioService.newDirectoryStream(root, filter)).thenReturn(expectedStream);

        final DirectoryStream<org.uberfire.java.nio.file.Path> actualStream = helper.getDMNPaths();

        assertEquals(expectedStream, actualStream);
    }

    @Test
    public void testDMNAssetsFilter() {

        final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter = helper.dmnAssetsFilter();
        final org.uberfire.java.nio.file.Path nioPath1 = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path nioPath2 = mock(org.uberfire.java.nio.file.Path.class);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);

        when(path1.getFileName()).thenReturn("/Users/karreiro/projects/dmn-project/file.dmn");
        when(path2.getFileName()).thenReturn("/Users/karreiro/projects/dmn-project/Readme.md");
        doReturn(path1).when(helper).convertPath(nioPath1);
        doReturn(path2).when(helper).convertPath(nioPath2);

        assertTrue(filter.accept(nioPath1));
        assertFalse(filter.accept(nioPath2));
    }

    @Test
    public void testGetStandaloneRootPath() {

        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path expectedPath = mock(org.uberfire.java.nio.file.Path.class);

        doReturn(path).when(helper).newPath(STANDALONE_FILE_NAME, STANDALONE_URI);
        doReturn(expectedPath).when(helper).convertPath(path);

        final org.uberfire.java.nio.file.Path actualPath = helper.getStandaloneRootPath();

        assertEquals(expectedPath, actualPath);
    }

    class DirectoryStreamFake implements DirectoryStream<org.uberfire.java.nio.file.Path> {

        private List<org.uberfire.java.nio.file.Path> paths;

        DirectoryStreamFake(final org.uberfire.java.nio.file.Path... paths) {
            this.paths = asList(paths);
        }

        @Override
        public Iterator<org.uberfire.java.nio.file.Path> iterator() {
            return paths.iterator();
        }

        @Override
        public void close() throws IOException {
            // empty.
        }
    }
}
