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
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.EdgeBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.EdgeBuildRequestImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.components.drag.ConnectorDragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class NewConnectorCommand<I> extends AbstractElementBuilderCommand<I> {

    private final ConnectorDragProxy<AbstractCanvasHandler> connectorDragProxyFactory;
    private final EdgeBuilderControl<AbstractCanvasHandler> edgeBuilderControl;

    private String edgeId;

    protected NewConnectorCommand() {
        this( null, null, null, null, null, null );
    }

    @Inject
    public NewConnectorCommand( final ClientFactoryService clientFactoryServices,
                                final ShapeManager shapeManager,
                                final DefinitionGlyphTooltip<?> glyphTooltip,
                                final GraphBoundsIndexer graphBoundsIndexer,
                                final ConnectorDragProxy<AbstractCanvasHandler> connectorDragProxyFactory,
                                final EdgeBuilderControl<AbstractCanvasHandler> edgeBuilderControl ) {
        super( clientFactoryServices, shapeManager, glyphTooltip, graphBoundsIndexer );
        this.connectorDragProxyFactory = connectorDragProxyFactory;
        this.edgeBuilderControl = edgeBuilderControl;

    }

    @PostConstruct
    public void init() {
        getGlyphTooltip().setPrefix( "Create a new " );
    }

    public void setEdgeIdentifier( final String edgeId ) {
        this.edgeId = edgeId;
    }

    @Override
    protected String getDefinitionIdentifier( final Context<AbstractCanvasHandler> context ) {
        return edgeId;
    }

    @Override
    protected String getGlyphDefinitionId() {
        return edgeId;
    }

    // TODO: I18n.
    @Override
    public String getTitle() {
        return "Creates a new connector";
    }

    @Override
    public void click( Context<AbstractCanvasHandler> context, Element element ) {
        super.click( context, element );
        // Same behavior as when mouse down, so use of the drag handler.
        this.mouseDown( context, element );
    }

    @Override
    protected DragProxy getDragProxyFactory() {
        return connectorDragProxyFactory;
    }

    @Override
    protected BuilderControl getBuilderControl() {
        return edgeBuilderControl;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Object createtBuilderControlItem( final Context<AbstractCanvasHandler> context,
                                                final Element source,
                                                final Element newElement ) {
        final Node<View<?>, Edge> sourceNode = ( Node<View<?>, Edge> ) source;
        final Edge<View<?>, Node> edge = ( Edge<View<?>, Node> ) newElement;
        final ShapeFactory<?, ?, ?> edgeFactory = getShapeManager().getFactory( edgeId );
        return new ConnectorDragProxy.Item() {
            @Override
            public Edge<View<?>, Node> getEdge() {
                return edge;
            }

            @Override
            public Node<View<?>, Edge> getSourceNode() {
                return sourceNode;
            }

            @Override
            public ShapeFactory getShapeFactory() {
                return edgeFactory;
            }
        };

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected boolean onDragProxyMove( final int x,
                                       final int y,
                                       final Element source,
                                       final Element newElement,
                                       final Node targetNode ) {
        final Node<View<?>, Edge> sourceNode = ( Node<View<?>, Edge> ) source;
        final Edge<View<?>, Node> edge = ( Edge<View<?>, Node> ) newElement;
        if ( null != targetNode ) {
            final EdgeBuildRequest request = new EdgeBuildRequestImpl( x, y, edge, sourceNode, targetNode );
            return edgeBuilderControl.allows( request );

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
        final Node<View<?>, Edge> sourceNode = ( Node<View<?>, Edge> ) source;
        final Edge<View<?>, Node> edge = ( Edge<View<?>, Node> ) newElement;
        return new EdgeBuildRequestImpl( x, y, edge, sourceNode, targetNode );
    }

    @Override
    public void destroy() {
        super.destroy();

    }

}
