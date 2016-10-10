/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.processing.index.map;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.IncrementalIndexBuilder;

import javax.enterprise.context.Dependent;

@Dependent
public class IncrementalMapIndexBuilder extends MapIndexBuilder
        implements IncrementalIndexBuilder<Graph<?, Node>,
        Node, Edge, MapIndex> {

    @Override
    public IncrementalIndexBuilder<Graph<?, Node>, Node, Edge, MapIndex> addNode( final MapIndex index,
                                                                                  final Node node ) {
        assert index != null && node != null;
        index.nodes.put( node.getUUID(), node );
        return this;
    }

    @Override
    public IncrementalIndexBuilder<Graph<?, Node>, Node, Edge, MapIndex> removeNode( final MapIndex index,
                                                                                     final Node node ) {
        assert index != null && node != null;
        index.nodes.remove( node.getUUID() );
        return this;
    }

    @Override
    public IncrementalIndexBuilder<Graph<?, Node>, Node, Edge, MapIndex> addEdge( final MapIndex index,
                                                                                  final Edge edge ) {
        assert index != null && edge != null;
        index.edges.put( edge.getUUID(), edge );
        return this;
    }

    @Override
    public IncrementalIndexBuilder<Graph<?, Node>, Node, Edge, MapIndex> removeEdge( final MapIndex index,
                                                                                     final Edge edge ) {
        assert index != null && edge != null;
        index.edges.remove( edge.getUUID() );
        return this;
    }

    @Override
    public IncrementalIndexBuilder<Graph<?, Node>, Node, Edge, MapIndex> clear( MapIndex index ) {
        assert index != null;
        index.nodes.clear();
        index.edges.clear();
        return this;
    }

}
