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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddOutputClauseCommand;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;

public class DecisionTableGrid extends BaseExpressionGrid<DecisionTable, DecisionTableUIModelMapper> implements DecisionTableGridControls.Presenter {

    public static final String DESCRIPTION_GROUP = "DecisionTable$Description";

    private TextBoxSingletonDOMElementFactory textBoxFactory;
    private TextAreaSingletonDOMElementFactory textAreaFactory;
    private TextBoxSingletonDOMElementFactory headerTextBoxFactory;
    private TextAreaSingletonDOMElementFactory headerTextAreaFactory;

    private DecisionTableGridControls controls;

    public DecisionTableGrid(final GridCellTuple parent,
                             final HasExpression hasExpression,
                             final Optional<DecisionTable> expression,
                             final Optional<HasName> hasName,
                             final DMNGridPanel gridPanel,
                             final DMNGridLayer gridLayer,
                             final SessionManager sessionManager,
                             final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                             final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                             final DecisionTableGridControls controls) {
        super(parent,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              new DecisionTableGridData(new DMNGridData(gridLayer),
                                        sessionManager,
                                        sessionCommandManager,
                                        expression,
                                        gridLayer::batch),
              new DecisionTableGridRenderer(),
              sessionManager,
              sessionCommandManager,
              editorSelectedEvent);
        this.controls = controls;

        this.textBoxFactory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                    gridLayer,
                                                                    this,
                                                                    sessionManager,
                                                                    sessionCommandManager,
                                                                    newCellHasNoValueCommand(),
                                                                    newCellHasValueCommand());
        this.textAreaFactory = new TextAreaSingletonDOMElementFactory(gridPanel,
                                                                      gridLayer,
                                                                      this,
                                                                      sessionManager,
                                                                      sessionCommandManager,
                                                                      newCellHasNoValueCommand(),
                                                                      newCellHasValueCommand());
        this.headerTextBoxFactory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                          gridLayer,
                                                                          this,
                                                                          sessionManager,
                                                                          sessionCommandManager,
                                                                          newHeaderHasNoValueCommand(),
                                                                          newHeaderHasValueCommand());
        this.headerTextAreaFactory = new TextAreaSingletonDOMElementFactory(gridPanel,
                                                                            gridLayer,
                                                                            this,
                                                                            sessionManager,
                                                                            sessionCommandManager,
                                                                            newHeaderHasNoValueCommand(),
                                                                            newHeaderHasValueCommand());

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();

        this.controls.init(this);
        this.controls.initHitPolicies(Arrays.asList(HitPolicy.values()));
        this.controls.initBuiltinAggregators(Arrays.asList(BuiltinAggregator.values()));
        this.controls.initDecisionTableOrientations(Arrays.asList(DecisionTableOrientation.values()));
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // makeUiModelMapper needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public DecisionTableUIModelMapper makeUiModelMapper() {
        return new DecisionTableUIModelMapper(this::getModel,
                                              () -> expression);
    }

    @Override
    public void initialiseUiColumns() {
        expression.ifPresent(e -> {
            model.appendColumn(new DecisionTableRowNumberColumn(e::getHitPolicy,
                                                                e::getAggregation));
            e.getInput().forEach(ic -> model.appendColumn(makeInputClauseColumn(ic)));
            e.getOutput().forEach(oc -> model.appendColumn(makeOutputClauseColumn(oc)));
            model.appendColumn(new DescriptionColumn(new BaseHeaderMetaData("Description",
                                                                            DESCRIPTION_GROUP),
                                                     textBoxFactory,
                                                     this));
        });

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer);
    }

    private InputClauseColumn makeInputClauseColumn(final InputClause ic) {
        final LiteralExpression le = ic.getInputExpression();
        final InputClauseColumn column = new InputClauseColumn(new InputClauseColumnHeaderMetaData(le::getText,
                                                                                                   le::setText,
                                                                                                   headerTextAreaFactory),
                                                               textAreaFactory,
                                                               this);
        return column;
    }

    private OutputClauseColumn makeOutputClauseColumn(final OutputClause oc) {
        final OutputClauseColumn column = new OutputClauseColumn(outputClauseHeaderMetaData(oc),
                                                                 textAreaFactory,
                                                                 this);
        return column;
    }

    private Supplier<List<GridColumn.HeaderMetaData>> outputClauseHeaderMetaData(final OutputClause oc) {
        return () -> {
            final List<GridColumn.HeaderMetaData> metaData = new ArrayList<>();
            expression.ifPresent(dtable -> {
                hasName.ifPresent(name -> {
                    final Name n = name.getName();
                    metaData.add(new OutputClauseColumnExpressionNameHeaderMetaData(n::getValue,
                                                                                    n::setValue,
                                                                                    headerTextBoxFactory));
                    if (dtable.getOutput().size() > 1) {
                        metaData.add(new OutputClauseColumnNameHeaderMetaData(oc::getName,
                                                                              oc::setName,
                                                                              headerTextBoxFactory));
                    }
                });
            });
            return metaData;
        };
    }

    @Override
    public void initialiseUiModel() {
        expression.ifPresent(e -> {
            e.getRule().forEach(r -> {
                int columnIndex = 0;
                model.appendRow(new DMNGridRow());
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           columnIndex++);
                for (int ici = 0; ici < e.getInput().size(); ici++) {
                    uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                               columnIndex++);
                }
                for (int oci = 0; oci < e.getOutput().size(); oci++) {
                    uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                               columnIndex++);
                }
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           columnIndex);
            });
        });
    }

    @Override
    public Optional<IsElement> getEditorControls() {
        expression.ifPresent(e -> {
            if (e.getHitPolicy() == null) {
                controls.enableHitPolicies(false);
            } else {
                controls.enableHitPolicies(true);
                controls.initSelectedHitPolicy(e.getHitPolicy());
            }
            if (e.getAggregation() == null) {
                controls.enableBuiltinAggregators(false);
            } else {
                controls.enableBuiltinAggregators(true);
                controls.initSelectedBuiltinAggregator(e.getAggregation());
            }
            if (e.getPreferredOrientation() == null) {
                controls.enableDecisionTableOrientation(false);
            } else {
                controls.enableDecisionTableOrientation(true);
                controls.initSelectedDecisionTableOrientation(e.getPreferredOrientation());
            }
            assertAggregationState(e);
        });
        return Optional.of(controls);
    }

    @Override
    public void addInputClause() {
        expression.ifPresent(dtable -> {
            final InputClause clause = new InputClause();
            final LiteralExpression le = new LiteralExpression();
            le.setText("input");
            clause.setInputExpression(le);

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddInputClauseCommand(dtable,
                                                                    clause,
                                                                    model,
                                                                    makeInputClauseColumn(clause),
                                                                    uiModelMapper,
                                                                    () -> {
                                                                        parent.assertWidth(DecisionTableGrid.this.getWidth());
                                                                        gridPanel.refreshScrollPosition();
                                                                        gridPanel.updatePanelSize();
                                                                        gridLayer.batch();
                                                                    }));
        });
    }

    @Override
    public void addOutputClause() {
        expression.ifPresent(dtable -> {
            final OutputClause clause = new OutputClause();
            clause.setName("output");

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddOutputClauseCommand(dtable,
                                                                     clause,
                                                                     model,
                                                                     makeOutputClauseColumn(clause),
                                                                     uiModelMapper,
                                                                     () -> {
                                                                         parent.assertWidth(DecisionTableGrid.this.getWidth());
                                                                         gridPanel.refreshScrollPosition();
                                                                         gridPanel.updatePanelSize();
                                                                         gridLayer.batch();
                                                                     }));
        });
    }

    @Override
    public void addDecisionRule() {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddDecisionRuleCommand(dtable,
                                                                     new DecisionRule(),
                                                                     model,
                                                                     new DMNGridRow(),
                                                                     uiModelMapper,
                                                                     () -> {
                                                                         gridPanel.refreshScrollPosition();
                                                                         gridPanel.updatePanelSize();
                                                                         gridLayer.batch();
                                                                     }));
        });
    }

    @Override
    public void setHitPolicy(final HitPolicy hitPolicy) {
        //TODO {manstis} This needs to be command-based
        expression.ifPresent(e -> {
            e.setHitPolicy(hitPolicy);
            assertAggregationState(e);
            gridLayer.batch();
        });
    }

    void assertAggregationState(final DecisionTable expression) {
        final HitPolicy hitPolicy = expression.getHitPolicy();
        if (hitPolicy == null) {
            controls.enableBuiltinAggregators(false);
            setBuiltinAggregator(null);
            return;
        }
        if (!HitPolicy.COLLECT.equals(hitPolicy)) {
            controls.enableBuiltinAggregators(false);
            setBuiltinAggregator(null);
            return;
        }

        if (expression.getAggregation() == null) {
            expression.setAggregation(BuiltinAggregator.COUNT);
        }
        controls.initSelectedBuiltinAggregator(expression.getAggregation());
        controls.enableBuiltinAggregators(true);
    }

    @Override
    public void setBuiltinAggregator(final BuiltinAggregator aggregator) {
        //TODO {manstis} This needs to be command-based
        expression.ifPresent(e -> {
            e.setAggregation(aggregator);
            gridLayer.batch();
        });
    }

    @Override
    public void setDecisionTableOrientation(final DecisionTableOrientation orientation) {
        //TODO {manstis} This needs to be command-based
        expression.ifPresent(e -> e.setPreferredOrientation(orientation));
    }
}
