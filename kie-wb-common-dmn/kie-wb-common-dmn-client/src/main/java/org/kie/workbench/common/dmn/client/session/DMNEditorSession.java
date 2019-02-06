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

package org.kie.workbench.common.dmn.client.session;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.canvas.controls.resize.DecisionServiceMoveDividerControl;
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
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.SingleLineTextEditorBox;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.AbstractCanvasShortcutsControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.Request;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultEditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.registry.impl.ClientCommandRegistry;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;
import org.uberfire.mvp.Command;

@Dependent
@DMNEditor
public class DMNEditorSession extends DefaultEditorSession implements DMNSession {

    @Inject
    public DMNEditorSession(final ManagedSession session,
                            final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                            final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final @Request SessionCommandManager<AbstractCanvasHandler> requestCommandManager,
                            final ClientCommandRegistry<org.kie.workbench.common.stunner.core.command.Command<AbstractCanvasHandler, CanvasViolation>> clientCommandRegistry) {
        super(session,
              canvasCommandManager,
              sessionCommandManager,
              requestCommandManager,
              clientCommandRegistry);
    }

    @Override
    public ManagedSession getSession() {
        return super.getSession();
    }

    @Override
    public void init(final Metadata metadata,
                     final Command callback) {
        super.init(s -> s.registerCanvasControl(ZoomControl.class)
                           .registerCanvasControl(PanControl.class)
                           .registerCanvasHandlerControl(SelectionControl.class,
                                                         MultipleSelection.class)
                           .registerCanvasHandlerControl(ResizeControl.class)
                           .registerCanvasHandlerControl(DecisionServiceMoveDividerControl.class)
                           .registerCanvasHandlerControl(ConnectionAcceptorControl.class)
                           .registerCanvasHandlerControl(ContainmentAcceptorControl.class)
                           .registerCanvasHandlerControl(DockingAcceptorControl.class)
                           .registerCanvasHandlerControl(CanvasInPlaceTextEditorControl.class,
                                                         SingleLineTextEditorBox.class)
                           .registerCanvasHandlerControl(LocationControl.class)
                           .registerCanvasHandlerControl(ToolboxControl.class)
                           .registerCanvasHandlerControl(ElementBuilderControl.class,
                                                         Observer.class)
                           .registerCanvasHandlerControl(NodeBuilderControl.class)
                           .registerCanvasHandlerControl(EdgeBuilderControl.class)
                           .registerCanvasHandlerControl(AbstractCanvasShortcutsControlImpl.class,
                                                         DMNEditor.class)
                           .registerCanvasControl(KeyboardControl.class)
                           .registerCanvasControl(ClipboardControl.class)
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
