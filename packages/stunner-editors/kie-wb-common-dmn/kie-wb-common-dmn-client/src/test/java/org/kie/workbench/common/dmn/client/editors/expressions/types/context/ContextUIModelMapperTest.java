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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridCell;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapperHelper.EXPRESSION_COLUMN_INDEX;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapperHelper.NAME_COLUMN_INDEX;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapperHelper.ROW_COLUMN_INDEX;

@RunWith(MockitoJUnitRunner.class)
public class ContextUIModelMapperTest extends BaseContextUIModelMapperTest<ContextUIModelMapper> {

    @Override
    protected ContextUIModelMapper getMapper(final boolean isOnlyVisualChangeAllowedSupplier) {
        return new ContextUIModelMapper(gridWidget,
                                        () -> uiModel,
                                        () -> Optional.of(context),
                                        () -> isOnlyVisualChangeAllowedSupplier,
                                        expressionEditorDefinitionsSupplier,
                                        listSelector,
                                        0);
    }

    @Test
    public void testFromDMNModelRowNumber() {
        setup(false);

        mapper.fromDMNModel(0, ROW_COLUMN_INDEX);
        mapper.fromDMNModel(1, ROW_COLUMN_INDEX);

        assertThat(uiModel.getCell(0, ROW_COLUMN_INDEX).getValue().getValue()).isEqualTo(1);
        assertThat(uiModel.getCell(0, ROW_COLUMN_INDEX).getSelectionStrategy()).isSameAs(RowSelectionStrategy.INSTANCE);

        assertThat(uiModel.getCell(1, ROW_COLUMN_INDEX).getValue().getValue()).isNull();
        assertThat(uiModel.getCell(1, ROW_COLUMN_INDEX).getSelectionStrategy()).isSameAs(RowSelectionStrategy.INSTANCE);
    }

    @Test
    public void testFromDMNModelName() {
        setup(false);

        mapper.fromDMNModel(0, NAME_COLUMN_INDEX);
        mapper.fromDMNModel(1, NAME_COLUMN_INDEX);

        assertEquals("ii1",
                     ((InformationItemCell.HasNameCell) uiModel.getCell(0, NAME_COLUMN_INDEX).getValue().getValue()).getName().getValue());
        assertFalse(((InformationItemCell.HasNameAndDataTypeCell) uiModel.getCell(1, NAME_COLUMN_INDEX).getValue().getValue()).hasData());
        assertEquals(ContextUIModelMapper.DEFAULT_ROW_CAPTION,
                     ((InformationItemCell.HasNameAndDataTypeCell) uiModel.getCell(1, NAME_COLUMN_INDEX).getValue().getValue()).getPlaceHolderText());
    }

    @Test
    public void testFromDMNModelExpression() {
        setup(false);

        mapper.fromDMNModel(0, EXPRESSION_COLUMN_INDEX);
        mapper.fromDMNModel(1, EXPRESSION_COLUMN_INDEX);

        assertUndefinedExpressionGridCellEditor(0, undefinedExpressionEditor);
        assertUndefinedExpressionGridCellEditor(1, literalExpressionEditor);
    }

    private void assertUndefinedExpressionGridCellEditor(final int uiRowIndex,
                                                         final BaseExpressionGrid editor) {
        assertTrue(uiModel.getCell(uiRowIndex, EXPRESSION_COLUMN_INDEX) instanceof ContextGridCell);
        final ContextGridCell contextGridCell1 = (ContextGridCell) uiModel.getCell(uiRowIndex, EXPRESSION_COLUMN_INDEX);
        assertTrue(contextGridCell1.getEditor().isPresent());
        assertEquals(listSelector, contextGridCell1.getEditor().get());
        assertTrue(contextGridCell1.getValue() instanceof ExpressionCellValue);
        assertTrue(contextGridCell1.getValue().getValue() instanceof Optional);
        assertEquals(editor, ((Optional) contextGridCell1.getValue().getValue()).get());
    }

    @Test
    public void testFromDMNModelCellTypes() {
        setup(false);

        IntStream.range(0, 2).forEach(rowIndex -> {
            mapper.fromDMNModel(rowIndex, ROW_COLUMN_INDEX);
            mapper.fromDMNModel(rowIndex, NAME_COLUMN_INDEX);
            mapper.fromDMNModel(rowIndex, EXPRESSION_COLUMN_INDEX);
        });

        assertThat(uiModel.getCell(0, ROW_COLUMN_INDEX)).isInstanceOf(ContextGridCell.class);
        assertThat(uiModel.getCell(0, NAME_COLUMN_INDEX)).isInstanceOf(ContextGridCell.class);
        assertThat(uiModel.getCell(0, EXPRESSION_COLUMN_INDEX)).isInstanceOf(ContextGridCell.class);

        assertThat(uiModel.getCell(1, ROW_COLUMN_INDEX)).isInstanceOf(DMNGridCell.class);
        assertThat(uiModel.getCell(1, NAME_COLUMN_INDEX)).isInstanceOf(DMNGridCell.class);
        assertThat(uiModel.getCell(1, EXPRESSION_COLUMN_INDEX)).isInstanceOf(DMNGridCell.class);
    }
}
