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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.MultipleValueFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.BooleanMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.CharacterMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.DateMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.DecimalMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.IntegerMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.StringMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.TypeInfo;

@Dependent
public class MultipleSelectorProvider extends BasicTypeFieldProvider<AbstractMultipleSelectorFieldDefinition> implements MultipleValueFieldProvider<AbstractMultipleSelectorFieldDefinition> {

    @Override
    public int getPriority() {
        return 50;
    }

    @Override
    protected void doRegisterFields() {
        // Integer types
        registerPropertyType(BigInteger.class);
        registerPropertyType(Byte.class);
        registerPropertyType(Integer.class);
        registerPropertyType(Long.class);
        registerPropertyType(Short.class);

        // Decimal types
        registerPropertyType(BigDecimal.class);
        registerPropertyType(Double.class);
        registerPropertyType(Float.class);

        // Date types
        registerPropertyType(Date.class);
        // TODO: Replace by class.getName once GWT supports the following types
        registerPropertyType("java.time.LocalDate");
        registerPropertyType("java.time.LocalDateTime");
        registerPropertyType("java.time.LocalTime");
        registerPropertyType("java.time.OffsetDateTime");

        registerPropertyType(Character.class);
        registerPropertyType(String.class);
        registerPropertyType(Object.class);
        registerPropertyType(Boolean.class);
    }

    @Override
    public AbstractMultipleSelectorFieldDefinition createFieldByType(TypeInfo typeInfo) {

        if (typeInfo.isMultiple()) {
            if (typeInfo.getClassName().equals(Object.class.getName())) {
                return new StringMultipleSelectorFieldDefinition();
            }
            if (typeInfo.getClassName().equals(String.class.getName())) {
                return new StringMultipleSelectorFieldDefinition();
            }
            if (typeInfo.getClassName().equals(Boolean.class.getName())) {
                return new BooleanMultipleSelectorFieldDefinition();
            }
            if (typeInfo.getClassName().equals(Character.class.getName())) {
                return new CharacterMultipleSelectorFieldDefinition();
            }
            if (typeInfo.getClassName().equals(BigInteger.class.getName()) ||
                    typeInfo.getClassName().equals(Byte.class.getName()) ||
                    typeInfo.getClassName().equals(Integer.class.getName()) ||
                    typeInfo.getClassName().equals(Long.class.getName()) ||
                    typeInfo.getClassName().equals(Short.class.getName())) {
                return new IntegerMultipleSelectorFieldDefinition();
            }
            if (typeInfo.getClassName().equals(BigDecimal.class.getName()) ||
                    typeInfo.getClassName().equals(Double.class.getName()) ||
                    typeInfo.getClassName().equals(Float.class.getName())) {
                return new DecimalMultipleSelectorFieldDefinition();
            }
            if (typeInfo.getClassName().equals(Date.class.getName()) ||
                    typeInfo.getClassName().equals("java.time.LocalDate") ||
                    typeInfo.getClassName().equals("java.time.LocalDateTime") ||
                    typeInfo.getClassName().equals("java.time.LocalTime") ||
                    typeInfo.getClassName().equals("java.time.OffsetDateTime")) {
                return new DateMultipleSelectorFieldDefinition();
            }
        }
        return null;
    }

    @Override
    public Class<? extends FieldType> getFieldType() {
        return MultipleSelectorFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return MultipleSelectorFieldType.NAME;
    }

    @Override
    public AbstractMultipleSelectorFieldDefinition getDefaultField() {
        return new StringMultipleSelectorFieldDefinition();
    }

    @Override
    public boolean isSupported(TypeInfo typeInfo) {
        return typeInfo.isMultiple() && super.isSupported(typeInfo);
    }
}
