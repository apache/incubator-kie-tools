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

package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearStatesToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.DeleteSelectionToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.RedoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ToolbarCommandFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.UndoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ValidateToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class EditorToolbarTest {

    @Mock
    private ToolbarView<AbstractToolbar> toolbarView;

    @Mock
    private ManagedInstance<AbstractToolbarItem<AbstractClientFullSession>> items;

    @Mock
    private ToolbarCommandFactory commandFactory;

    @Mock
    private VisitGraphToolbarCommand visitGraphToolbarCommand;

    @Mock
    private ClearToolbarCommand clearToolbarCommand;

    @Mock
    private ClearStatesToolbarCommand clearStatesToolbarCommand;

    @Mock
    private DeleteSelectionToolbarCommand deleteSelectionToolbarCommand;

    @Mock
    private SwitchGridToolbarCommand switchGridToolbarCommand;

    @Mock
    private UndoToolbarCommand undoToolbarCommand;

    @Mock
    private RedoToolbarCommand redoToolbarCommand;

    @Mock
    private ValidateToolbarCommand validateToolbarCommand;

    @Mock
    private ExportToPngToolbarCommand exportToPngToolbarCommand;

    @Mock
    private ExportToJpgToolbarCommand exportToJpgToolbarCommand;

    @Mock
    private ExportToPdfToolbarCommand exportToPdfToolbarCommand;

    private EditorToolbar toolbar;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(commandFactory.newVisitGraphCommand()).thenReturn(visitGraphToolbarCommand);
        when(commandFactory.newClearCommand()).thenReturn(clearToolbarCommand);
        when(commandFactory.newClearStatesCommand()).thenReturn(clearStatesToolbarCommand);
        when(commandFactory.newDeleteSelectedElementsCommand()).thenReturn(deleteSelectionToolbarCommand);
        when(commandFactory.newSwitchGridCommand()).thenReturn(switchGridToolbarCommand);
        when(commandFactory.newUndoCommand()).thenReturn(undoToolbarCommand);
        when(commandFactory.newRedoCommand()).thenReturn(redoToolbarCommand);
        when(commandFactory.newValidateCommand()).thenReturn(validateToolbarCommand);
        when(commandFactory.newExportToPngToolbarCommand()).thenReturn(exportToPngToolbarCommand);
        when(commandFactory.newExportToJpgToolbarCommand()).thenReturn(exportToJpgToolbarCommand);
        when(commandFactory.newExportToPdfToolbarCommand()).thenReturn(exportToPdfToolbarCommand);
        this.toolbar = new EditorToolbar(commandFactory,
                                         items,
                                         toolbarView);
    }

    @Test
    public void testToolbarCommands() {
        assertEquals(visitGraphToolbarCommand,
                     toolbar.getVisitGraphToolbarCommand());
        assertEquals(clearToolbarCommand,
                     toolbar.getClearToolbarCommand());
        assertEquals(clearStatesToolbarCommand,
                     toolbar.getClearStatesToolbarCommand());
        assertEquals(deleteSelectionToolbarCommand,
                     toolbar.getDeleteSelectionToolbarCommand());
        assertEquals(switchGridToolbarCommand,
                     toolbar.getSwitchGridToolbarCommand());
        assertEquals(undoToolbarCommand,
                     toolbar.getUndoToolbarCommand());
        assertEquals(redoToolbarCommand,
                     toolbar.getRedoToolbarCommand());
        assertEquals(validateToolbarCommand,
                     toolbar.getValidateToolbarCommand());
        assertEquals(exportToPngToolbarCommand,
                     toolbar.getExportToPngToolbarCommand());
        assertEquals(exportToJpgToolbarCommand,
                     toolbar.getExportToJpgToolbarCommand());
        assertEquals(exportToPdfToolbarCommand,
                     toolbar.getExportToPdfToolbarCommand());
    }
}
