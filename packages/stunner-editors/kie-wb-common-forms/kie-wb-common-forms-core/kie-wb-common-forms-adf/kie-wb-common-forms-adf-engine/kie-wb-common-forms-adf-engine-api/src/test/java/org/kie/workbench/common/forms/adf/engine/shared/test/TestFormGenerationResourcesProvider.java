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


package org.kie.workbench.common.forms.adf.engine.shared.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.forms.adf.definitions.settings.ColSpan;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.model.Address;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.model.Person;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.model.Weapon;
import org.kie.workbench.common.forms.adf.service.building.FieldStatusModifier;
import org.kie.workbench.common.forms.adf.service.building.FormGenerationResourcesProvider;
import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutColumnDefinition;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;

public class TestFormGenerationResourcesProvider implements FormGenerationResourcesProvider {

    private Map<String, FormDefinitionSettings> definitionSettings = new HashMap<>();

    private Map<String, FieldStatusModifier> fieldStatusModifiers = new HashMap<>();

    private Map<String, String> fieldStatusModifiersReferences = new HashMap<>();

    public TestFormGenerationResourcesProvider() {
        FormDefinitionSettings settings = new FormDefinitionSettings(Person.class.getName());

        settings.getI18nSettings();

        settings.setLayout(new LayoutDefinition(new LayoutColumnDefinition(ColSpan.AUTO)));

        FieldElement name = new FieldElement("name",
                                             "name",
                                             new TypeInfoImpl(String.class.getName()));
        name.setLabelKey("name.label");
        name.setReadOnly(false);
        name.setRequired(true);
        name.getParams().put("maxLength",
                             "100");
        name.getParams().put("placeHolder",
                             "name.placeHolder");

        FieldElement lastName = new FieldElement("lastName",
                                                 "lastName",
                                                 new TypeInfoImpl(String.class.getName()));
        lastName.setPreferredType(TextAreaFieldType.class);
        lastName.setLabelKey("lastName.label");
        lastName.setReadOnly(false);
        lastName.setRequired(false);
        lastName.getParams().put("maxLength",
                                 "300");
        lastName.getParams().put("placeHolder",
                                 "lastName.placeHolder");

        FieldElement birthDay = new FieldElement("birthDay",
                                                 "birthDay",
                                                 new TypeInfoImpl(Date.class.getName()));
        birthDay.setLabelKey("birthDay.label");
        birthDay.setReadOnly(false);
        birthDay.setRequired(false);
        birthDay.getParams().put("placeHolder",
                                 "birthDay.placeHolder");

        FieldElement married = new FieldElement("married",
                                                "married",
                                                new TypeInfoImpl(Boolean.class.getName()));
        married.setLabelKey("married.label");
        married.setReadOnly(false);
        married.setRequired(false);

        FieldElement height = new FieldElement("height",
                                               "height.value",
                                               new TypeInfoImpl(Double.class.getName()));
        height.setPreferredType(SliderFieldType.class);
        height.setLabelKey("height.label");
        height.setReadOnly(false);
        height.setRequired(false);

        FieldElement weight = new FieldElement("weight",
                                               "weight.value",
                                               new TypeInfoImpl(Integer.class.getName()));
        weight.setPreferredType(SliderFieldType.class);
        weight.setLabelKey("weight.label");
        weight.setReadOnly(false);
        weight.setRequired(false);

        FieldElement address = new FieldElement("address",
                                                "address",
                                                new TypeInfoImpl(TypeKind.OBJECT, Address.class.getName(), false));
        address.setLabelKey("address.label");
        address.setReadOnly(false);
        address.setRequired(false);

        FieldElement weapons = new FieldElement("weapons",
                                                "weapons",
                                                new TypeInfoImpl(TypeKind.OBJECT, Weapon.class.getName(),
                                                                 true));
        weapons.setLabelKey("weapons.label");
        weapons.setReadOnly(false);
        weapons.setRequired(false);

        settings.addFormElement(name);
        settings.addFormElement(lastName);
        settings.addFormElement(birthDay);
        settings.addFormElement(married);
        settings.addFormElement(height);
        settings.addFormElement(weight);
        settings.addFormElement(address);
        settings.addFormElement(weapons);

        FormDefinitionSettings addressSettings = new FormDefinitionSettings(Address.class.getName());
        addressSettings.setLayout(new LayoutDefinition(new LayoutColumnDefinition(ColSpan.AUTO)));
        FieldElement street = new FieldElement("street",
                                               "street",
                                               new TypeInfoImpl(String.class.getName()));
        street.setLabelKey("street.label");
        street.setReadOnly(false);
        street.setRequired(true);
        street.getParams().put("maxLength",
                               "100");
        street.getParams().put("placeHolder",
                               "street.placeHolder");
        FieldElement number = new FieldElement("number",
                                               "number",
                                               new TypeInfoImpl(Integer.class.getName()));
        number.setLabelKey("number.label");
        number.setReadOnly(false);
        number.setRequired(true);
        number.getParams().put("maxLength",
                               "3");
        number.getParams().put("placeHolder",
                               "number.placeHolder");
        FieldElement city = new FieldElement("city",
                                             "city",
                                             new TypeInfoImpl(String.class.getName()));
        city.setLabelKey("city.label");
        city.setReadOnly(false);
        city.setRequired(true);
        city.getParams().put("maxLength",
                             "100");
        city.getParams().put("placeHolder",
                             "city.placeHolder");

        addressSettings.addFormElement(street);
        addressSettings.addFormElement(number);
        addressSettings.addFormElement(city);

        FormDefinitionSettings weaponSettings = new FormDefinitionSettings(Weapon.class.getName());
        weaponSettings.setLayout(new LayoutDefinition(new LayoutColumnDefinition(ColSpan.AUTO)));
        FieldElement weaponName = new FieldElement("name",
                                                   "name",
                                                   new TypeInfoImpl(String.class.getName()));
        weaponName.setLabelKey("name.label");
        weaponName.setReadOnly(false);
        weaponName.setRequired(true);
        weaponName.getParams().put("maxLength",
                                   "100");
        weaponName.getParams().put("placeHolder",
                                   "name.placeHolder");
        FieldElement damage = new FieldElement("damage",
                                               "damage",
                                               new TypeInfoImpl(Integer.class.getName()));
        damage.setLabelKey("damage.label");
        damage.setReadOnly(false);
        damage.setRequired(true);
        damage.getParams().put("maxLength",
                               "3");
        damage.getParams().put("placeHolder",
                               "damage.placeHolder");

        weaponSettings.addFormElement(weaponName);
        weaponSettings.addFormElement(damage);

        definitionSettings.put(Person.class.getName(),
                               settings);
        definitionSettings.put(Address.class.getName(),
                               addressSettings);
        definitionSettings.put(Weapon.class.getName(),
                               weaponSettings);

        fieldStatusModifiers.put("heightModifier",
                                 (field, modelHeight) -> {
                                     // do nothing here
                                 });
        fieldStatusModifiersReferences.put(Person.class.getName() + ".height",
                                           "heightModifier");
        fieldStatusModifiers.put("weightModifier",
                                 (field, modelWeight) -> {
                                     // do nothing
                                 });
        fieldStatusModifiersReferences.put(Person.class.getName() + ".weight",
                                           "weightModifier");
    }

    @Override
    public Map<String, FormDefinitionSettings> getDefinitionSettings() {
        return definitionSettings;
    }

    @Override
    public Map<String, FieldStatusModifier> getFieldModifiers() {
        return fieldStatusModifiers;
    }

    @Override
    public Map<String, String> getFieldModifierReferences() {
        return fieldStatusModifiersReferences;
    }
}
