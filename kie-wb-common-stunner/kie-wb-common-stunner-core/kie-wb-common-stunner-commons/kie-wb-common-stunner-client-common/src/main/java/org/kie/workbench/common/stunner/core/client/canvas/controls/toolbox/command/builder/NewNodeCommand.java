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

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder;

import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.NodeBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.NodeBuildRequestImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryServices;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.mvp.Command;

import javax.enterprise.event.Event;

public abstract class NewNodeCommand<I> extends AbstractElementBuilderCommand<I> {

    NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory;
    NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl;
    Event<CanvasElementSelectedEvent> elementSelectedEvent;
    DefinitionUtils definitionUtils;
    CanvasLayoutUtils canvasLayoutUtils;

    protected String definitionId;
    protected int sourceMagnet;
    protected int targetMagnet;
    private HasEventHandlers<?, ?> layerEventHandlers;

    protected NewNodeCommand() {
        this( null, null, null, null, null, null, null, null, null, null );
    }

    public NewNodeCommand( final ClientDefinitionManager clientDefinitionManager,
                           final ClientFactoryServices clientFactoryServices,
                           final ShapeManager shapeManager,
                           final DefinitionGlyphTooltip<?> glyphTooltip,
                           final GraphBoundsIndexer graphBoundsIndexer,
                           final NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory,
                           final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl,
                           final DefinitionUtils definitionUtils,
                           final CanvasLayoutUtils canvasLayoutUtils,
                           final Event<CanvasElementSelectedEvent> elementSelectedEvent ) {
        super( clientDefinitionManager, clientFactoryServices, shapeManager, glyphTooltip, graphBoundsIndexer );
        this.nodeDragProxyFactory = nodeDragProxyFactory;
        this.nodeBuilderControl = nodeBuilderControl;
        this.definitionUtils = definitionUtils;
        this.canvasLayoutUtils = canvasLayoutUtils;
        this.elementSelectedEvent = elementSelectedEvent;
        this.layerEventHandlers = null;
    }

    public void setDefinitionIdentifier( final String definitionId ) {
        this.definitionId = definitionId;

    }

    protected String getEdgeIdentifier( final Context<AbstractCanvasHandler> context ) {
        final String defSetId = context.getCanvasHandler().getDiagram().getSettings().getDefinitionSetId();
        return definitionUtils.getDefaultConnectorId( defSetId );
    }

    @Override
    protected String getDefinitionIdentifier( final Context<AbstractCanvasHandler> context ) {
        return getEdgeIdentifier( context );
    }

    @Override
    protected String getGlyphDefinitionId() {
        return this.definitionId;
    }

    @Override
    public String getTitle() {
        return "Creates a new node";
    }

    @Override
    public void click( final Context<AbstractCanvasHandler> context,
                       final Element element ) {
        super.click( context, element );
        addOnNextLayoutPosition( context, element );
    }

    // TODO: Move all these stuff to canvas builder controls?
    @SuppressWarnings( "unchecked" )
    private void addOnNextLayoutPosition( final Context<AbstractCanvasHandler> context,
                                          final Element element ) {
        fireLoadingStarted( context );
        final AbstractCanvasHandler canvasHandler = context.getCanvasHandler();
        graphBoundsIndexer.setRootUUID( canvasHandler.getDiagram().getSettings().getCanvasRootUUID() );
        graphBoundsIndexer.build( canvasHandler.getDiagram().getGraph() );
        clientFactoryServices.newElement( UUID.uuid(), getDefinitionIdentifier( context ), new ServiceCallback<Element>() {

            @Override
            public void onSuccess( final Element newEdgeElement ) {
                onDefinitionInstanceBuilt( context, element, newEdgeElement, new Command() {

                    @Override
                    public void execute() {
                        getBuilderControl().enable( canvasHandler );
                        graphBoundsIndexer.build( canvasHandler.getDiagram().getGraph() );
                        // TODO: Use right magnets.
                        NewNodeCommand.this.sourceMagnet = 0;
                        NewNodeCommand.this.targetMagnet = 7;
                        final double[] next = canvasLayoutUtils.getNextLayoutPosition( canvasHandler, element );
                        NewNodeCommand.this.onComplete( context, element, newEdgeElement, ( int ) next[ 0 ], ( int ) next[ 1 ] );

                    }

                } );

            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                NewNodeCommand.this.onError( context, error );
            }

        } );

    }

    @Override
    protected DragProxy getDragProxyFactory() {
        return nodeDragProxyFactory;
    }

    @Override
    protected BuilderControl getBuilderControl() {
        return nodeBuilderControl;
    }

    @Override
    protected DragProxyCallback getDragProxyCallback( final Context<AbstractCanvasHandler> context,
                                                      final Element element,
                                                      final Element item ) {
        return new NodeDragProxyCallback() {

            @Override
            public void onStart( final int x,
                                 final int y ) {
                NewNodeCommand.this.onStart( context, element, item, x, y );

            }

            @Override
            public void onMove( final int x,
                                final int y ) {
                NewNodeCommand.this.onMove( context, element, item, x, y );

            }

            @Override
            public void onComplete( final int x,
                                    final int y ) {
            }

            @Override
            public void onComplete( final int x,
                                    final int y, final int sourceMagnet,
                                    final int targetMagnet ) {
                NewNodeCommand.this.sourceMagnet = sourceMagnet;
                NewNodeCommand.this.targetMagnet = targetMagnet;
                NewNodeCommand.this.onComplete( context, element, item, x, y );

            }

        };

    }

