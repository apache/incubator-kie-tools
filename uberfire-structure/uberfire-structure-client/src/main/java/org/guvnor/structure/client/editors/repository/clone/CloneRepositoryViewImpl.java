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

package org.guvnor.structure.client.editors.repository.clone;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.client.resources.i18n.CommonConstants;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;

@Dependent
public class CloneRepositoryViewImpl extends BaseModal implements CloneRepositoryView,
                                                                  HasCloseHandlers<CloneRepositoryViewImpl> {

    interface CloneRepositoryFormBinder
            extends
            UiBinder<Widget, CloneRepositoryViewImpl> {

    }

    private CloneRepositoryView.Presenter presenter;

    private static CloneRepositoryFormBinder uiBinder = GWT.create(CloneRepositoryFormBinder.class);

    @UiField
    Button clone;

    @UiField
    Button cancel;

    @UiField
    FormGroup organizationalUnitGroup;

    @UiField
    Select organizationalUnitDropdown;

    @UiField
    HelpBlock organizationalUnitHelpInline;

    @UiField
    FormGroup nameGroup;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpBlock nameHelpInline;

    @UiField
    FormGroup urlGroup;

    @UiField
    TextBox gitURLTextBox;

    @UiField
    HelpBlock urlHelpInline;

    @UiField
    TextBox usernameTextBox;

    @UiField
    Input passwordTextBox;

    @UiField
    FormLabel ouLabel;

    @UiField
    BaseModal popup;

    @UiHandler("clone")
    public void onCloneClick(final ClickEvent e) {
        presenter.handleCloneClick();
    }

    @UiHandler("cancel")
    public void onCancelClick(final ClickEvent e) {
        presenter.handleCancelClick();
    }

    @Override
    public void init(final CloneRepositoryView.Presenter presenter,
                     final boolean isOuMandatory) {
        this.presenter = presenter;

        setBody(uiBinder.createAndBindUi(this));
        setTitle(CoreConstants.INSTANCE.CloneRepository());

        ouLabel.setShowRequiredIndicator(isOuMandatory);

        nameTextBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(final KeyPressEvent event) {
                nameGroup.setValidationState(ValidationState.NONE);
                nameHelpInline.setText("");
            }
        });

        organizationalUnitDropdown.addValueChangeHandler(e -> {
            organizationalUnitGroup.setValidationState(ValidationState.NONE);
            organizationalUnitHelpInline.setText("");
        });

        gitURLTextBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(final KeyPressEvent event) {
                urlGroup.setValidationState(ValidationState.NONE);
                urlHelpInline.setText("");
            }
        });
    }

    @Override
    public void addOrganizationalUnitSelectEntry() {
        final Option option = new Option();
        option.setText(CoreConstants.INSTANCE.SelectEntry());
        organizationalUnitDropdown.add(option);
        organizationalUnitDropdown.refresh();
    }

    @Override
    public void addOrganizationalUnit(final OrganizationalUnit ou) {
        final String text = ou.getName();
        final String value = ou.getName();
        final Option option = new Option();
        option.setText(text);
        option.setValue(value);
        organizationalUnitDropdown.add(option);
        organizationalUnitDropdown.refresh();
    }

    @Override
    public void deleteOrganizationalUnit(final OrganizationalUnit ou) {
        Option optToDelete = null;
        for (int i = 0; i < organizationalUnitDropdown.getWidgetCount(); i++) {
            final Widget w = organizationalUnitDropdown.getWidget(i);
            if (w instanceof Option) {
                final Option o = (Option) w;
                if (o.getText().equals(ou.getName())) {
                    optToDelete = o;
                    break;
                }
            }
        }
        if (optToDelete != null) {
            organizationalUnitDropdown.remove(optToDelete);
            organizationalUnitDropdown.refresh();
        }
    }

    @Override
    public String getSelectedOrganizationalUnit() {
        return organizationalUnitDropdown.getValue();
    }

    @Override
    public boolean isGitUrlEmpty() {
        return gitURLTextBox.getText() == null || gitURLTextBox.getText().trim().isEmpty();
    }

    @Override
    public boolean isNameEmpty() {
        return nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty();
    }

    @Override
    public String getGitUrl() {
        return gitURLTextBox.getText().trim();
    }

    @Override
    public String getUsername() {
        return usernameTextBox.getText();
    }

    @Override
    public String getPassword() {
        return passwordTextBox.getText();
    }

    @Override
    public String getName() {
        return nameTextBox.getText();
    }

    @Override
    public void setName(final String name) {
        nameTextBox.setText(name);
    }

    @Override
    public void showUrlHelpMandatoryMessage() {
        urlHelpInline.setText(CoreConstants.INSTANCE.URLMandatory());
    }

    @Override
    public void showUrlHelpInvalidFormatMessage() {
        urlHelpInline.setText(CoreConstants.INSTANCE.InvalidUrlFormat());
    }

    @Override
    public void setUrlGroupType(final ValidationState state) {
        urlGroup.setValidationState(state);
    }

    @Override
    public void showNameHelpMandatoryMessage() {
        nameHelpInline.setText(CoreConstants.INSTANCE.RepositoryNaneMandatory());
    }

    @Override
    public void setNameGroupType(final ValidationState state) {
        nameGroup.setValidationState(state);
    }

    @Override
    public void showOrganizationalUnitHelpMandatoryMessage() {
        organizationalUnitHelpInline.setText(CoreConstants.INSTANCE.OrganizationalUnitMandatory());
    }

    @Override
    public void setOrganizationalUnitGroupType(final ValidationState state) {
        organizationalUnitGroup.setValidationState(state);
    }

    @Override
    public void setNameEnabled(final boolean enabled) {
        nameTextBox.setEnabled(enabled);
    }

    @Override
    public void setOrganizationalUnitEnabled(final boolean enabled) {
        organizationalUnitDropdown.setEnabled(enabled);
        organizationalUnitDropdown.refresh();
    }

    @Override
    public void setGitUrlEnabled(final boolean enabled) {
        gitURLTextBox.setEnabled(enabled);
    }

    @Override
    public void setUsernameEnabled(final boolean enabled) {
        usernameTextBox.setEnabled(enabled);
    }

    @Override
    public void setPasswordEnabled(final boolean enabled) {
        passwordTextBox.setEnabled(enabled);
    }

    @Override
    public void setCloneEnabled(final boolean enabled) {
        clone.setEnabled(enabled);
    }

    @Override
    public void setCancelEnabled(final boolean enabled) {
        cancel.setEnabled(enabled);
    }

    @Override
    public void setPopupCloseVisible(final boolean closeVisible) {
        setClosable(closeVisible);
    }

    @Override
    public void showBusyPopupMessage() {
        BusyPopup.showMessage(CoreConstants.INSTANCE.Cloning());
    }

    @Override
    public void closeBusyPopup() {
        BusyPopup.close();
    }

    @Override
    public boolean showAgreeNormalizeNameWindow(final String normalizedName) {
        return Window.confirm(CoreConstants.INSTANCE.RepositoryNameInvalid() + " \"" + normalizedName + "\". " + CoreConstants.INSTANCE.DoYouAgree());
    }

    @Override
    public void alertRepositoryCloned() {
        Window.alert(CoreConstants.INSTANCE.RepoCloneSuccess() + "\n\n" + CommonConstants.INSTANCE.IndexClonedRepositoryWarning());
    }

    @Override
    public void errorRepositoryAlreadyExist() {
        ErrorPopup.showMessage(CoreConstants.INSTANCE.RepoAlreadyExists());
    }

    @Override
    public void errorCloneRepositoryFail(final Throwable cause) {
        ErrorPopup.showMessage(CommonConstants.INSTANCE.RepoCloneFail() + " \n" + cause.getMessage());
    }

    @Override
    public void errorLoadOrganizationalUnitsFail(final Throwable cause) {
        ErrorPopup.showMessage(CoreConstants.INSTANCE.CantLoadOrganizationalUnits() + " \n" + cause.getMessage());
    }

    @Override
    public HandlerRegistration addCloseHandler(final CloseHandler<CloneRepositoryViewImpl> handler) {
        return addHandler(handler,
                          CloseEvent.getType());
    }

    @Override
    public void reset() {
        nameTextBox.setText("");
        nameGroup.setValidationState(ValidationState.NONE);
        nameHelpInline.setText("");

        organizationalUnitDropdown.setValue("");
        organizationalUnitDropdown.refresh();
        organizationalUnitGroup.setValidationState(ValidationState.NONE);
        organizationalUnitHelpInline.setText("");

        gitURLTextBox.setText("");
        urlGroup.setValidationState(ValidationState.NONE);
        urlHelpInline.setText("");

        usernameTextBox.setText("");
        passwordTextBox.setText("");
    }

    @Override
    public void show() {
        popup.show();
    }

    @Override
    public void hide() {
        popup.hide();
        CloseEvent.fire(this,
                        this);
    }
}
