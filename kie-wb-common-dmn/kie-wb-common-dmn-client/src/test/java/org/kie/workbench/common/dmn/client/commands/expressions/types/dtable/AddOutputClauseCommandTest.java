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
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
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
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddOutputClauseCommandTest {

    @Mock
    private DMNGridLayer selectionManager;

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private OutputClauseColumn uiOutputClauseColumn;

    @Mock
    private InputClauseColumn uiInputClauseColumn;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    private DecisionTable dtable;

    private OutputClause outputClause;

    private GridData uiModel;

    private DecisionTableUIModelMapper uiModelMapper;

    private AddOutputClauseCommand command;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Before
    public void setUp() throws Exception {
        this.dtable = new DecisionTable();
        this.uiModel = new DMNGridData(selectionManager);
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.outputClause = new OutputClause();
        this.uiModelMapper = new DecisionTableUIModelMapper(() -> uiModel,
                                                            () -> Optional.of(dtable));

        this.command = new AddOutputClauseCommand(dtable, outputClause, uiModel, uiOutputClauseColumn, uiModelMapper, canvasOperation);

        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiOutputClauseColumn).getIndex();
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
    public void testGraphCommandExecute() throws Exception {
        dtable.getRule().add(new DecisionRule());
        dtable.getRule().add(new DecisionRule());
        assertEquals(0, dtable.getOutput().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        // one new output column
        assertEquals(1, dtable.getOutput().size());

        // first rule
        assertEquals(1, dtable.getRule().get(0).getOutputEntry().size());
        assertEquals(AddOutputClauseCommand.OUTPUT_CLAUSE_DEFAULT_VALUE, dtable.getRule().get(0).getOutputEntry().get(0).getText());

        // second rule
        assertEquals(1, dtable.getRule().get(1).getOutputEntry().size());
        assertEquals(AddOutputClauseCommand.OUTPUT_CLAUSE_DEFAULT_VALUE, dtable.getRule().get(1).getOutputEntry().get(0).getText());
    }

    @Test
    public void testGraphCommandExecuteExistingNotAffected() throws Exception {
        final String ruleOneOldOutput = "old rule 1";
        final String ruleTwoOldOutput = "old rule 2";

        dtable.getOutput().add(new OutputClause());
        addRuleWithOutputClauseValues(ruleOneOldOutput);
        addRuleWithOutputClauseValues(ruleTwoOldOutput);

        assertEquals(1, dtable.getOutput().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(2, dtable.getOutput().size());

        // first rule
        assertEquals(2, dtable.getRule().get(0).getOutputEntry().size());
        assertEquals(ruleOneOldOutput, dtable.getRule().get(0).getOutputEntry().get(0).getText());
        assertEquals(AddOutputClauseCommand.OUTPUT_CLAUSE_DEFAULT_VALUE, dtable.getRule().get(0).getOutputEntry().get(1).getText());

        // second rule
        assertEquals(2, dtable.getRule().get(1).getOutputEntry().size());
        assertEquals(ruleTwoOldOutput, dtable.getRule().get(1).getOutputEntry().get(0).getText());
        assertEquals(AddOutputClauseCommand.OUTPUT_CLAUSE_DEFAULT_VALUE, dtable.getRule().get(1).getOutputEntry().get(1).getText());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGraphCommandUndoNoOutputClauseColumns() throws Exception {
        dtable.getRule().add(new DecisionRule());

        assertEquals(0, dtable.getOutput().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandUndoJustLastOutputClauseColumn() throws Exception {
        final String ruleOneOldOutput = "old rule 1";
        final String ruleTwoOldOutput = "old rule 2";

        dtable.getOutput().add(new OutputClause());
        addRuleWithOutputClauseValues(ruleOneOldOutput);
        addRuleWithOutputClauseValues(ruleTwoOldOutput);

        assertEquals(1, dtable.getOutput().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));

        assertEquals(1, dtable.getOutput().size());

        // first rule
        assertEquals(1, dtable.getRule().get(0).getOutputEntry().size());
        assertEquals(ruleOneOldOutput, dtable.getRule().get(0).getOutputEntry().get(0).getText());

        // second rule
        assertEquals(1, dtable.getRule().get(1).getOutputEntry().size());
        assertEquals(ruleTwoOldOutput, dtable.getRule().get(1).getOutputEntry().get(0).getText());
    }

    @Test
    public void testCanvasCommandAllow() throws Exception {
        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.allow(canvasHandler));
    }

    @Test
    public void testCanvasCommandAddOutputClauseToRuleWithInputs() throws Exception {
        final String ruleInputValue = "in value";
        final String ruleOutputValue = "out value";

        dtable.getInput().add(new InputClause());
        dtable.getRule().add(new DecisionRule() {{
            getInputEntry().add(new UnaryTests() {{
                setText(ruleInputValue);
            }});
            getOutputEntry().add(new LiteralExpression() {{
                setText(ruleOutputValue);
            }});
        }});

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);

        doReturn(1).when(uiInputClauseColumn).getIndex();
        doReturn(2).when(uiOutputClauseColumn).getIndex();
        uiModel.appendColumn(uiInputClauseColumn);
        uiModel.appendRow(new BaseGridRow());

        uiModelMapper.fromDMNModel(0, 1);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddOutputClauseCommand = command.newCanvasCommand(canvasHandler);
        canvasAddOutputClauseCommand.execute(canvasHandler);

        assertEquals(ruleInputValue, uiModel.getRow(0).getCells().get(1).getValue().getValue());
        assertEquals(ruleOutputValue, uiModel.getRow(0).getCells().get(2).getValue().getValue());

        assertEquals(3, uiModel.getColumnCount());
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddOutputClauseCommand.undo(canvasHandler));
        assertEquals(2, uiModel.getColumnCount());

        // one time in execute(), one time in undo()
        verify(canvasOperation, times(2)).execute();
    }

    @Test
    public void testCanvasCommandAddOutputClauseToRuleWithoutInputsThenUndo() throws Exception {
        final String ruleOneOutputValue = "one";
        final String ruleTwoOutputValue = "two";

        addRuleWithOutputClauseValues(ruleOneOutputValue);
        addRuleWithOutputClauseValues(ruleTwoOutputValue);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);

        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddOutputClauseCommand = command.newCanvasCommand(canvasHandler);
        canvasAddOutputClauseCommand.execute(canvasHandler);

        // first rule
        assertEquals(ruleOneOutputValue, uiModel.getRow(0).getCells().get(1).getValue().getValue());

        // second rule
        assertEquals(ruleTwoOutputValue, uiModel.getRow(1).getCells().get(1).getValue().getValue());

        assertEquals(2, uiModel.getColumnCount());
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddOutputClauseCommand.undo(canvasHandler));
        assertEquals(1, uiModel.getColumnCount());

        // one time in execute(), one time in undo()
        verify(canvasOperation, times(2)).execute();
    }

    @Test
    public void testCanvasCommandUndoWhenNothingBefore() throws Exception {
        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddOutputClauseCommand = command.newCanvasCommand(canvasHandler);

        canvasAddOutputClauseCommand.undo(canvasHandler);
        // just row number column
        assertEquals(1, uiModel.getColumnCount());

        verify(canvasOperation).execute();
    }

    private void addRuleWithOutputClauseValues(String... outputClauseValues) {
        dtable.getRule().add(new DecisionRule() {{
            Stream.of(outputClauseValues).forEach(oClause -> {
                getOutputEntry().add(new LiteralExpression() {{
                    setText(oClause);
                }});
            });
        }});
    }
}
