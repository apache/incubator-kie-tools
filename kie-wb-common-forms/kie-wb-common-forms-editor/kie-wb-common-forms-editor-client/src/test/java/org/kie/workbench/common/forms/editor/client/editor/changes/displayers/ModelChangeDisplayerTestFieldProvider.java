/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.changes.displayers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class ModelChangeDisplayerTestFieldProvider {

    protected static List<FieldDefinition> fields;

    static {
        TextBoxFieldDefinition name = new TextBoxFieldDefinition();
        name.setId("name");
        name.setName("employee_name");
        name.setLabel("Name");
        name.setPlaceHolder("Name");
        name.setBinding("name");
        name.setStandaloneClassName(String.class.getName());

        TextBoxFieldDefinition lastName = new TextBoxFieldDefinition();
        lastName.setId("lastName");
        lastName.setName("employee_lastName");
        lastName.setLabel("Last Name");
        lastName.setPlaceHolder("Last Name");
        lastName.setBinding("lastName");
        lastName.setStandaloneClassName(String.class.getName());

        DatePickerFieldDefinition birthday = new DatePickerFieldDefinition();
        birthday.setId("birthday");
        birthday.setName("employee_birthday");
        birthday.setLabel("Birthday");
        birthday.setBinding("birthday");
        birthday.setStandaloneClassName(Date.class.getName());

        CheckBoxFieldDefinition married = new CheckBoxFieldDefinition();
        married.setId("married");
        married.setName("employee_married");
        married.setLabel("Married");
        married.setBinding("married");
        married.setStandaloneClassName(Boolean.class.getName());

        SubFormFieldDefinition address = new SubFormFieldDefinition();
        address.setId("address");
        address.setName("employee_address");
        address.setLabel("Address");
        address.setBinding("address");
        address.setNestedForm("Address");
        address.setStandaloneClassName("Address");

        MultipleSubFormFieldDefinition hobbies = new MultipleSubFormFieldDefinition();
        hobbies.setId("hobbies");
        hobbies.setName("employee_hobbies");
        hobbies.setLabel("Hobbies");
        hobbies.setBinding("hobbies");
        hobbies.setCreationForm("Hobbies");
        hobbies.setEditionForm("Hobbies");
        hobbies.setStandaloneClassName("Hobbies");

        fields = new ArrayList<>();
        fields.add(name);
        fields.add(lastName);
        fields.add(birthday);
        fields.add(married);
        fields.add(address);
        fields.add(hobbies);
    }

    public static List<FieldDefinition> getFields() {
        return fields;
    }
}
