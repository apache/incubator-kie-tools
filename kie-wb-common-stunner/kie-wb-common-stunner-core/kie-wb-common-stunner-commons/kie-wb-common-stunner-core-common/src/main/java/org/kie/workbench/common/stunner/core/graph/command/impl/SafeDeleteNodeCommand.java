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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
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

    private String candidateUUID;
    private transient Node<?, Edge> node;
    private transient SafeDeleteNodeProcessor.Callback safeDeleteCallback;

    public SafeDeleteNodeCommand(final @MapsTo("candidateUUID") String candidateUUID) {
        this.candidateUUID = PortablePreconditions.checkNotNull("candidateUUID",
                                                                candidateUUID);
        this.safeDeleteCallback = null;
    }

    public SafeDeleteNodeCommand(final Node<?, Edge> node) {
        this(node.getUUID());
        this.node = node;
    }

    public SafeDeleteNodeCommand(final Node<?, Edge> node,
                                 final SafeDeleteNodeProcessor.Callback safeDeleteCallback) {
        this(node);
        this.safeDeleteCallback = safeDeleteCallback;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SafeDeleteNodeCommand initialize(final GraphCommandExecutionContext context) {
        super.initialize(context);
        final Node<Definition<?>, Edge> candidate = (Node<Definition<?>, Edge>) getCandidate(context);
        // Delete & set incoming & outgoing edges for the node being deleted.
        final List<Command<GraphCommandExecutionContext, RuleViolation>> commands = new LinkedList<>();
        new SafeDeleteNodeProcessor(candidate).run(new SafeDeleteNodeProcessor.Callback() {

            @Override
            public void deleteChildNode(final Node<Definition<?>, Edge> node) {
                commands.add(new SafeDeleteNodeCommand(node,
                                                       safeDeleteCallback).initialize(context));
                if (null != safeDeleteCallback) {
                    safeDeleteCallback.deleteChildNode(node);
                }
            }

            @Override
            public void deleteInViewEdge(final Edge<View<?>, Node> edge) {
                commands.add(new SetConnectionTargetNodeCommand(null,
                                                                edge));
                if (null != safeDeleteCallback) {
                    safeDeleteCallback.deleteInViewEdge(edge);
                }
            }

            @Override
            public void deleteInChildEdge(final Edge<Child, Node> edge) {
                final Node parent = edge.getSourceNode();
                commands.add(new RemoveChildCommand(parent,
                                                    candidate));
                if (null != safeDeleteCallback) {
                    safeDeleteCallback.deleteInChildEdge(edge);
                }
            }

            @Override
            public void deleteInDockEdge(final Edge<Dock, Node> edge) {
                final Node parent = edge.getSourceNode();
                commands.add(new UnDockNodeCommand(parent,
                                                   candidate));
                if (null != safeDeleteCallback) {
                    safeDeleteCallback.deleteInDockEdge(edge);
                }
            }

            @Override
            public void deleteOutViewEdge(final Edge<? extends View<?>, Node> edge) {
                commands.add(new SetConnectionSourceNodeCommand(null,
                                                                edge));
                if (null != safeDeleteCallback) {
                    safeDeleteCallback.deleteOutViewEdge(edge);
                }
            }

            @Override
            public void deleteNode(final Node<Definition<?>, Edge> node) {
                commands.add(new DeregisterNodeCommand(node));
                if (null != safeDeleteCallback) {
                    safeDeleteCallback.deleteNode(node);
                }
            }
        });
        // Add the commands above as composited.
        for (Command<GraphCommandExecutionContext, RuleViolation> command : commands) {
            SafeDeleteNodeCommand.this.addCommand(command);
        }
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

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        return super.undo(context,
                          true);
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
}
