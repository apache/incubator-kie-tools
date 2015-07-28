/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client.artifact;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewContainerFormPresenterTest {

    private NewContainerFormPresenter presenter;

    @Mock
    private NewContainerFormPresenter.View view;

    @Mock
    private M2RepoService m2RepoService;

    @Mock
    private ServerManagementService serverManagementService;

    @Mock
    private DependencyListWidgetPresenter dependencyListWidgetPresenter;

    private CallerMock<M2RepoService> m2Caller;
    private CallerMock<ServerManagementService> serverManagementCaller;

    private final String serverId = "my_server_id";

    @Before
    public void setup() {
        m2Caller = new CallerMock<M2RepoService>( m2RepoService );
        serverManagementCaller = new CallerMock<ServerManagementService>( serverManagementService );

        presenter = new NewContainerFormPresenter( view, m2Caller, serverManagementCaller, dependencyListWidgetPresenter );

        assertEquals( dependencyListWidgetPresenter, presenter.getDependencyListWidgetPresenter() );

        presenter.setServer( serverId );
    }

    @Test
    public void testContainerName() {
        assertEquals( serverId, presenter.getServerId() );
        assertEquals( serverId + "/containers/", presenter.getEndpoint() );

        presenter.setContainerName( "my_container" );

        assertEquals( "my_container", presenter.getContainerName() );
        assertEquals( serverId + "/containers/my_container", presenter.getEndpoint() );
    }

    @Test
    public void testCreateContainer() {
        assertEquals( serverId, presenter.getServerId() );
        assertEquals( serverId + "/containers/", presenter.getEndpoint() );

        presenter.createContainer( "my_container", "my-group", "my-artifact", "LATEST" );

        assertEquals( "my_container", presenter.getContainerName() );
        assertEquals( serverId + "/containers/my_container", presenter.getEndpoint() );
        assertEquals( "my-group", presenter.getGroupId() );
        assertEquals( "my-artifact", presenter.getArtifactId() );
        assertEquals( "LATEST", presenter.getVersion() );

        verify( serverManagementService, times( 1 ) ).createContainer( serverId, "my_container", new GAV( "my-group", "my-artifact", "LATEST" ) );
    }

    @Test
    public void testClose() {
        presenter.close();
        verify( view, times( 1 ) ).hide();
    }

    @Test
    public void testOnDependencyPathSelectedEvent() {
        final GAV gav = new GAV( "my-group", "my_path", "1.0.Final" );
        when( m2RepoService.loadGAVFromJar( "my_path-1.0.Final" ) ).thenReturn( gav );

        presenter.onDependencyPathSelectedEvent( new DependencyPathSelectedEvent( dependencyListWidgetPresenter, "my_path-1.0.Final" ) );

        assertEquals( gav.getGroupId(), presenter.getGroupId() );
        assertEquals( gav.getArtifactId(), presenter.getArtifactId() );
        assertEquals( gav.getVersion(), presenter.getVersion() );

        verify( m2RepoService, times( 1 ) ).loadGAVFromJar( "my_path-1.0.Final" );
    }

}
