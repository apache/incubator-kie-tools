/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.widgets.configresource.client.widget.unbound;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.widgets.configresource.client.resources.i18n.ImportConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class AddImportPopup extends BaseModal {

    interface AddGlobalPopupBinder
            extends
            UiBinder<Widget, AddImportPopup> {

    }

    private static AddGlobalPopupBinder uiBinder = GWT.create( AddGlobalPopupBinder.class );

    @UiField
    FormGroup importTypeGroup;

    @UiField
    TextBox importTypeTextBox;

    @UiField
    HelpBlock importTypeHelpInline;

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

    public AddImportPopup() {
        setTitle( ImportConstants.INSTANCE.addImportPopupTitle() );

        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( AddImportPopup.this ) );
        }} );
        add( footer );

        importTypeTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                importTypeGroup.setValidationState( ValidationState.NONE );
                importTypeHelpInline.setText( "" );
            }
        } );
    }

    private void onOKButtonClick() {
        boolean hasError = false;
        if ( importTypeTextBox.getText() == null || importTypeTextBox.getText().trim().isEmpty() ) {
            importTypeGroup.setValidationState( ValidationState.ERROR );
            importTypeHelpInline.setText( ImportConstants.INSTANCE.importTypeIsMandatory() );
            hasError = true;
        } else {
            importTypeGroup.setValidationState( ValidationState.NONE );
        }

        if ( hasError ) {
            return;
        }

        if ( callbackCommand != null ) {
            callbackCommand.execute();
        }
        hide();
    }

    public String getImportType() {
        return importTypeTextBox.getValue();
    }

    public void setCommand( final Command callbackCommand ) {
        this.callbackCommand = callbackCommand;
    }

    @Override
    public void show() {
        importTypeTextBox.setText( "" );
        importTypeGroup.setValidationState( ValidationState.NONE );
        importTypeHelpInline.setText( "" );
        super.show();
    }

}
