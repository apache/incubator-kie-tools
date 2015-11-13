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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.mockito.Mockito.*;

/**
 * Created by Cristiano Nicolai.
 */
@RunWith( GwtMockitoTestRunner.class )
public class WorkbenchMenuBarPresenterTest {

    @Mock
    private PerspectiveManager perspectiveManager;

    @Mock
    private WorkbenchMenuBarPresenter.View view;

    @InjectMocks
    private WorkbenchMenuBarPresenter presenter;

    @Test
    public void testAddCurrentPerspective(){
        final String perspectiveId = "perspectiveId";
        final Menus menus = MenuFactory.newSimpleItem( "test" ).perspective( perspectiveId ).endMenu().build();
        final PlaceRequest placeRequest = new DefaultPlaceRequest( perspectiveId );
        final PerspectiveActivity perspectiveActivity = mock( PerspectiveActivity.class );
        when( perspectiveActivity.getPlace() ).thenReturn( placeRequest );
        when( perspectiveManager.getCurrentPerspective() ).thenReturn( perspectiveActivity );
        presenter.addMenus( menus );
        verify( view ).selectMenu( menus.getItems().get( 0 ) );
    }

    @Test
    public void testAddPerspective(){
        final String perspectiveId = "perspectiveId";
        final Menus menus = MenuFactory.newSimpleItem( "test" ).perspective( perspectiveId ).endMenu().build();
        final PlaceRequest placeRequest = new DefaultPlaceRequest( "anyId" );
        final PerspectiveActivity perspectiveActivity = mock( PerspectiveActivity.class );
        when( perspectiveActivity.getPlace() ).thenReturn( placeRequest );
        when( perspectiveManager.getCurrentPerspective() ).thenReturn( perspectiveActivity );
        presenter.addMenus( menus );
        verify( view, never() ).selectMenu( menus.getItems().get( 0 ) );
    }
}
