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

package org.kie.workbench.common.screens.server.management.client.remote;

import java.util.Collections;
import java.util.List;
import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.Message;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.workbench.common.screens.server.management.client.events.ServerInstanceSelected;
import org.kie.workbench.common.screens.server.management.client.remote.empty.RemoteEmptyPresenter;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemotePresenterTest {

    @Mock
    RemoteStatusPresenter remoteStatusPresenter;

    @Mock
    RemoteEmptyPresenter remoteEmptyPresenter;

    Caller<RuntimeManagementService> runtimeManagementServiceCaller;

    @Mock
    RuntimeManagementService runtimeManagementService;

    Caller<SpecManagementService> specManagementServiceCaller;

    @Mock
    SpecManagementService specManagementService;

    @Spy
    Event<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();

    @Mock
    RemotePresenter.View view;

    RemotePresenter presenter;

    @Before
    public void setup() {
        runtimeManagementServiceCaller = new CallerMock<RuntimeManagementService>(runtimeManagementService);
        specManagementServiceCaller = new CallerMock<SpecManagementService>(specManagementService);
        doNothing().when(notification).fire(any(NotificationEvent.class));
        presenter = new RemotePresenter(
                view,
                remoteStatusPresenter,
                remoteEmptyPresenter,
                runtimeManagementServiceCaller,
                specManagementServiceCaller,
                notification
        );
    }

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testRemove() {
        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey("templateId", "serverName", "serverInstanceId", "url");
        presenter.onSelect(new ServerInstanceSelected(serverInstanceKey));

        presenter.remove();

        verify(specManagementService).deleteServerInstance(serverInstanceKey);
        verify(notification).fire(any(NotificationEvent.class));
    }

    @Test
    public void testSelectAndRefresh() {
        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey("templateId", "serverName", "serverInstanceId", "url");
        final Container container = new Container("containerSpecId", "containerName", serverInstanceKey, Collections.<Message>emptyList(), null, null);
        final List<Container> containers = Collections.singletonList(container);
        when(runtimeManagementService.getContainersByServerInstance(
                serverInstanceKey.getServerTemplateId(),
                serverInstanceKey.getServerInstanceId())).thenReturn(
                containers
        );

        presenter.onSelect(new ServerInstanceSelected(serverInstanceKey));

        verify(view).clear();
        verify(view).setServerName(serverInstanceKey.getServerName());
        verify(view).setServerURL(serverInstanceKey.getUrl());
        verify(remoteStatusPresenter).setup(containers);
        verify(view).setStatusPresenter(remoteStatusPresenter.getView());
    }

    @Test
    public void testSelectAndRefreshEmptyContainers() {
        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey("templateId", "serverName", "serverInstanceId", "url");
        when(runtimeManagementService.getContainersByServerInstance(
                serverInstanceKey.getServerTemplateId(),
                serverInstanceKey.getServerInstanceId())).thenReturn(
                Collections.<Container>emptyList()
        );

        presenter.onSelect(new ServerInstanceSelected(serverInstanceKey));

        verify(view).clear();
        verify(view).setServerName(serverInstanceKey.getServerName());
        verify(view).setServerURL(serverInstanceKey.getUrl());
        verify(view).setEmptyView(remoteEmptyPresenter.getView());
    }

    @Test
    public void testOnInstanceUpdate() {
        final ServerInstance serverInstance = new ServerInstance("templateId", "serverName", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList());
        presenter.onSelect(new ServerInstanceSelected(serverInstance));

        presenter.onInstanceUpdate(new ServerInstanceUpdated(serverInstance));

        verify(view, times(2)).clear();
        verify(view, times(2)).setServerName(serverInstance.getServerName());
        verify(view, times(2)).setServerURL(serverInstance.getUrl());
        verify(view, times(2)).setEmptyView(remoteEmptyPresenter.getView());
    }

    @Test
    public void testOnInstanceUpdateDifferentServer() {
        final ServerInstance serverInstance = new ServerInstance("templateId", "serverName", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList());
        presenter.onSelect(new ServerInstanceSelected(serverInstance));

        final ServerInstance serverInstance2 = new ServerInstance("templateId2", "serverName2", "serverInstanceId2", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList());
        presenter.onInstanceUpdate(new ServerInstanceUpdated(serverInstance2));

        verify(view).clear();
        verify(view).setServerName(serverInstance.getServerName());
        verify(view).setServerURL(serverInstance.getUrl());
        verify(view).setEmptyView(remoteEmptyPresenter.getView());
    }
}