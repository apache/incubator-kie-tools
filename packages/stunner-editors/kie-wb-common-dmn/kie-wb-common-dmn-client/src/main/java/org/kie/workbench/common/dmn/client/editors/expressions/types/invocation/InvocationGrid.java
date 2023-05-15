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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.AddParameterBindingCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.DeleteParameterBindingCommand;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.util.SelectionUtils;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
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
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

public class InvocationGrid extends BaseExpressionGrid<Invocation, InvocationGridData, InvocationUIModelMapper> implements HasListSelectorControl {

    /** MUST BE SYNCHRONIZED WITH WidthConstants.ts */
    public static final double INVOCATION_EXPRESSION_ENTRY_INFO_DEFAULT_WIDTH = 120d;

    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private final ValueAndDataTypePopoverView.Presenter headerEditor;

    public InvocationGrid(final GridCellTuple parent,
                          final Optional<String> nodeUUID,
                          final HasExpression hasExpression,
                          final Optional<HasName> hasName,
                          final DMNGridPanel gridPanel,
                          final DMNGridLayer gridLayer,
                          final InvocationGridData gridData,
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
              new InvocationGridRenderer(gridData),
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
    public InvocationUIModelMapper makeUiModelMapper() {
        return new InvocationUIModelMapper(this,
                                           this::getModel,
                                           getExpression(),
                                           () -> isOnlyVisualChangeAllowed,
                                           expressionEditorDefinitionsSupplier,
                                           listSelector,
                                           nesting);
    }

    @Override
    protected void initialiseUiColumns() {
        final List<GridColumn.HeaderMetaData> headerMetaData = new ArrayList<>();
        final RowNumberColumn rowNumberColumn = new RowNumberColumn();
        rowNumberColumn.setWidth(getAndSetInitialWidth(0, rowNumberColumn.getWidth()));

        if (nesting == 0) {
            headerMetaData.add(new InvocationColumnHeaderMetaData(hasExpression,
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
        headerMetaData.add(new InvocationColumnExpressionHeaderMetaData(this::getExpressionText,
                                                                        this::setExpressionText,
                                                                        getHeaderTextBoxFactory(),
                                                                        Optional.of(translationService.getTranslation(DMNEditorConstants.InvocationEditor_EnterFunction)),
                                                                        listSelector,
                                                                        this::getHeaderItems,
                                                                        this::onItemSelected));

        final InvocationParameterColumn nameColumn = new InvocationParameterColumn(headerMetaData,
                                                                                   getAndSetInitialWidth(1, INVOCATION_EXPRESSION_ENTRY_INFO_DEFAULT_WIDTH),
                                                                                   this,
                                                                                   rowIndex -> true,
                                                                                   clearValueConsumer(false, new Name()),
                                                                                   setValueConsumer(false),
                                                                                   setTypeRefConsumer(),
                                                                                   translationService,
                                                                                   cellEditorControls,
                                                                                   headerEditor);
        final ExpressionEditorColumn expressionColumn = new ExpressionEditorColumn(gridLayer,
                                                                                   headerMetaData,
                                                                                   getAndSetInitialWidth(2, UndefinedExpressionColumn.DEFAULT_WIDTH),
                                                                                   this);

        model.appendColumn(rowNumberColumn);
        model.appendColumn(nameColumn);
        model.appendColumn(expressionColumn);

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
    }

    private String getExpressionText() {
        return getExpression().get().map(invocation -> (LiteralExpression) invocation.getExpression())
                .map(LiteralExpression::getText)
                .orElse(new Text())
                .getValue();
    }

    private void setExpressionText(final String text) {
        getExpression().get().ifPresent(invocation -> ((LiteralExpression) invocation.getExpression()).getText().setValue(text));
    }

    @Override
    public void initialiseUiRows() {
        getExpression().get().ifPresent(invocation -> {
            invocation.getBinding().forEach(binding -> model.appendRow(new ExpressionEditorGridRow()));
        });
    }

    @Override
    public void initialiseUiCells() {
        getExpression().get().ifPresent(invocation -> {
            for (int rowIndex = 0; rowIndex < invocation.getBinding().size(); rowIndex++) {
                uiModelMapper.fromDMNModel(rowIndex,
                                           InvocationUIModelMapper.ROW_NUMBER_COLUMN_INDEX);
                uiModelMapper.fromDMNModel(rowIndex,
                                           InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX);
                uiModelMapper.fromDMNModel(rowIndex,
                                           InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX);
            }
        });
    }

    @SuppressWarnings("unused")
    List<ListSelectorItem> getHeaderItems(final int uiHeaderRowIndex,
                                          final int uiHeaderColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();

        items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.InvocationEditor_Header)));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.InvocationEditor_InsertParameter),
                                             true,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> addParameterBinding(model.getRowCount()));
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

