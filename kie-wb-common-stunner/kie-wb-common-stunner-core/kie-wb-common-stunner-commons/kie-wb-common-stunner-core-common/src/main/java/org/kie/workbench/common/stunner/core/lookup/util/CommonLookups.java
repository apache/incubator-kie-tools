/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.lookup.util;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupManager;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupRequestImpl;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionRepresentation;
import org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupManager;
import org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupRequestImpl;
import org.kie.workbench.common.stunner.core.rule.*;
import org.kie.workbench.common.stunner.core.rule.model.ModelRulesManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An utils class that provides common used look-ups and other logic for querying the domain model and the rules model,
 * that is used along the application.
 * <p>
 * // TODO: Some kind of cache to avoid frequently used lookups? Consider performance and memory, this class
 * is shared on both server and client sides.
 */
@ApplicationScoped
public class CommonLookups {

    private static Logger LOGGER = Logger.getLogger( CommonLookups.class.getName() );

    private final DefinitionUtils definitionUtils;
    private final DefinitionLookupManager definitionLookupManager;
    private final RuleLookupManager ruleLookupManager;
    private final GraphUtils graphUtils;
    private final FactoryManager factoryManager;

    protected CommonLookups() {
        this( null, null, null, null, null );
    }

    @Inject
    public CommonLookups( final DefinitionUtils definitionUtils,
                          final GraphUtils graphUtils,
                          final DefinitionLookupManager definitionLookupManager,
                          final RuleLookupManager ruleLookupManager,
                          final FactoryManager factoryManager ) {
        this.definitionUtils = definitionUtils;
        this.graphUtils = graphUtils;
        this.definitionLookupManager = definitionLookupManager;
        this.ruleLookupManager = ruleLookupManager;
        this.factoryManager = factoryManager;
    }

    /**
     * Returns the allowed edge definition identifiers that can be added as outgoing edges for the given source node.
     */
    public <T> Set<String> getAllowedConnectors( final ModelRulesManager modelRulesManager,
                                                 final String defSetId,
                                                 final Node<Definition<T>, Edge> sourceNode,
                                                 final int page,
                                                 final int pageSize ) {
        final Set<String> result = new LinkedHashSet<>();
        if ( null != defSetId && null != sourceNode ) {
            final T definition = sourceNode.getContent().getDefinition();
            final Set<String> connectionAllowedEdges = getConnectionRulesAllowedEdges( defSetId, definition, page, pageSize );
            if ( null != connectionAllowedEdges && !connectionAllowedEdges.isEmpty() ) {
                connectionAllowedEdges.stream().forEach( allowedEdgeId -> {
                    final int edgeCount = countOutgoingEdges( sourceNode, allowedEdgeId );
                    final RuleViolations oev = modelRulesManager
                            .edgeCardinality()
                            .evaluate( allowedEdgeId, sourceNode.getLabels(), edgeCount,
                            EdgeCardinalityRule.Type.OUTGOING, RuleManager.Operation.ADD );
                    final boolean oeCardinalityAllowed = pass( oev );
                    LOGGER.log( Level.FINE, "Outgoing edge cardinality rules evaluation " +
                            "result = [" + oeCardinalityAllowed + "]" );
                    if ( oeCardinalityAllowed ) {
                        result.add( allowedEdgeId );
                    }
                } );
            }

        }
        return result;
    }

    /**
     * Returns the allowed definition identifiers that can be used as target node for the given source node and
     * the given edge (connector) identifier.
     * This method only returns the definition identifiers that are considered the default types for its morph type,
     * it does NOT return all the identifiers for all the allowed target definitions.
     * <p>
     * TODO: Handle several result pages.
     */
    public <T> Set<String> getAllowedMorphDefaultDefinitions( final ModelRulesManager modelRulesManager,
                                                              final String defSetId,
                                                              final Graph<?, ? extends Node> graph,
                                                              final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                                              final String edgeId,
                                                              final int page,
                                                              final int pageSize ) {
        final Set<Object> allowedDefinitions =
                getAllowedTargetDefinitions( modelRulesManager, defSetId, graph, sourceNode, edgeId, page, pageSize );
        LOGGER.log( Level.FINE, "Target definitions allowed " +
                "for [" + sourceNode + "] and using the " +
                "connector [" + edgeId + "] " +
                "ARE [" + allowedDefinitions + "]" );
        if ( null != allowedDefinitions ) {
            final Set<String> result = new LinkedHashSet<>();
            allowedDefinitions.stream().forEach( definition -> {
                final String defId = getDefinitionManager().adapters().forDefinition().getId( definition );
                final MorphDefinition morphDefinition = definitionUtils.getMorphDefinition( definition );
                final boolean hasMorphBase = null != morphDefinition;
                final String id = hasMorphBase ? morphDefinition.getDefault() : defId;
                result.add( id );
            } );
            LOGGER.log( Level.FINE, "Target definitions group by morph base type allowed " +
                    "for [" + sourceNode + "] and using the " +
                    "connector [" + edgeId + "] " +
                    "ARE [" + result + "]" );
            return result;

        }
        return null;
    }

