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


package org.kie.workbench.common.forms.dynamic.client.test;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.forms.adf.rendering.FieldRendererTypesProvider;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;

public class TestFieldRendererTypesProvider implements FieldRendererTypesProvider<FieldRenderer> {

    private Map<Class<? extends FieldType>, Class<? extends FieldRenderer>> fieldTypeRenderers = new HashMap<>();
    private Map<Class<? extends FieldDefinition>, Class<? extends FieldRenderer>> fieldDefinitionRenderers = new HashMap<>();

    public TestFieldRendererTypesProvider() {
        fieldTypeRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.type.IntegerBoxFieldType.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.IntegerBoxFieldRenderer.class);
        fieldTypeRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.MultipleSelectorFieldType.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.MultipleSelectorFieldRenderer.class);
        fieldTypeRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.type.MultipleSubFormFieldType.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.MultipleSubFormFieldRenderer.class);
        fieldTypeRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.type.TextBoxFieldType.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.TextBoxFieldRenderer.class);
        fieldTypeRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.MultipleInputFieldType.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.MultipleInputFieldRenderer.class);
        fieldTypeRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.type.DatePickerFieldType.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.DatePickerFieldRenderer.class);
        fieldTypeRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.SliderFieldRenderer.class);
        fieldTypeRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.TextAreaFieldRenderer.class);
        fieldTypeRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.type.CheckBoxFieldType.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.CheckBoxFieldRenderer.class);
        fieldTypeRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.type.SubFormFieldType.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.SubFormFieldRenderer.class);
        fieldTypeRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.type.DecimalBoxFieldType.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.DecimalBoxFieldRenderer.class);
        fieldDefinitionRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.IntegerListBoxFieldDefinition.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.IntegerListBoxFieldRenderer.class);
        fieldDefinitionRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.DecimalRadioGroupFieldDefinition.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup.DecimalRadioGroupFieldRenderer.class);
        fieldDefinitionRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.StringListBoxFieldDefinition.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.StringListBoxFieldRenderer.class);
        fieldDefinitionRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.IntegerRadioGroupFieldDefinition.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup.IntegerRadioGroupFieldRenderer.class);
        fieldDefinitionRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.CharacterRadioGroupFieldDefinition.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup.CharacterRadioGroupFieldRenderer.class);
        fieldDefinitionRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.DecimalListBoxFieldDefinition.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.DecimalListBoxFieldRenderer.class);
        fieldDefinitionRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.StringRadioGroupFieldDefinition.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup.StringRadioGroupFieldRenderer.class);
        fieldDefinitionRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.EnumListBoxFieldDefinition.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.EnumListBoxFieldRenderer.class);
        fieldDefinitionRenderers.put(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.CharacterListBoxFieldDefinition.class, org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.listBox.CharacterListBoxFieldRenderer.class);
    }

    @Override
    public Map<Class<? extends FieldType>, Class<? extends FieldRenderer>> getFieldTypeRenderers() {
        return fieldTypeRenderers;
    }

    @Override
    public Map<Class<? extends FieldDefinition>, Class<? extends FieldRenderer>> getFieldDefinitionRenderers() {
        return fieldDefinitionRenderers;
    }
}