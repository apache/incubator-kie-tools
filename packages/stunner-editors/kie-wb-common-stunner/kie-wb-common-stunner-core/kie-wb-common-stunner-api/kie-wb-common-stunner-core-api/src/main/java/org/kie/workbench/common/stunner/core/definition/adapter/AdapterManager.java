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


package org.kie.workbench.common.stunner.core.definition.adapter;

import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;

/**
 * Entry point for handling all definition adapters present on the context.
 */
public interface AdapterManager {

    /**
     * A generic definition set adapter for any type. It provides a shortcut
     * for introspecting definition sets.
     *
     * @return The definition set adapter.
     */
    DefinitionSetAdapter<Object> forDefinitionSet();

    /**
     * A generic rule adapter for any type. It provides a shortcut
     * for introspecting the rules on for the different definition sets.
     *
     * @return The rule adapter.
     */
    DefinitionSetRuleAdapter<Object> forRules();

    /**
     * A generic definition adapter for any type. It provides a shortcut
     * for introspecting definitions.
     *
     * @return The definition adapter.
     */
    DefinitionAdapter<Object> forDefinition();

    /**
     * A generic property adapter for any type. It provides a shortcut
     * for introspecting properties.
     *
     * @return The property adapter.
     */
    PropertyAdapter<Object, Object> forProperty();

    /**
     * The registry that contains all the adapters present on the context.
     *
     * @return The adapter registry.
     */
    AdapterRegistry registry();
}
