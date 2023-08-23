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
package org.kie.workbench.common.dmn.client.editors.expressions.types.list;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
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
public class ListEditorDefinition extends BaseEditorDefinition<List, ListGridData> {

    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private ValueAndDataTypePopoverView.Presenter headerEditor;

    public ListEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public ListEditorDefinition(final DefinitionUtils definitionUtils,
                                final SessionManager sessionManager,
                                final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                final Event<ExpressionEditorChanged> editorSelectedEvent,
                                final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                                final ListSelectorView.Presenter listSelector,
                                final TranslationService translationService,
                                final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                final ValueAndDataTypePopoverView.Presenter headerEditor,
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
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.headerEditor = headerEditor;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.LIST;
    }

    @Override
    public String getName() {
        return translationService.format(DMNEditorConstants.ExpressionEditor_ListType);
    }

    @Override
    public Optional<List> getModelClass() {
        return Optional.of(new List());
    }

    @Override
    public void enrich(final Optional<String> nodeUUID,
                       final HasExpression hasExpression,
                       final Optional<List> expression) {
        expression.ifPresent(list -> {
            final LiteralExpression literalExpression = new LiteralExpression();
            list.getExpression().add(HasExpression.wrap(list, literalExpression));
            literalExpression.setParent(list);
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
        return Optional.of(new ListGrid(parent,
                                        nodeUUID,
                                        hasExpression,
                                        hasName,
                                        getGridPanel(),
                                        getGridLayer(),
                                        makeGridData(() -> Optional.ofNullable(((List) hasExpression.getExpression()))),
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
                                        expressionEditorDefinitionsSupplier,
                                        headerEditor,
                                        readOnlyProvider));
    }

    @Override
    protected ListGridData makeGridData(final Supplier<Optional<List>> expression) {
        return new ListGridData(new DMNGridData(),
                                sessionManager,
                                sessionCommandManager,
                                expression,
                                getGridLayer()::batch);
    }
}
