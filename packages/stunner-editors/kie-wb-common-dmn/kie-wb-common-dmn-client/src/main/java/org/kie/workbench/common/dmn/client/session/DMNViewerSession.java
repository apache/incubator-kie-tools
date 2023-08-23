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

package org.kie.workbench.common.dmn.client.session;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorControl;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayerControl;
import org.kie.workbench.common.dmn.client.widgets.layer.MousePanMediatorControl;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanelControl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultViewerSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;
import org.uberfire.mvp.Command;

@Dependent
@DMNEditor
public class DMNViewerSession extends DefaultViewerSession implements DMNSession {

    @Inject
    public DMNViewerSession(final ManagedSession session,
                            final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager) {
        super(session,
              canvasCommandManager);
    }

    @Override
    public ManagedSession getSession() {
        return super.getSession();
    }

    @Override
    public void init(final Metadata metadata,
                     final Command callback) {
        init(s -> s.registerCanvasControl(MediatorsControl.class)
                     .registerCanvasHandlerControl(SelectionControl.class,
                                                   MultipleSelection.class)
                     .registerCanvasHandlerControl(ToolboxControl.class)
                     .registerCanvasControl(ExpressionGridCache.class)
                     .registerCanvasControl(DMNGridLayerControl.class)
                     //The order of the following registrations is important. Do not re-order!
                     .registerCanvasControl(CellEditorControl.class)
                     .registerCanvasControl(MousePanMediatorControl.class)
                     .registerCanvasControl(DMNGridPanelControl.class)
                     .registerCanvasControl(ExpressionEditorControl.class),
             metadata,
             callback);
    }

    @Override
    public ExpressionGridCache getExpressionGridCache() {
        return (ExpressionGridCache) getSession().getCanvasControl(ExpressionGridCache.class);
    }

    @Override
    public DMNGridPanel getGridPanel() {
        return ((DMNGridPanelControl) getSession().getCanvasControl(DMNGridPanelControl.class)).getGridPanel();
    }

    @Override
    public DMNGridLayer getGridLayer() {
        return ((DMNGridLayerControl) getSession().getCanvasControl(DMNGridLayerControl.class)).getGridLayer();
    }

    @Override
    public CellEditorControlsView.Presenter getCellEditorControls() {
        return ((CellEditorControl) getSession().getCanvasControl(CellEditorControl.class)).getCellEditorControls();
    }

    @Override
    public RestrictedMousePanMediator getMousePanMediator() {
        return ((MousePanMediatorControl) getSession().getCanvasControl(MousePanMediatorControl.class)).getMousePanMediator();
    }

    @Override
    public ExpressionEditorView.Presenter getExpressionEditor() {
        return ((ExpressionEditorControl) getSession().getCanvasControl(ExpressionEditorControl.class)).getExpressionEditor();
    }
}
