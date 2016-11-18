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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.resize;

import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Element;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
// TODO: Handler registrations, update model, resize toolbox, etc

@Dependent
public class ResizeControlImpl extends AbstractCanvasHandlerRegistrationControl implements ResizeControl<AbstractCanvasHandler, Element> {

    private static Logger LOGGER = Logger.getLogger( ResizeControlImpl.class.getName() );

    private final CanvasCommandFactory canvasCommandFactory;
    private final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;

    protected ResizeControlImpl() {
        this( null, null );
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
        if ( registerCPHandlers( shape.getShapeView() ) ) {
            registerResizeHandlers( element, shape );
        }
    }

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
                    LOGGER.log( Level.FINE, "Shape [" + element.getUUID() + "] resized to size {"
                            + event.getWidth() + ", " + event.getHeight() + "]" );
                    // TODO: Update the model when resize is done.
                    /*CommandResult<CanvasViolation> result =
                            canvasCommandManager.execute( canvasHandler,
                                    canvasCommandFactory.UPDATE_PROPERTY( element, );
                    if ( CommandUtils.isError( result ) ) {
                        // TODO: DragContext#reset & show error somewhere.
                    }*/
                }

            };
            hasEventHandlers.addHandler( ViewEventType.RESIZE, resizeHandler );
            registerHandler( element.getUUID(), resizeHandler );
        }
    }

    /**
     * This method shows the shape's control points on when clicking on it.
     * TODO: Move this code to some view class or make the conrol points stuff more generic for shape views.
     */
    private boolean registerCPHandlers( final ShapeView<?> shapeView ) {
        if ( shapeView instanceof WiresShape ) {
            final WiresShape wiresShape = ( WiresShape ) shapeView;
            // Enable resize controls on chick + shift down.
            wiresShape
                    .setResizable( true )
                    .getGroup()
                    .addNodeMouseClickHandler( event -> {
                        final IControlHandleList controlHandles = wiresShape.loadControls( IControlHandle.ControlHandleStandardType.RESIZE );
                        if ( null != controlHandles ) {
                            if ( event.isShiftKeyDown() && !controlHandles.isVisible() ) {
                                controlHandles.show();
                            } else {
                                controlHandles.hide();
                            }
                        }
                    } );
            return true;
        }
        return false;
    }

}
