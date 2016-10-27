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
import org.kie.workbench.common.stunner.core.rule.CardinalityRule;
import org.kie.workbench.common.stunner.core.rule.ConnectionRule;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.Rule;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * An utils class that provides common used look-ups and other logic for querying the domain model and the rules model,
 * that is used along the application.
 * <p>
 * // TODO: Cache.
 */
@ApplicationScoped
public class CommonLookups {

    DefinitionUtils definitionUtils;
    DefinitionLookupManager definitionLookupManager;
    RuleLookupManager ruleLookupManager;
    GraphUtils graphUtils;
    FactoryManager factoryManager;

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
    public <T> Set<String> getAllowedConnectors( final String defSetId,
                                                 final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                                 final int page,
                                                 final int pageSize ) {
        final Set<String> result = new LinkedHashSet<>();
        if ( null != defSetId && null != sourceNode ) {
            final T definition = sourceNode.getContent().getDefinition();
            final Set<String> connectionAllowedEdges = getConnectionRulesAllowedEdges( defSetId, definition, page, pageSize );
            if ( null != connectionAllowedEdges && !connectionAllowedEdges.isEmpty() ) {
                for ( final String allowedEdgeId : connectionAllowedEdges ) {
                    final int edgeCount = countOutgoingEdges( sourceNode, allowedEdgeId );
                    final boolean isOutEdgeCardinalityRuleAllowed
                            = isOutEdgeCardinalityRuleAllowed( defSetId,
                            sourceNode.getContent().getDefinition(),
                            allowedEdgeId, edgeCount );
                    if ( isOutEdgeCardinalityRuleAllowed ) {
                        result.add( allowedEdgeId );
                    }
                }

            }

        }
        return result;
    }

    public <T> Set<String> getAllowedMorphDefaultDefinitions( final String defSetId,
                                                              final Graph<?, ? extends Node> graph,
                                                              final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                                              final String edgeId,
                                                              final int page,
                                                              final int pageSize ) {
        final Set<String> allowedDefinitions = getAllowedDefinitions( defSetId, graph, sourceNode, edgeId, page, pageSize );
        if ( null != allowedDefinitions && !allowedDefinitions.isEmpty() ) {
            final Set<String> result = new HashSet<>( allowedDefinitions.size() );
            for ( final String defId : allowedDefinitions ) {
                // TODO: Avoid new instances here.
                final Object definition = factoryManager.newDefinition( defId );
                final MorphDefinition morphDefinition = definitionUtils.getMorphDefinition( definition );
                final boolean hasMorphBase = null != morphDefinition;
                final String id = hasMorphBase ? morphDefinition.getDefault() : defId;
                result.add( id );

            }
            return result;

        }
        return null;
    }

    /**
     * Returns the allowed definition identifiers that can be used as target node for the given source node and
     * the given edge (connector) identifier.
     */
    public <T> Set<String> getAllowedDefinitions( final String defSetId,
                                                  final Graph<?, ? extends Node> graph,
                                                  final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                                  final String edgeId,
                                                  final int page,
                                                  final int pageSize ) {
        final Set<String> result = new LinkedHashSet<>();
        if ( null != defSetId && null != graph && null != sourceNode && null != edgeId ) {
            final T definition = sourceNode.getContent().getDefinition();
            final Set<String> allowedTargetRoles = getConnectionRulesAllowedTargets( defSetId, definition, edgeId, page, pageSize );
            if ( null != allowedTargetRoles && !allowedTargetRoles.isEmpty() ) {
                final Set<String> allowedTargetRoles2 = new LinkedHashSet<>();
                for ( final String s : allowedTargetRoles ) {
                    final boolean isCardinalityAllowed = isCardinalitySatisfied( defSetId, graph, definition );
                    if ( isCardinalityAllowed ) {
                        allowedTargetRoles2.add( s );
                    }

                }
                if ( !allowedTargetRoles2.isEmpty() ) {
                    final Set<String> allowedTargetRoles3 = new LinkedHashSet<>();
                    for ( final String s : allowedTargetRoles2 ) {
                        final HashSet<String> hs = new HashSet<String>( 1 ) {{
                            add( s );
                        }};
                        final boolean isInEdgeCardinalityRuleAllowed
                                = isInEdgeCardinalityRuleAllowed( defSetId,
                                hs, edgeId );
                        if ( isInEdgeCardinalityRuleAllowed ) {
                            allowedTargetRoles3.add( s );
                        }

                    }
                    return getDefinitions( defSetId, allowedTargetRoles3 );

                }

            }

        }
        return result;
    }

