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

import java.util.Optional;

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;

import static org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util.ExpressionModelFiller.fillRelationExpression;

public class FillRelationExpressionCommand extends FillExpressionCommand<RelationProps> {

    public FillRelationExpressionCommand(final HasExpression hasExpression,
                                         final RelationProps expressionProps,
                                         final Event<ExpressionEditorChanged> editorSelectedEvent,
                                         final String nodeUUID,
                                         final ExpressionEditorView view,
                                         final ItemDefinitionUtils itemDefinitionUtils,
                                         final Optional<HasName> hasName) {
        super(hasExpression, expressionProps, editorSelectedEvent, nodeUUID, view, itemDefinitionUtils, hasName);
    }

    @Override
    protected void fill() {
        final Relation relation = (Relation) getHasExpression().getExpression();
        fill(relation, getExpressionProps());
    }

    @Override
    protected Expression getNewExpression() {
        return new Relation();
    }

    void fill(final Relation expression, final RelationProps props) {
        fillRelationExpression(expression, props);
    }
}