        items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.InvocationEditor_Header)));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.InvocationEditor_InsertParameterAbove),
                                             !isMultiRow,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> addParameterBinding(uiRowIndex));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.InvocationEditor_InsertParameterBelow),
                                             !isMultiRow,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> addParameterBinding(uiRowIndex + 1));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.InvocationEditor_DeleteParameter),
                                             !isMultiRow && model.getRowCount() > 1,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 deleteParameterBinding(uiRowIndex);
                                             }));

        //If not ExpressionEditor column don't add extra items
        if (uiColumnIndex != InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX) {
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

    void addParameterBinding(final int index) {
        getExpression().get().ifPresent(invocation -> {
            final Binding binding = new Binding();
            final InformationItem parameter = new InformationItem();
            parameter.setName(new Name());
            binding.setParameter(parameter);

            final CommandResult<CanvasViolation> result = sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                                        new AddParameterBindingCommand(invocation,
                                                                                                                       binding,
                                                                                                                       model,
                                                                                                                       new ExpressionEditorGridRow(),
                                                                                                                       index,
                                                                                                                       uiModelMapper,
                                                                                                                       () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));

            if (!CommandUtils.isError(result)) {
                selectCell(index, InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX, false, false);
                startEditingCell(index, InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX);
            }
        });
    }

    void deleteParameterBinding(final int index) {
        getExpression().get().ifPresent(invocation -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteParameterBindingCommand(invocation,
                                                                            model,
                                                                            index,
                                                                            () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void clearExpressionType(final int uiRowIndex) {
        final GridCellTuple gc = new GridCellTuple(uiRowIndex,
                                                   InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX,
                                                   this);
        getExpression().get().ifPresent(invocation -> {
            final HasExpression hasExpression = invocation.getBinding().get(uiRowIndex);
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new ClearExpressionTypeCommand(gc,
                                                                         hasExpression,
                                                                         uiModelMapper,
                                                                         () -> {
                                                                             resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
                                                                             selectExpressionEditorFirstCell(uiRowIndex, InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX);
                                                                         },
                                                                         () -> {
                                                                             resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
                                                                             selectExpressionEditorFirstCell(uiRowIndex, InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX);
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

        if (uiColumnIndex == InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX) {
            if (getExpression().get().isPresent()) {
                final Invocation invocation = getExpression().get().get();
                fireDomainObjectSelectionEvent(invocation.getBinding().get(uiRowIndex).getVariable());
                return;
            }
        }
        super.doAfterSelectionChange(uiRowIndex, uiColumnIndex);
    }

    @Override
    public void doAfterHeaderSelectionChange(final int uiHeaderRowIndex,
                                             final int uiHeaderColumnIndex) {
        if (nesting == 0 && uiHeaderRowIndex == 0) {
            if (uiHeaderColumnIndex == InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX || uiHeaderColumnIndex == InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX) {
                final DMNModelInstrumentedBase base = hasExpression.asDMNModelInstrumentedBase();
                if (base instanceof DomainObject) {
                    fireDomainObjectSelectionEvent((DomainObject) base);
                    return;
                }
            }
        }
        super.doAfterHeaderSelectionChange(uiHeaderRowIndex, uiHeaderColumnIndex);
    }
}
