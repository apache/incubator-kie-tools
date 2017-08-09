/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;

@ApplicationScoped
public class DecisionTableEditorDefinition implements ExpressionEditorDefinition<DecisionTable> {

    private DecisionTableEditorView view;

    public DecisionTableEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public DecisionTableEditorDefinition(final DecisionTableEditorView view) {
        this.view = view;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.DECISION_TABLE;
    }

    @Override
    public String getName() {
        return DecisionTable.class.getSimpleName();
    }

    @Override
    public Optional<DecisionTable> getModelClass() {
        return Optional.of(new DecisionTable());
    }

    @Override
    public BaseExpressionEditorView.Editor<DecisionTable> getEditor() {
        return new BaseExpressionEditorView.Editor<DecisionTable>() {
            @Override
            public IsElement getView() {
                return view;
            }

            @Override
            public void setHasName(final Optional<HasName> hasName) {
                //Unsupported
            }

            @Override
            public void setExpression(final DecisionTable expression) {
                //Unsupported
            }
        };
    }
}
