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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.IsLiteralExpression;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGridRowNumberColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridRenderer;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
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
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public class FunctionSupplementaryGrid extends BaseExpressionGrid<Context, FunctionSupplementaryGridData, ContextUIModelMapper> implements HasListSelectorControl {

    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    public FunctionSupplementaryGrid(final GridCellTuple parent,
                                     final Optional<String> nodeUUID,
                                     final HasExpression hasExpression,
                                     final Optional<HasName> hasName,
                                     final DMNGridPanel gridPanel,
                                     final DMNGridLayer gridLayer,
                                     final FunctionSupplementaryGridData gridData,
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
        return new FunctionSupplementaryGridUIModelMapper(this,
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
                                                                                          getAndSetInitialWidth(0, ContextGridRowNumberColumn.DEFAULT_WIDTH));
        final NameColumn nameColumn = new NameColumn(getAndSetInitialWidth(1, DMNGridColumn.DEFAULT_WIDTH),
                                                     this);
        final ExpressionEditorColumn expressionColumn = new ExpressionEditorColumn(gridLayer,
                                                                                   Collections.emptyList(),
                                                                                   getAndSetInitialWidth(2, ExpressionEditorColumn.DEFAULT_WIDTH),
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

    public String getExpressionValue(final String key) {
        final Optional<Context> oContext = getExpression().get();
        if (oContext.isPresent()) {
            final Context context = oContext.get();
            return context.getContextEntry()
                    .stream()
                    .filter(ce -> Objects.equals(key, ce.getVariable().getName().getValue()))
                    .findFirst()
                    .map(ContextEntry::getExpression)
                    .filter(cee -> cee instanceof IsLiteralExpression)
                    .map(cee -> (IsLiteralExpression) cee)
                    .map(ile -> StringUtils.createUnquotedString(ile.getText().getValue()))
                    .orElse("");
        }
        return "";
    }

    public Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> getExpressionValueEditor(final String key) {
        final Optional<Context> oContext = getExpression().get();
        if (oContext.isPresent()) {
            final Context context = oContext.get();
            return context.getContextEntry()
                    .stream()
                    .filter(ce -> Objects.equals(key, ce.getVariable().getName().getValue()))
                    .findFirst()
                    .map(ce -> context.getContextEntry().indexOf(ce))
                    .map(rowIndex -> model.getCell(rowIndex, ContextUIModelMapperHelper.EXPRESSION_COLUMN_INDEX))
                    .map(GridCell::getValue)
                    .filter(value -> value instanceof ExpressionCellValue)
                    .map(value -> (ExpressionCellValue) value)
                    .flatMap(ExpressionCellValue::getValue);
        }
        return Optional.empty();
    }
}
