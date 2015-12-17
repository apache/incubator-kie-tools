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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

/**
 * A Popup drop-down Editor for the Dialect column attribute
 */
public class PopupDialectDropDownEditCell extends
                                          AbstractPopupEditCell<String, String> {

    private static String[] DIALECTS = { "java", "mvel" };

    private final ListBox listBox;

    public PopupDialectDropDownEditCell( boolean isReadOnly ) {
        super( isReadOnly );
        this.listBox = new ListBox();

        setItems( DIALECTS );

        // Tabbing out of the ListBox commits changes
        listBox.addKeyDownHandler( new KeyDownHandler() {

            public void onKeyDown( KeyDownEvent event ) {
                boolean keyTab = event.getNativeKeyCode() == KeyCodes.KEY_TAB;
                boolean keyEnter = event.getNativeKeyCode() == KeyCodes.KEY_ENTER;
                if ( keyEnter
                        || keyTab ) {
                    commit();
                }
            }

        } );

        vPanel.add( listBox );
    }

    @Override
    public void render( Context context,
                        String value,
                        SafeHtmlBuilder sb ) {
        if ( value != null ) {
            sb.append( renderer.render( value ) );
        }
    }

    // Set content of drop-down
    private void setItems( String[] items ) {
        for ( int i = 0; i < items.length; i++ ) {
            this.listBox.addItem( items[ i ] );
        }
    }

    // Commit the change
    @Override
    protected void commit() {

        // Update value
        String value = null;
        int selectedIndex = listBox.getSelectedIndex();
        if ( selectedIndex >= 0 ) {
            value = listBox.getValue( selectedIndex );
        }
        setValue( lastContext,
                  lastParent,
                  value );
        if ( valueUpdater != null ) {
            valueUpdater.update( value );
        }
        panel.hide();

    }

    // Start editing the cell
    @Override
    protected void startEditing( final Context context,
                                 final Element parent,
                                 final String value ) {

        // Select the appropriate item
        boolean emptyValue = ( value == null );
        if ( emptyValue ) {
            listBox.setSelectedIndex( 0 );
        } else {
            for ( int i = 0; i < listBox.getItemCount(); i++ ) {
                if ( listBox.getValue( i ).equals( value ) ) {
                    listBox.setSelectedIndex( i );
                    break;
                }
            }
        }

        panel.setPopupPositionAndShow( new PositionCallback() {
            public void setPosition( int offsetWidth,
                                     int offsetHeight ) {
                panel.setPopupPosition( parent.getAbsoluteLeft()
                                                + offsetX,
                                        parent.getAbsoluteTop()
                                                + offsetY );

                // Focus the first enabled control
                Scheduler.get().scheduleDeferred( new ScheduledCommand() {

                    public void execute() {
                        listBox.setFocus( true );
                    }

                } );
            }
        } );

    }

}
