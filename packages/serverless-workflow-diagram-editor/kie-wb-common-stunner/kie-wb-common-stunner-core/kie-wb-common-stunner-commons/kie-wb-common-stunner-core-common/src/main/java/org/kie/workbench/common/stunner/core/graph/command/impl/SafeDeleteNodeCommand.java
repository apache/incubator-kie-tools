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


package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.graph.util.SafeDeleteNodeProcessor;

/**
 * Deletes a node taking into account its ingoing / outgoing edges and safe remove all node's children as well, if any.
 */
@Portable
public class SafeDeleteNodeCommand extends AbstractGraphCompositeCommand {

    @Portable
    public static final class Options {

        private final boolean shortcutCandidateConnectors;
        private final Set<String> exclusions;

        public static Options defaults() {
            return new Options(true, new HashSet<>());
        }

        public static Options doNotShortcutConnectors() {
            return new Options(false, new HashSet<>());
        }

        public static Options exclude(final Set<String> ids) {
            return new Options(false, ids);
        }

        public Options(final @MapsTo("shortcutCandidateConnectors") boolean shortcutCandidateConnectors,
                       final @MapsTo("exclusions") Set<String> exclusions) {
            this.shortcutCandidateConnectors = shortcutCandidateConnectors;
            this.exclusions = exclusions;
        }

        public boolean isShortcutCandidateConnectors() {
            return shortcutCandidateConnectors;
        }

        public Set<String> getExclusions() {
            return exclusions;
        }
    }

    @NonPortable
    public interface SafeDeleteNodeCommandCallback extends SafeDeleteNodeProcessor.Callback {

        void setEdgeTargetNode(final Node<? extends View<?>, Edge> targetNode,
                               final Edge<? extends ViewConnector<?>, Node> candidate);
    }

    private final String candidateUUID;
    private final Options options;
    private transient Node<?, Edge> node;
    private transient Optional<SafeDeleteNodeCommandCallback> safeDeleteCallback;

