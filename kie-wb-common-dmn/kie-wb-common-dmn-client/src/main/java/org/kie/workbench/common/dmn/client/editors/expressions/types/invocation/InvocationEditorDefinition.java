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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
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
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

@ApplicationScoped
public class InvocationEditorDefinition extends BaseEditorDefinition<Invocation, InvocationGridData> {

    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private NameAndDataTypePopoverView.Presenter headerEditor;

    public InvocationEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public InvocationEditorDefinition(final DefinitionUtils definitionUtils,
                                      final SessionManager sessionManager,
                                      final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                      final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                      final Event<ExpressionEditorChanged> editorSelectedEvent,
                                      final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                      final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                                      final ListSelectorView.Presenter listSelector,
                                      final TranslationService translationService,
                                      final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                      final NameAndDataTypePopoverView.Presenter headerEditor) {
        super(definitionUtils,
              sessionManager,
              sessionCommandManager,
              canvasCommandFactory,
              editorSelectedEvent,
              refreshFormPropertiesEvent,
              domainObjectSelectionEvent,
              listSelector,
              translationService);
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.headerEditor = headerEditor;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.INVOCATION;
    }

    @Override
    public String getName() {
        return translationService.format(DMNEditorConstants.ExpressionEditor_InvocationType);
    }

    @Override
    public Optional<Invocation> getModelClass() {
        return Optional.of(new Invocation());
    }

    @Override
    public void enrich(final Optional<String> nodeUUID,
                       final HasExpression hasExpression,
                       final Optional<Invocation> expression) {
        expression.ifPresent(invocation -> {
            final LiteralExpression literalExpression = new LiteralExpression();
            invocation.setExpression(literalExpression);

            final InformationItem parameter = new InformationItem();
            parameter.getName().setValue(InvocationDefaultValueUtilities.getNewParameterName(invocation));
            final Binding binding = new Binding();
            binding.setParameter(parameter);
            invocation.getBinding().add(binding);

            //Setup parent relationships
            literalExpression.setParent(invocation);
            binding.setParent(invocation);
            parameter.setParent(binding);
        });
    }

    @Override
    public Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> getEditor(final GridCellTuple parent,
                                                                                                                         final Optional<String> nodeUUID,
                                                                                                                         final HasExpression hasExpression,
                                                                                                                         final Optional<HasName> hasName,
                                                                                                                         final boolean isOnlyVisualChangeAllowed,
                                                                                                                         final int nesting) {
        return Optional.of(new InvocationGrid(parent,
                                              nodeUUID,
                                              hasExpression,
                                              hasName,
                                              getGridPanel(),
                                              getGridLayer(),
                                              makeGridData(() -> Optional.ofNullable((Invocation) hasExpression.getExpression())),
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
                                              headerEditor));
    }

    @Override
    protected InvocationGridData makeGridData(final Supplier<Optional<Invocation>> expression) {
        return new InvocationGridData(new DMNGridData(),
                                      sessionManager,
                                      sessionCommandManager,
                                      expression,
                                      getGridLayer()::batch);
    }
}
