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
import javax.inject.Inject;
import javax.inject.Named;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BoundaryTransformMediator;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
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

    @DataField("expressionType")
    private Heading expressionType;

    private ExpressionContainerGrid expressionContainerGrid;

    private DMNGridPanel gridPanel;
    private DMNGridLayer gridLayer;
    private RestrictedMousePanMediator mousePanMediator;

    private CellEditorControlsView.Presenter cellEditorControls;
    private TranslationService translationService;
    private ListSelectorView.Presenter listSelector;

    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    public ExpressionEditorViewImpl() {
        //CDI proxy
    }

    @Inject
    public ExpressionEditorViewImpl(final Anchor returnToDRG,
                                    final @Named("h2") Heading expressionType,
                                    final @DMNEditor DMNGridPanel gridPanel,
                                    final @DMNEditor DMNGridLayer gridLayer,
                                    final @DMNEditor RestrictedMousePanMediator mousePanMediator,
                                    final CellEditorControlsView.Presenter cellEditorControls,
                                    final TranslationService translationService,
                                    final ListSelectorView.Presenter listSelector,
                                    final SessionManager sessionManager,
                                    final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                    final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier) {
        this.returnToDRG = returnToDRG;
        this.expressionType = expressionType;

        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.mousePanMediator = mousePanMediator;

        this.cellEditorControls = cellEditorControls;
        this.translationService = translationService;
        this.listSelector = listSelector;

        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
    }

    @DataField("dmn-table")
    @SuppressWarnings("unused")
    public GridLienzoPanel getGridPanel() {
        return gridPanel;
    }

    @Override
    public void init(final ExpressionEditorView.Presenter presenter) {
        this.presenter = presenter;

        setupGridPanel();
        setupGridWidget();
        setupGridWidgetPanControl();
    }

    protected void setupGridPanel() {
        final Transform transform = new Transform().scale(VP_SCALE);
        gridPanel.getViewport().setTransform(transform);
        gridPanel.add(gridLayer);

        gridPanel.getElement().setId("dmn_container_" + com.google.gwt.dom.client.Document.get().createUniqueId());
    }

    protected void setupGridWidget() {
        expressionContainerGrid = new ExpressionContainerGrid(gridLayer,
                                                              cellEditorControls,
                                                              translationService,
                                                              listSelector,
                                                              sessionManager,
                                                              sessionCommandManager,
                                                              expressionEditorDefinitionsSupplier,
                                                              getExpressionGridCacheSupplier(),
                                                              this::setExpressionTypeText,
                                                              this::setReturnToDRGText);
        gridLayer.removeAll();
        gridLayer.add(expressionContainerGrid);
        gridLayer.select(expressionContainerGrid);
        gridLayer.enterPinnedMode(expressionContainerGrid,
                                  () -> {/*Nothing*/});
    }

    // This class (ExpressionEditorViewImpl) is instantiated when injected into SessionDiagramEditorScreen
    // which is before a Session has been created and the ExpressionGridCache CanvasControl has been registered.
    // Therefore we need to defer instance access to a Supplier.
    protected Supplier<ExpressionGridCache> getExpressionGridCacheSupplier() {
        return () -> ((DMNSession) sessionManager.getCurrentSession()).getExpressionGridCache();
    }

    protected void setupGridWidgetPanControl() {
        final TransformMediator defaultTransformMediator = new BoundaryTransformMediator(expressionContainerGrid);
        mousePanMediator.setTransformMediator(defaultTransformMediator);
        mousePanMediator.setBatchDraw(true);
        gridLayer.setDefaultTransformMediator(defaultTransformMediator);
        gridPanel.getViewport().getMediators().push(mousePanMediator);
    }

    @Override
    public void setExpression(final String nodeUUID,
                              final HasExpression hasExpression,
                              final Optional<HasName> hasName) {
        expressionContainerGrid.setExpression(nodeUUID,
                                              hasExpression,
                                              hasName);

        setReturnToDRGText(hasName);
        setExpressionTypeText(Optional.ofNullable(hasExpression.getExpression()));
    }

    private void setReturnToDRGText(final Optional<HasName> hasName) {
        hasName.ifPresent(name -> returnToDRG.setTextContent(translationService.format(DMNEditorConstants.ExpressionEditor_ReturnToDRG,
                                                                                       name.getName().getValue())));
    }

    private void setExpressionTypeText(final Optional<Expression> expression) {
        expressionType.setTextContent(expression.isPresent() ?
                                              expression.get().getClass().getSimpleName() :
                                              "<" + translationService.getTranslation(DMNEditorConstants.ExpressionEditor_UndefinedExpressionType) + ">");
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
