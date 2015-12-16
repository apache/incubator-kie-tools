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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.generic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.ValuePairEditorUtil;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

@Dependent
public class GenericValuePairEditor
    implements GenericValuePairEditorView.Presenter,
        ValuePairEditor<String> {

    private GenericValuePairEditorView view;

    private ValuePairEditorHandler editorHandler;

    private String name;

    private String annotationClassName;

    private AnnotationValuePairDefinition valuePairDefinition;

    @Inject
    public GenericValuePairEditor( GenericValuePairEditorView view ) {
        this.view = view;
        view.init( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void clear() {
        view.clear();
    }

    @Override
    public void onValidate() {
        if ( editorHandler != null ) {
            editorHandler.onValidate();
        }
    }

    @Override
    public void onValueChange() {
        if ( editorHandler != null ) {
            editorHandler.onValueChange();
        }
    }

    public void init( AnnotationValuePairDefinition valuePairDefinition ) {
        this.valuePairDefinition = valuePairDefinition;
        view.setValuePairLabel( ValuePairEditorUtil.buildValuePairLabel( valuePairDefinition ) );
        view.showValuePairRequiredIndicator( !valuePairDefinition.hasDefaultValue() );
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getValue() {
        return view.getValue();
    }

    public void setValue( String value ) {
        view.setValue( value );
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public String getAnnotationClassName() {
        return annotationClassName;
    }

    public void setAnnotationClassName( String annotationClassName ) {
        this.annotationClassName = annotationClassName;
    }

    public void setErrorMessage( String errorMessage ) {
        view.setErrorMessage( errorMessage );
    }

    public void clearErrorMessage() {
        view.clearErrorMessage();
    }

    public void showValidateButton( boolean show ) {
        view.showValidateButton( show );
    }

    @Override
    public void showValuePairName( boolean show ) {
        view.showValuePairName( show );
    }

    public void refresh() {
        view.refresh();
    }

    @Override
    public void addEditorHandler( ValuePairEditorHandler editorHandler ) {
        this.editorHandler = editorHandler;
    }

    @Override
    public AnnotationValuePairDefinition getValuePairDefinition() {
        return valuePairDefinition;
    }
}