    public SafeDeleteNodeCommand(final @MapsTo("candidateUUID") String candidateUUID,
                                 final @MapsTo("options") Options options) {
        this.candidateUUID = checkNotNull("candidateUUID", candidateUUID);
        this.options = checkNotNull("options", options);
        this.safeDeleteCallback = Optional.empty();
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public SafeDeleteNodeCommand(final Node<?, Edge> node) {
        this(node,
             Options.defaults());
        this.node = node;
    }

    public SafeDeleteNodeCommand(final Node<?, Edge> node,
                                 final Options options) {
        this(node.getUUID(),
             options);
        this.node = node;
    }

    public SafeDeleteNodeCommand(final Node<?, Edge> node,
                                 final SafeDeleteNodeCommandCallback safeDeleteCallback,
                                 final Options options) {
        this(node,
             options);
        this.safeDeleteCallback = Optional.ofNullable(safeDeleteCallback);
    }

    public Optional<SafeDeleteNodeCommandCallback> getSafeDeleteCallback() {
        return safeDeleteCallback;
    }

    public boolean shouldKeepChildren(final Node<Definition<?>, Edge> candidate) {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SafeDeleteNodeCommand initialize(final GraphCommandExecutionContext context) {
        super.initialize(context);
        final Graph<?, Node> graph = getGraph(context);
        final Node<Definition<?>, Edge> candidate = (Node<Definition<?>, Edge>) getCandidate(context);

        deleteNode(graph, candidate);

        return this;
    }

    private void deleteNode(final Graph<?, Node> graph,
                            final Node<Definition<?>, Edge> candidate) {
        new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                    graph,
                                    candidate,
                                    shouldKeepChildren(candidate),
                                    new TreeWalkTraverseProcessorImpl(),
                                    getGraphsProvider())
                .run(createDeleteNodeAndChildrenCallback(candidate));
    }

    public GraphsProvider getGraphsProvider() {
        return null; // not required
    }

    protected DeregisterNodeCommand createDeregisterNodeCommand(final Node node) {
        return new DeregisterNodeCommand(node);
    }

    private SafeDeleteNodeProcessor.Callback createDeleteNodeAndChildrenCallback(final Node<Definition<?>, Edge> candidate) {
        return new SafeDeleteNodeProcessor.Callback() {

            private final Set<String> processedConnectors = new HashSet<>();

            @Override
            public void deleteCandidateConnector(final Edge<? extends View<?>, Node> edge) {
                if (!processedConnectors.contains(edge.getUUID())) {
                    processCandidateConnectors();
                }
            }

            @Override
            public boolean deleteConnector(final Edge<? extends View<?>, Node> edge) {
                return doDeleteConnector(edge);
            }

            @Override
            public void removeChild(final Element<?> parent,
                                    final Node<?, Edge> candidate) {
                if (shouldKeepChildren((Node<Definition<?>, Edge>) candidate)) {
                    createChangeParentCommands(parent, candidate);
                }

                addCommand(createRemoveChildCommand(parent, candidate));
                safeDeleteCallback.ifPresent(c -> c.removeChild(parent,
                                                                candidate));
            }

            @Override
            public void removeDock(final Node<?, Edge> parent,
                                   final Node<?, Edge> candidate) {
                addCommand(new UnDockNodeCommand(parent,
                                                 candidate));
                safeDeleteCallback.ifPresent(c -> c.removeDock(parent,
                                                               candidate));
            }

            @Override
            public void deleteCandidateNode(final Node<?, Edge> node) {
                deleteNode(node);
            }

            @Override
            public boolean deleteNode(final Node<?, Edge> node) {
                if (!isElementExcluded(node)) {
                    addCommand(createDeregisterNodeCommand(node));
                    safeDeleteCallback.ifPresent(c -> c.deleteNode(node));
                    return true;
                }
                return false;
            }

            private void processCandidateConnectors() {
                if (options.isShortcutCandidateConnectors() &&
                        hasSingleIncomingEdge()
                                .and(hasSingleOutgoingEdge())
                                .test(candidate)) {
                    final Edge<? extends ViewConnector<?>, Node> in = getViewConnector().apply(candidate.getInEdges());
                    final Edge<? extends ViewConnector<?>, Node> out = getViewConnector().apply(candidate.getOutEdges());
                    shortcut(in,
                             out);
                } else {
                    Stream.concat(candidate.getInEdges().stream(),
                                  candidate.getOutEdges().stream())
                            .filter(e -> e.getContent() instanceof ViewConnector)
                            .forEach(this::deleteConnector);
                }
            }

            private void shortcut(final Edge<? extends ViewConnector<?>, Node> in,
                                  final Edge<? extends ViewConnector<?>, Node> out) {
                final ViewConnector<?> outContent = out.getContent();
                final Node targetNode = out.getTargetNode();
                addCommand(getDeleteConnectorCommand(out));
                safeDeleteCallback.ifPresent(c -> c.deleteCandidateConnector(out));
                addCommand(new SetConnectionTargetNodeCommand(targetNode,
                                                              in,
                                                              outContent.getTargetConnection().orElse(null)));
                safeDeleteCallback.ifPresent(c -> c.setEdgeTargetNode(targetNode,
                                                                      in));
                processedConnectors.add(in.getUUID());
                processedConnectors.add(out.getUUID());
            }

            private boolean doDeleteConnector(final Edge<? extends View<?>, Node> edge) {
                if (!isElementExcluded(edge) && !processedConnectors.contains(edge.getUUID())) {
                    addCommand(getDeleteConnectorCommand(edge));
                    safeDeleteCallback.ifPresent(c -> c.deleteConnector(edge));
                    processedConnectors.add(edge.getUUID());
                    return true;
                }
                return false;
            }
        };
    }

    protected DeleteConnectorCommand getDeleteConnectorCommand(final Edge<? extends View<?>, Node> edge) {
        return new DeleteConnectorCommand(edge);
    }

    void createChangeParentCommands(final Element<?> canvas,
                                    final Node<?, Edge> candidate) {
        final List<Node> childNodes = GraphUtils.getChildNodes(candidate);
        for (final Node n : childNodes) {
            safeDeleteCallback.ifPresent(c -> c.moveChildToCanvasRoot(canvas.asNode(), n));
        }
    }

    protected AbstractGraphCommand createRemoveChildCommand(final Element<?> parent,
                                                            final Node<?, Edge> candidate) {
        return new RemoveChildrenCommand((Node<?, Edge>) parent,
                                         candidate);
    }

    private boolean isElementExcluded(final Element<?> e) {
        return !options.getExclusions().isEmpty() && options.getExclusions().contains(e.getUUID());
    }

    @Override
    protected boolean delegateRulesContextToChildren() {
        return true;
    }

    @SuppressWarnings("unchecked")
    private Node<? extends Definition<?>, Edge> getCandidate(final GraphCommandExecutionContext context) {
        if (null == node) {
            node = checkNodeNotNull(context,
                                    candidateUUID);
        }
        return (Node<View<?>, Edge>) node;
    }

    public Node<?, Edge> getNode() {
        return node;
    }

    public Options getOptions() {
        return options;
    }

    private static Predicate<Node<Definition<?>, Edge>> hasSingleIncomingEdge() {
        return node -> 1 == countViewConnectors().apply(node.getInEdges());
    }

    private static Predicate<Node<Definition<?>, Edge>> hasSingleOutgoingEdge() {
        return node -> 1 == countViewConnectors().apply(node.getOutEdges());
    }

    private static Function<List<Edge>, Long> countViewConnectors() {
        return edges -> edges
                .stream()
                .filter(e -> e.getContent() instanceof ViewConnector)
                .count();
    }

    private static Function<List<Edge>, Edge> getViewConnector() {
        return edges -> edges
                .stream()
                .filter(e -> e.getContent() instanceof ViewConnector)
                .findAny()
                .get();
    }

    @Override
    public String toString() {
        return "SafeDeleteNodeCommand [candidate=" + candidateUUID + "]";
    }
}
