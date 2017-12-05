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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGridRenderer;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

public class FunctionSupplementaryGrid extends BaseExpressionGrid<Context, ContextUIModelMapper> {

    private static final String EXPRESSION_COLUMN_GROUP = "FunctionSupplementaryGrid$ExpressionColumn1";

    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    public FunctionSupplementaryGrid(final GridCellTuple parent,
                                     final HasExpression hasExpression,
                                     final Optional<Context> expression,
                                     final Optional<HasName> hasName,
                                     final DMNGridPanel gridPanel,
                                     final DMNGridLayer gridLayer,
                                     final SessionManager sessionManager,
                                     final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                     final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                     final Event<ExpressionEditorSelectedEvent> editorSelectedEvent) {
        super(parent,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              new FunctionSupplementaryGridData(new DMNGridData(gridLayer),
                                                sessionManager,
                                                sessionCommandManager,
                                                expression,
                                                gridLayer::batch),
              new ContextGridRenderer(true),
              sessionManager,
              sessionCommandManager,
              editorSelectedEvent);
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
        return new ContextUIModelMapper(this::getModel,
                                        () -> expression,
                                        expressionEditorDefinitionsSupplier);
    }

    @Override
    public void initialiseUiColumns() {
        final DMNGridColumn<String> nameColumn = new NameColumn(this);
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
}
