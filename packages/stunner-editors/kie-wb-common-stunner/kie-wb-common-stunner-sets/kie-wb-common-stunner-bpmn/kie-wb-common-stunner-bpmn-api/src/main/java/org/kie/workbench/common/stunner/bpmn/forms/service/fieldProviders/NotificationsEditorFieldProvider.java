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
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.NotificationsEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.NotificationsEditorFieldType;

@Model
public class NotificationsEditorFieldProvider extends BasicTypeFieldProvider<NotificationsEditorFieldDefinition> {

    @Override
    public int getPriority() {
        return 20001;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(NotificationTypeListValue.class);
    }

    @Override
    public NotificationsEditorFieldDefinition createFieldByType(TypeInfo typeInfo) {
        return getDefaultField();
    }

    @Override
    public Class<NotificationsEditorFieldType> getFieldType() {
        return NotificationsEditorFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return NotificationsEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public NotificationsEditorFieldDefinition getDefaultField() {
        return new NotificationsEditorFieldDefinition();
    }
}

