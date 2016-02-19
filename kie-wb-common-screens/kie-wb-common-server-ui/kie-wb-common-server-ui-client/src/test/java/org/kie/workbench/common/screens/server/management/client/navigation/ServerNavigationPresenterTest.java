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

package org.kie.workbench.common.screens.server.management.client.navigation;


import java.util.Collections;
import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.events.AddNewServerTemplate;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateListRefresh;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerNavigationPresenterTest {

    @Spy
    Event<AddNewServerTemplate> addNewServerTemplateEvent = new EventSourceMock<AddNewServerTemplate>();

    @Spy
    Event<ServerTemplateListRefresh> serverTemplateListRefreshEvent = new EventSourceMock<ServerTemplateListRefresh>();

    @Spy
    Event<ServerTemplateSelected> serverTemplateSelectedEvent = new EventSourceMock<ServerTemplateSelected>();

    @Mock
    ServerNavigationPresenter.View view;

    ServerNavigationPresenter presenter;

    @Before
    public void init() {
        doNothing().when(addNewServerTemplateEvent).fire(any(AddNewServerTemplate.class));
        doNothing().when(serverTemplateListRefreshEvent).fire(any(ServerTemplateListRefresh.class));
        doNothing().when(serverTemplateSelectedEvent).fire(any(ServerTemplateSelected.class));
        presenter = spy(new ServerNavigationPresenter(
                view,
                addNewServerTemplateEvent,
                serverTemplateListRefreshEvent,
                serverTemplateSelectedEvent));
    }

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testClear() {
        presenter.clear();
        verify(view).clean();
    }

    @Test
    public void testSelect() {
        final String serverId = "serverId";

        presenter.select(serverId);

        final ArgumentCaptor<ServerTemplateSelected> serverTemplateSelectedCaptor = ArgumentCaptor.forClass(ServerTemplateSelected.class);
        verify(serverTemplateSelectedEvent).fire(serverTemplateSelectedCaptor.capture());
        assertEquals(serverId, serverTemplateSelectedCaptor.getValue().getServerTemplateKey().getId());
    }

    @Test
    public void testRefresh() {
        presenter.refresh();

        verify(serverTemplateListRefreshEvent).fire(any(ServerTemplateListRefresh.class));
    }

    @Test
    public void testNewTemplate() {
        presenter.newTemplate();

        verify(addNewServerTemplateEvent).fire(any(AddNewServerTemplate.class));
    }

    @Test
    public void testSetup() {
        final ServerTemplateKey serverTemplateKey = new ServerTemplateKey("ServerTemplateKeyId", "ServerTemplateKeyName");
        presenter.setup(serverTemplateKey, Collections.singletonList(serverTemplateKey));

        verify(view).clean();
        verify(view).addTemplate(serverTemplateKey.getId(), serverTemplateKey.getName());
    }

    @Test
    public void testSetupList() {
        final ServerTemplateKey serverTemplateKey = new ServerTemplateKey("ServerTemplateKeyId", "ServerTemplateKeyName");
        final ServerTemplateKey serverTemplateKey2 = new ServerTemplateKey("ServerTemplateKeyId2", "ServerTemplateKeyName2");
        presenter.setup(serverTemplateKey, Collections.singletonList(serverTemplateKey2));

        verify(view).clean();
        verify(view).addTemplate(serverTemplateKey.getId(), serverTemplateKey.getName());
        verify(view).addTemplate(serverTemplateKey2.getId(), serverTemplateKey2.getName());
    }

    @Test
    public void testOnSelect() {
        final ServerTemplateKey serverTemplateKey = new ServerTemplateKey("ServerTemplateKeyId", "ServerTemplateKeyName");
        presenter.onSelect(new ServerTemplateSelected(serverTemplateKey));

        verify(view).select(serverTemplateKey.getId());
    }

    @Test
    public void testOnServerTemplateUpdated(){
        final ServerTemplate serverTemplate = new ServerTemplate("ServerTemplateKeyId", "ServerTemplateKeyName");
        presenter.onServerTemplateUpdated(new ServerTemplateUpdated(serverTemplate));

        verify(view).addTemplate(serverTemplate.getId(), serverTemplate.getName());
    }

}
