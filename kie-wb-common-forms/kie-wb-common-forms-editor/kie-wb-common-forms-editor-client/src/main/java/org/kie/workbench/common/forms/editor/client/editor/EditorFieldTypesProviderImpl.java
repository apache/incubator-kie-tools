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

package org.kie.workbench.common.forms.editor.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.type.CheckBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.type.DatePickerFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.type.DecimalBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.image.type.PictureFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.type.IntegerBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.type.RadioGroupFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.type.TextBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.type.MultipleSubFormFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.type.SubFormFieldType;
import org.kie.workbench.common.forms.model.FieldType;

@ApplicationScoped
public class EditorFieldTypesProviderImpl implements EditorFieldTypesProvider {

    private List<FieldType> paletteFieldTypes = new ArrayList<>();
    private List<FieldType> fieldPropertiesFieldTypes = new ArrayList<>();

    @PostConstruct
    public void init() {
        paletteFieldTypes.add(new TextBoxFieldType());
        paletteFieldTypes.add(new TextAreaFieldType());
        paletteFieldTypes.add(new IntegerBoxFieldType());
        paletteFieldTypes.add(new DecimalBoxFieldType());
        paletteFieldTypes.add(new CheckBoxFieldType());
        paletteFieldTypes.add(new DatePickerFieldType());
        paletteFieldTypes.add(new SliderFieldType());
        paletteFieldTypes.add(new ListBoxFieldType());
        paletteFieldTypes.add(new RadioGroupFieldType());
        paletteFieldTypes.add(new PictureFieldType());
        fieldPropertiesFieldTypes.addAll(paletteFieldTypes);
        fieldPropertiesFieldTypes.add(new SubFormFieldType());
        fieldPropertiesFieldTypes.add(new MultipleSubFormFieldType());
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Collection<FieldType> getPaletteFieldTypes() {
        return paletteFieldTypes;
    }

    @Override
    public Collection<FieldType> getFieldPropertiesFieldTypes() {
        return fieldPropertiesFieldTypes;
    }
}
