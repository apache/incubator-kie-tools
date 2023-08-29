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

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BadCommandArgumentsException;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.DirectGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommand;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public abstract class AbstractGraphCompositeCommand
        extends AbstractCompositeCommand<GraphCommandExecutionContext, RuleViolation>
        implements GraphCommand {

    /**
     * Each child command operation can be done by:
     * - Each child command check rules
     * - The composite command implementation will check the necessary rules in a single run, no need
     * to evaluate rules on each child command.
     */
    protected abstract boolean delegateRulesContextToChildren();

    @Override
    protected CommandResult<RuleViolation> doUndo(final GraphCommandExecutionContext context,
                                                  final Command<GraphCommandExecutionContext, RuleViolation> command) {
        return command.undo(delegateRulesContextToChildren() ? context : buildEmptyExecutionContext(context));
    }

    @Override
    protected CommandResult<RuleViolation> doExecute(final GraphCommandExecutionContext context,
                                                     final Command<GraphCommandExecutionContext, RuleViolation> command) {
        return command.execute(delegateRulesContextToChildren() ? context : buildEmptyExecutionContext(context));
    }

    @Override
    protected CommandResult<RuleViolation> doAllow(final GraphCommandExecutionContext context,
                                                   final Command<GraphCommandExecutionContext, RuleViolation> command) {
        return command.allow(context);
    }

    @SuppressWarnings("unchecked")
    protected MutableIndex<Node, Edge> getMutableIndex(final GraphCommandExecutionContext context) {
        return (MutableIndex<Node, Edge>) context.getGraphIndex();
    }

    @SuppressWarnings("unchecked")
    protected Graph<?, Node> getGraph(final GraphCommandExecutionContext context) {
        return (Graph<?, Node>) context.getGraphIndex().getGraph();
    }

    @SuppressWarnings("unchecked")
    protected Node<?, Edge> getNode(final GraphCommandExecutionContext context,
                                    final String uuid) {
        return context.getGraphIndex().getNode(uuid);
    }

    @SuppressWarnings("unchecked")
    protected Edge<? extends View, Node> getViewEdge(final GraphCommandExecutionContext context,
                                                     final String uuid) {
        return context.getGraphIndex().getEdge(uuid);
    }

    protected Node<?, Edge> checkNodeNotNull(final GraphCommandExecutionContext context,
                                             final String uuid) {
        final Node<?, Edge> e = getNode(context,
                                        uuid);
        if (null == e) {
            throw new BadCommandArgumentsException(this,
                                                   uuid,
                                                   "Node not found for [" + uuid + "].");
        }
        return e;
    }

    private DirectGraphCommandExecutionContext buildEmptyExecutionContext(final GraphCommandExecutionContext context) {
        return new DirectGraphCommandExecutionContext(context.getDefinitionManager(),
                                                      context.getFactoryManager(),
                                                      context.getGraphIndex());
    }
}
