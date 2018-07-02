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

package org.kie.workbench.common.screens.server.management.client.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.Message;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.container.config.process.ContainerProcessConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.container.config.rules.ContainerRulesConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.container.status.ContainerRemoteStatusPresenter;
import org.kie.workbench.common.screens.server.management.client.container.status.empty.ContainerStatusEmptyPresenter;
import org.kie.workbench.common.screens.server.management.client.events.ContainerSpecSelected;
import org.kie.workbench.common.screens.server.management.client.events.RefreshRemoteServers;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.kie.workbench.common.screens.server.management.client.util.State;
import org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation;
import org.kie.workbench.common.screens.server.management.model.ContainerSpecData;
import org.kie.workbench.common.screens.server.management.model.ContainerUpdateEvent;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContainerPresenterTest {

    @Mock
    Logger logger;

    @Spy
    Event<ServerTemplateSelected> serverTemplateSelectedEvent = new EventSourceMock<ServerTemplateSelected>();

    @Spy
    Event<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();

    Caller<RuntimeManagementService> runtimeManagementServiceCaller;

    @Mock
    RuntimeManagementService runtimeManagementService;

    Caller<SpecManagementService> specManagementServiceCaller;

    @Mock
    SpecManagementService specManagementService;

    @Mock
    ContainerPresenter.View view;

    @Mock
    ContainerStatusEmptyPresenter containerStatusEmptyPresenter;

    @Mock
    ContainerStatusEmptyPresenter.View containerStatusEmptyPresenterView;

    @Mock
    ContainerRemoteStatusPresenter containerRemoteStatusPresenter;

    @Mock
    ContainerRemoteStatusPresenter.View containerRemoteStatusPresenterView;

    @Mock
    ContainerRulesConfigPresenter containerRulesConfigPresenter;

    @Mock
    ContainerProcessConfigPresenter containerProcessConfigPresenter;

    ContainerPresenter presenter;

    ReleaseId releaseId;

    ServerTemplateKey serverTemplateKey;

    ContainerSpec containerSpec;

    Collection<Container> containers;

    ContainerSpecData containerSpecData;

    @Before
    public void init() {
        runtimeManagementServiceCaller = new CallerMock<RuntimeManagementService>(runtimeManagementService);
        specManagementServiceCaller = new CallerMock<SpecManagementService>(specManagementService);
        doNothing().when(serverTemplateSelectedEvent).fire(any(ServerTemplateSelected.class));
        doNothing().when(notification).fire(any(NotificationEvent.class));
        when(containerStatusEmptyPresenter.getView()).thenReturn(containerStatusEmptyPresenterView);
        when(containerRemoteStatusPresenter.getView()).thenReturn(containerRemoteStatusPresenterView);
        presenter = spy(new ContainerPresenter(
                logger,
                view,
                containerRemoteStatusPresenter,
                containerStatusEmptyPresenter,
                containerProcessConfigPresenter,
                containerRulesConfigPresenter,
                runtimeManagementServiceCaller,
                specManagementServiceCaller,
                serverTemplateSelectedEvent,
                notification));

        releaseId = new ReleaseId("org.kie",
                                  "container",
                                  "1.0.0");
        serverTemplateKey = new ServerTemplateKey("serverTemplateKeyId",
                                                  "serverTemplateKeyName");
        containerSpec = new ContainerSpec("containerId",
                                          "containerName",
                                          serverTemplateKey,
                                          releaseId,
                                          KieContainerStatus.STOPPED,
                                          new HashMap<Capability, ContainerConfig>());
        containerSpec.addConfig(Capability.PROCESS,
                                new ProcessConfig());
        containerSpec.addConfig(Capability.RULE,
                                new RuleConfig());
        containers = new ArrayList<Container>();
        containerSpecData = new ContainerSpecData(containerSpec,
                                                  containers);

        presenter.setContainerSpec(containerSpec);
    }

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
        assertEquals(view,
                     presenter.getView());
        verify(view).setStatus(containerRemoteStatusPresenter.getView());
        verify(view).setRulesConfig(containerRulesConfigPresenter.getView());
        verify(view).setProcessConfig(containerProcessConfigPresenter.getView());
    }

    @Test
    public void testStartContainer() {
        presenter.loadContainers(containerSpecData);

        presenter.startContainer();

        verify(view).setContainerStartState(State.ENABLED);
        verify(view).setContainerStopState(State.DISABLED);
        verify(view).disableRemoveButton();

        final String errorMessage = "ERROR";
        when(view.getStartContainerErrorMessage()).thenReturn(errorMessage);
        doThrow(new RuntimeException()).when(specManagementService).startContainer(containerSpecData.getContainerSpec());
        presenter.startContainer();
        verify(notification).fire(new NotificationEvent(errorMessage,
                                                        NotificationEvent.NotificationType.ERROR));

        verify(view,
               times(2)).setContainerStartState(State.DISABLED);
        verify(view,
               times(2)).setContainerStopState(State.ENABLED);
        verify(view,
               times(2)).enableRemoveButton();
    }

    @Test
    public void testStopContainer() {
        presenter.loadContainers(containerSpecData);

        presenter.stopContainer();

        verify(view,
               times(2)).setContainerStartState(State.DISABLED);
        verify(view,
               times(2)).setContainerStopState(State.ENABLED);
        verify(view,
               times(2)).enableRemoveButton();

        final String errorMessage = "ERROR";
        when(view.getStopContainerErrorMessage()).thenReturn(errorMessage);
        doThrow(new RuntimeException()).when(specManagementService).stopContainer(containerSpecData.getContainerSpec());
        presenter.stopContainer();
        verify(notification).fire(new NotificationEvent(errorMessage,
                                                        NotificationEvent.NotificationType.ERROR));

        verify(view).setContainerStartState(State.ENABLED);
        verify(view).setContainerStopState(State.DISABLED);
        verify(view).disableRemoveButton();
    }

    @Test
    public void testLoadContainersEmpty() {
        presenter.loadContainers(containerSpecData);

        verifyLoad(true,
                   1);
    }

    @Test
    public void testLoadContainersOnlyOnSelectedContainerEvent() {

        ContainerSpec containerSpec1 = new ContainerSpec("containerId1",
                                                         "containerName",
                                                         serverTemplateKey,
                                                         releaseId,
                                                         KieContainerStatus.STOPPED,
                                                         new HashMap<Capability, ContainerConfig>());
        presenter.setContainerSpec(containerSpec1);
        presenter.loadContainers(containerSpecData);

        verifyLoad(true,
                   0);

        presenter.setContainerSpec(containerSpec);
        presenter.loadContainers(containerSpecData);

        verifyLoad(true,
                   1);

    }


    @Test
    public void testRefresh() {
        when(runtimeManagementService.getContainersByContainerSpec(
                serverTemplateKey.getId(),
                containerSpec.getId())).thenReturn(containerSpecData);

        presenter.loadContainers(containerSpecData);
        presenter.refresh();

        verifyLoad(true,
                   2);
    }

    @Test
    public void testLoadContainers() {
        final Container container = new Container("containerSpecId",
                                                  "containerName",
                                                  new ServerInstanceKey(),
                                                  Collections.<Message>emptyList(),
                                                  null,
                                                  null);
        containerSpecData.getContainers().add(container);
        presenter.loadContainers(containerSpecData);

        verifyLoad(true,
                   1);
    }

    @Test
    public void testLoadContainersNonStoped() {
        final Container container = new Container("containerSpecId",
                                                  "containerName",
                                                  new ServerInstanceKey(),
                                                  Collections.<Message>emptyList(),
                                                  null,
                                                  null);
        container.setStatus(KieContainerStatus.STARTED);
        containerSpecData.getContainers().add(container);
        presenter.loadContainers(containerSpecData);

        verifyLoad(false,
                   1);
    }

    private void verifyLoad(boolean empty,
                            int times) {
        verify(containerStatusEmptyPresenter,
               times(times)).setup(containerSpec);
        verify(containerRemoteStatusPresenter,
               times(times)).setup(containerSpec,
                                   containers);
        verify(view,
               times(times)).clear();

        if (empty) {
            verify(view,
                   times(times)).setStatus(containerStatusEmptyPresenterView);
            verify(view,
                   never()).setStatus(containerRemoteStatusPresenterView);
        } else {
            verify(view,
                   times(times)).setStatus(containerRemoteStatusPresenterView);
            verify(view,
                   never()).setStatus(containerStatusEmptyPresenterView);
        }

        verify(view,
               times(times)).setContainerName(containerSpec.getContainerName());
        verify(view,
               times(times)).setGroupIp(containerSpec.getReleasedId().getGroupId());
        verify(view,
               times(times)).setArtifactId(containerSpec.getReleasedId().getArtifactId());
        verify(containerRulesConfigPresenter,
               times(times)).setVersion(releaseId.getVersion());
        verify(containerProcessConfigPresenter,
               times(times)).disable();

        verify(view,
               times(times)).setContainerStartState(State.DISABLED);
        verify(view,
               times(times)).setContainerStopState(State.ENABLED);

        verify(containerProcessConfigPresenter,
               times(times)).setup(containerSpec,
                                   (ProcessConfig) containerSpec.getConfigs().get(Capability.PROCESS));
        verify(containerRulesConfigPresenter,
               times(times)).setup(containerSpec,
                                   (RuleConfig) containerSpec.getConfigs().get(Capability.RULE));
    }

    @Test
    public void testLoad() {
        when(runtimeManagementService.getContainersByContainerSpec(
                serverTemplateKey.getId(),
                containerSpec.getId())).thenReturn(containerSpecData);

        presenter.load(new ContainerSpecSelected(containerSpec));

        verifyLoad(true,
                   1);
    }

    @Test
    public void testRefreshOnContainerUpdateEventWhenRuntimeOperationIsNotStopContainer() {
        final ContainerUpdateEvent updateEvent = mock(ContainerUpdateEvent.class);

        when(updateEvent.getContainerRuntimeOperation()).thenReturn(ContainerRuntimeOperation.START_CONTAINER);
        doNothing().when(presenter).refresh();

        presenter.refreshOnContainerUpdateEvent(updateEvent);

        verify(presenter).refresh();
    }

    @Test
    public void testRefreshOnContainerUpdateEventWhenRuntimeOperationIsStopContainer() {
        final ContainerUpdateEvent updateEvent = mock(ContainerUpdateEvent.class);

        when(updateEvent.getContainerRuntimeOperation()).thenReturn(ContainerRuntimeOperation.STOP_CONTAINER);
        doNothing().when(presenter).refresh();

        presenter.refreshOnContainerUpdateEvent(updateEvent);

        verify(presenter, never()).refresh();
    }

    @Test
    public void testOnRefresh() {
        when(runtimeManagementService.getContainersByContainerSpec(
                serverTemplateKey.getId(),
                containerSpec.getId())).thenReturn(containerSpecData);

        presenter.onRefresh(new RefreshRemoteServers(containerSpec));

        verifyLoad(true,
                   1);
    }

    @Test
    public void testRemoveContainer() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Command command = (Command) invocation.getArguments()[0];
                if (command != null) {
                    command.execute();
                }
                return null;
            }
        }).when(view).confirmRemove(any(Command.class));
        final String successMessage = "SUCCESS";
        when(view.getRemoveContainerSuccessMessage()).thenReturn(successMessage);

        presenter.loadContainers(containerSpecData);
        presenter.removeContainer();

        verify(specManagementService).deleteContainerSpec(serverTemplateKey.getId(),
                                                          containerSpec.getId());

        final ArgumentCaptor<NotificationEvent> notificationCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notification).fire(notificationCaptor.capture());
        final NotificationEvent event = notificationCaptor.getValue();
        assertEquals(NotificationEvent.NotificationType.SUCCESS,
                     event.getType());
        assertEquals(successMessage,
                     event.getNotification());

        final ArgumentCaptor<ServerTemplateSelected> serverTemplateSelectedCaptor = ArgumentCaptor.forClass(ServerTemplateSelected.class);
        verify(serverTemplateSelectedEvent).fire(serverTemplateSelectedCaptor.capture());
        assertEquals(serverTemplateKey.getId(),
                     serverTemplateSelectedCaptor.getValue().getServerTemplateKey().getId());

        final String errorMessage = "ERROR";
        when(view.getRemoveContainerErrorMessage()).thenReturn(errorMessage);
        doThrow(new RuntimeException()).when(specManagementService).deleteContainerSpec(serverTemplateKey.getId(),
                                                                                        containerSpec.getId());
        presenter.removeContainer();
        verify(notification).fire(new NotificationEvent(errorMessage,
                                                        NotificationEvent.NotificationType.ERROR));
        verify(serverTemplateSelectedEvent,
               times(2)).fire(new ServerTemplateSelected(containerSpec.getServerTemplateKey()));
    }

    @Test //Test fix for GUVNOR-3579
    public void testLoadWhenRuntimeManagementServiceReturnsInvalidData() {
        ContainerSpecData badData = new ContainerSpecData(null, null);
        when(runtimeManagementService.getContainersByContainerSpec(anyObject(), anyObject())).thenReturn(badData);

        ContainerSpecKey lookupKey = new ContainerSpecKey("dummyId", "dummyName", new ServerTemplateKey("keyId", "keyName"));

        presenter.load(lookupKey); // Doesn't throw NPE when ContainerSpecData contain nulls

        verify(view, never()).setContainerName(anyString());
    }
}