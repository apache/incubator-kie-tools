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
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.EdgeBuildRequest;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.util.EdgeMagnetsHelper;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.CommandUtils;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class EdgeBuilderControlImpl extends AbstractCanvasHandlerControl implements EdgeBuilderControl<AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger( EdgeBuilderControlImpl.class.getName() );

    private final ClientDefinitionManager clientDefinitionManager;
    private final ShapeManager shapeManager;
    private final CanvasCommandFactory commandFactory;
    private final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    private final EdgeMagnetsHelper magnetsHelper;

    protected EdgeBuilderControlImpl() {
        this( null, null, null, null, null );
    }

    @Inject
    public EdgeBuilderControlImpl( final ClientDefinitionManager clientDefinitionManager,
                                   final ShapeManager shapeManager,
                                   final CanvasCommandFactory commandFactory,
                                   final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                   final EdgeMagnetsHelper magnetsHelper ) {
        this.clientDefinitionManager = clientDefinitionManager;
        this.shapeManager = shapeManager;
        this.commandFactory = commandFactory;
        this.canvasCommandManager = canvasCommandManager;
        this.magnetsHelper = magnetsHelper;
    }

    @Override
    public boolean allows( final EdgeBuildRequest request ) {
        final double x = request.getX();
        final double y = request.getY();
        final Edge<View<?>, Node> edge = request.getEdge();
        final AbstractCanvasHandler<?, ?> wch = canvasHandler;
        final Node<View<?>, Edge> inNode = request.getInNode();
        final Node<View<?>, Edge> outNode = request.getOutNode();
        boolean allowsSourceConn = true;
        if ( null != inNode ) {
            final CommandResult<CanvasViolation> cr1 = canvasCommandManager.allow( wch, commandFactory.SET_SOURCE_NODE( ( Node<? extends View<?>, Edge> ) inNode, edge, 0 ) );
            allowsSourceConn = isAllowed( cr1 );
        }
        boolean allowsTargetConn = true;
        if ( null != outNode ) {
            final CommandResult<CanvasViolation> cr2 = canvasCommandManager.allow( wch, commandFactory.SET_TARGET_NODE( ( Node<? extends View<?>, Edge> ) outNode, edge, 0 ) );
            allowsTargetConn = isAllowed( cr2 );
        }
        return allowsSourceConn & allowsTargetConn;

    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void build( final EdgeBuildRequest request,
                       final BuildCallback buildCallback ) {
        final double x = request.getX();
        final double y = request.getY();
        final Edge<View<?>, Node> edge = request.getEdge();
        final AbstractCanvasHandler<?, ?> wch = canvasHandler;
        final Node<View<?>, Edge> inNode = request.getInNode();
        final Node<View<?>, Edge> outNode = request.getOutNode();
        final Canvas canvas = canvasHandler.getCanvas();
        if ( null == inNode ) {
            throw new RuntimeException( " An edge must be into the outgoing edges list from a node." );

        }
        final Shape sourceShape = canvas.getShape( inNode.getUUID() );
        final Shape targetShape = outNode != null ? canvas.getShape( outNode.getUUID() ) : null;
        int[] magnetIndexes = new int[]{ 0, 0 };
        if ( outNode != null ) {
            magnetIndexes = magnetsHelper.getDefaultMagnetsIndex( sourceShape.getShapeView(),
                    targetShape.getShapeView() );

        }
        final Object edgeDef = edge.getContent().getDefinition();
        final String edgeDefId = clientDefinitionManager.adapters().forDefinition().getId( edgeDef );
        final ShapeFactory factory = shapeManager.getFactory( edgeDefId );
        canvasCommandManager
                .batch( commandFactory.ADD_EDGE( inNode, edge, factory ) )
                .batch( commandFactory.SET_SOURCE_NODE( ( Node<? extends View<?>, Edge> ) inNode, edge, magnetIndexes[ 0 ] ) );
        if ( null != outNode ) {
            canvasCommandManager.batch( commandFactory.SET_TARGET_NODE( ( Node<? extends View<?>, Edge> ) outNode, edge, magnetIndexes[ 1 ] ) );

        }
        final BatchCommandResult<CanvasViolation> results = canvasCommandManager.executeBatch( wch );
        if ( CommandUtils.isError( results ) ) {
            LOGGER.log( Level.SEVERE, results.toString() );

        }
        canvasHandler.applyElementMutation( edge, MutationContext.STATIC );
        buildCallback.onSuccess( edge.getUUID() );

    }

    @Override
    protected void doDisable() {
    }

    private boolean isAllowed( CommandResult<CanvasViolation> result ) {
        return !CommandResult.Type.ERROR.equals( result.getType() );
    }

}
