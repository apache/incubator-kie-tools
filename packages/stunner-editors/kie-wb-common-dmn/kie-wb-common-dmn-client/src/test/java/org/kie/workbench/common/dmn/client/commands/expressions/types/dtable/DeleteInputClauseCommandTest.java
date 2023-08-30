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

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.RuleAnnotationClauseColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
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
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell.DEFAULT_HEIGHT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteInputClauseCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private InputClauseColumn uiInputClauseColumn;

    @Mock
    private RuleAnnotationClauseColumn uiAnnotationClauseColumn;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    @Mock
    private org.uberfire.mvp.Command executeCanvasOperation;

    @Mock
    private org.uberfire.mvp.Command undoCanvasOperation;

    private DecisionTable dtable;

    private InputClause inputClause;

    private GridData uiModel;

    private DecisionTableUIModelMapper uiModelMapper;

    private DeleteInputClauseCommand command;

    @Before
    public void setup() {
        this.dtable = new DecisionTable();
        this.inputClause = new InputClause();
        this.dtable.getInput().add(inputClause);

        this.uiModel = new DMNGridData();
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiInputClauseColumn);
        this.uiModel.appendColumn(uiAnnotationClauseColumn);

        this.uiModelMapper = new DecisionTableUIModelMapper(() -> uiModel,
                                                            () -> Optional.of(dtable),
                                                            listSelector,
                                                            DEFAULT_HEIGHT);

        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiInputClauseColumn).getIndex();
        doReturn(2).when(uiAnnotationClauseColumn).getIndex();
    }

    private void makeCommand(final int uiColumnIndex) {
        this.command = spy(new DeleteInputClauseCommand(dtable,
                                                        uiModel,
                                                        uiColumnIndex,
                                                        uiModelMapper,
                                                        executeCanvasOperation,
                                                        undoCanvasOperation));
    }

    @Test
    public void testGraphCommandAllow() throws Exception {
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandCheck() throws Exception {
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandExecuteRemoveMiddle() {
        final InputClause firstInput = mock(InputClause.class);
        final InputClause lastInput = mock(InputClause.class);

        dtable.getInput().add(0, firstInput);
        dtable.getInput().add(lastInput);

        final UnaryTests inputOneValue = mock(UnaryTests.class);
        final UnaryTests inputTwoValue = mock(UnaryTests.class);
        final UnaryTests inputThreeValue = mock(UnaryTests.class);
        final DecisionRule rule = new DecisionRule();
        rule.getInputEntry().add(inputOneValue);
        rule.getInputEntry().add(inputTwoValue);
        rule.getInputEntry().add(inputThreeValue);

        dtable.getRule().add(rule);

        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT + 1);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));
        assertEquals(2,
                     dtable.getInput().size());
        assertEquals(firstInput,
                     dtable.getInput().get(0));
        assertEquals(lastInput,
                     dtable.getInput().get(1));
        assertEquals(2,
                     dtable.getRule().get(0).getInputEntry().size());
        assertEquals(inputOneValue,
                     dtable.getRule().get(0).getInputEntry().get(0));
        assertEquals(inputThreeValue,
                     dtable.getRule().get(0).getInputEntry().get(1));
    }

    @Test
    public void testGraphCommandExecute() {
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));
        assertEquals(0,
                     dtable.getInput().size());
    }

    @Test
    public void testGraphCommandExecuteAndThenUndo() {
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);

        assertEquals(0,
                     dtable.getInput().size());

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));

        assertEquals(1,
                     dtable.getInput().size());
        assertEquals(inputClause,
                     dtable.getInput().get(0));
    }

    @Test
    public void testCanvasCommandAllow() throws Exception {
        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.allow(canvasHandler));
    }

    @Test
    public void testCanvasCommandExecute() throws Exception {
        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddRuleCommand = command.newCanvasCommand(canvasHandler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddRuleCommand.execute(canvasHandler));

        assertThat(uiModel.getColumns()).containsOnly(uiRowNumberColumn,
                                                      uiAnnotationClauseColumn);

        verify(executeCanvasOperation).execute();
        verify(command).updateParentInformation();
    }

    @Test
    public void testCanvasCommandExecuteAndThenUndo() throws Exception {
        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddRuleCommand = command.newCanvasCommand(canvasHandler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddRuleCommand.execute(canvasHandler));

        assertThat(uiModel.getColumns()).containsOnly(uiRowNumberColumn,
                                                      uiAnnotationClauseColumn);

        reset(command);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddRuleCommand.undo(canvasHandler));

        assertThat(uiModel.getColumns()).containsOnly(uiRowNumberColumn,
                                                      uiInputClauseColumn,
                                                      uiAnnotationClauseColumn);

        verify(undoCanvasOperation).execute();
        verify(command).updateParentInformation();
    }

    @Test
    public void testComponentWidths() {
        when(uiInputClauseColumn.getWidth()).thenReturn(DMNGridColumn.DEFAULT_WIDTH);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        //Execute
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(dtable.getRequiredComponentWidthCount(),
                     dtable.getComponentWidths().size());

        //Undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));

        assertEquals(dtable.getRequiredComponentWidthCount(),
                     dtable.getComponentWidths().size());
        assertEquals(DMNGridColumn.DEFAULT_WIDTH,
                     dtable.getComponentWidths().get(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT),
                     0.0);
    }
}
