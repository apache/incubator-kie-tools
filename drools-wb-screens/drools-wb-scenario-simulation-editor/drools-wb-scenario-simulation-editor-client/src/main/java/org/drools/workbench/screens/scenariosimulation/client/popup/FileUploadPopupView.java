/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.client.popup;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Templated
public class FileUploadPopupView extends AbstractScenarioPopupView implements FileUploadPopup {

    private static final String FAKEPATH = "c:\\fakepath\\";

    @DataField("file")
    protected InputElement file = Document.get().createHiddenInputElement();

    @DataField("fileText")
    protected InputElement fileText = Document.get().createTextInputElement();

    @DataField("chooseButton")
    protected SpanElement chooseButton = Document.get().createSpanElement();

    @DataField("upload-warning")
    protected ParagraphElement uploadWarning = Document.get().createPElement();

    protected List<String> acceptedExtension = new ArrayList<>();

    protected static String fileContents;

    protected static String fileName = null;

    public static void fileLoaded(String fileContents) {
        FileUploadPopupView.fileContents = fileContents;
    }

    @Override
    public void show(final String mainTitleText,
                     final String okButtonText,
                     final Command okCommand) {
        fileContents = "";
        fileText.setValue("");
        if (!acceptedExtension.isEmpty()) {
            file.setAccept(String.join(",", acceptedExtension));
        }
        uploadWarning.setInnerText(ScenarioSimulationEditorConstants.INSTANCE.uploadWarning());
        okButton.setEnabled(false);
        super.show(mainTitleText,
                   okButtonText, okCommand);
    }

    @Override
    public String getFileContents() {
        return fileContents;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setAcceptedExtension(List<String> acceptedExtension) {
        this.acceptedExtension.clear();
        this.acceptedExtension.addAll(acceptedExtension);
    }

    @EventHandler("chooseButton")
    public void onChooseButtonClickEvent(ClickEvent clickEvent) {
        file.click();
    }

    @EventHandler("file")
    public void onFileChangeEvent(ChangeEvent event) {
        fileName = file.getValue();
        if (fileName.toLowerCase().startsWith(FAKEPATH)) {
            fileName = fileName.substring(FAKEPATH.length());
        }
        fileText.setValue(fileName);
        JavaScriptObject files = file.getPropertyJSO("files");
        readTextFile(files);
        if (!"".equals(fileText.getValue())) {
            okButton.setEnabled(true);
        }
    }

    public static native void readTextFile(JavaScriptObject files)/*-{
        var reader = new FileReader();
        reader.onload = function (e) {
            @org.drools.workbench.screens.scenariosimulation.client.popup.FileUploadPopupView::fileLoaded(*)(reader.result);
        };
        return reader.readAsText(files[0]);
    }-*/;
}
