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

package org.kie.workbench.common.forms.service.shared;

import java.util.Collection;

import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;

public interface FieldManager {

    String FIELD_NAME_SEPARATOR = "_";

    Collection<String> getBaseFieldTypes();

    FieldDefinition getDefinitionByFieldType(FieldType fieldType);

    FieldDefinition getDefinitionByFieldTypeName(String typeCode);

    FieldDefinition getDefinitionByDataType(TypeInfo typeInfo);

    FieldDefinition getDefinitionByModelProperty(ModelProperty modelProperty);

    Collection<String> getCompatibleFields(FieldDefinition fieldDefinition);

    Collection<String> getCompatibleTypes(FieldDefinition fieldDefinition);

    FieldDefinition getFieldFromProvider(String typeCode,
                                         TypeInfo typeInfo);

    FieldDefinition getDefinitionByFieldType(Class<? extends FieldType> fieldType,
                                             TypeInfo typeInfo);
}
