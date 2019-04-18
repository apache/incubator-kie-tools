/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
 */

package org.kie.workbench.common.screens.library.client.screens.project.actions;

import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.shared.test.TestRunnerService;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProjectMainActionsTest {

    @Mock
    TestRunnerService testRunnerService;
    @Mock
    WorkspaceProjectContext workspaceProjectContext;
    @Mock
    PlaceManager placeManager;
    @Mock
    User user;
    @Captor
    ArgumentCaptor<PlaceRequest> placeRequestArgumentCaptor;
    @Mock
    private ProjectMainActionsView view;
    @Mock
    private BuildExecutor executor;
    @Mock
    private TranslationService ts;
    private ProjectMainActions presenter;

    @Before
    public void init() {
        presenter = new ProjectMainActions(executor,
                                           view,
                                           new CallerMock<>(testRunnerService),
                                           workspaceProjectContext,
                                           placeManager,
                                           ts,
                                           user);

        presenter.init();

        verify(view).init(eq(presenter));

        presenter.getElement();

        verify(view).getElement();
    }

    @Test
    public void testBuildDropdown() {
        presenter.setBuildEnabled(false);

        verify(view).setBuildDropDownEnabled(eq(false));

        presenter.triggerBuild();
        verify(executor, never()).triggerBuild();

        presenter.triggerBuildAndInstall();
        verify(executor, never()).triggerBuildAndInstall();

        presenter.setBuildEnabled(true);

        verify(view).setBuildDropDownEnabled(eq(true));

        presenter.triggerBuild();
        verify(executor).triggerBuild();

        presenter.triggerBuildAndInstall();
        verify(executor).triggerBuildAndInstall();
    }

    @Test
    public void testDeployDropdownWithRedeploy() {
        presenter.setDeployEnabled(false);
        presenter.setRedeployEnabled(true);

        verify(view).setBuildAndDeployDropDownEnabled(eq(false));
        verify(view).setRedeployEnabled(eq(true));

        presenter.triggerBuildAndDeploy();
        verify(executor, never()).triggerBuildAndDeploy();

        presenter.triggerRedeploy();
        verify(executor, never()).triggerRedeploy();

        presenter.setDeployEnabled(true);
        presenter.setRedeployEnabled(true);

        verify(view).setBuildAndDeployDropDownEnabled(eq(true));
        verify(view, times(2)).setRedeployEnabled(eq(true));

        presenter.triggerBuildAndDeploy();
        verify(executor).triggerBuildAndDeploy();

        presenter.triggerRedeploy();
        verify(executor).triggerRedeploy();
    }

    @Test
    public void testDeployDropdownWithoutRedeploy() {
        presenter.setDeployEnabled(false);
        presenter.setRedeployEnabled(false);

        verify(view).setBuildAndDeployDropDownEnabled(eq(false));
        verify(view).setRedeployEnabled(eq(false));

        presenter.triggerBuildAndDeploy();
        verify(executor, never()).triggerBuildAndDeploy();

        presenter.triggerRedeploy();
        verify(executor, never()).triggerRedeploy();

        presenter.setDeployEnabled(true);
        presenter.setRedeployEnabled(false);

        verify(view).setBuildAndDeployDropDownEnabled(eq(true));
        verify(view, times(2)).setRedeployEnabled(eq(false));

        presenter.triggerBuildAndDeploy();
        verify(executor).triggerBuildAndDeploy();

        presenter.triggerRedeploy();
        verify(executor, never()).triggerRedeploy();
    }

    @Test
    public void testRunTestNoActiveModule() {
        doReturn(Optional.empty()).when(workspaceProjectContext).getActiveModule();

        presenter.onRunTest();

        verify(view, never()).showBusyIndicator(any());
        verify(placeManager, never()).goTo(any(PlaceRequest.class));
        verify(testRunnerService, never()).runAllTests(anyString(), any());
    }

    @Test
    public void testRunTest() {
        final Module module = mock(Module.class);
        final Path path = mock(Path.class);
        doReturn(path).when(module).getRootPath();
        doReturn(Optional.of(module)).when(workspaceProjectContext).getActiveModule();
        doReturn("mr. Trout").when(user).getIdentifier();

        presenter.onRunTest();

        verify(view).showBusyIndicator(any());
        verify(view).hideBusyIndicator();
        verify(placeManager).goTo(placeRequestArgumentCaptor.capture());
        verify(testRunnerService).runAllTests("mr. Trout", path);
        assertEquals("org.kie.guvnor.TestResults", placeRequestArgumentCaptor.getValue().getIdentifier());
    }
}
