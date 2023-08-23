/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.enterprise.event.Event;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.FunctionSupplementaryGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.DelegatingGridWidgetCellSelectorMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;

import static org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils.getExpressionTextLineHeight;

public abstract class LiteralExpressionPMMLGrid extends LiteralExpressionGrid {

    public LiteralExpressionPMMLGrid(final GridCellTuple parent,
                                     final Optional<String> nodeUUID,
                                     final HasExpression hasExpression,
                                     final Optional<HasName> hasName,
                                     final DMNGridPanel gridPanel,
                                     final DMNGridLayer gridLayer,
                                     final DMNGridData gridData,
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
                                     final ValueAndDataTypePopoverView.Presenter headerEditor,
                                     final ReadOnlyProvider readOnlyProvider) {
        super(parent,
              nodeUUID,
              hasExpression,
              hasName,
              gridPanel,
              gridLayer,
              gridData,
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
              headerEditor,
              readOnlyProvider);
    }

    @Override
    public List<NodeMouseEventHandler> getNodeMouseClickEventHandlers(final GridSelectionManager selectionManager) {
        final List<NodeMouseEventHandler> handlers = new ArrayList<>();
        handlers.add(new DelegatingGridWidgetCellSelectorMouseEventHandler(selectionManager,
                                                                           this::getParentInformation,
                                                                           () -> nesting));
        return handlers;
    }

    @Override
    public LiteralExpressionUIModelMapper makeUiModelMapper() {
        return new LiteralExpressionPMMLUIModelMapper(this::getModel,
                                                      getExpression(),
                                                      listSelector,
                                                      getExpressionTextLineHeight(getRenderer().getTheme()),
                                                      getPlaceHolder());
    }

    @Override
    protected void initialiseUiColumns() {
        final GridColumn literalExpressionColumn = new LiteralExpressionPMMLColumn(getBodyListBoxFactory(),
                                                                                   getAndSetInitialWidth(0, DMNGridColumn.DEFAULT_WIDTH),
                                                                                   this);

        model.appendColumn(literalExpressionColumn);
    }

    @Override
    //Increase visibility
    public Function<GridCellValueTuple, Command> newCellHasValueCommand() {
        return super.newCellHasValueCommand();
    }

    @Override
    //Increase visibility
    public Function<GridCellTuple, Command> newCellHasNoValueCommand() {
        return super.newCellHasNoValueCommand();
    }

    protected abstract String getPlaceHolder();

    protected abstract void loadValues(final Consumer<List<String>> consumer);

    protected String getExpressionPMMLValue(final String pmmlValueKey) {
        final Optional<FunctionSupplementaryGrid> oParentGridWidget = getParentFunctionSupplementaryGrid();
        if (oParentGridWidget.isPresent()) {
            final FunctionSupplementaryGrid parentGridWidget = oParentGridWidget.get();
            return parentGridWidget.getExpressionValue(pmmlValueKey);
        }
        return "";
    }

    protected Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> getExpressionPMMLValueEditor(final String pmmlValueKey) {
        final Optional<FunctionSupplementaryGrid> oParentGridWidget = getParentFunctionSupplementaryGrid();
        if (oParentGridWidget.isPresent()) {
            final FunctionSupplementaryGrid parentGridWidget = oParentGridWidget.get();
            return parentGridWidget.getExpressionValueEditor(pmmlValueKey);
        }
        return Optional.empty();
    }

    protected Optional<FunctionGrid> getParentFunctionGrid() {
        return getParentFunctionSupplementaryGrid()
                .map(pfsg -> pfsg.getParentInformation().getGridWidget())
                .filter(gw -> gw instanceof FunctionGrid)
                .map(gw -> (FunctionGrid) gw);
    }

    private Optional<FunctionSupplementaryGrid> getParentFunctionSupplementaryGrid() {
        final Optional<BaseExpressionGrid> oParentGridWidget = findParentGrid();
        if (oParentGridWidget.isPresent()) {
            final BaseExpressionGrid parentGridWidget = oParentGridWidget.get();
            if (parentGridWidget instanceof FunctionSupplementaryGrid) {
                final FunctionSupplementaryGrid fsg = (FunctionSupplementaryGrid) parentGridWidget;
                return Optional.of(fsg);
            }
        }
        return Optional.empty();
    }
}
