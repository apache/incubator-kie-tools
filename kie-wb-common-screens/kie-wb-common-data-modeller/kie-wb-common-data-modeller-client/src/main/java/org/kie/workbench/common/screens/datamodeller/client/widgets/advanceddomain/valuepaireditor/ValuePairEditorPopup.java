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

public class ValuePairEditorPopup
    implements IsWidget,
        ValuePairEditorPopupView.Presenter {

    private ValuePairEditorPopupView view;

    private ValuePairEditorPopupView.ValuePairEditorPopupHandler popupHandler;

    @Inject
    public ValuePairEditorPopup( ValuePairEditorPopupView view ) {
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

    public void addPopupHandler( ValuePairEditorPopupView.ValuePairEditorPopupHandler popupHandler ) {
        this.popupHandler = popupHandler;
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

    public String getName() {
        return view.getName();
    }

    public void setName( String name ) {
        view.setName( name );
    }

    public void setErrorMessage( String errorMessage ) {
        view.setErrorMessage( errorMessage );
    }

    public void cleanErrorMessage() {
        view.clearErrorMessage();
    }

    public String getAnnotationClassName() {
        return view.getAnnotationClassName();
    }

    public void setAnnotationClassName( String annotationClassName ) {
        view.setAnnotationClassName( annotationClassName );
    }

    public void setValue( String value ) {
        view.setValue( value );
    }

    public String getValue() {
        return view.getValue();
    }

    public void show() {
        view.show();
    }

    public void hide() {
        view.hide();
    }
}
