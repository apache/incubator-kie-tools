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

package org.uberfire.ext.security.management.client.widgets.management.editor.role;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;

public class RoleEditorView extends Composite implements RoleEditor.View {

    private static RoleEditorViewBinder uiBinder = GWT.create(RoleEditorViewBinder.class);
    @UiField
    FlowPanel aclPanel;
    @UiField
    FlowPanel aclSettingsPanel;
    @UiField
    Heading roleTitle;
    @UiField
    Button editButton;
    private RoleEditor presenter;

    @Override
    public void init(final RoleEditor presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public RoleEditor.View setRolename(String rolename) {
        String headerText = UsersManagementWidgetsConstants.INSTANCE.roleSettings(rolename);
        roleTitle.setText(headerText);
        roleTitle.setTitle(headerText);
        return this;
    }

    @Override
    public RoleEditor.View setEditButtonVisible(final boolean isVisible) {
        editButton.setVisible(isVisible);
        return this;
    }

    @Override
    public RoleEditor.View setACLSettings(IsWidget aclSettings) {
        aclSettingsPanel.clear();
        aclSettingsPanel.add(aclSettings);
        return this;
    }

    @Override
    public RoleEditor.View showACL(IsWidget aclViewer) {
        aclPanel.clear();
        aclPanel.add(aclViewer);
        return this;
    }

    @Override
    public RoleEditor.View editACL(IsWidget aclEditor) {
        aclPanel.clear();
        aclPanel.add(aclEditor);
        return this;
    }

    @UiHandler("editButton")
    public void onEditButtonClick(final ClickEvent event) {
        presenter.onEdit();
    }

    interface RoleEditorViewBinder extends UiBinder<Widget, RoleEditorView> {

    }
}
