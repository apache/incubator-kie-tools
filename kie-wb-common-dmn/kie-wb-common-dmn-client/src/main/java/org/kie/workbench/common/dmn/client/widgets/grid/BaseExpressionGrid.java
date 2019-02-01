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

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHeaderValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHeaderValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetTypeRefCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.EditableHeaderGridWidgetEditCellMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.IsRowDragHandle;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.DefaultGridWidgetCellSelectorMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

public abstract class BaseExpressionGrid<E extends Expression, D extends GridData, M extends BaseUIModelMapper<E>>
        extends BaseGrid
        implements ExpressionGridCache.IsCacheable,
                   HasDOMElementResources {

    public static final double DEFAULT_PADDING = 10.0;

    public static final Function<BaseExpressionGrid, Double> RESIZE_EXISTING = (beg) -> beg.getWidth() + beg.getPadding() * 2;

    public static final Function<BaseExpressionGrid, Double> RESIZE_EXISTING_MINIMUM = (beg) -> beg.getMinimumWidth() + beg.getPadding() * 2;

    protected final GridCellTuple parent;
    protected final Optional<E> expression;
    protected final DMNGridPanel gridPanel;
    protected final DefinitionUtils definitionUtils;
    protected final Event<ExpressionEditorChanged> editorSelectedEvent;
    protected final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    protected final ListSelectorView.Presenter listSelector;
    protected final int nesting;

    protected M uiModelMapper;

    public BaseExpressionGrid(final GridCellTuple parent,
                              final Optional<String> nodeUUID,
                              final HasExpression hasExpression,
                              final Optional<E> expression,
                              final Optional<HasName> hasName,
                              final DMNGridPanel gridPanel,
                              final DMNGridLayer gridLayer,
                              final D gridData,
                              final GridRenderer gridRenderer,
                              final DefinitionUtils definitionUtils,
                              final SessionManager sessionManager,
                              final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                              final Event<ExpressionEditorChanged> editorSelectedEvent,
                              final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                              final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                              final CellEditorControlsView.Presenter cellEditorControls,
                              final ListSelectorView.Presenter listSelector,
                              final TranslationService translationService,
                              final int nesting) {
        super(nodeUUID,
              hasExpression,
              hasName,
              gridLayer,
              gridData,
              gridRenderer,
              sessionManager,
              sessionCommandManager,
              refreshFormPropertiesEvent,
              domainObjectSelectionEvent,
              cellEditorControls,
              translationService);
        this.parent = parent;
        this.expression = expression;
        this.gridPanel = gridPanel;
        this.definitionUtils = definitionUtils;
        this.editorSelectedEvent = editorSelectedEvent;
        this.canvasCommandFactory = canvasCommandFactory;
        this.listSelector = listSelector;
        this.nesting = nesting;

        doInitialisation();
    }

    protected void doInitialisation() {
        this.uiModelMapper = makeUiModelMapper();

        initialiseUiColumns();
        initialiseUiModel();
    }

    protected abstract M makeUiModelMapper();

    protected abstract void initialiseUiColumns();

    protected abstract void initialiseUiModel();

    @SuppressWarnings("unchecked")
    public Consumer<HasName> clearDisplayNameConsumer(final boolean updateStunnerTitle) {
        return (hn) -> {
            final CompositeCommand.Builder commandBuilder = newHasNameHasNoValueCommand(hn);
            if (updateStunnerTitle) {
                getUpdateStunnerTitleCommand("").ifPresent(commandBuilder::addCommand);
            }
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          commandBuilder.build());
        };
    }

    @SuppressWarnings("unchecked")
    public BiConsumer<HasName, Name> setDisplayNameConsumer(final boolean updateStunnerTitle) {
        return (hn, name) -> {
            final CompositeCommand.Builder commandBuilder = newHasNameHasValueCommand(hn, name);
            if (updateStunnerTitle) {
                getUpdateStunnerTitleCommand(name.getValue()).ifPresent(commandBuilder::addCommand);
            }
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          commandBuilder.build());
        };
    }

    public BiConsumer<HasTypeRef, QName> setTypeRefConsumer() {
        return (htr, typeRef) -> sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                               new SetTypeRefCommand(htr,
                                                                                     typeRef,
                                                                                     () -> {
                                                                                         gridLayer.batch();
                                                                                         selectedDomainObject.ifPresent(this::fireDomainObjectSelectionEvent);
                                                                                     }));
    }

    protected CompositeCommand.Builder newHasNameHasNoValueCommand(final HasName hasName) {
        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommand.Builder<>();
        commandBuilder.addCommand(new DeleteHasNameCommand(hasName,
                                                           () -> {
                                                               gridLayer.batch();
                                                               selectedDomainObject.ifPresent(this::fireDomainObjectSelectionEvent);
                                                           }));
        return commandBuilder;
    }

    protected CompositeCommand.Builder newHasNameHasValueCommand(final HasName hasName,
                                                                 final Name name) {
        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommand.Builder<>();
        commandBuilder.addCommand(new SetHasNameCommand(hasName,
                                                        name,
                                                        () -> {
                                                            gridLayer.batch();
                                                            selectedDomainObject.ifPresent(this::fireDomainObjectSelectionEvent);
                                                        }));
        return commandBuilder;
    }

    protected Optional<AbstractCanvasGraphCommand> getUpdateStunnerTitleCommand(final String value) {
        AbstractCanvasGraphCommand command = null;
        if (getNodeUUID().isPresent()) {
            final String uuid = getNodeUUID().get();
            final AbstractCanvasHandler canvasHandler = (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
            final Element<?> element = canvasHandler.getGraphIndex().get(uuid);
            if (element.getContent() instanceof Definition) {
                final Definition definition = (Definition) element.getContent();
                final String nameId = definitionUtils.getNameIdentifier(definition.getDefinition());
                if (nameId != null) {
                    command = (AbstractCanvasGraphCommand) canvasCommandFactory.updatePropertyValue(element,
                                                                                                    nameId,
                                                                                                    value);
                }
            }
        }
        return Optional.ofNullable(command);
    }

    public TextAreaSingletonDOMElementFactory getBodyTextAreaFactory() {
        return new TextAreaSingletonDOMElementFactory(gridPanel,
                                                      gridLayer,
                                                      this,
                                                      sessionManager,
                                                      sessionCommandManager,
                                                      newCellHasNoValueCommand(),
                                                      newCellHasValueCommand());
    }

    protected Function<GridCellTuple, Command> newCellHasNoValueCommand() {
        return (gridCellTuple) -> new DeleteCellValueCommand(gridCellTuple,
                                                             () -> uiModelMapper,
                                                             gridLayer::batch);
    }

    protected Function<GridCellValueTuple, Command> newCellHasValueCommand() {
        return (gridCellValueTuple) -> new SetCellValueCommand(gridCellValueTuple,
                                                               () -> uiModelMapper,
                                                               gridLayer::batch);
    }

    public TextAreaSingletonDOMElementFactory getHeaderTextAreaFactory() {
        return new TextAreaSingletonDOMElementFactory(gridPanel,
                                                      gridLayer,
                                                      this,
                                                      sessionManager,
                                                      sessionCommandManager,
                                                      newHeaderHasNoValueCommand(),
                                                      newHeaderHasValueCommand());
    }

    public TextBoxSingletonDOMElementFactory getHeaderTextBoxFactory() {
        return new TextBoxSingletonDOMElementFactory(gridPanel,
                                                     gridLayer,
                                                     this,
                                                     sessionManager,
                                                     sessionCommandManager,
                                                     newHeaderHasNoValueCommand(),
                                                     newHeaderHasValueCommand());
    }

    protected Function<GridCellTuple, Command> newHeaderHasNoValueCommand() {
        return (gc) -> new DeleteHeaderValueCommand(extractEditableHeaderMetaData(gc),
                                                    gridLayer::batch);
    }

    protected Function<GridCellValueTuple, Command> newHeaderHasValueCommand() {
        return (gcv) -> {
            final String title = gcv.getValue().getValue().toString();
            return new SetHeaderValueCommand(title,
                                             extractEditableHeaderMetaData(gcv),
                                             gridLayer::batch);
        };
    }

    protected EditableHeaderMetaData extractEditableHeaderMetaData(final GridCellTuple gc) {
        final int headerRowIndex = gc.getRowIndex();
        final int headerColumnIndex = gc.getColumnIndex();
        final GridColumn.HeaderMetaData headerMetaData = uiModelMapper.getUiModel().get()
                .getColumns().get(headerColumnIndex)
                .getHeaderMetaData().get(headerRowIndex);
        if (headerMetaData instanceof EditableHeaderMetaData) {
            return (EditableHeaderMetaData) headerMetaData;
        }
        throw new IllegalArgumentException("Header (" + headerColumnIndex + ", " + headerRowIndex + ") was not an instanceof EditableHeaderMetaData");
    }

    @Override
    protected List<NodeMouseEventHandler> getNodeMouseClickEventHandlers(final GridSelectionManager selectionManager) {
        final List<NodeMouseEventHandler> handlers = new ArrayList<>();
        handlers.add(new DefaultGridWidgetCellSelectorMouseEventHandler(selectionManager));
        handlers.add(new EditableHeaderGridWidgetEditCellMouseEventHandler());
        return handlers;
    }

    @Override
    protected List<NodeMouseEventHandler> getNodeMouseDoubleClickEventHandlers(final GridSelectionManager selectionManager,
                                                                               final GridPinnedModeManager pinnedModeManager) {
        final List<NodeMouseEventHandler> handlers = new ArrayList<>();
        handlers.add(new EditableHeaderGridWidgetEditCellMouseEventHandler());
        return handlers;
    }

    @Override
    public boolean onDragHandle(final INodeXYEvent event) {
        return false;
    }

    @Override
    public Viewport getViewport() {
        // A GridWidget's Viewport may not have been set IF the grid has not been attached to a Layer.
        // This is possible when a nested Expression Editor is on a newly created non-visible row as the
        // GridRenderer ignores rows/cells outside of the Layer's visible extents.
        Viewport viewport = super.getViewport();
        if (viewport == null) {
            viewport = gridLayer.getViewport();
        }
        return viewport;
    }

    @Override
    public Layer getLayer() {
        // A GridWidget's Layer may not have been set IF the grid has not been attached to a Layer.
        // This is possible when a nested Expression Editor is on a newly created non-visible row as the
        // GridRenderer ignores rows/cells outside of the Layer's visible extents.
        Layer layer = super.getLayer();
        if (layer == null) {
            layer = gridLayer;
        }
        return layer;
    }

    @Override
    public void select() {
        fireExpressionEditorChanged();
        super.select();
    }

    private void fireExpressionEditorChanged() {
        editorSelectedEvent.fire(new ExpressionEditorChanged());
    }

    @Override
    public void deselect() {
        fireExpressionEditorChanged();
        getModel().clearSelections();
        clearSelectedDomainObject();
        super.deselect();
    }

    void clearSelectedDomainObject() {
        selectedDomainObject = Optional.empty();
    }

    public Optional<E> getExpression() {
        return expression;
    }

    public double getPadding() {
        return DEFAULT_PADDING;
    }

    public GridCellTuple getParentInformation() {
        return parent;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    //Package protected getter for Unit Tests.
    Optional<String> getNodeUUID() {
        return nodeUUID;
    }

    public double getMinimumWidth() {
        double minimumWidth = 0;
        final int columnCount = model.getColumnCount();
        final List<GridColumn<?>> uiColumns = model.getColumns();
        for (int columnIndex = 0; columnIndex < columnCount - 1; columnIndex++) {
            final GridColumn editorColumn = uiColumns.get(columnIndex);
            minimumWidth = minimumWidth + editorColumn.getWidth();
        }
        if (columnCount > 0) {
            minimumWidth = minimumWidth + uiColumns.get(columnCount - 1).getMinimumWidth();
        }
        return minimumWidth;
    }

    public void resize(final Function<BaseExpressionGrid, Double> requiredWidthSupplier) {
        doResize(new GridLayerRedrawManager.PrioritizedCommand(0) {
                     @Override
                     public void execute() {
                         gridLayer.draw();
                     }
                 },
                 requiredWidthSupplier);
    }

    protected void doResize(final GridLayerRedrawManager.PrioritizedCommand command,
                            final Function<BaseExpressionGrid, Double> requiredWidthSupplier) {
        final double proposedWidth = getWidth() + getPadding() * 2;
        parent.proposeContainingColumnWidth(proposedWidth, requiredWidthSupplier);

        gridPanel.refreshScrollPosition();
        gridPanel.updatePanelSize();
        parent.onResize();

        gridLayer.batch(command);
    }

    public void selectFirstCell() {
        final GridData uiModel = getModel();
        if (uiModel.getRowCount() == 0 || uiModel.getColumnCount() == 0) {
            return;
        }

        uiModel.clearSelections();
        uiModel.getColumns()
                .stream()
                .filter(c -> !(c instanceof IsRowDragHandle))
                .map(c -> uiModel.getColumns().indexOf(c))
                .findFirst()
                .ifPresent(index -> selectCell(0, index, false, false));
    }

    @Override
    public boolean selectCell(final Point2D ap,
                              final boolean isShiftKeyDown,
                              final boolean isControlKeyDown) {
        final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(this,
                                                                     ap.getY());
        final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex(this,
                                                                           ap.getX());
        if (uiRowIndex == null || uiColumnIndex == null) {
            return false;
        }

        gridLayer.select(this);

        final boolean isSelectionChanged = super.selectCell(uiRowIndex,
                                                            uiColumnIndex,
                                                            isShiftKeyDown,
                                                            isControlKeyDown);
        if (isSelectionChanged) {
            doAfterSelectionChange(uiRowIndex, uiColumnIndex);
        }

        return isSelectionChanged;
    }

    @Override
    public boolean selectCell(final int uiRowIndex,
                              final int uiColumnIndex,
                              final boolean isShiftKeyDown,
                              final boolean isControlKeyDown) {
        gridLayer.select(this);
        final boolean isSelectionChanged = super.selectCell(uiRowIndex,
                                                            uiColumnIndex,
                                                            isShiftKeyDown,
                                                            isControlKeyDown);
        if (isSelectionChanged) {
            doAfterSelectionChange(uiRowIndex, uiColumnIndex);
        }

        return isSelectionChanged;
    }

    @Override
    public boolean selectHeaderCell(final Point2D ap,
                                    final boolean isShiftKeyDown,
                                    final boolean isControlKeyDown) {
        final Integer uiHeaderRowIndex = CoordinateUtilities.getUiHeaderRowIndex(this,
                                                                                 ap);
        final Integer uiHeaderColumnIndex = CoordinateUtilities.getUiColumnIndex(this,
                                                                                 ap.getX());
        if (uiHeaderRowIndex == null || uiHeaderColumnIndex == null) {
            return false;
        }

        final boolean isSelectionChanged = super.selectHeaderCell(uiHeaderRowIndex,
                                                                  uiHeaderColumnIndex,
                                                                  isShiftKeyDown,
                                                                  isControlKeyDown);

        if (isSelectionChanged) {
            doAfterHeaderSelectionChange(uiHeaderRowIndex, uiHeaderColumnIndex);
        }

        return isSelectionChanged;
    }

    @Override
    public boolean selectHeaderCell(final int uiHeaderRowIndex,
                                    final int uiHeaderColumnIndex,
                                    final boolean isShiftKeyDown,
                                    final boolean isControlKeyDown) {
        final boolean isSelectionChanged = super.selectHeaderCell(uiHeaderRowIndex,
                                                                  uiHeaderColumnIndex,
                                                                  isShiftKeyDown,
                                                                  isControlKeyDown);

        if (isSelectionChanged) {
            doAfterHeaderSelectionChange(uiHeaderRowIndex, uiHeaderColumnIndex);
        }

        return isSelectionChanged;
    }

    @Override
    public boolean adjustSelection(final SelectionExtension direction,
                                   final boolean isShiftKeyDown) {
        final boolean isSelectionChanged = super.adjustSelection(direction, isShiftKeyDown);
        if (isSelectionChanged) {
            if (getModel().getSelectedCells().size() > 0) {
                final GridData.SelectedCell selectedCell = getModel().getSelectedCells().get(0);
                doAfterSelectionChange(selectedCell.getRowIndex(),
                                       ColumnIndexUtilities.findUiColumnIndex(getModel().getColumns(),
                                                                              selectedCell.getColumnIndex()));
            } else if (getModel().getSelectedHeaderCells().size() > 0) {
                final GridData.SelectedCell selectedHeraderCell = getModel().getSelectedHeaderCells().get(0);
                doAfterHeaderSelectionChange(selectedHeraderCell.getRowIndex(),
                                             ColumnIndexUtilities.findUiColumnIndex(getModel().getColumns(),
                                                                                    selectedHeraderCell.getColumnIndex()));
            }
        }
        return isSelectionChanged;
    }

    @Override
    public void destroyResources() {
        cellEditorControls.hide();
    }

    protected boolean hasAnyHeaderCellSelected() {
        return getModel().getSelectedHeaderCells().size() > 0;
    }

    protected boolean hasMultipleCellsSelected() {
        return getModel().getSelectedCells().size() > 1;
    }

    public void doAfterSelectionChange(final int uiRowIndex,
                                       final int uiColumnIndex) {
        fireDomainObjectSelectionEvent(new NOPDomainObject());
    }

    public void doAfterHeaderSelectionChange(final int uiHeaderRowIndex,
                                             final int uiHeaderColumnIndex) {
        fireDomainObjectSelectionEvent(new NOPDomainObject());
    }

    public void selectExpressionEditorFirstCell(final int uiRowIndex,
                                                final int uiColumnIndex) {
        final GridCellValue<?> value = model.getCell(uiRowIndex, uiColumnIndex).getValue();
        final Optional<BaseExpressionGrid> grid = ((ExpressionCellValue) value).getValue();
        grid.ifPresent(beg -> {
            ((DMNGridLayer) getLayer()).select(beg);
            beg.selectFirstCell();
        });
    }

    public Optional<BaseExpressionGrid> findParentGrid() {
        final GridWidget gridWidget = parent.getGridWidget();
        if (gridWidget instanceof BaseExpressionGrid) {
            return Optional.of((BaseExpressionGrid) gridWidget);
        }
        return Optional.empty();
    }
}
