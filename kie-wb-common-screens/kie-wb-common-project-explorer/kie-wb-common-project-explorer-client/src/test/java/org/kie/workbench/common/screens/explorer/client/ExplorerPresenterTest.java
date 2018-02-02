/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.ActiveContextOptions;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.technical.TechnicalViewPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.*;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;

@RunWith(MockitoJUnitRunner.class)
public class ExplorerPresenterTest {

    private ExplorerPresenter explorer;

    @Mock
    private ExplorerMenu menu;

    @Mock
    private ActiveContextOptions activeOptions;

    @Mock
    private BusinessViewPresenter businessViewPresenter;

    @Mock
    private TechnicalViewPresenter technicalViewPresenter;
    private WorkspaceProjectContext context;

    @Before
    public void setUp() throws Exception {
        context = new WorkspaceProjectContext();
        explorer = new ExplorerPresenter( mock( ExplorerView.class ),
                                          businessViewPresenter,
                                          technicalViewPresenter,
                                          context,
                                          activeOptions,
                                          menu );
    }

    @Test
    public void testOnStartUpNoInitPath() throws Exception {

        when( activeOptions.isTechnicalViewActive() ).thenReturn( true );

        PlaceRequest placeRequest = mock( PlaceRequest.class );

        ArgumentCaptor<Command> argumentCaptor = ArgumentCaptor.forClass( Command.class );

        explorer.onStartup( placeRequest );

        verify( activeOptions ).init( eq( placeRequest ),
                                      argumentCaptor.capture() );

        argumentCaptor.getValue().execute();

        verify( technicalViewPresenter ).setVisible( true );
        verify( businessViewPresenter ).setVisible( false );

        verify( technicalViewPresenter ).initialiseViewForActiveContext( context );
        verify( businessViewPresenter ).initialiseViewForActiveContext( context );
    }

    @Test
    public void testOnStartUpNoInit() throws Exception {

        when( activeOptions.isBusinessViewActive() ).thenReturn( true );

        PlaceRequest placeRequest = mock( PlaceRequest.class );
        when( placeRequest.getParameter( eq( "init_path" ),
                                         anyString() ) ).thenReturn( "something" );

        ArgumentCaptor<Command> argumentCaptor = ArgumentCaptor.forClass( Command.class );

        explorer.onStartup( placeRequest );

        verify( activeOptions ).init( eq( placeRequest ),
                                      argumentCaptor.capture() );

        argumentCaptor.getValue().execute();

        verify( technicalViewPresenter ).setVisible( false );
        verify( businessViewPresenter ).setVisible( true );

        verify( technicalViewPresenter ).initialiseViewForActiveContext( "something" );
        verify( businessViewPresenter ).initialiseViewForActiveContext( "something" );
    }

    @Test
    public void testTechViewRefresh() throws Exception {
        ArgumentCaptor<Command> argumentCaptor = ArgumentCaptor.forClass( Command.class );

        when( activeOptions.isTechnicalViewActive() ).thenReturn( true );

        verify( menu ).addRefreshCommand( argumentCaptor.capture() );

        argumentCaptor.getValue().execute();

        verify( technicalViewPresenter ).refresh();
        verify( businessViewPresenter, never() ).refresh();
    }

    @Test
    public void testBusinessViewRefresh() throws Exception {
        ArgumentCaptor<Command> argumentCaptor = ArgumentCaptor.forClass( Command.class );

        when( activeOptions.isBusinessViewActive() ).thenReturn( true );

        verify( menu ).addRefreshCommand( argumentCaptor.capture() );

        argumentCaptor.getValue().execute();

        verify( technicalViewPresenter, never() ).refresh();
        verify( businessViewPresenter ).refresh();
    }

    @Test
    public void testTechViewUpdate() throws Exception {
        ArgumentCaptor<Command> argumentCaptor = ArgumentCaptor.forClass( Command.class );

        when( activeOptions.isTechnicalViewActive() ).thenReturn( true );

        verify( menu ).addUpdateCommand( argumentCaptor.capture() );

        argumentCaptor.getValue().execute();

        verify( technicalViewPresenter ).update();
        verify( businessViewPresenter, never() ).update();
    }

    @Test
    public void testBusinessViewUpdate() throws Exception {
        ArgumentCaptor<Command> argumentCaptor = ArgumentCaptor.forClass( Command.class );

        when( activeOptions.isBusinessViewActive() ).thenReturn( true );

        verify( menu ).addUpdateCommand( argumentCaptor.capture() );

        argumentCaptor.getValue().execute();

        verify( technicalViewPresenter, never() ).update();
        verify( businessViewPresenter ).update();
    }
}