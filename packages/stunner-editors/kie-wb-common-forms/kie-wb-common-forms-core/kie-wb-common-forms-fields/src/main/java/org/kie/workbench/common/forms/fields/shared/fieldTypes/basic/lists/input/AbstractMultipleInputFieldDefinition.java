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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;

public abstract class AbstractMultipleInputFieldDefinition extends AbstractFieldDefinition {

    private static final MultipleInputFieldType FIELD_TYPE = new MultipleInputFieldType();

    @FormField(
            labelKey = "pageSize",
            afterElement = "label"
    )
    private Integer pageSize = 5;

    public AbstractMultipleInputFieldDefinition(String className) {
        super(className);
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof AbstractMultipleInputFieldDefinition) {
            AbstractMultipleInputFieldDefinition otherInput = (AbstractMultipleInputFieldDefinition) other;
            this.pageSize = otherInput.pageSize;
        }
    }

    @Override
    public TypeInfo getFieldTypeInfo() {
        TypeKind typeKind = Object.class.getName().equals(getStandaloneClassName()) ? TypeKind.OBJECT : TypeKind.BASE;
        return new TypeInfoImpl(typeKind,
                                getStandaloneClassName(),
                                true);
    }

    @Override
    public FieldType getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        AbstractMultipleInputFieldDefinition that = (AbstractMultipleInputFieldDefinition) o;

        return pageSize != null ? pageSize.equals(that.pageSize) : that.pageSize == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (pageSize != null ? pageSize.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
