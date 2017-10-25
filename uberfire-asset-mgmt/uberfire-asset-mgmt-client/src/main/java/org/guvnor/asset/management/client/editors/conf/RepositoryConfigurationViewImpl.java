/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.asset.management.client.editors.conf;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class RepositoryConfigurationViewImpl extends Composite implements RepositoryConfigurationPresenter.RepositoryConfigurationView {

    interface Binder
            extends UiBinder<Widget, RepositoryConfigurationViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @Inject
    private PlaceManager placeManager;

    private RepositoryConfigurationPresenter presenter;

    @UiField
    public ListBox chooseRepositoryBox;

    @UiField
    public Button configureButton;

    @UiField
    public TextBox sourceBranchText;

    @UiField
    public TextBox releaseBranchText;

    @UiField
    public TextBox devBranchText;

    @UiField
    public TextBox versionText;

    @UiField
    public TextBox currentVersionText;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);

    public RepositoryConfigurationViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(final RepositoryConfigurationPresenter presenter) {
        this.presenter = presenter;

        configureButton.setText(constants.Configure_Repository());
        sourceBranchText.setText("master");
        devBranchText.setText("dev");
        releaseBranchText.setText("release");
        currentVersionText.setReadOnly(true);
        chooseRepositoryBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                String value = chooseRepositoryBox.getSelectedValue();
                GWT.log(value);

                presenter.loadRepositoryStructure(value);
            }
        });
        presenter.loadRepositories();
    }

    @UiHandler("configureButton")
    public void configureButton(ClickEvent e) {

        presenter.configureRepository(chooseRepositoryBox.getSelectedValue(),
                                      sourceBranchText.getText(),
                                      devBranchText.getText(),
                                      releaseBranchText.getText(),
                                      versionText.getText());
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public ListBox getChooseRepositoryBox() {
        return chooseRepositoryBox;
    }

    @Override
    public void setCurrentVersionText(final String text) {
        currentVersionText.setText(text);
    }

    @Override
    public void setVersionText(final String text) {
        versionText.setText(text);
    }
}
