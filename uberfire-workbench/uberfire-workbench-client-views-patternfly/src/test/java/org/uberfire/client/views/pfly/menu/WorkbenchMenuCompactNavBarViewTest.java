/*
 *
 *  * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.views.pfly.menu;

import java.util.Collections;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.gwtbootstrap3.client.ui.AnchorButton;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownHeader;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListDropDown;
import org.gwtbootstrap3.client.ui.NavbarNav;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuPosition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class WorkbenchMenuCompactNavBarViewTest {

    @InjectMocks
    WorkbenchMenuCompactNavBarView workbenchMenuCompactNavBarView;

    @GwtMock
    NavbarNav navbarNav;

    @GwtMock
    AnchorListItem anchorListItem;

    @GwtMock
    ListDropDown listDropDown;

    @GwtMock
    AnchorButton anchorButton;

    @GwtMock
    DropDownMenu dropDownMenu;

    @GwtMock
    DropDownHeader dropDownHeader;

    @GwtMock
    Text text;

    @Test
    public void testAddMenuItem() {
        final String menuId = RandomStringUtils.random( 10 );
        final String menuParentId = null;
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;
        final Command command = mock( Command.class );

        workbenchMenuCompactNavBarView.addMenuItem( menuId, label, menuParentId, command, position );

        verify( dropDownMenu ).add( anchorListItem );
        verify( anchorListItem ).setText( label );
        assertEquals( anchorListItem, workbenchMenuCompactNavBarView.getMenuItemWidgetMap().get( menuId ) );
    }

    @Test
    public void testAddGroupMenuItem() {
        final String menuId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;

        workbenchMenuCompactNavBarView.addGroupMenuItem( menuId, label, position );

        verify( dropDownMenu ).add( dropDownHeader );
        verify( dropDownHeader ).setText( label );
    }

    @Test
    public void testAddContextMenuItem() {
        final String menuItemId = "menuItemId";
        final String menuId = RandomStringUtils.random( 10 );
        final String menuParentId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;
        final Command command = mock( Command.class );

        workbenchMenuCompactNavBarView.addContextMenuItem( menuItemId, menuId, label, menuParentId, command, position );

        verify( anchorListItem ).setText( label );
        assertEquals( anchorListItem, workbenchMenuCompactNavBarView.getMenuItemContextWidgetMap().get( menuId ) );
        assertTrue( workbenchMenuCompactNavBarView.getMenuItemContextMenus().containsValue( anchorListItem ) );
    }

    @Test
    public void testAddContextGroupMenuItem() {
        final String menuItemId = "menuItemId";
        final String menuId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;

        workbenchMenuCompactNavBarView.addContextGroupMenuItem( menuItemId, menuId, label, position );

        verify( anchorButton ).setText( label );
        verify( listDropDown ).add( anchorButton );
        assertEquals( dropDownMenu, workbenchMenuCompactNavBarView.getMenuItemContextWidgetMap().get( menuId ) );
        assertTrue( workbenchMenuCompactNavBarView.getMenuItemContextMenus().containsValue( listDropDown ) );
    }

    @Test
    public void testSelectElement() {
        final ComplexPanel panel = mock( ComplexPanel.class );
        final ComplexPanel panelActive = mock( ComplexPanel.class );

        when( dropDownMenu.iterator() ).thenReturn( Collections.<Widget>singletonList( panel ).iterator() );

        workbenchMenuCompactNavBarView.selectElement( panelActive );

        verify( panelActive ).addStyleName( Styles.ACTIVE );
        verify( panel ).removeStyleName( Styles.ACTIVE );
    }

    @Test
    public void testSelectMenuItemNoMenu() {
        final String menuId = "menuId";

        workbenchMenuCompactNavBarView.addContextMenuItem( menuId, "contextMenuId", "labelContext", null, null, null );
        workbenchMenuCompactNavBarView.selectMenuItem( menuId );

        verify( navbarNav ).add( anchorListItem );
        verify( text ).setText( "" );
    }

    @Test
    public void testSelectMenuItem() {
        final String menuId = "menuId";
        final String label = "label";
        final String labelContext = "labelContext";
        when( dropDownMenu.iterator() ).thenReturn( Collections.<Widget>emptyList().iterator() );

        workbenchMenuCompactNavBarView.addContextMenuItem( menuId, "contextMenuId", labelContext, null, null, null );
        workbenchMenuCompactNavBarView.addMenuItem( menuId, label, null, null, null );
        workbenchMenuCompactNavBarView.selectMenuItem( menuId );

        verify( navbarNav ).add( anchorListItem );
        verify( text ).setText( label );
        verify( anchorListItem ).setText( labelContext );
        verify( anchorListItem ).setText( label );
        verify( dropDownMenu ).add( anchorListItem );
    }

    @Test
    public void testClearContextMenu() {
        final String menuId = "menuId";

        workbenchMenuCompactNavBarView.addContextMenuItem( menuId, "contextMenuItemId", "labelContextMenu", null, null, null );
        workbenchMenuCompactNavBarView.addContextGroupMenuItem( menuId, "contextGroupMenuId", "labelContextGroup", null );
        workbenchMenuCompactNavBarView.clearContextMenu();

        assertTrue( workbenchMenuCompactNavBarView.getMenuItemContextMenus().isEmpty() );
        assertTrue( workbenchMenuCompactNavBarView.getMenuItemContextWidgetMap().isEmpty() );
        verify( anchorListItem, times(2) ).clear();
        verify( anchorListItem, times(2) ).removeFromParent();
        verify( listDropDown ).clear();
        verify( listDropDown ).removeFromParent();
    }

    @Test
    public void testClear() {
        workbenchMenuCompactNavBarView.getMenuItemContextMenus().put( "key", mock( ComplexPanel.class ) );
        workbenchMenuCompactNavBarView.getMenuItemContextWidgetMap().put( "key", mock( ComplexPanel.class ) );

        workbenchMenuCompactNavBarView.clear();

        verify( navbarNav ).clear();
        assertTrue( workbenchMenuCompactNavBarView.getMenuItemContextMenus().isEmpty() );
        assertTrue( workbenchMenuCompactNavBarView.getMenuItemContextWidgetMap().isEmpty() );
    }

}