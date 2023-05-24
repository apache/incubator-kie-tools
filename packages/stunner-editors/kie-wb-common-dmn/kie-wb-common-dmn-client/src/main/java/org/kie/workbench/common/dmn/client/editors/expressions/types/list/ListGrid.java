/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.editors.expressions.types.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.commands.expressions.types.list.AddListRowCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.list.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.list.DeleteListRowCommand;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGridRowNumberColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.util.SelectionUtils;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridRenderer;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

public class ListGrid extends BaseExpressionGrid<List, ListGridData, ListUIModelMapper> implements HasListSelectorControl {

    /** MUST BE SYNCHRONIZED WITH WidthConstants.ts */
    public static final double LIST_DEFAULT_WIDTH = 190d;

    private final ValueAndDataTypePopoverView.Presenter headerEditor;

    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    public ListGrid(final GridCellTuple parent,
                    final Optional<String> nodeUUID,
                    final HasExpression hasExpression,
                    final Optional<HasName> hasName,
                    final DMNGridPanel gridPanel,
                    final DMNGridLayer gridLayer,
                    final ListGridData gridData,
                    final DefinitionUtils definitionUtils,
                    final SessionManager sessionManager,
                    final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                    final DefaultCanvasCommandFactory canvasCommandFactory,
                    final Event<ExpressionEditorChanged> editorSelectedEvent,
                    final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                    final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                    final CellEditorControlsView.Presenter cellEditorControls,
                    final ListSelectorView.Presenter listSelector,
                    final TranslationService translationService,
                    final boolean isOnlyVisualChangeAllowed,
                    final int nesting,
                    final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                    final ValueAndDataTypePopoverView.Presenter headerEditor,
                    final ReadOnlyProvider readOnlyProvider) {
        super(parent,
              nodeUUID,
              hasExpression,
              hasName,
              gridPanel,
              gridLayer,
              gridData,
              new BaseExpressionGridRenderer(gridData),
              definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              editorSelectedEvent,
              refreshFormPropertiesEvent,
              domainObjectSelectionEvent,
              cellEditorControls,
              listSelector,
              translationService,
              isOnlyVisualChangeAllowed,
              nesting,
              readOnlyProvider);
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.headerEditor = headerEditor;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // makeUiModelMapper needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public ListUIModelMapper makeUiModelMapper() {
        return new ListUIModelMapper(this,
                                     this::getModel,
                                     getExpression(),
                                     () -> isOnlyVisualChangeAllowed,
                                     expressionEditorDefinitionsSupplier,
                                     listSelector,
                                     nesting);
    }

    @Override
    public void initialiseUiColumns() {
        final ContextGridRowNumberColumn rowNumberColumn = new ContextGridRowNumberColumn(Collections.emptyList(),
                                                                                          getAndSetInitialWidth(ListUIModelMapperHelper.ROW_COLUMN_INDEX,
                                                                                                                ContextGridRowNumberColumn.DEFAULT_WIDTH));
        final GridColumn listColumn = new ListExpressionEditorColumn(gridLayer,
                                                                     getAndSetInitialWidth(ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX,
                                                                                                                   LIST_DEFAULT_WIDTH),
                                                                     this);
        model.appendColumn(rowNumberColumn);
        model.appendColumn(listColumn);

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> true);
    }

    @Override
    public void initialiseUiRows() {
        getExpression().get().ifPresent(list -> {
            list.getExpression().forEach(e -> model.appendRow(new ExpressionEditorGridRow()));
        });
    }

    @Override
    public void initialiseUiCells() {
        getExpression().get().ifPresent(list -> {
            for (int rowIndex = 0; rowIndex < list.getExpression().size(); rowIndex++) {
                uiModelMapper.fromDMNModel(rowIndex,
                                           0);
                uiModelMapper.fromDMNModel(rowIndex,
                                           1);
            }
        });
    }

    @Override
    public java.util.List<ListSelectorItem> getItems(final int uiRowIndex,
                                                     final int uiColumnIndex) {
        final java.util.List<ListSelectorItem> items = new ArrayList<>();
        final boolean isMultiRow = SelectionUtils.isMultiRow(model);
        final boolean isMultiSelect = SelectionUtils.isMultiSelect(model);

        items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.ListEditor_HeaderRows)));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ListEditor_InsertRowAbove),
                                             !isMultiRow,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> addRow(uiRowIndex));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ListEditor_InsertRowBelow),
                                             !isMultiRow,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> addRow(uiRowIndex + 1));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ListEditor_DeleteRow),
                                             !isMultiRow && model.getRowCount() > 1,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> deleteRow(uiRowIndex));
                                             }));

        //If not ExpressionEditor column don't add extra items
        if (ListUIModelMapperHelper.getSection(uiColumnIndex) != ListUIModelMapperHelper.ListSection.EXPRESSION) {
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

        if (items.size() > 0) {
            items.add(new ListSelectorDividerItem());
        }
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ExpressionEditor_Clear),
                                             !isMultiSelect,
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

    void addRow(final int index) {
        getExpression().get().ifPresent(list -> {
            final GridRow listRow = new ExpressionEditorGridRow();
            final HasExpression hasExpression = HasExpression.wrap(list, new LiteralExpression());
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddListRowCommand(list,
                                                                hasExpression,
                                                                model,
                                                                listRow,
                                                                index,
                                                                uiModelMapper,
                                                                () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void deleteRow(final int index) {
        getExpression().get().ifPresent(list -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteListRowCommand(list,
                                                                   model,
                                                                   index,
                                                                   () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void clearExpressionType(final int uiRowIndex) {
        final GridCellTuple gc = new GridCellTuple(uiRowIndex,
                                                   ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX,
                                                   this);
        getExpression().get().ifPresent(list -> {
            final HasExpression hasExpression = list.getExpression().get(uiRowIndex);
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new ClearExpressionTypeCommand(gc,
                                                                         hasExpression,
                                                                         uiModelMapper,
                                                                         () -> {
                                                                             resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
                                                                             selectExpressionEditorFirstCell(uiRowIndex,
                                                                                                             ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX);
                                                                         },
                                                                         () -> {
                                                                             resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
                                                                             selectExpressionEditorFirstCell(uiRowIndex,
                                                                                                             ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX);
                                                                         }));
        });
    }

    @Override
    public void doAfterSelectionChange(final int uiRowIndex,
                                       final int uiColumnIndex) {
        if (hasAnyHeaderCellSelected() || hasMultipleCellsSelected()) {
            super.doAfterSelectionChange(uiRowIndex, uiColumnIndex);
            return;
        }

        if (getExpression().get().isPresent()) {
            final List list = getExpression().get().get();
            final ListUIModelMapperHelper.ListSection section = ListUIModelMapperHelper.getSection(uiColumnIndex);
            if (section == ListUIModelMapperHelper.ListSection.EXPRESSION) {
                final HasExpression hasExpression = list.getExpression().get(uiRowIndex);
                final Expression expression = hasExpression.getExpression();
                if (expression instanceof DomainObject) {
                    final DomainObject domainObject = (DomainObject) expression;
                    fireDomainObjectSelectionEvent(domainObject);
                    return;
                }
            }
        }
        super.doAfterSelectionChange(uiRowIndex, uiColumnIndex);
    }
}
