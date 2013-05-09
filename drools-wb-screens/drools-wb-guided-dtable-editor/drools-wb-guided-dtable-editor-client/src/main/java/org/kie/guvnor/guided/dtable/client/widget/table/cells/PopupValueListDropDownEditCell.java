/*
 * Copyright 2011 JBoss Inc
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
package org.kie.guvnor.guided.dtable.client.widget.table.cells;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import org.kie.workbench.widgets.decoratedgrid.client.widget.cells.AbstractPopupEditCell;
import org.kie.guvnor.commons.data.factconstraints.util.ConstraintValueEditorHelper;

/**
 * A Popup drop-down Editor ;-)
 */
public class PopupValueListDropDownEditCell extends
                                            AbstractPopupEditCell<String, String> {

    private final ListBox listBox;
    private String[][] items;

    public PopupValueListDropDownEditCell( final String[] items,
                                           final boolean isReadOnly ) {
        super( isReadOnly );

        this.listBox = new ListBox();
        setItems( items );

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

        //Render value
        if ( value != null ) {
            String label = getLabel( value );
            sb.append( renderer.render( label ) );
        }
    }

    // Set content of drop-down
    private void setItems( String[] items ) {
        this.listBox.clear();
        this.items = new String[ items.length ][ 2 ];
        for ( int i = 0; i < items.length; i++ ) {
            String item = items[ i ].trim();
            if ( item.indexOf( '=' ) > 0 ) {
                String[] splut = ConstraintValueEditorHelper.splitValue( item );
                this.items[ i ][ 0 ] = splut[ 0 ];
                this.items[ i ][ 1 ] = splut[ 1 ];
                this.listBox.addItem( splut[ 1 ],
                                      splut[ 0 ] );
            } else {
                this.items[ i ][ 0 ] = item;
                this.items[ i ][ 1 ] = item;
                this.listBox.addItem( item,
                                      item );
            }
        }
    }

    // Lookup the display text based on the value
    private String getLabel( String value ) {
        for ( int i = 0; i < this.items.length; i++ ) {
            if ( this.items[ i ][ 0 ].equals( value ) ) {
                return items[ i ][ 1 ];
            }
        }
        return value;
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
