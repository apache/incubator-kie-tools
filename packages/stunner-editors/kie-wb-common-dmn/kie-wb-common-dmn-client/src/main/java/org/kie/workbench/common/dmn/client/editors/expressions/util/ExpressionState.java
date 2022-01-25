/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import java.util.Objects;

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;

public class ExpressionState {

    private final HasExpression hasExpression;
    private final Event<ExpressionEditorChanged> editorSelectedEvent;
    private final ExpressionEditorView view;
    private final String nodeUUID;
    private Expression savedExpression;
    private String savedExpressionName;
    private QName savedTypeRef;

    public ExpressionState(final HasExpression hasExpression,
                           final Event<ExpressionEditorChanged> editorSelectedEvent,
                           final ExpressionEditorView view,
                           final String nodeUUID) {
        this.hasExpression = hasExpression;
        this.editorSelectedEvent = editorSelectedEvent;
        this.view = view;
        this.nodeUUID = nodeUUID;
    }

    public ExpressionEditorView getView() {
        return view;
    }

    public String getNodeUUID() {
        return nodeUUID;
    }

    public String getSavedExpressionName() {
        return savedExpressionName;
    }

    public void setSavedExpressionName(final String savedExpressionName) {
        this.savedExpressionName = savedExpressionName;
    }

    public Event<ExpressionEditorChanged> getEditorSelectedEvent() {
        return editorSelectedEvent;
    }

    public HasExpression getHasExpression() {
        return hasExpression;
    }

    public Expression getSavedExpression() {
        return savedExpression;
    }

    public void setSavedExpression(final Expression savedExpression) {
        this.savedExpression = savedExpression;
    }

    public QName getSavedTypeRef() {
        return savedTypeRef;
    }

    public void setSavedTypeRef(final QName savedTypeRef) {
        this.savedTypeRef = savedTypeRef;
    }

    public void apply() {
        restoreExpression();
        restoreTypeRef();
        restoreExpressionName();
        fireEditorSelectedEvent();
        getView().reloadEditor();
    }

    void fireEditorSelectedEvent() {
        getEditorSelectedEvent().fire(new ExpressionEditorChanged(getNodeUUID()));
    }

    void restoreExpressionName() {
        setExpressionName(getSavedExpressionName());
    }

    void setExpressionName(final String expressionName) {
        HasExpressionUtils.setExpressionName(getHasExpression(), expressionName);
    }

    void restoreExpression() {
        getHasExpression().setExpression(getSavedExpression());
    }

    void restoreTypeRef() {
        if (getHasExpression() instanceof HasVariable) {
            final HasVariable<InformationItemPrimary> hasVariable = (HasVariable<InformationItemPrimary>) hasExpression;
            hasVariable.getVariable().setTypeRef(getSavedTypeRef());
        }
    }

    public void saveCurrentState() {
        saveCurrentExpressionName();
        saveCurrentTypeRef();
        saveCurrentExpression();
    }

    void saveCurrentExpressionName() {
        if (getHasExpression() instanceof HasName) {
            final HasName hasName = (HasName) getHasExpression();
            setSavedExpressionName(hasName.getName().getValue());
        }
    }

    QName getCurrentTypeRef() {
        if (getHasExpression() instanceof HasVariable) {
            final HasVariable<InformationItemPrimary> hasVariable = (HasVariable<InformationItemPrimary>) hasExpression;
            return hasVariable.getVariable().getTypeRef();
        }
        return BuiltInType.UNDEFINED.asQName();
    }

    void saveCurrentTypeRef() {
        setSavedTypeRef(getCurrentTypeRef());
    }

    void saveCurrentExpression() {
        if (Objects.isNull(getHasExpression().getExpression())) {
            setSavedExpression(null);
        } else {
            setSavedExpression(getHasExpression().getExpression().copy());
        }
    }
}
