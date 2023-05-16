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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.ArrayList;
import java.util.Optional;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationColumnCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationRowCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.DeleteRelationColumnCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.DeleteRelationRowCommand;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.util.SelectionUtils;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridRenderer;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.LiteralExpressionGridRow;
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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.CellContextUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils.getExpressionTextLineHeight;

public class RelationGrid extends BaseExpressionGrid<Relation, RelationGridData, RelationUIModelMapper> implements HasListSelectorControl {

    private final TextAreaSingletonDOMElementFactory factory = getBodyTextAreaFactory();

    private final ManagedInstance<ValueAndDataTypePopoverView.Presenter> headerEditors;

    public RelationGrid(final GridCellTuple parent,
                        final Optional<String> nodeUUID,
                        final HasExpression hasExpression,
                        final Optional<HasName> hasName,
                        final DMNGridPanel gridPanel,
                        final DMNGridLayer gridLayer,
                        final RelationGridData gridData,
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
                        final ManagedInstance<ValueAndDataTypePopoverView.Presenter> headerEditors,
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
        this.headerEditors = headerEditors;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // makeUiModelMapper needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public RelationUIModelMapper makeUiModelMapper() {
        return new RelationUIModelMapper(this::getModel,
                                         getExpression(),
                                         listSelector,
                                         getExpressionTextLineHeight(getRenderer().getTheme()));
    }

