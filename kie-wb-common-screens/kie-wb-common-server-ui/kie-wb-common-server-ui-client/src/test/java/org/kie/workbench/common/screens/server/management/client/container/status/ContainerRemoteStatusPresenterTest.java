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

package org.kie.workbench.common.screens.server.management.client.container.status;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.screens.server.management.client.util.Convert.toKey;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
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
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.google.gwt.user.client.ui.IsWidget;

@RunWith(MockitoJUnitRunner.class)
public class ContainerRemoteStatusPresenterTest {

    @Mock
    Logger logger;

    @Mock
    ManagedInstance<ContainerCardPresenter> presenterProvider;

    @Mock
    ContainerRemoteStatusPresenter.View view;

    ContainerRemoteStatusPresenter presenter;

    @Before
    public void init() {
        presenter = spy( new ContainerRemoteStatusPresenter( logger, view, presenterProvider ) );
    }

    @Test
    public void testInit() {
        presenter.init();

        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testOnDelete() {
        final ContainerCardPresenter cardPresenter = mock( ContainerCardPresenter.class );
        when( cardPresenter.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        when( presenterProvider.get() ).thenReturn( cardPresenter );

        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey( "templateId", "serverName", "serverInstanceId", "url" );
        final Container container = new Container( "containerSpecId", "containerName", serverInstanceKey, Collections.<Message>emptyList(), null, null );
        container.setStatus( KieContainerStatus.STARTED );

        presenter.setup( new ContainerSpec(), Collections.singletonList( container ) );

        verify( view ).clear();
        verify( cardPresenter ).setup( container.getServerInstanceKey(), container );
        verify( view ).addCard( any( IsWidget.class ) );

        presenter.onDelete( new ServerInstanceDeleted( serverInstanceKey.getServerInstanceId() ) );

        verify( cardPresenter ).delete();

        presenter.onDelete( new ServerInstanceDeleted( "randomKey" ) );

        verify( cardPresenter ).delete();
    }

    @Test
    public void testOnServerInstanceUpdated() {
        final ContainerCardPresenter cardPresenter = mock( ContainerCardPresenter.class );
        when( cardPresenter.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        when( presenterProvider.get() ).thenReturn( cardPresenter );

        final ServerInstance serverInstance = new ServerInstance( "templateId", "serverName", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );
        final Container container = new Container( "containerSpecId", "containerName", serverInstance, Collections.<Message>emptyList(), null, null );
        container.setStatus( KieContainerStatus.STARTED );
        final Container containerToBeRemoved = new Container( "containerToBeRemovedSpecId", "containerToBeRemovedName", serverInstance, Collections.<Message>emptyList(), null, null );
        containerToBeRemoved.setStatus( KieContainerStatus.STARTED );
        serverInstance.addContainer( container );

        presenter.setup( new ContainerSpec(), Arrays.asList( container, containerToBeRemoved ) );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( serverInstance ) );

        //One container updated,  one removed
        verify( cardPresenter ).updateContent( serverInstance, container );
        verify( cardPresenter ).delete();
        final ArgumentCaptor<Container> containerCaptor = ArgumentCaptor.forClass( Container.class );
        verify( cardPresenter, times( 2 ) ).setup( eq( container.getServerInstanceKey() ), containerCaptor.capture() );
        final List<Container> containers = containerCaptor.getAllValues();
        assertEquals( 2, containers.size() );
        assertEquals( container, containers.get( 0 ) );
        assertEquals( containerToBeRemoved, containers.get( 1 ) );
    }

    @Test
    public void testOnServerInstanceUpdatedNewInstance() {
        presenter = spy( new ContainerRemoteStatusPresenter( logger, view, presenterProvider ) );

        final ContainerCardPresenter cardPresenter = mock( ContainerCardPresenter.class );
        when( cardPresenter.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        final ContainerCardPresenter cardPresenter2 = mock( ContainerCardPresenter.class );
        when( cardPresenter2.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        final ContainerCardPresenter cardPresenter3 = mock( ContainerCardPresenter.class );
        when( cardPresenter3.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        doReturn( cardPresenter )
                .doReturn( cardPresenter2 )
                .doReturn( cardPresenter3 )
                .when( presenterProvider ).get();

        final ServerInstance serverInstance = new ServerInstance( "templateId", "serverInstanceId", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );
        final Container existingContainer = new Container( "containerToBeRemovedSpecId", "containerToBeRemovedName", serverInstance, Collections.<Message>emptyList(), null, null );
        existingContainer.setStatus( KieContainerStatus.STARTED );

        final ServerInstance newServerInstance = new ServerInstance( "templateId", "newserverInstanceId", "newserverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );
        final Container newContainer = new Container( "containerSpecId", "containerName", newServerInstance, Collections.<Message>emptyList(), null, null );
        newContainer.setStatus( KieContainerStatus.STARTED );
        newServerInstance.addContainer( newContainer );

        presenter.setup( new ContainerSpec( "containerSpecId", "containerName", new ServerTemplateKey( "templateId", "templateId" ), new ReleaseId(), KieContainerStatus.STARTED, Collections.<Capability, ContainerConfig>emptyMap() ), Arrays.asList( existingContainer ) );

        verify( cardPresenter ).setup( toKey( serverInstance ), existingContainer );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( newServerInstance ) );

        //One container added
        verify( cardPresenter2 ).setup( toKey( newServerInstance ), newContainer );

        final Container newContainer2 = new Container( "containerSpecId", "containerNameX", newServerInstance, Collections.<Message>emptyList(), null, null );
        newContainer2.setStatus( KieContainerStatus.STARTED );
        newServerInstance.addContainer( newContainer2 );

        //One new container added to existing server
        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( newServerInstance ) );

        verify( cardPresenter3 ).setup( newContainer2.getServerInstanceKey(), newContainer2 );
    }

    @Test
    public void testOnServerInstanceUpdatedDuplicatedContainerName() {
        final ContainerCardPresenter cardPresenter = mock( ContainerCardPresenter.class );
        when( cardPresenter.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        when( presenterProvider.get() ).thenReturn( cardPresenter );

        final ServerInstance serverInstance = new ServerInstance( "templateId", "serverName", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );
        final Container container = new Container( "containerSpecId", "containerName", serverInstance, Collections.<Message>emptyList(), null, null );
        container.setStatus( KieContainerStatus.STARTED );
        final Container container2 = new Container( "containerSpecId2", "containerName", serverInstance, Collections.<Message>emptyList(), null, null );
        container2.setStatus( KieContainerStatus.STARTED );

        final Container containerToBeRemoved = new Container( "containerToBeRemovedSpecId", "containerToBeRemovedName", serverInstance, Collections.<Message>emptyList(), null, null );
        containerToBeRemoved.setStatus( KieContainerStatus.STARTED );
        serverInstance.addContainer( container );
        serverInstance.addContainer( container2 );

        presenter.setup( new ContainerSpec(), Arrays.asList( container, container2, containerToBeRemoved ) );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( serverInstance ) );

        //One container updated,  one removed
        verify( cardPresenter ).updateContent( serverInstance, container );
        verify( cardPresenter ).updateContent( serverInstance, container2 );
        verify( cardPresenter ).delete();
        final ArgumentCaptor<Container> containerCaptor = ArgumentCaptor.forClass( Container.class );
        verify( cardPresenter, times( 3 ) ).setup( eq( container.getServerInstanceKey() ), containerCaptor.capture() );
        final List<Container> containers = containerCaptor.getAllValues();
        assertEquals( 3, containers.size() );
        assertEquals( container, containers.get( 0 ) );
        assertEquals( container2, containers.get( 1 ) );
        assertEquals( containerToBeRemoved, containers.get( 2 ) );
    }

}