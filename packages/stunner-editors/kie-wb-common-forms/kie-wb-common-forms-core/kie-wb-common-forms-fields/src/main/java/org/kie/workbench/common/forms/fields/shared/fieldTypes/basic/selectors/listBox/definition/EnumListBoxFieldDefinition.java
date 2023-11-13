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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class EnumListBoxFieldDefinition extends ListBoxBaseDefinition<EnumSelectorOption, Enum> {

    @FormField(
            labelKey = "selector.options",
            afterElement = "label"
    )
    protected List<EnumSelectorOption> options = new ArrayList<>();

    @FormField(
            labelKey = "defaultValue",
            afterElement = "options"
    )
    protected Enum defaultValue;

    public EnumListBoxFieldDefinition() {
        super(Enum.class.getName());
    }

    @Override
    public List<EnumSelectorOption> getOptions() {
        return options;
    }

    @Override
    public void setOptions(List<EnumSelectorOption> options) {
        this.options = options;
    }

    @Override
    public TypeInfo getFieldTypeInfo() {
        return new TypeInfoImpl(TypeKind.ENUM,
                                standaloneClassName,
                                false);
    }

    @Override
    public Enum getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Enum defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        EnumListBoxFieldDefinition that = (EnumListBoxFieldDefinition) o;

        if (options != null ? !options.equals(that.options) : that.options != null) {
            return false;
        }
        return defaultValue != null ? defaultValue.equals(that.defaultValue) : that.defaultValue == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
