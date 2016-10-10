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

package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import com.google.gwt.user.client.Window;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServices;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationViolation;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidatorCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class CanvasSaveControlImpl
        extends AbstractCanvasHandlerControl
        implements CanvasSaveControl<AbstractCanvasHandler> {

    CanvasValidationControl<AbstractCanvasHandler> validationControl;
    ClientDiagramServices clientDiagramServices;
    GraphBoundsIndexer graphBoundsIndexer;

    @Inject
    public CanvasSaveControlImpl( final CanvasValidationControl<AbstractCanvasHandler> validationControl,
                                  final ClientDiagramServices clientDiagramServices,
                                  final GraphBoundsIndexer graphBoundsIndexer ) {
        this.validationControl = validationControl;
        this.clientDiagramServices = clientDiagramServices;
        this.graphBoundsIndexer = graphBoundsIndexer;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void enable( final AbstractCanvasHandler canvasHandler ) {
        super.enable( canvasHandler );
        validationControl.enable( canvasHandler );
    }

    @Override
    protected void doDisable() {
        this.validationControl.disable();
        this.validationControl = null;
        this.clientDiagramServices = null;
        this.graphBoundsIndexer = null;

    }

    @Override
    public void save() {
        this.save( null );

    }

    @Override
    public void save( final CanvasValidatorCallback validatorCallback ) {
        // Perform validation before saving.
        validationControl.validate( new CanvasValidatorCallback() {

            @Override
            public void onSuccess() {
                doSave( new ServiceCallback<Diagram>() {
                    @Override
                    public void onSuccess( final Diagram item ) {
                        // TODO: Throw event and refactor by the use of Notifications widget.
                        Window.alert( "Diagram saved successfully [UUID=" + item.getUUID() + "]" );
                        if ( null != validatorCallback ) {
                            validatorCallback.onSuccess();

                        }

                    }

                    @Override
                    public void onError( ClientRuntimeError error ) {
                        // TODO
                        validatorCallback.onFail( null );

                    }
                } );

            }

            @Override
            public void onFail( final Iterable<CanvasValidationViolation> violations ) {
                validatorCallback.onFail( violations );
            }

        } );

    }

    @SuppressWarnings( "unchecked" )
    private <T> void doSave( final ServiceCallback<Diagram> diagramServiceCallback ) {
        final Diagram diagram = canvasHandler.getDiagram();
        final Graph graph = diagram.getGraph();
        final double[] dBounds = graphBoundsIndexer
                .build( graph )
                .getTrimmedBounds();
        String thumbData = null;
        if ( dBounds[ 2 ] > 0 && dBounds[ 3 ] > 0 ) {
            final Layer layer = canvasHandler.getCanvas().getLayer();
            if ( null != layer ) {
                thumbData =
                        layer
                                .toDataURL( ( int ) dBounds[ 0 ],
                                        ( int ) dBounds[ 1 ],
                                        ( int ) dBounds[ 2 ],
                                        ( int ) dBounds[ 3 ] );

            }

        }
        // Update diagram's image data as thumbnail.
        diagram.getSettings().setThumbData( thumbData );
        // Perform update operation remote call.
        clientDiagramServices.update( diagram, diagramServiceCallback );

    }

}
