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

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.AddContextEntryCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.DeleteContextEntryCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.util.SelectionUtils;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridRenderer;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.HasRowDragRestrictions;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;

public class ContextGrid extends BaseExpressionGrid<Context, ContextGridData, ContextUIModelMapper> implements HasRowDragRestrictions,
                                                                                                               HasListSelectorControl {

    private static final String EXPRESSION_COLUMN_GROUP = "ContextGrid$ExpressionColumn1";

    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private final NameAndDataTypePopoverView.Presenter headerEditor;

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
                       final Event<ExpressionEditorChanged> editorSelectedEvent,
                       final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                       final CellEditorControlsView.Presenter cellEditorControls,
                       final ListSelectorView.Presenter listSelector,
                       final TranslationService translationService,
                       final int nesting,
                       final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                       final NameAndDataTypePopoverView.Presenter headerEditor) {
        super(parent,
              nodeUUID,
              hasExpression,
              expression,
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
              domainObjectSelectionEvent,
              cellEditorControls,
              listSelector,
              translationService,
              nesting);
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
        final List<GridColumn.HeaderMetaData> headerMetaData = new ArrayList<>();
        final ContextGridRowNumberColumn rowNumberColumn = new ContextGridRowNumberColumn(headerMetaData);
        if (nesting == 0) {
            rowNumberColumn.getHeaderMetaData().add(new BaseHeaderMetaData("#"));
            headerMetaData.add(new NameColumnHeaderMetaData(hasExpression,
                                                            expression,
                                                            hasName,
                                                            clearDisplayNameConsumer(true),
                                                            setDisplayNameConsumer(true),
                                                            setTypeRefConsumer(),
                                                            cellEditorControls,
                                                            headerEditor,
                                                            Optional.of(translationService.getTranslation(DMNEditorConstants.ContextEditor_EditExpression))));
        }

        final NameColumn nameColumn = new NameColumn(headerMetaData,
                                                     this,
                                                     (rowIndex) -> rowIndex != getModel().getRowCount() - 1,
                                                     clearDisplayNameConsumer(false),
                                                     setDisplayNameConsumer(false),
                                                     setTypeRefConsumer(),
                                                     cellEditorControls,
                                                     headerEditor,
                                                     Optional.of(translationService.getTranslation(DMNEditorConstants.ContextEditor_EditContextEntry)));
        final ExpressionEditorColumn expressionColumn = new ExpressionEditorColumn(gridLayer,
                                                                                   headerMetaData,
                                                                                   this);

        model.appendColumn(rowNumberColumn);
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
        final boolean isMultiRow = SelectionUtils.isMultiRow(model);
        final boolean isMultiSelect = SelectionUtils.isMultiSelect(model);

        if (uiRowIndex < model.getRowCount() - 1) {
            items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ContextEditor_InsertContextEntryAbove),
                                                 !isMultiRow,
                                                 () -> {
                                                     cellEditorControls.hide();
                                                     expression.ifPresent(e -> addContextEntry(uiRowIndex));
                                                 }));
            items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ContextEditor_InsertContextEntryBelow),
                                                 !isMultiRow,
                                                 () -> {
                                                     cellEditorControls.hide();
                                                     expression.ifPresent(e -> addContextEntry(uiRowIndex + 1));
                                                 }));
            items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ContextEditor_DeleteContextEntry),
                                                 !isMultiRow && model.getRowCount() > 2 && uiRowIndex < model.getRowCount() - 1,
                                                 () -> {
                                                     cellEditorControls.hide();
                                                     deleteContextEntry(uiRowIndex);
                                                 }));
        }

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

    void addContextEntry(final int index) {
        expression.ifPresent(c -> {
            final ContextEntry ce = new ContextEntry();
            final InformationItem informationItem = new InformationItem();
            informationItem.setName(new Name());
            ce.setVariable(informationItem);

            final CommandResult<CanvasViolation> result = sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                                        new AddContextEntryCommand(c,
                                                                                                                   ce,
                                                                                                                   model,
                                                                                                                   new DMNGridRow(),
                                                                                                                   index,
                                                                                                                   uiModelMapper,
                                                                                                                   () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));

            if (!CommandUtils.isError(result)) {
                selectCell(index, ContextUIModelMapperHelper.NAME_COLUMN_INDEX, false, false);
                startEditingCell(index, ContextUIModelMapperHelper.NAME_COLUMN_INDEX);
            }
        });
    }

    void deleteContextEntry(final int index) {
        expression.ifPresent(c -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteContextEntryCommand(c,
                                                                        model,
                                                                        index,
                                                                        () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void clearExpressionType(final int uiRowIndex) {
        final GridCellTuple gc = new GridCellTuple(uiRowIndex,
                                                   ContextUIModelMapperHelper.EXPRESSION_COLUMN_INDEX,
                                                   this);
        expression.ifPresent(context -> {
            final HasExpression hasExpression = context.getContextEntry().get(uiRowIndex);
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new ClearExpressionTypeCommand(gc,
                                                                         hasExpression,
                                                                         uiModelMapper,
                                                                         () -> {
                                                                             resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
                                                                             selectExpressionEditorFirstCell(uiRowIndex, ContextUIModelMapperHelper.EXPRESSION_COLUMN_INDEX);
                                                                         },
                                                                         () -> {
                                                                             resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
                                                                             selectExpressionEditorFirstCell(uiRowIndex, ContextUIModelMapperHelper.EXPRESSION_COLUMN_INDEX);
                                                                         }));
        });
    }

    @Override
    protected void doAfterSelectionChange(final int uiRowIndex,
                                          final int uiColumnIndex) {
        if (hasAnyHeaderCellSelected() || hasMultipleRowsSelected()) {
            super.doAfterSelectionChange(uiRowIndex, uiColumnIndex);
            return;
        }

        if (uiRowIndex < model.getRowCount() - 1) {
            if (expression.isPresent()) {
                final Context context = expression.get();
                fireDomainObjectSelectionEvent(context.getContextEntry().get(uiRowIndex).getVariable());
                return;
            }
        }
        super.doAfterSelectionChange(uiRowIndex, uiColumnIndex);
    }

    private boolean hasMultipleRowsSelected() {
        return getModel().getSelectedCells().stream().map(GridData.SelectedCell::getRowIndex).distinct().count() > 1;
    }

    @Override
    protected void doAfterHeaderSelectionChange(final int uiHeaderRowIndex,
                                                final int uiHeaderColumnIndex) {
        if (uiHeaderColumnIndex == ContextUIModelMapperHelper.NAME_COLUMN_INDEX || uiHeaderColumnIndex == ContextUIModelMapperHelper.EXPRESSION_COLUMN_INDEX) {
            final DMNModelInstrumentedBase base = hasExpression.asDMNModelInstrumentedBase();
            if (base instanceof DomainObject) {
                fireDomainObjectSelectionEvent((DomainObject) base);
                return;
            }
        }
        super.doAfterHeaderSelectionChange(uiHeaderRowIndex, uiHeaderColumnIndex);
    }
}
