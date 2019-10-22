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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;

@ApplicationScoped
public class DecisionNavigatorObserver {

    private DecisionNavigatorPresenter presenter;

    public void init(final DecisionNavigatorPresenter presenter) {
        this.presenter = presenter;
    }

    @SuppressWarnings("unused")
    void onCanvasClear(final @Observes CanvasClearEvent event) {
        getOptionalPresenter().ifPresent(p -> {
            p.removeAllElements();
            p.refreshTreeView();
        });
    }

    void onCanvasElementAdded(final @Observes CanvasElementAddedEvent event) {
        getOptionalPresenter().ifPresent(p -> p.addOrUpdateElement(event.getElement()));
    }

    void onCanvasElementUpdated(final @Observes CanvasElementUpdatedEvent event) {
        getOptionalPresenter().ifPresent(p -> p.addOrUpdateElement(event.getElement()));
    }

    void onCanvasElementRemoved(final @Observes CanvasElementRemovedEvent event) {
        getOptionalPresenter().ifPresent(p -> p.removeElement(event.getElement()));
    }

    void onNestedElementSelected(final @Observes EditExpressionEvent event) {
        selectItem(event);
        setActiveParent(event);
    }

    void onNestedElementAdded(final @Observes ExpressionEditorChanged event) {
        presenter.getGraph().ifPresent(this::updateNode);
    }

    private void updateNode(final Graph graph) {

        getActiveParent().ifPresent(activeParent -> {

            final String activeParentUUID = activeParent.getUUID();
            final Node node = graph.getNode(activeParentUUID);

            presenter.updateElement(node);

            activeParent.getChildren().forEach(e -> getTreePresenter().selectItem(e.getUUID()));
        });
    }

    void onNestedElementLostFocus(final @Observes CanvasFocusedShapeEvent event) {
        getTreePresenter().deselectItem();
    }

    void selectItem(final EditExpressionEvent event) {

        final HasExpression hasExpression = event.getHasExpression();
        final Optional<Expression> optionalExpression = Optional.ofNullable(hasExpression.getExpression());

        optionalExpression.ifPresent(expression -> {
            final String value = expression.getId().getValue();
            getTreePresenter().selectItem(value);
        });
    }

    Optional<DecisionNavigatorItem> getActiveParent() {
        return Optional.ofNullable(getTreePresenter().getActiveParent());
    }

    void setActiveParent(final EditExpressionEvent event) {
        final String uuid = event.getNodeUUID();
        getTreePresenter().setActiveParentUUID(uuid);
    }

    private DecisionNavigatorTreePresenter getTreePresenter() {
        return presenter.getTreePresenter();
    }

    private Optional<DecisionNavigatorPresenter> getOptionalPresenter() {
        return Optional.ofNullable(presenter);
    }
}
