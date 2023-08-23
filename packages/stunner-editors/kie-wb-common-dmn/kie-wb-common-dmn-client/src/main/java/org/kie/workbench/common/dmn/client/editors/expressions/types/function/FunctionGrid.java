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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.AddParameterCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.RemoveParameterCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.SetKindCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.UpdateParameterNameCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.UpdateParameterTypeRefCommand;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.HasKindSelectControl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.KindPopoverView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.HasParametersControl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersPopoverView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
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
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.mvp.Command;

public class FunctionGrid extends BaseExpressionGrid<FunctionDefinition, DMNGridData, FunctionUIModelMapper> implements HasListSelectorControl,
                                                                                                                        HasParametersControl,
                                                                                                                        HasKindSelectControl {

    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private final Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    private final ValueAndDataTypePopoverView.Presenter headerEditor;
    private final ParametersPopoverView.Presenter parametersEditor;
    private final KindPopoverView.Presenter kindEditor;

    public FunctionGrid(final GridCellTuple parent,
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
                        final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                        final Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier,
                        final ValueAndDataTypePopoverView.Presenter headerEditor,
                        final ParametersPopoverView.Presenter parametersEditor,
                        final KindPopoverView.Presenter kindEditor,
                        final ReadOnlyProvider readOnlyProvider) {
        super(parent,
              nodeUUID,
              hasExpression,
              hasName,
              gridPanel,
              gridLayer,
              gridData,
              new FunctionGridRenderer(gridData),
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
        this.supplementaryEditorDefinitionsSupplier = supplementaryEditorDefinitionsSupplier;
        this.headerEditor = headerEditor;
        this.parametersEditor = parametersEditor;
        this.kindEditor = kindEditor;

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
                                         getExpression(),
                                         () -> isOnlyVisualChangeAllowed,
                                         expressionEditorDefinitionsSupplier,
                                         supplementaryEditorDefinitionsSupplier,
                                         listSelector,
                                         nesting + 1);
    }

    @Override
    protected void initialiseUiColumns() {
        final List<GridColumn.HeaderMetaData> headerMetaData = new ArrayList<>();
        if (nesting == 0) {
            headerMetaData.add(new FunctionColumnNameHeaderMetaData(hasExpression,
                                                                    hasName,
                                                                    clearValueConsumer(true, new Name()),
                                                                    setValueConsumer(true),
                                                                    setTypeRefConsumer(),
                                                                    translationService,
                                                                    cellEditorControls,
                                                                    headerEditor));
        }
        headerMetaData.add(new FunctionColumnParametersHeaderMetaData(getExpression(),
                                                                      translationService,
                                                                      cellEditorControls,
                                                                      parametersEditor,
                                                                      this));

        final FunctionKindRowColumn kindColumn = new FunctionKindRowColumn(getExpression(),
                                                                           cellEditorControls,
                                                                           kindEditor,
                                                                           getAndSetInitialWidth(0, EmptyColumn.DEFAULT_WIDTH),
                                                                           this);

        final GridColumn expressionColumn = new FunctionColumn(gridLayer,
                                                               headerMetaData,
                                                               getAndSetInitialWidth(1, UndefinedExpressionColumn.DEFAULT_WIDTH),
                                                               this);

        model.appendColumn(kindColumn);
        model.appendColumn(expressionColumn);

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
    }

    @Override
    public void initialiseUiRows() {
        getExpression().get().ifPresent(e -> model.appendRow(new ExpressionEditorGridRow()));
    }

    @Override
    public void initialiseUiCells() {
        getExpression().get().ifPresent(e -> uiModelMapper.fromDMNModel(0, 1));
    }

    @Override
    @SuppressWarnings("unused")
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();
        final FunctionDefinition.Kind kind = KindUtilities.getKind(getExpression().get().get());

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

        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.ExpressionEditor_Clear),
                                             true,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 gridPanel.setFocus(true);
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
        getExpression().get().ifPresent(e -> parameters.addAll(e.getFormalParameter()));
        return parameters;
    }

    @Override
    public void addParameter(final Command onSuccess) {
        getExpression().get().ifPresent(e -> {
            final InformationItem parameter = new InformationItem();
            parameter.setName(new Name());

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
        getExpression().get().ifPresent(e -> {
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
                                    final String name,
                                    final Command onSuccess) {
        getExpression().get().ifPresent(e -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new UpdateParameterNameCommand(parameter,
                                                                         name,
                                                                         () -> {
                                                                             gridLayer.batch();
                                                                             onSuccess.execute();
                                                                         }));
        });
    }

    @Override
    public void updateParameterTypeRef(final InformationItem parameter,
                                       final QName typeRef) {
        getExpression().get().ifPresent(e -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new UpdateParameterTypeRefCommand(parameter,
                                                                            typeRef,
                                                                            gridLayer::batch));
        });
    }

    void setKind(final FunctionDefinition.Kind kind) {
        getExpression().get().ifPresent(function -> {
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
            final GridCellTuple parent = new GridCellTuple(0, 1, FunctionGrid.this);
            final Optional<Expression> expression = definition.getModelClass();
            definition.enrich(nodeUUID, hasExpression, expression);

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new SetKindCommand(parent,
                                                             function,
                                                             kind,
                                                             expression,
                                                             (editor) -> {
                                                                 editor.ifPresent(e -> {
                                                                     e.resize(BaseExpressionGrid.RESIZE_EXISTING);
                                                                     e.selectFirstCell();
                                                                 });
                                                             },
                                                             () -> {
                                                                 resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
                                                                 selectFirstCell();
                                                             },
                                                             () -> {
                                                                 Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> editor = Optional.empty();
                                                                 editor = definition.getEditor(parent,
                                                                                               Optional.empty(),
                                                                                               getExpression().get().get(),
                                                                                               hasName,
                                                                                               isOnlyVisualChangeAllowed,
                                                                                               nesting + 1);
                                                                 return editor;
                                                             }));
        });
    }

    void clearExpressionType() {
        getExpression().get().ifPresent(function -> {
            final GridCellTuple gc = new GridCellTuple(0,
                                                       1,
                                                       this);
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new ClearExpressionTypeCommand(gc,
                                                                         function,
                                                                         uiModelMapper,
                                                                         () -> {
                                                                             resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
                                                                             selectExpressionEditorFirstCell(0, 1);
                                                                         },
                                                                         () -> {
                                                                             resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
                                                                             selectExpressionEditorFirstCell(0, 1);
                                                                         }));
        });
    }

    @Override
    public void doAfterHeaderSelectionChange(final int uiHeaderRowIndex,
                                             final int uiHeaderColumnIndex) {
        if (nesting == 0 && uiHeaderRowIndex == 0) {
            final DMNModelInstrumentedBase base = hasExpression.asDMNModelInstrumentedBase();
            if (base instanceof DomainObject) {
                fireDomainObjectSelectionEvent((DomainObject) base);
                return;
            }
        }
        super.doAfterHeaderSelectionChange(uiHeaderRowIndex, uiHeaderColumnIndex);
    }

    @Override
    public void setFunctionKind(final FunctionDefinition.Kind kind) {
        setKind(kind);
    }
}