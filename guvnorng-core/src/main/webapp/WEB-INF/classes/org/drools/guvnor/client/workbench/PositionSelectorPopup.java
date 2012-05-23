/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.workbench;

import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.workbench.widgets.panels.PanelManager;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Popup to select a location to add a new widget
 */
public class PositionSelectorPopup extends PopupPanel {

    private ListBox positionChoices = new ListBox();

    private int     widgetCounter   = 1;

    public enum Position {
        NONE(
                "---None---"),
        NORTH,
        SOUTH,
        EAST,
        WEST,
        SELF;

        private String displayName;

        Position() {
            String s = super.toString();
            this.displayName = s.substring( 0,
                                            1 ) + s.substring( 1 ).toLowerCase();
        }

        Position(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

    }

    public PositionSelectorPopup() {
        initChoices();
        add( positionChoices );
    }

    private void initChoices() {
        for ( Position p : Position.values() ) {
            positionChoices.addItem( p.toString(),
                                     p.name() );
        }
        positionChoices.addChangeHandler( new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                final int selectedIndex = positionChoices.getSelectedIndex();
                if ( selectedIndex == -1 ) {
                    return;
                }
                final String selection = positionChoices.getValue( selectedIndex );
                Position position = Position.valueOf( selection );
                hide();

                //TODO {manstis} This can be any editor...
                final String title = position.toString() + " [" + (widgetCounter++) + "]";
                final Widget widget = new Image( GuvnorResources.INSTANCE.logo() );

                PanelManager.getInstance().addWorkbenchPanel( new WorkbenchPart( widget,
                                                                                 title ),
                                                              position );
            }

        } );
    }

    @Override
    public void show() {
        positionChoices.setSelectedIndex( 0 );
        super.show();
    }

}
