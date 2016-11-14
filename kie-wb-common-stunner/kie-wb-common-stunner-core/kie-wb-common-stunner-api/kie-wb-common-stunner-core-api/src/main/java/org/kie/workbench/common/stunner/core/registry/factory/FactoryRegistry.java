/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.registry.factory;

import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.Factory;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.registry.DynamicRegistry;

import java.util.Collection;

/**
 * Base registry type for different domain model object's factories.
 * @param <F> The type of the factory.
 */
public interface FactoryRegistry<F extends Factory<?>> extends DynamicRegistry<F> {

    /**
     * Return the factory for the Definition with <code>id</code> as identifier.
     * @param id The identifier for the Definition.
     */
    DefinitionFactory<?> getDefinitionFactory( String id );

    /**
     * Returns the factory for the type of the graph element.
     * @param type The graph element type, such as Nodes, Edges.
     */
    ElementFactory<?, ?, ?> getElementFactory( Class<? extends ElementFactory> type );

    /**
     * Returns the diagram factory for the given metadata type and the given DefinitionSet item.
     * @param id The Definition Set identifier for the diagram's graph.
     * @param metadataType The diagram's metadata type
     */
    DiagramFactory<?, ?> getDiagramFactory( String id, Class<? extends Metadata> metadataType );

    /**
     * Return all registered factories.
     */
    Collection<F> getAllFactories();

    /**
     * Clears this factory.
     */
    void clear();

}
