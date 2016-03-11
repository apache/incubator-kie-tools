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

package org.uberfire.client.docks.view.items;

import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
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

    void configureIcon( Button itemButton, ImageResource imageResource ) {
        if ( getDock().getIconType() != null ) {
            itemButton.setIcon( getIcon() );
            itemButton.setIconFixedWidth( true );
            itemButton.setIconPosition( IconPosition.LEFT );
        } else {
            configureImageIcon( itemButton, imageResource );
        }
    }

    void configureImageIcon( final Button itemButton,
                             final ImageResource imageResource ) {
        if ( imageResource != null ) {
            final Image imageIcon = new Image( imageResource );
            imageIcon.getElement().getStyle().setWidth( 14, Style.Unit.PX );
            imageIcon.getElement().getStyle().setHeight( 14, Style.Unit.PX );

            if ( itemButton.getText() != null && !itemButton.getText().isEmpty() ) {
                imageIcon.getElement().getStyle().setPosition( Style.Position.ABSOLUTE );
                imageIcon.getElement().getStyle().setTop( 3, Style.Unit.PX );
                imageIcon.getElement().getStyle().setLeft( 3, Style.Unit.PX );
                
                itemButton.getElement().getStyle().setPaddingLeft( 20, Style.Unit.PX );
                itemButton.getElement().getStyle().setPosition( Style.Position.RELATIVE );
            }

            itemButton.insert( imageIcon, 0 );
        }
    }

    private IconType getIcon() {
        if (dock.getIconType() == null) {
            return null;
        }

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
