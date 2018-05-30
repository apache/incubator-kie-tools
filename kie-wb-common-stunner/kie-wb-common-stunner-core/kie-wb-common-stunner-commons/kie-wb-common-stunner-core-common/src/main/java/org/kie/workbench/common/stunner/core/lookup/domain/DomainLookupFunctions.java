/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.lookup.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;

import static org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder.DomainContexts.cardinality;
import static org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder.DomainContexts.edgeCardinality;

public class DomainLookupFunctions {

    public interface DomainLookupFunction {

        Set<String> execute(DomainLookupContext context);
    }

    public static class LookupTargetRoles implements DomainLookupFunction {

        private final Node<? extends Definition<Object>, ? extends Edge> sourceNode;
        private final String edgeId;

        public LookupTargetRoles(final Node<? extends Definition<Object>, ? extends Edge> sourceNode,
                                 final String edgeId) {
            this.sourceNode = sourceNode;
            this.edgeId = edgeId;
        }

        @Override
        public Set<String> execute(final DomainLookupContext context) {
            final DomainLookupsCache cache = context.getCache();
            final int outEdgeCount = countOutgoingEdges(context,
                                                        sourceNode,
                                                        edgeId);
            final RuleViolations outEdgeCardinalityResult =
                    context.getRuleManager()
                            .evaluate(cache.getRuleSet(),
                                      edgeCardinality(sourceNode.getLabels(),
                                                      edgeId,
                                                      outEdgeCount,
                                                      EdgeCardinalityContext.Direction.OUTGOING,
                                                      Optional.of(CardinalityContext.Operation.ADD)));
            if (isValid(outEdgeCardinalityResult)) {
                final String defId =
                        context.getDefinitionManager().adapters().forDefinition().getId(sourceNode.getContent().getDefinition());
                return new LookupConnectionTargetRoles(edgeId, defId)
                        .execute(context);
            }
            return Collections.emptySet();
        }
    }

    public static class FilterConnectionTargetDefinitions implements DomainLookupFunction {

        private final String edgeId;
        private final Set<String> definitionIds;

        public FilterConnectionTargetDefinitions(final String edgeId,
                                                 final Set<String> definitionIds) {
            this.edgeId = edgeId;
            this.definitionIds = definitionIds;
        }

        @Override
        public Set<String> execute(final DomainLookupContext context) {
            final DomainLookupsCache cache = context.getCache();
            final Set<String> result = new LinkedHashSet<>();
            for (final String definitionId : definitionIds) {
                final Set<String> labels = context.getDefinitionsRegistry().getLabels(definitionId);
                final RuleViolations violations =
                        context.getRuleManager()
                                .evaluate(cache.getRuleSet(),
                                          RuleContextBuilder.DomainContexts.edgeCardinality(labels,
                                                                                            edgeId,
                                                                                            0,
                                                                                            EdgeCardinalityContext.Direction.INCOMING,
                                                                                            Optional.of(CardinalityContext.Operation.ADD)));
                if (isValid(violations)) {
                    result.add(definitionId);
                }
            }
            return result;
        }
    }

    public static class LookupTargetConnectors implements DomainLookupFunction {

        private final Node<? extends Definition<Object>, ? extends Edge> sourceNode;

        public LookupTargetConnectors(final Node<? extends Definition<Object>, ? extends Edge> sourceNode) {
            this.sourceNode = sourceNode;
        }

        @Override
        public Set<String> execute(final DomainLookupContext context) {
            final DomainLookupsCache cache = context.getCache();
            final Set<String> labels = sourceNode.getLabels();
            final Set<String> allowedTargetConnectors = context.getCache()
                    .getConnectionRules()
                    .stream()
                    .filter(rule -> isSourceConnectionAllowed(rule, labels))
                    .map(CanConnect::getRole)
                    .collect(Collectors.toSet());
            final Set<String> result = new LinkedHashSet<>();
            for (final String allowedConnector : allowedTargetConnectors) {
                final int outEdgeCount = countOutgoingEdges(context,
                                                            sourceNode,
                                                            allowedConnector);
                final RuleViolations outEdgeCardinalityResult =
                        context.getRuleManager()
                                .evaluate(cache.getRuleSet(),
                                          edgeCardinality(sourceNode.getLabels(),
                                                          allowedConnector,
                                                          outEdgeCount,
                                                          EdgeCardinalityContext.Direction.OUTGOING,
                                                          Optional.of(CardinalityContext.Operation.ADD)));
                if (isValid(outEdgeCardinalityResult)) {
                    result.add(allowedConnector);
                }
            }
            return result;
        }
    }

