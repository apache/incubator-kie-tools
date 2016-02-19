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

package org.kie.workbench.common.screens.server.management.client.container.status.empty;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.events.RefreshRemoteServers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContainerStatusEmptyPresenterTest {

    @Spy
    Event<RefreshRemoteServers> refreshRemoteServersEvent = new EventSourceMock<RefreshRemoteServers>();

    @Mock
    ContainerStatusEmptyPresenter.View view;

    ContainerStatusEmptyPresenter presenter;

    @Before
    public void init() {
        doNothing().when(refreshRemoteServersEvent).fire(any(RefreshRemoteServers.class));
        presenter = spy(new ContainerStatusEmptyPresenter(view, refreshRemoteServersEvent));
    }

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testRefresh() {
        final ContainerSpecKey containerSpecKey = new ContainerSpecKey("id", "name", new ServerTemplateKey());
        presenter.setup(containerSpecKey);
        presenter.refresh();

        final ArgumentCaptor<RefreshRemoteServers> refreshRemoteServersCaptor = ArgumentCaptor.forClass(RefreshRemoteServers.class);
        verify(refreshRemoteServersEvent).fire(refreshRemoteServersCaptor.capture());
        assertEquals(containerSpecKey, refreshRemoteServersCaptor.getValue().getContainerSpecKey());
    }

}