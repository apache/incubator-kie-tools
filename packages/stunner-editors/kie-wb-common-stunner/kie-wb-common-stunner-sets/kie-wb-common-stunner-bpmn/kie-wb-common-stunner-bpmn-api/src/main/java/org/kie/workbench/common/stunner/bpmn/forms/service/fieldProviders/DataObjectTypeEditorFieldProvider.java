/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.forms.service.fieldProviders;

import javax.enterprise.inject.Model;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.DataObjectTypeFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.DataObjectTypeFieldType;

@Model
public class DataObjectTypeEditorFieldProvider extends BasicTypeFieldProvider<DataObjectTypeFieldDefinition> {

    @Override
    public int getPriority() {
        return 20004;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(DataObjectTypeValue.class);
    }

    @Override
    public DataObjectTypeFieldDefinition createFieldByType(TypeInfo typeInfo) {
        return getDefaultField();
    }

    @Override
    public Class<DataObjectTypeFieldType> getFieldType() {
        return DataObjectTypeFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return DataObjectTypeFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public DataObjectTypeFieldDefinition getDefaultField() {
        return new DataObjectTypeFieldDefinition();
    }
}