    public static class LookupConnectionTargetRoles implements DomainLookupFunction {

        private final String edgeDefId;
        private final String sourceNodeDefId;

        public LookupConnectionTargetRoles(final String edgeDefId,
                                           final String sourceNodeDefId) {
            this.edgeDefId = edgeDefId;
            this.sourceNodeDefId = sourceNodeDefId;
        }

        @Override
        public Set<String> execute(final DomainLookupContext context) {
            final Set<String> labels = context.getDefinitionsRegistry().getLabels(sourceNodeDefId);
            return context.getCache()
                    .getConnectionRules()
                    .stream()
                    .filter(rule -> rule.getRole().equals(edgeDefId))
                    .filter(rule -> isSourceConnectionAllowed(rule, labels))
                    .flatMap(rule -> rule.getPermittedConnections().stream()
                            .map(CanConnect.PermittedConnection::getEndRole))
                    .collect(Collectors.toSet());
        }
    }

    public static class LookupAllowedDefinitionsByLabels implements DomainLookupFunction {

        private final Graph<?, ? extends Node> graph;
        private final Set<String> labels;

        public LookupAllowedDefinitionsByLabels(final Graph<?, ? extends Node> graph,
                                                final Set<String> labels) {
            this.graph = graph;
            this.labels = labels;
        }

        @Override
        public Set<String> execute(final DomainLookupContext context) {
            final Set<String> ids = new LookupDefinitionsByLabels(labels).execute(context);
            final Map<String, Integer> graphLabelCount = GraphUtils.getLabelsCount(graph,
                                                                                   labels);
            final Set<String> result = new LinkedHashSet<>();
            for (final String defId : ids) {
                final Set<String> defLabels = context.getDefinitionsRegistry().getLabels(defId);
                for (final String label : defLabels) {
                    final Integer roleCount = Optional.ofNullable(graphLabelCount.get(label)).orElse(0);
                    final RuleViolations violations =
                            context.getRuleManager()
                                    .evaluate(context.getCache().getRuleSet(),
                                              cardinality(Collections.singleton(label),
                                                          roleCount,
                                                          Optional.of(CardinalityContext.Operation.ADD)));
                    if (isValid(violations)) {
                        result.add(defId);
                    }
                }
            }
            return result;
        }
    }

    public static class LookupDefinitionsByLabels implements DomainLookupFunction {

        private final Set<String> labels;

        public LookupDefinitionsByLabels(final Set<String> labels) {
            this.labels = labels;
        }

        @Override
        public Set<String> execute(final DomainLookupContext context) {
            final DomainLookupsCache cache = context.getCache();
            return labels.stream()
                    .flatMap(label -> cache.getDefinitions(label).stream())
                    .collect(Collectors.toSet());
        }
    }

    public static boolean isSourceConnectionAllowed(final CanConnect rule,
                                                    final Set<String> labels) {
        return rule.getPermittedConnections().stream()
                .anyMatch(pc -> labels.contains(pc.getStartRole()));
    }

    private static <T> int countOutgoingEdges(final DomainLookupContext context,
                                              final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                              final String edgeId) {
        final List<? extends Edge> edges = sourceNode.getOutEdges();
        return GraphUtils.countEdges(context.getDefinitionManager(),
                                     edgeId,
                                     edges);
    }

    private static boolean isValid(final RuleViolations violations) {
        return null == violations || !violations.violations(RuleViolation.Type.ERROR).iterator().hasNext();
    }
}
