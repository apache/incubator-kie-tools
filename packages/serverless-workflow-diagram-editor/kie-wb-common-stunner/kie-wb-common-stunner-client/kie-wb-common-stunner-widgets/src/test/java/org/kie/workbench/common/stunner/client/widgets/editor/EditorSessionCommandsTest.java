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

package org.kie.workbench.common.stunner.client.widgets.editor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToJpgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPdfSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPngSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToSvgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SaveDiagramSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EditorSessionCommandsTest {

    @Mock
    private ManagedClientSessionCommands commands;

    @Mock
    private ClientSession session;

    private EditorSessionCommands editorSessionCommands;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.editorSessionCommands = new EditorSessionCommands(commands);
        when(commands.register(any(Class.class))).thenReturn(commands);
    }

    @Test
    public void testInit() {
        editorSessionCommands.init();

        final InOrder inOrder = inOrder(commands);

        inOrder.verify(commands).register(VisitGraphSessionCommand.class);
        inOrder.verify(commands).register(SwitchGridSessionCommand.class);
        inOrder.verify(commands).register(ClearSessionCommand.class);
        inOrder.verify(commands).register(DeleteSelectionSessionCommand.class);
        inOrder.verify(commands).register(UndoSessionCommand.class);
        inOrder.verify(commands).register(RedoSessionCommand.class);
        inOrder.verify(commands).register(ExportToPngSessionCommand.class);
        inOrder.verify(commands).register(ExportToJpgSessionCommand.class);
        inOrder.verify(commands).register(ExportToPdfSessionCommand.class);
        inOrder.verify(commands).register(ExportToSvgSessionCommand.class);
        inOrder.verify(commands).register(CopySelectionSessionCommand.class);
        inOrder.verify(commands).register(PasteSelectionSessionCommand.class);
        inOrder.verify(commands).register(CutSelectionSessionCommand.class);
        inOrder.verify(commands).register(SaveDiagramSessionCommand.class);
    }

    @Test
    public void testBind() {
        editorSessionCommands.bind(session);

        verify(commands).bind(session);
    }

    @Test
    public void testClear() {
        editorSessionCommands.clear();

        verify(commands).clearCommands();
    }

    @Test
    public void testDestory() {
        editorSessionCommands.destroy();

        verify(commands).destroy();
    }

    @Test
    public void testGetCommands() {
        assertEquals(commands, editorSessionCommands.getCommands());
    }

    @Test
    public void testGetVisitGraphSessionCommand() {
        editorSessionCommands.getVisitGraphSessionCommand();

        verify(commands).get(eq(VisitGraphSessionCommand.class));
    }

    @Test
    public void testGetSwitchGridSessionCommand() {
        editorSessionCommands.getSwitchGridSessionCommand();

        verify(commands).get(eq(SwitchGridSessionCommand.class));
    }

    @Test
    public void testGetClearSessionCommand() {
        editorSessionCommands.getClearSessionCommand();

        verify(commands).get(eq(ClearSessionCommand.class));
    }

    @Test
    public void testGetDeleteSelectionSessionCommand() {
        editorSessionCommands.getDeleteSelectionSessionCommand();

        verify(commands).get(eq(DeleteSelectionSessionCommand.class));
    }

    @Test
    public void testGetUndoSessionCommand() {
        editorSessionCommands.getUndoSessionCommand();

        verify(commands).get(eq(UndoSessionCommand.class));
    }

    @Test
    public void testGetRedoSessionCommand() {
        editorSessionCommands.getRedoSessionCommand();

        verify(commands).get(eq(RedoSessionCommand.class));
    }

    @Test
    public void testGetExportToPngSessionCommand() {
        editorSessionCommands.getExportToPngSessionCommand();

        verify(commands).get(eq(ExportToPngSessionCommand.class));
    }

    @Test
    public void testGetExportToJpgSessionCommand() {
        editorSessionCommands.getExportToJpgSessionCommand();

        verify(commands).get(eq(ExportToJpgSessionCommand.class));
    }

    @Test
    public void testGetExportToPdfSessionCommand() {
        editorSessionCommands.getExportToPdfSessionCommand();

        verify(commands).get(eq(ExportToPdfSessionCommand.class));
    }

    @Test
    public void testGetExportToSvgSessionCommand() {
        editorSessionCommands.getExportToSvgSessionCommand();

        verify(commands).get(eq(ExportToSvgSessionCommand.class));
    }

    @Test
    public void testGetCopySelectionSessionCommand() {
        editorSessionCommands.getCopySelectionSessionCommand();

        verify(commands).get(eq(CopySelectionSessionCommand.class));
    }

    @Test
    public void testGetPasteSelectionSessionCommand() {
        editorSessionCommands.getPasteSelectionSessionCommand();

        verify(commands).get(eq(PasteSelectionSessionCommand.class));
    }

    @Test
    public void testGetCutSelectionSessionCommand() {
        editorSessionCommands.getCutSelectionSessionCommand();

        verify(commands).get(eq(CutSelectionSessionCommand.class));
    }

    @Test
    public void testGetSaveDiagramSessionCommand() {
        editorSessionCommands.getSaveDiagramSessionCommand();

        verify(commands).get(eq(SaveDiagramSessionCommand.class));
    }
}
