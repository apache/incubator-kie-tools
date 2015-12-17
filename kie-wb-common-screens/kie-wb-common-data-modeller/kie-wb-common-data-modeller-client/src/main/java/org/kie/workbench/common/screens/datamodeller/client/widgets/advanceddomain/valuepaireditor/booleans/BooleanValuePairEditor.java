/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.booleans;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.ValuePairEditorUtil;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

public class BooleanValuePairEditor
        implements IsWidget,
        BooleanValuePairEditorView.Presenter,
        ValuePairEditor<Boolean> {

    private BooleanValuePairEditorView view;

    private Boolean currentValue;

    private AnnotationValuePairDefinition valuePairDefinition;

    private ValuePairEditorHandler editorHandler;

    public BooleanValuePairEditor() {
        view = GWT.create( BooleanValuePairEditorViewImpl.class );
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
        view.showValuePairRequiredIndicator( !valuePairDefinition.hasDefaultValue() );
    }

    @Override
    public void setValue( Boolean value ) {
        view.setSelectedValue( value != null ? value.toString() : BooleanValuePairEditorView.NOT_SELECTED );
        this.currentValue = value;
    }

    @Override
    public Boolean getValue( ) {
        return currentValue;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void clear() {
        view.setSelectedValue( BooleanValuePairEditorView.NOT_SELECTED  );
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
        String value = view.getSelectedValue();
        currentValue = !BooleanValuePairEditorView.NOT_SELECTED.equals( value ) ? Boolean.valueOf( value ) : null;
        if ( editorHandler != null ) {
            editorHandler.onValueChanged();
        }
    }
}
