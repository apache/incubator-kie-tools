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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
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
public class ContextEditorDefinition implements ExpressionEditorDefinition<Context> {

    private DMNGridPanel gridPanel;
    private DMNGridLayer gridLayer;
    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private Event<ExpressionEditorSelectedEvent> editorSelectedEvent;
    private ManagedInstance<ContextGridControls> controlsProvider;

    public ContextEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public ContextEditorDefinition(final @DMNEditor DMNGridPanel gridPanel,
                                   final @DMNEditor DMNGridLayer gridLayer,
                                   final SessionManager sessionManager,
                                   final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                   final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                   final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                                   final ManagedInstance<ContextGridControls> controlsProvider) {
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.editorSelectedEvent = editorSelectedEvent;
        this.controlsProvider = controlsProvider;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.CONTEXT;
    }

    @Override
    public String getName() {
        return Context.class.getSimpleName();
    }

    @Override
    public Optional<Context> getModelClass() {
        //Add one ContextEntry for the User to start with
        final Context context = new Context();
        final ContextEntry contextEntry = new ContextEntry();
        contextEntry.setVariable(new InformationItem());
        context.getContextEntry().add(contextEntry);

        //Add (default) "result" entry
        final ContextEntry resultEntry = new ContextEntry();
        resultEntry.setExpression(new LiteralExpression());
        context.getContextEntry().add(resultEntry);
        return Optional.of(context);
    }

    @Override
    public Optional<GridWidget> getEditor(final GridCellTuple parent,
                                          final HasExpression hasExpression,
                                          final Optional<Context> expression,
                                          final Optional<HasName> hasName,
                                          final boolean nested) {
        return Optional.of(new ContextGrid(parent,
                                           hasExpression,
                                           expression,
                                           hasName,
                                           gridPanel,
                                           gridLayer,
                                           sessionManager,
                                           sessionCommandManager,
                                           editorSelectedEvent,
                                           expressionEditorDefinitionsSupplier,
                                           controlsProvider.get(),
                                           nested));
    }
}
