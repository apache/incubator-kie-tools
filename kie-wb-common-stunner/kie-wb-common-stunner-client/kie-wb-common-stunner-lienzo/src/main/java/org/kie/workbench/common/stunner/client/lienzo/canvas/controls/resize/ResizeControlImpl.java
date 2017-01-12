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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.resize;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Point2D;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CommandResultImpl;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@Dependent
public class ResizeControlImpl extends AbstractCanvasHandlerRegistrationControl implements ResizeControl<AbstractCanvasHandler, Element> {

    private static Logger LOGGER = Logger.getLogger( ResizeControlImpl.class.getName() );

    private final CanvasCommandFactory canvasCommandFactory;
    private final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;

    protected ResizeControlImpl() {
        this( null,
              null );
    }

    @Inject
    public ResizeControlImpl( final CanvasCommandFactory canvasCommandFactory,
                              final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager ) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.canvasCommandManager = canvasCommandManager;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void register( final Element element ) {
        final AbstractCanvas<?> canvas = canvasHandler.getCanvas();
        final Shape<?> shape = canvas.getShape( element.getUUID() );
        if ( supportsResize( shape ) ) {
            registerCPHandlers( element,
                                shape.getShapeView() );
            registerResizeHandlers( element,
                                    shape );
        }
    }

    @Override
    public CommandResult<CanvasViolation> resize( final Element element,
                                                  final double width,
                                                  final double height ) {
        return doResize( element,
                         null,
                         null,
                         width,
                         height );
    }

    @Override
    public CommandResult<CanvasViolation> resize( final Element element,
                                                  final double x,
                                                  final double y,
                                                  final double width,
                                                  final double height ) {
        return doResize( element,
                         x,
                         y,
                         width,
                         height );
    }

    /**
     * To enable the resize control next bullets must be met:
     * - shape view must support resize event - for capturing user resize events
     * - shape view must support mouse click event - for enabling resize control points on mouse click
     * - shape view must support control points as well
     */
    private boolean supportsResize( final Shape<?> shape ) {
        final ShapeView<?> view = shape.getShapeView();
        final boolean supportsResize = ( view instanceof HasEventHandlers ) &&
                ( ( ( HasEventHandlers ) view ).supports( ViewEventType.RESIZE ) ) &&
                ( ( ( HasEventHandlers ) view ).supports( ViewEventType.MOUSE_CLICK ) );
        final boolean supportsCtrlPoints = ( view instanceof HasControlPoints );
        return supportsResize && supportsCtrlPoints;
    }

    /**
     * In order to show the shape's control points on mouse click + shift key down.
     */
    private void registerCPHandlers( final Element element,
                                     final ShapeView<?> shapeView ) {
        final HasEventHandlers hasEventHandlers = ( HasEventHandlers ) shapeView;
        final HasControlPoints hasControlPoints = ( HasControlPoints ) shapeView;
        if ( hasEventHandlers.supports( ViewEventType.MOUSE_CLICK ) ) {
            final MouseClickHandler clickHandler = new MouseClickHandler() {
                @Override
                public void handle( final MouseClickEvent event ) {
                    if ( event.isShiftKeyDown() && !hasControlPoints.areControlsVisible() ) {
                        hasControlPoints.showControlPoints( HasControlPoints.ControlPointType.RESIZE );
                    } else {
                        hasControlPoints.hideControlPoints();
                    }
                    canvasHandler.getCanvas().getLayer().draw();
                }
            };
            hasEventHandlers.addHandler( ViewEventType.MOUSE_CLICK,
                                         clickHandler );
            registerHandler( element.getUUID(),
                             clickHandler );
        }
    }

    @SuppressWarnings( "unchecked" )
    private void registerResizeHandlers( final Element element,
                                         final Shape<?> shape ) {
        if ( shape.getShapeView() instanceof HasEventHandlers ) {
            final HasEventHandlers hasEventHandlers = ( HasEventHandlers ) shape.getShapeView();
            final ResizeHandler resizeHandler = new ResizeHandler() {
                @Override
                public void start( final ResizeEvent event ) {
                }

                @Override
                public void handle( final ResizeEvent event ) {
                }

                @Override
                public void end( final ResizeEvent event ) {
                    LOGGER.log( Level.FINE,
                                "Shape [" + element.getUUID() + "] resized to size {"
                                        + event.getWidth() + ", " + event.getHeight() + "] " +
                                        "& Coordinates [" + event.getX() + ", " + event.getY() + "]" );
                    final Shape shape = canvasHandler.getCanvas().getShape( element.getUUID() );
                    final double x = shape.getShapeView().getShapeX();
                    final double y = shape.getShapeView().getShapeY();
                    final CommandResult<CanvasViolation> result =
                            doResize( element,
                                      shape,
                                      x + event.getX(),
                                      y + event.getY(),
                                      event.getWidth(),
                                      event.getHeight() );
                    if ( CommandUtils.isError( result ) ) {
                        LOGGER.log( Level.WARNING,
                                    "Command failed at resize end [result=" + result + "]" );
                    }
                }
            };
            hasEventHandlers.addHandler( ViewEventType.RESIZE,
                                         resizeHandler );
            registerHandler( element.getUUID(),
                             resizeHandler );
        }
    }

