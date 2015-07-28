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

package org.kie.workbench.common.screens.server.management.client.header;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.events.HeaderClearSelectionEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderDeleteEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderFilterEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderRefreshEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderSelectAllEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderServerStatusUpdateEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderStartEvent;
import org.kie.workbench.common.screens.server.management.client.events.HeaderStopEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HeaderPresenterTest {

    private HeaderPresenter headerPresenter;

    @Mock
    private HeaderPresenter.View headerView;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private EventSourceMock<HeaderFilterEvent> headerFilterEvent;

    @Mock
    private EventSourceMock<HeaderClearSelectionEvent> headerClearSelectionEvent;

    @Mock
    private EventSourceMock<HeaderSelectAllEvent> headerSelectAllEvent;

    @Mock
    private EventSourceMock<HeaderDeleteEvent> headerDeleteEvent;

    @Mock
    private EventSourceMock<HeaderStopEvent> headerStopEvent;

    @Mock
    private EventSourceMock<HeaderStartEvent> headerStartEvent;

    @Mock
    private EventSourceMock<HeaderRefreshEvent> headerRefreshEvent;

    @Mock
    private EventSourceMock<HeaderServerStatusUpdateEvent> headerServerStatusUpdateEvent;

    @Before
    public void setup() {
        headerPresenter = new HeaderPresenter( headerView, placeManager,
                                               headerFilterEvent, headerClearSelectionEvent, headerSelectAllEvent,
                                               headerDeleteEvent, headerStopEvent, headerStartEvent, headerRefreshEvent, headerServerStatusUpdateEvent );

        assertEquals( headerView, headerPresenter.getView() );
    }

    @Test
    public void testClearSelection() {
        verify( headerClearSelectionEvent, times( 0 ) ).fire( any( HeaderClearSelectionEvent.class ) );

        headerPresenter.clearSelection();

        verify( headerClearSelectionEvent, times( 1 ) ).fire( any( HeaderClearSelectionEvent.class ) );

        headerPresenter.clearSelection();

        verify( headerClearSelectionEvent, times( 2 ) ).fire( any( HeaderClearSelectionEvent.class ) );
    }

    @Test
    public void testRefresh() {
        verify( headerRefreshEvent, times( 0 ) ).fire( any( HeaderRefreshEvent.class ) );

        headerPresenter.refresh();

        verify( headerRefreshEvent, times( 1 ) ).fire( any( HeaderRefreshEvent.class ) );

        headerPresenter.refresh();

        verify( headerRefreshEvent, times( 2 ) ).fire( any( HeaderRefreshEvent.class ) );
    }

    @Test
    public void testSelectAll() {
        verify( headerSelectAllEvent, times( 0 ) ).fire( any( HeaderSelectAllEvent.class ) );

        headerPresenter.selectAll();

        verify( headerSelectAllEvent, times( 1 ) ).fire( any( HeaderSelectAllEvent.class ) );

        headerPresenter.selectAll();

        verify( headerSelectAllEvent, times( 2 ) ).fire( any( HeaderSelectAllEvent.class ) );
    }

    @Test
    public void testFilter() {
        verify( headerFilterEvent, times( 0 ) ).fire( any( HeaderFilterEvent.class ) );

        headerPresenter.filter( "xx" );

        verify( headerFilterEvent, times( 1 ) ).fire( any( HeaderFilterEvent.class ) );

        headerPresenter.filter( "as" );

        verify( headerFilterEvent, times( 2 ) ).fire( any( HeaderFilterEvent.class ) );
    }

    @Test
    public void testRegisterServer() {
        verify( placeManager, times( 0 ) ).goTo( "ServerRegistryEndpoint" );

        headerPresenter.registerServer();

        verify( placeManager, times( 1 ) ).goTo( "ServerRegistryEndpoint" );

        headerPresenter.registerServer();

        verify( placeManager, times( 2 ) ).goTo( "ServerRegistryEndpoint" );
    }

    @Test
    public void testDelete() {
        verify( headerDeleteEvent, times( 0 ) ).fire( any( HeaderDeleteEvent.class ) );

        headerPresenter.delete();

        verify( headerDeleteEvent, times( 1 ) ).fire( any( HeaderDeleteEvent.class ) );

        headerPresenter.hideDeleteContainer();
        headerPresenter.delete();

        verify( headerDeleteEvent, times( 1 ) ).fire( any( HeaderDeleteEvent.class ) );

        headerPresenter.displayDeleteContainer();
        headerPresenter.delete();
        verify( headerDeleteEvent, times( 2 ) ).fire( any( HeaderDeleteEvent.class ) );
    }

    @Test
    public void testStart() {
        verify( headerStartEvent, times( 0 ) ).fire( any( HeaderStartEvent.class ) );

        headerPresenter.start();

        verify( headerStartEvent, times( 1 ) ).fire( any( HeaderStartEvent.class ) );

        headerPresenter.hideStartContainer();
        headerPresenter.start();

        verify( headerStartEvent, times( 1 ) ).fire( any( HeaderStartEvent.class ) );

        headerPresenter.displayStartContainer();
        headerPresenter.start();
        verify( headerStartEvent, times( 2 ) ).fire( any( HeaderStartEvent.class ) );
    }

    @Test
    public void testStopContainer() {
        verify( headerStopEvent, times( 0 ) ).fire( any( HeaderStopEvent.class ) );

        headerPresenter.stopContainer();

        verify( headerStopEvent, times( 1 ) ).fire( any( HeaderStopEvent.class ) );

        headerPresenter.hideStopContainer();
        headerPresenter.stopContainer();

        verify( headerStopEvent, times( 1 ) ).fire( any( HeaderStopEvent.class ) );

        headerPresenter.displayStopContainer();
        headerPresenter.stopContainer();
        verify( headerStopEvent, times( 2 ) ).fire( any( HeaderStopEvent.class ) );
    }
}