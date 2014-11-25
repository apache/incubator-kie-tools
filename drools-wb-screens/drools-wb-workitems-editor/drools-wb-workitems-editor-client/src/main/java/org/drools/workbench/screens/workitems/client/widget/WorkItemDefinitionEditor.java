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

package org.drools.workbench.screens.workitems.client.widget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import org.drools.workbench.screens.workitems.client.resources.WorkItemsEditorResources;

/**
 * This is the Work Item definition editor widget.
 */
public class WorkItemDefinitionEditor extends Composite {

    private final TextArea textArea = new TextArea();

    public WorkItemDefinitionEditor() {
        textArea.setWidth( "100%" );
        textArea.setVisibleLines( 25 );
        textArea.setStyleName( WorkItemsEditorResources.INSTANCE.CSS().defaultTextArea() );
        textArea.getElement().setAttribute( "spellcheck",
                                            "false" );

        textArea.addKeyDownHandler( new KeyDownHandler() {

            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_TAB ) {
                    event.preventDefault();
                    event.stopPropagation();
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

    public void setContent( final String definition ) {
        textArea.setText( definition );
    }

    public void insertText( final String text ) {
        insertText( text,
                    false );
    }

    public void insertText( final String text,
                            final boolean isSpecialPaste ) {
        String textToInsert = text;
        final int i = textArea.getCursorPos();
        final String left = textArea.getText().substring( 0,
                                                          i );
        final String right = textArea.getText().substring( i,
                                                           textArea.getText().length() );

        int cursorPosition = left.toCharArray().length;
        if ( isSpecialPaste ) {
            int p = text.indexOf( "|" );
            if ( p > -1 ) {
                cursorPosition += p;
                textToInsert = textToInsert.replaceAll( "\\|",
                                                        "" );
            }
        }

        textArea.setFocus( true );
        textArea.setText( left + textToInsert + right );
        textArea.setCursorPos( cursorPosition );
    }

    public String getContent() {
        return textArea.getValue();
    }

}
