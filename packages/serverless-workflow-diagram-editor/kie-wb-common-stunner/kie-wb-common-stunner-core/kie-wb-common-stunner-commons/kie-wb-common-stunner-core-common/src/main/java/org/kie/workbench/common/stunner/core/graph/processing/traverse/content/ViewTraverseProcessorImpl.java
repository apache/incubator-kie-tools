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

import java.util.Objects;
import java.util.function.Predicate;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

@Dependent
public class ViewTraverseProcessorImpl extends AbstractContentTraverseProcessor<View<?>, Node<View, Edge>, Edge<View<?>, Node>, ContentTraverseCallback<View<?>, Node<View, Edge>, Edge<View<?>, Node>>>
        implements ViewTraverseProcessor {

    @Inject
    public ViewTraverseProcessorImpl(final TreeWalkTraverseProcessor treeWalkTraverseProcessor) {
        super(treeWalkTraverseProcessor);
        treeWalkTraverseProcessor.useStartNodePredicate(newStartNodePredicate());
    }

    protected Predicate<Node<?, Edge>> newStartNodePredicate() {
        return node -> !node.getInEdges().stream()
                .filter(ViewTraverseProcessorImpl::isViewEdge)
                .filter(ViewTraverseProcessorImpl::isNotCyclicEdge)
                .findAny()
                .isPresent();
    }

    @Override
    protected boolean accepts(final Edge edge) {
        return isViewEdge(edge);
    }

    private static boolean isViewEdge(final Edge edge) {
        return edge.getContent() instanceof View;
    }

    private static boolean isNotCyclicEdge(final Edge edge) {
        final String sourceId = edge.getSourceNode() != null ? edge.getSourceNode().getUUID() : null;
        final String targetId = edge.getTargetNode() != null ? edge.getTargetNode().getUUID() : null;
        return !Objects.equals(sourceId, targetId);
    }
}
