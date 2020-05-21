/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import org.guvnor.common.services.shared.file.upload.FileManagerFields;
import org.guvnor.common.services.shared.file.upload.FileOperation;
import org.gwtbootstrap3.client.ui.Form;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.ext.widgets.common.client.common.FileUploadFormEncoder;

/**
 * This wraps a file uploader utility
 */
public class AttachmentFileWidget extends Composite {

    private FileUpload up;
    private final Form form = GWT.create(Form.class);
    private final HorizontalPanel fields = GWT.create(HorizontalPanel.class);

    private final TextBox fieldFilePath = getHiddenField(FileManagerFields.FORM_FIELD_PATH,
                                                         "");
    private final TextBox fieldFileName = getHiddenField(FileManagerFields.FORM_FIELD_NAME,
                                                         "");
    private final TextBox fieldFileFullPath = getHiddenField(FileManagerFields.FORM_FIELD_FULL_PATH,
                                                             "");
    private final TextBox fieldFileOperation = getHiddenField(FileManagerFields.FORM_FIELD_OPERATION,
                                                              "");

    private Command successCallback;
    private Command errorCallback;
    private String[] validFileExtensions;

    private ClickHandler uploadButtonClickHanlder;

    private FileUploadFormEncoder formEncoder = new FileUploadFormEncoder();

    public AttachmentFileWidget() {
        setup(false);
    }

    public AttachmentFileWidget(final String[] validFileExtensions) {
        setup(false);
        setAccept(validFileExtensions);
    }

    public AttachmentFileWidget(final boolean addFileUpload) {
        setup(addFileUpload);
    }

    public AttachmentFileWidget(final String[] validFileExtensions,
                                final boolean addFileUpload) {
        setup(addFileUpload);
        setAccept(validFileExtensions);
    }

    void setup(boolean addFileUpload) {
        up = createUploadWidget(addFileUpload);
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        formEncoder.addUtf8Charset(form);

        // Validation is not performed in a SubmitHandler as it fails to be invoked with GWT-Bootstrap3. See:-
        // - https://issues.jboss.org/browse/GUVNOR-2302 and
        // - the underlying cause https://github.com/gwtbootstrap3/gwtbootstrap3/issues/375
        // Validation is now performed prior to the form being submitted.

        form.addSubmitCompleteHandler(new Form.SubmitCompleteHandler() {

            @Override
            public void onSubmitComplete(final Form.SubmitCompleteEvent event) {
                if ("OK".equalsIgnoreCase(event.getResults())) {
                    executeCallback(successCallback);
                    showMessage(CommonConstants.INSTANCE.UploadSuccess());
                } else {
                    executeCallback(errorCallback);
                    if (event.getResults().contains("org.uberfire.java.nio.file.FileAlreadyExistsException")) {
                        showMessage(org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.ExceptionFileAlreadyExists0(fieldFileName.getText()));
                    } else if (event.getResults().contains("DecisionTableParseException")) {
                        showMessage(CommonConstants.INSTANCE.UploadGenericError());
                    } else {
                        showMessage(org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.ExceptionGeneric0(event.getResults()));
                    }
                }
                reset();
            }
        });

        fields.add(up);
        fields.add(fieldFilePath);
        fields.add(fieldFileName);
        fields.add(fieldFileFullPath);
        fields.add(fieldFileOperation);

        form.add(fields);

        initWidget(form);
    }

    //Package protected to support overriding for tests
    FileUpload createUploadWidget(boolean addFileUpload) {
        FileUpload up = new FileUpload(new org.uberfire.mvp.Command() {
            @Override
            public void execute() {
                uploadButtonClickHanlder.onClick(null);
            }
        },
                                       addFileUpload);
        up.setName(FileManagerFields.UPLOAD_FIELD_NAME_ATTACH);
        return up;
    }

    private void executeCallback(final Command callback) {
        if (callback == null) {
            return;
        }
        callback.execute();
    }

    public void reset() {
        form.reset();
    }

    public void submit(final Path context,
                       final String fileName,
                       final String targetUrl,
                       final Command successCallback,
                       final Command errorCallback) {
        submit(fileName,
               context.toURI(),
               FileOperation.CREATE,
               "",
               targetUrl,
               successCallback,
               errorCallback);
    }

    public void submit(final Path path,
                       final String targetUrl,
                       final Command successCallback,
                       final Command errorCallback) {
        submit("",
               "",
               FileOperation.UPDATE,
               path.toURI(),
               targetUrl,
               successCallback,
               errorCallback);
    }

    void submit(final String fileName,
                final String filePath,
                final FileOperation operation,
                final String fileFullPath,
                final String targetUrl,
                final Command successCallback,
                final Command errorCallback) {
        setCallbacks(successCallback,
                     errorCallback);

        fieldFileName.setText(fileName);
        fieldFilePath.setText(filePath);
        fieldFileOperation.setText(operation.toString());
        fieldFileFullPath.setText(fileFullPath);

        form.setAction(targetUrl);
        if (isValid()) {
            form.submit();
        }
    }

    //Package protected to support calls from tests
    void setCallbacks(final Command successCallback,
                      final Command errorCallback) {
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
    }

    //Package protected to support calls/overriding for tests
    boolean isValid() {
        final String fileName = up.getFilename();
        if (fileName == null || "".equals(fileName)) {
            showMessage(CommonConstants.INSTANCE.UploadSelectAFile());
            executeCallback(errorCallback);
            return false;
        }
        if (validFileExtensions != null && validFileExtensions.length != 0) {
            boolean isValid = false;
            for (String extension : validFileExtensions) {
                if (hasExtension(fileName,
                                 extension)) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                showMessage(CommonConstants.INSTANCE.UploadFileTypeNotSupported() + "\n\n" + CommonConstants.INSTANCE.UploadFileTypeSupportedExtensions0(makeValidFileExtensionsText()));
                executeCallback(errorCallback);
                return false;
            }
        }
        return true;
    }

    private boolean hasExtension(String fileName,
                                 String extension) {
        String dotExtension = "." + extension;
        // it ends with the correct extension
        return fileName.endsWith(dotExtension)
                // and the '.<extension>' is not the whole filename - which would make it a dot file without an extension
                && fileName.length() > dotExtension.length();
    }

    private String makeValidFileExtensionsText() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < validFileExtensions.length; i++) {
            sb.append("\"").append(validFileExtensions[i]).append(((i < validFileExtensions.length - 1 ? "\", " : "\"")));
        }
        return sb.toString();
    }

    private void setAccept(final String[] validFileExtensions) {
        this.validFileExtensions = validFileExtensions;
        final InputElement element = up.getElement().cast();
        element.setAccept(makeAcceptString(validFileExtensions));
    }

    private String makeAcceptString(final String[] validFileExtensions) {
        if (validFileExtensions == null || validFileExtensions.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (String fileExtension : validFileExtensions) {
            sb.append(fileExtension).append(",");
        }
        sb.substring(0,
                     sb.length() - 1);
        return sb.toString();
    }

    private TextBox getHiddenField(final String name,
                                   final String value) {
        final TextBox t = new TextBox();
        t.setName(name);
        t.setText(value);
        t.setVisible(false);
        return t;
    }

    public void addClickHandler(final ClickHandler clickHandler) {
        this.uploadButtonClickHanlder = clickHandler;
    }

    public void setEnabled(boolean b) {
        up.setEnabled(b);
    }

    public void showMessage(String message) {
        Window.alert(message);
    }

    public String getFilenameSelectedToUpload() {
        return up.getFilename();
    }
}

