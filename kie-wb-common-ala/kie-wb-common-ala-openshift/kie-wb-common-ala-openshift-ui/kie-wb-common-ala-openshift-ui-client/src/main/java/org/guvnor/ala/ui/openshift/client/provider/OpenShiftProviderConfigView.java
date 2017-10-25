/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.openshift.client.provider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.widget.FormStatus;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.guvnor.ala.ui.client.util.UIUtil.EMPTY_STRING;
import static org.guvnor.ala.ui.client.widget.StyleHelper.setFormStatus;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@Templated
public class OpenShiftProviderConfigView
        implements IsElement,
                   OpenShiftProviderConfigPresenter.View {

    @DataField("provider-type-name")
    private HTMLElement providerTypeName = Window.getDocument().createElement("strong");

    @Inject
    @DataField("provider-name-form")
    private Div providerNameForm;

    @Inject
    @DataField("provider-name")
    private TextInput providerName;

    @Inject
    @DataField("master-url-form")
    private Div masterURLForm;

    @Inject
    @DataField("master-url")
    private TextInput masterURL;

    @Inject
    @DataField("username-form")
    private Div usernameForm;

    @Inject
    @DataField("username")
    private TextInput username;

    @Inject
    @DataField("password-form")
    private Div passwordForm;

    @Inject
    @DataField("password")
    private TextInput password;

    private OpenShiftProviderConfigPresenter presenter;

    @Override
    public void init(final OpenShiftProviderConfigPresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    private void init() {
        providerTypeName.setTextContent(getWizardTitle());
    }

    @Override
    public String getProviderName() {
        return providerName.getValue();
    }

    @Override
    public void setProviderName(String providerName) {
        this.providerName.setValue(providerName);
    }

    @Override
    public String getMasterURL() {
        return masterURL.getValue();
    }

    @Override
    public void setMasterURL(String masterURL) {
        this.masterURL.setValue(masterURL);
    }

    @Override
    public String getUsername() {
        return username.getValue();
    }

    @Override
    public void setUsername(String username) {
        this.username.setValue(username);
    }

    @Override
    public String getPassword() {
        return password.getValue();
    }

    @Override
    public void setPassword(String password) {
        this.password.setValue(password);
    }

    @Override
    public void disable() {
        resetFormState();
        enable(false);
    }

    @Override
    public void enable() {
        resetFormState();
        enable(true);
    }

    @Override
    public void setProviderNameStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(providerNameForm,
                      status);
    }

    @Override
    public void setMasterURLStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(masterURLForm,
                      status);
    }

    @Override
    public void setUsernameStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(usernameForm,
                      status);
    }

    @Override
    public void setPasswordStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(passwordForm,
                      status);
    }

    @Override
    public void clear() {
        resetFormState();
        this.providerName.setValue(EMPTY_STRING);
        this.masterURL.setValue(EMPTY_STRING);
        this.username.setValue(EMPTY_STRING);
        this.password.setValue(EMPTY_STRING);
    }

    @Override
    public String getWizardTitle() {
        //do not internationalize this value.
        return "OpenShift";
    }

    @EventHandler("provider-name")
    private void onProviderNameChange(@ForEvent("change") final Event event) {
        presenter.onProviderNameChange();
    }

    @EventHandler("master-url")
    private void onMasterURLChange(@ForEvent("change") final Event event) {
        presenter.onMasterURLChange();
    }

    @EventHandler("username")
    private void onUsernameChange(@ForEvent("change") final Event event) {
        presenter.onUserNameChange();
    }

    @EventHandler("password")
    private void onPasswordChange(@ForEvent("change") final Event event) {
        presenter.onPasswordChange();
    }

    private void resetFormState() {
        setFormStatus(providerNameForm,
                      FormStatus.VALID);
        setFormStatus(masterURLForm,
                      FormStatus.VALID);
        setFormStatus(usernameForm,
                      FormStatus.VALID);
        setFormStatus(passwordForm,
                      FormStatus.VALID);
    }

    private void enable(boolean enabled) {
        this.providerName.setDisabled(!enabled);
        this.masterURL.setDisabled(!enabled);
        this.username.setDisabled(!enabled);
        this.password.setDisabled(!enabled);
    }
}