    private CommandResult<CanvasViolation> doResize( final Element<? extends View<?>> element,
                                                     final Double x,
                                                     final Double y,
                                                     final double w,
                                                     final double h ) {
        final Shape shape = canvasHandler.getCanvas().getShape( element.getUUID() );
        return doResize( element,
                         shape,
                         x,
                         y,
                         w,
                         h );
    }

    @SuppressWarnings( "unchecked" )
    private CommandResult<CanvasViolation> doResize( final Element<? extends View<?>> element,
                                                     final Shape shape,
                                                     final Double x,
                                                     final Double y,
                                                     final double w,
                                                     final double h ) {
        // Calculate the new graph element's bounds.
        final Point2D current = ( null != x && null != y ) ? new Point2D( x,
                                                                          y ) : GraphUtils.getPosition( element.getContent() );
        final BoundsImpl newBounds = new BoundsImpl(
                new BoundImpl( current.getX(),
                               current.getY() ),
                new BoundImpl( current.getX() + w,
                               current.getY() + h )
        );
        // Check the new bound values that come from the user's action do not exceed graph ones.
        if ( !GraphUtils.checkBounds( canvasHandler.getDiagram().getGraph(),
                                      newBounds ) ) {
            return new CommandResultImpl<>(
                    CommandResult.Type.ERROR,
                    "Bounds exceeded",
                    new LinkedList<>()
            );
        }
        // Execute the update position and update property/ies command/s on the bean instance to achieve the new bounds.
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commands = getResizeCommands( element,
                                                                                                  shape,
                                                                                                  w,
                                                                                                  h );
        final CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>();
        if ( null != commands ) {
            if ( null != x && null != y ) {
                commandBuilder
                        .addCommand( canvasCommandFactory.updatePosition( ( Node<View<?>, Edge> ) element,
                                                                          x,
                                                                          y ) );
            }
            commands.stream().forEach( commandBuilder::addCommand );
        }
        final CommandResult<CanvasViolation> resizeResults = canvasCommandManager.execute( canvasHandler,
                                                                                           commandBuilder.build() );
        // Update the view bounds on the node content after successful resize.
        if ( !CommandUtils.isError( resizeResults ) ) {
            element.getContent().setBounds( newBounds );
        }
        return resizeResults;
    }

    /**
     * It provides the necessary canvas commands in order to update the domain model with new values that will met
     * the new bounding box size.
     * It always updates the element's position, as resize can update it, and it updates as well some of the bean's properties.
     */
    private List<Command<AbstractCanvasHandler, CanvasViolation>> getResizeCommands( final Element<? extends Definition<?>> element,
                                                                                     final Shape shape,
                                                                                     final double w,
                                                                                     final double h ) {
        final Definition content = ( Definition ) element.getContent();
        final Object def = content.getDefinition();
        final DefinitionAdapter<Object> adapter = canvasHandler
                .getClientDefinitionManager()
                .adapters()
                .registry()
                .getDefinitionAdapter( def.getClass() );
        final ShapeView<?> shapeView = shape.getShapeView();
        final List<Command<AbstractCanvasHandler, CanvasViolation>> result = new LinkedList<>();
        if ( shapeView instanceof HasSize ) {
            final Object width = adapter.getMetaProperty( PropertyMetaTypes.WIDTH,
                                                          def );
            final Object height = adapter.getMetaProperty( PropertyMetaTypes.HEIGHT,
                                                           def );
            final String wId = null != width ? canvasHandler.getClientDefinitionManager().adapters().forProperty().getId( width ) : null;
            final String hId = null != width ? canvasHandler.getClientDefinitionManager().adapters().forProperty().getId( height ) : null;
            if ( null != wId && null != hId ) {
                result.add( canvasCommandFactory.updatePropertyValue( element,
                                                                      wId,
                                                                      w ) );
                result.add( canvasCommandFactory.updatePropertyValue( element,
                                                                      hId,
                                                                      h ) );
            } else {
                LOGGER.log( Level.WARNING,
                            "Not width/height properties found for element [" + element.getUUID()
                                    + "], but the shape for it supports size." );
            }
        } else if ( shapeView instanceof HasRadius ) {
            final Object radius = adapter.getMetaProperty( PropertyMetaTypes.RADIUS,
                                                           def );
            final String rId = null != radius ? canvasHandler.getClientDefinitionManager().adapters().forProperty().getId( radius ) : null;
            if ( null != rId ) {
                final double r = w > h ? ( h / 2 ) : ( w / 2 );
                result.add( canvasCommandFactory.updatePropertyValue( element,
                                                                      rId,
                                                                      r ) );
            } else {
                LOGGER.log( Level.WARNING,
                            "Not radius property found for element [" + element.getUUID()
                                    + "], but the shape for it supports radius." );
            }
        }
        return result;
    }
}
