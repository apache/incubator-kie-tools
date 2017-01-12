/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;

public abstract class AbstractContainmentBasedControl<H extends CanvasHandler> implements CanvasControl<AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger( AbstractContainmentBasedControl.class.getName() );

    private CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;

    private AbstractCanvasHandler canvasHandler;

    public AbstractContainmentBasedControl( final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager ) {
        this.canvasCommandManager = canvasCommandManager;
    }

    protected abstract void doEnable( final WiresCanvas.View view );

    protected abstract void doDisable( final WiresCanvas.View view );

    protected abstract boolean isEdgeAccepted( final Edge edge );

    protected abstract Command<AbstractCanvasHandler, CanvasViolation> getAddEdgeCommand( final Node parent,
                                                                                          final Node child );

    protected abstract Command<AbstractCanvasHandler, CanvasViolation> getDeleteEdgeCommand( final Node parent,
                                                                                             final Node child );

    @Override
    public void enable( final AbstractCanvasHandler canvasHandler ) {
        this.canvasHandler = canvasHandler;
        final WiresCanvas.View canvasView = ( WiresCanvas.View ) canvasHandler.getCanvas().getView();
        doEnable( canvasView );
    }

    @Override
    public void disable() {
        if ( null != canvasHandler && null != canvasHandler.getCanvas() ) {
            final WiresCanvas.View canvasView = ( WiresCanvas.View ) canvasHandler.getCanvas().getView();
            doDisable( canvasView );
        }
        this.canvasHandler = null;
    }

    @SuppressWarnings( "unchecked" )
    public boolean allow( final Node parent,
                          final Node child ) {
        if ( parent == null && child == null ) {
            return false;
        }
        boolean isAllow = false;
        final Edge dockEdge = getTheEdge( child );
        final boolean isSameParent = isSameParent( parent,
                                                   dockEdge );
        if ( isSameParent ) {
            log( Level.FINE,
                 "Is same parent. isAllow=true" );
            isAllow = true;
        } else {
            final Command<AbstractCanvasHandler, CanvasViolation> command = getAddEdgeCommand( parent,
                                                                                               child );
            CommandResult<CanvasViolation> violations = canvasCommandManager.allow( canvasHandler,
                                                                                    command );
            isAllow = isAccept( violations );
            logResults( "isAllow",
                        command,
                        violations );
        }
        return isAllow;
    }

    @SuppressWarnings( "unchecked" )
    public boolean accept( final Node parent,
                           final Node child ) {
        if ( parent == null && child == null ) {
            return false;
        }
        final Edge dockEdge = getTheEdge( child );
        final boolean isSameParent = isSameParent( parent,
                                                   dockEdge );
        boolean isAccept = true;
        if ( !isSameParent ) {
            CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation> builder = null;
            // Remove current relationship.
            if ( null != dockEdge && null != dockEdge.getSourceNode() ) {
                builder = new CompositeCommandImpl
                        .CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>()
                        .reverse()
                        .addCommand( getDeleteEdgeCommand( dockEdge.getSourceNode(),
                                                           child ) );
            }
            // Add a new relationship.
            final Command<AbstractCanvasHandler, CanvasViolation> c = getAddEdgeCommand( parent,
                                                                                         child );
            final Command<AbstractCanvasHandler, CanvasViolation> command =
                    null == builder ?
                            c :
                            builder
                                    .addCommand( c )
                                    .build();
            final CommandResult<CanvasViolation> violations = canvasCommandManager.execute( canvasHandler,
                                                                                            command );
            isAccept = isAccept( violations );
            logResults( "isAccept",
                        command,
                        violations );
        } else {
            log( Level.FINE,
                 "isAccept = TRUE" );
        }
        return isAccept;
    }

    protected boolean isAccept( final WiresContainer wiresContainer,
                                final WiresShape wiresShape ) {
        if ( !isEnabled() || !isWiresShape( wiresContainer ) || !isWiresShape( wiresShape ) ) {
            return false;
        }
        return true;
    }

    protected AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    private boolean isEnabled() {
        return canvasHandler != null;
    }

    private boolean isSameParent( final Node parent,
                                  final Edge<Child, Node> edge ) {
        if ( null != edge ) {
            final Node sourceNode = edge.getSourceNode();
            if ( null != sourceNode ) {
                final String parentUUID = null != parent ? parent.getUUID() : canvasHandler.getDiagram().getMetadata().getCanvasRootUUID();
                return null != parentUUID && sourceNode.getUUID().equals( parentUUID );
            }
        }
        return parent == null;
    }

    @SuppressWarnings( "unchecked" )
    private Edge<Object, Node> getTheEdge( final Node child ) {
        if ( child != null ) {
            final List<Edge> outEdges = child.getInEdges();
            if ( null != outEdges && !outEdges.isEmpty() ) {
                for ( final Edge edge : outEdges ) {
                    if ( isEdgeAccepted( edge ) ) {
                        return edge;
                    }
                }
            }
        }
        return null;
    }

    private boolean isWiresShape( final WiresContainer wiresShape ) {
        return isWiresLayer( wiresShape ) ||
                ( null != wiresShape && null != wiresShape.getContainer().getUserData() &&
                        wiresShape.getContainer().getUserData().equals( WiresCanvas.WIRES_CANVAS_GROUP_ID ) );
    }

    private boolean isWiresLayer( final WiresContainer wiresShape ) {
        return null != wiresShape && wiresShape instanceof WiresLayer;
    }

    private boolean isAccept( final CommandResult<CanvasViolation> result ) {
        return !CommandUtils.isError( result );
    }

    private void logResults( final String prefix,
                             final Command<AbstractCanvasHandler, CanvasViolation> command,
                             final CommandResult<CanvasViolation> violations ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            final boolean isOk = isAccept( violations );
            if ( isOk ) {
                log( Level.FINE,
                     prefix + "= TRUE" );
            } else {
                log( Level.FINE,
                     prefix + "= FALSE " );
                log( Level.FINE,
                     "*************** Command = { " + command.toString() + " } " );
                log( Level.FINE,
                     "*************** Violations = { " + violations.getMessage() + " } " );
            }
        }
    }

    private void log( final Level level,
                      final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level,
                        message );
        }
    }
}
