/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.canvas;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.DefaultCanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;

@ApplicationScoped
@BPMN
public class BPMNCanvasFactory extends DefaultCanvasFactory {

    // CDI proxy.
    protected BPMNCanvasFactory() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public BPMNCanvasFactory(final ManagedInstance<ResizeControl> resizeControls,
                             final ManagedInstance<ConnectionAcceptorControl> connectionAcceptorControls,
                             final ManagedInstance<ContainmentAcceptorControl> containmentAcceptorControls,
                             final ManagedInstance<DockingAcceptorControl> dockingAcceptorControls,
                             final ManagedInstance<CanvasInPlaceTextEditorControl> inPlaceTextEditorControls,
                             final @MultipleSelection ManagedInstance<SelectionControl> selectionControls,
                             final ManagedInstance<LocationControl> locationControls,
                             final @BPMN ManagedInstance<ToolboxControl> toolboxControls,
                             final @Default @Observer ManagedInstance<ElementBuilderControl> elementBuilderControls,
                             final ManagedInstance<NodeBuilderControl> nodeBuilderControls,
                             final ManagedInstance<EdgeBuilderControl> edgeBuilderControls,
                             final ManagedInstance<ZoomControl> zoomControls,
                             final ManagedInstance<PanControl> panControls,
                             final ManagedInstance<KeyboardControl> keyboardControls,
                             final ManagedInstance<ClipboardControl> clipboardControls,
                             final ManagedInstance<ControlPointControl> controlPointControls,
                             final @Default ManagedInstance<AbstractCanvas> canvasInstances,
                             final @Default ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances) {
        super(resizeControls,
              connectionAcceptorControls,
              containmentAcceptorControls,
              dockingAcceptorControls,
              inPlaceTextEditorControls,
              selectionControls,
              locationControls,
              toolboxControls,
              elementBuilderControls,
              nodeBuilderControls,
              edgeBuilderControls,
              zoomControls,
              panControls,
              keyboardControls,
              clipboardControls,
              controlPointControls,
              canvasInstances,
              canvasHandlerInstances);
    }
}
