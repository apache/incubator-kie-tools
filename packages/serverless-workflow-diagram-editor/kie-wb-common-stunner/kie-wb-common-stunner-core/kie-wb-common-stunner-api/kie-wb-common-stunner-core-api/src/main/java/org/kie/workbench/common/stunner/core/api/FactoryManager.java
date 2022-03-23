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

package org.kie.workbench.common.stunner.core.api;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;

/**
 * Entry point that handles the different Factories and provides
 * different high level operations for constructing types.
 */
public interface FactoryManager {

    /**
     * Creates a new Definition by a given identifier.
     * @param id The definition identifier ( Eg: "task" )
     * @param <T> The definition instance type ( Eg. Task )
     * @return A new definition instance.
     */
    <T> T newDefinition(String id);

    /**
     * Creates a new graph element for the given Definition identifier.
     * @param uuid The element unique identifier.
     * @param id The definition identifier.
     * @return A new graph, node or edge which content is based on the a Definition.
     */
    Element<?> newElement(String uuid,
                          String id);

    /**
     * Creates a new diagram for the given Definition Set identifier.
     * @param name The unique diagram's name.
     * @param definitionSetId The definition set identifier.
     * @param metadata The diagram metadata.
     * @param <D> The diagram type.
     * @return A new diagram instance.
     */
    <M extends Metadata, D extends Diagram> D newDiagram(String name,
                                                         String definitionSetId,
                                                         M metadata);

    /**
     * The registry that handles all different factories.
     * @return The factory registry.
     */
    FactoryRegistry registry();
}
