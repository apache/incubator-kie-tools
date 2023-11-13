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
package org.kie.workbench.common.dmn.client.editors.expressions.types.list;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGridCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListUIModelMapperHelper.ROW_COLUMN_INDEX;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListUIModelMapperTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private ExpressionEditorColumn uiExpressionEditorColumn;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private LiteralExpressionGrid literalExpressionEditor;

    @Mock
    private UndefinedExpressionEditorDefinition undefinedExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid undefinedExpressionEditor;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private GridWidget gridWidget;

    @Captor
    private ArgumentCaptor<GridCellTuple> parentCaptor;

    private LiteralExpression literalExpression = new LiteralExpression();

    private BaseGridData uiModel;

    private List list;

    private Supplier<Optional<GridCellValue<?>>> cellValueSupplier;

    private ListUIModelMapper mapper;

    @SuppressWarnings("unchecked")
    public void setup(final Expression expression,
                      final boolean isOnlyVisualChangeAllowedSupplier) {
        this.uiModel = new BaseGridData();
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiExpressionEditorColumn);
        when(uiRowNumberColumn.getIndex()).thenReturn(0);
        when(uiExpressionEditorColumn.getIndex()).thenReturn(1);

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);
        when(literalExpressionEditor.getExpression()).thenReturn(() -> Optional.of(literalExpression));
        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(literalExpression));
        when(literalExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                         any(Optional.class),
                                                         any(HasExpression.class),
                                                         any(Optional.class),
                                                         anyBoolean(),
                                                         anyInt())).thenReturn(Optional.of(literalExpressionEditor));

        when(undefinedExpressionEditor.getExpression()).thenReturn(Optional::empty);
        when(undefinedExpressionEditorDefinition.getModelClass()).thenReturn(Optional.empty());
        when(undefinedExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                           any(Optional.class),
                                                           any(HasExpression.class),
                                                           any(Optional.class),
                                                           anyBoolean(),
                                                           anyInt())).thenReturn(Optional.of(undefinedExpressionEditor));

        this.list = new List();
        this.list.getExpression().add(HasExpression.wrap(list, expression));

        this.mapper = new ListUIModelMapper(gridWidget,
                                            () -> uiModel,
                                            () -> Optional.of(list),
                                            () -> isOnlyVisualChangeAllowedSupplier,
                                            expressionEditorDefinitionsSupplier,
                                            listSelector,
                                            0);
        this.cellValueSupplier = Optional::empty;
    }

    @Test
    public void testFromDMNModelRowNumber() {
        setup(new LiteralExpression(), false);

        mapper.fromDMNModel(0, ROW_COLUMN_INDEX);

        assertThat(uiModel.getCell(0, ROW_COLUMN_INDEX).getValue().getValue()).isEqualTo(1);
        assertThat(uiModel.getCell(0, ROW_COLUMN_INDEX).getSelectionStrategy()).isSameAs(RowSelectionStrategy.INSTANCE);
    }

    @Test
    public void testFromDMNModelUndefinedExpression() {
        setup(null, false);
        list.getExpression().add(HasExpression.wrap(list, null));

        mapper.fromDMNModel(0, EXPRESSION_COLUMN_INDEX);

        assertFromDMNModelUndefinedExpression(false);
    }

    @Test
    public void testFromDMNModelUndefinedExpressionWhenOnlyVisualChangeAllowed() {
        setup(null, true);
        list.getExpression().add(HasExpression.wrap(list, null));

        mapper.fromDMNModel(0, EXPRESSION_COLUMN_INDEX);

        assertFromDMNModelUndefinedExpression(true);
    }

    private void assertFromDMNModelUndefinedExpression(final boolean isOnlyVisualChangeAllowed) {
        assertTrue(uiModel.getCell(0, EXPRESSION_COLUMN_INDEX).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv = (ExpressionCellValue) uiModel.getCell(0, EXPRESSION_COLUMN_INDEX).getValue();
        assertEquals(undefinedExpressionEditor,
                     dcv.getValue().get());

        verify(undefinedExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                              eq(Optional.empty()),
                                                              eq(list.getExpression().get(0)),
                                                              eq(Optional.empty()),
                                                              eq(isOnlyVisualChangeAllowed),
                                                              eq(1));
        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(0, parent.getRowIndex());
        assertEquals(EXPRESSION_COLUMN_INDEX, parent.getColumnIndex());
        assertEquals(gridWidget, parent.getGridWidget());
    }

    @Test
    public void testFromDMNModelLiteralExpression() {
        setup(new LiteralExpression(), false);
        list.getExpression().add(HasExpression.wrap(list, new LiteralExpression()));

        mapper.fromDMNModel(0, EXPRESSION_COLUMN_INDEX);

        assertFromDMNModelLiteralExpression(false);
    }

    @Test
    public void testFromDMNModelLiteralExpressionWhenOnlyVisualChangeAllowed() {
        setup(new LiteralExpression(), true);
        list.getExpression().add(HasExpression.wrap(list, new LiteralExpression()));

        mapper.fromDMNModel(0, EXPRESSION_COLUMN_INDEX);

        assertFromDMNModelLiteralExpression(true);
    }

    private void assertFromDMNModelLiteralExpression(final boolean isOnlyVisualChangeAllowed) {
        assertTrue(uiModel.getCell(0, EXPRESSION_COLUMN_INDEX).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv = (ExpressionCellValue) uiModel.getCell(0, EXPRESSION_COLUMN_INDEX).getValue();
        assertEquals(literalExpressionEditor,
                     dcv.getValue().get());

        verify(literalExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                            eq(Optional.empty()),
                                                            eq(list.getExpression().get(0)),
                                                            eq(Optional.empty()),
                                                            eq(isOnlyVisualChangeAllowed),
                                                            eq(1));
        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(0, parent.getRowIndex());
        assertEquals(EXPRESSION_COLUMN_INDEX, parent.getColumnIndex());
        assertEquals(gridWidget, parent.getGridWidget());
    }

    @Test
    public void testFromDMNModelCellTypes() {
        setup(new LiteralExpression(), false);

        IntStream.range(0, 1).forEach(rowIndex -> {
            mapper.fromDMNModel(rowIndex, ROW_COLUMN_INDEX);
            mapper.fromDMNModel(rowIndex, EXPRESSION_COLUMN_INDEX);
        });

        assertThat(uiModel.getCell(0, ROW_COLUMN_INDEX)).isInstanceOf(ContextGridCell.class);
        assertThat(uiModel.getCell(0, EXPRESSION_COLUMN_INDEX)).isInstanceOf(ContextGridCell.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToDMNModelExpressionNull() {
        //Initially non-null value
        setup(new LiteralExpression(), false);

        cellValueSupplier = () -> Optional.of(new ExpressionCellValue(Optional.of(undefinedExpressionEditor)));

        mapper.toDMNModel(0,
                          EXPRESSION_COLUMN_INDEX,
                          cellValueSupplier);

        //..becomes null value once mapped from the cell value
        assertNull(list.getExpression().get(0).getExpression());
    }

    @Test
    public void testToDMNModelExpressionNonNull() {
        //Initially null value
        setup(null, false);

        cellValueSupplier = () -> Optional.of(new ExpressionCellValue(Optional.of(literalExpressionEditor)));

        mapper.toDMNModel(0,
                          EXPRESSION_COLUMN_INDEX,
                          cellValueSupplier);

        //..becomes non-null value once mapped from the cell value
        assertEquals(literalExpression,
                     list.getExpression().get(0).getExpression());
    }
}
