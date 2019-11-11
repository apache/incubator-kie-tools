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
import java.util.function.Function;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.marshalling.DMNImportTypesHelper;
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

    private DMNImportTypesHelper importTypesHelper;

    private DMNPathsHelperImpl pathsHelper;

    @Before
    public void setup() {
        importTypesHelper = new DMNImportTypesHelperImpl();
        pathsHelper = spy(new DMNPathsHelperImpl(refactoringQueryService,
                                                 importTypesHelper,
                                                 ioService));
    }

    @Test
    public void testGetModelsPathsWhenWorkspaceProjectIsNull() {
        doTestGetPathsWhenWorkspaceProjectIsNull(workspaceProject1 -> pathsHelper.getModelsPaths(null));
    }

    @Test
    public void testGetDMNModelsPathsWhenWorkspaceProjectIsNull() {
        doTestGetPathsWhenWorkspaceProjectIsNull(workspaceProject1 -> pathsHelper.getDMNModelsPaths(null));
    }

    @Test
    public void testGetPMMLDocumentsPathsWhenWorkspaceProjectIsNull() {
        doTestGetPathsWhenWorkspaceProjectIsNull(workspaceProject1 -> pathsHelper.getPMMLModelsPaths(null));
    }

    @SuppressWarnings("unchecked")
    private void doTestGetPathsWhenWorkspaceProjectIsNull(final Function<WorkspaceProject, List<Path>> paths) {
        final org.uberfire.java.nio.file.Path nioPath1 = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path nioPath2 = mock(org.uberfire.java.nio.file.Path.class);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final DirectoryStreamFake fakeStream = new DirectoryStreamFake(nioPath1, nioPath2);

        doReturn(fakeStream).when(pathsHelper).getStandaloneModelPaths(any(DirectoryStream.Filter.class));
        doReturn(path1).when(pathsHelper).convertPath(nioPath1);
        doReturn(path2).when(pathsHelper).convertPath(nioPath2);

        final List<Path> expectedPaths = asList(path1, path2);
        final List<Path> actualPaths = paths.apply(null);

        assertEquals(expectedPaths, actualPaths);
    }

    @Test
    public void testGetModelsPathsWhenWorkspaceProjectIsNotNull() {
        doTestGetPathsWhenWorkspaceProjectIsNotNull(workspaceProject -> pathsHelper.getModelsPaths(workspaceProject));
    }

    @Test
    public void testGetDMNModelsPathsWhenWorkspaceProjectIsNotNull() {
        doTestGetPathsWhenWorkspaceProjectIsNotNull(workspaceProject -> pathsHelper.getDMNModelsPaths(workspaceProject));
    }

    @Test
    public void testGetPMMLDocumentsPathsWhenWorkspaceProjectIsNotNull() {
        doTestGetPathsWhenWorkspaceProjectIsNotNull(workspaceProject -> pathsHelper.getPMMLModelsPaths(workspaceProject));
    }

    private void doTestGetPathsWhenWorkspaceProjectIsNotNull(final Function<WorkspaceProject, List<Path>> paths) {
        final Path rootPath = mock(Path.class);
        final String uri = "/src/path//project/root";
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

        final List<Path> result = paths.apply(workspaceProject);

        assertEquals(3, result.size());
        assertEquals(path1, result.get(0));
        assertEquals(path2, result.get(1));
        assertEquals(path3, result.get(2));
    }

    @Test
    public void testGetStandaloneModelPaths() {

        final org.uberfire.java.nio.file.Path root = mock(org.uberfire.java.nio.file.Path.class);
        final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter = (path) -> true;
        final DirectoryStream<org.uberfire.java.nio.file.Path> expectedStream = new DirectoryStreamFake();

        doReturn(root).when(pathsHelper).getStandaloneRootPath();
        doReturn(filter).when(pathsHelper).allModelsFilter();
        when(ioService.newDirectoryStream(root, filter)).thenReturn(expectedStream);

        final DirectoryStream<org.uberfire.java.nio.file.Path> actualStream = pathsHelper.getStandaloneModelPaths(filter);

        assertEquals(expectedStream, actualStream);
    }

    @Test
    public void testAllModelsFilter() {
        final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter = pathsHelper.allModelsFilter();
        final org.uberfire.java.nio.file.Path nioPath1 = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path nioPath2 = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path nioPath3 = mock(org.uberfire.java.nio.file.Path.class);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final Path path3 = mock(Path.class);

        when(path1.getFileName()).thenReturn("/Users/karreiro/projects/dmn-project/model.dmn");
        when(path2.getFileName()).thenReturn("/Users/karreiro/projects/dmn-project/document.pmml");
        when(path3.getFileName()).thenReturn("/Users/karreiro/projects/dmn-project/Readme.md");
        doReturn(path1).when(pathsHelper).convertPath(nioPath1);
        doReturn(path2).when(pathsHelper).convertPath(nioPath2);
        doReturn(path3).when(pathsHelper).convertPath(nioPath3);

        assertTrue(filter.accept(nioPath1));
        assertTrue(filter.accept(nioPath2));
        assertFalse(filter.accept(nioPath3));
    }

    @Test
    public void testDMNModelFilter() {
        final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter = pathsHelper.dmnModelFilter();
        final org.uberfire.java.nio.file.Path nioPath1 = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path nioPath2 = mock(org.uberfire.java.nio.file.Path.class);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);

        when(path1.getFileName()).thenReturn("/Users/karreiro/projects/dmn-project/file.dmn");
        when(path2.getFileName()).thenReturn("/Users/karreiro/projects/dmn-project/Readme.md");
        doReturn(path1).when(pathsHelper).convertPath(nioPath1);
        doReturn(path2).when(pathsHelper).convertPath(nioPath2);

        assertTrue(filter.accept(nioPath1));
        assertFalse(filter.accept(nioPath2));
    }

    @Test
    public void testPMMLDocumentFilter() {
        final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter = pathsHelper.pmmlDocumentFilter();
        final org.uberfire.java.nio.file.Path nioPath1 = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path nioPath2 = mock(org.uberfire.java.nio.file.Path.class);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);

        when(path1.getFileName()).thenReturn("/Users/karreiro/projects/dmn-project/file.pmml");
        when(path2.getFileName()).thenReturn("/Users/karreiro/projects/dmn-project/Readme.md");
        doReturn(path1).when(pathsHelper).convertPath(nioPath1);
        doReturn(path2).when(pathsHelper).convertPath(nioPath2);

        assertTrue(filter.accept(nioPath1));
        assertFalse(filter.accept(nioPath2));
    }

    @Test
    public void testGetStandaloneRootPath() {

        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path expectedPath = mock(org.uberfire.java.nio.file.Path.class);

        doReturn(path).when(pathsHelper).newPath(STANDALONE_FILE_NAME, STANDALONE_URI);
        doReturn(expectedPath).when(pathsHelper).convertPath(path);

        final org.uberfire.java.nio.file.Path actualPath = pathsHelper.getStandaloneRootPath();

        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void testGetRelativeURIWhenStandalone() {
        final Path includedModelPath = mock(Path.class);

        when(includedModelPath.getFileName()).thenReturn("file1");

        assertEquals("file1", pathsHelper.getRelativeURI(null, includedModelPath));
    }

    @Test
    public void testGetRelativeURIWhenSameLevel() {
        doTestGetRelativeURI("dmnModel.dmn",
                             "includedModel.dmn",
                             "../includedModel.dmn",
                             "includedModel.dmn");
    }

    @Test
    public void testGetRelativeURIWithForwardReference() {
        doTestGetRelativeURI("dmnModel.dmn",
                             "folder/includedModel.dmn",
                             "folder/includedModel.dmn",
                             "folder/includedModel.dmn");
    }

    @Test
    public void testGetRelativeURIWithBackwardReference() {
        doTestGetRelativeURI("folder/dmnModel.dmn",
                             "includedModel.dmn",
                             "../../includedModel.dmn",
                             "../includedModel.dmn");
    }

    // This test is of limited use as it mocks the real operations of org.uberfire.backend.vfs.Path.
    // However assuming the observed results of org.uberfire.backend.vfs.Paths operations are
    // correctly emulated here, we can test our code handles the responses correctly.
    private void doTestGetRelativeURI(final String dmnModelURI,
                                      final String includedModelURI,
                                      final String relativizedURI,
                                      final String relativeURI) {
        final Path dmnModelPath = mock(Path.class);
        final Path includedModelPath = mock(Path.class);
        final org.uberfire.java.nio.file.Path nioDMNModelPath = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path nioIncludedModelPath = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path nioRelativePath = mock(org.uberfire.java.nio.file.Path.class);

        when(dmnModelPath.getFileName()).thenReturn(dmnModelURI);
        when(includedModelPath.getFileName()).thenReturn(includedModelURI);
        when(nioDMNModelPath.relativize(nioIncludedModelPath)).thenReturn(nioRelativePath);
        when(nioRelativePath.toString()).thenReturn(relativizedURI);

        doReturn(dmnModelPath).when(pathsHelper).normalizePath(dmnModelPath);
        doReturn(includedModelPath).when(pathsHelper).normalizePath(includedModelPath);
        doReturn(nioDMNModelPath).when(pathsHelper).convertPath(dmnModelPath);
        doReturn(nioIncludedModelPath).when(pathsHelper).convertPath(includedModelPath);

        assertEquals(relativeURI, pathsHelper.getRelativeURI(dmnModelPath, includedModelPath));
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
