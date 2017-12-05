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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

@ApplicationScoped
public class FunctionEditorDefinition implements ExpressionEditorDefinition<FunctionDefinition> {

    private DMNGridPanel gridPanel;
    private DMNGridLayer gridLayer;
    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;
    private Event<ExpressionEditorSelectedEvent> editorSelectedEvent;
    private ManagedInstance<FunctionGridControls> controlsProvider;

    public FunctionEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public FunctionEditorDefinition(final @DMNEditor DMNGridPanel gridPanel,
                                    final @DMNEditor DMNGridLayer gridLayer,
                                    final SessionManager sessionManager,
                                    final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                    final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                    final @FunctionGridSupplementaryEditor Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier,
                                    final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                                    final ManagedInstance<FunctionGridControls> controlsProvider) {
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.supplementaryEditorDefinitionsSupplier = supplementaryEditorDefinitionsSupplier;
        this.editorSelectedEvent = editorSelectedEvent;
        this.controlsProvider = controlsProvider;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.FUNCTION;
    }

    @Override
    public String getName() {
        return FunctionDefinition.class.getSimpleName();
    }

    @Override
    public Optional<FunctionDefinition> getModelClass() {
        final FunctionDefinition function = new FunctionDefinition();
        function.getOtherAttributes().put(FunctionDefinition.KIND_QNAME,
                                          FunctionDefinition.Kind.FEEL.code());
        function.setExpression(new LiteralExpression());
        return Optional.of(function);
    }

    @Override
    public Optional<GridWidget> getEditor(final GridCellTuple parent,
                                          final HasExpression hasExpression,
                                          final Optional<FunctionDefinition> expression,
                                          final Optional<HasName> hasName,
                                          final boolean nested) {
        return Optional.of(new FunctionGrid(parent,
                                            hasExpression,
                                            expression,
                                            hasName,
                                            gridPanel,
                                            gridLayer,
                                            sessionManager,
                                            sessionCommandManager,
                                            expressionEditorDefinitionsSupplier,
                                            supplementaryEditorDefinitionsSupplier,
                                            editorSelectedEvent,
                                            controlsProvider.get(),
                                            nested));
    }
}
