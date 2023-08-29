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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.provider;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.CharacterRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.DecimalRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.IntegerRadioGroupFieldDefinition;
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
                return new IntegerRadioGroupFieldDefinition();
            }
            if (Float.class.getName().equals(className) ||
                    float.class.getName().equals(className) ||
                    Double.class.getName().equals(className) ||
                    double.class.getName().equals(className) ||
                    BigDecimal.class.getName().equals(className)) {
                return new DecimalRadioGroupFieldDefinition();
            }
            if (Character.class.getName().equals(className) ||
                    char.class.getName().equals(className)) {
                return new CharacterRadioGroupFieldDefinition();
            }
            return getDefaultField();
        }
        return null;
    }
}
