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
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverView;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

@ApplicationScoped
public class DecisionTableEditorDefinition extends BaseEditorDefinition<DecisionTable, DecisionTableGridData> {

    private HitPolicyPopoverView.Presenter hitPolicyEditor;
    private NameAndDataTypePopoverView.Presenter headerEditor;
    private DecisionTableEditorDefinitionEnricher enricher;

    public DecisionTableEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public DecisionTableEditorDefinition(final DefinitionUtils definitionUtils,
                                         final SessionManager sessionManager,
                                         final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                         final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                         final Event<ExpressionEditorChanged> editorSelectedEvent,
                                         final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                         final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                                         final ListSelectorView.Presenter listSelector,
                                         final TranslationService translationService,
                                         final HitPolicyPopoverView.Presenter hitPolicyEditor,
                                         final NameAndDataTypePopoverView.Presenter headerEditor,
                                         final DecisionTableEditorDefinitionEnricher enricher) {
        super(definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              editorSelectedEvent,
              refreshFormPropertiesEvent,
              domainObjectSelectionEvent,
              listSelector,
              translationService);
        this.hitPolicyEditor = hitPolicyEditor;
        this.headerEditor = headerEditor;
        this.enricher = enricher;
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
        return Optional.of(new DecisionTable());
    }

    @Override
    public void enrich(final Optional<String> nodeUUID,
                       final HasExpression hasExpression,
                       final Optional<DecisionTable> expression) {
        enricher.enrich(nodeUUID, hasExpression, expression);
    }

    @Override
    @SuppressWarnings("unused")
    public Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> getEditor(final GridCellTuple parent,
                                                                                                                         final Optional<String> nodeUUID,
                                                                                                                         final HasExpression hasExpression,
                                                                                                                         final Optional<HasName> hasName,
                                                                                                                         final boolean isOnlyVisualChangeAllowed,
                                                                                                                         final int nesting) {
        return Optional.of(new DecisionTableGrid(parent,
                                                 nodeUUID,
                                                 hasExpression,
                                                 hasName,
                                                 getGridPanel(),
                                                 getGridLayer(),
                                                 makeGridData(() -> Optional.ofNullable((DecisionTable) hasExpression.getExpression())),
                                                 definitionUtils,
                                                 sessionManager,
                                                 sessionCommandManager,
                                                 canvasCommandFactory,
                                                 editorSelectedEvent,
                                                 refreshFormPropertiesEvent,
                                                 domainObjectSelectionEvent,
                                                 getCellEditorControls(),
                                                 listSelector,
                                                 translationService,
                                                 isOnlyVisualChangeAllowed,
                                                 nesting,
                                                 hitPolicyEditor,
                                                 headerEditor));
    }

    @Override
    protected DecisionTableGridData makeGridData(final Supplier<Optional<DecisionTable>> expression) {
        return new DecisionTableGridData(new DMNGridData(),
                                         sessionManager,
                                         sessionCommandManager,
                                         expression,
                                         getGridLayer()::batch);
    }
}
