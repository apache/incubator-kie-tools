/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.util.SafeDeleteNodeProcessor;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Deletes a node taking into account its ingoing / outgoing edges and safe remove all node's children as well, if any.
 */
@Portable
public final class SafeDeleteNodeCommand extends AbstractGraphCompositeCommand {

    private static Logger LOGGER = Logger.getLogger(SafeDeleteNodeCommand.class.getName());

    @NonPortable
    public interface SafeDeleteNodeCommandCallback extends SafeDeleteNodeProcessor.Callback {

        void deleteEdge(final Edge<? extends View<?>, Node> edge);

        void setEdgeTargetNode(final Node<?, Edge> targetNode,
                               final Edge<? extends View<?>, Node> candidate);
    }

    private String candidateUUID;
    private transient Node<?, Edge> node;
    private transient Optional<SafeDeleteNodeCommandCallback> safeDeleteCallback;

    public SafeDeleteNodeCommand(final @MapsTo("candidateUUID") String candidateUUID) {
        this.candidateUUID = PortablePreconditions.checkNotNull("candidateUUID",
                                                                candidateUUID);
        this.safeDeleteCallback = Optional.empty();
    }

    public SafeDeleteNodeCommand(final Node<?, Edge> node) {
        this(node.getUUID());
        this.node = node;
    }

    public SafeDeleteNodeCommand(final Node<?, Edge> node,
                                 final SafeDeleteNodeCommandCallback safeDeleteCallback) {
        this(node);
        this.safeDeleteCallback = Optional.ofNullable(safeDeleteCallback);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SafeDeleteNodeCommand initialize(final GraphCommandExecutionContext context) {
        super.initialize(context);
        final Graph<?, Node> graph = getGraph(context);
        final Node<Definition<?>, Edge> candidate = (Node<Definition<?>, Edge>) getCandidate(context);
        new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                    graph,
                                    candidate)
                .run(new SafeDeleteNodeProcessor.Callback() {

                    @Override
                    public void deleteIncomingConnection(final Edge<? extends View<?>, Node> edge) {
                        log("IN SetConnectionTargetNodeCommand [edge=" + edge.getUUID() + "]");
                        addCommand(new SetConnectionTargetNodeCommand(null,
                                                                      edge));
                        safeDeleteCallback.ifPresent(c -> c.deleteIncomingConnection(edge));
                    }

                    @Override
                    public void deleteOutgoingConnection(final Edge<? extends View<?>, Node> edge) {
                        log("OUT SetConnectionSourceNodeCommand [edge=" + edge.getUUID() + "]");
                        addCommand(new SetConnectionSourceNodeCommand(null,
                                                                      edge));
                        safeDeleteCallback.ifPresent(c -> c.deleteOutgoingConnection(edge));
                    }

                    @Override
                    public void removeChild(final Element<?> parent,
                                            final Node<?, Edge> candidate) {
                        log("RemoveChildCommand [parent=" + parent.getUUID() +
                                    ", candidate=" + candidate.getUUID() + "]");
                        addCommand(new RemoveChildCommand((Node<?, Edge>) parent,
                                                          candidate));
                        safeDeleteCallback.ifPresent(c -> c.removeChild(parent,
                                                                        candidate));
                    }

                    @Override
                    public void removeDock(final Node<?, Edge> parent,
                                           final Node<?, Edge> candidate) {
                        log("UnDockNodeCommand [parent=" + parent.getUUID() +
                                    ", candidate=" + candidate.getUUID() + "]");
                        addCommand(new UnDockNodeCommand(parent,
                                                         candidate));
                        safeDeleteCallback.ifPresent(c -> c.removeDock(parent,
                                                                       candidate));
                    }

                    @Override
                    public void deleteNode(final Node<?, Edge> node) {
                        log("DeregisterNodeCommand [node=" + node.getUUID() + "]");
                        if (isTheCandidate(node)) {
                            processCandidateConnectorShortcut();
                        }
                        addCommand(new DeregisterNodeCommand(node));
                        safeDeleteCallback.ifPresent(c -> c.deleteNode(node));
                    }

                    private boolean isTheCandidate(final Node<?, Edge> node) {
                        return null != node && node.equals(candidate);
                    }

                    private void processCandidateConnectorShortcut() {
                        // Check if the in/out candidate connectors can be shortcut.
                        final long inConnectors = countViewConnectors(candidate.getInEdges());
                        final long outConnectors = countViewConnectors(candidate.getOutEdges());
                        final boolean isShortcut = 1 == inConnectors && 1 == outConnectors;
                        if (isShortcut) {
                            final Edge<? extends ViewConnector<?>, Node> in = getViewConnector(candidate.getInEdges());
                            final Edge<? extends ViewConnector<?>, Node> out = getViewConnector(candidate.getOutEdges());
                            shortcut(in,
                                     out);
                        }
                    }

                    private long countViewConnectors(final List<Edge> edgeList) {
                        return edgeList
                                .stream()
                                .filter(e -> e.getContent() instanceof ViewConnector)
                                .count();
                    }

                    private Edge getViewConnector(final List<Edge> edgeList) {
                        return edgeList
                                .stream()
                                .filter(e -> e.getContent() instanceof ViewConnector)
                                .findAny()
                                .get();
                    }

                    private void shortcut(final Edge<? extends ViewConnector<?>, Node> in,
                                          final Edge<? extends ViewConnector<?>, Node> out) {
                        final ViewConnector<?> outContent = out.getContent();
                        final Node targetNode = out.getTargetNode();
                        addCommand(new DeleteConnectorCommand(out));
                        safeDeleteCallback.ifPresent(c -> c.deleteEdge(out));
                        addCommand(new SetConnectionTargetNodeCommand(targetNode,
                                                                      in,
                                                                      outContent.getTargetMagnet().orElse(null),
                                                                      true));
                        safeDeleteCallback.ifPresent(c -> c.setEdgeTargetNode(targetNode,
                                                                              in));
                    }
                });

        return this;
    }

    @Override
    protected boolean delegateRulesContextToChildren() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> doAllow(final GraphCommandExecutionContext context,
                                                   final Command<GraphCommandExecutionContext, RuleViolation> command) {
        final CommandResult<RuleViolation> result = super.doAllow(context,
                                                                  command);
        if (!CommandUtils.isError(result) && hasRules(context)) {
            final Graph target = getGraph(context);
            final Node<View<?>, Edge> candidate = (Node<View<?>, Edge>) getCandidate(context);
            final GraphCommandResultBuilder builder = new GraphCommandResultBuilder();
            final Collection<RuleViolation> cardinalityRuleViolations =
                    doEvaluate(context,
                               RuleContextBuilder.GraphContexts.cardinality(target,
                                                                            Optional.of(candidate),
                                                                            Optional.of(CardinalityContext.Operation.DELETE)));
            builder.addViolations(cardinalityRuleViolations);
            for (final RuleViolation violation : cardinalityRuleViolations) {
                if (builder.isError(violation)) {
                    return builder.build();
                }
            }
            return builder.build();
        }
        return result;
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

    private boolean hasRules(final GraphCommandExecutionContext context) {
        return null != context.getRuleManager();
    }

    @Override
    public String toString() {
        return "SafeDeleteNodeCommand [candidate=" + candidateUUID + "]";
    }

    private void log(final String message) {
        LOGGER.log(Level.FINE,
                   message);
    }
}
