/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.processing.engine.handling;

import org.jboss.errai.databinding.client.api.Converter;

/**
 * Provides API to handle the input changes allowing to add callbacks and run
 * field validations.
 */
public interface FormHandler<T> {

    /**
     * Sets up the FormHandler for the given model, Creates a DataBinder instance
     * for the given model, configures all the field callbacks required to process
     * the field changes and binds the field Widgets to the DataBinder
     *
     * @param model The form model, it must not be null.
     */
    void setUp(T model);

    /**
     * Retrives the model used on the Form
     *
     * @return
     */
    T getModel();

    /**
     * Registers a new FormField to the FormHandler and sets up the Field Change
     * engine for it, The Field widget provided can binded to the form DataBinder
     * depending on how the FormHandler has been setUp.
     * <p>
     * Any of the setUp method's must be executed before register any FormField
     *
     * @param formField The FormField, it must not be null.
     */
    void registerInput(FormField formField);

    /**
     * Registers a new FormField to the FormHandler and sets up the Field Change
     * engine for it, The Field widget provided can binded to the form DataBinder
     * depending on how the FormHandler has been setUp.
     * <p>
     * Any of the setUp method's must be executed before register any FormField
     *
     * @param formField The FormField, it must not be null.
     * @param converter The value converter used in data binding, if necessary
     */
    void registerInput(FormField formField, Converter converter);

    /**
     * Validates all the form fields.
     *
     * @return
     */
    boolean validate();

    /**
     * Validates a specific Field of the form.
     *
     * @param propertyName
     * @return
     */
    boolean validate(String propertyName);

    /**
     * Clears the status of the FormHandler
     */
    void clear();

    /**
     * Adds FieldChangeHandler that will be notified when any of the form fields
     * value changes. Multiple handlers can be added.
     *
     * @param handler The handler, it must not be null.
     */
    void addFieldChangeHandler(FieldChangeHandler handler);

    /**
     * Adds FieldChangeHandler that will be notified when the specified field value
     * changes. Multiple handlers can be added.
     *
     * @param fieldName The name of the field, if it is null the handler will be notified
     *                  on any field change.
     * @param handler   The handler, it must not be null.
     */
    void addFieldChangeHandler(String fieldName, FieldChangeHandler handler);

    /**
     * Sets the form widgets into readOnly mode
     */
    void setReadOnly(boolean readOnly);

    /**
     * Retrieves the rendered {@link Form}
     *
     * @return the rendered {@link Form}
     */
    Form getForm();

    /**
     * Tries to flush the changes on the form UI into the bound model. If there's a validation error it will rollback
     * the changes and leave the form on the previous state.
     */
    void maybeFlush();
}
