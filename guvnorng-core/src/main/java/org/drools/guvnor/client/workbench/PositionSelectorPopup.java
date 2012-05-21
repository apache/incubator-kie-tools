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
import org.drools.guvnor.client.workbench.events.AddWorkbenchPanelEvent;
import org.drools.guvnor.client.workbench.events.FocusReceivedEvent;
import org.drools.guvnor.client.workbench.events.FocusReceivedEvent.FocusReceivedEventHandler;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Popup to select a location to add a new widget
 */
public class PositionSelectorPopup extends PopupPanel
    implements
    FocusReceivedEventHandler {

    private ListBox        positionChoices = new ListBox();

    private WorkbenchPanel target;

    private final EventBus eventBus;

    private int            widgetCounter   = 1;

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

    public PositionSelectorPopup(final EventBus eventBus) {
        this.eventBus = eventBus;
        initChoices();
        add( positionChoices );

        eventBus.addHandler( FocusReceivedEvent.TYPE,
                             this );
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

                final String title = position.toString() + " [" + (widgetCounter++) + "]";
                final Widget widget = new Image( GuvnorResources.INSTANCE.logo() );

                eventBus.fireEvent( new AddWorkbenchPanelEvent( title,
                                                                target,
                                                                position,
                                                                widget ) );
            }

        } );
    }

    @Override
    public void onFocusReceived(FocusReceivedEvent event) {
        target = event.getWorkbenchPanel();
    }

    @Override
    public void show() {
        positionChoices.setSelectedIndex( 0 );
        super.show();
    }
    
    

}
