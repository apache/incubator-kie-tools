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
package org.kie.workbench.common.screens.projecteditor.client.forms.repositories;

import java.util.HashSet;
import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class RepositoriesWidgetViewTest {

    @Mock
    private User identity;

    private RepositoriesWidgetViewImpl view;
    private RepositoriesWidgetPresenter presenter;

    private ModuleRepositories.ModuleRepository repository = new ModuleRepositories.ModuleRepository(true,
                                                                                                     new MavenRepositoryMetadata("id",
                                                                                                                                 "url",
                                                                                                                                 MavenRepositorySource.LOCAL));
    private Set<ModuleRepositories.ModuleRepository> repositories;

    @Before
    public void setup() {
        view = new RepositoriesWidgetViewImpl();
        presenter = new RepositoriesWidgetPresenter(identity,
                                                    view);
        repositories = new HashSet<ModuleRepositories.ModuleRepository>();
        repositories.add(repository);
    }

    @Test
    public void testRepositoryInclusion() {
        view.repositoryIncludeColumn.getFieldUpdater().update(0,
                                                              repository,
                                                              true);

        assertEquals(true,
                     repository.isIncluded());
    }

    @Test
    public void testRepositoryExclusion() {
        view.repositoryIncludeColumn.getFieldUpdater().update(0,
                                                              repository,
                                                              false);

        assertEquals(false,
                     repository.isIncluded());
    }
}
