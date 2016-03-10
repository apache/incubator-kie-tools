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

package org.uberfire.client.views.pfly.menu;

import java.util.Collections;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListDropDown;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.NavbarNav;
import org.gwtbootstrap3.client.ui.base.AbstractListItem;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.html.UnorderedList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuPosition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class WorkbenchMenuStandardNavBarViewTest {

    private WorkbenchMenuStandardNavBarView workbenchMenuStandardNavBarView;

    @GwtMock
    NavbarNav navbarNav;

    @GwtMock
    ListItem listItem;

    @GwtMock
    AnchorListItem anchorListItem;

    @GwtMock
    ListDropDown listDropDown;

    @GwtMock
    Anchor anchor;

    @GwtMock
    DropDownMenu dropDownMenu;

    @GwtMock
    UnorderedList unorderedList;

    @Before
    public void setup() {
        workbenchMenuStandardNavBarView = spy( WorkbenchMenuStandardNavBarView.class );

        workbenchMenuStandardNavBarView.getMenuItemWidgetMap().put( "menuItemId", spy( ComplexPanel.class ) );
    }

    @Test
    public void testAddMenuItemWithoutParent() {
        final String menuId = RandomStringUtils.random( 10 );
        final String menuParentId = null;
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;
        final Command command = mock( Command.class );

        workbenchMenuStandardNavBarView.addMenuItem( menuId, label, menuParentId, command, position );

        verify( workbenchMenuStandardNavBarView ).positionMenuItem( any( AbstractListItem.class ), eq( position ) );
    }

    @Test
    public void testAddMenuItemWithParent() {
        final String menuId = RandomStringUtils.random( 10 );
        final String menuParentId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;
        final Command command = mock( Command.class );

        workbenchMenuStandardNavBarView.addMenuItem( menuId, label, menuParentId, command, position );

        verify( workbenchMenuStandardNavBarView, never() ).positionMenuItem( any( AbstractListItem.class ), eq( position ) );
    }

    @Test
    public void testAddCustomMenuItem() {
        final Widget menu = GWT.create( Widget.class );
        final MenuPosition position = MenuPosition.RIGHT;

        workbenchMenuStandardNavBarView.addCustomMenuItem( menu, position );

        verify( workbenchMenuStandardNavBarView ).positionMenuItem( any( Widget.class ), eq( position ) );
    }

    @Test
    public void testAddGroupMenuItem() {
        final String menuId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;

        workbenchMenuStandardNavBarView.addGroupMenuItem( menuId, label, position );

        verify( workbenchMenuStandardNavBarView ).positionMenuItem( any( AbstractListItem.class ), eq( position ) );
    }

    @Test
    public void testAddContextMenuItem() {
        final String menuItemId = "menuItemId";
        final String menuId = RandomStringUtils.random( 10 );
        final String menuParentId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;
        final Command command = mock( Command.class );

        workbenchMenuStandardNavBarView.addContextMenuItem( menuItemId, menuId, label, menuParentId, command, position );

        verify( workbenchMenuStandardNavBarView ).positionMenuItem( any( AbstractListItem.class ), eq( position ) );
        verify( anchorListItem ).setText( label );
        assertEquals( anchorListItem, workbenchMenuStandardNavBarView.getMenuItemContextWidgetMap().get( menuId ) );
    }

    @Test
    public void testAddContextGroupMenuItem() {
        final String menuItemId = "menuItemId";
        final String menuId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;

        workbenchMenuStandardNavBarView.addContextGroupMenuItem( menuItemId, menuId, label, position );

        verify( workbenchMenuStandardNavBarView ).positionMenuItem( any( AbstractListItem.class ), eq( position ) );
        verify( anchor ).setText( label );
        verify( listDropDown ).add( anchor );
        assertEquals( dropDownMenu, workbenchMenuStandardNavBarView.getMenuItemContextWidgetMap().get( menuId ) );
    }

    @Test
    public void testPositionAbstractListItemOnLeft() {
        final AbstractListItem menuItem = mock( AbstractListItem.class );
        final MenuPosition position = MenuPosition.LEFT;

        workbenchMenuStandardNavBarView.positionMenuItem( menuItem, position );

        verify( menuItem, never() ).setPull( Pull.RIGHT );
    }

    @Test
    public void testPositionAbstractListItemOnRight() {
        final AbstractListItem menuItem = mock( AbstractListItem.class );
        final MenuPosition position = MenuPosition.RIGHT;

        workbenchMenuStandardNavBarView.positionMenuItem( menuItem, position );

        verify( menuItem ).setPull( Pull.RIGHT );
    }

    @Test
    public void testPositionWidgetOnLeft() {
        final Widget menuItem = spy( Widget.class );
        final MenuPosition position = MenuPosition.LEFT;

        workbenchMenuStandardNavBarView.positionMenuItem( menuItem, position );

        verify( menuItem.getElement().getStyle(), never() ).setFloat( Style.Float.RIGHT );
    }

    @Test
    public void testPositionWidgetOnRight() {
        final Widget menuItem = spy( Widget.class );
        final MenuPosition position = MenuPosition.RIGHT;

        when( menuItem.getElement() ).thenReturn( spy( Element.class ) );
        when( menuItem.getElement().getStyle() ).thenReturn( spy( Style.class ) );

        workbenchMenuStandardNavBarView.positionMenuItem( menuItem, position );

        verify( menuItem.getElement().getStyle() ).setFloat( Style.Float.RIGHT );
    }

    @Test
    public void testSelectElement() {
        final ListDropDown parent1 = mock(ListDropDown.class);
        final Widget parent2 = mock(Widget.class);
        final ComplexPanel panel = mock( ComplexPanel.class );

        when( navbarNav.iterator() ).thenReturn( Collections.<Widget>emptyList().iterator() );
        when( panel.getParent() ).thenReturn( parent2 );
        when( parent2.getParent() ).thenReturn( parent1 );

        workbenchMenuStandardNavBarView.selectElement( panel );

        verify( panel ).addStyleName( Styles.ACTIVE );
        verify( parent1 ).addStyleName( Styles.ACTIVE );
    }

    @Test
    public void testSelectMenuItemNoMenu() {
        final String menuId = "menuId";
        when( navbarNav.iterator() ).thenReturn( Collections.<Widget>emptyList().iterator() );
        when( listItem.getParent() ).thenReturn( mock(Widget.class) );

        workbenchMenuStandardNavBarView.selectMenuItem( menuId );

        verify( navbarNav ).add( listItem );
    }

    @Test
    public void testSelectMenuItem() {
        final String menuId = "menuId";
        final String label = "label";
        when( navbarNav.iterator() ).thenReturn( Collections.<Widget>emptyList().iterator() );
        when( anchorListItem.getParent() ).thenReturn( mock(Widget.class) );
        final Widget ulParent = mock( Widget.class );
        when( unorderedList.getParent() ).thenReturn( ulParent );

        workbenchMenuStandardNavBarView.addContextMenuItem( menuId, "contextMenuId", "labelContext", null, null, null );
        workbenchMenuStandardNavBarView.addMenuItem( menuId, label, null, null, null );
        workbenchMenuStandardNavBarView.selectMenuItem( menuId );

        verify( navbarNav ).add( anchorListItem );
        verify( anchorListItem ).setText( label );
        verify( unorderedList ).setVisible( true );
        verify( ulParent ).addStyleName( WorkbenchMenuNavBarView.UF_PERSPECTIVE_CONTEXT_MENU_CONTAINER );
    }

    @Test
    public void testClearContextMenu() {
        final String menuId = "menuId";
        when( navbarNav.iterator() ).thenReturn( Collections.<Widget>emptyList().iterator() );
        when( listItem.getParent() ).thenReturn( mock(Widget.class) );

        workbenchMenuStandardNavBarView.addContextMenuItem( menuId, "contextMenuItemId", "labelContextMenu", null, null, null );
        workbenchMenuStandardNavBarView.addContextGroupMenuItem( menuId, "contextGroupMenuId", "labelContextGroup", null );
        workbenchMenuStandardNavBarView.clearContextMenu();

        assertTrue( workbenchMenuStandardNavBarView.getContextContainerWidgetMap().isEmpty() );
        assertTrue( workbenchMenuStandardNavBarView.getMenuItemContextWidgetMap().isEmpty() );
        verify( anchorListItem ).clear();
        verify( anchorListItem ).removeFromParent();
        verify( dropDownMenu ).clear();
        verify( dropDownMenu ).removeFromParent();
    }

    @Test
    public void testClear() {
        workbenchMenuStandardNavBarView.getContextContainerWidgetMap().put( "key", mock( ComplexPanel.class ) );
        workbenchMenuStandardNavBarView.getMenuItemContextWidgetMap().put( "key", mock( ComplexPanel.class ) );

        workbenchMenuStandardNavBarView.clear();

        verify( navbarNav ).clear();
        assertTrue( workbenchMenuStandardNavBarView.getContextContainerWidgetMap().isEmpty() );
        assertTrue( workbenchMenuStandardNavBarView.getMenuItemContextWidgetMap().isEmpty() );
    }
}