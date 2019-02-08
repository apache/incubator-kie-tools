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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.redeploy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieServerMode;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.workbench.common.screens.projecteditor.build.exec.SnapshotDeployment;
import org.kie.workbench.common.screens.projecteditor.build.exec.SnapshotDeploymentSettings;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.deploy.AbstractBuildAndDeployExecutorTest;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentPopup;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.ARTIFACT;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.GROUP;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.SERVER_TEMPLATE_ID;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.SERVER_TEMPLATE_ID2;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.SERVER_TEMPLATE_NAME;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.util.BuildExecutionTestConstants.SERVER_TEMPLATE_NAME2;
import static org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources.CONSTANTS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(GwtMockitoTestRunner.class)
public class SnapshotRedeployExecutorTest extends AbstractBuildAndDeployExecutorTest<SnapshotRedeployExecutor> {

    @Spy
    private SnapshotDeploymentSettings settings = new SnapshotDeploymentSettings() {
        @Override
        public void save() {

        }
    };

    @Before
    public void setup() {
        super.setup();

        context = getSnapshotContext();

        runner = spy(new SnapshotRedeployExecutor(buildService, buildResultsEvent, notificationEvent, buildDialog, deploymentPopup, specManagementService, settings));
    }

    @Override
    protected KieServerMode getPreferredKieServerMode() {
        return KieServerMode.DEVELOPMENT;
    }

    @Test
    public void testRedeploySingleServerTemplateStartServer() {
        final ServerTemplate serverTemplate = new ServerTemplate(SERVER_TEMPLATE_ID, SERVER_TEMPLATE_NAME);
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);

        when(specManagementServiceMock.listServerTemplates()).thenReturn(new ServerTemplateList(Collections.singletonList(serverTemplate)));

        runner.run(context);

        verify(buildDialog).startBuild();

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        ArgumentCaptor<ContainerSpec> containerSpecArgumentCaptor = ArgumentCaptor.forClass(ContainerSpec.class);
        verify(specManagementServiceMock).saveContainerSpec(eq(serverTemplate.getId()), containerSpecArgumentCaptor.capture());
        ContainerSpec containerSpec = containerSpecArgumentCaptor.getValue();

        assertEquals(module.getPom().getGav().getArtifactId(), containerSpec.getContainerName());

        verify(specManagementServiceMock).startContainer(containerSpecArgumentCaptor.capture());
        containerSpec = containerSpecArgumentCaptor.getValue();

        assertEquals(module.getPom().getGav().getArtifactId(), containerSpec.getContainerName());

        verifyNotification(ProjectEditorResources.CONSTANTS.BuildSuccessful(), NotificationEvent.NotificationType.SUCCESS);
        verifyNotification(ProjectEditorResources.CONSTANTS.DeploySuccessfulAndContainerStarted(), NotificationEvent.NotificationType.SUCCESS);
        verify(notificationEvent, times(2)).fire(any(NotificationEvent.class));

        verify(buildDialog, atLeastOnce()).stopBuild();

