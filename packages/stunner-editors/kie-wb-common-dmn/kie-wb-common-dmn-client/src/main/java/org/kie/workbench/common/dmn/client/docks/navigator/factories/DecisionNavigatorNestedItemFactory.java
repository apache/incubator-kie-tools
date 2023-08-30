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

package org.kie.workbench.common.dmn.client.docks.navigator.factories;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.common.BoxedExpressionHelper;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemBuilder;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.CONTEXT;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.DECISION_TABLE;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.FUNCTION_DEFINITION;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.INVOCATION;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.LIST;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.LITERAL_EXPRESSION;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.RELATION;

@Dependent
public class DecisionNavigatorNestedItemFactory {

    private static final Map<Class<? extends Expression>, DecisionNavigatorItem.Type> ITEM_TYPE_BY_EXPRESSION =
            Stream.of(new AbstractMap.SimpleEntry<>(Context.class, CONTEXT),
                      new AbstractMap.SimpleEntry<>(DecisionTable.class, DECISION_TABLE),
                      new AbstractMap.SimpleEntry<>(FunctionDefinition.class, FUNCTION_DEFINITION),
                      new AbstractMap.SimpleEntry<>(Invocation.class, INVOCATION),
                      new AbstractMap.SimpleEntry<>(List.class, LIST),
                      new AbstractMap.SimpleEntry<>(LiteralExpression.class, LITERAL_EXPRESSION),
                      new AbstractMap.SimpleEntry<>(Relation.class, RELATION))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private final SessionManager sessionManager;

    private final Event<EditExpressionEvent> editExpressionEvent;

    private final DMNGraphUtils dmnGraphUtils;

    private final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    private final Event<CanvasSelectionEvent> canvasSelectionEvent;

    private final BoxedExpressionHelper helper;

    private final ReadOnlyProvider readOnlyProvider;

    @Inject
    public DecisionNavigatorNestedItemFactory(final SessionManager sessionManager,
                                              final Event<EditExpressionEvent> editExpressionEvent,
                                              final DMNGraphUtils dmnGraphUtils,
                                              final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                              final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                              final BoxedExpressionHelper helper,
                                              final ReadOnlyProvider readOnlyProvider) {
        this.sessionManager = sessionManager;
        this.editExpressionEvent = editExpressionEvent;
        this.dmnGraphUtils = dmnGraphUtils;
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.canvasSelectionEvent = canvasSelectionEvent;
        this.helper = helper;
        this.readOnlyProvider = readOnlyProvider;
    }

    public DecisionNavigatorItem makeItem(final Node<View, Edge> node) {

        final String uuid = getUUID(node);
        final DecisionNavigatorItem.Type type = getType(node);
        final String label = getLabel(node);
        final String parentUUID = node.getUUID();
        final Command onClick = makeOnClickCommand(node, parentUUID);

        return navigatorItemBuilder()
                .withUUID(uuid)
                .withLabel(label)
                .withType(type)
                .withOnClick(onClick)
                .withParentUUID(parentUUID)
                .build();
    }

    public boolean hasNestedElement(final Node<View, Edge> node) {
        return helper.getOptionalHasExpression(node).isPresent() && helper.getOptionalExpression(node).isPresent();
    }

    Command makeOnClickCommand(final Node<View, Edge> node,
                               final String uuid) {

        return () -> {

            final CanvasHandler canvas = dmnGraphUtils.getCanvasHandler();

            canvasSelectionEvent.fire(makeCanvasSelectionEvent(canvas, uuid));
            editExpressionEvent.fire(makeEditExpressionEvent(node));
        };
    }

    CanvasSelectionEvent makeCanvasSelectionEvent(final CanvasHandler canvas,
                                                  final String uuid) {
        return new CanvasSelectionEvent(canvas, uuid);
    }

    EditExpressionEvent makeEditExpressionEvent(final Node<View, Edge> node) {

        final ClientSession currentSession = sessionManager.getCurrentSession();
        final Object definition = DefinitionUtils.getElementDefinition(node);
        final HasExpression hasExpression = helper.getHasExpression(node);
        final Optional<HasName> hasName = Optional.of((HasName) definition);
        final boolean isOnlyVisualChangeAllowed = isOnlyVisualChangeAllowed(definition) || readOnlyProvider.isReadOnlyDiagram();

        return new EditExpressionEvent(currentSession, node.getUUID(), hasExpression, hasName, isOnlyVisualChangeAllowed);
    }

    private boolean isOnlyVisualChangeAllowed(final Object definition) {
        if (definition instanceof DRGElement) {
            return ((DRGElement) definition).isAllowOnlyVisualChange();
        }
        return false;
    }

    String getUUID(final Node<View, Edge> node) {
        final Expression expression = getExpression(node);
        return expression.getId().getValue();
    }

    String getLabel(final Node<View, Edge> node) {
        final Optional<Expression> expression = Optional.of(getExpression(node));
        return expressionEditorDefinitionsSupplier.get().getExpressionEditorDefinition(expression).get().getName();
    }

    DecisionNavigatorItem.Type getType(final Node<View, Edge> node) {
        return ITEM_TYPE_BY_EXPRESSION.get(getExpression(node).getClass());
    }

    private Expression getExpression(final Node<View, Edge> node) {
        return helper.getExpression(node);
    }

    private DecisionNavigatorItemBuilder navigatorItemBuilder() {
        return new DecisionNavigatorItemBuilder();
    }
}
