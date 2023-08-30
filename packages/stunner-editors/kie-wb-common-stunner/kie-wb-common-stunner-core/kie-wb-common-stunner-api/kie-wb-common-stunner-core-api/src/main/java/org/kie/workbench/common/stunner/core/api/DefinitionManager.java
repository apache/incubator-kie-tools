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


package org.kie.workbench.common.stunner.core.api;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.Default;

import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;

/**
 * Entry point for handling the different Definition Sets present on the context.
 */
public interface DefinitionManager {

    /**
     * The default qualifier for any Definition Set.
     */
    Annotation DEFAULT_QUALIFIER = new Default() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return Default.class;
        }
    };

    /**
     * The registry that contains the Definition Sets present on the context.
     * @return The definition set registry.
     */
    TypeDefinitionSetRegistry<?> definitionSets();

    /**
     * The manager for the different adapters present on the context.
     * @return The adapter manager.
     */
    AdapterManager adapters();

    CloneManager cloneManager();
}
