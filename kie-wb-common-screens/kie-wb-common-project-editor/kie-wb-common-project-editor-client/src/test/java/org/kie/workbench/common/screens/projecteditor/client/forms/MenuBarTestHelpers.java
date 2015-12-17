/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.Menus;

public class MenuBarTestHelpers {

    public static void clickFirst( Menus menus ) {
        click( menus, 1 );
    }

    public static void clickSecond( Menus menus ) {
        click( menus, 2 );
    }

    public static void clickThird( Menus menus ) {
        click( menus, 3 );
    }

    public static void click( Menus menus,
                              int itemNumber ) {

        int i = 0;
        for ( MenuItem menuItem : menus.getItems() ) {
            if ( menuItem instanceof MenuItemCommand ) {
                if ( i == itemNumber - 1 ) {
                    MenuItemCommand defaultMenuItemCommand = (MenuItemCommand) menuItem;
                    defaultMenuItemCommand.getCommand().execute();
                    break;
                }
                i++;
            }
        }
    }
}