    @Override
    public void initialiseUiColumns() {
        int uiColumnIndex = 0;
        final RowNumberColumn rowNumberColumn = new RowNumberColumn();
        rowNumberColumn.setWidth(getAndSetInitialWidth(uiColumnIndex++, rowNumberColumn.getWidth()));

        if (getExpression().get().isPresent()) {
            model.appendColumn(rowNumberColumn);
            final Relation e = getExpression().get().get();
            for (int index = 0; index < e.getColumn().size(); index++) {
                final GridColumn relationColumn = makeRelationColumn(uiColumnIndex++, e.getColumn().get(index));
                model.appendColumn(relationColumn);
            }
        }

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> true);
    }

    private RelationColumn makeRelationColumn(final int index,
                                              final InformationItem informationItem) {
        final RelationColumn relationColumn = new RelationColumn(new RelationColumnHeaderMetaData(informationItem,
                                                                                                  clearValueConsumer(false, new Name()),
                                                                                                  setValueConsumer(false),
                                                                                                  setTypeRefConsumer(),
                                                                                                  translationService,
                                                                                                  cellEditorControls,
                                                                                                  headerEditors.get(),
                                                                                                  listSelector,
                                                                                                  this::getHeaderItems,
                                                                                                  this::onItemSelected),
                                                                 factory,
                                                                 getAndSetInitialWidth(index, DMNGridColumn.DEFAULT_WIDTH), // MUST BE SYNCHRONIZED WITH WidthConstants.ts
                                                                 this);
        return relationColumn;
    }

    @Override
    public void initialiseUiRows() {
        getExpression().get().ifPresent(e -> {
            e.getRow().forEach(r -> model.appendRow(new LiteralExpressionGridRow()));
        });
    }

    @Override
    public void initialiseUiCells() {
        getExpression().get().ifPresent(e -> {
            for (int rowIndex = 0; rowIndex < e.getRow().size(); rowIndex++) {
                int columnIndex = 0;
                uiModelMapper.fromDMNModel(rowIndex,
                                           columnIndex++);
                for (int ii = 0; ii < e.getColumn().size(); ii++) {
                    uiModelMapper.fromDMNModel(rowIndex,
                                               columnIndex++);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    java.util.List<ListSelectorItem> getHeaderItems(final int uiHeaderRowIndex,
                                                    final int uiHeaderColumnIndex) {
        final java.util.List<ListSelectorItem> items = new ArrayList<>();

        addColumnItems(items, uiHeaderColumnIndex);

        return items;
    }

    @Override
    @SuppressWarnings("unused")
    public java.util.List<ListSelectorItem> getItems(final int uiRowIndex,
                                                     final int uiColumnIndex) {
        final java.util.List<ListSelectorItem> items = new ArrayList<>();

        addColumnItems(items, uiColumnIndex);

        final boolean isMultiRow = SelectionUtils.isMultiRow(model);
        items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.RelationEditor_HeaderRows)));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.RelationEditor_InsertRowAbove),
                                             !isMultiRow,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> addRow(uiRowIndex));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.RelationEditor_InsertRowBelow),
                                             !isMultiRow,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> addRow(uiRowIndex + 1));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.RelationEditor_DeleteRow),
                                             !isMultiRow && model.getRowCount() > 1,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> deleteRow(uiRowIndex));
                                             }));
        return items;
    }

    private void addColumnItems(final java.util.List<ListSelectorItem> items,
                                final int uiColumnIndex) {
        final boolean isMultiColumn = SelectionUtils.isMultiColumn(model);

        items.add(ListSelectorHeaderItem.build(translationService.format(DMNEditorConstants.RelationEditor_HeaderColumns)));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.RelationEditor_InsertColumnLeft),
                                             !isMultiColumn && uiColumnIndex > 0,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> addColumn(uiColumnIndex));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.RelationEditor_InsertColumnRight),
                                             !isMultiColumn && uiColumnIndex > 0,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> addColumn(uiColumnIndex + 1));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.RelationEditor_DeleteColumn),
                                             !isMultiColumn && model.getColumnCount() - RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT > 1 && uiColumnIndex > 0,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 getExpression().get().ifPresent(e -> deleteColumn(uiColumnIndex));
                                             }));
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        final ListSelectorTextItem li = (ListSelectorTextItem) item;
        li.getCommand().execute();
    }

    void addColumn(final int index) {
        getExpression().get().ifPresent(relation -> {
            final InformationItem informationItem = new InformationItem();
            informationItem.setName(new Name());

            final CommandResult<CanvasViolation> result = sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                                        new AddRelationColumnCommand(relation,
                                                                                                                     informationItem,
                                                                                                                     model,
                                                                                                                     () -> makeRelationColumn(index, informationItem),
                                                                                                                     index,
                                                                                                                     uiModelMapper,
                                                                                                                     () -> resize(BaseExpressionGrid.RESIZE_EXISTING),
                                                                                                                     () -> resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM)));

            if (!CommandUtils.isError(result)) {
                selectHeaderCell(0, index, false, false);
                CellContextUtilities.editSelectedCell(this);
            }
        });
    }

    void deleteColumn(final int index) {
        getExpression().get().ifPresent(relation -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteRelationColumnCommand(relation,
                                                                          model,
                                                                          index,
                                                                          uiModelMapper,
                                                                          () -> resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM),
                                                                          () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void addRow(final int index) {
        getExpression().get().ifPresent(relation -> {
            final GridRow relationRow = new LiteralExpressionGridRow();
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddRelationRowCommand(relation,
                                                                    new List(),
                                                                    model,
                                                                    relationRow,
                                                                    index,
                                                                    uiModelMapper,
                                                                    () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
        });
    }

    void deleteRow(final int index) {
        getExpression().get().ifPresent(relation -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteRelationRowCommand(relation,
                                                                       model,
                                                                       index,
                                                                       () -> resize(BaseExpressionGrid.RESIZE_EXISTING)));
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
            final Relation relation = getExpression().get().get();
            final RelationUIModelMapperHelper.RelationSection section = RelationUIModelMapperHelper.getSection(relation, uiColumnIndex);
            if (section == RelationUIModelMapperHelper.RelationSection.INFORMATION_ITEM) {
                final int iiIndex = RelationUIModelMapperHelper.getInformationItemIndex(relation, uiColumnIndex);
                final HasExpression hasExpression = relation.getRow().get(uiRowIndex).getExpression().get(iiIndex);
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

    @Override
    public void doAfterHeaderSelectionChange(final int uiHeaderRowIndex,
                                             final int uiHeaderColumnIndex) {
        if (getExpression().get().isPresent()) {
            final Relation relation = getExpression().get().get();
            final RelationUIModelMapperHelper.RelationSection section = RelationUIModelMapperHelper.getSection(relation, uiHeaderColumnIndex);
            if (section == RelationUIModelMapperHelper.RelationSection.INFORMATION_ITEM) {
                final int iiIndex = RelationUIModelMapperHelper.getInformationItemIndex(relation, uiHeaderColumnIndex);
                final InformationItem domainObject = relation.getColumn().get(iiIndex);
                fireDomainObjectSelectionEvent(domainObject);
                return;
            }
        }
        super.doAfterHeaderSelectionChange(uiHeaderRowIndex, uiHeaderColumnIndex);
    }
}
