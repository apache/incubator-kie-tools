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

package org.kie.workbench.common.screens.server.management.client;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.events.ServerTemplateDeleted;
import org.kie.server.controller.api.model.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.container.ContainerPresenter;
import org.kie.workbench.common.screens.server.management.client.container.empty.ServerContainerEmptyPresenter;
import org.kie.workbench.common.screens.server.management.client.empty.ServerEmptyPresenter;
import org.kie.workbench.common.screens.server.management.client.events.ContainerSpecSelected;
import org.kie.workbench.common.screens.server.management.client.events.ServerInstanceSelected;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.kie.workbench.common.screens.server.management.client.navigation.ServerNavigationPresenter;
import org.kie.workbench.common.screens.server.management.client.navigation.template.ServerTemplatePresenter;
import org.kie.workbench.common.screens.server.management.client.remote.RemotePresenter;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerManagementBrowserPresenterTest {

    @Mock
    Logger logger;

    @Mock
    ServerTemplatePresenter serverTemplatePresenter;

    @Mock
    ServerEmptyPresenter serverEmptyPresenter;

    @Mock
    ServerContainerEmptyPresenter serverContainerEmptyPresenter;

    @Mock
    ContainerPresenter containerPresenter;

    @Mock
    RemotePresenter remotePresenter;

    Caller<SpecManagementService> specManagementServiceCaller;

    @Mock
    SpecManagementService specManagementService;

    @Spy
    Event<ServerTemplateSelected> serverTemplateSelectedEvent = new EventSourceMock<ServerTemplateSelected>();

    @Mock
    ServerManagementBrowserPresenter.View view;

    @Mock
    ServerNavigationPresenter navigationPresenter;

    ServerManagementBrowserPresenter presenter;

    @Before
    public void init() {
        specManagementServiceCaller = new CallerMock<SpecManagementService>( specManagementService );
        doNothing().when( serverTemplateSelectedEvent ).fire( any( ServerTemplateSelected.class ) );
        presenter = spy( new ServerManagementBrowserPresenter(
                logger, view,
                navigationPresenter,
                serverTemplatePresenter,
                serverEmptyPresenter,
                serverContainerEmptyPresenter,
                containerPresenter,
                remotePresenter,
                specManagementServiceCaller,
                serverTemplateSelectedEvent ) );
    }

    @Test
    public void testInit() {
        presenter.init();

        verify( view ).setNavigation( navigationPresenter.getView() );
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testOnSelectedContainerSpec() {
        final ContainerPresenter.View containerView = mock( ContainerPresenter.View.class );
        when( containerPresenter.getView() ).thenReturn( containerView );

        presenter.onSelected( new ContainerSpecSelected( new ContainerSpecKey() ) );

        verify( view ).setContent( containerView );
    }

    @Test
    public void testOnSelectedServerInstance() {
        final RemotePresenter.View remoteView = mock( RemotePresenter.View.class );
        when( remotePresenter.getView() ).thenReturn( remoteView );

        presenter.onSelected( new ServerInstanceSelected( new ServerInstanceKey() ) );

        verify( view ).setContent( remoteView );
    }

    @Test
    public void testOnSelectedServerTemplate() {
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateId", "ServerTemplateName" );
        final ServerTemplateKey serverTemplateKey = new ServerTemplateKey( "ServerTemplateKeyId", "ServerTemplateKeyName" );
        when( specManagementService.getServerTemplate( serverTemplateKey.getId() ) ).thenReturn( serverTemplate );
        final ServerTemplatePresenter.View serverView = mock( ServerTemplatePresenter.View.class );
        when( serverTemplatePresenter.getView() ).thenReturn( serverView );
        final ServerContainerEmptyPresenter.View serverEmptyView = mock( ServerContainerEmptyPresenter.View.class );
        when( serverContainerEmptyPresenter.getView() ).thenReturn( serverEmptyView );

        presenter.onSelected( new ServerTemplateSelected( serverTemplateKey ) );

        verify( view ).setServerTemplate( serverView );
        verify( specManagementService ).getServerTemplate( serverTemplateKey.getId() );
        verify( serverContainerEmptyPresenter ).setTemplate( serverTemplate );
        verify( view ).setContent( serverEmptyView );
        verify( serverTemplatePresenter ).setup( serverTemplate, null );
    }

    @Test
    public void testOnSelectedNonEmptyServerTemplate() {
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateId", "ServerTemplateName" );
        final ContainerSpec toBeSelected = mock( ContainerSpec.class );
        serverTemplate.addContainerSpec( toBeSelected );
        when( toBeSelected.getId() ).thenReturn( "other-id" );
        final ContainerSpec forcedToBeSelected = mock( ContainerSpec.class );
        when( forcedToBeSelected.getId() ).thenReturn( "container-id" );
        serverTemplate.addContainerSpec( forcedToBeSelected );

        final ServerTemplateKey serverTemplateKey = new ServerTemplateKey( "ServerTemplateKeyId", "ServerTemplateKeyName" );
        when( specManagementService.getServerTemplate( serverTemplateKey.getId() ) ).thenReturn( serverTemplate );
        final ServerTemplatePresenter.View serverView = mock( ServerTemplatePresenter.View.class );
        when( serverTemplatePresenter.getView() ).thenReturn( serverView );

        presenter.onSelected( new ServerTemplateSelected( serverTemplateKey ) );

        verify( view ).setServerTemplate( serverView );
        verify( specManagementService ).getServerTemplate( serverTemplateKey.getId() );
        verify( serverTemplatePresenter ).setup( serverTemplate, toBeSelected );

        presenter.onSelected( new ServerTemplateSelected( serverTemplateKey, "container-id" ) );

        verify( serverTemplatePresenter ).setup( serverTemplate, forcedToBeSelected );
    }

    @Test
    public void testOnOpen() {
        final ServerTemplateKey serverTemplateKey = new ServerTemplateKey( "ServerTemplateKeyId", "ServerTemplateKeyName" );
        final List<ServerTemplateKey> serverTemplateKeys = Collections.singletonList( serverTemplateKey );
        when( specManagementService.listServerTemplateKeys() ).thenReturn( serverTemplateKeys );

        presenter.onOpen();

        verify( navigationPresenter ).setup( serverTemplateKey, serverTemplateKeys );
        final ArgumentCaptor<ServerTemplateSelected> templateSelectedCaptor = ArgumentCaptor.forClass( ServerTemplateSelected.class );
        verify( serverTemplateSelectedEvent ).fire( templateSelectedCaptor.capture() );
        assertEquals( serverTemplateKey, templateSelectedCaptor.getValue().getServerTemplateKey() );
    }

    @Test
    public void testSetupEmpty() {
        final ServerEmptyPresenter.View serverEmptyView = mock( ServerEmptyPresenter.View.class );
        when( serverEmptyPresenter.getView() ).thenReturn( serverEmptyView );

        presenter.setup( Collections.<ServerTemplateKey>emptyList(), null );

        verify( view ).setEmptyView( serverEmptyView );
        verify( navigationPresenter ).clear();
    }

    @Test
    public void testOnServerDeleted() {
        final ServerTemplateKey serverTemplateKey = new ServerTemplateKey( "ServerTemplateKeyId", "ServerTemplateKeyName" );
        final List<ServerTemplateKey> serverTemplateKeys = Collections.singletonList( serverTemplateKey );
        when( specManagementService.listServerTemplateKeys() ).thenReturn( serverTemplateKeys );

        presenter.onServerDeleted( new ServerTemplateDeleted() );

        verify( navigationPresenter ).setup( serverTemplateKey, serverTemplateKeys );
        final ArgumentCaptor<ServerTemplateSelected> templateSelectedCaptor = ArgumentCaptor.forClass( ServerTemplateSelected.class );
        verify( serverTemplateSelectedEvent ).fire( templateSelectedCaptor.capture() );
        assertEquals( serverTemplateKey, templateSelectedCaptor.getValue().getServerTemplateKey() );
    }

    @Test
    public void testOnServerTemplateUpdated() {
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateId", "ServerTemplateName" );

        presenter.onServerTemplateUpdated( new ServerTemplateUpdated( serverTemplate ) );

        final ArgumentCaptor<Collection> serverTemplateKeysCaptor = ArgumentCaptor.forClass( Collection.class );
        verify( navigationPresenter ).setup( eq( serverTemplate ), serverTemplateKeysCaptor.capture() );
        final Collection<ServerTemplateKey> serverTemplateKeys = serverTemplateKeysCaptor.getValue();
        assertEquals( 1, serverTemplateKeys.size() );
        assertTrue( serverTemplateKeys.contains( serverTemplate ) );

        final ArgumentCaptor<ServerTemplateSelected> templateSelectedCaptor = ArgumentCaptor.forClass( ServerTemplateSelected.class );
        verify( serverTemplateSelectedEvent ).fire( templateSelectedCaptor.capture() );
        assertEquals( serverTemplate, templateSelectedCaptor.getValue().getServerTemplateKey() );
    }

    @Test
    public void testOnDelete() {
        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey( "serverInstanceKeyId", "serverName", "serverInstanceId", "url" );
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateId", "ServerTemplateName" );
        serverTemplate.addServerInstance( serverInstanceKey );
        when( serverTemplatePresenter.getCurrentServerTemplate() ).thenReturn( serverTemplate );
        final ServerTemplateKey serverTemplateKey = new ServerTemplateKey( "ServerTemplateKeyId", "ServerTemplateKeyName" );
        final List<ServerTemplateKey> serverTemplateKeys = Collections.singletonList( serverTemplateKey );
        when( specManagementService.listServerTemplateKeys() ).thenReturn( serverTemplateKeys );

        presenter.onDelete( new ServerInstanceDeleted( serverInstanceKey.getServerInstanceId() ) );

        verify( navigationPresenter ).setup( serverTemplateKey, serverTemplateKeys );
        final ArgumentCaptor<ServerTemplateSelected> templateSelectedCaptor = ArgumentCaptor.forClass( ServerTemplateSelected.class );
        verify( serverTemplateSelectedEvent ).fire( templateSelectedCaptor.capture() );
        assertEquals( serverTemplateKey, templateSelectedCaptor.getValue().getServerTemplateKey() );
    }

    @Test
    public void testOnDeleteWithoutCurrentServer() {
        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey( "serverInstanceKeyId", "serverName", "serverInstanceId", "url" );

        presenter.onDelete( new ServerInstanceDeleted( serverInstanceKey.getServerInstanceId() ) );

        verify( specManagementService, never() ).listServerTemplateKeys();
    }

}