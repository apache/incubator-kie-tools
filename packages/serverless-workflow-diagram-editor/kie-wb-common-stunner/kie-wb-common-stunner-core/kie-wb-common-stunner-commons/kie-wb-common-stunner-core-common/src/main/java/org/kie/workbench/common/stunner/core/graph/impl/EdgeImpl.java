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

import jsinterop.annotations.JsType;
import org.kie.j2cl.tools.processors.annotations.GWT3Export;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;

@JsType
@GWT3Export
public class EdgeImpl<C> extends AbstractElement<C> implements Edge<C, Node> {

    private Node sourceNode;
    private Node targetNode;

    public EdgeImpl(final String uuid) {
        super(uuid);
    }

    @Override
    public Node getSourceNode() {
        return sourceNode;
    }

    @Override
    public Node getTargetNode() {
        return targetNode;
    }

    public void setSourceNode(final Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    public void setTargetNode(final Node targetNode) {
        this.targetNode = targetNode;
    }

    @Override
    public Node<C, Edge> asNode() {
        return null;
    }

    @Override
    public Edge<C, Node> asEdge() {
        return this;
    }
}
