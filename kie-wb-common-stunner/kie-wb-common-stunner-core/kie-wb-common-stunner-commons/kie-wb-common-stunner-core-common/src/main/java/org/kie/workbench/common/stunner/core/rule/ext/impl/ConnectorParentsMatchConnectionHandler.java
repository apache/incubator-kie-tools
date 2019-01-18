/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.ext.impl;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.graph.util.ParentsTypeMatcher;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
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

    private static Logger LOGGER = Logger.getLogger(ConnectorParentsMatchConnectionHandler.class.getName());

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

    private RuleViolations evaluateConnection(final RuleExtension rule,
                                              final GraphConnectionContext context) {
        LOGGER.log(Level.INFO,
                   "Evaluating rule handler [" + getClass().getName() + "]...");
        final Optional<Node<? extends View<?>, ? extends Edge>> sourceNode = context.getSource();
        final Optional<Node<? extends View<?>, ? extends Edge>> targetNode = context.getTarget();

        final DefaultRuleViolations result = new DefaultRuleViolations();
        boolean isValid = true;
        if (sourceNode.isPresent() && targetNode.isPresent()) {
            final Node<? extends View<?>, ? extends Edge> source = sourceNode.get();
            final Node<? extends View<?>, ? extends Edge> target = targetNode.get();
            final Element<? extends Definition> parentTarget = (Element<? extends Definition>) GraphUtils.getParent(target);
            final Element<? extends Definition> parentSource = (Element<? extends Definition>) GraphUtils.getParent(source);
            final Optional<Class<?>> parentType = getParentType(rule, parentTarget, parentSource);

            isValid = new ParentsTypeMatcher(definitionManager, parentType.orElse(null))
                    .test(source, target);
        }
        if (!isValid) {
            addViolation(context.getConnector().getUUID(),
                         rule,
                         result);
        }
        return result;
    }
}
