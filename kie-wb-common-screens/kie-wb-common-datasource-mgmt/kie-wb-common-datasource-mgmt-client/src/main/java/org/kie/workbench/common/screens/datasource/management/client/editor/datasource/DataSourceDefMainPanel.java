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

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.commons.data.Pair;

@Dependent
public class DataSourceDefMainPanel
        implements DataSourceDefMainPanelView.Presenter,
                   IsElement {

    private DataSourceDefMainPanelView.Handler handler;

    private DataSourceDefMainPanelView view;

    @Inject
    public DataSourceDefMainPanel(final DataSourceDefMainPanelView view) {
        this.view = view;
        view.init(this);
    }

    public void setHandler(final DataSourceDefMainPanelView.Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onNameChange() {
        if (handler != null) {
            handler.onNameChange();
        }
    }

    @Override
    public void onConnectionURLChange() {
        if (handler != null) {
            handler.onConnectionURLChange();
        }
    }

    @Override
    public void onUserChange() {
        if (handler != null) {
            handler.onUserChange();
        }
    }

    @Override
    public void onPasswordChange() {
        if (handler != null) {
            handler.onPasswordChange();
        }
    }

    @Override
    public void onDriverChange() {
        if (handler != null) {
            handler.onDriverChange();
        }
    }

    @Override
    public void onTestConnection() {
        if (handler != null) {
            handler.onTestConnection();
        }
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setName(final String name) {
        view.setName(name);
    }

    public String getName() {
        return view.getName();
    }

    public void setNameErrorMessage(final String message) {
        view.setNameErrorMessage(message);
    }

    public void clearNameErrorMessage() {
        view.clearNameErrorMessage();
    }

    public String getConnectionURL() {
        return view.getConnectionURL();
    }

    void setConnectionURL(final String connectionURL) {
        view.setConnectionURL(connectionURL);
    }

    public void setConnectionURLErrorMessage(String message) {
        view.setConnectionURLErrorMessage(message);
    }

    public void clearConnectionURLErrorMessage() {
        view.clearConnectionURLErrorMessage();
    }

    public String getUser() {
        return view.getUser();
    }

    public void setUser(final String user) {
        view.setUser(user);
    }

    public void setUserErrorMessage(final String message) {
        view.setUserErrorMessage(message);
    }

    public void clearUserErrorMessage() {
        view.clearUserErrorMessage();
    }

    public String getPassword() {
        return view.getPassword();
    }

    public void setPassword(final String password) {
        view.setPassword(password);
    }

    public void setPasswordErrorMessage(final String message) {
        view.setPasswordErrorMessage(message);
    }

    public void clearPasswordErrorMessage() {
        view.clearPasswordErrorMessage();
    }

    public String getDriver() {
        return view.getDriver();
    }

    public void setDriver(final String driver) {
        view.setDriver(driver);
    }

    public void setDriverErrorMessage(final String message) {
        view.setDriverErrorMessage(message);
    }

    public void clearDriverErrorMessage() {
        view.clearDriverErrorMessage();
    }

    public void loadDriverOptions(final List<Pair<String, String>> driverOptions,
                                  final boolean addEmptyOption) {
        view.loadDriverOptions(driverOptions,
                               addEmptyOption);
    }

    public void clear() {
        view.setName(null);
        view.clearNameErrorMessage();

        view.setConnectionURL(null);
        view.clearConnectionURLErrorMessage();

        view.setUser(null);
        view.clearUserErrorMessage();

        view.setPassword(null);
        view.clearPasswordErrorMessage();

        view.setDriver("");
        view.clearDriverErrorMessage();
    }
}
