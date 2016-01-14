/*
 *
 *  * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuPosition;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class WorkbenchMenuBarViewTest {

    @Mock
    private WorkbenchMenuCompactNavBarView workbenchMenuCompactNavBarView;

    @Mock
    private WorkbenchMenuStandardNavBarView workbenchMenuStandardNavBarView;

    @Mock
    private UtilityMenuBarView utilityMenuBarView;

    @InjectMocks
    private WorkbenchMenuBarView workbenchMenuBarView;

    @Test
    public void testAddMenuItems() {
        final String menuId = RandomStringUtils.random( 10 );
        final String menuParentId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );
        final Command command = new Command() {
            @Override
            public void execute() {

            }
        };
        workbenchMenuBarView.addMenuItem( menuId, label, menuParentId, command );

        verify( workbenchMenuCompactNavBarView ).addMenuItem( menuId, label, menuParentId, command );
        verify( workbenchMenuStandardNavBarView ).addMenuItem( menuId, label, menuParentId, command );
    }

    @Test
    public void testAddCustomMenuItem() {
        final Widget menu = GWT.create( Widget.class );
        workbenchMenuBarView.addCustomMenuItem( menu );

        verify( workbenchMenuCompactNavBarView ).addCustomMenuItem( menu );
        verify( workbenchMenuStandardNavBarView ).addCustomMenuItem( menu );
    }

    @Test
    public void testAddGroupMenuItem() {
        final String menuId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );

        workbenchMenuBarView.addGroupMenuItem( menuId, label );

        verify( workbenchMenuCompactNavBarView ).addGroupMenuItem( menuId, label );
        verify( workbenchMenuStandardNavBarView ).addGroupMenuItem( menuId, label );
    }

    @Test
    public void testAddContextMenuItem() {
        final String menuItemId = RandomStringUtils.random( 10 );
        final String menuId = RandomStringUtils.random( 10 );
        final String menuParentId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );
        final MenuPosition position = MenuPosition.RIGHT;
        final Command command = new Command() {
            @Override
            public void execute() {

            }
        };

        workbenchMenuBarView
                .addContextMenuItem( menuItemId, menuId, label, menuParentId, command, position );

        verify( workbenchMenuCompactNavBarView )
                .addContextMenuItem( menuItemId, menuId, label, menuParentId, command, position );
        verify( workbenchMenuStandardNavBarView )
                .addContextMenuItem( menuItemId, menuId, label, menuParentId, command, position );
    }

    @Test
    public void testAddContextGroupMenuItem() {
        final String menuItemId = RandomStringUtils.random( 10 );
        final String menuId = RandomStringUtils.random( 10 );
        final String label = RandomStringUtils.random( 10 );

        workbenchMenuBarView.addContextGroupMenuItem( menuItemId, menuId, label );

        verify( workbenchMenuCompactNavBarView ).addContextGroupMenuItem( menuItemId, menuId, label );
        verify( workbenchMenuStandardNavBarView ).addContextGroupMenuItem( menuItemId, menuId, label );
    }

    @Test
    public void testSelectMenu() {
        final String menuId = RandomStringUtils.random( 10 );
        workbenchMenuBarView.selectMenuItem( menuId );

        verify( workbenchMenuCompactNavBarView ).selectMenuItem( menuId );
        verify( workbenchMenuStandardNavBarView ).selectMenuItem( menuId );
    }

    @Test
    public void testClear() {
        workbenchMenuBarView.clear();

        verify( workbenchMenuCompactNavBarView ).clear();
        verify( workbenchMenuStandardNavBarView ).clear();
        verify( utilityMenuBarView ).clear();
    }

    @Test
    public void testEnableMenu() {
        final String menuId = RandomStringUtils.random( 10 );
        workbenchMenuBarView.enableMenuItem( menuId, true );

        verify( workbenchMenuCompactNavBarView ).enableMenuItem( menuId, true );
        verify( workbenchMenuStandardNavBarView ).enableMenuItem( menuId, true );
    }

    @Test
    public void testDisableMenu() {
        final String menuId = RandomStringUtils.random( 10 );
        workbenchMenuBarView.enableMenuItem( menuId, false );

        verify( workbenchMenuCompactNavBarView ).enableMenuItem( menuId, false );
        verify( workbenchMenuStandardNavBarView ).enableMenuItem( menuId, false );
    }

    @Test
    public void testEnableContextMenu() {
        final String menuId = RandomStringUtils.random( 10 );
        workbenchMenuBarView.enableContextMenuItem( menuId, true );

        verify( workbenchMenuCompactNavBarView ).enableContextMenuItem( menuId, true );
        verify( workbenchMenuStandardNavBarView ).enableContextMenuItem( menuId, true );
    }

    @Test
    public void testDisableContextMenu() {
        final String menuId = RandomStringUtils.random( 10 );
        workbenchMenuBarView.enableContextMenuItem( menuId, false );

        verify( workbenchMenuCompactNavBarView ).enableContextMenuItem( menuId, false );
        verify( workbenchMenuStandardNavBarView ).enableContextMenuItem( menuId, false );
    }

}