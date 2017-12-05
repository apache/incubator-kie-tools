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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.AddParameterCommand;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

public class FunctionGrid extends BaseExpressionGrid<FunctionDefinition, FunctionUIModelMapper> implements FunctionGridControls.Presenter {

    private static final double DEFAULT_WIDTH = 300.0;

    private FunctionGridControls controls;

    public FunctionGrid(final GridCellTuple parent,
                        final HasExpression hasExpression,
                        final Optional<FunctionDefinition> expression,
                        final Optional<HasName> hasName,
                        final DMNGridPanel gridPanel,
                        final DMNGridLayer gridLayer,
                        final SessionManager sessionManager,
                        final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                        final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                        final FunctionGridControls controls,
                        final boolean nested) {
        super(parent,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              new FunctionGridRenderer(nested),
              sessionManager,
              sessionCommandManager,
              editorSelectedEvent);
        this.controls = controls;
        this.controls.init(this);

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);
    }

    @Override
    public FunctionUIModelMapper makeUiModelMapper() {
        return new FunctionUIModelMapper(this::getModel,
                                         () -> expression);
    }

    @Override
    protected void initialiseUiColumns() {
        final TextAreaSingletonDOMElementFactory factory = new TextAreaSingletonDOMElementFactory(gridPanel,
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
        final GridColumn literalExpressionColumn = new FunctionColumn(new FunctionColumnNameHeaderMetaData(() -> hasName.orElse(HasName.NOP).getName().getValue(),
                                                                                                           (s) -> hasName.orElse(HasName.NOP).getName().setValue(s),
                                                                                                           headerFactory),
                                                                      new FunctionColumnParametersHeaderMetaData(this::extractExpressionLanguage,
                                                                                                                 this::extractFormalParameters),
                                                                      factory,
                                                                      this);
        literalExpressionColumn.setWidth(DEFAULT_WIDTH);

        model.appendColumn(literalExpressionColumn);
    }

    private String extractExpressionLanguage() {
        if (expression.isPresent()) {
            final FunctionDefinition function = expression.get();
            final Expression e = function.getExpression();
            final LiteralExpression le = (LiteralExpression) e;
            return le.getExpressionLanguage();
        } else {
            return "";
        }
    }

    private List<InformationItem> extractFormalParameters() {
        if (expression.isPresent()) {
            return expression.get().getFormalParameter();
        }
        return Collections.emptyList();
    }

    @Override
    protected void initialiseUiModel() {
        expression.ifPresent(e -> {
            model.appendRow(new DMNGridRow());
            uiModelMapper.fromDMNModel(0,
                                       0);
        });
    }

    @Override
    public Optional<IsElement> getEditorControls() {
        return Optional.of(controls);
    }

    @Override
    public void addFormalParameter() {
        expression.ifPresent(function -> {
            final InformationItem parameter = new InformationItem();
            parameter.setName(new Name("p" + function.getFormalParameter().size()));

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddParameterCommand(function,
                                                                  parameter,
                                                                  gridLayer::batch));
        });
    }
}
