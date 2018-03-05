/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddOutputClauseCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DecisionTableGridTest {

    private static final String HASNAME_NAME = "name";

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandContext;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    @Mock
    private EventSourceMock<ExpressionEditorSelectedEvent> editorSelectedEvent;

    @Mock
    private ManagedInstance<DecisionTableGridControls> controlsProvider;

    @Mock
    private DecisionTableGridControls controls;

    @Mock
    private CellEditorControls cellEditorControls;

    @Mock
    private TranslationService translationService;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Captor
    private ArgumentCaptor<AddInputClauseCommand> addInputClauseCommandCaptor;

    @Captor
    private ArgumentCaptor<AddOutputClauseCommand> addOutputClauseCommandCaptor;

    @Captor
    private ArgumentCaptor<AddDecisionRuleCommand> addDecisionRuleCommandCaptor;

    private Optional<DecisionTable> expression;

    private DecisionTableGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        final DecisionTableEditorDefinition definition = new DecisionTableEditorDefinition(gridPanel,
                                                                                           gridLayer,
                                                                                           sessionManager,
                                                                                           sessionCommandManager,
                                                                                           editorSelectedEvent,
                                                                                           cellEditorControls,
                                                                                           translationService,
                                                                                           controlsProvider);

        expression = definition.getModelClass();

        final Decision decision = new Decision();
        decision.setName(new Name(HASNAME_NAME));
        final Optional<HasName> hasName = Optional.of(decision);

        doReturn(controls).when(controlsProvider).get();
        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(canvasHandler).when(session).getCanvasHandler();
        doReturn(graphCommandContext).when(canvasHandler).getGraphExecutionContext();
        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());

        this.grid = spy((DecisionTableGrid) definition.getEditor(parent,
                                                                 hasExpression,
                                                                 expression,
                                                                 hasName,
                                                                 false).get());
    }

    @Test
    public void testInitialSetupFromDefinition() {
        final GridData uiModel = grid.getModel();
        assertTrue(uiModel instanceof DecisionTableGridData);

        assertEquals(4,
                     uiModel.getColumnCount());
        assertTrue(uiModel.getColumns().get(0) instanceof DecisionTableRowNumberColumn);
        assertTrue(uiModel.getColumns().get(1) instanceof InputClauseColumn);
        assertTrue(uiModel.getColumns().get(2) instanceof OutputClauseColumn);
        assertTrue(uiModel.getColumns().get(3) instanceof DescriptionColumn);

        assertEquals(1,
                     uiModel.getRowCount());

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(DecisionTableEditorDefinition.INPUT_CLAUSE_UNARY_TEST_TEXT,
                     uiModel.getCell(0, 1).getValue().getValue());
        assertEquals(DecisionTableEditorDefinition.OUTPUT_CLAUSE_EXPRESSION_TEXT,
                     uiModel.getCell(0, 2).getValue().getValue());
        assertEquals(DecisionTableEditorDefinition.RULE_DESCRIPTION,
                     uiModel.getCell(0, 3).getValue().getValue());
    }

    @Test
    public void testGetEditorControlsEnabled() {
        final DecisionTable dtable = expression.get();
        dtable.setHitPolicy(HitPolicy.FIRST);
        dtable.setAggregation(BuiltinAggregator.COUNT);
        dtable.setPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW);

        grid.getEditorControls();

        verify(controls).enableHitPolicies(eq(true));
        verify(controls).enableBuiltinAggregators(eq(true));
        verify(controls).enableDecisionTableOrientation(eq(true));

        verify(controls).initSelectedHitPolicy(eq(HitPolicy.FIRST));
        verify(controls).initSelectedBuiltinAggregator(BuiltinAggregator.COUNT);
        verify(controls).initSelectedDecisionTableOrientation(DecisionTableOrientation.RULE_AS_ROW);
    }

    @Test
    public void testGetEditorControlsDisabled() {
        //The DMN model returns defaults for HitPolicy and DecisionTableOrientation
        final DecisionTable dtable = expression.get();
        dtable.setHitPolicy(null);
        dtable.setAggregation(null);
        dtable.setPreferredOrientation(null);

        grid.getEditorControls();

        verify(controls).enableHitPolicies(eq(true));
        verify(controls, atLeast(1)).enableBuiltinAggregators(eq(false));
        verify(controls).enableDecisionTableOrientation(eq(true));

        verify(controls).initSelectedHitPolicy(eq(HitPolicy.UNIQUE));
        verify(controls, never()).initSelectedBuiltinAggregator(any(BuiltinAggregator.class));
        verify(controls).initSelectedDecisionTableOrientation(eq(DecisionTableOrientation.RULE_AS_ROW));
    }

    @Test
    public void testColumn0MetaData() {
        final GridColumn<?> column = grid.getModel().getColumns().get(0);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof RowNumberColumnHeaderMetaData);

        final RowNumberColumnHeaderMetaData md = (RowNumberColumnHeaderMetaData) header.get(0);
        expression.get().setHitPolicy(HitPolicy.FIRST);
        assertEquals("F",
                     md.getTitle());

        expression.get().setHitPolicy(HitPolicy.ANY);
        assertEquals("A",
                     md.getTitle());
    }

    @Test
    public void testColumn1MetaData() {
        final GridColumn<?> column = grid.getModel().getColumns().get(1);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof InputClauseColumnHeaderMetaData);

        final InputClauseColumnHeaderMetaData md = (InputClauseColumnHeaderMetaData) header.get(0);
        assertEquals(DecisionTableEditorDefinition.INPUT_CLAUSE_EXPRESSION_TEXT,
                     md.getTitle());
    }

    @Test
    public void testColumn2MetaData() {
        final GridColumn<?> column = grid.getModel().getColumns().get(2);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof OutputClauseColumnExpressionNameHeaderMetaData);

        final OutputClauseColumnExpressionNameHeaderMetaData md = (OutputClauseColumnExpressionNameHeaderMetaData) header.get(0);
        assertEquals(HASNAME_NAME,
                     md.getTitle());
    }

    @Test
    public void testColumn3MetaData() {
        final GridColumn<?> column = grid.getModel().getColumns().get(3);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof BaseHeaderMetaData);

        final BaseHeaderMetaData md = (BaseHeaderMetaData) header.get(0);
        assertEquals(DMNEditorConstants.DecisionTableEditor_DescriptionColumnHeader,
                     md.getTitle());
    }

    @Test
    public void testAddInputClause() {
        grid.addInputClause();

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              addInputClauseCommandCaptor.capture());

        final AddInputClauseCommand addInputClauseCommand = addInputClauseCommandCaptor.getValue();
        addInputClauseCommand.execute(canvasHandler);

        verify(parent).assertWidth(eq(grid.getWidth() + grid.getPadding() * 2));
        verifyGridPanelRefresh();
    }

    @Test
    public void testAddOutputClause() {
        grid.addOutputClause();

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              addOutputClauseCommandCaptor.capture());

        final AddOutputClauseCommand addOutputClauseCommand = addOutputClauseCommandCaptor.getValue();
        addOutputClauseCommand.execute(canvasHandler);

        verify(parent).assertWidth(eq(grid.getWidth() + grid.getPadding() * 2));
        verifyGridPanelRefresh();
    }

    @Test
    public void testAddDecisionRule() {
        grid.addDecisionRule();

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              addDecisionRuleCommandCaptor.capture());

        final AddDecisionRuleCommand addDecisionRuleCommand = addDecisionRuleCommandCaptor.getValue();
        addDecisionRuleCommand.execute(canvasHandler);

        verifyGridPanelRefresh();
    }

    private void verifyGridPanelRefresh() {
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch();
    }

    @Test
    public void testSetHitPolicyToNull() {
        grid.setHitPolicy(null);

        final DecisionTable dtable = expression.get();

        //The DMN model returns a default for HitPolicy if not set
        assertThat(dtable.getHitPolicy()).isEqualTo(HitPolicy.UNIQUE);
        assertThat(dtable.getAggregation()).isNull();

        verify(controls).enableBuiltinAggregators(eq(false));
    }

    @Test
    public void testSetHitPolicyThatDoesNotSupportAggregation() {
        grid.setHitPolicy(HitPolicy.PRIORITY);

        final DecisionTable dtable = expression.get();

        assertThat(dtable.getHitPolicy()).isEqualTo(HitPolicy.PRIORITY);
        assertThat(dtable.getAggregation()).isNull();

        verify(controls).enableBuiltinAggregators(eq(false));
    }

    @Test
    public void testSetHitPolicyThatDoesSupportAggregationWhenAggregationNotSet() {
        grid.setHitPolicy(HitPolicy.COLLECT);

        final DecisionTable dtable = expression.get();

        assertThat(dtable.getHitPolicy()).isEqualTo(HitPolicy.COLLECT);
        assertThat(dtable.getAggregation()).isEqualTo(BuiltinAggregator.COUNT);

        verify(controls).enableBuiltinAggregators(eq(true));
        verify(controls).initSelectedBuiltinAggregator(eq(BuiltinAggregator.COUNT));
    }

    @Test
    public void testSetHitPolicyThatDoesSupportAggregationWhenAggregationIsSet() {
        grid.setBuiltinAggregator(BuiltinAggregator.MIN);
        reset(controls);

        grid.setHitPolicy(HitPolicy.COLLECT);

        final DecisionTable dtable = expression.get();

        assertThat(dtable.getHitPolicy()).isEqualTo(HitPolicy.COLLECT);
        assertThat(dtable.getAggregation()).isEqualTo(BuiltinAggregator.MIN);

        verify(controls).enableBuiltinAggregators(eq(true));
        verify(controls).initSelectedBuiltinAggregator(eq(BuiltinAggregator.MIN));
    }

    @Test
    public void testSetBuiltinAggregatorToNull() {
        grid.setBuiltinAggregator(null);

        final DecisionTable dtable = expression.get();

        assertThat(dtable.getAggregation()).isNull();
    }

    @Test
    public void testSetBuiltinAggregatorToNotNull() {
        grid.setBuiltinAggregator(BuiltinAggregator.MIN);

        final DecisionTable dtable = expression.get();

        assertThat(dtable.getAggregation()).isEqualTo(BuiltinAggregator.MIN);
    }

    @Test
    public void testSetDecisionTableOrientationToNull() {
        grid.setDecisionTableOrientation(null);

        final DecisionTable dtable = expression.get();

        //The DMN model returns a default for DecisionTableOrientation if not set
        assertThat(dtable.getPreferredOrientation()).isEqualTo(DecisionTableOrientation.RULE_AS_ROW);
    }

    @Test
    public void testSetDecisionTableOrientationToNotNull() {
        grid.setDecisionTableOrientation(DecisionTableOrientation.CROSS_TABLE);

        final DecisionTable dtable = expression.get();

        assertThat(dtable.getPreferredOrientation()).isEqualTo(DecisionTableOrientation.CROSS_TABLE);
    }
}
