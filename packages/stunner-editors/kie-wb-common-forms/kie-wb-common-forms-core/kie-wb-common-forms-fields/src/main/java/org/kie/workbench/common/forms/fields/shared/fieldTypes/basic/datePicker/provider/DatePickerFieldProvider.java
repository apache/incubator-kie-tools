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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.provider;

import java.util.Date;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.type.DatePickerFieldType;
import org.kie.workbench.common.forms.model.TypeInfo;

@Dependent
public class DatePickerFieldProvider extends BasicTypeFieldProvider<DatePickerFieldDefinition> {

    @Override
    public Class<DatePickerFieldType> getFieldType() {
        return DatePickerFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return DatePickerFieldType.NAME;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(Date.class);

        // TODO: Replace by class.getName once GWT supports the following types
        registerPropertyType("java.time.LocalDate");
        registerPropertyType("java.time.LocalDateTime");
        registerPropertyType("java.time.LocalTime");
        registerPropertyType("java.time.OffsetDateTime");
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public DatePickerFieldDefinition getDefaultField() {
        return new DatePickerFieldDefinition();
    }

    @Override
    public DatePickerFieldDefinition createFieldByType(TypeInfo typeInfo) {
        return getDefaultField();
    }
}
