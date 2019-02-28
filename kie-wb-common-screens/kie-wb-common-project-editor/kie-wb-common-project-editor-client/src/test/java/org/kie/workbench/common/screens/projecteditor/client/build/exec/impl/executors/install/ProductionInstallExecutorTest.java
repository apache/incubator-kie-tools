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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.install;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources.CONSTANTS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ProductionInstallExecutorTest extends AbstractInstallExecutorTest<ProductionInstallExecutor> {

    @Captor
    private ArgumentCaptor<GAV> gavArgumentCaptor;

    @Captor
    private ArgumentCaptor<Command> okCommand;

    @Captor
    private ArgumentCaptor<Command> overrideCommand;

    @Before
    public void setup() {
        super.setup();

        context = getDefaultContext();

        runner = spy(new ProductionInstallExecutor(buildService, buildResultsEvent, notificationEvent, buildDialog, conflictingRepositoriesPopup));
    }

    @Test
    public void testBuildAndInstallGavAlreadyExistException() {
        when(buildServiceMock.buildAndDeploy(any(Module.class), eq(DeploymentMode.VALIDATED))).thenAnswer(invocationOnMock -> {
            throw new GAVAlreadyExistsException();
        });

        runner.run(context);

        verify(buildDialog).startBuild();

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        verify(buildDialog).hideBusyIndicator();

        verify(conflictingRepositoriesPopup).setContent(gavArgumentCaptor.capture(), any(), okCommand.capture(), overrideCommand.capture());

        assertEquals(context.getModule().getPom().getGav(), gavArgumentCaptor.getValue());

        verify(conflictingRepositoriesPopup).show();

        verify(notificationEvent, never()).fire(any());
        verify(buildResultsEvent, never()).fire(any());

        overrideCommand.getValue().execute();

        verify(buildServiceMock).buildAndDeploy(eq(context.getModule()), eq(DeploymentMode.FORCED));

        verifyNotification(CONSTANTS.BuildAndInstallSuccessful(), NotificationEvent.NotificationType.SUCCESS);

        verify(buildResultsEvent).fire(any());

        verify(buildDialog).stopBuild();

        okCommand.getValue().execute();

        verify(buildDialog, times(2)).stopBuild();
    }
}
