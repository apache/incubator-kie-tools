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
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
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
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BoundaryTransformMediator;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

@Templated
@Dependent
public class ExpressionEditorViewImpl implements ExpressionEditorView {

    static final double VP_SCALE = 1.0;

    private ExpressionEditorView.Presenter presenter;

    @DataField("returnToDRG")
    private Anchor returnToDRG;

    @DataField("editorControls")
    private Div expressionEditorControls;

    private ExpressionContainerGrid expressionContainerGrid;

    private Document document;

    private DMNGridPanel gridPanel;
    private DMNGridLayer gridLayer;
    private RestrictedMousePanMediator mousePanMediator;

    private Event<ExpressionEditorSelectedEvent> editorSelectedEvent;
    private CellEditorControls cellEditorControls;
    private TranslationService translationService;
    private ListSelector listSelector;

    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    public ExpressionEditorViewImpl() {
        //CDI proxy
    }

    @Inject
    public ExpressionEditorViewImpl(final Anchor returnToDRG,
                                    final Div expressionEditorControls,
                                    final Document document,
                                    final @DMNEditor DMNGridPanel gridPanel,
                                    final @DMNEditor DMNGridLayer gridLayer,
                                    final @DMNEditor RestrictedMousePanMediator mousePanMediator,
                                    final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                                    final CellEditorControls cellEditorControls,
                                    final TranslationService translationService,
                                    final ListSelector listSelector,
                                    final SessionManager sessionManager,
                                    final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                    final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier) {
        this.returnToDRG = returnToDRG;
        this.expressionEditorControls = expressionEditorControls;
        this.document = document;

        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.mousePanMediator = mousePanMediator;

        this.editorSelectedEvent = editorSelectedEvent;
        this.cellEditorControls = cellEditorControls;
        this.translationService = translationService;
        this.listSelector = listSelector;

        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;

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
        expressionContainerGrid = new ExpressionContainerGrid(gridPanel,
                                                              gridLayer,
                                                              editorSelectedEvent,
                                                              cellEditorControls,
                                                              translationService,
                                                              listSelector,
                                                              sessionManager,
                                                              sessionCommandManager,
                                                              expressionEditorDefinitionsSupplier);
        gridLayer.removeAll();
        gridLayer.add(expressionContainerGrid);
        gridLayer.select(expressionContainerGrid);
        gridLayer.enterPinnedMode(expressionContainerGrid,
                                  () -> {/*Nothing*/});
    }

    protected void setupGridWidgetPanControl() {
        final TransformMediator defaultTransformMediator = new BoundaryTransformMediator(expressionContainerGrid);
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
    public void setExpression(final Optional<HasName> hasName,
                              final HasExpression hasExpression) {
        expressionContainerGrid.setExpression(hasName,
                                              hasExpression);

        hasName.ifPresent(name -> returnToDRG.setTextContent(translationService.format(DMNEditorConstants.ExpressionEditor_ReturnToDRG,
                                                                                       name.getName().getValue())));

        //TODO {manstis} This can be removed when all EditorControls are removed
        final GridCellTuple parent = new GridCellTuple(0, 0, expressionContainerGrid);
        final Optional<Expression> expression = Optional.ofNullable(hasExpression.getExpression());
        final Optional<ExpressionEditorDefinition<Expression>> expressionEditorDefinition = expressionEditorDefinitionsSupplier.get().getExpressionEditorDefinition(expression);
        expressionEditorDefinition.ifPresent(definition -> {
            final Optional<BaseExpressionGrid> oEditor = definition.getEditor(parent,
                                                                              hasExpression,
                                                                              expression,
                                                                              hasName,
                                                                              false);
            onExpressionEditorSelected(oEditor);
        });
    }

    @Override
    public void onExpressionEditorSelected(final Optional<BaseExpressionGrid> oEditor) {
        DOMUtil.removeAllChildren(expressionEditorControls);

        if (oEditor.isPresent()) {
            final BaseExpressionGrid editor = oEditor.get();
            final Optional<IsElement> oEditorControls = editor.getEditorControls();
            if (oEditorControls.isPresent()) {
                final IsElement editorControls = oEditorControls.get();
                expressionEditorControls.appendChild(editorControls.getElement());
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler("returnToDRG")
    void onClickReturnToDRG(final ClickEvent event) {
        presenter.exit();
    }

    @Override
    public void onResize() {
        gridPanel.onResize();
    }
}
