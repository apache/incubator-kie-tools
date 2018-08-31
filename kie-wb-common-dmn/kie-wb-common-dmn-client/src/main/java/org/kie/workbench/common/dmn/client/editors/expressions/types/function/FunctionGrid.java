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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.AddParameterCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.RemoveParameterCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.SetKindCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.UpdateParameterNameCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.HasParametersControl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypeEditorView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormProperties;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.mvp.Command;

public class FunctionGrid extends BaseExpressionGrid<FunctionDefinition, DMNGridData, FunctionUIModelMapper> implements HasListSelectorControl,
                                                                                                                        HasParametersControl {

    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private final Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    private final NameAndDataTypeEditorView.Presenter headerEditor;
    private final ParametersEditorView.Presenter parametersEditor;

    public FunctionGrid(final GridCellTuple parent,
                        final Optional<String> nodeUUID,
                        final HasExpression hasExpression,
                        final Optional<FunctionDefinition> expression,
                        final Optional<HasName> hasName,
                        final DMNGridPanel gridPanel,
                        final DMNGridLayer gridLayer,
                        final DMNGridData gridData,
                        final DefinitionUtils definitionUtils,
                        final SessionManager sessionManager,
                        final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                        final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                        final Event<ExpressionEditorChanged> editorSelectedEvent,
                        final Event<RefreshFormProperties> refreshFormPropertiesEvent,
                        final CellEditorControlsView.Presenter cellEditorControls,
                        final ListSelectorView.Presenter listSelector,
                        final TranslationService translationService,
                        final int nesting,
                        final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                        final Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier,
                        final NameAndDataTypeEditorView.Presenter headerEditor,
                        final ParametersEditorView.Presenter parametersEditor) {
        super(parent,
              nodeUUID,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              gridData,
              new FunctionGridRenderer(nesting > 0),
              definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              editorSelectedEvent,
              refreshFormPropertiesEvent,
              cellEditorControls,
              listSelector,
              translationService,
              nesting);
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.supplementaryEditorDefinitionsSupplier = supplementaryEditorDefinitionsSupplier;
        this.headerEditor = headerEditor;
        this.parametersEditor = parametersEditor;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // makeUiModelMapper needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public FunctionUIModelMapper makeUiModelMapper() {
        return new FunctionUIModelMapper(this,
                                         this::getModel,
                                         () -> expression,
                                         expressionEditorDefinitionsSupplier,
                                         supplementaryEditorDefinitionsSupplier,
                                         listSelector,
                                         nesting + 1);
    }

    @Override
    protected void initialiseUiColumns() {
        HasTypeRef hasTypeRef = expression.get();
        final DMNModelInstrumentedBase base = hasExpression.asDMNModelInstrumentedBase();
        if (base instanceof HasVariable) {
            final HasVariable hasVariable = (HasVariable) base;
            hasTypeRef = hasVariable.getVariable();
        }

        final GridColumn expressionColumn = new FunctionColumn(gridLayer,
                                                               Arrays.asList(new FunctionColumnNameHeaderMetaData(hasName,
                                                                                                                  hasTypeRef,
                                                                                                                  clearDisplayNameConsumer(),
                                                                                                                  setDisplayNameConsumer(),
                                                                                                                  setTypeRefConsumer(),
                                                                                                                  cellEditorControls,
                                                                                                                  headerEditor),
                                                                             new FunctionColumnParametersHeaderMetaData(expression::get,
                                                                                                                        translationService,
                                                                                                                        cellEditorControls,
                                                                                                                        parametersEditor,
                                                                                                                        this)),
                                                               this);

        model.appendColumn(expressionColumn);

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Consumer<HasName> clearDisplayNameConsumer() {
        return (hn) -> {
            final CompositeCommand.Builder commandBuilder = newHasNameHasNoValueCommand(hn);
            getUpdateStunnerTitleCommand("").ifPresent(commandBuilder::addCommand);
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          commandBuilder.build());
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    protected BiConsumer<HasName, Name> setDisplayNameConsumer() {
        return (hn, name) -> {
            final CompositeCommand.Builder commandBuilder = newHasNameHasValueCommand(hn, name);
            getUpdateStunnerTitleCommand(name.getValue()).ifPresent(commandBuilder::addCommand);
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          commandBuilder.build());
        };
    }

    @Override
    protected void initialiseUiModel() {
        expression.ifPresent(e -> {
            model.appendRow(new DMNGridRow());
            uiModelMapper.fromDMNModel(0, 0);
        });
    }

    @Override
    protected boolean isHeaderHidden() {
        return false;
    }

    @Override
    @SuppressWarnings("unused")
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();
        final FunctionDefinition.Kind kind = KindUtilities.getKind(expression.get());
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.FunctionEditor_FEEL),
                                             !FunctionDefinition.Kind.FEEL.equals(kind),
                                             () -> {
                                                 cellEditorControls.hide();
                                                 expression.ifPresent(e -> setKind(FunctionDefinition.Kind.FEEL));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.FunctionEditor_JAVA),
                                             !FunctionDefinition.Kind.JAVA.equals(kind),
                                             () -> {
                                                 cellEditorControls.hide();
                                                 expression.ifPresent(e -> setKind(FunctionDefinition.Kind.JAVA));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.FunctionEditor_PMML),
                                             !FunctionDefinition.Kind.PMML.equals(kind),
                                             () -> {
                                                 cellEditorControls.hide();
                                                 expression.ifPresent(e -> setKind(FunctionDefinition.Kind.PMML));
                                             }));

        //If cell editor is UndefinedExpressionGrid don't add extra items
        final GridCell<?> cell = model.getCell(uiRowIndex, uiColumnIndex);
        final ExpressionCellValue ecv = (ExpressionCellValue) cell.getValue();
        if (!ecv.getValue().isPresent()) {
            return items;
        }
        final BaseExpressionGrid grid = ecv.getValue().get();
        if (grid instanceof UndefinedExpressionGrid) {
            return items;
        }

        items.add(new ListSelectorDividerItem());
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ExpressionEditor_Clear),
                                             true,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 clearExpressionType();
                                             }));

        return items;
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        final ListSelectorTextItem li = (ListSelectorTextItem) item;
        li.getCommand().execute();
    }

    @Override
    public List<InformationItem> getParameters() {
        final List<InformationItem> parameters = new ArrayList<>();
        expression.ifPresent(e -> parameters.addAll(e.getFormalParameter()));
        return parameters;
    }

    @Override
    public void addParameter(final Command onSuccess) {
        expression.ifPresent(e -> {
            final InformationItem parameter = new InformationItem();
            parameter.setName(new Name("p" + e.getFormalParameter().size()));

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddParameterCommand(e,
                                                                  parameter,
                                                                  () -> {
                                                                      gridLayer.batch();
                                                                      onSuccess.execute();
                                                                  }));
        });
    }

    @Override
    public void removeParameter(final InformationItem parameter,
                                final Command onSuccess) {
        expression.ifPresent(e -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new RemoveParameterCommand(e,
                                                                     parameter,
                                                                     () -> {
                                                                         gridLayer.batch();
                                                                         onSuccess.execute();
                                                                     }));
        });
    }

    @Override
    public void updateParameterName(final InformationItem parameter,
                                    final String name) {
        expression.ifPresent(e -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new UpdateParameterNameCommand(parameter,
                                                                         name,
                                                                         gridLayer::batch));
        });
    }

    void setKind(final FunctionDefinition.Kind kind) {
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

    void doSetKind(final FunctionDefinition.Kind kind,
                   final FunctionDefinition function,
                   final Optional<ExpressionEditorDefinition<Expression>> oDefinition) {
        oDefinition.ifPresent(definition -> {
            final GridCellTuple expressionParent = new GridCellTuple(0,
                                                                     0,
                                                                     this);
            final Optional<Expression> expression = definition.getModelClass();
            definition.enrich(nodeUUID, expression);

            final Optional<BaseExpressionGrid> gridWidget = definition.getEditor(expressionParent,
                                                                                 Optional.empty(),
                                                                                 hasExpression,
                                                                                 expression,
                                                                                 hasName,
                                                                                 nesting + 1);
            doSetKind(kind,
                      function,
                      expression,
                      gridWidget);
        });
    }

    void doSetKind(final FunctionDefinition.Kind kind,
                   final FunctionDefinition function,
                   final Optional<Expression> expression,
                   final Optional<BaseExpressionGrid> editor) {
        final GridCellValueTuple gcv = new GridCellValueTuple<>(0,
                                                                0,
                                                                this,
                                                                new ExpressionCellValue(editor));
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new SetKindCommand(gcv,
                                                         function,
                                                         kind,
                                                         expression,
                                                         () -> resizeBasedOnCellExpressionEditor(0, 0)));
    }

    void clearExpressionType() {
        expression.ifPresent(function -> {
            final GridCellTuple gc = new GridCellTuple(0,
                                                       0,
                                                       this);
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new ClearExpressionTypeCommand(gc,
                                                                         function,
                                                                         uiModelMapper,
                                                                         () -> resizeBasedOnCellExpressionEditor(0, 0)));
        });
    }
}
