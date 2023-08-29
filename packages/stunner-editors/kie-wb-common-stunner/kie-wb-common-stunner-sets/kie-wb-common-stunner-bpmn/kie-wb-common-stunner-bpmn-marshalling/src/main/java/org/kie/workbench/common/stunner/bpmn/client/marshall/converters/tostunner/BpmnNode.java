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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.BasePropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * The result of a Converter (to Stunner) is always a BpmnNode.
 * It wraps the underlying Stunner node into a data structure
 * that also encodes
 * <p>
 * 1) parent/child relationships
 * 2) other edges (and therefore, implicitly, other nodes)
 * that may be contained inside the node (e.g. in the case of a (Sub)Process)
 */
public abstract class BpmnNode {

    private final Node<? extends View<? extends BPMNViewDefinition>, ?> value;
    private final List<BpmnNode> children = new ArrayList<>();
    private List<BpmnEdge> edges = new ArrayList<>();
    private BpmnNode parent;
    private BasePropertyReader propertyReader;

    protected BpmnNode(Node<? extends View<? extends BPMNViewDefinition>, ?> value, BasePropertyReader propertyReader) {
        this.value = value;
        this.propertyReader = propertyReader;
    }

    public abstract boolean isDocked();

    public static class Simple extends BpmnNode {

        public Simple(Node<? extends View<? extends BPMNViewDefinition>, ?> value, BasePropertyReader propertyReader) {
            super(value, propertyReader);
        }

        @Override
        public boolean isDocked() {
            return false;
        }
    }

    public static class Docked extends BpmnNode {

        public Docked(Node<? extends View<? extends BPMNViewDefinition>, ?> value, BasePropertyReader propertyReader) {
            super(value, propertyReader);
        }

        @Override
        public boolean isDocked() {
            return true;
        }
    }

    public static BpmnNode of(Node<? extends View<? extends BPMNViewDefinition>, ?> value, BasePropertyReader propertyReader) {
        return new BpmnNode.Simple(value, propertyReader);
    }

    public BpmnNode docked() {
        return new BpmnNode.Docked(this.value, this.propertyReader);
    }

    public BpmnNode getParent() {
        return parent;
    }

    public void setParent(BpmnNode parent) {
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        this.parent = parent;
        parent.addChild(this);
    }

    public void addChild(BpmnNode child) {
        this.children.add(child);
    }

    public void removeChild(BpmnNode child) {
        this.children.remove(child);
    }

    public List<BpmnNode> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> value() {
        return value;
    }

    public BasePropertyReader getPropertyReader() {
        return propertyReader;
    }

    public void addAllEdges(Collection<BpmnEdge> bpmnEdges) {
        this.edges.addAll(bpmnEdges);
    }

    public List<BpmnEdge> getEdges() {
        return edges;
    }

    public boolean addEdge(BpmnEdge bpmnEdge) {
        return edges.add(bpmnEdge);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BpmnNode{");
        sb.append("value=").append(Optional.ofNullable(value)
                                           .map(Element::getContent)
                                           .filter(Objects::nonNull)
                                           .map(View::getDefinition)
                                           .filter(Objects::nonNull)
                                           .map(BPMNViewDefinition::toString)
                                           .orElse(""));
        sb.append('}');
        return sb.toString();
    }
}
