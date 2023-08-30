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

package org.kie.workbench.common.stunner.sw.client.shapes;

import java.util.List;
import java.util.Set;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.sw.definition.State;

public class StateNode implements Node<View<State>, Edge> {

    private View<State> view;

    public StateNode(View<State> view) {
        this.view = view;
    }

    @Override
    public List<Edge> getInEdges() {
        return null;
    }

    @Override
    public List<Edge> getOutEdges() {
        return null;
    }

    @Override
    public String getUUID() {
        return null;
    }

    @Override
    public Set<String> getLabels() {
        return null;
    }

    @Override
    public View<State> getContent() {
        return view;
    }

    @Override
    public void setContent(View<State> content) {
        this.view = content;
    }

    @Override
    public Node<View<State>, Edge> asNode() {
        return null;
    }

    @Override
    public Edge<View<State>, Node> asEdge() {
        return null;
    }
}
