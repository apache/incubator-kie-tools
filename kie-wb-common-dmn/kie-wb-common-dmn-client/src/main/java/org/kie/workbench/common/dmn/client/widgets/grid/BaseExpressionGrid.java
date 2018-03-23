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
import java.util.function.Function;
import java.util.function.Predicate;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHeaderValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHeaderValueCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderGridWidgetMouseDoubleClickHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

public abstract class BaseExpressionGrid<E extends Expression, M extends BaseUIModelMapper<E>> extends BaseGridWidget {

    public static final double DEFAULT_PADDING = 10.0;

    protected final GridCellTuple parent;
    protected final Optional<String> nodeUUID;

    protected final HasExpression hasExpression;
    protected final Optional<E> expression;
    protected final Optional<HasName> hasName;

    protected final DMNGridPanel gridPanel;
    protected final DMNGridLayer gridLayer;

    protected final DefinitionUtils definitionUtils;
    protected final SessionManager sessionManager;
    protected final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    protected final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    protected final CellEditorControlsView.Presenter cellEditorControls;
    protected final ListSelectorView.Presenter listSelector;

    protected final TranslationService translationService;

    protected M uiModelMapper;

    protected final int nesting;

    public BaseExpressionGrid(final GridCellTuple parent,
                              final Optional<String> nodeUUID,
                              final HasExpression hasExpression,
                              final Optional<E> expression,
                              final Optional<HasName> hasName,
                              final DMNGridPanel gridPanel,
                              final DMNGridLayer gridLayer,
                              final GridRenderer gridRenderer,
                              final DefinitionUtils definitionUtils,
                              final SessionManager sessionManager,
                              final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                              final CellEditorControlsView.Presenter cellEditorControls,
                              final ListSelectorView.Presenter listSelector,
                              final TranslationService translationService,
                              final int nesting) {
        this(parent,
             nodeUUID,
             hasExpression,
             expression,
             hasName,
             gridPanel,
             gridLayer,
             new DMNGridData(),
             gridRenderer,
             definitionUtils,
             sessionManager,
             sessionCommandManager,
             canvasCommandFactory,
             cellEditorControls,
             listSelector,
             translationService,
             nesting);
    }

