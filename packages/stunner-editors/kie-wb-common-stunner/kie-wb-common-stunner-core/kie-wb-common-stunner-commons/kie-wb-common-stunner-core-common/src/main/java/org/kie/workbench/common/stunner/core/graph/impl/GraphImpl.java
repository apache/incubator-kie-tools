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


package org.kie.workbench.common.stunner.core.graph.impl;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStore;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@Portable
public class GraphImpl<C> extends AbstractElement<C> implements Graph<C, Node> {

    private final GraphNodeStore<Node> nodeStore;

    public static <C> GraphImpl<C> build(final String uuid) {
        return new GraphImpl<>(uuid, new GraphNodeStoreImpl());
    }

    public GraphImpl(final @MapsTo("uuid") String uuid,
                     final @MapsTo("nodeStore") GraphNodeStore<Node> nodeStore) {
        super(uuid);
        this.nodeStore = Objects.requireNonNull(nodeStore, "Parameter named 'nodeStore' should be not null!");
    }

    @Override
    public Node addNode(final Node node) {
        return nodeStore.add(node);
    }

    @Override
    public Node removeNode(final String uuid) {
        return nodeStore.remove(uuid);
    }

    @Override
    public Node getNode(final String uuid) {
        return nodeStore.get(uuid);
    }

    @Override
    public Iterable<Node> nodes() {
        return nodeStore;
    }

    @Override
    public void clear() {
        nodeStore.clear();
    }

    @Override
    public Node<C, Edge> asNode() {
        return null;
    }

    @Override
    public Edge<C, Node> asEdge() {
        return null;
    }

    @Override
    public int hashCode() {
        return GraphUtils.computeGraphHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GraphImpl) {
            GraphImpl g = (GraphImpl) o;
            return this.hashCode() == g.hashCode();
        } else {
            return false;
        }
    }
}
