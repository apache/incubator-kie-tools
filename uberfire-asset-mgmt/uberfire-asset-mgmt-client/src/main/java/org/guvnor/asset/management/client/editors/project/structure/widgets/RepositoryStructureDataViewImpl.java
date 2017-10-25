/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.asset.management.client.editors.project.structure.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.i18n.Constants;
import org.gwtbootstrap3.client.ui.FormControlStatic;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Legend;

public class RepositoryStructureDataViewImpl
        extends Composite
        implements RepositoryStructureDataView {

    interface NewRepositoryStructureDataViewImplUIBinder
            extends UiBinder<Widget, RepositoryStructureDataViewImpl> {

    }

    private static NewRepositoryStructureDataViewImplUIBinder uiBinder = GWT.create(NewRepositoryStructureDataViewImplUIBinder.class);

    @UiField
    FormControlStatic groupIdTextBox;

    @UiField
    FormControlStatic artifactIdTextBox;

    @UiField
    FormControlStatic versionTextBox;

    @UiField
    FormLabel groupIdTextBoxHelpInline;

    @UiField
    FormLabel artifactIdTextBoxHelpInline;

    @UiField
    FormLabel versionTextBoxHelpInline;

    @UiField
    Legend projectTypeLabel;

    public RepositoryStructureDataViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setGroupId(final String groupId) {
        groupIdTextBox.setText(groupId);
    }

    @Override
    public String getGroupId() {
        return groupIdTextBox.getText();
    }

    @Override
    public void setArtifactId(final String artifactId) {
        artifactIdTextBox.setText(artifactId);
    }

    @Override
    public String getArtifactId() {
        return artifactIdTextBox.getText();
    }

    @Override
    public void setVersion(final String version) {
        versionTextBox.setText(version);
    }

    @Override
    public String getVersion() {
        return versionTextBox.getText();
    }

    @Override
    public void setEditUnmanagedRepositoryText() {
        projectTypeLabel.setText(Constants.INSTANCE.Repository_structure_view_edit_unmanaged_projectTypeLabel());
    }

    @Override
    public void setEditModuleVisibility(final boolean visible) {
        groupIdTextBox.setVisible(visible);
        groupIdTextBoxHelpInline.setVisible(visible);
        artifactIdTextBox.setVisible(visible);
        artifactIdTextBoxHelpInline.setVisible(visible);
        versionTextBox.setVisible(visible);
        versionTextBoxHelpInline.setVisible(visible);
    }

    @Override
    public void setEditMultiModuleProjectText() {
        projectTypeLabel.setText(Constants.INSTANCE.Repository_structure_view_edit_multi_projectTypeLabel());
        groupIdTextBoxHelpInline.setText(Constants.INSTANCE.Repository_structure_view_edit_multi_groupIdTextBoxHelpInline());
        artifactIdTextBoxHelpInline.setText(Constants.INSTANCE.Repository_structure_view_edit_multi_artifactIdTextBoxHelpInline());
        versionTextBoxHelpInline.setText(Constants.INSTANCE.Repository_structure_view_edit_multi_versionTextBoxHelpInline());
    }

    @Override
    public void setEditSingleModuleProjectText() {
        projectTypeLabel.setText(Constants.INSTANCE.Repository_structure_view_edit_single_projectTypeLabel());
        groupIdTextBoxHelpInline.setText(Constants.INSTANCE.Repository_structure_view_edit_single_groupIdTextBoxHelpInline());
        artifactIdTextBoxHelpInline.setText(Constants.INSTANCE.Repository_structure_view_edit_single_artifactIdTextBoxHelpInline());
        versionTextBoxHelpInline.setText(Constants.INSTANCE.Repository_structure_view_edit_single_versionTextBoxHelpInline());
    }

    @Override
    public void setCreateStructureText() {
        projectTypeLabel.setText(Constants.INSTANCE.Repository_structure_view_create_projectTypeLabel());
        groupIdTextBoxHelpInline.setText(Constants.INSTANCE.Repository_structure_view_create_groupIdTextBoxHelpInline());
        artifactIdTextBoxHelpInline.setText(Constants.INSTANCE.Repository_structure_view_create_artifactIdTextBoxHelpInline());
        versionTextBoxHelpInline.setText(Constants.INSTANCE.Repository_structure_view_create_versionTextBoxHelpInline());
    }

    public void clear() {
        groupIdTextBox.setText(null);
        artifactIdTextBox.setText(null);
        versionTextBox.setText(null);
    }
}