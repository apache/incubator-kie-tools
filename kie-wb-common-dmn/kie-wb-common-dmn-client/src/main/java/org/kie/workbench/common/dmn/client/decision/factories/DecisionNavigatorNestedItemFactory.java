/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.decision.factories;

import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.definition.v1_1.List;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.CONTEXT;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.DECISION_TABLE;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.FUNCTION_DEFINITION;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.INVOCATION;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.LIST;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.LITERAL_EXPRESSION;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.RELATION;

@Dependent
public class DecisionNavigatorNestedItemFactory {

    private static final Map<Class<? extends Expression>, DecisionNavigatorItem.Type> ITEM_TYPE_BY_EXPRESSION =
            new Maps.Builder<Class<? extends Expression>, DecisionNavigatorItem.Type>()
                    .put(Context.class, CONTEXT)
                    .put(DecisionTable.class, DECISION_TABLE)
                    .put(FunctionDefinition.class, FUNCTION_DEFINITION)
                    .put(Invocation.class, INVOCATION)
                    .put(List.class, LIST)
                    .put(LiteralExpression.class, LITERAL_EXPRESSION)
                    .put(Relation.class, RELATION)
                    .build();

    private final SessionManager sessionManager;

    private final Event<EditExpressionEvent> editExpressionEvent;

    private final DecisionNavigatorPresenter decisionNavigatorPresenter;

    private final Event<CanvasSelectionEvent> canvasSelectionEvent;

    @Inject
    public DecisionNavigatorNestedItemFactory(final SessionManager sessionManager,
                                              final Event<EditExpressionEvent> editExpressionEvent,
                                              final DecisionNavigatorPresenter decisionNavigatorPresenter,
                                              final Event<CanvasSelectionEvent> canvasSelectionEvent) {
        this.sessionManager = sessionManager;
        this.editExpressionEvent = editExpressionEvent;
        this.decisionNavigatorPresenter = decisionNavigatorPresenter;
        this.canvasSelectionEvent = canvasSelectionEvent;
    }

    public DecisionNavigatorItem makeItem(final Node<View, Edge> node) {

        final String uuid = getUUID(node);
        final DecisionNavigatorItem.Type type = getType(node);
        final String label = getLabel(node);
        final String parentUUID = node.getUUID();
        final Command onClick = makeOnClickCommand(node, parentUUID);

        return new DecisionNavigatorItem(uuid, label, type, onClick, parentUUID);
    }

    public boolean hasNestedElement(final Node<View, Edge> node) {
        return getOptionalHasExpression(node).isPresent() && getOptionalExpression(node).isPresent();
    }

    Command makeOnClickCommand(final Node<View, Edge> node,
                               final String uuid) {

        return () -> {

            final CanvasHandler canvas = decisionNavigatorPresenter.getHandler();

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
        final Optional<HasName> hasName = Optional.of((HasName) getDefinition(node));
        final HasExpression hasExpression = getHasExpression(node);

        return new EditExpressionEvent(currentSession, node.getUUID(), hasExpression, hasName);
    }

    String getUUID(final Node<View, Edge> node) {
        final Expression expression = getExpression(node);
        return expression.getId().getValue();
    }

    String getLabel(final Node<View, Edge> node) {
        return getExpression(node).getClass().getSimpleName();
    }

    DecisionNavigatorItem.Type getType(final Node<View, Edge> node) {
        return ITEM_TYPE_BY_EXPRESSION.get(getExpression(node).getClass());
    }

    Optional<HasExpression> getOptionalHasExpression(final Node<View, Edge> node) {

        final Object definition = getDefinition(node);
        final HasExpression expression;

        if (definition instanceof BusinessKnowledgeModel) {
            expression = ((BusinessKnowledgeModel) definition).asHasExpression();
        } else if (definition instanceof Decision) {
            expression = (Decision) definition;
        } else {
            expression = null;
        }

        return Optional.ofNullable(expression);
    }

    Optional<Expression> getOptionalExpression(final Node<View, Edge> node) {
        return Optional.ofNullable(getExpression(node));
    }

    Expression getExpression(final Node<View, Edge> node) {
        return getHasExpression(node).getExpression();
    }

    HasExpression getHasExpression(final Node<View, Edge> node) {
        return getOptionalHasExpression(node).orElseThrow(RuntimeException::new);
    }

    Object getDefinition(final Node<View, Edge> node) {
        return node.getContent().getDefinition();
    }
}
