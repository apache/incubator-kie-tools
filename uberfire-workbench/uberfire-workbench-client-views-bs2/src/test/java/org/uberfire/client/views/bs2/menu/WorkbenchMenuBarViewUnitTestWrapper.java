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
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Mockito.*;

public class WorkbenchMenuBarViewUnitTestWrapper extends WorkbenchMenuBarView{

    public void setupMocks(Nav menuBarLeft, Nav menuBarCenter, Nav menuBarRight){
        this.menuBarLeft = menuBarLeft;
        this.menuBarCenter =  menuBarCenter;
        this.menuBarRight = menuBarRight;

    }

    boolean notHavePermissionToMakeThis( MenuItem item ) {
        return false;
    }

    Widget makeNavLink( final MenuItem item ) {
        return mock(Widget.class);
    }

    Widget makeMenuCustom( MenuCustom item ) {
        return mock(Widget.class);
    }

    Widget makeMenuGroup( MenuGroup item ) {
        return mock(Widget.class);
    }

    Widget makeMenuItemCommand( final MenuItem item ) {
        return mock(Widget.class);
    }

}