    @Override
    protected void onStart( final Context<AbstractCanvasHandler> context,
                            final Element element,
                            final Element item,
                            final int x1,
                            final int y1 ) {
        super.onStart( context, element, item, x1, y1 );
        // Disable layer events handlers in order to avoid layer events while using the drag def.
        this.layerEventHandlers = getLayer( context );
        disableHandlers();

    }

    @Override
    protected void onItemBuilt( final Context<AbstractCanvasHandler> context,
                                final String uuid ) {
        super.onItemBuilt( context, uuid );
        fireElementSelectedEvent( elementSelectedEvent, context.getCanvasHandler(), uuid );

    }

    @Override
    protected void onError( final Context<AbstractCanvasHandler> context,
                            final ClientRuntimeError error ) {
        super.onError( context, error );
        // Enable layer events handlers again.
        enableHandlers();
    }

    protected Layer getLayer( final Context<AbstractCanvasHandler> context ) {
        return context.getCanvasHandler().getCanvas().getLayer();

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void onDefinitionInstanceBuilt( final Context<AbstractCanvasHandler> context,
                                              final Element source,
                                              final Element newElement,
                                              final Command callback ) {
        final Node<View<?>, Edge> sourceNode = ( Node<View<?>, Edge> ) source;
        final Edge<View<?>, Node> edge = ( Edge<View<?>, Node> ) newElement;
        // Create the new node.
        clientFactoryServices.newElement( UUID.uuid(), definitionId, new ServiceCallback<Element>() {

            @Override
            public void onSuccess( Element item ) {
                final Node<View<?>, Edge> node = ( Node<View<?>, Edge> ) item.asNode();
                // Perform the temporal def connections.
                edge.setSourceNode( sourceNode );
                edge.setTargetNode( node );
                NewNodeCommand.super.onDefinitionInstanceBuilt( context, source, newElement, callback );
            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                NewNodeCommand.this.onError( context, error );
            }

        } );

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Object createtBuilderControlItem( final Context<AbstractCanvasHandler> context,
                                                final Element source,
                                                final Element newElement ) {
        final Node<View<?>, Edge> sourceNode = ( Node<View<?>, Edge> ) source;
        final Edge<View<?>, Node> edge = ( Edge<View<?>, Node> ) newElement;
        final String edgeId = getDefinitionId( edge.getContent().getDefinition() );
        final ShapeFactory<?, AbstractCanvasHandler, ?> nodeShapeFactory = shapeManager.getFactory( definitionId );
        final ShapeFactory<?, AbstractCanvasHandler, ?> edgeShapeFactory = shapeManager.getFactory( edgeId );
        return new NodeDragProxy.Item<AbstractCanvasHandler>() {
            @Override
            public Node<View<?>, Edge> getNode() {
                return edge.getTargetNode();
            }

            @Override
            public ShapeFactory<?, AbstractCanvasHandler, ?> getNodeShapeFactory() {
                return nodeShapeFactory;
            }

            @Override
            public Edge<View<?>, Node> getInEdge() {
                return edge;
            }

            @Override
            public Node<View<?>, Edge> getInEdgeSourceNode() {
                return sourceNode;
            }

            @Override
            public ShapeFactory<?, AbstractCanvasHandler, ?> getInEdgeShapeFactory() {
                return edgeShapeFactory;
            }

        };

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected boolean onDragProxyMove( final int x,
                                       final int y,
                                       final Element source,
                                       final Element newElement,
                                       final Node parent ) {
        final Edge<View<?>, Node> edge = ( Edge<View<?>, Node> ) newElement;
        final Node<View<?>, Edge> node = ( Node<View<?>, Edge> ) edge.getTargetNode();
        final NodeBuildRequest request = new NodeBuildRequestImpl( x, y, node, edge );
        final boolean accepts = nodeBuilderControl.allows( request );
        if ( accepts ) {
            if ( null != parent ) {
                return true;

            }

        }
        return false;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected BuildRequest createBuildRequest( final int x,
                                               final int y,
                                               final Element source,
                                               final Element newElement,
                                               final Node targetNode ) {
        final Edge<View<?>, Node> edge = ( Edge<View<?>, Node> ) newElement;
        final Node<View<?>, Edge> node = ( Node<View<?>, Edge> ) edge.getTargetNode();
        return new NodeBuildRequestImpl( x, y, node, edge, this.sourceMagnet, this.targetMagnet );
    }

    @Override
    protected void clearDragProxy() {
        super.clearDragProxy();
        // Enable layers' events handlers again.
        enableHandlers();

    }

    private void disableHandlers() {
        if ( null != this.layerEventHandlers ) {
            this.layerEventHandlers.disableHandlers();
        }
    }

    private void enableHandlers() {
        if ( null != this.layerEventHandlers ) {
            this.layerEventHandlers.enableHandlers();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.layerEventHandlers = null;
        this.nodeDragProxyFactory = null;
        this.nodeBuilderControl = null;
        this.definitionUtils = null;

    }

    protected String getDefinitionId( final Object def ) {
        return definitionUtils.getDefinitionManager().adapters().forDefinition().getId( def );
    }

}
