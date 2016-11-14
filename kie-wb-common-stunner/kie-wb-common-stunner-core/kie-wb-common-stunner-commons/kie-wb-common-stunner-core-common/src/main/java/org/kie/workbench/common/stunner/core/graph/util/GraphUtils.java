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

package org.kie.workbench.common.stunner.core.graph.util;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@ApplicationScoped
public class GraphUtils {

    DefinitionManager definitionManager;

    protected GraphUtils() {
    }

    @Inject
    @SuppressWarnings( "all" )
    public GraphUtils( final DefinitionManager definitionManager ) {
        this.definitionManager = definitionManager;
    }

    @SuppressWarnings( "unchecked" )
    public static Graph<?, Node> getGraph( final GraphCommandExecutionContext context ) {
        return ( Graph<?, Node> ) context.getGraphIndex().getGraph();
    }

    public static Node<?, Edge> getNode( final GraphCommandExecutionContext context, final String uuid ) {
        return context.getGraphIndex().getNode( uuid );
    }

    public static Edge<? extends View, Node> getViewEdge( final GraphCommandExecutionContext context, final String uuid ) {
        return context.getGraphIndex().getEdge( uuid );
    }

    public Object getProperty( final Element<? extends Definition> element, final String id ) {
        return getProperty( definitionManager, element, id );
    }

    public static Object getProperty( final DefinitionManager definitionManager,
                                      final Element<? extends Definition> element,
                                      final String id ) {
        if ( null != element ) {
            final Object def = element.getContent().getDefinition();
            final Set<?> properties = definitionManager.adapters().forDefinition().getProperties( def );
            return getProperty( definitionManager, properties, id );
        }
        return null;
    }

    public Object getProperty( final Set<?> properties, final String id ) {
        return getProperty( definitionManager, properties, id );
    }

    public static Object getProperty( final DefinitionManager definitionManager,
                                      final Set<?> properties,
                                      final String id ) {
        if ( null != id && null != properties ) {
            for ( final Object property : properties ) {
                final String pId = definitionManager.adapters().forProperty().getId( property );
                if ( pId.equals( id ) ) {
                    return property;
                }
            }
        }
        return null;
    }

    public <T> int countDefinitions( final Graph<?, ? extends Node> target,
                                     final T definition ) {
        final String id = getDefinitionId( definition );
        int count = 1;
        for ( Node<? extends View, ? extends Edge> node : target.nodes() ) {
            if ( getElementDefinitionId( node ).equals( id ) ) {
                count++;
            }
        }
        return count;
    }

    public int countEdges( final String edgeId,
                           final List<? extends Edge> edges ) {
        if ( null != edges ) {
            int c = 0;
            for ( Edge e : edges ) {
                final String eId = getElementDefinitionId( e );
                if ( null != eId && edgeId.equals( eId ) ) {
                    c++;
                }

            }
            return c;
        }
        return 0;
    }

    private <T> String getDefinitionId( final T definition ) {
        return definitionManager.adapters().forDefinition().getId( definition );

    }

    public String getElementDefinitionId( final Element<?> element ) {
        String targetId = null;
        if ( element.getContent() instanceof Definition ) {
            final Object definition = ( ( Definition ) element.getContent() ).getDefinition();
            targetId = getDefinitionId( definition );

        } else if ( element.getContent() instanceof DefinitionSet ) {
            targetId = ( ( DefinitionSet ) element.getContent() ).getDefinition();

        }
        return targetId;
    }

    public static Double[] getPosition( final View element ) {
        final Bounds.Bound ul = element.getBounds().getUpperLeft();
        final double x = ul.getX();
        final double y = ul.getY();
        return new Double[]{ x, y };
    }

    public static Double[] getSize( final View element ) {
        final Bounds.Bound ul = element.getBounds().getUpperLeft();
        final Bounds.Bound lr = element.getBounds().getLowerRight();
        final double w = lr.getX() - ul.getX();
        final double h = lr.getY() - ul.getY();
        return new Double[]{ Math.abs( w ), Math.abs( h ) };
    }

    public static void updateBounds( final double radius,
                                     final View element ) {
        final Double[] coords = getPosition( element );
        updateBounds( coords[ 0 ], coords[ 1 ], radius, element );
    }

    public static void updateBounds( final double x,
                                     final double y,
                                     final double radius,
                                     final View element ) {
        updateBounds( x, y, radius * 2, radius * 2, element );
    }

    public static void updateBounds( final double width, final double height, final View element ) {
        final Double[] coords = getPosition( element );
        updateBounds( coords[ 0 ], coords[ 1 ], width, height, element );
    }

    public static void updateBounds( final double x,
                                     final double y,
                                     final double width,
                                     final double height,
                                     final View element ) {
        final Bounds bounds = new BoundsImpl(
                new BoundImpl( x, y ),
                new BoundImpl( x + width, y + height )
        );
        element.setBounds( bounds );
    }

    /**
     * Finds the first node in the graph structure for the given type.
     * @param graph The graph structure.
     * @param type The Definition type..
     */
    @SuppressWarnings( "unchecked" )
    public static <C> Node<Definition<C>, ?> getFirstNode( final Graph<?, Node> graph,
                                                           final Class<?> type ) {
        if ( null != graph ) {
            for ( final Node node : graph.nodes() ) {
                final Object content = node.getContent();
                try {
                    final Definition definitionContent = ( Definition ) content;
                    if ( instanceOf( definitionContent.getDefinition(), type ) ) {
                        return node;
                    }
                } catch ( final ClassCastException e ) {
                    // Node content does not contains a definition.
                }
            }
        }
        return null;
    }

    private static boolean instanceOf( final Object item, final Class<?> clazz ) {
        return null != item && item.getClass().equals( clazz );
    }

}
