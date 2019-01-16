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

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Binding;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InvocationUIModelMapperTest {

    @Mock
    private GridWidget gridWidget;

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private InvocationParameterColumn uiNameColumn;

    @Mock
    private ExpressionEditorColumn uiExpressionEditorColumn;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private LiteralExpressionGrid literalExpressionEditor;

    @Captor
    private ArgumentCaptor<GridCellTuple> parentCaptor;

    private LiteralExpression literalExpression = new LiteralExpression();

    private BaseGridData uiModel;

    private Invocation invocation;

    private Supplier<Optional<GridCellValue<?>>> cellValueSupplier;

    private InvocationUIModelMapper mapper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.uiModel = new BaseGridData();
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiNameColumn);
        this.uiModel.appendColumn(uiExpressionEditorColumn);
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiNameColumn).getIndex();
        doReturn(2).when(uiExpressionEditorColumn).getIndex();
        doReturn(uiModel).when(gridWidget).getModel();

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);

        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditor).getExpression();
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(Optional.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyInt());

        final LiteralExpression invocationExpression = new LiteralExpression();
        invocationExpression.getText().setValue("invocation-expression");
        final LiteralExpression bindingExpression = new LiteralExpression();
        bindingExpression.getText().setValue("binding-expression");
        final Binding binding = new Binding();
        final InformationItem parameter = new InformationItem();
        parameter.setName(new Name("p0"));
        binding.setParameter(parameter);
        binding.setExpression(bindingExpression);

        this.invocation = new Invocation();
        this.invocation.setExpression(invocationExpression);
        this.invocation.getBinding().add(binding);

        this.mapper = new InvocationUIModelMapper(gridWidget,
                                                  () -> uiModel,
                                                  () -> Optional.of(invocation),
                                                  expressionEditorDefinitionsSupplier,
                                                  listSelector,
                                                  0);
        this.cellValueSupplier = Optional::empty;
    }

    @Test
    public void testFromDMNModelRowNumber() {
        mapper.fromDMNModel(0, 0);

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(RowSelectionStrategy.INSTANCE,
                     uiModel.getCell(0, 0).getSelectionStrategy());
    }

    @Test
    public void testFromDMNModelBindingParameter() {
        mapper.fromDMNModel(0, 1);

        assertEquals("p0",
                     ((InformationItemCell.HasNameAndDataTypeCell) uiModel.getCell(0, 1).getValue().getValue()).getName().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFromDMNModelBindingExpression() {
        mapper.fromDMNModel(0, 2);

        assertNotNull(uiModel.getCell(0, 2));

        assertTrue(uiModel.getCell(0, 2).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv = (ExpressionCellValue) uiModel.getCell(0, 2).getValue();
        assertEquals(literalExpressionEditor,
                     dcv.getValue().get());

        verify(literalExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                            eq(Optional.empty()),
                                                            eq(invocation.getBinding().get(0)),
                                                            eq(Optional.of(invocation.getBinding().get(0).getExpression())),
                                                            eq(Optional.of(invocation.getBinding().get(0).getParameter())),
                                                            eq(1));
        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(0, parent.getRowIndex());
        assertEquals(2, parent.getColumnIndex());
        assertEquals(gridWidget, parent.getGridWidget());
    }

    @Test
    public void testToDMNModelBindingParameter() {
        cellValueSupplier = () -> Optional.of(new BaseGridCellValue<>("updated"));

        mapper.toDMNModel(0,
                          1,
                          cellValueSupplier);

        assertEquals("updated",
                     invocation.getBinding().get(0).getParameter().getName().getValue());
    }

    @Test
    public void testToDMNModelBindingExpression() {
        cellValueSupplier = () -> Optional.of(new ExpressionCellValue(Optional.of(literalExpressionEditor)));

        mapper.toDMNModel(0,
                          2,
                          cellValueSupplier);

        assertEquals(literalExpression,
                     invocation.getBinding().get(0).getExpression());
    }
}
