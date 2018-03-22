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

package org.kie.workbench.common.stunner.core.lookup.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupManager;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionRepresentation;
import org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupManager;
import org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupRequest;
import org.kie.workbench.common.stunner.core.rule.Rule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * An utils class that provides common used look-ups and other logic for querying the domain model and the rules model,
 * that is used along the application.
 * <p>
 * // TODO: Some kind of cache to avoid frequently used lookups? Consider performance and memory, this class
 * is shared on both server and client sides.
 */
@ApplicationScoped
public class CommonLookups {

    private static Logger LOGGER = Logger.getLogger(CommonLookups.class.getName());

    private final DefinitionUtils definitionUtils;
    private final DefinitionLookupManager definitionLookupManager;
    private final RuleManager ruleManager;
    private final RuleLookupManager ruleLookupManager;
    private final FactoryManager factoryManager;

    protected CommonLookups() {
        this(null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public CommonLookups(final DefinitionUtils definitionUtils,
                         final RuleManager ruleManager,
                         final DefinitionLookupManager definitionLookupManager,
                         final RuleLookupManager ruleLookupManager,
                         final FactoryManager factoryManager) {
        this.definitionUtils = definitionUtils;
        this.ruleManager = ruleManager;
        this.definitionLookupManager = definitionLookupManager;
        this.ruleLookupManager = ruleLookupManager;
        this.factoryManager = factoryManager;
    }

    /**
     * Returns the allowed edge definition identifiers that can be added as outgoing edges for the given source node.
     */
    public <T> Set<String> getAllowedConnectors(final String defSetId,
                                                final Node<? extends Definition<T>, Edge> sourceNode,
                                                final int page,
                                                final int pageSize) {
        final Set<String> result = new LinkedHashSet<>();
        if (null != defSetId && null != sourceNode) {
            final T definition = sourceNode.getContent().getDefinition();
            final Set<String> connectionAllowedEdges = getConnectionRulesAllowedEdges(defSetId,
                                                                                      definition,
                                                                                      page,
                                                                                      pageSize);
            if (null != connectionAllowedEdges && !connectionAllowedEdges.isEmpty()) {
                final RuleSet ruleSet = getRuleSet(defSetId);
                connectionAllowedEdges.stream().forEach(allowedEdgeId -> {
                    final int edgeCount = countOutgoingEdges(sourceNode,
                                                             allowedEdgeId);
                    final boolean oeCardinalityAllowed = getDefinitionLabels(definition).stream()
                            .filter(role -> pass(ruleManager.evaluate(ruleSet,
                                                                      RuleContextBuilder.DomainContexts.edgeCardinality(sourceNode.getLabels(),
                                                                                                                        allowedEdgeId,
                                                                                                                        edgeCount,
                                                                                                                        EdgeCardinalityContext.Direction.OUTGOING,
                                                                                                                        Optional.of(CardinalityContext.Operation.ADD)))))
                            .findAny()
                            .isPresent();
                    log(Level.FINEST,
                        "Outgoing edge cardinality rules evaluation - Result = [" + oeCardinalityAllowed + "]");
                    if (oeCardinalityAllowed) {
                        result.add(allowedEdgeId);
                    }
                });
            }
        }
        return result;
    }

    private RuleSet getRuleSet(final String defSetId) {
        checkNotNull("defSetId",
                     defSetId);
        final Object definitionSet = getDefinitionManager().definitionSets().getDefinitionSetById(defSetId);
        return getDefinitionManager()
                .adapters()
                .registry()
                .getDefinitionSetRuleAdapter(definitionSet.getClass())
                .getRuleSet(definitionSet);
    }

    /**
     * Returns the allowed definition identifiers that can be used as target node for the given source node and
     * the given edge (connector) identifier.
     * This method only returns the definition identifiers that are considered the default types for its morph type,
     * it does NOT return all the identifiers for all the allowed target definitions.
     * <p>
     * TODO: Handle several result pages.
     */
    public <T> Set<String> getAllowedMorphDefaultDefinitions(final String defSetId,
                                                             final Graph<?, ? extends Node> graph,
                                                             final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                                             final String edgeId,
                                                             final int page,
                                                             final int pageSize) {
        final Set<Object> allowedDefinitions = getAllowedTargetDefinitions(defSetId,
                                                                           graph,
                                                                           sourceNode,
                                                                           edgeId,
                                                                           page,
                                                                           pageSize);
        log(Level.FINEST,
            "Target definitions allowed " +
                    "for [" + sourceNode + "] and using the " +
                    "connector [" + edgeId + "] " +
                    "ARE [" + allowedDefinitions + "]");
        final Set<String> result = new LinkedHashSet<>();
        allowedDefinitions.stream().forEach(definition -> {
            final String defId = getDefinitionManager().adapters().forDefinition().getId(definition);
            final MorphDefinition morphDefinition = definitionUtils.getMorphDefinition(definition);
            final boolean hasMorphBase = null != morphDefinition;
            final String id = hasMorphBase ? morphDefinition.getDefault() : defId;
            result.add(id);
        });
        log(Level.FINEST,
            "Target definitions group by morph base type allowed " +
                    "for [" + sourceNode + "] and using the " +
                    "connector [" + edgeId + "] " +
                    "ARE [" + result + "]");
        return result;
    }

    /**
     * Returns the allowed definition identifiers that can be used as target node for the given source node and
     * the given edge (connector) identifier.
     * <p>
     * TODO: Handle several result pages.
     */
    @SuppressWarnings("unchecked")
    public <T> Set<Object> getAllowedTargetDefinitions(final String defSetId,
                                                       final Graph<?, ? extends Node> graph,
                                                       final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                                       final String edgeId,
                                                       final int page,
                                                       final int pageSize) {
        final Set<Object> result = new LinkedHashSet<>();

        if (null != defSetId && null != graph && null != sourceNode && null != edgeId) {
            final T definition = sourceNode.getContent().getDefinition();
            final RuleSet ruleSet = getRuleSet(defSetId);
            log(Level.FINEST,
                "*** Checking the target definitions allowed " +
                        "for [" + definition + "] and using the " +
                        "connector [" + edgeId + "] ***");
            // Check outgoing connectors cardinality for the source node ( plus the new one to be added ).
            final int outConnectorsCount = countOutgoingEdges(sourceNode,
                                                              edgeId);
            log(Level.FINEST,
                "The source node has  " + outConnectorsCount + "] outgoing connections.");
            final RuleViolations oev =
                    ruleManager.evaluate(ruleSet,
                                         RuleContextBuilder.DomainContexts.edgeCardinality(sourceNode.getLabels(),
                                                                                           edgeId,
                                                                                           outConnectorsCount,
                                                                                           EdgeCardinalityContext.Direction.OUTGOING,
                                                                                           Optional.of(CardinalityContext.Operation.ADD)));
            final boolean oeCardinalityAllowed = pass(oev);
            log(Level.FINEST,
                "Outgoing edge cardinality rules evaluation " +
                        "result = [" + oeCardinalityAllowed + "]");
            if (oeCardinalityAllowed) {
                // Obtain allowed target roles that pass connection rules.
                final Set<String> allowedConnectionRoles = getConnectionRulesAllowedTargets(defSetId,
                                                                                            definition,
                                                                                            edgeId,
                                                                                            page,
                                                                                            pageSize);
                log(Level.FINEST,
                    "Allowed target roles that pass connection rules " +
                            "ARE [" + allowedConnectionRoles + "]");
                if (null != allowedConnectionRoles) {
                    // Obtain a first set of candidate Defintiion identifiers.
                    final Set<String> allowedDefinitions = getDefinitions(defSetId,
                                                                          allowedConnectionRoles);
                    log(Level.FINEST,
                        "Allowed target definitions that pass connection rules " +
                                "ARE [" + allowedConnectionRoles + "]");
                    if (null != allowedDefinitions) {
                        final Map<String, Integer> graphLabelCount = GraphUtils.getLabelsCount(graph,
                                                                                               allowedConnectionRoles);
                        final int inConnectorsCount = countIncomingEdges(sourceNode,
                                                                         edgeId);
                        allowedDefinitions
                                .stream()
                                .forEach(defId -> {
                                    final Object targetDefinition = createDefinition(defId);
                                    if (null != targetDefinition) {
                                        final Set<String> targetDefinitionRoles =
                                                getDefinitionManager()
                                                        .adapters()
                                                        .forDefinition()
                                                        .getLabels(targetDefinition);
                                        // Check cardinality for each of the roles for this potential target node.
                                        final boolean hasCardinalityViolations = targetDefinitionRoles
                                                .stream()
                                                .filter(role -> {
                                                    final Integer roleCount = Optional.ofNullable(graphLabelCount.get(role)).orElse(0);
                                                    final RuleViolations violations =
                                                            ruleManager.evaluate(ruleSet,
                                                                                 RuleContextBuilder.DomainContexts.cardinality(Collections.singleton(role),
                                                                                                                               roleCount,
                                                                                                                               Optional.of(CardinalityContext.Operation.ADD)));
                                                    return !pass(violations);
                                                })
                                                .findFirst()
                                                .isPresent();
                                        log(Level.FINEST,
                                            "Cardinality rules evaluation " +
                                                    "result = [" + hasCardinalityViolations + "]");
                                        if (!hasCardinalityViolations) {
                                            // Check incoming connector cardinality for each the target node.
                                            final RuleViolations iev =
                                                    ruleManager.evaluate(ruleSet,
                                                                         RuleContextBuilder.DomainContexts.edgeCardinality(Collections.singleton(defId),
                                                                                                                           edgeId,
                                                                                                                           inConnectorsCount,
                                                                                                                           EdgeCardinalityContext.Direction.INCOMING,
                                                                                                                           Optional.of(CardinalityContext.Operation.ADD)));
                                            final boolean ieCardinalityAllowed = pass(iev);
                                            log(Level.FINEST,
                                                "Incoming edge cardinality rules evaluation " +
                                                        "result = [" + ieCardinalityAllowed + "]");
                                            if (ieCardinalityAllowed) {
                                                // This potential node can be used as target one, as it passes all rule checks.
                                                result.add(targetDefinition);
                                            }
                                        }
                                    }
                                });
                        return result;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns all the Definition Set's definition identifiers that contains the given labels.
     * <p>
     * TODO: Handle several result pages.
     */
    private Set<String> getDefinitions(final String defSetId,
                                       final Set<String> labels) {
        if (null != labels && !labels.isEmpty()) {
            final DefinitionLookupRequest request =
                    new DefinitionLookupRequest.Builder()
                            .definitionSetId(defSetId)
                            .labels(labels)
                            .page(0)
                            .pageSize(100)
                            .build();
            final LookupManager.LookupResponse<DefinitionRepresentation> response = definitionLookupManager.lookup(request);
            final List<DefinitionRepresentation> definitionRepresentations = response.getResults();
            if (null != definitionRepresentations && !definitionRepresentations.isEmpty()) {
                final Set<String> result = new LinkedHashSet<>();
                for (final DefinitionRepresentation definitionRepresentation : definitionRepresentations) {
                    final String id = definitionRepresentation.getDefinitionId();
                    result.add(id);
                }
                return result;
            }
        }
        return new HashSet<>(0);
    }

    /**
     * Returns the allowed edge identifiers that satisfy connection rules for the given
     * source definition.
     * @oaram sourceDefinition The domain model object ( not a graph element ).
     */
    private <T> Set<String> getConnectionRulesAllowedEdges(final String defSetId,
                                                           final T sourceDefinition,
                                                           final int page,
                                                           final int pageSize) {
        final List<Rule> rules = lookupConnectionRules(defSetId,
                                                       sourceDefinition,
                                                       null,
                                                       page,
                                                       pageSize);
        if (null != rules && !rules.isEmpty()) {
            final Set<String> result = new LinkedHashSet<>();
            for (final Rule rule : rules) {
                final CanConnect cr = (CanConnect) rule;
                final String edgeId = cr.getRole();
                result.add(edgeId);
            }
            return result;
        }
        return null;
    }

    /**
     * Returns the allowed ROLES that satisfy connection rules for a given source
     * definition ( domain model object, not a node ).and the given edge (connector) identifier.
     * <p>
     * TODO: Handle several result pages.
     */
    private <T> Set<String> getConnectionRulesAllowedTargets(final String defSetId,
                                                             final T sourceDefinition,
                                                             final String edgeId,
                                                             final int page,
                                                             final int pageSize) {
        final List<Rule> rules = lookupConnectionRules(defSetId,
                                                       sourceDefinition,
                                                       edgeId,
                                                       page,
                                                       pageSize);
        if (null != rules && !rules.isEmpty()) {
            final Set<String> result = new LinkedHashSet<>();
            final Set<String> sourceDefLabels = getDefinitionLabels(sourceDefinition);
            for (final Rule rule : rules) {
                final CanConnect cr = (CanConnect) rule;
                final List<CanConnect.PermittedConnection> connections = cr.getPermittedConnections();
                if (null != connections && !connections.isEmpty()) {
                    for (final CanConnect.PermittedConnection connection : connections) {
                        if (sourceDefLabels != null && sourceDefLabels.contains(connection.getStartRole())) {
                            result.add(connection.getEndRole());
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

    private <T> List<Rule> lookupConnectionRules(final String defSetId,
                                                 final T sourceDefinition,
                                                 final String edgeId,
                                                 final int page,
                                                 final int pageSize) {
        if (null != defSetId) {
            final Set<String> defLabels = getDefinitionLabels(sourceDefinition);
            final RuleLookupRequest.Builder builder = new RuleLookupRequest.Builder();
            builder.definitionSetId(defSetId)
                    .type(RuleLookupRequest.Builder.RuleType.CONNECTION)
                    .from(defLabels)
                    .page(page)
                    .pageSize(pageSize);
            if (null != edgeId) {
                builder.id(edgeId);
            }
            final RuleLookupRequest request = builder.build();
            final LookupManager.LookupResponse<Rule> response = ruleLookupManager.lookup(request);
            return response.getResults();
        }
        return null;
    }

    private <T> int countIncomingEdges(final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                       final String edgeId) {
        final List<? extends Edge> edges = sourceNode.getInEdges();
        return GraphUtils.countEdges(getDefinitionManager(),
                                     edgeId,
                                     edges);
    }

    private <T> int countOutgoingEdges(final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                       final String edgeId) {
        final List<? extends Edge> edges = sourceNode.getOutEdges();
        return GraphUtils.countEdges(getDefinitionManager(),
                                     edgeId,
                                     edges);
    }

    private <T> Set<String> getDefinitionLabels(final T definition) {
        return getDefinitionManager().adapters().forDefinition().getLabels(definition);
    }

    private boolean pass(final RuleViolations violations) {
        return null == violations || !violations.violations(RuleViolation.Type.ERROR).iterator().hasNext();
    }

    private Object createDefinition(final String defId) {
        // TODO: Avoid new instances here.
        return factoryManager.newDefinition(defId);
    }

    private DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }

    private void log(final Level level,
                     final String message) {
        LOGGER.log(level,
                   message);
    }
}
