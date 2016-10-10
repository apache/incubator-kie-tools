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
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasSaveControl;
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
import org.kie.workbench.common.stunner.core.command.stack.StackCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;

public abstract class AbstractFullSession extends CanvasReadOnlySessionImpl
        implements DefaultCanvasFullSession {

    ResizeControl<AbstractCanvasHandler, Element> resizeControl;
    CanvasValidationControl<AbstractCanvasHandler> canvasValidationControl;
    CanvasSaveControl<AbstractCanvasHandler> canvasSaveControl;
    CanvasPaletteControl<AbstractCanvasHandler> canvasPaletteControl;
    CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl;
    ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl;
    DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl;
    CanvasNameEditionControl<AbstractCanvasHandler, Element> canvasNameEditionControl;
    DragControl<AbstractCanvasHandler, Element> dragControl;
    ToolboxControl<AbstractCanvasHandler, Element> toolboxControl;
    ElementBuilderControl<AbstractCanvasHandler> builderControl;

    public AbstractFullSession( final AbstractCanvas canvas,
                                final AbstractCanvasHandler canvasHandler,
                                final ResizeControl<AbstractCanvasHandler, Element> resizeControl,
                                final CanvasValidationControl<AbstractCanvasHandler> canvasValidationControl,
                                final CanvasSaveControl<AbstractCanvasHandler> canvasSaveControl,
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
        this.canvasSaveControl = canvasSaveControl;
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
    public CanvasSaveControl<AbstractCanvasHandler> getCanvasSaveControl() {
        return canvasSaveControl;
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
    public ZoomControl<AbstractCanvas> getZoomControl() {
        return zoomControl;
    }

    @Override
    public PanControl<AbstractCanvas> getPanControl() {
        return panControl;
    }

    @Override
    public void doDispose() {
        if ( null != resizeControl ) {
            resizeControl.disable();
            resizeControl = null;
        }
        if ( null != canvasValidationControl ) {
            canvasValidationControl.disable();
            canvasValidationControl = null;
        }
        if ( null != canvasSaveControl ) {
            canvasSaveControl.disable();
            canvasSaveControl = null;
        }
        if ( null != canvasPaletteControl ) {
            canvasPaletteControl.disable();
            canvasPaletteControl = null;
        }
        if ( null != canvasCommandManager ) {
            if ( canvasCommandManager instanceof StackCommandManager ) {
                ( ( StackCommandManager ) canvasCommandManager ).getRegistry().clear();

            }
            canvasCommandManager = null;
        }
        if ( null != connectionAcceptorControl ) {
            connectionAcceptorControl.disable();
            connectionAcceptorControl = null;
        }
        if ( null != containmentAcceptorControl ) {
            containmentAcceptorControl.disable();
            containmentAcceptorControl = null;
        }
        if ( null != dockingAcceptorControl ) {
            dockingAcceptorControl.disable();
            dockingAcceptorControl = null;
        }
        if ( null != canvasNameEditionControl ) {
            canvasNameEditionControl.disable();
            canvasNameEditionControl = null;
        }
        if ( null != dragControl ) {
            dragControl.disable();
            dragControl = null;
        }
        if ( null != toolboxControl ) {
            toolboxControl.disable();
            toolboxControl = null;
        }
        if ( null != builderControl ) {
            builderControl.disable();
            builderControl = null;
        }

    }

}
