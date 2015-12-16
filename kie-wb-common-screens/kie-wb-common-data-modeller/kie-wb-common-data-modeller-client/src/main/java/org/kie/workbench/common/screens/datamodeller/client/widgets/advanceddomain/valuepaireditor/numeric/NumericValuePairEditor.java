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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.numeric;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.NumberType;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.ValuePairEditorUtil;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

public class NumericValuePairEditor
        implements NumericValuePairEditorView.Presenter,
        ValuePairEditor<Object> {

    private NumericValuePairEditorView view;

    private Object currentValue = null;

    private NumberType numberType = NumberType.INT;

    private AnnotationValuePairDefinition valuePairDefinition;

    private ValuePairEditorHandler editorHandler;

    boolean valid = true;

    public NumericValuePairEditor() {
        this( ( NumericValuePairEditorView ) GWT.create( NumericValuePairEditorViewImpl.class ) );
    }

    public NumericValuePairEditor( NumericValuePairEditorView view ) {
        this.view = view;
        view.init( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void init( AnnotationValuePairDefinition valuePairDefinition ) {
        this.valuePairDefinition = valuePairDefinition;
        numberType = ValuePairEditorUtil.getNumberType( valuePairDefinition );
        view.setValuePairLabel( ValuePairEditorUtil.buildValuePairLabel( valuePairDefinition ) );
        view.showValuePairRequiredIndicator( !valuePairDefinition.hasDefaultValue() );
    }

    public NumberType getNumberType() {
        return numberType;
    }

    public Object getValue( ) {
        return currentValue;
    }

    public void setValue( Object value ) {
        this.currentValue = value;
        view.setValue( value != null ? value.toString() : null );
        valid = true; //a bit optimistic. By now we can assume that when set programmatically the value is valid.
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void onValueChange() {
        String value = view.getValue();
        if ( "".equals( view.getValue() ) || view.getValue() == null ) {
            currentValue = null;
            valid = true;
            view.clearErrorMessage();
        } else {
            String errorMessage = null;
            try {
                currentValue = ValuePairEditorUtil.parseNumberValue( value, numberType );
                valid = true;
            } catch ( Exception e ) {
                errorMessage = e.getMessage();
                currentValue = null;
                valid = false;
            }

            if ( valid ) {
                view.clearErrorMessage();
            } else {
                view.setErrorMessage( errorMessage );
            }
        }
        if ( editorHandler != null ) {
            editorHandler.onValueChange();
        }
    }

    @Override
    public void clear() {
        view.clear();
        setValue( null );
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
        view.setErrorMessage( errorMessage );
    }

    @Override
    public void clearErrorMessage() {
        view.clearErrorMessage();
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

}
