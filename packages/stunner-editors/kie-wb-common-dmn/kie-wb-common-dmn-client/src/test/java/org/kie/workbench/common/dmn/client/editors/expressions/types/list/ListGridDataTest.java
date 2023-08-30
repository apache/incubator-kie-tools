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
package org.kie.workbench.common.dmn.client.editors.expressions.types.list;

import java.util.Collections;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.MoveRowsCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.mvp.Command;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ListGridDataTest {

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
    /**/
    @Mock
    private Command canvasOperation;

    private DMNGridData delegate;

    private ListGridData uiModel;

    private Optional<List> expression = Optional.of(new List());

    @Before
    public void setup() {
        this.delegate = spy(new DMNGridData());
        this.uiModel = new ListGridData(delegate,
                                        sessionManager,
                                        sessionCommandManager,
                                        () -> expression,
                                        canvasOperation);

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
    }

    @Test
    public void testMoveRowTo() {
        uiModel.moveRowTo(0,
                          gridRow);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              Mockito.<MoveRowsCommand>any());
    }

    @Test
    public void testMoveRowsTo() {
        uiModel.moveRowsTo(0,
                           Collections.singletonList(gridRow));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              Mockito.<MoveRowsCommand>any());
    }
}
