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

package org.kie.workbench.common.forms.processing.engine.handling.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.databinding.client.PropertyChangeUnsubscribeHandle;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandlerManager;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.FormFieldProvider;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormValidator;
import org.kie.workbench.common.forms.processing.engine.handling.IsNestedModel;

public class FormHandlerImpl<T> implements FormHandler<T>, FormFieldProvider {

    protected FormValidator validator;

    protected FieldChangeHandlerManager fieldChangeManager;

    protected FormHandlerHelper<T> handlerHelper;

    protected DataBinder<T> binder;

    protected List<FormField> formFields = new ArrayList<>();

    protected List<PropertyChangeUnsubscribeHandle> unsubscribeHandlers = new ArrayList<>();

    @Inject
    public FormHandlerImpl( FormValidator validator,
                            FieldChangeHandlerManager fieldChangeManager ) {
        this.validator = validator;
        this.fieldChangeManager = fieldChangeManager;

        this.validator.setFormFieldProvider( this );

        fieldChangeManager.setValidator( validator );
    }

    @Override
    public void setUp( DataBinder<T> binder ) {
        setUp( binder, false );
    }

    @Override
    public void setUp( DataBinder<T> binder, boolean bindInputs ) {
        Assert.notNull( "DataBinder cannot be null", binder );

        clear();

        this.binder = binder;
        this.handlerHelper = new BoundBinderHelper<>( binder, bindInputs );
    }

    @Override
    public void setUp( T model ) {
        Assert.notNull( "Model cannot be null", model );

        clear();

        this.binder = getBinderForModel( model );
        this.handlerHelper = new NewBinderHelper<>( model );
    }

    protected DataBinder<T> getBinderForModel( T model ) {
        return DataBinder.forModel( model );
    }

    @Override
    public void registerInput( FormField formField ) {
        Assert.notNull( "FormHandler isn't correctly initialized, please run any of the setUp methods before use",
                        binder );
        Assert.notNull( "FormField cannot be null!", formField );

        String fieldName = formField.getFieldName();
        IsWidget widget = formField.getWidget();

        formFields.add( formField );

        if ( handlerHelper.supportsInputBinding() && formField.isBindable() ) {
            binder.bind( widget, formField.getFieldBinding() );
        }

        fieldChangeManager.registerField( formField.getFieldName(), formField.isValidateOnChange() );

        /**
         * if field isn't bindable we cannot listen to field value changes.
         */
        if ( !formField.isBindable() ) {
            return;
        }

        if ( widget instanceof IsNestedModel ) {
            IsNestedModel nestedModelWidget = (IsNestedModel) widget;
            nestedModelWidget.addFieldChangeHandler( ( childFieldName, newValue ) -> fieldChangeManager.processFieldChange(
                    fieldName + "." + childFieldName,
                    newValue,
                    getModel() ) );

        } else {
            PropertyChangeUnsubscribeHandle unsubscribeHandle = binder.addPropertyChangeHandler(
                    formField.getFieldBinding(),
                    (PropertyChangeHandler) event -> fieldChangeManager.processFieldChange( fieldName,
                                                                                            event.getNewValue(),
                                                                                            binder.getModel() ) );
            unsubscribeHandlers.add( unsubscribeHandle );
        }
    }

    public void addFieldChangeHandler( FieldChangeHandler handler ) {
        addFieldChangeHandler( null, handler );
    }

    public void addFieldChangeHandler( String fieldName, FieldChangeHandler handler ) {
        Assert.notNull( "FieldChangeHandler cannot be null", handler );

        if ( fieldName != null ) {
            fieldChangeManager.addFieldChangeHandler( fieldName, handler );
        } else {
            fieldChangeManager.addFieldChangeHandler( handler );
        }
    }

    @Override
    public boolean validate() {
        return validator.validate( getModel() );
    }

    @Override
    public boolean validate( String propertyName ) {
        return validator.validate( propertyName, getModel() );
    }

    @Override
    public void setReadOnly( boolean readOnly ) {
        formFields.forEach( field -> field.setReadOnly( readOnly ) );
    }

    @Override
    public void clear() {

        // Check if it's initialized before clear.
        if ( handlerHelper == null || binder == null ) {
            return;
        }

        for ( PropertyChangeUnsubscribeHandle handle : unsubscribeHandlers ) {
            handle.unsubscribe();
        }
        unsubscribeHandlers.clear();
        formFields.clear();
        fieldChangeManager.clear();
        if ( handlerHelper.supportsInputBinding() ) {
            binder.unbind();
        }
    }

    public T getModel() {
        return handlerHelper.getModel();
    }

    @Override
    public FormField findFormField( String fieldName ) {

        for ( FormField field : formFields ) {
            if ( field.getFieldName().equals( fieldName ) || fieldName.equals( field.getFieldBinding() ) ) {
                return field;
            }
        }

        return null;
    }

    @Override
    public Collection<FormField> getAll() {
        return formFields;
    }

    private interface FormHandlerHelper<T> {
        boolean supportsInputBinding();

        T getModel();
    }

    protected class BoundBinderHelper<T> implements FormHandlerHelper {

        private boolean bindInputs;
        private DataBinder<T> dataBinder;


        public BoundBinderHelper( DataBinder<T> dataBinder, boolean bindInputs ) {
            this.dataBinder = dataBinder;
            this.bindInputs = bindInputs;
        }

        @Override
        public boolean supportsInputBinding() {
            return bindInputs;
        }

        @Override
        public T getModel() {
            return dataBinder.getModel();
        }
    }

    protected class NewBinderHelper<T> implements FormHandlerHelper {

        private T model;

        public NewBinderHelper( T model ) {
            this.model = model;
        }

        @Override
        public boolean supportsInputBinding() {
            return true;
        }

        @Override
        public T getModel() {
            return model;
        }
    }
}
