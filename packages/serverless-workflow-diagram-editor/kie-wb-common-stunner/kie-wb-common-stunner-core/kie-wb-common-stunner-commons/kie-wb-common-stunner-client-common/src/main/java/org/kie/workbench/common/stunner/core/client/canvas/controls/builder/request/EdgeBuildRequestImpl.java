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


package org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public final class EdgeBuildRequestImpl extends AbstractBuildRequest implements EdgeBuildRequest {

    private final Edge<? extends ViewConnector<?>, Node> edge;
    private Node<? extends View<?>, Edge> inNode;
    private Node<? extends View<?>, Edge> outNode;

    public EdgeBuildRequestImpl(final double x,
                                final double y,
                                final Edge<? extends ViewConnector<?>, Node> edge,
                                final Node<? extends View<?>, Edge> inNode,
                                final Node<? extends View<?>, Edge> outNode) {
        super(x,
              y);
        this.edge = edge;
        this.inNode = inNode;
        this.outNode = outNode;
    }

    @Override
    public Edge<? extends ViewConnector<?>, Node> getEdge() {
        return edge;
    }

    @Override
    public Node<? extends View<?>, Edge> getInNode() {
        return inNode;
    }

    @Override
    public Node<? extends View<?>, Edge> getOutNode() {
        return outNode;
    }
}
