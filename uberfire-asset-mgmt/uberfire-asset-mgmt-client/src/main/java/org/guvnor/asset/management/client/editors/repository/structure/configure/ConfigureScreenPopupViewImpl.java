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

package org.guvnor.asset.management.client.editors.repository.structure.configure;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class ConfigureScreenPopupViewImpl extends BaseModal {

    interface ConfigureScreenPopupWidgetBinder
            extends
            UiBinder<Widget, ConfigureScreenPopupViewImpl> {

    }

    private ConfigureScreenPopupWidgetBinder uiBinder = GWT.create(ConfigureScreenPopupWidgetBinder.class);

    @Inject
    private User identity;

    @UiField
    FormGroup repositoryTextGroup;

    @UiField
    TextBox repositoryText;

    @UiField
    HelpBlock repositoryTextHelpBlock;

    @UiField
    FormGroup sourceBranchTextGroup;

    @UiField
    TextBox sourceBranchText;

    @UiField
    HelpBlock sourceBranchTextHelpBlock;

    @UiField
    FormGroup devBranchTextGroup;

    @UiField
    TextBox devBranchText;

    @UiField
    HelpBlock devBranchTextHelpBlock;

    @UiField
    FormGroup releaseBranchTextGroup;

    @UiField
    TextBox releaseBranchText;

    @UiField
    HelpBlock releaseBranchTextHelpBlock;

    @UiField
    HelpBlock versionTextHelpBlock;

    @UiField
    FormGroup versionTextGroup;

    @UiField
    TextBox versionText;

    private Command callbackCommand;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {

            if (isEmpty(devBranchText.getText())) {
                devBranchTextGroup.setValidationState(ValidationState.ERROR);
                devBranchTextHelpBlock.setText(Constants.INSTANCE.FieldMandatory0("Dev Branch"));

                return;
            }

            if (isEmpty(releaseBranchText.getText())) {
                releaseBranchTextGroup.setValidationState(ValidationState.ERROR);
                releaseBranchTextHelpBlock.setText(Constants.INSTANCE.FieldMandatory0("Release Branch"));

                return;
            }

            if (isEmpty(versionText.getText())) {
                versionTextGroup.setValidationState(ValidationState.ERROR);
                versionTextHelpBlock.setText(Constants.INSTANCE.FieldMandatory0("Version"));

                return;
            }

            if (callbackCommand != null) {
                callbackCommand.execute();
            }
            hide();
        }

        private boolean isEmpty(String value) {
            if (value == null || value.isEmpty()) {
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

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons(okCommand,
                                                                                     cancelCommand);

    public ConfigureScreenPopupViewImpl() {
        setTitle(Constants.INSTANCE.Configure_Repository());
        setDataBackdrop(ModalBackdrop.STATIC);
        setDataKeyboard(true);
        setFade(true);
        setRemoveOnHide(true);

        setBody(uiBinder.createAndBindUi(this));
        add(footer);
    }

    public void configure(String repositoryAlias,
                          String branch,
                          String repositoryVersion,
                          Command command) {
        this.callbackCommand = command;
        this.devBranchText.setText("dev");
        this.devBranchTextHelpBlock.setText("The branch will be called (dev)-" + repositoryVersion);
        this.releaseBranchText.setText("release");
        this.releaseBranchTextHelpBlock.setText("The branch will be called (release)-" + repositoryVersion);
        this.sourceBranchText.setText(branch);
        this.repositoryText.setText(repositoryAlias);
        this.sourceBranchText.setReadOnly(true);
        this.repositoryText.setReadOnly(true);
        this.versionTextHelpBlock.setText("The current repository version is: " + repositoryVersion);
        this.versionText.setText(repositoryVersion);
    }

    public String getDevBranch() {
        return this.devBranchText.getText();
    }

    public String getReleaseBranch() {
        return this.releaseBranchText.getText();
    }

    public String getVersion() {
        return this.versionText.getText();
    }
}
