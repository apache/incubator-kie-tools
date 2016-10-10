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

package org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl;

import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequestImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.NodeBuildRequest;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.util.EdgeMagnetsHelper;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandUtils;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

@Dependent
public class NodeBuilderControlImpl extends AbstractCanvasHandlerControl implements NodeBuilderControl<AbstractCanvasHandler> {

    ClientDefinitionManager clientDefinitionManager;
    ShapeManager shapeManager;
    CanvasCommandFactory commandFactory;
    CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    ElementBuilderControl<AbstractCanvasHandler> elementBuilderControl;
    EdgeMagnetsHelper magnetsHelper;

    @Inject
    public NodeBuilderControlImpl( final ClientDefinitionManager clientDefinitionManager,
                                   final ShapeManager shapeManager,
                                   final CanvasCommandFactory commandFactory,
                                   final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                   final @Element ElementBuilderControl<AbstractCanvasHandler> elementBuilderControl,
                                   final EdgeMagnetsHelper magnetsHelper ) {
        this.clientDefinitionManager = clientDefinitionManager;
        this.shapeManager = shapeManager;
        this.commandFactory = commandFactory;
        this.canvasCommandManager = canvasCommandManager;
        this.elementBuilderControl = elementBuilderControl;
        this.magnetsHelper = magnetsHelper;
    }

    @Override
    public void enable( final AbstractCanvasHandler canvasHandler ) {
        super.enable( canvasHandler );
        this.elementBuilderControl.enable( canvasHandler );
    }

    @Override
    protected void doDisable() {
        this.elementBuilderControl.disable();
    }

    @Override
    public boolean allows( final NodeBuildRequest request ) {
        final double x = request.getX();
        final double y = request.getY();
        final Node<View<?>, Edge> node = request.getNode();
        if ( null != node ) {
            final ElementBuildRequest<AbstractCanvasHandler> request1 =
                    new ElementBuildRequestImpl( x, y, node.getContent().getDefinition(), null );
            return elementBuilderControl.allows( request1 );

        }
        return false;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void build( final NodeBuildRequest request,
                       final BuildCallback buildCallback ) {
        final double x = request.getX();
        final double y = request.getY();
        final Node<View<?>, Edge> node = request.getNode();
        final Edge<View<?>, Node> inEdge = request.getInEdge();
        final int sourceManget = request.getSourceManger();
        final int targetMagnet = request.getTargetMagnet();
        if ( null != node ) {
            final Object nodeDef = node.getContent().getDefinition();
            final String nodeId = clientDefinitionManager.adapters().forDefinition().getId( nodeDef );
            final ElementBuilderControlImpl ebc = getElementBuilderControl();
            final Node<View<?>, Edge> parent = ebc.getParent( x, y );
            final Double[] childCoordinates = ebc.getChildCoordinates( parent, x, y );
            final ShapeFactory<Object, AbstractCanvasHandler, ?> nodeShapeFactory = shapeManager.getFactory( nodeId );
            final List<Command<AbstractCanvasHandler, CanvasViolation>> commandList = new LinkedList<>();
            ebc.getElementCommands( node, parent, nodeShapeFactory, childCoordinates[ 0 ], childCoordinates[ 1 ], new AbstractElementBuilderControl.CommandsCallback() {
                @Override
                public void onComplete( final String uuid,
                                        final List<Command<AbstractCanvasHandler, CanvasViolation>> commands ) {
                    commandList.addAll( commands );
                    if ( inEdge != null ) {
                        final Object edgeDef = inEdge.getContent().getDefinition();
                        final String edgeId = clientDefinitionManager.adapters().forDefinition().getId( edgeDef );
                        final ShapeFactory<?, ?, ?> edgeFactory = shapeManager.getFactory( edgeId );
                        // The commands to batch for the edge that connects both nodes.
                        commandList.add( commandFactory.ADD_EDGE( inEdge.getSourceNode(), inEdge, edgeFactory ) );
                        commandList.add( commandFactory.SET_SOURCE_NODE( inEdge.getSourceNode(), inEdge, sourceManget ) );
                        commandList.add( commandFactory.SET_TARGET_NODE( node, inEdge, targetMagnet ) );

                    }
                    for ( final Command<AbstractCanvasHandler, CanvasViolation> command : commandList ) {
                        canvasCommandManager.batch( command );

                    }
                    BatchCommandResult<CanvasViolation> results = canvasCommandManager.executeBatch( canvasHandler );
                    if ( !CommandUtils.isError( results ) ) {
                        updateConnectorShape( inEdge, node, sourceManget, targetMagnet );

                    }
                    buildCallback.onSuccess( uuid );

                }

                @Override
                public void onError( final ClientRuntimeError error ) {
                    buildCallback.onError( error );

                }

            } );

        }

    }

    @SuppressWarnings( "unchecked" )
    protected void updateConnectorShape( final Edge<View<?>, Node> inEdge,
                                         final Node targetNode,
                                         final int sourceMagnet,
                                         final int targetManget ) {
        final ViewConnector connectorContent = ( ViewConnector ) inEdge.getContent();
        canvasHandler.applyElementMutation( inEdge, MutationContext.STATIC );
        final EdgeShape edgeShape = ( EdgeShape ) canvasHandler.getCanvas().getShape( inEdge.getUUID() );
        final Node source = inEdge.getSourceNode();
        if ( null != source && null != targetNode ) {
            final Shape<?> sShape = canvasHandler.getCanvas().getShape( source.getUUID() );
            final Shape<?> tShape = canvasHandler.getCanvas().getShape( targetNode.getUUID() );
            connectorContent.setSourceMagnetIndex( sourceMagnet );
            connectorContent.setTargetMagnetIndex( targetManget );
            edgeShape.applyConnections( inEdge, sShape.getShapeView(), tShape.getShapeView(), MutationContext.STATIC );

        }

    }

    protected ElementBuilderControlImpl getElementBuilderControl() {
        return ( ElementBuilderControlImpl ) elementBuilderControl;
    }

}
