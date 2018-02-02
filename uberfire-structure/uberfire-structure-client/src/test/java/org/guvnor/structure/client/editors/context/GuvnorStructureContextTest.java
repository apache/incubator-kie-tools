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

package org.guvnor.structure.client.editors.context;

import static org.guvnor.structure.client.editors.TestUtil.makeRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.spaces.Space;

@RunWith(MockitoJUnitRunner.class)
public class GuvnorStructureContextTest {

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private WorkspaceProjectContext projContext;

    private GuvnorStructureContext context;
    private ArrayList<Repository> repositories;
    private Collection<Repository> result;
    private Callback<Collection<Repository>> callback;

    @Before
    public void setUp() throws Exception {
        callback = spy(new Callback<Collection<Repository>>() {
            @Override
            public void callback(final Collection<Repository> result) {
                GuvnorStructureContextTest.this.result = result;
            }
        });

        repositories = new ArrayList<>();

        repositories.add(makeRepository("my-repo",
                                        "master",
                                        "dev"));
        repositories.add(makeRepository("your-repo",
                                        "master",
                                        "release"));

        OrganizationalUnit ou = mock(OrganizationalUnit.class);
        when(ou.getName()).thenReturn("space");
        when(projContext.getActiveOrganizationalUnit()).thenReturn(Optional.of(ou));

        when(repositoryService.getRepositories(eq(new Space("space")))).thenReturn(repositories);

        context = new GuvnorStructureContext(new CallerMock<>(repositoryService), projContext);

        getRepositories();
    }

    @Test
    public void testLoad() throws Exception {
        assertEquals(2,
                     result.size());

        Collection<String> repositoryAliases = getRepositoryAliases();

        assertTrue(repositoryAliases.contains("my-repo"));
        assertTrue(repositoryAliases.contains("your-repo"));
    }

    @Test
    public void testReLoadRemembersBranches() throws Exception {

        context.changeBranch("your-repo",
                             "release");

        context.getRepositories(callback);

        assertEquals(2,
                     result.size());

        assertEquals("master",
                     context.getCurrentBranch("my-repo"));
        assertEquals("release",
                     context.getCurrentBranch("your-repo"));

        verify(callback,
               times(2)).callback(anyCollection());
    }

    @Test
    public void testReLoadPicksUpNewRepositories() throws Exception {

        repositories.add(makeRepository("my-new-repo",
                                        "master"));

        context.getRepositories(callback);

        assertEquals(3,
                     result.size());

        assertEquals("master",
                     context.getCurrentBranch("my-repo"));
        assertEquals("master",
                     context.getCurrentBranch("your-repo"));
        assertEquals("master",
                     context.getCurrentBranch("my-new-repo"));

        verify(callback,
               times(2)).callback(anyCollection());
    }

    @Test
    public void testReLoadPicksUpRemovedRepositories() throws Exception {

        repositories.remove(1);

        context.getRepositories(callback);

        assertEquals(1,
                     result.size());

        assertEquals("master",
                     context.getCurrentBranch("my-repo"));
        assertNull(context.getCurrentBranch("your-repo"));
    }

    @Test
    public void testReLoadPicksUpRemovedBranch() throws Exception {

        // This deletes master branch
        final List<Branch> branchList = new ArrayList<>();
        final Branch devBranch = new Branch("dev",
                                            mock(Path.class));
        branchList.add(devBranch);
        when(repositories.get(0).getBranches()).thenReturn(branchList);
        when(repositories.get(0).getDefaultBranch()).thenReturn(Optional.of(devBranch));

        context.getRepositories(callback);

        assertEquals("dev",
                     context.getCurrentBranch("my-repo"));
        assertEquals("master",
                     context.getCurrentBranch("your-repo"));
    }

    @Test
    public void testNewRepository() throws Exception {
        context.onNewRepository(new NewRepositoryEvent(makeRepository("new-repo",
                                                                      "master")));

        assertEquals("master",
                     context.getCurrentBranch("new-repo"));
    }

    @Test
    public void testRemoveRepository() throws Exception {
        context.onRepositoryRemoved(new RepositoryRemovedEvent(makeRepository("your-repo")));

        assertNull(context.getCurrentBranch("your-repo"));
    }

    private Collection<String> getRepositoryAliases() {
        Collection<String> repositoryAliases = new ArrayList<>();
        for (Repository repository : result) {
            repositoryAliases.add(repository.getAlias());
        }
        return repositoryAliases;
    }

    @Test
    public void testLoadDefaultBranches() throws Exception {
        for (Repository repository : result) {
            assertEquals("master",
                         context.getCurrentBranch(repository.getAlias()));
        }
    }

    @Test
    public void testChangeBranch() throws Exception {

        context.changeBranch("my-repo",
                             "dev");

        for (final Repository repository : result) {
            if (repository.getAlias().equals("my-repo")) {
                assertEquals("dev",
                             context.getCurrentBranch(repository.getAlias()));
            } else {
                assertEquals("master",
                             context.getCurrentBranch(repository.getAlias()));
            }
        }
    }

    private void getRepositories() {
        context.getRepositories(callback);

        verify(callback).callback(anyCollection());
    }
}