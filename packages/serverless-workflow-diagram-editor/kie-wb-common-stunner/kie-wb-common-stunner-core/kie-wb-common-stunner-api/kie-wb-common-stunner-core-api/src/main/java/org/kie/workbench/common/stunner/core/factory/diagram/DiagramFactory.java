/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.factory.diagram;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.Factory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;

/**
 * Base Stunner factory type for generic Diagrams.
 */
public interface DiagramFactory<M extends Metadata, D extends Diagram<Graph, M>>
        extends Factory<String> {

    /**
     * The metadata class supported by the diagram types of this factory.
     */
    Class<? extends Metadata> getMetadataType();

    /**
     * Returns if the factory implementation should be used as the default one for a given Metadata type.
     */
    boolean isDefault();

    /**
     * Builds a diagram instance.
     * @param name The diagram's name.
     * @param metadata The diagram's metadata.
     * @param graph The diagram's graph
     */
    D build(final String name,
            final M metadata,
            final Graph<DefinitionSet, ?> graph);
}
