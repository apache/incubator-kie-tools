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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.provider;

import javax.enterprise.context.Dependent;

import org.jboss.errai.common.client.api.Assert;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.ModelTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.type.SubFormFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;

@Dependent
public class SubFormFieldProvider
        implements ModelTypeFieldProvider<SubFormFieldDefinition> {

    @Override
    public Class<SubFormFieldType> getFieldType() {
        return SubFormFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return SubFormFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public SubFormFieldDefinition getDefaultField() {
        return new SubFormFieldDefinition();
    }

    @Override
    public SubFormFieldDefinition getFieldByType(TypeInfo typeInfo) {
        if (typeInfo.getType().equals(TypeKind.OBJECT)) {
            return getDefaultField();
        }
        return null;
    }

    @Override
    public boolean isCompatible(FieldDefinition field) {
        Assert.notNull("Field cannot be null",
                       field);

        return field instanceof SubFormFieldDefinition;
    }
}
