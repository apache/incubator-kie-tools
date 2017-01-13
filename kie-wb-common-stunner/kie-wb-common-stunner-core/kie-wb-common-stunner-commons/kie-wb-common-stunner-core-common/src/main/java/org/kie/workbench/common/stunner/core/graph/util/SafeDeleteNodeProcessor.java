/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.util;

import java.util.List;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

// TODO: Handle other edge contents (dock) - generic or hardcoded?

public class SafeDeleteNodeProcessor {

    public interface Callback {

        void deleteChildNode(final Node<Definition<?>, Edge> node);

        void deleteInViewEdge(final Edge<View<?>, Node> edge);

        void deleteInChildEdge(final Edge<Child, Node> edge);

        void deleteInDockEdge(final Edge<Dock, Node> edge);

        void deleteOutViewEdge(final Edge<? extends View<?>, Node> edge);

        void deleteNode(final Node<Definition<?>, Edge> node);
    }

    private final Node<Definition<?>, Edge> candidate;

    public SafeDeleteNodeProcessor(final Node<Definition<?>, Edge> candidate) {
        this.candidate = candidate;
    }

    @SuppressWarnings("unchecked")
    public void run(final Callback callback) {
        // Check outgoing edges.
        final List<Edge> outEdges = candidate.getOutEdges();
        if (null != outEdges && !outEdges.isEmpty()) {
            for (final Edge outEdge : outEdges) {
                if (outEdge.getContent() instanceof View) {
                    callback.deleteOutViewEdge(outEdge);
                } else if (outEdge.getContent() instanceof Child) {
                    final Node target = outEdge.getTargetNode();
                    callback.deleteChildNode(target);
                }
            }
        }
        // Check incoming edges.
        final List<Edge> inEdges = candidate.getInEdges();
        if (null != inEdges && !inEdges.isEmpty()) {
            for (final Edge inEdge : inEdges) {
                if (inEdge.getContent() instanceof Child) {
                    callback.deleteInChildEdge(inEdge);
                }
                if (inEdge.getContent() instanceof Dock) {
                    callback.deleteInDockEdge(inEdge);
                } else if (inEdge.getContent() instanceof View) {
                    callback.deleteInViewEdge(inEdge);
                }
            }
        }
        // Finally delete this node in a safe way.
        callback.deleteNode(candidate);
    }
}
