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

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Binding;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.AddParameterBindingCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumnHeaderMetaData;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
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

public class InvocationGrid extends BaseExpressionGrid<Invocation, InvocationUIModelMapper> implements InvocationGridControls.Presenter {

    private static final String EXPRESSION_COLUMN_GROUP = "InvocationGrid$ExpressionColumn1";

    private final InvocationGridControls controls;
    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    public InvocationGrid(final GridCellTuple parent,
                          final HasExpression hasExpression,
                          final Optional<Invocation> expression,
                          final Optional<HasName> hasName,
                          final DMNGridPanel gridPanel,
                          final DMNGridLayer gridLayer,
                          final SessionManager sessionManager,
                          final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                          final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                          final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                          final InvocationGridControls controls,
                          final boolean nested) {
        super(parent,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              new InvocationGridData(new DMNGridData(gridLayer),
                                     sessionManager,
                                     sessionCommandManager,
                                     expression,
                                     gridLayer::batch),
              new InvocationGridRenderer(nested),
              sessionManager,
              sessionCommandManager,
              editorSelectedEvent);
        this.controls = controls;
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();

        this.controls.init(this);
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // makeUiModelMapper needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public InvocationUIModelMapper makeUiModelMapper() {
        return new InvocationUIModelMapper(this::getModel,
                                           () -> expression,
                                           expressionEditorDefinitionsSupplier);
    }

    @Override
    protected void initialiseUiColumns() {
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

        final InvocationColumnExpressionHeaderMetaData expressionHeaderMetaData = new InvocationColumnExpressionHeaderMetaData(this::getExpressionText,
                                                                                                                               this::setExpressionText,
                                                                                                                               headerFactory);
        final NameColumn nameColumn = new NameColumn(Arrays.asList(new NameColumnHeaderMetaData(() -> hasName.orElse(HasName.NOP).getName().getValue(),
                                                                                                (s) -> hasName.orElse(HasName.NOP).getName().setValue(s),
                                                                                                headerFactory),
                                                                   expressionHeaderMetaData),
                                                     factory,
                                                     this);
        final ExpressionEditorColumn expressionColumn = new ExpressionEditorColumn(Arrays.asList(new BaseHeaderMetaData("",
                                                                                                                        EXPRESSION_COLUMN_GROUP),
                                                                                                 expressionHeaderMetaData),
                                                                                   this);

        model.appendColumn(new RowNumberColumn());
        model.appendColumn(nameColumn);
        model.appendColumn(expressionColumn);

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
    }

    private String getExpressionText() {
        return ((LiteralExpression) expression.get().getExpression()).getText();
    }

    private void setExpressionText(final String text) {
        ((LiteralExpression) expression.get().getExpression()).setText(text);
    }

    @Override
    protected void initialiseUiModel() {
        expression.ifPresent(invocation -> {
            invocation.getBinding().stream().forEach(binding -> {
                model.appendRow(new DMNGridRow());
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           InvocationUIModelMapper.ROW_NUMBER_COLUMN_INDEX);
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX);
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX);
            });
        });
    }

    @Override
    public Optional<IsElement> getEditorControls() {
        return Optional.of(controls);
    }

    @Override
    public void addParameterBinding() {
        expression.ifPresent(invocation -> {
            final Binding binding = new Binding();
            final InformationItem parameter = new InformationItem();
            parameter.setName(new Name("p" + invocation.getBinding().size()));
            binding.setParameter(parameter);
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddParameterBindingCommand(invocation,
                                                                         binding,
                                                                         model,
                                                                         new DMNGridRow(),
                                                                         uiModelMapper,
                                                                         () -> {
                                                                             gridPanel.refreshScrollPosition();
                                                                             gridPanel.updatePanelSize();
                                                                             gridLayer.batch();
                                                                         }));
        });
    }
}
