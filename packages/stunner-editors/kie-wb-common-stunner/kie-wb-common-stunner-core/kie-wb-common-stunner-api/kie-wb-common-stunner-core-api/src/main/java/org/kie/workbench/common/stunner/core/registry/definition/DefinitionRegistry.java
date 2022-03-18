/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.registry.definition;

import org.kie.workbench.common.stunner.core.registry.DynamicRegistry;

/**
 * Base registry type for Definitions.
 * @param <D> The type of the Definition.
 */
public interface DefinitionRegistry<D> extends DynamicRegistry<D> {

    /**
     * Lookup the Definition of type <code>D</code> by its identifier.
     * @param id The Definition's identifier criteria.
     * @return The Definition of type <code>D</code> that this registry contains, <code>null</code> otherwise.
     */
    D getDefinitionById(final String id);

    /**
     * Clears the registry.
     */
    void clear();
}
