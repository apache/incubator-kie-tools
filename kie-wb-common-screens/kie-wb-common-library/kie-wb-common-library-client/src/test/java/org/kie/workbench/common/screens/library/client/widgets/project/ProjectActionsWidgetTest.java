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
 */

package org.kie.workbench.common.screens.library.client.widgets.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectActionsWidgetTest {

    @Mock
    private BuildExecutor buildExecutor;

    @Spy
    @InjectMocks
    private ProjectActionsWidget projectActionsWidget;

    @Test
    public void canCompileProject() {
        doReturn(true).when(projectActionsWidget).userCanBuildProject();

        projectActionsWidget.compileProject();

        verify(buildExecutor).triggerBuild();
    }

    @Test
    public void cannotCompileProject() {
        doReturn(false).when(projectActionsWidget).userCanBuildProject();

        projectActionsWidget.compileProject();

        verify(buildExecutor,
               never()).triggerBuild();
    }

    @Test
    public void canBuildAndDeploy() {
        doReturn(true).when(projectActionsWidget).userCanBuildProject();

        projectActionsWidget.buildAndDeployProject();

        verify(buildExecutor).triggerBuildAndDeploy();
    }

    @Test
    public void cannotBuildAndDeploy() {
        doReturn(false).when(projectActionsWidget).userCanBuildProject();

        projectActionsWidget.buildAndDeployProject();

        verify(buildExecutor,
               never()).triggerBuildAndDeploy();
    }
}
