/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.util;

import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.session.ClientReadOnlySession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class ClientSessionUtils {

    private GraphBoundsIndexer graphBoundsIndexer;

    protected ClientSessionUtils() {
        this( null );
    }

    @Inject
    public ClientSessionUtils( final GraphBoundsIndexer graphBoundsIndexer ) {
        this.graphBoundsIndexer = graphBoundsIndexer;
    }

    @SuppressWarnings( "unchecked" )
    public String canvasToImageData( final ClientSession session ) {
        if ( session instanceof ClientReadOnlySession ) {
            final SelectionControl<CanvasHandler<?, Canvas<?>>, Element> selectionControl =
                    ( ( ClientReadOnlySession ) session ).getSelectionControl();
            if ( null != selectionControl ) {
                // Ensure no selection present before creating the image data for the canvas.
                selectionControl.clearSelection();
            }
        }
        final CanvasHandler<?, Canvas<?>> canvasHandler = session.getCanvasHandler();
        return canvasToImageData( canvasHandler );
    }

    @SuppressWarnings( "unchecked" )
    private String canvasToImageData( final CanvasHandler<?, Canvas<?>> canvasHandler ) {
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
        return thumbData;
    }

}
