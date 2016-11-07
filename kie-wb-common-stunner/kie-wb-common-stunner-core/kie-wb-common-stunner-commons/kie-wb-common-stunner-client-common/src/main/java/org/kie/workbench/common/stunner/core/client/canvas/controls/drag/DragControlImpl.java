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

package org.kie.workbench.common.stunner.core.client.canvas.controls.drag;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Element;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class DragControlImpl extends AbstractCanvasHandlerRegistrationControl
        implements DragControl<AbstractCanvasHandler, Element> {

    CanvasCommandFactory canvasCommandFactory;
    CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;

    @Inject
    public DragControlImpl( final CanvasCommandFactory canvasCommandFactory,
                            final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager ) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.canvasCommandManager = canvasCommandManager;
    }

    @Override
    public void register( final Element element ) {
        final Shape shape = canvasHandler.getCanvas().getShape( element.getUUID() );
        if ( shape.getShapeView() instanceof HasEventHandlers ) {
            final HasEventHandlers hasEventHandlers = ( HasEventHandlers ) shape.getShapeView();
            DragHandler handler = new DragHandler() {

                @Override
                public void handle( final DragEvent event ) {
                }

                @Override
                public void start( final DragEvent event ) {
                }

                @Override
                public void end( final DragEvent event ) {
                    final double[] xy = getContainerXY( shape );
                    CommandResult<CanvasViolation> result = canvasCommandManager
                            .execute( canvasHandler, canvasCommandFactory
                                    .UPDATE_POSITION( element, xy[ 0 ], xy[ 1 ] ) );
                    if ( CommandUtils.isError( result ) ) {
                        // TODO: DragContext#reset
                    }
                }
            };
            hasEventHandlers.addHandler( ViewEventType.DRAG, handler );
            registerHandler( element.getUUID(), handler );

        }

    }

    protected double[] getContainerXY( final Shape shape ) {
        return new double[]{ shape.getShapeView().getShapeX(),
                shape.getShapeView().getShapeY() };
    }

}
