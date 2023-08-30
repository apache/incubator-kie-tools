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
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralProps;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;

import static org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util.ExpressionModelFiller.fillLiteralExpression;

public class FillLiteralExpressionCommand extends FillExpressionCommand<LiteralProps> {

    public FillLiteralExpressionCommand(final HasExpression hasExpression,
                                        final LiteralProps expressionProps,
                                        final Event<ExpressionEditorChanged> editorSelectedEvent,
                                        final String nodeUUID,
                                        final ItemDefinitionUtils itemDefinitionUtils,
                                        final Optional<HasName> hasName) {
        super(hasExpression, expressionProps, editorSelectedEvent, nodeUUID, itemDefinitionUtils, hasName);
    }

    @Override
    protected Expression getNewExpression() {
        return new LiteralExpression();
    }

    @Override
    protected void fill() {
        final LiteralExpression literalExpression = (LiteralExpression) getHasExpression().getExpression();
        fill(literalExpression, getExpressionProps());
    }

    @Override
    public boolean isCurrentExpressionOfTheSameType() {
        return getHasExpression().getExpression() instanceof LiteralExpression;
    }

    void fill(final LiteralExpression expression, final LiteralProps props) {
        fillLiteralExpression(expression, props);
    }
}
