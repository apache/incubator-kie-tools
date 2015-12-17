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

package org.kie.workbench.common.screens.datamodeller.client.widgets.droolsdomain;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.screens.datamodeller.client.util.ErrorPopupHelper;

@Dependent
public class DroolsDataObjectFieldEditorViewImpl
        extends Composite
        implements DroolsDataObjectFieldEditorView {

    interface DroolsDataObjectFieldEditorUIBinder
            extends UiBinder<Widget, DroolsDataObjectFieldEditorViewImpl> {

    }

    private static DroolsDataObjectFieldEditorUIBinder uiBinder = GWT.create( DroolsDataObjectFieldEditorUIBinder.class );

    @UiField
    CheckBox equalsSelector;

    @UiField
    FormGroup positionFormGroup;

    @UiField
    TextBox position;

    private Presenter presenter;

    public DroolsDataObjectFieldEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    protected void init() {
        position.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                presenter.onPositionChange();
            }
        } );

        equalsSelector.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onEqualsChange();
            }
        } );

        setReadonly( true );
    }

    @Override
    public void init( DroolsDataObjectFieldEditorView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public boolean getEquals() {
        return equalsSelector.getValue();
    }

    @Override
    public void setEquals( boolean equals ) {
        equalsSelector.setValue( equals );
    }

    @Override
    public String getPosition() {
        return position.getText();
    }

    @Override
    public void setPosition( String position ) {
        this.position.setText( position );
    }

    @Override
    public void setPositionOnError( boolean onError ) {
        positionFormGroup.setValidationState( onError ? ValidationState.ERROR : ValidationState.NONE );
    }

    @Override
    public void selectAllPositionText() {
        position.selectAll();
    }

    public void setReadonly( boolean readonly ) {
        boolean value = !readonly;
        equalsSelector.setEnabled( value );
        position.setEnabled( value );
    }

    @Override
    public void showErrorPopup( String message ) {
        ErrorPopupHelper.showErrorPopup( message );
    }

    @Override
    public void showErrorPopup( String message, org.uberfire.mvp.Command afterShowCommand, org.uberfire.mvp.Command afterCloseCommand ) {
        ErrorPopupHelper.showErrorPopup( message, afterShowCommand, afterCloseCommand );
    }
}