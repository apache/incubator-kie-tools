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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.client.commands.general.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;

public class ExpressionContainerGrid extends BaseGridWidget implements HasListSelectorControl {

    private static final String COLUMN_GROUP = "ExpressionContainerGrid$Expression0";

    private final DMNGridPanel gridPanel;
    private final DMNGridLayer gridLayer;
    private final CellEditorControlsView.Presenter cellEditorControls;
    private final TranslationService translationService;

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final GridCellTuple parent = new GridCellTuple(0, 0, this);

    private Optional<HasName> hasName;
    private HasExpression hasExpression;

    private ExpressionContainerUIModelMapper uiModelMapper;

    public ExpressionContainerGrid(final DMNGridPanel gridPanel,
                                   final DMNGridLayer gridLayer,
                                   final CellEditorControlsView.Presenter cellEditorControls,
                                   final TranslationService translationService,
                                   final ListSelectorView.Presenter listSelector,
                                   final SessionManager sessionManager,
                                   final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                   final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitions) {
        super(new DMNGridData(),
              gridLayer,
              gridLayer,
              new ExpressionContainerRenderer());
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.cellEditorControls = cellEditorControls;
        this.translationService = translationService;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;

        this.uiModelMapper = new ExpressionContainerUIModelMapper(parent,
                                                                  this::getModel,
                                                                  () -> Optional.ofNullable(hasExpression.getExpression()),
                                                                  () -> hasName,
                                                                  () -> hasExpression,
                                                                  expressionEditorDefinitions,
                                                                  listSelector);

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        final GridColumn expressionColumn = new ExpressionEditorColumn(gridLayer,
                                                                       new BaseHeaderMetaData(COLUMN_GROUP),
                                                                       this);
        expressionColumn.setMovable(false);
        expressionColumn.setResizable(true);

        model.appendColumn(expressionColumn);
        model.appendRow(new DMNGridRow());

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
    }

    @Override
    public boolean onDragHandle(final INodeXYEvent event) {
        return false;
    }

    @Override
    public void deselect() {
        getModel().clearSelections();
        super.deselect();
    }

    public void setExpression(final Optional<HasName> hasName,
                              final HasExpression hasExpression) {
        this.hasName = hasName;
        this.hasExpression = hasExpression;

        uiModelMapper.fromDMNModel(0, 0);

        synchroniseViewWhenExpressionEditorChanged();
    }

    @Override
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        return Collections.singletonList(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ExpressionEditor_Clear),
                                                                    true,
                                                                    () -> {
                                                                        cellEditorControls.hide();
                                                                        clearExpressionType();
                                                                    }));
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        final ListSelectorTextItem li = (ListSelectorTextItem) item;
        li.getCommand().execute();
    }

    void clearExpressionType() {
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new ClearExpressionTypeCommand(parent,
                                                                     hasExpression,
                                                                     uiModelMapper,
                                                                     this::synchroniseViewWhenExpressionEditorChanged));
    }

    private void synchroniseViewWhenExpressionEditorChanged() {
        parent.onResize();
        gridPanel.refreshScrollPosition();
        gridPanel.updatePanelSize();
        gridLayer.batch();
    }
}
