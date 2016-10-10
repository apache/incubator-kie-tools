/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.Iterator;
import java.util.Set;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
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
    FormGroup containerIdTextGroup;

    @UiField
    TextBox containerIdText;

    @UiField
    HelpBlock containerIdTextHelpInline;

    @UiField
    FormGroup containerAliasTextGroup;

    @UiField
    TextBox containerAliasText;

    @UiField
    HelpBlock containerAliasTextHelpInline;

    @UiField
    FormLabel serverTemplateLabel;

    @UiField
    FormGroup serverTemplateGroup;

    @UiField
    Select serverTemplateDropdown;

    @UiField
    HelpBlock serverTemplateHelpInline;

    @UiField
    CheckBox startContainerCheck;

    @UiField
    FormGroup startContainerRow;

    private Command callbackCommand;

    private ValidateExistingContainerCallback validateExistingContainerCallback;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {

            if ( isEmpty( containerIdText.getText() ) ) {
                containerIdTextGroup.setValidationState( ValidationState.ERROR );
                containerIdTextHelpInline.setText( ProjectEditorResources.CONSTANTS.FieldMandatory0( "ContainerId" ) );

                return;
            }

            if ( validateExistingContainerCallback != null && validateExistingContainerCallback.containerNameExists( containerIdText.getText() ) ) {
                containerIdTextGroup.setValidationState(ValidationState.ERROR);
                containerIdTextHelpInline.setText(ProjectEditorResources.CONSTANTS.ContainerIdAlreadyInUse());

                return;
            }

            if ( serverTemplateGroup.isVisible() && isEmpty(serverTemplateDropdown.getValue()) ) {
                serverTemplateGroup.setValidationState(ValidationState.ERROR);
                serverTemplateHelpInline.setText(ProjectEditorResources.CONSTANTS.FieldMandatory0( "Server template" ));

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

        final ModalBody modalBody = GWT.create(ModalBody.class);
        modalBody.add( uiBinder.createAndBindUi( DeploymentScreenPopupViewImpl.this ) );
        add( modalBody );
        add( footer );

    }

    @Override public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
        serverTemplateGroup.setVisible(false);
        containerIdText.setText(null);
        startContainerCheck.setValue(null);
        serverTemplateDropdown.setValue((String)null);
        final Iterator<Widget> options = serverTemplateDropdown.iterator();
        while(options.hasNext()){
            options.next();
            options.remove();
        }
        serverTemplateDropdown.refresh();
        validateExistingContainerCallback = null;

        containerIdTextGroup.setValidationState(ValidationState.NONE);
        containerIdTextHelpInline.setText("");

        serverTemplateGroup.setValidationState(ValidationState.NONE);
        serverTemplateHelpInline.setText("");
    }

    public void configure(Command command ) {
        this.callbackCommand = command;
    }

    public void addServerTemplates( final Set<String> serverTemplateIds ) {
        for (final String id : serverTemplateIds){
            final Option option = GWT.create(Option.class);
            option.setText( id );
            option.setValue( id );
            serverTemplateDropdown.add(option);
        }
        serverTemplateDropdown.refresh();
        serverTemplateGroup.setVisible(true);
    }

    public void setContainerId(final String containerId) {
        this.containerIdText.setText(containerId);
    }

    public String getContainerId() {
        return this.containerIdText.getText();
    }

    public void setContainerAlias(final String containerAlias) {
        this.containerAliasText.setText(containerAlias);
    }

    public String getContainerAlias() {
        return this.containerAliasText.getText();
    }

    public String getServerTemplate() {
        return this.serverTemplateDropdown.getValue();
    }

    public void setStartContainer(final boolean startContainer){
        startContainerCheck.setValue(startContainer);
    }

    public boolean getStartContainer() {
        return startContainerCheck.getValue();
    }

    public void setValidateExistingContainerCallback(final ValidateExistingContainerCallback validateExistingContainerCallback) {
        this.validateExistingContainerCallback = validateExistingContainerCallback;
    }

    interface ValidateExistingContainerCallback {

        boolean containerNameExists(String containerName);

    }

}
