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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.provider;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.RadioGroupBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.StringRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.type.RadioGroupFieldType;
import org.kie.workbench.common.forms.model.TypeInfo;

@Dependent
public class RadioGroupFieldProvider extends SelectorFieldProvider<RadioGroupBaseDefinition> {

    @Override
    public Class<RadioGroupFieldType> getFieldType() {
        return RadioGroupFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return RadioGroupBaseDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(String.class);
        registerPropertyType(Character.class);
        registerPropertyType(char.class);

// TODO: implement this fieldTypes
        registerPropertyType(BigDecimal.class);
        registerPropertyType(BigInteger.class);
        registerPropertyType(Byte.class);
        registerPropertyType(byte.class);
        registerPropertyType(Double.class);
        registerPropertyType(double.class);
        registerPropertyType(Float.class);
        registerPropertyType(float.class);
        registerPropertyType(Integer.class);
        registerPropertyType(int.class);
        registerPropertyType(Long.class);
        registerPropertyType(long.class);
        registerPropertyType(Short.class);
        registerPropertyType(short.class);
    }

    @Override
    public int getPriority() {
        return 8;
    }

    @Override
    public RadioGroupBaseDefinition getDefaultField() {
        return new StringRadioGroupFieldDefinition();
    }

    @Override
    public RadioGroupBaseDefinition createFieldByType(TypeInfo typeInfo) {
        if (isSupported(typeInfo)) {
            return getDefaultField();
        }
        return null;
    }
}
