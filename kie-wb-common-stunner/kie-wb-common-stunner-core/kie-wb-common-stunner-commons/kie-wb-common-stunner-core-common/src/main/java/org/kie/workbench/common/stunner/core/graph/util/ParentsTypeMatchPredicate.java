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

package org.kie.workbench.common.stunner.core.graph.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A predicate that checks if two nodes are using sharing the same parent
 * for a given type of Definition.
 */
class ParentsTypeMatchPredicate implements BiPredicate<Node<? extends View<?>, ? extends Edge>, Node<? extends View<?>, ? extends Edge>> {

    private final BiFunction<Node<? extends View<?>, ? extends Edge>, Class<?>, Optional<Element<?>>> parentProvider;
    private final BiPredicate<Node<?, ? extends Edge>, Element<?>> hasParentPredicate;
    private Class<?> parentType;

    ParentsTypeMatchPredicate(final BiFunction<Node<? extends View<?>, ? extends Edge>, Class<?>, Optional<Element<?>>> parentProvider,
                              final BiPredicate<Node<?, ? extends Edge>, Element<?>> hasParentPredicate) {
        this.parentProvider = parentProvider;
        this.hasParentPredicate = hasParentPredicate;
    }

    public ParentsTypeMatchPredicate forParentType(final Class<?> parentType) {
        this.parentType = parentType;
        return this;
    }

    /**
     * Tests if both nodes have same parent instance, for the given type.
     * @param nodeA A node
     * @param nodeB A node
     * @return It returns <code>true</code> in case both nodes exist and both are
     * child of the same parent instance for the given type, or in case bot nodes
     * exist and both are not child of any parent instance of the given type.
     */
    @Override
    public boolean test(final Node<? extends View<?>, ? extends Edge> nodeA,
                        final Node<? extends View<?>, ? extends Edge> nodeB) {

        checkNotNull("nodeA", nodeA);
        checkNotNull("nodeB", nodeB);
        final Optional<Element<?>> parentInstanceA = getParentInstance(nodeA, parentType);
        final Optional<Element<?>> parentInstanceB = getParentInstance(nodeB, parentType);

        return (parentInstanceA.isPresent() && parentInstanceB.isPresent())
                && (Objects.equals(parentInstanceA.get(), parentInstanceB.get()))
                && (hasParent(nodeA, parentInstanceA.get()) && hasParent(nodeB, parentInstanceB.get()));
    }

    private Optional<Element<?>> getParentInstance(final Node<? extends View<?>, ? extends Edge> node,
                                                   final Class<?> aClass) {
        return parentProvider.apply(node,
                                    aClass);
    }

    private boolean hasParent(final Node<?, ? extends Edge> node,
                              final Element<?> parent) {
        return hasParentPredicate.test(node,
                                       parent);
    }
}
