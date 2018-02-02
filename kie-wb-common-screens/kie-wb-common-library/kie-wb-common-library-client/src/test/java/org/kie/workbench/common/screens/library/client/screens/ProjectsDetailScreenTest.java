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
package org.kie.workbench.common.screens.library.client.screens;

import org.dashbuilder.displayer.client.Displayer;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ProjectMetricsFactory;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ProjectsDetailScreenTest {

    @Mock
    private ProjectsDetailScreen.View view;

    @Mock
    private ProjectMetricsFactory projectMetricsFactory;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private Displayer contributionsDisplayer;

    @Mock
    private POM pom;

    @Mock
    private WorkspaceProject project;

    @Mock
    private WorkspaceProjectContext projectContext;

    private ProjectDetailEvent projectDetailEvent;
    private ProjectsDetailScreen projectsDetail;

    @Before
    public void setup() {
        when(pom.getDescription()).thenReturn("desc");
        final Module module = mock(Module.class);
        doReturn(module).when(project).getMainModule();
        doReturn(pom).when(module).getPom();

        when(projectMetricsFactory.lookupCommitsOverTimeDisplayer_small(any())).thenReturn(contributionsDisplayer);

        when(projectContext.getActiveWorkspaceProject()).thenReturn(Optional.of(project));

        projectDetailEvent = new ProjectDetailEvent(project);
        projectsDetail = new ProjectsDetailScreen(view,
                                                  projectMetricsFactory,
                                                  libraryPlaces,
                                                  projectContext);
    }

    @Test
    public void testInit() throws Exception {
        projectsDetail.update(projectDetailEvent);
        verify(view).init(projectsDetail);
    }

    @Test
    public void testViewMetrics() throws Exception {
        projectsDetail.update(projectDetailEvent);
        projectsDetail.gotoProjectMetrics();
        verify(libraryPlaces).goToProjectMetrics();
    }

    @Test
    public void testUpdate() throws Exception {
        projectsDetail.update(projectDetailEvent);
        verify(view).updateDescription("desc");
        verify(view).updateContributionsMetric(contributionsDisplayer);
        verify(contributionsDisplayer).draw();
    }

    @Test
    public void testUpdateNullDescription() throws Exception {
        when(pom.getDescription()).thenReturn(null);
        projectDetailEvent = new ProjectDetailEvent(project);

        projectsDetail.update(projectDetailEvent);

        verify(view).updateDescription("");
        verify(view).updateContributionsMetric(contributionsDisplayer);
        verify(contributionsDisplayer).draw();
    }
}