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
package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Optional;

import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.mvp.Command;

public interface ExpressionEditorView extends org.jboss.errai.ui.client.local.api.IsElement,
                                              UberElement<ExpressionEditorView.Presenter> {

    interface Presenter extends IsElement {

        void init(final SessionPresenter<AbstractClientFullSession, ?, Diagram> presenter);

        void setHasName(final Optional<HasName> hasName);

        void setHasExpression(final HasExpression hasExpression);

        void setExpression(final Optional<Expression> expression);

        void onExpressionEditorSelected(final ExpressionEditorSelectedEvent event);

        void setExitCommand(final Command exitCommand);

        void exit();
    }

    void setEditor(final ExpressionEditorDefinition<Expression> definition,
                   final HasExpression hasExpression,
                   final Optional<HasName> hasName,
                   final Optional<Expression> expression);

    void onExpressionEditorSelected(final Optional<GridWidget> oEditor);
}
