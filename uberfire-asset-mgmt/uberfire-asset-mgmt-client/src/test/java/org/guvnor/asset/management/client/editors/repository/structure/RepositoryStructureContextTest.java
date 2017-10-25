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

package org.guvnor.asset.management.client.editors.repository.structure;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryStructureContextTest {

    private RepositoryStructureContext context;

    @Mock
    Repository currentRepository;

    @Mock
    Project currentProject;

    @Before
    public void setUp() throws Exception {
        context = new RepositoryStructureContext();
    }

    @Test
    public void testEmptySetupProjectChanged() throws Exception {
        assertTrue(context.activeProjectChanged(new Project()));
    }

    @Test
    public void testEmptySetupRepositoryChanged() throws Exception {
        assertTrue(context.repositoryOrBranchChanged(mock(Repository.class),
                                                     "master"));
    }

    @Test
    public void testProjectChanged() throws Exception {
        context.reset(currentRepository,
                      "master",
                      currentProject);

        assertTrue(context.activeProjectChanged(new Project()));
    }

    @Test
    public void testRepositoryChanged() throws Exception {
        context.reset(currentRepository,
                      "master",
                      currentProject);

        assertTrue(context.repositoryOrBranchChanged(mock(Repository.class),
                                                     "master"));
    }

    @Test
    public void testBranchChanged() throws Exception {
        context.reset(currentRepository,
                      "master",
                      currentProject);

        assertTrue(context.repositoryOrBranchChanged(currentRepository,
                                                     "dev"));
    }
}