        verify(deploymentPopup, never()).show(any());
    }

    @Test
    public void testRedeploySingleServerTemplateUpdateServer() {
        final ServerTemplate serverTemplate = new ServerTemplate(SERVER_TEMPLATE_ID, SERVER_TEMPLATE_NAME);
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);

        ContainerSpec spec = mock(ContainerSpec.class);
        when(spec.getId()).thenReturn(context.getContainerId());

        serverTemplate.addContainerSpec(spec);

        when(specManagementServiceMock.listServerTemplates()).thenReturn(new ServerTemplateList(Collections.singletonList(serverTemplate)));

        runner.run(context);

        verify(buildDialog).startBuild();

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        ArgumentCaptor<ContainerSpec> containerSpecArgumentCaptor = ArgumentCaptor.forClass(ContainerSpec.class);

        verify(specManagementServiceMock).updateContainerSpec(eq(SERVER_TEMPLATE_ID), eq(context.getContainerId()), containerSpecArgumentCaptor.capture(), eq(true));
        ContainerSpec containerSpec = containerSpecArgumentCaptor.getValue();

        assertEquals(module.getPom().getGav().getArtifactId(), containerSpec.getContainerName());

        verifyNotification(ProjectEditorResources.CONSTANTS.BuildSuccessful(), NotificationEvent.NotificationType.SUCCESS);
        verifyNotification(ProjectEditorResources.CONSTANTS.DeploySuccessfulAndContainerUpdated(), NotificationEvent.NotificationType.SUCCESS);

        verify(notificationEvent, times(2)).fire(any(NotificationEvent.class));

        verify(buildDialog, atLeastOnce()).stopBuild();

        verify(deploymentPopup, never()).show(any());
    }

    @Test
    public void testRedeploySingleServerTemplateUpdateServerWitException() {

        doAnswer(invocationOnMock -> {
            throw new Exception();
        }).when(specManagementServiceMock).updateContainerSpec(anyString(), anyString(), any(), anyBoolean());

        final ServerTemplate serverTemplate = new ServerTemplate(SERVER_TEMPLATE_ID, SERVER_TEMPLATE_NAME);
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);

        ContainerSpec spec = mock(ContainerSpec.class);
        when(spec.getId()).thenReturn(context.getContainerId());

        serverTemplate.addContainerSpec(spec);

        when(specManagementServiceMock.listServerTemplates()).thenReturn(new ServerTemplateList(Collections.singletonList(serverTemplate)));

        runner.run(context);

        verify(buildDialog).startBuild();

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        ArgumentCaptor<ContainerSpec> containerSpecArgumentCaptor = ArgumentCaptor.forClass(ContainerSpec.class);

        verify(specManagementServiceMock).updateContainerSpec(eq(SERVER_TEMPLATE_ID), eq(context.getContainerId()), containerSpecArgumentCaptor.capture(), eq(true));
        ContainerSpec containerSpec = containerSpecArgumentCaptor.getValue();

        assertEquals(module.getPom().getGav().getArtifactId(), containerSpec.getContainerName());

        verifyNotification(CONSTANTS.DeployFailed(), NotificationEvent.NotificationType.ERROR);

        verify(notificationEvent, times(2)).fire(any(NotificationEvent.class));

        verify(buildDialog, atLeastOnce()).stopBuild();

        verify(deploymentPopup, never()).show(any());
    }

    @Test
    public void testRedeploySingleServerTemplateWithoutStart() {
        final ServerTemplate serverTemplate = new ServerTemplate(SERVER_TEMPLATE_ID, SERVER_TEMPLATE_NAME);
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);

        when(specManagementServiceMock.listServerTemplates()).thenReturn(new ServerTemplateList(Collections.singletonList(serverTemplate)));

        context.setStartContainer(false);

        runner.run(context);

        verify(buildDialog).startBuild();

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        ArgumentCaptor<ContainerSpec> containerSpecArgumentCaptor = ArgumentCaptor.forClass(ContainerSpec.class);
        verify(specManagementServiceMock).saveContainerSpec(eq(serverTemplate.getId()), containerSpecArgumentCaptor.capture());
        ContainerSpec containerSpec = containerSpecArgumentCaptor.getValue();

        assertEquals(module.getPom().getGav().getArtifactId(), containerSpec.getContainerName());

        verify(specManagementServiceMock, never()).startContainer(any());

        assertEquals(module.getPom().getGav().getArtifactId(), containerSpec.getContainerName());

        verifyNotification(ProjectEditorResources.CONSTANTS.BuildSuccessful(), NotificationEvent.NotificationType.SUCCESS);
        verifyNotification(ProjectEditorResources.CONSTANTS.DeploySuccessful(), NotificationEvent.NotificationType.SUCCESS);
        verify(notificationEvent, times(2)).fire(any(NotificationEvent.class));

        verify(buildDialog, atLeastOnce()).stopBuild();

        verify(deploymentPopup, never()).show(any());
    }

    @Test
    public void testRedeploySingleServerTemplateWithException() {
        final ServerTemplate serverTemplate = new ServerTemplate(SERVER_TEMPLATE_ID, SERVER_TEMPLATE_NAME);
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);

        when(specManagementServiceMock.listServerTemplates()).thenReturn(new ServerTemplateList(Arrays.asList(serverTemplate)));

        when(buildServiceMock.buildAndDeploy(any(Module.class), any(DeploymentMode.class))).thenAnswer(invocationOnMock -> {
            throw new Exception();
        });

        runner.run(context);

        verify(buildDialog).startBuild();

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        verify(conflictingRepositoriesPopup, never()).show();

        verify(buildDialog).stopBuild();
    }

    @Test
    public void testRedeploySingleServerTemplateWithBuildErrors() {
        BuildMessage message = mock(BuildMessage.class);
        List<BuildMessage> messages = new ArrayList<>();
        messages.add(message);

        BuildResults results = mock(BuildResults.class);
        when(results.getErrorMessages()).thenReturn(messages);

        final ServerTemplate serverTemplate = new ServerTemplate(SERVER_TEMPLATE_ID, SERVER_TEMPLATE_NAME);
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);

        when(specManagementServiceMock.listServerTemplates()).thenReturn(new ServerTemplateList(Collections.singletonList(serverTemplate)));

        when(buildServiceMock.buildAndDeploy(any(), any(DeploymentMode.class))).thenReturn(results);

        runner.run(context);

        verify(buildDialog).startBuild();

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        verifyNotification(ProjectEditorResources.CONSTANTS.BuildFailed(), NotificationEvent.NotificationType.ERROR);

        verify(buildDialog, atLeastOnce()).stopBuild();
    }

    @Test
    public void testRedeployWithMultipleTemplates() {
        final ServerTemplate serverTemplate = new ServerTemplate(SERVER_TEMPLATE_ID, SERVER_TEMPLATE_NAME);
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);

        final ServerTemplate serverTemplate2 = new ServerTemplate(SERVER_TEMPLATE_ID2, SERVER_TEMPLATE_NAME2);
        serverTemplate2.setMode(KieServerMode.DEVELOPMENT);

        when(specManagementServiceMock.listServerTemplates()).thenReturn(new ServerTemplateList(Arrays.asList(serverTemplate, serverTemplate2)));

        runner.run(context);

        verify(buildDialog).startBuild();

        ArgumentCaptor<DeploymentPopup.Driver> driverArgumentCaptor = ArgumentCaptor.forClass(DeploymentPopup.Driver.class);

        verify(deploymentPopup).show(driverArgumentCaptor.capture());

        DeploymentPopup.Driver driver = driverArgumentCaptor.getValue();

        driver.finish(context.getContainerId(), context.getContainerAlias(), SERVER_TEMPLATE_ID, true);

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        verifyNotification(ProjectEditorResources.CONSTANTS.BuildSuccessful(), NotificationEvent.NotificationType.SUCCESS);
        verifyNotification(ProjectEditorResources.CONSTANTS.DeploySuccessfulAndContainerStarted(), NotificationEvent.NotificationType.SUCCESS);
        verify(notificationEvent, times(2)).fire(any(NotificationEvent.class));

        verify(settings).save();

        verify(buildDialog).stopBuild();

        driver.cancel();

        verify(buildDialog, times(2)).stopBuild();
    }

    @Test
    public void testRedeployWithMultipleTemplatesWithPreviousDeployment() {
        final ServerTemplate serverTemplate = new ServerTemplate(SERVER_TEMPLATE_ID, SERVER_TEMPLATE_NAME);
        serverTemplate.setMode(KieServerMode.DEVELOPMENT);
        final ServerTemplate serverTemplate2 = new ServerTemplate(SERVER_TEMPLATE_ID2, SERVER_TEMPLATE_NAME2);
        serverTemplate2.setMode(KieServerMode.DEVELOPMENT);

        ContainerSpec spec = mock(ContainerSpec.class);
        when(spec.getId()).thenReturn(context.getContainerId());

        serverTemplate2.addContainerSpec(spec);

        when(settings.getDeployment(anyString(), anyString())).thenReturn(Optional.of(new SnapshotDeployment(GROUP, ARTIFACT, SERVER_TEMPLATE_ID2)));

        when(specManagementServiceMock.listServerTemplates()).thenReturn(new ServerTemplateList(Arrays.asList(serverTemplate, serverTemplate2)));

        runner.run(context);

        verify(buildDialog).startBuild();

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        ArgumentCaptor<ContainerSpec> containerSpecArgumentCaptor = ArgumentCaptor.forClass(ContainerSpec.class);

        verify(specManagementServiceMock).updateContainerSpec(eq(SERVER_TEMPLATE_ID2), eq(context.getContainerId()), containerSpecArgumentCaptor.capture(), eq(true));
        ContainerSpec containerSpec = containerSpecArgumentCaptor.getValue();

        assertEquals(module.getPom().getGav().getArtifactId(), containerSpec.getContainerName());

        verifyNotification(ProjectEditorResources.CONSTANTS.BuildSuccessful(), NotificationEvent.NotificationType.SUCCESS);
        verifyNotification(ProjectEditorResources.CONSTANTS.DeploySuccessfulAndContainerUpdated(), NotificationEvent.NotificationType.SUCCESS);

        verify(notificationEvent, times(2)).fire(any(NotificationEvent.class));

        verify(buildDialog, atLeastOnce()).stopBuild();

        verify(deploymentPopup, never()).show(any());
    }
}
