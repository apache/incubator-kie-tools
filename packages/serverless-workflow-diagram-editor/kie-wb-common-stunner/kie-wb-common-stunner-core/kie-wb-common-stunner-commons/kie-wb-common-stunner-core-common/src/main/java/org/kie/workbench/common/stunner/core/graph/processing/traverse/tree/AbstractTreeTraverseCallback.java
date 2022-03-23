/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.processing.traverse.tree;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;

public abstract class AbstractTreeTraverseCallback<G extends Graph, N extends Node, E extends Edge>
        implements TreeTraverseCallback<G, N, E> {

    @Override
    public void startGraphTraversal(final G graph) {
    }

    @Override
    public boolean startNodeTraversal(final N node) {
        return false;
    }

    @Override
    public boolean startEdgeTraversal(final E edge) {
        return false;
    }

    @Override
    public void endNodeTraversal(final N node) {
    }

    @Override
    public void endEdgeTraversal(final E edge) {
    }

    @Override
    public void endGraphTraversal() {
    }
}
