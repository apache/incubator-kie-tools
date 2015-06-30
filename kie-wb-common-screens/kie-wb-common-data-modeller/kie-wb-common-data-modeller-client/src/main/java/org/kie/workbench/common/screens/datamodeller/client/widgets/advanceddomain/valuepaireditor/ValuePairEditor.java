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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ValuePairEditor
    implements IsWidget,
                ValuePairEditorView.Presenter {

    private ValuePairEditorView view;

    private ValuePairEditorView.ValuePairEditorHandler editorHandler;

    private String name;

    private String annotationClassName;

    @Inject
    public ValuePairEditor( ValuePairEditorView view ) {
        this.view = view;
        view.setPresenter( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void clear() {
        view.clear();
    }

    public void addEditorHandler( ValuePairEditorView.ValuePairEditorHandler editorHandler ) {
        this.editorHandler = editorHandler;
    }

    @Override
    public void onValidate() {
        if ( editorHandler != null ) {
            editorHandler.onValidate();
        }
    }

    @Override
    public void onValueChanged() {
        if ( editorHandler != null ) {
            editorHandler.onValueChanged( view.getValue() );
        }
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setNameLabel( String nameLabel ) {
        view.setNameLabel( nameLabel );
    }

    public String getValue() {
        return view.getValue();
    }

    public void setValue( String value ) {
        view.setValue( value );
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

    public void refresh() {
        view.refresh();
    }

}
