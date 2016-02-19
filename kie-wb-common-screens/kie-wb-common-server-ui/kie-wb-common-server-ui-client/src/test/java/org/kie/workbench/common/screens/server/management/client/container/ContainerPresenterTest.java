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

import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.container.config.process.ContainerProcessConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.container.config.rules.ContainerRulesConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.container.status.ContainerRemoteStatusPresenter;
import org.kie.workbench.common.screens.server.management.client.container.status.empty.ContainerStatusEmptyPresenter;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContainerPresenterTest {

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
    ContainerRemoteStatusPresenter containerRemoteStatusPresenter;

    @Mock
    ContainerRulesConfigPresenter containerRulesConfigPresenter;

    @Mock
    ContainerProcessConfigPresenter containerProcessConfigPresenter;

    ContainerPresenter presenter;

    @Before
    public void init() {
        runtimeManagementServiceCaller = new CallerMock<RuntimeManagementService>(runtimeManagementService);
        specManagementServiceCaller = new CallerMock<SpecManagementService>(specManagementService);
        doNothing().when(serverTemplateSelectedEvent).fire(any(ServerTemplateSelected.class));
        doNothing().when(notification).fire(any(NotificationEvent.class));
        presenter = spy(new ContainerPresenter(
                view,
                containerRemoteStatusPresenter,
                containerStatusEmptyPresenter,
                containerProcessConfigPresenter,
                containerRulesConfigPresenter,
                runtimeManagementServiceCaller,
                specManagementServiceCaller,
                serverTemplateSelectedEvent,
                notification));
    }

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
        assertEquals(view, presenter.getView());
        verify(view).setStatus(containerRemoteStatusPresenter.getView());
        verify(view).setRulesConfig(containerRulesConfigPresenter.getView());
        verify(view).setProcessConfig(containerProcessConfigPresenter.getView());
    }

}