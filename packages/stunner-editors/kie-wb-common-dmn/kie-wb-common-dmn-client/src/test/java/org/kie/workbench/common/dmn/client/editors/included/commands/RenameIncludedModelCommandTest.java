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

package org.kie.workbench.common.dmn.client.editors.included.commands;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardsGridComponent;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RenameIncludedModelCommandTest {

    @Mock
    private BaseIncludedModelActiveRecord includedModel;

    @Mock
    private DMNCardsGridComponent grid;

    @Mock
    private Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    private final String newName = "new name";

    private RenameIncludedModelCommand command;

    @Before
    public void setup() {
        command = spy(new RenameIncludedModelCommand(includedModel,
                                                     grid,
                                                     refreshDecisionComponentsEvent,
                                                     newName));
    }

    @Test
    public void testExecuteWhenIncludedModelIsValid() {
        doTestExecute("newName", "newName");
    }

    @Test
    public void testExecutedWhenIncludedModelIsValidWithWhitespace() {
        doTestExecute("   newName   ", "newName");
    }

    public void doTestExecute(final String newName, final String expectedName) {

        doReturn(newName).when(command).getNewName();
        final String oldName = "old name";
        when(includedModel.getName()).thenReturn(oldName);
        when(includedModel.isValid()).thenReturn(true);

        doNothing().when(command).refreshDecisionComponents();

        final CommandResult<CanvasViolation> result = command.execute(mock(AbstractCanvasHandler.class));

        verify(includedModel).setName(expectedName);
        verify(includedModel).update();
        verify(grid).refresh();
        verify(command).refreshDecisionComponents();

        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
    }

    @Test
    public void testExecuteWhenNameIsNotValid() {

        final InOrder inOrder = Mockito.inOrder(includedModel);
        final String oldName = "old name";
        when(includedModel.getName()).thenReturn(oldName);
        when(includedModel.isValid()).thenReturn(false);

        doNothing().when(command).refreshDecisionComponents();

        final CommandResult<CanvasViolation> result = command.execute(mock(AbstractCanvasHandler.class));

        inOrder.verify(includedModel).setName(newName);
        inOrder.verify(includedModel).setName(oldName);

        verify(includedModel, never()).update();
        verify(grid, never()).refresh();
        verify(command, never()).refreshDecisionComponents();

        assertNotEquals(CanvasCommandResultBuilder.SUCCESS, result);
    }

    @Test
    public void executeUndo() {

        final String oldName = "old name";
        doNothing().when(command).refreshDecisionComponents();

        command.setOldName(oldName);

        final CommandResult<CanvasViolation> result = command.undo(mock(AbstractCanvasHandler.class));

        verify(includedModel).setName(oldName);
        verify(includedModel).update();
        verify(grid).refresh();
        verify(command).refreshDecisionComponents();

        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
    }

    @Test
    public void testRefreshDecisionComponents() {

        command.refreshDecisionComponents();

        verify(refreshDecisionComponentsEvent).fire(any(RefreshDecisionComponents.class));
    }
}
