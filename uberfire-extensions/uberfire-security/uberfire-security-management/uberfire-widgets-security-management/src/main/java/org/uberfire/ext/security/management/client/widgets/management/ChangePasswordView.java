/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.form.validator.Validator;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;

/**
 * <p>View implementation for changing a user's password.</p>
 *           
 * @since 0.8.0
 */
@Dependent
public class ChangePasswordView extends Composite
        implements
        ChangePassword.View {

    interface ChangePasswordViewBinder
            extends
            UiBinder<FlowPanel, ChangePasswordView> {

    }

    private static ChangePasswordViewBinder uiBinder = GWT.create(ChangePasswordViewBinder.class);

    @UiField
    FlowPanel mainPanel;
    
    @UiField
    Form changePasswordForm;
    
    @UiField
    FormGroup newPasswordFormGroup;

    @UiField
    Input newPasswordBox;

    @UiField
    FormGroup repeatNewPasswordFormGroup;

    @UiField
    Input repeatNewPasswordBox;
    
    @UiField
    Button clearButton;

    @UiField
    Button updateButton;

    private final BaseModal modal = new BaseModal();
    private ChangePassword presenter;

    @Override
    public void init(final ChangePassword presenter) {
        this.presenter = presenter;
        initWidget( uiBinder.createAndBindUi( this ) );
        modal.setBody(this);
    }

    @Override
    public ChangePassword.View configure(final Validator<String> newPasswordBoxValidator,
                                    final Validator<String> repeatNewPasswordBoxValidator) {
        newPasswordBox.addValidator(newPasswordBoxValidator);
        repeatNewPasswordBox.addValidator(repeatNewPasswordBoxValidator);
        return this;
    }

    @Override
    public ChangePassword.View show(final String username) {
        modal.setTitle(username);
        showModal();
        return this;
    }

    @Override
    public ChangePassword.View hide() {
        modal.hide();
        return this;
    }

    @Override
    public ChangePassword.View clear() {
        changePasswordForm.reset();
        updateButton.state().reset();
        return this;
    }
    
    private void showModal() {
        modal.show();
    }

    @UiHandler( "clearButton" )
    public void onClear( final ClickEvent event ) {
        clear();
    }

    @UiHandler( "updateButton" )
    public void onUpdate( final ClickEvent event ) {
        updateButton.state().loading();
        final boolean isValid = changePasswordForm.validate();
        if (isValid && presenter.validatePasswordsMatch(newPasswordBox.getText(), repeatNewPasswordBox.getText())) {
            presenter.onUpdatePassword(newPasswordBox.getValue(), callback);
        } else {
            updateButton.state().reset();
        }
    }

    private final Command callback = new Command() {
        @Override
        public void execute() {
            clear();
        }
    };

}