/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.editor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PerformAutomaticLayoutCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNProjectToolbarStateHandlerTest {

    @Mock
    private DMNEditorMenuSessionItems editorMenuSessionItems;

    private DMNProjectToolbarStateHandler toolbarStateHandler;

    @Before
    public void setup() {
        this.toolbarStateHandler = new DMNProjectToolbarStateHandler(editorMenuSessionItems);

        when(editorMenuSessionItems.isItemEnabled(ClearSessionCommand.class)).thenReturn(true);
        when(editorMenuSessionItems.isItemEnabled(SwitchGridSessionCommand.class)).thenReturn(true);
        when(editorMenuSessionItems.isItemEnabled(VisitGraphSessionCommand.class)).thenReturn(true);
        when(editorMenuSessionItems.isItemEnabled(DeleteSelectionSessionCommand.class)).thenReturn(true);
        when(editorMenuSessionItems.isItemEnabled(CutSelectionSessionCommand.class)).thenReturn(true);
        when(editorMenuSessionItems.isItemEnabled(CopySelectionSessionCommand.class)).thenReturn(true);
        when(editorMenuSessionItems.isItemEnabled(PasteSelectionSessionCommand.class)).thenReturn(true);
        when(editorMenuSessionItems.isItemEnabled(PerformAutomaticLayoutCommand.class)).thenReturn(true);
    }

    @Test
    public void testEnterGridView() {
        toolbarStateHandler.enterGridView();

        verify(editorMenuSessionItems).setItemEnabled(ClearSessionCommand.class, false);
        verify(editorMenuSessionItems).setItemEnabled(SwitchGridSessionCommand.class, false);
        verify(editorMenuSessionItems).setItemEnabled(VisitGraphSessionCommand.class, false);
        verify(editorMenuSessionItems).setItemEnabled(DeleteSelectionSessionCommand.class, false);
        verify(editorMenuSessionItems).setItemEnabled(CutSelectionSessionCommand.class, false);
        verify(editorMenuSessionItems).setItemEnabled(CopySelectionSessionCommand.class, false);
        verify(editorMenuSessionItems).setItemEnabled(PasteSelectionSessionCommand.class, false);
        verify(editorMenuSessionItems).setItemEnabled(PerformAutomaticLayoutCommand.class, false);
    }

    @Test
    public void testEnterGraphView() {
        //First enter Grid view to retrieve current state
        toolbarStateHandler.enterGridView();

        toolbarStateHandler.enterGraphView();

        verify(editorMenuSessionItems).setItemEnabled(ClearSessionCommand.class, true);
        verify(editorMenuSessionItems).setItemEnabled(SwitchGridSessionCommand.class, true);
        verify(editorMenuSessionItems).setItemEnabled(VisitGraphSessionCommand.class, true);
        verify(editorMenuSessionItems).setItemEnabled(DeleteSelectionSessionCommand.class, true);
        verify(editorMenuSessionItems).setItemEnabled(CutSelectionSessionCommand.class, true);
        verify(editorMenuSessionItems).setItemEnabled(CopySelectionSessionCommand.class, true);
        verify(editorMenuSessionItems).setItemEnabled(PasteSelectionSessionCommand.class, true);
        verify(editorMenuSessionItems).setItemEnabled(PerformAutomaticLayoutCommand.class, true);
    }
}
