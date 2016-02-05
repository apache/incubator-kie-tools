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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.gwtbootstrap3.client.ui.base.AbstractListItem;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuPosition;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class WorkbenchMenuStandardNavBarViewTest {

    private WorkbenchMenuStandardNavBarView workbenchMenuStandardNavBarView;

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
        final Command command = new Command() {
            @Override
            public void execute() {

            }
        };

        workbenchMenuStandardNavBarView.addMenuItem( menuId, label, menuParentId, command, position );

        verify( workbenchMenuStandardNavBarView ).positionMenuItem( any( AbstractListItem.class ), eq( position ) );
    }

    @Test
    public void testAddMenuItemWithParent() {
        final String menuId = RandomStringUtils.random( 10 );
        final String menuParentId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;
        final Command command = new Command() {
            @Override
            public void execute() {

            }
        };

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
        final Command command = new Command() {
            @Override
            public void execute() {

            }
        };

        workbenchMenuStandardNavBarView.addContextMenuItem( menuItemId, menuId, label, menuParentId, command, position );

        verify( workbenchMenuStandardNavBarView ).positionMenuItem( any( AbstractListItem.class ), eq( position ) );
    }

    @Test
    public void testAddContextGroupMenuItem() {
        final String menuItemId = "menuItemId";
        final String menuId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;

        workbenchMenuStandardNavBarView.addContextGroupMenuItem( menuItemId, menuId, label, position );

        verify( workbenchMenuStandardNavBarView ).positionMenuItem( any( AbstractListItem.class ), eq( position ) );
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
}
