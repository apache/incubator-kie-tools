/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorItemFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;

@Dependent
public class DecisionNavigatorChildrenTraverse {

    private final ChildrenTraverseProcessor traverseProcessor;

    private final DecisionNavigatorItemFactory itemFactory;

    @Inject
    public DecisionNavigatorChildrenTraverse(final ChildrenTraverseProcessor traverseProcessor,
                                             final DecisionNavigatorItemFactory itemFactory) {
        this.traverseProcessor = traverseProcessor;
        this.itemFactory = itemFactory;
    }

    public List<DecisionNavigatorItem> getItems(final Graph graph) {

        final TraverseCallback traverseCallback = makeTraverseCallback();

        traverseProcessor.traverse(graph, traverseCallback);

        return traverseCallback.getItems();
    }

    TraverseCallback makeTraverseCallback() {
        return new TraverseCallback();
    }

    class TraverseCallback extends AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> {

        private List<DecisionNavigatorItem> items;

        TraverseCallback() {
            this.items = new ArrayList<>();
        }

        List<DecisionNavigatorItem> getItems() {
            return items;
        }

        @Override
        public boolean startNodeTraversal(final List<Node<View, Edge>> parents,
                                          final Node<View, Edge> node) {

            super.startNodeTraversal(parents, node);

            final Node<View, Edge> parentNode = parents.get(0);
            final DecisionNavigatorItem item = itemFactory.makeItem(node);

            findItem(parentNode).ifPresent(parent -> parent.addChild(item));

            return true;
        }

        @Override
        public void startNodeTraversal(final Node<View, Edge> node) {
            super.startNodeTraversal(node);
            getItems().add(itemFactory.makeRoot(node));
        }

        Optional<DecisionNavigatorItem> findItem(final Node<View, Edge> node) {

            return getItems()
                    .stream()
                    .filter(item -> Objects.equals(item.getUUID(), node.getUUID()))
                    .findFirst();
        }
    }
}
