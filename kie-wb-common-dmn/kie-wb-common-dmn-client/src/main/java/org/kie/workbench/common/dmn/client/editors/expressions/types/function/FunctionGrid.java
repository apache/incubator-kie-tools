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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.AddParameterCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.SetKindCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;

public class FunctionGrid extends BaseExpressionGrid<FunctionDefinition, FunctionUIModelMapper> implements FunctionGridControls.Presenter {

    private static final double DEFAULT_WIDTH = 300.0;

    private final FunctionGridControls controls;
    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private final Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    public FunctionGrid(final GridCellTuple parent,
                        final HasExpression hasExpression,
                        final Optional<FunctionDefinition> expression,
                        final Optional<HasName> hasName,
                        final DMNGridPanel gridPanel,
                        final DMNGridLayer gridLayer,
                        final SessionManager sessionManager,
                        final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                        final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                        final Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier,
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
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.supplementaryEditorDefinitionsSupplier = supplementaryEditorDefinitionsSupplier;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();

        this.controls.init(this);
        this.controls.initKinds(Arrays.asList(FunctionDefinition.Kind.values()));
        this.controls.initExpressionTypes(expressionEditorDefinitionsSupplier.get().stream().map(ExpressionEditorDefinition::getType).collect(Collectors.toList()));
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // makeUiModelMapper needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public FunctionUIModelMapper makeUiModelMapper() {
        return new FunctionUIModelMapper(this::getModel,
                                         () -> expression,
                                         expressionEditorDefinitionsSupplier,
                                         supplementaryEditorDefinitionsSupplier);
    }

    @Override
    protected void initialiseUiColumns() {
        final TextBoxSingletonDOMElementFactory headerFactory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                                                      gridLayer,
                                                                                                      this,
                                                                                                      sessionManager,
                                                                                                      sessionCommandManager,
                                                                                                      newHeaderHasNoValueCommand(),
                                                                                                      newHeaderHasValueCommand());
        final GridColumn expressionColumn = new ExpressionEditorColumn(Arrays.asList(new FunctionColumnNameHeaderMetaData(() -> hasName.orElse(HasName.NOP).getName().getValue(),
                                                                                                                          (s) -> hasName.orElse(HasName.NOP).getName().setValue(s),
                                                                                                                          headerFactory),
                                                                                     new FunctionColumnParametersHeaderMetaData(this::extractExpressionLanguage,
                                                                                                                                this::extractFormalParameters)),
                                                                       this);
        expressionColumn.setWidth(DEFAULT_WIDTH);

