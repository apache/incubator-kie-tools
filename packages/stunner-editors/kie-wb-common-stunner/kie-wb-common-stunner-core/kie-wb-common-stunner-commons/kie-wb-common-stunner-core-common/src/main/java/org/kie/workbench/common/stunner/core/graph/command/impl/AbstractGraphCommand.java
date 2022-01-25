/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collection;
import java.util.function.Function;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BadCommandArgumentsException;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.AbstractGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommand;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;

public abstract class AbstractGraphCommand implements GraphCommand {

    protected abstract CommandResult<RuleViolation> check(final GraphCommandExecutionContext context);

    @Override
    public CommandResult<RuleViolation> allow(final GraphCommandExecutionContext context) {
        return check(context);
    }

    protected Collection<RuleViolation> evaluate(final GraphCommandExecutionContext context,
                                                 final Function<RuleEvaluationContextBuilder.GraphContextBuilder, GraphEvaluationContext> contextBuilder) {
        return (Collection<RuleViolation>) ((AbstractGraphCommandExecutionContext) context).evaluate(contextBuilder).violations();
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

    @SuppressWarnings("unchecked")
    protected <C> Node<C, Edge> getNodeNotNull(final GraphCommandExecutionContext context,
                                               final String uuid) {
        final Node<?, Edge> node = getNode(context,
                                           uuid);
        if (null == node) {
            throw new BadCommandArgumentsException(this,
                                                   uuid,
                                                   "Node not found for [" + uuid + "].");
        }
        return (Node<C, Edge>) node;
    }

    @SuppressWarnings("unchecked")
    protected <C> Element<C> getElementNotNull(final GraphCommandExecutionContext context,
                                               final String uuid) {
        final Element element = context.getGraphIndex().get(uuid);
        if (null == element) {
            throw new BadCommandArgumentsException(this,
                                                   uuid,
                                                   "Element not found for [" + uuid + "].");
        }
        return (Element<C>) element;
    }
}