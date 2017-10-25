/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.registry.inmemory;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.source.Repository;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InMemorySourceRegistryTest {

    private static final String REPOSITORY_ID = "REPOSITORY_ID";

    private static final int REPOSITORIES_SIZE = 10;

    private InMemorySourceRegistry sourceRegistry;

    private Path path;
    private Repository repository;

    @Before
    public void setUp() {
        path = mock(Path.class);
        repository = mock(Repository.class);
        when(repository.getId()).thenReturn(REPOSITORY_ID);

        sourceRegistry = new InMemorySourceRegistry();
    }

    @Test
    public void testRegisterRepositorySources() {
        sourceRegistry.registerRepositorySources(path,
                                                 repository);
        List<Repository> result = sourceRegistry.getAllRepositories();
        assertTrue(result.contains(repository));
    }

    @Test
    public void testGetAllRepositories() {
        List<Repository> repositories = new ArrayList<>();
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < REPOSITORIES_SIZE; i++) {
            paths.add(mock(Path.class));
            Repository repository = mock(Repository.class);
            when(repository.getId()).thenReturn(REPOSITORY_ID + Integer.toString(i));
            repositories.add(repository);
        }

        for (int i = 0; i < REPOSITORIES_SIZE; i++) {
            sourceRegistry.registerRepositorySources(paths.get(i),
                                                     repositories.get(i));
        }

        List<Repository> result = sourceRegistry.getAllRepositories();
        assertEquals(repositories.size(),
                     result.size());
        for (Repository repository : repositories) {
            assertTrue(result.contains(repository));
        }
    }
}
