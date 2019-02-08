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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.deploy;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.junit.Test;
import org.kie.server.api.model.KieServerMode;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.AbstractExecutorTest;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources.CONSTANTS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public abstract class AbstractBuildAndDeployExecutorTest<RUNNER extends AbstractBuildAndDeployExecutor> extends AbstractExecutorTest<RUNNER> {

    protected abstract KieServerMode getPreferredKieServerMode();

    @Test
    public void testBuildAndDeployWithoutServerTemplate() {
        runner.run(context);

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());
        verifyNotification(ProjectEditorResources.CONSTANTS.BuildSuccessful(), NotificationEvent.NotificationType.SUCCESS);
        verifyNotification(ProjectEditorResources.CONSTANTS.DeploymentSkippedDueToNoServerTemplateConfiguredForMode(getPreferredKieServerMode().name().toLowerCase()), NotificationEvent.NotificationType.WARNING);
        verify(notificationEvent, times(2)).fire(any(NotificationEvent.class));
        verify(buildDialog, atLeastOnce()).stopBuild();

        verify(deploymentPopup, never()).show(any());
    }

    @Test
    public void testBuildAndDeployWithoutServerTemplateFail() {
        BuildMessage message = mock(BuildMessage.class);
        List<BuildMessage> messages = new ArrayList<>();
        messages.add(message);

        BuildResults results = mock(BuildResults.class);
        when(results.getErrorMessages()).thenReturn(messages);

        when(buildServiceMock.buildAndDeploy(any(), any(DeploymentMode.class))).thenReturn(results);

        runner.run(context);
        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());
        verifyNotification(ProjectEditorResources.CONSTANTS.BuildFailed(), NotificationEvent.NotificationType.ERROR);
        verify(buildDialog, atLeastOnce()).stopBuild();
    }

    @Test
    public void testBuildAndDeployWithoutServerTemplateError() {
        Answer answer = new Answer() {
            boolean executed = false;

            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (!executed) {
                    executed = true;
                    throw new Exception();
                }
                return new BuildResults();
            }
        };

        when(buildServiceMock.buildAndDeploy(any(Module.class), any(DeploymentMode.class))).thenAnswer(answer);

        runner.run(context);

        verify(buildDialog, times(2)).showBusyIndicator(CONSTANTS.Building());

        verify(notificationEvent, times(2)).fire(any(NotificationEvent.class));
        verify(buildDialog, atLeastOnce()).stopBuild();

        verify(buildDialog).stopBuild();
    }
}
