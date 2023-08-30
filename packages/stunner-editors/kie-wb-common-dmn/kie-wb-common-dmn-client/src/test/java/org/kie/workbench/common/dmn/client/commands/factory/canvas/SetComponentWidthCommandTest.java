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
package org.kie.workbench.common.dmn.client.commands.factory.canvas;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.general.NoOperationGraphCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SetComponentWidthCommandTest {

    private static final double WIDTH = 100.0;

    private static final double OLD_WIDTH = 200.0;

    @Mock
    private DMNGridColumn uiColumn;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private BaseGrid uiGridWidget;

    private SetComponentWidthCommand command;

    @Before
    public void setup() {
        this.command = new SetComponentWidthCommand(uiColumn,
                                                    OLD_WIDTH,
                                                    WIDTH);

        when(uiColumn.getGridWidget()).thenReturn(uiGridWidget);
    }

    @Test
    public void testTypes() {
        assertThat(command.newGraphCommand(canvasHandler)).isInstanceOf(NoOperationGraphCommand.class);
        assertThat(command.newCanvasCommand(canvasHandler)).isInstanceOf(SetComponentWidthCanvasCommand.class);
    }

    @Test
    public void testExecute() {
        assertThat(command.getCanvasCommand(canvasHandler).execute(canvasHandler)).isEqualTo(CanvasCommandResultBuilder.SUCCESS);

        verify(uiColumn).setWidth(WIDTH);
        verify(uiGridWidget).batch();
    }

    @Test
    public void testExecuteThenUndo() {
        assertThat(command.getCanvasCommand(canvasHandler).execute(canvasHandler)).isEqualTo(CanvasCommandResultBuilder.SUCCESS);

        verify(uiColumn).setWidth(WIDTH);
        verify(uiGridWidget).batch();

        assertThat(command.getCanvasCommand(canvasHandler).undo(canvasHandler)).isEqualTo(CanvasCommandResultBuilder.SUCCESS);

        verify(uiColumn).setWidth(OLD_WIDTH);
        verify(uiGridWidget, times(2)).batch();
    }
}
