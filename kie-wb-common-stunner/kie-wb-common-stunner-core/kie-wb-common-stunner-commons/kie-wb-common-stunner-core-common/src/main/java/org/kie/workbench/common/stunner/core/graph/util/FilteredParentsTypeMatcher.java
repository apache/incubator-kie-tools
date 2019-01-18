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
import java.util.function.Predicate;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A predicate that checks if two nodes are using sharing the same parent
 * for a given type of Definition. It also filters a given node and
 * uses the given parent as a candidate for it, instead of processing the graph structure.
 */
public class FilteredParentsTypeMatcher
        implements BiPredicate<Node<? extends View<?>, ? extends Edge>, Node<? extends View<?>, ? extends Edge>> {

    private final ParentsTypeMatchPredicate parentsTypeMatchPredicate;
    private final NodeCompositePredicate parentsMatchPredicate;
    private final Optional<Element<? extends Definition<?>>> candidateParent;
    private final Optional<Node<? extends Definition<?>, ? extends Edge>> candidateNode;
    private final Optional<Class<?>> parentType;

    public FilteredParentsTypeMatcher(final DefinitionManager definitionManager,
                                      final Element<? extends Definition<?>> candidateParent,
                                      final Node<? extends Definition<?>, ? extends Edge> candidateNode,
                                      final Optional<Class<?>> parentType) {
        this.candidateParent = Optional.ofNullable(candidateParent);
        this.candidateNode = Optional.ofNullable(candidateNode);
        this.parentsTypeMatchPredicate = new ParentsTypeMatchPredicate(new FilteredParentByDefinitionIdProvider(definitionManager),
                                                                       new FilteredHasParentPredicate(),
                                                                       parentType.orElse(null));
        this.parentsMatchPredicate = new NodeCompositePredicate(parentsTypeMatchPredicate,
                                                                new ParentsMatchForCandidatePredicate());
        this.parentType = parentType;
    }

    @Override
    public boolean test(final Node<? extends View<?>, ? extends Edge> node,
                        final Node<? extends View<?>, ? extends Edge> node2) {
        checkNotNull("node", node);
        checkNotNull("node2", node2);

        //in case there are no parent type to be tested
        if (!parentType.isPresent()) {
            return true;
        }

        //in case none nodes are candidates
        if (!isCandidate.test(node) && !isCandidate.test(node2)) {
            return true;
        }

        return parentsMatchPredicate.test(node, node2);
    }

    private class FilteredHasParentPredicate implements BiPredicate<Node<?, ? extends Edge>, Element<?>> {

        private final GraphUtils.HasParentPredicate hasParentPredicate;

        private FilteredHasParentPredicate() {
            this.hasParentPredicate = new GraphUtils.HasParentPredicate();
        }

        @Override
        public boolean test(final Node<?, ? extends Edge> node,
                            final Element<?> parent) {
            return isCandidate.test(node) ?
                    candidateParent
                            .filter(p -> Objects.equals(p, parent))
                            .isPresent() :
                    hasParentPredicate.test(node,
                                            parent);
        }
    }

    /**
     * Check if a candidate parent is the same for a given candidate node and is the current parent for other node, on the test condition.
     * This condition is necessary when changing a connection (source or target), both nodes should have the same parent.
     */
    private class ParentsMatchForCandidatePredicate implements BiPredicate<Node<? extends View<?>, ? extends Edge>, Node<? extends View<?>, ? extends Edge>> {

        @Override
        public boolean test(Node<? extends View<?>, ? extends Edge> node, Node<? extends View<?>, ? extends Edge> node2) {
            //check if the parent of the target node connection is the same as the candidate parent
            return isCandidate.test(node)
                    ? Objects.equals(GraphUtils.getParent(node2), candidateParent.orElse(null))
                    : Objects.equals(GraphUtils.getParent(node), candidateParent.orElse(null));
        }
    }

    private class FilteredParentByDefinitionIdProvider
            implements BiFunction<Node<? extends View<?>, ? extends Edge>, Class<?>, Optional<Element<?>>> {

        private final ParentsTypeMatcher.ParentByDefinitionIdProvider provider;

        private FilteredParentByDefinitionIdProvider(final DefinitionManager definitionManager) {
            this.provider = new ParentsTypeMatcher.ParentByDefinitionIdProvider(definitionManager);
        }

        @Override
        public Optional<Element<?>> apply(final Node<? extends View<?>, ? extends Edge> node,
                                          final Class<?> parentType) {
            return getParent(node, parentType);
        }

        private Optional<Element<?>> getParent(final Node<? extends View<?>, ? extends Edge> node,
                                               final Class<?> parentType) {
            return isCandidate.test(node) ?
                    getCandidateParentInstance(parentType) :
                    provider.apply(node, parentType);
        }

        @SuppressWarnings("unchecked")
        private Optional<Element<?>> getCandidateParentInstance(final Class<?> parentType) {
            return candidateParent.isPresent() && Objects.nonNull(parentType) ?
                    (ParentsTypeMatcher.ParentByDefinitionIdProvider.getDefinitionIdByTpe(parentType)
                            .equals(getCandidateParentId().get()) ?
                            Optional.of(candidateParent.get()) :
                            getParent((Node<? extends View<?>, ? extends Edge>) candidateParent.get(),
                                      parentType)) :
                    Optional.empty();
        }

        private Optional<String> getCandidateParentId() {
            return candidateParent.isPresent() ?
                    Optional.ofNullable(provider.definitionManager.adapters()
                                                .forDefinition()
                                                .getId(candidateParent.get().getContent().getDefinition())
                                                .value()) :
                    Optional.empty();
        }
    }

    private Predicate<Node<?, ? extends Edge>> isCandidate = new Predicate<Node<?, ? extends Edge>>() {
        @Override
        public boolean test(final Node<?, ? extends Edge> node) {
            return candidateNode
                    .filter(node::equals)
                    .isPresent();
        }
    };
}
