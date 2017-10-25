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

package org.guvnor.ala.ui.wildfly.client.provider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.util.PopupHelper;
import org.guvnor.ala.ui.client.widget.FormStatus;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.guvnor.ala.ui.client.widget.StyleHelper.setFormStatus;
import static org.guvnor.ala.ui.wildfly.client.resources.i18n.GuvnorAlaWildflyUIConstants.WF10ProviderConfigView_AllParamsNeedsCompletionForValidationMessage;
import static org.guvnor.ala.ui.wildfly.client.resources.i18n.GuvnorAlaWildflyUIConstants.WF10ProviderConfigView_TestConnectionFailMessage;
import static org.guvnor.ala.ui.wildfly.client.resources.i18n.GuvnorAlaWildflyUIConstants.WF10ProviderConfigView_TestConnectionSuccessfulMessage;
import static org.guvnor.ala.ui.wildfly.client.resources.i18n.GuvnorAlaWildflyUIConstants.WF10ProviderConfigView_TestConnectionUnExpectedErrorMessage;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@Templated
public class WF10ProviderConfigView
        implements IsElement,
                   WF10ProviderConfigPresenter.View {

    @Inject
    @DataField("provider-name-form")
    private Div providerNameForm;

    @DataField("provider-type-name")
    private HTMLElement providerTypeName = Window.getDocument().createElement("strong");

    @Inject
    @DataField("provider-name")
    private TextInput name;

    @Inject
    @DataField("host-form")
    private Div hostForm;

    @Inject
    @DataField("host")
    private TextInput host;

    @Inject
    @DataField("port-form")
    private Div portForm;

    @Inject
    @DataField("port")
    private TextInput port;

    @Inject
    @DataField("management-port")
    private TextInput managementPort;

    @Inject
    @DataField("management-port-form")
    private Div managementPortForm;

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

    @Inject
    @DataField("test-connection-button")
    private Button testConnectionButton;

    @Inject
    private TranslationService translationService;

    @Inject
    private PopupHelper popupHelper;

    private WF10ProviderConfigPresenter presenter;

    @PostConstruct
    private void init() {
        providerTypeName.setTextContent(getWizardTitle());
    }

    @Override
    public void init(final WF10ProviderConfigPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getProviderName() {
        return name.getValue();
    }

    @Override
    public String getHost() {
        return host.getValue();
    }

    @Override
    public String getPort() {
        return port.getValue();
    }

    @Override
    public String getManagementPort() {
        return managementPort.getValue();
    }

    @Override
    public String getUsername() {
        return username.getValue();
    }

    @Override
    public String getPassword() {
        return password.getValue();
    }

    @Override
    public void setProviderName(String name) {
        this.name.setValue(name);
    }

    @Override
    public void setHost(String host) {
        this.host.setValue(host);
    }

    @Override
    public void setPort(String port) {
        this.port.setValue(port);
    }

    @Override
    public void setManagementPort(String managementPort) {
        this.managementPort.setValue(managementPort);
    }

    @Override
    public void setUsername(String username) {
        this.username.setValue(username);
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
    public void setHostStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(hostForm,
                      status);
    }

    @Override
    public void setPortStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(portForm,
                      status);
    }

    @Override
    public void setManagementPortStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(managementPortForm,
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
        this.name.setValue("");
        this.host.setValue("");
        this.port.setValue("");
        this.managementPort.setValue("");
        this.username.setValue("");
        this.password.setValue("");
    }

    @Override
    public String getWizardTitle() {
        //do not internationalize
        return "WildFly 10";
    }

    @Override
    public String getParamsNotCompletedErrorMessage() {
        return translationService.format(WF10ProviderConfigView_AllParamsNeedsCompletionForValidationMessage);
    }

    @Override
    public String getTestConnectionFailMessage(String content) {
        return translationService.format(WF10ProviderConfigView_TestConnectionFailMessage,
                                         content);
    }

    @Override
    public String getTestConnectionSuccessfulMessage(String content) {
        return translationService.format(WF10ProviderConfigView_TestConnectionSuccessfulMessage,
                                         content);
    }

    @Override
    public String getTestConnectionUnExpectedErrorMessage(String content) {
        return translationService.format(WF10ProviderConfigView_TestConnectionUnExpectedErrorMessage,
                                         content);
    }

    @Override
    public void showErrorPopup(String message) {
        popupHelper.showErrorPopup(message);
    }

    @Override
    public void showInformationPopup(String message) {
        popupHelper.showInformationPopup(message);
    }

    private void enable(boolean enabled) {
        this.name.setDisabled(!enabled);
        this.host.setDisabled(!enabled);
        this.port.setDisabled(!enabled);
        this.managementPort.setDisabled(!enabled);
        this.username.setDisabled(!enabled);
        this.password.setDisabled(!enabled);
        this.testConnectionButton.setDisabled(!enabled);
    }

    private void resetFormState() {
        setFormStatus(providerNameForm,
                      FormStatus.VALID);
        setFormStatus(hostForm,
                      FormStatus.VALID);
        setFormStatus(portForm,
                      FormStatus.VALID);
        setFormStatus(managementPortForm,
                      FormStatus.VALID);
        setFormStatus(usernameForm,
                      FormStatus.VALID);
        setFormStatus(passwordForm,
                      FormStatus.VALID);
    }

    @EventHandler("provider-name")
    private void onProviderNameChange(@ForEvent("change") final Event event) {
        presenter.onProviderNameChange();
    }

    @EventHandler("host")
    private void onHostChange(@ForEvent("change") final Event event) {
        presenter.onHostChange();
    }

    @EventHandler("port")
    private void onPortChange(@ForEvent("change") final Event event) {
        presenter.onPortChange();
    }

    @EventHandler("management-port")
    private void onManagementPortChange(@ForEvent("change") final Event event) {
        presenter.onManagementPortChange();
    }

    @EventHandler("username")
    private void onUsernameChange(@ForEvent("change") final Event event) {
        presenter.onUserNameChange();
    }

    @EventHandler("password")
    private void onPasswordChange(@ForEvent("change") final Event event) {
        presenter.onPasswordChange();
    }

    @EventHandler("test-connection-button")
    private void onTestConnection(@ForEvent("click") Event event) {
        presenter.onTestConnection();
    }
}