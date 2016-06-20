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

package org.kie.workbench.common.screens.server.management.client.navigation.template;

import java.util.Collections;
import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.Message;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.events.AddNewContainer;
import org.kie.workbench.common.screens.server.management.client.events.ContainerSpecSelected;
import org.kie.workbench.common.screens.server.management.client.events.ServerInstanceSelected;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateListRefresh;
import org.kie.workbench.common.screens.server.management.client.navigation.template.copy.CopyPopupPresenter;
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
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerTemplatePresenterTest {

    @Mock
    Logger logger;

    @Mock
    CopyPopupPresenter copyPresenter;

    Caller<SpecManagementService> specManagementServiceCaller;

    @Mock
    SpecManagementService specManagementService;

    @Spy
    Event<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();

    @Spy
    Event<AddNewContainer> addNewContainerEvent = new EventSourceMock<AddNewContainer>();

    @Spy
    Event<ContainerSpecSelected> containerSpecSelectedEvent = new EventSourceMock<ContainerSpecSelected>();

    @Spy
    Event<ServerInstanceSelected> serverInstanceSelectedEvent = new EventSourceMock<ServerInstanceSelected>();

    @Spy
    Event<ServerTemplateListRefresh> serverTemplateListRefreshEvent = new EventSourceMock<ServerTemplateListRefresh>();

    @Mock
    ServerTemplatePresenter.View view;

    ServerTemplatePresenter presenter;

    @Before
    public void init() {
        specManagementServiceCaller = new CallerMock<SpecManagementService>( specManagementService );
        doNothing().when( notification ).fire( any( NotificationEvent.class ) );
        doNothing().when( addNewContainerEvent ).fire( any( AddNewContainer.class ) );
        doNothing().when( containerSpecSelectedEvent ).fire( any( ContainerSpecSelected.class ) );
        doNothing().when( serverInstanceSelectedEvent ).fire( any( ServerInstanceSelected.class ) );
        doNothing().when( serverTemplateListRefreshEvent ).fire( any( ServerTemplateListRefresh.class ) );
        presenter = spy( new ServerTemplatePresenter(
                logger, view,
                copyPresenter,
                specManagementServiceCaller,
                notification,
                addNewContainerEvent,
                containerSpecSelectedEvent,
                serverInstanceSelectedEvent,
                serverTemplateListRefreshEvent ) );
    }

    @Test
    public void testInit() {
        presenter.init();

        verify( view ).init( presenter );
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testOnContainerSelect() {
        final ServerTemplateKey serverTemplateKey = new ServerTemplateKey( "ServerTemplateKeyId", "ServerTemplateKeyName" );
        final ContainerSpecKey containerSpecKey = new ContainerSpecKey( "containerId", "containerName", serverTemplateKey );

        presenter.onContainerSelect( new ContainerSpecSelected( containerSpecKey ) );

        verify( view ).selectContainer( serverTemplateKey.getId(), containerSpecKey.getId() );
    }

    @Test
    public void testOnServerInstanceSelect() {
        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey( "serverInstanceKeyId", "serverName", "serverInstanceId", "url" );

        presenter.onServerInstanceSelect( new ServerInstanceSelected( serverInstanceKey ) );

        verify( view ).selectServerInstance( serverInstanceKey.getServerTemplateId(), serverInstanceKey.getServerInstanceId() );
    }

    @Test
    public void testOnServerInstanceUpdated() {
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateId", "ServerTemplateName" );
        presenter.setup( serverTemplate, null );

        assertEquals( serverTemplate, presenter.getCurrentServerTemplate() );

        final ServerInstance serverInstance = new ServerInstance( serverTemplate.getId(), "serverName", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( serverInstance ) );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( serverInstance ) );

        verify( view ).addServerInstance(
                eq( serverInstance.getServerTemplateId() ),
                eq( serverInstance.getServerInstanceId() ),
                eq( serverInstance.getServerName() ),
                any( Command.class ) );

        presenter.onServerInstanceDeleted( new ServerInstanceDeleted( serverInstance.getServerInstanceId() ) );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( serverInstance ) );

        verify( view, times( 2 ) ).addServerInstance(
                eq( serverInstance.getServerTemplateId() ),
                eq( serverInstance.getServerInstanceId() ),
                eq( serverInstance.getServerName() ),
                any( Command.class ) );
    }

    @Test
    public void testOnServerInstanceUpdatedWithoutCurrentServer() {
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateId", "ServerTemplateName" );
        final ServerInstance serverInstance = new ServerInstance( serverTemplate.getId(), "serverName", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( serverInstance ) );

        verify(view, never()).addServerInstance(
                anyString(),
                anyString(),
                anyString(),
                any(Command.class));
    }

    @Test
    public void testAddNewContainer() {
        presenter.addNewContainer();

        verify( addNewContainerEvent ).fire( any( AddNewContainer.class ) );
    }

    @Test
    public void testRemoveTemplate() {
        when( view.getRemoveTemplateErrorMessage() ).thenReturn( "ERROR" );
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                Command command = (Command) invocation.getArguments()[ 0 ];
                if ( command != null ) {
                    command.execute();
                }
                return null;
            }
        } ).when( view ).confirmRemove( any( Command.class ) );
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateKeyId", "ServerTemplateKeyName" );
        presenter.setup( serverTemplate, null );

        presenter.removeTemplate();

        verify( specManagementService ).deleteServerTemplate( serverTemplate.getId() );
        verify( serverTemplateListRefreshEvent ).fire( any( ServerTemplateListRefresh.class ) );

        doThrow( new RuntimeException() ).when( specManagementService ).deleteServerTemplate( serverTemplate.getId() );

        presenter.removeTemplate();

        verify( specManagementService, times( 2 ) ).deleteServerTemplate( serverTemplate.getId() );
        verify( serverTemplateListRefreshEvent, times( 2 ) ).fire( any( ServerTemplateListRefresh.class ) );
        verify( notification ).fire( new NotificationEvent( "ERROR", NotificationEvent.NotificationType.ERROR ) );
    }

    @Test
    public void testCopyTemplate() {
        final String newTemplateName = "NewTemplateName";
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                final ParameterizedCommand command = (ParameterizedCommand) invocation.getArguments()[ 0 ];
                if ( command != null ) {
                    command.execute( newTemplateName );
                }
                return null;
            }
        } ).when( copyPresenter ).copy( any( ParameterizedCommand.class ) );
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateKeyId", "ServerTemplateKeyName" );
        presenter.setup( serverTemplate, null );
        assertEquals( serverTemplate, presenter.getCurrentServerTemplate() );

        presenter.copyTemplate();

        verify( specManagementService ).copyServerTemplate( serverTemplate.getId(), newTemplateName, newTemplateName );
        verify( copyPresenter ).hide();
        final ArgumentCaptor<ServerTemplateListRefresh> serverTemplateCaptor = ArgumentCaptor.forClass( ServerTemplateListRefresh.class );
        verify( serverTemplateListRefreshEvent ).fire( serverTemplateCaptor.capture() );
        assertEquals( newTemplateName, serverTemplateCaptor.getValue().getSelectServerTemplateId() );

        doThrow( new RuntimeException() ).when( specManagementService ).copyServerTemplate( serverTemplate.getId(), newTemplateName, newTemplateName );

        presenter.copyTemplate();

        verify( specManagementService, times( 2 ) ).copyServerTemplate( serverTemplate.getId(), newTemplateName, newTemplateName );
        verify( copyPresenter ).errorDuringProcessing( anyString() );
    }

    @Test
    public void testSetup() {
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateKeyId", "ServerTemplateKeyName" );
        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey( "serverTemplateId", "serverName", "serverInstanceId", "url" );
        serverTemplate.addServerInstance( serverInstanceKey );

        final ReleaseId releaseId = new ReleaseId( "org.kie", "container", "1.0.0" );
        final ContainerSpec containerSpec = new ContainerSpec( "containerId", "containerName", serverTemplate, releaseId, KieContainerStatus.CREATING, null );
        serverTemplate.addContainerSpec( containerSpec );
        final ContainerSpec containerSpec1 = new ContainerSpec( "containerId1", "containerName1", serverTemplate, new ReleaseId( "org.kie", "container2", "1.0.0" ), KieContainerStatus.CREATING, null );
        serverTemplate.addContainerSpec( containerSpec1 );

        presenter.setup( serverTemplate, containerSpec );
        assertEquals( serverTemplate, presenter.getCurrentServerTemplate() );

        verify( view ).clear();
        verify( view ).setTemplate( serverTemplate.getId(), serverTemplate.getName() );
        verify( view ).setProcessCapability( false );
        verify( view ).setRulesCapability( false );
        verify( view ).setPlanningCapability( false );

        verify( view ).addContainer(
                eq( containerSpec.getServerTemplateKey().getId() ),
                eq( containerSpec.getId() ),
                eq( containerSpec.getContainerName() ),
                any( Command.class ) );

        verify( view ).addContainer(
                eq( containerSpec1.getServerTemplateKey().getId() ),
                eq( containerSpec1.getId() ),
                eq( containerSpec1.getContainerName() ),
                any( Command.class ) );

        final ArgumentCaptor<ContainerSpecSelected> selectedCaptor = ArgumentCaptor.forClass( ContainerSpecSelected.class );
        verify( containerSpecSelectedEvent ).fire( selectedCaptor.capture() );
        assertEquals( containerSpec, selectedCaptor.getValue().getContainerSpecKey() );

        verify( view ).addServerInstance(
                eq( serverInstanceKey.getServerTemplateId() ),
                eq( serverInstanceKey.getServerInstanceId() ),
                eq( serverInstanceKey.getServerName() ),
                any( Command.class ) );
    }

    @Test
    public void testSetupCapabilities() {
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateKeyId", "ServerTemplateKeyName" );
        serverTemplate.getCapabilities().add( Capability.PROCESS.toString() );
        serverTemplate.getCapabilities().add( Capability.PLANNING.toString() );
        serverTemplate.getCapabilities().add( Capability.RULE.toString() );

        presenter.setup( serverTemplate, null );
        assertEquals( serverTemplate, presenter.getCurrentServerTemplate() );

        verify( view ).clear();
        verify( view ).setTemplate( serverTemplate.getId(), serverTemplate.getName() );
        verify( view ).setProcessCapability( true );
        verify( view ).setRulesCapability( true );
        verify( view ).setPlanningCapability( true );
    }

}