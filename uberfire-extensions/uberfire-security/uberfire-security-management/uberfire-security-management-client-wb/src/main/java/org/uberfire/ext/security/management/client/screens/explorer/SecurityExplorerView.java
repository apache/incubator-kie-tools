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

package org.uberfire.ext.security.management.client.screens.explorer;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class SecurityExplorerView extends Composite implements SecurityExplorerScreen.View {

    private static Binder uiBinder = GWT.create(Binder.class);
    @UiField
    TabListItem rolesTab;
    @UiField
    TabListItem groupsTab;
    @UiField
    TabListItem usersTab;
    @UiField
    TabPane rolesPane;
    @UiField
    TabPane groupsPane;
    @UiField
    TabPane usersPane;
    SecurityExplorerScreen presenter;

    @Override
    public void init(SecurityExplorerScreen presenter) {
        this.presenter = checkNotNull("presenter",
                                      presenter);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(SecurityExplorerScreen presenter,
                     IsWidget rolesExplorer,
                     IsWidget groupsExplorer,
                     IsWidget usersExplorer) {
        this.init(presenter);
        rolesPane.add(rolesExplorer);
        groupsPane.add(groupsExplorer);
        usersPane.add(usersExplorer);
    }

    @Override
    public void rolesEnabled(boolean enabled) {
        rolesTab.setEnabled(enabled);
    }

    @Override
    public void groupsEnabled(boolean enabled) {
        groupsTab.setEnabled(enabled);
    }

    @Override
    public void usersEnabled(boolean enabled) {
        usersTab.setEnabled(enabled);
    }

    @Override
    public void rolesActive(boolean active) {
        rolesTab.setActive(active);
        rolesPane.setActive(active);
    }

    @Override
    public void groupsActive(boolean active) {
        groupsTab.setActive(active);
        groupsPane.setActive(active);
    }

    @Override
    public void usersActive(boolean active) {
        usersTab.setActive(active);
        usersPane.setActive(active);
    }

    interface Binder extends UiBinder<Widget, SecurityExplorerView> {

    }
}