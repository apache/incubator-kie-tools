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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
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

public abstract class BaseSupplementaryFunctionEditorDefinition extends BaseEditorDefinition<Context, FunctionSupplementaryGridData> {

    private static final BuiltInType DEFAULT_VARIABLE_TYPE = BuiltInType.STRING;

    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    protected BaseSupplementaryFunctionEditorDefinition() {
        //CDI proxy
    }

    public BaseSupplementaryFunctionEditorDefinition(final DefinitionUtils definitionUtils,
                                                     final SessionManager sessionManager,
                                                     final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                                     final DefaultCanvasCommandFactory canvasCommandFactory,
                                                     final Event<ExpressionEditorChanged> editorSelectedEvent,
                                                     final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                                     final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                                                     final ListSelectorView.Presenter listSelector,
                                                     final TranslationService translationService,
                                                     final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
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
    }

    @Override
    public Optional<Context> getModelClass() {
        return Optional.of(new Context());
    }

    @Override
    public void enrich(final Optional<String> nodeUUID,
                       final HasExpression hasExpression,
                       final Optional<Context> expression) {
        expression.ifPresent(context -> getVariableNames().forEach(name -> {
            final ContextEntry contextEntry = new ContextEntry();
            contextEntry.setVariable(createVariable(name));
            contextEntry.setExpression(new LiteralExpression());
            context.getContextEntry().add(contextEntry);
            contextEntry.setParent(context);
        }));
    }

    protected InformationItem createVariable(final String name) {
        final InformationItem variable = new InformationItem();
        variable.setName(new Name(name));
        variable.setTypeRef(new QName(DEFAULT_VARIABLE_TYPE));
        return variable;
    }

    @Override
    public Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> getEditor(final GridCellTuple parent,
                                                                                                                         final Optional<String> nodeUUID,
                                                                                                                         final HasExpression hasExpression,
                                                                                                                         final Optional<HasName> hasName,
                                                                                                                         final boolean isOnlyVisualChangeAllowed,
                                                                                                                         final int nesting) {
        return Optional.of(new FunctionSupplementaryGrid(parent,
                                                         nodeUUID,
                                                         hasExpression,
                                                         hasName,
                                                         getGridPanel(),
                                                         getGridLayer(),
                                                         makeGridData(() -> Optional.ofNullable(((Context) hasExpression.getExpression()))),
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
                                                         readOnlyProvider));
    }

    @Override
    protected FunctionSupplementaryGridData makeGridData(final Supplier<Optional<Context>> expression) {
        return new FunctionSupplementaryGridData(new DMNGridData(),
                                                 sessionManager,
                                                 sessionCommandManager,
                                                 expression,
                                                 getGridLayer()::batch);
    }

    protected abstract List<String> getVariableNames();
}
