/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.processing.engine.handling;

import org.jboss.errai.databinding.client.api.DataBinder;

/**
 * @author Pere Fernandez <pefernan@redhat.com>
 *
 * Provides API to handle the input changes allowing to add callbacks and run field validations.
 */
public interface FormHandler<T> {

    /**
     * Sets Up the FormHandler for the given DataBinder. It configures all the field callbacks required to process
     * the field changes but doesn't bind the field Widgets to the DataBinder. Binder mustn't be null.
     * @param binder The binder, must not be null.
     */
    void setUp( DataBinder<T> binder );

    /**
     * Sets up the FormHancler for the given DataBinder. It configures all the field callbacks required to process
     * the field changes and binds the field Widgets to the DataBinder depending on the bindingInputs param.
     * @param binder The binder, must not be null.
     * @param bindInputs Determines if the form widgets must be binded to the DataBinder when registered or not,
     */
    void setUp( DataBinder<T> binder, boolean bindInputs );

    /**
     * Sets up the FormHandler for the given model, Creates a DataBinder instance for the given model, configures all the field callbacks required to process
     * the field changes and binds the field Widgets to the DataBinder
     * @param model The form model, it must not be null.
     */
    void setUp( T model );

    /**
     * Retrives the model used on the Form
     * @return
     */
    T getModel();

    /**
     * Registers a new FormField to the FormHandler and sets up the Field Change engine for it,
     * The Field widget provided can binded to the form DataBinder depending on how the FormHandler has been setUp.
     *
     * Any of the setUp method's must be executed before register any FormField
     *
     * @param formField The FormField, it must not be null.
     */
    void registerInput( FormField formField );

    /**
     * Validates all the form fields.
     * @return
     */
    boolean validate();

    /**
     * Validates a specific Field of the form.
     * @param propertyName
     * @return
     */
    boolean validate( String propertyName );

    /**
     * Clears the status of th
     */
    void clear();

    /**
     * Adds FieldChangeHandler that will be notified when any of the form fields value changes. Multiple handlers can be
     * added.
     * @param handler The handler, it must not be null.
     */
    void addFieldChangeHandler( FieldChangeHandler handler );

    /**
     * Adds FieldChangeHandler that will be notified when the specified field value changes. Multiple handlers can be
     * added.
     * @param fieldName The name of the field, if it is null the handler will be notified on any field change.
     * @param handler The handler, it must not be null.
     */
    void addFieldChangeHandler( String fieldName, FieldChangeHandler handler );;

    /**
     * Sets the form widgets into readOnly mode
     */
    void setReadOnly( boolean readOnly );
}
