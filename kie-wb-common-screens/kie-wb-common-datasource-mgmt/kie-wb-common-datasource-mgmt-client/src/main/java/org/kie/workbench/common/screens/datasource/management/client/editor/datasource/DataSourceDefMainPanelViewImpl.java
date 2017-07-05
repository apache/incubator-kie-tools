/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.editor.datasource;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.commons.data.Pair;

import static org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants.DataSourceDefMainPanelViewImpl_emptyOption;
import static org.kie.workbench.common.screens.datasource.management.client.util.UIUtil.clearSpanMessage;
import static org.kie.workbench.common.screens.datasource.management.client.util.UIUtil.setGroupOnError;
import static org.kie.workbench.common.screens.datasource.management.client.util.UIUtil.setSpanMessage;

@Dependent
@Templated
public class DataSourceDefMainPanelViewImpl
        implements DataSourceDefMainPanelView,
                   IsElement {

    @Inject
    @DataField("name-form-group")
    private Div nameFormGroup;

    @Inject
    @DataField("name")
    private TextInput nameTextBox;

    @Inject
    @DataField("name-help")
    private Span nameHelp;

    @Inject
    @DataField("connection-url-form-group")
    private Div connectionURLFormGroup;

    @Inject
    @DataField("connection-url")
    private TextInput connectionURLTextBox;

    @Inject
    @DataField("connection-url-help")
    private Span connectionURLHelp;

    @Inject
    @DataField("user-form-group")
    private Div userFormGroup;

    @Inject
    @DataField("user")
    private TextInput userTextBox;

    @Inject
    @DataField("user-help")
    private Span userHelp;

    @Inject
    @DataField("password-form-group")
    private Div passwordFormGroup;

    @Inject
    @DataField("password")
    private TextInput passwordTextBox;

    @Inject
    @DataField("password-help")
    private Span passwordHelp;

    @Inject
    @DataField("driver-form-group")
    private Div driverFormGroup;

    @Inject
    @DataField("driver-selector")
    private Select driverSelector;

    @Inject
    @DataField("driver-selector-help")
    private Span driverSelectorHelp;

    @Inject
    @DataField("test-connection-button")
    private Button testConnection;

    @Inject
    private TranslationService translationService;

    private DataSourceDefMainPanelView.Presenter presenter;

    public DataSourceDefMainPanelViewImpl() {
    }

    @Override
    public void init(final DataSourceDefMainPanelView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setName(final String name) {
        this.nameTextBox.setValue(name);
    }

    @Override
    public String getName() {
        return nameTextBox.getValue();
    }

    public void setNameErrorMessage(final String message) {
        setGroupOnError(nameFormGroup,
                        true);
        setSpanMessage(nameHelp,
                       message);
    }

    public void clearNameErrorMessage() {
        setGroupOnError(nameFormGroup,
                        false);
        clearSpanMessage(nameHelp);
    }

    @Override
    public String getConnectionURL() {
        return connectionURLTextBox.getValue();
    }

    @Override
    public void setConnectionURL(final String connectionURL) {
        this.connectionURLTextBox.setValue(connectionURL);
    }

    @Override
    public void setConnectionURLErrorMessage(String message) {
        setGroupOnError(connectionURLFormGroup,
                        true);
        setSpanMessage(connectionURLHelp,
                       message);
    }

    @Override
    public void clearConnectionURLErrorMessage() {
        setGroupOnError(connectionURLFormGroup,
                        false);
        clearSpanMessage(connectionURLHelp);
    }

    @Override
    public String getUser() {
        return userTextBox.getValue();
    }

    @Override
    public void setUser(final String user) {
        this.userTextBox.setValue(user);
    }

    @Override
    public void setUserErrorMessage(String message) {
        setGroupOnError(userFormGroup,
                        true);
        setSpanMessage(userHelp,
                       message);
    }

    @Override
    public void clearUserErrorMessage() {
        setGroupOnError(userFormGroup,
                        false);
        clearSpanMessage(userHelp);
    }

    @Override
    public String getPassword() {
        return passwordTextBox.getValue();
    }

    @Override
    public void setPassword(final String password) {
        this.passwordTextBox.setValue(password);
    }

    @Override
    public void setPasswordErrorMessage(String message) {
        setGroupOnError(passwordFormGroup,
                        true);
        setSpanMessage(passwordHelp,
                       message);
    }

    @Override
    public void clearPasswordErrorMessage() {
        setGroupOnError(passwordFormGroup,
                        false);
        clearSpanMessage(passwordHelp);
    }

    @Override
    public String getDriver() {
        return driverSelector.getValue();
    }

    @Override
    public void setDriver(final String driver) {
        driverSelector.setValue(driver);
    }

    @Override
    public void setDriverErrorMessage(final String message) {
        setGroupOnError(driverFormGroup,
                        true);
        setSpanMessage(driverSelectorHelp,
                       message);
    }

    @Override
    public void clearDriverErrorMessage() {
        setGroupOnError(driverFormGroup,
                        false);
        clearSpanMessage(driverSelectorHelp);
    }

    @Override
    public void loadDriverOptions(final List<Pair<String, String>> driverOptions,
                                  final boolean addEmptyOption) {
        clear(driverSelector);
        if (addEmptyOption) {
            driverSelector.add(newOption(translationService.getTranslation(DataSourceDefMainPanelViewImpl_emptyOption),
                                         ""));
        }
        for (Pair<String, String> optionPair : driverOptions) {
            driverSelector.add(newOption(optionPair.getK1(),
                                         optionPair.getK2()));
        }
    }

    @EventHandler("name")
    private void onNameChange(@ForEvent("change") final Event event) {
        presenter.onNameChange();
    }

    @EventHandler("connection-url")
    private void onConnectionURLChange(@ForEvent("change") final Event event) {
        presenter.onConnectionURLChange();
    }

    @EventHandler("user")
    private void onUserChange(@ForEvent("change") final Event event) {
        presenter.onUserChange();
    }

    @EventHandler("password")
    private void onPasswordChange(@ForEvent("change") final Event event) {
        presenter.onPasswordChange();
    }

    @EventHandler("driver-selector")
    private void onDriverChange(@ForEvent("change") final Event event) {
        presenter.onDriverChange();
    }

    @EventHandler("test-connection-button")
    private void onTestConnection(@ForEvent("click") final Event event) {
        presenter.onTestConnection();
    }

    private Option newOption(final String text,
                             final String value) {
        final Option option = (Option) Window.getDocument().createElement("option");
        option.setTextContent(text);
        option.setValue(value);
        return option;
    }

    private void clear(final Select select) {
        for (int i = 0; i < select.getOptions().getLength(); i++) {
            select.remove(i);
        }
        select.setInnerHTML("");
    }
}