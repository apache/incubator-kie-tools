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
import java.util.Set;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindScorecardNamesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringStringPageRow;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScoreCardServiceLoaderImplTest {

    @Captor
    ArgumentCaptor<Set<ValueIndexTerm>> termsCaptor;
    @Mock
    private RefactoringQueryService refactoringQueryService;
    @Mock
    private WorkspaceProjectService projectService;
    @InjectMocks
    private ScoreCardServiceLoaderImpl scoreCardServiceLoader;
    private SimpleFileSystemProvider fileSystemProvider;

    @Before
    public void setUp() throws Exception {
        fileSystemProvider = new SimpleFileSystemProvider();
    }

    @Test
    public void find() {

        final org.uberfire.java.nio.file.Path root = fileSystemProvider.getPath(URI.create("default://master@myRepository/Test"));

        final Path path = Paths.convert(root);

        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        final Module module = mock(Module.class);
        doReturn(module).when(workspaceProject).getMainModule();
        doReturn(workspaceProject).when(projectService).resolveProject(path);
        doReturn(path).when(module).getRootPath();

        final ArrayList<RefactoringPageRow> rows = new ArrayList<>();
        final RefactoringStringPageRow row1 = new RefactoringStringPageRow();
        row1.setValue("row1");
        rows.add(row1);
        final RefactoringStringPageRow row2 = new RefactoringStringPageRow();
        row2.setValue("row2");
        rows.add(row2);

        doReturn(rows).when(refactoringQueryService).query(eq(FindScorecardNamesQuery.NAME),
                                                           termsCaptor.capture());

        final Set<String> result = scoreCardServiceLoader.find(path, "org.test");

        assertTerms(termsCaptor.getValue());

        assertEquals(2, result.size());
        assertTrue(result.contains("row1"));
        assertTrue(result.contains("row2"));
    }

    @Test
    public void noProject() {

        final org.uberfire.java.nio.file.Path root = fileSystemProvider.getPath(URI.create("default://master@myRepository/Test"));

        final Path path = Paths.convert(root);

        doThrow(new IllegalArgumentException()).when(projectService).resolveProject(path);

        final Set<String> result = scoreCardServiceLoader.find(path, "org.test");

        verify(refactoringQueryService, never()).query(eq(FindScorecardNamesQuery.NAME),
                                                       any());

        assertEquals(0, result.size());
    }


    private void assertTerms(Set<ValueIndexTerm> terms) {

        assertEquals(3, terms.size());

        boolean foundValueSharedPartIndexTerm = false;
        boolean foundValueModuleRootPathIndexTerm = false;
        boolean foundValuePackageNameIndexTerm = false;

        for (ValueIndexTerm term : terms) {

            if (term instanceof ValueSharedPartIndexTerm) {
                foundValueSharedPartIndexTerm = true;
                final ValueSharedPartIndexTerm sharedPartIndexTerm = (ValueSharedPartIndexTerm) term;

                assertEquals("*", sharedPartIndexTerm.getValue());
            } else if (term instanceof ValueModuleRootPathIndexTerm) {
                foundValueModuleRootPathIndexTerm = true;
                final ValueModuleRootPathIndexTerm moduleRootPathIndexTerm = (ValueModuleRootPathIndexTerm) term;

                assertEquals("file:///Test", moduleRootPathIndexTerm.getValue());
            } else if (term instanceof ValuePackageNameIndexTerm) {
                foundValuePackageNameIndexTerm = true;
                final ValuePackageNameIndexTerm packageNameIndexTerm = (ValuePackageNameIndexTerm) term;

                assertEquals("org.test", packageNameIndexTerm.getValue());
            }
        }

        assertTrue(foundValueSharedPartIndexTerm);
        assertTrue(foundValueModuleRootPathIndexTerm);
        assertTrue(foundValuePackageNameIndexTerm);
    }
}