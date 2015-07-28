/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.editor;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class DeploymentScreenPopupViewImpl extends BaseModal {

    interface DeploymentScreenPopupWidgetBinder
            extends
            UiBinder<Widget, DeploymentScreenPopupViewImpl> {

    }

    private DeploymentScreenPopupWidgetBinder uiBinder = GWT.create( DeploymentScreenPopupWidgetBinder.class );

    @Inject
    private User identity;

    @UiField
    FormGroup userNameTextGroup;

    @UiField
    TextBox userNameText;

    @UiField
    HelpBlock userNameTextHelpInline;

    @UiField
    FormGroup passwordTextGroup;

    @UiField
    Input passwordText;

    @UiField
    HelpBlock passwordTextHelpInline;

    @UiField
    FormGroup serverURLTextGroup;

    @UiField
    TextBox serverURLText;

    @UiField
    HelpBlock serverURLTextHelpInline;

    private Command callbackCommand;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {

            if ( isEmpty( userNameText.getText() ) ) {
                userNameTextGroup.setValidationState( ValidationState.ERROR );
                userNameTextHelpInline.setText( ProjectEditorResources.CONSTANTS.FieldMandatory0( "Username" ) );

                return;
            }

            if ( isEmpty( passwordText.getText() ) ) {
                passwordTextGroup.setValidationState( ValidationState.ERROR );
                passwordTextHelpInline.setText( ProjectEditorResources.CONSTANTS.FieldMandatory0( "Password" ) );

                return;
            }

            if ( isEmpty( serverURLText.getText() ) ) {
                serverURLTextGroup.setValidationState( ValidationState.ERROR );
                serverURLTextHelpInline.setText( ProjectEditorResources.CONSTANTS.FieldMandatory0( "ServerURL" ) );

                return;
            }

            if ( callbackCommand != null ) {
                callbackCommand.execute();
            }
            hide();
        }

        private boolean isEmpty( String value ) {
            if ( value == null || value.isEmpty() ) {
                return true;
            }

            return false;
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand, cancelCommand );

    public DeploymentScreenPopupViewImpl() {
        setTitle( ProjectEditorResources.CONSTANTS.BuildAndDeploy() );
        setDataBackdrop( ModalBackdrop.STATIC );
        setDataKeyboard( true );
        setFade( true );
        setRemoveOnHide( true );

        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( DeploymentScreenPopupViewImpl.this ) );
        }} );
        add( footer );
    }

    public void configure( Command command ) {
        this.callbackCommand = command;

        // set default values for the fields
        userNameText.setText( identity.getIdentifier() );
        serverURLText.setText( GWT.getModuleBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "" ) );
    }

    public String getUsername() {
        return this.userNameText.getText();
    }

    public String getPassword() {
        return this.passwordText.getText();
    }

    public String getServerURL() {
        return this.serverURLText.getText();
    }

}
