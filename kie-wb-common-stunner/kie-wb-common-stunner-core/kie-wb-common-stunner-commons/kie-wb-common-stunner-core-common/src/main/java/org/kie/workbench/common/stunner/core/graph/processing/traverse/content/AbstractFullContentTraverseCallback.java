/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.processing.traverse.content;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public abstract class AbstractFullContentTraverseCallback<N extends Node<View, Edge>, E extends Edge<Object, Node>>
        implements FullContentTraverseCallback<N, E> {

    @Override
    public void startViewEdgeTraversal( E edge ) {
    }

    @Override
    public void endViewEdgeTraversal( E edge ) {
    }

    @Override
    public void startChildEdgeTraversal( E edge ) {
    }

    @Override
    public void endChildEdgeTraversal( E edge ) {
    }

    @Override
    public void startParentEdgeTraversal( E edge ) {
    }

    @Override
    public void endParentEdgeTraversal( E edge ) {
    }

    @Override
    public void startGraphTraversal( Graph<DefinitionSet, N> graph ) {
    }

    @Override
    public void startEdgeTraversal( E edge ) {
    }

    @Override
    public void endEdgeTraversal( E edge ) {
    }

    @Override
    public void startNodeTraversal( N node ) {
    }

    @Override
    public void endNodeTraversal( N node ) {
    }

    @Override
    public void endGraphTraversal() {
    }

}
