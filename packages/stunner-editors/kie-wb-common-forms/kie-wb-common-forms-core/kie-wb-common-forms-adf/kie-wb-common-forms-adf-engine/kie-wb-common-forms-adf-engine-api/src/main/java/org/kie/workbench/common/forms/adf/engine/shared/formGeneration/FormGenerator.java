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


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;
import org.kie.workbench.common.forms.model.FormDefinition;

/**
 * Component able to generate a {@link FormDefinition} for java objects registered on the ADF Engine
 */
public interface FormGenerator {

    /**
     * Generates a {@link FormDefinition} for the given model if there are {@link FormDefinitionSettings} on the ADF
     * Engine for it. It uses the {@link FormElementFilter} params to filter form fields if needed.
     */
    FormDefinition generateFormForModel(Object model, FormElementFilter... filters);

    /**
     * Generates a {@link FormDefinition} for the given Class if there are {@link FormDefinitionSettings} on the ADF
     * Engine for it. It uses the {@link FormElementFilter} params to filter form fields if needed.
     */
    FormDefinition generateFormForClass(Class clazz, FormElementFilter... filters);

    /**
     * Generates a {@link FormDefinition} for the given className if there are {@link FormDefinitionSettings} on the ADF
     * Engine for it. It uses the {@link FormElementFilter} params to filter form fields if needed.
     */
    FormDefinition generateFormForClassName(String className, FormElementFilter... filters);
}
