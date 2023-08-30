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


package org.kie.workbench.common.stunner.core.graph.util;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * A class that checks if two nodes are using sharing the same parent type, given by certain Definition's types.
 * Only checks the parent types when both nodes are present.
 */
public class ParentTypesMatcher {

    private final Supplier<DefinitionManager> definitionManagerSupplier;
    private final Function<Node, Element> parentSupplier;
    private final Set<String> typeIds;

    public ParentTypesMatcher(final Supplier<DefinitionManager> definitionManagerSupplier,
                              final Function<Node, Element> parentSupplier,
                              final Class<?>[] types) {
        this.definitionManagerSupplier = definitionManagerSupplier;
        this.parentSupplier = parentSupplier;
        this.typeIds = Stream.of(types)
                .map(BindableAdapterUtils::getDefinitionId)
                .collect(Collectors.toSet());
    }

    /**
     * Tests if both nodes have same parent instance, for the given type.
     * @return It returns <code>true</code> in case both nodes exist and both are
     * child of the same parent instance of any of the given types, or in case bot nodes
     * exist and both are not child of any parent instance for the given types.
     */
    public BiPredicate<Node<? extends View<?>, ? extends Edge>, Node<? extends View<?>, ? extends Edge>> matcher() {
        return this::match;
    }

    private boolean match(final Node<? extends View<?>, ? extends Edge> nodeA,
                          final Node<? extends View<?>, ? extends Edge> nodeB) {
        // If no source or target nodes are present, this matcher does not apply and so it allows the operation.
        if ((nodeA == null && nodeB != null) || (nodeB == null && nodeA != null)) {
            return true;
        }
        final Optional<Element<? extends Definition>> parentA = getAnySupportedParent(nodeA);
        final Optional<Element<? extends Definition>> parentB = getAnySupportedParent(nodeB);
        return parentA.equals(parentB);
    }

    @SuppressWarnings("unchecked")
    private Optional<Element<? extends Definition>> getAnySupportedParent(final Element<? extends Definition<?>> candidate) {
        if (null != candidate && null != candidate.asNode()) {
            final Node<? extends Definition<?>, ? extends Edge> node = candidate.asNode();
            return GraphUtils.getParentByDefinitionId(definitionManagerSupplier.get(),
                                                      parentSupplier,
                                                      node,
                                                      typeIds::contains);
        }
        return Optional.empty();
    }
}
