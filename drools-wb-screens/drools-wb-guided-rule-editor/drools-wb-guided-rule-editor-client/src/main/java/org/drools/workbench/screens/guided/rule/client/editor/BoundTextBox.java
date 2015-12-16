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

package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.gwtbootstrap3.client.ui.TextBox;

public class BoundTextBox extends TextBox {

    public BoundTextBox( final BaseSingleFieldConstraint c ) {
        setStyleName( "constraint-value-Editor" ); //NON-NLS
        if ( c.getValue() == null ) {
            setText( "" );
        } else {
            setText( c.getValue() );
        }

        String v = c.getValue();
        if ( c.getValue() == null || v.length() < 7 ) {
            ( (InputElement) getElement().cast() ).setSize( 8 );

        } else {
            ( (InputElement) getElement().cast() ).setSize( v.length() + 1 );
        }

        addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                c.setValue( getText() );
            }
        } );

        addKeyUpHandler( new KeyUpHandler() {
            public void onKeyUp( KeyUpEvent event ) {
                int length = getText().length();
                ( (InputElement) getElement().cast() ).setSize( length > 0 ? length : 1 );
            }
        } );
    }
}
