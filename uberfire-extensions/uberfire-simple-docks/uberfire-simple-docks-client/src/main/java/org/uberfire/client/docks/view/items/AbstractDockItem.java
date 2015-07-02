/*
 * Copyright 2015 JBoss Inc
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

package org.uberfire.client.docks.view.items;

import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;


public abstract class AbstractDockItem extends Composite {

    private final boolean selected;
    private final UberfireDock dock;

    AbstractDockItem(UberfireDock dock) {
        this.dock = dock;
        this.selected = false;
    }

    public static AbstractDockItem create(UberfireDock dock, ParameterizedCommand<String> selectCommand, ParameterizedCommand<String> deselectCommand) {
        if (dock.getDockPosition() == UberfireDockPosition.SOUTH) {
            return new SouthDockItem(dock, selectCommand, deselectCommand);
        } else {
            return new SideDockItem(dock, selectCommand, deselectCommand);
        }
    }

    protected IconType getIcon() {
        try {
            return IconType.valueOf(dock.getIconType());
        } catch (Exception e) {
            return IconType.FOLDER_OPEN;
        }
    }

    public UberfireDock getDock() {
        return dock;
    }

    public String getIdentifier() {
        return dock.getIdentifier();
    }

    public String getLabel() {
        return dock.getLabel();
    }

    public abstract void selectAndExecuteExpandCommand();

    public abstract void select();

    public abstract void deselect();

    public void setupDnD() {

    }
}
