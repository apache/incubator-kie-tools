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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.provider;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.EnumListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.ListBoxBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.StringListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;

@Dependent
public class ListBoxFieldProvider extends SelectorFieldProvider<ListBoxBaseDefinition> {

    @Override
    public Class<ListBoxFieldType> getFieldType() {
        return ListBoxFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return ListBoxFieldType.NAME;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(String.class);
        registerPropertyType(char.class);
        registerPropertyType(Character.class);
    }

    @Override
    public int getPriority() {
        return 7;
    }

    @Override
    public ListBoxBaseDefinition getDefaultField() {
        return new StringListBoxFieldDefinition();
    }

    @Override
    public ListBoxBaseDefinition createFieldByType(TypeInfo typeInfo) {
        if (typeInfo.getType().equals(TypeKind.ENUM)) {
            return new EnumListBoxFieldDefinition();
        }

        for (String type : supportedTypes) {
            if (type.equals(typeInfo.getClassName())) {
                return new StringListBoxFieldDefinition();
            }
        }

        return new EnumListBoxFieldDefinition();
    }

    @Override
    public boolean supports(Class clazz) {
        return super.supports(clazz) || clazz.isEnum();
    }

    @Override
    protected boolean isSupported(TypeInfo typeInfo) {
        return super.isSupported(typeInfo) || typeInfo.getType().equals(TypeKind.ENUM);
    }
}
