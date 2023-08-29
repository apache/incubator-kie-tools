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

package org.kie.workbench.common.dmn.client.commands.expressions.types.dtable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableRowNumberColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class MoveRowsCommandTest {

    @Mock
    private DecisionTableRowNumberColumn uiRowNumberColumn;

    @Mock
    private InputClauseColumn uiInputClauseColumn;

    @Mock
    private OutputClauseColumn uiOutputClauseColumn;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    private DecisionTable dtable;

    @Mock
    private InputClause inputClause;

    @Mock
    private OutputClause outputClause;

    private DMNGridData uiModel;

    private MoveRowsCommand command;

    private Command<GraphCommandExecutionContext, RuleViolation> graphCommand;

    private Command<AbstractCanvasHandler, CanvasViolation> canvasCommand;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    private List<GridRow> rowsUnderTest = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        this.dtable = new DecisionTable();
        this.uiModel = new DMNGridData();

        dtable.getInput().add(inputClause);
        dtable.getOutput().add(outputClause);

        uiModel.appendColumn(uiRowNumberColumn);
        uiModel.appendColumn(uiInputClauseColumn);
        uiModel.appendColumn(uiOutputClauseColumn);

        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiInputClauseColumn).getIndex();
        doReturn(2).when(uiOutputClauseColumn).getIndex();

        rowsUnderTest.clear();

        appendRow(0, "a");
        appendRow(1, "b");
        appendRow(2, "c");
    }

    @Test
    public void moveSingleRowUp() throws Exception {
        moveRowsToPositionCommand(Collections.singletonList(rowsUnderTest.get(2)), 0);

        graphCommand.execute(graphCommandExecutionContext);
        canvasCommand.execute(canvasHandler);

        assertRow(0, "in c", "out c");
        assertRow(1, "in a", "out a");
        assertRow(2, "in b", "out b");
    }

    @Test
    public void moveSingleRowDown() throws Exception {
        moveRowsToPositionCommand(Collections.singletonList(rowsUnderTest.get(0)), 2);

        graphCommand.execute(graphCommandExecutionContext);
        canvasCommand.execute(canvasHandler);

        assertRow(0, "in b", "out b");
        assertRow(1, "in c", "out c");
        assertRow(2, "in a", "out a");
    }

    @Test
    public void moveMultipleRowsUp() throws Exception {
        moveRowsToPositionCommand(rowsUnderTest.subList(1, 3), 0);

        graphCommand.execute(graphCommandExecutionContext);
        canvasCommand.execute(canvasHandler);

        assertRow(0, "in b", "out b");
        assertRow(1, "in c", "out c");
        assertRow(2, "in a", "out a");
    }

    @Test
    public void moveMultipleRowsDown() throws Exception {
        moveRowsToPositionCommand(rowsUnderTest.subList(0, 2), 2);

        graphCommand.execute(graphCommandExecutionContext);
        canvasCommand.execute(canvasHandler);

        assertRow(0, "in c", "out c");
        assertRow(1, "in a", "out a");
        assertRow(2, "in b", "out b");
    }

    private void appendRow(final int rowIndex, final String rowIdentifier) {
        final String inValue = "in " + rowIdentifier;
        final String outValue = "out " + rowIdentifier;
        dtable.getRule().add(new DecisionRule() {{
            getInputEntry().add(new UnaryTests() {{
                getText().setValue(inValue);
            }});
            getOutputEntry().add(new LiteralExpression() {{
                getText().setValue(outValue);
            }});
        }});
        final GridRow uiRow = new BaseGridRow();
        uiModel.appendRow(uiRow);
        uiModel.setCellValue(rowIndex, 0, new BaseGridCellValue<>(rowIndex + 1));
        uiModel.setCellValue(rowIndex, 1, new BaseGridCellValue<>(inValue));
        uiModel.setCellValue(rowIndex, 2, new BaseGridCellValue<>(outValue));

        rowsUnderTest.add(uiRow);
    }

    private void moveRowsToPositionCommand(final List<GridRow> rows, int position) {
        command = new MoveRowsCommand(dtable, uiModel, position, rows, canvasOperation);

        graphCommand = command.newGraphCommand(canvasHandler);
        canvasCommand = command.newCanvasCommand(canvasHandler);
    }

    private void assertRow(final int rowIndex, final String inValue, final String outValue) {
        assertTableModelRow(rowIndex, inValue, outValue);
        assertUiModelRow(rowIndex, inValue, outValue);
    }

    private void assertTableModelRow(final int rowIndex, final String inValue, final String outValue) {
        assertEquals(inValue, dtable.getRule().get(rowIndex).getInputEntry().get(0).getText().getValue());
        assertEquals(outValue, dtable.getRule().get(rowIndex).getOutputEntry().get(0).getText().getValue());
    }

    private void assertUiModelRow(final int rowIndex, final String inValue, final String outValue) {
        assertEquals(rowIndex + 1, uiModel.getCell(rowIndex, 0).getValue().getValue());
        assertEquals(inValue, uiModel.getCell(rowIndex, 1).getValue().getValue());
        assertEquals(outValue, uiModel.getCell(rowIndex, 2).getValue().getValue());
    }
}