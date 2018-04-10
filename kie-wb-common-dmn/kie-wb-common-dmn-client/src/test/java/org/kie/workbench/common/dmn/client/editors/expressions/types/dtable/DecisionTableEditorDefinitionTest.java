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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyEditorView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;

@RunWith(LienzoMockitoTestRunner.class)
public class DecisionTableEditorDefinitionTest {

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

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
    private HitPolicyEditorView.Presenter hitPolicyEditor;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    private Optional<HasName> hasName = Optional.of(HasName.NOP);

    private DecisionTableEditorDefinition definition;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.definition = new DecisionTableEditorDefinition(gridPanel,
                                                            gridLayer,
                                                            definitionUtils,
                                                            sessionManager,
                                                            sessionCommandManager,
                                                            canvasCommandFactory,
                                                            editorSelectedEvent,
                                                            cellEditorControls,
                                                            listSelector,
                                                            translationService,
                                                            hitPolicyEditor);
        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
    }

    @Test
    public void testType() {
        assertThat(definition.getType()).isEqualTo(ExpressionType.DECISION_TABLE);
    }

    @Test
    public void testName() {
        assertThat(definition.getName()).isEqualTo(DMNEditorConstants.ExpressionEditor_DecisionTableExpressionType);
    }

    @Test
    public void testModelDefinition() {
        final Optional<DecisionTable> oModel = definition.getModelClass();
        assertThat(oModel).isPresent();

        final DecisionTable model = oModel.get();
        assertThat(model.getHitPolicy()).isEqualTo(HitPolicy.ANY);
        assertThat(model.getPreferredOrientation()).isEqualTo(DecisionTableOrientation.RULE_AS_ROW);

        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(1);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(LiteralExpression.class);

        final List<OutputClause> output = model.getOutput();
        assertThat(output.size()).isEqualTo(1);

        final List<DecisionRule> rules = model.getRule();
        assertThat(rules.size()).isEqualTo(1);

        final DecisionRule rule = rules.get(0);
        assertThat(rule.getInputEntry().size()).isEqualTo(1);
        assertThat(rule.getInputEntry().get(0)).isInstanceOf(UnaryTests.class);

        assertThat(rule.getOutputEntry().size()).isEqualTo(1);
        assertThat(rule.getOutputEntry().get(0)).isInstanceOf(LiteralExpression.class);

        assertThat(rule.getDescription()).isNotNull();
    }

    @Test
    public void testEditor() {
        final Optional<DecisionTable> expression = definition.getModelClass();
        final Optional<BaseExpressionGrid> oEditor = definition.getEditor(parent,
                                                                          Optional.empty(),
                                                                          hasExpression,
                                                                          expression,
                                                                          hasName,
                                                                          0);

        assertThat(oEditor).isPresent();

        final GridWidget editor = oEditor.get();
        assertThat(editor).isInstanceOf(DecisionTableGrid.class);
    }
}