    /**
     * Returns the allowed definition identifiers that can be used as target node for the given source node and
     * the given edge (connector) identifier.
     * <p>
     * TODO: Handle several result pages.
     */
    public <T> Set<Object> getAllowedTargetDefinitions( final ModelRulesManager modelRulesManager,
                                                        final String defSetId,
                                                        final Graph<?, ? extends Node> graph,
                                                        final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                                        final String edgeId,
                                                        final int page,
                                                        final int pageSize ) {
        if ( null != defSetId && null != graph && null != sourceNode && null != edgeId ) {
            final T definition = sourceNode.getContent().getDefinition();
            LOGGER.log( Level.FINE, "*** Checking the target definitions allowed " +
                    "for [" + definition + "] and using the " +
                    "connector [" + edgeId + "] ***" );
            // Check outgoing connectors cardinality for the source node ( plus the new one to be added ).
            final int outConnectorsCount = countOutgoingEdges( sourceNode, edgeId );
            LOGGER.log( Level.FINE, "The source node has  " + outConnectorsCount + "] outgoing connections." );
            final RuleViolations oev = modelRulesManager.edgeCardinality().evaluate( edgeId, sourceNode.getLabels(), outConnectorsCount,
                    EdgeCardinalityRule.Type.OUTGOING, RuleManager.Operation.ADD );
            final boolean oeCardinalityAllowed = pass( oev );
            LOGGER.log( Level.FINE, "Outgoing edge cardinality rules evaluation " +
                    "result = [" + oeCardinalityAllowed + "]" );
            if ( oeCardinalityAllowed ) {
                // Obtain allowed target roles that pass connection rules.
                final Set<String> allowedConnectionRoles =
                        getConnectionRulesAllowedTargets( defSetId, definition, edgeId, page, pageSize );
                LOGGER.log( Level.FINE, "Allowed target roles that pass connection rules " +
                        "ARE [" + allowedConnectionRoles + "]" );
                if ( null != allowedConnectionRoles ) {
                    // Obtain a first set of candidate Defintiion identifiers.
                    final Set<String> allowedDefinitions = getDefinitions( defSetId, allowedConnectionRoles );
                    LOGGER.log( Level.FINE, "Allowed target definitions that pass connection rules " +
                            "ARE [" + allowedConnectionRoles + "]" );
                    if ( null != allowedDefinitions ) {
                        final Map<String, Integer> graphLabelCount =
                                GraphUtils.getLabelsCount( graph, allowedConnectionRoles );
                        final int inConnectorsCount = countIncomingEdges( sourceNode, edgeId );
                        final Set<Object> result = new LinkedHashSet<>();
                        allowedDefinitions
                                .stream()
                                .forEach( defId -> {
                                    final Object targetDefinition = createDefinition( defId );
                                    if ( null != targetDefinition ) {
                                        final Set<String> targetDefinitionRoles =
                                                getDefinitionManager()
                                                        .adapters()
                                                        .forDefinition()
                                                        .getLabels( targetDefinition );
                                        // Check cardinality for each of the roles for this potential target node.
                                        final boolean hasCardinalityViolations = targetDefinitionRoles
                                                .stream()
                                                .filter( role -> {
                                                    final Integer i = graphLabelCount.get( role );
                                                    final RuleViolations violations =
                                                            modelRulesManager
                                                                    .cardinality()
                                                                    .evaluate( role, null != i ? i : 0,
                                                                            RuleManager.Operation.ADD );
                                                    return !pass( violations );
                                                } )
                                                .findFirst()
                                                .isPresent();
                                        LOGGER.log( Level.FINE, "Cardinality rules evaluation " +
                                                "result = [" + hasCardinalityViolations + "]" );
                                        if ( !hasCardinalityViolations ) {
                                            // Check incoming connector cardinality for each the target node.
                                            final RuleViolations iev = modelRulesManager
                                                    .edgeCardinality()
                                                    .evaluate( edgeId, targetDefinitionRoles, inConnectorsCount,
                                                    EdgeCardinalityRule.Type.INCOMING, RuleManager.Operation.ADD );
                                            final boolean ieCardinalityAllowed = pass( iev );
                                            LOGGER.log( Level.FINE, "Incoming edge cardinality rules evaluation " +
                                                    "result = [" + ieCardinalityAllowed + "]" );
                                            if ( ieCardinalityAllowed ) {
                                                // This potential node can be used as target one, as it passes all rule checks.
                                                result.add( targetDefinition );
                                            }
                                        }
                                    }
                                } );
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns all the Definition Set's definition identifiers that contains the given labels.
     * <p>
     * TODO: Handle several result pages.
     */
    private Set<String> getDefinitions( final String defSetId, final Set<String> labels ) {
        if ( null != labels && !labels.isEmpty() ) {
            final DefinitionLookupRequest request =
                    new DefinitionLookupRequestImpl.Builder()
                            .definitionSetId( defSetId )
                            .labels( labels )
                            .page( 0 )
                            .pageSize( 100 )
                            .build();
            final LookupManager.LookupResponse<DefinitionRepresentation> response = definitionLookupManager.lookup( request );
            final List<DefinitionRepresentation> definitionRepresentations = response.getResults();
            if ( null != definitionRepresentations && !definitionRepresentations.isEmpty() ) {
                final Set<String> result = new LinkedHashSet<>();
                for ( final DefinitionRepresentation definitionRepresentation : definitionRepresentations ) {
                    final String id = definitionRepresentation.getDefinitionId();
                    result.add( id );
                }
                return result;
            }

        }
        return new HashSet<>( 0 );
    }

    /**
     * Returns the allowed edge identifiers that satisfy connection rules for the given
     * source definition.
     *
     * @oaram sourceDefinition The domain model object ( not a graph element ).
     */
    private <T> Set<String> getConnectionRulesAllowedEdges( final String defSetId,
                                                           final T sourceDefinition,
                                                           final int page,
                                                           final int pageSize ) {
        final List<Rule> rules = lookupConnectionRules( defSetId, sourceDefinition, null, page, pageSize );
        if ( null != rules && !rules.isEmpty() ) {
            final Set<String> result = new LinkedHashSet<>();
            for ( final Rule rule : rules ) {
                final ConnectionRule cr = ( ConnectionRule ) rule;
                final String edgeId = cr.getId();
                result.add( edgeId );
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
    private  <T> Set<String> getConnectionRulesAllowedTargets( final String defSetId,
                                                             final T sourceDefinition,
                                                             final String edgeId,
                                                             final int page,
                                                             final int pageSize ) {
        final List<Rule> rules = lookupConnectionRules( defSetId, sourceDefinition, edgeId, page, pageSize );
        if ( null != rules && !rules.isEmpty() ) {
            final Set<String> result = new LinkedHashSet<>();
            for ( final Rule rule : rules ) {
                final ConnectionRule cr = ( ConnectionRule ) rule;
                final Set<ConnectionRule.PermittedConnection> connections = cr.getPermittedConnections();
                if ( null != connections && !connections.isEmpty() ) {
                    for ( final ConnectionRule.PermittedConnection connection : connections ) {
                        result.add( connection.getEndRole() );
                    }
                }
            }
            return result;
        }
        return null;
    }

    private <T> List<Rule> lookupConnectionRules( final String defSetId,
                                                  final T sourceDefinition,
                                                  final String edgeId,
                                                  final int page,
                                                  final int pageSize ) {
        if ( null != defSetId ) {
            final Set<String> defLabels = getDefinitionLabels( sourceDefinition );
            final RuleLookupRequestImpl.Builder builder = new RuleLookupRequestImpl.Builder();
            builder.definitionSetId( defSetId )
                    .type( RuleLookupRequestImpl.Builder.RuleType.CONNECTION )
                    .from( defLabels )
                    .page( page )
                    .pageSize( pageSize );
            if ( null != edgeId ) {
                builder.id( edgeId );
            }
            final RuleLookupRequest request = builder.build();
            final LookupManager.LookupResponse<Rule> response = ruleLookupManager.lookup( request );
            return response.getResults();
        }
        return null;
    }

    private <T> int countIncomingEdges( final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                        final String edgeId ) {
        final List<? extends Edge> edges = sourceNode.getInEdges();
        return graphUtils.countEdges( edgeId, edges );
    }

    private <T> int countOutgoingEdges( final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                        final String edgeId ) {
        final List<? extends Edge> edges = sourceNode.getOutEdges();
        return graphUtils.countEdges( edgeId, edges );
    }

    private <T> Set<String> getDefinitionLabels( final T definition ) {
        return getDefinitionManager().adapters().forDefinition().getLabels( definition );
    }

    private boolean pass( final RuleViolations violations ) {
        return null == violations || !violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext();
    }

    private Object createDefinition( final String defId ) {
        // TODO: Avoid new instances here.
        return factoryManager.newDefinition( defId );
    }

    private DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }

}
