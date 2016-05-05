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

package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.TextBox;

public class PropertyEditorPasswordTextBox extends AbstractPropertyEditorWidget {

    @UiField
    Input passwordTextBox;

    public PropertyEditorPasswordTextBox() {
        initWidget( uiBinder.createAndBindUi( this ) );
        passwordTextBox.addFocusHandler( new FocusHandler() {
            @Override
            public void onFocus( FocusEvent event ) {
                passwordTextBox.selectAll();
            }
        } );
    }

    public void setText( String text ) {
        passwordTextBox.setText( text );
    }

    public String getText() {
        return passwordTextBox.getText();
    }

    public void addKeyDownHandler( KeyDownHandler keyDownHandler ) {
        passwordTextBox.addKeyDownHandler( keyDownHandler );
    }

    public void addBlurHandler( BlurHandler blurHandler ) {
        passwordTextBox.addBlurHandler( blurHandler );
    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorPasswordTextBox> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}