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


package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;

import static org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand.toUUID;
import static org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand.toUUIDs;

public class UpdateChildrenCommand extends AbstractCanvasCompositeCommand {

    private final Node parent;
    private final Collection<Node> candidates;

    public UpdateChildrenCommand(final Node parent,
                                 final Collection<Node> candidates) {
        this.parent = parent;
        this.candidates = candidates;
    }

    public UpdateChildrenCommand(final Node parent,
                                 final Node candidate) {
        this.parent = parent;
        this.candidates = Collections.singleton(candidate);
    }

    @SuppressWarnings("unchecked")
    private void processCandidate(final Context context,
                                  final Node candidate) {
        final List<Edge<?, Node<?, Edge>>> candidateInEdges = candidate.getInEdges();
        final List<Edge<?, Node<?, Edge>>> candidateOutEdges = candidate.getOutEdges();
        final Optional<Edge<?, Node<?, Edge>>> currentParentEdge = candidateInEdges.stream()
                .filter(isDifferentParent())
                .findAny();
        final Optional<Edge<?, Node<?, Edge>>> currentDockEdge = candidateInEdges.stream()
                .filter(e -> e.getContent() instanceof Dock)
                .findAny();
        final Set<Edge> dockedParentEdges =
                candidateOutEdges.stream()
                        .filter(e -> e.getContent() instanceof Dock)
                        .map(Edge::getTargetNode)
                        .flatMap(node -> node.getInEdges().stream())
                        .filter(isDifferentParent())
                        .collect(Collectors.toSet());

        // If candidate is docked, un-dock it.
        currentDockEdge.ifPresent(e -> context.undock(e.getSourceNode(), candidate));

        // Remove current parent for candidate, if any.
        currentParentEdge.ifPresent(e -> context.removeChild(e.getSourceNode(), candidate));

        // Set new parent for the candidate, if necessary.
        currentParentEdge.ifPresent(e -> context.addChild(parent, candidate));

        // Remove current parent for nodes docked to the candidate and Undock them
        // Set new parent for the nodes docked on candidate and Dock them
        dockedParentEdges.forEach(e -> {
            context.removeChild(e.getSourceNode(), e.getTargetNode());
            context.undock(candidate, e.getTargetNode());
            context.addChild(parent, e.getTargetNode());
            context.dock(candidate, e.getTargetNode());
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AbstractCompositeCommand<AbstractCanvasHandler, CanvasViolation> initialize(final AbstractCanvasHandler canvasHandler) {
        super.initialize(canvasHandler);
        final Context context = new Context();
        candidates.forEach(candidate -> processCandidate(context,
                                                         candidate));
        consumeDockingMap(canvasHandler, context.undocked, (parent, child) -> addCommand(new UnDockNodeCommand(parent, child)));
        consumeChildrenMap(canvasHandler, context.removedChildren, (parent, children) -> addCommand(new RemoveChildrenCommand(parent, children)));
        consumeChildrenMap(canvasHandler, context.addedChildren, (parent, children) -> addCommand(new SetChildrenCommand(parent, children)));
        consumeDockingMap(canvasHandler, context.docked, (parent, child) -> addCommand(new DockNodeCommand(parent, child)));
        return this;
    }

    private Predicate<Edge> isDifferentParent() {
        return e -> e.getContent() instanceof Child &&
                !parent.equals(e.getSourceNode());
    }

    public Node getParent() {
        return parent;
    }

    public Collection<Node> getCandidates() {
        return candidates;
    }

    private static class Context {

        private final Map<String, Collection<String>> addedChildren;
        private final Map<String, Collection<String>> removedChildren;
        private final Map<String, String> docked;
        private final Map<String, String> undocked;

        private Context() {
            this.addedChildren = new HashMap<>();
            this.removedChildren = new HashMap<>();
            this.docked = new HashMap<>();
            this.undocked = new HashMap<>();
        }

        private void addChild(Element parent, Element child) {
            updateChildren(addedChildren, parent, child);
        }

        private void removeChild(Element parent, Element child) {
            updateChildren(removedChildren, parent, child);
        }

        private void dock(Element parent, Element child) {
            docked.put(child.getUUID(), parent.getUUID());
        }

        private void undock(Element parent, Element child) {
            undocked.put(child.getUUID(), parent.getUUID());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [parent=" + toUUID(parent) + "," +
                "candidates=" + toUUIDs(candidates) + "]";
    }

    private static void consumeChildrenMap(final AbstractCanvasHandler canvasHandler,
                                           final Map<String, Collection<String>> map,
                                           final BiConsumer<Node, Collection<Node<?, Edge>>> consumer) {
        map.entrySet().forEach(entry -> consumer.accept(toNode(canvasHandler, entry.getKey()),
                                                        toNodes(canvasHandler, entry.getValue())));
    }

    private static void consumeDockingMap(final AbstractCanvasHandler canvasHandler,
                                          final Map<String, String> map,
                                          final BiConsumer<Node, Node> consumer) {
        map.entrySet().forEach(entry -> consumer.accept(toNode(canvasHandler, entry.getValue()),
                                                        toNode(canvasHandler, entry.getKey())));
    }

    private static void updateChildren(final Map<String, Collection<String>> map,
                                       final Element parent,
                                       final Element child) {
        final String parentUUID = parent.getUUID();
        final String childUUID = child.getUUID();
        Collection<String> children = map.get(parentUUID);
        if (null == children) {
            children = new ArrayList<>();
            map.put(parentUUID, children);
        }
        children.add(childUUID);
    }

    private static Node toNode(final AbstractCanvasHandler canvasHandler,
                               final String id) {
        return canvasHandler.getGraphIndex().getNode(id);
    }

    @SuppressWarnings("unchecked")
    private static Collection<Node<?, Edge>> toNodes(final AbstractCanvasHandler canvasHandler,
                                                     final Collection<String> uuids) {
        List collected = uuids.stream()
                .map(uuid -> toNode(canvasHandler, uuid))
                .collect(Collectors.toList());
        return collected;
    }
}
