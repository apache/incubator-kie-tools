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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
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
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyEditorView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class DecisionTableEditorDefinition extends BaseEditorDefinition<DecisionTable, DecisionTableGridData> {

    static final String INPUT_CLAUSE_EXPRESSION_TEXT = "input";

    static final String INPUT_CLAUSE_UNARY_TEST_TEXT = "unary test";

    static final String OUTPUT_CLAUSE_NAME = "output";

    static final String OUTPUT_CLAUSE_EXPRESSION_TEXT = "literal expression";

    static final String RULE_DESCRIPTION = "A rule";

    private HitPolicyEditorView.Presenter hitPolicyEditor;

    public DecisionTableEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public DecisionTableEditorDefinition(final @DMNEditor DMNGridPanel gridPanel,
                                         final @DMNEditor DMNGridLayer gridLayer,
                                         final DefinitionUtils definitionUtils,
                                         final SessionManager sessionManager,
                                         final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                         final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                         final CellEditorControlsView.Presenter cellEditorControls,
                                         final ListSelectorView.Presenter listSelector,
                                         final TranslationService translationService,
                                         final HitPolicyEditorView.Presenter hitPolicyEditor) {
        super(gridPanel,
              gridLayer,
              definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              cellEditorControls,
              listSelector,
              translationService);
        this.hitPolicyEditor = hitPolicyEditor;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.DECISION_TABLE;
    }

    @Override
    public String getName() {
        return translationService.format(DMNEditorConstants.ExpressionEditor_DecisionTableExpressionType);
    }

    @Override
    public Optional<DecisionTable> getModelClass() {
        final DecisionTable dtable = new DecisionTable();
        dtable.setHitPolicy(HitPolicy.ANY);
        dtable.setPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW);

        final InputClause ic = new InputClause();
        final LiteralExpression le = new LiteralExpression();
        le.setText(INPUT_CLAUSE_EXPRESSION_TEXT);
        ic.setInputExpression(le);
        dtable.getInput().add(ic);

        final OutputClause oc = new OutputClause();
        oc.setName(OUTPUT_CLAUSE_NAME);
        dtable.getOutput().add(oc);

        final DecisionRule dr = new DecisionRule();
        final UnaryTests drut = new UnaryTests();
        drut.setText(INPUT_CLAUSE_UNARY_TEST_TEXT);
        dr.getInputEntry().add(drut);

        final LiteralExpression drle = new LiteralExpression();
        drle.setText(OUTPUT_CLAUSE_EXPRESSION_TEXT);
        dr.getOutputEntry().add(drle);

        final Description d = new Description();
        d.setValue(RULE_DESCRIPTION);
        dr.setDescription(d);

        dtable.getRule().add(dr);

        return Optional.of(dtable);
    }

    @Override
    @SuppressWarnings("unused")
    public Optional<BaseExpressionGrid> getEditor(final GridCellTuple parent,
                                                  final Optional<String> nodeUUID,
                                                  final HasExpression hasExpression,
                                                  final Optional<DecisionTable> expression,
                                                  final Optional<HasName> hasName,
                                                  final int nesting) {
        return Optional.of(new DecisionTableGrid(parent,
                                                 nodeUUID,
                                                 hasExpression,
                                                 expression,
                                                 hasName,
                                                 gridPanel,
                                                 gridLayer,
                                                 makeGridData(expression),
                                                 definitionUtils,
                                                 sessionManager,
                                                 sessionCommandManager,
                                                 canvasCommandFactory,
                                                 cellEditorControls,
                                                 listSelector,
                                                 translationService,
                                                 nesting,
                                                 hitPolicyEditor));
    }

    @Override
    protected DecisionTableGridData makeGridData(final Optional<DecisionTable> expression) {
        return new DecisionTableGridData(new DMNGridData(),
                                         sessionManager,
                                         sessionCommandManager,
                                         expression,
                                         gridLayer::batch);
    }
}
