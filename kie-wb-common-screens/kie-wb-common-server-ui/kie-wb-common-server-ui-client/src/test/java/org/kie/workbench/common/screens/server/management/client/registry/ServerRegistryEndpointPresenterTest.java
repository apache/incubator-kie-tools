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

package org.kie.workbench.common.screens.server.management.client.registry;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.events.ContainerStarted;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class ServerRegistryEndpointPresenterTest {

    private ServerRegistryEndpointPresenter presenter;

    @Mock
    private ServerRegistryEndpointPresenter.View view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private ServerManagementService serverManagementService;

    @Mock
    private ErrorPopupPresenter errorPopup;

    private Caller<ServerManagementService> serverManagementCaller;

    private PlaceRequest placeRequest = new DefaultPlaceRequest( "ServerRegistryEndpoint" );

    @Before
    public void setup() {
        serverManagementCaller = new CallerMock<ServerManagementService>( serverManagementService );

        presenter = new ServerRegistryEndpointPresenter( view, placeManager, serverManagementCaller, errorPopup );

        assertEquals( view, presenter.getView() );

        presenter.onStartup( placeRequest );
    }

    @Test
    public void testRegisterServer() {
        when( view.getBaseURL() ).thenReturn( "localhost/" );

        presenter.registerServer( "http:endpoint", "my_server", "123" );

        verify( view, times( 1 ) ).lockScreen();
        verify( placeManager, times( 1 ) ).forceClosePlace( placeRequest );
        verify( view, times( 1 ) ).unlockScreen();
        verify( errorPopup, times( 0 ) ).showMessage( anyString(), any( Command.class ), any( Command.class ) );

        doThrow( RuntimeException.class ).when( serverManagementService ).registerServer( "http:endpoint", "my_server", "123" );

        final ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass( Command.class );

        doAnswer( new Answer() {
            @Override
            public Object answer( InvocationOnMock invocationOnMock ) throws Throwable {
                commandCaptor.getValue().execute();
                return null;
            }
        } ).when( errorPopup ).showMessage( any( String.class ), any( Command.class ), commandCaptor.capture() );

        presenter.registerServer( "http:endpoint", "my_server", "123" );

        verify( errorPopup, times( 1 ) ).showMessage( anyString(), any( Command.class ), any( Command.class ) );
        verify( placeManager, times( 1 ) ).forceClosePlace( placeRequest );
        verify( view, times( 2 ) ).unlockScreen();
    }

}
