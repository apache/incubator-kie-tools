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


package org.kie.workbench.common.stunner.bpmn.forms.service.fieldProviders;

import javax.enterprise.inject.Model;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.TimerSettingsFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.TimerSettingsFieldType;

@Model
public class TimerSettingsFieldProvider extends BasicTypeFieldProvider<TimerSettingsFieldDefinition> {

    @Override
    public int getPriority() {
        return 50000;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(TimerSettingsValue.class);
    }

    @Override
    public TimerSettingsFieldDefinition createFieldByType(TypeInfo typeInfo) {
        return getDefaultField();
    }

    @Override
    public Class<TimerSettingsFieldType> getFieldType() {
        return TimerSettingsFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return TimerSettingsFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public TimerSettingsFieldDefinition getDefaultField() {
        return new TimerSettingsFieldDefinition();
    }
}