    /**
     * Returns all the Definition Set's definition identifiers that contains the given labels.
     */
    public Set<String> getDefinitions( final String defSetId, final Set<String> labels ) {
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
     * source definition ( domain model object, not a graph node ).
     */
    public <T> Set<String> getConnectionRulesAllowedEdges( final String defSetId,
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
     * Returns the allowed definitions identifiers that satisfy connection rules for a given source
     * definition ( domain model object, not a node ).and the given edge (connector) identifier.
     */
    public <T> Set<String> getConnectionRulesAllowedTargets( final String defSetId,
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

    /**
     * Check if cardinality rules satisfy that a given definition ( domain model object, not a node ).can be added into a graph
     * TODO: Delegate to  the @Model RulesManager.
     */
    public <T> boolean isCardinalitySatisfied( final String defSetId,
                                               final Graph<?, ? extends Node> target,
                                               final T sourceDefinition ) {
        final Set<String> defLabels = getDefinitionLabels( sourceDefinition );
        final RuleLookupRequest request =
                new RuleLookupRequestImpl.Builder()
                        .definitionSetId( defSetId )
                        .type( RuleLookupRequestImpl.Builder.RuleType.CARDINALITY )
                        .roleIn( defLabels )
                        .page( 0 )
                        .pageSize( 100 )
                        .build();
        final LookupManager.LookupResponse<Rule> response = ruleLookupManager.lookup( request );
        final List<Rule> rules = response.getResults();
        if ( null != rules && !rules.isEmpty() ) {
            final int count = graphUtils.countDefinitions( target, sourceDefinition );
            for ( final Rule rule : rules ) {
                final CardinalityRule cr = ( CardinalityRule ) rule;
                final int max = cr.getMaxOccurrences();
                if ( max == 0 || max >= count ) {
                    return false;
                }

            }

        }
        return true;

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

    private <T> boolean isInEdgeCardinalityRuleAllowed( final String defSetId,
                                                        final Set<String> defLabels,
                                                        final String edgeId ) {
        return isEdgeCardinalityRuleAllowed( defSetId, defLabels, edgeId,
                RuleLookupRequestImpl.Builder.EdgeType.INCOMING, 0 );

    }

    private <T> boolean isOutEdgeCardinalityRuleAllowed( final String defSetId,
                                                         final T sourceDefinition,
                                                         final String edgeId,
                                                         final int edgesCount ) {
        final Set<String> defLabels = getDefinitionLabels( sourceDefinition );
        return isEdgeCardinalityRuleAllowed( defSetId, defLabels, edgeId,
                RuleLookupRequestImpl.Builder.EdgeType.OUTGOING, edgesCount );

    }

    private <T> boolean isEdgeCardinalityRuleAllowed( final String defSetId,
                                                      final Set<String> defLabels,
                                                      final String edgeId,
                                                      final RuleLookupRequestImpl.Builder.EdgeType edgeType,
                                                      final int edgesCount ) {
        if ( null != defSetId ) {
            final RuleLookupRequest request =
                    new RuleLookupRequestImpl.Builder()
                            .definitionSetId( defSetId )
                            .type( RuleLookupRequestImpl.Builder.RuleType.EDGECARDINALITY )
                            .edgeType( edgeType )
                            .roleIn( defLabels )
                            .id( edgeId )
                            .page( 0 )
                            .pageSize( 100 )
                            .build();
            final LookupManager.LookupResponse<Rule> response = ruleLookupManager.lookup( request );
            final List<Rule> rules = response.getResults();
            // TODO: Delegate to the @Model RulesManager?
            if ( null != rules && !rules.isEmpty() ) {
                for ( final Rule rule : rules ) {
                    final EdgeCardinalityRule cr = ( EdgeCardinalityRule ) rule;
                    final int max = cr.getMaxOccurrences();
                    if ( max == 0 || max >= edgesCount ) {
                        return false;
                    }

                }

            }
            return true;

        }
        return false;
    }

    private <T> int countOutgoingEdges( final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                        final String edgeId ) {
        final List<? extends Edge> edges = sourceNode.getOutEdges();
        return graphUtils.countEdges( edgeId, edges );

    }

    private <T> Set<String> getDefinitionLabels( final T definition ) {
        return getDefinitionManager().adapters().forDefinition().getLabels( definition );

    }

    private DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }

}
