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
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.AddContextEntryCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.DeleteContextEntryCommand;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
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
import org.kie.workbench.common.dmn.client.widgets.grid.model.HasRowDragRestrictions;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;

public class ContextGrid extends BaseExpressionGrid<Context, ContextGridData, ContextUIModelMapper> implements HasRowDragRestrictions,
                                                                                                               HasListSelectorControl {

    /** MUST BE SYNCHRONIZED WITH WidthConstants.ts */
    public static final double CONTEXT_EXPRESSION_ENTRY_INFO_DEFAULT_WIDTH = 120d;

    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private final ValueAndDataTypePopoverView.Presenter headerEditor;

    public ContextGrid(final GridCellTuple parent,
                       final Optional<String> nodeUUID,
                       final HasExpression hasExpression,
                       final Optional<HasName> hasName,
                       final DMNGridPanel gridPanel,
                       final DMNGridLayer gridLayer,
                       final ContextGridData gridData,
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
    public ContextUIModelMapper makeUiModelMapper() {
        return new ContextUIModelMapper(this,
                                        this::getModel,
                                        getExpression(),
                                        () -> isOnlyVisualChangeAllowed,
                                        expressionEditorDefinitionsSupplier,
                                        listSelector,
                                        nesting);
    }

    @Override
    public void initialiseUiColumns() {
        final List<GridColumn.HeaderMetaData> headerMetaData = new ArrayList<>();
        final ContextGridRowNumberColumn rowNumberColumn = new ContextGridRowNumberColumn(headerMetaData,
                                                                                          getAndSetInitialWidth(ContextUIModelMapperHelper.ROW_COLUMN_INDEX,
                                                                                                                ContextGridRowNumberColumn.DEFAULT_WIDTH));
        if (nesting == 0) {
            rowNumberColumn.getHeaderMetaData().add(new BaseHeaderMetaData("#"));
            headerMetaData.add(new NameColumnHeaderMetaData(hasExpression,
                                                            hasName,
                                                            clearValueConsumer(true, new Name()),
                                                            setValueConsumer(true),
                                                            setTypeRefConsumer(),
                                                            translationService,
                                                            cellEditorControls,
                                                            headerEditor,
                                                            listSelector,
                                                            this::getHeaderItems,
                                                            this::onItemSelected));
        }

        final NameColumn nameColumn = new NameColumn(headerMetaData,
                                                     getAndSetInitialWidth(ContextUIModelMapperHelper.NAME_COLUMN_INDEX,
                                                             CONTEXT_EXPRESSION_ENTRY_INFO_DEFAULT_WIDTH),
                                                     this,
                                                     (rowIndex) -> rowIndex != getModel().getRowCount() - 1,
                                                     clearValueConsumer(false, new Name()),
                                                     setValueConsumer(false),
                                                     setTypeRefConsumer(),
                                                     translationService,
                                                     cellEditorControls,
                                                     headerEditor);
        final ExpressionEditorColumn expressionColumn = new ExpressionEditorColumn(gridLayer,
                                                                                   headerMetaData,
                                                                                   getAndSetInitialWidth(ContextUIModelMapperHelper.EXPRESSION_COLUMN_INDEX,
                                                                                                         UndefinedExpressionColumn.DEFAULT_WIDTH),
                                                                                   this);

        model.appendColumn(rowNumberColumn);
        model.appendColumn(nameColumn);
        model.appendColumn(expressionColumn);

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
    }

    @Override
    public void initialiseUiRows() {
        getExpression().get().ifPresent(c -> {
            c.getContextEntry().forEach(ce -> model.appendRow(new ExpressionEditorGridRow()));
        });
    }

    @Override
    public void initialiseUiCells() {
        getExpression().get().ifPresent(c -> {
            for (int rowIndex = 0; rowIndex < c.getContextEntry().size(); rowIndex++) {
                uiModelMapper.fromDMNModel(rowIndex,
                                           0);
                uiModelMapper.fromDMNModel(rowIndex,
                                           1);
                uiModelMapper.fromDMNModel(rowIndex,
                                           2);
            }
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

    @SuppressWarnings("unused")
    List<ListSelectorItem> getHeaderItems(final int uiHeaderRowIndex,
                                          final int uiHeaderColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();

        items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.ContextEditor_Header)));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ContextEditor_InsertContextEntry),
                                             true,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> addContextEntry(model.getRowCount() - 1));
                                             }));

        return items;
    }

    @Override
    @SuppressWarnings("unused")
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();
        final boolean isMultiRow = SelectionUtils.isMultiRow(model);
        final boolean isMultiSelect = SelectionUtils.isMultiSelect(model);

        if (uiRowIndex < model.getRowCount() - 1) {
            items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.ContextEditor_Header)));
            items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ContextEditor_InsertContextEntryAbove),
                                                 !isMultiRow,
                                                 () -> {
                                                     cellEditorControls.hide();
                                                     getExpression().get().ifPresent(e -> addContextEntry(uiRowIndex));
                                                 }));
            items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ContextEditor_InsertContextEntryBelow),
                                                 !isMultiRow,
                                                 () -> {
                                                     cellEditorControls.hide();
                                                     getExpression().get().ifPresent(e -> addContextEntry(uiRowIndex + 1));
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
        getExpression().get().ifPresent(c -> {
            final ContextEntry ce = new ContextEntry();
            final InformationItem informationItem = new InformationItem();
            informationItem.setName(new Name());
            ce.setVariable(informationItem);

            final CommandResult<CanvasViolation> result = sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                                        new AddContextEntryCommand(c,
                                                                                                                   ce,
                                                                                                                   model,
                                                                                                                   new ExpressionEditorGridRow(),
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
        getExpression().get().ifPresent(c -> {
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
        getExpression().get().ifPresent(context -> {
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
    public void doAfterSelectionChange(final int uiRowIndex,
                                       final int uiColumnIndex) {
        if (hasAnyHeaderCellSelected() || hasMultipleRowsSelected()) {
            super.doAfterSelectionChange(uiRowIndex, uiColumnIndex);
            return;
        }

        if (uiRowIndex < model.getRowCount() - 1) {
            if (getExpression().get().isPresent()) {
                final Context context = getExpression().get().get();
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
    public void doAfterHeaderSelectionChange(final int uiHeaderRowIndex,
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
