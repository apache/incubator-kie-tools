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

package org.kie.workbench.common.dmn.client.editors.expressions.commands;

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.util.HasExpressionUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;

public abstract class FillExpressionCommand<E extends ExpressionProps> {

    private final HasExpression hasExpression;
    private final E expressionProps;
    private final String nodeUUID;
    private final Event<ExpressionEditorChanged> editorSelectedEvent;
    private final ExpressionEditorView view;

    public FillExpressionCommand(final HasExpression hasExpression,
                                 final E expressionProps,
                                 final Event<ExpressionEditorChanged> editorSelectedEvent,
                                 final String nodeUUID,
                                 final ExpressionEditorView view) {
        this.hasExpression = hasExpression;
        this.expressionProps = expressionProps;
        this.nodeUUID = nodeUUID;
        this.editorSelectedEvent = editorSelectedEvent;
        this.view = view;
    }

    public HasExpression getHasExpression() {
        return hasExpression;
    }

    public E getExpressionProps() {
        return expressionProps;
    }

    public ExpressionEditorView getView() {
        return view;
    }

    public Event<ExpressionEditorChanged> getEditorSelectedEvent() {
        return editorSelectedEvent;
    }

    public String getNodeUUID() {
        return nodeUUID;
    }

    protected abstract void fill();

    public void execute() {
        fireEditorSelectedEvent();
        setExpressionName(getExpressionProps().name);
        setTypeRef(getExpressionProps().dataType);
        createExpression();
        fill();
    }

    void createExpression() {
        if (getHasExpression().getExpression() == null) {
            getHasExpression().setExpression(getNewExpression());
        }
    }

    protected abstract Expression getNewExpression();

    void fireEditorSelectedEvent() {
        getEditorSelectedEvent().fire(new ExpressionEditorChanged(getNodeUUID()));
    }

    void setTypeRef(final String dataType) {
        final QName typeRef = getTypeRef(dataType);
        if (hasExpression instanceof HasVariable) {
            @SuppressWarnings("unchecked")
            final HasVariable<InformationItemPrimary> hasVariable = (HasVariable<InformationItemPrimary>) hasExpression;
            hasVariable.getVariable().setTypeRef(typeRef);
        }
    }

    QName getTypeRef(final String dataType) {
        return BuiltInTypeUtils
                .findBuiltInTypeByName(dataType)
                .orElse(BuiltInType.UNDEFINED)
                .asQName();
    }

    void setExpressionName(final String expressionName) {
        HasExpressionUtils.setExpressionName(getHasExpression(), expressionName);
    }
}
