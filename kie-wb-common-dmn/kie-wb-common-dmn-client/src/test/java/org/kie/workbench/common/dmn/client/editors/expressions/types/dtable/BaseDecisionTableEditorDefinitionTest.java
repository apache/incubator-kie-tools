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

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverView;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public abstract class BaseDecisionTableEditorDefinitionTest {

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private TranslationService translationService;

    @Mock
    private HitPolicyPopoverView.Presenter hitPolicyEditor;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private NameAndDataTypePopoverView.Presenter headerEditor;

    @Mock
    protected GridCellTuple parent;

    @Mock
    protected HasExpression hasExpression;

    protected Optional<HasName> hasName = Optional.of(HasName.NOP);

    protected Graph<?, Node> graph = new GraphImpl<>(UUID.uuid(), new GraphNodeStoreImpl());

    protected DecisionTableEditorDefinition definition;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);

        this.definition = new DecisionTableEditorDefinition(definitionUtils,
                                                            sessionManager,
                                                            sessionCommandManager,
                                                            canvasCommandFactory,
                                                            editorSelectedEvent,
                                                            refreshFormPropertiesEvent,
                                                            domainObjectSelectionEvent,
                                                            listSelector,
                                                            translationService,
                                                            hitPolicyEditor,
                                                            headerEditor,
                                                            new DecisionTableEditorDefinitionEnricher(sessionManager,
                                                                                                      new DMNGraphUtils(sessionManager)));

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(anyString());
    }

    protected void assertBasicEnrichment(final DecisionTable model) {
        assertThat(model.getHitPolicy()).isEqualTo(HitPolicy.UNIQUE);
        assertThat(model.getPreferredOrientation()).isEqualTo(DecisionTableOrientation.RULE_AS_ROW);
    }

    protected void assertStandardInputClauseEnrichment(final DecisionTable model) {
        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(1);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(LiteralExpression.class);
        assertThat(input.get(0).getInputExpression().getText().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_PREFIX + "1");
    }

    protected void assertStandardOutputClauseEnrichment(final DecisionTable model) {
        final List<OutputClause> output = model.getOutput();
        assertThat(output.size()).isEqualTo(1);
        assertThat(output.get(0).getName()).isEqualTo(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_PREFIX + "1");
    }

    protected void assertStandardDecisionRuleEnrichment(final DecisionTable model,
                                                        final int inputClauseCount,
                                                        final int outputClauseCount) {
        final List<DecisionRule> rules = model.getRule();
        assertThat(rules.size()).isEqualTo(1);

        final DecisionRule rule = rules.get(0);
        assertThat(rule.getInputEntry().size()).isEqualTo(inputClauseCount);
        rule.getInputEntry().forEach(inputEntry -> {
            assertThat(inputEntry).isInstanceOf(UnaryTests.class);
            assertThat(inputEntry.getText().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT);
        });

        assertThat(rule.getOutputEntry().size()).isEqualTo(outputClauseCount);
        rule.getOutputEntry().forEach(outputEntry -> {
            assertThat(outputEntry).isInstanceOf(LiteralExpression.class);
            assertThat(outputEntry.getText().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT);
        });

        assertThat(rule.getDescription()).isNotNull();
        assertThat(rule.getDescription().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.RULE_DESCRIPTION);
    }

    protected void assertParentHierarchyEnrichment(final DecisionTable model,
                                                   final int inputClauseCount,
                                                   final int outputClauseCount) {
        final List<DecisionRule> rules = model.getRule();
        final DecisionRule rule = rules.get(0);

        final List<InputClause> inputClauses = model.getInput();
        assertThat(inputClauses.size()).isEqualTo(inputClauseCount);
        inputClauses.forEach(inputClause -> {
            assertThat(inputClause.getParent()).isEqualTo(model);
            assertThat(inputClause.getInputExpression().getParent()).isEqualTo(inputClause);
        });

        final List<OutputClause> outputClauses = model.getOutput();
        assertThat(outputClauses.size()).isEqualTo(outputClauseCount);
        outputClauses.forEach(outputClause -> assertThat(outputClause.getParent()).isEqualTo(model));

        assertThat(rule.getParent()).isEqualTo(model);
        final List<UnaryTests> inputEntries = rule.getInputEntry();
        assertThat(inputEntries.size()).isEqualTo(inputClauseCount);
        inputEntries.forEach(inputEntry -> assertThat(inputEntry.getParent()).isEqualTo(rule));

        final List<LiteralExpression> outputEntries = rule.getOutputEntry();
        assertThat(outputEntries.size()).isEqualTo(outputClauseCount);
        outputEntries.forEach(outputEntry -> assertThat(outputEntry.getParent()).isEqualTo(rule));
    }
}
