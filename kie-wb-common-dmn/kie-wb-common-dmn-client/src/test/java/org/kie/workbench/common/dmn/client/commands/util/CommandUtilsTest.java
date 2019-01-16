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

package org.kie.workbench.common.dmn.client.commands.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.mockito.Mock;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CommandUtilsTest {

    private static final int ROW_COUNT = 10;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private GridPinnedModeManager pinnedModeManager;

    @Mock
    private GridRenderer renderer;

    private DecisionRule decisionRuleOne;
    private DecisionRule decisionRuleTwo;
    private DecisionRule decisionRuleThree;

    private List<Object> allRows = new ArrayList<>();
    private List<Object> rowsToMove = new ArrayList<>();

    private DMNGridData uiModel;

    private GridWidget gridWidget;

    @Before
    public void setUp() throws Exception {
        decisionRuleOne = new DecisionRule();
        decisionRuleTwo = new DecisionRule();
        decisionRuleThree = new DecisionRule();

        decisionRuleOne.setId(new Id("1"));
        decisionRuleTwo.setId(new Id("2"));
        decisionRuleThree.setId(new Id("3"));

        allRows.clear();
        allRows.add(decisionRuleOne);
        allRows.add(decisionRuleTwo);
        allRows.add(decisionRuleThree);

        rowsToMove.clear();

        uiModel = new DMNGridData();
        gridWidget = new BaseGridWidget(uiModel,
                                        selectionManager,
                                        pinnedModeManager,
                                        renderer);
    }

    @Test
    public void testMoveOneRowUp() throws Exception {
        CommandUtils.moveRows(allRows, Collections.singletonList(decisionRuleThree), 0);

        Assertions.assertThat(allRows).containsSequence(decisionRuleThree, decisionRuleOne, decisionRuleTwo);
    }

    @Test
    public void testMoveOneRowUpMiddle() throws Exception {
        CommandUtils.moveRows(allRows, Collections.singletonList(decisionRuleThree), 1);

        Assertions.assertThat(allRows).containsSequence(decisionRuleOne, decisionRuleThree, decisionRuleTwo);
    }

    @Test
    public void testMoveOneRowDown() throws Exception {
        CommandUtils.moveRows(allRows, Collections.singletonList(decisionRuleOne), 2);

        Assertions.assertThat(allRows).containsSequence(decisionRuleTwo, decisionRuleThree, decisionRuleOne);
    }

    @Test
    public void testMoveOneRowDownMiddle() throws Exception {
        CommandUtils.moveRows(allRows, Collections.singletonList(decisionRuleOne), 1);

        Assertions.assertThat(allRows).containsSequence(decisionRuleTwo, decisionRuleOne, decisionRuleThree);
    }

    @Test
    public void testMoveTwoRowsUp() throws Exception {
        CommandUtils.moveRows(allRows, Arrays.asList(decisionRuleTwo, decisionRuleThree), 0);

        Assertions.assertThat(allRows).containsSequence(decisionRuleTwo, decisionRuleThree, decisionRuleOne);
    }

    @Test
    public void testMoveTwoRowsDown() throws Exception {
        CommandUtils.moveRows(allRows, Arrays.asList(decisionRuleOne, decisionRuleTwo), 2);

        Assertions.assertThat(allRows).containsSequence(decisionRuleThree, decisionRuleOne, decisionRuleTwo);
    }

    @Test
    public void testUpdateRowNumbers() {
        setupUiModel(Pair.newPair(new RowNumberColumn(),
                                  (rowIndex) -> new BaseGridCellValue<>(rowIndex + 1)));
        assertRowNumberValues();

        uiModel.moveRowTo(0, uiModel.getRow(ROW_COUNT - 1));
        CommandUtils.updateRowNumbers(uiModel, IntStream.range(0, ROW_COUNT));

        assertRowNumberValues();
    }

    private void assertRowNumberValues() {
        IntStream.range(0, ROW_COUNT)
                .forEach(rowIndex -> assertEquals(rowIndex + 1,
                                                  uiModel.getCell(rowIndex, 0).getValue().getValue()));
    }

    @Test
    public void testUpdateParentInformation_WithExpressionColumn() {
        setupUiModel(Pair.newPair(new ExpressionEditorColumn(gridLayer,
                                                             new BaseHeaderMetaData("column"), gridWidget),
                                  (rowIndex) -> {
                                      final BaseExpressionGrid grid = mock(BaseExpressionGrid.class);
                                      final GridCellTuple gct = new GridCellTuple(rowIndex, 0, mock(GridWidget.class));
                                      when(grid.getParentInformation()).thenReturn(gct);
                                      return new ExpressionCellValue(Optional.of(grid));
                                  }));
        assertParentInformationValues(0);

        uiModel.moveRowTo(0, uiModel.getRow(ROW_COUNT - 1));
        CommandUtils.updateParentInformation(uiModel);

        assertParentInformationValues(0);
    }

    @Test
    public void testUpdateParentInformation_WithExpressionColumnNullValues() {
        setupUiModelNullValues(Pair.newPair(new ExpressionEditorColumn(gridLayer,
                                                                       new BaseHeaderMetaData("column"),
                                                                       gridWidget),
                                            (rowIndex) -> {
                                                final BaseExpressionGrid grid = mock(BaseExpressionGrid.class);
                                                final GridCellTuple gct = new GridCellTuple(rowIndex, 0, mock(GridWidget.class));
                                                when(grid.getParentInformation()).thenReturn(gct);
                                                return new ExpressionCellValue(Optional.of(grid));
                                            }));
        try {
            CommandUtils.updateParentInformation(uiModel);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateParentInformation_WithMultipleColumns() {
        setupUiModel(Pair.newPair(new ExpressionEditorColumn(gridLayer,
                                                             new BaseHeaderMetaData("column"),
                                                             gridWidget),
                                  (rowIndex) -> {
                                      final BaseExpressionGrid grid = mock(BaseExpressionGrid.class);
                                      final GridCellTuple gct = new GridCellTuple(rowIndex, 0, mock(GridWidget.class));
                                      when(grid.getParentInformation()).thenReturn(gct);
                                      return new ExpressionCellValue(Optional.of(grid));
                                  }),
                     Pair.newPair(new RowNumberColumn(),
                                  (rowIndex) -> new BaseGridCellValue<>(rowIndex + 1)));
        assertParentInformationValues(0);

        uiModel.moveColumnTo(0, uiModel.getColumns().get(1));
        CommandUtils.updateParentInformation(uiModel);

        assertParentInformationValues(1);
    }

    @Test
    public void testExtractCellValueNoValue() throws Exception {
        final int rowIndex = 123;
        final int columnIndex = 456;
        final GridData gridData = mock(GridData.class);
        final GridWidget gridWidget = mock(GridWidget.class);
        final GridCellTuple cellTuple = new GridCellTuple(rowIndex, columnIndex, gridWidget);

        doReturn(gridData).when(gridWidget).getModel();
        doReturn(null).when(gridData).getCell(rowIndex, columnIndex);

        Assertions.assertThat(CommandUtils.extractGridCellValue(cellTuple)).isEmpty();
    }

    @Test
    public void testExtractCellValue() throws Exception {
        final int rowIndex = 123;
        final int columnIndex = 456;
        final GridCellValue gridCellValue = mock(GridCellValue.class);
        final GridCell gridCell = mock(GridCell.class);
        final GridData gridData = mock(GridData.class);
        final GridWidget gridWidget = mock(GridWidget.class);
        final GridCellTuple cellTuple = new GridCellTuple(rowIndex, columnIndex, gridWidget);

        doReturn(gridData).when(gridWidget).getModel();
        doReturn(gridCell).when(gridData).getCell(rowIndex, columnIndex);
        doReturn(gridCellValue).when(gridCell).getValue();

        Assertions.assertThat(CommandUtils.extractGridCellValue(cellTuple)).hasValue(gridCellValue);
    }

    private void assertParentInformationValues(final int expressionColumnIndex) {
        IntStream.range(0, ROW_COUNT)
                .forEach(rowIndex -> {
                    final ExpressionCellValue ecv = ((ExpressionCellValue) uiModel.getCell(rowIndex, expressionColumnIndex).getValue());
                    final BaseExpressionGrid grid = ecv.getValue().get();
                    assertEquals(rowIndex,
                                 grid.getParentInformation().getRowIndex());
                    assertEquals(expressionColumnIndex,
                                 grid.getParentInformation().getColumnIndex());
                });
    }

    @SafeVarargs
    private final void setupUiModel(final Pair<GridColumn, Function<Integer, GridCellValue>>... columns) {
        setupUiModelNullValues(columns);
        Arrays.asList(columns).forEach(column -> {
            IntStream.range(0, ROW_COUNT).forEach(rowIndex -> {
                uiModel.setCellValue(rowIndex,
                                     uiModel.getColumns().indexOf(column.getK1()),
                                     column.getK2().apply(rowIndex));
            });
        });
    }

    @SafeVarargs
    private final void setupUiModelNullValues(final Pair<GridColumn, Function<Integer, GridCellValue>>... columns) {
        Arrays.asList(columns).forEach(column -> uiModel.appendColumn(column.getK1()));
        IntStream.range(0, ROW_COUNT).forEach(rowIndex -> {
            uiModel.appendRow(new BaseGridRow());
        });
    }
}
