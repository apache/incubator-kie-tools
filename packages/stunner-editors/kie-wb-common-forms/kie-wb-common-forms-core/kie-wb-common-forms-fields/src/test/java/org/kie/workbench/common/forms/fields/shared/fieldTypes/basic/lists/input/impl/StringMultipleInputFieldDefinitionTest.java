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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl;

import org.junit.Test;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.AbstractFieldDefinitionTest;
import org.kie.workbench.common.forms.model.TypeKind;

import static org.junit.Assert.assertEquals;

public class StringMultipleInputFieldDefinitionTest extends AbstractFieldDefinitionTest<StringMultipleInputFieldDefinition> {

    @Override
    protected StringMultipleInputFieldDefinition getEmptyFieldDefinition() {
        return new StringMultipleInputFieldDefinition();
    }

    @Override
    protected StringMultipleInputFieldDefinition getFullFieldDefinition() {
        StringMultipleInputFieldDefinition fieldDefinition = new StringMultipleInputFieldDefinition();

        fieldDefinition.setPageSize(6);

        return fieldDefinition;
    }

    @Test
    public void testGetFieldType() {
        StringMultipleInputFieldDefinition fieldDefinition = new StringMultipleInputFieldDefinition();
        fieldDefinition.setStandaloneClassName(String.class.getName());
        assertEquals(TypeKind.BASE, fieldDefinition.getFieldTypeInfo().getType());
        fieldDefinition.setStandaloneClassName(Object.class.getName());
        assertEquals(TypeKind.OBJECT, fieldDefinition.getFieldTypeInfo().getType());
    }
}
