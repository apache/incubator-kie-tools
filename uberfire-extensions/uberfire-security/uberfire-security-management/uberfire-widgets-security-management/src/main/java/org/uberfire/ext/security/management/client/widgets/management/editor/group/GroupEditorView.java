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

package org.uberfire.ext.security.management.client.widgets.management.editor.group;

import javax.enterprise.context.Dependent;

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

@Dependent
public class GroupEditorView extends Composite implements GroupEditor.View {

    private static GroupEditorViewBinder uiBinder = GWT.create(GroupEditorViewBinder.class);
    @UiField
    Heading groupTitle;
    @UiField
    Button editButton;
    @UiField
    Button deleteButton;
    @UiField
    FlowPanel aclPanel;
    @UiField
    FlowPanel aclSettingsPanel;
    GroupEditor presenter;

    @Override
    public void init(final GroupEditor presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public GroupEditor.View show(final String name) {
        String headerText = UsersManagementWidgetsConstants.INSTANCE.groupSettings(name);
        groupTitle.setText(headerText);
        groupTitle.setTitle(headerText);
        return this;
    }

    @Override
    public GroupEditor.View setDeleteButtonVisible(boolean isVisible) {
        deleteButton.setVisible(isVisible);
        return this;
    }

    @Override
    public GroupEditor.View clear() {
        groupTitle.setText("");
        deleteButton.setVisible(false);
        return this;
    }

    @Override
    public GroupEditor.View setEditButtonVisible(final boolean isVisible) {
        editButton.setVisible(isVisible);
        return this;
    }

    @Override
    public GroupEditor.View setACLSettings(IsWidget aclSettings) {
        aclSettingsPanel.clear();
        aclSettingsPanel.add(aclSettings);
        return this;
    }

    @Override
    public GroupEditor.View showACL(IsWidget aclViewer) {
        aclPanel.clear();
        aclPanel.add(aclViewer);
        return this;
    }

    @Override
    public GroupEditor.View editACL(IsWidget aclEditor) {
        aclPanel.clear();
        aclPanel.add(aclEditor);
        return this;
    }

    @UiHandler("editButton")
    public void onEditButtonClick(final ClickEvent event) {
        presenter.onEdit();
    }

    @UiHandler("deleteButton")
    public void onDeleteButtonClick(final ClickEvent event) {
        if (presenter != null) {
            presenter.onDelete();
        }
    }

    interface GroupEditorViewBinder
            extends
            UiBinder<Widget, GroupEditorView> {

    }
}