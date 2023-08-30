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

package org.kie.workbench.common.dmn.client.editors.expressions.commands;

import java.util.Optional;

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.util.HasNameUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;

public abstract class FillExpressionCommand<E extends ExpressionProps> {

    private final HasExpression hasExpression;
    private final E expressionProps;
    private final String nodeUUID;
    private final Event<ExpressionEditorChanged> editorSelectedEvent;
    private final ItemDefinitionUtils itemDefinitionUtils;
    private final Optional<HasName> hasName;

    protected FillExpressionCommand(final HasExpression hasExpression,
                                    final E expressionProps,
                                    final Event<ExpressionEditorChanged> editorSelectedEvent,
                                    final String nodeUUID,
                                    final ItemDefinitionUtils itemDefinitionUtils,
                                    final Optional<HasName> hasName) {
        this.hasExpression = hasExpression;
        this.expressionProps = expressionProps;
        this.nodeUUID = nodeUUID;
        this.editorSelectedEvent = editorSelectedEvent;
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.hasName = hasName;
    }

    public HasExpression getHasExpression() {
        return hasExpression;
    }

    public E getExpressionProps() {
        return expressionProps;
    }

    protected ItemDefinitionUtils getItemDefinitionUtils() {
        return itemDefinitionUtils;
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

    public abstract boolean isCurrentExpressionOfTheSameType();

    void createExpression() {
        if (getHasExpression().getExpression() == null || !isCurrentExpressionOfTheSameType()) {
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
        } else if (hasExpression.getExpression() != null) {
            final DMNModelInstrumentedBase parent = hasExpression.getExpression().asDMNModelInstrumentedBase().getParent();
            if (parent instanceof HasVariable) {
                ((HasVariable<InformationItemPrimary>) parent).getVariable().setTypeRef(typeRef);
            }
        }
    }

    QName getTypeRef(final String dataType) {
        final Optional<BuiltInType> builtInType = BuiltInTypeUtils.findBuiltInTypeByName(dataType);
        final Optional<ItemDefinition> itemDefinitionType = itemDefinitionUtils.findByName(dataType);
        if (builtInType.isPresent()) {
            return builtInType.get().asQName();
        } else if (itemDefinitionType.isPresent()) {
            final Name name = itemDefinitionType.get().getName();
            return new QName(QName.NULL_NS_URI,
                             name.getValue(),
                             QName.DEFAULT_NS_PREFIX);
        }
        return BuiltInType.UNDEFINED.asQName();
    }

    void setExpressionName(final String expressionName) {
        final HasName fallbackHasName = hasExpression instanceof HasName ? (HasName) hasExpression : HasName.NOP;
        HasNameUtils.setName(hasName.orElse(fallbackHasName), expressionName);
    }
}
