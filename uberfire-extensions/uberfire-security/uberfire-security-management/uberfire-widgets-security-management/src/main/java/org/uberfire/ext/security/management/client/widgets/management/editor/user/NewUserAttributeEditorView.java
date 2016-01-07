/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.editor.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.form.error.BasicEditorError;
import org.gwtbootstrap3.client.ui.form.validator.Validator;

import javax.enterprise.context.Dependent;

@Dependent
public class NewUserAttributeEditorView extends Composite implements NewUserAttributeEditor.View {

    interface NewUserAttributeViewBinder
            extends
            UiBinder<Widget, NewUserAttributeEditorView> {

    }

    private static NewUserAttributeViewBinder uiBinder = GWT.create(NewUserAttributeViewBinder.class);

    @UiField
    Row addAttributeButtonRow;

    @UiField
    Button addAttributeButton;

    @UiField
    Row addAttributeRow;
    
    @UiField
    FormGroup newAttributeNameFormGroup;

    @UiField
    TextBox newAttributeNameBox;

    @UiField
    FormGroup newAttributeValueFormGroup;

    @UiField
    TextBox newAttributeValueBox;

    @UiField
    Button newAttributeCancelButton;

    @UiField
    Button newAttributeSaveButton;

    private NewUserAttributeEditor presenter;

    @UiConstructor
    public NewUserAttributeEditorView() {
        
    }

    @Override
    public void init(final NewUserAttributeEditor presenter) {
        this.presenter = presenter;

        initWidget( uiBinder.createAndBindUi( this ) );
        
        
    }

    @Override
    public NewUserAttributeEditor.View configure(final Validator<String> attributeNameValidator,
                                                 final Validator<String> attributeValueValidator) {
        // Text box bootstrap validators.
        newAttributeNameBox.addValidator(attributeNameValidator);
        newAttributeValueBox.addValidator(attributeValueValidator);
        return this;
    }

    @Override
    public EditorError createAttributeNameError(String value, String message) {
        return new BasicEditorError(newAttributeNameBox, value, message);
    }

    @Override
    public EditorError createAttributeValueError(String value, String message) {
        return new BasicEditorError(newAttributeValueBox, value, message);
    }

    @Override
    public NewUserAttributeEditor.View setShowAddButton(boolean isCreateButton) {
        addAttributeButtonRow.setVisible(isCreateButton);
        return this;
    }

    @Override
    public NewUserAttributeEditor.View setShowForm(boolean isCreationForm) {
        addAttributeRow.setVisible(isCreationForm);
        return this;
    }

    @Override
    public NewUserAttributeEditor.View reset() {
        newAttributeNameBox.setText("");
        newAttributeNameFormGroup.setValidationState(ValidationState.NONE);
        newAttributeValueBox.setText("");
        newAttributeValueFormGroup.setValidationState(ValidationState.NONE);
        return this;
    }

    @UiHandler( "addAttributeButton" )
    public void onAddAttributeButtonClick( final ClickEvent event ) {
        presenter.onNewAttributeClick();
    }

    @UiHandler( "newAttributeCancelButton" )
    public void onNewAttributeCancelButtonClick( final ClickEvent event ) {
        presenter.onCancel();
    }

    @UiHandler( "newAttributeSaveButton" )
    public void onNewAttributeSaveButtonClick( final ClickEvent event ) {
        final boolean isValid = newAttributeNameBox.validate() && newAttributeValueBox.validate();
        if (isValid) {
            newAttributeNameFormGroup.setValidationState(ValidationState.NONE);
            newAttributeValueFormGroup.setValidationState(ValidationState.NONE);
            final String name = newAttributeNameBox.getText();
            final String value = newAttributeValueBox.getText();
            presenter.addNewAttribute(name, value);
        }
    }
    
}