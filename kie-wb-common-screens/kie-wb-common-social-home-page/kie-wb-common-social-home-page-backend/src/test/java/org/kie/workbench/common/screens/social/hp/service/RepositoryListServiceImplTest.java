/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.social.hp.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.social.hp.security.SocialEventRepositoryConstraint;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.spaces.Space;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryListServiceImplTest {

    RepositoryListServiceImpl service = new RepositoryListServiceImpl();

    @Before
    public void setup() {
        Set<Repository> repositories = new HashSet<Repository>();
        repositories.add(new GitRepository("dora",
                                           new Space("space")));
        final SocialEventRepositoryConstraint socialEventRepositoryConstraint = mock(
                SocialEventRepositoryConstraint.class);
        when(socialEventRepositoryConstraint.getAuthorizedRepositories()).thenReturn(repositories);
        service.repositoryConstraint = socialEventRepositoryConstraint;
    }

    @Test
    public void getRepositories() {
        final List<String> repositories = service.getRepositories();
        assertEquals(1,
                     repositories.size());
        assertEquals("dora",
                     repositories.get(0));
    }
}