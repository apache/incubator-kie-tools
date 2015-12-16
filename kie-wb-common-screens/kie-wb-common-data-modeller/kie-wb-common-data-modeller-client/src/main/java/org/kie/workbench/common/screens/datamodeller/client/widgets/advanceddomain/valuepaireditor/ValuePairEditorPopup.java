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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

@Dependent
public class ValuePairEditorPopup
    implements IsWidget,
        ValuePairEditorPopupView.Presenter {

    @Inject
    private ValuePairEditorPopupView view;

    private ValuePairEditorPopupView.ValuePairEditorPopupHandler popupHandler;

    private AnnotationValuePairDefinition valuePairDefinition;

    private String annotationClassName;

    public ValuePairEditorPopup() {
    }

    @PostConstruct
    private void init( ) {
        view.setPresenter( this );
    }

    public void init( String annotationClassName, AnnotationValuePairDefinition valuePairDefinition ) {
        this.annotationClassName = annotationClassName;
        this.valuePairDefinition = valuePairDefinition;
        view.init( valuePairDefinition );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void clear() {
        view.clear();
    }

    public void addPopupHandler( ValuePairEditorPopupView.ValuePairEditorPopupHandler popupHandler ) {
        this.popupHandler = popupHandler;
    }

    public boolean isGenericEditor() {
        return view.isGenericEditor();
    }

    public boolean isValid() {
        return view.isValid();
    }

    @Override
    public void onOk() {
        if ( popupHandler != null ) {
            popupHandler.onOk();
        }
    }

    @Override
    public void onCancel() {
        if ( popupHandler != null ) {
            popupHandler.onCancel();
        }
    }

    @Override
    public void onClose() {
        if ( popupHandler != null ) {
            popupHandler.onClose();
        }
    }

    @Override
    public void onValidate() {

    }

    @Override
    public void onValueChange() {

    }

    public AnnotationValuePairDefinition getValuePairDefinition() {
        return valuePairDefinition;
    }

    public void setErrorMessage( String errorMessage ) {
        view.setErrorMessage( errorMessage );
    }

    public void cleanErrorMessage() {
        view.clearErrorMessage();
    }

    public String getAnnotationClassName() {
        return annotationClassName;
    }

    public void setValue( Object value ) {
        view.setValue( value );
    }

    public Object getValue() {
        return view.getValue();
    }

    public void show() {
        view.show();
    }

    public void hide() {
        view.hide();
    }
}
