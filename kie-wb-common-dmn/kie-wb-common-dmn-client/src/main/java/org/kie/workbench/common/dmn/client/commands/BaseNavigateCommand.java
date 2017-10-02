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

package org.kie.workbench.common.dmn.client.commands;

import java.util.Optional;

import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.AbstractSessionPresenter;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.impl.AbstractGraphCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public abstract class BaseNavigateCommand extends AbstractCanvasGraphCommand {

    static final NoOperationGraphCommand NOP_GRAPH_COMMAND = new NoOperationGraphCommand();

    protected ExpressionEditorView.Presenter editor;
    protected SessionPresenter<AbstractClientFullSession, ?, Diagram> presenter;
    protected SessionManager sessionManager;
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    protected Optional<HasName> hasName;
    protected HasExpression hasExpression;

    public BaseNavigateCommand(final ExpressionEditorView.Presenter editor,
                               final SessionPresenter<AbstractClientFullSession, ?, Diagram> presenter,
                               final SessionManager sessionManager,
                               final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                               final Optional<HasName> hasName,
                               final HasExpression hasExpression) {
        this.editor = editor;
        this.presenter = presenter;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.hasName = hasName;
        this.hasExpression = hasExpression;
    }

    protected void navigateToExpressionEditor(final Optional<HasName> hasName,
                                              final HasExpression hasExpression) {
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new NavigateToExpressionEditorCommand(editor,
                                                                            presenter,
                                                                            sessionManager,
                                                                            sessionCommandManager,
                                                                            hasName,
                                                                            hasExpression));
    }

    protected void navigateToDRGEditor(final Optional<HasName> hasName,
                                       final HasExpression hasExpression) {
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new NavigateToDRGEditorCommand(editor,
                                                                     presenter,
                                                                     sessionManager,
                                                                     sessionCommandManager,
                                                                     hasName,
                                                                     hasExpression));
    }

    protected void enableHandlers(final boolean enabled) {
        final CanvasHandler handler = getCanvasHandler();
        if (handler == null) {
            return;
        }
        final Layer layer = handler.getCanvas().getLayer();
        if (enabled) {
            layer.enableHandlers();
            ((AbstractClientSession) sessionManager.getCurrentSession()).resume();
        } else {
            layer.disableHandlers();
            ((AbstractClientSession) sessionManager.getCurrentSession()).pause();
        }
    }

    protected void addExpressionEditorToCanvasWidget() {
        presenter.getView().setCanvasWidget(ElementWrapperWidget.getWidget(editor.getElement()));
    }

    protected void addDRGEditorToCanvasWidget() {
        presenter.getView().setCanvasWidget(((AbstractSessionPresenter) presenter).getDisplayer().getView());
    }

    protected void hidePaletteWidget(final boolean hidden) {
        presenter.getPalette().setVisible(!hidden);
    }

    private CanvasHandler getCanvasHandler() {
        return null != sessionManager.getCurrentSession() ? sessionManager.getCurrentSession().getCanvasHandler() : null;
    }

    public static class NoOperationGraphCommand extends AbstractGraphCommand {

        @Override
        protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
            return GraphCommandResultBuilder.SUCCESS;
        }

        @Override
        public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
            return GraphCommandResultBuilder.SUCCESS;
        }

        @Override
        public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
            return GraphCommandResultBuilder.SUCCESS;
        }
    }
}
