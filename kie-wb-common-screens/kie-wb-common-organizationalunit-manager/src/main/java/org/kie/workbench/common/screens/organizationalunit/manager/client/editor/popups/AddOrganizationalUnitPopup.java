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

package org.kie.workbench.common.screens.organizationalunit.manager.client.editor.popups;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.organizationalunit.manager.client.resources.i18n.OrganizationalUnitManagerConstants;
import org.kie.workbench.common.services.shared.validation.ValidatorCallback;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithPayload;
import org.uberfire.client.common.popups.footers.ModalFooterOKCancelButtons;

public class AddOrganizationalUnitPopup extends Modal {

    interface AddOrganizationalUnitPopupBinder
            extends
            UiBinder<Widget, AddOrganizationalUnitPopup> {

    }

    private static AddOrganizationalUnitPopupBinder uiBinder = GWT.create( AddOrganizationalUnitPopupBinder.class );

    @UiField
    ControlGroup nameGroup;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpInline nameHelpInline;

    @UiField
    TextBox ownerTextBox;

    private Command callbackCommand;

    private CommandWithPayload<ValidatorCallback> validationCallbackCommand;

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

    public AddOrganizationalUnitPopup() {
        setTitle( OrganizationalUnitManagerConstants.INSTANCE.AddOrganizationalUnitPopupTitle() );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );

        nameTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                nameGroup.setType( ControlGroupType.NONE );
                nameHelpInline.setText( "" );
            }
        } );
    }

    private void onOKButtonClick() {
        nameGroup.setType( ControlGroupType.NONE );
        if ( nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty() ) {
            nameGroup.setType( ControlGroupType.ERROR );
            nameHelpInline.setText( OrganizationalUnitManagerConstants.INSTANCE.OrganizationalUnitNameIsMandatory() );
            return;
        }

        if ( validationCallbackCommand == null ) {
            onOKSuccess();
            return;
        }

        validationCallbackCommand.execute( new ValidatorCallback() {

            @Override
            public void onSuccess() {
                onOKSuccess();
            }

            @Override
            public void onFailure() {
                nameGroup.setType( ControlGroupType.ERROR );
                nameHelpInline.setText( OrganizationalUnitManagerConstants.INSTANCE.OrganizationalUnitAlreadyExists() );
            }
        } );

    }

    private void onOKSuccess() {
        if ( callbackCommand != null ) {
            callbackCommand.execute();
        }
        hide();
    }

    public String getOrganizationalUnitName() {
        return nameTextBox.getText();
    }

    public String getOrganizationalUnitOwner() {
        return ownerTextBox.getText();
    }

    public void setCallback( final Command callbackCommand ) {
        this.callbackCommand = callbackCommand;
    }

    public void setValidationCallbackCommand( final CommandWithPayload<ValidatorCallback> validationCallbackCommand ) {
        this.validationCallbackCommand = validationCallbackCommand;
    }

    @Override
    public void show() {
        nameTextBox.setText( "" );
        nameGroup.setType( ControlGroupType.NONE );
        nameHelpInline.setText( "" );
        ownerTextBox.setText( "" );
        super.show();
    }
}
