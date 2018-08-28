/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.kie.workbench.common.screens.library.client.util;

import java.util.List;
import java.util.stream.Collectors;

import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.hamcrest.CoreMatchers;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectMetricsFactoryTest {

    private ProjectMetricsFactory projectMetricsFactory;

    @Mock
    private DisplayerLocator displayerLocator;

    @Mock
    private TranslationService ts;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorkspaceProject workspaceProject;

    @Before
    public void setUp() {
        this.projectMetricsFactory = new ProjectMetricsFactory(ts,
                                                               displayerLocator);
    }

    @Test
    public void testCreateProjectFilter() {
        when(workspaceProject.getName()).thenReturn("project1");
        when(workspaceProject.getOrganizationalUnit().getName()).thenReturn("mySpace");
        when(workspaceProject.getRepository().getAlias()).thenReturn("alias");

        LogicalExprFilter filter = (LogicalExprFilter) this.projectMetricsFactory.createProjectFilter(workspaceProject);
        List<String> filters = filter.getLogicalTerms()
                .stream()
                .map(e -> {
                    CoreFunctionFilter f = ((CoreFunctionFilter) e);
                    return f.getColumnId() + "=" + f.getParameters().get(0);
                })
                .collect(Collectors.toList());

        assertThat(filters.size(),
                   CoreMatchers.is(3));

        assertThat(filters,
                   CoreMatchers.hasItems("project=project1",
                                         "organization=mySpace",
                                         "repository=alias"));
    }
}