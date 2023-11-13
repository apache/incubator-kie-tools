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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.TypeKind;

public abstract class SelectorFieldProvider<DEFINITION extends SelectorFieldBaseDefinition> extends BasicTypeFieldProvider<DEFINITION> {

    @Override
    public boolean isCompatible(FieldDefinition field) {
        if (super.isCompatible(field)) {
            return true;
        }
        return field.getFieldTypeInfo().getType().equals(TypeKind.ENUM);
    }

    public boolean supports(Class clazz) {
        if (super.supports(clazz)) {
            return true;
        }
        return clazz.isEnum();
    }
}
