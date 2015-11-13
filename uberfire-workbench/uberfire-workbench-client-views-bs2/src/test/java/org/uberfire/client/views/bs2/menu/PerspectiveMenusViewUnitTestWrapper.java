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

import com.github.gwtbootstrap.client.ui.NavPills;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Mockito.*;

public class PerspectiveMenusViewUnitTestWrapper extends PerspectiveContextMenusView {

    public void setupMocks(NavPills menuBar){
       this.menuBar = menuBar;
    }

    @Override
    Widget makeMenuGroup( MenuGroup item ) {
        return mock(Widget.class);
    }
    @Override
    Widget makeMenuItemCommand( final MenuItem item ) {
        return mock(Widget.class);
    }

    boolean notHavePermissionToMakeThis( MenuItem item ) {
        return false;
    }

}
