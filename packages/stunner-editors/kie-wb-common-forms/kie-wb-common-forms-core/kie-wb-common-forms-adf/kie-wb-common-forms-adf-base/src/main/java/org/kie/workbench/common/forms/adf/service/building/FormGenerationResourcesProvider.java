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


package org.kie.workbench.common.forms.adf.service.building;

import java.util.Map;

import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;

/**
 * Definition of a resource provider for the Annotation Driven Forms Engine.
 * <p>
 * The implementation for this class is automatically generated via annotation processing
 */
public interface FormGenerationResourcesProvider {

    /**
     * Retrieves a Map containing all the links between classes and it's {@link FormDefinitionSettings} on the module.
     */
    Map<String, FormDefinitionSettings> getDefinitionSettings();

    /**
     * Retrieves a Map containing the all the module {@link FieldStatusModifier}
     */
    Map<String, FieldStatusModifier> getFieldModifiers();

    /**
     * Retrieves a Map containing the link between fieldNames and {@link FieldStatusModifier} name on the module.
     */
    Map<String, String> getFieldModifierReferences();
}
