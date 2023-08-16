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
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public final class NodeBuildRequestImpl extends AbstractBuildRequest implements NodeBuildRequest {

    private final Node<? extends View<?>, Edge> node;
    private Edge<? extends ViewConnector<?>, Node> inEdge;
    private final Connection sourceConnection;
    private final Connection targetConnection;

    public NodeBuildRequestImpl(final double x,
                                final double y,
                                final Node<? extends View<?>, Edge> node,
                                final Edge<? extends ViewConnector<?>, Node> inEdge,
                                final Connection sourceConnection,
                                final Connection targetConnection) {
        super(x,
              y);
        this.node = node;
        this.inEdge = inEdge;
        this.targetConnection = targetConnection;
        this.sourceConnection = sourceConnection;
    }

    @Override
    public Node<? extends View<?>, Edge> getNode() {
        return node;
    }

    @Override
    public Edge<? extends ViewConnector<?>, Node> getInEdge() {
        return inEdge;
    }

    public Connection getSourceConnection() {
        return sourceConnection;
    }

    public Connection getTargetConnection() {
        return targetConnection;
    }
}
