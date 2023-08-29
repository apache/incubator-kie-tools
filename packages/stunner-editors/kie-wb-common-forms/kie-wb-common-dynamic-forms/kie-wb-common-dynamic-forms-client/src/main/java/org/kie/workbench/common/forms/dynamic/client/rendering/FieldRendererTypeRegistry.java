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


package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.forms.adf.rendering.FieldRendererTypesProvider;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;

public class FieldRendererTypeRegistry {

    private static Map<Class<? extends FieldType>, Class<? extends FieldRenderer>> fieldTypeRenderers = new HashMap<>();

    private static Map<Class<? extends FieldDefinition>, Class<? extends FieldRenderer>> fieldDefinitionRemderers = new HashMap<>();

    private FieldRendererTypeRegistry() {
    }

    public static void load(FieldRendererTypesProvider provider) {
        if (!provider.getFieldTypeRenderers().isEmpty()) {
            fieldTypeRenderers.putAll(provider.getFieldTypeRenderers());
        }
        if (!provider.getFieldDefinitionRenderers().isEmpty()) {
            fieldDefinitionRemderers.putAll(provider.getFieldDefinitionRenderers());
        }
    }

    public static Class<? extends FieldRenderer> getFieldRenderer(FieldDefinition fieldDefinition) {
        Class<? extends FieldRenderer> rendererClass = fieldDefinitionRemderers.get(fieldDefinition.getClass());

        if (rendererClass == null) {
            rendererClass = fieldTypeRenderers.get(fieldDefinition.getFieldType().getClass());
        }

        return rendererClass;
    }
}
