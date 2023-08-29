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


package org.kie.workbench.common.stunner.core.rule.ext.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.ParentTypesMatcher;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.handler.impl.GraphEvaluationHandlerUtils;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

/**
 * A rule handler that checks if both source and target nodes for a given connector
 * have the same parent instance for a given type.
 * <p>
 * This handler applies for graph connection contexts.
 * <p>
 * The RuleExtension instance used by this handler needs the following arguments:
 * - RuleExtension#getTypeArguments()[0] - The parent type
 * - RuleExtension#getArguments()[0] - The rule violation's message
 * <p>
 * Example:
 * <code>
 * @RuleExtension( handler = ConnectorParentsMatchConnectionHandler.class,
 * typeArguments = {TheParentType.class}
 * arguments = {"My violation's message"}
 * )
 * public class MyConnectorBean {
 * }
 * </code>
 */
@ApplicationScoped
public class ConnectorParentsMatchConnectionHandler
        extends AbstractParentsMatchHandler<ConnectorParentsMatchConnectionHandler, GraphConnectionContext> {

    private final DefinitionManager definitionManager;
    private final GraphEvaluationHandlerUtils evalUtils;

    protected ConnectorParentsMatchConnectionHandler() {
        this(null);
    }

    @Inject
    public ConnectorParentsMatchConnectionHandler(final DefinitionManager definitionManager) {
        this.definitionManager = definitionManager;
        this.evalUtils = new GraphEvaluationHandlerUtils(definitionManager);
    }

    @Override
    public Class<ConnectorParentsMatchConnectionHandler> getExtensionType() {
        return ConnectorParentsMatchConnectionHandler.class;
    }

    @Override
    public Class<GraphConnectionContext> getContextType() {
        return GraphConnectionContext.class;
    }

    @Override
    public boolean accepts(final RuleExtension rule,
                           final GraphConnectionContext context) {
        return acceptsConnection(rule,
                                 context);
    }

    @Override
    public RuleViolations evaluate(final RuleExtension rule,
                                   final GraphConnectionContext context) {
        return evaluateConnection(rule,
                                  context);
    }

    private boolean acceptsConnection(final RuleExtension rule,
                                      final GraphConnectionContext context) {
        final Edge<? extends View<?>, ? extends Node> connector = context.getConnector();
        return evalUtils.getElementDefinitionId(connector).equals(rule.getId());
    }

    @SuppressWarnings("unchecked")
    private RuleViolations evaluateConnection(final RuleExtension rule,
                                              final GraphConnectionContext context) {
        final Edge connector = context.getConnector();
        final GraphEvaluationState.ConnectionState connectionState = context.getState().getConnectionState();
        final Node<? extends View<?>, ? extends Edge> sourceNode =
                (Node<? extends View<?>, ? extends Edge>) context.getSource()
                        .orElse(connectionState.getSource(connector));
        final Node<? extends View<?>, ? extends Edge> targetNode =
                (Node<? extends View<?>, ? extends Edge>) context.getTarget()
                        .orElse(connectionState.getTarget(connector));
        final DefaultRuleViolations result = new DefaultRuleViolations();
        final GraphEvaluationState.ContainmentState containmentState = context.getState().getContainmentState();
        final boolean isValid = new ParentTypesMatcher(() -> definitionManager,
                                                       containmentState::getParent,
                                                       rule.getTypeArguments())
                .matcher()
                .test(sourceNode,
                      targetNode);
        if (!isValid) {
            addViolation(context.getConnector().getUUID(),
                         rule,
                         result);
        }
        return result;
    }
}
