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

package org.uberfire.client.views.pfly.menu;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.uberfire.workbench.model.menu.MenuPosition;

/**
 * Widgets that can contain menu items implement this interface.
 */
public interface HasMenuItems extends IsWidget {

    /**
     * Adds a new menu item to the end of the current list of menu items at the given position.
     * @param position the position to append the menu item at. Not all menu containers support positioning; those that don't
     * will ignore this parameter. Null is always allowed and should be treated the same as CENTER by
     * position-aware containers.
     * @param menuContent the content that should appear in the given menu item. Should have an Anchor element as its only
     * direct child, or should be an {@link AnchorListItem} which is a convenient shorthand for an Anchor
     * inside a ListItem.
     */
    void addMenuItem( MenuPosition position, Widget menuContent );

}
