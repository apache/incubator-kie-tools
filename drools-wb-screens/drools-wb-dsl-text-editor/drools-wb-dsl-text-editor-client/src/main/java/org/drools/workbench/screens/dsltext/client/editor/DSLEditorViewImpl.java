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

package org.drools.workbench.screens.dsltext.client.editor;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import org.drools.workbench.screens.dsltext.client.resources.DSLTextEditorResources;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.ResizableTextArea;

/**
 * The view for the Domain Specific Language editor
 */
public class DSLEditorViewImpl
        extends Composite
        implements RequiresResize,
                   DSLEditorView {

    private final ResizableTextArea dslText = new ResizableTextArea();

    private boolean isDirty;

    public DSLEditorViewImpl() {
        dslText.setWidth( "100%" );
        dslText.getElement().setAttribute( "spellcheck",
                                           "false" );
        dslText.setStyleName( DSLTextEditorResources.INSTANCE.CSS().defaultTextArea() );

        dslText.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                isDirty = true;
            }
        } );

        dslText.addKeyDownHandler( new KeyDownHandler() {

            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_TAB ) {
                    int pos = dslText.getCursorPos();
                    insertText( "\t" );
                    dslText.setCursorPos( pos + 1 );
                    dslText.cancelKey();
                    dslText.setFocus( true );
                }
            }
        } );

        initWidget( dslText );
    }

    private void insertText( final String ins ) {
        final int i = dslText.getCursorPos();
        final String left = dslText.getText().substring( 0,
                                                         i );
        final String right = dslText.getText().substring( i,
                                                          dslText.getText().length() );
        dslText.setText( left + ins + right );
    }

    @Override
    public void setContent( final String input ) {
        final String content;
        if ( input == null ) {
            content = "";
        } else {
            content = input;
        }
        dslText.setText( content );
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setNotDirty() {
        this.isDirty = false;
    }

    @Override
    public String getContent() {
        return dslText.getValue();
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

    @Override
    public void makeReadOnly() {
        dslText.setEnabled( false );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width,
                      height );
        dslText.onResize();
    }

    @Override
    public void alertReadOnly() {
        Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
