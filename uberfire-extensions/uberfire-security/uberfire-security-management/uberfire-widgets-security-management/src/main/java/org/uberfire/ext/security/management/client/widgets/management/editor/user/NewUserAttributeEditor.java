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

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.form.validator.Validator;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.editor.HasRestrictedValues;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.events.CreateUserAttributeEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>Presenter class for creating a new user attribute.</p>
 */
@Dependent
public class NewUserAttributeEditor implements IsWidget, HasRestrictedValues<Collection<String>> {

    public interface View extends UberView<NewUserAttributeEditor> {

        View configure(Validator<String> attributeNameValidator, Validator<String> attributeValueValidator);
        EditorError createAttributeNameError(String value ,String message);
        EditorError createAttributeValueError(String value ,String message);
        View reset();
        View setShowAddButton(boolean isCreateButton);
        View setShowForm(boolean isCreationForm);
        
    }
    
    Event<CreateUserAttributeEvent> createUserAttributeEventEvent;
    public View view;
    Collection<String> restrictedAttributeNames;
    
    @Inject
    public NewUserAttributeEditor(final View view,
                                  final Event<CreateUserAttributeEvent> createUserAttributeEventEvent) {
        this.view = view;
        this.createUserAttributeEventEvent = createUserAttributeEventEvent;
    }

    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.configure(attributeNameValidator, attributeValueValidator);
    }

    public NewUserAttributeEditor showAddButton() {
        view.reset().setShowAddButton(true).setShowForm(false);
        return this;
    }

    public NewUserAttributeEditor showForm() {
        view.reset().setShowAddButton(false).setShowForm(true);
        return this;
    }

    public NewUserAttributeEditor clear() {
        showAddButton();
        restrictedAttributeNames = null;
        return this;
    }

    @Override
    public void setRestrictedValues(final Collection<String> value) {
        this.restrictedAttributeNames = value;
    }
    
    /*  ******************************************************************************************************
                                 VIEW CALLBACKS 
     ****************************************************************************************************** */
    
    void onNewAttributeClick() {
        showForm();
    }

    void onCancel() {
        showAddButton();
    }

    void addNewAttribute(final String name, final String value) {
        final Map.Entry<String, String> attr = createAttributeEntry(name, value);
        createUserAttributeEventEvent.fire(new CreateUserAttributeEvent(this, attr));
    }

    /*  ******************************************************************************************************
                                 PRIVATE METHODS AND VALIDATORS
     ****************************************************************************************************** */

    final Validator<String> attributeNameValidator = new Validator<String>() {
        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public List<EditorError> validate(final Editor<String> editor, final String s) {
            final List<EditorError> result = new ArrayList<EditorError>();
            final String errorMsg = validateAttributeName(s);
            if (errorMsg != null) {
                EditorError e = view.createAttributeNameError(s, errorMsg);
                result.add(e);
            }
            return result;
        }
    };

    final Validator<String> attributeValueValidator = new Validator<String>() {
        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public List<EditorError> validate(final Editor<String> editor, final String s) {
            final List<EditorError> result = new ArrayList<EditorError>();
            final String errorMsg = validateAttributeValue(s);
            if (errorMsg != null) {
                EditorError e = view.createAttributeValueError(s, errorMsg);
                result.add(e);
            }
            return result;
        }
    };


    String validateAttributeName(final String value) {
        if (isEmpty(value)) {
            return UsersManagementWidgetsConstants.INSTANCE.nameIsMandatory();
        }
        if (containsAttribute(value)) {
            return UsersManagementWidgetsConstants.INSTANCE.attributeAlreadyExists();
        }
        return null;
    }

    String validateAttributeValue(final String value) {
        if (isEmpty(value)) {
            return UsersManagementWidgetsConstants.INSTANCE.valueIsMandatory();
        }
        return null;
    }

    private Map.Entry<String, String> createAttributeEntry(final String key, final String value) {
        return new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public String setValue(String value) {
                return null;
            }
        };
    }

    private boolean containsAttribute(final String key) {
        return key != null && this.restrictedAttributeNames != null 
                && restrictedAttributeNames.contains(key);
    }

    private boolean isEmpty(final String str) {
        return str == null || str.trim().length() == 0;
    }
}
