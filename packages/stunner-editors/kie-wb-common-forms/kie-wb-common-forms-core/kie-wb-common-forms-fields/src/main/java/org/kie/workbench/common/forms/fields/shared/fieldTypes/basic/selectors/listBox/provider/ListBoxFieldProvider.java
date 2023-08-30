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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.provider;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.CharacterListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.DecimalListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.EnumListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.IntegerListBoxFieldDefinition;
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
        registerPropertyType(Integer.class);
        registerPropertyType(int.class);
        registerPropertyType(Long.class);
        registerPropertyType(long.class);
        registerPropertyType(BigInteger.class);
        registerPropertyType(Double.class);
        registerPropertyType(double.class);
        registerPropertyType(Float.class);
        registerPropertyType(float.class);
        registerPropertyType(BigDecimal.class);
        registerPropertyType(Short.class);
        registerPropertyType(short.class);
        registerPropertyType(Byte.class);
        registerPropertyType(byte.class);
        registerPropertyType(Character.class);
        registerPropertyType(char.class);
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

        String className = typeInfo.getClassName();

        if (Byte.class.getName().equals(className) ||
                byte.class.getName().equals(className) ||
                Short.class.getName().equals(className) ||
                short.class.getName().equals(className) ||
                Integer.class.getName().equals(className) ||
                int.class.getName().equals(className) ||
                Long.class.getName().equals(className) ||
                long.class.getName().equals(className) ||
                BigInteger.class.getName().equals(className)) {
            return new IntegerListBoxFieldDefinition();
        }
        if (Float.class.getName().equals(className) ||
                float.class.getName().equals(className) ||
                Double.class.getName().equals(className) ||
                double.class.getName().equals(className) ||
                BigDecimal.class.getName().equals(className)) {
            return new DecimalListBoxFieldDefinition();
        }
        if (Character.class.getName().equals(className) ||
                char.class.getName().equals(className)) {
            return new CharacterListBoxFieldDefinition();
        }

        for (String type : getSupportedTypes()) {
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
    public boolean isSupported(TypeInfo typeInfo) {
        return super.isSupported(typeInfo) || typeInfo.getType().equals(TypeKind.ENUM);
    }
}
