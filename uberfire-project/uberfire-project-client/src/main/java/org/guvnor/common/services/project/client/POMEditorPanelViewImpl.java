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

package org.guvnor.common.services.project.client;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.client.resources.ProjectResources;
import org.guvnor.common.services.project.model.GAV;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.workbench.events.NotificationEvent;

public class POMEditorPanelViewImpl
        extends Composite
        implements POMEditorPanelView {

    private String tabTitleLabel = ProjectResources.CONSTANTS.ProjectModel();

    interface GroupArtifactVersionEditorPanelViewImplBinder
            extends
            UiBinder<Widget, POMEditorPanelViewImpl> {

    }

    private static GroupArtifactVersionEditorPanelViewImplBinder uiBinder = GWT.create(GroupArtifactVersionEditorPanelViewImplBinder.class);

    private Event<NotificationEvent> notificationEvent;

    @UiField
    TextBox pomNameTextBox;

    @UiField
    FormGroup pomNameGroup;

    @UiField
    HelpBlock pomNameHelpBlock;

    @UiField
    TextArea pomDescriptionTextArea;

    @UiField(provided = true)
    GAVEditor gavEditor;

    @UiField(provided = true)
    GAVEditor parentGavEditor;

    @UiField
    FieldSet parentGavEditorFieldSet;

    private Presenter presenter;

    public POMEditorPanelViewImpl() {
    }

    @Inject
    public POMEditorPanelViewImpl(final Event<NotificationEvent> notificationEvent,
                                  final GAVEditor parentGavEditor,
                                  final GAVEditor gavEditor) {
        this.notificationEvent = notificationEvent;
        this.parentGavEditor = parentGavEditor;
        this.gavEditor = gavEditor;

        initWidget(uiBinder.createAndBindUi(this));

        parentGavEditor.disableGroupID("");
        parentGavEditor.disableArtifactID("");
        parentGavEditor.disableVersion("");
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showSaveSuccessful(final String fileName) {
        notificationEvent.fire(new NotificationEvent(ProjectResources.CONSTANTS.SaveSuccessful(fileName)));
    }

    @Override
    public String getTitleWidget() {
        return tabTitleLabel;
    }

    @Override
    public void setName(final String projectName) {
        pomNameTextBox.setText(projectName);
    }

    @Override
    public void setDescription(final String projectDescription) {
        pomDescriptionTextArea.setText(projectDescription);
    }

    @Override
    public void setArtifactID(final String artifactID) {
        gavEditor.setArtifactID(artifactID);
    }

    @Override
    public void showParentGAV() {
        parentGavEditorFieldSet.setVisible(true);
    }

    @Override
    public void hideParentGAV() {
        parentGavEditorFieldSet.setVisible(false);
    }

    @Override
    public void setParentGAV(final GAV gav) {
        parentGavEditor.setGAV(gav);
    }

    @Override
    public void setGAV(final GAV gav) {
        gavEditor.setGAV(gav);
    }

    @Override
    public void addGroupIdChangeHandler(final GroupIdChangeHandler changeHandler) {
        gavEditor.addGroupIdChangeHandler(changeHandler);
    }

    @Override
    public void addArtifactIdChangeHandler(final ArtifactIdChangeHandler changeHandler) {
        gavEditor.addArtifactIdChangeHandler(changeHandler);
    }

    @Override
    public void addVersionChangeHandler(final VersionChangeHandler changeHandler) {
        gavEditor.addVersionChangeHandler(changeHandler);
    }

    @Override
    public void setReadOnly() {
        gavEditor.setReadOnly();
    }

    @Override
    public void disableGroupID(final String reason) {
        gavEditor.disableGroupID(reason);
    }

    @Override
    public void disableArtifactID(final String reason) {
        gavEditor.disableArtifactID(reason);
    }

    @Override
    public void disableVersion(final String reason) {
        gavEditor.disableVersion(reason);
    }

    @Override
    public void enableGroupID() {
        gavEditor.enableGroupID();
    }

    @Override
    public void enableArtifactID() {
        gavEditor.enableArtifactID();
    }

    @Override
    public void enableVersion() {
        gavEditor.enableVersion();
    }

    @Override
    public void setTitleText(final String titleText) {
        tabTitleLabel = titleText;
    }

    @Override
    public void setProjectModelTitleText() {
        tabTitleLabel = ProjectResources.CONSTANTS.ProjectModel();
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public void setValidName(final boolean isValid) {
        if (isValid) {
            pomNameGroup.setValidationState(ValidationState.NONE);
            pomNameHelpBlock.setText("");
        } else {
            pomNameGroup.setValidationState(ValidationState.ERROR);
            pomNameHelpBlock.setText(ProjectResources.CONSTANTS.invalidName());
        }
    }

    @Override
    public void setValidGroupID(final boolean isValid) {
        gavEditor.setValidGroupID(isValid);
    }

    @Override
    public void setValidArtifactID(final boolean isValid) {
        gavEditor.setValidArtifactID(isValid);
    }

    @Override
    public void setValidVersion(final boolean isValid) {
        gavEditor.setValidVersion(isValid);
    }

    @UiHandler("pomNameTextBox")
    //Use KeyUpEvent as ValueChangeEvent is only fired when the focus is lost
    public void onNameChange(final KeyUpEvent event) {
        presenter.onNameChange(pomNameTextBox.getText());
    }

    @UiHandler("openProjectContext")
    public void onOpenProjectContext(final ClickEvent event) {
        presenter.onOpenProjectContext();
    }

    @UiHandler("pomDescriptionTextArea")
    public void onDescriptionChange(final ValueChangeEvent<String> event) {
        presenter.onDescriptionChange(pomDescriptionTextArea.getText());
    }
}
