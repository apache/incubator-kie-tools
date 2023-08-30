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

import java.util.Arrays;
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
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.RuleAnnotationClauseColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MoveColumnsCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private InputClauseColumn uiInputClauseColumnOne;

    @Mock
    private InputClauseColumn uiInputClauseColumnTwo;

    @Mock
    private InputClauseColumn uiInputClauseColumnThree;

    @Mock
    private OutputClauseColumn uiOutputClauseColumnOne;

    @Mock
    private OutputClauseColumn uiOutputClauseColumnTwo;

    @Mock
    private OutputClauseColumn uiOutputClauseColumnThree;

    @Mock
    private RuleAnnotationClauseColumn uiRuleAnnotationClauseColumnOne;

    @Mock
    private RuleAnnotationClauseColumn uiRuleAnnotationClauseColumnTwo;

    @Mock
    private RuleAnnotationClauseColumn uiRuleAnnotationClauseColumnThree;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    private DecisionTable dtable;

    @Mock
    private InputClause inputClauseOne;

    @Mock
    private InputClause inputClauseTwo;

    @Mock
    private InputClause inputClauseThree;

    @Mock
    private OutputClause outputClauseOne;

    @Mock
    private OutputClause outputClauseTwo;

    @Mock
    private OutputClause outputClauseThree;

    @Mock
    private RuleAnnotationClause annotationClauseOne;

    @Mock
    private RuleAnnotationClause annotationClauseTwo;

    @Mock
    private RuleAnnotationClause annotationClauseThree;

    private DMNGridData uiModel;

    private MoveColumnsCommand command;

    private Command<GraphCommandExecutionContext, RuleViolation> graphCommand;

    private Command<AbstractCanvasHandler, CanvasViolation> canvasCommand;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Before
    public void setUp() {
        this.dtable = new DecisionTable();
        this.uiModel = new DMNGridData();

        dtable.getInput().add(inputClauseOne);
        dtable.getInput().add(inputClauseTwo);
        dtable.getInput().add(inputClauseThree);
        dtable.getOutput().add(outputClauseOne);
        dtable.getOutput().add(outputClauseTwo);
        dtable.getOutput().add(outputClauseThree);
        dtable.getAnnotations().add(annotationClauseOne);
        dtable.getAnnotations().add(annotationClauseTwo);
        dtable.getAnnotations().add(annotationClauseThree);

        dtable.getRule().add(new DecisionRule() {{
            getInputEntry().add(new UnaryTests());
            getInputEntry().add(new UnaryTests());
            getInputEntry().add(new UnaryTests());
            getOutputEntry().add(new LiteralExpression());
            getOutputEntry().add(new LiteralExpression());
            getOutputEntry().add(new LiteralExpression());
            getAnnotationEntry().add(new RuleAnnotationClauseText());
            getAnnotationEntry().add(new RuleAnnotationClauseText());
            getAnnotationEntry().add(new RuleAnnotationClauseText());
        }});

        uiModel.appendColumn(uiRowNumberColumn);
        uiModel.appendColumn(uiInputClauseColumnOne);
        uiModel.appendColumn(uiInputClauseColumnTwo);
        uiModel.appendColumn(uiInputClauseColumnThree);
        uiModel.appendColumn(uiOutputClauseColumnOne);
        uiModel.appendColumn(uiOutputClauseColumnTwo);
        uiModel.appendColumn(uiOutputClauseColumnThree);
        uiModel.appendColumn(uiRuleAnnotationClauseColumnOne);
        uiModel.appendColumn(uiRuleAnnotationClauseColumnTwo);
        uiModel.appendColumn(uiRuleAnnotationClauseColumnThree);

        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiInputClauseColumnOne).getIndex();
        doReturn(2).when(uiInputClauseColumnTwo).getIndex();
        doReturn(3).when(uiInputClauseColumnThree).getIndex();
        doReturn(4).when(uiOutputClauseColumnOne).getIndex();
        doReturn(5).when(uiOutputClauseColumnTwo).getIndex();
        doReturn(6).when(uiOutputClauseColumnThree).getIndex();
        doReturn(7).when(uiRuleAnnotationClauseColumnOne).getIndex();
        doReturn(8).when(uiRuleAnnotationClauseColumnTwo).getIndex();
        doReturn(9).when(uiRuleAnnotationClauseColumnThree).getIndex();
    }

    @Test
    public void testCommandAllow() {
        moveColumnsToPositionCommand(Collections.singletonList(uiInputClauseColumnThree), 1);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.allow(canvasHandler));
    }

    @Test
    public void testMoveSingleInputColumnLeft() {
        moveColumnsToPositionCommand(Collections.singletonList(uiInputClauseColumnThree), 1);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(1, 2, 0, 0, 1, 2, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(2, 3, 1, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void testMoveMultipleInputColumnsLeft() {
        moveColumnsToPositionCommand(Arrays.asList(uiInputClauseColumnTwo, uiInputClauseColumnThree), 1);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(2, 0, 1, 0, 1, 2, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(3, 1, 2, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void testMoveSingleInputColumnRight() {
        moveColumnsToPositionCommand(Collections.singletonList(uiInputClauseColumnOne), 3);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(2, 0, 1, 0, 1, 2, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(3, 1, 2, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void testMoveMultipleInputColumnsRight() {
        moveColumnsToPositionCommand(Arrays.asList(uiInputClauseColumnOne, uiInputClauseColumnTwo), 3);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(1, 2, 0, 0, 1, 2, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(2, 3, 1, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void testMoveSingleOutputColumnLeft() {
        moveColumnsToPositionCommand(Collections.singletonList(uiOutputClauseColumnThree), 4);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 1, 2, 0, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 5, 6, 4, 7, 8, 9);
    }

    @Test
    public void testMoveMultipleOutputColumnsLeft() {
        moveColumnsToPositionCommand(Arrays.asList(uiOutputClauseColumnTwo, uiOutputClauseColumnThree), 4);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 2, 0, 1, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 6, 4, 5, 7, 8, 9);
    }

    @Test
    public void testMoveSingleOutputColumnRight() {
        moveColumnsToPositionCommand(Collections.singletonList(uiOutputClauseColumnOne), 6);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 2, 0, 1, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 6, 4, 5, 7, 8, 9);
    }

    @Test
    public void testMoveMultipleOutputColumnsRight() {
        moveColumnsToPositionCommand(Arrays.asList(uiOutputClauseColumnOne, uiOutputClauseColumnTwo), 6);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 1, 2, 0, 0, 1, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 5, 6, 4, 7, 8, 9);
    }

    @Test
    public void testMoveSingleAnnotationColumnWithDuplicatedTitle() {

        final DecisionTable decisionTable = new DecisionTable();
        final DMNGridData model = new DMNGridData();

        final RuleAnnotationClause clauseOne = new RuleAnnotationClause();
        final RuleAnnotationClause clauseTwo = new RuleAnnotationClause();
        final RuleAnnotationClause clauseThree = new RuleAnnotationClause();
        decisionTable.getAnnotations().add(clauseOne);
        decisionTable.getAnnotations().add(clauseTwo);
        decisionTable.getAnnotations().add(clauseThree);

        decisionTable.getRule().add(new DecisionRule() {{
            getAnnotationEntry().add(new RuleAnnotationClauseText());
            getAnnotationEntry().add(new RuleAnnotationClauseText());
            getAnnotationEntry().add(new RuleAnnotationClauseText());
        }});

        final RuleAnnotationClauseColumn columnOne = mock(RuleAnnotationClauseColumn.class);
        final RuleAnnotationClauseColumn columnTwo = mock(RuleAnnotationClauseColumn.class);
        final RuleAnnotationClauseColumn columnThree = mock(RuleAnnotationClauseColumn.class);
        model.appendColumn(uiRowNumberColumn);
        model.appendColumn(columnOne);
        model.appendColumn(columnTwo);
        model.appendColumn(columnThree);
        when(columnOne.getIndex()).thenReturn(1);
        when(columnOne.getIndex()).thenReturn(2);
        when(columnOne.getIndex()).thenReturn(3);

        command = new MoveColumnsCommand(decisionTable, model, 2, Arrays.asList(columnOne), canvasOperation);
        graphCommand = command.newGraphCommand(canvasHandler);
        canvasCommand = command.newCanvasCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));
    }

    @Test
    public void testMoveSingleAnnotationColumnLeft() {
        moveColumnsToPositionCommand(Collections.singletonList(uiRuleAnnotationClauseColumnThree), 7);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 0, 1, 2, 1, 2, 0);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 4, 5, 6, 8, 9, 7);
    }

    @Test
    public void testMoveMultipleAnnotationColumnsLeft() {
        moveColumnsToPositionCommand(Arrays.asList(uiRuleAnnotationClauseColumnTwo, uiRuleAnnotationClauseColumnThree), 7);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 0, 1, 2, 2, 0, 1);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 4, 5, 6, 9, 7, 8);
    }

    @Test
    public void testMoveSingleAnnotationColumnRight() {
        moveColumnsToPositionCommand(Collections.singletonList(uiRuleAnnotationClauseColumnOne), 8);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 0, 1, 2, 1, 0, 2);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 4, 5, 6, 8, 7, 9);
    }

    @Test
    public void testMoveMultipleAnnotationColumnsRight() {
        moveColumnsToPositionCommand(Arrays.asList(uiRuleAnnotationClauseColumnOne, uiRuleAnnotationClauseColumnTwo), 9);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertClauses(0, 1, 2, 0, 1, 2, 1, 2, 0);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(canvasHandler));

        assertColumns(1, 2, 3, 4, 5, 6, 8, 9, 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveSingleAnnotationToOutputs() {
        moveColumnsToPositionCommand(Collections.singletonList(uiRuleAnnotationClauseColumnOne), 4);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveSingleAnnotationToInputs() {
        moveColumnsToPositionCommand(Collections.singletonList(uiRuleAnnotationClauseColumnTwo), 1);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveSingleInputToOutputs() {
        moveColumnsToPositionCommand(Collections.singletonList(uiInputClauseColumnOne), 4);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveSingleInputToAnnotations() {
        moveColumnsToPositionCommand(Collections.singletonList(uiInputClauseColumnOne), 7);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveSingleOutputToInputs() {
        moveColumnsToPositionCommand(Collections.singletonList(uiOutputClauseColumnOne), 1);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveSingleOutputToAnnotations() {
        moveColumnsToPositionCommand(Collections.singletonList(uiOutputClauseColumnOne), 7);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveMixedToInputs() {
        moveColumnsToPositionCommand(Arrays.asList(uiInputClauseColumnOne, uiOutputClauseColumnOne), 3);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveMixedToOutputs() {
        moveColumnsToPositionCommand(Arrays.asList(uiOutputClauseColumnOne, uiInputClauseColumnOne), 6);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveMixedToAnnotations() {
        moveColumnsToPositionCommand(Arrays.asList(uiOutputClauseColumnOne, uiInputClauseColumnOne), 7);

        graphCommand.execute(graphCommandExecutionContext);
    }

    @Test
    public void testInputClauseComponentWidths() {
        moveColumnsToPositionCommand(Arrays.asList(uiInputClauseColumnTwo, uiInputClauseColumnThree), 1);

        assertComponentWidths(1, 2, 3);
    }

    @Test
    public void testOutputClauseComponentWidths() {
        moveColumnsToPositionCommand(Arrays.asList(uiOutputClauseColumnTwo, uiOutputClauseColumnThree), 4);

        assertComponentWidths(4, 5, 6);
    }

    @Test
    public void testAnnotationClauseComponentWidths() {
        moveColumnsToPositionCommand(Arrays.asList(uiRuleAnnotationClauseColumnTwo, uiRuleAnnotationClauseColumnThree), 7);

        assertComponentWidths(7, 8, 9);
    }

    private void assertComponentWidths(final int uiColumn1Index,
                                       final int uiColumn2Index,
                                       final int uiColumn3Index) {
        final List<Double> componentWidths = dtable.getComponentWidths();
        componentWidths.set(uiColumn1Index, 10.0);
        componentWidths.set(uiColumn2Index, 20.0);
        componentWidths.set(uiColumn3Index, 30.0);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        //Execute
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(dtable.getRequiredComponentWidthCount(),
                     dtable.getComponentWidths().size());

        assertEquals(20.0,
                     dtable.getComponentWidths().get(uiColumn1Index),
                     0.0);
        assertEquals(30.0,
                     dtable.getComponentWidths().get(uiColumn2Index),
                     0.0);
        assertEquals(10.0,
                     dtable.getComponentWidths().get(uiColumn3Index),
                     0.0);

        //Move UI columns as MoveColumnsCommand.undo() relies on the UiModel being updated
        command.newCanvasCommand(canvasHandler).execute(canvasHandler);

        //Undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));

        assertEquals(dtable.getRequiredComponentWidthCount(),
                     dtable.getComponentWidths().size());
        assertEquals(10.0,
                     dtable.getComponentWidths().get(uiColumn1Index),
                     0.0);
        assertEquals(20.0,
                     dtable.getComponentWidths().get(uiColumn2Index),
                     0.0);
        assertEquals(30.0,
                     dtable.getComponentWidths().get(uiColumn3Index),
                     0.0);
    }

    private void assertClauses(final int... clausesIndexes) {
        assertEquals(inputClauseOne, dtable.getInput().get(clausesIndexes[0]));
        assertEquals(inputClauseTwo, dtable.getInput().get(clausesIndexes[1]));
        assertEquals(inputClauseThree, dtable.getInput().get(clausesIndexes[2]));
        assertEquals(outputClauseOne, dtable.getOutput().get(clausesIndexes[3]));
        assertEquals(outputClauseTwo, dtable.getOutput().get(clausesIndexes[4]));
        assertEquals(outputClauseThree, dtable.getOutput().get(clausesIndexes[5]));
        assertEquals(annotationClauseOne, dtable.getAnnotations().get(clausesIndexes[6]));
        assertEquals(annotationClauseTwo, dtable.getAnnotations().get(clausesIndexes[7]));
        assertEquals(annotationClauseThree, dtable.getAnnotations().get(clausesIndexes[8]));
    }

    private void assertColumns(final int... columnIndexes) {
        assertEquals(uiInputClauseColumnOne, uiModel.getColumns().get(columnIndexes[0]));
        assertEquals(uiInputClauseColumnTwo, uiModel.getColumns().get(columnIndexes[1]));
        assertEquals(uiInputClauseColumnThree, uiModel.getColumns().get(columnIndexes[2]));
        assertEquals(uiOutputClauseColumnOne, uiModel.getColumns().get(columnIndexes[3]));
        assertEquals(uiOutputClauseColumnTwo, uiModel.getColumns().get(columnIndexes[4]));
        assertEquals(uiOutputClauseColumnThree, uiModel.getColumns().get(columnIndexes[5]));
        assertEquals(uiRuleAnnotationClauseColumnOne, uiModel.getColumns().get(columnIndexes[6]));
        assertEquals(uiRuleAnnotationClauseColumnTwo, uiModel.getColumns().get(columnIndexes[7]));
        assertEquals(uiRuleAnnotationClauseColumnThree, uiModel.getColumns().get(columnIndexes[8]));
    }

    private void moveColumnsToPositionCommand(final List<GridColumn<?>> columns, final int position) {
        command = new MoveColumnsCommand(dtable, uiModel, position, columns, canvasOperation);

        graphCommand = command.newGraphCommand(canvasHandler);
        canvasCommand = command.newCanvasCommand(canvasHandler);
    }
}
