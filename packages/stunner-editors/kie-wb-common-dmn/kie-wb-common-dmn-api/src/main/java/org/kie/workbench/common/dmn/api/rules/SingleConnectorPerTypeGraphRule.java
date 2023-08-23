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
package org.kie.workbench.common.dmn.api.rules;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.RuleViolationImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class SingleConnectorPerTypeGraphRule extends RuleExtensionHandler<SingleConnectorPerTypeGraphRule, GraphConnectionContext> {

    static final String ERROR_MESSAGE = "Connection would violate single connection per type requirement.";

    @Override
    public Class<SingleConnectorPerTypeGraphRule> getExtensionType() {
        return SingleConnectorPerTypeGraphRule.class;
    }

    @Override
    public Class<GraphConnectionContext> getContextType() {
        return GraphConnectionContext.class;
    }

    @Override
    public boolean accepts(final RuleExtension rule,
                           final GraphConnectionContext context) {
        final Object o = DefinitionUtils.getElementDefinition(context.getConnector());
        final Class<?> type = rule.getTypeArguments()[0];
        return o.getClass().equals(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RuleViolations evaluate(final RuleExtension rule,
                                   final GraphConnectionContext context) {
        final Optional<Node<? extends View<?>, ? extends Edge>> oSource = context.getSource();
        final Optional<Node<? extends View<?>, ? extends Edge>> oTarget = context.getTarget();
        final DefaultRuleViolations result = new DefaultRuleViolations();

        //Only validate when source and target nodes are set
        if (!(oSource.isPresent() && oTarget.isPresent())) {
            return result;
        }

        final Node<? extends View<?>, ? extends Edge> source = oSource.get();
        final Node<? extends View<?>, ? extends Edge> target = oTarget.get();
        final Edge<? extends View<?>, ? extends Node> connector = context.getConnector();

        if (isConnectionAlreadyFormed(source,
                                      target,
                                      connector)) {
            result.addViolation(new RuleViolationImpl(ERROR_MESSAGE));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    protected boolean isConnectionAlreadyFormed(final Node<? extends View<?>, ? extends Edge> source,
                                                final Node<? extends View<?>, ? extends Edge> target,
                                                final Edge<? extends View<?>, ? extends Node> connector) {
        final Class<?> newConnectorClass = connector
                .getContent()
                .getDefinition()
                .getClass();

        //Check for existing outgoing connections of the same type
        return source.getOutEdges()
                .stream()
                .filter(e -> !Objects.equals(e,
                                             connector))
                .flatMap(e -> {
                    final Object content = e.getContent();
                    if (content instanceof Definition) {
                        final Class<?> existingConnectorClass = ((Definition) content).getDefinition().getClass();
                        if (newConnectorClass.equals(existingConnectorClass)) {
                            return Stream.of(e);
                        }
                    }
                    return Stream.empty();
                })
                .distinct()
                .map(Edge::getTargetNode)
                .anyMatch(n -> Objects.equals(n,
                                              target));
    }
}
