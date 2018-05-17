/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.test.util;

import java.util.Date;
import java.util.stream.Collectors;

import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.test.model.Address;
import org.kie.workbench.common.forms.dynamic.test.model.Employee;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.PortableJavaModel;

public class TestFormGenerator {

    public static final String EMPLOYEE_NAME = "name";
    public static final String EMPLOYEE_SURNAME = "surname";
    public static final String EMPLOYEE_BIRTHDAY = "birthday";
    public static final String EMPLOYEE_AGE = "age";
    public static final String EMPLOYEE_AGE_BINDING = "age.value";
    public static final String EMPLOYEE_MARRIED = "married";
    public static final String EMPLOYEE_ADDRESS = "address";

    public static final String ADDRESS_STREET = "street";
    public static final String ADDRESS_NUM = "num";

    public static StaticModelFormRenderingContext getContextForEmployee(Employee employee) {
        FormDefinition form = getEmployeeForm();
        StaticModelFormRenderingContext context = new StaticModelFormRenderingContext("");
        context.setRootForm(form);
        context.setModel(employee);
        context.getAvailableForms().put(form.getId(),
                                        form);

        form = getAddressForm();
        context.getAvailableForms().put(form.getId(),
                                        form);

        return context;
    }

    public static FormDefinition getEmployeeForm() {
        FormDefinition form = new FormDefinition();
        form.setName("Employee");
        form.setId("Employee");

        TextBoxFieldDefinition name = new TextBoxFieldDefinition();
        name.setId(EMPLOYEE_NAME);
        name.setName(EMPLOYEE_NAME);
        name.setLabel("Name");
        name.setPlaceHolder("Name");
        name.setBinding(EMPLOYEE_NAME);
        name.setStandaloneClassName(String.class.getName());

        TextBoxFieldDefinition lastName = new TextBoxFieldDefinition();
        lastName.setId(EMPLOYEE_SURNAME);
        lastName.setName(EMPLOYEE_SURNAME);
        lastName.setLabel("Surname");
        lastName.setPlaceHolder("SurName");
        lastName.setBinding(EMPLOYEE_SURNAME);
        lastName.setStandaloneClassName(String.class.getName());

        DatePickerFieldDefinition birthday = new DatePickerFieldDefinition();
        birthday.setId(EMPLOYEE_BIRTHDAY);
        birthday.setName(EMPLOYEE_BIRTHDAY);
        birthday.setLabel("Birthday");
        birthday.setBinding(EMPLOYEE_BIRTHDAY);
        birthday.setStandaloneClassName(Date.class.getName());

        TextBoxFieldDefinition age = new TextBoxFieldDefinition();
        age.setId(EMPLOYEE_AGE);
        age.setName(EMPLOYEE_AGE);
        age.setLabel("Age");
        age.setPlaceHolder("Age");
        age.setBinding(EMPLOYEE_AGE_BINDING);
        age.setStandaloneClassName(Integer.class.getName());

        CheckBoxFieldDefinition married = new CheckBoxFieldDefinition();
        married.setId(EMPLOYEE_MARRIED);
        married.setName(EMPLOYEE_MARRIED);
        married.setLabel("Married");
        married.setBinding(EMPLOYEE_MARRIED);
        married.setStandaloneClassName(Boolean.class.getName());

        SubFormFieldDefinition address = new SubFormFieldDefinition();
        address.setId(EMPLOYEE_ADDRESS);
        address.setName(EMPLOYEE_ADDRESS);
        address.setLabel("Address");
        address.setBinding(EMPLOYEE_ADDRESS);
        address.setNestedForm("Address");
        address.setStandaloneClassName(Address.class.getName());

        form.getFields().add(name);
        form.getFields().add(lastName);
        form.getFields().add(birthday);
        form.getFields().add(age);
        form.getFields().add(married);
        form.getFields().add(address);

        form.setModel(generateModelFor(form));

        return form;
    }

    public static FormDefinition getAddressForm() {
        FormDefinition form = new FormDefinition();
        form.setName("Address");
        form.setId("Address");

        TextBoxFieldDefinition name = new TextBoxFieldDefinition();
        name.setId(ADDRESS_STREET);
        name.setName(ADDRESS_STREET);
        name.setLabel("Street Name");
        name.setPlaceHolder("Street Name");
        name.setBinding(ADDRESS_STREET);
        name.setStandaloneClassName(String.class.getName());

        TextBoxFieldDefinition num = new TextBoxFieldDefinition();
        num.setId(ADDRESS_NUM);
        num.setName(ADDRESS_NUM);
        num.setLabel("#");
        num.setPlaceHolder("#");
        num.setBinding(ADDRESS_NUM);
        num.setStandaloneClassName(Integer.class.getName());

        form.getFields().add(name);
        form.getFields().add(num);

        form.setModel(generateModelFor(form));

        return form;
    }

    public static FormModel generateModelFor(FormDefinition form) {
        PortableJavaModel model = new PortableJavaModel(Employee.class.getName());

        model.getProperties().addAll(form.getFields().stream().map(TestFormGenerator::generatePropertyFor).collect(Collectors.toList()));

        return model;
    }

    private static ModelProperty generatePropertyFor(FieldDefinition field) {
        return new ModelPropertyImpl(field.getBinding(), field.getFieldTypeInfo());
    }
}
