/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.widgets.menu;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchMenuBarPresenterTest {

    @Mock
    private PerspectiveManager perspectiveManager;

    @Mock
    protected AuthorizationManager authzManager;

    @Mock
    protected User identity;

    @Mock
    private ActivityManager activityManager;

    @Mock
    private WorkbenchMenuBarPresenter.View view;

    @InjectMocks
    private WorkbenchMenuBarPresenter presenter;

    @Test
    public void testAddCurrentPerspective() {
        final String perspectiveId = "perspectiveId";
        final Menus menus = MenuFactory.newSimpleItem( "test" ).perspective( perspectiveId ).endMenu().build();
        final PlaceRequest placeRequest = new DefaultPlaceRequest( perspectiveId );
        final PerspectiveActivity perspectiveActivity = mock( PerspectiveActivity.class );

        when( perspectiveActivity.getPlace() ).thenReturn( placeRequest );
        when( perspectiveManager.getCurrentPerspective() ).thenReturn( perspectiveActivity );
        when( authzManager.authorize( any( Resource.class ), eq( identity ) ) ).thenReturn( true );

        presenter.addMenus( menus );
        verify( view ).selectMenuItem( perspectiveId );
    }

    @Test
    public void testAddPerspective() {
        final String perspectiveId = "perspectiveId";
        final Menus menus = MenuFactory.newSimpleItem( "test" ).perspective( perspectiveId ).endMenu().build();
        final PlaceRequest placeRequest = new DefaultPlaceRequest( "anyId" );
        final PerspectiveActivity perspectiveActivity = mock( PerspectiveActivity.class );

        when( perspectiveActivity.getPlace() ).thenReturn( placeRequest );
        when( perspectiveManager.getCurrentPerspective() ).thenReturn( perspectiveActivity );
        when( authzManager.authorize( any( Resource.class ), eq( identity ) ) ).thenReturn( true );

        presenter.addMenus( menus );

        verify( view, never() ).selectMenuItem( perspectiveId );
    }

    @Test
    public void testPerspectiveChangeEvent() {
        final String perspectiveId = "perspectiveId";
        final Menus menus = MenuFactory.newSimpleItem( "test" ).perspective( perspectiveId ).endMenu().build();
        final PlaceRequest placeRequest = new DefaultPlaceRequest( perspectiveId );
        final PerspectiveActivity perspectiveActivity = mock( PerspectiveActivity.class );
        final PerspectiveChange perspectiveChange = new PerspectiveChange( placeRequest, null, null, perspectiveId );

        when( perspectiveActivity.getPlace() ).thenReturn( placeRequest );
        when( authzManager.authorize( any( Resource.class ), eq( identity ) ) ).thenReturn( true );

        presenter.addMenus( menus );
        presenter.onPerspectiveChange( perspectiveChange );

        verify( view ).selectMenuItem( perspectiveId );
    }

    @Test
    public void testAddMenuWithPermission() {
        final String perspectiveId = "perspectiveId";
        final String label = "perspectiveLabel";
        final Menus menus = MenuFactory.newSimpleItem( label ).perspective( perspectiveId ).endMenu().build();
        when( authzManager.authorize( menus.getItems().get( 0 ), identity ) ).thenReturn( true );

        presenter.addMenus( menus );

        verify( authzManager ).authorize( menus.getItems().get( 0 ), identity );
        verify( view ).addMenuItem( eq( perspectiveId ), eq( label ), isNull( String.class ), any( Command.class ), any( MenuPosition.class ) );
    }

    @Test
    public void testAddMenuWithoutPermission() {
        final String perspectiveId = "perspectiveId";
        final String label = "perspectiveLabel";
        final Menus menus = MenuFactory.newSimpleItem( label ).perspective( perspectiveId ).endMenu().build();
        when( authzManager.authorize( menus.getItems().get( 0 ), identity ) ).thenReturn( false );

        presenter.addMenus( menus );

        verify( authzManager ).authorize( menus.getItems().get( 0 ), identity );
        verify( view, never() ).addMenuItem( eq( perspectiveId ), eq( label ), isNull( String.class ), any( Command.class ), any( MenuPosition.class ) );
    }

    @Test
    public void testAddContextMenuWithPermission() {
        final String perspectiveId = "perspectiveId";
        final String contextLabel = "contextLabel";
        final MenuPosition position = MenuPosition.LEFT;
        final Menus contextMenus = MenuFactory.newSimpleItem( contextLabel ).endMenu().build();
        final PerspectiveActivity activity = mock( PerspectiveActivity.class );
        final PlaceRequest placeRequest = mock( PlaceRequest.class );

        when( activity.getIdentifier() ).thenReturn( perspectiveId );
        when( activity.getMenus() ).thenReturn( contextMenus );
        when( authzManager.authorize( contextMenus.getItems().get( 0 ), identity ) ).thenReturn( true );
        when( activityManager.getActivity( placeRequest ) ).thenReturn( activity );

        presenter.onPerspectiveChange( new PerspectiveChange(placeRequest, null, contextMenus, perspectiveId) );

        verify( authzManager ).authorize( contextMenus.getItems().get( 0 ), identity );
        verify( view ).clearContextMenu();
        verify( view ).addContextMenuItem( eq( perspectiveId ), anyString(), eq( contextLabel ), isNull( String.class ), any( Command.class ), eq( position ) );
    }

    @Test
    public void testAddContextMenuWithoutPermission() {
        final String perspectiveId = "perspectiveId";
        final String contextLabel = "contextLabel";
        final Menus contextMenus = MenuFactory.newSimpleItem( contextLabel ).endMenu().build();
        final PerspectiveActivity activity = mock( PerspectiveActivity.class );
        final PlaceRequest placeRequest = mock( PlaceRequest.class );

        when( activity.getIdentifier() ).thenReturn( perspectiveId );
        when( activity.getMenus() ).thenReturn( contextMenus );
        when( authzManager.authorize( contextMenus.getItems().get( 0 ), identity ) ).thenReturn( false );
        when( activityManager.getActivity( placeRequest ) ).thenReturn( activity );

        presenter.onPerspectiveChange( new PerspectiveChange(placeRequest, null, contextMenus, perspectiveId) );

        verify( authzManager ).authorize( contextMenus.getItems().get( 0 ), identity );
        verify( view ).clearContextMenu();
        verify( view, never() ).addContextMenuItem( anyString(), anyString(), anyString(), anyString(), any( Command.class ), any( MenuPosition.class ) );
    }

    @Test
    public void testSetupEnableDisableMenuItemCommand() {
        final String label = "command";
        final Command command = mock( Command.class );
        final Menus menus = MenuFactory.newSimpleItem( label ).respondsWith( command ).endMenu().build();

        when( authzManager.authorize( menus.getItems().get( 0 ), identity ) ).thenReturn( true );

        presenter.addMenus( menus );

        menus.getItems().get( 0 ).setEnabled( true );
        verify( view ).enableMenuItem( anyString(), eq( true ) );

        menus.getItems().get( 0 ).setEnabled( false );
        verify( view ).enableMenuItem( anyString(), eq( false ) );
    }

    @Test
    public void testSetupEnableDisableMenuItemPlace() {
        final String label = "placeLabel";
        final PlaceRequest place = mock( PlaceRequest.class );
        final Menus menus = MenuFactory.newSimpleItem( label ).place( place ).endMenu().build();

        when( authzManager.authorize( menus.getItems().get( 0 ), identity ) ).thenReturn( true );

        presenter.addMenus( menus );

        menus.getItems().get( 0 ).setEnabled( true );
        verify( view ).enableMenuItem( anyString(), eq( true ) );

        menus.getItems().get( 0 ).setEnabled( false );
        verify( view ).enableMenuItem( anyString(), eq( false ) );
    }

    @Test
    public void testSetupEnableDisableMenuItemPerspective() {
        final String label = "perspectiveLabel";
        final String perspectiveId = "perspectiveId";
        final Menus menus = MenuFactory.newSimpleItem( label ).perspective( perspectiveId ).endMenu().build();

        when( authzManager.authorize( menus.getItems().get( 0 ), identity ) ).thenReturn( true );

        presenter.addMenus( menus );

        menus.getItems().get( 0 ).setEnabled( true );
        verify( view ).enableMenuItem( anyString(), eq( true ) );

        menus.getItems().get( 0 ).setEnabled( false );
        verify( view ).enableMenuItem( anyString(), eq( false ) );
    }

    @Test
    public void testSetupEnableDisableContextMenuItem() {
        final String contextLabel = "contextLabel";
        final String perspectiveId = "perspectiveId";
        final Menus contextMenus = MenuFactory.newSimpleItem( contextLabel ).endMenu().build();
        final PerspectiveActivity activity = mock( PerspectiveActivity.class );
        final PlaceRequest placeRequest = mock( PlaceRequest.class );

        when( activity.getIdentifier() ).thenReturn( perspectiveId );
        when( activity.getMenus() ).thenReturn( contextMenus );
        when( authzManager.authorize( contextMenus.getItems().get( 0 ), identity ) ).thenReturn( true );
        when( activityManager.getActivity( placeRequest ) ).thenReturn( activity );

        presenter.onPerspectiveChange( new PerspectiveChange( placeRequest, null, contextMenus, perspectiveId ) );

        contextMenus.getItems().get( 0 ).setEnabled( true );
        verify( view ).enableContextMenuItem( anyString(), eq( true ) );

        contextMenus.getItems().get( 0 ).setEnabled( false );
        verify( view ).enableContextMenuItem( anyString(), eq( false ) );
    }

    @Test
    public void testMenuInsertionOrder() {
        final String perspectiveId = "perspectiveId";
        final String label = "perspectiveLabel";
        final Menus firstMenus = MenuFactory.newSimpleItem( label ).perspective( perspectiveId ).endMenu().build();
        final Menus secondMenus = MenuFactory.newSimpleItem( label ).orderAll( 1 ).perspective( perspectiveId ).endMenu().build();
        final Menus thirdMenus = MenuFactory.newSimpleItem( label ).orderAll( 2 ).perspective( perspectiveId ).endMenu().build();

        when( authzManager.authorize( firstMenus.getItems().get( 0 ), identity ) ).thenReturn( true );
        when( authzManager.authorize( secondMenus.getItems().get( 0 ), identity ) ).thenReturn( true );
        when( authzManager.authorize( thirdMenus.getItems().get( 0 ), identity ) ).thenReturn( true );

        presenter.addMenus( thirdMenus );
        presenter.addMenus( firstMenus );
        presenter.addMenus( secondMenus );

        assertEquals( 3, presenter.getAddedMenus().size() );
        assertSame( firstMenus, presenter.getAddedMenus().get( 0 ) );
        assertSame( secondMenus, presenter.getAddedMenus().get( 1 ) );
        assertSame( thirdMenus, presenter.getAddedMenus().get( 2 ) );
    }

    @Test
    public void testView() {
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testCollapse() {
        presenter.collapse();

        assertFalse(presenter.isUseExpandedMode());
        verify(view).collapse();
    }

    @Test
    public void testExpand() {
        presenter.expand();

        assertTrue(presenter.isUseExpandedMode());
        verify(view).expand();
    }

    @Test
    public void testAddCollapseHandler(){
        final Command command = mock(Command.class);

        presenter.addCollapseHandler(command);

        verify(view).addCollapseHandler(command);
    }

    @Test
    public void testExpandHandler(){
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Command) invocation.getArguments()[0]).execute();
                return null;
            }
        }).when(view).addExpandHandler(any(Command.class));

        presenter.setup();

        assertTrue(presenter.isExpanded());
    }

    @Test
    public void testCollapseHandler(){
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Command) invocation.getArguments()[0]).execute();
                return null;
            }
        }).when(view).addCollapseHandler(any(Command.class));

        presenter.setup();

        assertFalse(presenter.isExpanded());
    }

    @Test
    public void testAddExpandHandler(){
        final Command command = mock(Command.class);

        presenter.addExpandHandler(command);

        verify(view).addExpandHandler(command);
    }

    @Test
    public void testClear() {
        presenter.clear();

        verify(view).clear();
    }

    @Test
    public void testOnPlaceMaximized() {
        presenter.onPlaceMaximized(mock(PlaceMaximizedEvent.class));

        verify(view).collapse();
    }

    @Test
    public void testOnPlaceMinimized() {
        presenter.onPlaceMinimized(mock(PlaceMinimizedEvent.class));

        verify(view).expand();
    }

    @Test
    public void testOnPlaceMinimizedExpandMode() {
        presenter.collapse();
        presenter.onPlaceMinimized(mock(PlaceMinimizedEvent.class));

        verify(view, never()).expand();
    }

}