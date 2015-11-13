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

package org.uberfire.ext.layout.editor.client.components;

import javax.enterprise.event.Event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.InputSize;
import org.uberfire.ext.layout.editor.client.dnd.GridValueValidator;
import org.uberfire.workbench.events.NotificationEvent;

public class GridLayoutDragComponent extends InternalDragComponent {

    private final Event<NotificationEvent> ufNotification;

    private String span;

    public GridLayoutDragComponent( final String span,
                                    final Event<NotificationEvent> ufNotification ) {
        this.span = span;
        this.ufNotification = ufNotification;
    }

    public String label() {
        return span;
    }

    @Override
    public IsWidget getDragWidget() {
        final TextBox textBox = GWT.create( TextBox.class );
        textBox.setText( span );
        textBox.addBlurHandler( new BlurHandler() {
            @Override
            public void onBlur( BlurEvent event ) {
                GridValueValidator grid = new GridValueValidator();
                if ( !grid.isValid( textBox.getText() ) ) {
                    ufNotification.fire( new NotificationEvent( grid.getValidationError(), NotificationEvent.NotificationType.ERROR ) );
                    returnToOldValue( span, textBox );
                } else {
                    updateValue( textBox );
                }
            }
        } );
        textBox.setSize( InputSize.SMALL );
        return textBox;
    }

    private void updateValue( TextBox textBox ) {
        this.span = textBox.getText();
    }

    private void returnToOldValue( String V,
                                   TextBox textBox ) {
        textBox.setText( V );
    }
}
