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

package org.guvnor.common.services.project.client.preferences;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.preferences.WorkbenchPreferenceScopeResolutionStrategies;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScopedResolutionStrategySupplierTest {

    @Mock
    private WorkbenchPreferenceScopeResolutionStrategies scopeResolutionStrategies;

    private ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier;

    private Project project;

    @Before
    public void setup() {
        projectScopedResolutionStrategySupplier = spy(new ProjectScopedResolutionStrategySupplier(scopeResolutionStrategies));

        project = mock(Project.class);
        doReturn("projectIdentifier").when(project).getIdentifier();
        doReturn("projectIdentifierEncoded").when(project).getEncodedIdentifier();
    }

    @Test
    public void getWithoutProjectTest() {
        final PreferenceScopeResolutionStrategyInfo preferenceScopeResolutionStrategyInfo = projectScopedResolutionStrategySupplier.get();

        verify(scopeResolutionStrategies).getUserInfoFor(null,
                                                         null);
    }

    @Test
    public void getWithProjectTest() {
        ProjectContextChangeEvent event = mock(ProjectContextChangeEvent.class);
        doReturn(project).when(event).getProject();

        projectScopedResolutionStrategySupplier.selectedProjectChanged(event);
        final PreferenceScopeResolutionStrategyInfo preferenceScopeResolutionStrategyInfo = projectScopedResolutionStrategySupplier.get();

        verify(scopeResolutionStrategies).getUserInfoFor("project",
                                                         "projectIdentifierEncoded");
    }
}