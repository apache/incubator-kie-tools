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


package org.kie.workbench.common.stunner.core.registry.definition;

import java.util.Collection;

import org.kie.workbench.common.stunner.core.registry.Registry;

/**
 * Base registry type for Definition Sets.
 * @param <T> The type of the Definition Set.
 */
public interface DefinitionSetRegistry<T> extends Registry<T> {

    /**
     * Lookup the Definition Set of type <code>T</code> by its identifier.
     * @param id The Definition Set's identifier criteria.
     * @return The Defintiion Set of type <code>T</code> that this registry contains, <code>null</code> ortherwise.
     */
    T getDefinitionSetById(final String id);

    /**
     * Retun a collection of all Definition Set's present on this registry.
     */
    Collection<T> getAllDefinitionSets();
}
