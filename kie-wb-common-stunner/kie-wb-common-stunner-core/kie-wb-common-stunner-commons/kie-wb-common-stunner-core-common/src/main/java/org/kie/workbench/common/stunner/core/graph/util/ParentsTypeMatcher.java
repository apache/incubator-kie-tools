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

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * A predicate that checks if two nodes are using sharing the same parent
 * for a given type of Definition.
 */
public class ParentsTypeMatcher
        implements BiPredicate<Node<? extends View<?>, ? extends Edge>, Node<? extends View<?>, ? extends Edge>> {

    private final ParentsTypeMatchPredicate parentsTypeMatch;

    public ParentsTypeMatcher(final DefinitionManager definitionManager) {
        this.parentsTypeMatch = new ParentsTypeMatchPredicate(new ParentByDefinitionIdProvider(definitionManager),
                                                              new GraphUtils.HasParentPredicate());
    }

    public ParentsTypeMatcher forParentType(final Class<?> parentType) {
        this.parentsTypeMatch.forParentType(parentType);
        return this;
    }

    @Override
    public boolean test(final Node<? extends View<?>, ? extends Edge> node,
                        final Node<? extends View<?>, ? extends Edge> node2) {
        return parentsTypeMatch.test(node,
                                     node2);
    }

    static class ParentByDefinitionIdProvider
            implements BiFunction<Node<? extends View<?>, ? extends Edge>, Class<?>, Optional<Element<?>>> {

        final DefinitionManager definitionManager;

        ParentByDefinitionIdProvider(final DefinitionManager definitionManager) {
            this.definitionManager = definitionManager;
        }

        @Override
        public Optional<Element<?>> apply(final Node<? extends View<?>, ? extends Edge> node,
                                          final Class<?> parentType) {
            return Objects.nonNull(node) && Objects.nonNull(parentType)
                    ? GraphUtils.getParentByDefinitionId(definitionManager,
                                                         node,
                                                         getDefinitionIdByTpe(parentType))
                    : Optional.empty();
        }

        static String getDefinitionIdByTpe(final Class<?> type) {
            return BindableAdapterUtils.getDefinitionId(type);
        }
    }
}
