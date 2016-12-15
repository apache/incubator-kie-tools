/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.controls.drag;

import org.kie.workbench.common.stunner.core.client.canvas.*;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class DragControlImpl extends AbstractCanvasHandlerRegistrationControl
        implements DragControl<AbstractCanvasHandler, Element> {

    private static Logger LOGGER = Logger.getLogger( DragControlImpl.class.getName() );

    private final CanvasCommandFactory canvasCommandFactory;
    private final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    private CanvasGrid dragGrid;

    @Inject
    public DragControlImpl( final CanvasCommandFactory canvasCommandFactory,
                            final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager ) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.canvasCommandManager = canvasCommandManager;
    }

    @Override
    public DragControl<AbstractCanvasHandler, Element> setDragGrid( final CanvasGrid grid ) {
        this.dragGrid = grid;
        return this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void register( final Element element ) {
        final AbstractCanvas<?> canvas = canvasHandler.getCanvas();
        final Shape<?> shape = canvas.getShape( element.getUUID() );
        if ( shape.getShapeView() instanceof HasEventHandlers ) {
            final HasEventHandlers hasEventHandlers = ( HasEventHandlers ) shape.getShapeView();
            final DragHandler handler = new DragHandler() {

                private final double[] shapeSize = new double[] { 0, 0 };
                private CanvasGrid grid = null;

                @Override
                public void start( final DragEvent event ) {
                    final Double[] size = GraphUtils.getSize( ( View ) element.getContent() );
                    shapeSize[0] = size[0];
                    shapeSize[1] = size[1];
                    if ( isDragGridEnabled() ) {
                        this.grid = canvas.getGrid();
                        if ( null == grid ) {
                            canvas.setGrid( dragGrid );
                        }
                    }
                }

                @Override
                public void handle( final DragEvent event ) {
                    ensureDragConstrains( shape.getShapeView(), shapeSize );
                }

                @Override
                public void end( final DragEvent event ) {
                    final double x = shape.getShapeView().getShapeX();
                    final double y = shape.getShapeView().getShapeY();
                    CommandResult<CanvasViolation> result = canvasCommandManager
                            .execute( canvasHandler, canvasCommandFactory
                                    .UPDATE_POSITION( ( Node<View<?>, Edge> ) element, x, y ) );
                    if ( CommandUtils.isError( result ) ) {
                        // TODO: DragContext#reset
                    }
                    if ( isDragGridEnabled() ) {
                        canvas.setGrid( this.grid );
                        this.grid = null;
                    }
                }
            };
            hasEventHandlers.addHandler( ViewEventType.DRAG, handler );
            registerHandler( element.getUUID(), handler );

        }

    }

    /**
     * Setting dragBounds for the shape doesn't work on lienzo side, so
     * ensure drag does not exceed the canvas bounds.
     * @param shapeView The shape view instance being drag.
     */
    private void ensureDragConstrains( final ShapeView<?> shapeView,
                                       final double[] shapeSize ) {
        final int mw = canvasHandler.getCanvas().getWidth();
        final int mh = canvasHandler.getCanvas().getHeight();
        final Point2D sa = shapeView.getShapeAbsoluteLocation();
        LOGGER.log( Level.FINE, "Ensuring drag constraints for absolute coordinates at [" + sa.getX() + ", " + sa.getY() + "]" );
        final double ax = mw - shapeSize[0];
        final double ay = mh - shapeSize[1];
        final boolean xb = sa.getX() >= ax || sa.getX() < 0;
        final boolean yb = sa.getY() >= ay || sa.getY() < 0;
        if ( xb || yb ) {
            final double tx = sa.getX() >= ax ? ax : ( sa.getX() < 0 ? 0 : sa.getX() );
            final double ty = sa.getY() >= ay ? ay : ( sa.getY() < 0 ? 0 : sa.getY() );
            LOGGER.log( Level.FINE, "Setting constraint coordinates at [" + tx + ", " + ty + "]" );
            shapeView.setShapeX( tx );
            shapeView.setShapeY( ty );
        }
    }

    private boolean isDragGridEnabled() {
        return null != dragGrid;
    }

}
