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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.ValuePairEditorUtil;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.uberfire.commons.data.Pair;

public class BooleanValuePairEditor
        implements BooleanValuePairEditorView.Presenter,
        ValuePairEditor<Boolean> {

    private BooleanValuePairEditorView view;

    private Boolean currentValue = null;

    private AnnotationValuePairDefinition valuePairDefinition;

    private ValuePairEditorHandler editorHandler;

    private static List<Pair<String, String>> options = new ArrayList<Pair<String, String>>();

    static {
        options.add( new Pair<String, String>( "true", "true" ) );
        options.add( new Pair<String, String>( "false", "false" ) );
    }

    public BooleanValuePairEditor() {
        this ( ( BooleanValuePairEditorViewImpl ) GWT.create( BooleanValuePairEditorViewImpl.class ) );
    }

    public BooleanValuePairEditor( BooleanValuePairEditorView view ) {
        this.view = view;
        view.init( this );
        view.initOptions( options );
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
        view.setSelectedValue( value != null ? value.toString() : UIUtil.NOT_SELECTED );
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
        setValue( null );
        view.clearErrorMessage();
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

    @Override
    public void onValueChange() {
        String value = view.getSelectedValue();
        currentValue = !UIUtil.NOT_SELECTED.equals( value ) ? Boolean.valueOf( value ) : null;
        if ( editorHandler != null ) {
            editorHandler.onValueChange();
        }
    }
}
