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

package org.kie.workbench.common.dmn.client.commands.expressions.types.dtable;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionRuleFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableDefaultValueUtilities;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DescriptionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddDecisionRuleCommandTest {

    private DecisionTable dtable;

    private DecisionRule rule;

    private GridData uiModel;

    private GridRow uiModelRow;

    private DecisionTableUIModelMapper uiModelMapper;

    private AddDecisionRuleCommand command;

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private InputClauseColumn uiInputClauseColumn;

    @Mock
    private OutputClauseColumn uiOutputClauseColumn;

    @Mock
    private DescriptionColumn uiDescriptionColumn;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Before
    public void setup() {
        this.dtable = new DecisionTable();
        this.uiModel = new DMNGridData();
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModelRow = new BaseGridRow();
        this.uiModelMapper = new DecisionTableUIModelMapper(() -> uiModel,
                                                            () -> Optional.of(dtable),
                                                            listSelector);

        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiInputClauseColumn).getIndex();
        doReturn(2).when(uiOutputClauseColumn).getIndex();
        doReturn(3).when(uiDescriptionColumn).getIndex();
    }

    private void makeCommand(final int index) {
        this.rule = DecisionRuleFactory.makeDecisionRule(dtable);
        this.command = spy(new AddDecisionRuleCommand(dtable,
                                                      rule,
                                                      uiModel,
                                                      uiModelRow,
                                                      index,
                                                      uiModelMapper,
                                                      canvasOperation));
    }

    @Test
    public void testGraphCommandAllow() throws Exception {
        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandCheck() throws Exception {
        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandExecuteConstructedDescription() {
        makeCommand(0);

        assertEquals(0, dtable.getRule().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));
        assertEquals(1, dtable.getRule().size());
        assertEquals(rule, dtable.getRule().get(0));
        assertTrue(rule.getDescription() != null);
        assertTrue(rule.getDescription().getValue() != null);
        assertEquals(DecisionTableDefaultValueUtilities.RULE_DESCRIPTION, rule.getDescription().getValue());

        assertEquals(dtable,
                     rule.getParent());
    }

    @Test
    public void testGraphCommandExecuteConstructedRuleInputs() {
        assertEquals(0, dtable.getRule().size());
        final int inputsCount = 2;

        for (int i = 0; i < inputsCount; i++) {
            dtable.getInput().add(new InputClause());
        }

        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));
        assertEquals(1, dtable.getRule().size());
        assertEquals(rule, dtable.getRule().get(0));

        assertEquals(inputsCount, rule.getInputEntry().size());
        assertEquals(0, rule.getOutputEntry().size());

        for (int inputIndex = 0; inputIndex < inputsCount; inputIndex++) {
            assertTrue(rule.getInputEntry().get(inputIndex).getText() != null);
            assertEquals(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT, rule.getInputEntry().get(inputIndex).getText().getValue());
            assertEquals(rule, rule.getInputEntry().get(inputIndex).getParent());
        }

        assertEquals(dtable,
                     rule.getParent());
    }

    @Test
    public void testGraphCommandExecuteConstructedRuleOutputs() {
        assertEquals(0, dtable.getRule().size());
        final int outputsCount = 2;

        for (int i = 0; i < outputsCount; i++) {
            dtable.getOutput().add(new OutputClause());
        }

        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));
        assertEquals(1, dtable.getRule().size());
        assertEquals(rule, dtable.getRule().get(0));

        assertEquals(0, rule.getInputEntry().size());
        assertEquals(outputsCount, rule.getOutputEntry().size());

        for (int outputIndex = 0; outputIndex < outputsCount; outputIndex++) {
            assertTrue(rule.getOutputEntry().get(outputIndex).getText() != null);
            assertEquals(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT, rule.getOutputEntry().get(outputIndex).getText().getValue());
            assertEquals(rule, rule.getOutputEntry().get(outputIndex).getParent());
        }

        assertEquals(dtable,
                     rule.getParent());
    }

    @Test
    public void testGraphCommandUndo() {
        makeCommand(0);

        assertEquals(0, dtable.getRule().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);

        assertEquals(1, dtable.getRule().size());

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));

        assertEquals(0, dtable.getRule().size());
    }

    @Test
    public void testGraphCommandExecuteInsertBelow() {
        //The default behaviour of tests in this class is to "insert above"
        final DecisionRule existingRule = new DecisionRule();
        dtable.getRule().add(existingRule);
        makeCommand(1);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);

        assertEquals(2,
                     dtable.getRule().size());
        assertEquals(existingRule,
                     dtable.getRule().get(0));
        assertEquals(rule,
                     dtable.getRule().get(1));
    }

    @Test
    public void testGraphCommandExecuteInsertBelowThenUndo() {
        //The default behaviour of tests in this class is to "insert above"
        final DecisionRule existingRule = new DecisionRule();
        dtable.getRule().add(existingRule);
        makeCommand(1);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);
        graphCommand.undo(graphCommandExecutionContext);

        assertEquals(1,
                     dtable.getRule().size());
        assertEquals(existingRule,
                     dtable.getRule().get(0));
    }

    @Test
    public void testCanvasCommandAllow() throws Exception {
        makeCommand(0);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.allow(canvasHandler));
    }

    @Test
    public void testCanvasCommandAddRuleAndThenUndo() throws Exception {
        dtable.getInput().add(new InputClause());
        dtable.getOutput().add(new OutputClause());

        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);

        uiModel.appendColumn(uiInputClauseColumn);
        uiModel.appendColumn(uiOutputClauseColumn);
        uiModel.appendColumn(uiDescriptionColumn);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddRuleCommand = command.newCanvasCommand(canvasHandler);
        canvasAddRuleCommand.execute(canvasHandler);

        assertEquals(1, uiModel.getRowCount());
        assertDefaultUiRowValues(0);

        canvasAddRuleCommand.undo(canvasHandler);
        assertEquals(0, uiModel.getRowCount());

        // one time in execute(), one time in undo()
        verify(canvasOperation, times(2)).execute();
        verify(command, times(2)).updateRowNumbers();
        verify(command, times(2)).updateParentInformation();
    }

    @Test
    public void testCanvasCommandExecuteInsertBelow() {
        //The default behaviour of tests in this class is to "insert above"
        final DecisionRule existingRule = new DecisionRule();
        final GridRow existingUiRow = new BaseGridRow();
        dtable.getRule().add(existingRule);
        uiModel.appendRow(existingUiRow);

        dtable.getInput().add(new InputClause());
        dtable.getOutput().add(new OutputClause());

        makeCommand(1);

        uiModel.appendColumn(uiInputClauseColumn);
        uiModel.appendColumn(uiOutputClauseColumn);
        uiModel.appendColumn(uiDescriptionColumn);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);
        canvasCommand.execute(canvasHandler);

        assertEquals(2,
                     uiModel.getRowCount());
        assertEquals(existingUiRow,
                     uiModel.getRow(0));
        assertEquals(uiModelRow,
                     uiModel.getRow(1));
        assertDefaultUiRowValues(1);

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();
    }

    @Test
    public void testCanvasCommandExecuteInsertBelowThenUndo() {
        //The default behaviour of tests in this class is to "insert above"
        final DecisionRule existingRule = new DecisionRule();
        final GridRow existingUiRow = new BaseGridRow();
        dtable.getRule().add(existingRule);
        uiModel.appendRow(existingUiRow);
        makeCommand(1);

        uiModel.appendColumn(uiInputClauseColumn);
        uiModel.appendColumn(uiOutputClauseColumn);
        uiModel.appendColumn(uiDescriptionColumn);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);
        canvasCommand.execute(canvasHandler);
        canvasCommand.undo(canvasHandler);

        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(existingUiRow,
                     uiModel.getRow(0));

        // one time in execute(), one time in undo()
        verify(canvasOperation, times(2)).execute();
        verify(command, times(2)).updateRowNumbers();
        verify(command, times(2)).updateParentInformation();
    }

    private void assertDefaultUiRowValues(final int uiRowIndex) {
        final GridRow uiGridRow = uiModel.getRow(uiRowIndex);
        assertEquals(uiRowIndex + 1, uiGridRow.getCells().get(0).getValue().getValue());
        assertEquals(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT, uiGridRow.getCells().get(1).getValue().getValue());
        assertEquals(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT, uiGridRow.getCells().get(2).getValue().getValue());
        assertEquals(DecisionTableDefaultValueUtilities.RULE_DESCRIPTION, uiGridRow.getCells().get(3).getValue().getValue());
    }
}
