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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Collections;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.MoveRowsCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.MoveColumnsCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.mvp.Command;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class RelationGridDataTest {

    @Mock
    private GridRow gridRow;

    @Mock
    private GridColumn gridColumn1;

    @Mock
    private GridColumn gridColumn2;

    @Mock
    private GridColumn gridColumn3;

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

    private DMNGridData delegate;

    private RelationGridData uiModel;

    private Optional<Relation> expression = Optional.of(new Relation());

    @Before
    public void setup() {
        this.delegate = spy(new DMNGridData());
        this.uiModel = new RelationGridData(delegate,
                                            sessionManager,
                                            sessionCommandManager,
                                            () -> expression,
                                            canvasOperation);

        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(canvasHandler).when(session).getCanvasHandler();
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

    @Test
    public void testMoveColumnTo() {
        uiModel.moveColumnTo(0,
                             gridColumn1);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              Mockito.<MoveColumnsCommand>any());
    }

    @Test
    public void testMoveColumnsTo() {
        uiModel.moveColumnsTo(0,
                              Collections.singletonList(gridColumn1));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              Mockito.<MoveColumnsCommand>any());
    }

    @Test
    public void testAppendColumn() {
        uiModel.appendColumn(gridColumn1);

        verify(delegate).appendColumn(eq(gridColumn1));

        verify(gridColumn1).setResizable(eq(false));
    }

    @Test
    public void testAppendColumns() {
        uiModel.appendColumn(gridColumn1);

        reset(gridColumn1);

        uiModel.appendColumn(gridColumn2);

        verify(gridColumn1).setResizable(eq(false));
        verify(gridColumn2).setResizable(eq(false));

        reset(gridColumn1, gridColumn2);

        uiModel.appendColumn(gridColumn3);

        verify(gridColumn1).setResizable(eq(false));
        verify(gridColumn2).setResizable(eq(true));
        verify(gridColumn3).setResizable(eq(false));
    }

    @Test
    public void testInsertColumn() {
        uiModel.insertColumn(0, gridColumn1);

        verify(delegate).insertColumn(eq(0),
                                      eq(gridColumn1));

        verify(gridColumn1).setResizable(eq(false));
    }

    @Test
    public void testInsertColumnsInOrder() {
        uiModel.insertColumn(0, gridColumn1);

        reset(gridColumn1);

        uiModel.insertColumn(1, gridColumn2);

        verify(gridColumn1).setResizable(eq(false));
        verify(gridColumn2).setResizable(eq(false));

        reset(gridColumn1, gridColumn2);

        uiModel.insertColumn(2, gridColumn3);

        verify(gridColumn1).setResizable(eq(false));
        verify(gridColumn2).setResizable(eq(true));
        verify(gridColumn3).setResizable(eq(false));
    }

    @Test
    public void testInsertColumnsNotInOrder() {
        uiModel.insertColumn(0, gridColumn1);

        reset(gridColumn1);

        uiModel.insertColumn(0, gridColumn2);

        verify(gridColumn1).setResizable(eq(false));
        verify(gridColumn2).setResizable(eq(false));

        reset(gridColumn1, gridColumn2);

        uiModel.insertColumn(0, gridColumn3);

        verify(gridColumn1).setResizable(eq(false));
        verify(gridColumn2).setResizable(eq(true));
        verify(gridColumn3).setResizable(eq(false));
    }

    @Test
    public void testDeleteColumn() {
        uiModel.appendColumn(gridColumn1);
        uiModel.appendColumn(gridColumn2);

        //Reset as methods were invoked by the appendColumn(..) calls
        reset(gridColumn1);
        uiModel.deleteColumn(gridColumn2);

        verify(delegate).deleteColumn(eq(gridColumn2));

        verify(gridColumn1).setResizable(eq(false));
    }
}
