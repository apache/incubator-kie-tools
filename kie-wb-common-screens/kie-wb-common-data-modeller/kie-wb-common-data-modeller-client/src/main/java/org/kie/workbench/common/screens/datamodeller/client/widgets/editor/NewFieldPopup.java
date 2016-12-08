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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.uberfire.commons.data.Pair;

@Dependent
public class NewFieldPopup
        implements NewFieldPopupView.Presenter {

    private NewFieldPopupView view;

    private NewFieldPopupView.NewFieldPopupHandler popupHandler;

    private DataModelerContext context;

    public NewFieldPopup() {
    }

    @Inject
    public NewFieldPopup( NewFieldPopupView view ) {
        this.view = view;
        view.init( this );
    }

    public void init( DataModelerContext context ) {
        view.clear();
        this.context = context;
        List<Pair<String, String>> typeList;
        if ( context != null && context.getDataModel() != null ) {
            typeList = DataModelerUtils.buildFieldTypeOptions( context.getHelper().getOrderedBaseTypes().values(),
                    context.getDataModel().getDataObjects(),
                    context.getDataModel().getJavaEnums(),
                    context.getDataModel().getExternalClasses(),
                    context.getDataModel().getDependencyJavaEnums(),
                    false );
        } else {
            typeList = new ArrayList<Pair<String, String>>();
        }
        view.initTypeList( typeList, true );
    }

    public void show() {
        view.show();
    }

    public void hide() {
        view.hide();
    }

    public void addPopupHandler( NewFieldPopupView.NewFieldPopupHandler popupHandler ) {
        this.popupHandler = popupHandler;
    }

    public void setErrorMessage( String errorMessage ) {
        view.setErrorMessage( errorMessage );
    }

    public void resetInput() {
        view.clear();
        view.setFocusOnFieldName();
    }

    @Override
    public void onCreate() {
        if ( popupHandler != null ) {
            popupHandler.onCreate( view.getFieldName(),
                    view.getFieldLabel(),
                    view.getSelectedType(),
                    view.getIsMultiple() );
        }
    }

    @Override
    public void onCreateAndContinue() {
        if ( popupHandler != null ) {
            popupHandler.onCreateAndContinue( view.getFieldName(),
                    view.getFieldLabel(),
                    view.getSelectedType(),
                    view.getIsMultiple() );
        }
    }

    @Override
    public void onCancel() {
        if ( popupHandler != null ) {
            popupHandler.onCancel();
        } else {
            hide();
        }
    }

    @Override
    public void onTypeChange() {
        String selectedType = view.getSelectedType();
        if ( context != null && context.getHelper() != null ) {
            if ( context.getHelper().isPrimitiveType( selectedType ) ) {
                view.enableIsMultiple( false );
                view.setIsMultiple( false );
            } else {
                view.enableIsMultiple( true );
            }
        }
    }

}
