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

package org.kie.workbench.common.dmn.client.commands.general;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.event.Event;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.AbstractSessionPresenter;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.client.workbench.widgets.ResizeFlowPanel;

public abstract class BaseNavigateCommand extends AbstractCanvasGraphCommand {

    static final NoOperationGraphCommand NOP_GRAPH_COMMAND = new NoOperationGraphCommand();

    protected final ExpressionEditorView.Presenter editor;
    protected final SessionPresenter<? extends ClientSession, ?, Diagram> presenter;
    protected final SessionManager sessionManager;
    protected final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    protected final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    protected final String nodeUUID;
    protected final HasExpression hasExpression;
    protected final Optional<HasName> hasName;
    protected final boolean isOnlyVisualChangeAllowed;

    public BaseNavigateCommand(final ExpressionEditorView.Presenter editor,
                               final SessionPresenter<? extends ClientSession, ?, Diagram> presenter,
                               final SessionManager sessionManager,
                               final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                               final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                               final String nodeUUID,
                               final HasExpression hasExpression,
                               final Optional<HasName> hasName,
                               final boolean isOnlyVisualChangeAllowed) {
        this.editor = editor;
        this.presenter = presenter;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.nodeUUID = nodeUUID;
        this.hasExpression = hasExpression;
        this.hasName = hasName;
        this.isOnlyVisualChangeAllowed = isOnlyVisualChangeAllowed;
    }

    protected void navigateToExpressionEditor(final HasExpression hasExpression,
                                              final Optional<HasName> hasName) {
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new NavigateToExpressionEditorCommand(editor,
                                                                            presenter,
                                                                            sessionManager,
                                                                            sessionCommandManager,
                                                                            refreshFormPropertiesEvent,
                                                                            nodeUUID,
                                                                            hasExpression,
                                                                            hasName,
                                                                            isOnlyVisualChangeAllowed));
    }

    protected void navigateToDRGEditor(final HasExpression hasExpression,
                                       final Optional<HasName> hasName) {
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      new NavigateToDRGEditorCommand(editor,
                                                                     presenter,
                                                                     sessionManager,
                                                                     sessionCommandManager,
                                                                     refreshFormPropertiesEvent,
                                                                     nodeUUID,
                                                                     hasExpression,
                                                                     hasName,
                                                                     isOnlyVisualChangeAllowed));
    }

    protected void enableHandlers(final boolean enabled) {
        final CanvasHandler handler = getCanvasHandler();
        if (handler == null) {
            return;
        }
        final LienzoCanvas canvas = (LienzoCanvas) handler.getCanvas();
        if (enabled) {
            canvas.enableHandlers();
        } else {
            canvas.disableHandlers();
        }
    }

    protected void addExpressionEditorToCanvasWidget() {
        final ResizeFlowPanel container = wrapElementForErrai1090();
        presenter.getView().setCanvasWidget(container);
        presenter.lostFocus();
        editor.getView().setFocus();

        Scheduler.get().scheduleDeferred(container::onResize);
    }

    // See https://issues.jboss.org/browse/ERRAI-1090
    // The Widget returned from ElementWrapperWidget does not implement interfaces
    // defined on the editor.getElement() and hence RequiresResize is lost.
    // Wrap the editor in a ResizeFlowPanel to support RequiresResize.
    protected ResizeFlowPanel wrapElementForErrai1090() {
        final Widget w = ElementWrapperWidget.getWidget(editor.getElement());
        final ResizeFlowPanel container = new ResizeFlowPanel() {

            @Override
            public void onResize() {
                super.onResize();
                editor.getView().onResize();
            }
        };
        container.getElement().setId("dmn-expression-editor-container");
        container.getElement().getStyle().setDisplay(Style.Display.FLEX);
        container.getElement().getStyle().setWidth(100.0, Style.Unit.PCT);
        container.getElement().getStyle().setHeight(100.0, Style.Unit.PCT);
        container.add(w);

        return container;
    }

    protected void addDRGEditorToCanvasWidget() {
        presenter.getView().setCanvasWidget(((AbstractSessionPresenter) presenter).getDisplayer().getView());
        presenter.focus();
    }

    protected void hidePaletteWidget(final boolean hidden) {
        if (!Objects.isNull(presenter.getPalette())) {
            presenter.getPalette().setVisible(!hidden);
        }
    }

    protected void unmountNewBoxedExpressionEditor() {
        editor.unmountNewBoxedExpressionEditor();
    }

    private CanvasHandler getCanvasHandler() {
        return null != sessionManager.getCurrentSession() ? sessionManager.getCurrentSession().getCanvasHandler() : null;
    }
}
