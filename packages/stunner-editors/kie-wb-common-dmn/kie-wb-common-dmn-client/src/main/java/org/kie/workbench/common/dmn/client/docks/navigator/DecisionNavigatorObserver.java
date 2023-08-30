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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;

@ApplicationScoped
public class DecisionNavigatorObserver {

    private DecisionNavigatorPresenter presenter;

    public void init(final DecisionNavigatorPresenter presenter) {
        this.presenter = presenter;
    }

    void onCanvasClear(final @Observes CanvasClearEvent event) {
        getOptionalPresenter().ifPresent(DecisionNavigatorPresenter::refresh);
    }

    void onCanvasElementAdded(final @Observes CanvasElementAddedEvent event) {
        getOptionalPresenter().ifPresent(DecisionNavigatorPresenter::refresh);
    }

    void onCanvasElementUpdated(final @Observes CanvasElementUpdatedEvent event) {
        getOptionalPresenter().ifPresent(DecisionNavigatorPresenter::refresh);
    }

    void onCanvasElementRemoved(final @Observes CanvasElementRemovedEvent event) {
        getOptionalPresenter().ifPresent(DecisionNavigatorPresenter::refresh);
    }

    void onNestedElementSelected(final @Observes EditExpressionEvent event) {
        getOptionalPresenter().ifPresent(DecisionNavigatorPresenter::refresh);
    }

    void onNestedElementAdded(final @Observes ExpressionEditorChanged event) {
        getOptionalPresenter().ifPresent(DecisionNavigatorPresenter::refresh);
    }

    void onDMNDiagramSelected(final @Observes DMNDiagramSelected event) {
        getOptionalPresenter().ifPresent(DecisionNavigatorPresenter::refresh);
    }

    void selectItem(final HasExpression hasExpression) {

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
        getTreePresenter().setActiveParentUUID(event.getNodeUUID());
    }

    private DecisionNavigatorTreePresenter getTreePresenter() {
        return presenter.getTreePresenter();
    }

    private Optional<DecisionNavigatorPresenter> getOptionalPresenter() {
        return Optional.ofNullable(presenter);
    }
}

