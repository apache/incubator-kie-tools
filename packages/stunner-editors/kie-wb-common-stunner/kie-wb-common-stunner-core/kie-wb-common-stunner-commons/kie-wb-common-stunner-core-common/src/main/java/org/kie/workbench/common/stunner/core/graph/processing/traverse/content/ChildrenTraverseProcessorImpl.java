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


package org.kie.workbench.common.stunner.core.graph.processing.traverse.content;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

@Dependent
public final class ChildrenTraverseProcessorImpl extends AbstractContentTraverseProcessor<Child, Node<View, Edge>, Edge<Child, Node>, ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>>
        implements ChildrenTraverseProcessor {

    private final ParentStack parentStack = new ParentStack();

    @Inject
    public ChildrenTraverseProcessorImpl(final TreeWalkTraverseProcessor treeWalkTraverseProcessor) {
        super(treeWalkTraverseProcessor);
        treeWalkTraverseProcessor.useStartNodePredicate(node -> !node.getInEdges().stream()
                .filter(e -> e.getContent() instanceof Child)
                .findAny()
                .isPresent());
    }

    @Override
    public ChildrenTraverseProcessor setRootUUID(final String rootUUID) {
        parentStack.setRootUUID(rootUUID);
        return this;
    }

    @Override
    protected void doStartGraphTraversal(final Graph graph,
                                         final ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> callback) {
        parentStack.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean doStartEdgeTraversal(final Edge edge,
                                           final ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> callback) {
        if (accepts(edge)) {
            final Node<View, Edge> parent = edge.getSourceNode();
            parentStack.push(parent);
            callback.startEdgeTraversal(edge);
            return true;
        }
        return false;
    }

    @Override
    protected boolean doEndEdgeTraversal(final Edge edge,
                                         final ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> callback) {
        if (accepts(edge)) {
            parentStack.pop();
            callback.endEdgeTraversal(edge);
            return true;
        }
        return false;
    }

    @Override
    protected void doEndGraphTraversal(final Graph graph,
                                       final ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> callback) {
        callback.endGraphTraversal();
        parentStack.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean doStartNodeTraversal(final Node node,
                                           final ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> callback) {

        if (!parentStack.isRootDefined() || parentStack.isRootPresent()) {
            return fireNodeTraverseCallback(node,
                                            callback);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean fireNodeTraverseCallback(final Node node,
                                             final ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> callback) {
        if (!parentStack.isEmpty()) {
            return callback.startNodeTraversal(parentStack.asList(),
                                               node);
        } else {
            callback.startNodeTraversal(node);
            return true;
        }
    }

    @Override
    protected boolean accepts(final Edge edge) {
        return edge.getContent() instanceof Child;
    }

    private class ParentStack {

        private final Stack<Node<View, Edge>> stack = new Stack<>();
        private Optional<String> rootUUID;
        private boolean hasParent;

        public ParentStack() {
            this.rootUUID = Optional.empty();
            this.hasParent = false;
        }

        public void setRootUUID(final String uuid) {
            this.rootUUID = Optional.ofNullable(uuid);
        }

        public Node<View, Edge> push(final Node<View, Edge> item) {
            if (isRootUUID(item)) {
                hasParent = true;
            }
            return stack.push(item);
        }

        public Node<View, Edge> peek() {
            return stack.peek();
        }

        public Node<View, Edge> pop() {
            final Node<View, Edge> pop = stack.pop();
            if (isRootUUID(pop)) {
                hasParent = false;
            }
            return pop;
        }

        public void clear() {
            hasParent = false;
            stack.clear();
        }

        public boolean isRootDefined() {
            return this.rootUUID.isPresent();
        }

        public boolean isRootPresent() {
            return hasParent;
        }

        public boolean isEmpty() {
            return stack.isEmpty();
        }

        public List<Node<View, Edge>> asList() {
            return stack.stream()
                    .collect(Collectors.toList());
        }

        private boolean isRootUUID(final Node node) {
            return rootUUID.isPresent() &&
                    null != node &&
                    node.getUUID().equals(rootUUID.get());
        }
    }
}
