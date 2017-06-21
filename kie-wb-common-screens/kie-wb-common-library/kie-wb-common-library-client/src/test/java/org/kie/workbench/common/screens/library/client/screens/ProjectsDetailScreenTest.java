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
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ProjectMetricsFactory;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

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
    private Project project;

    @Mock
    private ProjectInfo projectInfo;

    private ProjectDetailEvent projectDetailEvent;
    private ProjectsDetailScreen projectsDetail;

    @Before
    public void setup() {
        when(pom.getDescription()).thenReturn("desc");
        doReturn(pom).when(project).getPom();
        when(projectInfo.getProject() ).thenReturn(project);
        when(projectMetricsFactory.lookupCommitsOverTimeDisplayer_small(any())).thenReturn(contributionsDisplayer);

        projectDetailEvent = new ProjectDetailEvent(projectInfo);
        projectsDetail = new ProjectsDetailScreen(view, projectMetricsFactory, libraryPlaces);
        projectsDetail.update(projectDetailEvent);
    }

    @Test
    public void testInit() throws Exception {
        verify(view).init(projectsDetail);
    }

    @Test
    public void testViewMetrics() throws Exception {
        projectsDetail.gotoProjectMetrics();
        verify(libraryPlaces).goToProjectMetrics(projectInfo);
    }

    @Test
    public void testUpdate() throws Exception {
        verify(view).updateDescription("desc");
        verify(view).updateContributionsMetric(contributionsDisplayer);
        verify(contributionsDisplayer ).draw();
    }
}