        model.appendColumn(expressionColumn);

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
    }

    @Override
    protected void initialiseUiModel() {
        expression.ifPresent(e -> {
            model.appendRow(new DMNGridRow());
            uiModelMapper.fromDMNModel(0, 0);
        });
    }

    @Override
    public Optional<IsElement> getEditorControls() {
        controls.enableKind(false);
        controls.enableExpressionType(false);
        expression.ifPresent(e -> {
            final FunctionDefinition.Kind kind = extractExpressionLanguage();
            controls.initSelectedKind(kind);
            controls.enableKind(true);

            switch (kind) {
                case FEEL:
                    final Optional<ExpressionType> type = extractExpressionType();
                    type.ifPresent(t -> {
                        controls.initSelectedExpressionType(t);
                        controls.enableExpressionType(true);
                    });
                    break;
                case JAVA:
                case PMML:
            }
        });
        return Optional.of(controls);
    }

    private FunctionDefinition.Kind extractExpressionLanguage() {
        if (expression.isPresent()) {
            final FunctionDefinition function = expression.get();
            final Map<QName, String> attributes = function.getOtherAttributes();
            final String code = attributes.getOrDefault(FunctionDefinition.KIND_QNAME,
                                                        FunctionDefinition.Kind.FEEL.code());
            return FunctionDefinition.Kind.determineFromString(code);
        } else {
            return FunctionDefinition.Kind.FEEL;
        }
    }

    private List<InformationItem> extractFormalParameters() {
        if (expression.isPresent()) {
            final FunctionDefinition function = expression.get();
            return function.getFormalParameter();
        }
        return Collections.emptyList();
    }

    private Optional<ExpressionType> extractExpressionType() {
        if (expression.isPresent()) {
            final Expression e = expression.get().getExpression();
            final Optional<ExpressionEditorDefinition<Expression>> definition = expressionEditorDefinitionsSupplier.get().getExpressionEditorDefinition(Optional.ofNullable(e));
            if (definition.isPresent()) {
                return Optional.of(definition.get().getType());
            }
        }
        return Optional.empty();
    }

    @Override
    public void addFormalParameter() {
        expression.ifPresent(e -> {
            final InformationItem parameter = new InformationItem();
            parameter.setName(new Name("p" + e.getFormalParameter().size()));

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddParameterCommand(e,
                                                                  parameter,
                                                                  gridLayer::batch));
        });
    }

    @Override
    public void setKind(final FunctionDefinition.Kind kind) {
        expression.ifPresent(function -> {
            switch (kind) {
                case FEEL:
                    doSetKind(kind,
                              function,
                              expressionEditorDefinitionsSupplier.get().getExpressionEditorDefinition(ExpressionType.LITERAL_EXPRESSION));
                    break;
                case JAVA:
                    doSetKind(kind,
                              function,
                              supplementaryEditorDefinitionsSupplier.get().getExpressionEditorDefinition(ExpressionType.FUNCTION_JAVA));
                    break;
                case PMML:
                    doSetKind(kind,
                              function,
                              supplementaryEditorDefinitionsSupplier.get().getExpressionEditorDefinition(ExpressionType.FUNCTION_PMML));
            }
        });
    }

    private void doSetKind(final FunctionDefinition.Kind kind,
                           final FunctionDefinition function,
                           final Optional<ExpressionEditorDefinition<Expression>> oDefinition) {
        oDefinition.ifPresent(definition -> {
            final GridCellTuple expressionParent = new GridCellTuple(0, 0, model);
            final Optional<Expression> expression = definition.getModelClass();
            final Optional<GridWidget> gridWidget = definition.getEditor(expressionParent,
                                                                         hasExpression,
                                                                         expression,
                                                                         hasName,
                                                                         true);
            doSetKind(kind,
                      function,
                      expression,
                      gridWidget);
        });
    }

    void doSetKind(final FunctionDefinition.Kind kind,
                   final FunctionDefinition function,
                   final Optional<Expression> expression,
                   final Optional<GridWidget> editor) {
        final GridCellValueTuple gcv = new GridCellValueTuple<>(0,
                                                                0,
                                                                model,
                                                                new ExpressionCellValue(editor));
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new SetKindCommand(gcv,
                                                         function,
                                                         kind,
                                                         expression,
                                                         () -> synchroniseViewWhenExpressionEditorChanged(editor)));
    }

    @Override
    public void setExpressionType(final ExpressionType type) {
        final Optional<ExpressionEditorDefinition<Expression>> expressionEditorDefinition = expressionEditorDefinitionsSupplier.get().getExpressionEditorDefinition(type);
        expressionEditorDefinition.ifPresent(ed -> {
            final Optional<Expression> expression = ed.getModelClass();
            final GridCellTuple expressionParent = new GridCellTuple(0, 0, model);
            final Optional<GridWidget> editor = ed.getEditor(expressionParent,
                                                             hasExpression,
                                                             expression,
                                                             hasName,
                                                             true);
            final GridCellValueTuple gcv = new GridCellValueTuple<>(0,
                                                                    0,
                                                                    model,
                                                                    new ExpressionCellValue(editor));

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new SetCellValueCommand(gcv,
                                                                  () -> uiModelMapper,
                                                                  () -> synchroniseViewWhenExpressionEditorChanged(editor)));
        });
    }

    void synchroniseViewWhenExpressionEditorChanged(final Optional<GridWidget> oEditor) {
        parent.onResize();
        gridPanel.refreshScrollPosition();
        gridPanel.updatePanelSize();
        gridLayer.batch(new GridLayerRedrawManager.PrioritizedCommand(0) {
            @Override
            public void execute() {
                gridLayer.draw();
                oEditor.ifPresent(gridLayer::select);
            }
        });
        getEditorControls();
    }
}
