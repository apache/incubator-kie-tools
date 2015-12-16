/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.globals.client.editor;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.globals.client.resources.i18n.GlobalsEditorConstants;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class AddGlobalPopup extends BaseModal {

    interface AddGlobalPopupBinder
            extends
            UiBinder<Widget, AddGlobalPopup> {

    }

    private static AddGlobalPopupBinder uiBinder = GWT.create( AddGlobalPopupBinder.class );

    @UiField
    FormGroup aliasGroup;

    @UiField
    TextBox aliasTextBox;

    @UiField
    HelpBlock aliasHelpInline;

    @UiField
    FormGroup classNameGroup;

    @UiField
    ListBox classNameListBox;

    @UiField
    HelpBlock classNameHelpInline;

    private Command callbackCommand;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            onOKButtonClick();
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand,
                                                                                      cancelCommand );

    public AddGlobalPopup() {
        setTitle( GlobalsEditorConstants.INSTANCE.addGlobalPopupTitle() );

        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( AddGlobalPopup.this ) );
        }} );
        add( footer );

        aliasTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                aliasGroup.setValidationState( ValidationState.NONE );
                aliasHelpInline.setText( "" );
            }
        } );
    }

    private void onOKButtonClick() {
        boolean hasError = false;
        if ( aliasTextBox.getText() == null || aliasTextBox.getText().trim().isEmpty() ) {
            aliasGroup.setValidationState( ValidationState.ERROR );
            aliasHelpInline.setText( GlobalsEditorConstants.INSTANCE.aliasIsMandatory() );
            hasError = true;
        } else {
            aliasGroup.setValidationState( ValidationState.NONE );
        }

        if ( classNameListBox.getSelectedIndex() < 0 ) {
            classNameGroup.setValidationState( ValidationState.ERROR );
            classNameHelpInline.setText( GlobalsEditorConstants.INSTANCE.classNameIsMandatory() );
            hasError = true;
        } else {
            classNameGroup.setValidationState( ValidationState.NONE );
        }

        if ( hasError ) {
            return;
        }

        if ( callbackCommand != null ) {
            callbackCommand.execute();
        }
        hide();
    }

    public String getAlias() {
        return aliasTextBox.getText();
    }

    public String getClassName() {
        return classNameListBox.getSelectedValue();
    }

    public void setContent( final Command callbackCommand,
                            final List<String> fullyQualifiedClassNames ) {
        this.callbackCommand = callbackCommand;
        this.classNameListBox.clear();
        this.aliasTextBox.setText( "" );
        for ( String className : fullyQualifiedClassNames ) {
            classNameListBox.addItem( className );
        }
    }

}
