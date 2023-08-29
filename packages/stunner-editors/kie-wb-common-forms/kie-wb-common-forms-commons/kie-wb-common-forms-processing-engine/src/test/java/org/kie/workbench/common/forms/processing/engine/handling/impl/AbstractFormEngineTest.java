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


package org.kie.workbench.common.forms.processing.engine.handling.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import junit.framework.TestCase;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.IsNestedModel;
import org.kie.workbench.common.forms.processing.engine.handling.NeedsFlush;
import org.kie.workbench.common.forms.processing.engine.handling.impl.model.Model;
import org.kie.workbench.common.forms.processing.engine.handling.impl.model.User;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;

import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public abstract class AbstractFormEngineTest extends TestCase {

    public static final String VALUE_FIELD_NAME = "value";
    public static final String USER_NAME_FIELD_NAME = "user_name";
    public static final String USER_LAST_NAME_FIELD_NAME = "user_lastName";
    public static final String USER_BIRTHDAY_FIELD_NAME = "user_birthday";
    public static final String USER_MARRIED_FIELD_NAME = "user_married";
    public static final String USER_ADDRESS_FIELD_NAME = "user_address";

    public static final String[] ALL_FIELD_NAMES = {
            VALUE_FIELD_NAME,
            USER_NAME_FIELD_NAME,
            USER_LAST_NAME_FIELD_NAME,
            USER_BIRTHDAY_FIELD_NAME,
            USER_MARRIED_FIELD_NAME,
            USER_ADDRESS_FIELD_NAME
    };

    protected int executionCounts;

    @Mock
    protected FieldChangeHandler anonymous;

    @Mock
    protected FieldChangeHandler value;

    @Mock
    protected FieldChangeHandler userName;

    @Mock
    protected FieldChangeHandler userLastName;

    @Mock
    protected FieldChangeHandler userBirthday;

    @Mock
    protected FieldChangeHandler userMarried;

    @Mock
    protected FieldChangeHandler userAddress;

    protected Model model;

    protected FormField valueField;
    protected FormField nameField;
    protected FormField lastNameField;
    protected FormField birthdayField;
    protected FormField marriedField;
    protected FormField addressField;

    protected void init() {

        User user = new User();

        user.setName("John");
        user.setLastName("Snow");
        user.setBirtDay(new Date());
        user.setMarried(false);
        user.setAddress("Winterfell");

        model = new Model();
        model.setUser(user);
        model.setValue(25);

        valueField = generateFormField(VALUE_FIELD_NAME, "value", true, false);
        nameField = generateFormField(USER_NAME_FIELD_NAME, "user.name", true, false);
        lastNameField = generateFormField(USER_LAST_NAME_FIELD_NAME, "user.lastName", true, false);
        birthdayField = generateFormField(USER_BIRTHDAY_FIELD_NAME, "user.birthday", true, false);
        marriedField = generateFormField(USER_MARRIED_FIELD_NAME, "user.married", true, false);
        addressField = generateFormField(USER_ADDRESS_FIELD_NAME, "user.address", true, true);

        executionCounts = 0;

        Answer answer = invocationOnMock -> {
            executionCounts++;
            return null;
        };

        doAnswer(answer).when(anonymous).onFieldChange(anyString(),
                                                       anyObject());
        doAnswer(answer).when(value).onFieldChange(anyString(),
                                                   anyObject());
        doAnswer(answer).when(userName).onFieldChange(anyString(),
                                                      anyObject());
        doAnswer(answer).when(userLastName).onFieldChange(anyString(),
                                                          anyObject());
        doAnswer(answer).when(userBirthday).onFieldChange(anyString(),
                                                          anyObject());
        doAnswer(answer).when(userMarried).onFieldChange(anyString(),
                                                         anyObject());
        doAnswer(answer).when(userAddress).onFieldChange(anyString(),
                                                         anyObject());
    }

    public FormField generateFormField(String fieldName,
                                       String binding,
                                       boolean validateOnChange,
                                       boolean nestedModel) {

        Widget widget = mock(Widget.class);

        List<Class> extraInterfaces = new ArrayList<>();

        extraInterfaces.add(HasValue.class);

        if (nestedModel) {
            extraInterfaces.add(IsNestedModel.class);
            extraInterfaces.add(NeedsFlush.class);
        }

        IsWidget isWidget = mock(IsWidget.class,
                                 withSettings().extraInterfaces(extraInterfaces.toArray(new Class[extraInterfaces.size()])));

        when(isWidget.asWidget()).thenReturn(widget);

        FormField field = mock(FormField.class);

        when(field.getFieldName()).thenReturn(fieldName);
        when(field.getFieldBinding()).thenReturn(binding);
        when(field.isValidateOnChange()).thenReturn(validateOnChange);
        when(field.isBindable()).thenReturn(true);
        when(field.getWidget()).thenReturn(isWidget);
        when(field.isContentValid()).thenReturn(true);

        return field;
    }

    protected void checkClearedFields(FormField... clearedFields) {
        Arrays.stream(clearedFields).forEach(field -> {
            assertNotNull(field);
            verify(field,
                   atLeastOnce()).clearError();
        });
    }

    protected void checkWrongFields(FormField... wrongFields) {
        /*
        Checking that the validation given fields has been successfull. The conditions to check:
        - Group Verification: VALIDATION_ERROR_CLASSNAME should be added to at least one time
            (it may be more if there are more validation errors)
        - HelpBlock Verification: helpBlock's innerHTML should be modified at least two times (one to clean it up
            and at least one more to add the validation error message )
        */
        doValidationFailure(atLeast(1),
                            wrongFields);
    }

    protected void checkValidFields(FormField... validFields) {

        /*
        Checking that the validation given fields has been successfull. The conditions to check:
        - Group Verification: group shouldn't contain the VALIDATION_ERROR_CLASSNAME
        - HelpBlock Verification: helpBlock's innerHTML should be modified only one time (to clean it up)
        */
        doValidationFailure(never(),
                            validFields);
    }

    protected void doValidationFailure(VerificationMode setErrorTimes,
                                       FormField... clearedFields) {

        Arrays.stream(clearedFields).forEach(field -> {
            assertNotNull(field);
            verify(field,
                   atLeastOnce()).clearError();
            verify(field,
                   setErrorTimes).showError(anyString());
        });
    }
}
