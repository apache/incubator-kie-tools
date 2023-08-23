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
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DeleteRuleAnnotationClauseCommandTest {

    @Mock
    private DecisionTable decisionTable;

    @Test
    public void testExtractColumnData() {

        final DeleteRuleAnnotationClauseCommand command = mock(DeleteRuleAnnotationClauseCommand.class);
        doCallRealMethod().when(command).extractColumnData();

        final int clauseIndex = 2;
        final DecisionRule rule1 = mock(DecisionRule.class);
        final DecisionRule rule2 = mock(DecisionRule.class);
        final List<DecisionRule> rules = Arrays.asList(rule1, rule2);
        final List rule1AnnotationEntry = mock(List.class);
        final List rule2AnnotationEntry = mock(List.class);
        final RuleAnnotationClauseText text1 = mock(RuleAnnotationClauseText.class);
        final RuleAnnotationClauseText text2 = mock(RuleAnnotationClauseText.class);

        when(command.getDecisionTable()).thenReturn(decisionTable);
        when(command.getRuleAnnotationClauseIndex()).thenReturn(clauseIndex);
        when(rule1AnnotationEntry.get(clauseIndex)).thenReturn(text1);
        when(rule2AnnotationEntry.get(clauseIndex)).thenReturn(text2);
        when(rule1.getAnnotationEntry()).thenReturn(rule1AnnotationEntry);
        when(rule2.getAnnotationEntry()).thenReturn(rule2AnnotationEntry);
        when(decisionTable.getRule()).thenReturn(rules);

        final List<RuleAnnotationClauseText> columnData = command.extractColumnData();

        assertEquals(2, columnData.size());
        assertTrue(columnData.contains(text1));
        assertTrue(columnData.contains(text2));
    }

    @Test
    public void testGetRuleAnnotationClauseIndex() {

        final int columnIndex = 15;
        final int inputSize = 2;
        final int outputSize = 5;
        final List input = mock(List.class);
        final List output = mock(List.class);
        final DeleteRuleAnnotationClauseCommand deleteCommand = mock(DeleteRuleAnnotationClauseCommand.class);
        doCallRealMethod().when(deleteCommand).getRuleAnnotationClauseIndex();
        when(deleteCommand.getDecisionTable()).thenReturn(decisionTable);
        when(deleteCommand.getUiColumnIndex()).thenReturn(columnIndex);
        when(input.size()).thenReturn(inputSize);
        when(output.size()).thenReturn(outputSize);
        when(decisionTable.getInput()).thenReturn(input);
        when(decisionTable.getOutput()).thenReturn(output);

        final int expected = columnIndex - DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT - inputSize - outputSize;

        final int actual = deleteCommand.getRuleAnnotationClauseIndex();

        assertEquals(expected, actual);
    }

    @Test
    public void testNewGraphCommandExecute() {

        final int uiColumnIndex = 3;
        final int annotationClauseIndex = 2;
        final AbstractCanvasHandler handler = mock(AbstractCanvasHandler.class);
        final DeleteRuleAnnotationClauseCommand command = mock(DeleteRuleAnnotationClauseCommand.class);
        final DecisionRule rule1 = mock(DecisionRule.class);
        final DecisionRule rule2 = mock(DecisionRule.class);
        final List<DecisionRule> rules = Arrays.asList(rule1, rule2);
        final List rule1AnnotationEntries = mock(List.class);
        final List rule2AnnotationEntries = mock(List.class);
        final List annotations = mock(List.class);
        final List widths = mock(List.class);
        final GraphCommandExecutionContext context = mock(GraphCommandExecutionContext.class);

        doCallRealMethod().when(command).newGraphCommand(handler);
        when(command.getDecisionTable()).thenReturn(decisionTable);
        when(command.getRuleAnnotationClauseIndex()).thenReturn(annotationClauseIndex);
        when(command.getUiColumnIndex()).thenReturn(uiColumnIndex);
        when(rule1.getAnnotationEntry()).thenReturn(rule1AnnotationEntries);
        when(rule2.getAnnotationEntry()).thenReturn(rule2AnnotationEntries);
        when(decisionTable.getRule()).thenReturn(rules);
        when(decisionTable.getAnnotations()).thenReturn(annotations);
        when(decisionTable.getComponentWidths()).thenReturn(widths);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(handler);

        final CommandResult<RuleViolation> result = graphCommand.execute(context);

        assertEquals(GraphCommandResultBuilder.SUCCESS, result);
        verify(widths).remove(uiColumnIndex);
        verify(rule1AnnotationEntries).remove(annotationClauseIndex);
        verify(rule2AnnotationEntries).remove(annotationClauseIndex);
        verify(annotations).remove(annotationClauseIndex);
    }

    @Test
    public void testNewGraphCommandUndo() {

        final int uiColumnIndex = 3;
        final int annotationClauseIndex = 2;
        final AbstractCanvasHandler handler = mock(AbstractCanvasHandler.class);
        final DeleteRuleAnnotationClauseCommand command = mock(DeleteRuleAnnotationClauseCommand.class);
        final DecisionRule rule1 = mock(DecisionRule.class);
        final DecisionRule rule2 = mock(DecisionRule.class);
        final List<DecisionRule> rules = Arrays.asList(rule1, rule2);
        final List rule1AnnotationEntries = mock(List.class);
        final List rule2AnnotationEntries = mock(List.class);
        final List annotations = mock(List.class);
        final List widths = mock(List.class);
        final GraphCommandExecutionContext context = mock(GraphCommandExecutionContext.class);
        final RuleAnnotationClause oldRuleClause = mock(RuleAnnotationClause.class);
        final GridColumn oldUiModelColumn = mock(GridColumn.class);
        final double oldUiModelColumnWidth = 123.4D;
        final RuleAnnotationClauseText deleted1 = mock(RuleAnnotationClauseText.class);
        final RuleAnnotationClauseText deleted2 = mock(RuleAnnotationClauseText.class);
        final List<RuleAnnotationClauseText> oldCommandData = Arrays.asList(deleted1, deleted2);

        doCallRealMethod().when(command).newGraphCommand(handler);
        when(command.getOldColumnData()).thenReturn(oldCommandData);
        when(command.getOldRuleClause()).thenReturn(oldRuleClause);
        when(oldUiModelColumn.getWidth()).thenReturn(oldUiModelColumnWidth);
        when(command.getOldUiModelColumn()).thenReturn(oldUiModelColumn);
        when(command.getDecisionTable()).thenReturn(decisionTable);
        when(command.getRuleAnnotationClauseIndex()).thenReturn(annotationClauseIndex);
        when(command.getUiColumnIndex()).thenReturn(uiColumnIndex);
        when(rule1.getAnnotationEntry()).thenReturn(rule1AnnotationEntries);
        when(rule2.getAnnotationEntry()).thenReturn(rule2AnnotationEntries);
        when(decisionTable.getRule()).thenReturn(rules);
        when(decisionTable.getAnnotations()).thenReturn(annotations);
        when(decisionTable.getComponentWidths()).thenReturn(widths);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(handler);

        final CommandResult<RuleViolation> result = graphCommand.undo(context);

        assertEquals(GraphCommandResultBuilder.SUCCESS, result);
        verify(annotations).add(annotationClauseIndex, oldRuleClause);
        verify(rule1AnnotationEntries).add(annotationClauseIndex, deleted1);
        verify(rule2AnnotationEntries).add(annotationClauseIndex, deleted2);
    }

    @Test
    public void testNewCanvasCommandExecute() {

        final DeleteRuleAnnotationClauseCommand command = mock(DeleteRuleAnnotationClauseCommand.class);
        final AbstractCanvasHandler handler = mock(AbstractCanvasHandler.class);
        final int uiColumnIndex = 7;
        final List columns = mock(List.class);
        final GridData uiModel = mock(GridData.class);
        final org.uberfire.mvp.Command executeCanvasOperation = mock(org.uberfire.mvp.Command.class);
        final GridColumn gridColumn = mock(GridColumn.class);

        doCallRealMethod().when(command).newCanvasCommand(handler);
        when(command.getUiModel()).thenReturn(uiModel);
        when(uiModel.getColumns()).thenReturn(columns);
        when(command.getUiColumnIndex()).thenReturn(uiColumnIndex);
        when(command.getExecuteCanvasOperation()).thenReturn(executeCanvasOperation);
        when(columns.get(uiColumnIndex)).thenReturn(gridColumn);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(handler);
        final CommandResult<CanvasViolation> result = canvasCommand.execute(handler);

        verify(uiModel).deleteColumn(gridColumn);
        verify(command).updateParentInformation();
        verify(executeCanvasOperation).execute();
        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
    }

    @Test
    public void testNewCanvasCommandUndo() {
        final DeleteRuleAnnotationClauseCommand command = mock(DeleteRuleAnnotationClauseCommand.class);
        final AbstractCanvasHandler handler = mock(AbstractCanvasHandler.class);
        final GridData uiModel = mock(GridData.class);
        final GridColumn oldUiModelColumn = mock(GridColumn.class);
        final int uiColumnIndex = 3;
        final DecisionRule rule1 = mock(DecisionRule.class);
        final DecisionRule rule2 = mock(DecisionRule.class);
        final List<DecisionRule> rules = Arrays.asList(rule1, rule2);
        final org.uberfire.mvp.Command undoCanvasOperation = mock(org.uberfire.mvp.Command.class);
        final DecisionTableUIModelMapper uiModelMapper = mock(DecisionTableUIModelMapper.class);

        doCallRealMethod().when(command).newCanvasCommand(handler);

        when(command.getUiModel()).thenReturn(uiModel);
        when(command.getUiColumnIndex()).thenReturn(uiColumnIndex);
        when(command.getOldUiModelColumn()).thenReturn(oldUiModelColumn);
        when(command.getDecisionTable()).thenReturn(decisionTable);
        when(decisionTable.getRule()).thenReturn(rules);
        when(command.getUiModelMapper()).thenReturn(uiModelMapper);
        when(command.getUndoCanvasOperation()).thenReturn(undoCanvasOperation);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(handler);
        final CommandResult<CanvasViolation> result = canvasCommand.undo(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
        verify(uiModelMapper).fromDMNModel(0, uiColumnIndex);
        verify(uiModelMapper).fromDMNModel(1, uiColumnIndex);
        verify(command).updateParentInformation();
        verify(undoCanvasOperation).execute();
    }
}