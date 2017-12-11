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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;

public class UndefinedExpressionGrid extends BaseExpressionGrid<Expression, UndefinedExpressionUIModelMapper> {

    private static final String EXPRESSION_COLUMN_GROUP = "UndefinedExpressionGrid$ExpressionColumn";

    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private boolean nested;

    public UndefinedExpressionGrid(final GridCellTuple parent,
                                   final HasExpression hasExpression,
                                   final Optional<Expression> expression,
                                   final Optional<HasName> hasName,
                                   final DMNGridPanel gridPanel,
                                   final DMNGridLayer gridLayer,
                                   final SessionManager sessionManager,
                                   final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                   final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                   final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                                   final boolean nested) {
        super(parent,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              new UndefinedExpressionGridRenderer(),
              sessionManager,
              sessionCommandManager,
              editorSelectedEvent);
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.nested = nested;

        //Render the cell content to Lienzo's SelectionLayer so we can handle Events on child elements
        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> true);

        super.doInitialisation();
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // UndefinedExpressionColumn needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public UndefinedExpressionUIModelMapper makeUiModelMapper() {
        return new UndefinedExpressionUIModelMapper(this::getModel,
                                                    () -> expression,
                                                    hasExpression);
    }

    @Override
    protected void initialiseUiColumns() {
        final GridColumn undefinedExpressionColumn = new UndefinedExpressionColumn(new BaseHeaderMetaData("",
                                                                                                          EXPRESSION_COLUMN_GROUP),
                                                                                   expressionEditorDefinitionsSupplier,
                                                                                   this);
        undefinedExpressionColumn.setMovable(false);
        undefinedExpressionColumn.setResizable(false);

        model.appendColumn(undefinedExpressionColumn);
    }

    @Override
    protected void initialiseUiModel() {
        model.appendRow(new DMNGridRow());
    }

    @Override
    public Optional<IsElement> getEditorControls() {
        return Optional.empty();
    }

    public void onExpressionTypeChanged(final ExpressionType type) {
        final Optional<Expression> expression = expressionEditorDefinitionsSupplier
                .get()
                .stream()
                .filter(e -> e.getType().equals(type))
                .map(ExpressionEditorDefinition::getModelClass)
                .findFirst()
                .get();

        final Optional<ExpressionEditorDefinition<Expression>> expressionEditorDefinition = expressionEditorDefinitionsSupplier.get().getExpressionEditorDefinition(expression);
        expressionEditorDefinition.ifPresent(ed -> {
            final Optional<BaseExpressionGrid> editor = ed.getEditor(parent,
                                                                     hasExpression,
                                                                     expression,
                                                                     hasName,
                                                                     nested);
            final GridCellValueTuple gcv = new GridCellValueTuple<>(parent.getRowIndex(),
                                                                    parent.getColumnIndex(),
                                                                    parent.getGridData(),
                                                                    new ExpressionCellValue(editor));

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new SetCellValueCommand(gcv,
                                                                  () -> uiModelMapper,
                                                                  () -> {
                                                                      parent.onResize();
                                                                      gridPanel.refreshScrollPosition();
                                                                      gridPanel.updatePanelSize();
                                                                      gridLayer.batch(new GridLayerRedrawManager.PrioritizedCommand(0) {
                                                                          @Override
                                                                          public void execute() {
                                                                              gridLayer.draw();
                                                                              gridLayer.select(editor.get());
                                                                          }
                                                                      });
                                                                  }));
        });
    }
}
