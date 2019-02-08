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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.AbstractExecutorTest;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources.CONSTANTS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractBuildExecutorTest extends AbstractExecutorTest<BuildExecutor> {

    @Test
    public void testDefaultBuild() {

        runner.run(context);

        verify(buildDialog).startBuild();
        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());
        verifyNotification(ProjectEditorResources.CONSTANTS.BuildSuccessful(), NotificationEvent.NotificationType.SUCCESS);
        verify(buildResultsEvent).fire(any());
        verify(buildDialog).stopBuild();
    }

    @Test
    public void testDefaultBuildErrorResults() {

        BuildMessage message = mock(BuildMessage.class);
        List<BuildMessage> messages = new ArrayList<>();
        messages.add(message);

        BuildResults results = mock(BuildResults.class);
        when(results.getErrorMessages()).thenReturn(messages);

        when(buildServiceMock.build(any())).thenReturn(results);

        runner.run(context);

        verify(buildDialog).startBuild();
        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());
        verifyNotification(ProjectEditorResources.CONSTANTS.BuildFailed(), NotificationEvent.NotificationType.ERROR);
        verify(buildResultsEvent).fire(any());
        verify(buildDialog).stopBuild();
    }

    @Test
    public void testBuildError() {
        when(buildServiceMock.build(any())).thenAnswer(invocationOnMock -> {
            throw new Exception();
        });

        runner.run(context);

        verify(buildDialog).startBuild();
        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());
        verify(notificationEvent, never()).fire(any());
        verify(buildResultsEvent, never()).fire(any());
        verify(buildDialog).stopBuild();
    }

    @Test
    public void testBuildManagedRepository() throws Exception {
        final Map<String, Object> env = new HashMap<String, Object>() {
            {
                put("managed", true);
            }
        };

        when(repository.getEnvironment()).thenReturn(env);

        runner.run(context);

        verify(buildDialog).startBuild();
        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());
        verify(buildServiceMock).build(eq(module));
        verify(buildDialog).stopBuild();
    }

    @Test
    public void testBuildNotManagedRepositoryNonClashingGAV() throws Exception {
        final Map<String, Object> env = new HashMap<String, Object>() {
            {
                put("managed", false);
            }
        };

        when(repository.getEnvironment()).thenReturn(env);

        runner.run(context);

        verify(buildDialog).startBuild();
        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());
        verify(buildServiceMock).build(eq(module));
        verify(buildDialog).stopBuild();
    }
}
