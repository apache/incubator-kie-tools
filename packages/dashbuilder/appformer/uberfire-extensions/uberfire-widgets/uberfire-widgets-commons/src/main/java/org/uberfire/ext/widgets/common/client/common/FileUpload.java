/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.uberfire.mvp.Command;

public class FileUpload extends Composite {

    public static final String FAKEPATH = "C:\\\\fakepath\\\\";    
    private static FileUploadBinder uiBinder = GWT.create(FileUploadBinder.class);
    private final Command command;
    @UiField
    InputGroupAddon uploadButton;

    @UiField
    InputGroupAddon chooseButton;

    @UiField
    Input file;

    @UiField
    Input fileText;

    @UiField
    Input inputFileName;

    private boolean isDisabled = false;

    public FileUpload() {
        this(null,
             false);
    }

    public FileUpload(final Command command) {
        this(command, true);
    }

    public FileUpload(final Command command,
                      boolean displayUploadButton) {
        initWidget(uiBinder.createAndBindUi(this));
        this.command = command;
        fileText.setReadOnly(true);
        fileText.setName("inputFileName");

        file.addChangeHandler(getFileChangeHandler());

        chooseButton.addDomHandler(event -> ((InputElement) file.getElement().cast()).click(),
                ClickEvent.getType());

        if (displayUploadButton) {
            uploadButton.addDomHandler(event -> {
                if (isDisabled) {
                    return;
                }
                if (command != null) {
                    command.execute();
                }

            }, ClickEvent.getType());

        } else {
            uploadButton.removeFromParent();
            uploadButton = null;
        }
    }

    public void setName(final String name) {
        file.setName(name);
    }

    public void upload() {
        command.execute();
    }

    public String getFilename() {
        var value = file.getValue();
        return value == null ? null : value.replaceAll(FAKEPATH, "");
    }

    public void setAccept(String type) {
        file.getElement().setAttribute("accept", type);
    }

    public void clear() {
        file.setValue("");
        fileText.setText("");
    }

    public void setEnabled(boolean b) {
        if (uploadButton == null) {
            return;
        }
        if (!b) {
            isDisabled = true;
            uploadButton.addStyleName("disabled");
        } else {
            isDisabled = false;
            uploadButton.removeStyleName("disabled");
        }
    }

    public void setInputFileName(String text) {
        inputFileName.setValue(text);
    }

    protected ChangeHandler getFileChangeHandler() {
        return (event) -> {
            var fileName = file.getValue().replaceAll(FAKEPATH, "");
            setInputFileName(fileName);
            fileText.setValue(fileName);
        };
    }

    interface FileUploadBinder extends UiBinder<Widget, FileUpload> {

    }
}
