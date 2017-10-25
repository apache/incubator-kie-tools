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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mocks.CallerMock;

import static org.guvnor.structure.client.editors.TestUtil.makeRepository;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GuvnorStructureContextTest {

    @Mock
    private RepositoryService repositoryService;

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

        when(repositoryService.getRepositories()).thenReturn(repositories);

        context = new GuvnorStructureContext(new CallerMock<>(repositoryService));

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
        when(repositories.get(0).getBranches()).thenReturn(Arrays.asList("dev"));
        when(repositories.get(0).getDefaultBranch()).thenReturn("dev");

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