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

package org.kie.workbench.common.stunner.core.client.session.impl;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.session.ClientReadOnlySession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Element;

public abstract class AbstractClientReadOnlySession extends AbstractClientSession
        implements ClientReadOnlySession<AbstractCanvas, AbstractCanvasHandler> {

    private SelectionControl<AbstractCanvasHandler, Element> selectionControl;
    private ZoomControl<AbstractCanvas> zoomControl;
    private PanControl<AbstractCanvas> panControl;

    private CanvasShapeListener shapeListener;
    private CanvasElementListener elementListener;

    public AbstractClientReadOnlySession( final AbstractCanvas canvas,
                                          final AbstractCanvasHandler canvasHandler,
                                          final SelectionControl<AbstractCanvasHandler, Element> selectionControl,
                                          final ZoomControl<AbstractCanvas> zoomControl,
                                          final PanControl<AbstractCanvas> panControl ) {
        super( canvas,
               canvasHandler );
        this.selectionControl = selectionControl;
        this.zoomControl = zoomControl;
        this.panControl = panControl;
    }

    @Override
    public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
        return selectionControl;
    }

    @Override
    public ZoomControl<AbstractCanvas> getZoomControl() {
        return zoomControl;
    }

    @Override
    public PanControl<AbstractCanvas> getPanControl() {
        return panControl;
    }

    @Override
    protected void doOpen() {
        initializeListeners();
        enableControls();
    }

    @Override
    public void doDispose() {
        removeListeners();
        disableControls();
    }

    @Override
    protected void doPause() {
        // TODO: Performance improvements: Disable controls here ( all handlers etc will get disabled ).
    }

    @Override
    protected void doResume() {
        // TODO: Performance improvements: Re-enable controls here.
    }

    private void initializeListeners() {
        // Canvas listeners.
        final AbstractCanvas canvas = getCanvasHandler().getCanvas();
        this.shapeListener = new CanvasShapeListener() {

            @Override
            public void register( final Shape item ) {
                onRegisterShape( item );
            }

            @Override
            public void deregister( final Shape item ) {
                onDeregisterShape( item );
            }

            @Override
            public void clear() {
                onClear();
            }
        };
        canvas.addRegistrationListener( shapeListener );
        // Canvas handler listeners.
        this.elementListener = new CanvasElementListener() {

            @Override
            public void update( final Element item ) {
                onElementRegistration( item,
                                       false,
                                       true );
            }

            @Override
            public void register( final Element item ) {
                onRegisterElement( item );
            }

            @Override
            public void deregister( final Element item ) {
                onDeregisterElement( item );
            }

            @Override
            public void clear() {
                onClear();
            }
        };
        getCanvasHandler().addRegistrationListener( elementListener );
    }

    private void removeListeners() {
        if ( null != shapeListener ) {
            getCanvas().removeRegistrationListener( shapeListener );
        }
        if ( null != elementListener ) {
            getCanvasHandler().removeRegistrationListener( elementListener );
        }
    }

    private void onRegisterShape( final Shape shape ) {
        onShapeRegistration( shape,
                             true );
    }

    private void onDeregisterShape( final Shape shape ) {
        onShapeRegistration( shape,
                             false );
    }

    private void onRegisterElement( final Element element ) {
        onElementRegistration( element,
                               true,
                               false );
    }

    private void onDeregisterElement( final Element element ) {
        onElementRegistration( element,
                               false,
                               false );
    }

    protected void onElementRegistration( final Element element,
                                          final boolean add,
                                          final boolean update ) {
        if ( update ) {
            fireRegistrationUpdateListeners( getSelectionControl(),
                                             element );
        } else {
            fireRegistrationListeners( getSelectionControl(),
                                       element,
                                       add );
        }
    }

    protected void onShapeRegistration( final Shape shape,
                                        final boolean add ) {
        fireRegistrationListeners( getZoomControl(),
                                   shape,
                                   add );
        fireRegistrationListeners( getPanControl(),
                                   shape,
                                   add );
    }

    protected void onClear() {
        fireRegistrationClearListeners( getSelectionControl() );
    }

    protected void enableControls() {
        enableControl( getSelectionControl(),
                       getCanvasHandler() );
        enableControl( getZoomControl(),
                       getCanvas() );
        enableControl( getPanControl(),
                       getCanvas() );
    }

    protected void disableControls() {
        if ( null != getSelectionControl() ) {
            getSelectionControl().disable();
        }
        if ( null != getZoomControl() ) {
            getZoomControl().disable();
        }
        if ( null != getPanControl() ) {
            getPanControl().disable();
        }
    }
}
