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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Collections;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.MoveRowsCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ContextGridDataTest {

    @Mock
    private GridRow gridRow;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Command canvasOperation;

    @Mock
    private DMNGridData delegate;

    private ContextGridData uiModel;

    private Optional<Context> expression = Optional.of(new Context());

    @Before
    public void setup() {
        this.uiModel = new ContextGridData(delegate,
                                           sessionManager,
                                           sessionCommandManager,
                                           () -> expression,
                                           canvasOperation);

        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(canvasHandler).when(session).getCanvasHandler();
    }

    @Test
    public void testMoveRowToPermitted() {
        doReturn(GraphCommandResultBuilder.SUCCESS).when(sessionCommandManager).allow(eq(canvasHandler),
                                                                                      any(MoveRowsCommand.class));

        uiModel.moveRowTo(0,
                          gridRow);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(MoveRowsCommand.class));
    }

    @Test
    public void testMoveRowsToPermitted() {
        doReturn(GraphCommandResultBuilder.SUCCESS).when(sessionCommandManager).allow(eq(canvasHandler),
                                                                                      any(MoveRowsCommand.class));

        uiModel.moveRowsTo(0,
                           Collections.singletonList(gridRow));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(MoveRowsCommand.class));
    }

    @Test
    public void testMoveRowToNotPermitted() {
        doReturn(GraphCommandResultBuilder.failed()).when(sessionCommandManager).allow(eq(canvasHandler),
                                                                                     any(MoveRowsCommand.class));

        uiModel.moveRowTo(0,
                          gridRow);

        verify(sessionCommandManager,
               never()).execute(any(AbstractCanvasHandler.class),
                                any(MoveRowsCommand.class));
    }

    @Test
    public void testMoveRowsToNotPermitted() {
        doReturn(GraphCommandResultBuilder.failed()).when(sessionCommandManager).allow(eq(canvasHandler),
                                                                                     any(MoveRowsCommand.class));

        uiModel.moveRowsTo(0,
                           Collections.singletonList(gridRow));

        verify(sessionCommandManager,
               never()).execute(any(AbstractCanvasHandler.class),
                                any(MoveRowsCommand.class));
    }
}
