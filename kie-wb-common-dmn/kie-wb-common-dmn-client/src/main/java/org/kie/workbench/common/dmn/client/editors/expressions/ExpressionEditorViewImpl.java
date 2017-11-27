/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.widgets.grid.BoundaryTransformMediator;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.HasExpressionEditorControls;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

@Templated
@Dependent
public class ExpressionEditorViewImpl implements ExpressionEditorView {

    private static final double VP_SCALE = 1.0;

    private ExpressionEditorView.Presenter presenter;

    @DataField("exitButton")
    private Div exitButton;

    @DataField("editorControls")
    private Div expressionEditorControls;

    private Document document;

    private TranslationService ts;
    private DMNGridPanel gridPanel;
    private DMNGridLayer gridLayer;
    private RestrictedMousePanMediator mousePanMediator;
    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    protected ExpressionContainer expressionContainer;

    public ExpressionEditorViewImpl() {
        //CDI proxy
    }

    @Inject
    public ExpressionEditorViewImpl(final Div exitButton,
                                    final Div expressionEditorControls,
                                    final Document document,
                                    final TranslationService ts,
                                    final @DMNEditor DMNGridPanel gridPanel,
                                    final @DMNEditor DMNGridLayer gridLayer,
                                    final @DMNEditor RestrictedMousePanMediator mousePanMediator,
                                    final SessionManager sessionManager,
                                    final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager) {
        this.exitButton = exitButton;
        this.expressionEditorControls = expressionEditorControls;
        this.document = document;
        this.ts = ts;
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.mousePanMediator = mousePanMediator;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;

        setupGridPanel();
        setupGridWidget();
        setupGridWidgetPanControl();
    }

    @DataField("dmn-table")
    @SuppressWarnings("unused")
    public GridLienzoPanel getGridPanel() {
        return gridPanel;
    }

    protected void setupGridPanel() {
        final Transform transform = new Transform().scale(VP_SCALE);
        gridPanel.getViewport().setTransform(transform);
        gridPanel.add(gridLayer);

        gridPanel.getElement().setId("dmn_container_" + com.google.gwt.dom.client.Document.get().createUniqueId());
    }

    protected void setupGridWidget() {
        expressionContainer = new ExpressionContainer(gridLayer);
        gridLayer.removeAll();
        gridLayer.add(expressionContainer);
        gridLayer.select(expressionContainer);
        gridLayer.enterPinnedMode(expressionContainer,
                                  () -> {/*Nothing*/});
    }

    protected void setupGridWidgetPanControl() {
        final TransformMediator defaultTransformMediator = new BoundaryTransformMediator(expressionContainer);
        mousePanMediator.setTransformMediator(defaultTransformMediator);
        mousePanMediator.setBatchDraw(true);
        gridLayer.setDefaultTransformMediator(defaultTransformMediator);
        gridPanel.getViewport().getMediators().push(mousePanMediator);
    }

    @Override
    public void init(final ExpressionEditorView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setEditor(final ExpressionEditorDefinition<Expression> definition,
                          final HasExpression hasExpression,
                          final Optional<HasName> hasName,
                          final Optional<Expression> expression) {
        final Optional<GridWidget> oEditor = definition.getEditor(new GridCellTuple(0,
                                                                                    0,
                                                                                    expressionContainer.getModel()),
                                                                  hasExpression,
                                                                  expression,
                                                                  hasName,
                                                                  false);
        expressionContainer.getModel().setCell(0,
                                               0,
                                               new DMNExpressionCellValue(oEditor));
        gridPanel.refreshScrollPosition();
        gridPanel.updatePanelSize();
        gridLayer.batch();

        onExpressionEditorSelected(oEditor);
    }

    @Override
    public void onExpressionEditorSelected(final Optional<GridWidget> oEditor) {
        DOMUtil.removeAllChildren(expressionEditorControls);

        if (oEditor.isPresent()) {
            final GridWidget editor = oEditor.get();
            if (editor instanceof HasExpressionEditorControls) {
                final HasExpressionEditorControls hasControls = (HasExpressionEditorControls) editor;
                final Optional<IsElement> oEditorControls = hasControls.getEditorControls();
                if (oEditorControls.isPresent()) {
                    final IsElement editorControls = oEditorControls.get();
                    expressionEditorControls.appendChild(editorControls.getElement());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler("exitButton")
    void onClickExitButton(final ClickEvent event) {
        presenter.exit();
    }
}
