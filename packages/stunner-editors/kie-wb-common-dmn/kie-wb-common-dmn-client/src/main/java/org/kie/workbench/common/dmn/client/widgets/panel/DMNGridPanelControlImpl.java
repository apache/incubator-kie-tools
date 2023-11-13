/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.panel;

import javax.enterprise.context.Dependent;

import com.google.gwt.event.dom.client.ContextMenuHandler;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

@Dependent
public class DMNGridPanelControlImpl extends AbstractCanvasControl<AbstractCanvas> implements DMNGridPanelControl {

    private DMNGridPanel panel;

    @Override
    public void bind(final DMNSession session) {
        final DMNGridLayer gridLayer = session.getGridLayer();
        final CellEditorControlsView.Presenter cellEditorControls = session.getCellEditorControls();
        final RestrictedMousePanMediator mousePanMediator = session.getMousePanMediator();

        final DMNGridPanelCellSelectionHandler selectionHandler = new DMNGridPanelCellSelectionHandlerImpl(gridLayer);
        final ContextMenuHandler contextMenuHandler = new DMNGridPanelContextMenuHandler(gridLayer,
                                                                                         cellEditorControls,
                                                                                         selectionHandler);

        this.panel = new DMNGridPanel(gridLayer,
                                      mousePanMediator,
                                      contextMenuHandler);
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected void doDestroy() {
        panel = null;
    }

    @Override
    public DMNGridPanel getGridPanel() {
        return panel;
    }
}
