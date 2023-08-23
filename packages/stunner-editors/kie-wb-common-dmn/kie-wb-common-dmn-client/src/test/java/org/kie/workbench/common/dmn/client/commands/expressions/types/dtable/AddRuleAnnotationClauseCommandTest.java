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
import java.util.Optional;
import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableDefaultValueUtilities;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.RuleAnnotationClauseColumn;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AddRuleAnnotationClauseCommandTest {

    @Mock
    private DecisionTable decisionTable;

    @Mock
    private RuleAnnotationClause ruleAnnotationClause;

    @Mock
    private GridData uiModel;

    @Mock
    private Supplier<RuleAnnotationClauseColumn> uiModelColumnSupplier;

    private final int uiColumnIndex = 4;

    @Mock
    private DecisionTableUIModelMapper uiModelMapper;

    @Mock
    private org.uberfire.mvp.Command executeCanvasOperation;

    @Mock
    private org.uberfire.mvp.Command undoCanvasOperation;

    @Mock
    private List<Double> componentsWidths;

    @Mock
    private List<RuleAnnotationClause> annotations;

    @Captor
    private ArgumentCaptor<RuleAnnotationClauseText> clauseTextCaptor;

    private AddRuleAnnotationClauseCommand command;

    @Before
    public void setup() {
        command = spy(new AddRuleAnnotationClauseCommand(decisionTable,
                                                         ruleAnnotationClause,
                                                         uiModel,
                                                         uiModelColumnSupplier,
                                                         uiColumnIndex,
                                                         uiModelMapper,
                                                         executeCanvasOperation,
                                                         undoCanvasOperation));

        when(decisionTable.getComponentWidths()).thenReturn(componentsWidths);
        when(decisionTable.getAnnotations()).thenReturn(annotations);
    }

    @Test
    public void testNewGraphCommandExecute() {

        final AbstractCanvasHandler context = mock(AbstractCanvasHandler.class);
        final GraphCommandExecutionContext executionContext = mock(GraphCommandExecutionContext.class);
        final DecisionRule rule1 = mock(DecisionRule.class);
        final DecisionRule rule2 = mock(DecisionRule.class);
        final List<DecisionRule> rules = Arrays.asList(rule1, rule2);
        final int clauseIndex = 2;
        final Name ruleAnnotationClauseName = mock(Name.class);
                final List rule1AnnotationEntries = mock(List.class);
        final List rule2AnnotationEntries = mock(List.class);

        doReturn(clauseIndex).when(command).getClauseIndex();

        when(rule1.getAnnotationEntry()).thenReturn(rule1AnnotationEntries);
        when(rule2.getAnnotationEntry()).thenReturn(rule2AnnotationEntries);
        when(ruleAnnotationClause.getName()).thenReturn(ruleAnnotationClauseName);
        when(decisionTable.getRule()).thenReturn(rules);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(context);

        final CommandResult<RuleViolation> commandResult = graphCommand.execute(executionContext);

        verify(rule1AnnotationEntries).add(eq(clauseIndex), clauseTextCaptor.capture());
        verify(rule2AnnotationEntries).add(eq(clauseIndex), clauseTextCaptor.capture());
        verify(componentsWidths).add(uiColumnIndex, null);
        verify(annotations).add(clauseIndex, ruleAnnotationClause);
        verify(ruleAnnotationClause).setParent(decisionTable);

        final List<RuleAnnotationClauseText> capturedValues = clauseTextCaptor.getAllValues();
        assertEquals(2, capturedValues.size());
        assertEquals(DecisionTableDefaultValueUtilities.RULE_ANNOTATION_CLAUSE_EXPRESSION_TEXT, capturedValues.get(0).getText().getValue());
        assertEquals(DecisionTableDefaultValueUtilities.RULE_ANNOTATION_CLAUSE_EXPRESSION_TEXT, capturedValues.get(1).getText().getValue());
        assertEquals(rule1, capturedValues.get(0).getParent());
        assertEquals(rule2, capturedValues.get(1).getParent());
        assertEquals(GraphCommandResultBuilder.SUCCESS, commandResult);
    }

    @Test
    public void testNewGraphCommandUndo() {

        final AbstractCanvasHandler context = mock(AbstractCanvasHandler.class);
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(context);
        final GraphCommandExecutionContext executionContext = mock(GraphCommandExecutionContext.class);
        final int ruleAnnotationIndex = 3;
        final DecisionRule rule1 = mock(DecisionRule.class);
        final DecisionRule rule2 = mock(DecisionRule.class);
        final List rule1AnnotationEntries = mock(List.class);
        final List rule2AnnotationEntries = mock(List.class);
        final List<DecisionRule> rules = Arrays.asList(rule1, rule2);

        when(rule1.getAnnotationEntry()).thenReturn(rule1AnnotationEntries);
        when(rule2.getAnnotationEntry()).thenReturn(rule2AnnotationEntries);
        when(decisionTable.getRule()).thenReturn(rules);
        when(annotations.indexOf(ruleAnnotationClause)).thenReturn(ruleAnnotationIndex);

        final CommandResult<RuleViolation> undoResult = graphCommand.undo(executionContext);

        verify(componentsWidths).remove(uiColumnIndex);
        verify(rule1AnnotationEntries).remove(ruleAnnotationIndex);
        verify(rule2AnnotationEntries).remove(ruleAnnotationIndex);
        verify(annotations).remove(ruleAnnotationClause);

        assertEquals(GraphCommandResultBuilder.SUCCESS, undoResult);
    }

    @Test
    public void testGetClauseIndex() {

        final List dtOutputs = mock(List.class);
        final List dtInputs = mock(List.class);
        final int outputsSize = 3;
        final int inputsSize = 7;
        when(dtOutputs.size()).thenReturn(outputsSize);
        when(dtInputs.size()).thenReturn(inputsSize);
        when(decisionTable.getOutput()).thenReturn(dtOutputs);
        when(decisionTable.getInput()).thenReturn(dtInputs);

        final int expected = uiColumnIndex - DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT - inputsSize - outputsSize;

        final int actual = command.getClauseIndex();

        verify(dtOutputs).size();
        verify(dtInputs).size();

        assertEquals(expected, actual);
    }

    @Test
    public void testNewCanvasCommandExecute() {

        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);
        final RuleAnnotationClauseColumn column = mock(RuleAnnotationClauseColumn.class);
        final List<DecisionRule> rules = mock(List.class);
        when(rules.size()).thenReturn(3);
        when(decisionTable.getRule()).thenReturn(rules);
        when(uiModelColumnSupplier.get()).thenReturn(column);

        final CommandResult<CanvasViolation> result = canvasCommand.execute(canvasHandler);

        verify(uiModelMapper).fromDMNModel(0, uiColumnIndex);
        verify(uiModelMapper).fromDMNModel(1, uiColumnIndex);
        verify(uiModelMapper).fromDMNModel(2, uiColumnIndex);
        verify(command).updateParentInformation();
        verify(executeCanvasOperation).execute();
        verify(uiModelColumnSupplier).get();
        verify(uiModel).insertColumn(uiColumnIndex, column);
        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
    }

    @Test
    public void testNewCanvasCommandUndo() {

        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);

        final RuleAnnotationClauseColumn column = mock(RuleAnnotationClauseColumn.class);
        final Optional<RuleAnnotationClauseColumn> uiModelColumn = Optional.of(column);
        doReturn(uiModelColumn).when(command).getUiModelColumn();

        final CommandResult<CanvasViolation> result = canvasCommand.undo(canvasHandler);

        verify(command).updateParentInformation();
        verify(undoCanvasOperation).execute();
        verify(uiModel).deleteColumn(column);

        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
    }
}