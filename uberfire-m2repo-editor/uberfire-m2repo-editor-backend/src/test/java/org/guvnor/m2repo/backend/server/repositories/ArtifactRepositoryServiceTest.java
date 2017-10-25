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
 *
 */

package org.guvnor.m2repo.backend.server.repositories;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.MockInstanceImpl;

import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactRepositoryServiceTest {

    private ArtifactRepositoryService artifactRepositoryService;
    private ArtifactRepository artifactRepository1;
    private ArtifactRepository artifactRepository2;
    private ArtifactRepository artifactRepository3;
    private ArtifactRepository artifactRepository4;
    private ArtifactRepository artifactRepository5;

    @Before
    public void setUp() {

        artifactRepository1 = mock(ArtifactRepository.class);
        artifactRepository2 = mock(ArtifactRepository.class);
        artifactRepository3 = mock(ArtifactRepository.class);
        artifactRepository4 = mock(ArtifactRepository.class);
        artifactRepository5 = mock(ArtifactRepository.class);

        when(artifactRepository1.isRepository()).thenReturn(true);
        when(artifactRepository2.isRepository()).thenReturn(true);
        when(artifactRepository3.isRepository()).thenReturn(true);
        when(artifactRepository4.isRepository()).thenReturn(true);
        when(artifactRepository5.isRepository()).thenReturn(false);

        when(artifactRepository1.isPomRepository()).thenReturn(true);
        when(artifactRepository2.isPomRepository()).thenReturn(true);
        when(artifactRepository3.isPomRepository()).thenReturn(false);
        when(artifactRepository4.isPomRepository()).thenReturn(false);
        when(artifactRepository5.isPomRepository()).thenReturn(false);

        MockInstanceImpl<ArtifactRepository> instance = new MockInstanceImpl<>(artifactRepository1,
                                                                               artifactRepository2,
                                                                               artifactRepository3,
                                                                               artifactRepository4,
                                                                               artifactRepository5);

        this.artifactRepositoryService = new ArtifactRepositoryService(instance);
    }

    @Test
    public void testDeploymentRepositories() {
        List<? extends ArtifactRepository> repositories = this.artifactRepositoryService.getRepositories();
        assertEquals(4,
                     repositories.size());
        assertTrue(Arrays.asList(this.artifactRepository1,
                                 this.artifactRepository2,
                                 this.artifactRepository3,
                                 this.artifactRepository4).containsAll(repositories));
        assertFalse(repositories.contains(this.artifactRepository5));
    }

    @Test
    public void testPomRepositories() {
        List<? extends ArtifactRepository> repositories = this.artifactRepositoryService.getPomRepositories();
        assertEquals(2,
                     repositories.size());
        assertTrue(Arrays.asList(this.artifactRepository1,
                                 this.artifactRepository2).containsAll(repositories));
        assertFalse(repositories.contains(this.artifactRepository3));
        assertFalse(repositories.contains(this.artifactRepository4));
        assertFalse(repositories.contains(this.artifactRepository5));
    }
}