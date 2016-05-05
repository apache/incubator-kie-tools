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
package org.uberfire.ext.apps.client.home.components.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.uberfire.ext.apps.api.Directory;
import org.uberfire.ext.apps.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.mvp.ParameterizedCommand;

public class NewDirectoryPopup
        extends BaseModal {

    private ParameterizedCommand clickCommand;

    interface Binder
            extends
            UiBinder<Widget, NewDirectoryPopup> {

    }

    @UiField
    FormGroup directoryNameControlGroup;

    @UiField
    TextBox directoryName;

    @UiField
    HelpBlock directoryNameInline;

    private DirectoryNameValidator directoryNameValidator;

    private static Binder uiBinder = GWT.create( Binder.class );

    public NewDirectoryPopup( final Directory currentDirectory ) {
        setTitle( CommonConstants.INSTANCE.CreateDir() );
        setBody( uiBinder.createAndBindUi( NewDirectoryPopup.this ) );

        add( new ModalFooterOKCancelButtons(
                     new Command() {
                         @Override
                         public void execute() {
                             okButton();
                         }
                     },
                     new Command() {
                         @Override
                         public void execute() {
                             cancelButton();
                         }
                     } )
           );
        directoryNameValidator = new DirectoryNameValidator( currentDirectory );
    }

    public void show( ParameterizedCommand clickCommand ) {
        this.clickCommand = clickCommand;
        show();
    }

    private void cancelButton() {
        closePopup();
    }

    private void okButton() {
        if ( directoryNameValidator.isValid( directoryName.getText() ) ) {
            this.clickCommand.execute( directoryName.getText() );
            closePopup();
        } else {
            directoryNameControlGroup.setValidationState( ValidationState.ERROR );
            directoryNameInline.setText( directoryNameValidator.getValidationError() );
        }
    }

    private void closePopup() {
        this.directoryName.setText( "" );
        hide();
        super.hide();
    }

}
