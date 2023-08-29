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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

@ApplicationScoped
public class RelationEditorDefinition extends BaseEditorDefinition<Relation, RelationGridData> {

    private ManagedInstance<ValueAndDataTypePopoverView.Presenter> headerEditors;

    public RelationEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public RelationEditorDefinition(final DefinitionUtils definitionUtils,
                                    final SessionManager sessionManager,
                                    final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                    final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                    final Event<ExpressionEditorChanged> editorSelectedEvent,
                                    final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                    final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                                    final ListSelectorView.Presenter listSelector,
                                    final TranslationService translationService,
                                    final ManagedInstance<ValueAndDataTypePopoverView.Presenter> headerEditors,
                                    final ReadOnlyProvider readOnlyProvider) {
        super(definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              editorSelectedEvent,
              refreshFormPropertiesEvent,
              domainObjectSelectionEvent,
              listSelector,
              translationService,
              readOnlyProvider);
        this.headerEditors = headerEditors;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.RELATION;
    }

    @Override
    public String getName() {
        return translationService.format(DMNEditorConstants.ExpressionEditor_RelationType);
    }

    @Override
    public Optional<Relation> getModelClass() {
        return Optional.of(new Relation());
    }

    @Override
    public void enrich(final Optional<String> nodeUUID,
                       final HasExpression hasExpression,
                       final Optional<Relation> expression) {
        expression.ifPresent(relation -> {
            final InformationItem column = new InformationItem();
            column.getName().setValue(RelationDefaultValueUtilities.getNewColumnName(relation));
            final org.kie.workbench.common.dmn.api.definition.model.List row = new org.kie.workbench.common.dmn.api.definition.model.List();
            final LiteralExpression literalExpression = new LiteralExpression();
            row.getExpression().add(HasExpression.wrap(relation, literalExpression));
            relation.getColumn().add(column);
            relation.getRow().add(row);

            //Setup parent relationships
            column.setParent(relation);
            row.setParent(relation);
            literalExpression.setParent(row);
        });
    }

    @Override
    @SuppressWarnings("unused")
    public Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> getEditor(final GridCellTuple parent,
                                                                                                                         final Optional<String> nodeUUID,
                                                                                                                         final HasExpression hasExpression,
                                                                                                                         final Optional<HasName> hasName,
                                                                                                                         final boolean isOnlyVisualChangeAllowed,
                                                                                                                         final int nesting) {
        return Optional.of(new RelationGrid(parent,
                                            nodeUUID,
                                            hasExpression,
                                            hasName,
                                            getGridPanel(),
                                            getGridLayer(),
                                            makeGridData(() -> Optional.ofNullable(((Relation) hasExpression.getExpression()))),
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
                                            headerEditors,
                                            readOnlyProvider));
    }

    @Override
    protected RelationGridData makeGridData(final Supplier<Optional<Relation>> expression) {
        return new RelationGridData(new DMNGridData(),
                                    sessionManager,
                                    sessionCommandManager,
                                    expression,
                                    getGridLayer()::batch);
    }
}
