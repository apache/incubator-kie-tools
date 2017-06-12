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

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectsDetailScreenTest {

    @Mock
    private ProjectsDetailScreen.View view;

    private ProjectsDetailScreen projectsDetail;

    @Before
    public void setup() {
        projectsDetail = new ProjectsDetailScreen(view);
    }

    @Test
    public void testUpdate() throws Exception {
        final POM pom = mock(POM.class);
        when(pom.getDescription()).thenReturn("desc");

        final Project project = mock(Project.class);
        doReturn(pom).when(project).getPom();

        final ProjectInfo projectInfo = mock(ProjectInfo.class);
        when(projectInfo.getProject()).thenReturn(project);

        ProjectDetailEvent event = new ProjectDetailEvent(projectInfo);

        projectsDetail.update(event);

        verify(view).update("desc");
    }
}