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

import com.google.gwt.user.client.ui.IsWidget;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.widgets.palette.PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.AbstractSessionPresenter;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.ResizeFlowPanel;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseNavigationCommandTest {

    @Mock
    protected ExpressionEditorView.Presenter editor;

    @Mock
    protected ExpressionEditorView expressionEditorView;

    @Mock
    protected ResizeFlowPanel editorContainerForErrai1090;

    @Mock
    protected AbstractSessionPresenter sessionPresenter;

    @Mock
    protected SessionPresenter.View sessionPresenterView;

    @Mock
    protected SessionViewer sessionView;

    @Mock
    protected IsWidget view;

    @Mock
    protected SessionManager sessionManager;

    @Mock
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    protected EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    protected EditorSession session;

    @Mock
    protected HasName hasName;

    @Mock
    protected HasExpression hasExpression;

    @Mock
    protected GraphCommandExecutionContext graphCommandExecutionContext;

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    @Mock
    protected LienzoCanvas canvas;

    @Mock
    protected PaletteWidget palette;

    @Captor
    protected ArgumentCaptor<RefreshFormPropertiesEvent> refreshFormPropertiesEventCaptor;

    protected BaseNavigateCommand command;

    @SuppressWarnings("unchecked")
    public void setup(final boolean isOnlyVisualChangeAllowed) {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(sessionPresenter.getView()).thenReturn(sessionPresenterView);
        when(sessionPresenter.getDisplayer()).thenReturn(sessionView);
        when(sessionView.getView()).thenReturn(view);
        when(editor.getView()).thenReturn(expressionEditorView);
        when(sessionPresenter.getPalette()).thenReturn(palette);

        this.command = spy(getCommand(isOnlyVisualChangeAllowed));

        doReturn(editorContainerForErrai1090).when(command).wrapElementForErrai1090();
    }

    protected abstract BaseNavigateCommand getCommand(final boolean isOnlyVisualChangeAllowed);

    @Test
    public void verifyGraphCommandIsNoOperation() {
        setup(false);

        assertEquals(BaseNavigateCommand.NOP_GRAPH_COMMAND,
                     command.getGraphCommand(canvasHandler));
    }

    @Test
    public void allowCanvasCommand() {
        setup(false);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).allow(canvasHandler));
    }

    @Test
    public void testHidePaletteWidget() {

        setup(false);

        command.hidePaletteWidget(true);

        verify(palette).setVisible(false);
    }

    @Test
    public void testShowPaletteWidget() {

        setup(false);

        command.hidePaletteWidget(false);

        verify(palette).setVisible(true);
    }

    @Test
    public void testHidePaletteWidgetWhenPaletteIsNotPresent() {

        setup(false);

        when(sessionPresenter.getPalette()).thenReturn(null);

        command.hidePaletteWidget(true);

        verify(palette, never()).setVisible(anyBoolean());
    }

    public abstract void executeCanvasCommand();

    public abstract void undoCanvasCommand();
}
