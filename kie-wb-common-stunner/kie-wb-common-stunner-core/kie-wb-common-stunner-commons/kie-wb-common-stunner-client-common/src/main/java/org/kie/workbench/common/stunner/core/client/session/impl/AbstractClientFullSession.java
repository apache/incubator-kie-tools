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

package org.kie.workbench.common.stunner.core.client.session.impl;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasNameEditionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasValidationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.palette.CanvasPaletteControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.command.stack.StackCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;

public abstract class AbstractClientFullSession extends ClientReadOnlySessionImpl
        implements ClientFullSession<AbstractCanvas, AbstractCanvasHandler> {

    private ResizeControl<AbstractCanvasHandler, Element> resizeControl;
    private CanvasValidationControl<AbstractCanvasHandler> canvasValidationControl;
    private CanvasPaletteControl<AbstractCanvasHandler> canvasPaletteControl;
    private CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    private ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl;
    private ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl;
    private DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl;
    private CanvasNameEditionControl<AbstractCanvasHandler, Element> canvasNameEditionControl;
    private DragControl<AbstractCanvasHandler, Element> dragControl;
    private ToolboxControl<AbstractCanvasHandler, Element> toolboxControl;
    private ElementBuilderControl<AbstractCanvasHandler> builderControl;

    public AbstractClientFullSession( final AbstractCanvas canvas,
                                      final AbstractCanvasHandler canvasHandler,
                                      final ResizeControl<AbstractCanvasHandler, Element> resizeControl,
                                      final CanvasValidationControl<AbstractCanvasHandler> canvasValidationControl,
                                      final CanvasPaletteControl<AbstractCanvasHandler> canvasPaletteControl,
                                      final SelectionControl<AbstractCanvasHandler, Element> selectionControl,
                                      final ZoomControl<AbstractCanvas> zoomControl,
                                      final PanControl<AbstractCanvas> panControl,
                                      final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                      final ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl,
                                      final ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl,
                                      final DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl,
                                      final CanvasNameEditionControl<AbstractCanvasHandler, Element> canvasNameEditionControl,
                                      final DragControl<AbstractCanvasHandler, Element> dragControl,
                                      final ToolboxControl<AbstractCanvasHandler, Element> toolboxControl,
                                      final ElementBuilderControl<AbstractCanvasHandler> builderControl ) {
        super( canvas, canvasHandler, selectionControl, zoomControl, panControl );
        this.resizeControl = resizeControl;
        this.canvasValidationControl = canvasValidationControl;
        this.canvasPaletteControl = canvasPaletteControl;
        this.canvasCommandManager = canvasCommandManager;
        this.connectionAcceptorControl = connectionAcceptorControl;
        this.containmentAcceptorControl = containmentAcceptorControl;
        this.dockingAcceptorControl = dockingAcceptorControl;
        this.canvasNameEditionControl = canvasNameEditionControl;
        this.dragControl = dragControl;
        this.toolboxControl = toolboxControl;
        this.builderControl = builderControl;
    }

    @Override
    public ResizeControl<AbstractCanvasHandler, Element> getResizeControl() {
        return resizeControl;
    }

    @Override
    public CanvasValidationControl<AbstractCanvasHandler> getCanvasValidationControl() {
        return canvasValidationControl;
    }

    @Override
    public CanvasPaletteControl<AbstractCanvasHandler> getCanvasPaletteControl() {
        return canvasPaletteControl;
    }

    @Override
    public CanvasCommandManager<AbstractCanvasHandler> getCanvasCommandManager() {
        return canvasCommandManager;
    }

    @Override
    public ConnectionAcceptorControl<AbstractCanvasHandler> getConnectionAcceptorControl() {
        return connectionAcceptorControl;
    }

    @Override
    public ContainmentAcceptorControl<AbstractCanvasHandler> getContainmentAcceptorControl() {
        return containmentAcceptorControl;
    }

    @Override
    public DockingAcceptorControl<AbstractCanvasHandler> getDockingAcceptorControl() {
        return dockingAcceptorControl;
    }

    @Override
    public CanvasNameEditionControl<AbstractCanvasHandler, Element> getCanvasNameEditionControl() {
        return canvasNameEditionControl;
    }

    @Override
    public DragControl<AbstractCanvasHandler, Element> getDragControl() {
        return dragControl;
    }

    @Override
    public ToolboxControl<AbstractCanvasHandler, Element> getToolboxControl() {
        return toolboxControl;
    }

    @Override
    public ElementBuilderControl<AbstractCanvasHandler> getBuilderControl() {
        return builderControl;
    }

    @Override
    protected void enableControls() {
        super.enableControls();
        enableControl( getResizeControl(), getCanvasHandler() );
        enableControl( getConnectionAcceptorControl(), getCanvasHandler() );
        enableControl( getContainmentAcceptorControl(), getCanvasHandler() );
        enableControl( getDockingAcceptorControl(), getCanvasHandler() );
        enableControl( getDragControl(), getCanvasHandler() );
        enableControl( getToolboxControl(), getCanvasHandler() );
        enableControl( getBuilderControl(), getCanvasHandler() );
        enableControl( getCanvasValidationControl(), getCanvasHandler() );
        enableControl( getCanvasPaletteControl(), getCanvasHandler() );
        enableControl( getCanvasNameEditionControl(), getCanvasHandler() );
    }

    @Override
    protected void disableControls() {
        super.disableControls();
        if ( null != getResizeControl() ) {
            getResizeControl().disable();
        }
        if ( null != getCanvasValidationControl() ) {
            getCanvasValidationControl().disable();
        }
        if ( null != getCanvasPaletteControl() ) {
            getCanvasPaletteControl().disable();
        }
        if ( null != getCanvasCommandManager() ) {
            if ( getCanvasCommandManager() instanceof StackCommandManager ) {
                ( ( StackCommandManager ) getCanvasCommandManager() ).getRegistry().clear();
            }
        }
        if ( null != getConnectionAcceptorControl() ) {
            getConnectionAcceptorControl().disable();
        }
        if ( null != getContainmentAcceptorControl() ) {
            getContainmentAcceptorControl().disable();
        }
        if ( null != getDockingAcceptorControl() ) {
            getDockingAcceptorControl().disable();
        }
        if ( null != getCanvasNameEditionControl() ) {
            getCanvasNameEditionControl().disable();
        }
        if ( null != getDragControl() ) {
            getDragControl().disable();
        }
        if ( null != getToolboxControl() ) {
            getToolboxControl().disable();
        }
        if ( null != getBuilderControl() ) {
            getBuilderControl().disable();
        }
    }

    @Override
    protected void onElementRegistration( final Element element,
                                          final boolean add,
                                          final boolean update ) {
        super.onElementRegistration( element, add, update );
        if ( update ) {
            fireRegistrationUpdateListeners( getResizeControl(), element );
            fireRegistrationUpdateListeners( getConnectionAcceptorControl(), element );
            fireRegistrationUpdateListeners( getContainmentAcceptorControl(), element );
            fireRegistrationUpdateListeners( getDockingAcceptorControl(), element );
            fireRegistrationUpdateListeners( getDragControl(), element );
            fireRegistrationUpdateListeners( getToolboxControl(), element );
            fireRegistrationUpdateListeners( getBuilderControl(), element );
            fireRegistrationUpdateListeners( getCanvasPaletteControl(), element );
            fireRegistrationUpdateListeners( getCanvasNameEditionControl(), element );
        } else {
            fireRegistrationListeners( getResizeControl(), element, add );
            fireRegistrationListeners( getConnectionAcceptorControl(), element, add );
            fireRegistrationListeners( getContainmentAcceptorControl(), element, add );
            fireRegistrationListeners( getDockingAcceptorControl(), element, add );
            fireRegistrationListeners( getDragControl(), element, add );
            fireRegistrationListeners( getToolboxControl(), element, add );
            fireRegistrationListeners( getBuilderControl(), element, add );
            fireRegistrationListeners( getCanvasPaletteControl(), element, add );
            fireRegistrationListeners( getCanvasNameEditionControl(), element, add );
        }
    }

    @Override
    protected void onClear() {
        super.onClear();
        fireRegistrationClearListeners( getResizeControl() );
        fireRegistrationClearListeners( getConnectionAcceptorControl() );
        fireRegistrationClearListeners( getContainmentAcceptorControl() );
        fireRegistrationClearListeners( getDockingAcceptorControl() );
        fireRegistrationClearListeners( getDragControl() );
        fireRegistrationClearListeners( getToolboxControl() );
        fireRegistrationClearListeners( getBuilderControl() );
        fireRegistrationClearListeners( getCanvasValidationControl() );
        fireRegistrationClearListeners( getCanvasPaletteControl() );
        fireRegistrationClearListeners( getCanvasNameEditionControl() );
    }

}
