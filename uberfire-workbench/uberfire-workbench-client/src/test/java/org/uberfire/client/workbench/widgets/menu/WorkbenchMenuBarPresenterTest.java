/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import com.google.common.collect.Sets;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
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
        verify( view ).addMenuItem( eq( perspectiveId ), eq( label ), isNull( String.class ), any( Command.class ) );
    }

    @Test
    public void testAddMenuWithoutPermission() {
        final String perspectiveId = "perspectiveId";
        final String label = "perspectiveLabel";
        final Menus menus = MenuFactory.newSimpleItem( label ).perspective( perspectiveId ).endMenu().build();
        when( authzManager.authorize( menus.getItems().get( 0 ), identity ) ).thenReturn( false );

        presenter.addMenus( menus );

        verify( authzManager ).authorize( menus.getItems().get( 0 ), identity );
        verify( view, never() ).addMenuItem( eq( perspectiveId ), eq( label ), isNull( String.class ), any( Command.class ) );
    }

    @Test
    public void testAddContextMenuWithPermission() {
        final String perspectiveId = "perspectiveId";
        final String label = "perspectiveLabel";
        final String contextLabel = "contextLabel";
        final Menus menus = MenuFactory.newSimpleItem( label ).perspective( perspectiveId ).endMenu().build();
        final Menus contextMenus = MenuFactory.newSimpleItem( contextLabel ).endMenu().build();
        final PerspectiveActivity activity = mock( PerspectiveActivity.class );

        when( activity.getIdentifier() ).thenReturn( perspectiveId );
        when( activity.getMenus() ).thenReturn( contextMenus );
        when( authzManager.authorize( menus.getItems().get( 0 ), identity ) ).thenReturn( true );
        when( authzManager.authorize( contextMenus.getItems().get( 0 ), identity ) ).thenReturn( true );
        when( activityManager.getActivities( PerspectiveActivity.class ) ).thenReturn( Sets.newHashSet( activity ) );

        presenter.addMenus( menus );

        verify( authzManager ).authorize( menus.getItems().get( 0 ), identity );
        verify( view ).addMenuItem( eq( perspectiveId ), eq( label ), isNull( String.class ), any( Command.class ) );
        verify( view ).addContextMenuItem( eq( perspectiveId ), anyString(), eq( contextLabel ), isNull( String.class ), any( Command.class ) );
    }

    @Test
    public void testAddContextMenuWithoutPermission() {
        final String perspectiveId = "perspectiveId";
        final String label = "perspectiveLabel";
        final String contextLabel = "contextLabel";
        final Menus menus = MenuFactory.newSimpleItem( label ).perspective( perspectiveId ).endMenu().build();
        final Menus contextMenus = MenuFactory.newSimpleItem( contextLabel ).endMenu().build();
        final PerspectiveActivity activity = mock( PerspectiveActivity.class );

        when( activity.getIdentifier() ).thenReturn( perspectiveId );
        when( activity.getMenus() ).thenReturn( contextMenus );
        when( authzManager.authorize( menus.getItems().get( 0 ), identity ) ).thenReturn( true );
        when( authzManager.authorize( contextMenus.getItems().get( 0 ), identity ) ).thenReturn( false );
        when( activityManager.getActivities( PerspectiveActivity.class ) ).thenReturn( Sets.newHashSet( activity ) );

        presenter.addMenus( menus );

        verify( authzManager ).authorize( menus.getItems().get( 0 ), identity );
        verify( view ).addMenuItem( eq( perspectiveId ), eq( label ), isNull( String.class ), any( Command.class ) );
        verify( view, never() ).addContextMenuItem( anyString(), anyString(), anyString(), anyString(), any( Command.class ) );
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
        final String label = "perspectiveLabel";
        final String contextLabel = "contextLabel";
        final String perspectiveId = "perspectiveId";
        final Menus menus = MenuFactory.newSimpleItem( label ).perspective( perspectiveId ).endMenu().build();
        final Menus contextMenus = MenuFactory.newSimpleItem( contextLabel ).endMenu().build();
        final PerspectiveActivity activity = mock( PerspectiveActivity.class );

        when( activity.getIdentifier() ).thenReturn( perspectiveId );
        when( activity.getMenus() ).thenReturn( contextMenus );
        when( authzManager.authorize( menus.getItems().get( 0 ), identity ) ).thenReturn( true );
        when( authzManager.authorize( contextMenus.getItems().get( 0 ), identity ) ).thenReturn( true );
        when( activityManager.getActivities( PerspectiveActivity.class ) ).thenReturn( Sets.newHashSet( activity ) );

        when( authzManager.authorize( menus.getItems().get( 0 ), identity ) ).thenReturn( true );

        presenter.addMenus( menus );

        contextMenus.getItems().get( 0 ).setEnabled( true );
        verify( view ).enableContextMenuItem( anyString(), eq( true ) );

        contextMenus.getItems().get( 0 ).setEnabled( false );
        verify( view ).enableContextMenuItem( anyString(), eq( false ) );
    }

}
