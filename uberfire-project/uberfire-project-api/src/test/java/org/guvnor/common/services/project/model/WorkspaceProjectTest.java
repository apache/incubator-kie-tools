/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.model;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WorkspaceProjectTest {

    @Test(expected = IllegalArgumentException.class)
    public void OUCanNotBeNull() throws Exception {
        new WorkspaceProject(null,
                             mock(Repository.class),
                             mock(Branch.class),
                             mock(Module.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ProjectCanNotBeNull() throws Exception {
        new WorkspaceProject(mock(OrganizationalUnit.class),
                             null,
                             mock(Branch.class),
                             mock(Module.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void BranchCanNotBeNull() throws Exception {
        new WorkspaceProject(mock(OrganizationalUnit.class),
                             mock(Repository.class),
                             null,
                             mock(Module.class));
    }

    @Test
    public void ModuleCanBeNull() throws Exception {
        try {
            new WorkspaceProject(mock(OrganizationalUnit.class),
                                 mock(Repository.class),
                                 mock(Branch.class),
                                 null);
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    public void getNameNoModule() throws Exception {
        final Repository repository = mock(Repository.class);
        doReturn("my-repo").when(repository).getAlias();

        final WorkspaceProject workspaceProject = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                       repository,
                                                                       mock(Branch.class),
                                                                       null);

        assertEquals("my-repo",
                     workspaceProject.getName());
    }

    @Test
    public void getName() throws Exception {
        final Repository repository = mock(Repository.class);
        doReturn("my-repo").when(repository).getAlias();

        final Module mainModule = mock(Module.class);
        doReturn("my-module").when(mainModule).getModuleName();

        final WorkspaceProject workspaceProject = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                       repository,
                                                                       mock(Branch.class),
                                                                       mainModule);

        assertEquals("my-module",
                     workspaceProject.getName());
    }

    @Test
    public void getNameNoModuleName() throws Exception {
        final Repository repository = mock(Repository.class);
        doReturn("my-repo").when(repository).getAlias();

        final Module mainModule = mock(Module.class);
        doReturn("").when(mainModule).getModuleName();

        final WorkspaceProject workspaceProject = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                       repository,
                                                                       mock(Branch.class),
                                                                       mainModule);

        assertEquals("my-repo",
                     workspaceProject.getName());
    }

    @Test
    public void getRootPath() throws Exception {
        final Branch branch = mock(Branch.class);
        final Path branchPath = mock(Path.class);
        doReturn(branchPath).when(branch).getPath();

        final WorkspaceProject workspaceProject = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                       mock(Repository.class),
                                                                       branch,
                                                                       mock(Module.class));

        assertEquals(branchPath,
                     workspaceProject.getRootPath());
    }

    @Test
    public void requiresRefresh() throws Exception {
        final WorkspaceProject workspaceProject = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                       mock(Repository.class),
                                                                       mock(Branch.class),
                                                                       mock(Module.class));

        assertTrue(workspaceProject.requiresRefresh());

        workspaceProject.markAsCached();

        assertFalse(workspaceProject.requiresRefresh());
    }
}