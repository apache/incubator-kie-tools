/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.m2repo.client.upload;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import org.guvnor.m2repo.client.resources.i18n.M2RepoEditorConstants;
import org.guvnor.m2repo.model.HTMLFileManagerFields;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.gwtbootstrap3.client.ui.gwt.FormPanel;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.ext.widgets.common.client.common.FormStyleItem;
import org.uberfire.ext.widgets.common.client.common.FormStyleLayout;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

public class UploadFormViewImpl
        extends BaseModal implements UploadFormView {

    private FormStyleLayout form = new FormStyleLayout();

    private final TextBox hiddenGroupIdField = GWT.create(TextBox.class);
    private final TextBox hiddenArtifactIdField = GWT.create(TextBox.class);
    private final TextBox hiddenVersionIdField = GWT.create(TextBox.class);
    private FormStyleItem groupIdItem;
    private FormStyleItem artifactIdItem;
    private FormStyleItem versionIdItem;

    private Presenter presenter;

    protected FileUpload uploader;

    public UploadFormViewImpl() {
        this.setTitle(M2RepoEditorConstants.INSTANCE.ArtifactUpload());
        this.setBody(doUploadForm());
        this.add(new ModalFooter() {{
            add(new Button(M2RepoEditorConstants.INSTANCE.Cancel()) {{
                addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        hide();
                    }
                });
            }});
        }});
    }

    private Form doUploadForm() {
        form.setAction(getWebContext() + "/maven2");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        form.setType(FormType.HORIZONTAL);

        form.addSubmitCompleteHandler(new Form.SubmitCompleteHandler() {
            public void onSubmitComplete(final Form.SubmitCompleteEvent event) {
                presenter.handleSubmitComplete(event);
            }
        });

        uploader = new FileUpload(() -> {
            if (presenter.isFileNameValid()) {
                form.submit();
            }
        });

        uploader.setName(HTMLFileManagerFields.UPLOAD_FIELD_NAME_ATTACH);

        hiddenGroupIdField.setName(HTMLFileManagerFields.GROUP_ID);
        hiddenArtifactIdField.setName(HTMLFileManagerFields.ARTIFACT_ID);
        hiddenVersionIdField.setName(HTMLFileManagerFields.VERSION_ID);

        form.addAttribute("File",
                          uploader);
        groupIdItem = form.addAttribute("Group IDENTIFIER",
                                        hiddenGroupIdField);
        artifactIdItem = form.addAttribute("Artifact IDENTIFIER",
                                           hiddenArtifactIdField);
        versionIdItem = form.addAttribute("Version IDENTIFIER",
                                          hiddenVersionIdField);

        hideGAVInputs();

        return form;
    }

    private String getWebContext() {
        String context = GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/",
                                                        "");
        if (context.endsWith("/")) {
            context = context.substring(0,
                                        context.length() - 1);
        }
        return context;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showUploadingBusy() {
        BusyPopup.showMessage(M2RepoEditorConstants.INSTANCE.Uploading());
    }

    @Override
    public void hideUploadingBusy() {
        BusyPopup.close();
    }

    @Override
    public void showSelectFileUploadWarning() {
        showMessage(M2RepoEditorConstants.INSTANCE.SelectFileUpload());
    }

    @Override
    public void showUnsupportedFileTypeWarning() {
        showMessage(M2RepoEditorConstants.INSTANCE.UnsupportedFileType());
    }

    @Override
    public void showUploadedSuccessfullyMessage() {
        showMessage(M2RepoEditorConstants.INSTANCE.UploadedSuccessfully());
    }

    @Override
    public void showInvalidJarNoPomWarning() {
        showMessage(M2RepoEditorConstants.INSTANCE.InvalidJarNotPom());
    }

    @Override
    public void showInvalidPomWarning() {
        showMessage(M2RepoEditorConstants.INSTANCE.InvalidPom());
    }

    @Override
    public void showUploadFailedError(final String message) {
        showErrorMessage(M2RepoEditorConstants.INSTANCE.UploadFailed() + message);
    }

    @Override
    public void showGAVInputs() {
        hiddenArtifactIdField.setVisible(true);
        hiddenGroupIdField.setVisible(true);
        hiddenVersionIdField.setVisible(true);
        toggleFormStyleItem(groupIdItem,
                            true);
        toggleFormStyleItem(artifactIdItem,
                            true);
        toggleFormStyleItem(versionIdItem,
                            true);
    }

    @Override
    public void hideGAVInputs() {
        toggleFormStyleItem(groupIdItem,
                            false);
        toggleFormStyleItem(artifactIdItem,
                            false);
        toggleFormStyleItem(versionIdItem,
                            false);
        hideTextBox(hiddenArtifactIdField);
        hideTextBox(hiddenGroupIdField);
        hideTextBox(hiddenVersionIdField);
    }

    private void toggleFormStyleItem(final FormStyleItem item,
                                     final boolean toggle) {
        if (item != null) {
            item.setVisible(toggle);
        }
    }

    private void hideTextBox(final TextBox textBox) {
        textBox.setText(null);
        textBox.setVisible(false);
    }

    @Override
    public String getFileName() {
        return uploader.getFilename();
    }

    @Override
    public void hide() {
        super.hide();
        uploader.clear();
    }

    @Override
    public void removeFromParent() {
        super.removeFromParent();
        uploader.clear();
    }

    private void showMessage(final String message) {
        Window.alert(message);
    }

    private void showErrorMessage(final String message) {
        ErrorPopup.showMessage(message);
    }
}