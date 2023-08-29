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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.provider;

import java.math.BigInteger;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.type.IntegerBoxFieldType;
import org.kie.workbench.common.forms.model.TypeInfo;

@Dependent
public class IntegerBoxFieldProvider extends BasicTypeFieldProvider<IntegerBoxFieldDefinition> {

    @Override
    public Class<IntegerBoxFieldType> getFieldType() {
        return IntegerBoxFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return IntegerBoxFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(BigInteger.class);
        registerPropertyType(Byte.class);
        registerPropertyType(byte.class);
        registerPropertyType(Integer.class);
        registerPropertyType(int.class);
        registerPropertyType(Long.class);
        registerPropertyType(long.class);
        registerPropertyType(Short.class);
        registerPropertyType(short.class);
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public IntegerBoxFieldDefinition getDefaultField() {
        return new IntegerBoxFieldDefinition();
    }

    @Override
    public IntegerBoxFieldDefinition createFieldByType(TypeInfo typeInfo) {
        return getDefaultField();
    }
}
