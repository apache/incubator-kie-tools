/*
 * Copyright 2012 JBoss Inc
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
import org.kie.guvnor.commons.data.factconstraints.util.ConstraintValueEditorHelper;
import org.kie.guvnor.datamodel.model.DropDownData;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellTableDropDownDataValueMapProvider;

/**
 * A Popup drop-down Editor ;-)
 */
public class PopupDropDownEditCell extends
                                   AbstractPopupEditCell<String, String> {

    private final ListBox    listBox;
    private       String[][] items;

    private final String factType;
    private final String factField;

    private final PackageDataModelOracle sce;
    private final CellTableDropDownDataValueMapProvider dropDownManager;

    public PopupDropDownEditCell( final String factType,
                                  final String factField,
                                  final PackageDataModelOracle sce,
                                  final CellTableDropDownDataValueMapProvider dropDownManager,
                                  final boolean isReadOnly ) {
        super( isReadOnly );
        this.factType = factType;
        this.factField = factField;
        this.dropDownManager = dropDownManager;
        this.sce = sce;

        this.listBox = new ListBox();

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

        //We need to get the list of potential values to lookup the "Display" value from the "Stored" value.
        //Since the content of the list may be different for each cell (dependent enumerations) the list
        //has to be populated "on demand". 
        DropDownData dd = sce.getEnums( this.factType,
                                        this.factField,
                                        this.dropDownManager.getCurrentValueMap( context ) );
        if ( dd == null ) {
            return;
        }
        setItems( dd.getFixedList() );

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

        //We need to get the list of potential values for the enumeration. Since the content 
        //of the list may be different for each cell (dependent enumerations) the list
        //has to be populated "on demand". 
        DropDownData dd = sce.getEnums( this.factType,
                                        this.factField,
                                        this.dropDownManager.getCurrentValueMap( context ) );
        if ( dd == null ) {
            return;
        }
        setItems( dd.getFixedList() );

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
