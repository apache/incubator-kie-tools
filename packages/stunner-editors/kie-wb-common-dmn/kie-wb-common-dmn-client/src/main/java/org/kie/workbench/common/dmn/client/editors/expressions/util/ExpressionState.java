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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.UpdateCanvasNodeNameCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;

public class ExpressionState {

    private final HasExpression hasExpression;
    private final Event<ExpressionEditorChanged> editorSelectedEvent;
    private final String nodeUUID;
    private final Optional<HasName> hasName;
    private final UpdateCanvasNodeNameCommand updateCanvasNodeCommand;

    private Expression savedExpression;
    private String savedExpressionName;
    private QName savedTypeRef;

    public ExpressionState(final HasExpression hasExpression,
                           final Event<ExpressionEditorChanged> editorSelectedEvent,
                           final String nodeUUID,
                           final Optional<HasName> hasName,
                           final UpdateCanvasNodeNameCommand updateCanvasNodeNameCommand) {
        this.hasExpression = hasExpression;
        this.editorSelectedEvent = editorSelectedEvent;
        this.nodeUUID = nodeUUID;
        this.hasName = hasName;
        this.updateCanvasNodeCommand = updateCanvasNodeNameCommand;
    }

    public Optional<HasName> getHasName() {
        return hasName;
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
    }

    void fireEditorSelectedEvent() {
        getEditorSelectedEvent().fire(new ExpressionEditorChanged(getNodeUUID()));
    }

    void restoreExpressionName() {
        setExpressionName(getSavedExpressionName());
    }

    void setExpressionName(final String expressionName) {
        final HasName fallbackHasName = getFallbackHasName();
        HasNameUtils.setName(getHasName().orElse(fallbackHasName), expressionName);
        updateCanvasNodeCommand.execute(getNodeUUID(), getHasName().orElse(null));
    }

    HasName getFallbackHasName() {
        return getHasExpression() instanceof HasName ? (HasName) getHasExpression() : HasName.NOP;
    }

    void restoreExpression() {
        if (getHasExpression().asDMNModelInstrumentedBase() instanceof BusinessKnowledgeModel) {
            BusinessKnowledgeModel bkModel = ((BusinessKnowledgeModel) getHasExpression().asDMNModelInstrumentedBase());
            DMNModelInstrumentedBase bkModelParent = bkModel.getEncapsulatedLogic().getParent();
            bkModel.setEncapsulatedLogic((FunctionDefinition) getSavedExpression());
            bkModel.getEncapsulatedLogic().setParent(bkModelParent);
        } else if (getHasExpression() instanceof HasExpression) {
            getHasExpression().setExpression(getSavedExpression());
        }
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
        final HasName fallbackHasName = getFallbackHasName();
        setSavedExpressionName(getHasName().orElse(fallbackHasName).getName().getValue());
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
            setSavedExpression(getHasExpression().getExpression().exactCopy());
        }
    }
}
