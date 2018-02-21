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
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.AddContextEntryCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.DeleteContextEntryCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.HasRowDragRestrictions;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

public class ContextGrid extends BaseExpressionGrid<Context, ContextUIModelMapper> implements HasRowDragRestrictions,
                                                                                              HasListSelectorControl {

    private static final String EXPRESSION_COLUMN_GROUP = "ContextGrid$ExpressionColumn1";

    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private ListSelector listSelector;

    public ContextGrid(final GridCellTuple parent,
                       final HasExpression hasExpression,
                       final Optional<Context> expression,
                       final Optional<HasName> hasName,
                       final DMNGridPanel gridPanel,
                       final DMNGridLayer gridLayer,
                       final SessionManager sessionManager,
                       final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                       final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                       final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                       final CellEditorControls cellEditorControls,
                       final TranslationService translationService,
                       final ListSelector listSelector,
                       final boolean isNested) {
        super(parent,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              new ContextGridData(new DMNGridData(gridLayer),
                                  sessionManager,
                                  sessionCommandManager,
                                  expression,
                                  gridLayer::batch),
              new ContextGridRenderer(isNested),
              sessionManager,
              sessionCommandManager,
              editorSelectedEvent,
              cellEditorControls,
              translationService,
              isNested);
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.listSelector = listSelector;

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
        return new ContextUIModelMapper(this::getModel,
                                        () -> expression,
                                        expressionEditorDefinitionsSupplier,
                                        listSelector);
    }

    @Override
    public void initialiseUiColumns() {
        final TextBoxSingletonDOMElementFactory factory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                                                gridLayer,
                                                                                                this,
                                                                                                sessionManager,
                                                                                                sessionCommandManager,
                                                                                                newCellHasNoValueCommand(),
                                                                                                newCellHasValueCommand());
        final TextBoxSingletonDOMElementFactory headerFactory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                                                      gridLayer,
                                                                                                      this,
                                                                                                      sessionManager,
                                                                                                      sessionCommandManager,
                                                                                                      newHeaderHasNoValueCommand(),
                                                                                                      newHeaderHasValueCommand());

        final NameColumn nameColumn = new NameColumn(new NameColumnHeaderMetaData(() -> hasName.orElse(HasName.NOP).getName().getValue(),
                                                                                  (s) -> hasName.orElse(HasName.NOP).getName().setValue(s),
                                                                                  headerFactory),
                                                     factory,
                                                     this);
        final ExpressionEditorColumn expressionColumn = new ExpressionEditorColumn(new BaseHeaderMetaData("",
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
    public Optional<IsElement> getEditorControls() {
        return Optional.empty();
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
                                                                     () -> {
                                                                         parent.onResize();
                                                                         gridPanel.refreshScrollPosition();
                                                                         gridPanel.updatePanelSize();
                                                                         gridLayer.batch();
                                                                     }));
        });
    }

    void deleteContextEntry(final int index) {
        expression.ifPresent(c -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteContextEntryCommand(c,
                                                                        model,
                                                                        index,
                                                                        () -> {
                                                                            parent.onResize();
                                                                            gridPanel.refreshScrollPosition();
                                                                            gridPanel.updatePanelSize();
                                                                            gridLayer.batch();
                                                                        }));
        });
    }
}
