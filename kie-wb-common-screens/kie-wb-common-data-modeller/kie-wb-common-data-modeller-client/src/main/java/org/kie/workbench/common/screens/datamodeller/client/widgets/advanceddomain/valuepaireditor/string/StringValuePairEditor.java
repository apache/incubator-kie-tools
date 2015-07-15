/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.string;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.ValuePairEditorUtil;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

public class StringValuePairEditor
        implements IsWidget,
        StringValuePairEditorView.Presenter,
        ValuePairEditor<String> {

    private StringValuePairEditorView view;

    private String currentValue;

    private AnnotationValuePairDefinition valuePairDefinition;

    private ValuePairEditorHandler editorHandler;

    public StringValuePairEditor() {
        view = GWT.create( StringValuePairEditorViewImpl.class );
        view.setPresenter( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void init( AnnotationValuePairDefinition valuePairDefinition ) {
        this.valuePairDefinition = valuePairDefinition;
        view.setValuePairLabel( ValuePairEditorUtil.buildValuePairLabel( valuePairDefinition ) );
    }

    @Override
    public void setValue( String value ) {
        view.setValue( value );
        this.currentValue = value;
    }

    @Override
    public String getValue( ) {
        return currentValue;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void clear() {
        view.clear();
    }

    @Override
    public void addEditorHandler( ValuePairEditorHandler editorHandler ) {
        this.editorHandler = editorHandler;
    }

    @Override
    public AnnotationValuePairDefinition getValuePairDefinition() {
        return valuePairDefinition;
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        //TODO implement if needed
    }

    @Override
    public void clearErrorMessage() {
        //TODO implement if needed
    }

    @Override
    public void showValidateButton( boolean show ) {
        //This editor doesn't need the validate button.
    }

    @Override
    public void showValuePairName( boolean show ) {
        view.showValuePairName( show );
    }

    @Override
    public void refresh() {
        //This editor doesn't need the refresh method.
    }

    @Override
    public void onValueChanged() {
        currentValue = view.getValue();
        if ( editorHandler != null ) {
            editorHandler.onValueChanged();
        }
    }
}
