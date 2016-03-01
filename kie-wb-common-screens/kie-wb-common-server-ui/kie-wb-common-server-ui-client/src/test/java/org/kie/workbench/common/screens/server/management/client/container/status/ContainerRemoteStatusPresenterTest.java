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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
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

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.server.management.client.util.Convert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContainerRemoteStatusPresenterTest {

    @Mock
    ContainerRemoteStatusPresenter.View view;

    ContainerRemoteStatusPresenter presenter;

    @Before
    public void init() {
        presenter = spy( new ContainerRemoteStatusPresenter( view ) );
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
        doReturn( cardPresenter ).when( presenter ).newCard();

        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey( "templateId", "serverName", "serverInstanceId", "url" );
        final Container container = new Container( "containerSpecId", "containerName", serverInstanceKey, Collections.<Message>emptyList(), null, null );

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
        doReturn( cardPresenter ).when( presenter ).newCard();

        final ServerInstance serverInstance = new ServerInstance( "templateId", "serverName", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );
        final Container container = new Container( "containerSpecId", "containerName", serverInstance, Collections.<Message>emptyList(), null, null );
        final Container containerToBeRemoved = new Container( "containerToBeRemovedSpecId", "containerToBeRemovedName", serverInstance, Collections.<Message>emptyList(), null, null );
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
        final ContainerCardPresenter cardPresenter = mock( ContainerCardPresenter.class );
        when( cardPresenter.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        final ContainerCardPresenter cardPresenter2 = mock( ContainerCardPresenter.class );
        when( cardPresenter2.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        final ContainerCardPresenter cardPresenter3 = mock( ContainerCardPresenter.class );
        when( cardPresenter3.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );

        presenter = spy( new ContainerRemoteStatusPresenter( view ) {
            int i = 0;

            @Override
            ContainerCardPresenter newCard() {
                i++;
                if ( i == 1 ) {
                    return cardPresenter;
                } else if ( i == 2 ) {
                    return cardPresenter2;
                }
                return cardPresenter3;
            }
        } );

        final ServerInstance serverInstance = new ServerInstance( "templateId", "serverInstanceId", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );
        final Container existingContainer = new Container( "containerToBeRemovedSpecId", "containerToBeRemovedName", serverInstance, Collections.<Message>emptyList(), null, null );

        final ServerInstance newServerInstance = new ServerInstance( "templateId", "newserverInstanceId", "newserverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );
        final Container newContainer = new Container( "containerSpecId", "containerName", newServerInstance, Collections.<Message>emptyList(), null, null );
        newServerInstance.addContainer( newContainer );

        presenter.setup( new ContainerSpec( "containerSpecId", "containerName", new ServerTemplateKey( "templateId", "templateId" ), new ReleaseId(), KieContainerStatus.STARTED, Collections.<Capability, ContainerConfig>emptyMap() ), Arrays.asList( existingContainer ) );

        verify( cardPresenter ).setup( toKey( serverInstance ), existingContainer );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( newServerInstance ) );

        //One container added
        verify( cardPresenter2 ).setup( toKey( newServerInstance ), newContainer );

        final Container newContainer2 = new Container( "containerSpecId", "containerNameX", newServerInstance, Collections.<Message>emptyList(), null, null );
        newServerInstance.addContainer( newContainer2 );

        //One new container added to existing server
        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( newServerInstance ) );

        verify( cardPresenter3 ).setup( newContainer2.getServerInstanceKey(), newContainer2 );
    }

}