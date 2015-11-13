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

package org.uberfire.client.views.bs2.menu;

import com.github.gwtbootstrap.client.ui.Nav;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class WorkbenchMenuBarViewTest {

    private WorkbenchMenuBarViewUnitTestWrapper workbenchMenuBarViewUnitTestWrapper;
    private WorkbenchMenuBarView workbenchMenuBarViewWithoutPermission;

    public Nav menuBarLeft;

    public Nav menuBarCenter;

    public Nav menuBarRight;

    private Menus menuTopLevel;

    @Before
    public void setup() {
        menuBarLeft = GWT.create( Nav.class );
        menuBarCenter = GWT.create( Nav.class );
        menuBarRight = GWT.create( Nav.class );

        menuTopLevel = MenusFixture.buildTopLevelMenu();

        workbenchMenuBarViewUnitTestWrapper = new WorkbenchMenuBarViewUnitTestWrapper();
        workbenchMenuBarViewUnitTestWrapper.setupMocks( menuBarLeft, menuBarCenter, menuBarRight );

        workbenchMenuBarViewWithoutPermission = new WorkbenchMenuBarView() {

            boolean notHavePermissionToMakeThis( MenuItem item ) {
                return true;
            }
        };

    }

    @Test
    public void simpleAddMenuItems() {

        workbenchMenuBarViewUnitTestWrapper.addMenuItems( menuTopLevel );

        verify( menuBarLeft, Mockito.times( 1 ) ).add( any( Widget.class ) );
        verify( menuBarCenter, Mockito.times( 1 ) ).add( any( Widget.class ) );
        verify( menuBarRight, Mockito.times( 2 ) ).add( any( Widget.class ) );
    }

    @Test
    public void makeItemWithoutPermissionShouldReturnNull() {
        assertNull( workbenchMenuBarViewWithoutPermission.makeItem( menuTopLevel.getItems().get( 0 ) ) );
    }

    @Test
    public void makeMenuItemCommand() {
        WorkbenchMenuBarViewUnitTestWrapper spy = spy( workbenchMenuBarViewUnitTestWrapper );
        MenuItem item = MenusFixture.buildMenuItemCommand();
        Widget widget = spy.makeItem( item );
        verify( spy, Mockito.times( 1 ) ).makeMenuItemCommand(item );

    }

    @Test
    public void makeMenuGroup() {
        WorkbenchMenuBarViewUnitTestWrapper spy = spy( workbenchMenuBarViewUnitTestWrapper );
        MenuItem item = MenusFixture.buildMenuGroupItem();
        Widget widget = spy.makeItem( item );
        verify( spy, Mockito.times( 1 ) ).makeMenuGroup( (MenuGroup) item );

    }

    @Test
    public void makeMenuCustom() {
        WorkbenchMenuBarViewUnitTestWrapper spy = spy( workbenchMenuBarViewUnitTestWrapper );
        MenuItem item = MenusFixture.buildCustomMenu();
        Widget widget = spy.makeItem(item );
        verify( spy, Mockito.times( 1 ) ).makeMenuCustom( (MenuCustom) item );
    }

    @Test
    public void makeNavLink() {
        WorkbenchMenuBarViewUnitTestWrapper spy = spy( workbenchMenuBarViewUnitTestWrapper );
        MenuItem item = menuTopLevel.getItems().get( 0 );
        Widget widget = spy.makeItem( item );
        verify( spy, Mockito.times( 1 ) ).makeNavLink( item );
    }

}