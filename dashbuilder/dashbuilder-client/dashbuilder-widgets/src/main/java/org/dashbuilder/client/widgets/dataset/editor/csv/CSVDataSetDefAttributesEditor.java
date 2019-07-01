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
package org.dashbuilder.client.widgets.dataset.editor.csv;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.file.FileUploadEditor;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * <p>CSV Data Set specific attributes editor presenter.</p>
 * 
 * @since 0.4.0 
 */
@Dependent
public class CSVDataSetDefAttributesEditor implements IsWidget, org.dashbuilder.dataset.client.editor.CSVDataSetDefAttributesEditor {

    public interface View extends UberView<CSVDataSetDefAttributesEditor> {
        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(ValueBoxEditor.View fileURLView, IsWidget filePathView,
                         ValueBoxEditor.View sepCharView, ValueBoxEditor.View quoteCharView,
                         ValueBoxEditor.View escCharView, ValueBoxEditor.View datePatternView,
                         ValueBoxEditor.View numberPatternView);
        
        void showFilePathInput();

        void showFileURLInput();
        
    }

    DataSetClientServices dataSetClientServices;
    ValueBoxEditor<String> fileURL;
    FileUploadEditor filePath;
    ValueBoxEditor<Character> separatorChar;
    ValueBoxEditor<Character> quoteChar;
    ValueBoxEditor<Character> escapeChar;
    ValueBoxEditor<String> datePattern;
    ValueBoxEditor<String> numberPattern;
    public View view;
    CSVDataSetDef value;
    boolean isUsingFilePath = true;

    @Inject
    public CSVDataSetDefAttributesEditor(final DataSetClientServices dataSetClientServices,
                                         final ValueBoxEditor<String> fileURL,
                                         final FileUploadEditor filePath,
                                         final ValueBoxEditor<Character> separatorChar,
                                         final ValueBoxEditor<Character> quoteChar,
                                         final ValueBoxEditor<Character> escapeChar,
                                         final ValueBoxEditor<String> datePattern,
                                         final ValueBoxEditor<String> numberPattern,
                                         final View view) {
        this.dataSetClientServices = dataSetClientServices;
        this.fileURL = fileURL;
        this.filePath = filePath;
        this.separatorChar = separatorChar;
        this.quoteChar = quoteChar;
        this.escapeChar = escapeChar;
        this.datePattern = datePattern;
        this.numberPattern = numberPattern;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        // Initialize the Bean specific attributes editor view.
        view.init(this);
        view.initWidgets(fileURL.view, filePath.view, separatorChar.view, quoteChar.view,
                escapeChar.view, datePattern.view, numberPattern.view);
        fileURL.addHelpContent(DataSetEditorConstants.INSTANCE.csv_URL(),
                DataSetEditorConstants.INSTANCE.csv_URL_description(),
                Placement.BOTTOM);
        filePath.addHelpContent(DataSetEditorConstants.INSTANCE.csv_filePath(),
                DataSetEditorConstants.INSTANCE.csv_filePath_description(),
                Placement.BOTTOM);
        separatorChar.addHelpContent(DataSetEditorConstants.INSTANCE.csv_sepChar(),
                DataSetEditorConstants.INSTANCE.csv_sepChar_description(),
                Placement.BOTTOM);
        quoteChar.addHelpContent(DataSetEditorConstants.INSTANCE.csv_quoteChar(),
                DataSetEditorConstants.INSTANCE.csv_quoteChar_description(),
                Placement.BOTTOM);
        escapeChar.addHelpContent(DataSetEditorConstants.INSTANCE.csv_escapeChar(),
                DataSetEditorConstants.INSTANCE.csv_escapeChar_description(),
                Placement.BOTTOM);
        datePattern.addHelpContent(DataSetEditorConstants.INSTANCE.csv_datePattern(),
                DataSetEditorConstants.INSTANCE.csv_datePattern_description(),
                Placement.BOTTOM);
        numberPattern.addHelpContent(DataSetEditorConstants.INSTANCE.csv_numberPattern(),
                DataSetEditorConstants.INSTANCE.csv_numberPattern_description(),
                Placement.BOTTOM);

        // Configure file upload component.
        filePath.configure("csvFileUpload", new FileUploadEditor.FileUploadEditorCallback() {
            @Override
            public String getUploadFileName() {
                return value.getUUID() + ".csv";
            }

            @Override
            public String getUploadFileUrl() {
                String csvPath = "default://master@dashbuilder/datasets/tmp/" + value.getUUID() + ".csv";
                return dataSetClientServices.getUploadFileUrl(csvPath);
            }
        });
        
        // By default, show the file path input.
        view.showFilePathInput();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public ValueBoxEditor<String> fileURL() {
        return fileURL;
    }

    @Override
    public FileUploadEditor filePath() {
        return filePath;
    }

    @Override
    public ValueBoxEditor<Character> separatorChar() {
        return separatorChar;
    }

    @Override
    public ValueBoxEditor<Character> quoteChar() {
        return quoteChar;
    }

    @Override
    public ValueBoxEditor<Character> escapeChar() {
        return escapeChar;
    }

    @Override
    public ValueBoxEditor<String> datePattern() {
        return datePattern;
    }

    @Override
    public ValueBoxEditor<String> numberPattern() {
        return numberPattern;
    }

    @Override
    public boolean isUsingFilePath() {
        return isUsingFilePath;
    }

    @Override
    public void flush() {

    }

    @Override
    public void onPropertyChange(final String... paths) {

    }

    @Override
    public void setValue(final CSVDataSetDef value) {
        this.value = value;
        if (value != null && value.getFileURL() != null) {
            useFileURL();
        } else {
            useFilePath();
        }
    }

    @Override
    public void setDelegate(final EditorDelegate<CSVDataSetDef> delegate) {

    }

    /*************************************************************
     ** VIEW CALLBACK METHODS **
     *************************************************************/
    
    void onUseFilePathButtonClick() {
        useFilePath();
    }

    void onUseFileURLButtonClick() {
        useFileURL();
    }

    /*************************************************************
     ** PRIVATE PRESENTER METHODS **
     *************************************************************/
    
    void useFilePath() {
        this.isUsingFilePath = true;
        view.showFilePathInput();
    }
    
    void useFileURL() {
        this.isUsingFilePath = false;
        view.showFileURLInput();
    }
}
