/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.javaeditor.client.widget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import org.kie.uberfire.client.common.ResizableTextArea;

public class EditJavaSourceWidget extends Composite implements RequiresResize {

    private final ResizableTextArea textArea = new ResizableTextArea();

    private boolean isDirty;

    public EditJavaSourceWidget() {
        textArea.setWidth( "100%" );
        textArea.getElement().setAttribute( "spellcheck",
                "false" );

        Element element = textArea.getElement();
        DOM.setStyleAttribute( element, "fontFamily", "monospace" );

        textArea.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                isDirty = true;
            }
        } );

        textArea.addKeyDownHandler( new KeyDownHandler() {

            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_TAB ) {
                    int pos = textArea.getCursorPos();
                    insertText( "\t" );
                    textArea.setCursorPos( pos + 1 );
                    textArea.cancelKey();
                    textArea.setFocus( true );
                }
            }
        } );

        initWidget( textArea );
    }

    public void setContent( final String input ) {
        if ( input == null ) {
            textArea.setText( "" );
        } else {
            textArea.setText( input );
        }
    }

    public String getContent() {
        return textArea.getValue();
    }

    public void clearContent() {
        setContent( "" );
    }

    private void insertText( final String ins ) {
        final int i = textArea.getCursorPos();
        final String left = textArea.getText().substring( 0,
                i );
        final String right = textArea.getText().substring( i,
                textArea.getText().length() );
        textArea.setText( left + ins + right );
    }

    public void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setReadonly( boolean readonly ) {
        textArea.setReadOnly( readonly );
    }

    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return textArea.addChangeHandler( handler );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width,
                height );
        textArea.onResize();
    }
}
