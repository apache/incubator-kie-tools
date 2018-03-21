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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Binding;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.AddParameterBindingCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.DeleteParameterBindingCommand;
import org.kie.workbench.common.dmn.client.commands.general.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumnHeaderMetaData;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

public class InvocationGrid extends BaseExpressionGrid<Invocation, InvocationUIModelMapper> implements HasListSelectorControl {

    private static final String EXPRESSION_COLUMN_GROUP = "InvocationGrid$ExpressionColumn1";

    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private final ListSelectorView.Presenter listSelector;

    public InvocationGrid(final GridCellTuple parent,
                          final HasExpression hasExpression,
                          final Optional<Invocation> expression,
                          final Optional<HasName> hasName,
                          final DMNGridPanel gridPanel,
                          final DMNGridLayer gridLayer,
                          final SessionManager sessionManager,
                          final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                          final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                          final CellEditorControlsView.Presenter cellEditorControls,
                          final TranslationService translationService,
                          final ListSelectorView.Presenter listSelector,
                          final int nesting) {
        super(parent,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              new InvocationGridData(new DMNGridData(),
                                     sessionManager,
                                     sessionCommandManager,
                                     expression,
                                     gridLayer::batch),
              new InvocationGridRenderer(nesting > 0),
              sessionManager,
              sessionCommandManager,
              cellEditorControls,
              translationService,
              nesting);
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.listSelector = listSelector;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // makeUiModelMapper needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public InvocationUIModelMapper makeUiModelMapper() {
        return new InvocationUIModelMapper(this,
                                           this::getModel,
                                           () -> expression,
                                           expressionEditorDefinitionsSupplier,
                                           listSelector,
                                           nesting);
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
        final ExpressionEditorColumn expressionColumn = new ExpressionEditorColumn(gridLayer,
                                                                                   Arrays.asList(new BaseHeaderMetaData("",
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
    protected boolean isHeaderHidden() {
        return false;
    }

    @Override
    @SuppressWarnings("unused")
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.InvocationEditor_InsertParameterAbove),
                                             true,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 expression.ifPresent(e -> addParameterBinding(uiRowIndex));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.InvocationEditor_InsertParameterBelow),
                                             true,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 expression.ifPresent(e -> addParameterBinding(uiRowIndex + 1));
                                             }));
        items.add(ListSelectorTextItem.build(translationService.format(DMNEditorConstants.InvocationEditor_DeleteParameter),
                                             model.getRowCount() > 1,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 deleteParameterBinding(uiRowIndex);
                                             }));

        //If not ExpressionEditor column don't add extra items
        if (uiColumnIndex != InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX) {
            return items;
        }

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
                                                 clearExpressionType(uiRowIndex);
                                             }));

        return items;
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        final ListSelectorTextItem li = (ListSelectorTextItem) item;
        li.getCommand().execute();
    }

    void addParameterBinding(final int index) {
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
                                                                         index,
                                                                         uiModelMapper,
                                                                         this::synchroniseView));
        });
    }

    void deleteParameterBinding(final int index) {
        expression.ifPresent(invocation -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteParameterBindingCommand(invocation,
                                                                            model,
                                                                            index,
                                                                            this::synchroniseView));
        });
    }

    void clearExpressionType(final int uiRowIndex) {
        final GridCellTuple gc = new GridCellTuple(uiRowIndex,
                                                   InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX,
                                                   this);
        final HasExpression hasExpression = expression.get().getBinding().get(uiRowIndex);
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new ClearExpressionTypeCommand(gc,
                                                                     hasExpression,
                                                                     uiModelMapper,
                                                                     () -> synchroniseViewWhenExpressionEditorChanged(this)));
    }
}