    public BaseExpressionGrid(final GridCellTuple parent,
                              final Optional<String> nodeUUID,
                              final HasExpression hasExpression,
                              final Optional<E> expression,
                              final Optional<HasName> hasName,
                              final DMNGridPanel gridPanel,
                              final DMNGridLayer gridLayer,
                              final GridData gridData,
                              final GridRenderer gridRenderer,
                              final DefinitionUtils definitionUtils,
                              final SessionManager sessionManager,
                              final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                              final CellEditorControlsView.Presenter cellEditorControls,
                              final ListSelectorView.Presenter listSelector,
                              final TranslationService translationService,
                              final int nesting) {
        super(gridData,
              gridLayer,
              gridLayer,
              gridRenderer);
        this.parent = parent;
        this.nodeUUID = nodeUUID;
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.definitionUtils = definitionUtils;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.cellEditorControls = cellEditorControls;
        this.listSelector = listSelector;
        this.translationService = translationService;

        this.hasExpression = hasExpression;
        this.expression = expression;
        this.hasName = hasName;
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

    protected Function<GridCellTuple, Command> newCellHasNoValueCommand() {
        return (gc) -> new DeleteCellValueCommand(gc,
                                                  () -> uiModelMapper,
                                                  gridLayer::batch);
    }

    protected Function<GridCellValueTuple, Command> newCellHasValueCommand() {
        return (gcv) -> new SetCellValueCommand(gcv,
                                                () -> uiModelMapper,
                                                gridLayer::batch);
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

    protected Function<GridCellTuple, Command> newHeaderHasNameHasNoValueCommand() {
        return (gcv) -> {
            final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommand.Builder<>();
            commandBuilder.addCommand(new DeleteHeaderValueCommand(extractEditableHeaderMetaData(gcv),
                                                                   gridLayer::batch));
            getUpdateStunnerTitleCommand("").ifPresent(commandBuilder::addCommand);
            return commandBuilder.build();
        };
    }

    protected Function<GridCellValueTuple, Command> newHeaderHasNameHasValueCommand() {
        return (gcv) -> {
            final String title = gcv.getValue().getValue().toString();
            final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommand.Builder<>();
            commandBuilder.addCommand(new SetHeaderValueCommand(title,
                                                                extractEditableHeaderMetaData(gcv),
                                                                gridLayer::batch));
            getUpdateStunnerTitleCommand(title).ifPresent(commandBuilder::addCommand);
            return commandBuilder.build();
        };
    }

    protected Optional<AbstractCanvasGraphCommand> getUpdateStunnerTitleCommand(final String value) {
        AbstractCanvasGraphCommand command = null;
        if (nodeUUID.isPresent()) {
            final String uuid = nodeUUID.get();
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

    public TextBoxSingletonDOMElementFactory getBodyTextBoxFactory() {
        return new TextBoxSingletonDOMElementFactory(gridPanel,
                                                     gridLayer,
                                                     this,
                                                     sessionManager,
                                                     sessionCommandManager,
                                                     newCellHasNoValueCommand(),
                                                     newCellHasValueCommand());
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

    public TextBoxSingletonDOMElementFactory getHeaderHasNameTextBoxFactory() {
        return new TextBoxSingletonDOMElementFactory(gridPanel,
                                                     gridLayer,
                                                     this,
                                                     sessionManager,
                                                     sessionCommandManager,
                                                     newHeaderHasNameHasNoValueCommand(),
                                                     newHeaderHasNameHasValueCommand());
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
    protected NodeMouseDoubleClickHandler getGridMouseDoubleClickHandler(final GridSelectionManager selectionManager,
                                                                         final GridPinnedModeManager pinnedModeManager) {
        return new EditableHeaderGridWidgetMouseDoubleClickHandler(this,
                                                                   selectionManager,
                                                                   pinnedModeManager,
                                                                   renderer);
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
        selectFirstCell();
        super.select();
    }

    @Override
    public void deselect() {
        getModel().clearSelections();
        super.deselect();
    }

    @Override
    protected void executeRenderQueueCommands(final boolean isSelectionLayer) {
        final List<Pair<Group, GridRenderer.RendererCommand>> gridLineCommands = new ArrayList<>();
        final List<Pair<Group, GridRenderer.RendererCommand>> allOtherCommands = new ArrayList<>();
        final List<Pair<Group, GridRenderer.RendererCommand>> selectedCellsCommands = new ArrayList<>();
        for (Pair<Group, List<GridRenderer.RendererCommand>> p : renderQueue) {
            final Group parent = p.getK1();
            final List<GridRenderer.RendererCommand> commands = p.getK2();
            for (GridRenderer.RendererCommand command : commands) {
                if (command instanceof GridRenderer.RenderSelectedCellsCommand) {
                    selectedCellsCommands.add(new Pair<>(parent, command));
                } else if (command instanceof GridRenderer.RenderHeaderGridLinesCommand) {
                    gridLineCommands.add(new Pair<>(parent, command));
                } else if (command instanceof GridRenderer.RenderBodyGridLinesCommand) {
                    gridLineCommands.add(new Pair<>(parent, command));
                } else {
                    allOtherCommands.add(new Pair<>(parent, command));
                }
            }
        }

        final Predicate<Pair<Group, GridRenderer.RendererCommand>> renderHeader = (p) -> {
            final GridRenderer.RendererCommand command = p.getK2();
            if (isHeaderHidden()) {
                return !(command instanceof GridRenderer.RendererHeaderCommand);
            }
            return true;
        };

        renderQueue.clear();
        allOtherCommands.stream().filter(renderHeader).forEach(p -> addCommandToRenderQueue(p.getK1(), p.getK2()));
        gridLineCommands.stream().filter(renderHeader).forEach(p -> addCommandToRenderQueue(p.getK1(), p.getK2()));
        selectedCellsCommands.stream().filter(renderHeader).forEach(p -> addCommandToRenderQueue(p.getK1(), p.getK2()));

        super.executeRenderQueueCommands(isSelectionLayer);
    }

    protected abstract boolean isHeaderHidden();

    public Optional<E> getExpression() {
        return expression;
    }

    public double getPadding() {
        return DEFAULT_PADDING;
    }

    public GridCellTuple getParentInformation() {
        return parent;
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

    public void synchroniseView() {
        gridPanel.refreshScrollPosition();
        gridPanel.updatePanelSize();
        parent.onResize();

        gridLayer.batch(new GridLayerRedrawManager.PrioritizedCommand(0) {
            @Override
            public void execute() {
                gridLayer.draw();
            }
        });
    }

    public void synchroniseViewWhenExpressionEditorChanged(final BaseExpressionGrid editor) {
        final double proposedWidth = editor.getWidth() + editor.getPadding() * 2;
        parent.proposeContainingColumnWidth(proposedWidth);
        gridPanel.refreshScrollPosition();
        gridPanel.updatePanelSize();
        parent.onResize();

        gridLayer.batch(new GridLayerRedrawManager.PrioritizedCommand(0) {
            @Override
            public void execute() {
                gridLayer.draw();
                gridLayer.select(editor);
            }
        });
    }

    public void selectFirstCell() {
        final GridData uiModel = getModel();
        if (uiModel.getRowCount() == 0 || uiModel.getColumnCount() == 0) {
            gridLayer.getGridWidgets().forEach(gw -> gw.getModel().clearSelections());
        }
        uiModel.getColumns()
                .stream()
                .filter(c -> !(c instanceof RowNumberColumn))
                .map(c -> uiModel.getColumns().indexOf(c))
                .findFirst()
                .ifPresent(index -> uiModel.selectCell(0, index));
    }

    public Optional<BaseExpressionGrid> findParentGrid() {
        final GridWidget gridWidget = parent.getGridWidget();
        if (gridWidget instanceof BaseExpressionGrid) {
            return Optional.of((BaseExpressionGrid) gridWidget);
        }
        return Optional.empty();
    }
}
