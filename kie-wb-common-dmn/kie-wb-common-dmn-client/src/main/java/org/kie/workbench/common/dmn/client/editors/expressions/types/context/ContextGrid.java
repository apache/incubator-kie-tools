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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.AddContextEntryCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.DeleteContextEntryCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.HasRowDragRestrictions;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

public class ContextGrid extends BaseExpressionGrid<Context, ContextGridData, ContextUIModelMapper> implements HasRowDragRestrictions,
                                                                                                               HasListSelectorControl {

    private static final String EXPRESSION_COLUMN_GROUP = "ContextGrid$ExpressionColumn1";

    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    public ContextGrid(final GridCellTuple parent,
                       final Optional<String> nodeUUID,
                       final HasExpression hasExpression,
                       final Optional<Context> expression,
                       final Optional<HasName> hasName,
                       final DMNGridPanel gridPanel,
                       final DMNGridLayer gridLayer,
                       final ContextGridData gridData,
                       final DefinitionUtils definitionUtils,
                       final SessionManager sessionManager,
                       final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                       final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                       final CellEditorControlsView.Presenter cellEditorControls,
                       final ListSelectorView.Presenter listSelector,
                       final TranslationService translationService,
                       final int nesting,
                       final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier) {
        super(parent,
              nodeUUID,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              gridData,
              new ContextGridRenderer(nesting > 0),
              definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              cellEditorControls,
              listSelector,
              translationService,
              nesting);
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // makeUiModelMapper needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public ContextUIModelMapper makeUiModelMapper() {
        return new ContextUIModelMapper(this,
                                        this::getModel,
                                        () -> expression,
                                        expressionEditorDefinitionsSupplier,
                                        listSelector,
                                        nesting);
    }

    @Override
    public void initialiseUiColumns() {
        final NameColumn nameColumn = new NameColumn(new NameColumnHeaderMetaData(() -> hasName.orElse(HasName.NOP).getName().getValue(),
                                                                                  (s) -> hasName.orElse(HasName.NOP).getName().setValue(s),
                                                                                  getHeaderHasNameTextBoxFactory()),
                                                     getBodyTextBoxFactory(),
                                                     this);
        final ExpressionEditorColumn expressionColumn = new ExpressionEditorColumn(gridLayer,
                                                                                   new BaseHeaderMetaData("",
                                                                                                          EXPRESSION_COLUMN_GROUP),
                                                                                   this);

        model.appendColumn(new RowNumberColumn());
        model.appendColumn(nameColumn);
        model.appendColumn(expressionColumn);

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
    }

    @Override
    public void initialiseUiModel() {
        expression.ifPresent(c -> {
            c.getContextEntry().stream().forEach(ce -> {
                model.appendRow(new DMNGridRow());
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           0);
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           1);
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           2);
            });
        });
    }

    @Override
    protected boolean isHeaderHidden() {
        return nesting > 0;
    }

    @Override
    public boolean isRowDragPermitted(final GridWidgetDnDHandlersState state) {
        final GridWidgetHandlersOperation operation = state.getOperation();
        if (operation == GridWidgetHandlersOperation.ROW_MOVE_PENDING) {
            final int lastRowIndex = model.getRowCount() - 1;
            final List<GridRow> rows = state.getActiveGridRows();
            return !rows.contains(model.getRow(lastRowIndex));
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();
        if (uiRowIndex == model.getRowCount() - 1) {
            return items;
        }

        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ContextEditor_InsertContextEntryAbove),
                                             true,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 expression.ifPresent(e -> addContextEntry(uiRowIndex));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ContextEditor_InsertContextEntryBelow),
                                             true,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 expression.ifPresent(e -> addContextEntry(uiRowIndex + 1));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ContextEditor_DeleteContextEntry),
                                             model.getRowCount() > 2 && uiRowIndex < model.getRowCount() - 1,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 deleteContextEntry(uiRowIndex);
                                             }));

        //If not ExpressionEditor column don't add extra items
        if (ContextUIModelMapperHelper.getSection(uiColumnIndex) != ContextUIModelMapperHelper.ContextSection.EXPRESSION) {
            return items;
        }

        //If cell editor is UndefinedExpressionGrid don't add extra items
        final GridCell<?> cell = model.getCell(uiRowIndex, uiColumnIndex);
        final ExpressionCellValue ecv = (ExpressionCellValue) cell.getValue();
        if (!ecv.getValue().isPresent()) {
            return items;
        }
        final BaseExpressionGrid grid = ecv.getValue().get();
        if (grid instanceof UndefinedExpressionGrid) {
            return items;
        }

        items.add(new ListSelectorDividerItem());
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ExpressionEditor_Clear),
                                             true,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 clearExpressionType(uiRowIndex);
                                             }));

        return items;
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        final ListSelectorTextItem li = (ListSelectorTextItem) item;
        li.getCommand().execute();
    }

    void addContextEntry(final int index) {
        expression.ifPresent(c -> {
            final ContextEntry ce = new ContextEntry();
            ce.setVariable(new InformationItem());
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddContextEntryCommand(c,
                                                                     ce,
                                                                     model,
                                                                     new DMNGridRow(),
                                                                     index,
                                                                     uiModelMapper,
                                                                     this::resize));
        });
    }

    void deleteContextEntry(final int index) {
        expression.ifPresent(c -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteContextEntryCommand(c,
                                                                        model,
                                                                        index,
                                                                        this::resize));
        });
    }

    void clearExpressionType(final int uiRowIndex) {
        final GridCellTuple gc = new GridCellTuple(uiRowIndex,
                                                   ContextUIModelMapperHelper.EXPRESSION_COLUMN_INDEX,
                                                   this);
        final HasExpression hasExpression = expression.get().getContextEntry().get(uiRowIndex);
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new ClearExpressionTypeCommand(gc,
                                                                     hasExpression,
                                                                     uiModelMapper,
                                                                     () -> resizeBasedOnCellExpressionEditor(uiRowIndex,
                                                                                                             ContextUIModelMapperHelper.EXPRESSION_COLUMN_INDEX)));
    }
}
