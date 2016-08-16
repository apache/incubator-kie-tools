/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.service;

import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldTypeInfo;

import java.util.Collection;

public interface FieldManager {

    public static final String FIELD_NAME_SEPARATOR = "_";

    Collection<String> getBaseFieldTypes();

    FieldDefinition getDefinitionByTypeCode( String typeCode );

    FieldDefinition getDefinitionByValueType( FieldTypeInfo typeInfo );

    Collection<String> getCompatibleFields( FieldDefinition fieldDefinition );

    FieldDefinition getFieldFromProvider( String typeCode, FieldTypeInfo typeInfo );

    FieldDefinition getFieldFromProviderWithType( String typeCode, FieldTypeInfo typeInfo );
}
