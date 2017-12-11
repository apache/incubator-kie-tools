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
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
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
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;

@ApplicationScoped
public class DecisionTableEditorDefinition implements ExpressionEditorDefinition<DecisionTable> {

    private DMNGridPanel gridPanel;
    private DMNGridLayer gridLayer;
    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private Event<ExpressionEditorSelectedEvent> editorSelectedEvent;
    private ManagedInstance<DecisionTableGridControls> controlsProvider;

    public DecisionTableEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public DecisionTableEditorDefinition(final @DMNEditor DMNGridPanel gridPanel,
                                         final @DMNEditor DMNGridLayer gridLayer,
                                         final SessionManager sessionManager,
                                         final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                         final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                                         final ManagedInstance<DecisionTableGridControls> controlsProvider) {
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.editorSelectedEvent = editorSelectedEvent;
        this.controlsProvider = controlsProvider;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.DECISION_TABLE;
    }

    @Override
    public String getName() {
        return DecisionTable.class.getSimpleName();
    }

    @Override
    public Optional<DecisionTable> getModelClass() {
        final DecisionTable dtable = new DecisionTable();
        dtable.setHitPolicy(HitPolicy.ANY);
        dtable.setPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW);

        final InputClause ic = new InputClause();
        final LiteralExpression le = new LiteralExpression();
        le.setText("input");
        ic.setInputExpression(le);
        dtable.getInput().add(ic);

        final OutputClause oc = new OutputClause();
        oc.setName("output");
        dtable.getOutput().add(oc);

        final DecisionRule dr = new DecisionRule();
        final UnaryTests drut = new UnaryTests();
        drut.setText("unary test");
        dr.getInputEntry().add(drut);

        final LiteralExpression drle = new LiteralExpression();
        drle.setText("literal expression");
        dr.getOutputEntry().add(drle);

        final Description d = new Description();
        d.setValue("A rule");
        dr.setDescription(d);

        dtable.getRule().add(dr);

        return Optional.of(dtable);
    }

    @Override
    @SuppressWarnings("unused")
    public Optional<BaseExpressionGrid> getEditor(final GridCellTuple parent,
                                                  final HasExpression hasExpression,
                                                  final Optional<DecisionTable> expression,
                                                  final Optional<HasName> hasName,
                                                  final boolean nested) {
        return Optional.of(new DecisionTableGrid(parent,
                                                 hasExpression,
                                                 expression,
                                                 hasName,
                                                 gridPanel,
                                                 gridLayer,
                                                 sessionManager,
                                                 sessionCommandManager,
                                                 editorSelectedEvent,
                                                 controlsProvider.get()));
    }
}
