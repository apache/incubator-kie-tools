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

package org.uberfire.user.management.client.popups;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
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
import org.uberfire.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.user.management.client.resources.i18n.UserManagementConstants;
import org.uberfire.user.management.client.utils.UserManagementUtils;
import org.uberfire.user.management.model.UserInformation;

public class EditUserRolesPopup extends Modal {

    interface EditUserRolesPopupBinder
            extends
            UiBinder<Widget, EditUserRolesPopup> {

    }

    private static EditUserRolesPopupBinder uiBinder = GWT.create( EditUserRolesPopupBinder.class );

    @UiField
    ControlGroup userNameGroup;

    @UiField
    Label userNameLabel;

    @UiField
    ControlGroup userRolesGroup;

    @UiField
    TextBox userRolesTextBox;

    @UiField
    HelpInline userRolesHelpInline;

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

    public EditUserRolesPopup() {
        setTitle( UserManagementConstants.INSTANCE.addUserPopupTitle() );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );

        userRolesTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                userRolesGroup.setType( ControlGroupType.NONE );
                userRolesHelpInline.setText( "" );
            }
        } );
    }

    private void onOKButtonClick() {
        boolean hasError = false;

        if ( userRolesTextBox.getText() == null || userRolesTextBox.getText().trim().isEmpty() ) {
            userRolesGroup.setType( ControlGroupType.ERROR );
            userRolesHelpInline.setText( UserManagementConstants.INSTANCE.userRolesIsMandatory() );
            hasError = true;
        } else {
            userRolesGroup.setType( ControlGroupType.NONE );
            userRolesHelpInline.setText( "" );
        }

        if ( hasError ) {
            return;
        }

        if ( callbackCommand != null ) {
            callbackCommand.execute();
        }
        hide();
    }

    @Override
    public void show() {
        userNameGroup.setType( ControlGroupType.NONE );
        userRolesGroup.setType( ControlGroupType.NONE );
        userRolesHelpInline.setText( "" );
        super.show();
    }

    public String getUserRoles() {
        return userRolesTextBox.getText();
    }

    public void setCallbackCommand( final Command callbackCommand ) {
        this.callbackCommand = callbackCommand;
    }

    public void setUserInformation( final UserInformation userInformation ) {
        this.userNameLabel.setText( userInformation.getUserName() );
        this.userRolesTextBox.setText( UserManagementUtils.convertUserRoles( userInformation.getUserRoles() ) );
    }

}
