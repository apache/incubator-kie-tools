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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell.DEFAULT_HEIGHT;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RelationUIModelMapperTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private RelationColumn uiRelationColumn1;

    @Mock
    private RelationColumn uiRelationColumn2;

    @Mock
    private ListSelectorView.Presenter listSelector;

    private BaseGridData uiModel;

    private Relation relation;

    private Supplier<Optional<GridCellValue<?>>> cellValueSupplier;

    private RelationUIModelMapper mapper;

    @Before
    public void setup() {
        this.uiModel = new BaseGridData();
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiRelationColumn1);
        this.uiModel.appendColumn(uiRelationColumn2);
        when(uiRowNumberColumn.getIndex()).thenReturn(0);
        when(uiRelationColumn1.getIndex()).thenReturn(1);
        when(uiRelationColumn2.getIndex()).thenReturn(2);

        this.relation = new Relation();
        this.relation.getColumn().add(new InformationItem());
        this.relation.getColumn().add(new InformationItem());
        final List rowList1 = new List();
        rowList1.getExpression().add(HasExpression.wrap(rowList1,
                                                        new LiteralExpression() {{
                                                            getText().setValue("le(1,0)");
                                                        }}));
        rowList1.getExpression().add(HasExpression.wrap(rowList1,
                                                        new LiteralExpression() {{
                                                            getText().setValue("le(2,0)");
                                                        }}));
        final List rowList2 = new List();
        rowList2.getExpression().add(HasExpression.wrap(rowList2,
                                                        new LiteralExpression() {{
                                                            getText().setValue("le(1,1)");
                                                        }}));
        rowList2.getExpression().add(HasExpression.wrap(rowList2,
                                                        new LiteralExpression() {{
                                                            getText().setValue("le(2,1)");
                                                        }}));

        this.relation.getRow().add(rowList1);
        this.relation.getRow().add(rowList2);

        this.mapper = new RelationUIModelMapper(() -> uiModel,
                                                () -> Optional.of(relation),
                                                listSelector,
                                                DEFAULT_HEIGHT);
        this.cellValueSupplier = Optional::empty;
    }

    @Test
    public void testFromDMNModelRowNumber() {
        mapper.fromDMNModel(0, 0);
        mapper.fromDMNModel(1, 0);

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(RowSelectionStrategy.INSTANCE,
                     uiModel.getCell(0, 0).getSelectionStrategy());

        assertEquals(2,
                     uiModel.getCell(1, 0).getValue().getValue());
        assertEquals(RowSelectionStrategy.INSTANCE,
                     uiModel.getCell(1, 0).getSelectionStrategy());
    }

    @Test
    public void testFromDMNModelLiteralExpressions() {
        mapper.fromDMNModel(0, 1);
        mapper.fromDMNModel(0, 2);
        mapper.fromDMNModel(1, 1);
        mapper.fromDMNModel(1, 2);

        assertEquals("le(1,0)",
                     uiModel.getCell(0, 1).getValue().getValue());
        assertEquals("le(2,0)",
                     uiModel.getCell(0, 2).getValue().getValue());
        assertEquals("le(1,1)",
                     uiModel.getCell(1, 1).getValue().getValue());
        assertEquals("le(2,1)",
                     uiModel.getCell(1, 2).getValue().getValue());
    }

    @Test
    public void testFromDMNModelCellTypes() {
        IntStream.range(0, 2).forEach(rowIndex -> {
            mapper.fromDMNModel(rowIndex, 0);
            mapper.fromDMNModel(rowIndex, 1);
            mapper.fromDMNModel(rowIndex, 2);
        });

        assertThat(uiModel.getCell(0, 0)).isInstanceOf(RelationGridCell.class);
        assertThat(uiModel.getCell(0, 1)).isInstanceOf(RelationGridCell.class);
        assertThat(uiModel.getCell(0, 2)).isInstanceOf(RelationGridCell.class);

        assertThat(uiModel.getCell(1, 0)).isInstanceOf(RelationGridCell.class);
        assertThat(uiModel.getCell(1, 1)).isInstanceOf(RelationGridCell.class);
        assertThat(uiModel.getCell(1, 2)).isInstanceOf(RelationGridCell.class);
    }

    @Test
    public void testToDMNModelLiteralExpressionsNullValue() {
        cellValueSupplier = () -> Optional.of(new BaseGridCellValue<>(null));
        for (int uiRowIndex = 0; uiRowIndex < uiModel.getRowCount(); uiRowIndex++) {
            for (int uiColumnIndex = 1; uiColumnIndex < uiModel.getColumnCount(); uiColumnIndex++) {
                mapper.toDMNModel(uiRowIndex,
                                  uiColumnIndex,
                                  cellValueSupplier);
                final LiteralExpression le = (LiteralExpression) relation
                        .getRow()
                        .get(uiRowIndex)
                        .getExpression()
                        .get(uiColumnIndex - RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT)
                        .getExpression();

                assertNull(le.getText().getValue());
            }
        }
    }

    @Test
    public void testToDMNModelLiteralExpressionsEmptyValue() {
        for (int uiRowIndex = 0; uiRowIndex < uiModel.getRowCount(); uiRowIndex++) {
            for (int uiColumnIndex = 1; uiColumnIndex < uiModel.getColumnCount(); uiColumnIndex++) {
                mapper.toDMNModel(uiRowIndex,
                                  uiColumnIndex,
                                  cellValueSupplier);
                final LiteralExpression le = (LiteralExpression) relation
                        .getRow()
                        .get(uiRowIndex)
                        .getExpression()
                        .get(uiColumnIndex - RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT)
                        .getExpression();

                assertEquals("",
                             le.getText().getValue());
            }
        }
    }

    @Test
    public void testToDMNModelLiteralExpressionsNonEmptyValue() {
        for (int uiRowIndex = 0; uiRowIndex < uiModel.getRowCount(); uiRowIndex++) {
            for (int uiColumnIndex = 1; uiColumnIndex < uiModel.getColumnCount(); uiColumnIndex++) {
                final String value = "(" + uiColumnIndex + "," + uiRowIndex + ")";
                cellValueSupplier = () -> Optional.of(new BaseGridCellValue<>(value));
                mapper.toDMNModel(uiRowIndex,
                                  uiColumnIndex,
                                  cellValueSupplier);
                final LiteralExpression le = (LiteralExpression) relation
                        .getRow()
                        .get(uiRowIndex)
                        .getExpression()
                        .get(uiColumnIndex - RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT)
                        .getExpression();

                assertEquals(value,
                             le.getText().getValue());
            }
        }
    }
}
