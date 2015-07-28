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
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.widgets.common./**/client.common.NumericShortTextBox;

/**
 * A Popup Text Editor for Short values.
 */
public class PopupNumericShortEditCell extends
                                       AbstractPopupEditCell<Short, Short> {

    private final TextBox textBox = new NumericShortTextBox();

    public PopupNumericShortEditCell( boolean isReadOnly ) {
        super( isReadOnly );

        // Tabbing out of the TextBox commits changes
        textBox.addKeyDownHandler( new KeyDownHandler() {

            public void onKeyDown( KeyDownEvent event ) {
                boolean keyTab = event.getNativeKeyCode() == KeyCodes.KEY_TAB;
                boolean keyEnter = event.getNativeKeyCode() == KeyCodes.KEY_ENTER;
                if ( keyEnter || keyTab ) {
                    commit();
                }
            }

        } );

        vPanel.add( textBox );
    }

    @Override
    public void render( Context context,
                        Short value,
                        SafeHtmlBuilder sb ) {
        if ( value != null ) {
            sb.append( renderer.render( value.toString() ) );
        }
    }

    // Commit the change
    @Override
    protected void commit() {

        // Update value
        String text = textBox.getValue();
        Short number = null;
        if ( text.length() > 0 ) {
            try {
                number = new Short( text );
            } catch ( NumberFormatException e ) {
                number = new Short( "0" );
            }
        }
        setValue( lastContext,
                  lastParent,
                  number );
        if ( valueUpdater != null ) {
            valueUpdater.update( number );
        }
        panel.hide();
    }

    // Start editing the cell
    @Override
    protected void startEditing( final Context context,
                                 final Element parent,
                                 final Short value ) {

        textBox.setValue( ( value == null ? "" : value.toString() ) );

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
                        String text = textBox.getValue();
                        textBox.setFocus( true );
                        textBox.setCursorPos( text.length() );
                        textBox.setSelectionRange( 0,
                                                   text.length() );
                    }

                } );
            }
        } );

    }

}
