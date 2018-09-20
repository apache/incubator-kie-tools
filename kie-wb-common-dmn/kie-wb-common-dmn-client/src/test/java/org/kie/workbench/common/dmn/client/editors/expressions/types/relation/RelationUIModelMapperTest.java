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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.List;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

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
        this.uiModel.appendRow(new DMNGridRow());
        this.uiModel.appendRow(new DMNGridRow());
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiRelationColumn1);
        this.uiModel.appendColumn(uiRelationColumn2);
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiRelationColumn1).getIndex();
        doReturn(2).when(uiRelationColumn2).getIndex();

        this.relation = new Relation();
        this.relation.getColumn().add(new InformationItem());
        this.relation.getColumn().add(new InformationItem());
        this.relation.getRow().add(new List() {{
            getExpression().add(new LiteralExpression() {{
                setText("le(1,0)");
            }});
            getExpression().add(new LiteralExpression() {{
                setText("le(2,0)");
            }});
        }});
        this.relation.getRow().add(new List() {{
            getExpression().add(new LiteralExpression() {{
                setText("le(1,1)");
            }});
            getExpression().add(new LiteralExpression() {{
                setText("le(2,1)");
            }});
        }});

        this.mapper = new RelationUIModelMapper(() -> uiModel,
                                                () -> Optional.of(relation),
                                                listSelector);
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
                        .get(uiColumnIndex - RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

                assertNull(le.getText());
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
                        .get(uiColumnIndex - RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

                assertEquals("",
                             le.getText());
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
                        .get(uiColumnIndex - RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

                assertEquals(value,
                             le.getText());
            }
        }
    }
}
